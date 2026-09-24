-- =============================================
-- Script de creación de tablas
-- Gestión de Usuarios - Arquitectura Hexagonal
-- Compatible con PostgreSQL (Render)
-- =============================================

CREATE TABLE IF NOT EXISTS users (
                                     id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'MEMBER', 'REVIEWER')),
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'BLOCKED')),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (email)
    );

-- Usuario administrador inicial (password: Admin1234!)
-- Reemplaza el hash con uno real generado con BCrypt antes de producción
INSERT INTO users (id, name, email, password, role, status)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'Administrador',
           'admin@example.com',
           '$2a$12$placeholderHashReplaceWithRealBCryptHash',
           'ADMIN',
           'ACTIVE'
       ) ON CONFLICT (id) DO NOTHING;