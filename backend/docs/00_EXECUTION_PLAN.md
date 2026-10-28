# Engineering Knowledge Assistant
## Master Execution Plan

**Purpose:** This is the main execution document. Use it as your day-to-day roadmap.

**Rule:** Do not move to the next phase until the current phase’s definition of done is complete.

---

## 1. Project Summary

Build a multi-tenant internal knowledge assistant where engineering teams:

1. Create a workspace.
2. Invite team members.
3. Upload documents.
4. Process documents into searchable chunks.
5. Generate embeddings.
6. Ask natural-language questions.
7. Receive answers with citations.
8. Provide feedback on answers.

### MVP Promise

> Upload your team documents, ask engineering questions, and receive cited answers from your own knowledge base.

### Core Workflow

```text
Register / Login
→ Create workspace
→ Invite members
→ Upload document
→ Ingest document asynchronously
→ Chunk document
→ Generate embeddings
→ Store vectors in PostgreSQL pgvector
→ Ask question
→ Retrieve relevant chunks
→ Generate answer
→ Show citations
→ Collect feedback
```

---

## 2. Recommended Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- pgvector
- Flyway
- Lombok
- MapStruct
- JUnit 5
- Mockito
- Testcontainers
- Springdoc OpenAPI
- Docker

### Frontend

- React 18
- TypeScript
- Vite
- React Router
- Tailwind CSS
- React Query
- Axios or Fetch
- Zod

### AI / Embeddings

Use a local-first setup:

- Local embeddings: `sentence-transformers` through a Python FastAPI service.
- Local LLM: Ollama.
- Vector database: PostgreSQL + pgvector.

Optional later:

- OpenAI-compatible embedding provider.
- OpenAI-compatible LLM provider.

---

## 3. Repository Structure

```text
engineering-knowledge-assistant/
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── .dockerignore
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   ├── Dockerfile
│   └── .dockerignore
├── ai-service/
│   ├── app/
│   ├── requirements.txt
│   └── Dockerfile
├── docs/
│   ├── 00_EXECUTION_PLAN.md
│   ├── 01_REQUIREMENTS.md
│   ├── 02_ARCHITECTURE.md
│   ├── 03_DATABASE_DESIGN.md
│   ├── 04_API_DESIGN.md
│   ├── 05_BACKEND_DEVELOPMENT_GUIDE.md
│   ├── 06_FRONTEND_DEVELOPMENT_GUIDE.md
│   ├── 07_AI_EMBEDDINGS_GUIDE.md
│   ├── 08_TESTING_PLAN.md
│   ├── 09_DOCKER_DEPLOYMENT.md
│   └── 10_LEARNING_CHECKLIST.md
├── .github/
│   └── workflows/
│       └── ci.yml
├── .gitignore
├── README.md
└── docker-compose.yml
```

---

## 4. Development Phases

### Phase 0: Project Setup

**Goal:** Create a clean, runnable foundation.

Tasks:

```text
[ ] Create GitHub repository
[ ] Create root folders: backend, frontend, ai-service, docs
[ ] Initialize Git at root
[ ] Create README.md
[ ] Create .gitignore
[ ] Create docker-compose.yml
[ ] Create backend Spring Boot project
[ ] Create frontend React project
[ ] Create ai-service Python project
[ ] Run PostgreSQL + pgvector in Docker
[ ] Verify backend connects to PostgreSQL
[ ] Verify frontend runs
[ ] Verify ai-service runs
[ ] Make first commit
```

Definition of done:

```text
All three applications start locally.
PostgreSQL with pgvector is running.
Git repository is initialized at root.
```

---

### Phase 1: Authentication and Workspaces

**Goal:** Build identity, access control, and multi-tenancy.

Tasks:

```text
[ ] Create users table migration
[ ] Create workspaces table migration
[ ] Create workspace_members table migration
[ ] Create User entity
[ ] Create Workspace entity
[ ] Create WorkspaceMember entity
[ ] Create UserRepository
[ ] Create WorkspaceRepository
[ ] Create WorkspaceMemberRepository
[ ] Implement password hashing
[ ] Implement register endpoint
[ ] Implement login endpoint
[ ] Implement JWT generation
[ ] Implement JWT authentication filter
[ ] Implement refresh-token endpoint
[ ] Implement current-user endpoint
[ ] Implement workspace creation
[ ] Implement workspace membership
[ ] Implement role-based authorization
[ ] Create React register page
[ ] Create React login page
[ ] Create dashboard shell
[ ] Create workspace page
[ ] Write auth tests
[ ] Write workspace tests
```

Definition of done:

```text
A user can register, log in, create a workspace, invite a member,
and cannot access another user’s workspace.
```

---

### Phase 2: Document Upload and Ingestion

**Goal:** Accept documents and turn them into searchable chunks.

Tasks:

```text
[ ] Create documents table migration
[ ] Create document_versions table migration
[ ] Create document_chunks table migration
[ ] Create ingestion_jobs table migration
[ ] Create Document entity
[ ] Create DocumentVersion entity
[ ] Create DocumentChunk entity
[ ] Create IngestionJob entity
[ ] Implement file-upload endpoint
[ ] Validate file type and size
[ ] Store uploaded file
[ ] Create document and document version
[ ] Create ingestion job
[ ] Implement background worker
[ ] Implement Markdown text extraction
[ ] Implement PDF text extraction
[ ] Implement text cleaning
[ ] Implement chunking
[ ] Call embedding service
[ ] Store chunks and embeddings
[ ] Update ingestion-job status
[ ] Handle failed jobs
[ ] Implement retry
[ ] Create React upload page
[ ] Create document list page
[ ] Create document status page
```

Definition of done:

```text
An uploaded Markdown or PDF document becomes chunks and embeddings
stored in PostgreSQL pgvector.
```

---

### Phase 3: Semantic Search and Answers

**Goal:** Let users ask questions and receive cited answers.

Tasks:

```text
[ ] Create questions table migration
[ ] Create answers table migration
[ ] Create answer_sources table migration
[ ] Create feedback table migration
[ ] Create Question entity
[ ] Create Answer entity
[ ] Create AnswerSource entity
[ ] Create Feedback entity
[ ] Implement question endpoint
[ ] Embed user question
[ ] Implement pgvector similarity search
[ ] Filter results by workspace
[ ] Implement keyword search
[ ] Combine keyword and vector results
[ ] Build grounded prompt
[ ] Call local LLM through Ollama
[ ] Save answer and sources
[ ] Return citations
[ ] Create React search page
[ ] Display answer
[ ] Display source citations
[ ] Implement feedback endpoint
[ ] Add feedback UI
```

Definition of done:

```text
A user asks a question and receives an answer generated only from
relevant chunks in their workspace, with visible citations.
```

---

### Phase 4: Hardening and Deployment

**Goal:** Make the project production-quality and portfolio-ready.

Tasks:

```text
[ ] Add global exception handling
[ ] Add request validation
[ ] Add audit logging
[ ] Add search history
[ ] Add ingestion-job dashboard
[ ] Add reindex endpoint
[ ] Add health checks
[ ] Add structured logging
[ ] Add correlation IDs
[ ] Write unit tests
[ ] Write integration tests
[ ] Add Testcontainers
[ ] Add Swagger documentation
[ ] Add GitHub Actions CI
[ ] Add production Dockerfile for frontend
[ ] Add production Docker Compose
[ ] Deploy backend
[ ] Deploy frontend
[ ] Deploy database
[ ] Write README
[ ] Write architecture documentation
[ ] Record demo video
```

Definition of done:

```text
The application is deployed, documented, tested, and a new user can
complete the full workflow.
```

---

## 5. Build Order Rule

Always build vertical slices.

Do not build:

```text
All entities → all repositories → all services → all controllers → all UI
```

Build:

```text
One user action → database → repository → service → controller → UI → test
```

### First vertical slice

```text
Register → Login → Get current user
```

### Second vertical slice

```text
Create workspace → Invite member → List members
```

### Third vertical slice

```text
Upload Markdown → Create ingestion job → See document status
```

### Fourth vertical slice

```text
Ingest Markdown → Create chunks → Generate embeddings → Store in pgvector
```

### Fifth vertical slice

```text
Ask question → Retrieve chunks → Show sources
```

### Sixth vertical slice

```text
Generate answer → Show citations → Collect feedback
```

---

## 6. Daily Engineering Routine

Use this routine for every feature.

```text
1. Choose one user story.
2. Write acceptance criteria.
3. Identify input and output.
4. Identify database changes.
5. Define API contract.
6. Write test cases.
7. Implement the smallest working version.
8. Test manually.
9. Refactor.
10. Commit.
11. Update documentation.
```

### Example

```text
Feature: User can register.

User story:
As a visitor, I want to create an account.

Input:
email, password, fullName

Output:
userId, email, fullName, accessToken

Database:
users table

API:
POST /api/v1/auth/register

Business rules:
- Email must be unique.
- Password must be hashed.
- User must be saved.
- JWT must be returned.

Failure cases:
- Email already exists.
- Invalid email.
- Weak password.
- Missing fields.
```

---

## 7. Git Workflow

Use one repository at the root.

```bash
cd engineering-knowledge-assistant
git status
git add .
git commit -m "feat(auth): add register endpoint"
git push origin main
```

Use branches for features:

```bash
git checkout -b feature/document-upload
```

Commit format:

```text
type(scope): short description
```

Examples:

```text
feat(auth): add JWT login endpoint
feat(documents): add PDF upload endpoint
feat(search): add pgvector similarity search
fix(ingestion): retry failed embedding jobs
chore(docker): add postgres healthcheck
docs(readme): add local setup instructions
```

---

## 8. Definition of Complete MVP

The project is complete when:

```text
[ ] A user can register and log in.
[ ] A user can create a workspace.
[ ] An admin can invite members.
[ ] Roles control access.
[ ] An editor can upload Markdown and PDF files.
[ ] Documents are processed asynchronously.
[ ] Documents are chunked.
[ ] Embeddings are generated locally.
[ ] Embeddings are stored in pgvector.
[ ] Users can ask questions.
[ ] Search returns workspace-scoped results.
[ ] Answers are generated from retrieved chunks.
[ ] Every answer has citations.
[ ] Users can give feedback.
[ ] Admins can view ingestion jobs.
[ ] Admins can retry failed jobs.
[ ] The system has tests.
[ ] The system has CI.
[ ] The system is Dockerized.
[ ] The system is deployed.
[ ] The README explains setup and architecture.
```

---

## 9. What Not To Do

Do not:

- Build all database tables before building one feature.
- Add OpenAI before local embeddings work.
- Add Slack, GitHub, or Notion integrations in MVP.
- Build a mobile app.
- Add payments.
- Use microservices.
- Build an admin dashboard before the core user flow works.
- Copy large generated code without understanding it.
- Skip tests until the end.
- Start another project before finishing this one.

---

## 10. Success Metric

You succeed when you can explain and rebuild this workflow without help:

```text
Upload document
→ Extract text
→ Chunk text
→ Embed chunks
→ Store vectors
→ Search by question
→ Retrieve relevant chunks
→ Generate grounded answer
→ Return citations
```

That is the engineering skill this project is meant to teach.
