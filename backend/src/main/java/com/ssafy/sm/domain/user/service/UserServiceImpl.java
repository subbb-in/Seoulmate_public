package com.ssafy.sm.domain.user.service;

import com.ssafy.sm.common.util.FileStore;
import com.ssafy.sm.domain.user.entity.User;
import com.ssafy.sm.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.sm.domain.user.dto.UserRequest;
import com.ssafy.sm.domain.user.dto.UserResponse;
import com.ssafy.sm.domain.user.exception.DuplicateIdException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
    private final FileStore fileStore;

	public UserServiceImpl(UserRepository userRepository, FileStore fileStore) {
        this.userRepository = userRepository;
        this.fileStore = fileStore;
	}

	// 회원가입
	@Override
	@Transactional
	public void createUser(UserRequest.Join joinDto) {
		// 1. 이메일 중복 검사
		userRepository.findByEmailAndIsDeletedFalse(joinDto.getEmail())
				.ifPresent(u -> {throw new DuplicateIdException("이미 존재하는 이메일입니다."); });
		// 2. Entity 변환 및 저장
		User user = User.builder()
				.email(joinDto.getEmail())
				.pw(joinDto.getPw())
				.nickname(joinDto.getNickname())
				.birth(joinDto.getBirth() != null ?
						joinDto.getBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : null)
				.languageCode(joinDto.getLanguageCode())
				.createdAt(LocalDateTime.now())
				.isDeleted(false)
				.isAdmin(false)
				.build();

		userRepository.save(user);
	}

	// 로그인
	@Override
	public UserResponse login(UserRequest.Login loginDto) {
		// 1. 비밀번호 조회
		User user = userRepository.findByEmailAndIsDeletedFalse(loginDto.getEmail())
				.orElse(null);

		// 2. 비밀번호 일치 확인 (단순 문자열 비교, 추후 암호화 매칭으로 변경)
		if (user != null && user.getPw().equals(loginDto.getPw())) {
			return convertToResponse(user);
		}
		return null; // 로그인 실패
	}

	// 회원 조회
	@Override
	public UserResponse getUser(Long userNo) {
		return userRepository.findByUserNoAndIsDeletedFalse(userNo)
				.map(this::convertToResponse)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
	}

	// 회원 수정 //
	// 파일 삭제 로직 추가(Issue #44)
	@Override
	@Transactional
	public void updateUser(Long userNo, UserRequest.Update updateDto, MultipartFile file) throws IOException {
		User user = userRepository.findByUserNoAndIsDeletedFalse(userNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		String storedFileName = user.getProfileImg();

		// 새 프로필 사진 처리
		if (file != null && !file.isEmpty()) {
			if(storedFileName != null) {
				fileStore.deleteFile(storedFileName);
			}
			FileStore.UploadFile uploadFile = fileStore.storeFile(file);
			storedFileName = uploadFile.getStoredName();
		}

		// Dirty Checking(userRepository.save() 호출 안해도 됨)
		user.updateUserInfo(
				updateDto.getPw(),
				updateDto.getNickname(),
				updateDto.getBirth() != null ? updateDto.getBirth().toString() : null,
				updateDto.getLanguageCode(),
				storedFileName
		);
	}

	// 회원 삭제 //
    @Override
	@Transactional
    public void deleteUser(Long userNo, UserRequest.Delete deleteDto) {
        // DB에 저장된 비밀번호 조회
        User user = userRepository.findByUserNoAndIsDeletedFalse(userNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 비밀번호 일치 여부 확인
        if (!user.getPw().equals(deleteDto.getPw())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 회원 탈퇴 시 프로필 사진도 삭제
        if (user.getProfileImg() != null) {
			fileStore.deleteFile(user.getProfileImg());
		}

        //탈퇴 처리(Soft Delete)
		user.deleteUser();
    }

	// Entity -> DTO 변환 로직(추후 MapStruct 등으로 고도화 가능)
	private UserResponse convertToResponse(User user) {
		return UserResponse.builder()
				.userNo(user.getUserNo())
				.email(user.getEmail())
				.nickname(user.getNickname())
				.birth(user.getBirth() != null ? LocalDate.parse(user.getBirth()) : null)
				.languageCode(user.getLanguageCode())
				.createdAt(user.getCreatedAt())
				.isAdmin(user.getIsAdmin() != null && user.getIsAdmin())
				.profileImg(user.getProfileImg())
				.build();
	}

	// Bridge Methods
	@Override
	@Transactional
	public void updateUserInfo(Long userNo, UserRequest.Update updateDto) {
		try {
			updateUser(userNo, updateDto, null);
		} catch (IOException e) {
			throw new RuntimeException("회원 정보 수정 중 오류가 발생했습니다.");
		}
	}

	@Override
	@Transactional
	public void updateUserImage(Long userNo, MultipartFile file) {
		try {
			updateUser(userNo, new UserRequest.Update(), file);
		} catch (IOException e) {
			throw new RuntimeException("프로필 이미지 수정 중 오류가 발생했습니다.");
		}
	}

//	 프로필 사진 수정 //
//	@Override
//	@Transactional
//	public void updateUserImage(Long userNo, MultipartFile file) {
//		// 1. 파일이 비어있는지 체크
//		if (file == null || file.isEmpty()) {
//			throw new IllegalArgumentException("업로드할 파일이 없습니다.");
//		}
//
//		try {
//			// 2. 기존 프로필 사진 삭제를 위해 유저 정보 조회
//			UserResponse oldUser = userMapper.findByUserNo(userNo);
//
//			// 주의: 기존 코드에서는 Mapper XML에서 profile_img를 조회 안 하고 있었음. 수정 필수!
//			if (oldUser.getProfileImg() != null) {
//				fileStore.deleteFile(oldUser.getProfileImg());
//			}
//
//			// 3. 새 파일 저장 (Common FileStore 이용)
//			FileStore.UploadFile uploadFile = fileStore.storeFile(file);
//			String storedFileName = uploadFile.getStoredName();
//
//			// 4. DB에 저장된 파일명만 업데이트 (전용 메서드 호출)
//			userMapper.updateProfileImage(userNo, storedFileName);
//
//		} catch (IOException e) {
//			throw new RuntimeException("프로필 사진 업로드 중 오류가 발생했습니다.", e);
//		}
//	}
//
//	// 회원 정보 수정 : 텍스트 //
//	@Override
//	@Transactional
//	public void updateUserInfo(Long userNo, UserRequest.Update updateDto) {
//		int updatedRows = userMapper.updateUser(userNo, updateDto);
//
//		if(updatedRows == 0) {
//			throw new IllegalStateException("회원 정보 수정에 실패했습니다. userNo=" + userNo);
//		}
//	}
}