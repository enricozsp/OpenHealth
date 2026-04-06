package dev.openhealth.OpenHealth.usuario.entity;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_cadastro_de_usuarios")

public class ModeloUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id_usuario;
    private String nome;
    private String senha;

    @JsonFormat(pattern = "yyy-MM-dd")
    private LocalDate data_nascimento;

    @Column(unique = true)
    private String cpf;

    private String tipo_sanguineo;


    public ModeloUsuario() {
    }

    public ModeloUsuario(Long id_usuario, String nome, LocalDate data_nascimento, String cpf, String tipo_sanguineo, String senha) {
        this.id_usuario = id_usuario;
        this.nome = nome;
        this.data_nascimento = data_nascimento;
        this.cpf = cpf;
        this.tipo_sanguineo = tipo_sanguineo;
        this.senha = senha;
    }

    public String getSenha() {return senha; }

    public void setSenha(String senha) {this.senha = senha; }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(LocalDate data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getTipo_sanguineo() {
        return tipo_sanguineo;
    }

    public void setTipo_sanguineo(String tipo_sanguineo) {
        this.tipo_sanguineo = tipo_sanguineo;
    }
}
