<template>
  <el-container class="layout-container">
    <div v-if="isMobile && !isCollapse" class="layout-mask" @click="isCollapse = true" />
    <el-aside
      :width="isCollapse ? '64px' : '210px'"
      :class="['layout-aside', { 'aside-collapsed': isCollapse, 'aside-mobile': isMobile, 'aside-mobile-open': isMobile && !isCollapse }]"
    >
      <div class="logo-wrap">
        <img src="/xianhua.png" class="logo-icon" />
        <span v-show="!isCollapse" class="logo-text">念园系统</span>
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
        <template v-for="menu in userMenus" :key="menu.id">
          <el-sub-menu v-if="menu.children && menu.children.length && hasVisibleChildren(menu)" :index="'menu-' + menu.id">
            <template #title>
              <el-icon><component :is="getIcon(menu.icon)" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <template v-for="child in menu.children" :key="child.id">
              <el-menu-item v-if="child.type !== 'btn' && !child.hidden" :index="child.path">
                {{ child.title }}
              </el-menu-item>
            </template>
          </el-sub-menu>
          <el-menu-item v-else-if="!menu.hidden && menu.type !== 'btn'" :index="menu.path">
            <el-icon><component :is="getIcon(menu.icon)" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :title="isCollapse ? '展开菜单' : '收起菜单'" @click="toggleSidebar">
            <MenuIcon v-if="isMobile" />
            <Fold v-else-if="!isCollapse" />
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
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Compass, DataAnalysis, Place, Connection, User, Setting,
  Fold, Expand, ArrowDown, UserFilled, SwitchButton,
  Document, Menu as MenuIcon, Grid, List, Monitor, Operation
} from '@element-plus/icons-vue'
import { usePermissionStore } from '@/stores/permission'
import { getUserMenuTree, getUserPermissions } from '@/api/menu'

const router = useRouter()
const route = useRoute()
const permissionStore = usePermissionStore()

const isCollapse = ref(false)
const isMobile = ref(false)

function checkMobile() {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) isCollapse.value = true
}

function toggleSidebar() {
  isCollapse.value = !isCollapse.value
}
const defaultAvatar = 'https://i.pravatar.cc/150?img=3'
const user = reactive({ username: '', avatar: '', role: '' })
const userMenus = ref<any[]>([])

function refreshUserDisplay() {
  const currentUser = localStorage.getItem('currentUser')
  if (currentUser) {
    const u = JSON.parse(currentUser)
    user.username = u.nickname || u.username || u.mobile || '用户'
    user.avatar = u.headImg || ''
  }
}

window.addEventListener('user-info-updated', refreshUserDisplay)

const iconMap: Record<string, any> = {
  DataAnalysis, Place, Connection, User, Setting,
  Document, Menu: MenuIcon, Grid, List, Monitor, Operation, Compass
}

function getIcon(name: string) {
  if (!name) return Document
  return iconMap[name] || Document
}

function hasVisibleChildren(menu: any) {
  return menu.children?.some((c: any) => c.type !== 'btn' && !c.hidden)
}

const currentRoute = computed(() => route.path)

const breadcrumbMap = computed(() => {
  const map: Record<string, string> = {}
  function walk(menus: any[]) {
    for (const m of menus) {
      if (m.path) map[m.path] = m.title
      if (m.children) walk(m.children)
    }
  }
  walk(userMenus.value)
  return map
})

const breadcrumbName = computed(() => breadcrumbMap.value[route.path] || '')

async function loadUserMenusAndPermissions() {
  try {
    const currentUser = localStorage.getItem('currentUser')
    if (currentUser) {
      const u = JSON.parse(currentUser)
      user.username = u.nickname || u.username || u.mobile || '用户'
      user.avatar = u.headImg || ''
      user.role = u.role || ''

      if (u.permissions) {
        permissionStore.setPermissions(u.permissions)
      }
    }

    const [menus, permissions] = await Promise.all([
      getUserMenuTree(),
      getUserPermissions()
    ])
    userMenus.value = menus || []
    permissionStore.setPermissions(permissions || [])
    permissionStore.setMenuTree(menus || [])
  } catch {
    userMenus.value = []
  }
}

watch(() => route.path, () => {
  if (isMobile.value) isCollapse.value = true
})

onMounted(() => {
  loadUserMenusAndPermissions()
  checkMobile()
  window.addEventListener('resize', checkMobile)
})
onUnmounted(() => {
  window.removeEventListener('user-info-updated', refreshUserDisplay)
  window.removeEventListener('resize', checkMobile)
})

const goProfile = () => router.push('/profile')

const logout = async () => {
  try {
    const { logoutAPI } = await import('@/api/auth')
    await logoutAPI()
  } catch { /* 即使接口失败也要退出 */ }
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  permissionStore.clear()
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

.layout-aside.aside-collapsed .logo-wrap {
  padding: 0 14px;
}

.logo-wrap {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 0 16px;
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255,255,255,.08);
  white-space: nowrap;
  overflow: hidden;
  transition: padding .28s;
}

.logo-icon {
  width: 36px;
  height: 36px;
  object-fit: contain;
  flex-shrink: 0;
  filter: drop-shadow(0 1px 2px rgba(0,0,0,.15));
}

.logo-text {
  letter-spacing: 2px;
  transition: opacity .2s;
  overflow: hidden;
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

/* 移动端适配 */
@media (max-width: 768px) {
  .layout-mask {
    position: fixed;
    inset: 0;
    z-index: 1000;
    background: rgba(0,0,0,.4);
  }

  .layout-aside.aside-mobile {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1001;
    transform: translateX(-100%);
    transition: transform .28s;
  }

  .layout-aside.aside-mobile.aside-mobile-open {
    transform: translateX(0);
  }

  .layout-aside.aside-mobile .el-menu {
    width: 260px !important;
  }

  .layout-header {
    padding: 0 12px;
  }

  .header-left .el-breadcrumb {
    display: none;
  }

  .layout-main {
    padding: 12px;
  }

  .username {
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
