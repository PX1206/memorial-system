import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'
import Login from '@/views/Login.vue'
import Memorial from '@/views/memorial/Memorial.vue'

const routes = [
  { path: '/login', component: Login },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { requiresAuth: true }
      },

      // 墓碑管理
      {
        path: 'tomb',
        component: () => import('@/views/tomb/TombList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'tomb/message',
        component: () => import('@/views/tomb/TombMessage.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'tomb/checkin',
        component: () => import('@/views/tomb/TombCheckin.vue'),
        meta: { requiresAuth: true }
      },

      // 家族管理
      {
        path: 'family',
        component: () => import('@/views/family/FamilyList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'family/member',
        component: () => import('@/views/family/FamilyMember.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'family/join',
        component: () => import('@/views/family/FamilyJoin.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'family/member/bind',
        component: () => import('@/views/family/FamilyMemberBind.vue'),
        meta: { requiresAuth: true }
      },

      // 用户管理
      {
        path: 'user',
        component: () => import('@/views/user/UserList.vue'),
        meta: { requiresAuth: true }
      },

      // 系统管理
      {
        path: 'system/role',
        component: () => import('@/views/system/RoleList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'system/menu',
        component: () => import('@/views/system/MenuList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'system/file',
        component: () => import('@/views/system/FileList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'system/log',
        component: () => import('@/views/system/SystemLog.vue'),
        meta: { requiresAuth: true }
      },

      {
        path: 'profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },

  { path: '/memorial/:id', component: Memorial },

  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.path === '/login') {
    if (token) return next('/dashboard')
    return next()
  }

  if (to.meta.requiresAuth && !token) {
    // 邀请码/绑定码：存 localStorage 备用（注册后再登录时 redirect 可能丢失 code）
    if (to.path === '/family/join' && to.query.code) {
      try { localStorage.setItem('pendingInviteCode', String(to.query.code)) } catch (_) {}
    } else if (to.path === '/family/member/bind' && to.query.code) {
      try { localStorage.setItem('pendingBindCode', String(to.query.code)) } catch (_) {}
    }
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router
