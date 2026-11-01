package com.corp.borala.form;

import com.corp.borala.model.TipoMidia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 * Formulário de mídia do restaurante. Foto é enviada como arquivo; vídeo continua por link (url).
 * Qual dos dois é obrigatório depende do tipo e é conferido em {@code MidiaService}.
 */
public class MidiaForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    /** Link do vídeo. */
    @Size(max = 500, message = "O endereço deve ter no máximo 500 caracteres")
    private String url;

    @NotNull(message = "Selecione o tipo")
    private TipoMidia tipo;

    /** Imagem enviada (tipo foto). */
    private MultipartFile arquivo;

    /** Caminho da foto atual, só para exibir na edição. */
    private String fotoAtual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TipoMidia getTipo() {
        return tipo;
    }

    public void setTipo(TipoMidia tipo) {
        this.tipo = tipo;
    }

    public MultipartFile getArquivo() {
        return arquivo;
    }

    public void setArquivo(MultipartFile arquivo) {
        this.arquivo = arquivo;
    }

    public String getFotoAtual() {
        return fotoAtual;
    }

    public void setFotoAtual(String fotoAtual) {
        this.fotoAtual = fotoAtual;
    }
}
