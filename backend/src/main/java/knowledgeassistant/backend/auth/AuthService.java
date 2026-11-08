package knowledgeassistant.backend.auth;

import knowledgeassistant.backend.auth.dto.AuthRequest;
import knowledgeassistant.backend.auth.dto.AuthResponse;
import knowledgeassistant.backend.common.exception.EmailAlreadyUsedException;
import knowledgeassistant.backend.user.User;
import knowledgeassistant.backend.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(AuthRequest request){
        String normalizedEmail = normalizeEmail(request.email());

        if(userRepository.existsByEmailIgnoreCase(normalizedEmail)){
            throw new EmailAlreadyUsedException("An account for this email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .email(normalizedEmail)
                .password(hashedPassword)
                .displayName(request.displayName())
                .build();

        try {
            User savedUser = userRepository.save(user);
            return mapToResponse(savedUser);
        }catch (DataIntegrityViolationException e){
            throw new EmailAlreadyUsedException("An account for this email already exists");
        }
    }

    private String normalizeEmail(String email){
        return email.trim().toLowerCase();
    }

    //helper mapper to convert Entity -> DTO
    private AuthResponse mapToResponse(User user){
        return AuthResponse.builder()
                .userId(user.getId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .build();
    }
}
