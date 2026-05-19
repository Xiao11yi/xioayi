import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('../views/login/Login.vue') },
  {
    path: '/',
    component: () => import('../layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/dashboard/Dashboard.vue') },
      { path: 'product', component: () => import('../views/product/ProductList.vue') },
      { path: 'coupon', component: () => import('../views/coupon/CouponList.vue') },
      { path: 'order', component: () => import('../views/order/OrderList.vue') },
      { path: 'user', component: () => import('../views/user/UserList.vue') }
    ]
  },
  {
    path: '/shop',
    component: () => import('../views/shop/ShopLayout.vue'),
    redirect: '/shop/products',
    children: [
      { path: 'products', component: () => import('../views/shop/ShopProducts.vue') },
      { path: 'coupons', component: () => import('../views/shop/ShopCoupons.vue') },
      { path: 'orders', component: () => import('../views/shop/ShopOrders.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const noAuth = ['/login', '/shop', '/shop/products', '/shop/coupons']
  if (!noAuth.includes(to.path) && to.path.startsWith('/shop')) {
    next() // shop sub-routes that need auth will check internally
  } else if (to.path !== '/login' && !localStorage.getItem('token')) {
    next('/login')
  } else {
    next()
  }
})

export default router
