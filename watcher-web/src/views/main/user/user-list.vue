<template>
  <div class="user-list">
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="username" :label="$t('message.user.username') || '用户名'" width="200" />
      <el-table-column prop="status" :label="$t('message.user.status') || '状态'" width="150">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'info'">
            {{ row.status === 'active' ? '已激活' : '未激活' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :label="$t('message.user.createTime') || '创建时间'" width="200">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" :label="$t('message.user.lastLoginTime') || '最后登录时间'" width="200">
        <template #default="{ row }">
          {{ formatDate(row.lastLoginTime) }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('message.common.operation') || '操作'">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">
            {{ $t('message.common.detail') || '详情' }}
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

    <el-dialog v-model="detailVisible" :title="$t('message.user.userDetail') || '用户详情'" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item :label="$t('message.user.username') || '用户名'">
          {{ currentUser.username }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('message.user.status') || '状态'">
          <el-tag :type="currentUser.status === 'active' ? 'success' : 'info'">
            {{ currentUser.status === 'active' ? '已激活' : '未激活' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('message.user.createTime') || '创建时间'">
          {{ formatDate(currentUser.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('message.user.lastLoginTime') || '最后登录时间'">
          {{ formatDate(currentUser.lastLoginTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import {ref, reactive, onMounted} from "vue";
import {getUserListApi} from "@/api/user";
import {ElMessage} from "element-plus";

const tableData = ref([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const detailVisible = ref(false);
const currentUser = ref<any>({});

const loadData = async () => {
  try {
    const res: any = await getUserListApi({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    });
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

const viewDetail = (row: any) => {
  currentUser.value = row;
  detailVisible.value = true;
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
.user-list {
  padding: 20px;
}
</style>
