package com.ssafy.sm.domain.user.repository;

import com.ssafy.sm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 회원 조회(중복 체크, 로그인)
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // PK로 조회
    Optional<User> findByUserNoAndIsDeletedFalse(Long userNo);
}
