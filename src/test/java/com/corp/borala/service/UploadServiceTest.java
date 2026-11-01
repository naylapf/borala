package com.corp.borala.service;

import com.corp.borala.exception.CampoInvalidoException;
import com.corp.borala.model.Imagem;
import com.corp.borala.repository.ImagemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UploadServiceTest {

    private final ImagemRepository repositorio = mock(ImagemRepository.class);
    private final UploadService servico = new UploadService(repositorio);

    private static byte[] png() {
        return new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    }

    @Test
    void salvaPngNoBancoEDevolveCaminhoPublico() throws Exception {
        when(repositorio.save(any(Imagem.class))).thenAnswer(chamada -> {
            Imagem imagem = chamada.getArgument(0);
            assertEquals("image/png", imagem.getTipoConteudo());
            Imagem salva = new Imagem(imagem.getTipoConteudo(), imagem.getDados());
            var campo = Imagem.class.getDeclaredField("id");
            campo.setAccessible(true);
            campo.set(salva, 42L);
            return salva;
        });
        MockMultipartFile arquivo = new MockMultipartFile("foto", "qualquer.txt", "text/plain", png());

        assertEquals("/fotos/42", servico.salvarImagem(arquivo, "arquivoFoto"));
    }

    @Test
    void rejeitaArquivoQueNaoEImagem() {
        MockMultipartFile arquivo = new MockMultipartFile("foto", "foto.png", "image/png", "texto".getBytes());

        assertThrows(CampoInvalidoException.class, () -> servico.salvarImagem(arquivo, "arquivoFoto"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void rejeitaImagemMaiorQueCincoMegas() {
        byte[] grande = new byte[5 * 1024 * 1024 + 1];
        System.arraycopy(png(), 0, grande, 0, 12);
        MockMultipartFile arquivo = new MockMultipartFile("foto", "foto.png", "image/png", grande);

        assertThrows(CampoInvalidoException.class, () -> servico.salvarImagem(arquivo, "arquivoFoto"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void excluiSomenteFotosDoBanco() {
        when(repositorio.existsById(7L)).thenReturn(true);

        servico.excluir("/fotos/7");
        servico.excluir("/img/demo/numar.png");
        servico.excluir("https://exemplo.com/foto.jpg");
        servico.excluir("/fotos/abc");
        servico.excluir(null);

        verify(repositorio, times(1)).deleteById(any());
        verify(repositorio).deleteById(7L);
    }

    @Test
    void reduzFotoGrandeParaJpegMenorSemPassarDe1600px() throws Exception {
        BufferedImage foto = new BufferedImage(3200, 2400, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 3200; x++) {
            for (int y = 0; y < 2400; y++) {
                foto.setRGB(x, y, ((x * 7 + y * 13) % 256) << 16 | ((x + y) % 256) << 8 | (x * y) % 256);
            }
        }
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(foto, "png", png);
        when(repositorio.save(any(Imagem.class))).thenAnswer(chamada -> chamada.getArgument(0));
        ArgumentCaptor<Imagem> gravada = ArgumentCaptor.forClass(Imagem.class);

        servico.salvarImagem(new MockMultipartFile("foto", "grande.png", "image/png", png.toByteArray()), "arquivoFoto");

        verify(repositorio).save(gravada.capture());
        assertEquals("image/jpeg", gravada.getValue().getTipoConteudo());
        assertTrue(gravada.getValue().getDados().length < png.size());
        BufferedImage resultado = ImageIO.read(new ByteArrayInputStream(gravada.getValue().getDados()));
        assertEquals(1600, resultado.getWidth());
        assertEquals(1200, resultado.getHeight());
    }

    @Test
    void orientacaoExifTrocaOsLadosQuandoGira() {
        BufferedImage paisagem = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);

        BufferedImage girada = UploadService.orientar(paisagem, 6);

        assertEquals(100, girada.getWidth());
        assertEquals(200, girada.getHeight());
        assertEquals(200, UploadService.orientar(paisagem, 3).getWidth());
        assertEquals(1, UploadService.lerOrientacao(png()));
    }

    @Test
    void resumoDeNotaFormataMediaEEstrelas() {
        ResumoNota nota = new ResumoNota(4.26, 3);

        assertEquals("4,3", nota.getMediaTexto());
        assertEquals(4, nota.getEstrelas());
        assertTrue(nota.getTotal() == 3);
    }
}
