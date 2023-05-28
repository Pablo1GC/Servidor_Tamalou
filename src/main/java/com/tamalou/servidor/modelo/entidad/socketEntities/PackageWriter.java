package com.tamalou.servidor.modelo.entidad.socketEntities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeWriter;
import org.springframework.lang.NonNull;

import java.io.OutputStream;
import java.io.PrintStream;

public class PackageWriter{

    private final PrintStream printStream;

    public PackageWriter(OutputStream stream) {
        this.printStream = new PrintStream(stream);
    }

    public void packAndWrite(int signal, @NonNull Object object){
        write(new Package(signal, object));
    }

    public void packAndWrite(int signal, @NonNull JsonField... fields){
        JsonObject object = new JsonObject();
        for (JsonField property : fields){
            object.add(property.getKey(), property.getValue());
        }

        packAndWrite(signal, object);
    }

    public void packAndWrite(int signal){
        write(new Package(signal));
    }

    public synchronized void write(Package pack){
        System.out.println(pack);
        printStream.println(pack);
    }
}
