package com.corp.borala.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/** Formulário de cadastro e edição de prato. */
public class PratoForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    @NotBlank(message = "Informe o nome do prato")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Informe a descrição ou os ingredientes")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @NotNull(message = "Informe o preço")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Valor inválido")
    private BigDecimal preco;

    /** Um prato novo começa disponível. */
    private boolean disponibilidade = true;

    @NotNull(message = "Informe quantas pessoas o prato serve")
    @Min(value = 1, message = "O prato deve servir ao menos 1 pessoa")
    @Max(value = 100, message = "Valor muito alto")
    private Integer rendimento;

    /** Caminho da foto atual, só para exibir na edição; o serviço usa o valor guardado no banco. */
    private String foto;

    /** Nova foto enviada (opcional). */
    private MultipartFile arquivoFoto;

    /** Marcado na edição para tirar a foto do prato. */
    private boolean removerFoto;

    @NotNull(message = "Selecione a categoria")
    private Long categoriaId;

    @NotNull(message = "Selecione o tipo de culinária")
    private Long tipoCulinariaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public boolean isDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(boolean disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Integer getRendimento() {
        return rendimento;
    }

    public void setRendimento(Integer rendimento) {
        this.rendimento = rendimento;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public MultipartFile getArquivoFoto() {
        return arquivoFoto;
    }

    public void setArquivoFoto(MultipartFile arquivoFoto) {
        this.arquivoFoto = arquivoFoto;
    }

    public boolean isRemoverFoto() {
        return removerFoto;
    }

    public void setRemoverFoto(boolean removerFoto) {
        this.removerFoto = removerFoto;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getTipoCulinariaId() {
        return tipoCulinariaId;
    }

    public void setTipoCulinariaId(Long tipoCulinariaId) {
        this.tipoCulinariaId = tipoCulinariaId;
    }
}
