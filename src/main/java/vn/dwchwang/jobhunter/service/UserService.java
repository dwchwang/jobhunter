package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.dto.*;
import vn.dwchwang.jobhunter.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleCreateUser(User user) {
        this.userRepository.save(user);
    }

    public Boolean handleExistsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUser convertToResCreateUser(User user) {
        ResCreateUser resCreateUser = new ResCreateUser();
        resCreateUser.setId(user.getId());
        resCreateUser.setEmail(user.getEmail());
        resCreateUser.setName(user.getName());
        resCreateUser.setGender(user.getGender());
        resCreateUser.setAge(user.getAge());
        resCreateUser.setAddress(user.getAddress());
        resCreateUser.setCreatedAt(user.getCreatedAt());
        return resCreateUser;
    }

    public void handleRemoveUser(long id) {
        this.userRepository.deleteById(id);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdateAt(user.getUpdatedAt());
        return resUserDTO;
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

        List<ResUserDTO> resUserDTOList = pageUser.getContent()
                                                  .stream()
                                                  .map(item -> new ResUserDTO(
                                                          item.getId(),
                                                          item.getName(),
                                                          item.getEmail(),
                                                          item.getAge(),
                                                          item.getGender(),
                                                          item.getAddress(),
                                                          item.getCreatedAt(),
                                                          item.getUpdatedAt()
                                                  ))
                                                  .collect(Collectors.toList());
        userDto.setResult(resUserDTOList);
        return userDto;
    }

    public User handleUpdateUserById(User inputUser) {
        Optional<User> user = this.userRepository.findById(inputUser.getId());
        User updatedUser = user.orElse(null);

        if (updatedUser != null) {
            updatedUser.setName(inputUser.getName());
            updatedUser.setAge(inputUser.getAge());
            updatedUser.setAddress(inputUser.getAddress());
            updatedUser.setGender(inputUser.getGender());
            updatedUser = this.userRepository.save(updatedUser);
        }
        return updatedUser;
    }

    public ResUpdateUser convertToResUpdateUser(User user) {
        ResUpdateUser resCreateUser = new ResUpdateUser();
        resCreateUser.setId(user.getId());
        resCreateUser.setName(user.getName());
        resCreateUser.setGender(user.getGender());
        resCreateUser.setAge(user.getAge());
        resCreateUser.setAddress(user.getAddress());
        resCreateUser.setUpdateAt(user.getUpdatedAt());
        return resCreateUser;
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User user = this.handleGetUserByUserName(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

}
