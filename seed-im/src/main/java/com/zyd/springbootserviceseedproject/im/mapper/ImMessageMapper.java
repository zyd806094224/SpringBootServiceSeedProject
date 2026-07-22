package com.zyd.springbootserviceseedproject.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IM 消息 Mapper
 *
 * @author zhaoyudong
 */
@Mapper
public interface ImMessageMapper extends BaseMapper<ImMessage> {

    /**
     * 历史消息分页（基于 msg_id 游标向前翻页）：
     * - lastMsgId 为空：查最新的一页
     * - lastMsgId 非空：查比该 ID 更早（更旧）的一页
     * 结果按 send_time 倒序（最新在前），客户端拿到后正序展示
     */
    List<ImMessage> selectHistoryPage(@Param("conversationId") Long conversationId,
                                      @Param("lastMsgId") Long lastMsgId,
                                      @Param("size") int size);
}
