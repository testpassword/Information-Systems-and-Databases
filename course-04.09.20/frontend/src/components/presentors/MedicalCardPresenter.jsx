import React from "react"
import {Button, Form, Input, InputNumber, Radio} from "antd";

class MedicalCardCreator extends React.Component {

    render() {
        return <Form>
            <Form.Item
                label="Diseases"
                name="diseases">
                <Input placeholder="Use space as separator"/>
            </Form.Item>
            <Form.Item
                label="Gender"
                name="gender"
                rules={[ { required: true, message: "Choose gender" }]}>
                <Radio.Group>
                    <Radio value={true}>male</Radio>
                    <Radio value={false}>female</Radio>
                </Radio.Group>
            </Form.Item>
            <Form.Item
                label="Height cm"
                name="heightCm"
                rules={[ { required: true, message: "Input employee height" }]}>
                <InputNumber/>
            </Form.Item>
            <Form.Item
                label="Weight kg"
                name="weightKg"
                rules={[ { required: true, message: "Input employee weight" }]}>
                <InputNumber/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
    }
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
    },
    creator: <MedicalCardCreator/>
}

export default MedicalCardPresenter