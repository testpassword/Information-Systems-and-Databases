import React from "react"
import {Button, Form, Input, InputNumber, Radio} from "antd"
import BasePresenter from "./BasePresenter";

class PositionCreator extends React.Component {

    render() {
        return <Form>
            <Form.Item
                label="Forces"
                name="forces">
                <Radio.Group
                    options={BasePresenter.filteredColumns.status.map(o => o.text)}
                    optionType="button"
                />
            </Form.Item>
            <Form.Item
                label="name"
                name="name"
                rules={[{ required: true, message: "Input position name" }]}>
                <Input/>
            </Form.Item>
            <Form.Item
                label="Rank"
                name="rank">
                <Input/>
            </Form.Item>
            <Form.Item
                label="Salary"
                name="salary"
                rules={[{ required: true, message: "Set employee salary - it should be greater then three hundred bucks" }]}>
                <InputNumber min={300}/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
    }
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
    },
    creator: <PositionCreator/>
}

export default PositionPresenter