-- =====================================================================
-- PadelGest: dades de prova
--
-- Omple la base de dades amb usuaris, pistes i reserves per provar
-- 
--
-- Primer ESBORRA totes les dades que hi hagi a les taules.
-- 
--
-- Usuaris (correu / contrasenya):
--   admin@padelgest.cat    / admin1234      ADMIN
--   fernando@padelgest.cat / jugador1234    USER
--   neus@padelgest.cat     / jugadora1234   USER
--   albert@padelgest.cat   / jugador1234    USER
--   mariona@padelgest.cat  / jugadora1234   USER
--   daniel@padelgest.cat   / jugador1234    USER (desactivat)
--
-- Les dates de les reserves són relatives al dia que s'executa
-- (CURRENT_DATE = avui)
-- =====================================================================

-- Buida les taules y comença els id per 1
TRUNCATE TABLE reserves, pistes, usuaris RESTART IDENTITY CASCADE;

-- Usuaris (contrasenyes xifrades amb PBKDF2, generades pel servidor)
INSERT INTO usuaris (nom, cognoms, email, contrasenya_hash, rol, actiu) VALUES
('Admin', 'Club', 'admin@padelgest.cat', '622b24bb95f18f44b9f3a91017a9affd412abf58afb704f30d6d919fec8681355550b90e904365d26cce7e3ad40af5c8', 'ADMIN', TRUE),
('Fernando', 'Padilla', 'fernando@padelgest.cat', 'c79e83df8edfda18aef62f8afe073167f3573953aa12a39839c9977841d5858b9bc084eaef0c41d81b4c3b04094d262e', 'USER', TRUE),
('Neus', 'Ortuño', 'neus@padelgest.cat', '83afb683304c99aa47a5f27b35a770994ce4d6835c8077eb533fb9222d1fd85201db8dac9aa6da3dfd73e69e7fa7f595', 'USER', TRUE),
('Albert', 'Pedrola', 'albert@padelgest.cat', 'ebedb9e478ffdd848b4fbe0d1a2d320b88f21411bb1bb1785eae3f8ec8d40ce590d888011037cd13276978f424a81274', 'USER', TRUE),
('Mariona', 'Padilla', 'mariona@padelgest.cat', '90760e286b0987b02c3870ef9132957c3d6e608bfde1682c9291ca5292aa45a84954a99204b835bae12b548d95722245', 'USER', TRUE),
('Daniel', 'Gonzalez', 'daniel@padelgest.cat', '08363a56d5c0de948a9f79f62ed4f4298f5a9fe6981130609d201d7aa9c5d5107bf45df9f575cbbefeb74454435291ab', 'USER', FALSE);

-- Pistes (la 4 està desactivada)
INSERT INTO pistes (nom, tipus, activa) VALUES
('Pista 1', 'INTERIOR', TRUE),
('Pista 2', 'INTERIOR', TRUE),
('Pista 3', 'EXTERIOR', TRUE),
('Pista 4', 'EXTERIOR', FALSE);

-- Reserves (id_usuari: 1 Admin, 2 Fernando, 3 Neus, 4 Albert, 5 Mariona)
INSERT INTO reserves (id_usuari, id_pista, data, hora_inici, hora_fi, estat) VALUES
-- Fa 3 dies (historial)
(2, 1, CURRENT_DATE - 3, '10:00', '11:00', 'ACTIVA'),
(5, 2, CURRENT_DATE - 3, '12:00', '13:00', 'ACTIVA'),
-- Avui al vespre (per provar la verificació a recepció)
(2, 1, CURRENT_DATE, '21:00', '22:00', 'ACTIVA'),
(3, 2, CURRENT_DATE, '22:00', '23:00', 'ACTIVA'),
-- Demà
(2, 1, CURRENT_DATE + 1, '10:00', '11:00', 'ACTIVA'),
(3, 1, CURRENT_DATE + 1, '11:00', '12:00', 'ACTIVA'),
(4, 1, CURRENT_DATE + 1, '18:00', '19:00', 'ACTIVA'),
(5, 2, CURRENT_DATE + 1, '19:00', '20:00', 'ACTIVA'),
(4, 3, CURRENT_DATE + 1, '09:00', '10:00', 'CANCELLADA'),
-- Demà passat
(2, 2, CURRENT_DATE + 2, '17:00', '18:00', 'ACTIVA'),
(3, 3, CURRENT_DATE + 2, '18:00', '19:00', 'ACTIVA');
