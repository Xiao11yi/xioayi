import { defineStore } from 'pinia'
import { loginApi, logoutApi } from '../api/auth'
import router from '../router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || ''
  }),
  actions: {
    async login(data) {
      const res = await loginApi(data)
      this.token = res.data.token
      this.username = res.data.username
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      router.push('/')
    },
    async logout() {
      await logoutApi()
      this.token = ''
      this.username = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/login')
    }
  }
})
