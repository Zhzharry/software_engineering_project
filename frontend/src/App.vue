<template>
  <div id="app">
    <nav class="navbar">
      <div class="container">
        <div class="nav-content">
          <h1 class="logo">多源数据管理系统</h1>
          <div class="nav-links">
            <router-link to="/users" class="nav-link">用户管理</router-link>
            <router-link to="/modules" class="nav-link">模块管理</router-link>
            <router-link to="/raw-data" class="nav-link">原始数据</router-link>
            <router-link to="/log-data" class="nav-link">日志数据</router-link>
            <router-link to="/processed-data" class="nav-link">处理数据</router-link>
          </div>
        </div>
      </div>
    </nav>
    <main class="main-content">
      <div class="container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<style scoped>
.navbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-link {
  color: #4a5568;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-link:hover {
  background: #edf2f7;
  color: #667eea;
}

.nav-link.router-link-active {
  background: #667eea;
  color: white;
}

.main-content {
  padding: 40px 0;
}
</style>

