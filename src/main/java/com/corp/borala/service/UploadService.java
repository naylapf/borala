package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.model.Imagem;
import com.corp.borala.repository.ImagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Guarda as imagens enviadas pelos usuários no banco de dados e devolve o caminho público
 * ({@code /fotos/<id>}), que é o valor gravado no prato ou na mídia. O tipo é conferido pelo
 * conteúdo do arquivo (JPG, PNG ou WEBP), não pelo nome, e o tamanho máximo é de 5 MB.
 * Roda dentro da transação de quem chama: se ela for desfeita, a imagem também some.
 */
@Service
public class UploadService {

    public static final String PREFIXO_PUBLICO = "/fotos/";
    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;
    private static final int LADO_MAXIMO = 1600;
    private static final int LIMIAR_BYTES = 300 * 1024;
    private static final float QUALIDADE_JPEG = 0.85f;

    private final ImagemRepository imagemRepository;

    public UploadService(ImagemRepository imagemRepository) {
        this.imagemRepository = imagemRepository;
    }

    /** Valida e grava a imagem; {@code campo} é o campo do formulário que recebe a mensagem de erro. */
    public String salvarImagem(MultipartFile arquivo, String campo) {
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new CampoInvalidoException(campo, "A imagem deve ter no máximo 5 MB");
        }
        try {
            byte[] dados = arquivo.getBytes();
            String tipo = detectarTipo(dados);
            if (tipo == null) {
                throw new CampoInvalidoException(campo, "Envie uma imagem JPG, PNG ou WEBP");
            }
            Otimizada otimizada = otimizar(dados, tipo);
            return PREFIXO_PUBLICO + imagemRepository.save(new Imagem(otimizada.tipo(), otimizada.dados())).getId();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler a imagem", e);
        }
    }

    /** Apaga a imagem se o caminho for de uma foto nossa; links externos, fotos demo e valores vazios são ignorados. */
    public void excluir(String caminho) {
        if (caminho == null || !caminho.startsWith(PREFIXO_PUBLICO)) {
            return;
        }
        try {
            Long id = Long.valueOf(caminho.substring(PREFIXO_PUBLICO.length()));
            if (imagemRepository.existsById(id)) {
                imagemRepository.deleteById(id);
            }
        } catch (NumberFormatException e) {
            // caminho fora do padrão: nada a apagar
        }
    }

    /**
     * Reduz o espaço no banco sem estragar a foto: imagens grandes (mais de 1600 px ou 300 KB) são giradas
     * conforme o EXIF, reduzidas e regravadas (JPEG com qualidade 85, ou PNG se tiver transparência).
     * Se algo falhar, ou o resultado não ficar menor, vale o arquivo original. WEBP não é reprocessado.
     */
    private Otimizada otimizar(byte[] original, String tipo) {
        Otimizada semMudanca = new Otimizada(original, tipo);
        if (tipo.equals("image/webp")) {
            return semMudanca;
        }
        try {
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(original));
            if (imagem == null) {
                return semMudanca;
            }
            boolean girar = tipo.equals("image/jpeg") && lerOrientacao(original) > 1;
            boolean grande = Math.max(imagem.getWidth(), imagem.getHeight()) > LADO_MAXIMO || original.length > LIMIAR_BYTES;
            if (!grande && !girar) {
                return semMudanca;
            }
            boolean transparente = imagem.getColorModel().hasAlpha();
            if (girar) {
                imagem = orientar(imagem, lerOrientacao(original));
            }
            imagem = reduzir(imagem, transparente);
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            String novoTipo = transparente ? "image/png" : "image/jpeg";
            if (transparente) {
                ImageIO.write(imagem, "png", saida);
            } else {
                gravarJpeg(imagem, saida);
            }
            // sem ganho de tamanho e sem rotação a corrigir: fica o original
            if (saida.size() >= original.length && !girar) {
                return semMudanca;
            }
            return new Otimizada(saida.toByteArray(), novoTipo);
        } catch (IOException | RuntimeException e) {
            return semMudanca;
        }
    }

    private BufferedImage reduzir(BufferedImage imagem, boolean transparente) {
        int maior = Math.max(imagem.getWidth(), imagem.getHeight());
        double escala = maior > LADO_MAXIMO ? (double) LADO_MAXIMO / maior : 1.0;
        int largura = Math.max(1, (int) Math.round(imagem.getWidth() * escala));
        int altura = Math.max(1, (int) Math.round(imagem.getHeight() * escala));
        BufferedImage nova = new BufferedImage(largura, altura,
                transparente ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g = nova.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (!transparente) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, largura, altura);
            }
            g.drawImage(imagem, 0, 0, largura, altura, null);
        } finally {
            g.dispose();
        }
        return nova;
    }

    private void gravarJpeg(BufferedImage imagem, ByteArrayOutputStream saida) throws IOException {
        ImageWriter escritor = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam parametros = escritor.getDefaultWriteParam();
        parametros.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        parametros.setCompressionQuality(QUALIDADE_JPEG);
        try (ImageOutputStream destino = ImageIO.createImageOutputStream(saida)) {
            escritor.setOutput(destino);
            escritor.write(null, new IIOImage(imagem, null, null), parametros);
        } finally {
            escritor.dispose();
        }
    }

    /**
     * Aplica a orientação EXIF (1 a 8) à imagem. O navegador faz isso sozinho ao exibir o JPEG original,
     * mas ao regravar o arquivo o EXIF se perde, então a rotação precisa ser aplicada nos pixels.
     */
    static BufferedImage orientar(BufferedImage imagem, int orientacao) {
        int w = imagem.getWidth();
        int h = imagem.getHeight();
        AffineTransform t = new AffineTransform();
        switch (orientacao) {
            case 2 -> { t.scale(-1, 1); t.translate(-w, 0); }
            case 3 -> { t.translate(w, h); t.rotate(Math.PI); }
            case 4 -> { t.scale(1, -1); t.translate(0, -h); }
            case 5 -> { t.rotate(Math.PI / 2); t.scale(1, -1); }
            case 6 -> { t.translate(h, 0); t.rotate(Math.PI / 2); }
            case 7 -> { t.scale(-1, 1); t.translate(-h, 0); t.translate(0, w); t.rotate(3 * Math.PI / 2); }
            case 8 -> { t.translate(0, w); t.rotate(3 * Math.PI / 2); }
            default -> { return imagem; }
        }
        boolean trocaLados = orientacao >= 5;
        int tipo = imagem.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage nova = new BufferedImage(trocaLados ? h : w, trocaLados ? w : h, tipo);
        Graphics2D g = nova.createGraphics();
        try {
            g.drawImage(imagem, t, null);
        } finally {
            g.dispose();
        }
        return nova;
    }

    /** Lê a tag de orientação do EXIF de um JPEG; devolve 1 (normal) se não houver ou se o arquivo estiver fora do padrão. */
    static int lerOrientacao(byte[] jpeg) {
        try {
            int pos = 2;
            while (pos + 4 < jpeg.length && (jpeg[pos] & 0xFF) == 0xFF) {
                int marcador = jpeg[pos + 1] & 0xFF;
                int tamanho = ((jpeg[pos + 2] & 0xFF) << 8) | (jpeg[pos + 3] & 0xFF);
                if (marcador == 0xDA) {
                    break;
                }
                if (marcador == 0xE1 && jpeg[pos + 4] == 'E' && jpeg[pos + 5] == 'x'
                        && jpeg[pos + 6] == 'i' && jpeg[pos + 7] == 'f') {
                    int tiff = pos + 10;
                    boolean pequenoPrimeiro = jpeg[tiff] == 'I';
                    int ifd = tiff + lerInteiro(jpeg, tiff + 4, 4, pequenoPrimeiro);
                    int entradas = lerInteiro(jpeg, ifd, 2, pequenoPrimeiro);
                    for (int i = 0; i < entradas; i++) {
                        int entrada = ifd + 2 + i * 12;
                        if (lerInteiro(jpeg, entrada, 2, pequenoPrimeiro) == 0x0112) {
                            int valor = lerInteiro(jpeg, entrada + 8, 2, pequenoPrimeiro);
                            return valor >= 1 && valor <= 8 ? valor : 1;
                        }
                    }
                    return 1;
                }
                pos += 2 + tamanho;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // EXIF truncado: trata como imagem sem rotação
        }
        return 1;
    }

    private static int lerInteiro(byte[] b, int inicio, int bytes, boolean pequenoPrimeiro) {
        int valor = 0;
        for (int i = 0; i < bytes; i++) {
            int byteAtual = b[pequenoPrimeiro ? inicio + bytes - 1 - i : inicio + i] & 0xFF;
            valor = (valor << 8) | byteAtual;
        }
        return valor;
    }

    private record Otimizada(byte[] dados, String tipo) {
    }

    /** Reconhece JPG, PNG e WEBP pelos primeiros bytes; devolve null para qualquer outro formato. */
    private String detectarTipo(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "image/png";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "image/webp";
        }
        return null;
    }
}
