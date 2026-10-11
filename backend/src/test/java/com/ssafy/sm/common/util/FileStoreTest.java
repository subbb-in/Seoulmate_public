package com.ssafy.sm.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
// 테스트용 임시 폴더 지정
@TestPropertySource(properties = "file.upload-dir=" + "${java.io.tmpdir}/test_uploads/")
class FileStoreTest {

    @Autowired
    private FileStore fileStore;

    @Test
    @DisplayName("1. [보안] 허용되지 않은 확장자는 예외가 발생해야 한다")
    void storeFile_SecurityCheck() {
        // given: 실행 파일(.exe) 준비
        MockMultipartFile badFile = new MockMultipartFile(
                "file",
                "virus.exe",
                "application/x-msdownload",
                "malicious code".getBytes()
        );

        // when & then: 저장 시도 시 예외 발생 확인
        assertThatThrownBy(() -> fileStore.storeFile(badFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 파일 형식");
    }

    @Test
    @DisplayName("2. [기능] 파일 저장 및 삭제 테스트")
    void deleteFile_Check() throws IOException {
        // given: 정상 이미지 파일 준비
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "image data".getBytes()
        );

        // 1. 파일 저장
        FileStore.UploadFile result = fileStore.storeFile(imageFile);
        String storedName = result.getStoredName();

        // 저장 확인
        File savedFile = new File(fileStore.getFullPath(storedName));
        assertThat(savedFile.exists()).isTrue();

        // 2. 파일 삭제
        fileStore.deleteFile(storedName);

        // 3. 삭제 확인 (파일이 없어야 함)
        assertThat(savedFile.exists()).isFalse();
    }
}