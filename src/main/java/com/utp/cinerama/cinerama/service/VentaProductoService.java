package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.model.Producto;
import com.utp.cinerama.cinerama.model.VentaProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class VentaProductoService {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ClienteService clienteService;

    private List<VentaProducto> ventas = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public VentaProductoService() {
        // Los datos de prueba se inicializan después de la inyección
    }

    // Métodos CRUD
    public List<VentaProducto> obtenerTodasLasVentas() {
        return new ArrayList<>(ventas);
    }

    public Optional<VentaProducto> obtenerVentaPorId(Long id) {
        return ventas.stream()
                .filter(venta -> venta.getId().equals(id))
                .findFirst();
    }

    public VentaProducto crearVenta(Cliente cliente, String metodoPago) {
        VentaProducto venta = new VentaProducto(contador.getAndIncrement(), cliente, metodoPago);
        ventas.add(venta);
        return venta;
    }

    // Métodos para gestionar los productos en la venta
    public VentaProducto agregarProducto(Long ventaId, Long productoId, Integer cantidad) {
        Optional<VentaProducto> ventaOpt = obtenerVentaPorId(ventaId);
        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(productoId);
        
        if (ventaOpt.isEmpty() || productoOpt.isEmpty()) {
            throw new RuntimeException("Venta o producto no encontrado");
        }
        
        VentaProducto venta = ventaOpt.get();
        Producto producto = productoOpt.get();
        
        if (venta.getCompletada()) {
            throw new RuntimeException("No se puede modificar una venta completada");
        }
        
        if (!producto.tieneStock() || producto.getStock() < cantidad) {
            throw new RuntimeException("No hay suficiente stock para " + producto.getNombre());
        }
        
        venta.agregarProducto(producto, cantidad);
        return venta;
    }

    public VentaProducto eliminarProducto(Long ventaId, Long productoId) {
        Optional<VentaProducto> ventaOpt = obtenerVentaPorId(ventaId);
        
        if (ventaOpt.isEmpty()) {
            throw new RuntimeException("Venta no encontrada");
        }
        
        VentaProducto venta = ventaOpt.get();
        
        if (venta.getCompletada()) {
            throw new RuntimeException("No se puede modificar una venta completada");
        }
        
        venta.eliminarProducto(productoId);
        return venta;
    }

    public VentaProducto actualizarCantidadProducto(Long ventaId, Long productoId, Integer nuevaCantidad) {
        Optional<VentaProducto> ventaOpt = obtenerVentaPorId(ventaId);
        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(productoId);
        
        if (ventaOpt.isEmpty() || productoOpt.isEmpty()) {
            throw new RuntimeException("Venta o producto no encontrado");
        }
        
        VentaProducto venta = ventaOpt.get();
        Producto producto = productoOpt.get();
        
        if (venta.getCompletada()) {
            throw new RuntimeException("No se puede modificar una venta completada");
        }
        
        if (nuevaCantidad > 0 && (producto.getStock() < nuevaCantidad)) {
            throw new RuntimeException("No hay suficiente stock para " + producto.getNombre());
        }
        
        venta.actualizarCantidadProducto(productoId, nuevaCantidad);
        return venta;
    }

    // Métodos para completar la venta
    public VentaProducto completarVenta(Long ventaId) {
        Optional<VentaProducto> ventaOpt = obtenerVentaPorId(ventaId);
        
        if (ventaOpt.isEmpty()) {
            throw new RuntimeException("Venta no encontrada");
        }
        
        VentaProducto venta = ventaOpt.get();
        
        if (venta.getCompletada()) {
            throw new RuntimeException("La venta ya está completada");
        }
        
        if (venta.getDetalles().isEmpty()) {
            throw new RuntimeException("No se puede completar una venta sin productos");
        }
        
        // Verificar stock y actualizar inventario
        for (VentaProducto.DetalleVentaProducto detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            Integer cantidad = detalle.getCantidad();
            
            if (!productoService.reducirStock(producto.getId(), cantidad)) {
                throw new RuntimeException("No hay suficiente stock para " + producto.getNombre());
            }
        }
        
        venta.completarVenta();
        return venta;
    }

    // Métodos de búsqueda
    public List<VentaProducto> buscarPorCliente(Long clienteId) {
        return ventas.stream()
                .filter(venta -> venta.getCliente() != null && 
                               venta.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());
    }

    public List<VentaProducto> buscarPorFecha(LocalDate fecha) {
        return ventas.stream()
                .filter(venta -> venta.getFechaVenta().toLocalDate().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<VentaProducto> buscarVentasCompletadas() {
        return ventas.stream()
                .filter(VentaProducto::getCompletada)
                .collect(Collectors.toList());
    }

    public List<VentaProducto> buscarVentasPendientes() {
        return ventas.stream()
                .filter(venta -> !venta.getCompletada())
                .collect(Collectors.toList());
    }

    // Métodos de inicialización de datos de prueba
    public void inicializarDatosPrueba() {
        if (ventas.isEmpty() && clienteService != null && productoService != null) {
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            List<Producto> productos = productoService.obtenerTodosLosProductos();
            
            if (!clientes.isEmpty() && !productos.isEmpty()) {
                crearVentasPrueba(clientes, productos);
            }
        }
    }

    private void crearVentasPrueba(List<Cliente> clientes, List<Producto> productos) {
        if (clientes.size() >= 2 && productos.size() >= 3) {
            // Venta 1 - Completada
            VentaProducto venta1 = crearVenta(clientes.get(0), "EFECTIVO");
            venta1.agregarProducto(productos.get(0), 2);  // 2 Palomitas grandes
            venta1.agregarProducto(productos.get(1), 2);  // 2 Coca-Cola grandes
            venta1.completarVenta();
            
            // Venta 2 - Pendiente
            VentaProducto venta2 = crearVenta(clientes.get(1), "TARJETA");
            venta2.agregarProducto(productos.get(2), 1);  // 1 Chocolate M&Ms
            venta2.agregarProducto(productos.get(3), 2);  // 2 Gomitas surtidas
            
            // Reducir stock para las ventas de prueba
            productoService.reducirStock(productos.get(0).getId(), 2);
            productoService.reducirStock(productos.get(1).getId(), 2);
        }
    }
}