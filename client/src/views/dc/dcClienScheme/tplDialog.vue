<template>
  <div>
    <el-dialog
      title="模板选择"
      :visible.sync="templateVisible"
      :close-on-click-modal="false"
      width="50%"
      @close="onTemplateDialogClose()"
      v-loading="loading"
      :append-to-body="true"
    >
      <el-form
        :model="templateForm"
        :rules="templateRules"
        ref="dcTemplateForm"
        label-width="120px"
        label-position="right"
        class="edit-form"
      >
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="后端方案" prop="scheme.id" style="margin-bottom: 0">
              <el-select
                v-model="templateForm.scheme"
                value-key="id"
                filterable
                clearable
                placeholder="请选择后端方案"
                @change="onChangeScheme"
                @clear="
                  templateForm.scheme = {
                    id: null,
                    name: null
                  }
                "
              >
                <el-option v-for="scheme in scheme_List" :key="scheme.id" :label="scheme.name" :value="scheme"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="templateForm.scheme.id != null" gutter="20">
          <el-col :span="8" style="margin: 10px 0" v-for="(item, index) in handleTemplate_List" :key="index">
            <el-card
              class="box-card template-card"
              shadow="hover"
              :class="templateForm.template.id == item.id ? 'card-selected' : ''"
            >
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
                <div v-if="templateForm.template.id == item.id" class="selected-flag"><i class="el-icon-check"></i></div>
                <el-button v-else type="primary" size="mini" round @click="onChangeTemplate(item)">选择模板</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button v-if="template_List.length > 0" type="primary" @click="chooseType()" :plain="true">确 定</el-button>
        <el-button :plain="true" @click="onTemplateDialogClose()">关 闭</el-button>
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
import { constructDbModel } from './utils'

const parseTemplate = window['vue-codegenerator'].parseTemplate

export default {
  extends: BaseUI,
  name: 'tpl-dialog',
  components: {
    OperationIcon
  },
  data() {
    return {
      dbModel: null,
      scheme_List: [], // 后端方案
      template_List: [], // 模板
      templateForm: this.initTemplateForm(),
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
    onChangeScheme(val) {
      this.dbModel = null
      if (!validatenull(val) && !validatenull(val.id)) {
        this.setLoad()
        getDcSchemeById(val.id)
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
    onChangeTemplate(val) {
      this.templateForm.template = {
        id: val.id,
        name: val.name,
        json: val.json
      }
    },
    chooseType() {
      this.$refs['dcTemplateForm'].validate((valid) => {
        if (valid) {
          if (!validatenull(this.templateForm.template.id)) {
            this.$emit('after-change', this.templateForm, this.dbModel)
            this.templateVisible = false
          } else {
            this.showMessage({
              type: 'warning',
              msg: '请选择模板'
            })
          }
        }
      })
    },
    onTemplateDialogClose() {
      this.templateVisible = false
    },
    initTemplateForm() {
      return {
        scheme: {
          // 后端方案
          id: null,
          name: null
        },
        template: {
          // 选择的模板类型
          id: null,
          name: null,
          json: null
        }
      }
    },
    initOptions() {
      let scheme_search = {
        params: []
      }
      // 数据权限: 低代码-代码方案dc_scheme
      this.pushDataPermissions(scheme_search.params, this.$route.meta.routerId, '1088722824589459486')
      this.scheme_List.splice(0, this.scheme_List.length)
      listDcSchemeAll(scheme_search).then((responseData) => {
        this.scheme_List = responseData.data
      })
      let template_search = {
        params: [
          {
            columnName: 'type',
            queryType: '=',
            value: '0'
          }
        ]
      }
      // 数据权限: 低代码-模板dc_template
      this.pushDataPermissions(template_search.params, this.$route.meta.routerId, '1154414160417153685')
      this.template_List.splice(0, this.template_List.length)
      listTemplateAndFile(template_search).then((responseData) => {
        this.template_List = responseData.data
      })
    },
    matchModel(tpl) {
      if (!this.dbModel) {
        return false
      }
      let jsonTemplate = parseTemplate(tpl.json) // 模板JSON转为对象
      // TODO 需要如果匹配代码异常的兼容性处理
      return eval(`(${jsonTemplate.matchJs}).call(this, this.dbModel)`)
    }
  },
  watch: {},
  mounted: function () {
    this.$nextTick(() => {
      this.$on('selectTemplate', function () {
        this.templateVisible = true // 选择模板
        this.initOptions()
      })
      this.$on('switchTemplate', function (tpl, dbModel) {
        this.templateVisible = true // 选择模板

        this.templateForm = tpl
        this.dbModel = dbModel
        this.initOptions()
      })
    })
  }
}
</script>
