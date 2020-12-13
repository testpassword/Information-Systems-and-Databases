class EntitiesApi {

    static idBuffer = null

    static get(entityUrl, ids = []) {
        /* Это наглядный пример плохого дизайна: изначально когда я писал api для бекенда, я предполагал, что в теле GET
        запроса будет передаваться json-массив с id нужных элементов и пустой массив, если клиент хочет получить все элементы.
        На этапе создания фронтенда я вспомнил, что по спецификации HTTP передавать тело с GET не рекомендуется, а fetch()
        и вовсе это запрещает. Т.к. кардинально менять api бэка не хотелось, решил костыльно передавать json-массив как
        параметр запроса. */
        const url = entityUrl + "?=" + new URLSearchParams({ "ids":  JSON.stringify({ selectedIds: ids }) })
        const req = { method: "GET", mode: "cors" }
        return fetch(url, req)
    }

    static post(entityUrl, idField, item) {
        const req = {
            method: "POST",
            mode: "cors",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                [idField]: null,
                ...item
            })
        }
        return fetch(entityUrl, req)
    }

    static put(entityUrl, idField, item, key) {
        const req = {
            method: "PUT",
            mode: "cors",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                [idField]: item[idField],
                [key]: item[key]
            })
        }
        return fetch(entityUrl, req)
    }

    static delete(entityUrl, ids) {
        const req = {
            method: "DELETE",
            mode: "cors",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ droppedIds: ids })
        }
        return fetch(entityUrl, req)
    }
}

export default EntitiesApi