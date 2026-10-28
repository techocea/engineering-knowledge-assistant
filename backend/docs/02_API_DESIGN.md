# Engineering Knowledge Assistant
## API Design Document

Base URL:

```text
/api/v1
```

---

## 1. Authentication APIs

### Register

```text
POST /api/v1/auth/register
```

Request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123",
  "fullName": "Brightly Dunsford"
}
```

Response:

```json
{
  "userId": "uuid",
  "email": "user@example.com",
  "fullName": "Brightly Dunsford",
  "accessToken": "jwt-token"
}
```

### Login

```text
POST /api/v1/auth/login
```

Request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123"
}
```

Response:

```json
{
  "userId": "uuid",
  "email": "user@example.com",
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token"
}
```

### Refresh Token

```text
POST /api/v1/auth/refresh
```

Request:

```json
{
  "refreshToken": "refresh-token"
}
```

Response:

```json
{
  "accessToken": "new-jwt-token",
  "refreshToken": "new-refresh-token"
}
```

### Current User

```text
GET /api/v1/auth/me
```

Response:

```json
{
  "userId": "uuid",
  "email": "user@example.com",
  "fullName": "Brightly Dunsford"
}
```

---

## 2. Workspace APIs

### Create Workspace

```text
POST /api/v1/workspaces
```

Request:

```json
{
  "name": "Platform Team"
}
```

Response:

```json
{
  "workspaceId": "uuid",
  "name": "Platform Team",
  "slug": "platform-team",
  "role": "ADMIN"
}
```

### List My Workspaces

```text
GET /api/v1/workspaces
```

### Get Workspace

```text
GET /api/v1/workspaces/{workspaceId}
```

### Add Member

```text
POST /api/v1/workspaces/{workspaceId}/members
```

Request:

```json
{
  "email": "member@example.com",
  "role": "MEMBER"
}
```

### List Members

```text
GET /api/v1/workspaces/{workspaceId}/members
```

### Remove Member

```text
DELETE /api/v1/workspaces/{workspaceId}/members/{userId}
```

---

## 3. Document APIs

### Upload Document

```text
POST /api/v1/workspaces/{workspaceId}/documents
Content-Type: multipart/form-data
```

Form fields:

```text
file
title
```

Response:

```json
{
  "documentId": "uuid",
  "title": "Deployment Runbook",
  "status": "PROCESSING",
  "versionNumber": 1
}
```

### List Documents

```text
GET /api/v1/workspaces/{workspaceId}/documents
```

### Get Document

```text
GET /api/v1/workspaces/{workspaceId}/documents/{documentId}
```

### Get Document Status

```text
GET /api/v1/workspaces/{workspaceId}/documents/{documentId}/status
```

### Delete Document

```text
DELETE /api/v1/workspaces/{workspaceId}/documents/{documentId}
```

### Reindex Document

```text
POST /api/v1/workspaces/{workspaceId}/documents/{documentId}/reindex
```

---

## 4. Search APIs

### Semantic Search

```text
POST /api/v1/workspaces/{workspaceId}/search
```

Request:

```json
{
  "query": "How do we deploy the backend?",
  "topK": 5
}
```

Response:

```json
{
  "results": [
    {
      "chunkId": "uuid",
      "documentId": "uuid",
      "documentTitle": "Deployment Runbook",
      "pageNumber": 4,
      "content": "The backend is deployed using Docker Compose...",
      "similarityScore": 0.91
    }
  ]
}
```

---

## 5. Question and Answer APIs

### Ask Question

```text
POST /api/v1/workspaces/{workspaceId}/questions
```

Request:

```json
{
  "question": "How do we deploy the backend?",
  "topK": 5
}
```

Response:

```json
{
  "questionId": "uuid",
  "answerId": "uuid",
  "answer": "The backend is deployed using Docker Compose...",
  "sources": [
    {
      "documentId": "uuid",
      "documentTitle": "Deployment Runbook",
      "pageNumber": 4,
      "similarityScore": 0.91,
      "excerpt": "The backend is deployed using Docker Compose..."
    }
  ]
}
```

### Get Question and Answer

```text
GET /api/v1/workspaces/{workspaceId}/questions/{questionId}
```

### Submit Feedback

```text
POST /api/v1/answers/{answerId}/feedback
```

Request:

```json
{
  "rating": "HELPFUL",
  "comment": "This answered my deployment question."
}
```

---

## 6. Admin APIs

### List Ingestion Jobs

```text
GET /api/v1/workspaces/{workspaceId}/ingestion-jobs
```

### Retry Failed Job

```text
POST /api/v1/workspaces/{workspaceId}/ingestion-jobs/{jobId}/retry
```

### Audit Logs

```text
GET /api/v1/workspaces/{workspaceId}/audit-logs
```

---

## 7. Error Format

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

## 8. HTTP Status Rules

| Situation | Status |
|---|---:|
| Successful creation | 201 |
| Successful read/update | 200 |
| Async processing accepted | 202 |
| Invalid input | 400 |
| Missing/invalid token | 401 |
| Insufficient permission | 403 |
| Resource not found | 404 |
| Conflict, such as duplicate email | 409 |
| File too large | 413 |
| AI provider unavailable | 503 |
