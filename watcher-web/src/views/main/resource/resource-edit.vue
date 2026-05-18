<template>
  <div class="resource-edit-wrapper">
    <div class="form-container">
      <div class="form-title">{{ $t("message.resource.editResource") }}</div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        class="resource-form"
      >
        <el-form-item :label="$t('message.resource.name')" prop="resourceName">
          <el-input
            v-model="form.resourceName"
            :placeholder="$t('message.resource.namePlaceholder')"
            maxlength="100"
          />
        </el-form-item>

        <el-form-item :label="$t('message.resource.platform')" prop="platform">
          <el-select v-model="form.platform" :placeholder="$t('message.resource.selectPlatform')">
            <el-option
              v-for="item in platformList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('message.resource.ipAddress')" prop="ipAddress">
          <el-input
            v-model="form.ipAddress"
            :placeholder="$t('message.resource.ipPlaceholder')"
          />
        </el-form-item>

        <el-form-item :label="$t('message.resource.port')" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" />
        </el-form-item>

        <el-form-item :label="$t('message.resource.protocol')" prop="protocol">
          <el-radio-group v-model="form.protocol">
            <el-radio label="HTTP">HTTP</el-radio>
            <el-radio label="HTTPS">HTTPS</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="$t('message.resource.restUsername')" prop="ac">
          <el-input
            v-model="form.ac"
            :placeholder="$t('message.resource.restUsernamePlaceholder')"
          />
        </el-form-item>

        <el-form-item :label="$t('message.resource.restPassword')" prop="ci">
          <el-input
            v-model="form.ci"
            type="password"
            :placeholder="$t('message.resource.restPasswordPlaceholder')"
            show-password
          />
        </el-form-item>

        <el-form-item :label="$t('message.resource.serverUsername')" prop="serverUsername">
          <el-input
            v-model="form.serverUsername"
            :placeholder="$t('message.resource.serverUsernamePlaceholder')"
          />
        </el-form-item>

        <el-form-item :label="$t('message.resource.serverPassword')" prop="serverPassword">
          <el-input
            v-model="form.serverPassword"
            type="password"
            :placeholder="$t('message.resource.serverPasswordPlaceholder')"
            show-password
          />
        </el-form-item>

        <el-form-item :label="$t('message.resource.serverPort')" prop="serverPort">
          <el-input-number v-model="form.serverPort" :min="1" :max="65535" :step="1" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ $t("message.common.submit") }}
          </el-button>
          <el-button @click="handleCancel">{{ $t("message.common.cancel") }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import resourceApi from "@/api/resource";

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const formRef = ref();
const loading = ref(false);
const resourceId = ref(route.query.id as string);

const form = reactive({
  resourceName: "",
  platform: "",
  ipAddress: "",
  port: 443,
  protocol: "HTTPS",
  ac: "",
  ci: "",
  serverUsername: "",
  serverPassword: "",
  serverPort: 22,
});

const platformList = [
  { value: "workspace", label: "Workspace" },
  { value: "uis", label: "UIS" },
  { value: "cas", label: "CAS" },
  // { value: "onestor", label: "ONEStor" },
];

const rules = {
  resourceName: [
    { required: true, message: t("message.resource.nameRequired"), trigger: "blur" },
  ],
  platform: [
    { required: true, message: t("message.resource.platformRequired"), trigger: "change" },
  ],
  ipAddress: [
    { required: true, message: t("message.resource.ipRequired"), trigger: "blur" },
    { pattern: /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/, 
      message: t("message.resource.ipInvalid"), trigger: "blur" },
  ],
  ac: [
    { required: true, message: t("message.resource.restUsernameRequired"), trigger: "blur" },
  ],
  ci: [
    { required: true, message: t("message.resource.restPasswordRequired"), trigger: "blur" },
  ],
};

// 加载资源详情
const loadResourceDetail = async () => {
  try {
    const res = await resourceApi.getResourceDetail(resourceId.value);
    if (res.data) {
      const data = res.data;
      form.resourceName = data.resourceName || "";
      form.platform = data.platform || "";
      form.ipAddress = data.ipAddress || "";
      form.port = data.port || 443;
      form.protocol = data.protocol || "HTTPS";
      form.ac = data.ac || "";
      form.ci = data.ci || "";
      form.serverUsername = data.serverUsername || "";
      form.serverPassword = data.serverPassword || "";
      form.serverPort = data.serverPort || 22;
    }
  } catch (error) {
    console.error("Failed to load resource detail:", error);
  }
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    const platformMap: Record<string, string> = {
      workspace: "WORKSPACE",
      uis: "UIS",
      cas: "CAS",
      // onestor: "ONESTOR",
    };
    
    const params = {
      id: resourceId.value,
      resourceName: form.resourceName,
      platform: platformMap[form.platform] || form.platform.toUpperCase(),
      ipAddress: form.ipAddress,
      port: form.port,
      protocol: form.protocol,
      authType: "Digest",
      ac: form.ac,
      ci: form.ci,
      serverUsername: form.serverUsername,
      serverPassword: form.serverPassword,
      serverPort: form.serverPort,
    };

    await resourceApi.updateResource(params);
    ElMessage.success(t("message.resource.updateSuccess"));
    router.push({ name: "resource-index" });
  } catch (error: any) {
    ElMessage.error(error.message || t("message.resource.updateFailed"));
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.push({ name: "resource-index" });
};

onMounted(() => {
  loadResourceDetail();
});
</script>

<style lang="scss" scoped>
.resource-edit-wrapper {
  padding: 24px;
  background-color: #fff;
  min-height: calc(100vh - 120px);

  .form-container {
    max-width: 800px;
    margin: 0 auto;

    .form-title {
      font-size: 20px;
      font-weight: 700;
      color: #333;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #eee;
    }

    .resource-form {
      :deep(.el-form-item) {
        margin-bottom: 24px;
      }

      :deep(.el-input),
      :deep(.el-select) {
        width: 300px;
      }

      :deep(.el-input-number) {
        width: 150px;
      }
    }
  }
}
</style>
