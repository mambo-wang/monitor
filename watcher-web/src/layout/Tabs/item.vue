<template>
  <div class="tags-view-item" :class="active? 'active' : ''">
    <router-link :to="menu.path" v-if="menu.meta.title">
      {{ $t(menu.meta.title) }}
    </router-link>
    <el-icon @click.stop="reload" v-if="active"><refresh-right /></el-icon>
    <el-icon @click.stop="closeTab" v-if="!menu.meta.hideClose" :alt="$t('message.common.del')"><close /></el-icon>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { RefreshRight, Close } from '@element-plus/icons'
export default defineComponent({
  props: {
    menu: {
      type: Object,
      default: () => {
        return {
          path: '',
          meta: {
            label: '',
            hideClose: false
          }
        }
      }
    },
    active: {
      type: Boolean,
      default: false
    }
  },
  components: {
    RefreshRight,
    Close
  },
  setup(props, { emit }) {
    // 关闭按钮
    function closeTab() {
      emit('close')
    }
    // 刷新按钮
    function reload() {
      emit('reload')
    }
    return {
      closeTab,
      reload
    }
  }
})
</script>

<style lang="scss" scoped>
  .tags-view-item {
    display: inline-flex;
    align-items: center;
    position: relative;
    cursor: pointer;
       height: 37px;
    line-height: 26px;
    border: 1px solid var(--system-header-border-color);
    color: #262626;
    background: var(--system-header-tab-background);
    padding: 0 8px;
    font-size: 14px;
    
    
    // border-radius: 10px;
    a {
      color:#262626;
      height: 26px;
      display: inline-block;
      padding-left: 8px;
      padding-right: 8px;
    }
    .el-icon-refresh-right {
      display: inline-block;
      margin-right: 5px;
    }
    .el-icon-close {
      display: inline-block;
      height: 26px;
    }
    &:first-of-type {
      // margin-left: 15px;
    }
    &:last-of-type {
      margin-right: 15px;
    }
    &.active {
      background:  #FAFAFA;
      border-color: #F0F0F0;
      color: #262626;
      a {
        color: #262626;
      }
      &:hover {
        background: rgba(43, 133, 251, 0.15);
      }
    }
    &:hover {
      background-color: var(--system-header-item-hover-color);
    }
  }
</style>