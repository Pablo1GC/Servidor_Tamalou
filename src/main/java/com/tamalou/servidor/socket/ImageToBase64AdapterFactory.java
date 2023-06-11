package com.tamalou.servidor.socket;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageToBase64AdapterFactory implements TypeAdapterFactory {
     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

       return new TypeAdapter<T>() {
         @Override
         public void write(JsonWriter out, T value) throws IOException {
           if (value == null) {
             out.nullValue();
           } else {
             String base64Image = Base64.getEncoder().encodeToString((byte[])value);
             out.value(base64Image);
           }
         }

         @Override
         public T read(JsonReader in) throws IOException {
           if (in.peek() == JsonToken.NULL) {
             in.nextNull();
             return null;
           } else {
             String base64Image = in.nextString();
             return (T) Base64.getDecoder().decode(base64Image);
           }
         }
       };
     }

   }
