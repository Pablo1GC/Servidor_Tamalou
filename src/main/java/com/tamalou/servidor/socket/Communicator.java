package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import org.springframework.lang.NonNull;

public class Communicator {
    static Gson codifier = new Gson();


    public static void sendCommunication(Player player, int signal, @NonNull Object object) {
        player.writter.printf("{\"signal\": %d, \"data\": %s}\n", signal, codifier.toJson(object));
    }

    public static void sendCommunication(Player player, int signal) {
        player.writter.printf("{\"signal\": %d}\n", signal);
    }
}
