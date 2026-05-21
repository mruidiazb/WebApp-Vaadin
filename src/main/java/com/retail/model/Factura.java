package com.example.model;

import java.time.LocalDate;

public class Factura implements Cloneable {
    private Long id;
    private String cliente;
    private LocalDate fecha;
    private Double total;
    private String estado;

    public Factura() {}

    public Factura(Long id, String cliente, LocalDate fecha, Double total, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public Factura clone() {
        try {
            return (Factura) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Factura(id, cliente, fecha, total, estado);
        }
    }
}
