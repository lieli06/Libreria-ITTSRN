package com.example.itts_rn_libreria;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView otpTextView, countdownText;
    private EditText keyInput;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        otpTextView = findViewById(R.id.otpTextView);
        countdownText = findViewById(R.id.countdownText);
        keyInput = findViewById(R.id.keyInput);
        progressBar = findViewById(R.id.progressBar);
        Button generateOtpButton = findViewById(R.id.generateOtpButton);
        Button copyButton = findViewById(R.id.copyButton);

        // 监听生成 OTP 按钮
        generateOtpButton.setOnClickListener(v -> generateOTP());

        // 监听复制按钮
        copyButton.setOnClickListener(v -> {
            String otp = otpTextView.getText().toString();
            copyToClipboard(otp);
        });

        // 启动倒计时
        startRealTimeCountdown();
    }

    private void generateOTP() {
        String key = keyInput.getText().toString();
        if (key.isEmpty()) {
            otpTextView.setText("Inserisci Password");
            return;
        }

        long currentMinute = System.currentTimeMillis() / 60000;
        String input = key + currentMinute;
        otpTextView.setText(hashToNumericOTP(input));
    }

    private String hashToNumericOTP(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            // 取前 8 个字节，并转换为 8 位数字
            StringBuilder numericOTP = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 8; i++) {
                int num = (hash[i] & 0xFF) % 10; // 限制在 0~9
                numericOTP.append(num);
            }
            return numericOTP.toString();
        } catch (NoSuchAlgorithmException e) {
            return "ERROR";
        }
    }

    private void startRealTimeCountdown() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // 获取当前秒数
                Calendar calendar = Calendar.getInstance();
                int second = calendar.get(Calendar.SECOND);
                int remainingTime = 60 - second;

                countdownText.setText("Tempo Rimasto: " + remainingTime + "s");

                // 更新进度条（数值方式）
                progressBar.setProgress(remainingTime * 100 / 60);

                if (remainingTime == 59) { // 新一分钟，刷新 OTP
                    generateOTP();
                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timerRunnable);
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("OTP", text);
        clipboard.setPrimaryClip(clip);
    }
}
