package dev.openhealth.OpenHealth.usuario.dto;

public class UserResponseDTO {

    private String mensagem;

    public UserResponseDTO(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
