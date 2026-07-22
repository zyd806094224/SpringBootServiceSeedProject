package com.zyd.springbootserviceseedproject.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zyd.springbootserviceseedproject.common.core.domain.BaseEntity;

/**
 * 备忘录分类表 memo_category
 *
 * @author zyd
 */
public class MemoCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long categoryId;

    /** 所属用户ID */
    private Long userId;

    /** 分类名称 */
    private String categoryName;

    /** 分类图标 */
    private String categoryIcon;

    /** 显示排序 */
    private Integer orderNum;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 备忘录数量（非持久化，列表查询时统计） */
    private Integer memoCount;

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    @NotBlank(message = "分类名称不能为空")
    @Size(min = 0, max = 100, message = "分类名称长度不能超过100个字符")
    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon()
    {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon)
    {
        this.categoryIcon = categoryIcon;
    }

    @NotNull(message = "显示排序不能为空")
    public Integer getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum)
    {
        this.orderNum = orderNum;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getMemoCount()
    {
        return memoCount;
    }

    public void setMemoCount(Integer memoCount)
    {
        this.memoCount = memoCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("userId", getUserId())
            .append("categoryName", getCategoryName())
            .append("categoryIcon", getCategoryIcon())
            .append("orderNum", getOrderNum())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("memoCount", getMemoCount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
