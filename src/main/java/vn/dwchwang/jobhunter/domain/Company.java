package vn.dwchwang.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Ten cong ty ko duoc de trong")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private  String createdBy;
    private String updatedBy;

    @OneToMany( mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users;

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
