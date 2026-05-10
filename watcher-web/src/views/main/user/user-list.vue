<template>
  <div class="layout-container">
    <div class="user-list-wrapper" v-loading="loading">
      <div class="page-title"><span>{{ $t("message.user.userListTitle") }}</span></div>
      <div class="user-list">
        <em-table :data="userTableData"
          border
          stripe
          class="user-table"
          :header-cell-style="{backgroundColor: '#ececec', height: '50px'}">
          <el-table-column :label="$t('message.common.index')" type="index" width="80" align="center"></el-table-column>
          <el-table-column :label="$t('message.user.username')" prop="username" show-overflow-tooltip></el-table-column>
        </em-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import emTable from "@/components/table/index.vue";
import { getUserList } from "@/api/user";

const loading = ref<boolean>(false);
const userTableData = reactive<{ username: string }[]>([]);

const loadUserList = () => {
  loading.value = true;
  getUserList()
    .then((res: any) => {
      loading.value = false;
      if (res.data && res.data.length) {
        userTableData.splice(0, userTableData.length,
          ...res.data.map((name: string) => ({ username: name })));
      } else {
        userTableData.splice(0, userTableData.length);
      }
    })
    .catch(() => {
      loading.value = false;
      ElMessage.error({ message: "加载失败", grouping: true });
    });
};

onMounted(() => {
  loadUserList();
});
</script>

<style lang="scss" scoped>
.user-list-wrapper {
  padding: 24px 24px 0 24px;
  .page-title {
    display: flex;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }
  .user-list {
    margin-top: 16px;
    padding: 0 24px;
  }
}
</style>
