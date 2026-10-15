-- Creació de les taules de PadelGest

CREATE TABLE usuaris (
    id_usuari BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    cognoms VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    contrasenya_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(10) NOT NULL DEFAULT 'USER',
    actiu BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_usuaris_rol CHECK (rol IN ('ADMIN', 'USER'))
);

CREATE TABLE pistes (
    id_pista BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    tipus VARCHAR(10) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_pistes_tipus CHECK (tipus IN ('INTERIOR', 'EXTERIOR'))
);

CREATE TABLE reserves (
    id_reserva BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuari BIGINT NOT NULL,
    id_pista BIGINT NOT NULL,
    data DATE NOT NULL,
    hora_inici TIME NOT NULL,
    hora_fi TIME NOT NULL,
    estat VARCHAR(10) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT fk_reserva_usuari FOREIGN KEY (id_usuari) REFERENCES usuaris(id_usuari),
    CONSTRAINT fk_reserva_pista FOREIGN KEY (id_pista) REFERENCES pistes(id_pista),
    CONSTRAINT chk_reserva_horari CHECK (hora_fi > hora_inici),
    CONSTRAINT chk_reserva_estat CHECK (estat IN ('ACTIVA', 'CANCELLADA'))
);