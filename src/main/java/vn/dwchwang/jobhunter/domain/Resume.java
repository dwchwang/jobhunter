package vn.dwchwang.jobhunter.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.SecurityUtil;
import vn.dwchwang.jobhunter.util.constant.StatusEnum;

import java.time.Instant;


@Entity
@Table(name = "resumes")
@Getter
@Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "email khong duoc de trong")
    private String email;
    @NotBlank(message = "url khong duoc de trong")
    private String url;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private  String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

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
