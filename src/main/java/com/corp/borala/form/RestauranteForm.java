package com.corp.borala.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.ArrayList;
import java.util.List;

/** Formulário de cadastro e edição de restaurante. */
public class RestauranteForm {

    /** Null ao criar; preenchido ao editar. */
    private Long id;

    @NotBlank(message = "Informe o nome do restaurante")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Informe o CNPJ")
    @CNPJ(message = "CNPJ inválido: confira os 14 dígitos, que precisam formar um CNPJ real. Ex.: 11.222.333/0001-81")
    private String cnpj;

    @NotBlank(message = "Informe o CNAE")
    @Pattern(regexp = "^\\d{4}-?\\d/?\\d{2}$", message = "CNAE inválido. Ex.: 5611-2/01")
    private String cnae;

    @NotBlank(message = "Informe o telefone")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone inválido. Ex.: (67) 3333-4444")
    private String telefone;

    private boolean verificacao;

    // ----- endereço -----
    @NotBlank(message = "Informe a rua ou avenida")
    @Size(max = 255, message = "Texto muito longo")
    private String logradouro;

    @NotBlank(message = "Informe o bairro")
    @Size(max = 255, message = "Texto muito longo")
    private String bairro;

    @NotBlank(message = "Informe a cidade")
    @Size(max = 255, message = "Texto muito longo")
    private String cidade;

    @NotBlank(message = "Informe a UF")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF inválida. Ex.: MS")
    private String uf;

    @NotBlank(message = "Informe o CEP")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido. Ex.: 79002-000")
    private String cep;

    @Size(max = 255, message = "Texto muito longo")
    private String complemento;

    // ----- relacionamentos -----
    /** Só o ADMIN escolhe o dono; no cadastro do dono e na edição pelo próprio dono este campo é ignorado. */
    private Long donoId;

    @NotEmpty(message = "Selecione ao menos um tipo de culinária")
    private List<Long> tiposCulinariaIds = new ArrayList<>();

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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCnae() {
        return cnae;
    }

    public void setCnae(String cnae) {
        this.cnae = cnae;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isVerificacao() {
        return verificacao;
    }

    public void setVerificacao(boolean verificacao) {
        this.verificacao = verificacao;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Long getDonoId() {
        return donoId;
    }

    public void setDonoId(Long donoId) {
        this.donoId = donoId;
    }

    public List<Long> getTiposCulinariaIds() {
        return tiposCulinariaIds;
    }

    public void setTiposCulinariaIds(List<Long> tiposCulinariaIds) {
        this.tiposCulinariaIds = tiposCulinariaIds;
    }
}
