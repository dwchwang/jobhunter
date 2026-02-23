package vn.dwchwang.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.service.UserService;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User inputUser) {
        String hashedPassword = this.passwordEncoder.encode(inputUser.getPassword());
        inputUser.setPassword(hashedPassword);
        return ResponseEntity.ok(this.userService.handleCreateUser(inputUser));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        User user = this.userService.handleGetUserById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        return this.userService.handleGetAllUsers();
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable long id, @RequestBody User inputUser) {
        return this.userService.handleUpdateUserById(id, inputUser);
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) throws IdInvalidException {
        if(id >= 1500){
            throw new IdInvalidException("Id khong qua 1500");
        }
        this.userService.handleRemoveUser(id);
        return ResponseEntity.ok("delete user with id: " + id);
    }
}

