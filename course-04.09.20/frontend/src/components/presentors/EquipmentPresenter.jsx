import React from "react"
import { Button, Form, Input } from "antd"
import AbstractCreator from "./AbstractCreator.jsx"

class EquipmentCreator extends AbstractCreator {

    render() {
        return <Form onFinish={this.onTrigger}>
            <Form.Item
                label="Camouflage"
                name="camouflage">
                <Input/>
            </Form.Item>
            <Form.Item
                label="Communication"
                name="communication">
                <Input/>
            </Form.Item>
            <Form.Item
                label="Intelligence"
                name="intelligence">
                <Input/>
            </Form.Item>
            <Form.Item
                label="Medical"
                name="medical">
                <Input/>
            </Form.Item>
            <Form.Item
                label="Extras"
                name="extra">
                <Input placeholder="Use space as separator"/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
    }
}

const EquipmentPresenter = {
    url: "http://localhost:9090/equipment",
    idField: "equipId",
    creator: <EquipmentCreator/>
}

export default EquipmentPresenter