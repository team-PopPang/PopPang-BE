package com.poppang.be.domain.auth.presentation;

import com.poppang.be.domain.auth.apple.application.AppleAuthService;
import com.poppang.be.domain.auth.apple.dto.request.AppleAppLoginRequestDto;
import com.poppang.be.domain.auth.application.AuthService;
import com.poppang.be.domain.auth.dto.request.AutoLoginRequestDto;
import com.poppang.be.domain.auth.dto.response.LoginResponseDto;
import com.poppang.be.domain.auth.dto.response.SignupResponseDto;
import com.poppang.be.domain.auth.google.application.GoogleAuthService;
import com.poppang.be.domain.auth.google.dto.request.GoogleAppLoginRequestDto;
import com.poppang.be.domain.auth.kakao.application.KakaoAuthService;
import com.poppang.be.domain.auth.kakao.dto.request.KakaoAppLoginRequestDto;
import com.poppang.be.domain.auth.kakao.dto.request.SignupRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "소셜 로그인/회원가입 및 자동 로그인 API")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final AppleAuthService appleAuthService;
    private final GoogleAuthService googleAuthService;
    private final AuthService authService;

    /* ---------- 웹(브라우저)용: GET code 콜백 ---------- */

    @Operation(
            summary = "[WEB] 카카오 로그인 콜백",
            description = "카카오 OAuth 인가코드(code)로 액세스 토큰 교환 후, 사용자 식별/업서트 및 유저 정보 반환"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/kakao/login")
    public ResponseEntity<LoginResponseDto> kakaoWebLogin(
            @Parameter(description = "카카오 OAuth 인가 코드", required = true)
            @RequestParam("code") String authCode
    ) {
        return ResponseEntity.ok(kakaoAuthService.webLogin(authCode));
    }

    @Operation(summary = "[WEB] 애플 로그인 콜백", description = "애플 인가코드로 로그인 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @GetMapping("/apple/login")
    public ResponseEntity<LoginResponseDto> appleWebLogin(
            @Parameter(description = "애플 OAuth 인가 코드", required = true)
            @RequestParam("code") String authCode
    ) {
        return ResponseEntity.ok(appleAuthService.webLogin(authCode));
    }

    @Operation(summary = "[WEB] 구글 로그인 콜백", description = "구글 인가코드로 로그인 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @GetMapping("/google/login")
    public ResponseEntity<LoginResponseDto> googleWebLogin(
            @Parameter(description = "구글 OAuth 인가 코드", required = true)
            @RequestParam("code") String authCode
    ) {
        return ResponseEntity.ok(googleAuthService.webLogin(authCode));
    }

    /* ---------- 앱(Native)용: POST JSON 바디 ---------- */
    @Operation(summary = "[APP] 카카오 로그인", description = "카카오 앱에서 받은 액세스 토큰 등으로 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @PostMapping("/kakao/mobile/login")
    public ResponseEntity<LoginResponseDto> kakaoMobileLogin(
            @RequestBody(
                    description = "카카오 앱 로그인 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = KakaoAppLoginRequestDto.class))
            )
            @org.springframework.web.bind.annotation.RequestBody KakaoAppLoginRequestDto body
    ) {
        return ResponseEntity.ok(kakaoAuthService.mobileLogin(body));
    }

    @Operation(summary = "[APP] 애플 로그인", description = "애플 앱에서 받은 토큰/ID 토큰으로 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @PostMapping("/apple/mobile/login")
    public ResponseEntity<LoginResponseDto> appleMobileLogin(
            @RequestBody(
                    description = "애플 앱 로그인 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AppleAppLoginRequestDto.class))
            )
            @org.springframework.web.bind.annotation.RequestBody AppleAppLoginRequestDto body
    ) {
        return ResponseEntity.ok(appleAuthService.mobileLogin(body));
    }

    @Operation(summary = "[APP] 구글 로그인", description = "구글 앱에서 받은 액세스 토큰 등으로 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)))
    })
    @PostMapping("/google/mobile/login")
    public ResponseEntity<LoginResponseDto> googleMobileLogin(
            @RequestBody(
                    description = "구글 앱 로그인 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GoogleAppLoginRequestDto.class))
            )
            @org.springframework.web.bind.annotation.RequestBody GoogleAppLoginRequestDto body
    ) {
        return ResponseEntity.ok(googleAuthService.mobileLogin(body));
    }

    /* ---------- 앱 자동 로그인 ---------- */

    @Operation(summary = "자동 로그인", description = "앱 로컬에 보관 중인 uid로 사용자 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @PostMapping("/autoLogin")
    public ResponseEntity<LoginResponseDto> autoLogin(
            @RequestBody(
                    description = "자동 로그인 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AutoLoginRequestDto.class))
            )
            @org.springframework.web.bind.annotation.RequestBody AutoLoginRequestDto body
    ) {
        return ResponseEntity.ok(authService.autoLogin(body));
    }

    /* ---------- 회원가입 ---------- */

    @Operation(summary = "[카카오] 회원가입", description = "카카오 로그인 이후, 닉네임/알림/키워드/추천 카테고리 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(schema = @Schema(implementation = SignupResponseDto.class)))
    })
    @PostMapping("/kakao/signup")
    public ResponseEntity<SignupResponseDto> kakaoSignup(
            @RequestBody(
                    description = "회원가입 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignupRequestDto.class))
            )
            @org.springframework.web.bind.annotation.RequestBody SignupRequestDto body
    ) {
        return ResponseEntity.ok(kakaoAuthService.signup(body));
    }

    @Operation(summary = "[애플] 회원가입", description = "애플 로그인 이후 회원가입 완료 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(schema = @Schema(implementation = SignupResponseDto.class)))
    })
    @PostMapping("/apple/signup")
    public ResponseEntity<SignupResponseDto> appleSignup(
            @org.springframework.web.bind.annotation.RequestBody SignupRequestDto body
    ) {
        return ResponseEntity.ok(appleAuthService.signup(body));
    }

    @Operation(summary = "[구글] 회원가입", description = "구글 로그인 이후 회원가입 완료 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(schema = @Schema(implementation = SignupResponseDto.class)))
    })
    @PostMapping("/google/signup")
    public ResponseEntity<SignupResponseDto> googleSignup(
            @org.springframework.web.bind.annotation.RequestBody SignupRequestDto body
    ) {
        return ResponseEntity.ok(googleAuthService.signup(body));
    }

}