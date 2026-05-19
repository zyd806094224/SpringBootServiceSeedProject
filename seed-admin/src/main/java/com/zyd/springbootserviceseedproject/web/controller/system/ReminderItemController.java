package com.zyd.springbootserviceseedproject.web.controller.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.zyd.springbootserviceseedproject.system.domain.ReminderItem;
import com.zyd.springbootserviceseedproject.system.service.IReminderItemService;

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
}
