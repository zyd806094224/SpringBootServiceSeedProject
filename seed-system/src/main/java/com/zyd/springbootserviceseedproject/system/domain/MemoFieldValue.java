package com.zyd.springbootserviceseedproject.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zyd.springbootserviceseedproject.common.core.domain.BaseEntity;

/**
 * 备忘录字段值表 memo_field_value
 *
 * @author zyd
 */
public class MemoFieldValue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 值ID */
    private Long valueId;

    /** 备忘录ID */
    private Long memoId;

    /** 字段定义ID */
    private Long fieldId;

    /** 字段值 */
    private String fieldValue;

    public Long getValueId()
    {
        return valueId;
    }

    public void setValueId(Long valueId)
    {
        this.valueId = valueId;
    }

    public Long getMemoId()
    {
        return memoId;
    }

    public void setMemoId(Long memoId)
    {
        this.memoId = memoId;
    }

    public Long getFieldId()
    {
        return fieldId;
    }

    public void setFieldId(Long fieldId)
    {
        this.fieldId = fieldId;
    }

    public String getFieldValue()
    {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue)
    {
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("valueId", getValueId())
            .append("memoId", getMemoId())
            .append("fieldId", getFieldId())
            .append("fieldValue", getFieldValue())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
