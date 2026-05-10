<template>
  <div class="container">
    <div class="header">
      <div class="header-left">
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
        <div class="welcome">{{ $t("message.system.registerTitle") }}</div>
        <el-form class="form" @submit.prevent>
          <el-input
            size="large"
            v-model="form.username"
            placeholder="请输入用户名"
            maxlength="50"
          >
            <template #prepend>
              用户名
            </template>
          </el-input>
          <el-input
            size="large"
            v-model="form.password"
            :type="passwordType"
            placeholder="请输入密码"
            maxlength="50"
            style="margin-top: 16px"
          >
            <template #prepend>
              密码
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
            placeholder="请再次输入密码"
            maxlength="50"
            style="margin-top: 16px"
            @keyup.enter.native="submit"
          >
            <template #prepend>
              确认密码
            </template>
          </el-input>
          <el-input
            size="large"
            v-model="form.remark"
            placeholder="可选，填写申请说明"
            maxlength="200"
            style="margin-top: 16px"
          >
            <template #prepend>
              备注
            </template>
          </el-input>
          <el-button
            :loading="form.loading"
            @click="submit"
            style="width: 100%; margin-top: 24px; background-color: #0546ce; color: #ffffff"
            size="medium"
          >
            {{ $t("message.system.submitRegister") }}
          </el-button>
          <div class="login-link">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { register } from "@/api/user";

const router = useRouter();
let form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  remark: "",
  loading: false,
});
const passwordType = ref("password");

const passwordTypeChange = () => {
  passwordType.value = passwordType.value === "" ? "password" : "";
};

const submit = () => {
  if (form.username === "") {
    ElMessage.warning({ message: "请输入用户名", type: "warning" });
    return;
  }
  if (form.password === "") {
    ElMessage.warning({ message: "请输入密码", type: "warning" });
    return;
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning({ message: "两次密码输入不一致", type: "warning" });
    return;
  }
  form.loading = true;
  register({
    username: form.username,
    password: form.password,
    remark: form.remark,
  })
    .then(() => {
      ElMessage.success({
        message: "注册申请已提交，请等待审批",
        type: "success",
      });
      router.push("/login");
    })
    .catch((e: any) => {
      ElMessage.error({
        message: e?.message || "注册失败",
        type: "error",
      });
    })
    .finally(() => {
      form.loading = false;
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

      .password-icon {
        cursor: pointer;
        color: #409eff;
      }
    }
  }
}

.login-link {
  display: flex;
  margin-top: 16px;
  justify-content: center;
  color: #7F7F7F;
  font-size: 14px;
  a {
    color: #0546ce;
    text-decoration: none;
    margin-left: 4px;
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
  }
}
</style>
