package com.example.Service;

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

@Log4j2
@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final DisponibilidadService disponibilidadService;
    private final TablaUsuariosService tablaUsuarios;

    public CitaService(CitaRepository citaRepository, DisponibilidadService disponibilidadService, TablaUsuariosService tablaUsuarios) {
        this.citaRepository = citaRepository;
        this.disponibilidadService = disponibilidadService;
        this.tablaUsuarios = tablaUsuarios;
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

        Personal manicurista = tablaUsuarios.buscarPersonal(datosCita.idManicurista());

        if (manicurista == null) {
            log.warn("Cita rechazada: la manicurista no existe");
            throw new LogicaNegocioException("La manicurista no existe");
        }

        if (datosCita.fecha().isBefore(LocalDate.now())) {
            log.warn("Cita rechazada la fecha ya pasó");
            throw new LogicaNegocioException("La fecha ya pasó");
        }

        // 4. servicios = disponibilidadService.armarServicios(...)
        serviciosTomados = disponibilidadService.armarServicios(datosCita.idsServicios());

         duracion  = disponibilidadService.calcularDuracionServicio(serviciosTomados);


         valor     = calcularValor(serviciosTomados);

        horaFin   = datosCita.horaInicio().plusMinutes(duracion);

        // 8. Volver a revisar que la hora siga libre
        List<Cita> citasDia = citaRepository.findByManicuristaIdPersonalAndFecha(
                manicurista.getIdPersonal(), datosCita.fecha());

        List<LocalTime> horasLibres = disponibilidadService.calcularHorasDisponible(
                citasDia, manicurista.getHoraEntrada(), manicurista.getHoraSalida(), duracion);

        if (!horasLibres.contains(datosCita.horaInicio())) {
            log.warn("Horario rechazado: {} a las {} ya no está disponible para {}",
                    datosCita.fecha(), datosCita.horaInicio(), manicurista.getIdPersonal());
            throw new LogicaNegocioException("El horario ya no está disponible");
        }

        if (datosCita.fecha().equals(LocalDate.now()) && datosCita.horaInicio().isBefore(LocalTime.now())) {
            log.warn("Cita rechazada: la hora de hoy ya pasó");
            throw new LogicaNegocioException("Esa hora ya pasó");
        }

        cita.setFecha(datosCita.fecha());
        cita.setHoraInicio(datosCita.horaInicio());
        cita.setHoraFin(horaFin);
        cita.setCliente(cliente);
        cita.setManicurista(manicurista);
        cita.setServicios(serviciosTomados);
        cita.setValorTotal(valor);
        cita.setEstado(EstadoCita.PENDIENTE);

        Cita citaGuardada = citaRepository.save(cita);

        // 11. Log de comportamiento
        log.info("Cita agendada");


        // 12. Devolver la respuesta para la página
        return convertirAResponse(citaGuardada);
    }

    private double calcularValor(List<Servicio> serviciosTomados) {

        double valorTotal = 0;

        for (Servicio servicio : serviciosTomados) {
            valorTotal += servicio.getPrecio();
        }

        return valorTotal;
    }

    private CitaResponse convertirAResponse(Cita cita) {

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
                cita.getEstado().name()
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


}






