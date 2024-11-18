package me.haneul.config.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponseDTO<T> {
    private boolean success;
    private T response;

    @Builder
    private ApiResponseDTO(boolean success, T response) {
        this.success = success;
        this.response = response;
    }
}
