<template>
  <el-row class="dc-container">
    <tpl-dialog ref="tplDialog" v-on:after-change="afterChangeTpl" class="dc-tpl-dialog_ElImport"></tpl-dialog>
    <el-dialog
      v-on:open="onDialogOpen"
      v-on:close="onDialogClose"
      v-loading="loading"
      width="100%"
      fullscreen
      :title="dialogProps.title"
      :visible.sync="dialogProps.visible"
      :show-close="false"
      :close-on-click-modal="false"
      class="code-desinge-dialog"
    >
      <div slot="title" class="dialog-header">
        <el-form ref="editForm" :model="editFormData" label-width="100px" class="edit-form">
          <el-row>
            <el-col :span="12">
              <el-form-item
                prop="name"
                label="标题"
                :rules="[{ required: true, message: '标题不能为空', trigger: 'blur' }]"
                class="dc-el-form-item_SingleInput"
              >
                <el-input
                  v-model="editFormData.name"
                  :maxLength="128"
                  placeholder="请输入名称"
                  clearable
                  :disabled="action == 'view'"
                  class="dc-el-input_SingleInput"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-tooltip v-if="action != 'view'" disabled class="dc-el-tooltip_Button">
                <el-button type="success" v-on:click="openTemplateDialog" class="dc-el-button_Button">更换模板</el-button>
              </el-tooltip>
              <el-dropdown
                :disabled="isDesignerDisabled"
                v-if="action != 'view'"
                split-button
                type="warning"
                v-on:click="generate(false)"
              >
                生成代码
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item><el-button type="text" v-on:click="generate(true)">覆盖文件</el-button></el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <el-tooltip v-if="action != 'view'" disabled class="dc-el-tooltip_Button">
                <el-button type="primary" v-on:click="onSubmit" :disabled="!isDesignerDisabled" class="dc-el-button_Button">
                  保存
                </el-button>
              </el-tooltip>
              <el-tooltip disabled class="dc-el-tooltip_Button">
                <el-button v-on:click="dialogProps.visible = false" class="dc-el-button_Button">关闭</el-button>
              </el-tooltip>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="code-generator-container" :class="settings.size" v-if="editFormData.scheme.id && dbModel">
        <Designer
          ref="designer"
          v-if="jsonTpl && jsonTpl.pages && jsonTpl.pages.length > 0 && dbModel"
          :style="colorObject"
          v-model="jsonTpl"
          :dbModel="dbModel"
          :tplMode="false"
          class="dc-Designer_ElImport"
        ></Designer>
      </div>
    </el-dialog>
  </el-row>
</template>
<script>
import { validatenull } from '@/utils/validate'
/** 根据用户界面配置import组件 结束 */
import TplDialog from './tplDialog'
/** 根据用户界面配置import组件 结束 */
import { getExtensionName } from '@/utils/file'
import { getDcSchemeById } from '@/api/dc/dcScheme'
import { getDcClienSchemeById, saveDcClienScheme } from '@/api/dc/dcClienScheme'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import { constructDbModel } from './utils'
import { generateCode } from '@/api/dc/dcCode'

const renderCode = window['vue-codegenerator'].renderCode
const prettify = window['vue-codegenerator'].prettify
const stringifyTemplate = window['vue-codegenerator'].stringifyTemplate
const parseTemplate = window['vue-codegenerator'].parseTemplate
export default {
  extends: BaseUI,
  name: 'dcClienScheme-form',
  components: {
    /** 根据用户界面配置组件 开始 */
    TplDialog,
    /** 根据用户界面配置组件 结束 */

    OperationIcon
  },
  data() {
    return {
      /** 根据用户界面配置生成data数据 开始 */
      editFormData: this.initEditData(),
      jsonTpl: null, // 当前编辑的模板json对象
      dbModel: null,
      isDesignerDisabled: false, // 生成代码按钮
      // 对话框属性变量
      dialogProps: {
        visible: false,
        title: '前端方案'
      },
      dialogTitle: '前端方案',
      // 选项变量

      /** 根据用户界面配置生成data数据 结束 */
      tplUnwatch: null,
      bizUnwatch: null,
      // 窗口操作类型 view/edit/add
      action: ''
    }
  },
  props: {
    // 权限
    permission: {
      type: Object
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
    generate(replace) {
      // 生成代码
      /* 是否替换文件 */
      this.editFormData.replace = replace
      this.setLoad()
      if (!validatenull(this.jsonTpl) && !validatenull(this.jsonTpl.pages)) {
        for (let i = 0; i < this.jsonTpl.pages.length; i++) {
          const page = this.jsonTpl.pages[i]
          let code = null
          if (this.jsonTpl.pages[i].fileName == 'metadata.js') {
            code = renderCode(page.source, {
              element: this.jsonTpl.pages[0],
              ...this.dbModel
            })
            code = prettify(getExtensionName(page.fileName), code)
          } else {
            code = renderCode(page.source, {
              element: page,
              ...this.dbModel
            })
            code = prettify(getExtensionName(page.fileName), code)
          }
          this.$set(this.jsonTpl.pages[i], 'code', code)
        }
      }
      this.editFormData.templateJson = stringifyTemplate(this.jsonTpl)
      generateCode(this.editFormData)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.isDesignerDisabled = false
            this.loading = false
          }
          this.resetLoad()
          this.showMessage(responseData)
        })
        .catch((error) => {
          this.outputError(error)
        })
    },
    openTemplateDialog() {
      this.$refs.tplDialog.$emit(
        'switchTemplate',
        {
          template: this.editFormData.template,
          scheme: this.editFormData.scheme
        },
        this.dbModel
      )
    },

    onSubmit() {
      this.$refs['editForm'].validate((valid) => {
        if (valid) {
          this.doSave()
        } else {
          return false
        }
      })
    },
    doSave() {
      this.setLoad()
      if (!validatenull(this.jsonTpl) && !validatenull(this.jsonTpl.pages)) {
        let version = 0
        if (this.jsonTpl.hasOwnProperty('version')) {
          // 已存在版本号
          version = this.jsonTpl.version
        }
        this.$set(this.jsonTpl, 'version', version + 1)
        for (let i = 0; i < this.jsonTpl.pages.length; i++) {
          this.$set(this.jsonTpl.pages[i], 'version', version + 1)
          const page = this.jsonTpl.pages[i]
          const code = renderCode(page.source, {
            element: page,
            ...this.dbModel
          })
          this.$set(this.jsonTpl.pages[i], 'code', code)
        }
      }
      this.editFormData.templateJson = stringifyTemplate(this.jsonTpl)

      saveDcClienScheme(this.editFormData)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.editFormData.id = responseData.data.id
            this.editFormData.updateDate = responseData.data.updateDate
            this.isDesignerDisabled = false
            this.showMessage({
              type: 'success',
              msg: '保存成功'
            })
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        })
        .catch((error) => {
          this.outputError(error)
        })
    },
    switchEdit() {
      this.action = 'edit'
      this.dialogProps.title = `修改${this.dialogTitle}`
      this.initOptions(this.editFormData)
    },

    getFormById(id) {
      this.setLoad()
      return new Promise((resolve) => {
        getDcClienSchemeById(id)
          .then((responseData) => {
            let form = {}
            if (responseData.code == 100) {
              form = responseData.data
              this.jsonTpl = parseTemplate(form.templateJson)
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
            resolve(form)
          })
          .catch((error) => {
            this.outputError(error)
          })
      })
    },
    onDialogClose() {
      this.resetWatch()
      this.$emit('save-finished')
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['editForm'].clearValidate()
      })
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
    initOptions(This) {
      // 初始化自定义类型选择框选项变量
    },
    initEditData(This) {
      return {
        name: '', // 名称
        scheme: {
          // 后端方案
          id: null,
          name: null
        },
        template: {
          // 模板
          id: null,
          name: null
        },
        templateJson: '', // 模板json
        remarks: '' // 备注信息
      }
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
        this.bizUnwatch = this.$watch(
          'editFormData.name',
          function (val, oldVal) {
            this.isDesignerDisabled = true
          },
          {}
        )
      })
    },
    resetWatch() {
      this.tplUnwatch && this.tplUnwatch()
      this.tplUnwatch = null

      this.bizUnwatch && this.bizUnwatch()
      this.bizUnwatch = null
    },
    afterChangeTpl(clientTpl, dbModel) {
      let version = this.jsonTpl.version
      this.jsonTpl = parseTemplate(clientTpl.template.json)
      this.$set(this.jsonTpl, 'version', version)
      this.editFormData.templateJson = clientTpl.template.json // 模板JSON
      this.editFormData.template = clientTpl.template // 所选模板对象
      this.editFormData.scheme = clientTpl.scheme

      this.dbModel = dbModel

      // 执行模板初始化JS方法
      this.$nextTick(() => {
        if (this.$refs.designer) {
          this.$refs.designer.$emit('doInitJs')
        }
      })
    }
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on('openViewDialog', async function (id) {
        this.action = 'view'
        this.dialogProps.title = `查看${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        this.initOptions(this.editFormData)
        this.initDbModel(this.editFormData.scheme)
        this.dialogProps.visible = true
      })

      this.$on('openEditDialog', async function (id) {
        this.action = 'edit'
        this.dialogProps.title = `修改${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        this.initOptions(this.editFormData)
        this.dialogProps.visible = true
        this.isDesignerDisabled = false
        this.initDbModel(this.editFormData.scheme)
        this.setWatch()
      })
      this.$on('openAddDialog', function (clientTpl, dbModel) {
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = this.initEditData()
        this.initOptions(this.editFormData)
        this.dialogProps.visible = true
        this.isDesignerDisabled = true // 不能生成代码
        this.jsonTpl = parseTemplate(clientTpl.template.json) // 模板JSON转为对象
        this.$set(this.jsonTpl, 'version', 1)
        this.editFormData.templateJson = clientTpl.template.json // 模板JSON
        this.editFormData.template = clientTpl.template // 所选模板对象
        this.editFormData.scheme = clientTpl.scheme // 所选后端方案
        this.editFormData.name = clientTpl.scheme.name
        this.dbModel = dbModel
        this.setWatch()
        // 执行模板初始化JS方法
        this.$nextTick(() => {
          this.$refs.designer.$emit('doInitJs')
        })
      })
      this.$on('openCopyDialog', async function (id) {
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        this.initOptions(this.editFormData)
        this.editFormData.id = null //把id设置为空，添加一个新的

        this.dialogProps.visible = true
        this.isDesignerDisabled = true // 不能生成代码
        this.$set(this.jsonTpl, 'version', 1)
        this.initDbModel(this.editFormData.scheme)
        this.setWatch()
      })
    })
  }
}
</script>
<style scoped lang="scss">
/* var(--active-color) */
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
