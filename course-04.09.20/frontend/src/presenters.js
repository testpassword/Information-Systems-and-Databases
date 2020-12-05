import React from "react"

const BasePresenter = {
    url: "http://localhost:9090/base",
    idField: "baseId",
    filteredColumns: {
        status: [
            { text: "working", value: "working" },
            { text: "closed", value: "closed" },
            { text: "destroyed", value: "destroyed" },
            { text: "abandoned", value: "abandoned" },
            { text: "captured", value: "captured" },
            { text: "for_sale", value: "for_sale" }
        ]
    }
}

const CampaignPresenter = {
    url: "http://localhost:9090/campaign",
    idField: "campId",
    filteredColumns: {
        executionStatus: [
            { text: "completed", value: "completed" },
            { text: "in the progress", value: "in the progress" },
            { text: "failed", value: "failed" },
            { text: "canceled", value: "canceled" }
        ]
    }
}

const EmployeePresenter = {
    url: "http://localhost:9090/employee",
    idField: "empId",
    filteredColumns: {
        isMarried: [
            { text: "married", value: true },
            { text: "single", value: false }
        ]
    }
}

const EquipmentPresenter = {
    url: "http://localhost:9090/equipment",
    idField: "equipId"
}

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

export { BasePresenter, CampaignPresenter, EmployeePresenter, EquipmentPresenter, MedicalCardPresenter, MissionPresenter,
    MrePresenter, PositionPresenter, TransportPresenter, WeaponPresenter }

        /*this.columns = [
            {
                title: "LOCATION",
                dataIndex: "location",
                sorter: (a, b) => a.location.localeCompare(b.location),
                /!*...this.getColumnSearchProps("location")*!/
            },
            {
                title: "STATUS",
                dataIndex: "status",
                sorter: (a, b) => a.status.localeCompare(b.status),
                /!*...this.getColumnSearchProps("status")*!/
                render: it => {
                    let color
                    switch (it) {
                        case "working":
                        case "for_sale":
                            color = "green"
                            break
                        case "closed":
                        case "abandoned":
                            color = "geekblue"
                            break
                        case "captured":
                        case "destroyed":
                            color = "volcano"
                            break
                        default:
                            color = 'gray'
                    }
                    return <Tag color={color} key={it}>{it.toUpperCase()}</Tag>
                }
            }
        ]*/