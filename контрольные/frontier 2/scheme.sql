-- group is reserved word
CREATE TABLE groups
(
    number INTEGER PRIMARY KEY,
    name TEXT -- this field used only for inserted statement (without it, we cant insert nothing)
);

CREATE TABLE person
(
    id INTEGER PRIMARY KEY,
    name TEXT,
    age SMALLINT
);

CREATE TABLE dept
(
    id INTEGER PRIMARY KEY,
    name TEXT
);

CREATE TABLE student
(
    id INTEGER PRIMARY KEY,
    group_num INTEGER REFERENCES groups,
    person_id INTEGER REFERENCES person,
    dept_id INTEGER REFERENCES dept
);

CREATE TABLE employee
(
    id INTEGER PRIMARY KEY,
    person_id INTEGER REFERENCES person,
    dept_id INTEGER REFERENCES dept
);