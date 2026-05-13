<template>
  <div class="resource-selector">
    <div class="selector-header">
      <h3>资源选择</h3>
      <el-button size="small" @click="handleRefresh" :loading="refreshing">
        <i class="el-icon-refresh"></i>
      </el-button>
    </div>
    
    <div class="platform-tabs">
      <el-radio-group v-model="selectedPlatformId" size="small">
        <el-radio-button
          v-for="platform in platforms"
          :key="platform.id"
          :label="platform.id"
        >
          {{ platform.name }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <div class="resource-tree" v-loading="loading">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="false"
        :default-expand-all="false"
        highlight-current
        @node-click="handleNodeClick"
        @node-expand="handleNodeExpand"
        @node-collapse="handleNodeCollapse"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-icon">
              <i :class="getNodeIcon(data.type)"></i>
            </span>
            <span class="node-label">{{ node.label }}</span>
            <span class="node-status" v-if="data.metrics">
              <span class="metric-badge cpu">{{ data.metrics.cpu }}%</span>
            </span>
          </span>
        </template>
      </el-tree>

      <div v-if="!loading && treeData.length === 0" class="empty-state">
        暂无资源数据
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { ElTree } from 'element-plus';
import { ElMessage } from 'element-plus';
import { useResourceTree } from '@/views/large-display/composables/useResourceTree';
import type { ResourceType } from '@/views/large-display/types';

interface Props {
  platforms?: Array<{ id: number; name: string }>;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'select', data: { id: number; name: string; type: ResourceType }): void;
  (e: 'platformChange', platformId: number): void;
}>();

const {
  tree,
  selectedNode,
  loadPlatforms,
  toggleExpand,
  selectNode,
  reset,
} = useResourceTree();

const treeRef = ref<InstanceType<typeof ElTree>>();
const selectedPlatformId = ref<number | null>(null);
const loading = ref(false);
const refreshing = ref(false);

const treeData = computed(() => tree.value);
const treeProps = {
  children: 'children',
  label: 'name',
  disabled: 'disabled',
};

onMounted(async () => {
  await handleRefresh();
});

watch(selectedPlatformId, async (newId) => {
  if (newId) {
    emit('platformChange', newId);
  }
});

async function handleRefresh() {
  try {
    refreshing.value = true;
    await loadPlatforms();
    
    if (props.platforms && props.platforms.length > 0) {
      selectedPlatformId.value = props.platforms[0].id;
    } else if (treeData.value.length > 0) {
      selectedPlatformId.value = treeData.value[0].id;
    }
  } catch (error) {
    ElMessage.error('加载资源失败');
  } finally {
    refreshing.value = false;
  }
}

function handleNodeClick(data: any) {
  selectNode(data.id);
  
  if (data.type === 'HOST' || data.type === 'VM') {
    emit('select', {
      id: data.id,
      name: data.name,
      type: data.type,
    });
  }
}

async function handleNodeExpand(data: any) {
  await toggleExpand(data.id);
}

function handleNodeCollapse(data: any) {
  // Handled by tree state
}

function getNodeIcon(type: ResourceType): string {
  const icons: Record<ResourceType, string> = {
    DESKTOP_POOL: 'el-icon-document',
    TERMINAL: 'el-icon-monitor',
    CLUSTER: 'el-icon-box',
    HOST: 'el-icon-s-platform',
    VM: 'el-icon-s-cooperation',
  };
  return icons[type] || 'el-icon-document';
}

// Expose methods for parent component
defineExpose({
  handleRefresh,
});
</script>

<style scoped>
.resource-selector {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 4px;
}

.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.selector-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.platform-tabs {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.resource-tree {
  flex: 1;
  overflow: auto;
  padding: 8px;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
}

.node-icon {
  margin-right: 8px;
  color: #666;
}

.node-label {
  flex: 1;
}

.node-status {
  margin-left: 8px;
}

.metric-badge {
  display: inline-block;
  padding: 2px 6px;
  font-size: 11px;
  border-radius: 3px;
  color: #fff;
}

.metric-badge.cpu {
  background: #5470c6;
}

.metric-badge.memory {
  background: #73c0de;
}

.empty-state {
  padding: 40px 16px;
  text-align: center;
  color: #999;
}
</style>
