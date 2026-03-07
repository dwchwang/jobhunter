package vn.dwchwang.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.Role;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.request.ReqLoginDTO;
import vn.dwchwang.jobhunter.domain.response.ResCreateUserDTO;
import vn.dwchwang.jobhunter.domain.response.RestLoginDTO;
import vn.dwchwang.jobhunter.service.UserService;
import vn.dwchwang.jobhunter.util.SecurityUtil;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.refresh-token-validity-in-seconds}")
  private Long refreshTokenExpiration;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
      SecurityUtil securityUtil,
      UserService userService,
      PasswordEncoder passwordEncoder) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  // DTO for register request
  @lombok.Getter
  @lombok.Setter
  public static class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private int age;
    private String gender;
    private String address;
  }

  @PostMapping("/auth/register")
  @ApiMessage("Register a new user")
  public ResponseEntity<ResCreateUserDTO> register(@RequestBody RegisterRequest req) throws IdInvalidException {
    if (this.userService.handleExistsByEmail(req.getEmail())) {
      throw new IdInvalidException("Email " + req.getEmail() + " đã tồn tại, hãy dùng email khác");
    }
    User newUser = new User();
    newUser.setName(req.getName());
    newUser.setEmail(req.getEmail());
    newUser.setPassword(this.passwordEncoder.encode(req.getPassword()));
    newUser.setAge(req.getAge());
    newUser.setAddress(req.getAddress());
    // Set gender via enum
    try {
      newUser.setGender(vn.dwchwang.jobhunter.util.constant.GenderEnum.valueOf(req.getGender()));
    } catch (Exception ignored) {
    }
    this.userService.handleCreateUser(newUser);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.userService.convertToResCreateUser(newUser));
  }

  @PostMapping("/auth/login")
  @ApiMessage("Login")
  public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        reqLoginDTO.getUsername(), reqLoginDTO.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    RestLoginDTO restLoginDTO = new RestLoginDTO();
    User currentUserDB = this.userService.handleGetUserByUserName(reqLoginDTO.getUsername());
    if (currentUserDB != null) {
      restLoginDTO.setUser(buildUserLogin(currentUserDB));
    }

    String access_Token = this.securityUtil.createAccessToken(authentication.getName(), restLoginDTO.getUser());
    restLoginDTO.setAccessToken(access_Token);

    String refresh_token = this.securityUtil.createRefreshToken(reqLoginDTO.getUsername(), restLoginDTO);
    this.userService.updateUserToken(refresh_token, reqLoginDTO.getUsername());

    ResponseCookie resCookie = ResponseCookie
        .from("refresh_token", refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookie.toString())
        .body(restLoginDTO);
  }

  @GetMapping("/auth/account")
  @ApiMessage("fetch account")
  public ResponseEntity<RestLoginDTO.UserGetAccount> getAccount() {
    String email = SecurityUtil.getCurrentUserLogin().isPresent()
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";
    User currentUserDB = this.userService.handleGetUserByUserName(email);
    RestLoginDTO.UserGetAccount userGetAccount = new RestLoginDTO.UserGetAccount();
    if (currentUserDB != null) {
      userGetAccount.setUser(buildUserLogin(currentUserDB));
    }
    return ResponseEntity.ok(userGetAccount);
  }

  @GetMapping("/auth/refresh")
  @ApiMessage("Get user by refresh token")
  public ResponseEntity<RestLoginDTO> getRefreshToken(
      @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws IdInvalidException {
    if (refresh_token.equals("abc")) {
      throw new IdInvalidException("refresh token is not found in cookies");
    }
    Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
    String email = decodedToken.getSubject();
    User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
    if (currentUser == null) {
      throw new IdInvalidException("Refresh Token không hợp lệ");
    }

    RestLoginDTO restLoginDTO = new RestLoginDTO();
    User currentUserDB = this.userService.handleGetUserByUserName(email);
    if (currentUserDB != null) {
      restLoginDTO.setUser(buildUserLogin(currentUserDB));
    }

    String access_Token = this.securityUtil.createAccessToken(email, restLoginDTO.getUser());
    restLoginDTO.setAccessToken(access_Token);

    String new_refresh_token = this.securityUtil.createRefreshToken(email, restLoginDTO);
    this.userService.updateUserToken(new_refresh_token, email);

    ResponseCookie resCookie = ResponseCookie
        .from("refresh_token", new_refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookie.toString())
        .body(restLoginDTO);
  }

  @PostMapping("/auth/logout")
  @ApiMessage("Logout user")
  public ResponseEntity<Void> logout() throws IdInvalidException {
    String email = SecurityUtil.getCurrentUserLogin().isPresent()
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";
    if (email.isEmpty()) {
      throw new IdInvalidException("Access token không hợp lệ");
    }
    this.userService.updateUserToken(null, email);
    ResponseCookie deleteSpringCookie = ResponseCookie
        .from("refresh_token", null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
        .body(null);
  }

  /**
   * Build RestLoginDTO.UserLogin from User entity, including role + permissions.
   */
  private RestLoginDTO.UserLogin buildUserLogin(User user) {
    RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();
    userLogin.setId(user.getId());
    userLogin.setName(user.getName());
    userLogin.setEmail(user.getEmail());

    Role role = user.getRole();
    if (role != null) {
      List<RestLoginDTO.PermissionUser> permissionUsers = null;
      if (role.getPermissions() != null) {
        permissionUsers = role.getPermissions().stream()
            .map(p -> new RestLoginDTO.PermissionUser(
                p.getId(), p.getName(), p.getApiPath(), p.getMethod(), p.getModule()))
            .collect(Collectors.toList());
      }
      userLogin.setRole(new RestLoginDTO.RoleUser(role.getId(), role.getName(), permissionUsers));
    }
    return userLogin;
  }
}
