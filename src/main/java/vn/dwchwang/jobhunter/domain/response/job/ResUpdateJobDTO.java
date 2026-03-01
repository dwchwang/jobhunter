package vn.dwchwang.jobhunter.domain.response.job;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {
    private Long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private List<String> skills;

    private Instant updateAt;
    private String updateBy;
}
