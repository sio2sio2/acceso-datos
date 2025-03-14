PRAGMA foreign_keys = ON;  -- Propio de SQLite

-- Descoméntese GENERATED, etc. si no se usa SQLite.
CREATE TABLE Departamento (
   id_departamento  INTEGER PRIMARY KEY /* GENERATED BY DEFAULT AS IDENTITY */,
   denominacion     VARCHAR(50)    NOT NULL
);

CREATE TABLE Centro (
   id_centro        INTEGER        PRIMARY KEY /* GENERATED BY DEFAULT AS IDENTITY */,
   nombre           VARCHAR(100)   NOT NULL
);

CREATE TABLE Profesor (
   id_profesor      INTEGER        PRIMARY KEY /* GENERATED BY DEFAULT AS IDENTITY */,
   apelativo        VARCHAR(50)    NOT NULL,
   nombre           VARCHAR(75)    NOT NULL,
   apellidos        VARCHAR(150)   NOT NULL,
   sustituye        INTEGER        UNIQUE DEFAULT NULL,

   CONSTRAINT fk_pro_pro FOREIGN KEY(sustituye) REFERENCES Profesor(id_profesor)
      ON DELETE SET NULL
      ON UPDATE CASCADE
);

CREATE TABLE Trabaja (
   profesor         INTEGER,
   centro           INTEGER,
   departamento     INTEGER,
   -- Para permitir que un mismo profesor tenga varios casilleros.
   casillero        INTEGER ARRAY    NOT NULL,
   
   CONSTRAINT pk_tra     PRIMARY KEY(profesor, centro),
   CONSTRAINT fk_tra_pro FOREIGN KEY(profesor) REFERENCES Profesor(id_profesor)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
   CONSTRAINT fk_tra_cla FOREIGN KEY(centro) REFERENCES Centro(id_centro)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
   CONSTRAINT fk_est_dep FOREIGN KEY(departamento) REFERENCES Departamento(id_departamento)
      ON DELETE SET NULL
      ON UPDATE CASCADE
);


-- Los sustitutos no puede aparecer en la tabla Trabaja
CREATE TRIGGER NoSustitutoEnTrabaja
BEFORE INSERT ON Trabaja
FOR EACH ROW
WHEN (SELECT sustituye FROM Profesor WHERE id_profesor = NEW.profesor) IS NOT NULL
BEGIN
   SELECT RAISE(FAIL, 'Imposible registrar un sustituto en Trabaja: hace la misma labor que el sustituido.');
END;

INSERT INTO Departamento (denominacion) VALUES
	('Lengua Española y Literatura'),
	('Inglés'),
	('Francés'),
	('Física y Química'),
	('Geografía e Historia'),
	('Tecnología'),
	('Clásicas');

INSERT INTO Centro VALUES
   (11004866, 'IES Castillo de Luna'),
   (11701164, 'IES Astaroth'),
   (11007533, 'IES Arroyo Hondo');

INSERT INTO Profesor (apelativo, nombre, apellidos) VALUES
   ('Manolo', 'Manuel', 'Ibañez Verano'),
   ('Juan', 'Juna Alberto', 'Cossío Álvarez'),
   ('Loli', 'Dolores', 'Japón Peinado'),
   ('Merche', 'Mercedes', 'Páez Beltrán'),
   ('Santi', 'Santiago', 'Menéndez Campany'),
   ('Inma', 'María Inmaculada', 'Díaz Mateos'),
   ('Valentín', 'Valentín', 'Téllez Mellado');
   
-- Sustituciones.
UPDATE Profesor SET sustituye = 1 WHERE id_profesor = 3; -- Loli sustituye a Manolo.
UPDATE Profesor SET sustituye = 4 WHERE id_profesor = 5; -- Santi sustituye a Merche.
UPDATE Profesor SET sustituye = 5 WHERE id_profesor = 6; -- Inma sustituye a Santi.
UPDATE Profesor SET sustituye = 6 WHERE id_profesor = 7; -- Valentín sustituye a Inma.

-- Asignación de profesores a centros.
INSERT INTO Trabaja VALUES
   (1, 11004866, 1, 13),
   (2, 11004866, 2, 3),
   (1, 11701164, 1, 25),
   (4, 11007533, 5, 67);

.mode column

SELECT id_profesor,apelativo,sustituye FROM Profesor;

CREATE VIEW PlantillaFuncionamiento AS
   WITH Sustituido AS (
      SELECT id_profesor, sustituye
         FROM Profesor
         WHERE sustituye IS NOT NULL
      UNION ALL
      SELECT S.id_profesor, P.sustituye
         FROM Profesor P
                  INNER JOIN
              Sustituido S ON P.id_profesor = S.sustituye
         WHERE P.sustituye is NOT NULL
   )
   SELECT DISTINCT S1.id_profesor AS sustituto,
          S1.sustituye AS titular,
          CASE WHEN (S3.id_profesor IS NULL) THEN 1 ELSE 0 END AS activo
      FROM Sustituido S1
               LEFT JOIN
           Sustituido S2 ON S1.sustituye = S2.id_profesor
               LEFT JOIN
           Sustituido S3 ON S1.id_profesor = S3.sustituye
      WHERE S2.id_profesor IS NULL
   UNION
   SELECT P1.id_profesor AS sustituto,
          P1.id_profesor AS titular,
          CASE WHEN (P3.id_profesor IS NULL) THEN 1 ELSE 0 END AS activo
      FROM Profesor P1
               LEFT JOIN
           Profesor P2 ON P1.sustituye = P2.id_profesor
               LEFT JOIN
           Profesor P3 ON P1.id_profesor = P3.sustituye
      WHERE P2.id_profesor IS NULL;


SELECT * FROM PlantillaFuncionamiento;

-- Listado de profesores activos con centros en los que trabaja.
SELECT P.apelativo AS profe,
       T.centro AS centro,
       D.denominacion AS departamento,
       T.casillero AS casillero
   FROM Trabaja T
         INNER JOIN
        PlantillaFuncionamiento F ON F.titular = T.profesor
         INNER JOIN
        Departamento D ON T.departamento = D.id_departamento
         INNER JOIN
        Profesor P ON P.id_profesor = F.sustituto
   WHERE F.activo = 1
   ORDER BY P.id_profesor;
