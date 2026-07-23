package com.zyd.springbootserviceseedproject.im.controller;

import com.zyd.springbootserviceseedproject.common.core.domain.Result;
import com.zyd.springbootserviceseedproject.common.utils.SecurityUtils;
import com.zyd.springbootserviceseedproject.im.domain.ImConversation;
import com.zyd.springbootserviceseedproject.im.domain.ImMessage;
import com.zyd.springbootserviceseedproject.im.domain.vo.ConversationVO;
import com.zyd.springbootserviceseedproject.im.domain.vo.SimpleUserVO;
import com.zyd.springbootserviceseedproject.im.service.IImChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IM 聊天 REST 接口（v2 主流设计）
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
     * 获取/创建会话（唯一会话，min_user_id + max_user_id）
     * 客户端发消息前先调此接口拿 conversationId。
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
     * 会话列表（当前用户视角，含对方昵称/头像、各自未读数）
     */
    @GetMapping("/conversations")
    public Result getConversationList() {
        Long userId = SecurityUtils.getUserId();
        List<ConversationVO> list = imChatService.getConversationList(userId);
        return Result.success(list, (long) list.size());
    }

    /**
     * 历史消息分页（按 conversation_id 查，游标向前翻）
     *
     * @param conversationId 会话ID（通过 /chat/conversation 获取）
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
     * 标记会话已读（清零当前用户的未读数）
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

    /**
     * IM 用户列表（排除自己，用于发起聊天时选择联系人）
     */
    @GetMapping("/users")
    public Result getChatUserList() {
        Long userId = SecurityUtils.getUserId();
        List<SimpleUserVO> list = imChatService.getChatUserList(userId);
        return Result.success(list, (long) list.size());
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
