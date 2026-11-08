package knowledgeassistant.backend.auth;

import jakarta.validation.Valid;
import knowledgeassistant.backend.auth.dto.AuthRequest;
import knowledgeassistant.backend.auth.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request){
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .ok()
                .location(URI.create("/api/v1/users/" + response.userId()))
                .body(response);
    }
}
