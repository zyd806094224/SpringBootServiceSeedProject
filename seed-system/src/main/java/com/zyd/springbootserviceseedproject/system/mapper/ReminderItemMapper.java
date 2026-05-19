package com.zyd.springbootserviceseedproject.system.mapper;

import java.util.List;
import com.zyd.springbootserviceseedproject.system.domain.ReminderItem;

/**
 * 提醒事项 数据层
 *
 * @author zyd
 */
public interface ReminderItemMapper
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
     * 查询需要提醒的事项（定时任务调用）
     */
    public List<ReminderItem> selectPendingReminders();

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
}
