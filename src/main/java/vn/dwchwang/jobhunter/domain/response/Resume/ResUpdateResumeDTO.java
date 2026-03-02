package vn.dwchwang.jobhunter.domain.response.Resume;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateResumeDTO {
    private Long id;
    private Instant updatedAt;
    private String updatedBy;
}
