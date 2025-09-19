package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Boleto;
import com.utp.cinerama.cinerama.model.Boleto.EstadoBoleto;
import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.model.Funcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BoletoService {

    @Autowired
    private FuncionService funcionService;
    
    @Autowired
    private ClienteService clienteService;

    private List<Boleto> boletos = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public BoletoService() {
        // Los datos de prueba se inicializan después de la inyección de dependencias
    }

    // Método para inicializar datos después de la inyección
    public void inicializarDatosPrueba() {
        if (boletos.isEmpty() && funcionService != null && clienteService != null) {
            List<Funcion> funciones = funcionService.obtenerTodasLasFunciones();
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            if (!funciones.isEmpty() && !clientes.isEmpty()) {
                crearBoletosPrueba(funciones, clientes);
            }
        }
    }

    // Métodos CRUD
    public List<Boleto> obtenerTodosLosBoletos() {
        inicializarDatosPrueba(); // Asegurar que hay datos
        return new ArrayList<>(boletos);
    }

    public Optional<Boleto> obtenerBoletoPorId(Long id) {
        return boletos.stream()
                .filter(boleto -> boleto.getId().equals(id))
                .findFirst();
    }

    public Boleto crearBoleto(Boleto boleto) {
        validarBoleto(boleto);
        
        // Verificar que el asiento no esté ocupado en esa función
        boolean asientoOcupado = boletos.stream()
                .anyMatch(b -> b.getFuncion().getId().equals(boleto.getFuncion().getId()) &&
                          b.getAsiento().equals(boleto.getAsiento()) &&
                          b.esValido());
        
        if (asientoOcupado) {
            throw new RuntimeException("El asiento " + boleto.getAsiento() + " ya está ocupado para esta función");
        }

        // Verificar que la función tenga asientos disponibles
        if (!boleto.getFuncion().hayAsientosDisponibles()) {
            throw new RuntimeException("No hay asientos disponibles para esta función");
        }

        // Reservar asiento en la función
        funcionService.reservarAsiento(boleto.getFuncion().getId());

        boleto.setId(contador.getAndIncrement());
        boleto.setFechaCompra(LocalDateTime.now());
        
        // Si no se especifica estado, por defecto es RESERVADO
        if (boleto.getEstado() == null) {
            boleto.setEstado(EstadoBoleto.RESERVADO);
        }

        boletos.add(boleto);
        return boleto;
    }

    public Optional<Boleto> actualizarBoleto(Long id, Boleto boletoActualizado) {
        Optional<Boleto> boletoExistente = obtenerBoletoPorId(id);
        
        if (boletoExistente.isPresent()) {
            Boleto boleto = boletoExistente.get();
            
            // Verificar si se cambia el asiento
            if (!boleto.getAsiento().equals(boletoActualizado.getAsiento()) ||
                !boleto.getFuncion().getId().equals(boletoActualizado.getFuncion().getId())) {
                
                // Verificar que el nuevo asiento no esté ocupado
                boolean asientoOcupado = boletos.stream()
                        .anyMatch(b -> !b.getId().equals(id) &&
                                  b.getFuncion().getId().equals(boletoActualizado.getFuncion().getId()) &&
                                  b.getAsiento().equals(boletoActualizado.getAsiento()) &&
                                  b.esValido());
                
                if (asientoOcupado) {
                    throw new RuntimeException("El asiento " + boletoActualizado.getAsiento() + " ya está ocupado");
                }

                // Liberar asiento anterior si se cambia de función
                if (!boleto.getFuncion().getId().equals(boletoActualizado.getFuncion().getId())) {
                    funcionService.liberarAsiento(boleto.getFuncion().getId());
                    funcionService.reservarAsiento(boletoActualizado.getFuncion().getId());
                }
            }

            boleto.setFuncion(boletoActualizado.getFuncion());
            boleto.setAsiento(boletoActualizado.getAsiento());
            boleto.setPrecio(boletoActualizado.getPrecio());
            boleto.setEstado(boletoActualizado.getEstado());
            boleto.setCliente(boletoActualizado.getCliente());
            
            validarBoleto(boleto);
            return Optional.of(boleto);
        }
        
        return Optional.empty();
    }

    public boolean eliminarBoleto(Long id) {
        Optional<Boleto> boleto = obtenerBoletoPorId(id);
        if (boleto.isPresent()) {
            // Liberar asiento si el boleto es válido
            if (boleto.get().esValido()) {
                funcionService.liberarAsiento(boleto.get().getFuncion().getId());
            }
            return boletos.removeIf(b -> b.getId().equals(id));
        }
        return false;
    }

    // Métodos de negocio específicos
    public boolean pagarBoleto(Long id) {
        Optional<Boleto> boleto = obtenerBoletoPorId(id);
        if (boleto.isPresent() && boleto.get().getEstado() == EstadoBoleto.RESERVADO) {
            boleto.get().setEstado(EstadoBoleto.PAGADO);
            return true;
        }
        return false;
    }

    public boolean cancelarBoleto(Long id) {
        Optional<Boleto> boleto = obtenerBoletoPorId(id);
        if (boleto.isPresent() && boleto.get().puedeSerCancelado()) {
            boleto.get().cancelar();
            // Liberar asiento en la función
            funcionService.liberarAsiento(boleto.get().getFuncion().getId());
            return true;
        }
        return false;
    }

    public boolean marcarBoletoComoUsado(Long id) {
        Optional<Boleto> boleto = obtenerBoletoPorId(id);
        if (boleto.isPresent() && boleto.get().getEstado() == EstadoBoleto.PAGADO) {
            boleto.get().marcarComoUsado();
            return true;
        }
        return false;
    }

    // Métodos de búsqueda específicos
    public List<Boleto> buscarPorCliente(Long clienteId) {
        return boletos.stream()
                .filter(boleto -> boleto.getCliente() != null && 
                                 boleto.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());
    }

    public List<Boleto> buscarPorFuncion(Long funcionId) {
        return boletos.stream()
                .filter(boleto -> boleto.getFuncion().getId().equals(funcionId))
                .collect(Collectors.toList());
    }

    public List<Boleto> buscarPorEstado(EstadoBoleto estado) {
        return boletos.stream()
                .filter(boleto -> boleto.getEstado() == estado)
                .collect(Collectors.toList());
    }

    public List<Boleto> buscarBoletosValidos() {
        return boletos.stream()
                .filter(Boleto::esValido)
                .collect(Collectors.toList());
    }

    public Optional<Boleto> buscarPorFuncionYAsiento(Long funcionId, String asiento) {
        return boletos.stream()
                .filter(boleto -> boleto.getFuncion().getId().equals(funcionId) &&
                                 boleto.getAsiento().equals(asiento) &&
                                 boleto.esValido())
                .findFirst();
    }

    public List<String> obtenerAsientosOcupados(Long funcionId) {
        return boletos.stream()
                .filter(boleto -> boleto.getFuncion().getId().equals(funcionId) &&
                                 boleto.esValido())
                .map(Boleto::getAsiento)
                .collect(Collectors.toList());
    }

    // Métodos de estadísticas
    public BigDecimal calcularIngresosPorFuncion(Long funcionId) {
        return boletos.stream()
                .filter(boleto -> boleto.getFuncion().getId().equals(funcionId) &&
                                 boleto.getEstado() == EstadoBoleto.PAGADO)
                .map(Boleto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long contarBoletosVendidos(Long funcionId) {
        return boletos.stream()
                .filter(boleto -> boleto.getFuncion().getId().equals(funcionId) &&
                                 boleto.getEstado() == EstadoBoleto.PAGADO)
                .count();
    }

    // Métodos auxiliares
    private void validarBoleto(Boleto boleto) {
        if (boleto.getFuncion() == null) {
            throw new RuntimeException("La función es obligatoria");
        }
        
        if (boleto.getAsiento() == null || boleto.getAsiento().trim().isEmpty()) {
            throw new RuntimeException("El asiento es obligatorio");
        }
        
        if (boleto.getPrecio() == null || boleto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }
        
        if (boleto.getCliente() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        // Validar formato del asiento (ej: A1, B5, etc.)
        if (!boleto.getAsiento().matches("^[A-Z]\\d+$")) {
            throw new RuntimeException("El formato del asiento debe ser una letra seguida de números (ej: A1, B5)");
        }
    }

    private void crearBoletosPrueba(List<Funcion> funciones, List<Cliente> clientes) {
        if (funciones.size() >= 2 && clientes.size() >= 2) {
            // Boleto 1
            Boleto boleto1 = new Boleto();
            boleto1.setId(contador.getAndIncrement());
            boleto1.setFuncion(funciones.get(0)); // Primera función
            boleto1.setAsiento("A1");
            boleto1.setPrecio(funciones.get(0).getPelicula().getPrecio());
            boleto1.setEstado(EstadoBoleto.PAGADO);
            boleto1.setFechaCompra(LocalDateTime.now().minusHours(2));
            boleto1.setCliente(clientes.get(0)); // Primer cliente

            // Boleto 2
            Boleto boleto2 = new Boleto();
            boleto2.setId(contador.getAndIncrement());
            boleto2.setFuncion(funciones.get(0)); // Primera función
            boleto2.setAsiento("A2");
            boleto2.setPrecio(funciones.get(0).getPelicula().getPrecio());
            boleto2.setEstado(EstadoBoleto.RESERVADO);
            boleto2.setFechaCompra(LocalDateTime.now().minusHours(1));
            boleto2.setCliente(clientes.get(1)); // Segundo cliente

            // Boleto 3
            Boleto boleto3 = new Boleto();
            boleto3.setId(contador.getAndIncrement());
            boleto3.setFuncion(funciones.get(1)); // Segunda función
            boleto3.setAsiento("B1");
            boleto3.setPrecio(funciones.get(1).getPelicula().getPrecio());
            boleto3.setEstado(EstadoBoleto.PAGADO);
            boleto3.setFechaCompra(LocalDateTime.now().minusMinutes(30));
            boleto3.setCliente(clientes.get(0)); // Primer cliente

            boletos.add(boleto1);
            boletos.add(boleto2);
            boletos.add(boleto3);

            // Actualizar asientos disponibles en las funciones
            funcionService.reservarAsiento(funciones.get(0).getId()); // Para boleto1
            funcionService.reservarAsiento(funciones.get(0).getId()); // Para boleto2
            funcionService.reservarAsiento(funciones.get(1).getId()); // Para boleto3
        }
    }
}
