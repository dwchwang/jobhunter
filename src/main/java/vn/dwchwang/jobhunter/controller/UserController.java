package vn.dwchwang.jobhunter.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.turkraft.springfilter.boot.Filter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResCreateUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResUpdateUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.service.UserService;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // DTO for register request from FE (fullName, email, password, phone)
    @Getter
    @Setter
    public static class RegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String phone;
    }

    // DTO for register response (uses _id to match FE expectation)
    @Getter
    @Setter
    public static class RegisterResponse {
        @JsonProperty("_id")
        private long id;
        private String name;
        private String email;
    }

    @PostMapping("/user/register")
    @ApiMessage("Register new user")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest req) throws IdInvalidException {
        Boolean existingUser = this.userService.handleExistsByEmail(req.getEmail());
        if (existingUser) {
            throw new IdInvalidException(
                    "Email " + req.getEmail() + " đã tồn tại, hãy dùng email khác"
            );
        }
        User newUser = new User();
        newUser.setName(req.getFullName());
        newUser.setEmail(req.getEmail());
        newUser.setPassword(this.passwordEncoder.encode(req.getPassword()));
        this.userService.handleCreateUser(newUser);

        RegisterResponse res = new RegisterResponse();
        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody User inputUser) throws IdInvalidException {
        Boolean existingUser = this.userService.handleExistsByEmail(inputUser.getEmail());
        if(existingUser) {
            throw new IdInvalidException(
                    "Email " + inputUser.getEmail() + " da ton tai, hay dung email khac"
            );
        }
        String hashedPassword = this.passwordEncoder.encode(inputUser.getPassword());
        inputUser.setPassword(hashedPassword);
        this.userService.handleCreateUser(inputUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUser(inputUser));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable long id) throws IdInvalidException {
        User user = this.userService.handleGetUserById(id);
        if(user == null) {
            throw new IdInvalidException("User with id " + id + " not found");
        }
        this.userService.handleGetUserById(id);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable
            ) {
//
        return ResponseEntity.ok(this.userService.handleGetAllUsers(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User inputUser) throws IdInvalidException {
        User existingUser = this.userService.handleUpdateUserById(inputUser);
        if(existingUser == null) {
            throw new IdInvalidException("User voi id: " + inputUser.getId() + " khong ton tai");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUser(existingUser));
    }


    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws IdInvalidException {
        User existingUser = this.userService.handleGetUserById(id);
        if(existingUser == null) {
            throw new IdInvalidException("User voi id: " + id + " khong ton tai");
        }
        this.userService.handleRemoveUser(id);
        return ResponseEntity.ok(null);
    }
}

