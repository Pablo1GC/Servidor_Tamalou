package com.tamalou.servidor.servidor;

public class Signal {

    public static final int SI = 1;
    public static final int NO = 0;

    public static final int CONECTARSE = 299328;
    public static final int JUGADORES_EN_LOBBY        = 923;


    public static final int ENVIAR_SELECCION          = 51; // Piedra papel o tijera
    public static final int ENVIAR_SELECCION_RIVAL    = 52;
    public static final int ENVIAR_NOMBRE             = 53;

    public static final int NOMBRE_DEL_RIVAL          = 940;

    public static final int COMENZAR_ENFRENTAMIENTO   = 991;
    public static final int COMENZAR_TORNEO           = 992;

    public static final int FINAL_DE_RONDA            = 54;
    public static final int FINAL_DE_ENFRENTAMIENTO   = 55;
    public static final int FINAL_DE_TORNEO           = 56;
    public static final int CONEXION_EXITOSA          = 58;



    public static final int PIEDRA                    = 100;
    public static final int PAPEL                     = 101;
    public static final int TIJERA                    = 102;

    public static final int GANADOR_DE_RONDA          = 200;
    public static final int GANADOR_DE_ENFRENTAMIENTO = 201;
    public static final int GANADOR_DE_TORNEO         = 202;

    public static final int NOMBRE_GANADOR_DEL_ENFRENTAMIENTO = 493;

    public static final int NOMBRE_GANADOR_DEL_TORNEO = 494;

    public static final int PERDEDOR_DE_RONDA           = 203;
    public static final int PERDEDOR_DE_ENFRENTAMIENTO  = 204;
    public static final int PERDEDOR_DE_TORNEO          = 205;

    public static final int EMPATE                      = 207;

    public static final int PAQUETE_PUNTUACION          = 808;

    public static final int COMENZAR_PARTIDA_FINAL = 209;

    public static final int PREGUNTA_REVANCHA         = 666;

    public static final int ERROR                     = -1;
    public static final int SELECCION_INCORRECTA      = -2;

    public static final char SEPARADOR                 = '|';

    public static final int LOBBY_LLENO =               810;
    public static final int CONEXION_EXITOSA_TORNEO =   9030;

    public static final int DESCONECTADO               = 329;



    // Señales que puede enviar el cliente

    public static final int CREAR_TORNEO_PUBLICO            = 10001;
    public static final int CREAR_TORNEO_PRIVADO            = 10002;
    public static final int UNIRSE_TORNEO_PUBLICO = 10003;
    public static final int UNIRSE_TORNEO_PRIVADO = 10004;
    public static final int SOLICITAR_LISTA_TORNEOS         = 10005;


    // Señales con las que el servidor responde a esas peticiones

    public static final int TORNEO_LLENO                    = 20001;
    public static final int TORNEO_INEXISTENTE              = 20002;
    public static final int UNION_EXITOSA_TORNEO            = 20004;
    public static final int LISTA_TORNEOS                   = 20005;
    public static final int CLAVE_TORNEO                    = 20006;
    public static final int NOMBRE_TORNEO                   = 20007;
}