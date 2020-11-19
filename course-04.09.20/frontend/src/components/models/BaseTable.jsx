import React from "react"
import { Table, message, Input, Button, Space } from "antd"
import Highlighter from "react-highlight-words"
import { SearchOutlined } from "@ant-design/icons"

class BaseTable extends React.Component {

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
        confirm();
        this.setState({
            searchText: selectedKeys[0],
            searchedColumn: dataIndex,
        })
    }

    handleReset = clearFilters => {
        clearFilters()
        this.setState({ searchText: '' })
    }

    state = {
        TEMPLATE_URL: "http://localhost:9090/base?=",
        COLUMNS: [
            {
                title: "LOCATION",
                dataIndex: "location",
                key: "location",
                sorter: (a, b) => a.location.localeCompare(b.location),
                defaultSortOrder: "ascend",
                sortDirections: ["ascend", "descend"],
                ...this.getColumnSearchProps("location")
            },
            {
                title: "STATUS",
                dataIndex: "status",
                key: "status",
                defaultSortOrder: "ascend",
                sorter: (a, b) => a.location.localeCompare(b.location),
                sortDirections: ["ascend", "descend"],
                ...this.getColumnSearchProps("status")
            }
        ],
        error: null,
        isLoaded: false,
        bases: [],
        searchText: "",
        searchedColumn: ""
    }

    componentDidMount() {
        /* Это наглядный пример плохого дизайна: изначально когда я писал api для бекенда, я предполагал, что в теле GET
        запроса будет передаваться json-массив с id нужных элементов и пустой массив, если клиент хочет получить все элементы.
        На этапе создания фронтенда я вспомнил, что по спецификации HTTP передавать тело с GET не рекомендуется, а fetch()
        и вовсе это запрещает. Т.к. кардинально менять api бэка не хотелось, решил костыльно передавать json-массив как
        параметр запроса. */
        const url = this.state.TEMPLATE_URL + new URLSearchParams({
            "ids": '{ "selectedIds": [] }'
        })
        const req = {
            method: "GET",
            mode: 'cors'
        }
        fetch(url, req).then(res => res.json()).then(
            data => {
                this.setState({
                    isLoaded: true,
                    bases: data
                })
                message.success({
                    top: 100,
                    content: "Data loaded",
                    marginTop: "20vh"
                })
            },
            error => {
                this.setState({
                    isLoaded: true,
                    error
                })
                message.error({
                    top: 24,
                    content: "Error while loading data",
                    marginTop: "20vh"
                })
            })
    }

    render() {
        const { error, isLoaded, bases } = this.state;
        if (error) return <div>Ошибка: {error.message}</div>
        else if (!isLoaded) return <div>Загрузка...</div>;
        else return <Table dataSource={bases} columns={this.state.COLUMNS}/>
    }
}

export default BaseTable