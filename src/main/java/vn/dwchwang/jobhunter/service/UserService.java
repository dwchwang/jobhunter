package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.dto.Meta;
import vn.dwchwang.jobhunter.domain.dto.ResultPaginationDTO;
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

    public ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO userDto = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());
        userDto.setMeta(meta);
        userDto.setResult(pageUser.getContent());
        return userDto;
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
