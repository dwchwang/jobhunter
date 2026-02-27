package vn.dwchwang.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.dwchwang.jobhunter.domain.User;
import vn.dwchwang.jobhunter.domain.dto.LoginDTO;
import vn.dwchwang.jobhunter.domain.dto.RestLoginDTO;
import vn.dwchwang.jobhunter.service.UserService;
import vn.dwchwang.jobhunter.util.SecurityUtil;
import vn.dwchwang.jobhunter.util.annotation.ApiMessage;
import vn.dwchwang.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                          UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // set thong tin nguoi dung dang nhap vao context (co the su dung sau nay)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        RestLoginDTO restLoginDTO = new RestLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if (currentUserDB != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(currentUserDB.getId(),
                                                                          currentUserDB.getName(),
                                                                          currentUserDB.getEmail());
            restLoginDTO.setUser(userLogin);
            restLoginDTO.setUser(userLogin);
        }
        // create access token
        String access_Token = this.securityUtil.createAccessToken(authentication.getName(), restLoginDTO.getUser());
        restLoginDTO.setAccessToken(access_Token);

        // create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), restLoginDTO);
        //update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // set cookies
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
    public ResponseEntity<RestLoginDTO.UserGetAccount> getAccount(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.handleGetUserByUserName(email);
        RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();
        RestLoginDTO.UserGetAccount userGetAccount = new RestLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setName(currentUserDB.getName());
            userLogin.setEmail(currentUserDB.getEmail());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<RestLoginDTO>  getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token
            ) throws IdInvalidException {
        if(refresh_token.equals("abc")){
            throw new IdInvalidException("refresh token is not found in cookies");
        }
        //check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email =  decodedToken.getSubject();
        // check token + email
        User currentUser= this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if(currentUser == null) {
            throw new IdInvalidException("Refresh Token khong hop le");
        }
        RestLoginDTO restLoginDTO = new RestLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        if (currentUserDB != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(currentUserDB.getId(),
                                                                          currentUserDB.getName(),
                                                                          currentUserDB.getEmail());
            restLoginDTO.setUser(userLogin);
            restLoginDTO.setUser(userLogin);
        }
        // create access token
        String access_Token = this.securityUtil.createAccessToken(email, restLoginDTO.getUser());
        restLoginDTO.setAccessToken(access_Token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, restLoginDTO);
        //update user
        this.userService.updateUserToken(new_refresh_token, email);

        // set cookies
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
    public ResponseEntity<Void>  logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if(email.isEmpty()){
            throw new IdInvalidException("Access token khong hop le");
        }
        //update refresh token = null
        this.userService.updateUserToken(null, email);
        // remove refresh token from cookie
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
}
