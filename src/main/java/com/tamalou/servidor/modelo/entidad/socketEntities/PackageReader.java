package com.tamalou.servidor.modelo.entidad.socketEntities;

import com.google.gson.Gson;
import org.springframework.lang.NonNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class PackageReader {

    Gson gson = new Gson();

    private final Scanner reader;

    public PackageReader(Scanner reader) {
        this.reader = reader;
    }

    public Package readPackage(){
        String message = null;
        try {
            message = reader.nextLine();
            return gson.fromJson(message, Package.class);
        } catch (Exception e){
            System.out.println("Invalid json package: " + message);
            throw e;
        }
    }
}
