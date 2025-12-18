# 灾情数据解码与查询API文档

## 概述

本文档描述了灾情数据管理系统中新增的ID解码、地理信息解析和数据查询相关的API接口。

## 一、ID解码接口

### 1.1 解码单个ID

**接口地址：** `GET /api/disaster-decode/decode/{id}`

**功能说明：** 解码36位一体化编码ID，返回结构化的解码信息

**请求参数：**
- `id` (路径参数): 36位一体化编码ID

**响应示例：**
```json
{
  "code": 200,
  "message": "解码成功",
  "data": {
    "originalId": "123456789012345678901234567890123456",
    "geoCode": "123456789012",
    "timeCode": "20231201120000",
    "dateTime": "2023-12-01T12:00:00",
    "sourceCategoryCode": "0",
    "sourceSubcategoryCode": "01",
    "sourceCategoryName": "系统内部",
    "sourceSubcategoryName": "后方指挥部",
    "carrierCode": "0",
    "carrierName": "文字",
    "disasterCode": "302001",
    "disasterCategoryCode": "3",
    "disasterSubcategoryCode": "02",
    "disasterIndicatorCode": "001",
    "disasterCategoryName": "房屋破坏",
    "disasterSubcategoryName": "砖木",
    "disasterIndicatorName": "一般损坏面积",
    "description": "房屋破坏 - 砖木 - 一般损坏面积 | 来源: 后方指挥部 | 载体: 文字 | 时间: 2023-12-01T12:00:00"
  },
  "timestamp": 1701424800000
}
```

### 1.2 批量解码ID

**接口地址：** `POST /api/disaster-decode/decode/batch`

**功能说明：** 批量解码多个ID

**请求体：**
```json
{
  "ids": [
    "123456789012345678901234567890123456",
    "987654321098765432109876543210987654"
  ]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "成功解码2个ID",
  "data": [
    {
      "originalId": "123456789012345678901234567890123456",
      ...
    }
  ],
  "timestamp": 1701424800000
}
```

### 1.3 验证ID格式

**接口地址：** `GET /api/disaster-decode/validate/{id}`

**功能说明：** 验证36位ID格式是否正确

**响应示例：**
```json
{
  "code": 200,
  "message": "ID格式正确",
  "data": true,
  "timestamp": 1701424800000
}
```

## 二、地理信息接口

### 2.1 根据地理码获取位置信息

**接口地址：** `GET /api/disaster-decode/geo/{geoCode}`

**功能说明：** 根据12位地理码获取地理位置信息

**请求参数：**
- `geoCode` (路径参数): 12位地理码

**响应示例：**
```json
{
  "code": 200,
  "message": "获取地理位置信息成功",
  "data": {
    "geoCode": "123456789012",
    "longitude": 104.066,
    "latitude": 30.572,
    "province": "四川省",
    "city": "成都市",
    "district": "锦江区",
    "address": "春熙路",
    "formattedAddress": "四川省成都市锦江区春熙路"
  }
}
```

### 2.2 根据经纬度逆地理编码

**接口地址：** `GET /api/disaster-decode/geo/reverse`

**功能说明：** 根据经纬度获取地理位置信息（使用高德API）

**请求参数：**
- `longitude` (查询参数): 经度
- `latitude` (查询参数): 纬度

**响应示例：**
```json
{
  "code": 200,
  "message": "获取地理位置信息成功",
  "data": {
    "longitude": 104.066,
    "latitude": 30.572,
    "province": "四川省",
    "city": "成都市",
    "district": "锦江区",
    "address": "春熙路",
    "formattedAddress": "四川省成都市锦江区春熙路"
  }
}
```

## 三、数据查询接口

### 3.1 根据灾害大类查询

**接口地址：** `GET /api/disaster-data/category/{category}`

**功能说明：** 根据灾害大类查询数据（如：房屋破坏、人员伤亡等）

**请求参数：**
- `category` (路径参数): 灾害大类名称

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "...",
      "disasterCategory": "房屋破坏",
      ...
    }
  ]
}
```

### 3.2 根据灾害子类查询

**接口地址：** `GET /api/disaster-data/subcategory/{subcategory}`

**功能说明：** 根据灾害子类查询数据（如：砖木、框架等）

### 3.3 根据来源查询

**接口地址：** `GET /api/disaster-data/source/{source}`

**功能说明：** 根据来源查询数据（如：后方指挥部、互联网感知等）

### 3.4 根据载体类型查询

**接口地址：** `GET /api/disaster-data/carrier/{carrierType}`

**功能说明：** 根据载体类型查询数据（文字/图像/音频/视频）

### 3.5 根据地理码查询

**接口地址：** `GET /api/disaster-data/geo/{geoCode}`

**功能说明：** 根据12位地理码查询数据

### 3.6 根据时间范围查询

**接口地址：** `GET /api/disaster-data/time-range`

**功能说明：** 根据灾情发生时间范围查询数据

**请求参数：**
- `start` (查询参数): 开始时间 (ISO格式: 2023-12-01T00:00:00)
- `end` (查询参数): 结束时间 (ISO格式: 2023-12-31T23:59:59)

### 3.7 综合查询

**接口地址：** `POST /api/disaster-data/query`

**功能说明：** 支持多条件组合查询

**请求体：**
```json
{
  "disasterCategory": "房屋破坏",
  "disasterSubcategory": "砖木",
  "source": "后方指挥部",
  "carrierType": "文字",
  "geoCode": "123456789012",
  "startTime": "2023-12-01T00:00:00",
  "endTime": "2023-12-31T23:59:59",
  "page": 1,
  "size": 10
}
```

### 3.8 获取数据详情（包含解码信息）

**接口地址：** `GET /api/disaster-data/detail/{id}`

**功能说明：** 获取数据详情，包含完整的解码信息

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "rawData": {
      "id": "...",
      "disasterId": "123456789012345678901234567890123456",
      ...
    },
    "decodedId": {
      "originalId": "123456789012345678901234567890123456",
      "disasterCategoryName": "房屋破坏",
      ...
    }
  }
}
```

## 四、数据处理接口

### 4.1 处理并解码单个数据

**接口地址：** `POST /api/disaster-process/process`

**功能说明：** 处理RawData，自动识别并解码36位ID

**请求体：**
```json
{
  "id": "...",
  "dataContent": "...",
  "disasterId": "123456789012345678901234567890123456"
}
```

### 4.2 批量处理并解码

**接口地址：** `POST /api/disaster-process/process/batch`

**功能说明：** 批量处理并解码数据

**请求体：**
```json
[
  {
    "id": "...",
    "dataContent": "..."
  }
]
```

### 4.3 根据ID字符串处理

**接口地址：** `POST /api/disaster-process/process/by-id-string`

**功能说明：** 从指定的ID字符串中提取36位ID并解码

**请求体：**
```json
{
  "rawData": {
    "id": "...",
    "dataContent": "..."
  },
  "idString": "文件名或其他包含36位ID的字符串"
}
```

## 五、ID编码规则说明

### 5.1 36位ID结构

```
位置         长度    说明
1-12         12位   地理码（地理信息编码）
13-26        14位   时间码（YYYYMMDDHHMMSS）
27           1位    来源码大类
28-29        2位    来源码子类
30           1位    载体码（0文字, 1图像, 2音频, 3视频）
31-36        6位    灾情码
  ├─ 31      1位    灾害大类代码
  ├─ 32-33   2位    灾害子类代码
  └─ 34-36   3位    灾情指标代码
```

### 5.2 灾害大类代码对照表

| 代码 | 名称 |
|------|------|
| 1 | 震情 |
| 2 | 人员伤亡及失踪 |
| 3 | 房屋破坏 |
| 4 | 生命线工程灾情 |
| 5 | 次生灾害 |

### 5.3 灾害子类代码示例

**房屋破坏 (3)：**
- 01: 土木
- 02: 砖木
- 03: 砖混
- 04: 框架
- 05: 其他

**次生灾害 (5)：**
- 01: 崩塌
- 02: 滑坡
- 03: 泥石流
- 04: 岩溶塌陷
- 05: 地裂缝
- 06: 地面沉降
- 07: 其他

### 5.4 来源码对照表

**大类：**
- 0: 系统内部
- 1: 外部接口

**子类：**
- 01: 后方指挥部
- 02: 前方指挥部
- 03: 现场调查
- 04: 互联网感知
- 05: 传感器监测
- 06: 卫星遥感
- 07: 其他

### 5.5 载体码对照表

| 代码 | 名称 |
|------|------|
| 0 | 文字 |
| 1 | 图像 |
| 2 | 音频 |
| 3 | 视频 |

## 六、示例

### 示例1：解析ID `010302001`

假设完整36位ID为：`12345678901220231201120000010302001`

解析结果：
- 地理码：`123456789012`
- 时间码：`20231201120000` (2023-12-01 12:00:00)
- 来源码：`0` + `01` = 系统内部 + 后方指挥部
- 载体码：`0` = 文字
- 灾情码：`302001`
  - 灾害大类：`3` = 房屋破坏
  - 灾害子类：`02` = 砖木
  - 灾情指标：`001` = 一般损坏面积

### 示例2：解析ID `504002`

假设完整36位ID为：`1234567890122023120112000001504002`

解析结果：
- 地理码：`123456789012`
- 时间码：`20231201120000`
- 来源码：`0` + `01` = 系统内部 + 后方指挥部
- 载体码：`0` = 文字
- 灾情码：`504002`
  - 灾害大类：`5` = 次生灾害
  - 灾害子类：`04` = 岩溶塌陷
  - 灾情指标：`002` = 灾害范围

## 七、文件解码接口

### 7.1 解码文件名

**接口地址：** `POST /api/disaster-decode/file/decode-filename`

**功能说明：** 从文件名中提取36位ID并解码

**请求体：**
```json
{
  "fileName": "123456789012345678901234567890123456.jpg"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "文件名解码成功",
  "data": {
    "fileName": "123456789012345678901234567890123456.jpg",
    "extractedId": "123456789012345678901234567890123456",
    "hasValidId": true,
    "decodedId": {
      "originalId": "123456789012345678901234567890123456",
      "disasterCategoryName": "房屋破坏",
      ...
    }
  }
}
```

### 7.2 批量解码文件名

**接口地址：** `POST /api/disaster-decode/file/decode-filenames/batch`

**功能说明：** 批量解码多个文件名

**请求体：**
```json
{
  "fileNames": [
    "123456789012345678901234567890123456.jpg",
    "987654321098765432109876543210987654.png"
  ]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "批量解码完成：成功2个，失败0个",
  "data": [
    {
      "fileName": "123456789012345678901234567890123456.jpg",
      "extractedId": "123456789012345678901234567890123456",
      "hasValidId": true,
      "decodedId": {...}
    }
  ]
}
```

### 7.3 解码Excel文件内容

**接口地址：** `POST /api/disaster-decode/file/decode-excel`

**功能说明：** 读取Excel文件，提取其中的ID列并解码

**请求参数：**
- `file` (文件): Excel文件（.xlsx或.xls）
- `idColumnIndex` (查询参数，可选): ID列索引（从0开始，如果为null则自动查找）
- `descriptionColumnIndex` (查询参数，可选): 描述列索引（可选）

**请求示例（使用curl）：**
```bash
curl -X POST "http://localhost:8080/api/disaster-decode/file/decode-excel?idColumnIndex=0&descriptionColumnIndex=1" \
  -F "file=@data.xlsx"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "Excel解码完成：共100行，成功95行，失败5行",
  "data": {
    "fileName": "data.xlsx",
    "totalRows": 100,
    "successCount": 95,
    "failCount": 5,
    "rows": [
      {
        "rowIndex": 2,
        "id": "123456789012345678901234567890123456",
        "description": "房屋破坏数据",
        "success": true,
        "decodedId": {
          "originalId": "123456789012345678901234567890123456",
          "disasterCategoryName": "房屋破坏",
          ...
        }
      },
      {
        "rowIndex": 3,
        "id": "987654321098765432109876543210987654",
        "description": "人员伤亡数据",
        "success": true,
        "decodedId": {...}
      }
    ]
  }
}
```

### 7.4 解码Excel文件内容（从文件路径）

**接口地址：** `POST /api/disaster-decode/file/decode-excel/path`

**功能说明：** 从服务器文件路径读取Excel文件并解码

**请求体：**
```json
{
  "filePath": "/path/to/data.xlsx",
  "idColumnIndex": 0,
  "descriptionColumnIndex": 1
}
```

**响应格式：** 与7.3相同

## 八、高德地图API配置

系统已集成高德地图API，配置信息位于 `application.yml`：

```yaml
amap:
  api:
    key: 514cde08eadb88096bcf0fe0a11f5e88
    security: bed34790b035008203b5ea72cb23920d
```

## 九、注意事项

1. 所有时间参数使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss`
2. 36位ID必须全部为数字
3. 地理码解析功能需要根据实际编码规则完善
4. 批量操作建议控制数量，避免超时
5. 数据保存时会自动尝试解码ID，如果解码失败，仍会保存原始数据
6. **文件名解码：** 系统会自动从文件名中提取36位数字作为ID，支持文件名中包含其他字符
7. **Excel解码：** 
   - 支持.xlsx和.xls格式
   - 如果不指定ID列索引，系统会自动查找包含36位ID的列
   - 自动查找逻辑：先检查表头（包含"ID"、"编码"等关键词），再检查数据行
   - 支持读取描述列（可选）
   - 每行的解码结果包含成功/失败状态和错误信息

