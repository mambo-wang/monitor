<template>
  <div class="approval-list">
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane :label="$t('message.approval.pending') || '待审批'" name="pending" />
      <el-tab-pane :label="$t('message.approval.all') || '全部'" name="all" />
    </el-tabs>

    <el-table :data="tableData" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="username" :label="$t('message.user.username') || '用户名'" width="150" />
      <el-table-column prop="status" :label="$t('message.user.status') || '状态'" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.statusDesc || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" :label="$t('message.approval.submitTime') || '申请时间'" width="180">
        <template #default="{ row }">
          {{ formatDate(row.submitTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="approver" :label="$t('message.approval.approver') || '审批人'" width="120">
        <template #default="{ row }">
          {{ row.approver || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="approveTime" :label="$t('message.approval.approveTime') || '审批时间'" width="180">
        <template #default="{ row }">
          {{ formatDate(row.approveTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="rejectReason" :label="$t('message.approval.rejectReason') || '拒绝原因'">
        <template #default="{ row }">
          {{ row.rejectReason || '-' }}
        </template>
      </el-table-column>
      <el-table-column v-if="activeTab === 'pending'" :label="$t('message.common.operation') || '操作'" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="success" size="small" @click="handleApprove(row)">
            {{ $t('message.approval.approve') || '同意' }}
          </el-button>
          <el-button type="danger" size="small" @click="handleReject(row)">
            {{ $t('message.approval.reject') || '拒绝' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px"
    />

    <el-dialog v-model="rejectVisible" :title="$t('message.approval.rejectTitle') || '拒绝原因'" width="500px">
      <el-form>
        <el-form-item :label="$t('message.approval.rejectReason') || '拒绝原因'" required>
          <el-input
              v-model="rejectForm.rejectReason"
              type="textarea"
              :rows="3"
              :placeholder="$t('message.approval.rejectReasonPlaceholder') || '请输入拒绝原因'"
          />
        </el-form-item>
        <el-form-item :label="$t('message.approval.remark') || '备注'">
          <el-input
              v-model="rejectForm.remark"
              type="textarea"
              :rows="2"
              :placeholder="$t('message.approval.remarkPlaceholder') || '请输入备注（可选）'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="primary" @click="confirmReject">{{ $t('message.approval.confirmReject') || '确认拒绝' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import {ref, reactive, onMounted} from "vue";
import {getPendingListApi, getRegistrationListApi, approveRegistrationApi, rejectRegistrationApi} from "@/api/user";
import {ElMessage, ElMessageBox} from "element-plus";

const activeTab = ref('pending');
const tableData = ref([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const rejectVisible = ref(false);
const currentRow = ref<any>({});
const rejectForm = reactive({
  rejectReason: '',
  remark: '',
});

const getStatusType = (status: string) => {
  switch (status) {
    case 'pending': return 'warning';
    case 'approved': return 'success';
    case 'rejected': return 'danger';
    default: return 'info';
  }
};

const loadData = async () => {
  try {
    const res: any = activeTab.value === 'pending'
        ? await getPendingListApi({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
        : await getRegistrationListApi({ pageNum: pagination.pageNum, pageSize: pagination.pageSize });
    if (res.state === "SUCCESS") {
      tableData.value = res.data.records || [];
      pagination.total = res.data.total || 0;
    }
  } catch (error: any) {
    ElMessage.error({ message: error.message || '加载失败', type: "error" });
  }
};

const formatDate = (date: string) => {
  if (!date) return '-';
  return new Date(date).toLocaleString();
};

const handleApprove = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要通过该注册申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    const res: any = await approveRegistrationApi(row.id);
    if (res.state === "SUCCESS") {
      ElMessage.success({ message: '审批通过', type: "success" });
      loadData();
    } else {
      ElMessage.error({ message: res.failureMessage || '操作失败', type: "error" });
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error({ message: error.message || '操作失败', type: "error" });
    }
  }
};

const handleReject = (row: any) => {
  currentRow.value = row;
  rejectForm.rejectReason = '';
  rejectForm.remark = '';
  rejectVisible.value = true;
};

const confirmReject = async () => {
  if (!rejectForm.rejectReason.trim()) {
    ElMessage.warning({ message: '请输入拒绝原因', type: "warning" });
    return;
  }
  try {
    const res: any = await rejectRegistrationApi({
      id: currentRow.value.id,
      rejectReason: rejectForm.rejectReason,
      remark: rejectForm.remark,
    });
    if (res.state === "SUCCESS") {
      ElMessage.success({ message: '已拒绝', type: "success" });
      rejectVisible.value = false;
      loadData();
    } else {
      ElMessage.error({ message: res.failureMessage || '操作失败', type: "error" });
    }
  } catch (error: any) {
    ElMessage.error({ message: error.message || '操作失败', type: "error" });
  }
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
.approval-list {
  padding: 20px;
}
</style>
