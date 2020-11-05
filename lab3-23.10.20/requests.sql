-- Вариант 1754
-- 1
explain analyse select "НАИМЕНОВАНИЕ", "ЧЛВК_ИД" from "Н_ТИПЫ_ВЕДОМОСТЕЙ" тв
    join "Н_ВЕДОМОСТИ" в on в."ТВ_ИД" = тв."ИД"
where "НАИМЕНОВАНИЕ" < 'Перезачёт' and
    в."ИД" < 1250972;

-- нет смысла строить индекс

-- 2
explain analyse select л."ИМЯ", в."ДАТА", с."ИД" from "Н_ЛЮДИ" л
    join "Н_ВЕДОМОСТИ" в on в."ЧЛВК_ИД" = л."ИД"
    join "Н_СЕССИЯ" с on с."ЧЛВК_ИД" = л."ИД"
where "ФАМИЛИЯ" = 'Афанасьев' and
        в."ЧЛВК_ИД" = 142390 and
        "УЧГОД" = '2001/2002';

create unique index "Н_СЕССИЯ-УЧГОД-BTREE" on "Н_СЕССИЯ" using btree("УЧГОД");

/*
https://habr.com/ru/post/276973/
https://edu.postgrespro.ru/qpt/qpt_05_bitmapscan.html
https://postgrespro.ru/docs/postgrespro/10/using-explain
https://postgrespro.ru/docs/postgresql/13/indexes-types
https://postgrespro.ru/docs/postgresql/13/sql-createindex
https://ru.wikipedia.org/wiki/Оптимизация_запросов_СУБД
*/