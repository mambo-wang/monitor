package com.virtual.cloud.om.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class RpcPagingLoadResult<D> extends RpcListLoadResult<D> implements PagingLoadResult<D> {


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "请求的offset值", example = "20")
    private long offset = 0;

    @ApiModelProperty(value = "所有数据总长度", example = "563")
    private long totalLength = 0;

    @ApiModelProperty(value = "总页数", example = "1")
    private int totalPages = 0;

    protected RpcPagingLoadResult() {
    }


    public RpcPagingLoadResult(List<D> data) {
        super(data);
    }


    public RpcPagingLoadResult(List<D> data, long offset, long totalLength) {
        super(data);
        this.offset = offset;
        this.totalLength = totalLength;
    }


    public RpcPagingLoadResult(List<D> data, String successMessage) {
        super(data, successMessage);
    }


    public RpcPagingLoadResult(List<D> data, int offset, int totalLength, String successMessage) {
        super(data, successMessage);
        this.offset = offset;
        this.totalLength = totalLength;
    }

    public RpcPagingLoadResult(int errorCode, String failureMessage) {
        super(errorCode, failureMessage);
    }

    public int getOffset() {
        return (int) offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getTotalPages() {
        return this.totalPages;
    }

    public int getTotalLength() {
        return (int) totalLength;
    }

    @Override
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }
}
