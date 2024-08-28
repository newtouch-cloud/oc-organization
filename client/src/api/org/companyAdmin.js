import request from '@/utils/request'

export const getCompanyAdminById = (id) =>
  request({
    url: '/org/companyAdmin/' + id,
    method: 'get'
  })

export const listCompanyAdminPage = (search) =>
  request({
    url: '/org/companyAdmin/list',
    method: 'post',
    data: search
  })

export const listCompanyAdminAll = (search) =>
  request({
    url: '/org/companyAdmin/listAll',
    method: 'post',
    data: search
  })

export const saveCompanyAdmin = (companyAdmin) =>
  request({
    url: '/org/companyAdmin/save',
    method: 'post',
    data: companyAdmin
  })

export const deleteCompanyAdmin = (companyAdmin) =>
  request({
    url: '/org/companyAdmin/delete',
    method: 'post',
    data: companyAdmin
  })

export const bulkInsertCompanyAdmin = (companyAdmins) =>
  request({
    url: '/org/companyAdmin/bulkInsert',
    method: 'post',
    data: companyAdmins
  })

export const bulkUpdateCompanyAdmin = (companyAdmins) =>
  request({
    url: '/org/companyAdmin/bulkUpdate',
    method: 'post',
    data: companyAdmins
  })

export const bulkDeleteCompanyAdmin = (companyAdmins) =>
  request({
    url: '/org/companyAdmin/bulkDelete',
    method: 'post',
    data: companyAdmins
  })
