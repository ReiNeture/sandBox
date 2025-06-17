package vteam.common.tools.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class AESGCMGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AESGCMGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("AES-GCM 加解密工具");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setResizable(false); // 🔒 禁止調整視窗大小
        frame.setLayout(new GridBagLayout());
        
        // 移除最大化按鈕，但保留標題列
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setExtendedState(JFrame.NORMAL); // 確保視窗不會啟動時最大化
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 新增提示標籤
        JLabel warningLabel = new JLabel("⚠ 每次加密結果都會不同，產生後請立即複製加密結果，以免遺失！");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel encryptLabel = new JLabel("加密:");
        JTextField encryptField = new JTextField(20);
        JLabel encryptResultLabel = new JLabel("加密結果:");
        JTextField encryptResultField = new JTextField(20);
//        encryptResultField.setEditable(false);

        JLabel decryptLabel = new JLabel("解密:");
        JTextField decryptField = new JTextField(20);
        JLabel decryptResultLabel = new JLabel("解密結果:");
        JTextField decryptResultField = new JTextField(20);
//        decryptResultField.setEditable(false);

        JButton executeButton = new JButton("執行");
        executeButton.setPreferredSize(new Dimension(100, 30));

        
        JLabel keyLabel = new JLabel("<html><u>查看金鑰</u></html>");
        keyLabel.setForeground(Color.BLUE);
        keyLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        keyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); // 設定較小字體
        
        keyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String keyBase64 = AESGCMTools.getKeyBase64();
                    JOptionPane.showMessageDialog(frame, "目前加解密 Key:\n" + keyBase64, "加解密 Key", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "取得 Key 時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String plainText = encryptField.getText();
                    if (!plainText.isEmpty()) {
                        String encryptedText = AESGCMTools.encrypt(plainText);
                        encryptResultField.setText(encryptedText);
                    }
                    String encryptedInput = decryptField.getText();
                    if (!encryptedInput.isEmpty()) {
                        String decryptedText = AESGCMTools.decrypt(encryptedInput);
                        decryptResultField.setText(decryptedText);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 設定 GridBagConstraints 來控制位置和大小
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        frame.add(warningLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        frame.add(encryptLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        frame.add(encryptField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        frame.add(encryptResultLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        frame.add(encryptResultField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        frame.add(decryptLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        frame.add(decryptField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        frame.add(decryptResultLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        frame.add(decryptResultField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(executeButton, gbc);

        gbc.gridy = 6;
        gbc.gridwidth = 1;
        frame.add(keyLabel, gbc);

        frame.setVisible(true);
    }
}
