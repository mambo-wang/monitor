import request from "@/utils/system/request";

const checkFile = (params: object) =>
  request({
    url: "/file/upload",
    method: "get",
    params,
  });

const uploadFile = (data: object) =>
  request({
    url: "/file/upload",
    method: "post",
    data,
  });

export default {
  checkFile,
  uploadFile,
};
