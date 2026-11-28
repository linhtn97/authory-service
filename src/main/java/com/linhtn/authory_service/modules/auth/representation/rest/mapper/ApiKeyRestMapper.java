package com.linhtn.authory_service.modules.auth.representation.rest.mapper;

import java.util.List;

import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.ApiKeyCreateResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.ApiKeyResponse;

public class ApiKeyRestMapper {

    private ApiKeyRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ApiKeyResponse toResponse(ApiCredentialResult result) {
        if (result == null) {
            return null;
        }
        return ApiKeyResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .clientId(result.getClientId())
                .apiKey(maskApiKey(result.getApiKey()))
                .status(result.getStatus())
                .revoked(result.getRevoked())
                .expiredAt(result.getExpiredAt())
                .createdAt(result.getCreatedAt())
                .build();
    }

    public static List<ApiKeyResponse> toResponseList(List<ApiCredentialResult> results) {
        if (results == null) {
            return List.of();
        }
        return results.stream().map(ApiKeyRestMapper::toResponse).toList();
    }

    public static ApiKeyCreateResponse toCreateResponse(ApiCredentialResult result) {
        if (result == null) {
            return null;
        }
        return ApiKeyCreateResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .clientId(result.getClientId())
                .clientKey(result.getHashedSecret())
                .apiKey(result.getApiKey())
                .status(result.getStatus())
                .expiredAt(result.getExpiredAt())
                .createdAt(result.getCreatedAt())
                .build();
    }

    public static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 10) {
            return "********";
        }
        // e.g. "ak_47d5" + "..." + "59f0"
        return apiKey.substring(0, 7) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
