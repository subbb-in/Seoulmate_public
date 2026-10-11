package com.ssafy.sm.domain.user.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.common.jwt.JwtTokenProvider;
import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.user.dto.TokenResponse;
import com.ssafy.sm.domain.user.dto.UserRequest;
import com.ssafy.sm.domain.user.dto.UserResponse;
import com.ssafy.sm.domain.user.entity.RefreshToken;
import com.ssafy.sm.domain.user.entity.User;
import com.ssafy.sm.domain.user.repository.RefreshTokenRepository;
import com.ssafy.sm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인 → Access + Refresh 토큰 발급
    @PostMapping("/login")
    @Transactional
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody UserRequest.Login loginDto) {
        User user = userRepository.findByEmailAndIsDeletedFalse(loginDto.getEmail())
                .orElse(null);

        if (user == null || !matchPassword(loginDto.getPw(), user.getPw())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("이메일 또는 비밀번호가 일치하지 않습니다."));
        }

        boolean isAdmin = user.getIsAdmin() != null && user.getIsAdmin();
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserNo(), user.getEmail(), isAdmin);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserNo());

        saveRefreshToken(user.getUserNo(), refreshToken);

        UserResponse userResponse = UserResponse.builder()
                .userNo(user.getUserNo())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birth(user.getBirth() != null ? java.time.LocalDate.parse(user.getBirth()) : null)
                .languageCode(user.getLanguageCode())
                .createdAt(user.getCreatedAt())
                .isAdmin(isAdmin)
                .profileImg(user.getProfileImg())
                .build();

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();

        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenResponse));
    }

    // 토큰 재발급 (Refresh → 새 Access 발급)
    @PostMapping("/reissue")
    @Transactional
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestHeader("Authorization") String bearerRefresh) {
        String refreshToken = bearerRefresh.startsWith("Bearer ") ? bearerRefresh.substring(7) : bearerRefresh;

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh 토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요."));
        }

        Long userNo = jwtTokenProvider.getUserNo(refreshToken);

        RefreshToken stored = refreshTokenRepository.findByUserNo(userNo).orElse(null);
        if (stored == null || !stored.getToken().equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("유효하지 않은 Refresh 토큰입니다. 다시 로그인해주세요."));
        }

        User user = userRepository.findByUserNoAndIsDeletedFalse(userNo).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
        }

        boolean isAdmin = user.getIsAdmin() != null && user.getIsAdmin();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserNo(), user.getEmail(), isAdmin);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserNo());

        saveRefreshToken(user.getUserNo(), newRefreshToken);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity.ok(ApiResponse.success("토큰 재발급 성공", tokenResponse));
    }

    // 로그아웃 (Refresh 토큰 삭제)
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> logout() {
        Long userNo = SecurityUtil.getCurrentUserNo();
        refreshTokenRepository.deleteByUserNo(userNo);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }

    // 비밀번호 매칭 (평문 호환 + BCrypt)
    private boolean matchPassword(String rawPassword, String storedPassword) {
        // BCrypt로 저장된 비밀번호
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        // 기존 평문 비밀번호 (마이그레이션 전 호환)
        return rawPassword.equals(storedPassword);
    }

    private void saveRefreshToken(Long userNo, String token) {
        long expiryMs = jwtTokenProvider.getRefreshTokenExpiry();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(expiryMs / 1000);

        RefreshToken refreshToken = refreshTokenRepository.findByUserNo(userNo)
                .orElse(null);

        if (refreshToken != null) {
            refreshToken.updateToken(token, expiryDate);
        } else {
            refreshToken = RefreshToken.builder()
                    .userNo(userNo)
                    .token(token)
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
    }
}
