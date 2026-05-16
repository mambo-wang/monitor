<template>
  <div class="container">
    <div class="header">
      <div class="header-left">
        <div class="headertitle">
          <span v-if="!isCollapse">{{ $t("message.system.systemTitle") }}</span>
        </div>
      </div>
    </div>
    <div class="register-body">
      <div class="left">
        <img class="loginleft" src="@/assets/images/loginleft.png" alt=""/>
      </div>
      <div class="box">
        <div class="welcome">{{ $t("message.system.registerTitle") }}</div>
        <el-form class="form" @submit.prevent>
          <el-input
              size="large"
              v-model="form.username"
              :placeholder="$t('message.system.username')"
              name="username"
              maxlength="50"
              @keyup.enter.native="submit"
          >
            <template #prepend>
              {{ $t("message.system.username") }}
            </template>
          </el-input>
          <el-input
              size="large"
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
          <el-input
              size="large"
              v-model="form.confirmPassword"
              :type="passwordType"
              :placeholder="$t('message.system.confirmPassword')"
              name="confirmPassword"
              maxlength="50"
              @keyup.enter.native="submit"
          >
            <template #prepend>
              {{ $t("message.system.confirmPassword") }}
            </template>
          </el-input>
          <el-button
              :loading="form.loading"
              @click="submit"
              style="width: 100%; background-color: #0546ce; color: #ffffff"
              size="medium"
          >
            {{ $t("message.system.submitRegister") }}
          </el-button>
          <div class="back-to-login">
            <span @click="goToLogin">{{ $t("message.system.backToLogin") }}</span>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {defineComponent, ref, reactive} from "vue";
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import i18n from "@/locale";

const {t} = i18n.global;

const router = useRouter();
const isCollapse = ref(false);
const passwordType = ref("password");

let form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  loading: false,
});

const passwordTypeChange = () => {
  passwordType.value = passwordType.value === "" ? "password" : "";
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
    if (form.confirmPassword === "") {
      ElMessage.warning({
        message: t("message.common.emptyTip"),
        type: "warning",
      });
      return;
    }
    if (form.password !== form.confirmPassword) {
      ElMessage.warning({
        message: t("message.system.passwordNotMatch"),
        type: "warning",
      });
      return;
    }
    resolve(true);
  });
};

const goToLogin = () => {
  router.push("/login");
};

const submit = () => {
  checkForm().then(() => {
    form.loading = true;
    // 模拟注册 API 调用
    setTimeout(() => {
      ElMessage.success({
        message: t("message.system.registerSuccess"),
        type: "success",
      });
      form.loading = false;
    }, 1000);
  });
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

  .register-body {
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
  }

  .loginleft {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }

  .box {
    top: 162px;
    float: left;
    left: 200px;
    position: relative;
    height: 460px;
    width: 440px;
    border-radius: 0px;

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

    .back-to-login {
      text-align: center;
      margin-top: 16px;

      span {
        color: #409eff;
        cursor: pointer;
        font-size: 14px;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>