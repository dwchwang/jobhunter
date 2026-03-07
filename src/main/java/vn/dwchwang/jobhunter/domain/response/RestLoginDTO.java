package vn.dwchwang.jobhunter.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestLoginDTO {
  @JsonProperty("access_token")
  private String accessToken;
  private UserLogin user;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserLogin {
    private long id;
    private String name;
    private String email;
    private RoleUser role;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RoleUser {
    private long id;
    private String name;
    private List<PermissionUser> permissions;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PermissionUser {
    private long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserGetAccount {
    private UserLogin user;
  }

}
