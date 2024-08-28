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
          <el-tab-pane label="公司" name="editForm" class="dc-el-tab-pane_ElTabPane">
            <el-form
              ref="editForm"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <el-row>
                <el-form-item prop="parent.id" label="父级" class="dc-el-form-item_CascaderInput">
                  <el-cascader
                    :disabled="!permission.add"
                    ref="parentCascader"
                    :options="parentOptions"
                    v-model="editFormData.parent.id"
                    :style="{ width: '100%' }"
                    placeholder="请选择父级"
                    :props="{ label: 'name', value: 'id', children: 'children', checkStrictly: true, emitPath: false }"
                    clearable
                    value-key="id"
                    filterable
                    separator="/"
                    v-on:change="onParentChange"
                    class="dc-el-cascader_CascaderInput"
                  ></el-cascader>
                </el-form-item>
                <el-form-item
                  prop="code"
                  label="编号"
                  :rules="[{ required: true, message: '编号不能为空', trigger: 'blur' }]"
                  class="dc-el-form-item_SingleInput"
                >
                  <el-input
                    v-model="editFormData.code"
                    :maxLength="64"
                    placeholder="请输入编号"
                    clearable
                    autocomplete="new-password"
                    class="dc-el-input_SingleInput"
                  ></el-input>
                </el-form-item>
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
                <el-form-item
                  prop="fullName"
                  label="全称"
                  :rules="[{ required: true, message: '全称不能为空', trigger: 'blur' }]"
                  class="dc-el-form-item_SingleInput"
                >
                  <el-input
                    v-model="editFormData.fullName"
                    :maxLength="512"
                    placeholder="请输入全称"
                    clearable
                    autocomplete="new-password"
                    class="dc-el-input_SingleInput"
                  ></el-input>
                </el-form-item>
                <el-form-item prop="chairman" label="董事长" class="dc-el-form-item_SelectInput">
                  <el-select
                    v-model="editFormData.chairman"
                    :style="{ width: '100%' }"
                    placeholder="请选择董事长"
                    clearable
                    value-key="id"
                    filterable
                    class="dc-el-select_SelectInput"
                  >
                    <el-option
                      v-for="(item, index) in chairmanOptions"
                      :key="index"
                      :label="item.name"
                      :value="item"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item prop="manager" label="总经理" class="dc-el-form-item_SelectInput">
                  <el-select
                    v-model="editFormData.manager"
                    :style="{ width: '100%' }"
                    placeholder="请选择总经理"
                    clearable
                    value-key="id"
                    filterable
                    class="dc-el-select_SelectInput"
                  >
                    <el-option
                      v-for="(item, index) in managerOptions"
                      :key="index"
                      :label="item.name"
                      :value="item"
                    ></el-option>
                  </el-select>
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
          <el-tab-pane label="公司管理员" name="companyAdminTable" class="dc-el-tab-pane_ElTabPane">
            <el-row v-if="action != 'view'" gutter="0" type="flex" justify="end" align="top" class="dc-el-row_ElRow">
              <el-tooltip disabled class="dc-el-tooltip_Button">
                <el-button
                  type="primary"
                  icon="el-icon-plus"
                  v-on:click="onAddCompanyAdminList"
                  class="dc-el-button_Button"
                ></el-button>
              </el-tooltip>
            </el-row>
            <el-form
              ref="companyAdminForm"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <el-row>
                <ux-grid
                  :data="editFormData.companyAdminList"
                  ref="companyAdminTable"
                  :currentRow="table126CurrentRow"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.name'"
                        label-width="0px"
                        :rules="[{ required: true, message: '用户名不能为空', trigger: 'blur' }]"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.user.name"
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
                          :prop="'companyAdminList.' + rowIndex + '.user.name'"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.loginName'"
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
                          :prop="'companyAdminList.' + rowIndex + '.user.loginName'"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.loginPasswordUpdate'"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.loginPasswordUpdate'"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.loginPassword'"
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
                                    :prop="'companyAdminList.' + rowIndex + '.user.loginPassword'"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.password'"
                        label-width="0px"
                        :rules="[{ required: true, message: '确认密码不能为空', trigger: 'blur' }]"
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
                                    :prop="'companyAdminList.' + rowIndex + '.user.password'"
                                    label-width="0px"
                                    :rules="[{ required: true, message: '确认密码不能为空', trigger: 'blur' }]"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.phone'"
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
                          :prop="'companyAdminList.' + rowIndex + '.user.phone'"
                          label-width="0px"
                          :rules="[{ required: true, message: '手机号不能为空', trigger: 'blur' }]"
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
                        :prop="'companyAdminList.' + rowIndex + '.user.email'"
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
                          :prop="'companyAdminList.' + rowIndex + '.user.email'"
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
                        :prop="'companyAdminList.' + rowIndex + '.remarks'"
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
                          :prop="'companyAdminList.' + rowIndex + '.remarks'"
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
                    min-width="80px"
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
                        v-on:click="onDeleteChild(editFormData.companyAdminList, scope.rowIndex)"
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
import { treeCompany } from '@/api/org/company.js'
import { listUserAll } from '@/api/admin/user.js'
/** 根据用户界面配置import组件 结束 */
import { getCompanyById, saveCompany } from '@/api/org/company'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
export default {
  extends: BaseUI,
  name: 'company-form',
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
        title: '公司管理'
      },
      dialogTitle: '公司管理',
      // 选项变量
      // 父级选项
      parentOptions: [],
      // 董事长选项
      chairmanOptions: [],
      // 总经理选项
      managerOptions: [],
      table126CurrentRow: {}, // 当前行
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

      this.$refs['companyAdminForm'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 'companyAdminTable'
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

      saveCompany(this.editFormData)
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
        getCompanyById(id)
          .then((responseData) => {
            let form = {}
            if (responseData.code == 100) {
              form = responseData.data
              if (validatenull(form.parent)) {
                form.parent = {
                  id: null
                }
              }
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

    listParentOptions() {
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

      treeCompany(search_List).then((responseData) => {
        if (responseData.code == 100) {
          this.parentOptions = responseData.data
        } else {
          this.showMessage(responseData)
        }
      })
    },

    listChairmanOptions() {
      let search_List = {
        params: []
      }
      // filter条件
      search_List.params.push.apply(search_List.params, [
        {
          columnName: 'tenant_id',
          queryType: '=',
          value: currentUser.tenant.id
        },
        {
          columnName: 'company.id',
          queryType: '=',
          value: currentUser.company.id == '0' ? '' : currentUser.company.id
        },
        {
          columnName: 'department.id',
          queryType: '!=',
          value: '0'
        }
      ])

      // 数据权限: 用户sys_user
      this.pushDataPermissions(search_List.params, this.$route.meta.routerId, '1289059804542828547')

      listUserAll(search_List).then((responseData) => {
        if (responseData.code == 100) {
          this.chairmanOptions = responseData.data
        } else {
          this.showMessage(responseData)
        }
      })
    },

    listManagerOptions() {
      let search_List = {
        params: []
      }
      // filter条件
      search_List.params.push.apply(search_List.params, [
        {
          columnName: 'tenant_id',
          queryType: '=',
          value: currentUser.tenant.id
        },
        {
          columnName: 'company.id',
          queryType: '=',
          value: currentUser.company.id == '0' ? '' : currentUser.company.id
        },
        {
          columnName: 'department.id',
          queryType: '!=',
          value: '0'
        }
      ])

      // 数据权限: 用户sys_user
      this.pushDataPermissions(search_List.params, this.$route.meta.routerId, '1289059804542828547')

      listUserAll(search_List).then((responseData) => {
        if (responseData.code == 100) {
          this.managerOptions = responseData.data
        } else {
          this.showMessage(responseData)
        }
      })
    },

    initOptions(This) {
      // 初始化自定义类型选择框选项变量

      this.listParentOptions()

      this.listChairmanOptions()

      this.listManagerOptions()
    },
    onDeleteChild(tableData, index) {
      // 根据下标删除子表数据
      tableData.splice(index, 1)
    },
    onParentChange() {
      let nodes = this.$refs['parentCascader'].getCheckedNodes()
      if (nodes.length > 0) {
        if (nodes[0] && nodes[0].data && nodes[0].data.children) {
          this.editFormData.sort = (nodes[0].data.children.length + 1) * 10000
        } else if (nodes[0]) {
          this.editFormData.sort = 10000
        } else {
          this.editFormData.sort = (this.parentOptions.length + 1) * 10000
        }
      } else {
        this.editFormData.sort = (this.parentOptions.length + 1) * 10000
      }
    },
    initEditData(This) {
      return {
        tenant: {
          id: currentUser.tenant.id,
          name: null
        },
        // 父级
        parent: {
          id: validatenull(This) || validatenull(This.id) ? null : This.id,
          name: validatenull(This) || validatenull(This.name) ? null : This.name
        },
        code: '', // 编号
        name: '', // 名称
        fullName: '', // 全称
        chairman: {
          id: null,
          name: null
        },
        manager: {
          id: null,
          name: null
        },
        remarks: '', // 备注信息
        companyAdminList: []
      }
    },

    onAddCompanyAdminList() {
      this.editFormData.companyAdminList.push({
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
    parentOptions(newVal, oldVal) {
      if (newVal != oldVal && this.action == 'add') {
        this.$nextTick(() => {
          this.onParentChange()
        })
      }
    },
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
      this.$on('openAddDialog', function (parent) {
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = this.initEditData(parent)
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

        for (var i = 0; i <= this.editFormData.companyAdminList.length - 1; i++) {
          this.editFormData.companyAdminList[i].id = null
          this.editFormData.companyAdminList[i].user.loginPasswordUpdate = true
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
