<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="layout-aside">
      <div class="logo-wrap">
        <el-icon :size="24"><Compass /></el-icon>
        <span v-show="!isCollapse" class="logo-text">管理系统</span>
      </div>

      <el-menu
        router
        :default-active="currentRoute"
        :collapse="isCollapse"
        background-color="#1d1e1f"
        text-color="rgba(255,255,255,.65)"
        active-text-color="#fff"
        :collapse-transition="false"
        class="layout-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>首页概览</template>
        </el-menu-item>

        <el-sub-menu index="tomb-group">
          <template #title>
            <el-icon><Place /></el-icon>
            <span>墓碑管理</span>
          </template>
          <el-menu-item index="/tomb">墓碑列表</el-menu-item>
          <el-menu-item index="/tomb/message">留言管理</el-menu-item>
          <el-menu-item index="/tomb/checkin">打卡记录</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="family-group">
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>家族管理</span>
          </template>
          <el-menu-item index="/family">家族列表</el-menu-item>
          <el-menu-item index="/family/member">成员管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="user-group" v-if="isAdmin">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user">用户列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="system-group" v-if="isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/log">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumbName">{{ breadcrumbName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-avatar :src="user.avatar || defaultAvatar" :size="32" />
              <span class="username">{{ user.username || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goProfile">
                  <el-icon><UserFilled /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Compass, DataAnalysis, Place, Connection, User, Setting,
  Fold, Expand, ArrowDown, UserFilled, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const defaultAvatar = 'https://i.pravatar.cc/150?img=3'
const user = reactive({ username: '', avatar: '', role: '' })

const isAdmin = computed(() => user.role === 'admin')
const currentRoute = computed(() => route.path)

const breadcrumbMap: Record<string, string> = {
  '/dashboard': '首页概览',
  '/tomb': '墓碑列表',
  '/tomb/message': '留言管理',
  '/tomb/checkin': '打卡记录',
  '/family': '家族列表',
  '/family/member': '成员管理',
  '/user': '用户列表',
  '/system/role': '角色管理',
  '/system/log': '操作日志'
}
const breadcrumbName = computed(() => breadcrumbMap[route.path] || '')

onMounted(() => {
  const currentUser = localStorage.getItem('currentUser')
  if (currentUser) {
    const u = JSON.parse(currentUser)
    user.username = u.username || u.mobile || '用户'
    user.avatar = u.headImg || ''
    user.role = u.role || 'admin'
  }
})

const goProfile = () => router.push('/profile')

const logout = async () => {
  try {
    const { logoutAPI } = await import('@/api/auth')
    await logoutAPI()
  } catch { /* 即使接口失败也要退出 */ }
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background: #1d1e1f;
  transition: width .28s;
  overflow: hidden;
}

.logo-wrap {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255,255,255,.08);
  white-space: nowrap;
  overflow: hidden;
}

.logo-text {
  letter-spacing: 1px;
}

.layout-menu {
  border-right: none;
}

.layout-menu:not(.el-menu--collapse) {
  width: 210px;
}

.layout-menu .el-menu-item.is-active {
  background: rgba(64,158,255,.15) !important;
}

.layout-header {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.username {
  font-size: 14px;
  color: #333;
}

.layout-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
