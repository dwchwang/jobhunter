package vn.dwchwang.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.SecurityUtil;
import vn.dwchwang.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Entity
@Getter @Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
    private String password;


    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private  String createdBy;
    private String updatedBy;

    public User() {
    }
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
    }

    @PrePersist
    public void hanldeBeforeCreated() {
        this.createdBy = SecurityUtil.getCurrentUserLogin()
                                     .isPresent() ? SecurityUtil.getCurrentUserLogin()
                                                                .get() : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void hanldeBeforeUpdated() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin()
                                     .isPresent() ? SecurityUtil.getCurrentUserLogin()
                                                                .get() : "";
        this.updatedAt = Instant.now();
    }
}
