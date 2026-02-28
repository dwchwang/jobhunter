package vn.dwchwang.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.dwchwang.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter @Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updateAt;
    private CompanyUser company;

    @Getter @Setter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
