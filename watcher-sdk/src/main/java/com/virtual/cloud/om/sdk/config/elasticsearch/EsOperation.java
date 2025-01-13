package com.virtual.cloud.om.sdk.config.elasticsearch;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 操作ES索引
 *
 */
@Slf4j
@Service
public class EsOperation {
 
    @Resource
    private RestHighLevelClient restHighLevelClient;
    private final RequestOptions options = RequestOptions.DEFAULT;
 
    /**
     * 判断索引是否存在
     */
    public boolean checkIndex(String index) {
        try {
            return restHighLevelClient.indices().exists(new GetIndexRequest(index), options);
        } catch (Exception e) {
            log.error("EsIndexOperation checkIndex error.", e);
        }
        return Boolean.FALSE;
    }
 
    /**
     * 创建索引
     *
     * @param indexName         es索引名
     * @param esSettingFilePath es索引的alias、settings和mapping的配置文件
     */
    public boolean createIndex(String indexName, String esSettingFilePath) {
        String aliases = null;
        String mappings = null;
        String settings = null;
        if (StringUtils.isNotBlank(esSettingFilePath)) {
            try {
                String fileContent = FileUtil.readFileContent(esSettingFilePath);
                if (StringUtils.isNotBlank(fileContent)) {
                    JSONObject jsonObject = JSONUtil.parseObj(fileContent);
                    aliases = jsonObject.getStr("aliases");
                    mappings = jsonObject.getStr("mappings");
                    settings = jsonObject.getStr("settings");
                }
            } catch (Exception e) {
                log.error("createIndex error.", e);
                return false;
            }
        }
 
        if (checkIndex(indexName)) {
            log.error("createIndex indexName:[{}]已存在", indexName);
            return false;
        }
 
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        if ((StringUtils.isNotBlank(aliases))) {
            request.aliases(aliases, XContentType.JSON);
        }
 
        if (StringUtils.isNotBlank(mappings)) {
            request.mapping(mappings, XContentType.JSON);
        }
 
        if (StringUtils.isNotBlank(settings)) {
            request.settings(settings, XContentType.JSON);
        }
 
        try {
            this.restHighLevelClient.indices().create(request, options);
            return true;
        } catch (IOException e) {
            log.error("EsIndexOperation createIndex error.", e);
            return false;
        }
    }
 
    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) {
        try {
            if (checkIndex(indexName)) {
                DeleteIndexRequest request = new DeleteIndexRequest(indexName);
                AcknowledgedResponse response = restHighLevelClient.indices().delete(request, options);
                return response.isAcknowledged();
            }
        } catch (Exception e) {
            log.error("EsIndexOperation deleteIndex error.", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 查询总数
     */
    public Long count(String indexName) {
        CountRequest countRequest = new CountRequest(indexName);
        try {
            CountResponse countResponse = restHighLevelClient.count(countRequest, options);
            return countResponse.getCount();
        } catch (Exception e) {
            log.error("EsQueryOperation count error.", e);
        }
        return 0L;
    }

    /**
     * 查询数据集
     */
    public List<Map<String, Object>> list(String indexName, SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResp = restHighLevelClient.search(searchRequest, options);
            List<Map<String, Object>> data = new ArrayList<>();
            SearchHit[] searchHitArr = searchResp.getHits().getHits();
            for (SearchHit searchHit : searchHitArr) {
                Map<String, Object> temp = searchHit.getSourceAsMap();
                temp.put("id", searchHit.getId());
                data.add(temp);
            }
            return data;
        } catch (Exception e) {
            log.error("EsQueryOperation list error.", e);
        }
        return null;
    }

    /**
     * 查询数据集
     */
    public List<String> listString(String indexName, SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResp = restHighLevelClient.search(searchRequest, options);
            List<String> data = new ArrayList<>();
            SearchHit[] searchHitArr = searchResp.getHits().getHits();
            for (SearchHit searchHit : searchHitArr) {
                String temp = searchHit.getSourceAsString();
                data.add(temp);
            }
            return data;
        } catch (Exception e) {
            log.error("EsQueryOperation list error.", e);
        }
        return null;
    }

    /**
     * 写入数据
     */
    public boolean insertWithoutId(String indexName, Map<String, String> dataMap) {
        try {
            BulkRequest request = new BulkRequest();
            request.add(new IndexRequest(indexName).opType("create")
                    .source(dataMap, XContentType.JSON));
            this.restHighLevelClient.bulk(request, options);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("EsDataOperation insert error.", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 写入数据
     */
    public boolean insert(String indexName, Map<String, Object> dataMap) {
        try {
            BulkRequest request = new BulkRequest();
            request.add(new IndexRequest(indexName).opType("create")
                    .id(dataMap.get("id").toString())
                    .source(dataMap, XContentType.JSON));
            this.restHighLevelClient.bulk(request, options);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("EsDataOperation insert error.", e);
        }
        return Boolean.FALSE;
    }

    public boolean insert(String indexName, String data) {
        try {
            BulkRequest request = new BulkRequest();
            request.add(new IndexRequest(indexName).opType("create")
                    .source(data, XContentType.JSON));
            this.restHighLevelClient.bulk(request, options);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("EsDataOperation insert error.", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 批量写入数据
     */
    public boolean batchInsert(String indexName, List<Map<String, Object>> userIndexList) {
        try {
            BulkRequest request = new BulkRequest();
            for (Map<String, Object> dataMap : userIndexList) {
                request.add(new IndexRequest(indexName).opType("create")
                        .id(dataMap.get("id").toString())
                        .source(dataMap, XContentType.JSON));
            }
            this.restHighLevelClient.bulk(request, options);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("EsDataOperation batchInsert error.", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 根据id更新数据，可以直接修改索引结构
     *
     * @param refreshPolicy 数据刷新策略
     */
    public boolean update(String indexName, Map<String, Object> dataMap, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, dataMap.get("id").toString());
            updateRequest.setRefreshPolicy(refreshPolicy);
            updateRequest.doc(dataMap);
            this.restHighLevelClient.update(updateRequest, options);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("EsDataOperation update error.", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 删除数据
     */
    public boolean delete(String indexName, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
            this.restHighLevelClient.delete(deleteRequest, options);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("EsDataOperation delete error.", e);
        }
        return Boolean.FALSE;
    }

}