<template>
  <el-row class="dc-container">
    <el-dialog
      v-on:open="onDialogOpen"
      v-on:close="onDialogClose"
      v-loading="loading"
      width="100%"
      fullscreen
      :title="dialogProps.title"
      :visible.sync="dialogProps.visible"
      :close-on-click-modal="false"
      class="dc-el-dialog_ElDialog"
    >
      <el-row style="height: 100%">
        <el-tabs v-model="tabName" type="border-card" tab-position="top" class="dc-el-tabs_ElTabs">
          <el-tab-pane label="基本信息" :name="1" :disabled="action != 'view'" class="dc-el-tab-pane_ElTabPane">
            <el-form
              ref="editForm"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <el-row>
                <el-col :span="8">
                  <el-form-item
                    prop="name"
                    label="名称"
                    :rules="[{ required: true, message: '名称不能为空', trigger: 'blur' }]"
                    class="dc-el-form-item_SingleInput"
                  >
                    <el-input
                      v-model="editFormData.name"
                      :maxLength="128"
                      placeholder="请输入方案名称"
                      clearable
                      class="dc-el-input_SingleInput"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    prop="functionName"
                    label="功能名"
                    :rules="[{ required: true, message: '功能名不能为空', trigger: 'blur' }]"
                    class="dc-el-form-item_SingleInput"
                  >
                    <el-input
                      v-model="editFormData.functionName"
                      :maxLength="128"
                      placeholder="请输入功能名"
                      clearable
                      class="dc-el-input_SingleInput"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    prop="packageName"
                    label="包路径"
                    :rules="[{ required: true, message: '包路径不能为空', trigger: 'blur' }]"
                    class="dc-el-form-item_SingleInput"
                  >
                    <el-input
                      v-model="editFormData.packageName"
                      :maxLength="512"
                      placeholder="请输入包路径"
                      clearable
                      class="dc-el-input_SingleInput"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item
                    prop="moduleName"
                    label="模块名"
                    :rules="[{ required: true, message: '模块名不能为空', trigger: 'blur' }]"
                    class="dc-el-form-item_SingleInput"
                  >
                    <el-input
                      v-model="editFormData.moduleName"
                      :maxLength="128"
                      placeholder="请输入模块名"
                      clearable
                      class="dc-el-input_SingleInput"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item prop="subModuleName" label="子模块名" class="dc-el-form-item_SingleInput">
                    <el-input
                      v-model="editFormData.subModuleName"
                      :maxLength="128"
                      placeholder="请输入子模块名"
                      clearable
                      class="dc-el-input_SingleInput"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="业务表" :name="2" :disabled="action != 'view'" class="dc-el-tab-pane_ElTabPane">
            <el-form
              ref="dcTableForm"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <div v-for="(table, index) in editFormData.dcTableList" :key="index">
                <el-row v-if="action != 'view'" gutter="0" type="flex" justify="end" align="top" class="dc-el-row_ElRow">
                  <el-tooltip v-if="table.isMainTable == 1" disabled class="dc-el-tooltip_Button">
                    <el-button
                      type="primary"
                      icon="el-icon-plus"
                      v-on:click="onAddDcTableRow(editFormData.dcTableList)"
                      class="dc-el-button_Button"
                    >
                      添加子表
                    </el-button>
                  </el-tooltip>
                  <el-tooltip v-if="table.isMainTable == 0" disabled class="dc-el-tooltip_Button">
                    <el-button
                      type="primary"
                      icon="el-icon-delete"
                      v-on:click="onRemoveDcTableRow(index)"
                      class="dc-el-button_Button"
                    >
                      移除
                    </el-button>
                  </el-tooltip>
                </el-row>
                <el-divider v-if="table.isMainTable == 1" content-position="left" class="dc-el-divider_ElDivider">
                  业务表
                </el-divider>
                <el-divider content-position="left" v-else class="dc-el-divider_ElDivider">子表</el-divider>
                <el-row>
                  <el-col :span="8">
                    <el-form-item
                      label="业务表"
                      :prop="'dcTableList.' + index + '.name'"
                      :rules="[{ required: true, message: '请选择业务表', trigger: 'change' }]"
                      class="dc-el-form-item_SelectInput"
                    >
                      <el-select
                        v-model="table.name"
                        value-key="name"
                        v-on:change="onTableChange(index, $event, table.isMainTable)"
                        filterable
                        clearable
                        class="dc-el-select_SelectInput"
                        placeholder="请选择名称"
                        :style='{"width":"100%"}'
                      >
                        <el-option
                          :disabled="
                            selectTableList
                              .map((item) => {
                                return item.value
                              })
                              .indexOf(item.name) != -1
                          "
                          v-for="item in tableName_List"
                          :key="item.name"
                          :label="item.name + ' : ' + item.comments"
                          :value="item"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      label="实体类名"
                      :prop="'dcTableList.' + index + '.className'"
                      :rules="[{ required: true, message: '实体类名不能为空', trigger: 'blur' }]"
                      class="dc-el-form-item_SingleInput"
                    >
                      <el-input
                        v-model="table.className"
                        :maxLength="128"
                        placeholder="请输入实体类名"
                        clearable
                        class="dc-el-input_SingleInput"
                      ></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      label="说明"
                      :prop="'dcTableList.' + index + '.comments'"
                      :rules="[{ required: true, message: '说明不能为空', trigger: 'blur' }]"
                      class="dc-el-form-item_SingleInput"
                    >
                      <el-input
                        v-model="table.comments"
                        :maxLength="512"
                        placeholder="请输入说明"
                        clearable
                        class="dc-el-input_SingleInput"
                      ></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8" v-if="table.isMainTable == 0">
                    <el-form-item
                      label="父表外键"
                      :prop="'dcTableList.' + index + '.parentTableFk'"
                      :rules="[{ required: true, message: '父表外键不能为空', trigger: 'change' }]"
                      class="dc-el-form-item_SelectInput"
                    >
                      <el-select
                        v-model="table.parentTableFk"
                        v-on:change="onFkChange(index, $event)"
                        value-key="name"
                        filterable
                        clearable
                        class="dc-el-select_SelectInput"
                        placeholder="请选择父表外键"
                        :style='{"width":"100%"}'
                      >
                        <el-option
                          v-for="item in table.columnList"
                          :key="item.name"
                          :label="item.name + ' : ' + item.comments"
                          :value="item.name"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      label="排序字段"
                      :prop="'dcTableList.' + index + '.orderColumns'"
                      class="dc-el-form-item_SelectInput"
                    >
                      <StringMultiSelect
                        v-model="table.orderColumns"
                        :opts="genOderColumns(table.columnList)"
                        valKey="name"
                        label="comments"
                        placeholder="请选排序字段"
                      ></StringMultiSelect>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8" v-if="table.updateDateExists">
                    <el-form-item label="覆盖检查" :prop="'dcTableList.' + index + '.checkVersion'">
                      <el-switch
                        v-model="table.checkVersion"
                        active-color="#13ce66"
                        inactive-color="#dbdfe6"
                        active-value="1"
                        inactive-value="0"
                        class="dc-el-switch_Switch"
                      ></el-switch>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8" v-if="table.isMainTable == 0">
                    <!-- 关联表和一对一表不能同时选中 -->
                    <el-form-item label="一对一表" :prop="'dcTableList.' + index + '.isOneToOne'">
                      <el-switch
                        :disabled="table.isAssociation == 1"
                        v-model="table.isOneToOne"
                        active-color="#13ce66"
                        inactive-color="#dbdfe6"
                        active-value="1"
                        inactive-value="0"
                        class="dc-el-switch_Switch"
                      ></el-switch>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8" v-if="table.isMainTable == 0">
                    <el-form-item label="关联表" :prop="'dcTableList.' + index + '.isAssociation'">
                      <el-switch
                        :disabled="table.isOneToOne == 1"
                        v-model="table.isAssociation"
                        active-color="#13ce66"
                        inactive-color="#dbdfe6"
                        active-value="1"
                        inactive-value="0"
                        class="dc-el-switch_Switch"
                      ></el-switch>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8" v-if="table.isAssociation == '1'">
                    <el-form-item
                      label="对象字段"
                      :prop="'dcTableList.' + index + '.associationField'"
                      :rules="[{ required: true, message: '对象字段不能为空', trigger: 'change' }]"
                      class="dc-el-form-item_SelectInput"
                    >
                      <el-select
                        v-model="table.associationField"
                        value-key="name"
                        filterable
                        clearable
                        placeholder="请选择对象字段"
                        class="dc-el-select_SelectInput"
                        :style='{"width":"100%"}'
                      >
                        <el-option
                          v-for="item in table.columnList"
                          :key="item.name"
                          :label="item.name + ' : ' + item.comments"
                          :value="item.name"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="筛选条件" :prop="'dcTableList.' + index + '.filter'">
                      <el-input
                        v-model="table.filter"
                        type="textarea"
                        placeholder="请输入筛选条件"
                        rows="2"
                        :maxLength="2000"
                        class="dc-el-input_SingleInput"
                      ></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="数据模型" :name="3" :disabled="action != 'view'" class="dc-el-tab-pane_ElTabPane">
            <el-container direction="horizontal" class="dc-el-container_ElContainer">
              <el-main class="dc-el-main_ElMain">
                <!--tabs切换  开始-->
                <el-radio-group
                  v-model="tableColumnIndex"
                  style="margin-bottom: 10px"
                  v-if="editFormData.dcTableList && editFormData.dcTableList.length > 1"
                >
                  <el-radio-button v-for="(table, index) in editFormData.dcTableList" :key="index" :label="index + 1">
                    {{ index == 0 ? '主表' : '子表' }}({{ table.name }})
                  </el-radio-button>
                </el-radio-group>
                <!--tabs切换  结束-->
              </el-main>
              <el-aside width="200px" height="60px" class="dc-el-aside_ElAside">
                <el-row gutter="0" type="flex" justify="end" align="top" class="dc-el-row_ElRow">
                  <el-tooltip disabled class="dc-el-tooltip_Button">
                    <el-button type="primary" @click="onRefreshColumn" icon="el-icon-refresh" class="dc-el-button_Button">
                      刷新
                    </el-button>
                  </el-tooltip>
                </el-row>
              </el-aside>
            </el-container>
            <el-form
              ref="dcTableColumnForm"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <div class="tab-item" v-for="(table, index) in editFormData.dcTableList" :key="index">
                <ux-grid
                  v-show="tableColumnIndex == index + 1"
                  :data="table.columnList"
                  ref="dcTable"
                  border
                  v-on:current-change="
                    (currentRow, oldCurrentRow) => {
                      dcTableColumnCurrentRow = currentRow
                    }
                  "
                  :edit-config="{ trigger: 'click', mode: 'cell' }"
                  :expand-config="{ expandAll: false }"
                  :row-class-name="setClassName"
                >
                  <ux-table-column field="" type="expand" width="40" resizable align="left">
                    <template v-slot:content="{ row }">
                      <el-form-item label-width="0">
                        <div v-if="row.childColumn && row.childColumn.length > 0">
                          关联字段：
                          <el-select
                            style="width: 120px"
                            :disabled="action == 'view'"
                            v-model="row.associatedField"
                            filterable
                            clearable
                            placeholder="请选择关联字段"
                          >
                            <el-option
                              v-for="(field, index) in row.childColumn"
                              :key="index"
                              :label="field.name"
                              :value="field.name"
                            ></el-option>
                          </el-select>
                        </div>
                        <el-checkbox-group
                          v-model="row.childFieldKeys"
                          style="margin-left: 10px"
                          :disabled="action == 'view'"
                          v-on:change="onChangeChildFiled(row)"
                        >
                          <el-checkbox v-for="column in row.childColumn" :label="column.name" :key="column.name">
                            <span style="margin-right: 10px">{{ column.name + column.comments }}</span>
                          </el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="name"
                    title="字段名"
                    min-width="180px"
                    resizable
                    align="left"
                    header-align="center"
                    class="dc-ux-table-column_ElTableColumn"
                  ></ux-table-column>
                  <ux-table-column
                    field="comments"
                    title="说明"
                    resizable
                    min-width="200px"
                    align="left"
                    header-align="center"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.comments'"
                        label-width="0px"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.comments"
                          :maxLength="500"
                          placeholder="请输入说明"
                          clearable
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.comments'"
                          label-width="0px"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.comments }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="jdbcType"
                    title="物理类型"
                    resizable
                    align="left"
                    min-width="160px"
                    header-align="center"
                    class="dc-ux-table-column_ElTableColumn"
                  ></ux-table-column>
                  <ux-table-column
                    field="javaType.value"
                    title="数据类型"
                    resizable
                    min-width="120px"
                    align="left"
                    header-align="center"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SelectInput"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.javaType'"
                        label-width="0px"
                        class="dc-el-form-item_SelectInput"
                      >
                        <el-select
                          v-model="row.javaType"
                          value-key="value"
                          v-on:change="onJavaTypeChange(row, $event, table.columnList)"
                          filterable
                          clearable
                          placeholder="请选择JAVA类型"
                          class="dc-el-select_SelectInput"
                          v-on:focus="setCurrentFocus(row, index)"
                        >
                          <el-option
                            v-for="javaType in javaType_DcTableColumn_List"
                            :key="javaType.value"
                            :label="javaType.name"
                            :value="javaType"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.javaType'"
                          label-width="0px"
                          class="dc-el-form-item_SelectInput"
                        >
                          {{ row.javaType.name }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="customType"
                    title="自定义类型"
                    resizable
                    min-width="180px"
                    align="left"
                    header-align="center"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SelectInput"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.customType'"
                        label-width="0px"
                        class="dc-el-form-item_SelectInput"
                      >
                        <!-- 选择本方案 -->
                        <el-select
                          v-if="row.javaType.value == 'This'"
                          v-model="row.currentSchemeTable"
                          filterable
                          clearable
                          class="dc-el-select_SelectInput"
                          placeholder="请选择"
                        >
                          <el-option
                            :disabled="action == 'view'"
                            v-for="(item, index) in editFormData.dcTableList"
                            :key="index"
                            :label="item.name"
                            :value="item.name"
                            v-on:click.native="onChangeCustomType(row, item)"
                          ></el-option>
                        </el-select>
                        <!-- 选择custom类型 -->
                        <el-select
                          v-else-if="row.javaType.value == 'Custom'"
                          value-key="id"
                          v-model="row.customType"
                          filterable
                          clearable
                          class="dc-el-select_SelectInput"
                          placeholder="请选择"
                          v-on:change="onChangeCustomType(row, $event)"
                        >
                          <el-option
                            :disabled="action == 'view'"
                            v-for="(item, index) in customType_DcTableColumn_List.filter(
                              (custom) => custom.scheme['name'] !== editFormData.name
                            )"
                            :key="index"
                            :label="item.className + (item.scheme ? '(' + item.scheme.name + ')' : '')"
                            :value="item"
                          ></el-option>
                        </el-select>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.customType'"
                          label-width="0px"
                          class="dc-el-form-item_SelectInput"
                        >
                          {{
                            row.javaType.value == 'This'
                              ? row.currentSchemeTable
                              : row.customType.id
                              ? row.customType.className + row.customType.scheme.name
                              : ''
                          }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="javaField"
                    title="属性名"
                    resizable
                    min-width="180px"
                    align="left"
                    header-align="center"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.javaField'"
                        label-width="0px"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.javaField"
                          :maxLength="1024"
                          placeholder="请输入"
                          clearable
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.javaField'"
                          label-width="0px"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.javaField }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="defVal"
                    title="默认值"
                    resizable
                    align="left"
                    min-width="120px"
                    header-align="center"
                    :edit-render="{ autofocus: '.el-input__inner' }"
                    class="dc-ux-table-column_SingleInput"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot:edit="{ row, rowIndex }">
                      <el-form-item
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.javaField'"
                        label-width="0px"
                        class="dc-el-form-item_SingleInput"
                      >
                        <el-input
                          v-model="row.defVal"
                          :maxLength="2000"
                          placeholder="请输入"
                          clearable
                          class="dc-el-input_SingleInput"
                        ></el-input>
                      </el-form-item>
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <span class="my-input-sc">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.javaField'"
                          label-width="0px"
                          class="dc-el-form-item_SingleInput"
                        >
                          {{ row.defVal }}
                        </el-form-item>
                      </span>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="isPk"
                    title="主键"
                    min-width="60px"
                    resizable
                    align="left"
                    header-align="center"
                    class="dc-ux-table-column_ElTableColumn"
                  >
                    <template v-slot="{ row, rowIndex }">
                      <div
                        style="color: #f56c6c"
                        v-html="
                          ((cellValue, row, index) => {
                            switch (cellValue) {
                              case '1':
                                return '√'
                              default:
                                return ''
                            }
                          })(row.isPk, row, rowIndex)
                        "
                      ></div>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    class="dc-ux-table-column_ElTableColumn"
                    field="isNull"
                    title="可空"
                    min-width="60px"
                    resizable
                    align="left"
                    header-align="center"
                  >
                    <template v-slot="{ row, rowIndex }">
                      <div
                        style="color: #f56c6c"
                        v-html="
                          ((cellValue, row, index) => {
                            switch (cellValue) {
                              case '1':
                                return '√'
                              default:
                                return ''
                            }
                          })(row.isNull, row, rowIndex)
                        "
                      ></div>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="isInsert"
                    title="可插"
                    resizable
                    min-width="80px"
                    align="left"
                    header-align="center"
                    class="dc-ux-table-column_Switch"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <el-form-item
                        v-if="action != 'view'"
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.isInsert'"
                        label-width="0px"
                        class="dc-el-form-item_Switch"
                      >
                        <el-switch
                          v-model="row.isInsert"
                          active-color="#13ce66"
                          inactive-color="#dbdfe6"
                          active-value="1"
                          inactive-value="0"
                          class="dc-el-switch_Switch"
                        ></el-switch>
                      </el-form-item>
                      <li v-else class="el-icon-check" style="color: #f56c6c"></li>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    field="isEdit"
                    title="可编"
                    min-width="80px"
                    resizable
                    align="left"
                    header-align="center"
                    class="dc-ux-table-column_Switch"
                  >
                    <template v-slot:header="{ column }">
                      {{ column.title }}
                    </template>
                    <template v-slot="{ row, rowIndex }">
                      <el-form-item
                        v-if="action != 'view'"
                        :prop="'dcTableList.' + index + '.columnList.' + rowIndex + '.isEdit'"
                        label-width="0px"
                        class="dc-el-form-item_Switch"
                      >
                        <el-switch
                          v-model="row.isEdit"
                          active-color="#13ce66"
                          inactive-color="#dbdfe6"
                          active-value="1"
                          inactive-value="0"
                          class="dc-el-switch_Switch"
                        ></el-switch>
                      </el-form-item>
                      <li v-else class="el-icon-check" style="color: #f56c6c"></li>
                    </template>
                  </ux-table-column>
                  <ux-table-column
                    title="操作"
                    tree-node
                    resizable
                    width="50px"
                    fixed="right"
                    align="center"
                    header-align="center"
                    class="dc-ux-table-column_ElTableOptColumn"
                  >
                    <template slot-scope="scope">
                      <OperationIcon
                        v-on:click="onRemoveColumn(scope)"
                        type="danger"
                        content="提示信息"
                        placement="bottom"
                        icon-name="el-icon-delete"
                        class="dc-OperationIcon_IconButton"
                      ></OperationIcon>
                    </template>
                  </ux-table-column>
                </ux-grid>
              </div>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="约束条件" :name="4" :disabled="action != 'view'" class="dc-el-tab-pane_ElTabPane">
            <el-form
              ref="dcConstraint"
              :model="editFormData"
              label-width="100px"
              :disabled="action == 'view'"
              class="dc-el-form_ElEditForm"
            >
              <div v-for="(table, index) in editFormData.dcTableList" :key="index">
                <el-divider content-position="left" class="dc-el-divider_ElDivider">{{table.name + ': ' + table.comments}}</el-divider>
                <el-row>
                  <ux-grid
                    :data="table.constraintList"
                    ref="dcTableConstraint"
                    border
                    :key="constraintKey"
                    v-on:current-change="
                      (currentRow, oldCurrentRow) => {
                        dcTableColumnCurrentRow = currentRow
                      }
                    "
                    :edit-config="{ trigger: 'click', mode: 'cell' }"
                  >
                    <ux-table-column
                      field="name"
                      title="名称"
                      resizable
                      min-width="180px"
                      align="left"
                      header-align="center"
                      :edit-render="{ autofocus: '.el-input__inner' }"
                    >
                      <template v-slot:header="{ column }">
                        <span style="color: #f56c6c">*</span>
                        {{ column.title }}
                      </template>
                      <template v-slot:edit="{ row, rowIndex }">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.constraintList.' + rowIndex + '.name'"
                          label-width="0px"
                          :rules="[{ required: true, message: '名称不能为空', trigger: 'blur' }]"
                        >
                          <el-input v-model="row.name" :maxLength="200" placeholder="请输入名称" clearable></el-input>
                        </el-form-item>
                      </template>
                      <template v-slot="{ row, rowIndex }">
                        <span class="my-input-sc">
                          <el-form-item
                            :prop="'dcTableList.' + index + '.constraintList.' + rowIndex + '.name'"
                            label-width="0px"
                            :rules="[{ required: true, message: '名称不能为空', trigger: 'blur' }]"
                          >
                            {{ row.name }}
                          </el-form-item>
                        </span>
                      </template>
                    </ux-table-column>
                    <ux-table-column
                      field="comments"
                      title="描述"
                      resizable
                      min-width="200px"
                      align="left"
                      header-align="center"
                      :edit-render="{ autofocus: '.el-input__inner' }"
                    >
                      <template v-slot:header="{ column }">
                        {{ column.title }}
                      </template>
                      <template v-slot:edit="{ row, rowIndex }">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.constraintList.' + rowIndex + '.comments'"
                          label-width="0px"
                        >
                          <el-input v-model="row.comments" :maxLength="500" placeholder="请输入描述" clearable></el-input>
                        </el-form-item>
                      </template>
                      <template v-slot="{ row, rowIndex }">
                        <span class="my-input-sc">
                          <el-form-item
                            :prop="'dcTableList.' + index + '.constraintList.' + rowIndex + '.comments'"
                            label-width="0px"
                          >
                            {{ row.comments }}
                          </el-form-item>
                        </span>
                      </template>
                    </ux-table-column>
                    <ux-table-column
                      field="constraintColumn"
                      title="约束字段"
                      min-width="180px"
                      resizable
                      align="left"
                      header-align="center"
                      :edit-render="{ autofocus: '.el-input__inner' }"
                    >
                      <template v-slot:header="{ column }">
                        {{ column.title }}
                      </template>
                      <template v-slot:edit="{ row, rowIndex }">
                        <el-form-item
                          :prop="'dcTableList.' + index + '.constraintList.' + rowIndex + '.constraintColumn'"
                          :rules="[{ required: true, type: '', message: '请选择约束字段', trigger: 'change' }]"
                          label-width="0px"
                          class="dc-el-form-item_SelectInput"
                        >
                          <StringMultiSelect
                            v-model="row.constraintColumn"
                            :opts="table.columnList"
                            valKey="name"
                            label="name"
                            placeholder="请选择约束字段"
                          ></StringMultiSelect>
                        </el-form-item>
                      </template>
                      <template v-slot="{ row, rowIndex }">
                        <span class="my-input-sc">
                          <el-form-item
                            :prop="'dcTableList.' + index + '.constraintList.' + rowIndex + '.constraintColumn'"
                            :rules="[{ required: true, type: '', message: '请选择约束字段', trigger: 'change' }]"
                            label-width="0px"
                            class="dc-el-form-item_SelectInput"
                          >
                            {{ row.constraintColumn }}
                          </el-form-item>
                        </span>
                      </template>
                    </ux-table-column>
                    <ux-table-column
                      title="操作"
                      resizable
                      min-width="80px"
                      fixed="right"
                      align="left"
                      header-align="center"
                      v-if="action != 'view'"
                      class="dc-ux-table-column_ElTableOptColumn"
                    >
                      <template v-slot:header="scope">
                        <span>操作</span>
                        <OperationIcon
                          v-on:click="onAddConstraintRow(table.constraintList)"
                          type="primary"
                          content="添加"
                          placement="top"
                          icon-name="el-icon-plus"
                          class="dc-OperationIcon_IconButton"
                        ></OperationIcon>
                      </template>
                      <template slot-scope="scope">
                        <OperationIcon
                          v-on:click="onDeleteConstraintRow(scope.$index, table.constraintList)"
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
              </div>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-row>
      <!-- 按钮  开始-->
      <span slot="footer" class="dialog-footer">
        <el-button-group class="btnGroup" v-if="action != 'view'">
          <el-button :disabled="tabName == 1" icon="el-icon-arrow-left" :plain="true" v-on:click="doPrevStep">上一步</el-button>
          <el-button :disabled="tabName == 4" :plain="true" v-on:click="doNextStep">
            下一步
            <i class="el-icon-arrow-right el-icon--right"></i>
          </el-button>
        </el-button-group>
        <el-button v-if="action != 'view' && tabName == 4" type="primary" :plain="true" v-on:click="onSubmit">保 存</el-button>
        <el-button v-if="action != 'view'" :plain="true" v-on:click="onDialogClose()">取 消</el-button>
        <el-button v-if="action == 'view'" :plain="true" v-on:click="onDialogClose()">关 闭</el-button>
      </span>
      <!-- 按钮 结束-->
    </el-dialog>
  </el-row>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { mapGetters } from 'vuex'
/** 根据用户界面配置import组件 结束 */
import { listSchemaTableAll } from '@/api/dc/schemaTable'
import { listDictItemAll } from '@/api/sys/dictItem'
/** 根据用户界面配置import组件 结束 */
import { listDcSchemeAll, getDcSchemeById, saveDcScheme } from '@/api/dc/dcScheme'
import { listTableColumn, listTableAndColumn } from '@/api/dc/dcTable'
import BaseUI from '@/views/components/baseUI'
import OperationIcon from '@/components/OperationIcon'
import StringMultiSelect from '@/components/StringMultiSelect'

const renderCode = window['vue-codegenerator'].renderCode

export default {
  extends: BaseUI,
  name: 'dcScheme-form',
  components: {
    /** 根据用户界面配置组件 开始 */

    /** 根据用户界面配置组件 结束 */
    StringMultiSelect,
    OperationIcon
  },
  data() {
    return {
      /** 根据用户界面配置生成data数据 开始 */
      editFormData: this.initEditData(),
      dbModel: {},
      selectTableList: [], // 已选中的业务表集合
      tableColumnIndex: 1, // 数据模型中，主表子表tab切换
      scheme_DcTable_List: [], // 代码方案
      customType_DcTableColumn_List: [], // 自定义类型
      // 对话框属性变量
      dialogProps: {
        visible: false,
        title: '后端方案'
      },
      dialogTitle: '后端方案',
      editFormData: {
        table239Data: []
      },
      constraintKey: 1,
      // 选项变量
      // 业务表选项
      tableName_List: [],
      dcTableColumnCurrentRow: {}, // 当前行
      // null选项
      javaType_DcTableColumn_List: [],
      tabName: 1, // tab标签页
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
    ...mapGetters(['settings']),
    tabOptionBtnTop() {
      let top
      switch (this.settings.size) {
        case 'medium':
          top = -46 + 'px'
          break
        case 'small':
          top = -42 + 'px'
          break
        case 'mini':
          top = -38 + 'px'
          break
        default:
          top = -50 + 'px'
      }
      return top
    }
  },
  methods: {
    doPrevStep() {
      // 上一步
      // this.tabIndex--
      this.tabName--
    },
    doNextStep() {
      // 下一步
      if (this.tabName == 1) {
        this.$refs['editForm'].validate((valid, object) => {
          if (valid) {
            this.tabName++
          }
        })
      } else if (this.tabName == 2) {
        this.$refs['dcTableForm'].validate((valid, object) => {
          if (valid) {
            this.tabName++
          }
        })
      } else if (this.tabName == 3) {
        this.$refs['dcTableColumnForm'].validate((valid, object) => {
          if (valid) {
            this.tabName++
          }
        })
      } else {
        this.tabName++
      }
    },

    onSubmit() {
      let validFlag = true
      this.$refs['dcTableForm'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 2
          validFlag = false
          return false
        }
      })

      this.$refs['dcTableColumnForm'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 3
          validFlag = false
          return false
        }
      })

      this.$refs['dcConstraint'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 4
          validFlag = false
          return false
        }
      })

      this.$refs['editForm'].validate((valid) => {
        if (valid) {
        } else {
          this.tabName = 1
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
      if (!validatenull(this.editFormData.dcTableList)) {
        for (let i = 0; i < this.editFormData.dcTableList.length; i++) {
          this.editFormData.dcTableList[i].constraintJson = JSON.stringify(this.editFormData.dcTableList[i].constraintList)
          this.editFormData.dcTableList[i].columnList.forEach((column) => {
            this.$delete(column, 'childColumn')
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

      saveDcScheme(this.editFormData)
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

    onAddDcTableRow(tableData) {
      tableData.push(this.initDcTableRow(0)) // 后期添加都为子表
    },
    onRemoveDcTableRow(index) {
      // 移除子表
      this.editFormData.dcTableList.splice(index, 1)
    },
    getFormById(id) {
      this.setLoad()
      return new Promise((resolve) => {
        getDcSchemeById(id)
          .then((responseData) => {
            let form = {}
            if (responseData.code == 100) {
              form = responseData.data
              if (!validatenull(form.dcTableList)) {
                for (let i = 0; i < form.dcTableList.length; i++) {
                  this.$set(form.dcTableList[i], 'constraintList', JSON.parse(form.dcTableList[i].constraintJson))
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
    initDcTableRow(isMainTable) {
      return {
        tableEntity: {},
        name: '', // 表
        source: '', // 表来源
        scheme: {
          // 父表 代码方案
          id: validatenull(parent) || validatenull(parent.scheme) ? null : parent.scheme.id,
          name: validatenull(parent) || validatenull(parent.scheme) ? null : parent.scheme.name
        },
        className: '', // 实体类名
        comments: '', // 说明
        parentTableFk: '', // 父表外键
        isMainTable: isMainTable, // 是否主表（0：否，1：是）
        isAssociation: 0, // 是否关联表（0：否，1：是）
        isOneToOne: 0, // 是否一对一表（0：否，1：是）
        associationField: '', // 对象字段
        checkVersion: '0',
        filter: '', // 筛选条件
        constraintJson: '', // 表约束
        orderColumns: '', // 排序字段
        remarks: '', // 备注信息
        columnList: [], // 表字段
        constraintList: [] // 约束条件
      }
    },
    onDeleteRow(index, tableData) {
      tableData.splice(index, 1)
    },
    onAddConstraintRow(tableData) {
      tableData.push({
        name: '', // 名称
        comments: '', // 描述
        constraintColumn: '', // 约束字段
        remarks: '' // 备注信息
      })
      this.constraintKey++
    },
    onDeleteConstraintRow(index, tableData) {
      tableData.splice(index, 1)
    },
    async onTableChange(index, newValue, isMainTable) {
      // 设置业务表字段不可重复选择
      let selectDataIndex = this.selectTableList.findIndex((item) => {
        if (item.key == index) {
          return true
        }
      })
      if (selectDataIndex != -1) {
        this.selectTableList[selectDataIndex].value = newValue.name
      } else {
        this.selectTableList.push({
          key: index,
          value: newValue.name
        })
      }
      let ThisTable = this.editFormData.dcTableList[index]
      ThisTable.parentTableFk = '' // 父表外键重置
      if (validatenull(newValue)) {
        ThisTable.name = '' // 表
        ThisTable.comments = '' // 说明
        ThisTable.source = '' // 表来源
        ThisTable.className = '' // 表来源
        ThisTable.checkVersion = '0' // 版本检查
        ThisTable = this.initDcTableRow(isMainTable)
        // this.editFormData = this.initFormModel()
      } else {
        this.loading = true
        // 初始化类名
        ThisTable.name = newValue.name // 表
        ThisTable.comments = newValue.comments // 说明
        ThisTable.source = newValue.source // 表来源
        ThisTable.className =
          ThisTable.name.substring(0, 1).toUpperCase() +
          ThisTable.name.substring(1).replace(/\_(\w)/g, function (all, letter) {
            return letter.toUpperCase()
          })
        try {
          let search = {
            params: [
              {
                columnName: 'table_name',
                queryType: '=',
                value: newValue.name
              }
            ]
          }
          await listTableColumn(search).then((responseData) => {
            if (responseData.code == 100) {
              ThisTable.isMainTable = isMainTable
              ThisTable.isAssociation = 0
              ThisTable.isOneToOne = 0
              ThisTable.columnList = responseData.data
              if (!validatenull(ThisTable.columnList)) {
                let column = ThisTable.columnList.find((item) => {
                  return item.name == 'update_date'
                })
                ThisTable.updateDateExists = !validatenull(column) ? true : false
                ThisTable.checkVersion = !validatenull(column) ? '1' : '0'
              }
            } else {
              this.showMessage(responseData)
            }
          })
          this.loading = false
        } catch (error) {
          this.outputError(error)
        }
      }
    },
    genOderColumns(columns) {
      let list = []
      columns.forEach((col) => {
        list.push({
          name: 'a.' + col.name + ' ASC',
          comments: col.comments + ' 升序'
        })
        list.push({
          name: 'a.' + col.name + ' DESC',
          comments: col.comments + ' 降序'
        })
      })
      return list
    },
    onFkChange(index, newValue) {
      let ThisTable = this.editFormData.dcTableList[index]
      if (newValue && newValue != 'id') {
        ThisTable.columnList.forEach(function (item, index, array) {
          if (item.name == newValue && item.javaType.value != 'Custom') {
            item.javaType = {
              id: '8100',
              value: 'Custom',
              name: 'Custom'
            }
            if (item.javaField.substring(item.javaField.length - 2, item.javaField.length) == 'Id') {
              item.javaField = item.javaField.substring(0, item.javaField.length - 2)
            }
          }
        })
      }
    },
    onJavaTypeChange(row, javaType) {
      row.currentSchemeTable = '' // 清空本方案
      row.customType = '' // 自定义类型
      row.childFields = [] // 选中的关联表字段
      this.$set(row, 'childFieldKeys', []) // 选中的关联表字段Key
      row.childColumn = [] // 关联表字段
      row.associatedField = '' // 关联表对应的关联字段
      if (javaType && (javaType.value == 'This' || javaType.value == 'Custom')) {
        // 8031：代表本方案 8006: custom
        if (
          this.dcTableColumnCurrentRow.javaField.substring(
            this.dcTableColumnCurrentRow.javaField.length - 2,
            this.dcTableColumnCurrentRow.javaField.length
          ) == 'Id'
        ) {
          this.dcTableColumnCurrentRow.javaField = this.dcTableColumnCurrentRow.javaField.substring(
            0,
            this.dcTableColumnCurrentRow.javaField.length - 2
          )
        }
      }
    },
    setCurrentFocus(row, index) {
      if (this.$refs.dcTable.length > 0) {
        this.$refs.dcTable[index].setCurrentRow(row)
      }
    },
    setClassName({ row, rowIndex }) {
      // 在这里写判断条件，控制哪些行可展开，那些行不可展开
      if (row.javaType && (row.javaType.value == 'This' || row.javaType.value == 'Custom')) {
        return '' // 可展开
      } else {
        return 'hide-expand' // 不可展开
      }
    },
    onChangeCustomType(row, val) {
      row.childFields = [] // 选中的关联表字段
      this.$set(row, 'childFieldKeys', []) // 选中的关联表字段Key
      row.childColumn = [] // 关联表字段
      row.associatedField = '' // 关联表对应的关联字段
      if (!validatenull(val.columnList)) {
        this.$set(row, 'childColumn', val.columnList) // 关联表字段
        let childFieldKeys = []
        let childFields = []
        val.columnList.forEach((item) => {
          if (item.name == 'id' || item.name == 'name') {
            childFieldKeys.push(item.name)
            childFields.push({
              label: item.comments,
              value: item.name,
              javaType: item.javaType,
              dcTable: item.dcTable,
              associatedField: item.associatedField,
              customType: item.customType,
              decimalLenth: item.decimalLenth
            })
          }
        })
        this.$set(row, 'childFieldKeys', childFieldKeys) // 已选中字段Keys
        this.$set(row, 'childFields', childFields) // 已选中字段
        this.$set(row, 'associatedField', 'id') // 已选中字段
      }
    },
    onChangeChildFiled(row) {
      // 修改选中的关联表值
      if (!validatenull(row.childFieldKeys) && !validatenull(row.childColumn)) {
        row.childFields = []
        row.childFieldKeys.forEach((field) => {
          row.childColumn.forEach((column) => {
            if (field === column.name) {
              row.childFields.push({
                label: column.comments,
                value: column.name,
                javaType: column.javaType,
                dcTable: column.dcTable,
                associatedField: column.associatedField,
                customType: column.customType,
                decimalLenth: column.decimalLenth
              })
            }
          })
        })
      }
    },
    initOptions(This) {
      // 初始化自定义类型选择框选项变量
      this.getTableList() // 业务表

      this.getJavaTypeList() // java类型

      this.getCustomList() // 自定义类型

      this.getSchemeList() // 代码方案
    },
    getTableList() {
      // 选中的业务表清空
      this.selectTableList = []
      let st_search = {
        params: []
      }
      this.tableName_List = []
      listSchemaTableAll(st_search).then((responseData) => {
        this.tableName_List = responseData.data
      })
    },
    getJavaTypeList() {
      let javaType_search = {
        params: [
          {
            columnName: 'dict_type_id',
            queryType: '=',
            value: '5006'
          }
        ]
      }
      this.javaType_DcTableColumn_List = []
      listDictItemAll(javaType_search).then((responseData) => {
        this.javaType_DcTableColumn_List = responseData.data
      })
    },
    getSchemeList() {
      let scheme_DcTable_search = {
        params: []
      }
      // 数据权限: 低代码-代码方案dc_scheme
      this.pushDataPermissions(scheme_DcTable_search.params, this.$route.meta.routerId, '1088722824589459486')
      this.scheme_DcTable_List.splice(0, this.scheme_DcTable_List.length)
      listDcSchemeAll(scheme_DcTable_search).then((responseData) => {
        this.scheme_DcTable_List = responseData.data
      })
    },
    getCustomList() {
      let customType_search = {
        params: []
      }
      this.customType_DcTableColumn_List = []
      listTableAndColumn(customType_search).then((responseData) => {
        this.customType_DcTableColumn_List = responseData.data
        // 查看、修改JAVA类型为custom、thisObj时，自定义类型关联表字段回显问题。
        if (this.action == 'edit' || this.action == 'view' || this.action == 'copy') {
          this.editFormData.dcTableList.forEach((table) => {
            table.columnList.forEach((column) => {
              this.$set(column, 'childColumn', []) // 关联表字段
              if (column.javaType.value == 'Custom') {
                // custom
                let custom = this.customType_DcTableColumn_List.find((item) => {
                  return item.id == column.customType.id //取出customType_DcTableColumn_List里的id相等的那条数据所有的信息
                })
                if (!validatenull(custom)) {
                  const childField = JSON.parse(column.childFields)
                  this.$set(column, 'childFields', JSON.parse(column.childFields)) // 关联表字段
                  const childFieldKeys = []
                  if (!validatenull(childField)) {
                    childField.forEach((item) => {
                      childFieldKeys.push(item.value)
                    })
                  }
                  this.$set(column, 'childFieldKeys', childFieldKeys) // 关联表字段
                  this.$set(column, 'childColumn', custom.columnList) // 关联表字段
                }
              } else if (column.javaType.value == 'This' && !validatenull(column.currentSchemeTable)) {
                // thisObj
                let schemeTable = this.editFormData.dcTableList.find((item) => {
                  return item.name == column.currentSchemeTable
                })
                if (!validatenull(schemeTable) && !validatenull(schemeTable.columnList)) {
                  this.$set(column, 'childColumn', JSON.parse(JSON.stringify(schemeTable.columnList))) // 关联表字段
                  const childField = JSON.parse(column.childFields)
                  this.$set(column, 'childFields', JSON.parse(column.childFields)) // 关联表字段
                  const childFieldKeys = []
                  if (!validatenull(childField)) {
                    childField.forEach((item) => {
                      childFieldKeys.push(item.value)
                    })
                  }
                  this.$set(column, 'childFieldKeys', childFieldKeys) // 关联表字段
                }
              }
            })
          })
        }
      })
    },
    onDeleteChild(tableData, index) {
      // 根据下标删除子表数据
      tableData.splice(index, 1)
    },
    initEditData(This) {
      return {
        name: '', // 方案名称
        packageName: '', // 包路径
        moduleName: '', // 模块名
        subModuleName: '', // 子模块名
        functionName: '', // 功能名
        template: {
          // 代码分类
          id: null,
          name: ''
        },
        templateJson: '', // 模板JSON
        remarks: '', // 备注信息
        dcTableList: [this.initDcTableRow(1)] // 子表列表，默认存在一个主表
      }
    },
    onRefreshColumn() {
      const currentTable = this.editFormData.dcTableList[this.tableColumnIndex - 1]
      if (!validatenull(currentTable.name)) {
        this.loading = true
        let search = {
          params: [
            {
              columnName: 'table_name',
              queryType: '=',
              value: currentTable.name
            }
          ]
        }
        listTableColumn(search).then((responseData) => {
          if (responseData.code == 100) {
            const newColumns = []
            responseData.data.forEach((item) => {
              if (!currentTable.columnList.some((oldItem) => item.name == oldItem.name)) {
                newColumns.push(item)
              }
            })
            currentTable.columnList.push.apply(currentTable.columnList, newColumns)
            this.loading = false
          } else {
            this.showMessage(responseData)
          }
        })
      }
    },
    onRemoveColumn(scope) {
      const currentTable = this.editFormData.dcTableList[this.tableColumnIndex - 1]
      currentTable.columnList.splice(scope.rowIndex, 1)
    }
  },
  watch: {
    tabName(val, oldVal) {
      this.$nextTick(() => {
        if (val == 4) {
          if (this.$refs['dcTableConstraint']) {
            for (let i = 0; i < this.$refs['dcTableConstraint'].length; i++) {
              if (this.$refs['dcTableConstraint'][i].doLayout) {
                this.$refs['dcTableConstraint'][i].doLayout()
              }
            }
          }
        } else if (val == 3) {
          if (this.$refs['dcTable']) {
            for (let i = 0; i < this.$refs['dcTable'].length; i++) {
              if (this.$refs['dcTable'][i].doLayout) {
                this.$refs['dcTable'][i].doLayout()
              }
            }
          }
        }
      })
    },
    tableColumnIndex(val, oldVal) {
      if (val != oldVal) {
        this.$refs.dcTable[val - 1].refreshColumn()
      }
    }
  },
  mounted: function () {
    this.$nextTick(() => {
      this.$on('openViewDialog', async function (id) {
        this.action = 'view'
        this.dialogProps.title = `查看${this.dialogTitle}`
        const formEntity = await this.getFormById(id)
        if (!validatenull(formEntity)) {
          this.editFormData = {
            ...this.initEditData(),
            ...formEntity
          }
          this.initOptions(this.editFormData)
          this.tabName = 1
          this.tableColumnIndex = 1
          this.dialogProps.visible = true
          console.log('bizFormModel = ', this.editFormData)
          // console.log('bizFormModel = ', JSON.stringify(this.editFormData).replace(/[\\]/g,''))
        }
      })

      this.$on('openEditDialog', async function (id) {
        this.action = 'edit'
        this.dialogProps.title = `修改${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        this.initOptions(this.editFormData)
        this.tabName = 1
        this.tableColumnIndex = 1
        this.dialogProps.visible = true
      })
      this.$on('openAddDialog', function () {
        this.action = 'add'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = this.initEditData()
        this.initOptions(this.editFormData)
        this.tabName = 1
        this.tableColumnIndex = 1
        this.dialogProps.visible = true
      })
      this.$on('openCopyDialog', async function (id) {
        this.action = 'copy'
        this.dialogProps.title = `添加${this.dialogTitle}`
        this.editFormData = {
          ...this.initEditData(),
          ...(await this.getFormById(id))
        }
        this.initOptions(this.editFormData)
        this.tabName = 1
        this.tableColumnIndex = 1
        this.editFormData.id = null //把id设置为空，添加一个新的
        this.editFormData.template = {
          id: null,
          name: null
        }
        this.editFormData.templateJson = ''
        for (var i = 0; i <= this.editFormData.dcTableList.length - 1; i++) {
          this.editFormData.dcTableList[i].id = null
          for (var j = 0; j <= this.editFormData.dcTableList[i].columnList.length - 1; j++) {
            this.editFormData.dcTableList[i].columnList[j].id = null
          }
        }
        this.dialogProps.visible = true
      })
    })
  }
}
</script>
<style>
.hide-expand .elx-cell .elx-icon--arrow-right {
  display: none;
}

.btnGroup {
  position: absolute;
  left: 50%;
}

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
