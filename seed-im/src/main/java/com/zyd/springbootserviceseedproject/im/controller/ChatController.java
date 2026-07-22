package com.zyd.springbootserviceseedproject.im.controller;

import com.zyd.springbootserviceseedproject.common.core.domain.Result;
import com.zyd.springbootserviceseedproject.common.utils.SecurityUtils;
import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
import com.zyd.springbootserviceseedproject.im.service.IImChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IM 聊天 REST 接口
 *
 * 走标准 JWT 鉴权（JwtAuthenticationTokenFilter），通过 [SecurityUtils] 取当前登录用户。
 * 实时消息推送走 WebSocket /ws，这里只提供会话/历史/未读等 HTTP 接口。
 * 统一返回 [Result]（code/msg/total/data），与移动端 BaseResponse 对齐。
 *
 * @author zhaoyudong
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private IImChatService imChatService;

    /**
     * 获取/创建会话
     *
     * @param body {"targetId": 2}
     */
    @PostMapping("/conversation")
    public Result getOrCreateConversation(@RequestBody Map<String, Object> body) {
        Long userId = SecurityUtils.getUserId();
        Long targetId = asLong(body.get("targetId"));
        if (targetId == null) {
            return Result.fail(400, "targetId 不能为空");
        }
        ImConversation conversation = imChatService.getOrCreateConversation(userId, targetId);
        return Result.success(conversation);
    }

    /**
     * 会话列表（含对方昵称/头像）
     */
    @GetMapping("/conversations")
    public Result getConversationList() {
        Long userId = SecurityUtils.getUserId();
        List<ConversationVO> list = imChatService.getConversationList(userId);
        return Result.success(list, (long) list.size());
    }

    /**
     * 历史消息分页（基于 msgId 游标向前翻）
     *
     * @param conversationId 会话ID
     * @param lastMsgId      游标（上一页最后一条 msgId），不传则查最新一页
     * @param size           每页条数，默认 20
     */
    @GetMapping("/history")
    public Result getHistory(@RequestParam("conversationId") Long conversationId,
                             @RequestParam(value = "lastMsgId", required = false) Long lastMsgId,
                             @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        List<ImMessage> list = imChatService.getHistoryMessages(conversationId, lastMsgId, size);
        return Result.success(list, (long) list.size());
    }

    /**
     * 标记会话已读
     *
     * @param body {"conversationId": 1}
     */
    @PostMapping("/read")
    public Result markRead(@RequestBody Map<String, Object> body) {
        Long userId = SecurityUtils.getUserId();
        Long conversationId = asLong(body.get("conversationId"));
        if (conversationId == null) {
            return Result.fail(400, "conversationId 不能为空");
        }
        imChatService.markRead(conversationId, userId);
        return Result.success();
    }

    /**
     * 未读消息总数
     */
    @GetMapping("/unread/count")
    public Result getUnreadCount() {
        Long userId = SecurityUtils.getUserId();
        int total = imChatService.getUnreadTotal(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("count", total);
        return Result.success(data);
    }

    // ---- 工具 ----

    private Long asLong(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return Long.parseLong(val.toString());
    }
}
