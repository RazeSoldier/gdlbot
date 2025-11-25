# GDLBot
用Java编写的机器人。提供获取PH相关通知和实时转发discord消息到QQ群的能力。

使用[Overflow库](https://github.com/MrXiaoM/Overflow)代替缺乏维护的Mirai库，作为客户端连接OneBot实现

## 功能
- 转发Discord消息到QQ（需要在Discord注册自己的机器人并在服务器添加机器人赋予频道读取权限）
- 查询PH官网的即将到来事件，perping、新手教学等（需要在PH有角色）
- 查询PH黑名单（需要具有blacklist权限）

## 系统要求
- Docker
- 代理，用来连接Discord

## 安装
### 下载源代码
```bash
git clone https://github.com/RazeSoldier/gdlbot.git
```

### 方法一：机器人本体运行 (GDLBot only)
如果你已经有其他OneBot实现可以只运行机器人本体，如果你不清楚什么是OneBot或者没有相关实现可以方法二
```bash
docker run -d --name gdlbot --env-file env.list -e ONEBOT_HOST=ws://host.docker.internal:8000 razesoldier/gdlbot:0.2
```
 - `-d` 指定容器后台运行
 - `--name gdlbot` 指定容器名为gdlbot
 - `--env-file env.list` 指定环境变量文件env.list，会将文件内容视为环境变量传递给容器
 - `-e ONEBOT_HOST=ws://host.docker.internal:8000` （必须）指定OneBot实现，必须以ws://开头，host.docker.internal代表宿主IP

### 方法二：Docker Compose运行 (GDLBot + Lagrange)
`composer.yaml`编排了GDLBot + [Lagrange](https://github.com/LagrangeDev/Lagrange.Core)
```bash
docker compose up -d
```

## 安装
1. 克隆仓库：
```bash
git clone https://github.com/RazeSoldier/gdlbot.git
cd gdlbot
```

2. 构建项目：
```bash
gradle build
```

## 配置
1. 编辑`config.json`文件：
```json
{
  "qq": {
    "account": "你的QQ号",
    "password": "你的QQ密码"
  },
  "ph": {
    "cookie": "你的PH站点cookie"
  },
  "tencentCloud": {
    "secretId": "腾讯云API SecretId",
    "secretKey": "腾讯云API SecretKey"
  },
  "proxy": {
    "type": "socks5",
    "host": "代理地址",
    "port": 代理端口
  },
  "discord": {
    "token": "你的Discord Bot Token",
    "rules": [
      {
        "discordChannelId": "Discord频道ID",
        "qqGroupId": "QQ群号"
      }
    ]
  }
}
```

2. 配置环境变量（容器化部署时需要）：
```bash
# 编辑env.list文件
ONEBOT_HOST=ws://onebot-backend:8089
PROXY_TYPE=socks5
PROXY_HOST=代理地址
PROXY_PORT=代理端口
```

## 运行
### 本地运行
```bash
gradle run
```

### 容器化部署
1. 构建并启动服务：
```bash
docker-compose up -d
```

2. 停止服务：
```bash
docker-compose down
```

## 许可证
本项目使用AGPL-3.0许可证。许可证的完整文本可参见[LICENSE](https://github.com/RazeSoldier/gdlbot/blob/master/LICENSE)

## 支持
如果有任何问题或建议，欢迎提PR或者issue