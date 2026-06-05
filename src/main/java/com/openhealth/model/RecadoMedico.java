package com.openhealth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "recado_medico")
public class RecadoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 4000)
    private String mensagem;

    @Column(name = "data_criacao", nullable = false)
    private Instant dataCriacao;

    @Column(name = "data_leitura")
    private Instant dataLeitura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private StatusRecado status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Profile paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Doctor medico;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public Instant getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Instant dataCriacao) { this.dataCriacao = dataCriacao; }
    public Instant getDataLeitura() { return dataLeitura; }
    public void setDataLeitura(Instant dataLeitura) { this.dataLeitura = dataLeitura; }
    public StatusRecado getStatus() { return status; }
    public void setStatus(StatusRecado status) { this.status = status; }
    public Profile getPaciente() { return paciente; }
    public void setPaciente(Profile paciente) { this.paciente = paciente; }
    public Doctor getMedico() { return medico; }
    public void setMedico(Doctor medico) { this.medico = medico; }
}