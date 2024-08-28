import request from '@/utils/request'

export const generateCode = (genCase) => 
    request({
        url: '/dc/dcCode/generateCode',
        method: 'post',
        data: genCase
    })

export const cleanCode = (genCase) => 
    request({
        url: '/gen/genCode/clscode',
        method: 'post',
        data: genCase
    })