<template>
  <div>
    <el-dialog
      title="模板选择"
      :visible.sync="templateVisible"
      :close-on-click-modal="false"
      width="50%"
      v-on:close="onTemplateDialogClose()"
      v-loading="loading"
      :append-to-body="true"
    >
      <el-row gutter="20">
        <el-col :span="8" style="margin: 10px 0" v-for="(item, index) in handleTemplate_List" :key="index">
          <el-card class="box-card template-card" shadow="hover" :class="template.id == item.id ? 'card-selected' : ''">
            <el-carousel height="200px">
              <el-carousel-item v-for="(img, index) in item.fileList" :key="index">
                <el-image :src="`data:image/png;base64,${img.fileByte}`">
                  <div slot="error" class="error-image">
                    <i class="el-icon-document"></i>
                  </div>
                </el-image>
              </el-carousel-item>
            </el-carousel>
            <div class="card-title">{{ item.name }}</div>
            <div class="card-detail">
              <p>{{ item.remarks }}</p>
              <el-popover placement="top-start" width="200" trigger="hover" :content="item.remarks">
                <el-button type="text" slot="reference">详情</el-button>
              </el-popover>
            </div>
            <div class="card-select">
              <div v-if="template.id == item.id" class="selected-flag"><i class="el-icon-check"></i></div>
              <el-button v-else type="primary" size="mini" round v-on:click="onChangeTemplate(item)">选择模板</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <span slot="footer" class="dialog-footer">
        <el-button v-if="template_List.length > 0" type="primary" v-on:click="chooseType()" :plain="true">确 定</el-button>
        <el-button :plain="true" v-on:click="onTemplateDialogClose()">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { validatenull } from '@/utils/validate'
import { listDcSchemeAll, getDcSchemeById } from '@/api/dc/dcScheme'
import { listTemplateAndFile } from '@/api/dc/dcTemplate'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import { constructDbModel } from '@/views/dc/dcClienScheme/utils'

export default {
  extends: BaseUI,
  name: 'tpl-dialog',
  components: {
    OperationIcon
  },
  data() {
    return {
      dbModel: null,
      template_List: [], // 模板
      template: this.initTemplate(),
      templateVisible: false, // 模板弹窗
      templateRules: {
        'scheme.id': [
          {
            required: true,
            message: '请选择后端方案',
            trigger: 'change'
          }
        ]
      }
    }
  },
  props: {},
  computed: {
    ...mapGetters(['settings']),
    handleTemplate_List() {
      return this.template_List.filter((item) => {
        return this.matchModel(item)
      })
    }
  },
  methods: {
    getSchemeInitDbModel(val) {
      this.dbModel = null
      if (!validatenull(val) && !validatenull(val.id)) {
        this.dbModel = constructDbModel(val)
      }
    },
    onChangeTemplate(val) {
      this.template = {
        id: val.id,
        name: val.name,
        json: val.json
      }
    },
    chooseType() {
      if (!validatenull(this.template.id)) {
        this.$emit('after-change', this.template, this.dbModel)
        this.templateVisible = false
      } else {
        this.showMessage({
          type: 'warning',
          msg: '请选择模板'
        })
      }
    },
    onTemplateDialogClose() {
      this.templateVisible = false
    },
    initTemplate() {
      return {
        id: null,
        name: null,
        json: null
      }
    },
    initOptions() {
      let template_search = {
        params: [
          {
            columnName: 'type',
            queryType: '=',
            value: '1'
          }
        ]
      }
      listTemplateAndFile(template_search).then((responseData) => {
        this.template_List = responseData.data
      })
    },
    matchModel(tpl) {
      if (!this.dbModel) {
        return false
      }
      let jsonTemplate = JSON.parse(tpl.json) // 模板JSON转为对象
      // TODO 需要如果匹配代码异常的兼容性处理
      return eval(`(${jsonTemplate.matchJs}).call(this, this.dbModel)`)
    }
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on('selectTemplate', function (scheme) {
        this.template = this.initTemplate()
        this.getSchemeInitDbModel(scheme)
        this.templateVisible = true // 选择模板
        this.initOptions()
      })
      this.$on('switchTemplate', function (tpl, dbModel) {
        this.templateVisible = true // 选择模板

        this.template = tpl
        this.dbModel = dbModel
        this.initOptions()
      })
    })
  }
}
</script>
