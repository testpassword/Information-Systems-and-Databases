import React, { useContext, useState, useEffect, useRef } from "react"
import {Table, message, Input, Button, Space, Tag, Form } from "antd"
import Highlighter from "react-highlight-words"
import { SearchOutlined } from "@ant-design/icons"

const EditableContext = React.createContext()

const EditableRow = ({ index, ...props }) => {
    const [form] = Form.useForm();
    return (
        <Form form={form} component={false}>
            <EditableContext.Provider value={form}>
                <tr {...props} />
            </EditableContext.Provider>
        </Form>
    )
}

const EditableCell = ({ title, editable, children, dataIndex, record, handleSave, ...restProps }) => {
    const [editing, setEditing] = useState(false)
    const inputRef = useRef()
    const form = useContext(EditableContext);
    useEffect(() => {
        if (editing) inputRef.current.focus()
    }, [editing])
    const toggleEdit = () => {
        setEditing(!editing)
        form.setFieldsValue({ [dataIndex]: record[dataIndex] })
    }
    const save = async(e) => {
        try {
            const values = await form.validateFields()
            toggleEdit()
            handleSave({ ...record, ...values })
        } catch (errInfo) { console.log('Save failed:', errInfo) }
    }
    let childNode = children
    if (editable) {
        childNode = editing ? (
            <Form.Item
                style={{ margin: 0 }}
                name={dataIndex}
                rules={[
                    {
                        required: true,
                        message: `${title} is required.`,
                    }
                ]}>
                <Input ref={inputRef} onPressEnter={save} onBlur={save} />
            </Form.Item>
        ) : (
            <div
                className="editable-cell-value-wrap"
                style={{ paddingRight: 24 }}
                onClick={toggleEdit}>
                {children}
            </div>
        )
    }
    return <td {...restProps}>{childNode}</td>
}

class EntityTable extends React.Component {

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

    onSelectChange = selectedRowKeys => this.setState({selectedRowKeys})

    handleSave = (modified_record) => {
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

    state = {
        error: null,
        isLoading: true,
        items: [],
        searchText: "",
        searchedColumn: "",
        selectedRowKeys: [],
        columns: []
    }

    createColumnsFromObject(object) {
        const cols = Object.keys(object).map(key => {
            const strKey = key.toString()
            return {
                title: strKey.toUpperCase(),
                dataIndex: strKey,
                defaultSortOrder: "ascend",
                sortDirections: ["ascend", "descend"],
                editable: true,
                sorter: (a, b) => a[key].localeCompare(b[key]),
                ...this.getColumnSearchProps(strKey)
            }
        })
        this.setState({ columns: cols })
    }

    getData() {
        /* Это наглядный пример плохого дизайна: изначально когда я писал api для бекенда, я предполагал, что в теле GET
        запроса будет передаваться json-массив с id нужных элементов и пустой массив, если клиент хочет получить все элементы.
        На этапе создания фронтенда я вспомнил, что по спецификации HTTP передавать тело с GET не рекомендуется, а fetch()
        и вовсе это запрещает. Т.к. кардинально менять api бэка не хотелось, решил костыльно передавать json-массив как
        параметр запроса. */
        const url = this.props.presenter.url + "?=" + new URLSearchParams({
            "ids": '{ "selectedIds": [] }'
        })
        const req = {
            method: "GET",
            mode: "cors"
        }
        fetch(url, req).then(res => res.json()).then(
            data => {
                this.setState({
                    isLoading: false,
                    items: data
                })
                // TODO: проверка системы, если данных нет
                this.createColumnsFromObject(data[0])
                message.success({
                    top: 100,
                    content: "Data loaded",
                    marginTop: "20vh"
                })
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

    componentDidMount() { this.getData() } // Вызывается лишь раз, при создании компонента

    componentDidUpdate(prevProps) { // Вызывается каждый раз при обновлении props-ов из родителя
        if (this.props.presenter !== prevProps.presenter) this.getData()
    }

    render() {
        const { isLoading, items, selectedRowKeys } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
            selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT]
        }
        const components = {
            body: {
                row: EditableRow,
                cell: EditableCell
            }
        }
        const columns = this.state.columns.map((col) => {
            if (!col.editable) return col
            return {
                ...col,
                onCell: (record) => ({
                    record,
                    editable: col.editable,
                    dataIndex: col.dataIndex,
                    title: col.title,
                    handleSave: this.handleSave,
                })
            }
        })
        return <Table
            components={components}
            rowKey={this.props.presenter.idField}
            rowSelection={rowSelection}
            columns={columns}
            dataSource={items}
            loading={isLoading}
            pagination={{ position: ["bottomCenter"] }}
        />
    }
}

//TODO: освобождение ресурсов

export default EntityTable