package vn.dwchwang.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter @Setter
public class ResUpdateUser {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updateAt;
}
