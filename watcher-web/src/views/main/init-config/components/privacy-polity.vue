<template>
  <el-dialog width="800px" :close-on-click-modal="false">
    <template #title>
      <div style="text-align:left; font-weight: bold; color:#0546CE">{{ $t("message.privacyPolicy.privacyPolicy") }}</div>
    </template>
    <div class="privacyPolicyDiv" style="max-height:450px;overflow:auto">
      <p class="privacyPolicyTitle">{{ $t('message.privacyPolicy.privacyPolicyTitle') }}</p>
      <div class="privacyPolicyContent">
        <p v-for="index in 91" :key="index" :class="`content-${index}`">
          {{ $t(`message.privacyPolicy.contentBody${index}`) }}
        </p>
      </div>
    </div>
    <template #footer>
      <el-button @click="agree" class="confirm-button" style="margin-right:20px">{{ $t('message.privacyPolicy.agree')
      }}</el-button>
      <el-button @click="disagree" class="close-button" style="border:1px solid #176EFF;color:#176EFF" theme="default">{{
        $t('message.privacyPolicy.disagree') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, getCurrentInstance, onMounted, reactive, ref } from "vue";
import { useI18n } from 'vue-i18n'

const emit = defineEmits(["confirmPrivacyPolicy"]);

const agree = () => {
  emit('confirmPrivacyPolicy', [1]);
}
const disagree = () => {
  emit('confirmPrivacyPolicy', []);
}


</script>

<style lang="scss" scoped>
.privacyPolicyDiv {
  padding: 20px 20px;
  border: 1px solid #b7b7b7;
  border-radius: 5px;
}

.privacyPolicyTitle {
  font-size: 18px;
  text-align: center;
  font-weight: 600;
  margin-bottom: 30px;
}

.privacyPolicyContent {
  p {
    text-indent: 28px;
    text-align: left;

    &.content-90 {
      text-indent: 224px;
    }

    &.content-91 {
      text-indent: 211px;
    }
  }
}
</style>
