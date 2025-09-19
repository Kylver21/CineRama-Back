package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private List<Cliente> clientes = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public ClienteService() {
        inicializarDatosPrueba();
    }

    // Métodos CRUD
    public List<Cliente> obtenerTodosLosClientes() {
        return new ArrayList<>(clientes);
    }

    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clientes.stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst();
    }

    public Cliente crearCliente(Cliente cliente) {
        // Verificar email único
        boolean existeEmail = clientes.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(cliente.getEmail()));
        
        if (existeEmail) {
            throw new RuntimeException("Ya existe un cliente con ese email: " + cliente.getEmail());
        }

        cliente.setId(contador.getAndIncrement());
        validarCliente(cliente);
        clientes.add(cliente);
        return cliente;
    }

    public Optional<Cliente> actualizarCliente(Long id, Cliente clienteActualizado) {
        Optional<Cliente> clienteExistente = obtenerClientePorId(id);
        
        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            
            // Verificar email único (excluyendo el cliente actual)
            boolean existeOtroEmail = clientes.stream()
                    .anyMatch(c -> !c.getId().equals(id) && 
                             c.getEmail().equalsIgnoreCase(clienteActualizado.getEmail()));
            
            if (existeOtroEmail) {
                throw new RuntimeException("Ya existe otro cliente con ese email: " + clienteActualizado.getEmail());
            }

            cliente.setNombre(clienteActualizado.getNombre());
            cliente.setEmail(clienteActualizado.getEmail());
            cliente.setTelefono(clienteActualizado.getTelefono());
            
            validarCliente(cliente);
            return Optional.of(cliente);
        }
        
        return Optional.empty();
    }

    public Optional<Cliente> actualizarClienteParcial(Long id, Cliente clienteParcial) {
        Optional<Cliente> clienteExistente = obtenerClientePorId(id);
        
        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            
            if (clienteParcial.getNombre() != null) {
                cliente.setNombre(clienteParcial.getNombre());
            }
            
            if (clienteParcial.getEmail() != null) {
                // Verificar email único
                boolean existeOtroEmail = clientes.stream()
                        .anyMatch(c -> !c.getId().equals(id) && 
                                 c.getEmail().equalsIgnoreCase(clienteParcial.getEmail()));
                
                if (existeOtroEmail) {
                    throw new RuntimeException("Ya existe otro cliente con ese email: " + clienteParcial.getEmail());
                }
                cliente.setEmail(clienteParcial.getEmail());
            }
            
            if (clienteParcial.getTelefono() != null) {
                cliente.setTelefono(clienteParcial.getTelefono());
            }
            
            validarCliente(cliente);
            return Optional.of(cliente);
        }
        
        return Optional.empty();
    }

    public boolean eliminarCliente(Long id) {
        return clientes.removeIf(cliente -> cliente.getId().equals(id));
    }

    // Métodos de búsqueda específicos
    public Optional<Cliente> buscarPorEmail(String email) {
        return clientes.stream()
                .filter(cliente -> cliente.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clientes.stream()
                .filter(cliente -> cliente.getNombre().toLowerCase()
                        .contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<Cliente> buscarPorTelefono(String telefono) {
        return clientes.stream()
                .filter(cliente -> cliente.getTelefono().equals(telefono))
                .findFirst();
    }

    public boolean existeEmail(String email) {
        return clientes.stream()
                .anyMatch(cliente -> cliente.getEmail().equalsIgnoreCase(email));
    }

    public boolean existeTelefono(String telefono) {
        return clientes.stream()
                .anyMatch(cliente -> cliente.getTelefono().equals(telefono));
    }

    // Métodos auxiliares
    private void validarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio");
        }
        
        if (!cliente.emailValido()) {
            throw new RuntimeException("El formato del email no es válido");
        }
        
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }
        
        if (!cliente.telefonoValido()) {
            throw new RuntimeException("El teléfono debe tener 9 dígitos");
        }
    }

    private void inicializarDatosPrueba() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(contador.getAndIncrement());
        cliente1.setNombre("Pedro Gomez");
        cliente1.setEmail("pedro.gomez@email.com");
        cliente1.setTelefono("987654321");

        Cliente cliente2 = new Cliente();
        cliente2.setId(contador.getAndIncrement());
        cliente2.setNombre("Karely García");
        cliente2.setEmail("karely.garcia@email.com");
        cliente2.setTelefono("987654322");

        Cliente cliente3 = new Cliente();
        cliente3.setId(contador.getAndIncrement());
        cliente3.setNombre("Carlos Celis");
        cliente3.setEmail("carlos.celis@email.com");
        cliente3.setTelefono("987654323");

        clientes.add(cliente1);
        clientes.add(cliente2);
        clientes.add(cliente3);
    }
}