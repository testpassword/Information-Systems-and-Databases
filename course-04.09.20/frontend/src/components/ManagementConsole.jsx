import React from "react"
import { Layout, Menu, Modal, Button, Space } from "antd"
import Credits from "./Credits"
import { CalendarOutlined, TeamOutlined, CarOutlined, HomeOutlined, GlobalOutlined, MedicineBoxOutlined,
    ShoppingOutlined, MoneyCollectOutlined, CoffeeOutlined, DeleteOutlined, PlusOutlined } from "@ant-design/icons"
import EntityTable from "./EntityTable"
import { BasePresenter, CampaignPresenter, EmployeePresenter, EquipmentPresenter, MedicalCardPresenter, MissionPresenter,
    MrePresenter, PositionPresenter, TransportPresenter, WeaponPresenter } from "../presenters/presenters.js"

class ManagementConsole extends React.Component {

    state = {
        menuIsCollapsed: false,
        presenter: BasePresenter
    }

    onCollapse = menuIsCollapsed => { this.setState({ menuIsCollapsed }) }

    changeEntityTable(p) { this.setState({ presenter: p }) }

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
                            onClick={() => this.changeEntityTable(BasePresenter)}>
                            Bases
                        </Menu.Item>
                        <Menu.Item
                            icon={<GlobalOutlined/>}
                            onClick={() => this.changeEntityTable(CampaignPresenter)}>
                            Campaigns
                        </Menu.Item>
                        <Menu.Item
                            icon={<TeamOutlined/>}
                            onClick={() => this.changeEntityTable(EmployeePresenter)}>
                            Employees
                        </Menu.Item>
                        <Menu.Item
                            icon={<ShoppingOutlined/>}
                            onClick={() => this.changeEntityTable(EquipmentPresenter)}>
                            Equipments
                        </Menu.Item>
                        <Menu.Item
                            icon={<MedicineBoxOutlined/>}
                            onClick={() => this.changeEntityTable(MedicalCardPresenter)}>
                            Medical Cards
                        </Menu.Item>
                        <Menu.Item
                            icon={<CalendarOutlined/>}
                            onClick={() => this.changeEntityTable(MissionPresenter)}>
                            Missions
                        </Menu.Item>
                        <Menu.Item
                            icon={<CoffeeOutlined/>}
                            onClick={() => this.changeEntityTable(MrePresenter)}>
                            MREs
                        </Menu.Item>
                        <Menu.Item
                            icon={<MoneyCollectOutlined/>}
                            onClick={() => this.changeEntityTable(PositionPresenter)}>
                            Positions
                        </Menu.Item>
                        <Menu.Item
                            icon={<CarOutlined/>}
                            onClick={() => this.changeEntityTable(TransportPresenter)}>
                            Transports
                        </Menu.Item>
                        <Menu.Item
                            onClick={() => this.changeEntityTable(WeaponPresenter)}>
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
                            <EntityTable presenter={this.state.presenter}/>
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