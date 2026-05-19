import request from '../utils/request'

export function listCoupons(params) { return request.get('/coupons', { params }) }
export function getCoupon(id) { return request.get(`/coupons/${id}`) }
export function createCoupon(data) { return request.post('/coupons', data) }
export function updateCoupon(id, data) { return request.put(`/coupons/${id}`, data) }
export function deleteCoupon(id) { return request.delete(`/coupons/${id}`) }
export function grabCoupon(id) { return request.post(`/coupons/${id}/grab`) }
