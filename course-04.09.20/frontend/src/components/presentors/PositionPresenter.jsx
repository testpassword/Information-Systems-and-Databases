import React from "react"
import {Button, Form, Input, InputNumber, message, Radio} from "antd"
import BasePresenter from "./BasePresenter"
import AbstractCreator from "./AbstractCreator.jsx"
import EntitiesApi from "../../EntitiesApi";
import EntitySimpleTable from "../EntitySimpleTable"
import EquipmentPresenter from "./EquipmentPresenter"

class PositionCreator extends AbstractCreator {

    state = {
        equipBtn: "select position",
        equipId: null
    }

    equipOk = () => {
        if (EntitiesApi.idBuffer !== null) {
            this.setState({ equipBtn: EntitiesApi.idBuffer })
            EntitiesApi.idBuffer = null
        } else message.error({ content: "You should choose equipment from table" })
    }

    render() {
        return <Form onFinish={this.onTrigger}>
            <Form.Item
                label="Equipment ID"
                name="equipId">
                <Button
                    type="link"
                    onClick={ () => this.showConfirm(<EntitySimpleTable presenter={EquipmentPresenter}/>, this.equipOk) }>
                    {this.state.equipBtn}
                </Button>
                <Input value={this.state.equipId} style={{display: "none"}}/>
            </Form.Item>
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
    url: "position",
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