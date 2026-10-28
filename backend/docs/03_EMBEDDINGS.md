# Engineering Knowledge Assistant
## AI, Embeddings, and RAG Guide

---

## 1. What Is an Embedding?

An embedding is a numerical representation of text.

Example:

```text
"How do we deploy the backend?"
→ [0.012, -0.034, 0.078, ..., 0.021]
```

Similar meanings produce similar vectors.

```text
"How do we deploy the backend?"
"How is the backend deployed?"

These should have close vectors.
```

---

## 2. What Is pgvector?

`pgvector` is a PostgreSQL extension that stores vectors and supports similarity search.

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE document_chunks (
    id UUID PRIMARY KEY,
    content TEXT,
    embedding vector(384)
);
```

Search:

```sql
SELECT id, content,
       embedding <=> :questionVector AS similarity
FROM document_chunks
WHERE workspace_id = :workspaceId
ORDER BY embedding <=> :questionVector
LIMIT 5;
```

`<=>` is cosine distance in pgvector.

---

## 3. Local AI Architecture

Use local models so your application uses only its own data.

```text
React frontend
   ↓
Spring Boot backend
   ↓
Python FastAPI AI service
   ↓
sentence-transformers
   ↓
PostgreSQL pgvector

Spring Boot backend
   ↓
Ollama
   ↓
Local LLM
```

---

## 4. Embedding Service

Create a Python FastAPI service:

```text
ai-service/
├── app/
│   └── main.py
├── requirements.txt
└── Dockerfile
```

### `requirements.txt`

```text
fastapi
uvicorn
sentence-transformers
```

### `app/main.py`

```python
from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer

app = FastAPI()

model = SentenceTransformer("all-MiniLM-L6-v2")

class EmbedRequest(BaseModel):
    texts: list[str]

class EmbedResponse(BaseModel):
    embeddings: list[list[float]]

@app.post("/embed", response_model=EmbedResponse)
def embed(request: EmbedRequest):
    vectors = model.encode(request.texts, normalize_embeddings=True)
    return {"embeddings": vectors.tolist()}
```

Run:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

---

## 5. Spring Boot Embedding Client

```java
@Service
public class LocalEmbeddingService implements EmbeddingService {

    private final RestClient restClient;

    public LocalEmbeddingService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8000")
                .build();
    }

    @Override
    public float[] embed(String text) {
        EmbedRequest request = new EmbedRequest(List.of(text));
        EmbedResponse response = restClient
                .post()
                .uri("/embed")
                .body(request)
                .retrieve()
                .body(EmbedResponse.class);

        return response.embeddings().get(0);
    }
}
```

---

## 6. Chunking Strategy

Do not store an entire document as one embedding.

Chunk by:

- 500–800 characters, or
- 300–500 tokens, or
- Markdown headings/sections.

Add overlap:

```text
Chunk 1: characters 0–800
Chunk 2: characters 700–1500
Chunk 3: characters 1400–2200
```

Overlap helps preserve context across boundaries.

---

## 7. Metadata to Store

For every chunk, store:

```text
workspace_id
document_id
document_version_id
chunk_index
content
page_number
heading
token_count
embedding
created_at
```

Metadata improves filtering and citation quality.

---

## 8. Retrieval Flow

```text
User question
→ Embed question
→ Compare with stored chunk vectors
→ Return top K similar chunks
→ Filter by workspace
→ Optionally filter by document type or date
→ Return results
```

---

## 9. RAG Flow

RAG means **Retrieval-Augmented Generation**.

```text
User question
→ Retrieve relevant chunks
→ Build prompt with chunks
→ Send prompt to LLM
→ LLM answers using only provided context
→ Return answer + citations
```

### Prompt Template

```text
You are an internal engineering knowledge assistant.

Answer the user question using only the provided context.

If the context does not contain enough information, say:
"I cannot answer this confidently from the available documents."

Do not invent information.

Context:
[chunk 1]
[chunk 2]
[chunk 3]

Question:
[user question]

Answer:
```

---

## 10. Ollama Integration

Install Ollama:

```bash
curl -fsSL https://ollama.com/install.sh | sh
ollama pull llama3
ollama run llama3
```

Spring Boot can call:

```text
POST http://localhost:11434/api/generate
```

Example request:

```json
{
  "model": "llama3",
  "prompt": "Answer using only this context...",
  "stream": false
}
```

---

## 11. Local vs External AI

| Option | Data privacy | Cost | Setup | Best for |
|---|---|---|---|---|
| Local sentence-transformers + Ollama | High | Free after setup | Medium | Learning, private data |
| OpenAI embeddings + GPT | Depends on provider terms | Pay per use | Low | Fast MVP |
| Azure OpenAI | Enterprise controls | Pay per use | Medium | Business production |

For your learning goal, start local.

---

## 12. Evaluation

Do not assume search works. Test it.

Create test questions:

```text
Question: How do we deploy the backend?
Expected source: Deployment Runbook

Question: What is the password-reset flow?
Expected source: Authentication Guide

Question: What database do we use?
Expected source: Architecture Guide
```

Measure:

```text
Did the correct chunk appear in top 5?
Did the answer cite the correct document?
Was the answer factually grounded?
```

---

## 13. Common Mistakes

Avoid:

- Embedding an entire PDF as one vector.
- Using different embedding models for documents and questions.
- Ignoring workspace filtering.
- Storing no metadata.
- Using huge chunks.
- Using no chunk overlap.
- Letting the LLM answer without context.
- Not showing citations.
- Not handling embedding failures.
