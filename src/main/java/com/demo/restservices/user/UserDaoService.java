package com.demo.restservices.user;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Repository
@Transactional
public class UserDaoService {

    private static List<User> users = new ArrayList<>();
    int usersCount = 3;

    static{
        users.add(new User(1, "Adam", new Date()));
        users.add(new User(2, "Eve", new Date()));
        users.add(new User(3, "Andrew", new Date()));
        users.add(new User(4, "Ryan", new Date()));
    }

    public List<User> findAll(){
        return users;
    }

    public User save(User user){
        if(user.getId() == null){
            user.setId(++usersCount);
        }
        users.add(user);
        return user;
    }

    public User findOne(int id){
        for (User user: users) {
            if(user.getId() == id)
                return user;
        }
        return null;
    }

    public User deleteById(int id){
        Iterator<User> iterator = users.iterator();
        for (User user: users) {
            if(user.getId() == id)
                return user;
        }
        return null;
    }
}
