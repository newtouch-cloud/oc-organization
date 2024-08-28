import request from '@/utils/request'

export const getUserById = (id) =>
  request({
    url: '/admin/user/' + id,
    method: 'get'
  })

export const listUserPage = (search) =>
  request({
    url: '/admin/user/list',
    method: 'post',
    data: search
  })

export const listUserAll = (search) =>
  request({
    url: '/admin/user/listAll',
    method: 'post',
    data: search
  })

export const saveUser = (user) =>
  request({
    url: '/admin/user/save',
    method: 'post',
    data: user
  })

export const deleteUser = (user) =>
  request({
    url: '/admin/user/delete',
    method: 'post',
    data: user
  })

export const bulkInsertUser = (users) =>
  request({
    url: '/admin/user/bulkInsert',
    method: 'post',
    data: users
  })

export const bulkUpdateUser = (users) =>
  request({
    url: '/admin/user/bulkUpdate',
    method: 'post',
    data: users
  })

export const bulkDeleteUser = (users) =>
  request({
    url: '/admin/user/bulkDelete',
    method: 'post',
    data: users
  })

// 修改加密字段
