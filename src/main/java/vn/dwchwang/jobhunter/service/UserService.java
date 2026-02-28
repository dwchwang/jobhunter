package vn.dwchwang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.response.ResCreateUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResUpdateUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResUserDTO;
import vn.dwchwang.jobhunter.domain.response.ResultPaginationDTO;
import vn.dwchwang.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public void handleCreateUser(User user) {
        //check Company
        if(user.getCompany() != null) {
            Optional<Company> optionalCompany = this.companyService.findById(user.getCompany().getId());
            user.setCompany(optionalCompany.orElse(null));
        }
        this.userRepository.save(user);
    }

    public Boolean handleExistsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUser(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUserDTO = new ResCreateUserDTO.CompanyUser();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());

        if(user.getCompany() != null) {
            companyUserDTO.setId(user.getCompany().getId());
            companyUserDTO.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(companyUserDTO);
        }

        return resCreateUserDTO;
    }

    public void handleRemoveUser(long id) {
        this.userRepository.deleteById(id);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser companyUserDTO = new ResUserDTO.CompanyUser();
        if(user.getCompany() != null) {
            companyUserDTO.setId(user.getCompany().getId());
            companyUserDTO.setName(user.getCompany().getName());
            resUserDTO.setCompany(companyUserDTO);
        }
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
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
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
                                                          item.getUpdatedAt(),
                                                          new ResUserDTO.CompanyUser(
                                                                  item.getCompany() != null ?
                                                                          item.getCompany().getId() : 0,
                                                                  item.getCompany() != null ?
                                                                          item.getCompany().getName() : null
                                                          )
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
            //check company
            if(inputUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(inputUser.getCompany().getId());
                updatedUser.setCompany(companyOptional.orElse(null));
            }
            updatedUser = this.userRepository.save(updatedUser);
        }

        return updatedUser;
    }

    public ResUpdateUserDTO convertToResUpdateUser(User user) {
        ResUpdateUserDTO resCreateUser = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUserDTO = new ResUpdateUserDTO.CompanyUser();
        resCreateUser.setId(user.getId());
        resCreateUser.setName(user.getName());
        resCreateUser.setGender(user.getGender());
        resCreateUser.setAge(user.getAge());
        resCreateUser.setAddress(user.getAddress());
        resCreateUser.setUpdateAt(user.getUpdatedAt());
        if(user.getCompany() != null) {
            companyUserDTO.setId(user.getCompany().getId());
            companyUserDTO.setName(user.getCompany().getName());
            resCreateUser.setCompany(companyUserDTO);
        }
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
