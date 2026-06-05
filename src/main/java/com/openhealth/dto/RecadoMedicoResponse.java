package com.openhealth.dto;

import com.openhealth.model.StatusRecado;

import java.time.Instant;

public class RecadoMedicoResponse {

    private Long id;
    private String titulo;
    private String mensagem;
    private Instant dataCriacao;
    private Instant dataLeitura;
    private StatusRecado status;
    private Long medicoId;
    private String medicoNome;
    private Long pacienteId;
    private String pacienteNome;

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
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public String getMedicoNome() { return medicoNome; }
    public void setMedicoNome(String medicoNome) { this.medicoNome = medicoNome; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getPacienteNome() { return pacienteNome; }
    public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }
}