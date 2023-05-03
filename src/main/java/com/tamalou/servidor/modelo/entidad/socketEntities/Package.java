package com.tamalou.servidor.modelo.entidad.socketEntities;

import java.io.Serializable;

public class Package<T> implements Serializable {

    public int signal;
    public T data;

    public Package(int signal, T data){
        this.signal = signal;
        this.data = data;
    }

    public static class MatchPackage extends Package<Match>{

        public MatchPackage(int signal, Match data) {
            super(signal, data);
        }
    }
}
