-- https://stackoverflow.com/questions/7296846/how-to-implement-one-to-one-one-to-many-and-many-to-many-relationships-while-de
CREATE TABLE base
(
    baseId   SERIAL PRIMARY KEY,
    location TEXT,
    status   TEXT
);

CREATE TABLE mre
(
    mreId         SERIAL PRIMARY KEY,
    breakfast     TEXT     NOT NULL,
    lunch         TEXT     NOT NULL,
    dinner        TEXT     NOT NULL,
    foodAdditives TEXT,
    kkal          SMALLINT NOT NULL CHECK (kkal > 1000),
    proteins      SMALLINT NOT NULL CHECK (proteins > 0),
    fats          SMALLINT NOT NULL CHECK (fats > 0),
    carbohydrate  SMALLINT NOT NULL CHECK (carbohydrate > 0)
);

CREATE TABLE equipment
(
    equipId       SERIAL PRIMARY KEY,
    camouflage    TEXT,
    communication TEXT,
    intelligence  TEXT,
    medical       TEXT,
    mreId         INTEGER NOT NULL REFERENCES mre,
    extra         TEXT
);

CREATE TYPE force AS ENUM ('СВ', 'ВМФ', 'ВКС');

CREATE TABLE position
(
    posId   SERIAL PRIMARY KEY,
    name    TEXT           NOT NULL,
    salary  NUMERIC(11, 2) NOT NULL CHECK (salary > 12130),
    rank    TEXT,
    equipId INTEGER        REFERENCES equipment ON DELETE SET NULL,
    forces  FORCE
);

CREATE TABLE employee
(
    empId       SERIAL PRIMARY KEY,
    name        TEXT    NOT NULL,
    surname     TEXT    NOT NULL,
    dateOfBirth DATE    NOT NULL CHECK (DATE_PART('year', AGE(dateOfBirth)) >= 18),
    education   TEXT,
    hiringDate  DATE DEFAULT CURRENT_DATE,
    posId       INTEGER NOT NULL REFERENCES position ON DELETE RESTRICT,
    isMarried   BOOLEAN NOT NULL,
    baseId      INTEGER
);

CREATE TYPE blood AS ENUM ('0+', '0-', 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-');

CREATE TABLE medical_card
(
    medId     SERIAL PRIMARY KEY,
    empId     INTEGER REFERENCES employee ON DELETE CASCADE,
    height_cm SMALLINT NOT NULL CHECK (height_cm >= 150),
    weight_kg SMALLINT NOT NULL CHECK (weight_kg >= 45),
    diseases  TEXT,
    blood     BLOOD    NOT NULL,
    gender    BOOLEAN  NOT NULL
);

CREATE TABLE weapon
(
    weaponId      SERIAL PRIMARY KEY,
    name          TEXT NOT NULL,
    type          TEXT NOT NULL,
    caliber       REAL CHECK (caliber > 0),
    rateOfFire    SMALLINT CHECK (rateOfFire > 0),
    barrelLength  SMALLINT CHECK (barrelLength > 0),
    sightingRange SMALLINT CHECK (sightingRange > 0)
);

CREATE TABLE campaing
(
    campId          SERIAL PRIMARY KEY,
    name            TEXT           NOT NULL,
    customer        TEXT           NOT NULL,
    earning         NUMERIC(11, 2) NOT NULL CHECK (earning > 0),
    spending        NUMERIC(11, 2) NOT NULL CHECK (spending > 0),
    executionStatus TEXT
);

CREATE TABLE mission
(
    missId            SERIAL PRIMARY KEY,
    campId            INTEGER NOT NULL REFERENCES campaing ON DELETE CASCADE,
    startDateAndTime  TIMESTAMP,
    endDateAndTime    TIMESTAMP,
    legalStatus       TEXT,
    departureLocation TEXT,
    arrivalLocation   TEXT,
    enemies           TEXT
);

CREATE TABLE transport
(
    transId SERIAL PRIMARY KEY,
    name    TEXT NOT NULL,
    type    TEXT NOT NULL,
    status  TEXT
);

CREATE TABLE equip_weapon
(
    equipId  INTEGER NOT NULL REFERENCES equipment,
    weaponId INTEGER NOT NULL REFERENCES weapon
);

CREATE TABLE missions_transport
(
    missId  INTEGER NOT NULL REFERENCES mission,
    transId INTEGER NOT NULL REFERENCES transport
);

CREATE TABLE inspection
(
    empId       INTEGER NOT NULL REFERENCES employee,
    transId     INTEGER NOT NULL REFERENCES transport,
    serviceDate DATE DEFAULT CURRENT_DATE
);

CREATE TABLE missions_emp
(
    missId INTEGER NOT NULL REFERENCES mission,
    empId  INTEGER NOT NULL REFERENCES employee
);