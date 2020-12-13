import React from "react"
import {Button, DatePicker, Form, Input, Radio, Modal, message} from "antd"
import moment from "moment"
import EntitySimpleTable from "../EntitySimpleTable.jsx"
import AbstractCreator from "./AbstractCreator.jsx"
import EntitiesApi from "../../EntitiesApi.js"
import PositionPresenter from "./PositionPresenter"

class EmployeeCreator extends AbstractCreator {

    state = {
        baseBtn: "select base",
        baseId: null,
        posBtn: "select position",
        posId: null
    }

    onOk = () => {
        if (EntitiesApi.idBuffer !== null) {
            this.setState({ btnText: EntitiesApi.idBuffer })
            EntitiesApi.idBuffer = null
        } else message.error({ content: "Nothing is select" })
    }

    onCancel() {}

    render() {
        return <Form onFinish={this.onTrigger}>
            <Form.Item
                label="Base ID"
                name="baseId">
                <Button
                    type="link"
                    onClick={ () => this.showConfirm(
                        <EntitySimpleTable presenter={EmployeePresenter}/>, this.onOk, this.onCancel) }>
                    {this.state.baseBtn}
                </Button>
                <Input value={this.state.baseId} style={{display: "none"}}/>
            </Form.Item>
            <Form.Item
                label="Date of birth"
                name="dateOfBirth"
                rules={[{ required: true, message: "Input date of birth new employee" }]}>
                <DatePicker format={['DD/MM/YYYY', 'DD/MM/YY']}/>
            </Form.Item>
            <Form.Item
                label="Education"
                name="education"
                rules={[{ message: "Input education of new employee" }]}>
                <Input/>
            </Form.Item>
            <Form.Item
                label="Hiring date"
                name="hiringDate"
                rules={[{ required: true, message: "Input date of hiring new employee" }]}>
                <DatePicker format={"DD.MM.YYYY"} defaultValue={ moment() }/>
            </Form.Item>
            <Form.Item
                label="Is married"
                name="isMarried"
                rules={[{ required: true, message: "Choose married status of new employee" }]}>
                <Radio.Group>
                    <Radio value={true}>yes</Radio>
                    <Radio value={false}>no</Radio>
                </Radio.Group>
            </Form.Item>
            <Form.Item
                label="Name"
                name="name"
                rules={[ { required: true, message: "Input employee name" }]}>
                <Input/>
            </Form.Item>
            <Form.Item
                label="Position ID"
                name="posId">
                <Button
                    type="link"
                    onClick={ () => this.showConfirm(
                        <EntitySimpleTable presenter={PositionPresenter}/>, this.onOk, this.onCancel) }>
                    {this.state.posBtn}
                </Button>
                TODO: смена надписи на кнопке
                <Input value={this.state.posId} style={{display: "none"}}/>
            </Form.Item>
            <Form.Item
                label="Surname"
                name="surname"
                rules={[ { required: true, message: "Input employee surname" }]}>
                <Input/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit">Submit</Button>
            </Form.Item>
        </Form>
    }
}

const EmployeePresenter = {
    url: "http://localhost:9090/employee",
    idField: "empId",
    filteredColumns: {
        isMarried: [
            { text: "married", value: true },
            { text: "single", value: false }
        ]
    },
    creator: <EmployeeCreator/>
}

export default EmployeePresenter