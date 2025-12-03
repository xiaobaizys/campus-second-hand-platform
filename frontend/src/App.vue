<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 这里可以添加全局逻辑
</script>

<style>
@import '@/styles/responsive.css';

:root {
  --bg-color: #f5f7fa;
  --text-color: #303133;
  --text-color-light: #606266;
  --border-color: #ebeef5;
  --card-bg: #ffffff;
  --header-bg: #ffffff;
  --header-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
}

.dark-mode {
  --bg-color: #000000;
  --text-color: #e4e7ed;
  --text-color-light: #909399;
  --border-color: #303030;
  --card-bg: #000000;
  --header-bg: #000000;
  --header-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-color);
  min-height: 100vh;
  background-color: var(--bg-color);
  transition: background-color 0.3s, color 0.3s;
}

/* 深色模式下的全局样式 */
.dark-mode {
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* 深色模式下的卡片样式 */
.dark-mode .el-card,
.dark-mode .el-dialog,
.dark-mode .el-dropdown-menu,
.dark-mode .el-input,
.dark-mode .el-select-dropdown,
.dark-mode .el-menu {
  background-color: var(--card-bg) !important;
  border-color: var(--border-color) !important;
}

/* 深色模式下的文字颜色 */
.dark-mode .el-card__header,
.dark-mode .el-card__body,
.dark-mode .el-dialog__title,
.dark-mode .el-dialog__body,
.dark-mode .el-dropdown-menu__item,
.dark-mode .el-input__inner,
.dark-mode .el-select-dropdown__item,
.dark-mode .el-menu-item,
.dark-mode .el-submenu__title {
  color: var(--text-color) !important;
}

/* 深色模式下的边框颜色 */
.dark-mode .el-divider,
.dark-mode .el-input,
.dark-mode .el-select,
.dark-mode .el-button,
.dark-mode .el-avatar {
  border-color: var(--border-color) !important;
}

/* 深色模式下的导航栏样式 */
.dark-mode .app-header {
  background-color: var(--header-bg) !important;
  box-shadow: var(--header-shadow) !important;
}

/* 深色模式下的链接颜色 */
.dark-mode .nav-item {
  color: var(--text-color) !important;
}

.dark-mode .nav-item:hover,
.dark-mode .nav-item.router-link-active {
  color: var(--primary-color) !important;
}

/* 深色模式下的用户名颜色 */
.dark-mode .username {
  color: var(--text-color) !important;
}

/* 深色模式下的通知图标颜色 */
.dark-mode .notification-icon {
  color: var(--text-color) !important;
}

/* 深色模式下的搜索框样式 */
.dark-mode .search-input .el-input__inner {
  background-color: var(--card-bg) !important;
  color: var(--text-color) !important;
}

/* 深色模式下的按钮样式 */
.dark-mode .el-button {
  background-color: var(--card-bg) !important;
  color: var(--text-color) !important;
  border-color: var(--border-color) !important;
}

.dark-mode .el-button:hover {
  border-color: var(--primary-color) !important;
  color: var(--primary-color) !important;
}

.dark-mode .el-button--primary {
  background-color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: #ffffff !important;
}

.dark-mode .el-button--danger {
  background-color: var(--danger-color) !important;
  border-color: var(--danger-color) !important;
  color: #ffffff !important;
}
</style>