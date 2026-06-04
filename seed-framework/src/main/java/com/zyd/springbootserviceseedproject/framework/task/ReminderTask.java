package com.zyd.springbootserviceseedproject.framework.task;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.zyd.springbootserviceseedproject.system.domain.ReminderItem;
import com.zyd.springbootserviceseedproject.system.service.IReminderItemService;
import com.zyd.springbootserviceseedproject.system.service.impl.ReminderItemServiceImpl;
import com.zyd.springbootserviceseedproject.framework.web.service.EmailService;

/**
 * 提醒事项定时任务
 *
 * @author zyd
 */
@Component
public class ReminderTask
{
    private static final Logger log = LoggerFactory.getLogger(ReminderTask.class);

    @Autowired
    private IReminderItemService reminderItemService;

    @Autowired
    private ReminderItemServiceImpl reminderItemServiceImpl;

    @Autowired
    private EmailService emailService;

    @Value("${reminder.enabled:true}")
    private boolean reminderEnabled;

    /**
     * 每分钟检查提醒
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void checkAndSendReminders()
    {
        if (!reminderEnabled)
        {
            return;
        }
        try
        {
            List<ReminderItem> pendingList = reminderItemService.selectPendingReminders();
            if (pendingList.isEmpty())
            {
                return;
            }
            log.info("本次检查发现{}条待提醒事项", pendingList.size());
            for (ReminderItem item : pendingList)
            {
                try
                {
                    // 获取收件人邮箱
                    String recipient = reminderItemServiceImpl.getRecipientEmail(item);
                    if (recipient == null || recipient.isEmpty())
                    {
                        log.warn("提醒事项无收件邮箱, reminderId={}", item.getReminderId());
                        // 后移下次提醒时间，避免每分钟重复刷日志
                        reminderItemServiceImpl.updateAfterRemindFailed(item);
                        continue;
                    }
                    // 构建并发送邮件
                    String subject = buildSubject(item);
                    String content = buildHtmlContent(item);
                    emailService.sendHtmlEmailSync(recipient, subject, content);
                    // 发送成功，更新状态
                    reminderItemServiceImpl.updateAfterRemindSent(item);
                    log.info("提醒邮件已发送: reminderId={}, title={}, to={}", item.getReminderId(), item.getTitle(), recipient);
                }
                catch (Exception e)
                {
                    log.error("提醒事项处理失败, reminderId={}", item.getReminderId(), e);
                    // 发送失败，后移30分钟重试
                    try
                    {
                        reminderItemServiceImpl.updateAfterRemindFailed(item);
                    }
                    catch (Exception ex)
                    {
                        log.error("更新失败重试时间异常, reminderId={}", item.getReminderId(), ex);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("提醒定时任务执行异常", e);
        }
    }

    /**
     * 构建邮件主题
     */
    private String buildSubject(ReminderItem item)
    {
        long days = calculateDaysRemaining(item.getDueDate());
        String categoryLabel = getCategoryLabel(item.getCategory());
        if (days > 0)
        {
            return String.format("【提醒】%s（%s）- 还有%d天到期", item.getTitle(), categoryLabel, days);
        }
        else if (days == 0)
        {
            return String.format("【提醒】%s（%s）- 今天到期！", item.getTitle(), categoryLabel);
        }
        else
        {
            return String.format("【提醒】%s（%s）- 已过期%d天", item.getTitle(), categoryLabel, Math.abs(days));
        }
    }

    /**
     * 构建HTML邮件内容
     */
    private String buildHtmlContent(ReminderItem item)
    {
        long days = calculateDaysRemaining(item.getDueDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = sdf.format(item.getDueDate());

        String statusText;
        String statusColor;
        if (days > 0)
        {
            statusText = "还有" + days + "天到期";
            statusColor = "#E6A23C";
        }
        else if (days == 0)
        {
            statusText = "今天到期！";
            statusColor = "#F56C6C";
        }
        else
        {
            statusText = "已过期" + Math.abs(days) + "天";
            statusColor = "#F56C6C";
        }

        StringBuilder html = new StringBuilder();
        html.append("<div style=\"max-width:600px;margin:0 auto;font-family:'Microsoft YaHei',sans-serif;\">");
        html.append("<div style=\"background:#409EFF;color:#fff;padding:20px;border-radius:8px 8px 0 0;\">");
        html.append("<h2 style=\"margin:0;\">提醒事项通知</h2>");
        html.append("</div>");
        html.append("<div style=\"border:1px solid #EBEEF5;border-top:none;padding:20px;border-radius:0 0 8px 8px;\">");
        html.append("<table style=\"width:100%;border-collapse:collapse;\">");
        html.append("<tr><td style=\"padding:10px 0;color:#909399;width:100px;\">事项标题</td>");
        html.append("<td style=\"padding:10px 0;font-weight:bold;\">").append(escapeHtml(item.getTitle())).append("</td></tr>");
        if (item.getDescription() != null && !item.getDescription().isEmpty())
        {
            html.append("<tr><td style=\"padding:10px 0;color:#909399;\">事项描述</td>");
            html.append("<td style=\"padding:10px 0;\">").append(escapeHtml(item.getDescription())).append("</td></tr>");
        }
        html.append("<tr><td style=\"padding:10px 0;color:#909399;\">到期日期</td>");
        html.append("<td style=\"padding:10px 0;\">").append(dueDateStr).append("</td></tr>");
        html.append("<tr><td style=\"padding:10px 0;color:#909399;\">当前状态</td>");
        html.append("<td style=\"padding:10px 0;color:").append(statusColor).append(";font-weight:bold;\">").append(statusText).append("</td></tr>");
        html.append("<tr><td style=\"padding:10px 0;color:#909399;\">分类</td>");
        html.append("<td style=\"padding:10px 0;\">").append(getCategoryLabel(item.getCategory())).append("</td></tr>");
        if (item.getRenewalCount() != null && item.getRenewalCount() > 0)
        {
            html.append("<tr><td style=\"padding:10px 0;color:#909399;\">已续期</td>");
            html.append("<td style=\"padding:10px 0;\">").append(item.getRenewalCount()).append("次</td></tr>");
        }
        html.append("</table>");
        if (days <= 0)
        {
            html.append("<div style=\"margin-top:20px;padding:15px;background:#FEF0F0;border-radius:4px;color:#F56C6C;\">");
            html.append("该事项已").append(days == 0 ? "到期" : "过期").append("，请及时处理或续期！");
            html.append("</div>");
        }
        html.append("</div>");
        html.append("<div style=\"text-align:center;color:#C0C4CC;font-size:12px;margin-top:10px;\">");
        html.append("此邮件由系统自动发送，请勿回复</div>");
        html.append("</div>");
        return html.toString();
    }

    private String escapeHtml(String text)
    {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private long calculateDaysRemaining(Date dueDate)
    {
        LocalDate today = LocalDate.now();
        LocalDate due = dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(today, due);
    }

    private String getCategoryLabel(String category)
    {
        if (category == null) return "其他";
        switch (category)
        {
            case "work": return "工作";
            case "life": return "生活";
            case "finance": return "财务";
            case "health": return "健康";
            case "subscription": return "订阅";
            case "license": return "证件";
            default: return "其他";
        }
    }
}
