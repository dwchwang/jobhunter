package vn.dwchwang.jobhunter.domain.response.Resume;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.constant.StatusEnum;

import java.time.Instant;

@Getter
@Setter
public class ResGetResumeDTO {
    private Long id;
    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private  String createdBy;
    private String updatedBy;

    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResume {
        private Long userId;
        private String userName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobResume {
        private Long jobId;
        private String jobName;
    }
}
