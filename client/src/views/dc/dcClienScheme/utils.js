// 根据后台方案构造dbModel
export function constructDbModel(scheme) {
  if (scheme) {
    const mainTable = scheme.dcTableList.find((item) => item.isMainTable === '1')
    let childTables = scheme.dcTableList.filter((item) => item.isMainTable !== '1')
    childTables = childTables.map((item) => ({
      ...item,
      columns: item.columnList
    }))
    return JSON.parse(
      JSON.stringify({
        ...scheme,
        ...{
          className: mainTable.className,
          table: {
            ...mainTable,
            columns: mainTable.columnList
          },
          childTables
        }
      })
    )
  }
}
