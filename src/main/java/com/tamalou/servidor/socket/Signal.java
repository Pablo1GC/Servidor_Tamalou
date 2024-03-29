package com.tamalou.servidor.socket;

public class Signal {

    public static final int YES = 1;
    public static final int NO = 0;

    public static final int CONECTARSE = 299328;

    public static final int CONEXION_EXITOSA          = 58;



    public static final int PLAYER_JOINED_GAME        = 21;
    public static final int START_GAME                = 22;
    public static final int START_TURN                = 23;
    public static final int OTHER_PLAYER_TURN         = 24;
    public static final int END_GAME                  = 25;




    // ROUND SIGNALS
    public static final int END_ROUND                 = 131;
    public static final int SHOW_CARD_GRABBED = 132;
    public static final int SHOW_CARD_GRABBED_INDEX = 29329;
    public static final int SHOW_LAST_CARD_DISCARDED = 133;
    public static final int ASK_PLAYER_TO_STAND       = 137;
    public static final int PLAYER_STANDS             = 135;
    public static final int OTHER_PLAYER_SEES_CARD = 136;
    public static final int PLAYER_DISCARDS_CARD      = 140;
    public static final int PLAYER_SWITCH_CARD_DECK   = 138;
    public static final int ASK_PLAYER_SELECT_OPONENT = 143;
    public static final int ASK_PLAYER_SELECT_PLAY    = 141;
    public static final int ASK_PLAYER_CARD_OPTION = 142;
    public static final int ASK_PLAYER_SWITCH_CARD    = 145;
    public static final int ASK_PLAYER_SELECT_CARD    = 144;
    public static final int PLAYER_POINTS_PENALTY     = 2320;
    public static final int PLAYER_ONE_CARD_LESS      = 146;
    public static final int PLAYER_CARDS_EMPTY        = 148;
    public static final int OTHER_PLAYER_STANDS       = 149;
    public static final int PLAYER_SEES_OWN_CARD      = 150;
    public static final int PLAYER_SEES_OPONENT_CARD      = 1502;
    public static final int ASK_PLAYER_SELECT_OPONENT_CARD      = 151;
    public static final int PLAYERS_SWITCHED_CARDS = 192391;
    public static final int OTHER_PLAYER_SEES_OPONENT_CARD = 2321;

    public static final int PLAYER_SWITCH_CARD_PLAYER     = 152;
    public static final int PLAYER_DISCONNECTED = 154;



    public static final int CARD_DECK_TO_PLAYER       = 132;
    public static final int DISCARDED_DECK_IS_EMPTY = 133;
    public static final int DECK_IS_EMPTY             = 134;
    public static final int HAND_IS_EMPTY             = 135;
    public static final int PLAYER_TAKE_CARD_DECK     = 132;
    public static final int PLAYER_SWITCH_CARD_DISCARTED_DECK   = 132;


    public static final int GANADOR_DE_RONDA          = 200;
    public static final int GANADOR_DE_ENFRENTAMIENTO = 201;
    public static final int GANADOR_DE_TORNEO         = 202;

    public static final int NOMBRE_GANADOR_DEL_ENFRENTAMIENTO = 493;

    public static final int NOMBRE_GANADOR_DEL_TORNEO = 494;

    public static final int PERDEDOR_DE_RONDA           = 203;
    public static final int PERDEDOR_DE_ENFRENTAMIENTO  = 204;
    public static final int PERDEDOR_DE_TORNEO          = 205;

    public static final int EMPATE                      = 207;

    public static final int PLAYERS_POINTS = 808;

    public static final int COMENZAR_PARTIDA_FINAL = 209;

    public static final int PREGUNTA_REVANCHA         = 666;

    public static final int ERROR                     = -1;
    public static final int SELECCION_INCORRECTA      = -2;

    public static final char SEPARADOR                 = '|';

    public static final int LOBBY_LLENO =               810;
    public static final int SUCCESSFUL_MATCH_CREATION =   9030;

    public static final int DESCONECTADO               = 329;



    // Señales que puede enviar el cliente

    public static final int CREATE_PUBLIC_GAME = 10001;
    public static final int CREATE_PRIVATE_GAME = 10002;
    public static final int JOIN_PUBLIC_GAME = 10003;
    public static final int JOIN_PRIVATE_GAME = 10004;
    public static final int REQUEST_PUBLIC_GAME_LIST = 10005;
    public static final int LEAVE_GAME                      = 8071995;
    public static final int INVITE_PLAYER                   = 2020;

    public static final int INVITATION_RECEIVED             = 1930;

    public static final int REQUEST_FRIENDS_STATUS          = 2010;

    public static final int FRIENDS_STATUS                  = 3929;

    public static final int ASK_CONNECTED                   = 292810;

    // Señales con las que el servidor responde a esas peticiones

    public static final int TORNEO_LLENO                    = 20001;
    public static final int TORNEO_INEXISTENTE              = 20002;
    public static final int UNION_EXITOSA_TORNEO            = 20004;
    public static final int LISTA_TORNEOS                   = 20005;
    public static final int CLAVE_TORNEO                    = 20006;
    public static final int NOMBRE_TORNEO                   = 20007;
}