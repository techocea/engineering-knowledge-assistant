CREATE
EXTENSION IF NOT EXISTS vector;

CREATE TABLE users
(
    id            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    full_name     VARCHAR(255)        NOT NULL,
    created_at    TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE TABLE workspaces
(
    id         UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    name       VARCHAR(255)        NOT NULL,
    slug       VARCHAR(255) UNIQUE NOT NULL,
    owner_id   UUID                NOT NULL REFERENCES users (id),
    created_at TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE TABLE workspace_members
(
    id           UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    workspace_id UUID        NOT NULL REFERENCES workspaces (id) ON DELETE CASCADE,
    user_id      UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role         VARCHAR(50) NOT NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT now(),
    UNIQUE (workspace_id, user_id)
);