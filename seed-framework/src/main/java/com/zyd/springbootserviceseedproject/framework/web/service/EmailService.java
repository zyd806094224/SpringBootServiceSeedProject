package com.zyd.springbootserviceseedproject.framework.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

/**
 * 邮件发送服务
 *
 * @author zyd
 */
@Service
public class EmailService
{
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content HTML内容
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String content)
    {
        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        }
        catch (Exception e)
        {
            log.error("邮件发送失败: to={}, subject={}", to, subject, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 同步发送HTML邮件（定时任务中使用）
     */
    public void sendHtmlEmailSync(String to, String subject, String content)
    {
        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        }
        catch (Exception e)
        {
            log.error("邮件发送失败: to={}, subject={}", to, subject, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}
