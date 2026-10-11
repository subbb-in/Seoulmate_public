package com.ssafy.sm.common.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.upload-dir}")
    private String fileDir;

    // [보안] 허용할 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        if(multipartFiles == null || multipartFiles.isEmpty()) {
            return storeFileResult;
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originName = multipartFile.getOriginalFilename();
        if (originName == null) {
            originName = "";
        }

        // 확장자명 검증
        validateExtension(originName);

        String storedName = createStoredName(originName);

        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        multipartFile.transferTo(new File(getFullPath(storedName)));

        return new UploadFile(originName, storedName);
    }

    // 파일 삭제 메서드
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        File file = new File(getFullPath(filename));
        if (file.exists()) {
            file.delete();
        }
    }

    // 확장자 검증 메서드
    private void validateExtension(String originName) {
        String ext = extractExt(originName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. jpg, jpeg, png, gif만 업로드 가능합니다.");
        }
    }


    // 저장용 이름 생성 메서드
    private String createStoredName(String originName) {
        String ext = extractExt(originName);
        String uuid = UUID.randomUUID().toString();

        // ext는 절대 null이 아니므로 empty만 체크
        if (ext.isEmpty()) {
            return uuid;
        }
        return uuid + "." + ext;
    }

    private String extractExt(String originName) {
        int pos = originName.lastIndexOf(".");
        if (pos == -1) return "";
        return originName.substring(pos + 1);
    }

    @Getter
    public static class UploadFile {
        private final String originName; // final 추가
        private final String storedName; // final 추가

        public UploadFile(String originalFilename, String storeFileName) {
            this.originName = originalFilename;
            this.storedName = storeFileName;
        }

    }
}