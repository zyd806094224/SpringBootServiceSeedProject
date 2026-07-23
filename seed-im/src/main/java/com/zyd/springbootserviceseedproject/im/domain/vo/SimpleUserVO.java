package com.zyd.springbootserviceseedproject.im.domain.vo;

import lombok.Data;

/**
 * 用户精简信息 VO（IM 用户列表用）
 *
 * @author zhaoyudong
 */
@Data
public class SimpleUserVO {

    /** 用户ID */
    private Long userId;

    /** 昵称 */
    private String nickName;

    /** 用户名 */
    private String userName;

    /** 头像URL */
    private String avatar;
}
