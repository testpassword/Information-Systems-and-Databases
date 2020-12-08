import React from "react"
import {Button, Form, Input, Radio} from "antd";
import BasePresenter from "./BasePresenter";

class TransportCreator extends React.Component {

    render() {
        return <Form>
            <Form.Item
                label="Name"
                name="name"
                rules={[{ required: true, message: "Input transport name" }]}>
                <Input/>
            </Form.Item>
            <Form.Item
                label="Status"
                name="status"
                rules={[{ required: true, message: "Choose status of transport" }]}>
                <Radio.Group
                    options={BasePresenter.filteredColumns.status.map(o => o.text)}
                    optionType="button"
                />
            </Form.Item>
            <Form.Item
                label="Type"
                name="type"
                rules={[{ required: true, message: "Input type of transport" }]}>
                <Input/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
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
    },
    creator: <TransportCreator/>
}

export default TransportPresenter