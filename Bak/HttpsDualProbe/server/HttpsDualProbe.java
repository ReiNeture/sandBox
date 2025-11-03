package test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class HttpsDualProbe {

    // 目標 URL
    private static final String TARGET =
            "https://127.0.0.1:8443/client/hello";

    public static void main(String[] args) {
        System.out.println("=== HTTPS Dual Probe (Default Trust vs. Embedded Cert) ===");
        System.out.println("Target: " + TARGET);

        // 從瀏覽器直接匯出的憑證
        String certPath = "C:/p/cer/server2.crt";

        // (A)、(B) 建議一次只開啟一個區塊 以免log顯示錯誤 
        
        // A) 預設信任：多半會因為自簽/公司CA而失敗
        System.out.println("\n[A] Default TrustManager (JVM cacerts) ===>");
        probe(defaultClient());

        // B) 自帶憑證：只信任你提供的那張/那條
        System.out.println("\n[B] Embedded Cert (" + certPath + ") ===>");
        probe(embeddedCertClient(certPath));

        System.out.println("\n=== Done ===");
    }

    /** 以 JVM 預設 TrustManager 建立 WebClient */
    private static WebClient defaultClient() {
        HttpClient http = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(http))
                .build();
    }

    /** 以「程式自帶憑證」建立 WebClient（不動 JVM，全程僅此 Client 生效） */
    private static WebClient embeddedCertClient(String certPath) {
        try {
            SslContext sslContext = SslContextBuilder.forClient()
                    // 可吃 PEM/CER，只要是 X.509（若是 DER .cer，可先轉 pem；或直接給這行一個對的 .cer）
                    .trustManager(new File(certPath))
                    .build();

            HttpClient http = HttpClient.create()
                    .secure(ssl -> ssl.sslContext(sslContext))
                    .responseTimeout(Duration.ofSeconds(30));

            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(http))
                    .build();
        } catch (SSLException e) {
            throw new IllegalStateException("初始化 SSLContext 失敗：" + e.getMessage(), e);
        }
    }

    /** 真正發 GET（TLS 在 HTTP 之前，不論 GET/POST 都會先驗憑證） */
    private static void probe(WebClient client) {
        long t0 = System.nanoTime();
        try {
            String body = client.get()
                    .uri(TARGET)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .timeout(Duration.ofSeconds(30))
                    .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                    .onErrorResume(ex -> {
                        System.out.println("❌ Error: " + ex.getClass().getSimpleName() + " — " + ex.getMessage());
                        return Mono.empty();
                    })
                    .block();

            long ms = (System.nanoTime() - t0) / 1_000_000;
            if (body != null) {
                System.out.println("✅ OK (" + ms + " ms), body.length=" + body.length());
                System.out.println("BODY=" + body);
                // 如需看字串：System.out.println(body);
            } else {
                System.out.println("↪ 已記錄錯誤（見上方）。耗時 " + ms + " ms");
            }
        } catch (Exception e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("💥 Exception after " + ms + " ms: " + e);
        }
    }

}