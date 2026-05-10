<template>
  <div class="layout-container" v-loading="loading">
    <div v-if="detail">
      <!-- 申请信息 -->
      <div class="info-card">
        <div class="card-title">{{ $t("message.user.applicationInfo") }}</div>
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="$t('message.user.username')">
            {{ detail.username }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.user.status')">
            <el-tag :type="statusTagType">
              {{ statusLabel }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.user.remark')" :span="2">
            {{ detail.remark || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.user.submitTime')">
            {{ detail.submitTime }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.user.rejectCount')">
            <span v-if="detail.rejectCount > 0" style="color: #E6A23C;">{{ detail.rejectCount }}</span>
            <span v-else>0</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 拒绝原因（仅已拒绝时显示） -->
      <div v-if="detail.status === 'rejected'" class="info-card">
        <div class="card-title">{{ $t("message.user.rejectReason") }}</div>
        <p>{{ detail.rejectReason || $t('message.user.noReason') }}</p>
      </div>

      <!-- 操作按钮（仅待审批时显示） -->
      <div v-if="detail.status === 'pending'" class="action-bar">
        <el-button type="success" :loading="actionLoading" @click="handleApprove">
          {{ $t("message.user.approve") }}
        </el-button>
        <el-button type="danger" :loading="actionLoading" @click="openRejectDialog">
          {{ $t("message.user.reject") }}
        </el-button>
      </div>

      <!-- 历史申请记录 -->
      <div class="info-card">
        <div class="card-title">{{ $t("message.user.historyRecords") }}</div>
        <el-table :data="detail.historyList" border stripe :header-cell-style="{backgroundColor: '#ececec', height: '40px'}">
          <el-table-column :label="$t('message.user.submitTime')" prop="submitTime"></el-table-column>
          <el-table-column :label="$t('message.user.status')" prop="status">
            <template #default="scope">
              <el-tag :type="getHistoryTagType(scope.row.status)">
                {{ getHistoryStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('message.user.rejectReason')" prop="rejectReason">
            <template #default="scope">
              {{ scope.row.rejectReason || '-' }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('message.user.approveTime')" prop="approveTime">
            <template #default="scope">
              {{ scope.row.approveTime || '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 拒绝弹窗 -->
    <el-dialog v-model="rejectDialogVisible" :title="$t('message.user.rejectDialogTitle')" width="500px">
      <el-form :label-width="'100px'">
        <el-form-item :label="$t('message.user.rejectReason')">
          <el-input v-model="rejectReason" type="textarea" :rows="3"
            :placeholder="$t('message.user.rejectReasonPlaceholder')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">{{ $t("message.common.cancel") }}</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleReject">
          {{ $t("message.common.confirm") }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getRegisterDetail, approveRegister, rejectRegister } from "@/api/user";
import type { RegisterDetailVO } from "@/api/user";

const route = useRoute();
const loading = ref<boolean>(false);
const actionLoading = ref<boolean>(false);
const detail = ref<RegisterDetailVO | null>(null);
const rejectDialogVisible = ref<boolean>(false);
const rejectReason = ref<string>('');

const statusTagType = computed(() => {
  if (detail.value?.status === 'pending') return 'warning';
  if (detail.value?.status === 'approved') return 'success';
  return 'danger';
});

const statusLabel = computed(() => {
  if (detail.value?.status === 'pending') return '待审批';
  if (detail.value?.status === 'approved') return '已通过';
  return '已拒绝';
});

const getHistoryTagType = (status: string) => {
  if (status === 'pending') return 'warning';
  if (status === 'approved') return 'success';
  return 'danger';
};

const getHistoryStatusLabel = (status: string) => {
  if (status === 'pending') return '待审批';
  if (status === 'approved') return '已通过';
  return '已拒绝';
};

const loadDetail = () => {
  const id = route.query.id as string;
  if (!id) return;
  loading.value = true;
  getRegisterDetail(id)
    .then((res: any) => {
      loading.value = false;
      detail.value = res.data;
    })
    .catch(() => {
      loading.value = false;
      ElMessage.error({ message: "加载失败", grouping: true });
    });
};

const handleApprove = () => {
  if (!detail.value) return;
  ElMessageBox.confirm('确定同意该用户的注册申请？', '确认审批')
    .then(() => {
      actionLoading.value = true;
      approveRegister(detail.value!.id)
        .then(() => {
          ElMessage.success({ message: '已同意该申请', grouping: true });
          loadDetail();
        })
        .catch(() => {
          ElMessage.error({ message: '操作失败', grouping: true });
        })
        .finally(() => {
          actionLoading.value = false;
        });
    })
    .catch(() => {});
};

const openRejectDialog = () => {
  rejectReason.value = '';
  rejectDialogVisible.value = true;
};

const handleReject = () => {
  if (!detail.value) return;
  actionLoading.value = true;
  rejectRegister({ id: detail.value.id, rejectReason: rejectReason.value })
    .then(() => {
      ElMessage.success({ message: '已拒绝该申请', grouping: true });
      rejectDialogVisible.value = false;
      loadDetail();
    })
    .catch(() => {
      ElMessage.error({ message: '操作失败', grouping: true });
    })
    .finally(() => {
      actionLoading.value = false;
    });
};

onMounted(() => {
  loadDetail();
});
</script>

<style lang="scss" scoped>
.layout-container {
  padding: 24px;
}
.info-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  .card-title {
    font-weight: 700;
    font-size: 16px;
    color: #333;
    margin-bottom: 16px;
  }
  p {
    color: #666;
    line-height: 1.6;
  }
}
.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}
</style>
