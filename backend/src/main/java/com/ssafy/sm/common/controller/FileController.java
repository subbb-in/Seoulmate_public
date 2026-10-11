package com.ssafy.sm.common.controller;

import com.ssafy.sm.common.util.FileStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileStore fileStore;
    public FileController(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    // 파일 다운로드 API
    // GET /api/files/download/{filename}
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws MalformedURLException {
        // 실제 파일 경로 가져오기
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(filename));
        // 파일명 한글 깨짐 방지 인코딩
        String encodedUploadFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        // Content-Disposition 헤더를 attachment로 설정
        // attachment(첨부파일)라고 명시하면 브라우저는 즉시 다운로드 대화 상자를 띄움
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
    }
}
