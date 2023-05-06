package com.tamalou.servidor.modelo.entidad.socketEntities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import org.springframework.lang.NonNull;

import java.io.Serializable;

public class Package implements Serializable {
    public static Gson gson = new Gson();

    public int signal;
    public JsonElement data;

    public Package(int signal, @NonNull Object data){
        this.signal = signal;
        this.data = gson.toJsonTree(data);
    }

    public Package(int signal){
        this.signal = signal;
        this.data = null;
    }

    public static String pack(int signal, @NonNull Object object){
        return String.format("{\"signal\": %d, \"data\": %s}", signal, gson.toJson(object));
    }

    public static String pack(int signal){
        return String.format("{\"signal\": %d}", signal);
    }

    @Override
    public String toString() {
        if(data != null){

            if(data.isJsonObject())
                return String.format("{\"signal\": %d, \"data\": %s}", signal, data.getAsJsonObject());

            else if (data.isJsonArray())
                return String.format("{\"signal\": %d, \"data\": %s}", signal, data.getAsJsonArray());

            else
                return String.format("{\"signal\": %d, \"data\": \"%s\"}", signal, data.getAsString());

        }

        return String.format("{\"signal\": %d}", signal);
    }
}
