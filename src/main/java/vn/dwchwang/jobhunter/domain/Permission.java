package vn.dwchwang.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Khong duoc de trong name")
    private String name;
    @NotBlank(message = "Khong duoc de trong apiPath")
    private String apiPath;
    @NotBlank(message = "Khong duoc de trong method")
    private String method;
    @NotBlank(message = "Khong duoc de trong module")
    private String module;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;

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
