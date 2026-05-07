package com.zyd.springbootserviceseedproject.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zyd.springbootserviceseedproject.common.core.domain.BaseEntity;

/**
 * 密码管理账号表 pm_account
 *
 * @author zyd
 */
public class PmAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账号ID */
    private Long accountId;

    /** 所属用户ID */
    private Long userId;

    /** 账号标题 */
    private String title;

    /** 分类(social/work/finance/other) */
    private String category;

    /** 用户名/邮箱/手机号 */
    private String username;

    /** 密码(AES加密存储) */
    private String password;

    /** 网站URL */
    private String url;

    /** 删除标志(0=存在 2=删除) */
    private String delFlag;

    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    @NotBlank(message = "账号标题不能为空")
    @Size(min = 0, max = 100, message = "账号标题长度不能超过100个字符")
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    @NotBlank(message = "用户名不能为空")
    @Size(min = 0, max = 200, message = "用户名长度不能超过200个字符")
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @NotBlank(message = "密码不能为空")
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Size(min = 0, max = 500, message = "网站URL长度不能超过500个字符")
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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
            .append("accountId", getAccountId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("category", getCategory())
            .append("username", getUsername())
            .append("password", "******")
            .append("url", getUrl())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
