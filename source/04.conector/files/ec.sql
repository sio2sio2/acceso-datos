CREATE TABLE Centro (
   id             INTEGER        PRIMARY KEY /* GENERATED BY DEFAULT AS IDENTITY */,
   nombre         VARCHAR(200)   NOT NULL,
   titularidad    CHAR(7)        CHECK (titularidad IN ('público', 'privado'))
);

CREATE TABLE Estudiante (
   id             INTEGER        PRIMARY KEY /* GENERATED BY DEFAULT AS IDENTITY */,
   nombre         VARCHAR(250)   NOT NULL,
   nacimiento     DATE           NOT NULL,
   centro         INTEGER,

   CONSTRAINT fk_est_cen FOREIGN KEY(centro) REFERENCES Centro(id)
      ON DELETE SET NULL
      ON UPDATE CASCADE
);

-- Datos
INSERT INTO Centro VALUES
   (11004866, 'IES Castillo de Luna', 'público'),
   (11007533, 'IES Arroyo Hondo', 'público'),
   (11700603, 'IES Pintor Juan Lara', 'público'),
   (11701164, 'IES Astaroth', 'público');
