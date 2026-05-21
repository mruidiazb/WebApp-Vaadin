package com.example.model;

public class Usuario implements Cloneable {
    private Long id;
    private String nombre;
    private String username;
    private String rol;

    public Usuario() {}

    public Usuario(Long id, String nombre, String username, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public Usuario clone() {
        try {
            return (Usuario) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Usuario(id, nombre, username, rol);
        }
    }
}
