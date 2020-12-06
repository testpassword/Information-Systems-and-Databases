import React from "react"

const MedicalCardPresenter = {
    url: "http://localhost:9090/medicalCard",
    idField: "medId",
    filteredColumns: {
        blood: [
            { text: "O-", value: "O-" },
            { text: "O+", value: "O+" },
            { text: "A-", value: "A-" },
            { text: "A+", value: "A+" },
            { text: "B-", value: "B+" },
            { text: "AB-", value: "AB-" },
            { text: "AB+", value: "AB+" },
        ],
        gender: [
            { text: "male", value: true },
            { text: "female", value: false }
        ]
    }
}

const MissionPresenter = {
    url: "http://localhost:9090/mission",
    idField: "missId",
    filteredColumns: {
        legalStatus: [
            { text: "legal", value: true },
            { text: "illegal", value: false }
        ]
    }
}

const MrePresenter = {
    url: "http://localhost:9090/mre",
    idField: "mreId"
}

const PositionPresenter = {
    url: "http://localhost:9090/position",
    idField: "posId",
    filteredColumns: {
        forces: [
            { text: "NAVY", value: "NAVY" },
            { text: "AF", value: "AF" },
            { text: "GF", value: "GF" }
        ]
    }
}

const TransportPresenter = {
    url: "http://localhost:9090/transport",
    idField: "transId",
    filteredColumns: {
        status: [
            { text: "available", value: "available" },
            { text: "under_repair", value: "under_repair" },
            { text: "destroyed", value: "destroyed" },
            { text: "broken", value: "broken" }
        ]
    }
}

const WeaponPresenter = {
    url: "http://localhost:9090/weapon",
    idField: "weaponId"
}

export { MedicalCardPresenter, MissionPresenter,
    MrePresenter, PositionPresenter, TransportPresenter, WeaponPresenter }