package com.example.Service;

import com.example.Dto.ClienteResponse;
import com.example.Errores.LogicaNegocioException;
import com.example.Model.Cliente;
import com.example.Repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FidelidadService {

    // Reglas del plan de fidelidad
    public static final int PUNTOS_PARA_BONO = 6;
    public static final double VALOR_BONO = 30000;

    private final ClienteRepository clienteRepository;
    private final TablaUsuariosService tablaUsuarios;

    public FidelidadService(ClienteRepository clienteRepository, TablaUsuariosService tablaUsuarios) {
        this.clienteRepository = clienteRepository;
        this.tablaUsuarios = tablaUsuarios;
    }

    // Suma un punto por cada servicio pagado; al llegar a 6 puntos se genera el bono y los que sobran se conservan
    public void sumarPuntos(String numeroTel, int puntosGanados) {

        // 1. La clienta se busca en la tabla hash, para que la tabla y MySQL queden iguales
        Cliente cliente = tablaUsuarios.buscarCliente(numeroTel);

        if (cliente == null) {
            log.warn("Puntos rechazados: la clienta no existe");
            throw new LogicaNegocioException("La clienta no existe");
        }

        // Acumular
        int puntos = cliente.getPuntos() + puntosGanados;

        // 3. Si alcanza para un bono y no tiene uno sin usar: se genera y se restan 6 puntos
        if (puntos >= PUNTOS_PARA_BONO && !cliente.isBonosDisponibles()) {
            cliente.setBonosDisponibles(true);
            puntos -= PUNTOS_PARA_BONO;
            log.info("Bono de fidelidad generado para la clienta");
        }

        cliente.setPuntos(puntos);
        clienteRepository.save(cliente);

        log.info("Clienta sumó puntos");
    }

    // Si la clienta tiene un bono, lo gasta y responde true; si no tiene, responde false y no cambia nada
    public boolean usarBono(Cliente cliente) {

        boolean usado = false;

        if (cliente.isBonosDisponibles()) {
            cliente.setBonosDisponibles(false);
            clienteRepository.save(cliente);
            usado = true;
            log.info("Bono usado por la clienta");
        }

        return usado;
    }

    // Descuenta el bono del valor de la cita sin dejarlo negativo
    public double valorConBono(double valor) {
        return Math.max(0, valor - VALOR_BONO);
    }

    // Mostrarlos en el perfil
    public ClienteResponse consultarCliente(String numeroTel) {

        Cliente cliente = tablaUsuarios.buscarCliente(numeroTel);

        if (cliente == null) {
            log.warn("Consulta rechazada: la clienta {} no existe", numeroTel);
            throw new LogicaNegocioException("La clienta no existe");
        }

        return new ClienteResponse(
                cliente.getNumeroTel(),
                cliente.getNombre(),
                cliente.getPuntos(),
                Math.max(0, PUNTOS_PARA_BONO - cliente.getPuntos()),
                cliente.isBonosDisponibles() ? 1 : 0
        );
    }
}