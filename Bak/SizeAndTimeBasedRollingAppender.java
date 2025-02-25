package vteam.common.tools.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class SizeAndTimeBasedRollingAppender extends AppenderSkeleton {
    private String fileName;
    private long maxFileSize = 10 * 1024 * 1024; // 預設 10MB
    private int maxBackupIndex = 3; // 最多保留 3 個備份檔案
    private String datePattern = "yyyy-MM-dd";
    private PrintWriter writer;
    private File logFile;
    private String lastDate = "";
    private long currentFileSize = 0; // 記錄當前檔案大小，避免頻繁調用 logFile.length()

    public void setFile(String file) {
        this.fileName = file;
    }

    public void setMaxFileSize(String size) {
        this.maxFileSize = parseFileSize(size);
    }

    public void setMaxBackupIndex(int index) {
        this.maxBackupIndex = index;
    }

    @Override
    public void activateOptions() {
        try {
            checkAndRotateLogFileOnStartup(); // 伺服器啟動時檢查是否要切換檔案
            openLogFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void append(LoggingEvent event) {
        try {
            String logMessage = this.layout.format(event);
            long logSize = logMessage.getBytes(StandardCharsets.UTF_8).length;

            // **先寫入日誌，再檢查是否超過大小**
            writer.print(logMessage);
            writer.flush();

            // **累加寫入的大小，減少頻繁的 File.length() 呼叫**
            currentFileSize += logSize;

            // **條件觸發時才真正去檢查檔案大小**
            if (currentFileSize >= maxFileSize) {
                rotateLogFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 伺服器啟動時檢查是否需要滾動日誌
     */
    private void checkAndRotateLogFileOnStartup() throws IOException {
        File logDir = new File(new File(fileName).getParent());
        if (!logDir.exists()) {
            logDir.mkdirs(); // 確保日誌目錄存在
        }

        logFile = new File(fileName); // 預設最新的 log 檔案
        if (logFile.exists()) {
            // 讀取現有檔案的最後修改時間
            String currentDate = new SimpleDateFormat(datePattern).format(new Date());
            String lastModifiedDate = new SimpleDateFormat(datePattern).format(new Date(logFile.lastModified()));

            if (!currentDate.equals(lastModifiedDate)) {
                // **若日期變更，則將舊檔案重新命名**
                rotateLogFile();
            } else {
                // **若日期未變更，則直接開啟現有檔案**
                currentFileSize = logFile.length();
            }
        } else {
            // **首次啟動且無日誌檔，則直接建立新的日誌檔案**
            logFile.createNewFile();
            currentFileSize = 0;
        }
    }

    /**
     * 開啟最新的 log 檔案
     */
    private void openLogFile() throws IOException {
        if (writer != null) {
            writer.close();
        }
        writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8));
    }

    /**
     * 進行日誌滾動（切換檔案）
     */
    private void rotateLogFile() throws IOException {
        if (writer != null) {
            writer.close();
        }

        // **更新 lastDate**
        lastDate = new SimpleDateFormat(datePattern).format(new Date());
        String archiveName = fileName + "." + lastDate;
        File archivedFile = new File(archiveName);

        if (archivedFile.exists()) {
            renameOldFiles();
        }
        logFile.renameTo(archivedFile);

        // **重新開啟新的 "BussLog.log"**
        logFile = new File(fileName);
        openLogFile();

        // **重置大小計算**
        currentFileSize = logFile.length();
    }


    /**
     * 保留舊的日誌檔案，最多 maxBackupIndex 個備份
     */
    private void renameOldFiles() {
        for (int i = maxBackupIndex; i > 0; i--) {
            File oldFile = new File(fileName + "." + lastDate + "." + i);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            File prevFile = new File(fileName + "." + lastDate + "." + (i - 1));
            if (prevFile.exists()) {
                prevFile.renameTo(oldFile);
            }
        }
        File newFile = new File(fileName + "." + lastDate);
        newFile.renameTo(new File(fileName + "." + lastDate + ".1"));
    }

    private long parseFileSize(String size) {
        if (size.toUpperCase().endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.toUpperCase().endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        }
        return Long.parseLong(size);
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
