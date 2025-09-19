package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Cliente;
import com.utp.cinerama.cinerama.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // GET /api/clientes - Obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    // GET /api/clientes/{id} - Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.obtenerClientePorId(id);
        
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/clientes - Crear nuevo cliente
    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.crearCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/clientes/{id} - Actualizar cliente completo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Optional<Cliente> clienteActualizado = clienteService.actualizarCliente(id, cliente);
            
            if (clienteActualizado.isPresent()) {
                return ResponseEntity.ok(clienteActualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PATCH /api/clientes/{id} - Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarClienteParcial(@PathVariable Long id, @RequestBody Cliente clienteParcial) {
        try {
            Optional<Cliente> clienteActualizado = clienteService.actualizarClienteParcial(id, clienteParcial);
            
            if (clienteActualizado.isPresent()) {
                return ResponseEntity.ok(clienteActualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/clientes/{id} - Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        boolean eliminado = clienteService.eliminarCliente(id);
        
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // GET /api/clientes/email/{email} - Buscar cliente por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Cliente> buscarPorEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteService.buscarPorEmail(email);
        
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/clientes/telefono/{telefono} - Buscar cliente por teléfono
    @GetMapping("/telefono/{telefono}")
    public ResponseEntity<Cliente> buscarPorTelefono(@PathVariable String telefono) {
        Optional<Cliente> cliente = clienteService.buscarPorTelefono(telefono);
        
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/clientes/buscar?nombre=juan - Buscar clientes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarClientes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono) {
        
        List<Cliente> clientes;
        
        if (nombre != null) {
            clientes = clienteService.buscarPorNombre(nombre);
        } else if (email != null) {
            Optional<Cliente> cliente = clienteService.buscarPorEmail(email);
            clientes = cliente.map(List::of).orElse(List.of());
        } else if (telefono != null) {
            Optional<Cliente> cliente = clienteService.buscarPorTelefono(telefono);
            clientes = cliente.map(List::of).orElse(List.of());
        } else {
            clientes = clienteService.obtenerTodosLosClientes();
        }
        
        return ResponseEntity.ok(clientes);
    }

    // GET /api/clientes/validar/email/{email} - Verificar si existe un email
    @GetMapping("/validar/email/{email}")
    public ResponseEntity<?> validarEmail(@PathVariable String email) {
        boolean existe = clienteService.existeEmail(email);
        return ResponseEntity.ok("{\"existe\": " + existe + "}");
    }

    // GET /api/clientes/validar/telefono/{telefono} - Verificar si existe un teléfono
    @GetMapping("/validar/telefono/{telefono}")
    public ResponseEntity<?> validarTelefono(@PathVariable String telefono) {
        boolean existe = clienteService.existeTelefono(telefono);
        return ResponseEntity.ok("{\"existe\": " + existe + "}");
    }
}