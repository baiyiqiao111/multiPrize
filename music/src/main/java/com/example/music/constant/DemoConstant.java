package com.example.music.constant;

public class DemoConstant {
    public static final String EMAIL_CONTENT="<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\" />\n" +
            "  <title>用户激活邮件</title>\n" +
            "</head>\n" +
            "<body style=\"margin:0; padding:0; background-color:#f5f6f8; font-family:Arial, sans-serif;\">\n" +
            "  <div style=\"max-width:600px; margin:40px auto; background:#ffffff; padding:32px; border-radius:8px;\">\n" +
            "    \n" +
            "    <h2 style=\"margin-top:0; color:#333333;\">账号激活验证</h2>\n" +
            "\n" +
            "    <p style=\"font-size:16px; color:#555555;\">\n" +
            "      您好，感谢您注册我们的账号。\n" +
            "    </p>\n" +
            "\n" +
            "    <p style=\"font-size:16px; color:#555555;\">\n" +
            "      您的激活码是：\n" +
            "    </p>\n" +
            "\n" +
            "    <!-- 这里修改成你自己的六位数字激活码 -->\n" +
            "    <div style=\"font-size:32px; font-weight:bold; letter-spacing:8px; color:#2f80ed; margin:24px 0;\">\n" +
            "      123456\n" +
            "    </div>\n" +
            "\n" +
            "    <p style=\"font-size:14px; color:#777777;\">\n" +
            "      请在页面中输入该激活码完成账号激活。\n" +
            "    </p>\n" +
            "\n" +
            "    <p style=\"font-size:14px; color:#999999;\">\n" +
            "      如果这不是您本人操作，请忽略此邮件。\n" +
            "    </p>\n" +
            "\n" +
            "    <hr style=\"border:none; border-top:1px solid #eeeeee; margin:24px 0;\" />\n" +
            "\n" +
            "    <p style=\"font-size:12px; color:#aaaaaa;\">\n" +
            "      本邮件由系统自动发送，请勿直接回复。\n" +
            "    </p>\n" +
            "\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>";
    public static final double COMMENT_SCORE=2.0;
    public static final double LIKE_SCORE=1.0;

}
