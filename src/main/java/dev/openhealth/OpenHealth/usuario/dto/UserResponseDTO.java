package dev.openhealth.OpenHealth.usuario.dto;

public class UserResponseDTO {

    private String mensagem;
    private String nome;

    public UserResponseDTO(String mensagem) {
        this.mensagem = mensagem;
    }

    public UserResponseDTO(String mensagem, String nome) {
        this.mensagem = mensagem;
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
