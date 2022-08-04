package com.demo.restservices.user;

import com.demo.restservices.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class UserJPAResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping(value = "/jpa/users", produces = "application/json")
    public @ResponseBody List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(value = "/jpa/users/{id}")
    public @ResponseBody EntityModel<User> retrieveUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            throw new UserNotFoundException("IdNotFound: " + id);
        }
        EntityModel<User> resource = EntityModel.of(user.get());
        //se implementa el model de hateoas
        //EntityModel<User> model = EntityModel.of(user);
        //se crea el link para hateoas hacia el metodo retrieveAllUsers()
        WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        //se anade los links al respone de cada user
        resource.add(linkToUsers.withRel("all-users"));

        return resource;
    }

    @PostMapping("/jpa/users")
    public @ResponseBody ResponseEntity<Object> createUsers(@Valid @RequestBody User user){
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        //Se envia de vuelta un status code de 'created'
        System.out.println(location);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/jpa/users/{id}")
    public @ResponseBody void deleteById(@PathVariable int id){
        userRepository.deleteById(id);
    }

    @GetMapping("/jpa/users/{id}/posts")
    public @ResponseBody List<Post> retrieveAllPosts(@PathVariable int id){
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new UserNotFoundException("id not found-"+id);
        }
        return userOptional.get().getPosts();
    }

    @PostMapping("/jpa/users/{id}/posts")
    public @ResponseBody ResponseEntity<Object> createPost(@PathVariable int id,@RequestBody Post post){
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new UserNotFoundException("IdNotFound: " + id);
        }
        User user = userOptional.get();
        post.setUser(user);
        postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();
        //Se envia de vuelta un status code de 'created'
        System.out.println(location);
        return ResponseEntity.created(location).build();
    }

}
