package org.auslides.security.repository;

import org.auslides.security.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {
    private Map<String, User> cache = new HashMap<>() ;

    @Override
    public User findByEmail(String email) {
        return cache.get(email);
    }

    @Override
    public User findByEmailAndActive(String email, boolean active) {
        return cache.get(email);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(cache.values()) ;
    }

    @Override
    public void deleteAll() {
        cache.clear();
    }

    @Override
    public void save(User user) {
        cache.put(user.getEmail(), user) ;
    }

    @Override
    public int count() {
        return cache.size();
    }
}
