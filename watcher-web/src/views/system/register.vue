<template>
  <div class="container">
    <div class="header">
      <div class="header-left">
        <div class="headertitle">
          <span>{{ $t("message.system.registerTitle") || '用户注册' }}</span>
        </div>
      </div>
    </div>
    <div class="login-body">
      <div class="left">
        <img class="loginleft" src="@/assets/images/loginleft.png" alt=""/>
      </div>
      <div class="box">
        <div class="welcome">{{ $t("message.system.registerTitle") || '用户注册' }}</div>
        <el-form class="form" @submit.prevent>
          <el-input
              size="large"
              v-model="form.username"
              :placeholder="$t('message.system.username') || '用户名'"
              name="username"
              maxlength="50"
              @keyup.enter.native="submit"
              style="margin-bottom: 16px"
          >
            <template #prepend>
              {{ $t("message.system.username") || '用户名' }}
            </template>
          </el-input>
          <el-input
              size="large"
              v-model="form.password"
              type="password"
              :placeholder="$t('message.system.password')"
              name="password"
              maxlength="50"
              @keyup.enter.native="submit"
              style="margin-bottom: 16px"
          >
            <template #prepend>
              {{ $t("message.system.password") }}
            </template>
          </el-input>
          <el-input
              size="large"
              v-model="form.confirmPassword"
              type="password"
              :placeholder="$t('message.system.confirmPassword') || '确认密码'"
              name="confirmPassword"
              maxlength="50"
              @keyup.enter.native="submit"
          >
            <template #prepend>
              {{ $t("message.system.confirmPassword") || '确认密码' }}
            </template>
          </el-input>
          <el-button
              :loading="form.loading"
              @click="submit"
              style="width: 100%; background-color: #0546ce; color: #ffffff; margin-top: 20px"
              size="medium"
          >
            {{ $t("message.system.register") || '注册' }}
          </el-button>
          <div class="register-link">
            <span @click="goLogin">{{ $t("message.system.hasAccount") || '已有账号？去登录' }}</span>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {reactive} from "vue";
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {registerApi} from "@/api/user";
import i18n from "@/locale";

const {t} = i18n.global;
const router = useRouter();

let form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  loading: false,
});

const checkForm = () => {
  return new Promise((resolve, reject) => {
    if (form.username === "") {
      ElMessage.warning({ message: t("message.common.emptyTip"), type: "warning" });
      return;
    }
    if (form.password === "") {
      ElMessage.warning({ message: t("message.common.emptyTip"), type: "warning" });
      return;
    }
    if (form.confirmPassword === "") {
      ElMessage.warning({ message: t("message.common.emptyTip"), type: "warning" });
      return;
    }
    if (form.password !== form.confirmPassword) {
      ElMessage.warning({ message: t("message.system.twicePasswordNotMatch") || '两次输入的密码不一致', type: "warning" });
      return;
    }
    resolve(true);
  });
};

const submit = async () => {
  await checkForm();
  form.loading = true;
  try {
    const res: any = await registerApi({
      username: form.username,
      password: form.password,
      confirmPassword: form.confirmPassword,
    });
    if (res.state === "SUCCESS") {
      ElMessage.success({ message: '注册成功，请等待审批', type: "success" });
      setTimeout(() => {
        router.push("/login");
      }, 1500);
    } else {
      ElMessage.error({ message: res.failureMessage || '注册失败', type: "error" });
    }
  } catch (error: any) {
    ElMessage.error({ message: error.message || '注册失败', type: "error" });
  } finally {
    form.loading = false;
  }
};

const goLogin = () => {
  router.push("/login");
};
</script>

<style lang="scss" scoped>
.container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #eef0f3;

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
        font-weight: 700;
        color: var(--system-primary-color);
      }
    }
  }

  .welcome {
    font-weight: 700;
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

    .form {
      padding: 0 40px 0 40px;

      :deep(.el-input-group__prepend) {
        background-color: #ffffff;
        border-right-color: #ffffff;
      }

      .el-input {
        margin-bottom: 16px;
      }
    }
  }
}

.register-link {
  margin-top: 16px;
  text-align: center;
  span {
    color: #0546ce;
    cursor: pointer;
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
