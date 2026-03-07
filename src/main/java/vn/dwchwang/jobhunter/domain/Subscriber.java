package vn.dwchwang.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "subscribers")
@Getter
@Setter
public class Subscriber {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  @Column(unique = true)
  private String email;

  @ManyToMany(fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { "subscribers" })
  @JoinTable(name = "subscriber_skill", joinColumns = @JoinColumn(name = "subscriber_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private List<Skill> skills;

  private Instant createdAt;
  private Instant updatedAt;
  private String createdBy;
  private String updatedBy;

  @PrePersist
  public void handleBeforeCreated() {
    this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void handleBeforeUpdated() {
    this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
    this.updatedAt = Instant.now();
  }
}
