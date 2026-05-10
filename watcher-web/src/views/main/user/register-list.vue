<template>
  <div class="layout-container">
    <div class="register-list-wrapper" v-loading="loading">
      <div class="page-title"><span>{{ $t("message.user.registerApprovalTitle") }}</span></div>
      <div class="register-list">
        <em-table :data="registerList"
          border
          stripe
          @row-click="goDetail"
          class="register-table"
          :header-cell-style="{backgroundColor: '#ececec', height: '50px'}">
          <el-table-column :label="$t('message.user.username')" prop="username" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('message.user.remark')" prop="remark" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.remark || '-' }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('message.user.rejectCount')" prop="rejectCount" width="120">
            <template #default="scope">
              <span v-if="scope.row.rejectCount > 0" style="color: #E6A23C;">{{ scope.row.rejectCount }}</span>
              <span v-else>0</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('message.user.submitTime')" prop="submitTime" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('message.agent.operation')" width="120">
            <template #default="scope">
              <el-button type="text" @click.stop="goDetail(scope.row)">
                {{ $t("message.user.viewDetail") }}
              </el-button>
            </template>
          </el-table-column>
        </em-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import emTable from "@/components/table/index.vue";
import { getPendingRegisterList } from "@/api/user";
import type { RegisterListItemVO } from "@/api/user";

const router = useRouter();
const loading = ref<boolean>(false);
const registerList = reactive<RegisterListItemVO[]>([]);

const getRegisterListData = () => {
  loading.value = true;
  getPendingRegisterList()
    .then((res: any) => {
      loading.value = false;
      if (res.data && res.data.length) {
        registerList.splice(0, registerList.length, ...res.data);
      } else {
        registerList.splice(0, registerList.length);
      }
    })
    .catch(() => {
      loading.value = false;
      ElMessage.error({ message: "加载失败", grouping: true });
    });
};

const goDetail = (row: RegisterListItemVO) => {
  router.push({ path: "/main/user/register-detail", query: { id: row.id } });
};

onMounted(() => {
  getRegisterListData();
});
</script>

<style lang="scss" scoped>
.register-list-wrapper {
  padding: 24px 24px 0 24px;
  .page-title {
    display: flex;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }
  .register-list {
    margin-top: 16px;
    padding: 0 24px;
  }
}
</style>
