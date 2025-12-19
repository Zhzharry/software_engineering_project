# 高德地图API密钥使用说明

## 密钥配置

### 前端Web端API（JS API）
- **用途**：前端地图可视化、地图显示、标记点渲染等
- **密钥**：`667083e32d238add0a83812184ecb210`
- **安全密钥**：`fd075bd4dc91d1b2215acd9d4f625ff5`
- **使用位置**：`frontend/src/views/MapVisualization.vue`
- **配置方式**：
  ```javascript
  window._AMapSecurityConfig = {
    securityJsCode: 'fd075bd4dc91d1b2215acd9d4f625ff5'
  }
  script.src = 'https://webapi.amap.com/maps?v=2.0&key=667083e32d238add0a83812184ecb210&plugin=...'
  ```

### 后端服务端API（Web Service）
- **用途**：后端地理编码、逆地理编码、行政区划查询等
- **密钥**：`3f02caa01d89c1d4d03e88ee2259e2f7`
- **使用位置**：`src/main/resources/application.yml`
- **配置方式**：
  ```yaml
  amap:
    api:
      key: 3f02caa01d89c1d4d03e88ee2259e2f7
      security: ""  # 服务端API通常不需要security
  ```

## 使用场景区分

### 前端使用场景
- ✅ 地图可视化显示
- ✅ 地图标记点渲染
- ✅ 地图聚合显示
- ✅ 热力图显示
- ✅ 地图交互（缩放、拖拽等）
- ❌ 不能用于后端HTTP请求

### 后端使用场景
- ✅ 地理编码（地址转坐标）
- ✅ 逆地理编码（坐标转地址）
- ✅ 行政区划查询
- ✅ 路径规划
- ✅ 其他Web Service API调用
- ❌ 不能用于前端地图显示

## 注意事项

1. **密钥类型不能混用**：
   - Web端（JS API）密钥只能在前端使用
   - Web Service密钥只能在后端使用
   - 混用会导致 `USERKEY_PLAT_NOMATCH` 错误

2. **安全密钥配置**：
   - 前端Web端API需要配置安全密钥（securityJsCode）
   - 后端Web Service API通常不需要安全密钥

3. **白名单配置**：
   - 建议在高德开放平台配置白名单
   - 前端：添加 `localhost:5173`、`localhost:5174` 等
   - 后端：添加服务器IP地址

4. **调用量限制**：
   - 两个密钥的调用量是独立的
   - 注意各自的日调用量限制

## 配置检查清单

- [x] 前端使用Web端密钥：`667083e32d238add0a83812184ecb210`
- [x] 前端配置安全密钥：`fd075bd4dc91d1b2215acd9d4f625ff5`
- [x] 后端使用服务端密钥：`3f02caa01d89c1d4d03e88ee2259e2f7`
- [ ] 在高德开放平台配置白名单（可选但推荐）

