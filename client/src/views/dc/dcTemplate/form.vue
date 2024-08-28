<template>
  <el-row class="dc-container">
    <el-dialog
      v-on:open="onDialogOpen"
      v-on:close="onDialogClose"
      v-loading="loading"
      :show-close="false"
      fullscreen
      :title="dialogProps.title"
      :visible.sync="dialogProps.visible"
      :close-on-click-modal="false"
      class="code-desinge-dialog"
    >
      <div slot="title" class="dialog-header">
        <el-col :span="24 / 2">
          <span>{{ editFormData.name }}</span>
        </el-col>
        <el-popover
          ref="popover"
          title="模板详情"
          width="800"
          trigger="hover"
          placement="top-start"
          offset="0"
          class="dc-el-popover_ElPopover"
        >
          <el-form
            ref="editForm"
            :model="editFormData"
            label-width="100px"
            :disabled="action == 'view'"
            class="dc-el-form_ElEditForm"
          >
            <el-row>
              <el-form-item
                prop="name"
                label="名称"
                :rules="[{ required: true, message: '名称不能为空', trigger: 'blur' }]"
                class="dc-el-form-item_SingleInput"
              >
                <el-input
                  v-model="editFormData.name"
                  :maxLength="128"
                  placeholder="请输入名称"
                  clearable
                  class="dc-el-input_SingleInput"
                ></el-input>
              </el-form-item>
              <el-form-item prop="remarks" label="详情" class="dc-el-form-item_MutilpleInput">
                <el-input
                  v-model="editFormData.remarks"
                  type="textarea"
                  placeholder="请输入详情"
                  rows="2"
                  :maxLength="-1"
                  class="dc-el-input_MutilpleInput"
                ></el-input>
              </el-form-item>
              <upload-file
                title="详情图"
                v-if="dialogProps.visible"
                :action="action"
                :objectId="editFormData.fileId"
                v-model="editFormData.fileIdFile"
                class="dc-upload-file_ElUploadFile"
              ></upload-file>
            </el-row>
          </el-form>
          <template slot="reference">
            <el-tooltip disabled class="dc-el-tooltip_Button">
              <el-button type="warning" class="dc-el-button_Button">详情</el-button>
            </el-tooltip>
          </template>
        </el-popover>
        <el-tooltip v-if="action != 'view'" disabled class="dc-el-tooltip_Button">
          <el-button type="primary" v-on:click="onSubmit('dcTemplateForm')" class="dc-el-button_Button">保存</el-button>
        </el-tooltip>
        <el-tooltip disabled class="dc-el-tooltip_Button">
          <el-button v-on:click="onDialogClose()" class="dc-el-button_Button">关闭</el-button>
        </el-tooltip>
      </div>
      <Designer
        ref="designer"
        v-model="editFormData.json"
        v-if="editFormData.json"
        :style="colorObject"
        :key="designerKey"
        :dbModel="dbModel"
        :tplMode="true"
        class="dc-Designer_ElImport"
      ></Designer>
    </el-dialog>
  </el-row>
</template>
<script>
import { validatenull } from '@/utils/validate'
/** 根据用户界面配置import组件 结束 */
import uploadFile from '@/views/components/uploadFile'
/** 根据用户界面配置import组件 结束 */
import { getDcTemplateById, saveDcTemplate } from '@/api/dc/dcTemplate'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import defaultTemplate from './defaultTemplate'

const stringifyTemplate = window['vue-codegenerator'].stringifyTemplate
const parseTemplate = window['vue-codegenerator'].parseTemplate

export default {
  extends: BaseUI,
  name: 'dcTemplate-form',
  components: {
    /** 根据用户界面配置组件 开始 */
    uploadFile,
    /** 根据用户界面配置组件 结束 */

    OperationIcon
  },
  data() {
    return {
      designerKey: 1,
      dbModel: this.initModel(),
      /** 根据用户界面配置生成data数据 开始 */
      editFormData: this.initEditData(),
      // 对话框属性变量
      dialogProps: {
        visible: false,
        title: '模板管理'
      },
      dialogTitle: '模板管理',
      // 选项变量

      /** 根据用户界面配置生成data数据 结束 */

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
      if (!validatenull(this.editFormData.json)) {
        this.editFormData.json = stringifyTemplate(this.editFormData.json)
      }

      let formData = this.createFormData(this.editFormData)
      saveDcTemplate(formData)
        .then((responseData) => {
          if (responseData.code == 100) {
            this.dialogProps.visible = false
            this.showMessage({
              type: 'success',
              msg: '保存成功'
            })
            this.$emit('save-finished')
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

    createFormData(editFormData) {
      let formData = new FormData()
      let deletes = []

      for (let idx in editFormData.fileIdFile.uploads) {
        formData.append('fileIdUploads', editFormData.fileIdFile.uploads[idx].raw)
      }
      deletes = deletes.concat(editFormData.fileIdFile.deletes)

      formData.append('entity', JSON.stringify(editFormData))
      formData.append('deleteIds', JSON.stringify(deletes))
      return formData
    },

    getFormById(id) {
      this.setLoad()
      return new Promise((resolve) => {
        getDcTemplateById(id)
          .then((responseData) => {
            let form = {}
            if (responseData.code == 100) {
              form = responseData.data
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
      this.dialogProps.visible = false
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs['editForm'].clearValidate()
      })
    },

    initOptions(This) {
      // 初始化自定义类型选择框选项变量
    },
    initEditData(This) {
      return {
        type: this.$route.params.typeParam, // 类别，0：前端，1：后台
        json: defaultTemplate, // 模板json
        name: '', // 名称
        remarks: '', // 详情
        fileId: null,
        fileIdFile: {
          deletes: [], // 待删除（已上传）的文件列表
          uploads: [] // 待上传的文件列表
        }
      }
    },
    initScheme() {
      return {
        name: '', // 方案名称
        packageName: '', // 包路径
        moduleName: '', // 模块名
        subModuleName: '', // 子模块名
        functionName: '', // 功能名
        template: {
          // 代码分类
          id: '',
          name: ''
        },
        templateJson: '', // 模板JSON
        remarks: '', // 备注信息
        dcTableList: [
          {
            tableEntity: {},
            name: '', // 表
            source: '', // 表来源
            scheme: {
              // 父表 代码方案
              id: '',
              name: ''
            },
            className: '', // 实体类名
            comments: '', // 说明
            parentTableFk: '', // 父表外键
            isMainTable: 0, // 是否主表（0：否，1：是）
            isAssociation: 0, // 是否关联表（0：否，1：是）
            associationField: '', // 对象字段
            filter: '', // 筛选条件
            constraintJson: '', // 表约束
            orderColumns: '', // 排序字段
            remarks: '', // 备注信息
            columnList: [], // 表字段
            constraintList: [] // 约束条件
          }
        ] // 子表列表，默认存在一个主表
      }
    },
    initModel() {
      return JSON.parse(
        JSON.stringify({
          ...this.initScheme(),
          ...{
            className: this.initScheme().dcTableList[0].className,
            table: {
              ...this.initScheme().dcTableList[0],
              columns: this.initScheme().dcTableList[0].columnList,
              constraints: [],
              orderbys: [],
              columnList: undefined,
              columnJson: undefined,
              constraint_List: undefined,
              constraintJson: undefined,
              constraintList: undefined,
              order_List: undefined
            },
            childTables: [],
            dcTableList: undefined
          }
        })
      )
    }
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on('openViewDialog', async function (id) {
        this.designerKey++
        this.action = 'view'
        this.dialogProps.title = `查看${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        if (!validatenull(this.editFormData.json)) {
          this.editFormData.json = parseTemplate(this.editFormData.json)
        } else {
          this.editFormData.json = defaultTemplate
        }
        this.initOptions(this.editFormData)
        this.dialogProps.visible = true
      })

      this.$on('openEditDialog', async function (id) {
        this.designerKey++
        this.action = 'edit'
        this.dialogProps.title = `修改${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        if (!validatenull(this.editFormData.json)) {
          this.editFormData.json = parseTemplate(this.editFormData.json)
        } else {
          this.editFormData.json = defaultTemplate
        }
        this.initOptions(this.editFormData)
        this.dialogProps.visible = true
      })
      this.$on('openAddDialog', function () {
        this.designerKey++
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = this.initEditData()
        this.initOptions(this.editFormData)
        this.dialogProps.visible = true
      })
      this.$on('openCopyDialog', async function (id) {
        this.designerKey++
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        if (!validatenull(this.editFormData.json)) {
          this.editFormData.json = parseTemplate(this.editFormData.json)
        } else {
          this.editFormData.json = defaultTemplate
        }
        this.initOptions(this.editFormData)
        this.editFormData.id = null //把id设置为空，添加一个新的

        this.editFormData.fileId = null

        this.dialogProps.visible = true
      })
    })
  }
}
</script>
<style scoped lang="scss">
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
