import React from "react"
import EntitiesApi from "../EntitiesApi.js"
import {Table, message, Modal } from "antd"

class EntitySimpleTable extends React.Component {

    state = {
        items: [],
        isModalVisible: true
    }

    handleOk = () => this.setState({ isModalVisible: false })

    handleCancel = () => this.setState({ isModalVisible: false })

    createSimpleColumnsFromObject = object => {
        const cols = Object.keys(object).map(key => {
            const strKey = key.toString()
            const filters = this.props.presenter.filteredColumns
            let modifiedColumn = {
                title: strKey.split(/(?=[A-Z])/).map(s => s.toUpperCase()).join(" "),
                dataIndex: strKey,
                defaultSortOrder: "ascend",
                sortDirections: ["ascend", "descend"],
                sorter: (a, b) => a[key].localeCompare(b[key]),
            }
            if (filters !== undefined && strKey in filters)
                modifiedColumn = {
                    ...modifiedColumn,
                    filters: filters[strKey],
                    onFilter: (v, r) => r[strKey] === v
                }
            return modifiedColumn
        })
        this.setState({ columns: cols })
    }

    getRecords = () => {
        EntitiesApi.get(this.props.presenter.url)
            .then(res => res.json())
            .then(
                data => {
                    this.setState({ items: data })
                    this.createSimpleColumnsFromObject(data[0])
                },
                error => {
                    //TODO: message и закрыть окно
                }
            )
    }

    render() {
        return <Modal
                title="Select a required entity"
                visible={this.state.isModalVisible}
                onOk={this.handleOk}
                onCancel={this.handleCancel}>
            <Table
                rowKey={this.props.presenter.idField}
                columns={this.state.columns}
                dataSource={this.state.items}
            />
        </Modal>
    }
}

export default EntitySimpleTable