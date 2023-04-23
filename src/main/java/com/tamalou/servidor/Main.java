package com.tamalou.servidor;

import com.tamalou.servidor.servidor.ClientConnection;
import com.tamalou.servidor.servidor.SignalManager;
import com.tamalou.servidor.servidor.TournamentManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@SpringBootApplication
public class Main {
    public static final int PUERTO = 8080;

    public static void main(String[] args) {

        TournamentManager manejadorTorneos = new TournamentManager();
        SignalManager signalManager = new SignalManager(manejadorTorneos);
        ClientConnection clientConnection = new ClientConnection(manejadorTorneos, signalManager);
        Socket socketAlCliente = null;
        InetSocketAddress direccion = new InetSocketAddress(PUERTO);
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(direccion);
            do {
                socketAlCliente = serverSocket.accept();
                clientConnection.conectarCliente(socketAlCliente);
            } while (true);
        } catch (IOException e) {
            System.err.println("SERVIDOR: Error de entrada/salida");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("SERVIDOR: Error -> " + e);
            e.printStackTrace();
        }


        /**
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

        partida1.jugarPartida(); */
    }

}
