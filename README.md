# 上下班时间记录系统

## 项目简介

这是一个基于Spring Boot的上下班时间记录系统，支持根据国家日历自动判断工作日和节假日，记录上下班时间，并自动计算平均工时。

## 技术栈

### 后端
- Spring Boot 2.6.3
- MyBatis-Plus 3.5.3
- H2数据库（本地数据库，无需安装）
- Java 8+

### 前端
- HTML5
- CSS3
- JavaScript (原生)

## 项目结构

```
.
├── app/                          # 后端代码目录
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/practise/demo/
│   │   │   │       ├── common/          # 公共层
│   │   │   │       │   ├── constant/    # 常量类
│   │   │   │       │   ├── exception/   # 异常处理
│   │   │   │       │   └── response/    # 响应类
│   │   │   │       ├── config/          # 配置类
│   │   │   │       ├── controller/      # 控制器
│   │   │   │       ├── mapper/          # 数据访问层
│   │   │   │       ├── model/           # 数据模型
│   │   │   │       │   ├── dto/         # 数据传输对象
│   │   │   │       │   ├── entity/      # 实体类
│   │   │   │       │   └── vo/          # 视图对象
│   │   │   │       ├── service/         # 业务层
│   │   │   │       └── util/            # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml      # 配置文件
│   │   │       └── db/
│   │   │           └── schema.sql       # 数据库初始化脚本
│   │   └── test/                        # 测试代码
│   └── pom.xml                          # Maven配置
├── front/                        # 前端代码目录
│   ├── index.html               # 主页面
│   ├── css/
│   │   └── style.css           # 样式文件
│   └── js/
│       └── app.js              # 前端逻辑
└── README.md                    # 项目说明

```

## 功能特性

1. **上下班时间记录**
   - 支持选择日期和上下班时间
   - 自动判断工作日/节假日（基于国家日历）
   - 自动计算工作时长

2. **平均工时统计**
   - 总记录数统计
   - 总工作时长统计
   - 平均工作时长计算

3. **记录管理**
   - 添加/编辑记录
   - 删除记录
   - 按日期范围查询记录

4. **国家日历支持**
   - 自动识别法定节假日
   - 支持调休工作日
   - 自动标记工作日/节假日

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+

### 启动步骤

1. **启动后端服务**

```bash
cd app
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

2. **打开前端页面**

直接在浏览器中打开 `front/index.html` 文件，或使用HTTP服务器：

```bash
# 使用Python启动简单HTTP服务器
cd front
python -m http.server 8000
```

然后访问 `http://localhost:8000`

### 数据库配置

系统使用H2本地数据库，无需额外安装。数据库文件会自动创建在 `./data/worktime.mv.db`

访问H2控制台：`http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/worktime`
- 用户名: `sa`
- 密码: (空)

## API接口

### 1. 保存/更新记录

```
POST /api/work-time/save
Content-Type: application/json

{
  "id": null,              // 更新时传入ID
  "workDate": "2024-01-15",
  "startTime": "09:00:00",
  "endTime": "18:00:00",
  "remark": "备注信息"
}
```

### 2. 查询所有记录

```
GET /api/work-time/list
```

### 3. 按日期范围查询

```
GET /api/work-time/list-by-date?startDate=2024-01-01&endDate=2024-01-31
```

### 4. 查询单条记录

```
GET /api/work-time/{id}
```

### 5. 删除记录

```
DELETE /api/work-time/{id}
```

### 6. 计算平均工时

```
GET /api/work-time/average
```

响应示例：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalRecords": 20,
    "totalHours": 160.00,
    "averageHours": 8.00,
    "averageMinutes": 480
  }
}
```

## 国家日历说明

系统内置了2024年的国家法定节假日和调休工作日：

- **法定节假日**：元旦、春节、清明节、劳动节、端午节、中秋节、国庆节
- **调休工作日**：法定节假日调休需要上班的周末

工作日判断逻辑：
1. 如果是调休工作日，直接返回工作日
2. 如果是法定节假日，返回节假日
3. 否则判断是否为周末（周六、周日）

## 注意事项

1. **数据库文件位置**：H2数据库文件存储在 `./data/worktime.mv.db`，请勿删除
2. **跨域配置**：前端和后端分离部署时，需要配置CORS（已配置允许所有来源）
3. **日期格式**：日期格式为 `yyyy-MM-dd`，时间格式为 `HH:mm:ss`
4. **工作时长计算**：如果下班时间小于上班时间，系统会自动处理跨天情况

## 开发说明

### 后端代码格式

后端代码参考 `src` 目录的代码风格：
- 使用标准的Spring Boot项目结构
- Controller层处理HTTP请求
- Service层处理业务逻辑
- Mapper层处理数据库操作
- 统一的异常处理和响应格式

### 前端代码

前端使用原生HTML/CSS/JavaScript，无需构建工具：
- `index.html`：主页面结构
- `css/style.css`：样式文件
- `js/app.js`：前端逻辑

## 常见问题

1. **数据库连接失败**
   - 检查 `./data` 目录是否有写入权限
   - 确认H2数据库文件未被其他进程占用

2. **前端无法连接后端**
   - 检查后端服务是否启动（`http://localhost:8080`）
   - 检查浏览器控制台是否有CORS错误
   - 确认 `front/js/app.js` 中的 `API_BASE_URL` 配置正确

3. **日期判断不准确**
   - 系统内置了2024年的节假日数据
   - 如需更新其他年份，请修改 `ChineseCalendarUtil.java`

## 许可证

MIT License
