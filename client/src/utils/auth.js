// import Cookies from 'js-cookie'

const TokenKey = 'token'
const CurrentUserKey = 'currentUser'
const RoutersKey = 'routers'
const SysSettingKey = 'sysSetting'
const DataPermisionsKey = 'dataPermisions'
const PersonalThemeKey = 'personalTheme'
const CurrentCompanyKey = 'currentCompany'
const CurrenTenantKey = 'currenTenant'

const CurrenAppKey = 'currenAppKey'   // 微应用appkey

// 清除本地所有数据
export function clearLocalData() {
  sessionStorage.clear()
}

// 获得token
export function getLocalToken() {
  return sessionStorage.getItem(TokenKey)
}
// 保存token
export function setLocalToken(token) {
  sessionStorage.setItem(TokenKey, token)
}
// 清除tocken
export function removeLocalToken() {
  sessionStorage.removeItem(TokenKey)
}

// 获得登录用户
export function getLocalCurrentUser() {
  return JSON.parse(sessionStorage.getItem(CurrentUserKey))
}
// 保存登录用户
export function setLocalCurrentUser(currentUser) {
  sessionStorage.setItem(CurrentUserKey, JSON.stringify(currentUser))
}

// 获取登录用户的路由
export function getLocalRouters() {
  return JSON.parse(sessionStorage.getItem(RoutersKey))
}
// 保存登录用户路由
export function setLocalRouters(routers) {
  sessionStorage.setItem(RoutersKey, JSON.stringify(routers))
}

// 获取系统设置
export function getLocalSysSetting() {
  return JSON.parse(sessionStorage.getItem(SysSettingKey))
}
// 保存系统设置
export function setLocalSysSetting(settings) {
  sessionStorage.setItem(SysSettingKey, JSON.stringify(settings))
}


// 获取登录用户数据权限
export function getLocalDataPermisions() {
  return JSON.parse(sessionStorage.getItem(DataPermisionsKey))
}
// 保存登录用户数据权限
export function setLocalDataPermisions(dataPermission) {
  sessionStorage.setItem(DataPermisionsKey, JSON.stringify(dataPermission))
}

// 获取登录用户主题
export function getLocalPersonalTheme() {
  return JSON.parse(sessionStorage.getItem(PersonalThemeKey))
}
// 保存登录用户主题
export function setLocalPersonalTheme(personalTheme) {
  sessionStorage.setItem(PersonalThemeKey, JSON.stringify(personalTheme))
}

// 获取登录用户公司
export function getLocalCurrentCompany() {
  return JSON.parse(sessionStorage.getItem(CurrentCompanyKey))
}
// 保存登录用户公司
export function setLocalCurrentCompany(currentCompany) {
  sessionStorage.setItem(CurrentCompanyKey, JSON.stringify(currentCompany))
}

// 获取登录用户所属租户
export function getLocalCurrentTenant() {
  return JSON.parse(sessionStorage.getItem(CurrenTenantKey))
}
// 保存登录用户所属租户
export function setLocalCurrentTenant(currentTenant) {
  sessionStorage.setItem(CurrenTenantKey, JSON.stringify(currentTenant))
}

/**
 * 获取根路由
 * 微应用模式下，路由根地址为 /appKey
 * 独立应用模式下，路由根地址为 /
 * @returns
 */
export function getAppRoot() {
  // 微应用时，appKey作为路由前缀
  const appKey = sessionStorage.getItem(CurrenAppKey)
  return appKey ? '/' + appKey  : ''
}
