package my.debug;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExecutionTimer {

    /** 儲存開始時間的快取表（thread-safe） */
    private static final Map<String, Long> START_TIME_MAP = new ConcurrentHashMap<>();

    /** 禁止外部實例化 */
    private ExecutionTimer() {
        throw new AssertionError("No com.example.utils.ExecutionTimer instances for you!");
    }

    /**
     * 啟動計時器
     *
     * @param name 任務名稱（用於標識不同計時）
     */
    public static void start(String name) {
        START_TIME_MAP.put(name, System.nanoTime());
        System.out.printf("⚙️ [%s] 計時開始%n", name);
    }

    /**
     * 結束計時器並印出執行時間
     *
     * @param name 任務名稱（必須與 start 相同）
     */
    public static void end(String name) {
        Long start = START_TIME_MAP.remove(name);

        if (start == null) {
            System.err.printf("⚠️ [%s] 尚未啟動計時或已被移除%n", name);
            return;
        }

        long elapsedNanos = System.nanoTime() - start;
        double elapsedMillis = elapsedNanos / 1_000_000.0;
        double elapsedSeconds = elapsedMillis / 1000.0;

        System.out.printf("✅ [%s] 完成，耗時：%.3f 秒 (%.3f 毫秒)%n", name, elapsedSeconds, elapsedMillis);
    }
}