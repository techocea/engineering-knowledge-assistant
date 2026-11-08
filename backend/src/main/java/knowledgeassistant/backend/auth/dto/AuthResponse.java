package knowledgeassistant.backend.auth.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthResponse(
        UUID userId,
        String email,
        String displayName
) {
}
