-- Как были экипированы люди после участия в миссиях после которых сломался транспорт
SELECT equipment.* FROM employee
    JOIN position USING (pos_id)
    JOIN equipment USING (equip_id)
WHERE emp_id IN (
    SELECT emp_id FROM missions_emp
    WHERE miss_id IN (
        SELECT miss_id FROM missions_transport
        WHERE trans_id IN (
            SELECT trans_id FROM transport
            WHERE status ~'broken'
        )
        LIMIT 1
    )
)