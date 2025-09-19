package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.model.Pago;
import com.utp.cinerama.cinerama.model.Boleto;
import com.utp.cinerama.cinerama.model.VentaProducto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private BoletoService boletoService;
    
    @Autowired
    private VentaProductoService ventaProductoService;

    private List<Pago> pagos = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public PagoService() {
        // Constructor vacío
    }

    // Métodos CRUD básicos
    public List<Pago> obtenerTodosLosPagos() {
        return new ArrayList<>(pagos);
    }

    public Optional<Pago> obtenerPagoPorId(Long id) {
        return pagos.stream()
                .filter(pago -> pago.getId().equals(id))
                .findFirst();
    }
    
    public Optional<Pago> obtenerPagoPorReferencia(String referencia) {
        return pagos.stream()
                .filter(pago -> pago.getReferencia().equals(referencia))
                .findFirst();
    }

    // Crear pago para boletos
    public Pago crearPagoParaBoletos(List<Long> boletosIds, Cliente cliente, 
                                     Pago.MetodoPago metodoPago, 
                                     Pago.TipoComprobante tipoComprobante) {
        
        // Verificar que todos los boletos existen
        List<Boleto> boletos = new ArrayList<>();
        for (Long boletoId : boletosIds) {
            Optional<Boleto> boletoOpt = boletoService.obtenerBoletoPorId(boletoId);
            if (boletoOpt.isEmpty()) {
                throw new RuntimeException("Boleto no encontrado: " + boletoId);
            }
            boletos.add(boletoOpt.get());
        }
        
        // Calcular monto total
        BigDecimal montoTotal = boletos.stream()
                .map(Boleto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Crear pago
        Pago pago = new Pago(contador.getAndIncrement(), montoTotal, cliente, 
                           metodoPago, null, tipoComprobante);
        
        pagos.add(pago);
        return pago;
    }

    // Crear pago para productos
    public Pago crearPagoParaVentaProducto(Long ventaProductoId, Cliente cliente,
                                          Pago.MetodoPago metodoPago, 
                                          Pago.TipoComprobante tipoComprobante) {
        
        // Verificar que la venta existe
        Optional<VentaProducto> ventaOpt = ventaProductoService.obtenerVentaPorId(ventaProductoId);
        if (ventaOpt.isEmpty()) {
            throw new RuntimeException("Venta no encontrada: " + ventaProductoId);
        }
        
        VentaProducto venta = ventaOpt.get();
        
        // Crear pago
        Pago pago = new Pago(contador.getAndIncrement(), venta.getTotal(), cliente, 
                           metodoPago, null, tipoComprobante);
        
        pagos.add(pago);
        return pago;
    }

    // Procesar pago con tarjeta
    public Pago procesarPagoTarjeta(Long pagoId, String numeroTarjeta) {
        Optional<Pago> pagoOpt = obtenerPagoPorId(pagoId);
        
        if (pagoOpt.isEmpty()) {
            throw new RuntimeException("Pago no encontrado: " + pagoId);
        }
        
        Pago pago = pagoOpt.get();
        pago.setNumeroTarjeta(numeroTarjeta);
        pago.completarPago();
        
        return pago;
    }

    // Procesar pago con apps (Yape/Plin)
    public Pago procesarPagoApp(Long pagoId) {
        Optional<Pago> pagoOpt = obtenerPagoPorId(pagoId);
        
        if (pagoOpt.isEmpty()) {
            throw new RuntimeException("Pago no encontrado: " + pagoId);
        }
        
        Pago pago = pagoOpt.get();
        pago.completarPago();
        
        return pago;
    }

    // Búsqueda de pagos por cliente
    public List<Pago> buscarPagosPorCliente(Long clienteId) {
        return pagos.stream()
                .filter(pago -> pago.getCliente() != null && pago.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());
    }

    // Búsqueda de pagos por fecha
    public List<Pago> buscarPagosPorFecha(LocalDate fecha) {
        return pagos.stream()
                .filter(pago -> pago.getFechaPago() != null &&
                               pago.getFechaPago().toLocalDate().equals(fecha))
                .collect(Collectors.toList());
    }

    // Búsqueda de pagos por estado
    public List<Pago> buscarPagosPorEstado(Pago.EstadoPago estado) {
        return pagos.stream()
                .filter(pago -> pago.getEstado() == estado)
                .collect(Collectors.toList());
    }

    // Reportes - Total de ventas por día
    public BigDecimal calcularTotalVentasPorFecha(LocalDate fecha) {
        return pagos.stream()
                .filter(pago -> pago.getEstado() == Pago.EstadoPago.COMPLETADO &&
                              pago.getFechaPago() != null &&
                              pago.getFechaPago().toLocalDate().equals(fecha))
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Datos de prueba
    public void inicializarDatosPrueba() {
        if (pagos.isEmpty() && clienteService != null) {
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            
            if (!clientes.isEmpty()) {
                Cliente cliente = clientes.get(0);
                
                // Pago con tarjeta
                Pago pago1 = new Pago();
                pago1.setId(contador.getAndIncrement());
                pago1.setCliente(cliente);
                pago1.setMonto(new BigDecimal("63.00"));
                pago1.setMetodo(Pago.MetodoPago.TARJETA_CREDITO);
                pago1.setNumeroTarjeta("4111111111111111");
                pago1.setTipoComprobante(Pago.TipoComprobante.BOLETA);
                pago1.completarPago();
                
                // Pago con Yape
                Pago pago2 = new Pago();
                pago2.setId(contador.getAndIncrement());
                pago2.setCliente(cliente);
                pago2.setMonto(new BigDecimal("33.00"));
                pago2.setMetodo(Pago.MetodoPago.APP_YAPE);
                pago2.setTipoComprobante(Pago.TipoComprobante.BOLETA);
                pago2.completarPago();
                
                pagos.add(pago1);
                pagos.add(pago2);
            }
        }
    }
}