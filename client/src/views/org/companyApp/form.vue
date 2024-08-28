<template>
  <el-row class="dc-container">
    <el-dialog
      v-on:open="onDialogOpen"
      v-on:close="onDialogClose"
      v-loading="loading"
      width="30%"
      :fullscreen="false"
      :title="dialogProps.title"
      :visible.sync="dialogProps.visible"
      :close-on-click-modal="false"
      class="dc-el-dialog_ElDialog"
    >
      <el-row>
        <el-form
          ref="editForm"
          :model="editFormData"
          label-width="100px"
          :disabled="action == 'view'"
          class="dc-el-form_ElEditForm"
        >
          <el-row>
            <el-form-item
              prop="company.id"
              label="公司"
              v-if="currentUser.company.id == '0'"
              :rules="[{ required: true, message: '公司不能为空', trigger: 'change' }]"
              class="dc-el-form-item_SelectInput"
            >
              <el-select
                v-model="editFormData.company"
                :style="{ width: '100%' }"
                placeholder="请选择公司"
                clearable
                value-key="id"
                filterable
                class="dc-el-select_SelectInput"
              >
                <el-option v-for="(item, index) in companyOptions" :key="index" :label="item.name" :value="item"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              prop="tenantAppVersion.id"
              label="应用版本"
              :rules="[{ required: true, message: '应用版本不能为空', trigger: 'change' }]"
              class="dc-el-form-item_SelectInput"
            >
              <el-select
                v-model="editFormData.tenantAppVersion"
                :style="{ width: '100%' }"
                placeholder="请选择应用版本"
                clearable
                value-key="id"
                filterable
                class="dc-el-select_SelectInput"
              >
                <el-option
                  v-for="(item, index) in tenantAppVersionOptions"
                  :key="index"
                  :label="`${item.applicationVersion.app.name}-${item.applicationVersion.name}`"
                  :value="item"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="isLocked" label="禁用" class="dc-el-form-item_Switch">
              <el-switch
                v-model="editFormData.isLocked"
                active-value="1"
                inactive-value="0"
                class="dc-el-switch_Switch"
              ></el-switch>
            </el-form-item>
            <el-form-item prop="remarks" label="备注信息" class="dc-el-form-item_MutilpleInput">
              <el-input
                v-model="editFormData.remarks"
                type="textarea"
                placeholder="请输入备注信息"
                rows="2"
                :maxLength="255"
                class="dc-el-input_MutilpleInput"
              ></el-input>
            </el-form-item>
          </el-row>
        </el-form>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button v-on:click="onSubmit" type="primary" v-show="action != 'view'">保存</el-button>
        <el-button v-on:click="onDialogClose" v-if="action != 'view'">取消</el-button>
        <el-button v-on:click="onDialogClose" v-if="action == 'view'">关闭</el-button>
      </span>
    </el-dialog>
  </el-row>
</template>
<script>
import { validatenull } from '@/utils/validate'
/** 根据用户界面配置import组件 结束 */
import { listCompanyAll } from '@/api/org/company.js'
import { listTenantAppAll, remoteListAll } from '@/api/ten/tenantApp.js'
/** 根据用户界面配置import组件 结束 */
import { getCompanyAppById, saveCompanyApp } from '@/api/org/companyApp'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'companyApp-form',
  components: {
    /** 根据用户界面配置组件 开始 */

    /** 根据用户界面配置组件 结束 */

    OperationIcon
  },
  data() {
    return {
      /** 根据用户界面配置生成data数据 开始 */
      editFormData: this.initEditData(),
      // 对话框属性变量
      dialogProps: {
        visible: false,
        title: '公司应用'
      },
      dialogTitle: '公司应用',
      // 选项变量
      // 公司选项
      companyOptions: [],
      // 应用版本选项
      tenantAppVersionOptions: [],
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
  computed: {},
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

      saveCompanyApp(this.editFormData)
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

    getFormById(id) {
      this.setLoad()
      return new Promise((resolve) => {
        getCompanyAppById(id)
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

    listCompanyOptions() {
      let search_List = {
        params: []
      }
      // filter条件
      search_List.params.push.apply(search_List.params, [
        {
          columnName: 'id',
          queryType: '=',
          value: currentUser.company.id == '0' ? '' : currentUser.company.id
        }
      ])

      // 数据权限: 公司org_company
      this.pushDataPermissions(search_List.params, this.$route.meta.routerId, '1287908822026887245')

      listCompanyAll(search_List).then((responseData) => {
        if (responseData.code == 100) {
          this.companyOptions = responseData.data
        } else {
          this.showMessage(responseData)
        }
      })
    },

    listTenantAppVersionOptions() {
      let search_List = {
        params: []
      }
      // filter条件
      search_List.params.push.apply(search_List.params, [
        {
          columnName: 'tenant_id',
          queryType: '=',
          value: currentUser.tenant.id
        }
      ])

      // 数据权限: 租户应用ten_tenant_app
      this.pushDataPermissions(search_List.params, this.$route.meta.routerId, '1391465689041903620')

      remoteListAll(search_List).then((responseData) => {
        if (responseData.code == 100) {
          this.tenantAppVersionOptions = responseData.data
        } else {
          this.showMessage(responseData)
        }
      })
    },

    initOptions(This) {
      // 初始化自定义类型选择框选项变量

      this.listCompanyOptions()

      this.listTenantAppVersionOptions()
    },
    initEditData(This) {
      return {
        company: {
          id: currentUser.company.id,
          name: null
        },
        tenantAppVersion: {
          id: null,
          tenantId: null,
          appVersionId: null,
          dbUrl: null,
          dbPassword: null,
          dbUsername: null,
          dbName: null,
          dbPort: null,
          dbIp: null,
          isLocked: null
        },
        isLocked: '0', // 禁用
        remarks: '' // 备注信息
      }
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
      })
      this.$on('openAddDialog', function (parent) {
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...parent
        }
        this.initOptions(this.editFormData)
        this.dialogProps.visible = true
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
      })
    })
  }
}
</script>
