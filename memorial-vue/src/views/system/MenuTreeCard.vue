<template>
  <div class="menu-tree-card">
    <div class="menu-card">
      <div class="menu-card__main">
        <div class="menu-card__head">
          <el-icon v-if="item.icon" class="menu-card__icon"><component :is="getIcon(item.icon)" /></el-icon>
          <span v-else class="menu-card__icon-placeholder">·</span>
          <div class="menu-card__info">
            <div class="menu-card__title">{{ item.title }}</div>
            <div class="menu-card__meta">
              <el-tag v-if="item.type === 'dir'" type="warning" size="small">目录</el-tag>
              <el-tag v-else-if="item.type === 'menu'" type="success" size="small">菜单</el-tag>
              <el-tag v-else type="info" size="small">按钮</el-tag>
              <span v-if="item.path" class="menu-card__path">{{ item.path }}</span>
              <span v-if="item.permission" class="menu-card__perm">{{ item.permission }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="menu-card__actions">
        <el-button size="small" type="primary" link @click="$emit('edit', item)" v-permission="'sys:menu:edit'">编辑</el-button>
        <el-button size="small" type="success" link @click="$emit('add-child', item)" v-permission="'sys:menu:add'">添加子级</el-button>
        <el-button size="small" type="danger" link @click="$emit('delete', item)" v-permission="'sys:menu:delete'">删除</el-button>
      </div>
    </div>
    <div v-if="item.children?.length" class="menu-children">
      <MenuTreeCard
        v-for="child in item.children"
        :key="child.id"
        :item="child"
        :get-icon="getIcon"
        @edit="$emit('edit', $event)"
        @add-child="$emit('add-child', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  item: { type: Object, required: true },
  getIcon: { type: Function, required: true }
})
defineEmits(['edit', 'add-child', 'delete'])
</script>

<style scoped>
.menu-tree-card { margin-bottom: 8px; }
.menu-card {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background: #fafafa;
}
.menu-card__main { margin-bottom: 10px; }
.menu-card__head { display: flex; align-items: flex-start; gap: 10px; }
.menu-card__icon { font-size: 20px; color: #409eff; flex-shrink: 0; margin-top: 2px; }
.menu-card__icon-placeholder { font-size: 18px; color: #c0c4cc; width: 20px; text-align: center; flex-shrink: 0; }
.menu-card__info { flex: 1; min-width: 0; }
.menu-card__title { font-weight: 600; font-size: 15px; color: #303133; margin-bottom: 6px; }
.menu-card__meta { display: flex; flex-wrap: wrap; align-items: center; gap: 6px; font-size: 12px; }
.menu-card__path { color: #606266; word-break: break-all; }
.menu-card__perm { color: #909399; word-break: break-all; }
.menu-card__actions { display: flex; flex-wrap: wrap; gap: 4px; padding-top: 10px; border-top: 1px solid #ebeef5; }
.menu-children { margin-left: 16px; margin-top: 8px; padding-left: 12px; border-left: 2px solid #e4e7ed; }
</style>
