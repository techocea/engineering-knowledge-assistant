# Engineering Knowledge Assistant
## Requirements Document

---

## 1. Product Overview

### 1.1 Product Name

Engineering Knowledge Assistant

### 1.2 Product Type

Multi-tenant B2B SaaS-style internal knowledge assistant.

### 1.3 Problem

Engineering teams store knowledge across PDFs, Markdown documents, runbooks, architecture notes, meeting notes, and internal wikis. Finding the right information is slow, especially for new team members.

### 1.4 Solution

Teams upload documents to a private workspace. The system processes the documents, creates embeddings, and allows users to ask natural-language questions. Answers include citations to the exact source document and chunk.

### 1.5 MVP Promise

> Ask an engineering question and receive a cited answer from your team’s documentation.

---

## 2. Users and Roles

### 2.1 User Types

| Role | Permissions |
|---|---|
| Visitor | Register and log in |
| Workspace Admin | Manage workspace, members, documents, jobs, and settings |
| Editor | Upload, update, delete, and reindex documents |
| Member | Search documents and ask questions |
| System | Process documents and generate embeddings |

### 2.2 Role Rules

```text
ADMIN:
- Can do everything inside the workspace.
- Can manage members.
- Can delete workspace.
- Can view audit logs.
- Can retry ingestion jobs.

EDITOR:
- Can upload documents.
- Can delete documents.
- Can reindex documents.
- Can search and ask questions.
- Cannot manage members.

MEMBER:
- Can search documents.
- Can ask questions.
- Can give feedback.
- Cannot upload or delete documents.
```

---

## 3. Functional Requirements

### 3.1 Authentication

```text
FR-01: A visitor can register with email, password, and full name.
FR-02: A user can log in with valid credentials.
FR-03: Passwords must be hashed.
FR-04: A user receives a JWT access token after login.
FR-05: A user can refresh their session.
FR-06: A user can log out.
FR-07: A user can view their own profile.
```

### 3.2 Workspaces

```text
FR-08: An authenticated user can create a workspace.
FR-09: The workspace creator becomes ADMIN.
FR-10: Each workspace must have a unique slug.
FR-11: A user can belong to multiple workspaces.
FR-12: A user can view only workspaces they belong to.
FR-13: An admin can invite a user to a workspace.
FR-14: An admin can assign roles.
FR-15: An admin can remove a member.
```

### 3.3 Documents

```text
FR-16: An EDITOR or ADMIN can upload a document.
FR-17: Supported formats: PDF, Markdown, plain text.
FR-18: Maximum file size: 20 MB.
FR-19: Unsupported file types must be rejected.
FR-20: Each upload creates a document version.
FR-21: A document can have multiple versions.
FR-22: A user can view document metadata.
FR-23: A user can view document processing status.
FR-24: An EDITOR or ADMIN can delete a document.
FR-25: Deleting a document must remove or deactivate its searchable chunks.
```

### 3.4 Ingestion

```text
FR-26: Document ingestion must run asynchronously.
FR-27: The system must extract text from uploaded documents.
FR-28: The system must clean extracted text.
FR-29: The system must split text into chunks.
FR-30: The system must generate embeddings for each chunk.
FR-31: The system must store chunks and embeddings in PostgreSQL pgvector.
FR-32: The document status must become READY after successful ingestion.
FR-33: The document status must become FAILED if ingestion fails.
FR-34: Failed ingestion jobs must store an error message.
FR-35: Failed ingestion jobs can be retried.
FR-36: Ingestion must be workspace-scoped.
```

### 3.5 Search

```text
FR-37: A user can ask a question in natural language.
FR-38: The system must embed the question.
FR-39: The system must retrieve the most similar chunks.
FR-40: Search results must be filtered by workspace.
FR-41: Search results must include document title, excerpt, and similarity score.
FR-42: Search results must be paginated.
FR-43: The system should support keyword search alongside vector search.
```

### 3.6 Answers

```text
FR-44: The system must generate an answer using retrieved chunks.
FR-45: The answer must include citations.
FR-46: Each citation must link to a document and chunk.
FR-47: The system must save questions, answers, and sources.
FR-48: If no relevant context is found, the system must clearly state that it cannot answer confidently.
```

### 3.9 Feedback

```text
FR-49: A user can mark an answer as HELPFUL or UNHELPFUL.
FR-50: A user can optionally add feedback comments.
FR-51: A user can update feedback.
FR-52: Feedback must be linked to the answer and user.
```

### 3.10 Administration

```text
FR-53: Admin can view ingestion jobs.
FR-54: Admin can view failed job errors.
FR-55: Admin can retry failed jobs.
FR-56: Admin can reindex documents.
FR-57: Admin can view audit logs.
```

---

## 4. Non-Functional Requirements

| ID | Requirement |
|---|---|
| NFR-01 | Passwords must be hashed using BCrypt. |
| NFR-02 | All workspace data must be isolated by workspace ID. |
| NFR-03 | API authentication must use JWT. |
| NFR-04 | Sensitive actions must be recorded in audit logs. |
| NFR-05 | File uploads must be validated. |
| NFR-06 | Large documents must be processed asynchronously. |
| NFR-07 | API responses must use consistent error format. |
| NFR-08 | Database schema changes must use Flyway migrations. |
| NFR-09 | Core flows must have automated tests. |
| NFR-10 | The application must run using Docker Compose. |
| NFR-11 | API must be documented using OpenAPI/Swagger. |
| NFR-12 | Logs must not contain passwords, tokens, or API keys. |

---

## 5. Out of Scope

The following are explicitly excluded from MVP:

```text
- Slack integration
- GitHub integration
- Notion integration
- Google Drive integration
- Real-time collaborative editing
- Mobile application
- Payments
- Subscriptions
- OCR for scanned PDFs
- Multi-language support
- Public document sharing
- Enterprise SSO
- Microservices
- Multiple LLM provider selection
- Autonomous AI agents
- Email notifications
```

---

## 6. Acceptance Criteria for MVP

The MVP is accepted when:

```text
1. A user can register and log in.
2. A user can create a workspace.
3. An admin can invite another user.
4. Roles restrict access correctly.
5. An editor can upload a Markdown file.
6. An editor can upload a PDF file.
7. The document is processed asynchronously.
8. Chunks and embeddings are stored.
9. A member can ask a question.
10. Search returns relevant workspace-scoped chunks.
11. The answer includes citations.
12. Feedback can be submitted.
13. A failed ingestion job can be retried.
14. A user cannot access another workspace.
15. The application runs with Docker Compose.
```
