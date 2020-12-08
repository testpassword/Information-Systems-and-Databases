import React from "react"
import {Button, Form, Input, InputNumber} from "antd";

class WeaponCreator extends React.Component {

    render() {
        return <Form>
            <Form.Item
                label="Caliber"
                name="caliber">
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Name"
                name="name"
                rules={[{ required: true, message: "Input name of weapon" }]}>
            </Form.Item>
            <Form.Item
                label="Rate of fire"
                name="rateOfFire">
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Sighting range m"
                name="sightingRangeM">
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Type"
                name="type"
                rules={[{ required: true, message: "Input type of weapon" }]}>
                <Input/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
    }
}

const WeaponPresenter = {
    url: "http://localhost:9090/weapon",
    idField: "weaponId",
    creator: <WeaponCreator/>
}

export default WeaponPresenter