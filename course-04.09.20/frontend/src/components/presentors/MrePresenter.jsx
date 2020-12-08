import React from "react"
import {Button, Form, Input, InputNumber} from "antd"

class MreCreator extends React.Component {

    render() {
        return <Form>
            <Form.Item
                label="Breakfast"
                name="breakfast"
                rules={[{ required: true, message: "Set breakfast! Hungry solder - dead solder." }]}>
                <Input/>
            </Form.Item>
            <Form.Item
                label="Carbohydrate"
                name="carbohydrate"
                rules={[{ required: true, message: "Set total carbohydrate value in MRE" }]}>
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Dinner"
                name="dinner"
                rules={[{ required: true, message: "Set dinner! Hungry solder - dead solder." }]}>
                <Input/>
            </Form.Item>
            <Form.Item
                label="Fats"
                name="fats"
                rules={[{ required: true, message: "Set total fats value in MRE" }]}>
                <InputNumber min={0}/>
            </Form.Item>
            <Form.Item
                label="Food additives"
                name="foodAdditives">
                <Input placeholder="Use space as separator"/>
            </Form.Item>
            <Form.Item
                label="Kkal"
                name="kkal"
                rules={[{ required: true, message: "Set total fats value in MRE" }]}>
                <InputNumber min={3000}/>
            </Form.Item>
            <Form.Item
                label="Lunch"
                name="lunch"
                rules={[{ required: true, message: "Set lunch! Hungry solder - dead solder." }]}>
                <Input/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
    }
}

const MrePresenter = {
    url: "http://localhost:9090/mre",
    idField: "mreId",
    creator: <MreCreator/>
}

export default MrePresenter