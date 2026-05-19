package com.zyd.springbootserviceseedproject.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zyd.springbootserviceseedproject.common.core.domain.BaseEntity;

/**
 * 提醒事项表 reminder_item
 *
 * @author zyd
 */
public class ReminderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提醒事项ID */
    private Long reminderId;

    /** 所属用户ID */
    private Long userId;

    /** 事项标题 */
    private String title;

    /** 事项描述 */
    private String description;

    /** 分类(work/life/finance/health/subscription/license/other) */
    private String category;

    /** 到期日期 */
    private Date dueDate;

    /** 提前几天开始提醒(默认7天) */
    private Integer remindBeforeDays;

    /** 每天提醒时间 */
    private Date remindTime;

    /** 到期后提醒频率(天,默认每天) */
    private Integer overdueFrequency;

    /** 状态(0=待提醒 1=提醒中 2=已到期 3=已完成 4=已关闭) */
    private String status;

    /** 收件人邮箱(为空则用系统配置) */
    private String recipientEmail;

    /** 续期次数 */
    private Integer renewalCount;

    /** 最后续期时间 */
    private Date lastRenewalDate;

    /** 原始到期日期 */
    private Date originalDueDate;

    /** 最后提醒发送时间 */
    private Date lastRemindTime;

    /** 下次提醒时间 */
    private Date nextRemindTime;

    /** 删除标志(0=存在 2=删除) */
    private String delFlag;

    public Long getReminderId()
    {
        return reminderId;
    }

    public void setReminderId(Long reminderId)
    {
        this.reminderId = reminderId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    @NotBlank(message = "事项标题不能为空")
    @Size(min = 0, max = 200, message = "事项标题长度不能超过200个字符")
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Size(min = 0, max = 1000, message = "事项描述长度不能超过1000个字符")
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @NotNull(message = "到期日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Integer getRemindBeforeDays()
    {
        return remindBeforeDays;
    }

    public void setRemindBeforeDays(Integer remindBeforeDays)
    {
        this.remindBeforeDays = remindBeforeDays;
    }

    @JsonFormat(pattern = "HH:mm")
    public Date getRemindTime()
    {
        return remindTime;
    }

    public void setRemindTime(Date remindTime)
    {
        this.remindTime = remindTime;
    }

    public Integer getOverdueFrequency()
    {
        return overdueFrequency;
    }

    public void setOverdueFrequency(Integer overdueFrequency)
    {
        this.overdueFrequency = overdueFrequency;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRecipientEmail()
    {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail)
    {
        this.recipientEmail = recipientEmail;
    }

    public Integer getRenewalCount()
    {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount)
    {
        this.renewalCount = renewalCount;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLastRenewalDate()
    {
        return lastRenewalDate;
    }

    public void setLastRenewalDate(Date lastRenewalDate)
    {
        this.lastRenewalDate = lastRenewalDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getOriginalDueDate()
    {
        return originalDueDate;
    }

    public void setOriginalDueDate(Date originalDueDate)
    {
        this.originalDueDate = originalDueDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLastRemindTime()
    {
        return lastRemindTime;
    }

    public void setLastRemindTime(Date lastRemindTime)
    {
        this.lastRemindTime = lastRemindTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextRemindTime()
    {
        return nextRemindTime;
    }

    public void setNextRemindTime(Date nextRemindTime)
    {
        this.nextRemindTime = nextRemindTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reminderId", getReminderId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("category", getCategory())
            .append("dueDate", getDueDate())
            .append("remindBeforeDays", getRemindBeforeDays())
            .append("remindTime", getRemindTime())
            .append("overdueFrequency", getOverdueFrequency())
            .append("status", getStatus())
            .append("recipientEmail", getRecipientEmail())
            .append("renewalCount", getRenewalCount())
            .append("lastRenewalDate", getLastRenewalDate())
            .append("originalDueDate", getOriginalDueDate())
            .append("lastRemindTime", getLastRemindTime())
            .append("nextRemindTime", getNextRemindTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
