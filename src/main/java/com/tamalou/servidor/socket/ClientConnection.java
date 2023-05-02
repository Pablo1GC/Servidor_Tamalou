package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Package;

import java.io.*;
import java.net.Socket;
import java.util.List;
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
            Gson gson = new Gson();
            Player player = new Player(clientSocket);


            int senalConectarse = gson.fromJson(player.reader.nextLine(), Package.class).signals.get(0);
            if (senalConectarse == Signal.CONECTARSE)
                player.writter.println(gson.toJson(new Package(List.of(Signal.CONEXION_EXITOSA), null)));
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