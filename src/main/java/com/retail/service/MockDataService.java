package com.example.service;

import com.example.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MockDataService {

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();
    private final List<Proveedor> proveedores = new ArrayList<>();
    private final List<Factura> facturas = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();

    private long clienteIdSeq = 1;
    private long productoIdSeq = 1;
    private long proveedorIdSeq = 1;
    private long facturaIdSeq = 1;
    private long usuarioIdSeq = 1;

    public MockDataService() {
        initMockData();
    }

    private void initMockData() {
        // Clientes
        saveCliente(new Cliente(null, "Juan Pérez", "juan.perez@example.com", "+57 300 123 4567", "Tech Solutions S.A.S."));
        saveCliente(new Cliente(null, "María Gómez", "maria.gomez@example.com", "+57 311 987 6543", "Innova Global"));
        saveCliente(new Cliente(null, "Carlos Rodríguez", "carlos.rod@example.com", "+57 315 555 7788", "Constructor del Norte"));
        saveCliente(new Cliente(null, "Ana Martínez", "ana.martinez@example.com", "+57 320 444 2211", "Diseños & Estilos"));

        // Productos
        saveProducto(new Producto(null, "Laptop Gamer ASUS", "Tecnología", 1200.0, 15));
        saveProducto(new Producto(null, "Monitor UltraWide 34\"", "Tecnología", 450.0, 8));
        saveProducto(new Producto(null, "Silla Ergonómica Pro", "Muebles", 250.0, 20));
        saveProducto(new Producto(null, "Teclado Mecánico RGB", "Accesorios", 85.0, 50));
        saveProducto(new Producto(null, "Mouse Inalámbrico Ergo", "Accesorios", 45.0, 120));

        // Proveedores
        saveProveedor(new Proveedor(null, "DistriTech Ltda.", "Alejandro Silva", "+57 301 222 3344", "Bogotá"));
        saveProveedor(new Proveedor(null, "Muebles Finos Inc.", "Beatriz Ortiz", "+57 302 333 4455", "Medellín"));
        saveProveedor(new Proveedor(null, "Accesorios del Pacífico", "Camilo Pérez", "+57 303 444 5566", "Cali"));
        saveProveedor(new Proveedor(null, "Importaciones Globales", "Diana Restrepo", "+57 304 555 6677", "Barranquilla"));

        // Facturas
        saveFactura(new Factura(null, "Juan Pérez", LocalDate.of(2026, 5, 10), 1200.0, "Pagada"));
        saveFactura(new Factura(null, "María Gómez", LocalDate.of(2026, 5, 12), 450.0, "Pendiente"));
        saveFactura(new Factura(null, "Carlos Rodríguez", LocalDate.of(2026, 5, 15), 250.0, "Pagada"));
        saveFactura(new Factura(null, "Ana Martínez", LocalDate.of(2026, 5, 18), 130.0, "Pendiente"));

        // Usuarios
        saveUsuario(new Usuario(null, "Administrador Sistema", "admin", "Administrador"));
        saveUsuario(new Usuario(null, "Juan Editor", "editor", "Editor"));
        saveUsuario(new Usuario(null, "Carolina Soporte", "soporte", "Soporte"));
    }

    // --- CLIENTE CRUD ---
    public List<Cliente> getClientes(String filter) {
        if (filter == null || filter.isEmpty()) {
            return clientes.stream().map(Cliente::clone).collect(Collectors.toList());
        }
        String lowerFilter = filter.toLowerCase();
        return clientes.stream()
                .filter(c -> (c.getNombre() != null && c.getNombre().toLowerCase().contains(lowerFilter)) ||
                             (c.getEmpresa() != null && c.getEmpresa().toLowerCase().contains(lowerFilter)))
                .map(Cliente::clone)
                .collect(Collectors.toList());
    }

    public Optional<Cliente> getCliente(Long id) {
        return clientes.stream().filter(c -> c.getId().equals(id)).map(Cliente::clone).findFirst();
    }

    public synchronized void saveCliente(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(clienteIdSeq++);
            clientes.add(cliente.clone());
        } else {
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId().equals(cliente.getId())) {
                    clientes.set(i, cliente.clone());
                    return;
                }
            }
        }
    }

    public synchronized void deleteCliente(Long id) {
        clientes.removeIf(c -> c.getId().equals(id));
    }

    // --- PRODUCTO CRUD ---
    public List<Producto> getProductos(String filter) {
        if (filter == null || filter.isEmpty()) {
            return productos.stream().map(Producto::clone).collect(Collectors.toList());
        }
        String lowerFilter = filter.toLowerCase();
        return productos.stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(lowerFilter)) ||
                             (p.getCategoria() != null && p.getCategoria().toLowerCase().contains(lowerFilter)))
                .map(Producto::clone)
                .collect(Collectors.toList());
    }

    public Optional<Producto> getProducto(Long id) {
        return productos.stream().filter(p -> p.getId().equals(id)).map(Producto::clone).findFirst();
    }

    public synchronized void saveProducto(Producto producto) {
        if (producto.getId() == null) {
            producto.setId(productoIdSeq++);
            productos.add(producto.clone());
        } else {
            for (int i = 0; i < productos.size(); i++) {
                if (productos.get(i).getId().equals(producto.getId())) {
                    productos.set(i, producto.clone());
                    return;
                }
            }
        }
    }

    public synchronized void deleteProducto(Long id) {
        productos.removeIf(p -> p.getId().equals(id));
    }

    // --- PROVEEDOR CRUD ---
    public List<Proveedor> getProveedores(String filter) {
        if (filter == null || filter.isEmpty()) {
            return proveedores.stream().map(Proveedor::clone).collect(Collectors.toList());
        }
        String lowerFilter = filter.toLowerCase();
        return proveedores.stream()
                .filter(p -> (p.getEmpresa() != null && p.getEmpresa().toLowerCase().contains(lowerFilter)) ||
                             (p.getContacto() != null && p.getContacto().toLowerCase().contains(lowerFilter)))
                .map(Proveedor::clone)
                .collect(Collectors.toList());
    }

    public Optional<Proveedor> getProveedor(Long id) {
        return proveedores.stream().filter(p -> p.getId().equals(id)).map(Proveedor::clone).findFirst();
    }

    public synchronized void saveProveedor(Proveedor proveedor) {
        if (proveedor.getId() == null) {
            proveedor.setId(proveedorIdSeq++);
            proveedores.add(proveedor.clone());
        } else {
            for (int i = 0; i < proveedores.size(); i++) {
                if (proveedores.get(i).getId().equals(proveedor.getId())) {
                    proveedores.set(i, proveedor.clone());
                    return;
                }
            }
        }
    }

    public synchronized void deleteProveedor(Long id) {
        proveedores.removeIf(p -> p.getId().equals(id));
    }

    // --- FACTURA CRUD ---
    public List<Factura> getFacturas(String filter) {
        if (filter == null || filter.isEmpty()) {
            return facturas.stream().map(Factura::clone).collect(Collectors.toList());
        }
        String lowerFilter = filter.toLowerCase();
        return facturas.stream()
                .filter(f -> (f.getCliente() != null && f.getCliente().toLowerCase().contains(lowerFilter)) ||
                             (f.getEstado() != null && f.getEstado().toLowerCase().contains(lowerFilter)))
                .map(Factura::clone)
                .collect(Collectors.toList());
    }

    public Optional<Factura> getFactura(Long id) {
        return facturas.stream().filter(f -> f.getId().equals(id)).map(Factura::clone).findFirst();
    }

    public synchronized void saveFactura(Factura factura) {
        if (factura.getId() == null) {
            factura.setId(facturaIdSeq++);
            facturas.add(factura.clone());
        } else {
            for (int i = 0; i < facturas.size(); i++) {
                if (facturas.get(i).getId().equals(factura.getId())) {
                    facturas.set(i, factura.clone());
                    return;
                }
            }
        }
    }

    public synchronized void deleteFactura(Long id) {
        facturas.removeIf(f -> f.getId().equals(id));
    }

    // --- USUARIO CRUD ---
    public List<Usuario> getUsuarios(String filter) {
        if (filter == null || filter.isEmpty()) {
            return usuarios.stream().map(Usuario::clone).collect(Collectors.toList());
        }
        String lowerFilter = filter.toLowerCase();
        return usuarios.stream()
                .filter(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(lowerFilter)) ||
                             (u.getUsername() != null && u.getUsername().toLowerCase().contains(lowerFilter)))
                .map(Usuario::clone)
                .collect(Collectors.toList());
    }

    public Optional<Usuario> getUsuario(Long id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).map(Usuario::clone).findFirst();
    }

    public synchronized void saveUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(usuarioIdSeq++);
            usuarios.add(usuario.clone());
        } else {
            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i).getId().equals(usuario.getId())) {
                    usuarios.set(i, usuario.clone());
                    return;
                }
            }
        }
    }

    public synchronized void deleteUsuario(Long id) {
        usuarios.removeIf(u -> u.getId().equals(id));
    }
}
