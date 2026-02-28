package vn.dwchwang.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.dwchwang.jobhunter.domain.Company;
import vn.dwchwang.jobhunter.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String username);
    Boolean existsByEmail(String email);
    User findByRefreshTokenAndEmail(String refresh_token, String email);
    List<User> findByCompany(Company com);
}
