import request from '@/utils/request'

export const synchronizationCompany = () =>
  request({
    url: '/synchronization/company',
    method: 'post'
  })

export const synchronizationDepartment = () =>
  request({
    url: '/synchronization/department',
    method: 'post'
  })

export const synchronizationUser = () =>
  request({
    url: '/synchronization/user',
    method: 'post'
  })
