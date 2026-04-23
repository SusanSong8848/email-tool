# 邮件群发工具（email-tool）

## 简介
这是一个基于 Java 17 的命令行邮件群发工具，面向课程实验与小型批量邮件任务。项目包含两个核心模块：
- Clean：清洗通讯录数据，输出标准化联系人或邮箱列表。
- Mailer：基于模板批量发送邮件，支持预览、限速、重试与黑名单过滤。

## 功能特性
- 通讯录清洗：支持多分隔符输入（逗号、分号、竖线）。
- 邮箱合法性校验：过滤非法邮箱记录。
- 数据输出格式化：支持 formal 与 lower 两种格式。
- 邮箱列表导出：可按邮箱模式导出并去重。
- 批量发送：按模板替换占位符并发送。
- 发送限速：保证发送节奏，避免过快触发 SMTP 限制。
- 自动重试：发送失败后按策略重试。
- 黑名单过滤：跳过指定收件人。
- 预览模式：生成 .eml 草稿文件，不实际发送。
- 发送日志：支持 SUCCESS、FAILED、SKIPPED、PREVIEW 状态。

## 技术栈
- Java 17
- Maven
- Jakarta Mail 2.0.1
- JUnit 5

## 环境要求
- JDK 17 或更高版本
- Maven 3.8+
- 可用的 SMTP 邮箱账号（用于 --send 模式）

运行说明：
- `--preview` 可直接使用 `java -cp target/classes ...` 运行。
- `--send` 需要 Jakarta Mail 依赖在运行时类路径中（推荐用 Maven 运行）。

## 安装与构建
1. 克隆或进入项目目录。
2. 编译项目：

```bash
mvn clean compile
```

3. 运行测试：

```bash
mvn test
```

4. 打包：

```bash
mvn clean package
```

## 配置说明（SMTP 设置）
Mailer 在发送模式下会从类路径读取 mail.properties。

建议创建文件：
- src/main/resources/mail.properties

示例配置：

```properties
mail.smtp.host=smtp.example.com
mail.smtp.port=587
mail.username=your_account@example.com
mail.password=your_password
mail.smtp.auth=true
mail.smtp.starttls.enable=true

# 可选：发送限速与重试间隔（毫秒）
mail.send.minIntervalMs=501
mail.retry.intervalMs=3000
```

说明：
- mail.username 与 mail.password 不应硬编码在 Java 代码中。
- 项目已通过 .gitignore 忽略 *.properties，避免敏感配置误提交。



## 使用方法
### Clean 子命令
用途：清洗联系人输入文件，并输出规范化结果或邮箱列表。

样例 1（清洗通讯录，clean + formal，输出 clean.txt 与 bad.txt）：

```powershell
@"
alice , Alice@Example.COM , shanghai
Bob;bob@example.com;Beijing
charlie| charlie@example.com |   New   York
Daisy, daisyexample.com, Paris
Eve, eve@evil., LA
Frank|frank@foo.com
,,,
	Grace  ; GRACE@EXAMPLE.COM ; shen zhen
"@ | Set-Content -Encoding UTF8 raw.txt

java -cp target/classes com.student.emailtool.cleaner.Clean -i raw.txt -o clean.txt --bad bad.txt --mode clean --format formal
```

样例 2（导出邮箱名单，emails + lower + dedup，输出 emails.txt）：

```powershell
@"
Tom|TOM@EXAMPLE.COM|Hangzhou
tom,tom@example.com,hangzhou
Jerry; jerry@Example.Com ; Shanghai
BadGuy|badguy.example.com|Nowhere
ALICE, Alice@Example.COM, SH
"@ | Set-Content -Encoding UTF8 raw2.txt

java -cp target/classes com.student.emailtool.cleaner.Clean -i raw2.txt -o emails.txt --mode emails --format lower --dedup
```

常用参数：
- -i <input>：输入文件（必填）
- -o <output>：输出文件，默认输出到标准输出
- --bad <file>：保存不合法行
- --mode <clean|emails>：输出模式
- --format <formal|lower>：clean 模式下的格式
- --dedup：emails 模式去重
- --log <file>：写入统计日志
- --verbose：在标准错误输出统计信息

### Mailer 子命令
用途：按模板批量发送邮件，或生成预览 .eml 文件。

模板要求：
- 支持邮件头与正文混排的纯文本模板
- 头部区域必须包含 `Subject:`，`To:` 等其他头部可选
- 头部与正文之间用一个空行分隔
- 支持占位符：{name}、{email}、{city}

样例 3（邮件预览，读取样例 1 生成的 clean.txt 与模板，使用样例 2 生成的 emails.txt，输出 outbox/*.eml）：

```powershell
@"
To: {email}
Subject: Club Notice: Welcome {name}

Hi {name},
Your city is {city}.
See you at the club!
"@ | Set-Content -Encoding UTF8 template.txt

java -cp target/classes com.student.emailtool.mailer.Mailer -e emails.txt -c clean.txt -t template.txt --preview
```

预览模式写日志示例（可选）：

```powershell
java -cp target/classes com.student.emailtool.mailer.Mailer -e emails.txt -c clean.txt -t template.txt --preview --log out/sent.log
```

示例日志状态说明：
- `PREVIEW`：已成功生成 `.eml` 预览文件
- `SKIPPED`：被黑名单过滤或联系人缺失
- `FAILED`：预览文件写入失败

发送模式示例：

```bash
mvn -q exec:java -Dexec.mainClass=com.student.emailtool.mailer.Mailer -Dexec.args="-e emails.txt -c contacts_clean.csv -t template.txt --send --log sent.log --blacklist blacklist.txt"
```

常用参数：
- -e <emails.txt>：收件人邮箱列表（必填）
- -c <contacts.csv>：联系人文件（必填）
- -t <template.txt>：邮件模板（必填）
- --preview：预览模式，输出到 outbox/
- --send：实际发送模式
- --log <sent.log>：日志文件（--send 必填，--preview 可选）
- --blacklist <file>：黑名单文件（可选）

PowerShell 输入文件建议：
- 若使用 PowerShell 5.1，建议用 `Set-Content -Encoding utf8NoBOM`，避免 UTF-8 BOM 导致首字符异常（如 `Alice` 变成乱码前缀）。

## 测试
当前包含 JUnit 5 单元测试：
- util.EmailValidatorTest：覆盖合法与多类非法邮箱场景。
- model.ContactTest：覆盖 toCsvLine 在 formal 与 lower 下的输出。

执行：

```bash
mvn test
```

## 目录结构
```text
email-tool/
├─ pom.xml
├─ src/
│  ├─ main/
│  │  └─ java/com/student/emailtool/
│  │     ├─ cleaner/
│  │     │  └─ Clean.java
│  │     ├─ mailer/
│  │     │  ├─ JakartaMailSender.java
│  │     │  └─ Mailer.java
│  │     ├─ model/
│  │     │  ├─ Contact.java
│  │     │  └─ SendLog.java
│  │     └─ util/
│  │        └─ EmailValidator.java
│  └─ test/
│     └─ java/com/student/emailtool/
│        ├─ model/
│        │  └─ ContactTest.java
│        └─ util/
│           └─ EmailValidatorTest.java
└─ outbox/ (预览模式输出)
```

## 贡献指南
1. 新建分支进行开发。
2. 保持代码风格一致，补充必要测试。
3. 提交前运行 mvn test。
4. 提交 PR 时说明变更内容与验证结果。

## 许可证
当前仓库未明确声明开源许可证。
如需开源发布，建议补充 LICENSE 文件（例如 MIT 或 Apache-2.0）。



## 建设性意见：
### 建设性意见 1：限速实现去依赖化
- **问题**：Agent 初始方案使用 Guava 库的 RateLimiter 进行发送限速。
- **我的改进要求**：要求改为纯 Java 原生实现，通过 System.currentTimeMillis() 计算间隔并调用 Thread.sleep 补足差值。
- **改进效果**：消除了对 Guava 的依赖，项目更加轻量，且限速精度对于邮件发送场景完全足够。

### 建设性意见 2：防御性编程补充
- **问题**：Agent 生成的 prepareEmail 方法未处理 Contact 对象或 name 字段为 null 的情况，可能导致 NullPointerException。
- **我的改进要求**：要求增加空值判断，将 null 值替换为空字符串或默认值。
- **改进效果**：提高了程序的健壮性，避免因个别联系人数据缺失导致整个发送流程中断。

### 建设性意见 3：预览模式文件格式规范
- **问题**：预览模式最初只生成了纯文本文件，缺少标准邮件头，无法直接导入邮件客户端。
- **我的改进要求**：要求生成符合 RFC 822 格式的 .eml 文件，包含 From、To、Subject、Date、MIME-Version 等头部。
- **改进效果**：预览文件可直接双击用 Outlook/Thunderbird 打开，极大提升了预览体验。


## Vibe-coding：
[Java 邮件群发工具 Maven 项目结构创建](src/docs/chat1.json)
[重构 Clean.java 以支持 Contact 类](src/docs/chat2.json)
[完善 Mailer.java 中的 sendAllEmails() 方法](src/docs/chat3.json)
[JUnit 5 单元测试编写请求](src/docs/chat4.json)
[Mailer.java 安全策略实现审查与验证](src/docs/chat5.json)
[Java Mailer 类初始化错误分析](src/docs/chat6.json)
[Java邮件工具类初始化错误分析](src/docs/chat7.json)

# 远程仓库 URL：
**https://github.com/SusanSong8848/email-tool.git** 本项目以同步到本人的Git远程仓库