import React from "react"
import {Button, DatePicker, Form, Input, Radio} from "antd"
import moment from "moment"

class EmployeeCreator extends React.Component {

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
                label="Date of birth"
                name="date_of_birth"
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
                name="hiring_birth"
                rules={[{ required: true, message: "Input date of hiring new employee" }]}>
                <DatePicker format={"DD.MM.YYYY"} defaultValue={moment()}/>
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