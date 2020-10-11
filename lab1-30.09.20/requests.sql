--какие существа живут на тех планетах, сообщения с которых получали не земляне
SELECT name FROM creature WHERE planetOfOrigin IN (
    SELECT planetId FROM antenna WHERE antennaId IN (
        SELECT senderId FROM message WHERE messageId IN (
            SELECT messageId
            FROM transmission
            WHERE reciverId IN (
                SELECT antennaId
                FROM antenna
                         JOIN planet USING (planetid)
                WHERE planet.name NOT LIKE 'Земля')
        )
    )
);