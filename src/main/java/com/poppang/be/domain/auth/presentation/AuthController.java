package com.poppang.be.domain.auth.presentation;

import com.poppang.be.domain.auth.apple.application.AppleAuthService;
import com.poppang.be.domain.auth.apple.dto.request.AppleAppLoginRequestDto;
import com.poppang.be.domain.auth.dto.request.AutoLoginRequestDto;
import com.poppang.be.domain.auth.application.AuthService;
import com.poppang.be.domain.auth.google.application.GoogleAuthService;
import com.poppang.be.domain.auth.google.dto.request.GoogleAppLoginRequestDto;
import com.poppang.be.domain.auth.kakao.application.KakaoAuthService;
import com.poppang.be.domain.auth.kakao.dto.request.KakaoAppLoginRequestDto;
import com.poppang.be.domain.auth.kakao.dto.request.SignupRequestDto;
import com.poppang.be.domain.auth.dto.response.LoginResponseDto;
import com.poppang.be.domain.auth.dto.response.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final AppleAuthService appleAuthService;
    private final GoogleAuthService googleAuthService;
    private final AuthService authService;

    /* ---------- 웹(브라우저)용: GET code 콜백 ---------- */
    // [카카오] 로그인
    @GetMapping("/kakao/login")
    public ResponseEntity<LoginResponseDto> kakaoWebLogin(@RequestParam("code") String authCode) {
        LoginResponseDto loginResponseDto = kakaoAuthService.webLogin(authCode);

        return ResponseEntity.ok(loginResponseDto);
    }

    // [애플] 로그인
    @GetMapping("/apple/login")
    public ResponseEntity<LoginResponseDto> appleWebLogin(@RequestParam("code") String authCode) {
        LoginResponseDto loginResponseDto = appleAuthService.webLogin(authCode);

        return ResponseEntity.ok(loginResponseDto);
    }

    // [구글] 로그인
    @GetMapping("/google/login")
    public ResponseEntity<LoginResponseDto> googleWebLogin(@RequestParam("code") String authCode) {
        LoginResponseDto loginResponseDto = googleAuthService.webLogin(authCode);

        return ResponseEntity.ok(loginResponseDto);
    }

    /* ---------- 앱(Native)용: POST JSON 바디 ---------- */
    // [카카오] 로그인
    @PostMapping("/kakao/mobile/login")
    public ResponseEntity<LoginResponseDto> kakaoMobileLogin(@RequestBody KakaoAppLoginRequestDto kakaoAppLoginRequestDto) {
        LoginResponseDto loginResponseDto = kakaoAuthService.mobileLogin(kakaoAppLoginRequestDto);

        return ResponseEntity.ok(loginResponseDto);
    }

    // [애플] 로그인
    @PostMapping("/apple/mobile/login")
    public ResponseEntity<LoginResponseDto> appleMobileLogin(@RequestBody AppleAppLoginRequestDto appleAppLoginRequestDto) {
        LoginResponseDto loginResponseDto = appleAuthService.mobileLogin(appleAppLoginRequestDto);

        return ResponseEntity.ok(loginResponseDto);
    }

    // [구글] 로그인
    @PostMapping("/google/mobile/login")
    public ResponseEntity<LoginResponseDto> googleMobileLogin(@RequestBody GoogleAppLoginRequestDto googleAppLoginRequestDto) {
        LoginResponseDto loginResponseDto = googleAuthService.mobileLogin(googleAppLoginRequestDto);

        return ResponseEntity.ok(loginResponseDto);
    }

    /* ---------- 앱 자동 로그인 ---------- */
    @PostMapping("/autoLogin")
    public ResponseEntity<LoginResponseDto> autoLogin(@RequestBody AutoLoginRequestDto autoLoginRequestDto) {
        LoginResponseDto loginResponseDto = authService.autoLogin(autoLoginRequestDto);

        return ResponseEntity.ok(loginResponseDto);
    }

    /* ---------- 회원가입 ---------- */
    // [카카오] 회원가입
    @PostMapping("/kakao/signup")
    public ResponseEntity<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto signupRequestDto) {

        SignupResponseDto signupResponseDto = kakaoAuthService.signup(signupRequestDto);

        return ResponseEntity.ok(signupResponseDto);
    }

    // [애플] 회원가입
    @PostMapping("/apple/signup")
    public ResponseEntity<SignupResponseDto> appleSignup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto signupResponseDto = appleAuthService.signup(signupRequestDto);

        return ResponseEntity.ok(signupResponseDto);
    }

    // [구글] 회원가입
    @PostMapping("/google/signup")
    public ResponseEntity<SignupResponseDto> googleSignup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto signupResponseDto = googleAuthService.signup(signupRequestDto);

        return ResponseEntity.ok(signupResponseDto);
    }

}
