package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funciones")
@CrossOrigin(origins = "*")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    // GET /api/funciones - Obtener todas las funciones
    @GetMapping
    public ResponseEntity<List<Funcion>> obtenerTodasLasFunciones() {
        List<Funcion> funciones = funcionService.obtenerTodasLasFunciones();
        return ResponseEntity.ok(funciones);
    }

    // GET /api/funciones/{id} - Obtener función por ID
    @GetMapping("/{id}")
    public ResponseEntity<Funcion> obtenerFuncionPorId(@PathVariable Long id) {
        Optional<Funcion> funcion = funcionService.obtenerFuncionPorId(id);
        
        if (funcion.isPresent()) {
            return ResponseEntity.ok(funcion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/funciones - Crear nueva función
    @PostMapping
    public ResponseEntity<?> crearFuncion(@RequestBody Funcion funcion) {
        try {
            Funcion nuevaFuncion = funcionService.crearFuncion(funcion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFuncion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/funciones/{id} - Actualizar función completa
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFuncion(@PathVariable Long id, @RequestBody Funcion funcion) {
        try {
            Optional<Funcion> funcionActualizada = funcionService.actualizarFuncion(id, funcion);
            
            if (funcionActualizada.isPresent()) {
                return ResponseEntity.ok(funcionActualizada.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/funciones/{id} - Eliminar función
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFuncion(@PathVariable Long id) {
        boolean eliminada = funcionService.eliminarFuncion(id);
        
        if (eliminada) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/funciones/pelicula/{peliculaId} - Buscar funciones por película
    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<List<Funcion>> buscarPorPelicula(@PathVariable Long peliculaId) {
        List<Funcion> funciones = funcionService.buscarPorPelicula(peliculaId);
        return ResponseEntity.ok(funciones);
    }

    // GET /api/funciones/fecha/{fecha} - Buscar funciones por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Funcion>> buscarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Funcion> funciones = funcionService.buscarPorFecha(fecha);
        return ResponseEntity.ok(funciones);
    }

    // GET /api/funciones/sala/{salaId} - Buscar funciones por sala ID
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<Funcion>> buscarPorSala(@PathVariable Long salaId) {
        List<Funcion> funciones = funcionService.buscarPorSala(salaId);
        return ResponseEntity.ok(funciones);
    }

    // GET /api/funciones/sala/nombre/{nombreSala} - Buscar funciones por nombre de sala
    @GetMapping("/sala/nombre/{nombreSala}")
    public ResponseEntity<List<Funcion>> buscarPorNombreSala(@PathVariable String nombreSala) {
        List<Funcion> funciones = funcionService.buscarPorNombreSala(nombreSala);
        return ResponseEntity.ok(funciones);
    }

    // GET /api/funciones/disponibles - Obtener funciones con asientos disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Funcion>> obtenerFuncionesDisponibles() {
        List<Funcion> funciones = funcionService.buscarFuncionesDisponibles();
        return ResponseEntity.ok(funciones);
    }

    // POST /api/funciones/{id}/reservar - Reservar asiento en función
    @PostMapping("/{id}/reservar")
    public ResponseEntity<?> reservarAsiento(@PathVariable Long id) {
        boolean reservado = funcionService.reservarAsiento(id);
        
        if (reservado) {
            return ResponseEntity.ok("{\"mensaje\": \"Asiento reservado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo reservar el asiento\"}");
        }
    }

    // POST /api/funciones/{id}/liberar - Liberar asiento en función
    @PostMapping("/{id}/liberar")
    public ResponseEntity<?> liberarAsiento(@PathVariable Long id) {
        boolean liberado = funcionService.liberarAsiento(id);
        
        if (liberado) {
            return ResponseEntity.ok("{\"mensaje\": \"Asiento liberado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo liberar el asiento\"}");
        }
    }

    // GET /api/funciones/buscar - Buscar con múltiples criterios
    @GetMapping("/buscar")
    public ResponseEntity<List<Funcion>> buscarFunciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) String nombreSala) {
        
        List<Funcion> funciones;
        
        if (fecha != null && salaId != null) {
            funciones = funcionService.buscarPorFechaYSala(fecha, salaId);
        } else if (fecha != null) {
            funciones = funcionService.buscarPorFecha(fecha);
        } else if (salaId != null) {
            funciones = funcionService.buscarPorSala(salaId);
        } else if (nombreSala != null) {
            funciones = funcionService.buscarPorNombreSala(nombreSala);
        } else {
            funciones = funcionService.obtenerTodasLasFunciones();
        }
        
        return ResponseEntity.ok(funciones);
    }
}