import React from "react"
import {Table, message, Input, Button, Space, Layout, Popover, Checkbox} from "antd"
import Highlighter from "react-highlight-words"
import {DeleteOutlined, PlusOutlined, SearchOutlined} from "@ant-design/icons"
import {Header, Content } from "antd/lib/layout/layout";
import {EditableCell, EditableRow} from "./EditableRow.jsx";

class EntityTable extends React.Component {

    state = {
        error: null,
        isLoading: true,
        items: [],
        searchText: "",
        searchedColumn: "",
        selectedRowKeys: [],
        columns: []
    }

    getColumnSearchProps = dataIndex => ({
        filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
            <div style={{ padding: 8 }}>
                <Input
                    ref={ node => { this.searchInput = node} }
                    placeholder={`Search ${dataIndex}`}
                    value={selectedKeys[0]}
                    onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                    onPressEnter={() => this.handleSearch(selectedKeys, confirm, dataIndex)}
                    style={{ width: 188, marginBottom: 8, display: 'block' }}
                />
                <Space>
                    <Button
                        type="primary"
                        onClick={() => this.handleSearch(selectedKeys, confirm, dataIndex)}
                        icon={<SearchOutlined />}
                        size="small"
                        style={{ width: 90 }}>
                        Search
                    </Button>
                    <Button onClick={() => this.handleReset(clearFilters)} size="small" style={{ width: 90 }}>
                        Reset
                    </Button>
                </Space>
            </div>
        ),
        filterIcon: filtered => <SearchOutlined style={{ color: filtered ? '#1890ff' : undefined }} />,
        onFilter: (value, record) =>
            record[dataIndex] ?
                record[dataIndex].toString().toLowerCase().includes(value.toLowerCase())
                : '',
        onFilterDropdownVisibleChange: visible => { if (visible) setTimeout(() => this.searchInput.select(), 100) },
        render: text =>
            this.state.searchedColumn === dataIndex ? (
                <Highlighter
                    highlightStyle={{ backgroundColor: '#ffc069', padding: 0 }}
                    searchWords={[this.state.searchText]}
                    autoEscape
                    textToHighlight={text ? text.toString() : ''}
                />
            ) : (text)
    })

    handleSearch = (selectedKeys, confirm, dataIndex) => {
        confirm()
        this.setState({
            searchText: selectedKeys[0],
            searchedColumn: dataIndex
        })
    }

    handleReset = clearFilters => {
        clearFilters()
        this.setState({ searchText: "" })
    }

    onSelectChange = selectedRowKeys => { this.setState({selectedRowKeys}) }

    handleSave = modified_record => {
        const p = this.props.presenter
        const items = this.state.items
        const orig_record = items.find((it) => modified_record.baseId === it.baseId)
        Object.keys(orig_record).forEach(key => {
            if (orig_record[key] !== modified_record[key]) {
                const req = {
                    method: "PUT",
                    mode: "cors",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        [p.idField]: modified_record[p.idField],
                        [key]: modified_record[key]
                    })
                }
                this.setState({ isLoading: true })
                fetch(p.url, req).then(res => res.text()).then(
                    data => {
                        this.setState({ isLoading: false })
                        console.log(data)
                        message.success({
                            top: 100,
                            content: data,
                            marginTop: "20vh"
                        })
                        orig_record[key] = modified_record[key]
                        this.setState({ items: items })
                    },
                    error => {
                        this.setState({
                            isLoading: false,
                            error: error
                        })
                        message.error({
                            top: 24,
                            content: error.message.toLocaleString(),
                            marginTop: "20vh"
                        })
                    })
            }
        })
    }

    createColumnsFromObject = object => {
        const cols = Object.keys(object).map(key => {
            const strKey = key.toString()
            const filters = this.props.presenter.filteredColumns
            let modifiedColumn = {
                title: strKey.split(/(?=[A-Z])/).map(s => s.toUpperCase()).join(" "),
                dataIndex: strKey,
                defaultSortOrder: "ascend",
                sortDirections: ["ascend", "descend"],
                editable: (strKey !== this.props.presenter.idField),
                sorter: (a, b) => a[key].localeCompare(b[key]),
            }
            modifiedColumn = (filters !== undefined && strKey in filters) ?
                {
                    ...modifiedColumn,
                    filters: filters[strKey],
                    onFilter: (v, r) => r[strKey] === v
            } :
                {
                    ...this.getColumnSearchProps(strKey),
                    ...modifiedColumn
                }
            if (typeof object[key] === "boolean")
                modifiedColumn = {
                    ...modifiedColumn,
                    render: t => <Checkbox checked={t}/>
                }
            if (modifiedColumn.editable)
                modifiedColumn = {
                    ...modifiedColumn,
                    onCell: (record) => ({
                        record,
                        editable: modifiedColumn.editable,
                        dataIndex: modifiedColumn.dataIndex,
                        title: modifiedColumn.title,
                        handleSave: this.handleSave,
                    })
                }
            return modifiedColumn
        })
        this.setState({ columns: cols })
    }

    getRecords = () => {
        /* Это наглядный пример плохого дизайна: изначально когда я писал api для бекенда, я предполагал, что в теле GET
        запроса будет передаваться json-массив с id нужных элементов и пустой массив, если клиент хочет получить все элементы.
        На этапе создания фронтенда я вспомнил, что по спецификации HTTP передавать тело с GET не рекомендуется, а fetch()
        и вовсе это запрещает. Т.к. кардинально менять api бэка не хотелось, решил костыльно передавать json-массив как
        параметр запроса. */
        const url = this.props.presenter.url + "?=" + new URLSearchParams({
            "ids": '{ "selectedIds": [] }'
        })
        const req = { method: "GET", mode: "cors" }
        fetch(url, req).then(res => res.json()).then(
            data => {
                this.setState({ isLoading: false, items: data })
                if (data.length !== 0) this.createColumnsFromObject(data[0])
                message.success({
                    top: 100,
                    content: "Data loaded",
                    marginTop: "20vh"
                })
            },
            error => {
                this.setState({
                    isLoading: false,
                    error: error })
                message.error({
                    top: 24,
                    content: error.message.toLocaleString(),
                    marginTop: "20vh"
                })
            })
    }

    removeRecords = () => {
        const url = this.props.presenter.url
        const req = {
            method: "DELETE",
            mode: "cors",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ droppedIds: this.state.selectedRowKeys })
        }
        fetch(url, req).then(res => res.text()).then(
            data => {
                message.success({ top: 100, content: data, marginTop: "20vh" })
                const newItems = this.state.items.filter(x => !this.state.selectedRowKeys.includes(x[this.props.presenter.idField]))
                this.setState({ items: newItems })
            },
            error => {
                this.setState({ error: error })
                message.error({ top: 24, content: error.message.toLocaleString(), marginTop: "20vh" })
            })
    }

    addRecord = () => {
        //TODO: взять колонки, убрать ту, где id собрать элемент
    }

    // Вызывается лишь раз, при создании компонента
    componentDidMount() { this.getRecords() }

    // Вызывается каждый раз при обновлении props-ов из родителя
    componentDidUpdate(prevProps) { if (this.props.presenter !== prevProps.presenter) this.getRecords() }

    render() {
        const { isLoading, items, selectedRowKeys } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
            selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT]
        }
        const components = {body: { row: EditableRow, cell: EditableCell }}
        return <Layout className="site-layout">
            <Header style={{ position: 'fixed', zIndex: 1, width: '100%' }}>
                <Space size={"middle"}>
                    <Popover trigger="click" content={<EntityCreator/>}>
                        <Button
                            icon={<PlusOutlined/>}
                            ghost={true}
                            onClick={this.addRecord}>
                            Add record
                        </Button>
                    </Popover>
                    <Button
                        icon={<DeleteOutlined/>}
                        ghost={true}
                        danger
                        onClick={this.removeRecords}>
                        Remove
                    </Button>
                </Space>
            </Header>
            <Content style={{ marginTop: "64px" }}>
                <div className="site-layout-background" style={{ minHeight: 360 }}>
                    <Table
                        components={components}
                        rowKey={this.props.presenter.idField}
                        rowSelection={rowSelection}
                        columns={this.state.columns}
                        dataSource={items}
                        loading={isLoading}
                        pagination={{ position: ["bottomCenter"] }}
                    />
                </div>
            </Content>
        </Layout>
    }
}

class EntityCreator extends React.Component {
    /*Это пример плохого дизайна 2, но уже со стороны фронта*/
    render() {
        return <div>
            <Table
            />
            <Button>Create</Button>
        </div>
    }
}

//TODO: освобождение ресурсов

export default EntityTable