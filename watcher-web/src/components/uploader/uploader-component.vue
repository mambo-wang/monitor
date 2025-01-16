<template>
  <uploader
    :options="options"
    class="uploader-component"
    ref="uploader"
    :file-status-text="statusText"
    @file-added="onFileAdded"
    @file-success="onFileSuccess"
    @file-error="onFileError"
    @file-removed="onFileRemove"
    @files-added="onFilesAdded"
  >
    <uploader-drop class="drop-drag" @click="clickUpload">
      <div class="icon-wrapper">
        <MessageBox class="drop-drag-icon" />
      </div>
      <p class="description">{{ $t("message.uploader.uploaderTitle") }}</p>
      <uploader-btn class="button-area" id="uploader-button-area"></uploader-btn>
      <!-- <uploader-btn>select images</uploader-btn>
      <uploader-btn :directory="true">select folder</uploader-btn>-->
    </uploader-drop>

    <uploader-list class="file-list-wrapper">
      <template #default="scope">
        <el-scrollbar
          v-if="
            scope.fileList.length !== 0 || uploadedFileList.list.length !== 0
          "
        >
          <ul class="uploaded-file-list">
            <li v-for="list in uploadedFileList.list" :key="list.id">
              <div class="file-info-bar">
                <div class="uploaded-file-list-progress-wrapper">
                  <div class="file-icon-wrapper paperclip-icon">
                    <Paperclip class="file-icon" />
                  </div>
                  <div style="flex: 1; display: flex">
                    <div class="file-name" :title="list.name">{{ getFileName(list.name) }}</div>
                    <div style="margin-left: 30px">{{ formatedFileSize(list.size) }}</div>
                  </div>
                  <div
                    class="file-icon-wrapper"
                    @click="handleRemoveUploadedFile(list)"
                    :title="$t('message.uploader.cancelUpload')"
                  >
                    <CloseBold class="file-icon" />
                  </div>
                </div>
              </div>
              <el-progress :percentage="100" />
            </li>
          </ul>

          <ul class="file-list">
            <li v-for="file in scope.fileList" :key="file.id">
              <uploader-file
                :class="'file_' + file.id"
                class="file-info-bar"
                ref="files"
                :file="file"
                :list="true"
                #default="list"
              >
                <div class="progress-wrapper">
                  <div class="name-action">
                    <div class="file-icon-wrapper paperclip-icon">
                      <Paperclip class="file-icon" />
                    </div>
                    <div style="flex: 1; display: flex">
                      <div class="file-name" :title="list.file.name">{{ getFileName(list.file.name)}}</div>
                      <div
                        v-if="!file.hasMd5"
                        style="margin-left: 30px"
                      >{{ $t("message.uploader.computeMd5") }}</div>
                      <div v-if="file.hasMd5" style="margin-left: 30px">{{ list.formatedSize }}</div>
                    </div>
                    <div
                      class="file-icon-wrapper"
                      :title="$t('message.uploader.pauseUpload')"
                      v-if="
                        file.hasMd5 && file.paused === false && !file.completed && !file.error
                      "
                      @click="hanlePauseFile(file)"
                    >
                      <VideoPause class="file-icon" />
                    </div>
                    <div
                      class="file-icon-wrapper"
                      :title="$t('message.uploader.startUpload')"
                      v-if="
                        file.hasMd5 && file.paused === true && !file.completed && !file.error
                      "
                      @click="handleResumeFile(file)"
                    >
                      <CaretRight class="file-icon" />
                    </div>
                    <div
                      class="file-icon-wrapper"
                      @click="handleRetryFile(file)"
                      :title="$t('message.uploader.retryUpload')"
                      v-if="
                        file.hasMd5 && file.error === true && !file.completed
                      "
                    >
                      <RefreshRight class="file-icon" />
                    </div>
                    <div
                      class="file-icon-wrapper"
                      :title="$t('message.uploader.cancelUpload')"
                      @click="handleRemoveFile(file)"
                      v-if="file.hasMd5"
                    >
                      <CloseBold class="file-icon" />
                    </div>
                  </div>
                  <el-progress
                    :percentage="setProgress(list.progress, file)"
                    :color="setProgressColor(file)"
                  />
                </div>
              </uploader-file>
            </li>
          </ul>
        </el-scrollbar>
        <div
          v-if="
            scope.fileList.length === 0 && uploadedFileList.list.length === 0
          "
          class="no-file-list"
        >{{ $t("message.uploader.noFileList") }}</div>
      </template>
    </uploader-list>
  </uploader>
</template>

<script lang="ts">
import { nextTick, ref, onMounted, reactive, h, toRefs, toRef, watch } from "vue";
import {
  Paperclip,
  MessageBox,
  CloseBold,
  RefreshRight,
  VideoPause,
  CaretRight,
} from "@element-plus/icons";
import _ from "lodash";
import uploaderService from "@/api/uploader";
import SparkMD5 from "spark-md5";
import $ from "jquery";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";

export default {
  components: {
    Paperclip,
    MessageBox,
    CloseBold,
    RefreshRight,
    VideoPause,
    CaretRight,
  },
  props: {
    uploadedList: {
      type: Array,
      default: () => {
        return [];
      },
    },
  },
  setup(props: { [key: string]: any }, context: any) {
    const { t } = useI18n();
    const uploadedFileList = reactive<any>({
      list: [],
    });
    watch(() => props.uploadedList, (newVal) => {
      uploadedFileList.list = JSON.parse(JSON.stringify(newVal));
    })
    const fileListLoading = ref<boolean>(false);
    const uploader = ref<any>();
    const fileList = reactive({
      list: [],
    });
    const options: { [key: string]: any } = {
      target: "/itrans/file/upload",
      chunkSize: `${20 * 1024 * 1024}`, //10M-10485760  20M-20971520
      forceChunkSize: true,
      fileParameterName: "file",
      simultaneousUploads: 3,
      action: "POST",
      maxChunkRetries: 3,
      testChunks: true,
      checkChunkUploadedByResponse: (chunk: any, message: any) => {
        if (chunk.file.isUploaded) {
          // 如果上传的文件已存在，就不用上传了
          return true;
        }
        const objMessage = JSON.parse(message);
        if (objMessage.state === 1) {
          chunk.file.error = true;
          handleGetRequestError(objMessage);
          return true;
        }
        if (!objMessage.data) {
          return true;
        }
        if (objMessage.data.exists) {
          chunk.file.fileId = objMessage.data.fileId;
          emitUploaderSuccess(objMessage.data.fileId);
          return true;
        }
        return (objMessage.data.uploaded || []).indexOf(chunk.offset + 1) >= 0;
      },
      headers: {},
      processParams: (params: any) => {
        return {
          ...params,
          md5: params.identifier.replace(
            `${params.filename}-${params.totalSize}-`,
            ""
          ),
        };
      },
      processResponse: (response:any,cb:any,file:any)=>{
        const res = JSON.parse(response);
        if (res.state === 0 && res.data && res.data.exists) {
          file.fileId = res.data.fileId;
          emitUploaderSuccess(file.fileId);
        }
        cb(null,response)
      }
    };
    const statusText = {
      success: t("message.uploader.success"),
      error: t("message.uploader.error"),
      uploading: t("message.uploader.uploading"),
      paused: t("message.uploader.paused"),
      waiting: t("message.uploader.waiting"),
    };
    const complete = () => {
      console.log("complete", arguments);
    };
    const fileComplete = () => {
      console.log("file complete", arguments);
    };
    const clickUpload = () => {
      if ($("#uploader-button-area")) {
        $("#uploader-button-area").click();
      }
    };
    const onFileAdded = (file: any) => {
      computeMD5(file);
    };
    const onFileSuccess = (rootFile: any, file: any, response: any) => {
      // const res = JSON.parse(response);
      // if (res.state === 0 && res.data) {
      //   file.fileId = res.data.fileId;
      //   emitUploaderSuccess(file.fileId)
      // }
    };
    const onFileError = (rootFile: any, file: any, response: any) => {
      const res = JSON.parse(response);
      if (res.state === 1) {
        messageTip(res.data.failureMessage, "error");
      }
    };
    const onFileRemove = (file: any) => {
      if (file.fileId) {
        emitUploaderDelete(file.fileId)
      }
    };
    const onFilesAdded = (file: any) => {
      fileList.list = file;
    };
    const emitUploaderSuccess = (fileId: number) => {
      context.emit("uploaderSuccess", fileId);
    };
    const emitUploaderDelete = (fileId: number) => {
      context.emit("uploaderDelete", fileId);
    };
    const computeMD5 = (file: any) => {
      fileListLoading.value = true;
      file.hasMd5 = false;
      file.pause();
      const spark = new SparkMD5.ArrayBuffer();
      const fileReader = new FileReader();
      const time = new Date().getTime();
      const blobSlice =
        File.prototype.slice ||
        File.prototype.mozSlice ||
        File.prototype.webkitSlice;
      let currentChunk = 0;
      const chunkSize = 20 * 1024 * 1024;
      const chunks = Math.ceil(file.size / chunkSize);
      loadNext();
      fileReader.onload = (e: any) => {
        spark.append(e.target.result);
        if (currentChunk < chunks) {
          currentChunk++;
          loadNext();
          // 实时展示MD5的计算进度
          nextTick(() => {
            $(`.myStatus_${file.id}`).text(
              "校验MD5 " + ((currentChunk / chunks) * 100).toFixed(0) + "%"
            );
          });
        } else {
          const md5 = spark.end();
          computeMD5Success(md5, file);
          console.log(
            `MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${file.size
            } 用时：${new Date().getTime() - time} ms`
          );
        }
      };
      fileReader.onerror = function () {
        new Error(`文件${file.name}读取出错，请检查该文件`);
        file.cancel();
      };
      function loadNext() {
        const start = currentChunk * chunkSize;
        const end =
          start + chunkSize >= file.size ? file.size : start + chunkSize;
        fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end));
      }
    };
    const computeMD5Success = (md5: any, file: any) => {
      file.uniqueIdentifier = `${file.name}-${file.size}-${md5}`;
      file.identifier = `${file.name}-${file.size}-${md5}`;
      file.hasMd5 = true;
      file.resume();
    };
    const handleGetRequestError = (objMessage: any) => {
      messageTip(objMessage.failureMessage, "error");
    };
    const messageTip = (message: string, type: any) => {
      ElMessage({
        message,
        type,
        grouping: true
      });
    };
    const hanlePauseFile = (file: any) => {
      file.pause();
    };
    const handleResumeFile = (file: any) => {
      file.resume();
    };
    const handleRemoveFile = (file: any) => {
      file.cancel();
    };
    const handleRetryFile = (file: any) => {
      file.retry();
    };
    const handleRemoveUploadedFile = (list: any) => {
      const index = _.findIndex(
        uploadedFileList.list,
        (item: any) => item.id === list.id
      );
      if (index !== -1) {
        uploadedFileList.list.splice(index, 1);
        context.emit("uploaderDelete", list.id);
      }
    };
    const formatedFileSize = (size: number) => {
      if (size < 1024) {
        return size.toFixed(0) + ' bytes'
      } else if (size < 1024 * 1024) {
        return (size / 1024.0).toFixed(0) + ' KB'
      } else if (size < 1024 * 1024 * 1024) {
        return (size / 1024.0 / 1024.0).toFixed(1) + ' MB'
      } else {
        return (size / 1024.0 / 1024.0 / 1024.0).toFixed(1) + ' GB'
      }
    }
    const setProgress = (progress: number, file: any) => {
      return file.error === true ? 100 : progress;
    };
    const setProgressColor = (file: any) => {
      return file.error === true ? "red" : "#409eff";
    };
    const getFileName = (name: string) => {
      const length = 20;
      return `${name.substring(0, length)}...`;
    }
    return {
      uploader,
      options,
      statusText,
      clickUpload,
      onFileAdded,
      onFileSuccess,
      onFileError,
      onFileRemove,
      onFilesAdded,
      fileList,
      fileListLoading,
      hanlePauseFile,
      handleResumeFile,
      handleRemoveFile,
      handleRetryFile,
      handleRemoveUploadedFile,
      uploadedFileList,
      formatedFileSize,
      setProgress,
      setProgressColor,
      getFileName
    };
  },
};
</script>

<style lang="scss" scoped>
.uploader-component {
  height: 100%;
  width: 100%;
  display: flex;

  font-size: 14px;
  :deep(.uploader-drop) {
    border-color: #409eff;
    border-radius: 16px;
  }
  .drop-drag {
    height: 200px;
    width: 40%;
    .icon-wrapper {
      width: 100%;
      padding: 60px 0 10px 0;
      display: flex;
      justify-content: center;
      .drop-drag-icon {
        width: 48px;
        height: 48px;
        color: #409eff;
      }
    }

    .description {
      width: 100%;
      display: flex;
      justify-content: center;
    }
    .button-area {
      display: none;
    }
  }

  .file-list-wrapper {
    width: 60%;
    height: 200px;
    padding: 10px 0;
    margin-left: 20px;
    .file-icon {
      width: 16px;
      height: 16px;
    }
    .file-list {
      list-style-type: none;
      margin: 0;
      > li {
        display: flex;
        width: 100%;
        align-items: center;

        .file-info-bar {
          flex: 1;
          overflow: visible;
          border: none;
          line-height: 30px;

          .name-action {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            flex: 1;
          }
          .file-icon-wrapper {
            display: flex;
            align-items: center;
            width: 10%;
            cursor: pointer;
          }
          .paperclip-icon {
            width: 5%;
            cursor: default;
          }
        }
      }
    }
    .no-file-list {
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: default;
    }
    .uploaded-file-list {
      list-style-type: none;
      margin: 0;
      .file-info-bar {
        line-height: 30px;
      }
      .uploaded-file-list-progress-wrapper {
        display: flex;
        align-items: center;
        .file-icon-wrapper {
          display: flex;
          align-items: center;
          width: 10%;
          cursor: pointer;
        }
        .paperclip-icon {
          width: 5%;
          cursor: default;
        }
      }
    }
  }
}
</style>
