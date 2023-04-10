package com.tamalou.servidor;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Partida;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        // SpringApplication.run(Main.class, args);
        Player player1 = new Player("Jugador1");
        Player player2 = new Player("Jugador2");
        Player player3 = new Player("Jugador3");
        Player player4 = new Player("Jugador4");

        ArrayList listaJugadores = new ArrayList();
        listaJugadores.add(player1);
        listaJugadores.add(player2);
        listaJugadores.add(player3);
        listaJugadores.add(player4);

        Partida partida1 = new Partida(10, 100, listaJugadores);

        partida1.jugarPartida();
    }

}
