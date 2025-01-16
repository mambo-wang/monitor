import request from "@/utils/system/request";


const getDeployStatus = () =>
    request({
        url: "/deploy",
        method: "get",
    });

const deploySingleNode = (data: object) =>
    request({
        url: "/deploy/single",
        method: "post",
        data
    });

const deployBatchNode = (data: object) =>
    request({
        url: "/deploy/batch",
        method: "post",
        data
    });

const manageComponent = (data: object) =>
    request({
        url: "/deploy/manage",
        method: "put",
        data
    });

const tenantAuth = (data: object) =>
    request({
        url: "/dataCenter/auth",
        method: "post",
        data
    });

const getTenantConfig = () =>
    request({
        url: "/dataCenter/dataCenterConfig",
        method: "get"
    });

const getWebsocketState = () => request({
    url: "/dataCenter/websocketState",
    method: "get"
});

export default {
    getDeployStatus,
    deploySingleNode,
    deployBatchNode,
    manageComponent,
    tenantAuth,
    getTenantConfig,
    getWebsocketState
}