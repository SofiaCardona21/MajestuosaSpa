package com.example.Service;

import com.example.Dto.CambioEstadoRequest;
import com.example.Dto.CitaRequest;
import com.example.Dto.CitaResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.*;
import com.example.Repository.CitaRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@Log4j2
@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final DisponibilidadService disponibilidadService;
    private final TablaUsuariosService tablaUsuarios;
    private final TurnoService turnoService;
    private final FidelidadService fidelidadService;


    public CitaService(CitaRepository citaRepository, DisponibilidadService disponibilidadService, TablaUsuariosService tablaUsuarios, TurnoService turnoService, FidelidadService fidelidadService) {
        this.citaRepository = citaRepository;
        this.disponibilidadService = disponibilidadService;
        this.tablaUsuarios = tablaUsuarios;
        this.turnoService = turnoService;
        this.fidelidadService = fidelidadService;


    }

    public CitaResponse agendar (CitaRequest datosCita){

        List<Servicio> serviciosTomados = new ArrayList<>();
        int duracion = 0;
        LocalTime horaFin ;
        Cita cita = new Cita();
        double valor = 0;

        //VALIDACIONES PARA AGENDAR
        Cliente cliente = tablaUsuarios.buscarCliente(datosCita.celularCliente());

        if (cliente == null) {
            log.warn("Cita rechazada: la clienta no existe");
            throw new LogicaNegocioException("La clienta no existe");
        }

        if (datosCita.fecha().isBefore(LocalDate.now())) {
            log.warn("Cita rechazada la fecha ya pasó");
            throw new LogicaNegocioException("La fecha ya pasó");
        }

        if (datosCita.fecha().equals(LocalDate.now()) && datosCita.horaInicio().isBefore(LocalTime.now())) {
            log.warn("Cita rechazada: la hora de hoy ya pasó");
            throw new LogicaNegocioException("Esa hora ya pasó");
        }

        serviciosTomados = disponibilidadService.armarServicios(datosCita.idsServicios());
        duracion  = disponibilidadService.calcularDuracionServicio(serviciosTomados);

        String idElegida = datosCita.idManicurista();

        // por si no se elije una, busca la primera en disponibles
        if (idElegida == null || idElegida.isBlank()) {
            List<String> libres = disponibilidadService.manicuristasLibresA(datosCita.fecha(), datosCita.horaInicio(), duracion);
            idElegida = turnoService.asignarCualquiera(libres);
        }

        Personal manicurista = tablaUsuarios.buscarPersonal(idElegida);

        if (manicurista == null || manicurista.getRol() != Rol.MANICURISTA) {
            log.warn("Cita rechazada: la manicurista no existe");
            throw new LogicaNegocioException("La manicurista no existe");
        }

        valor = calcularValor(serviciosTomados);
        horaFin   = datosCita.horaInicio().plusMinutes(duracion);

        // volver a revisar que la hora siga libre. Para evitar errores si dos piden al mismo tiempo
        List<Cita> citasDia = citaRepository.findByManicuristaIdPersonalAndFecha(
                manicurista.getIdPersonal(), datosCita.fecha());

        List<LocalTime> horasLibres = disponibilidadService.calcularHorasDisponible(
                citasDia, manicurista.getHoraEntrada(), manicurista.getHoraSalida(), duracion);

        if (!horasLibres.contains(datosCita.horaInicio())) {
            log.warn("Horario rechazado: {} a las {} ya no está disponible para {}",
                    datosCita.fecha(), datosCita.horaInicio(), manicurista.getIdPersonal());
            throw new LogicaNegocioException("El horario ya no está disponible");
        }

        // Fidelidad: si la clienta tiene un bono, se usa en esta cita y se descuenta del valor
        boolean bonoAplicado = fidelidadService.usarBono(cliente);

        if (bonoAplicado) {
            valor = fidelidadService.valorConBono(valor);
        }

        cita.setFecha(datosCita.fecha());
        cita.setHoraInicio(datosCita.horaInicio());
        cita.setHoraFin(horaFin);
        cita.setCliente(cliente);
        cita.setManicurista(manicurista);
        cita.setServicios(serviciosTomados);
        cita.setValorTotal(valor);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setBonoAplicado(bonoAplicado);

        Cita citaGuardada = citaRepository.save(cita);

        log.info("Cita agendada");

        return convertirAResponse(citaGuardada);
    }


    private double calcularValor(List<Servicio> serviciosTomados) {

        double valorTotal = 0;

        for (Servicio servicio : serviciosTomados) {
            valorTotal += servicio.getPrecio();
        }

        return valorTotal;
    }

    public CitaResponse convertirAResponse(Cita cita) {

        List<String> nombresServicios = new ArrayList<>();

        for (Servicio servicio : cita.getServicios()) {
            nombresServicios.add(servicio.getNombreServicio());
        }

        return new CitaResponse(
                cita.getIdCita(),
                cita.getFecha(),
                cita.getHoraInicio(),
                cita.getHoraFin(),
                nombresServicios,
                cita.getCliente().getNombre(),
                cita.getManicurista().getNombrePersonal(),
                cita.getValorTotal(),
                cita.getEstado().name(),
                cita.isBonoAplicado()
        );
    }

    public List<CitaResponse> listaCitasCliente(String celular) {

        List<Cita> citasBD = citaRepository.findByClienteNumeroTel(celular);

        List<CitaResponse> respuestaListaCitas = new ArrayList<>();

        for (Cita cita : citasBD) {
            respuestaListaCitas.add(convertirAResponse(cita));
        }

        return respuestaListaCitas;
    }

    // Cambia el estado de una cita, revisando con la lógica del negocio
    public CitaResponse cambiarEstado(Long idCita, CambioEstadoRequest datosEstado) {

        //Buscar la cita en MySQL por su ID
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> {
                    log.warn("Cambio de estado rechazado: la cita no existe");
                    return new LogicaNegocioException("La cita no existe");
                });

        EstadoCita estadoActual = cita.getEstado();
        EstadoCita estadoNuevo = datosEstado.estado();
        LocalDateTime ahora = LocalDateTime.now();

        //Una cita pagada ya terminó, no hay más cambios
        if (estadoActual == EstadoCita.PAGADA) {
            log.warn("Cambio de estado rechazado: la cita ya está pagada");
            throw new LogicaNegocioException("La cita ya está pagada");
        }

        //Tiene que haber un cambio
        if (estadoNuevo == estadoActual) {
            log.warn("Cambio de estado rechazado: la cita ya está en ese estado");
            throw new LogicaNegocioException("La cita ya está en ese estado");
        }

        //No vuelve a pendiente
        if (estadoNuevo == EstadoCita.PENDIENTE) {
            log.warn("Cambio de estado rechazado: la cita no puede volver a pendiente");
            throw new LogicaNegocioException("Una cita no puede volver a pendiente");
        }

        // Revisar que ya haya empezado la cita
        LocalDateTime inicioCita = LocalDateTime.of(cita.getFecha(), cita.getHoraInicio());

        if (ahora.isBefore(inicioCita)) {
            log.warn("Cambio de estado rechazado: la cita no ha empezado");
            throw new LogicaNegocioException("La cita no ha empezado");
        }

        //Cuando se paga, revisar si terminó antes de lo establecido
        if (estadoNuevo == EstadoCita.PAGADA) {


            LocalDateTime finCita = LocalDateTime.of(cita.getFecha(), cita.getHoraFin());

            //La hora final es cuando se paga
            if (ahora.isBefore(finCita)) {
                LocalTime horaReal = ahora.toLocalTime().withSecond(0).withNano(0);

                log.info("Cita pagada antes de tiempo, se libera el tiempo restante");

                cita.setHoraFin(horaReal);
            }

            //PUNTOS DE FIDELIDAD
            // Fidelidad: un punto por cada servicio de la cita
            fidelidadService.sumarPuntos(cita.getCliente().getNumeroTel(), cita.getServicios().size());
        }

        //Cambiar el estado y guardar en BD
        cita.setEstado(estadoNuevo);
        Cita citaGuardada = citaRepository.save(cita);

        log.info("Cita cambió de estado");

        return convertirAResponse(citaGuardada);
    }
}