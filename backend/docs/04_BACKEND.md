# Engineering Knowledge Assistant
## Backend Development Guide

---

## 1. Backend Goal

Build a Spring Boot API that supports:

- Authentication.
- Workspaces.
- Role-based access.
- Document upload.
- Async ingestion.
- Embeddings.
- Semantic search.
- Cited answers.
- Feedback.
- Audit logs.

---

## 2. Package Structure

```text
backend/src/main/java/com/yourname/knowledgeassistant/
├── KnowledgeAssistantApplication.java
├── common/
│   ├── config/
│   ├── exception/
│   ├── dto/
│   └── util/
├── identity/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── workspaces/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── documents/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── ingestion/
│   ├── worker/
│   ├── service/
│   ├── extractor/
│   ├── chunker/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── embeddings/
│   ├── client/
│   ├── service/
│   └── dto/
├── search/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── dto/
├── answers/
│   ├── controller/
│   ├── service/
│   ├── prompt/
│   ├── repository/
│   └── dto/
├── feedback/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── dto/
└── audit/
    ├── service/
    ├── repository/
    └── entity/
```

---

## 3. Layer Responsibilities

| Layer | Responsibility | Should not do |
|---|---|---|
| Controller | Handle HTTP, validate input, call service | Business logic, database access |
| Service | Business rules, orchestration | HTTP details, direct SQL |
| Repository | Database access | Business rules |
| Entity | Map to database table | API response formatting |
| DTO | API input/output | Database logic |

---

## 4. First Feature: Register User

### 4.1 User Story

```text
As a visitor,
I want to register,
so that I can access the application.
```

### 4.2 API

```text
POST /api/v1/auth/register
```

### 4.3 DTOs

```java
public record RegisterRequest(
        String email,
        String password,
        String fullName
) {}
```

```java
public record AuthResponse(
        UUID userId,
        String email,
        String fullName,
        String accessToken
) {}
```

### 4.4 Entity

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

### 4.5 Repository

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
}
```

### 4.6 Service

```java
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                token
        );
    }
}
```

### 4.7 Controller

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

---

## 5. Document Upload Feature

### 5.1 User Story

```text
As an editor,
I want to upload a document,
so that it can be indexed and searched.
```

### 5.2 API

```text
POST /api/v1/workspaces/{workspaceId}/documents
```

### 5.3 Service Logic

```text
1. Check user is authenticated.
2. Check user belongs to workspace.
3. Check user role is ADMIN or EDITOR.
4. Validate file type.
5. Validate file size.
6. Store file.
7. Create document.
8. Create document version.
9. Create ingestion job.
10. Return 202 Accepted.
```

### 5.4 Important Rule

Do not process the document inside the upload request.

Return quickly:

```json
{
  "documentId": "uuid",
  "status": "PROCESSING"
}
```

---

## 6. Ingestion Worker

### 6.1 Job Lifecycle

```text
QUEUED
→ RUNNING
→ COMPLETED
```

or

```text
QUEUED
→ RUNNING
→ FAILED
→ RETRYING
→ COMPLETED / FAILED
```

### 6.2 Worker Steps

```text
1. Pick QUEUED job.
2. Mark job RUNNING.
3. Load document version.
4. Extract text.
5. Clean text.
6. Split into chunks.
7. Generate embeddings.
8. Save chunks.
9. Mark job COMPLETED.
10. Mark document READY.
```

### 6.3 Failure Handling

```text
Catch exception
→ Save error message
→ Increment attempts
→ Retry if attempts < max
→ Mark FAILED if retries exceeded
```

---

## 7. Embedding Service Interface

```java
public interface EmbeddingService {
    float[] embed(String text);
    List<float[]> embedBatch(List<String> texts);
}
```

Implementations:

```text
LocalEmbeddingService
OpenAiEmbeddingService
```

The rest of the application should depend on the interface, not a specific provider.

---

## 8. Search Service

### 8.1 Semantic Search

```text
1. Embed user query.
2. Query pgvector.
3. Filter by workspace_id.
4. Return top K chunks.
```

### 8.2 Hybrid Search

Combine:

```text
Keyword score
+
Vector similarity score
```

Example ranking:

```text
finalScore =
    (0.7 * vectorScore)
  + (0.3 * keywordScore)
```

Start simple. Improve ranking only after basic search works.

---

## 9. Answer Generation

### 9.1 Prompt Structure

```text
You are an internal engineering knowledge assistant.

Answer the user question using only the provided context.

If the context does not contain enough information, say that you cannot
answer confidently.

Context:
[chunk 1]
[chunk 2]
[chunk 3]

Question:
[ user question ]

Answer:
```

### 9.2 Citation Rule

Every answer must store the chunks used.

```text
Answer
├── Source 1: Document A, chunk 12
├── Source 2: Document B, chunk 4
└── Source 3: Document C, chunk 8
```

---

## 10. Error Handling

Create a global exception handler.

Handle:

```text
ValidationException
DuplicateEmailException
WorkspaceAccessDeniedException
ResourceNotFoundException
FileStorageException
EmbeddingException
LLMException
```

Return consistent JSON:

```json
{
  "timestamp": "2026-10-28T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "File type is not supported",
  "path": "/api/v1/workspaces/123/documents"
}
```

---

## 11. Backend Build Order

```text
[ ] Project setup
[ ] Docker Compose database
[ ] Flyway migration
[ ] User entity
[ ] Auth service
[ ] Auth controller
[ ] JWT filter
[ ] Workspace entity
[ ] Workspace service
[ ] Workspace controller
[ ] Role-based access
[ ] Document entity
[ ] Document upload
[ ] Ingestion job
[ ] Background worker
[ ] Text extraction
[ ] Chunking
[ ] Embedding client
[ ] pgvector search
[ ] Question endpoint
[ ] Answer generation
[ ] Citations
[ ] Feedback
[ ] Audit logs
[ ] Tests
[ ] Swagger
[ ] Dockerfile
[ ] CI
```
