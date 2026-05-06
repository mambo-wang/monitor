import request from "@/utils/system/request";

const getResourceList = (params: object) =>
    request({
        url: "/resource/list",
        method: "get",
        params
    });

const getResourceDetail = (id: string) =>
    request({
        url: `/resource/detail/${id}`,
        method: "get"
    });

const createResource = (data: object) =>
    request({
        url: "/resource/create",
        method: "post",
        data
    });

const batchCreateResource = (data: object) =>
    request({
        url: "/resource/batchCreate",
        method: "post",
        data
    });

const updateResource = (data: object) =>
    request({
        url: "/resource/update",
        method: "put",
        data
    });

const deleteResource = (id: string) =>
    request({
        url: `/resource/delete/${id}`,
        method: "delete"
    });

const updateUsable = (id: string, usable: number) =>
    request({
        url: `/resource/usable/${id}`,
        method: "put",
        params: { usable }
    });

const updateRemote = (id: string, remote: number) =>
    request({
        url: `/resource/remote/${id}`,
        method: "put",
        params: { remote }
    });

export default {
    getResourceList,
    getResourceDetail,
    createResource,
    batchCreateResource,
    updateResource,
    deleteResource,
    updateUsable,
    updateRemote,
}
