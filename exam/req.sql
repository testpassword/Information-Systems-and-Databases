-- вывести имена и фамилии тех, кто "одевал" (by Николаев) и зимную, и летнюю одежду
SELECT имя, фамилия FROM человек
WHERE человекID IN (
    SELECT человекID, COUNT(сезон) FROM человек
    JOIN человек_одежда USING (человекID)
    JOIN одежда USING (одеждаID)
    JOIN тип_одежды USING (тип_одеждыID)
    JOIN сезон USING (сезонID)
    WHERE сезон IN ('ЗИМА', 'ЛЕТО')
    GROUP BY человекID
    HAVING COUNT(сезон) = 2
);