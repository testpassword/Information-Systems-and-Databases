-- Вариант 2
SELECT s.id, p.name FROM student s
    JOIN person p ON s.person_id = p.id
    JOIN employee e ON p.id = e.person_id
    JOIN dept d ON d.id = s.dept_id
WHERE d.name LIKE 'ПИиКТ';

/*
 • B-дерево для операций с сравнениями
 • Hash для простого равенства (=)
 • Gist для работы со составными данными (координаты, геом. фигуры)
 • Brin для усреднения определённого участка данных
 • Битовая карта для сложных операторов where
*/

CREATE UNIQUE INDEX dept_name ON dept USING btree(name); -- postgres
CREATE INDEX dept_name ON dept (name); -- sqlite3 (btree as default)