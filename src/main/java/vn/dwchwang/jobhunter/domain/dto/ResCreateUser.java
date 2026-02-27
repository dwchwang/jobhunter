package vn.dwchwang.jobhunter.domain.dto;


import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Setter @Getter
public class ResCreateUser {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
}
