<template>
  <div class="metric-detail-wrapper">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack">{{ $t('message.common.back') }}</el-button>
        <span class="page-title">{{ $t('message.metric.metricDetail') }} - {{ resourceName }}</span>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="loadData" :loading="loading">
          {{ $t('message.agent.refresh') }}
        </el-button>
      </div>
    </div>

    <div class="content-wrapper" v-loading="loading">
      <!-- 资源信息 -->
      <el-card class="info-card">
        <template #header>
          <span>{{ $t('message.metric.resourceInfo') }}</span>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item :label="$t('message.resource.ipAddress')">
            {{ resourceInfo.ipAddress || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.resource.platform')">
            {{ resourceInfo.platform || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="ID">
            {{ resourceId }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- cluster_basic 指标数据 -->
      <el-card class="data-card">
        <template #header>
          <span>cluster_basic 指标数据</span>
        </template>
        
        <el-table :data="tableData" border stripe max-height="500">
          <el-table-column prop="metricName" label="指标名称" width="200" />
          <el-table-column label="指标值" min-width="300">
            <template #default="{ row }">
              <pre v-if="formatMetricValue(row.metricValue)" class="metric-value-json">{{ formatMetricValue(row.metricValue) }}</pre>
              <span v-else>{{ row.metricValue || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="metricUnit" label="单位" width="100" />
          <el-table-column prop="reportTime" label="上报时间" width="180" />
        </el-table>
        
        <el-empty v-if="tableData.length === 0 && !loading" description="暂无数据" />
        
        <div class="pagination-wrapper" v-if="totalCount > 0">
          <el-pagination
            @current-change="handlePageChange"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="totalCount"
            layout="total, prev, pager, next"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import request from "@/utils/system/request";

const route = useRoute();
const router = useRouter();

const resourceId = ref(route.query.id as string);
const resourceName = ref(route.query.name as string || "Unknown");
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(50);
const totalCount = ref(0);

const resourceInfo = reactive({
  ipAddress: "",
  platform: "",
});

const tableData = ref<any[]>([]);

const formatMetricValue = (value: any): string => {
  if (value === null || value === undefined) return '';
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2);
  }
  return '';
};

const goBack = () => {
  router.push({ name: "resource-index" });
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadData();
};

const loadResourceInfo = async () => {
  try {
    const res = await request({
      url: `/resource/detail/${resourceId.value}`,
      method: "get",
    });
    if (res.successMessage || res.data) {
      Object.assign(resourceInfo, res.data || {});
    }
  } catch (error) {
    console.error("Failed to load resource info:", error);
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await request({
      url: "/metric/list",
      method: "get",
      params: {
        resourceId: resourceId.value,
        metricType: "cluster_basic",
        page: currentPage.value - 1,
        size: pageSize.value,
      },
    });
    
    if (res.successMessage || res.data) {
      tableData.value = res.data || [];
      totalCount.value = res.totalLength || 0;
    } else {
      tableData.value = [];
      totalCount.value = 0;
    }
  } catch (error) {
    console.error("Failed to load metric data:", error);
    tableData.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadResourceInfo();
  loadData();
});
</script>

<style lang="scss" scoped>
.metric-detail-wrapper {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .page-title {
        font-size: 18px;
        font-weight: 600;
      }
    }
  }

  .content-wrapper {
    .info-card,
    .data-card {
      margin-bottom: 20px;
    }

    .pagination-wrapper {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .metric-value-json {
    margin: 0;
    font-size: 12px;
    line-height: 1.5;
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style>
