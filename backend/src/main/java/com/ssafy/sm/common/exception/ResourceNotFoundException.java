package com.ssafy.sm.common.exception;

/**
 * 요청한 리소스(데이터)를 찾을 수 없을 때 발생하는 사용자 정의 예외 클래스입니다.
 * GlobalExceptionHandler가 이 예외를 감지하여 404 응답을 생성합니다.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(String errorCode, String message) {
        super(message); // RuntimeException의 생성자를 호출하여 메시지를 설정합니다.
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
