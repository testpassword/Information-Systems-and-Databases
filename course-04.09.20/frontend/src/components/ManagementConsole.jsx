import React from "react"
import { Layout, Menu, Modal, Button, Space } from "antd"
import Credits from "./Credits"
import { CalendarOutlined, TeamOutlined, CarOutlined,
    HomeOutlined, GlobalOutlined, MedicineBoxOutlined,
    ShoppingOutlined, MoneyCollectOutlined, CoffeeOutlined,
    DeleteOutlined, PlusOutlined } from "@ant-design/icons"
import EntityTable from "./EntityTable"
import Base from "./models/models.js"

class ManagementConsole extends React.Component {

    state = {
        menuIsCollapsed: false,
        entityTable: <EntityTable/>
    }

    onCollapse = menuIsCollapsed => { this.setState({ menuIsCollapsed }) }

    loadEntityTable = entityTable => { this.setState({entityTable}) }

    info() {
        Modal.info({
            title: "Credits",
            content: <Credits/>
        })
    }

    render() {
        const { menuIsCollapsed } = this.state;
        const { Content, Footer, Sider, Header } = Layout;
        return (
            <Layout style={{ minHeight: '100vh' }} theme="dark">
                <Sider collapsible collapsed={menuIsCollapsed} onCollapse={this.onCollapse}>
                    <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                        <Menu.Item
                            icon={<HomeOutlined/>}
                            onClick={this.loadEntityTable.bind(this, <EntityTable/>)}>
                            Bases
                        </Menu.Item>
                        <Menu.Item
                            icon={<GlobalOutlined/>}>
                            Campaigns
                        </Menu.Item>
                        <Menu.Item
                            icon={<TeamOutlined/>}>
                            Employees
                        </Menu.Item>
                        <Menu.Item
                            icon={<ShoppingOutlined/>}>
                            Equipments
                        </Menu.Item>
                        <Menu.Item
                            icon={<MedicineBoxOutlined/>}>
                            Medical Cards
                        </Menu.Item>
                        <Menu.Item
                            icon={<CalendarOutlined/>}>
                            Missions
                        </Menu.Item>
                        <Menu.Item
                            icon={<CoffeeOutlined/>}>
                            MREs
                        </Menu.Item>
                        <Menu.Item
                            icon={<MoneyCollectOutlined/>}>
                            Positions
                        </Menu.Item>
                        <Menu.Item
                            icon={<CarOutlined/>}>
                            Transports
                        </Menu.Item>
                        <Menu.Item>
                            Weapons
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout className="site-layout">
                    <Header style={{ position: 'fixed', zIndex: 1, width: '100%' }}>
                        <Space size={"middle"}>
                            <Button
                                icon={<PlusOutlined/>}
                                ghost={true}>
                                Add record
                            </Button>
                            <Button
                                icon={<DeleteOutlined/>}
                                ghost={true}
                                danger>
                                Remove
                            </Button>
                        </Space>
                    </Header>
                    <Content style={{ marginTop: "64px" }}>
                        <div className="site-layout-background" style={{ minHeight: 360 }}>
                            {this.state.entityTable}
                        </div>
                    </Content>
                    <Footer style={{ textAlign: "center"}}>
                        <a onClick={ this.info }>Kulbako Artemy ©2020</a>
                    </Footer>
                </Layout>
            </Layout>
        )
    }
}

export default ManagementConsole