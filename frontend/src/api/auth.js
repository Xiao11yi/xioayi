import request from '../utils/request'

export function loginApi(data) { return request.post('/auth/login', data) }
export function logoutApi() { return request.post('/auth/logout') }
export function helloApi() { return request.get('/auth/api/hello') }
