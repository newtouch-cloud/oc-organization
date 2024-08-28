import request from '@/utils/request'

export const getTenantAppById = (id) =>
  request({
    url: '/ten/tenantApp/' + id,
    method: 'get'
  })

export const listTenantAppPage = (search) =>
  request({
    url: '/ten/tenantApp/list',
    method: 'post',
    data: search
  })
export const remoteList = (search) =>
  request({
    url: '/ten/tenantApp/remoteList',
    method: 'post',
    data: search
  })

export const listTenantAppAll = (search) =>
  request({
    url: '/ten/tenantApp/listAll',
    method: 'post',
    data: search
  })
export const remoteListAll = (search) =>
  request({
    url: '/ten/tenantApp/remoteListAll',
    method: 'post',
    data: search
  })

export const versionListAll = (search) =>
  request({
    url: '/ten/tenantApp/versionListAll',
    method: 'post',
    data: search
  })

export const saveTenantApp = (tenantApp) =>
  request({
    url: '/ten/tenantApp/save',
    method: 'post',
    data: tenantApp
  })

export const deleteTenantApp = (tenantApp) =>
  request({
    url: '/ten/tenantApp/delete',
    method: 'post',
    data: tenantApp
  })

export const bulkInsertTenantApp = (tenantApps) =>
  request({
    url: '/ten/tenantApp/bulkInsert',
    method: 'post',
    data: tenantApps
  })

export const bulkUpdateTenantApp = (tenantApps) =>
  request({
    url: '/ten/tenantApp/bulkUpdate',
    method: 'post',
    data: tenantApps
  })

export const bulkDeleteTenantApp = (tenantApps) =>
  request({
    url: '/ten/tenantApp/bulkDelete',
    method: 'post',
    data: tenantApps
  })
