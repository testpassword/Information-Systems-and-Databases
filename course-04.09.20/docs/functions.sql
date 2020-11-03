--1: Тех, кто не имеет воинских званий, нельзя отправлять на боевые миссии.
CREATE FUNCTION is_military_on_mission() RETURNS trigger AS $$
DECLARE enemy TEXT;
    DECLARE rank TEXT;
BEGIN
    enemy = (SELECT enemies FROM mission WHERE miss_id = new.miss_id);
    rank = (SELECT rank FROM position JOIN employee USING (pos_id) WHERE emp_id = new.emp_id);
    IF (enemy IS NOT NULL OR !~~ '') AND (rank IS NULL OR ~~ '') THEN
        RAISE EXCEPTION 'Cannot set not military employee to a combat mission';
    ELSE RETURN new;
    END IF;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER is_military_on_mission BEFORE INSERT OR UPDATE ON missions_emp
    FOR EACH ROW EXECUTE FUNCTION is_military_on_mission();

/*
2: Информационная система должна учитывая какие сотрудники отправились на миссии (один и тот же сотрудник не
    может находиться на двух миссиях одновременно).
*/
CREATE FUNCTION check_periods_of_emp_missions() RETURNS trigger AS $$
    DECLARE inserted_miss mission;
    BEGIN
        inserted_miss = (SELECT * FROM mission WHERE miss_id = new.miss_id);
        IF (TRUE) IN (
            SELECT (inserted_miss.start_date_and_time, inserted_miss.end_date_and_time) OVERLAPS
                   (start_date_and_time, end_date_and_time) FROM mission
                WHERE miss_id IN (
                    SELECT miss_id FROM missions_emp WHERE emp_id = new.emp_id
                    )) THEN
            RAISE EXCEPTION 'This worker cannot be assigned to a mission as he was on another mission at the time';
        ELSE RETURN new;
        END IF;
    END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER check_emp_mission_period BEFORE INSERT OR UPDATE ON missions_emp
    FOR EACH ROW EXECUTE FUNCTION check_periods_of_emp_missions();

--3: Работников неподходящих по физическим данным запрещено устраивать как военных сотрудников (рост < 150 см или вес < 45 кг).
CREATE FUNCTION check_physical_condition() RETURNS trigger AS $$
    DECLARE card medical_card;
    BEGIN
        card = (SELECT height_cm, weight_kg FROM medical_card JOIN employee USING (emp_id) WHERE emp_id = new.emp_id);
        IF card.height_cm < 150 OR card.weight_kg < 45 THEN
            RAISE EXCEPTION 'Cannot hair this employee to military position because his physical data does not require the minimum';
        ELSE RETURN new;
        END IF;
    END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER check_physical_condition BEFORE INSERT OR UPDATE ON employee
    FOR EACH ROW EXECUTE FUNCTION check_physical_condition();

/*
 4: Необходимо хранить историю инспекций транспорта (реализована отдельной таблицей),
    а транспорт со статусами «сломан» или «в ремонте» нельзя использовать в операциях.
 */
CREATE FUNCTION check_transport_condition() RETURNS trigger AS $$
    BEGIN
        IF (SELECT status FROM transport WHERE trans_id = new.trans_id AND status = 'available') IS NULL THEN
            RAISE EXCEPTION 'Cannot set not available transport to mission';
        ELSE RETURN new;
        END IF;
    END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER check_transport_condition BEFORE INSERT OR UPDATE ON missions_transport
    FOR EACH ROW EXECUTE FUNCTION check_transport_condition();

-- 5: Если за базой не закреплён ни один сотрудник, стоит закрыть её.
CREATE FUNCTION close_empty_bases() RETURNS SETOF void AS $$
    BEGIN
        DELETE FROM base WHERE base_id IN (
            SELECT base_id FROM base
                JOIN employee USING (base_id)
            GROUP BY base_id
            HAVING COUNT(emp_id) = 0
            );
    END;
$$ LANGUAGE plpgsql;

/*
6: Стараться отправлять на боевые операции при прочих равных в первую очередь неженатых военных, давно не
    участвовавших в миссиях, имеющих большой опыт работы.
 */
CREATE FUNCTION get_combat_candidates(n int DEFAULT 1) RETURNS employee AS $$
    BEGIN
        SELECT emp_id FROM employee
            JOIN position USING (pos_id)
            JOIN missions_emp USING (emp_id)
            JOIN mission USING (miss_id)
        WHERE rank IS NOT NULL OR !~~ ''
        ORDER BY is_married DESC, end_date_and_time DESC, hiring_date DESC
        LIMIT n;
    END;
$$ LANGUAGE plpgsql;

/*
http://firststeps.ru/sql/oracle/r.php?43
https://postgrespro.ru/docs/postgresql/13/plpgsql-trigger
*/