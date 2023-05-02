package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import com.tamalou.servidor.socket.Signal;

import java.io.Serializable;
import java.util.List;

public class Package implements Serializable {

    public Package(List<Integer> signals, String data){
        this.signals = signals;
        this.data = data;
    }
    public List<Integer> signals;
    public String data;
}
