import React from 'react';
import 'antd/dist/antd.css';
import { Layout, Menu, Breadcrumb } from 'antd';

class ManagementConsole extends React.Component {

    state = {
        menuIsCollapsed: false,
        modalIsVisible: false
    };

    onCollapse = menuIsCollapsed => { this.setState({ menuIsCollapsed }) };
    //TODO: модальное окно и его статус

    render() {
        const { menuIsCollapsed } = this.state;
        const { Header, Content, Footer, Sider } = Layout;
        return (
            <Layout style={{ minHeight: '100vh' }}>
                <Sider collapsible collapsed={menuIsCollapsed} onCollapse={this.onCollapse}>
                    <div className="logo" />
                    <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                        <Menu.Item key="1">Bases</Menu.Item>
                        <Menu.Item key="2">Campaigns</Menu.Item>
                        <Menu.Item key="3">Employees</Menu.Item>
                        <Menu.Item key="4">Equipments</Menu.Item>
                        <Menu.Item key="5">Medical Cards</Menu.Item>
                        <Menu.Item key="6">Missions</Menu.Item>
                        <Menu.Item key="8">MREs</Menu.Item>
                        <Menu.Item key="9">Positions</Menu.Item>
                        <Menu.Item key="10">Transports</Menu.Item>
                        <Menu.Item key="11">Weapons</Menu.Item>
                    </Menu>
                </Sider>
                <Layout className="site-layout">
                    <Header className="site-layout-background" style={{ padding: 0 }}/>
                    <Content style={{ margin: '0 16px' }}>
                        <Breadcrumb style={{ margin: '16px 0' }}>
                            <Breadcrumb.Item>User</Breadcrumb.Item>
                            <Breadcrumb.Item>Bill</Breadcrumb.Item>
                        </Breadcrumb>
                        <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
                            Bill is a cat.
                        </div>
                    </Content>
                    <Footer style={{ textAlign: 'center' }}>Kulbako Artemy ©2020</Footer>
                </Layout>
            </Layout>
        );
    }
}

export default ManagementConsole;