import React from "react"
import {Button, DatePicker, Form, Input, Radio} from "antd";

class MissionCreator extends React.Component {

    render() {
        const { RangePicker } = DatePicker
        return <Form>
            <Form.Item
                label="Arrival location"
                name="arrivalLocation">
                <Input placeholder="Use space as separator"/>
            </Form.Item>
            <Form.Item
                label="Departure location"
                name="departureLocation">
                <Input placeholder="Use space as separator"/>
            </Form.Item>
            <Form.Item
                label="Period"
                name="period">
                <RangePicker showTime/>
            </Form.Item>
            <Form.Item
                label="Enemies"
                name="enemies">
                <Input/>
            </Form.Item>
            <Form.Item
                label="Legal status"
                name="legalStatus"
                rules={[ { required: true, message: "Choose legality status of mission" }]}>
                <Radio.Group>
                    <Radio value={true}>legal</Radio>
                    <Radio value={false}>illegal</Radio>
                </Radio.Group>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
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
    },
    creator: <MissionCreator/>
}

export default MissionPresenter