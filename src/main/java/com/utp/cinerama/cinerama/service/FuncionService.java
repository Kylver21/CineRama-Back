package com.utp.cinerama.cinerama.service;

import com.utp.cinerama.cinerama.model.Funcion;
import com.utp.cinerama.cinerama.model.Pelicula;
import com.utp.cinerama.cinerama.model.Sala;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class FuncionService {

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private SalaService salaService;

    private List<Funcion> funciones = new ArrayList<>();
    private AtomicLong contador = new AtomicLong(1);

    public FuncionService() {
        // Los datos de prueba se inicializan después de que se inyecte PeliculaService y SalaService
    }

    // Método para inicializar datos después de la inyección
    public void inicializarDatosPrueba() {
        if (funciones.isEmpty() && peliculaService != null && salaService != null) {
            List<Pelicula> peliculas = peliculaService.obtenerTodasLasPeliculas();
            List<Sala> salas = salaService.obtenerTodasLasSalas();
            if (!peliculas.isEmpty() && !salas.isEmpty()) {
                crearFuncionesPrueba(peliculas, salas);
            }
        }
    }

    // Métodos CRUD
    public List<Funcion> obtenerTodasLasFunciones() {
        inicializarDatosPrueba(); // Asegurar que hay datos
        return new ArrayList<>(funciones);
    }

    public Optional<Funcion> obtenerFuncionPorId(Long id) {
        return funciones.stream()
                .filter(funcion -> funcion.getId().equals(id))
                .findFirst();
    }

    public Funcion crearFuncion(Funcion funcion) {
        validarFuncion(funcion);
        
        // Verificar que no hay conflicto de horario en la misma sala
        boolean hayConflicto = funciones.stream()
                .anyMatch(f -> f.getSala().getId().equals(funcion.getSala().getId()) &&
                          f.getFecha().equals(funcion.getFecha()) &&
                          Math.abs(f.getHora().toSecondOfDay() - funcion.getHora().toSecondOfDay()) < 7200); // 2 horas
        
        if (hayConflicto) {
            throw new RuntimeException("Ya existe una función en esa sala y horario cercano");
        }

        funcion.setId(contador.getAndIncrement());
        funciones.add(funcion);
        return funcion;
    }

    public Optional<Funcion> actualizarFuncion(Long id, Funcion funcionActualizada) {
        Optional<Funcion> funcionExistente = obtenerFuncionPorId(id);
        
        if (funcionExistente.isPresent()) {
            Funcion funcion = funcionExistente.get();
            
            // Verificar conflicto de horario (excluyendo la función actual)
            boolean hayConflicto = funciones.stream()
                    .anyMatch(f -> !f.getId().equals(id) &&
                              f.getSala().getId().equals(funcionActualizada.getSala().getId()) &&
                              f.getFecha().equals(funcionActualizada.getFecha()) &&
                              Math.abs(f.getHora().toSecondOfDay() - funcionActualizada.getHora().toSecondOfDay()) < 7200);
            
            if (hayConflicto) {
                throw new RuntimeException("Ya existe una función en esa sala y horario cercano");
            }

            funcion.setPelicula(funcionActualizada.getPelicula());
            funcion.setSala(funcionActualizada.getSala());
            funcion.setFecha(funcionActualizada.getFecha());
            funcion.setHora(funcionActualizada.getHora());
            funcion.setAsientosDisponibles(funcionActualizada.getAsientosDisponibles());
            funcion.setAsientosTotales(funcionActualizada.getAsientosTotales());
            
            validarFuncion(funcion);
            return Optional.of(funcion);
        }
        
        return Optional.empty();
    }

    public boolean eliminarFuncion(Long id) {
        return funciones.removeIf(funcion -> funcion.getId().equals(id));
    }

    // Métodos de búsqueda específicos
    public List<Funcion> buscarPorPelicula(Long peliculaId) {
        return funciones.stream()
                .filter(funcion -> funcion.getPelicula() != null && 
                                 funcion.getPelicula().getId().equals(peliculaId))
                .collect(Collectors.toList());
    }

    public List<Funcion> buscarPorFecha(LocalDate fecha) {
        return funciones.stream()
                .filter(funcion -> funcion.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Funcion> buscarPorSala(Long salaId) {
        return funciones.stream()
                .filter(funcion -> funcion.getSala().getId().equals(salaId))
                .collect(Collectors.toList());
    }

    public List<Funcion> buscarPorNombreSala(String nombreSala) {
        return funciones.stream()
                .filter(funcion -> funcion.getSala().getNombre().equalsIgnoreCase(nombreSala))
                .collect(Collectors.toList());
    }

    public List<Funcion> buscarPorFechaYSala(LocalDate fecha, Long salaId) {
        return funciones.stream()
                .filter(funcion -> funcion.getFecha().equals(fecha) && 
                                 funcion.getSala().getId().equals(salaId))
                .collect(Collectors.toList());
    }

    public List<Funcion> buscarFuncionesDisponibles() {
        return funciones.stream()
                .filter(Funcion::hayAsientosDisponibles)
                .collect(Collectors.toList());
    }

    // Métodos de reserva
    public boolean reservarAsiento(Long funcionId) {
        Optional<Funcion> funcion = obtenerFuncionPorId(funcionId);
        if (funcion.isPresent()) {
            return funcion.get().reservarAsiento();
        }
        return false;
    }

    public boolean liberarAsiento(Long funcionId) {
        Optional<Funcion> funcion = obtenerFuncionPorId(funcionId);
        if (funcion.isPresent()) {
            funcion.get().liberarAsiento();
            return true;
        }
        return false;
    }

    // Métodos auxiliares
    private void validarFuncion(Funcion funcion) {
        if (funcion.getPelicula() == null) {
            throw new RuntimeException("La película es obligatoria");
        }
        
        if (funcion.getSala() == null) {
            throw new RuntimeException("La sala es obligatoria");
        }
        
        if (funcion.getFecha() == null) {
            throw new RuntimeException("La fecha es obligatoria");
        }
        
        if (funcion.getHora() == null) {
            throw new RuntimeException("La hora es obligatoria");
        }
        
        if (funcion.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden crear funciones en fechas pasadas");
        }
        
        if (funcion.getAsientosTotales() == null || funcion.getAsientosTotales() <= 0) {
            throw new RuntimeException("El total de asientos debe ser mayor a 0");
        }
        
        if (funcion.getAsientosDisponibles() == null || 
            funcion.getAsientosDisponibles() < 0 || 
            funcion.getAsientosDisponibles() > funcion.getAsientosTotales()) {
            throw new RuntimeException("Los asientos disponibles deben estar entre 0 y el total de asientos");
        }
    }

    private void crearFuncionesPrueba(List<Pelicula> peliculas, List<Sala> salas) {
        if (peliculas.size() >= 3 && salas.size() >= 2) {
            // Función 1
            Funcion funcion1 = new Funcion();
            funcion1.setId(contador.getAndIncrement());
            funcion1.setPelicula(peliculas.get(0)); // Avengers
            funcion1.setSala(salas.get(0)); // Primera sala
            funcion1.setFecha(LocalDate.now().plusDays(1));
            funcion1.setHora(LocalTime.of(18, 30));
            funcion1.setAsientosTotales(salas.get(0).getCapacidadTotal());
            funcion1.setAsientosDisponibles(salas.get(0).getCapacidadTotal() - 15); // 15 asientos ocupados

            // Función 2
            Funcion funcion2 = new Funcion();
            funcion2.setId(contador.getAndIncrement());
            funcion2.setPelicula(peliculas.get(1)); // Coco
            funcion2.setSala(salas.get(1)); // Segunda sala
            funcion2.setFecha(LocalDate.now().plusDays(1));
            funcion2.setHora(LocalTime.of(16, 0));
            funcion2.setAsientosTotales(salas.get(1).getCapacidadTotal());
            funcion2.setAsientosDisponibles(salas.get(1).getCapacidadTotal() - 10); // 10 asientos ocupados

            // Función 3
            Funcion funcion3 = new Funcion();
            funcion3.setId(contador.getAndIncrement());
            funcion3.setPelicula(peliculas.get(2)); // El Padrino
            funcion3.setSala(salas.get(0)); // Primera sala (diferente horario)
            funcion3.setFecha(LocalDate.now().plusDays(2));
            funcion3.setHora(LocalTime.of(21, 0));
            funcion3.setAsientosTotales(salas.get(0).getCapacidadTotal());
            funcion3.setAsientosDisponibles(salas.get(0).getCapacidadTotal()); // Sin ocupar

            funciones.add(funcion1);
            funciones.add(funcion2);
            funciones.add(funcion3);
        }
    }
}