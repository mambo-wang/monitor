import request from "@/utils/system/request";


const getInitStatus = () =>
    request({
        url: "/dataCenter/step",
        method: "get",
    });

const getNetworkCard = () =>
    request({
        url: "/deploy/network",
        method: "get",
    });

const saveNetworkConfig = (data: object) =>
    request({
        url: "/deploy/network",
        method: "post",
        data
    });

const getNetworkConfig = () =>
    request({
        url: "/deploy/network/master",
        method: "get",
    });

const getDhcpIp = (name: string) =>
    request({
        url: `/deploy/network/dhcp/${name}`,
        method: "put",
    });

const getWifi = () =>
    request({
        url: "deploy/network/wifis",
        method: "get",
    });

const getNetworkList = () =>
    request({
        url: "deploy/network/config/nodes",
        method: "get",
    });

const editNetworkConfig = (data: object) =>
    request({
        url: "/deploy/network",
        method: "put",
        data
    });

const editStep = (data: object) =>
    request({
        url: "/deploy/step",
        method: "put",
        data
    });

const getNetworkInfo = (params: object) =>
    request({
        url: "deploy/network/config/node",
        method: "get",
        params
    });

const getRouterList = (params: object) =>
    request({
        url: "deploy/route",
        method: "get",
        params
    });

const addRouter = (data: object) =>
    request({
        url: "deploy/route",
        method: "post",
        data
    });

const getCollectionIp = () =>
    request({
        url: "/deploy",
        method: "get",
    });

const testRouterData = (data: object) =>
    request({
        url: "deploy/route/check",
        method: "post",
        data
    });

const editRouter = (data: object) =>
    request({
        url: "deploy/route",
        method: "put",
        data
    });

const deleteRouter = (data: object) =>
    request({
        url: "deploy/route",
        method: "delete",
        data
    });
const addTest = (data: object) =>
    request({
        url: "deploy/route/add/check ",
        method: "post",
        data
    });
const editTest = (data: object) =>
    request({
        url: "deploy/route/edit/check ",
        method: "post",
        data
    });

export default {
    getInitStatus,
    getNetworkCard,
    saveNetworkConfig,
    getDhcpIp,
    getNetworkConfig,
    getWifi,
    getNetworkList,
    editNetworkConfig,
    editStep,
    getNetworkInfo,
    getRouterList,
    addRouter,
    getCollectionIp,
    testRouterData,
    editRouter,
    deleteRouter,
    addTest,
    editTest,
}
