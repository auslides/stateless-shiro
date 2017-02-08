package org.auslides.security.rest;

import org.auslides.security.model.Permission;
import org.auslides.security.model.Role;
import org.auslides.security.model.User;
import org.auslides.security.repository.UserRepository;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private DefaultPasswordService passwordService;

    @Autowired
    private UserRepository userRepo;

    @PutMapping("/init")
    public ResponseEntity initScenario() {
        log.info("Initializing scenario..");
        // clean-up users, roles and permissions
        userRepo.deleteAll();

        // define permissions
        final Permission p1 = new Permission();
        p1.setName("VIEW_ALL_USERS");

        final Permission p2 = new Permission();
        p2.setName("DO_SOMETHING");

        // define roles
        final Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleAdmin.getPermissions().add(p1);
        final Role roleDoSomthing = new Role();
        roleDoSomthing.setName("DO_SOMETHING");
        roleDoSomthing.getPermissions().add(p2);

        // define user
        final User user = new User();
        user.setActive(true);
        user.setEmail("balala@gmail.com");
        user.setName("Balala");
        user.setPassword(passwordService.encryptPassword("1111"));
        user.getRoles().add(roleAdmin);
        userRepo.save(user);

        final User dsUser = new User() ;
        dsUser.setActive(true);
        dsUser.setEmail("dopper@gmail.com");
        dsUser.setName("Dopper");
        dsUser.setPassword(passwordService.encryptPassword("1111"));
        dsUser.getRoles().add(roleDoSomthing);
        userRepo.save(dsUser);

        log.info("Scenario initiated.");
        return new ResponseEntity(HttpStatus.OK) ;
    }

    @PostMapping(value = "auth")
    public void authenticate(@RequestBody final UsernamePasswordToken credentials) {
        log.info("Authenticating {}", credentials.getUsername());
        final Subject subject = SecurityUtils.getSubject();
        subject.login(credentials);
    }

    @DeleteMapping("logout")
    @RequiresAuthentication
    public void deleteLoginToken() {
        // this was caught by BearerTokenRevokeFilter
    }

    @GetMapping
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @GetMapping(value = "do_something")
    @RequiresAuthentication
    @RequiresRoles("DO_SOMETHING")
    public List<User> dontHavePermission() {
        return userRepo.findAll();
    }
}
