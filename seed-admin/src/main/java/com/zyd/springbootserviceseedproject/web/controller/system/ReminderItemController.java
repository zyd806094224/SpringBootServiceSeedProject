package com.zyd.springbootserviceseedproject.web.controller.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zyd.springbootserviceseedproject.common.core.controller.BaseController;
import com.zyd.springbootserviceseedproject.common.core.domain.AjaxResult;
import com.zyd.springbootserviceseedproject.common.core.page.TableDataInfo;
import com.zyd.springbootserviceseedproject.framework.web.service.EmailService;
import com.zyd.springbootserviceseedproject.system.domain.ReminderItem;
import com.zyd.springbootserviceseedproject.system.service.IReminderItemService;
import com.zyd.springbootserviceseedproject.system.service.impl.ReminderItemServiceImpl;

/**
 * 提醒事项 信息操作处理
 *
 * @author zyd
 */
@RestController
@RequestMapping("/api/reminder")
public class ReminderItemController extends BaseController
{
    @Autowired
    private IReminderItemService reminderItemService;

    @Autowired
    private ReminderItemServiceImpl reminderItemServiceImpl;

    @Autowired
    private EmailService emailService;

    /**
     * 获取提醒事项列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ReminderItem query)
    {
        query.setUserId(getUserId());
        startPage();
        List<ReminderItem> list = reminderItemService.selectReminderItemList(query);
        return getDataTable(list);
    }

    /**
     * 获取提醒事项详情
     */
    @GetMapping(value = "/{reminderId}")
    public AjaxResult getInfo(@PathVariable Long reminderId)
    {
        ReminderItem item = reminderItemService.selectReminderItemById(reminderId);
        if (item == null || !item.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        return success(item);
    }

    /**
     * 新增提醒事项
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ReminderItem item)
    {
        item.setUserId(getUserId());
        item.setCreateBy(getUsername());
        int rows = reminderItemService.insertReminderItem(item);
        if (rows > 0)
        {
            return success(reminderItemService.selectReminderItemById(item.getReminderId()));
        }
        return error("新增提醒事项失败");
    }

    /**
     * 修改提醒事项
     */
    @PutMapping(value = "/{reminderId}")
    public AjaxResult edit(@PathVariable Long reminderId, @Validated @RequestBody ReminderItem item)
    {
        ReminderItem existing = reminderItemService.selectReminderItemById(reminderId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        item.setReminderId(reminderId);
        item.setUpdateBy(getUsername());
        int rows = reminderItemService.updateReminderItem(item);
        if (rows > 0)
        {
            return success(reminderItemService.selectReminderItemById(reminderId));
        }
        return error("修改提醒事项失败");
    }

    /**
     * 删除提醒事项
     */
    @DeleteMapping(value = "/{reminderId}")
    public AjaxResult remove(@PathVariable Long reminderId)
    {
        ReminderItem existing = reminderItemService.selectReminderItemById(reminderId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        return toAjax(reminderItemService.deleteReminderItemById(reminderId));
    }

    /**
     * 续期提醒事项
     */
    @PutMapping(value = "/{reminderId}/renew")
    public AjaxResult renew(@PathVariable Long reminderId, @RequestBody Map<String, String> body)
    {
        ReminderItem existing = reminderItemService.selectReminderItemById(reminderId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        String newDueDateStr = body.get("newDueDate");
        if (newDueDateStr == null || newDueDateStr.isEmpty())
        {
            return error("新的到期日期不能为空");
        }
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDueDate = sdf.parse(newDueDateStr);
            int rows = reminderItemService.renewReminderItem(reminderId, newDueDate);
            if (rows > 0)
            {
                return success(reminderItemService.selectReminderItemById(reminderId));
            }
            return error("续期失败");
        }
        catch (ParseException e)
        {
            return error("日期格式错误");
        }
    }

    /**
     * 标记为已完成
     */
    @PutMapping(value = "/{reminderId}/complete")
    public AjaxResult complete(@PathVariable Long reminderId)
    {
        ReminderItem existing = reminderItemService.selectReminderItemById(reminderId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        return toAjax(reminderItemService.completeReminderItem(reminderId));
    }

    /**
     * 关闭提醒
     */
    @PutMapping(value = "/{reminderId}/close")
    public AjaxResult close(@PathVariable Long reminderId)
    {
        ReminderItem existing = reminderItemService.selectReminderItemById(reminderId);
        if (existing == null || !existing.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        return toAjax(reminderItemService.closeReminderItem(reminderId));
    }

    /**
     * 手动发送提醒邮件
     */
    @PostMapping(value = "/{reminderId}/send")
    public AjaxResult sendReminder(@PathVariable Long reminderId, @RequestBody Map<String, String> body)
    {
        ReminderItem item = reminderItemService.selectReminderItemById(reminderId);
        if (item == null || !item.getUserId().equals(getUserId()))
        {
            return error("提醒事项不存在");
        }
        String recipient = reminderItemServiceImpl.getRecipientEmail(item);
        if (recipient == null || recipient.isEmpty())
        {
            return error("未配置收件邮箱");
        }
        String customContent = body.get("customContent");
        try
        {
            String subject = buildSendSubject(item);
            String content = buildSendHtmlContent(item, customContent);
            emailService.sendHtmlEmailSync(recipient, subject, content);
            return success("提醒邮件已发送至 " + recipient);
        }
        catch (Exception e)
        {
            return error("邮件发送失败: " + e.getMessage());
        }
    }

    private String buildSendSubject(ReminderItem item)
    {
        long days = calculateDaysRemaining(item.getDueDate());
        if (days > 0)
        {
            return String.format("【提醒】%s - 还有%d天到期", item.getTitle(), days);
        }
        else if (days == 0)
        {
            return String.format("【提醒】%s - 今天到期！", item.getTitle());
        }
        else
        {
            return String.format("【提醒】%s - 已过期%d天", item.getTitle(), Math.abs(days));
        }
    }

    private String buildSendHtmlContent(ReminderItem item, String customContent)
    {
        long days = calculateDaysRemaining(item.getDueDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = sdf.format(item.getDueDate());

        String statusText;
        String statusColor;
        if (days > 0) { statusText = "还有" + days + "天到期"; statusColor = "#E6A23C"; }
        else if (days == 0) { statusText = "今天到期！"; statusColor = "#F56C6C"; }
        else { statusText = "已过期" + Math.abs(days) + "天"; statusColor = "#F56C6C"; }

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
        html.append("</table>");
        if (customContent != null && !customContent.isEmpty())
        {
            html.append("<div style=\"margin-top:15px;padding:15px;background:#F4F4F5;border-radius:4px;\">");
            html.append("<div style=\"color:#909399;margin-bottom:8px;\">自定义提醒内容：</div>");
            html.append("<div>").append(escapeHtml(customContent)).append("</div>");
            html.append("</div>");
        }
        html.append("</div>");
        html.append("<div style=\"text-align:center;color:#C0C4CC;font-size:12px;margin-top:10px;\">");
        html.append("此邮件由系统自动发送，请勿回复</div>");
        html.append("</div>");
        return html.toString();
    }

    private long calculateDaysRemaining(Date dueDate)
    {
        LocalDate today = LocalDate.now();
        LocalDate due = dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(today, due);
    }

    private String escapeHtml(String text)
    {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
