package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Sala;
import com.utp.cinerama.cinerama.model.Sala.TipoSala;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class SalaService {

    private List<Sala> salas = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public SalaService() {
        inicializarDatosPrueba();
    }

    // Métodos CRUD
    public List<Sala> obtenerTodasLasSalas() {
        return new ArrayList<>(salas);
    }

    public Optional<Sala> obtenerSalaPorId(Long id) {
        return salas.stream()
                .filter(sala -> sala.getId().equals(id))
                .findFirst();
    }

    public Sala crearSala(Sala sala) {
        // Verificar nombre único
        boolean existeNombre = salas.stream()
                .anyMatch(s -> s.getNombre().equalsIgnoreCase(sala.getNombre()));
        
        if (existeNombre) {
            throw new RuntimeException("Ya existe una sala con ese nombre: " + sala.getNombre());
        }

        sala.setId(contador.getAndIncrement());
        validarSala(sala);
        salas.add(sala);
        return sala;
    }

    public Optional<Sala> actualizarSala(Long id, Sala salaActualizada) {
        Optional<Sala> salaExistente = obtenerSalaPorId(id);
        
        if (salaExistente.isPresent()) {
            Sala sala = salaExistente.get();
            
            // Verificar nombre único (excluyendo la sala actual)
            boolean existeOtroNombre = salas.stream()
                    .anyMatch(s -> !s.getId().equals(id) && 
                             s.getNombre().equalsIgnoreCase(salaActualizada.getNombre()));
            
            if (existeOtroNombre) {
                throw new RuntimeException("Ya existe otra sala con ese nombre: " + salaActualizada.getNombre());
            }

            sala.setNombre(salaActualizada.getNombre());
            sala.setDescripcion(salaActualizada.getDescripcion());
            sala.setCapacidadTotal(salaActualizada.getCapacidadTotal());
            sala.setTipo(salaActualizada.getTipo());
            sala.setEstado(salaActualizada.getEstado());
            
            validarSala(sala);
            return Optional.of(sala);
        }
        
        return Optional.empty();
    }

    public Optional<Sala> actualizarSalaParcial(Long id, Sala salaParcial) {
        Optional<Sala> salaExistente = obtenerSalaPorId(id);
        
        if (salaExistente.isPresent()) {
            Sala sala = salaExistente.get();
            
            if (salaParcial.getNombre() != null) {
                // Verificar nombre único
                boolean existeOtroNombre = salas.stream()
                        .anyMatch(s -> !s.getId().equals(id) && 
                                 s.getNombre().equalsIgnoreCase(salaParcial.getNombre()));
                
                if (existeOtroNombre) {
                    throw new RuntimeException("Ya existe otra sala con ese nombre: " + salaParcial.getNombre());
                }
                sala.setNombre(salaParcial.getNombre());
            }
            
            if (salaParcial.getDescripcion() != null) {
                sala.setDescripcion(salaParcial.getDescripcion());
            }
            
            if (salaParcial.getCapacidadTotal() != null) {
                sala.setCapacidadTotal(salaParcial.getCapacidadTotal());
            }
            
            if (salaParcial.getTipo() != null) {
                sala.setTipo(salaParcial.getTipo());
            }
            
            if (salaParcial.getEstado() != null) {
                sala.setEstado(salaParcial.getEstado());
            }
            
            validarSala(sala);
            return Optional.of(sala);
        }
        
        return Optional.empty();
    }

    public boolean eliminarSala(Long id) {
        return salas.removeIf(sala -> sala.getId().equals(id));
    }

    // Métodos de búsqueda específicos
    public Optional<Sala> buscarPorNombre(String nombre) {
        return salas.stream()
                .filter(sala -> sala.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    public List<Sala> buscarPorTipo(TipoSala tipo) {
        return salas.stream()
                .filter(sala -> sala.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public List<Sala> buscarSalasActivas() {
        return salas.stream()
                .filter(Sala::estaActiva)
                .collect(Collectors.toList());
    }

    public List<Sala> buscarSalas2D() {
        return salas.stream()
                .filter(Sala::es2D)
                .collect(Collectors.toList());
    }

    public List<Sala> buscarPorCapacidadMinima(Integer capacidadMinima) {
        return salas.stream()
                .filter(sala -> sala.getCapacidadTotal() >= capacidadMinima)
                .collect(Collectors.toList());
    }

    public boolean existeNombre(String nombre) {
        return salas.stream()
                .anyMatch(sala -> sala.getNombre().equalsIgnoreCase(nombre));
    }

    // Métodos de control de estado
    public boolean activarSala(Long id) {
        Optional<Sala> sala = obtenerSalaPorId(id);
        if (sala.isPresent()) {
            sala.get().setEstado(true);
            return true;
        }
        return false;
    }

    public boolean desactivarSala(Long id) {
        Optional<Sala> sala = obtenerSalaPorId(id);
        if (sala.isPresent()) {
            sala.get().setEstado(false);
            return true;
        }
        return false;
    }

    // Métodos auxiliares
    private void validarSala(Sala sala) {
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la sala es obligatorio");
        }
        
        if (sala.getCapacidadTotal() == null || sala.getCapacidadTotal() <= 0) {
            throw new RuntimeException("La capacidad total debe ser mayor a 0");
        }
        
        if (sala.getCapacidadTotal() > 500) {
            throw new RuntimeException("La capacidad total no puede exceder 500 asientos");
        }
        
        if (sala.getTipo() == null) {
            throw new RuntimeException("El tipo de sala es obligatorio");
        }
        
        if (sala.getEstado() == null) {
            sala.setEstado(true); // Por defecto activa
        }
    }

    private void inicializarDatosPrueba() {
        // Sala 1 - Normal
        Sala sala1 = new Sala();
        sala1.setId(contador.getAndIncrement());
        sala1.setNombre("Sala 1");
        sala1.setDescripcion("Sala principal con pantalla estándar");
        sala1.setCapacidadTotal(120);
        sala1.setTipo(TipoSala.NORMAL);
        sala1.setEstado(true);

        // Sala 2 - Normal
        Sala sala2 = new Sala();
        sala2.setId(contador.getAndIncrement());
        sala2.setNombre("Sala 2");
        sala2.setDescripcion("Sala secundaria con buena acústica");
        sala2.setCapacidadTotal(100);
        sala2.setTipo(TipoSala.NORMAL);
        sala2.setEstado(true);

        // Sala 3 - Normal
        Sala sala3 = new Sala();
        sala3.setId(contador.getAndIncrement());
        sala3.setNombre("Sala 3");
        sala3.setDescripcion("Sala con excelente acústica y cómodos asientos");
        sala3.setCapacidadTotal(80);
        sala3.setTipo(TipoSala.NORMAL);
        sala3.setEstado(true);

        // Sala 4 - 2D
        Sala sala4 = new Sala();
        sala4.setId(contador.getAndIncrement());
        sala4.setNombre("Sala 2D-1");
        sala4.setDescripcion("Sala especializada para películas en 2D con alta definición");
        sala4.setCapacidadTotal(90);
        sala4.setTipo(TipoSala.SALA_2D);
        sala4.setEstado(true);

        // Sala 5 - Normal
        Sala sala5 = new Sala();
        sala5.setId(contador.getAndIncrement());
        sala5.setNombre("Sala 5");
        sala5.setDescripcion("Sala amplia para estrenos");
        sala5.setCapacidadTotal(140);
        sala5.setTipo(TipoSala.NORMAL);
        sala5.setEstado(true);

        // Sala 6 - 2D
        Sala sala6 = new Sala();
        sala6.setId(contador.getAndIncrement());
        sala6.setNombre("Sala 2D-2");
        sala6.setDescripcion("Sala 2D de alta calidad con sonido envolvente");
        sala6.setCapacidadTotal(60);
        sala6.setTipo(TipoSala.SALA_2D);
        sala6.setEstado(true);

        salas.add(sala1);
        salas.add(sala2);
        salas.add(sala3);
        salas.add(sala4);
        salas.add(sala5);
        salas.add(sala6);
    }
}
