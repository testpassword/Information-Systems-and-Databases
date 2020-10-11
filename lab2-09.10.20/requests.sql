-- Вариант 9981
-- 1
select "ИМЯ", "ДАТА" from "Н_ЛЮДИ" л
    join "Н_СЕССИЯ" с on л."ИД" = с."ЧЛВК_ИД"
where л."ИМЯ" > 'Александр' and
        с."УЧГОД" in ('2001/2002') and
        с."УЧГОД" < ('2008/2009');
-- 2
select л."ИД", в."ЧЛВК_ИД", с."ИД" from "Н_ЛЮДИ" л
    right join "Н_ВЕДОМОСТИ" в on л."ИД" = в."ЧЛВК_ИД"
    right join "Н_СЕССИЯ" с on л."ИД" = с."ЧЛВК_ИД"
where л."ИМЯ" < 'Николай' and
      в."ЧЛВК_ИД" = 153285;
-- 3
select count(*) from "Н_УЧЕНИКИ" у
    join "Н_ЛЮДИ" л on у."ЧЛВК_ИД" = л."ИД"
where "ГРУППА" like '3102' and
      (DATE_PART('year', AGE("ДАТА_РОЖДЕНИЯ")) >= 25);
-- 4

/*select "ГРУППА" from "Н_УЧЕНИКИ" у
group by "ГРУППА"
having count("ГРУППА") = 10*/

-- 5

-- 6
-- 7