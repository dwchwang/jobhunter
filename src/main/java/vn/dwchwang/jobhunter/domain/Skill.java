package vn.dwchwang.jobhunter.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ten khong duoc de trong")
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "skills")
    @JsonIgnore
    List<Job> jobs;

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
