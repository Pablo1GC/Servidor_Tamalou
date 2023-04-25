package com.tamalou.servidor.socket;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Partida;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TournamentManager {

    private HashMap<String, Partida> torneoHashMap;

    public TournamentManager() {
        torneoHashMap = new HashMap<>();
    }

    public String agregarTorneo(Player creador, Partida game) {
        boolean incorrecto = true;
        String clave;
        do {
            clave = cadenaAleatoria(10);
            if (torneoHashMap.get(clave) == null) {
                torneoHashMap.put(clave, game);
                System.out.println("Torneo creado con clave: " + clave);
                incorrecto = false;
            }
        } while (incorrecto);
        return clave;
    }

    public Partida obtenerTorneo(String clave) {
        return torneoHashMap.get(clave);
    }

    public synchronized HashMap<String, Partida> mostrarTorneos() {
        HashMap<String, Partida> torneosPublicos = new HashMap<>();
        torneoHashMap.forEach((key, value) -> {
            if (!value.isPrivado())
                torneosPublicos.put(key, value);
        });
        return torneosPublicos;
    }

    public synchronized int unirJugadorATorneo(Player player, String clave) {
        if (torneoHashMap.get(clave) == null)
            return Signal.TORNEO_INEXISTENTE;

        if (torneoHashMap.get(clave).getPlayersList().size() == 4)
            return Signal.TORNEO_LLENO;
        else {
            torneoHashMap.get(clave).addPlayer(player);
            return Signal.UNION_EXITOSA_TORNEO;
        }
    }


    private String cadenaAleatoria(int longitud) {
        // El banco de caracteres
        String banco = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        // La cadena en donde iremos agregando un carácter aleatorio
        StringBuilder cadena = new StringBuilder();
        for (int x = 0; x < longitud; x++) {
            int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
            char caracterAleatorio = banco.charAt(indiceAleatorio);
            cadena.append(caracterAleatorio);
        }
        return cadena.toString();
    }

    private int numeroAleatorioEnRango(int minimo, int maximo) {
        // nextInt regresa en rango pero con límite superior exclusivo, por eso sumamos 1
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }


}