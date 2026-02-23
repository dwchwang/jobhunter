package vn.dwchwang.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        this.userRepository.save(user);
        return user;
    }

    public void handleRemoveUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElse(null);
    }

    public List<User> handleGetAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleUpdateUserById(long id, User inputUser) {
        Optional<User> user = this.userRepository.findById(id);
        User updatedUser = user.orElse(null);

        if(updatedUser != null) {
            updatedUser.setName(inputUser.getName());
            updatedUser.setEmail(inputUser.getEmail());
            updatedUser.setPassword(inputUser.getPassword());

            updatedUser = this.userRepository.save(updatedUser);
        }
        return updatedUser;
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }
}
