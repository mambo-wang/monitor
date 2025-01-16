import request from "@/utils/system/request";


const getResourceList = (params: object) =>
    request({
        url: "/resourceRemote/selectAll",
        method: "get",
        params
    });

const authRole = (data: object) =>
    request({
        url: "/resourceRemote/userAuth",
        method: "post",
        data
    });

const settingSsh = (data: object) =>
    request({
        url: "/resourceRemote/modify",
        method: "post",
        data
    });

export default {
    getResourceList,
    authRole,
    settingSsh,
}
