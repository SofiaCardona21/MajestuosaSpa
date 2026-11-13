package com.example.Service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component// nos sirve para instanciar automáticamente cuando se arranca el programa
public class ColaTurnosManicuristasService {

    private final Queue<String> colaLlegadasManicuristas = new LinkedList<>();

    public void registrarLlegada(String idManicurista) {
        colaLlegadasManicuristas.add(idManicurista);
    }


    public boolean estaVacia() {
        return colaLlegadasManicuristas.isEmpty();
    }


    public List<String> verOrden() {
        return new ArrayList<>(colaLlegadasManicuristas);
    }


    //Para mantener sincronizado la cola con lo almacenado en la base de datos
    public void restaurarCola(List<String> idsManicuristas) {

        colaLlegadasManicuristas.clear(); //vaciamos la cola
        colaLlegadasManicuristas.addAll(idsManicuristas); // le agregamos otra vez todo en el orden que esta en la BD
    }


    public String asignarPrimeraLibre(List<String> libres) {

        String manicuristaAsignada = "";
        String primeraManicuristaActual = "";
        int tamanoCola = colaLlegadasManicuristas.size();

        for (int i = 0; i < tamanoCola; i++) {

            primeraManicuristaActual = colaLlegadasManicuristas.poll();

            //para ver si la primera en la cola esta disponible y darle la cita a esa
            if (manicuristaAsignada.equals("") && libres.contains(primeraManicuristaActual)) {
                manicuristaAsignada = primeraManicuristaActual;
            }

            //si no esta libre entonces para que conserve su orden la ponemos al final de la cola
            //y cuando se recorra toda queda el mismo orden
            else {
                colaLlegadasManicuristas.add(primeraManicuristaActual);
            }
        }
        //a la que se le asigni la cita la volvemos a poner al final de la cola
        if (!(manicuristaAsignada.equals(""))) {
            colaLlegadasManicuristas.add(manicuristaAsignada);
        }

        return manicuristaAsignada;
    }
}