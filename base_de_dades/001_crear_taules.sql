-- Creació de les taules de PadelGest

CREATE TABLE usuarios (
    id_usuario BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(10) NOT NULL DEFAULT 'USER',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_usuarios_rol CHECK (rol IN ('ADMIN', 'USER'))
);

CREATE TABLE pistas (
    id_pista BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(10) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_pistas_tipo CHECK (tipo IN ('INTERIOR', 'EXTERIOR'))
);

CREATE TABLE reservas (
    id_reserva BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_pista BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_reserva_pista FOREIGN KEY (id_pista) REFERENCES pistas(id_pista),
    CONSTRAINT chk_reserva_horario CHECK (hora_fin > hora_inicio),
    CONSTRAINT chk_reserva_estado CHECK (estado IN ('ACTIVA', 'CANCELADA'))
);
