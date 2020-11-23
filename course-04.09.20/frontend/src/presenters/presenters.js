import {Tag} from "antd"
import React from "react"

const BasePresenter = {
    url: "http://localhost:9090/base",
    idField: "baseId"
}

const CampaignPresenter = {
    url: "http://localhost:9090/campaign",
    idField: "campId"
}

const EmployeePresenter = {
    url: "http://localhost:9090/employee",
    idField: "empId"
}

const EquipmentPresenter = {
    url: "http://localhost:9090/equipment",
    idField: "equipId"
}

const MedicalCardPresenter = {
    url: "http://localhost:9090/medicalCard",
    idField: "medId"
}

const MissionPresenter = {
    url: "http://localhost:9090/mission",
    idField: "missId"
}

const MrePresenter = {
    url: "http://localhost:9090/mre",
    idField: "mreId"
}

const PositionPresenter = {
    url: "http://localhost:9090/position",
    idField: "posId"
}

const TransportPresenter = {
    url: "http://localhost:9090/transport",
    idField: "transId"
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