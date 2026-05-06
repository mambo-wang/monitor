<template>
  <div class="container">
    <div class="header">
      <div class="header-left">
        <!-- <img class="logo" src="@/assets/images/logo.png" /> -->
        <div class="headertitle">
          <span v-if="!isCollapse">{{ $t("message.system.systemTitle") }}</span>
        </div>
      </div>
    </div>
    <div class="login-body">
      <div class="left">
        <img class="loginleft" src="@/assets/images/loginleft.png" alt=""/>
      </div>
      <div class="box">
        <div class="welcome">{{ $t("message.system.adminPassword") }}</div>
        <el-form class="form" @submit.prevent>
          <el-input
              size="large"
              ref="password"
              v-model="form.password"
              :type="passwordType"
              :placeholder="$t('message.system.password')"
              name="password"
              maxlength="50"
              @keyup.enter.native="submit"
          >
            <template #prepend>
              {{ $t("message.system.password") }}
            </template>
            <template #append>
              <i
                  class="sfont password-icon"
                  :class="passwordType ? 'system-yanjing-guan' : 'system-yanjing'"
                  @click="passwordTypeChange"
              ></i>
            </template>
          </el-input>
          <el-button
              :loading="form.loading"
              @click="submit"
              style="width: 100%; background-color: #0546ce; color: #ffffff"
              size="medium"
          >
            {{ $t("message.system.login") }}
          </el-button>
          <div class="login-password-tip">
            {{$t("message.system.loginPasswordTip")}}
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {defineComponent, ref, reactive, onMounted} from "vue";
import {useStore} from "vuex";
import {useRouter, useRoute} from "vue-router";
import type {RouteLocationRaw} from "vue-router";
import {getAuthRoutes} from "@/router/permission";
import {ElMessage} from "element-plus";
import util from "@/utils/system/common-util";
import i18n from "@/locale";
import _ from "lodash";

const {t} = i18n.global;

const store = useStore();
const router = useRouter();
const route = useRoute();
let form = reactive({
  username: "admin",
  password: "",
  loading: false,
});
let checked = reactive<{ [key: string]: any }>({list: []});
const passwordType = ref("password");
onMounted(() => {
  getCookie();
});

const getCookie = () => {
  if (document.cookie.length > 0) {
    const arr = document.cookie.split("; "); // 这里显示的格式需要切割一下自己可输出看下

    let cookieName =
        _.find(arr, function (item) {
          return item.includes("username");
        }) || "";
    let cookiePwd =
        _.find(arr, function (item) {
          return item.includes("userpassword");
        }) || "";
    const arrName = cookieName.split("username=")[1]; // 再次切割
    const arrPassword = cookiePwd.split("userpassword=")[1]; // 再次切割
    // 判断查找相对应的值

    // form.username = arrName; // 保存到保存数据的地方
    // form.password = util.decryptBySm4(arrPassword);
    // checked.list.push("true");
  }
};

// 设置cookie
const setCookie = (username: any, password: any, day: any) => {
  const expiration = new Date(); // 获取时间
  expiration.setTime(expiration.getTime() + 24 * 60 * 60 * 1000 * day); // 保存的天数
  // 字符串拼接cookie
  window.document.cookie =
      "username" + "=" + username + ";path=/;expires=" + expiration.toUTCString();
  window.document.cookie =
      "userpassword" + "=" + password + ";path=/;expires=" + expiration.toUTCString();
};
const passwordTypeChange = () => {
  passwordType.value === ""
      ? (passwordType.value = "password")
      : (passwordType.value = "");
};
const checkForm = () => {
  return new Promise((resolve, reject) => {
    if (form.username === "") {
      ElMessage.warning({
        message: t("message.common.emptyTip"),
        type: "warning",
      });
      return;
    }
    if (form.password === "") {
      ElMessage.warning({
        message: t("message.common.emptyTip"),
        type: "warning",
      });
      return;
    }
    resolve(true);
  });
};
const submit = () => {
  checkForm().then(() => {
    form.loading = true;
    // if (checked.list[0] == "true") {
    //   // 传入账号名，密码，和保存天数3个参数
    //   setCookie(form.username, util.encryptBySm4(form.password), 7);
    // } else {
    //   // 如果没有选中自动登录，那就清除cookie
    //   setCookie("", "", -1); // 修改2值都为空，天数为负1天就好了
    // }
    let params = {
      username: form.username,
      password: form.password,
    };
    store
        .dispatch("user/login", params)
        .then(async () => {
          setCookie("", "", -1);
          ElMessage.success({
            message: t("message.system.loginSuccess"),
            type: "success",
            showClose: true,
            duration: 1000,
          });
          location.reload();
        })
        .finally(() => {
          form.loading = false;
        });
  });
};

const forgetPassword = () => {
  form.password = "";
  checked.list = [];
  setCookie("", "", -1);
};
</script>

<style lang="scss" scoped>
.container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #eef0f3;
  -moz-background-size: 100% 100%;
  background-size: 100% 100%;

  .header {
    position: relative;
    width: 100vw;
    height: 50px;
    box-shadow: none;

    .header-left {
      position: relative;
      height: 32px;
      left: 16px;
      top: 9px;

      .logo {
        position: relative;
        width: 32px;
        height: 32px;
        float: left;
      }

      .headertitle {
        position: relative;
        float: left;
        height: 24px;
        left: 32px;
        top: 32px;
        font-family: Roboto;
        font-size: 32px;
        font-style: normal;
        font-weight: 700;
        line-height: 24px;
        letter-spacing: 0.005em;
        text-align: left;
        color: var(--system-primary-color);
      }
    }
  }

  .welcome {
    font-weight: 700;
    font-style: normal;
    font-size: 18px;
    color: #333333;
    margin-top: 20px;
    text-align: left;
    line-height: 32px;
    padding: 68px 0 12px 40px;
  }

  .login-body {
    position: relative;
    height: calc(100vh - 50px);
    width: 50%;
    min-width: 880px;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }

  .left {
    float: left;
    position: relative;
    height: 460px;
    width: 440px;
    top: 162px;
    border-radius: 0px;
    // background-color: #ffffff;
  }

  .loginleft {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }

  .el-checkbox-group {
    float: left;
  }

  .forgetPassword {
    float: right;
    color: #000000;
    cursor: pointer;
  }

  .el-form-item--mini.el-form-item {
    margin-bottom: 28px;
  }

  :deep(.el-checkbox__label) {
    color: #000000;
  }

  .box {
    top: 162px;
    float: left;
    left: 200px;
    position: relative;
    height: 460px;
    width: 440px;

    border-radius: 0px;
    // background-color: #ffffff;

    h1 {
      margin-top: 80px;
      text-align: center;
    }

    .form {
      padding: 0 40px 0 40px;

      :deep(.el-input-group__prepend) {
        background-color: #ffffff;
        border-right-color: #ffffff;
      }

      :deep(.el-input-group__append) {
        background-color: #ffffff;
      }

      .el-input {
        margin-bottom: 28px;
      }

      .password-icon {
        cursor: pointer;
        color: #409eff;
      }
    }

    .fixed-top-right {
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }
}

@media screen and (max-width: 750px) {
  .container .box {
    width: 100vw;
    height: 100vh;
    box-shadow: none;
    left: 0;
    top: 0;
    transform: none;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: #e5e5e5;

    h1 {
      margin-top: 0;
    }

    .form {
    }
  }
}
.login-password-tip{
  display: flex;
  margin-top: 32px;
  color: #7F7F7F;
  font-size: 14px;
}
</style>
