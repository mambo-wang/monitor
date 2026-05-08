<template>
  <div class="topology-tree-container">
    <div class="tree-header">
      <span class="tree-title">{{ treeTitle }}</span>
      <el-button link @click="refreshTree">
        <el-icon :class="{ 'is-loading': loading }"><Refresh /></el-icon>
      </el-button>
    </div>
    <div class="tree-search">
      <el-input
        v-model="searchText"
        :placeholder="searchPlaceholder"
        clearable
        @input="onSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    <div class="tree-content" v-loading="loading">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="treeProps"
        :node-key="'id'"
        :default-expand-all="false"
        :expand-on-click-node="false"
        :highlight-current="true"
        @node-click="onNodeClick"
        :filter-node-method="filterNode"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-icon">
              <el-icon><component :is="getNodeIcon(data.type)" /></el-icon>
            </span>
            <span class="node-label">{{ node.label }}</span>
            <span class="node-status">
              <span
                class="status-dot"
                :class="getStatusClass(data.status)"
              ></span>
            </span>
          </span>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { ElTree } from "element-plus";
import { Search, Refresh } from "@element-plus/icons-vue";
import dashboardApi, { type ResourceTreeNode } from "@/api/dashboard";

interface Props {
  platform?: string;
  timeRange?: {
    startTime: string;
    endTime: string;
    refreshInterval: number;
  };
}

const props = withDefaults(defineProps<Props>(), {
  platform: "all",
  timeRange: () => ({
    startTime: "",
    endTime: "",
    refreshInterval: 0
  })
});

const emit = defineEmits<{
  select: [node: ResourceTreeNode];
}>();

const { t } = useI18n();

const treeTitle = computed(() => t("message.dashboard.resourceTree"));
const searchPlaceholder = computed(() => t("message.dashboard.searchPlaceholder"));

const treeRef = ref<InstanceType<typeof ElTree>>();
const treeData = ref<any[]>([]);
const loading = ref(false);
const searchText = ref("");

const treeProps = {
  children: "children",
  label: "label",
};

const iconMap: Record<string, string> = {
  cluster: "FolderOpened",
  host: "Box",
  vm: "Cpu",
  domain: "Monitor",
  desktop_pool: "Box",
  storage_pool: "Odometer",
};

const getNodeIcon = (type: string) => {
  return iconMap[type] || "Box";
};

const getStatusClass = (status?: string) => {
  switch (status) {
    case "healthy":
      return "status-healthy";
    case "warning":
      return "status-warning";
    case "error":
      return "status-error";
    default:
      return "status-healthy";
  }
};

const fetchResourceTree = async () => {
  loading.value = true;
  try {
    const res: any = await dashboardApi.getResourceTree();
    if (res.data) {
      treeData.value = transformToTree(res.data);
    }
  } catch (error) {
    console.error("Failed to fetch resource tree:", error);
  } finally {
    loading.value = false;
  }
};

const transformToTree = (data: any[]): any[] => {
  return data.map((item) => ({
    id: item.id || item.resourceId,
    label: item.resourceName || item.label || item.name,
    type: mapResourceType(item),
    platform: item.platform?.toLowerCase() || "cas",
    status: item.status || "healthy",
    children: item.children?.length ? transformToTree(item.children) : undefined,
    parentId: item.parentId
  }));
};

const mapResourceType = (item: any): string => {
  const type = item.type || item.resourceType || "";
  const name = (item.resourceName || item.label || "").toLowerCase();

  if (name.includes("cluster")) return "cluster";
  if (name.includes("host")) return "host";
  if (name.includes("pool")) return "desktop_pool";
  if (name.includes("vm") || name.includes("desktop")) return "vm";
  if (name.includes("domain")) return "domain";

  return type || "host";
};

const filterNode = (value: string, data: any) => {
  if (!value) return true;
  return data.label?.toLowerCase().includes(value.toLowerCase());
};

const onSearch = () => {
  treeRef.value?.filter(searchText.value);
};

const onNodeClick = (data: any) => {
  emit("select", data as ResourceTreeNode);
};

const refreshTree = () => {
  fetchResourceTree();
};

watch(
  () => props.platform,
  () => {
    fetchResourceTree();
  }
);

onMounted(() => {
  fetchResourceTree();
});
</script>

<style lang="scss" scoped>
.topology-tree-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;

  .tree-title {
    font-weight: 600;
    font-size: 14px;
    color: #303133;
  }
}

.tree-search {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.tree-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;

  .node-icon {
    display: flex;
    align-items: center;
    color: #606266;
  }

  .node-label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .node-status {
    display: flex;
    align-items: center;
  }

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;

    &.status-healthy {
      background-color: #67c23a;
    }

    &.status-warning {
      background-color: #e6a23c;
    }

    &.status-error {
      background-color: #f56c6c;
    }
  }
}

:deep(.el-tree-node__content) {
  height: 32px;
}
</style>
