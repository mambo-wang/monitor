<template>
  <div class="system-table-box">
    <el-table
      v-bind="$attrs"
      ref="table"
      class="system-table"
      :stripe="hasStripe"
      :border="hasBorder"
      :header-cell-style="{backgroundColor: '#ececec', height: '50px'}"
      :data="data"
       :default-sort="{ prop: 'date', order: 'descending' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" align="center" width="50" v-if="showSelection" sortable />
      <el-table-column :label="$t('message.common.order')" width="60" align="center" v-if="showIndex" >
        <template #default="scope">
          {{ (page.index - 1) * page.size + scope.$index + 1 }}
        </template>
      </el-table-column>
      <slot></slot>
    </el-table>
    <div class="pagination-wrapper">
    <el-pagination
      v-if="showPage"
      v-model:current-page="page.index"
      class="system-page"
      background
      :layout="pageLayout"
      :total="page.total"
      :page-size="page.size"
      :page-sizes="pageSizes"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    >
    </el-pagination>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, onActivated, onMounted } from 'vue'
import { Page } from '@/components/table/type'
export default defineComponent({
  props: {
    data: { type: Array, default: () => [] }, // 数据源
    select: { type: Array, default: () => [] }, // 已选择的数据，与selection结合使用
    showIndex: { type: Boolean, default: false }, // 是否展示index选择，默认否
    showSelection: { type: Boolean, default: false }, // 是否展示选择框，默认否
    showPage: { type: Boolean, default: true }, // 是否展示页级组件，默认是
    hasStripe: { tyep: Boolean, default: true },
    hasBorder: { tyep: Boolean, default: false },
    page: { // 分页参数
      type: Object,
      default: () => {
        return { index: 1, size: 20, total: 0 }
      }
    },
    pageLayout: { type: String, default: "total, sizes, prev, pager, next, jumper" }, // 分页需要显示的东西，默认全部
    pageSizes: { type: Array, default: [5, 10, 20, 50, 100] }
  },
  setup(props, context) {
    const table: any = ref(null)
    let timer: any = null
    // 分页相关：监听页码切换事件
    const handleCurrentChange = (val: Number) => {
      if (timer) {
        props.page.index = 1
      } else {
        props.page.index = val
        context.emit("getTableData")
      }
    }
    // 分页相关：监听单页显示数量切换事件
    const handleSizeChange = (val: Number) => {
      timer = 'work'
      setTimeout(() => {
        timer = null
      }, 100)
      props.page.size = val
      props.page.index = 1
      context.emit("getTableData", true)
    }
    // 选择监听器
    const handleSelectionChange = (val: []) =>{
      context.emit("selection-change", val)
    }
    // 解决BUG：keep-alive组件使用时，表格浮层高度不对的问题
    onActivated(() => {
      table.value.doLayout()
    })
    return {
      table,
      handleCurrentChange,
      handleSizeChange,
      handleSelectionChange
    }
  }
})
</script>

<style lang="scss" scoped>
  .system-table-box {
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    padding: 20px 24px;
    .system-table {
      box-sizing: border-box;
      flex: 1;
    }
    
    .pagination-wrapper {
      display: flex;
      width: 100%;
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
  :deep(.el-table thead){
    color: #262626;
    font-size:14px;
  }
  :deep(.el-table--mini){
     font-size:14px;
      color:#595959
  }
  :deep(.el-input--mini .el-input__inner){
height:28px
  }

</style>