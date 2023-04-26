package com.tamalou.servidor.socket;

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


    // CARDS
    public static final int CARD_1                    = 101;
    public static final int CARD_2                    = 102;
    public static final int CARD_3                    = 103;
    public static final int CARD_4                    = 104;
    public static final int CARD_5                    = 105;
    public static final int CARD_6                    = 106;
    public static final int CARD_7                    = 107;
    public static final int CARD_8                    = 108;
    public static final int CARD_9                    = 109;
    public static final int CARD_10                   = 110;
    public static final int CARD_11                   = 111;
    public static final int CARD_12                   = 112;
    public static final int CARD_13                   = 113;
    public static final int SUIT_HEART                = 120;
    public static final int SUIT_SPADES               = 121;
    public static final int SUIT_DIAMONDS             = 122;
    public static final int SUIT_CLUBS                = 123;

    // ROUND SIGNALS
    public static final int END_ROUND                 = 131;
    public static final int CARD_DECK_TO_PLAYER       = 132;
    public static final int MAZE_IS_EMPTY             = 132;
    public static final int DECK_IS_EMPTY             = 132;
    public static final int HAND_IS_EMPTY             = 132;
    public static final int SHOW_LAST_CARD_DECK       = 132;
    public static final int ASK_PLAYER_TO_STAND       = 132;
    public static final int PLAYER_STANDS             = 132;
    public static final int PLAYER_TAKE_CARD_DECK     = 132;
    public static final int PLAYER_DISCARDS_CARD      = 132;
    public static final int PLAYER_SWITCH_CARD_MAZE   = 132;
    public static final int PLAYER_USE_CARD_POWER     = 132;
    public static final int ASK_PLAYER_SELECT_OPONENT = 132;
    public static final int ASK_PLAYER_SELECT_CARD    = 132;
    public static final int ASK_PLAYER_SWITCH_CARD    = 132;
    public static final int PLAYER_POINTS_PENALTY     = 132;


    public static final int PLAYER_1                  = 132;
    public static final int PLAYER_2                  = 132;
    public static final int PLAYER_3                  = 132;
    public static final int PLAYER_4                  = 132;

    public static final int PLAYER_CARD_1             = 132;
    public static final int PLAYER_CARD_2             = 132;
    public static final int PLAYER_CARD_3             = 132;
    public static final int PLAYER_CARD_4             = 132;

















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
    public static final int UNIRSE_TORNEO_PUBLICO           = 10003;
    public static final int UNIRSE_TORNEO_PRIVADO           = 10004;
    public static final int SOLICITAR_LISTA_TORNEOS         = 10005;


    // Señales con las que el servidor responde a esas peticiones

    public static final int TORNEO_LLENO                    = 20001;
    public static final int TORNEO_INEXISTENTE              = 20002;
    public static final int UNION_EXITOSA_TORNEO            = 20004;
    public static final int LISTA_TORNEOS                   = 20005;
    public static final int CLAVE_TORNEO                    = 20006;
    public static final int NOMBRE_TORNEO                   = 20007;
}