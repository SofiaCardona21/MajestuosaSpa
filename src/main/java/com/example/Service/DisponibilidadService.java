package com.example.Service;

import com.example.Dto.HorarioDisponibleResponse;
import com.example.Dto.ManicuristaResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.*;
import com.example.Repository.CitaRepository;
import com.example.Repository.PersonalRepository;
import com.example.Repository.ServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;



@Service
@Slf4j
public class DisponibilidadService {

    private record ManicuristaConHorasLibres (Personal manicurista, List<LocalTime> horas){ }
    private final CitaRepository citaRepository;
    private final PersonalRepository personalRepository;
    private final ServicioRepository servicioRepository;

    private static final LocalTime ENTRADA = LocalTime.of(10, 0);
    private static final LocalTime SALIDA = LocalTime.of(21, 0);


    public DisponibilidadService(CitaRepository citaRepository, PersonalRepository personalRepository, ServicioRepository servicioRepository) {
        this.citaRepository = citaRepository;
        this.personalRepository = personalRepository;
        this.servicioRepository = servicioRepository;
    }

    public List<LocalTime> calcularHorasDisponible(List<Cita> citasDia,
                                                   LocalTime horaEntrada,
                                                   LocalTime horaSalida,
                                                   int duracionMinutos) {

        PriorityQueue<Cita> colaCitas = new PriorityQueue<>();

        colaCitas.addAll(citasDia);


        List<LocalTime> horasDisponibles = new ArrayList<>();

        LocalTime inicioLibre = horaEntrada;

        while (!colaCitas.isEmpty()) {

            Cita cita = colaCitas.poll();

            revisarEspacio(inicioLibre, cita.getHoraInicio(), duracionMinutos, horasDisponibles);
//Hace referencia al fin del espacio libre cita.getHoraInicio()

            inicioLibre = cita.getHoraFin();
        }

        revisarEspacio(inicioLibre, horaSalida, duracionMinutos, horasDisponibles);

        return horasDisponibles;
    }

    private LocalTime redondearMediaHora(LocalTime hora) {

        int minutos = hora.getMinute();
        int segundos = hora.getSecond();

        if ((minutos == 0 || minutos == 30) && segundos == 0) {
            return hora;
        }

        // 2. Si los minutos están entre 0 y 29 devuelve a la hora
        else if (minutos < 30) {
            return hora.withMinute(30).withSecond(0).withNano(0);
        }

        // 3. Si los minutos están entre 30 y 59 aproximar a la otra hora
        return hora.plusHours(1).withMinute(0).withSecond(0).withNano(0);

    }

    private void revisarEspacio(LocalTime inicioEspacio, LocalTime finEspacio,
                                int duracionMinutos, List<LocalTime> horasDisponibles) {

        // 1. Redondear el inicio del espacio a la siguiente media hora

        LocalTime horaInicioEspacioLibre = redondearMediaHora(inicioEspacio);


        while (!((horaInicioEspacioLibre.plusMinutes(duracionMinutos)).isAfter(finEspacio))) {

            horasDisponibles.add(horaInicioEspacioLibre);

            horaInicioEspacioLibre = horaInicioEspacioLibre.plusMinutes(30);

        }

    }

    public List<Servicio> armarServicios(List<String> idServicios) {

        List<Servicio> serviciosObjetos = new ArrayList<>();


        for (String idABuscar : idServicios) {
            Servicio servicio = servicioRepository.findById(idABuscar)
                    .orElseThrow(() -> new LogicaNegocioException("El servicio no existe"));

            serviciosObjetos.add(servicio);

        }

        return serviciosObjetos;


    }

    public int calcularDuracionServicio(List<Servicio> servicios) {

        int duracion = 0;

        for (Servicio servicioASumar : servicios) {
            duracion += servicioASumar.getDuracionMinutos();
        }

        return duracion;


    }

    public List<HorarioDisponibleResponse> consultarHorarios(LocalDate fechaSolicitada, List<String> idsServicio) {

        long startTime = System.nanoTime();

        List <Servicio> servicios = new ArrayList<>();
        int duracion= 0;

        List <ManicuristaConHorasLibres> disponibilidad = new ArrayList<>();

        List<LocalTime> horasDisponibles = new ArrayList<>();

        List<HorarioDisponibleResponse> disponibilidadRespuesta = new ArrayList<>();

        LocalTime recorrerDia = ENTRADA;

        if (fechaSolicitada.isBefore(LocalDate.now())) {

            log.warn("Consulta rechazada, la fecha ya paso");
            throw new LogicaNegocioException("La fecha ya pasó");

        }

        servicios = armarServicios(idsServicio);
        duracion = calcularDuracionServicio(servicios);

        for (Personal manicurista : personalRepository.findByRol(Rol.MANICURISTA)) {

            List<Cita> citasDia = citaRepository.findByManicuristaIdPersonalAndFecha(manicurista.getIdPersonal(), fechaSolicitada);

            horasDisponibles= calcularHorasDisponible(citasDia, ENTRADA, SALIDA, duracion);

            disponibilidad.add(new ManicuristaConHorasLibres(manicurista, horasDisponibles));

        }

        while (!recorrerDia.plusMinutes(duracion).isAfter(SALIDA)) {

            if (!(fechaSolicitada.equals(LocalDate.now()) && recorrerDia.isBefore(LocalTime.now()))){

                List<ManicuristaResponse> ManicuristasLibres = new ArrayList<>();

                // Revisar cada manicurista
                for (ManicuristaConHorasLibres ManicuristaConEsaHoraLibre : disponibilidad) {
                    if (ManicuristaConEsaHoraLibre.horas().contains(recorrerDia)) {
                        ManicuristasLibres.add(new ManicuristaResponse(
                                ManicuristaConEsaHoraLibre.manicurista().getIdPersonal(),
                                ManicuristaConEsaHoraLibre.manicurista().getNombrePersonal()));
                    }
                }

                if (! ManicuristasLibres.isEmpty()) {
                    disponibilidadRespuesta.add(new HorarioDisponibleResponse(recorrerDia, ManicuristasLibres));
                }
            }
            recorrerDia = recorrerDia.plusMinutes(30);

        }



        long endTime = System.nanoTime();
        long tiempoEjecucion = endTime - startTime;

        log.info("Proceso ejecutado en  " + tiempoEjecucion + "nanosegundos");

        return disponibilidadRespuesta;

    }


}
