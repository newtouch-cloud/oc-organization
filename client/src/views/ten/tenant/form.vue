<template>
  <el-row class="dc-container">
    <el-dialog
      v-on:open="onDialogOpen"
      v-on:close="onDialogClose"
      v-loading="loading"
      width="70%"
      :fullscreen="false"
      :title="dialogProps.title"
      :visible.sync="dialogProps.visible"
      :close-on-click-modal="false"
      class="dc-el-dialog_ElDialog"
    >
      <el-row>
        <el-tabs v-model="tabName" type="border-card" tab-position="top" class="dc-el-tabs_ElTabs">
          <el-tab-pane label="租户" name="editForm" class="dc-el-tab-pane_ElTabPane">
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
                    autocomplete="new-password"
                    class="dc-el-input_SingleInput"
                  ></el-input>
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
          </el-tab-pane>
          <el-tab-pane label="租户管理员" name="tenantAdminTable" class="dc-el-tab-pane_ElTabPane">
            <el-row v-if="action != 'view'" gutter="0" type="flex" justify="end" align="top" class="dc-el-row_ElRow">
              <el-tooltip disabled class="dc-el-tooltip_Button">
                <el-button
                  type="primary"
                  icon="el-icon-plus"
                  v-on:click="onAddTenantAdminList"
                  class="dc-el-button_Button"
                ></el-button>
              </el-tooltip>
            </el-row>
            <el-form
              ref="tenantAdminForm"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <el-row>
                <ux-grid
                  :data="editFormData.tenantAdminList"
                  ref="tenantAdminTable"
                  :currentRow="table116CurrentRow"
                  height="200px"
                  border
                  :edit-config="{ trigger: 'click', mode: 'cell' }"
                  class="dc-ux-grid_EditTable"
                >
                  <ux-table-column
                    field=""
                    title="用户名"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    show-overflow
                    min-width="120px"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      <span style="color: #f56c6c">*</span>
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.user.name'"
                        label-width="0px"
                        :rules="[{ required: true, message: '用户名不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.name"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          autocomplete="new-password"
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'tenantAdminList.' + rowIndex + '.user.name'"
                          label-width="0px"
                          :rules="[{ required: true, message: '用户名不能为空', trigger: 'blur' }]"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.user.name }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field=""
                    title="登录名"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    min-width="120px"
                    show-overflow
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      <span style="color: #f56c6c">*</span>
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.user.loginName'"
                        label-width="0px"
                        :rules="[{ required: true, message: '登录名不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.loginName"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'tenantAdminList.' + rowIndex + '.user.loginName'"
                          label-width="0px"
                          :rules="[{ required: true, message: '登录名不能为空', trigger: 'blur' }]"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.user.loginName }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field=""
                    title="修改密码"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    show-overflow
                    min-width="80px"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_Switch"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.user.loginPasswordUpdate'"
                        label-width="0px"
                        class="dc-el-form-item_Switch"
                      >
                        <el-switch
                          v-model="row.user.loginPasswordUpdate"
                          :active-value="true"
                          :inactive-value="false"
                          class="dc-el-switch_Switch"
                        ></el-switch>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.user.loginPasswordUpdate'"
                        label-width="0px"
                        class="dc-el-form-item_Switch"
                      >
                        <el-switch
                          v-model="row.user.loginPasswordUpdate"
                          :active-value="true"
                          :inactive-value="false"
                          class="dc-el-switch_Switch"
                        ></el-switch>
                      </el-form-item>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field=""
                    title="密码"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    show-overflow
                    min-width="120px"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      <span style="color: #f56c6c">*</span>
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item v-if="row.user.loginPasswordUpdate == true"
                        :prop="'tenantAdminList.' + rowIndex + '.user.loginPassword'"
                        label-width="0px"
                        :rules="[{ required: true, message: '密码不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.loginPassword"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          show-password
                          autocomplete="new-password"
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <el-form-item v-if="row.user.loginPasswordUpdate == true"
                        :prop="'tenantAdminList.' + rowIndex + '.user.loginPassword'"
                        label-width="0px"
                        :rules="[{ required: true, message: '密码不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.loginPassword"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          show-password
                          autocomplete="new-password"
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field=""
                    title="确认密码"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    show-overflow
                    min-width="120px"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      <span style="color: #f56c6c">*</span>
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item v-if="row.user.loginPasswordUpdate == true"
                        :prop="'tenantAdminList.' + rowIndex + '.user.password'"
                        label-width="0px"
                        :rules="[
                          { required: true, message: '确认密码不能为空', trigger: 'blur' },
                          { pattern: new RegExp(`^${row.user.loginPassword}$`), message: '两次输入密码不一致!', trigger: 'blur' }
                        ]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.password"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          show-password
                          autocomplete="new-password"
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <el-form-item v-if="row.user.loginPasswordUpdate == true"
                        :prop="'tenantAdminList.' + rowIndex + '.user.password'"
                        label-width="0px"
                        :rules="[
                          { required: true, message: '确认密码不能为空', trigger: 'blur' },
                          { pattern: new RegExp(`^${row.user.loginPassword}$`), message: '两次输入密码不一致!', trigger: 'blur' }
                        ]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.password"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          show-password
                          autocomplete="new-password"
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field=""
                    title="手机号"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    show-overflow
                    min-width="120px"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      <span style="color: #f56c6c">*</span>
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.user.phone'"
                        label-width="0px"
                        :rules="[{ required: true, message: '手机号不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.phone"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'tenantAdminList.' + rowIndex + '.user.phone'"
                          label-width="0px"
                          :rules="[{ required: true, message: '手1机号不能为空', trigger: 'blur' }]"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.user.phone }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field=""
                    title="邮箱"
                    tree-node
                    resizable
                    align="left"
                    header-align="center"
                    show-overflow
                    min-width="120px"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      <span style="color: #f56c6c">*</span>
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.user.email'"
                        label-width="0px"
                        :rules="[{ required: true, message: '邮箱不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.email"
                          :maxLength="-1"
                          placeholder="请输入"
                          clearable
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'tenantAdminList.' + rowIndex + '.user.email'"
                          label-width="0px"
                          :rules="[{ required: true, message: '邮箱不能为空', trigger: 'blur' }]"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.user.email }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="remarks"
                    title="备注信息"
                    tree-node
                    resizable
                    min-width="120px"
                    align="left"
                    header-align="center"
                    show-overflow
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_MutilpleInput"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'tenantAdminList.' + rowIndex + '.remarks'"
                        label-width="0px"
                        class="dc-el-form-item_MutilpleInput"
                      >
                        <el-input
                          v-model="row.remarks"
                          type="textarea"
                          placeholder="请输入备注信息"
                          rows="2"
                          :maxLength="255"
                          class="dc-el-input_MutilpleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'tenantAdminList.' + rowIndex + '.remarks'"
                          label-width="0px"
                          class="dc-el-form-item_MutilpleInput"
                        >
                          {{ row.remarks }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    title="操作"
                    tree-node
                    resizable
                    width="80px"
                    fixed="right"
                    align="left"
                    header-align="center"
                    v-if="action != 'view'"
                    class="dc-ux-table-column_ElTableOptColumn"
                  >
                    <template v-slot:header="scope">
                      <span>操作</span>
                    </template>
                    <template slot-scope="scope">
                      <OperationIcon
                        v-on:click="onDeleteChild(editFormData.tenantAdminList, scope.rowIndex)"
                        type="danger"
                        content="删除"
                        placement="top"
                        icon-name="el-icon-delete"
                        class="dc-OperationIcon_IconButton"
                      ></OperationIcon>
                    </template>
                  </ux-table-column>
                </ux-grid>
              </el-row>
            </el-form>
          </el-tab-pane>
        </el-tabs>
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

/** 根据用户界面配置import组件 结束 */
import { getTenantById, saveTenant } from '@/api/ten/tenant'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'tenant-form',
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
        title: '租户'
      },
      dialogTitle: '租户',
      // 选项变量
      table116CurrentRow: {}, // 当前行
      tabName: 'editForm', // tab标签页
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
      let validFlag = true

      this.$refs['tenantAdminForm'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 'tenantAdminTable'
          validFlag = false
          return false
        }
      })

      this.$refs['editForm'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 'editForm'
          validFlag = false
          return false
        }
      })
      if (validFlag) {
        this.doSave()
      }
    },
    doSave() {
      this.setLoad()
      console.log(this.editFormData)
      saveTenant(this.editFormData)
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
        getTenantById(id)
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
    onDeleteChild(tableData, index) {
      // 根据下标删除子表数据
      tableData.splice(index, 1)
    },
    initEditData(This) {
      return {
        name: '', // 名称
        isLocked: '0', // 禁用
        remarks: '', // 备注信息
        tenantAdminList: []
      }
    },

    onAddTenantAdminList() {
      this.editFormData.tenantAdminList.push({
        user: {
          id: null,

          name: null,

          loginName: null,

          loginPassword: null,

          password: null,

          phone: null,

          email: null,

          loginPasswordUpdate: this.dialogProps && this.action == 'add' ? true : false // 是否修改密码
        },

        remarks: '' // 备注信息
      })
    }
  },
  watch: {
    tabName(val, oldVal) {
      this.$nextTick(() => {
        if (this.$refs[val] && this.$refs[val].doLayout) {
          this.$refs[val].doLayout()
        }
      })
    }
  },
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
        this.tabName = 'editForm'
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
        this.tabName = 'editForm'
        this.dialogProps.visible = true
      })
      this.$on('openAddDialog', function () {
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = this.initEditData()
        this.initOptions(this.editFormData)
        this.tabName = 'editForm'
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
        this.tabName = 'editForm'
        this.editFormData.id = null //把id设置为空，添加一个新的

        for (var i = 0; i <= this.editFormData.tenantAdminList.length - 1; i++) {
          this.editFormData.tenantAdminList[i].id = null
          this.editFormData.tenantAdminList[i].user.loginPasswordUpdate = true
        }

        this.dialogProps.visible = true
      })
    })
  }
}
</script>
<style>
.my-input-sc {
  display: inline-block;
  height: 30px;
  line-height: 30px;
  -webkit-appearance: none;
  background-color: #ffffff;
  background-image: none;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  box-sizing: border-box;
  color: #606266;
  font-size: inherit;
  outline: none;
  padding: 0 15px;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  width: 100%;
}
</style>
