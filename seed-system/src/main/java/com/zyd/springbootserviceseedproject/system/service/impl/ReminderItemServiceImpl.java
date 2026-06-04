package com.zyd.springbootserviceseedproject.system.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zyd.springbootserviceseedproject.common.core.domain.entity.SysUser;
import com.zyd.springbootserviceseedproject.system.domain.ReminderItem;
import com.zyd.springbootserviceseedproject.system.mapper.ReminderItemMapper;
import com.zyd.springbootserviceseedproject.system.service.IReminderItemService;
import com.zyd.springbootserviceseedproject.system.service.ISysUserService;

/**
 * 提醒事项 服务层实现
 *
 * @author zyd
 */
@Service
public class ReminderItemServiceImpl implements IReminderItemService
{
    private static final Logger log = LoggerFactory.getLogger(ReminderItemServiceImpl.class);

    /** 状态常量 */
    private static final String STATUS_PENDING = "0";
    private static final String STATUS_REMINDING = "1";
    private static final String STATUS_OVERDUE = "2";
    private static final String STATUS_COMPLETED = "3";
    private static final String STATUS_CLOSED = "4";

    @Autowired
    private ReminderItemMapper reminderItemMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Value("${reminder.default-recipient:}")
    private String defaultRecipient;

    @Override
    public ReminderItem selectReminderItemById(Long reminderId)
    {
        return reminderItemMapper.selectReminderItemById(reminderId);
    }

    @Override
    public List<ReminderItem> selectReminderItemList(ReminderItem reminderItem)
    {
        return reminderItemMapper.selectReminderItemList(reminderItem);
    }

    @Override
    public int insertReminderItem(ReminderItem reminderItem)
    {
        // 设置默认值
        if (reminderItem.getRemindBeforeDays() == null)
        {
            reminderItem.setRemindBeforeDays(7);
        }
        if (reminderItem.getOverdueFrequency() == null)
        {
            reminderItem.setOverdueFrequency(1);
        }
        if (reminderItem.getStatus() == null || reminderItem.getStatus().isEmpty())
        {
            reminderItem.setStatus(STATUS_PENDING);
        }
        if (reminderItem.getRenewalCount() == null)
        {
            reminderItem.setRenewalCount(0);
        }
        // 保存原始到期日期
        reminderItem.setOriginalDueDate(reminderItem.getDueDate());
        // 根据到期日期计算初始状态
        updateStatusAfterRemind(reminderItem);
        // 计算下次提醒时间
        reminderItem.setNextRemindTime(calculateNextRemindTime(reminderItem));
        return reminderItemMapper.insertReminderItem(reminderItem);
    }

    @Override
    public int updateReminderItem(ReminderItem reminderItem)
    {
        // 重新计算下次提醒时间
        ReminderItem existing = reminderItemMapper.selectReminderItemById(reminderItem.getReminderId());
        if (existing != null)
        {
            // 合并未传入的字段
            mergeFields(existing, reminderItem);
            // 仅对非终态（已完成/已关闭）的事项重新计算状态和下次提醒时间
            if (!STATUS_COMPLETED.equals(reminderItem.getStatus())
                    && !STATUS_CLOSED.equals(reminderItem.getStatus()))
            {
                updateStatusAfterRemind(reminderItem);
                reminderItem.setNextRemindTime(calculateNextRemindTime(reminderItem));
            }
        }
        return reminderItemMapper.updateReminderItem(reminderItem);
    }

    @Override
    public int deleteReminderItemById(Long reminderId)
    {
        return reminderItemMapper.deleteReminderItemById(reminderId);
    }

    @Override
    public int deleteReminderItemByIds(Long[] reminderIds)
    {
        return reminderItemMapper.deleteReminderItemByIds(reminderIds);
    }

    @Override
    @Transactional
    public int renewReminderItem(Long reminderId, Date newDueDate)
    {
        ReminderItem item = reminderItemMapper.selectReminderItemById(reminderId);
        if (item == null)
        {
            return 0;
        }
        // 首次续期保存原始到期日期
        if (item.getOriginalDueDate() == null)
        {
            item.setOriginalDueDate(item.getDueDate());
        }
        item.setDueDate(newDueDate);
        item.setRenewalCount(item.getRenewalCount() + 1);
        item.setLastRenewalDate(new Date());
        // 根据新的到期日期计算状态
        updateStatusAfterRemind(item);
        item.setNextRemindTime(calculateNextRemindTime(item));
        return reminderItemMapper.updateReminderItem(item);
    }

    @Override
    public int completeReminderItem(Long reminderId)
    {
        ReminderItem item = reminderItemMapper.selectReminderItemById(reminderId);
        if (item == null)
        {
            return 0;
        }
        item.setStatus(STATUS_COMPLETED);
        item.setNextRemindTime(null);
        return reminderItemMapper.updateReminderItem(item);
    }

    @Override
    public int closeReminderItem(Long reminderId)
    {
        ReminderItem item = reminderItemMapper.selectReminderItemById(reminderId);
        if (item == null)
        {
            return 0;
        }
        item.setStatus(STATUS_CLOSED);
        item.setNextRemindTime(null);
        return reminderItemMapper.updateReminderItem(item);
    }

    @Override
    public List<ReminderItem> selectPendingReminders()
    {
        return reminderItemMapper.selectPendingReminders();
    }

    @Override
    public int updateAfterRemindSent(ReminderItem item)
    {
        // 更新状态
        updateStatusAfterRemind(item);
        // 更新最后提醒时间
        item.setLastRemindTime(new Date());
        // 重新计算下次提醒时间
        item.setNextRemindTime(calculateNextRemindTime(item));
        return reminderItemMapper.updateReminderItem(item);
    }

    /**
     * 提醒发送失败时，将下次提醒时间后移30分钟重试
     */
    public int updateAfterRemindFailed(ReminderItem item)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        item.setNextRemindTime(cal.getTime());
        return reminderItemMapper.updateReminderItem(item);
    }

    /**
     * 根据到期日期更新状态
     */
    private void updateStatusAfterRemind(ReminderItem item)
    {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = toLocalDate(item.getDueDate());
        LocalDate remindStartDate = dueDate.minusDays(item.getRemindBeforeDays());

        if (today.isBefore(remindStartDate))
        {
            item.setStatus(STATUS_PENDING);
        }
        else if (!today.isAfter(dueDate))
        {
            // today <= dueDate，到期当天仍算"提醒中"
            item.setStatus(STATUS_REMINDING);
        }
        else
        {
            item.setStatus(STATUS_OVERDUE);
        }
    }

    /**
     * 计算下次提醒时间
     */
    private Date calculateNextRemindTime(ReminderItem item)
    {
        // 已完成或已关闭不提醒
        if (STATUS_COMPLETED.equals(item.getStatus()) || STATUS_CLOSED.equals(item.getStatus()))
        {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = toLocalDate(item.getDueDate());
        LocalTime remindTime = toLocalTime(item.getRemindTime());

        if (remindTime == null)
        {
            remindTime = LocalTime.of(9, 0);
        }

        int remindBeforeDays = item.getRemindBeforeDays() != null ? item.getRemindBeforeDays() : 7;
        int overdueFrequency = item.getOverdueFrequency() != null ? item.getOverdueFrequency() : 1;

        // 提醒窗口开始日期
        LocalDate remindStartDate = dueDate.minusDays(remindBeforeDays);

        if (today.isBefore(remindStartDate))
        {
            // 尚未进入提醒窗口，下次提醒 = remindStartDate + remindTime
            return toDate(LocalDateTime.of(remindStartDate, remindTime));
        }
        else if (!today.isAfter(dueDate))
        {
            // 在提醒窗口内（today <= dueDate），到期当天仍在此分支
            LocalDateTime todayRemind = LocalDateTime.of(today, remindTime);
            if (LocalDateTime.now().isAfter(todayRemind))
            {
                // 今天的提醒时间已过，明天再提醒
                if (today.isBefore(dueDate))
                {
                    return toDate(LocalDateTime.of(today.plusDays(1), remindTime));
                }
                else
                {
                    // 到期当天已过提醒时间，后续进入过期逻辑
                    return calculateOverdueNextRemindTime(item, today, dueDate, remindTime, overdueFrequency);
                }
            }
            return toDate(todayRemind);
        }
        else
        {
            return calculateOverdueNextRemindTime(item, today, dueDate, remindTime, overdueFrequency);
        }
    }

    /**
     * 计算过期后的下次提醒时间，始终使用配置的remindTime
     */
    private Date calculateOverdueNextRemindTime(ReminderItem item, LocalDate today,
            LocalDate dueDate, LocalTime remindTime, int overdueFrequency)
    {
        if (overdueFrequency <= 0)
        {
            return null;
        }
        // 计算下次提醒日期：基于最后提醒日期 + overdueFrequency天
        LocalDate nextDate;
        if (item.getLastRemindTime() != null)
        {
            LocalDate lastRemindDate = toLocalDateTime(item.getLastRemindTime()).toLocalDate();
            nextDate = lastRemindDate.plusDays(overdueFrequency);
        }
        else
        {
            nextDate = dueDate.plusDays(overdueFrequency);
        }
        // 始终使用配置的remindTime，不使用lastRemindTime的时间
        LocalDateTime nextTime = LocalDateTime.of(nextDate, remindTime);
        // 如果还在过去，从今天开始算
        if (nextTime.isBefore(LocalDateTime.now()))
        {
            nextTime = LocalDateTime.of(today, remindTime);
            if (LocalDateTime.now().isAfter(nextTime))
            {
                nextTime = LocalDateTime.of(today.plusDays(overdueFrequency), remindTime);
            }
        }
        return toDate(nextTime);
    }

    /**
     * 合并更新字段（保留未传入的旧值）
     */
    private void mergeFields(ReminderItem existing, ReminderItem update)
    {
        if (update.getTitle() == null) update.setTitle(existing.getTitle());
        if (update.getDescription() == null) update.setDescription(existing.getDescription());
        if (update.getCategory() == null) update.setCategory(existing.getCategory());
        if (update.getDueDate() == null) update.setDueDate(existing.getDueDate());
        if (update.getRemindBeforeDays() == null) update.setRemindBeforeDays(existing.getRemindBeforeDays());
        if (update.getRemindTime() == null) update.setRemindTime(existing.getRemindTime());
        if (update.getOverdueFrequency() == null) update.setOverdueFrequency(existing.getOverdueFrequency());
        if (update.getStatus() == null) update.setStatus(existing.getStatus());
    }

    /**
     * 获取收件人邮箱
     */
    public String getRecipientEmail(ReminderItem item)
    {
        // 优先使用事项配置的邮箱
        if (item.getRecipientEmail() != null && !item.getRecipientEmail().isEmpty())
        {
            return item.getRecipientEmail();
        }
        // 其次使用用户邮箱
        try
        {
            SysUser user = sysUserService.selectUserById(item.getUserId());
            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty())
            {
                return user.getEmail();
            }
        }
        catch (Exception e)
        {
            log.warn("查询用户邮箱失败, userId={}", item.getUserId(), e);
        }
        // 最后使用系统默认邮箱
        return defaultRecipient;
    }

    /**
     * 计算剩余天数或过期天数
     */
    public static long calculateDaysRemaining(Date dueDate)
    {
        LocalDate today = LocalDate.now();
        LocalDate due = toLocalDateStatic(dueDate);
        return java.time.temporal.ChronoUnit.DAYS.between(today, due);
    }

    private static LocalDate toLocalDateStatic(Date date)
    {
        if (date == null) return LocalDate.now();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDate toLocalDate(Date date)
    {
        return toLocalDateStatic(date);
    }

    private LocalTime toLocalTime(Date date)
    {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    private LocalDateTime toLocalDateTime(Date date)
    {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Date toDate(LocalDateTime localDateTime)
    {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
