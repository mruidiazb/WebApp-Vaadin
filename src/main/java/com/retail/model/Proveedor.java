package com.example.model;

public class Proveedor implements Cloneable {
    private Long id;
    private String empresa;
    private String contacto;
    private String telefono;
    private String ciudad;

    public Proveedor() {}

    public Proveedor(Long id, String empresa, String contacto, String telefono, String ciudad) {
        this.id = id;
        this.empresa = empresa;
        this.contacto = contacto;
        this.telefono = telefono;
        this.ciudad = ciudad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    @Override
    public Proveedor clone() {
        try {
            return (Proveedor) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Proveedor(id, empresa, contacto, telefono, ciudad);
        }
    }
}
