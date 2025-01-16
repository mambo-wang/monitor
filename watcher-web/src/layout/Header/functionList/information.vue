

<template>
  <el-dropdown @command="handleCommand" size="medium" placement="bottom-end">
    <span class="el-dropdown-link">
      <img class="userhead" src="@/assets/images/Bell.png" alt="">
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <div style="min-width:336px;min-height:100px;max-height:326px">
          <div class="info-title">
            {{ $t('message.common.info') }}
          </div>
          <el-divider></el-divider>
          <template v-for="item in informationList" :key="item.index">
          <div class="info-content">
            <div class="content-title1" v-if="item.type===1">
              【{{item.typeStr}}】
            </div>
            <div class="content-title2" v-if="item.type===2">
              【{{item.typeStr}}】
            </div>
            <div class="content-title3" v-if="item.type===3">
              【{{item.typeStr}}】
            </div>
            <div class="content-title4" v-if="item.type===4">
              【{{item.typeStr}}】
            </div>
            <div class="content">
             {{item.content}}
            </div>
            <div class="info-time">
               {{item.time}}
            </div>
         
          </div>
   <el-divider></el-divider>
          </template>
        </div>

      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script lang="ts" setup>
import { defineComponent,onMounted } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import { useI18n } from "vue-i18n";
import { changeTitle } from "@/utils/system/title";
import { reactive, ref, watchEffect } from "vue";
import io from 'socket.io-client'
import { isNullOrUndefined } from "util";
    const { locale, t } = useI18n();
    const route = useRoute();
    const store = useStore();
    // 国际化语言切换
    const handleCommand = (command: string) => {
      locale.value = command;
      store.commit("app/stateChange", { name: "lang", value: command });
      changeTitle(route.meta.title);
      document.querySelector("html")!.setAttribute("lang", command);
    };
    let informationList=reactive([{
      type:1,
      typeStr:'待办',
      content:'XXX文件内部传输申请（mock测试数据）',
      time:'2022/3/5 14:23:12'
    },
    {
      type:2,
      typeStr:'驳回',
      content:'XXX文件内部传输申请（mock测试数据）',
      time:'2022/3/5 14:23:12'
    },
    {
      type:3,
      typeStr:'通过',
      content:'XXX文件内部传输申请（mock测试数据）',
      time:'2022/3/5 14:23:12'
    },{
      type:4,
      typeStr:'收件',
      content:'XXX文件内部传输申请（mock测试数据）',
      time:'2022/3/5 14:23:12'
    },
    ])
    let socket =null
    onMounted(() => {
//  io('ws://'+window.location.origin +`/itrans/test`)
});
   
 
</script>

<style lang="scss" scoped>
.userhead {
 
}
.el-divider--horizontal{
  margin:0;
}
.info-title{
  font-size: 16px;
  color:#2B85FB;
  padding:3px 0  0 10px;
  margin-bottom: 10px;
}

.info-content{
  padding:10px;
    // background-color: rgba(248, 240, 255, 1);
    
}
.content-title1{
  float: left;
   color:#2B85FB;
}
.content-title2{
  float: left;
   color:#FF4D4F;
}
.content-title3{
  float: left;
   color:#52C41A;
}
.content-title4{
  float: left;
   color:#52C41A;
}
.content{
  float: left;
    margin-left: 10px;
}
.info-time{
  padding-left: 3px;
  clear: both;
  margin-top:30px;
  color:gray
  
}
</style>