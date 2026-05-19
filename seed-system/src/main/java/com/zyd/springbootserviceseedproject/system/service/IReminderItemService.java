package com.zyd.springbootserviceseedproject.system.service;

import java.util.Date;
import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.ReminderItem;

/**
 * 提醒事项 服务层
 *
 * @author zyd
 */
public interface IReminderItemService
{
    /**
     * 查询提醒事项
     */
    public ReminderItem selectReminderItemById(Long reminderId);

    /**
     * 查询提醒事项列表
     */
    public List<ReminderItem> selectReminderItemList(ReminderItem reminderItem);

    /**
     * 新增提醒事项
     */
    public int insertReminderItem(ReminderItem reminderItem);

    /**
     * 修改提醒事项
     */
    public int updateReminderItem(ReminderItem reminderItem);

    /**
     * 删除提醒事项（逻辑删除）
     */
    public int deleteReminderItemById(Long reminderId);

    /**
     * 批量删除提醒事项（逻辑删除）
     */
    public int deleteReminderItemByIds(Long[] reminderIds);

    /**
     * 续期提醒事项
     */
    public int renewReminderItem(Long reminderId, Date newDueDate);

    /**
     * 标记为已完成
     */
    public int completeReminderItem(Long reminderId);

    /**
     * 关闭提醒
     */
    public int closeReminderItem(Long reminderId);

    /**
     * 查询需要提醒的事项（定时任务调用）
     */
    public List<ReminderItem> selectPendingReminders();

    /**
     * 更新提醒发送后的状态（定时任务调用）
     */
    public int updateAfterRemindSent(ReminderItem item);
}
