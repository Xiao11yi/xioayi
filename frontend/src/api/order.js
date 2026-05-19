import request from '../utils/request'

export function createOrder(productId) { return request.post(`/orders/product/${productId}`) }
export function getOrder(orderNo) { return request.get(`/orders/${orderNo}`) }
export function payOrder(orderNo) { return request.post(`/orders/${orderNo}/pay`) }
