<template>
  <el-row class="dc-container">
    <tpl-dialog ref="tplDialog" v-on:after-change="afterChangeTpl" class="dc-tpl-dialog_ElImport"></tpl-dialog>
    <el-dialog
      :visible.sync="dialogProps.visible"
      :close-on-click-modal="false"
      fullscreen
      :lock-scroll="true"
      destroy-on-close
      :show-close="false"
      class="code-desinge-dialog"
      v-loading="loading"
      v-on:close="onDialogClose"
    >
      <div slot="title" class="dialog-header" style="text-align: right">
        <el-button type="success" :plain="true" v-on:click="openTemplateDialog">更换模板</el-button>
        <el-dropdown :disabled="isDesignerDisabled" split-button type="warning" v-on:click="saveAndGenerate(false, 'generate')">
          生成代码
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <el-button type="text" :plain="true" v-on:click="saveAndGenerate(true, 'generate')">覆盖文件</el-button>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button :disabled="!isDesignerDisabled" type="primary" :plain="true" v-on:click="saveAndGenerate('', '')">
          保 存
        </el-button>
        <el-button :plain="true" v-on:click="onDialogClose">关 闭</el-button>
      </div>
      <div class="code-generator-container" :class="settings.size">
        <Designer
          ref="designer"
          :style="colorObject"
          v-if="jsonTpl && jsonTpl.pages && jsonTpl.pages.length > 0 && dbModel"
          v-model="jsonTpl"
          :dbModel="dbModel"
          :tplMode="false"
        ></Designer>
        <div class="item-empty-info" v-else>该模板为空模板</div>
      </div>
    </el-dialog>
  </el-row>
</template>
<script>
import Vue from 'vue'
import { validatenull } from '@/utils/validate'
import { getExtensionName } from '@/utils/file'
import BaseUI from '@/views/components/baseUI'
import { generateCode } from '@/api/dc/dcCode'
import OperationIcon from '@/components/OperationIcon'
import { getDcSchemeById } from '@/api/dc/dcScheme'
import { listDcTemplateAll } from '@/api/dc/dcTemplate'
import { saveDcScheme } from '@/api/dc/dcScheme'

import TplDialog from './tplDialog'
import { constructDbModel } from '@/views/dc/dcClienScheme/utils'

const renderCode = window['vue-codegenerator'].renderCode
const prettify = window['vue-codegenerator'].prettify
export default {
  extends: BaseUI,
  name: 'code-desinge',
  components: {
    OperationIcon,
    TplDialog
  },
  data() {
    return {
      jsonTpl: null, // 当前编辑的模板json对象
      bizFormModel: this.initFormModel(),
      isDesignerDisabled: false, // 生成代码按钮/保存按钮是否可操作， true：生成代码可操作，false：保存按钮可操作
      // TODO 临时处理后台模型
      dbModel: null,
      dialogProps: {
        visible: false
      },
      tplUnwatch: null,
      bizUnwatch: null
    }
  },
  computed: {
    ...Vuex.mapGetters(['settings']),
    colorObject() {
      return {
        '--active-color': this.settings.theme
      }
    }
  },
  methods: {
    saveAndGenerate(replace, type) {
      // 生成代码
      this.setLoad()
      // 保存code
      if (!validatenull(this.jsonTpl) && !validatenull(this.jsonTpl.pages)) {
        for (let i = 0; i < this.jsonTpl.pages.length; i++) {
          const page = this.jsonTpl.pages[i]
          let dbModel = JSON.parse(JSON.stringify(this.dbModel))
          if (page.hasOwnProperty('tableName')) {
            let childTable = this.dbModel.childTables.find((item) => {
              return page.tableName == item.name
            })
            dbModel = {
              ...this.dbModel,
              ...{
                table: {
                  ...childTable
                },
                className: childTable.className
              }
            }
            dbModel.childTables = []
          }
          let code = renderCode(page.source, {
            element: page,
            ...dbModel
          })
          code = prettify(getExtensionName(page.fileName), code)
          this.$set(this.jsonTpl.pages[i], 'code', code)
        }
      }
      if (!validatenull(this.bizFormModel.dcTableList)) {
        for (let i = 0; i < this.bizFormModel.dcTableList.length; i++) {
          this.bizFormModel.dcTableList[i].columnList.forEach((column) => {
            if (column.javaType.value == 'Custom' || column.javaType.value == 'This') {
              // custom | thisObj
              // 判断当前的childFields是否已经转换过类型
              let child = typeof column.childFields == 'object' ? JSON.stringify(column.childFields) : column.childFields
              column.childFields = child
            } else {
              column.childFields = ''
            }
          })
        }
      }
      this.bizFormModel.templateJson = JSON.stringify(this.jsonTpl)
      if (type == 'generate') {
        // 生成代码
        /* 是否替换文件 */
        this.bizFormModel.replace = replace
        generateCode(this.bizFormModel)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.loading = false
              this.isDesignerDisabled = false
            }
            this.showMessage(responseData)
            this.resetLoad()
          })
          .catch((error) => {
            this.outputError(error)
          })
      } else {
        saveDcScheme(this.bizFormModel)
          .then((responseData) => {
            if (responseData.code == 100) {
              this.$message({
                type: 'success',
                message: '保存成功'
              })
              this.bizFormModel.id = responseData.data.id
              this.bizFormModel.updateDate = responseData.data.updateDate
              this.bizFormModel.dcTableList.forEach((tb) => {
                let nTb = responseData.data.dcTableList.find((newTb) => tb.name == newTb.name)
                tb.updateDate = nTb.updateDate
              })
              this.isDesignerDisabled = false
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          })
          .catch((error) => {
            this.outputError(error)
          })
      }
    },
    openTemplateDialog() {
      this.$refs.tplDialog.$emit('switchTemplate', this.bizFormModel.template, this.dbModel)
    },
    initFormModel(This) {
      return {
        name: '', // 方案名称
        packageName: '', // 包路径
        moduleName: '', // 模块名
        subModuleName: '', // 子模块名
        functionName: '', // 功能名
        template: {
          // 代码分类
          id: '',
          name: '',
          json: ''
        },
        templateJson: '', // 模板JSON
        remarks: '', // 备注信息
        dcTableList: [] // 子表列表，默认存在一个主表
      }
    },
    initDbModel(backScheme) {
      this.dbModel = null
      if (!validatenull(backScheme) && !validatenull(backScheme.id)) {
        this.setLoad()
        getDcSchemeById(backScheme.id)
          .then((responseData) => {
            if (responseData.code == 100) {
              let schema = responseData.data
              if (!validatenull(schema.dcTableList)) {
                for (let i = 0; i < schema.dcTableList.length; i++) {
                  const constraintList = schema.dcTableList[i].constraintJson
                  this.$set(
                    schema.dcTableList[i],
                    'constraintList',
                    !validatenull(constraintList) ? JSON.parse(constraintList) : []
                  )
                  for (let j = 0; j < schema.dcTableList[i].columnList.length; j++) {
                    const childFields = schema.dcTableList[i].columnList[j].childFields
                    this.$set(
                      schema.dcTableList[i].columnList[j],
                      'childFields',
                      !validatenull(childFields) ? JSON.parse(childFields) : []
                    )
                  }
                }
              }
              this.dbModel = constructDbModel(schema)
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          })
          .catch((error) => {
            this.outputError(error)
          })
      }
    },
    onDialogClose() {
      this.resetWatch()
      this.dialogProps.visible = false
      this.$emit('save-finished')
    },
    afterChangeTpl(javaTpl, dbModel) {
      this.jsonTpl = JSON.parse(javaTpl.json)
      this.bizFormModel.templateJson = javaTpl.json // 模板JSON
      this.bizFormModel.template = javaTpl // 所选模板对象

      this.dbModel = dbModel

      // 执行模板初始化JS方法
      this.$nextTick(() => {
        if (this.$refs.designer) {
          this.$refs.designer.$emit('doInitJs')
        }
      })
    },
    setWatch() {
      this.$nextTick(() => {
        this.tplUnwatch = this.$watch(
          'jsonTpl',
          function (val, oldVal) {
            this.isDesignerDisabled = true
          },
          {
            deep: true
          }
        )
      })
    },
    resetWatch() {
      this.tplUnwatch && this.tplUnwatch()
      this.tplUnwatch = null
    },
    initOptions() {}
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on('openGenerateDcSchemeDialog', function (row) {
        this.isDesignerDisabled = false
        this.jsonTpl = JSON.parse(row.templateJson)
        this.bizFormModel = row
        this.initDbModel(row)
        this.setWatch()
        this.dialogProps.visible = true
      })
      this.$on('openAddGenerateDcSchemeDialog', function (javaTpl, dbModel, row) {
        this.dbModel = dbModel
        this.isDesignerDisabled = true // 不能生成代码
        this.jsonTpl = JSON.parse(javaTpl.json) // 模板JSON转为对象

        this.bizFormModel = row
        this.bizFormModel.templateJson = javaTpl.json // 模板JSON
        this.bizFormModel.template = javaTpl // 所选模板对象

        this.setWatch()
        this.dialogProps.visible = true
        // 执行模板初始化JS方法
        this.$nextTick(() => {
          this.$refs.designer.$emit('doInitJs')
        })
      })
    })
  }
}
</script>
<style scoped lang="scss">
.item-empty-info {
  position: absolute;
  top: 50%;
  left: 50%;
  margin-top: -10px;
  margin-left: -63px;
  text-align: center;
  font-size: 14px;
  color: #ccb1ea;
  z-index: 2000;
}

.code-generator-container {
  /deep/ .page-opt .drawing-item-plus {
    background-color: var(--active-color);
  }

  /deep/ .right-board {
    .document-link {
      background-color: var(--active-color);
    }
  }

  /deep/ .drawing-row-item.active-from-item,
  /deep/ .drawing-row-item .active-from-item {
    border-color: var(--active-color);
    outline-color: var(--active-color);
  }

  /deep/ .drawing-row-item .drawing-item-btns > .drawing-item-icon,
  /deep/ .drawing-item .drawing-item-btns > .drawing-item-icon {
    background-color: var(--active-color) !important;
    border-color: var(--active-color) !important;
  }

  /deep/ .drawing-item:hover,
  /deep/ .drawing-row-item:hover {
    outline-color: var(--active-color);
  }
}
</style>
