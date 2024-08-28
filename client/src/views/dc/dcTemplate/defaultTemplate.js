export default {
  matchJs:
    '/**\n * 根据数据模型，检查模型是否是配\n * @param {Object} dbModel  后台配置的数据模型\n * @returns  true 匹配， false 不匹配\n */\nfunction match(dbModel) {\n  return true\n}\n',
  viewJs: 'export default {\n\n  data() {\n    return {\n    }\n  }\n}\n',
  initJs:
    '/**\n * 在首次打开时，初始化界面组件\n * @param {Object} template 代码模版对象\n * @param {Object} dbModel  后台配置的数据模型\n * @param {Object} tools    工具包\n * @returns\n */\nfunction init(template, dbModel, tools) {\n\n}',
  pages: []
}
