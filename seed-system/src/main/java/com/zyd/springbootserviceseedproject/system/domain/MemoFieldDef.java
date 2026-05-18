package com.zyd.springbootserviceseedproject.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zyd.springbootserviceseedproject.common.core.domain.BaseEntity;

/**
 * 备忘录字段定义表 memo_field_def
 *
 * @author zyd
 */
public class MemoFieldDef extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 字段定义ID */
    private Long fieldId;

    /** 所属分类ID */
    private Long categoryId;

    /** 字段名称 */
    private String fieldName;

    /** 字段编码 */
    private String fieldCode;

    /** 字段类型（text/textarea/number/date/select/radio） */
    private String fieldType;

    /** 选项值（select/radio的JSON数组） */
    private String fieldOptions;

    /** 默认值 */
    private String defaultValue;

    /** 输入提示 */
    private String placeholder;

    /** 是否必填（0否 1是） */
    private String isRequired;

    /** 排序号 */
    private Integer sortOrder;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public Long getFieldId()
    {
        return fieldId;
    }

    public void setFieldId(Long fieldId)
    {
        this.fieldId = fieldId;
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

    @NotBlank(message = "字段名称不能为空")
    @Size(min = 0, max = 100, message = "字段名称长度不能超过100个字符")
    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldCode()
    {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode)
    {
        this.fieldCode = fieldCode;
    }

    @NotBlank(message = "字段类型不能为空")
    public String getFieldType()
    {
        return fieldType;
    }

    public void setFieldType(String fieldType)
    {
        this.fieldType = fieldType;
    }

    public String getFieldOptions()
    {
        return fieldOptions;
    }

    public void setFieldOptions(String fieldOptions)
    {
        this.fieldOptions = fieldOptions;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public String getPlaceholder()
    {
        return placeholder;
    }

    public void setPlaceholder(String placeholder)
    {
        this.placeholder = placeholder;
    }

    public String getIsRequired()
    {
        return isRequired;
    }

    public void setIsRequired(String isRequired)
    {
        this.isRequired = isRequired;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
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
            .append("fieldId", getFieldId())
            .append("categoryId", getCategoryId())
            .append("fieldName", getFieldName())
            .append("fieldCode", getFieldCode())
            .append("fieldType", getFieldType())
            .append("fieldOptions", getFieldOptions())
            .append("defaultValue", getDefaultValue())
            .append("placeholder", getPlaceholder())
            .append("isRequired", getIsRequired())
            .append("sortOrder", getSortOrder())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
