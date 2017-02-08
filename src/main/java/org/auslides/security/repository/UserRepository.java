package org.auslides.security.repository;

import org.auslides.security.model.User;

import java.util.List;

/**
 * DAO for {@link User}.
 */
public interface UserRepository {
    User findByEmail(String email);
    User findByEmailAndActive(String email, boolean active);
    List<User> findAll();
    void deleteAll();
    void save(User user);
    int count();
}
