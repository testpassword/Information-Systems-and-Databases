-- Вариант 1754
-- 1
explain select "НАИМЕНОВАНИЕ", "ЧЛВК_ИД" from "Н_ТИПЫ_ВЕДОМОСТЕЙ" тв
    join "Н_ВЕДОМОСТИ" в on в."ТВ_ИД" = тв."ИД"
where "НАИМЕНОВАНИЕ" < 'Перезачёт' and
    в."ИД" < 1250972;
-- 2
explain select л."ИМЯ", в."ДАТА", с."ИД" from "Н_ЛЮДИ" л
    join "Н_ВЕДОМОСТИ" в on в."ЧЛВК_ИД" = л."ИД"
    join "Н_СЕССИЯ" с on с."ЧЛВК_ИД" = л."ИД"
where "ФАМИЛИЯ" = 'Афанасьев' and
        в."ЧЛВК_ИД" = 142390 and
        "УЧГОД" = '2001/2002';