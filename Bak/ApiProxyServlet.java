package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vteam.ctbc.frame.system.log.VteamLogFactory;
import vteam.ctbc.frame.system.log.VteamLogger;

@WebServlet("/api-proxy/*")
public class ApiProxyServlet extends HttpServlet {
	
	private static final long serialVersionUID = 7813288910476534102L;
	private static final VteamLogger bussLog = VteamLogFactory.getBussLog(ApiProxyServlet.class.getName());

	private static final String AR_BASE_URL = "http://192.168.18.207:8090/CTBC"; // AR 伺服器 URL
	// ResourceBundle appConfig = ResourceBundle.getBundle("app_config", Locale.getDefault(), GetLoader.loader());
	// appConfig.getString("AR_IP_SIT")
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String path = req.getPathInfo(); // 取得 /BPaidQuery、/SPaidQuery...
    	
    	bussLog.info("轉發到 AR 的對應 API, path=" + path);
    	
        if (path == null || path.equals("/")) {
            bussLog.error("Invalid API request");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid API request, path=" + path);
            return;
        }
        
        String targetUrl = AR_BASE_URL + path; // 轉發到 AR 的對應 API

        // 讀取 WBK 傳入的 JSON
        String requestBody = readRequestBody(req);

        // 轉發請求至 AR
        String arResponse = forwardToAR(targetUrl, requestBody);

        // 回傳 AR 的 JSON 給 WBK
        resp.setContentType("application/json");
        resp.getWriter().write(arResponse);
    }

    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private String forwardToAR(String targetUrl, String jsonBody) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // 傳送 JSON
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
        }

        // 讀取回應
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
}