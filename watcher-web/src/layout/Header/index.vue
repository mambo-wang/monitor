<template>
  <header>
    <div class="left-box">
      <!-- 收缩按钮 -->
      <!-- <div class="menu-icon" @click="opendStateChange">
        <i
          class="sfont head-fold"
          :class="isCollapse ? 'system-s-unfold' : 'system-s-fold'"
        ></i>
      </div> -->
      <!-- <Breadcrumb /> -->
    </div>
    <div class="right-box">
      <!-- 快捷功能按钮 -->
      <div class="function-list">
        <!-- <div class="function-list-item hidden-sm-and-down"><Full-screen /></div>
        <div class="function-list-item hidden-sm-and-down"><Word /></div> -->
        <!-- <div class="function-list-item"><SizeChange /></div> -->
        <div v-show="false" class="function-list-item hidden-sm-and-down">
          <Theme/>
        </div>

        <!-- <div class="function-list-item hidden-sm-and-down">
          <el-badge :value="unreadInfo" :max="9" class="item"
            ><Information />
          </el-badge>
        </div> -->

        <!-- <div class="marginleft" />
        <div class="function-list-item hidden-sm-and-down"><Userinfo /></div> -->
      </div>
      <!-- 用户信息 -->

      <div class="user-info">
        <el-dropdown>
          <span class="el-dropdown-link">
            {{ userName }}
            <!-- <i class="sfont system-xiala"></i> -->
            <Userinfo class="user-image"/>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="openModifyPassword" style="padding: 4px 12px;border-bottom: 1px solid #eee;">{{
                  $t("message.system.changePassword")
                }}
              </el-dropdown-item>
              <el-dropdown-item @click="loginOut" style="padding: 4px 12px">{{
                  $t("message.system.loginOut")
                }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <modify-password v-model="modifyPasswordVisible"
                       v-if="modifyPasswordVisible"
                       :password="password"
                       @confirmModifyPassword="confirmModifyPassword"
                       @closeModifyPassword="closeModifyPassword"/>
    </div>
  </header>
</template>

<script lang="ts" setup>
import {defineComponent, computed, reactive, ref, onMounted} from "vue";
import {useStore} from "vuex";
import {useRouter, useRoute} from "vue-router";
import FullScreen from "./functionList/fullscreen.vue";
import Word from "./functionList/word.vue";
import SizeChange from "./functionList/sizeChange.vue";
import Userinfo from "./functionList/headerImage.vue";
import Information from "./functionList/information.vue";
import Theme from "./functionList/theme.vue";
import Breadcrumb from "./Breadcrumb.vue";
import modifyPassword from "./modify-password.vue";
import _ from "lodash";
import util from "@/utils/system/common-util";

const store = useStore();
const router = useRouter();
const route = useRoute();
const layer = reactive({
  show: false,
  showButton: true,
});
const modifyPasswordVisible = ref<boolean>(false);
const password = ref<string>('');
const unreadInfo = ref(4);
const isCollapse = computed(() => store.state.app.isCollapse);
// isCollapse change to hide/show the sidebar
const opendStateChange = () => {
  store.commit("app/isCollapseChange", !isCollapse.value);
};
let userName = ref();
// login out the system
const loginOut = () => {
  store.dispatch("user/loginOut");
};

const showPasswordLayer = () => {
  layer.show = true;
};

onMounted(() => {
  if (
      localStorage.getItem("vuex") !== null &&
      JSON.parse(localStorage.getItem("vuex") || "").user !== undefined &&
      JSON.parse(localStorage.getItem("vuex") || "").user.info !== undefined
  ) {
    userName.value = JSON.parse(
        localStorage.getItem("vuex") || ""
    ).user.info.userName;
  }
});
const openModifyPassword = () => {
  password.value = getPassword();
  modifyPasswordVisible.value = true;
}
const confirmModifyPassword = () => {
  closeModifyPassword();
  loginOut();
}
const closeModifyPassword = () => {
  modifyPasswordVisible.value = false;
}
const getPassword = () => {
  const {user} = JSON.parse(window.localStorage.getItem("vuex"));
  if (user && user.info) {
    return util.decryptBySm4(user.info.password);
  }
};
</script>

<style lang="scss" scoped>
:deep(.el-badge__content.is-fixed) {
  font-size: 12px;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  // background-color: var(--system-header-background);
  padding-right: 22px;
}

.left-box {
  height: 100%;
  display: flex;
  align-items: center;

  .menu-icon {
    width: 60px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 25px;
    font-weight: 100;
    cursor: pointer;
    margin-right: 10px;

    &:hover {
      background-color: var(--system-header-item-hover-color);
    }

    i {
      color: var(--system-header-text-color);
    }
  }
}

.right-box {
  display: flex;
  justify-content: center;
  align-items: center;

  .function-list {
    display: flex;

    .function-list-item {
      width: 32px;
      height: 32px;
      display: flex;
      justify-content: center;
      align-items: center;

      :deep(i) {
        color: var(--system-header-text-color);
      }
    }
  }

  .user-info {
    margin-right: 20px;

    .el-dropdown-link {
      color: var(--system-header-breadcrumb-text-color);

      .user-image {
        width: 32px;
        height: 32px;
      }
    }
  }
}

.head-fold {
  font-size: 20px;
}
</style>
