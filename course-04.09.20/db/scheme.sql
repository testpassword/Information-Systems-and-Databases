-- https://stackoverflow.com/questions/7296846/how-to-implement-one-to-one-one-to-many-and-many-to-many-relationships-while-de
CREATE TABLE base
(
    base_id  SERIAL PRIMARY KEY,
    location TEXT NOT NULL,
    status   TEXT NOT NULL
);

CREATE TABLE mre
(
    mre_id         SERIAL PRIMARY KEY,
    breakfast      TEXT     NOT NULL,
    lunch          TEXT     NOT NULL,
    dinner         TEXT     NOT NULL,
    food_additives TEXT,
    kkal           SMALLINT NOT NULL CHECK (kkal >= 3000),
    proteins       SMALLINT NOT NULL CHECK (proteins > 0),
    fats           SMALLINT NOT NULL CHECK (fats > 0),
    carbohydrate   SMALLINT NOT NULL CHECK (carbohydrate > 0)
);

CREATE TABLE equipment
(
    equip_id      SERIAL PRIMARY KEY,
    camouflage    TEXT,
    communication TEXT,
    intelligence  TEXT,
    medical       TEXT,
    mre_id        INTEGER NOT NULL REFERENCES mre ON DELETE RESTRICT,
    extra         TEXT
);

CREATE TYPE force AS ENUM ('GF', 'NAVY', 'AF');

CREATE TABLE position
(
    pos_id   SERIAL PRIMARY KEY,
    name     TEXT           NOT NULL,
    salary   NUMERIC(11, 2) NOT NULL CHECK (salary >= 300),
    rank     TEXT,
    equip_id INTEGER        REFERENCES equipment ON DELETE SET NULL,
    forces   FORCE
);

CREATE TABLE employee
(
    emp_id        SERIAL PRIMARY KEY,
    name          TEXT    NOT NULL,
    surname       TEXT    NOT NULL,
    date_of_birth DATE    NOT NULL CHECK (DATE_PART('year', AGE(date_of_birth)) >= 18),
    education     TEXT,
    hiring_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    pos_id        INTEGER NOT NULL REFERENCES position ON DELETE RESTRICT,
    is_married    BOOLEAN NOT NULL,
    base_id       INTEGER REFERENCES base ON DELETE SET NULL
);

CREATE TABLE medical_card
(
    med_id    SERIAL PRIMARY KEY,
    emp_id    INTEGER  NOT NULL REFERENCES employee ON DELETE CASCADE,
    height_cm SMALLINT NOT NULL,
    weight_kg SMALLINT NOT NULL,
    diseases  TEXT,
    blood     TEXT     NOT NULL,
    gender    BOOLEAN  NOT NULL
);

CREATE TABLE weapon
(
    weapon_id        SERIAL PRIMARY KEY,
    name             TEXT NOT NULL,
    type             TEXT NOT NULL,
    caliber          REAL CHECK (caliber > 0),
    rate_of_fire     SMALLINT CHECK (rate_of_fire > 0),
    sighting_range_m SMALLINT CHECK (sighting_range_m > 0)
);

CREATE TABLE campaign
(
    camp_id          SERIAL PRIMARY KEY,
    name             TEXT           NOT NULL,
    customer         TEXT           NOT NULL,
    earning          NUMERIC(11, 2) NOT NULL CHECK (earning >= 0),
    spending         NUMERIC(11, 2) NOT NULL CHECK (spending >= 0),
    execution_status TEXT
);

CREATE TABLE mission
(
    miss_id             SERIAL PRIMARY KEY,
    camp_id             INTEGER NOT NULL REFERENCES campaign ON DELETE CASCADE,
    start_date_and_time TIMESTAMP,
    end_date_and_time   TIMESTAMP,
    legal_status        BOOLEAN NOT NULL,
    departure_location  TEXT,
    arrival_location    TEXT,
    enemies             TEXT
);

CREATE TABLE transport
(
    trans_id SERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    type     TEXT NOT NULL,
    status   TEXT NOT NULL
);

CREATE TABLE equip_weapon
(
    equip_id  INTEGER NOT NULL REFERENCES equipment,
    weapon_id INTEGER NOT NULL REFERENCES weapon
);

CREATE TABLE missions_transport
(
    miss_id  INTEGER NOT NULL REFERENCES mission,
    trans_id INTEGER NOT NULL REFERENCES transport
);

CREATE TABLE inspection
(
    emp_id       INTEGER NOT NULL REFERENCES employee,
    trans_id     INTEGER NOT NULL REFERENCES transport,
    service_date DATE    NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE missions_emp
(
    miss_id INTEGER NOT NULL REFERENCES mission,
    emp_id  INTEGER NOT NULL REFERENCES employee
);