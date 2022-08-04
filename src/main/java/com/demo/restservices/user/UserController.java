package com.demo.restservices.user;

import com.demo.restservices.exception.UserNotFoundException;
import com.demo.restservices.exception.UsersListEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserDaoService dao;

    @GetMapping(value = "/users", produces = "application/json")
    public @ResponseBody List<User> retrieveAllUsers() {
        List<User> list = dao.findAll();
        if(list.isEmpty()){
            throw new UsersListEmptyException("List is Empty");
        }
        return list;
    }

    @GetMapping(value = "/users/{id}")
    public @ResponseBody EntityModel<User> retrieveUser(@PathVariable int id){
        User user = dao.findOne(id);
        System.out.println(user);
        if(user == null){
            throw new UserNotFoundException("IdNotFound: " + id);
        }
        //se implementa el model de hateoas
        EntityModel<User> model = EntityModel.of(user);
        //se crea el link para hateoas hacia el metodo retrieveAllUsers()
        WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        //se anade los links al respone de cada user
        model.add(linkToUsers.withRel("all-users"));

        return model;
    }

    @PostMapping("/users")
    public @ResponseBody ResponseEntity<Object> createUsers(@Valid @RequestBody User user){
        User savedUser = dao.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        //Se envia de vuelta un status code de 'created'
        System.out.println(location);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/users/{id}")
    public @ResponseBody void deleteById(@PathVariable int id){
        User deletedUser = dao.deleteById(id);
        if(deletedUser == null){
            throw new UserNotFoundException("id not found - "+id);
        }
    }

}
