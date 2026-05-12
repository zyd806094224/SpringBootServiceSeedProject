# 阿里云服务器 IP + 自签名证书 HTTPS 配置指南

> 适用场景：无域名 + 国内阿里云服务器 + 宝塔面板 + Java 后端服务
> 最终效果：`https://公网IP:8443` → Nginx → `http://127.0.0.1:Java端口`

---

## 一、生成自签名证书

在服务器终端执行（把 `你的公网IP` 替换成真实 IP）：

```bash
mkdir -p /etc/ssl/myapp

openssl req -x509 -newkey rsa:2048 \
  -keyout /etc/ssl/myapp/key.pem \
  -out /etc/ssl/myapp/cert.pem \
  -days 3650 -nodes \
  -subj "/CN=你的公网IP"
```

查看证书内容（后面要用到）：

```bash
cat /etc/ssl/myapp/cert.pem   # 证书内容
cat /etc/ssl/myapp/key.pem    # 密钥内容
```

> 有效期 3650 天（10年），到期后重新执行以上命令即可

---

## 二、宝塔面板配置

### 1. 新建站点

宝塔 → **网站** → **添加站点**

```
域名：你的公网IP
PHP版本：纯静态
```

### 2. 配置反向代理

站点列表 → 站点「设置」→ **反向代理** → **添加反向代理**

```
代理名称：java-api
目标URL：http://127.0.0.1:你的Java端口
发送域名：$host
```

### 3. 配置 SSL 证书

站点「设置」→ **SSL** → **其他证书**

- 上方框粘贴 `cert.pem` 的内容
- 下方框粘贴 `key.pem` 的内容
- 点击「保存」

### 4. 修改监听端口

站点「设置」→ **配置文件**

把：
```nginx
listen 443 ssl;
```
改为：
```nginx
listen 8443 ssl;
```

保存 → 重载 Nginx。

---

## 三、开放端口

### 宝塔防火墙

宝塔 → **安全** → **系统防火墙** → 放行端口 `8443`

### 阿里云安全组

阿里云控制台 → ECS → 安全组 → 入方向 → 添加规则：

| 协议 | 端口 | 来源 |
|------|------|------|
| TCP | 8443 | 0.0.0.0/0 |

---

## 四、关闭 Java 端口外网访问（重要）

只让 Nginx 内部访问 Java，避免绕过 HTTPS 直接访问：

**阿里云安全组：** 删除 Java 端口（如 8066）的入站规则

**宝塔防火墙：** 删除 Java 端口的放行规则

```
外网用户
  ├─ https://IP:8443 → Nginx → 127.0.0.1:Java端口 ✅
  └─ http://IP:Java端口 → 安全组拦截 ❌
```

---

## 五、本地电脑信任证书（去掉浏览器警告）

把证书下载到本地：

```bash
scp root@你的公网IP:/etc/ssl/myapp/cert.pem ./cert.pem
```

**Windows 导入：**
1. 双击 `cert.pem`
2. 点击「安装证书」
3. 存储位置选「本地计算机」
4. 选「受信任的根证书颁发机构」
5. 完成 → 重启浏览器

**Mac 导入：**
```bash
sudo security add-trusted-cert -d -r trustRoot \
  -k /Library/Keychains/System.keychain cert.pem
```

---

## 六、验证

```bash
# 忽略证书警告测试接口
curl -k https://你的公网IP:8443/你的接口路径
```

浏览器访问 `https://你的公网IP:8443`，地址栏出现 🔒 即成功。

---

## 常见问题

**Q: 浏览器提示 ERR_CERT_AUTHORITY_INVALID**
> 按第五步把证书导入系统信任即可

**Q: 访问 8443 超时**
> 检查阿里云安全组和宝塔防火墙是否都放行了 8443

**Q: 接口返回 502 Bad Gateway**
> Java 服务未启动，或反向代理填写的端口不对

**Q: 原来的 HTTP 接口还能访问**
> 去阿里云安全组删除 Java 端口的入站规则

**Q: 证书 10 年后过期**
> 重新执行第一步生成新证书，替换 cert.pem 和 key.pem，宝塔 SSL 重新粘贴保存

---

## 快速备忘

| 项目 | 值 |
|------|----|
| 证书路径 | `/etc/ssl/myapp/cert.pem` |
| 密钥路径 | `/etc/ssl/myapp/key.pem` |
| 外网访问端口 | `8443` |
| Java 服务端口 | `你的端口（如8066）` |
| 访问地址 | `https://你的公网IP:8443` |
