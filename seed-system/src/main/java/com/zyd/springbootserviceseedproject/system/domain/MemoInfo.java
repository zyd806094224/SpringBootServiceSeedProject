package com.zyd.springbootserviceseedproject.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zyd.springbootserviceseedproject.common.core.domain.BaseEntity;

/**
 * 备忘录信息表 memo_info
 *
 * @author zyd
 */
public class MemoInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 备忘录ID */
    private Long memoId;

    /** 所属用户ID */
    private Long userId;

    /** 所属分类ID */
    private Long categoryId;

    /** 备忘录名称 */
    private String memoName;

    /** 简要描述 */
    private String memoDesc;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 分类名称（联查用） */
    private String categoryName;

    public Long getMemoId()
    {
        return memoId;
    }

    public void setMemoId(Long memoId)
    {
        this.memoId = memoId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    @NotNull(message = "所属分类不能为空")
    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    @NotBlank(message = "备忘录名称不能为空")
    @Size(min = 0, max = 200, message = "备忘录名称长度不能超过200个字符")
    public String getMemoName()
    {
        return memoName;
    }

    public void setMemoName(String memoName)
    {
        this.memoName = memoName;
    }

    public String getMemoDesc()
    {
        return memoDesc;
    }

    public void setMemoDesc(String memoDesc)
    {
        this.memoDesc = memoDesc;
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

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("memoId", getMemoId())
            .append("userId", getUserId())
            .append("categoryId", getCategoryId())
            .append("memoName", getMemoName())
            .append("memoDesc", getMemoDesc())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("categoryName", getCategoryName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
