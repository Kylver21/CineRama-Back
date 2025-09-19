package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Pelicula;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PeliculaService {

    private List<Pelicula> peliculas = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public PeliculaService() {
        inicializarDatosPrueba();
    }

    // Métodos CRUD
    public List<Pelicula> obtenerTodasLasPeliculas() {
        return new ArrayList<>(peliculas);
    }

    public Optional<Pelicula> obtenerPeliculaPorId(Long id) {
        return peliculas.stream()
                .filter(pelicula -> pelicula.getId().equals(id))
                .findFirst();
    }

    public Pelicula crearPelicula(Pelicula pelicula) {
        boolean existeTitulo = peliculas.stream()
                .anyMatch(p -> p.getTitulo().equalsIgnoreCase(pelicula.getTitulo()));
        
        if (existeTitulo) {
            throw new RuntimeException("Ya existe una película con ese título: " + pelicula.getTitulo());
        }

        pelicula.setId(contador.getAndIncrement());
        validarPelicula(pelicula);
        peliculas.add(pelicula);
        return pelicula;
    }

    public Optional<Pelicula> actualizarPelicula(Long id, Pelicula peliculaActualizada) {
        Optional<Pelicula> peliculaExistente = obtenerPeliculaPorId(id);
        
        if (peliculaExistente.isPresent()) {
            Pelicula pelicula = peliculaExistente.get();
            
            boolean existeOtroTitulo = peliculas.stream()
                    .anyMatch(p -> !p.getId().equals(id) && 
                             p.getTitulo().equalsIgnoreCase(peliculaActualizada.getTitulo()));
            
            if (existeOtroTitulo) {
                throw new RuntimeException("Ya existe otra película con ese título: " + peliculaActualizada.getTitulo());
            }

            pelicula.setTitulo(peliculaActualizada.getTitulo());
            pelicula.setGenero(peliculaActualizada.getGenero());
            pelicula.setDuracion(peliculaActualizada.getDuracion());
            pelicula.setClasificacion(peliculaActualizada.getClasificacion());
            pelicula.setSinopsis(peliculaActualizada.getSinopsis());
            pelicula.setPrecio(peliculaActualizada.getPrecio());
            
            validarPelicula(pelicula);
            return Optional.of(pelicula);
        }
        
        return Optional.empty();
    }

    public Optional<Pelicula> actualizarPeliculaParcial(Long id, Pelicula peliculaParcial) {
        Optional<Pelicula> peliculaExistente = obtenerPeliculaPorId(id);
        
        if (peliculaExistente.isPresent()) {
            Pelicula pelicula = peliculaExistente.get();
            
            if (peliculaParcial.getTitulo() != null) {
                boolean existeOtroTitulo = peliculas.stream()
                        .anyMatch(p -> !p.getId().equals(id) && 
                                 p.getTitulo().equalsIgnoreCase(peliculaParcial.getTitulo()));
                
                if (existeOtroTitulo) {
                    throw new RuntimeException("Ya existe otra película con ese título: " + peliculaParcial.getTitulo());
                }
                pelicula.setTitulo(peliculaParcial.getTitulo());
            }
            
            if (peliculaParcial.getGenero() != null) {
                pelicula.setGenero(peliculaParcial.getGenero());
            }
            
            if (peliculaParcial.getDuracion() != null) {
                pelicula.setDuracion(peliculaParcial.getDuracion());
            }
            
            if (peliculaParcial.getClasificacion() != null) {
                pelicula.setClasificacion(peliculaParcial.getClasificacion());
            }
            
            if (peliculaParcial.getSinopsis() != null) {
                pelicula.setSinopsis(peliculaParcial.getSinopsis());
            }
            
            if (peliculaParcial.getPrecio() != null) {
                pelicula.setPrecio(peliculaParcial.getPrecio());
            }
            
            validarPelicula(pelicula);
            return Optional.of(pelicula);
        }
        
        return Optional.empty();
    }

    public boolean eliminarPelicula(Long id) {
        return peliculas.removeIf(pelicula -> pelicula.getId().equals(id));
    }

    public List<Pelicula> buscarPorGenero(String genero) {
        return peliculas.stream()
                .filter(pelicula -> pelicula.getGenero().equalsIgnoreCase(genero))
                .collect(Collectors.toList());
    }

    public List<Pelicula> buscarPorTitulo(String titulo) {
        return peliculas.stream()
                .filter(pelicula -> pelicula.getTitulo().toLowerCase()
                        .contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Pelicula> buscarPorClasificacion(String clasificacion) {
        return peliculas.stream()
                .filter(pelicula -> pelicula.getClasificacion().equalsIgnoreCase(clasificacion))
                .collect(Collectors.toList());
    }

    // Métodos auxiliares
    private void validarPelicula(Pelicula pelicula) {
        if (pelicula.getTitulo() == null || pelicula.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("El título es obligatorio");
        }
        
        if (pelicula.getGenero() == null || pelicula.getGenero().trim().isEmpty()) {
            throw new RuntimeException("El género es obligatorio");
        }
        
        if (pelicula.getDuracion() == null || pelicula.getDuracion() <= 0) {
            throw new RuntimeException("La duración debe ser mayor a 0");
        }
        
        if (pelicula.getPrecio() == null || pelicula.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }
    }

    private void inicializarDatosPrueba() {
        Pelicula pelicula1 = new Pelicula();
        pelicula1.setId(contador.getAndIncrement());
        pelicula1.setTitulo("Avengers: Endgame");
        pelicula1.setGenero("Acción");
        pelicula1.setDuracion(181);
        pelicula1.setClasificacion("PG-13");
        pelicula1.setSinopsis("Los Vengadores se unen para derrotar a Thanos y restaurar el universo.");
        pelicula1.setPrecio(new BigDecimal("12.50"));

        Pelicula pelicula2 = new Pelicula();
        pelicula2.setId(contador.getAndIncrement());
        pelicula2.setTitulo("Coco");
        pelicula2.setGenero("Animación");
        pelicula2.setDuracion(105);
        pelicula2.setClasificacion("PG");
        pelicula2.setSinopsis("Un niño viaja al mundo de los muertos para descubrir su historia familiar.");
        pelicula2.setPrecio(new BigDecimal("10.00"));

        Pelicula pelicula3 = new Pelicula();
        pelicula3.setId(contador.getAndIncrement());
        pelicula3.setTitulo("El Padrino");
        pelicula3.setGenero("Drama");
        pelicula3.setDuracion(175);
        pelicula3.setClasificacion("R");
        pelicula3.setSinopsis("La saga de una familia mafiosa en Nueva York.");
        pelicula3.setPrecio(new BigDecimal("11.00"));

        peliculas.add(pelicula1);
        peliculas.add(pelicula2);
        peliculas.add(pelicula3);
    }
}