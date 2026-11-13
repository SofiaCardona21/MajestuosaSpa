package com.example.Service;

import com.example.Dto.LlegadaRequest;
import com.example.Dto.TurnoResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.OrdenLlegadaManicurista;
import com.example.Model.Personal;
import com.example.Model.Rol;
import com.example.Repository.OrdenLlegadaManicuristaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TurnoService {

    private final OrdenLlegadaManicuristaRepository ordenLlegadaManicuristaRepository;
    private final ColaTurnosManicuristasService colaTurnos;
    private final TablaUsuariosService tablaUsuarios;

    // para ver si la cola de manicuristas esta al dia o se tiene que actualizar
    private LocalDate fechaCola = null;

    //MÉTODOS:

    public TurnoService(OrdenLlegadaManicuristaRepository ordenLlegadaManicuristaRepository, ColaTurnosManicuristasService colaTurnos, TablaUsuariosService tablaUsuarios) {
        this.ordenLlegadaManicuristaRepository = ordenLlegadaManicuristaRepository;
        this.colaTurnos = colaTurnos;
        this.tablaUsuarios = tablaUsuarios;
    }

    // Revisa si la cola es de hoy o si por eror se cambió el día
    // ent la vuelve a armar con las llegadas de hoy guardadas en la BD
    private void prepararColaDeHoy() {

        LocalDate fechaHoy = LocalDate.now();

        if (fechaCola == null || !fechaCola.equals(fechaHoy)){

            List<OrdenLlegadaManicurista> llegadasHoy = ordenLlegadaManicuristaRepository.findByFechaOrderByPosicionAsc(fechaHoy);

            List<String> listaIdsLlegadas = new ArrayList<>();

            for (OrdenLlegadaManicurista idManicuristaLlegada : llegadasHoy) {  //recorremos todos los datos precargados y los añadimos a la tabla
                listaIdsLlegadas.add(idManicuristaLlegada.getManicurista().getIdPersonal());
            }

            colaTurnos.restaurarCola(listaIdsLlegadas);
            fechaCola = fechaHoy;

            log.info("Cola de turnos restaurada con manicuristas");
        }
    }

    public List<TurnoResponse> registrarLlegada (LlegadaRequest datosLlegada) {

        prepararColaDeHoy();

        Personal manicuristaQueLlego = tablaUsuarios.buscarPersonal(datosLlegada.idManicurista());

        if (manicuristaQueLlego == null || manicuristaQueLlego.getRol() != Rol.MANICURISTA){
            log.warn("Llegada rechazada la manicurista no existe");
            throw new LogicaNegocioException("La manicurista no existe");
        } else if (ordenLlegadaManicuristaRepository.existsByManicuristaIdPersonalAndFecha(datosLlegada.idManicurista(), LocalDate.now())) {
            log.warn("Llegada rechazada: ya se registro la llegada de " + manicuristaQueLlego.getNombrePersonal() + " hoy");
            throw new LogicaNegocioException("Esa manicurista ya registró su llegada hoy");
        }

        colaTurnos.registrarLlegada(datosLlegada.idManicurista());
        int posicion = colaTurnos.verOrden().size();

        OrdenLlegadaManicurista llegada = new OrdenLlegadaManicurista();
        llegada.setManicurista(manicuristaQueLlego);
        llegada.setFecha(fechaCola);
        llegada.setPosicion(posicion);

        ordenLlegadaManicuristaRepository.save(llegada);

        log.info("Llegada registrada");

        return verOrden(); //muestra la lista de turnos con lo q hicimos abajo
    }

    // hace como una copia del orden de la cola de turnos, para que puedan leerlo sin tocar la cola real
    public List<TurnoResponse> verOrden() {

        prepararColaDeHoy();

        List <String> ordenIds = colaTurnos.verOrden();
        List<TurnoResponse> respuestaTurnos = new ArrayList<>();

        // Con cada id de la cola se busca la manicurista en la tabla de usuarios
        // para mostrar los turnos en orden con su posición en la cola, su id y su nombre

        for (int i = 0; i < ordenIds.size(); i++) {
           Personal manicurista = tablaUsuarios.buscarPersonal(ordenIds.get(i));
            respuestaTurnos.add(new TurnoResponse(i +1,manicurista.getIdPersonal(),manicurista.getNombrePersonal()));
        }
      return  respuestaTurnos;
    }

    // Este es para actualizar en la base de datos las posiciones de las llegadas
    //porque cambian cuando se asigna una cita en "cualquiera"

    private void guardarOrdenEnBD() {

        List<OrdenLlegadaManicurista> llegadasHoy = ordenLlegadaManicuristaRepository.findByFechaOrderByPosicionAsc(fechaCola);

        //es como el orden actual
        List<String> ordenNuevo = colaTurnos.verOrden();

        for (OrdenLlegadaManicurista llegada : llegadasHoy) {
            String idManicurista = llegada.getManicurista().getIdPersonal();
            int puesto = ordenNuevo.indexOf(idManicurista);
            // el indexOf es para buscar algo especifico en una lista
            llegada.setPosicion(puesto + 1);
        }

        // Actualiza el estado de la cola en la BD
        ordenLlegadaManicuristaRepository.saveAll(llegadasHoy);
    }

    // es para cuando se elije "cualquiera", ent se le da la cita a la que este de primera
    // en la cola y se pasa a final y si no hay disponibles lanza una excepción
    public String asignarCualquiera(List<String> libres) {

        prepararColaDeHoy(); //ayuda a tener la cola actualizada

        if (colaTurnos.estaVacia()) {
            log.warn("Cualquiera rechazado porque todavía no ha llegado ninguna manicurista hoy");
            throw new LogicaNegocioException("Todavía no ha llegado ninguna manicurista");
        }

        List<String> ordenAntes = colaTurnos.verOrden();

        String asignada = colaTurnos.asignarPrimeraLibre(libres);

        if (asignada.equals("")) {
            log.warn("Cualquiera rechazado: ninguna manicurista de la cola está libre");
            throw new LogicaNegocioException("Ninguna manicurista está libre a esa hora");
        }
        //actualizo el orden
        guardarOrdenEnBD();
        return asignada;
    }

    // Devuelve quiénes llegaron hoy, para saber en qué horas mostrar la opción "Cualquiera"
    public List<String> idsEnCola() {
        prepararColaDeHoy();
        return colaTurnos.verOrden();
    }
}
