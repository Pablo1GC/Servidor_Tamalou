package com.tamalou.servidor.modelo.entidad.socketEntities;

import com.google.gson.JsonElement;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class JsonField {

    private String key;
    private JsonElement value;

    public JsonField(String key, Object value){
        this.key = key;
        this.value = Package.gson.toJsonTree(value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }
}
