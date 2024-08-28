import request from '@/utils/request'

export const getCompanyAppById = (id) =>
  request({
    url: '/org/companyApp/' + id,
    method: 'get'
  })

export const listCompanyAppPage = (search) =>
  request({
    url: '/org/companyApp/list',
    method: 'post',
    data: search
  })
export const remotePage = (search) =>
  request({
    url: '/org/companyApp/remoteList',
    method: 'post',
    data: search
  })

export const listCompanyAppAll = (search) =>
  request({
    url: '/org/companyApp/listAll',
    method: 'post',
    data: search
  })

export const saveCompanyApp = (companyApp) =>
  request({
    url: '/org/companyApp/save',
    method: 'post',
    data: companyApp
  })

export const deleteCompanyApp = (companyApp) =>
  request({
    url: '/org/companyApp/delete',
    method: 'post',
    data: companyApp
  })

export const bulkInsertCompanyApp = (companyApps) =>
  request({
    url: '/org/companyApp/bulkInsert',
    method: 'post',
    data: companyApps
  })

export const bulkUpdateCompanyApp = (companyApps) =>
  request({
    url: '/org/companyApp/bulkUpdate',
    method: 'post',
    data: companyApps
  })

export const bulkDeleteCompanyApp = (companyApps) =>
  request({
    url: '/org/companyApp/bulkDelete',
    method: 'post',
    data: companyApps
  })
