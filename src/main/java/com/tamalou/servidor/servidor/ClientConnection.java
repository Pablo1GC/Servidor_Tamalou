package com.tamalou.servidor.servidor;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientConnection {

    private TournamentManager tournamentManager;

    private SignalManager signalManager;

    public ClientConnection(TournamentManager tournamentManager, SignalManager signalManager){
        this.tournamentManager = tournamentManager;
        this.signalManager = signalManager;
    }


    public void conectarCliente(Socket clientSocket) {
        try {
            Player player = new Player(clientSocket);


            int senalConectarse = Integer.parseInt(player.reader.nextLine());
            if (senalConectarse == Signal.CONECTARSE)
                player.writter.println(Signal.CONEXION_EXITOSA);
            else {
                clientSocket.close();
                return;
            }

            System.out.println("SERVIDOR: Jugador con IP " + player.socket.getInetAddress().getHostName() + " se ha conectado.");
            signalManager.manage(player);

        } catch (IOException e) {
            System.err.println("SERVIDOR: Error de entrada/salida");
            e.printStackTrace();
        } catch (NoSuchElementException e){
            System.out.println("SERVIDOR: Cliente desconectado.");
        } catch (Exception e) {
            System.err.println("SERVIDOR: Error -> " + e);
            e.printStackTrace();
        }
    }
}