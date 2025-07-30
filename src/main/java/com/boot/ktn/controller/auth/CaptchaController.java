package com.boot.ktn.controller.auth;

import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("${api.base.path}/auth")
public class CaptchaController {

    // Generate CAPTCHA image
    @GetMapping(value = "/captcha", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateCaptcha(HttpSession session) throws Exception {
        // Generate random CAPTCHA text using commons-lang3
        String captchaText = RandomStringUtils.random(6, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        session.setAttribute("captchaText", captchaText);

        // Create CAPTCHA image
        int width = 250;
        int height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw random lines for noise
        Random random = new Random();
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Draw CAPTCHA text with adjusted spacing
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 42)); // Slightly larger font size
        int charWidth = 30; // Approximate width per character
        int spacing = 5; // Additional spacing between characters
        int totalTextWidth = 6 * charWidth + 5 * spacing; // 6 characters + 5 gaps
        int startX = (width - totalTextWidth) / 2; // Center horizontally
        int startY = (height + 42) / 2 - 5; // Center vertically, slight upward adjustment

        // Draw each character individually with spacing
        for (int i = 0; i < captchaText.length(); i++) {
            g2d.drawString(String.valueOf(captchaText.charAt(i)), startX + i * (charWidth + spacing), startY);
        }

        g2d.dispose();

        // Convert image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    // Verify CAPTCHA input
    @SuppressWarnings("unchecked") // Safe cast for session.getAttribute
    @PostMapping("/captcha")
    public ResponseEntity<Map<String, Object>> verifyCaptcha(@RequestBody Map<String, String> request, HttpSession session) {
        String userInput = request.get("captchaInput");
        String captchaText = (String) session.getAttribute("captchaText");

        // Define response structure
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();

        if (userInput != null && userInput.equalsIgnoreCase(captchaText)) {
            responseData.put("success", true);
            response.put("success", true);
            response.put("data", responseData);
            response.put("errCd", "00");
            response.put("errMsg", "");
        } else {
            responseData.put("success", false);
            responseData.put("errMsg", "캡챠가 일치하지 않습니다.");
            response.put("success", true); // Backend convention
            response.put("data", null);
            response.put("errCd", "01");
            response.put("errMsg", "캡챠 검증 실패");
        }

        return ResponseEntity.ok(response);
    }
}