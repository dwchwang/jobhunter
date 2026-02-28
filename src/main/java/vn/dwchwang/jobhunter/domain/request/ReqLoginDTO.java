package vn.dwchwang.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;

public class ReqLoginDTO {

    @NotBlank(message = "Username khong duoc de trong")
    private String username;
    @NotBlank(message = "Password khong duoc de trong")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
