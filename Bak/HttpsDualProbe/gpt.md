沒問題！下面給你一套「最小但乾淨」的範例：一個可部署到本機 Tomcat 的 **GET** 範例 + **自簽憑證（含 SAN）建立** + **Tomcat HTTPS 設定**。
重點是：**憑證一定要把你要用來連線的主機名/IP 寫進 SAN**（不然就會出現你剛剛那個 Hostname 驗證錯誤）。

---

# ① 建一個極簡可 GET 的 Web 專案（WAR）

## `pom.xml`（假設 Tomcat 9.x，Servlet 4 / `javax.*`）

> 如果你用 Tomcat 10+（`jakarta.*`），我在下面補充改法。

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0  http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>demo</groupId>
  <artifactId>hello-https</artifactId>
  <version>1.0.0</version>
  <packaging>war</packaging>

  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <!-- 對 Tomcat 9.x：使用 javax.servlet-api 4.x，scope=provided -->
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>4.0.1</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>

  <build>
    <finalName>hello-https</finalName>
  </build>
</project>
```

## `src/main/java/demo/web/HelloServlet.java`

```java
package demo.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 仙氣兔兔版最小 GET 範例：/hello
 * 乾淨可讀，無第三方依賴，布署即用。
 */
@WebServlet(name = "HelloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().println("🟢 HTTPS OK — " + req.getRequestURL());
    }
}
```

## `src/main/webapp/WEB-INF/web.xml`（簡化版）

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         version="4.0">
  <display-name>hello-https</display-name>
</web-app>
```

建置：

```bash
mvn clean package
```

→ 產生 `target/hello-https.war`，丟到 Tomcat 的 `webapps/` 目錄即可。

---

# ② 建立 **自簽憑證（含 SAN）** 給 Tomcat 用

> 目的：讓你用 `https://localhost` 或 `https://127.0.0.1` 或你的內網 IP 連線時，**主機名驗證也能通過**。

在你電腦上執行（Windows/Linux/macOS 皆可）：

```bash
# 建一個 JKS keystore，alias=tomcat，含 SAN（視你要測的目標調整）
keytool -genkeypair -alias tomcat \
  -keyalg RSA -keysize 2048 -validity 825 \
  -dname "CN=localhost, OU=Dev, O=YourOrg, L=Taipei, ST=Taiwan, C=TW" \
  -ext "SAN=dns:localhost,ip:127.0.0.1,ip:192.168.18.5" \
  -keystore /path/to/server.jks -storepass changeit -keypass changeit
```

> 說明
>
> * `-ext "SAN=..."` 請把你要用來連線的 **IP / DNS** 都列進去（至少 `127.0.0.1` + 你的內網 IP）。
> * 想改用 PKCS#12 也 OK：加 `-storetype PKCS12`，Tomcat 一樣吃。

---

# ③ 在 Tomcat 開 HTTPS Connector

開 `TOMCAT_HOME/conf/server.xml`，找到（或加入）以下 Connector（改成你的 keystore 路徑與密碼）：

```xml
<!-- 建議用 8443 測試 -->
<Connector
    port="8443"
    protocol="org.apache.coyote.http11.Http11NioProtocol"
    SSLEnabled="true"
    scheme="https"
    secure="true"
    clientAuth="false"
    sslProtocol="TLS"
    keystoreFile="/path/to/server.jks"
    keystorePass="changeit"
    keyAlias="tomcat" />
```

> 若你用的是 PKCS12，另外加上：`keystoreType="PKCS12"`。

重啟 Tomcat：

```bash
# 依你作業系統
$TOMCAT_HOME/bin/shutdown.sh
$TOMCAT_HOME/bin/startup.sh
```

---

# ④ 測試

瀏覽器或 curl 測：

```bash
# DNS
https://localhost:8443/hello-https/hello

# IP（必須在憑證 SAN 內）
https://127.0.0.1:8443/hello-https/hello
https://192.168.18.5:8443/hello-https/hello
```

第一次瀏覽器會警告「自簽」不是公有 CA；**這是預期**。
如果你用 Java 客戶端測試並要通過「信任鏈」，請把伺服器憑證匯出給 client 使用：

```bash
# 從 keystore 匯出公鑰憑證（給客戶端 trustManager 用；安全）
keytool -exportcert -alias tomcat -keystore /path/to/server.jks \
  -storepass changeit -rfc -file server.cer
```

---

## （可選）Tomcat 10+（`jakarta.*`）的差異

若你是 Tomcat 10/10.1/11，請把程式碼與依賴換成 `jakarta.servlet`：

**pom.xml**

```xml
<dependency>
  <groupId>jakarta.servlet</groupId>
  <artifactId>jakarta.servlet-api</artifactId>
  <version>5.0.0</version>
  <scope>provided</scope>
</dependency>
```

**HelloServlet.java**

```java
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
...
```

**web.xml**

```xml
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         version="5.0">
  <display-name>hello-https</display-name>
</web-app>
```

---

## 小抄（常見坑避免）

* **必加 SAN**：要用 IP 連，就把 IP 放進 `-ext "SAN=ip:..."`；用 DNS 連，就把 `dns:...` 加進去。
* **路徑權限**：`server.xml` 的 `keystoreFile` 請用**絕對路徑**，避免 Tomcat 找不到檔案。
* **Port**：8443 未被占用；Windows 上記得允許 Java 開這個埠。
* **WAR 路徑**：`/hello-https/hello` 的 context path 來自 WAR 檔名（`<finalName>`）。
* **客戶端**要「信任自簽」→ 匯出 `server.cer` 給 client 用 `trustManager(new File("server.cer"))`。

---

如果你要，我可以再幫你生成一個「zip 模板」的檔案清單（含上述檔案結構），
或把 **JBoss** 的 keystore 生成命令（含 IP/DNS SAN）也一併給你，兩邊測起來就完全一致。
