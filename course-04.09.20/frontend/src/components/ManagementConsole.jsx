import React from "react"
import { Layout, Menu, Modal, Button } from "antd"
import Credits from "./Credits"
import { CalendarOutlined, TeamOutlined, CarOutlined,
    HomeOutlined, GlobalOutlined, MedicineBoxOutlined,
    ShoppingOutlined, MoneyCollectOutlined, CoffeeOutlined,
    DeleteOutlined, PlusOutlined } from "@ant-design/icons"
import BaseTable from "./models/BaseTable"
import WeaponTable from "./models/WeaponTable"

class ManagementConsole extends React.Component {

    state = {
        menuIsCollapsed: false,
        entityTable: <BaseTable/>
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
                            onClick={this.loadEntityTable.bind(this, <BaseTable/>)}>
                            Bases
                        </Menu.Item>
                        <Menu.Item
                            icon={<GlobalOutlined/>}
                            onClick={this.loadEntityTable.bind(this, <WeaponTable/>)}>
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
                        {/*Сюда PageHEADER и кнопки
                        TODO: убрать выделение меню-итемов*/}
                        <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['2']}>
                            <Menu.Item key="1" icon={<PlusOutlined/>}>
                                Add record
                            </Menu.Item>
                            <Menu.Item key="2" icon={<DeleteOutlined/>}>
                                Delete
                            </Menu.Item>
                        </Menu>
                    </Header>
                    <Content style={{ margin: "40px 0px" }}>
                        <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
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