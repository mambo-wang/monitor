<template>
  <el-dialog width="500px" :close-on-click-modal="false">
    <template #title>
      <div class="recertify-confirm-title">{{ $t("message.tenant.confirmAction") }}</div>
    </template>
    <p style="color: #e51e1e; text-align: justify">{{ $t("message.tenant.confirmActionTip") }}</p>
    <el-form :model="formData" :rules="validateRule" ref="confirmActionFormRef" style="margin-top:20px;">
      <el-form-item prop="confirm">
        <el-input v-model="formData.confirm" @input="setButtonStatus"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button
            type="primary"
            @click="confirm"
            :disabled="disabled"
            style="width: 80px; height: 35px"
        >{{ $t("message.tenant.confirm") }}
        </el-button>
        <el-button
            @click="cancel"
            style="width: 80px; height: 35px; margin: 0 20px"
        >{{ $t("message.tenant.cancel") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import {reactive, ref} from "vue";
import {useI18n} from "vue-i18n";

const {t} = useI18n();
const confirmActionFormRef = ref();
const disabled = ref<boolean>(true);
const emit = defineEmits(["closeRecertify", "confirmRecertify"]);
const formData = reactive({
  confirm: ""
});
const validateRule = reactive({
  confirm: [{
    required: true,
    message: t("message.tenant.required"),
    trigger: "change",
  }, {
    pattern: /^confirm$/i,
    message: t("message.tenant.confirmTip"),
    trigger: "change"
  }]
})
const setButtonStatus = () => {
  if (confirmActionFormRef.value) {
    confirmActionFormRef.value.validate((valid: any) => {
      disabled.value = !valid;
    });
  }
};
const confirm = () => {
  emit("confirmRecertify");
};
const cancel = () => {
  emit("closeRecertify");
};
</script>

<style lang="scss" scoped>
.recertify-confirm-title {
  font-size: 16px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #d8dce5;
}
</style>
