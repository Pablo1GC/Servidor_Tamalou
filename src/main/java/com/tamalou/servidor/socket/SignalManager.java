package com.tamalou.servidor.socket;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Partida;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SignalManager {

    private ArrayList<Player> jugadoresLobby;

    private ArrayList<Player> jugadoresEnPartida;

    private TournamentManager tournamentManager;

    public SignalManager(TournamentManager manager) {
        this.tournamentManager = manager;
    }


    /**
     * Empieza un nuevo hilo para este player que se encarga
     * de manejar las posibles peticiones que tenga el cliente
     * hasta que se conecte a un torneo o se desconecte.
     *
     * @param player a manejar
     */
    public void manage(Player player) {
        new Thread(() -> {
            boolean continuar = false;
            do {
                try{
                    String resultado_str = player.reader.nextLine();
                    int senal = Signal.ERROR;

                    senal = Integer.parseInt(resultado_str);
                    System.out.println("Senal de " + player.getName() + " recibida en el servidor: " +senal);


                    continuar = switch (senal) {
                        case Signal.CREAR_TORNEO_PUBLICO               -> manejarCrearTorneo(player, false);
                        case Signal.CREAR_TORNEO_PRIVADO               -> manejarCrearTorneo(player, true);
                        case Signal.UNIRSE_TORNEO_PUBLICO,
                                Signal.UNIRSE_TORNEO_PRIVADO -> manejarUnirseTorneo(player);
                        case Signal.SOLICITAR_LISTA_TORNEOS            -> manejarListaTorneos(player.writter);
                        default -> false;

                    };
                } catch (NoSuchElementException e){
                    System.out.println("El player con IP " + player.socket.getInetAddress().getHostName() + " se ha desconectado.");
                    continuar = false;
                }

            } while (continuar);
        }).start();
    }


    private boolean manejarListaTorneos(PrintStream writer){
//        HashMap<String, Partida> torneosPublicos = tournamentManager.mostrarTorneos();
//        writer.println(Signal.LISTA_TORNEOS);
//        writer.println(torneosPublicos.size());
//        torneosPublicos.forEach((key, value) -> {
//            String nombre = value.getGameName();
//            String jugadores = value.getPlayersList().size() + " /  4";
//
//            writer.println(
//                    nombre + Signal.SEPARADOR +
//                    jugadores + Signal.SEPARADOR +
//                    key);
//        });

        writer.printf("{signal: %d, data: {name: Hola, players: 123, key}}", Signal.LISTA_TORNEOS);
        return true;

    }


    private boolean manejarUnirseTorneo(Player jugador) throws NoSuchElementException {
        System.out.println("Unirse");
        String clave = jugador.reader.nextLine();

        jugador.writter.println(Signal.ENVIAR_NOMBRE);
        jugador.name = jugador.reader.nextLine();

        int resultado = tournamentManager.unirJugadorATorneo(jugador, clave);

        System.out.println("Clave: " + clave);
        System.out.println("Resultado: " + resultado);
        jugador.writter.println(resultado);

        if(resultado == Signal.UNION_EXITOSA_TORNEO){
            jugador.writter.println(Signal.NOMBRE_TORNEO);
            jugador.writter.println(tournamentManager.obtenerTorneo(clave).getGameName());

        }

        return resultado != Signal.UNION_EXITOSA_TORNEO;
    }

    /**
     * Crea un torneo y devuelve la clave para entrar a ese torneo (Solo si el usuario manda su nombre).
     * No unimos al jugador directamente porque no le hemos preguntado su nombre.
     * @param player
     * @param esPrivado Si el torneo es privado o no
     * @return Clave para unirse al torneo.
     */
    private boolean manejarCrearTorneo(Player player, boolean esPrivado) throws NoSuchElementException {
        String nombreDelTorneo = player.reader.nextLine();
        System.out.println("Datos:" + nombreDelTorneo);

        player.writter.println(Signal.ENVIAR_NOMBRE);
        player.name = player.reader.nextLine();

        Partida partida = new Partida(esPrivado, nombreDelTorneo);
        String clave = tournamentManager.agregarTorneo(player, partida);
        tournamentManager.unirJugadorATorneo(player, clave);

        if(esPrivado){
            player.writter.println(Signal.CLAVE_TORNEO);
            player.writter.println(clave);
        }

        player.writter.println(Signal.CONEXION_EXITOSA_TORNEO);

        return false;
    }
}