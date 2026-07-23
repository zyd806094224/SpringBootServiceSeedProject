package com.zyd.springbootserviceseedproject.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IM 消息 Mapper（v2 主流设计）
 *
 * 历史查询按 conversation_id 单字段过滤（走索引 idx_conv_time），简洁高效。
 *
 * @author zhaoyudong
 */
@Mapper
public interface ImMessageMapper extends BaseMapper<ImMessage> {

    /**
     * 历史消息分页（基于 msg_id 游标向前翻页）：
     * 按 conversation_id 查（唯一会话，不分方向）。
     * - lastMsgId 为空：查最新的一页
     * - lastMsgId 非空：查比该 ID 更早（更旧）的一页
     * 结果按 msg_id 倒序（最新在前），客户端拿到后正序展示
     *
     * @param conversationId 会话ID
     * @param lastMsgId      游标
     * @param size           每页条数
     */
    List<ImMessage> selectHistoryPage(@Param("conversationId") Long conversationId,
                                      @Param("lastMsgId") Long lastMsgId,
                                      @Param("size") int size);
}
