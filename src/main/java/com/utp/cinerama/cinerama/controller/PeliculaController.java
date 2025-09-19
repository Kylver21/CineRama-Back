package com.utp.cinerama.cinerama.controller;

import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/peliculas")
@CrossOrigin(origins = "*")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    // GET /api/peliculas - Obtener todas las películas
    @GetMapping
    public ResponseEntity<List<Pelicula>> obtenerTodasLasPeliculas() {
        List<Pelicula> peliculas = peliculaService.obtenerTodasLasPeliculas();
        return ResponseEntity.ok(peliculas);
    }

    // GET /api/peliculas/{id} - Obtener película por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> obtenerPeliculaPorId(@PathVariable Long id) {
        Optional<Pelicula> pelicula = peliculaService.obtenerPeliculaPorId(id);
        
        if (pelicula.isPresent()) {
            return ResponseEntity.ok(pelicula.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/peliculas - Crear nueva película
    @PostMapping
    public ResponseEntity<?> crearPelicula(@RequestBody Pelicula pelicula) {
        try {
            Pelicula nuevaPelicula = peliculaService.crearPelicula(pelicula);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPelicula);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PUT /api/peliculas/{id} - Actualizar película completa
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPelicula(@PathVariable Long id, @RequestBody Pelicula pelicula) {
        try {
            Optional<Pelicula> peliculaActualizada = peliculaService.actualizarPelicula(id, pelicula);
            
            if (peliculaActualizada.isPresent()) {
                return ResponseEntity.ok(peliculaActualizada.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // PATCH /api/peliculas/{id} - Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarPeliculaParcial(@PathVariable Long id, @RequestBody Pelicula peliculaParcial) {
        try {
            Optional<Pelicula> peliculaActualizada = peliculaService.actualizarPeliculaParcial(id, peliculaParcial);
            
            if (peliculaActualizada.isPresent()) {
                return ResponseEntity.ok(peliculaActualizada.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DELETE /api/peliculas/{id} - Eliminar película
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPelicula(@PathVariable Long id) {
        boolean eliminada = peliculaService.eliminarPelicula(id);
        
        if (eliminada) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // GET /api/peliculas/buscar?genero=accion&titulo=avengers - Buscar películas
    @GetMapping("/buscar")
    public ResponseEntity<List<Pelicula>> buscarPeliculas(
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String clasificacion) {
        
        List<Pelicula> peliculas;
        
        if (genero != null) {
            peliculas = peliculaService.buscarPorGenero(genero);
        } else if (titulo != null) {
            peliculas = peliculaService.buscarPorTitulo(titulo);
        } else if (clasificacion != null) {
            peliculas = peliculaService.buscarPorClasificacion(clasificacion);
        } else {
            peliculas = peliculaService.obtenerTodasLasPeliculas();
        }
        
        return ResponseEntity.ok(peliculas);
    }
}