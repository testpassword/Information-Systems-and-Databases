import React from "react"
import { Button, Form, Input } from "antd"

class EquipmentCreator extends React.Component {

    onFinish = values => {
        //TODO: вернуть EntityTable
        console.log('Success:', values)
    }

    onFinishFailed = errorInfo => {
        console.log('Failed:', errorInfo)
    }

    render() {
        return <Form
            onFinish={this.onFinish}
            onFinishFailed={this.onFinishFailed}>
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