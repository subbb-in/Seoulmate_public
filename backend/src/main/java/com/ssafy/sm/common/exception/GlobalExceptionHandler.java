package com.ssafy.sm.common.exception;

import com.ssafy.sm.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 파일 크기 초과 예외 처리
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("파일 용량이 너무 큽니다. 제한 용량을 확인해주세요."));
    }

    // 2. 파일 입출력 예외
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(IOException exc) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("파일 처리 중 오류가 발생했습니다."));
    }

    // 3. 잘못된 파일 형식(IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleArgumentException(IllegalArgumentException exc) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(exc.getMessage()));
    }

    /**
     * [데이터 충돌: 409 Conflict - DataIntegrityViolation]
     * DB의 Unique Constraint(고유 제약 조건)을 위반했을 때 발생
     */
    // 4. 데이터 제약 조건 위반(DataIntegrityViolationException)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Database integrity violation (Possible Conflict 409): {}", ex.getMessage());

        String detailedMessage = "요청된 데이터가 이미 존재하거나 유효성 제약 조건을 위반했습니다.";

        ApiResponse<?> response = ApiResponse.error(detailedMessage);

        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(response);
    }

    /**
     * [데이터 없음: 404 Not Found]
     * Service에서 throw new ResourceNotFoundException(...) 할 때
     */
    // 5. 데이터 없음(ResourceNotFoundException)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        // errorCode는 로그로만 남기고, 클라이언트에는 메시지만 전달
        ApiResponse<?> response = ApiResponse.error(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(response);
    }

    // 6. 그 외 모든 예외(위에서 거르지 못한 오류들)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exc) {
        exc.printStackTrace(); // 서버 로그는 남기고
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
    }
}
