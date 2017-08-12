package com.thenasker.euvendomais;

public class Venta {
    private String nome;
    private String rotaImg;
    private double valorFin;
    private int limite;
    private String timestamp;
    private String timestampFim;
    private double valorPago;

    public Venta(String nome, String rotaImg, double valorFin,int limite, String timestamp, String timestampFim, double valorPago){
        this.nome = nome;
        this.rotaImg = rotaImg;
        this.valorFin = valorFin;
        this.limite = limite;
        this.timestamp = timestamp;
        this.timestampFim = timestampFim;
        this.valorPago = valorPago;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getValorFin() {
        return valorFin;
    }

    public double getValorPago() {
        return valorPago;
    }

    public int getLimite() {
        return limite;
    }

    public String getNome() {
        return nome;
    }

    public String getRotaImg() {
        return rotaImg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTimestampFim() {
        return timestampFim;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setRotaImg(String rotaImg) {
        this.rotaImg = rotaImg;
    }

    public void setTimestampFim(String timestampFim) {
        this.timestampFim = timestampFim;
    }

    public void setValorFin(double valorFin) {
        this.valorFin = valorFin;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }
}
