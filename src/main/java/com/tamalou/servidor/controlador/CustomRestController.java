package com.tamalou.servidor.controlador;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomRestController {

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> addUser() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.get(0).setUsername("Franco");
        users.get(0).setUsername("Brian");

        return new ResponseEntity<>(users, HttpStatus.FOUND);
    }


}
