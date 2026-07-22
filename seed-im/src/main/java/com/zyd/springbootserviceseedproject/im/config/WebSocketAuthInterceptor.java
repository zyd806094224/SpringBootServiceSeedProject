package com.zyd.springbootserviceseedproject.im.config;

import com.zyd.springbootserviceseedproject.common.core.domain.model.LoginUser;
import com.zyd.springbootserviceseedproject.common.utils.StringUtils;
import com.zyd.springbootserviceseedproject.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 握手鉴权拦截器
 *
 * 移动端无法在 WS 握手阶段设置自定义 Header（iOS Darwin 引擎限制），
 * 因此 token 通过 URL query 参数传递：ws://host:port/ws?token=xxx
 *
 * 复用若依标准 [TokenService] 解析 token（与 /login 签发的 token 体系一致），
 * 取出 LoginUser 的 userId 放入 WebSocketSession 的 attributes，后续 Handler 通过 "userId" 取出。
 * 鉴权失败直接拒绝握手（返回 false）。
 *
 * @author zhaoyudong
 */
@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    /** attributes 中存储 userId 的 key */
    public static final String ATTR_USER_ID = "userId";

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String token = extractToken(request.getURI());
            if (StringUtils.isEmpty(token)) {
                log.warn("WS 握手失败：缺少 token 参数");
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
            // TokenService.getToken() 取 header 时会去 "Bearer " 前缀，
            // WS query 传的是裸 token，这里包一层让 TokenService 正确解析。
            LoginUser loginUser = tokenService.parseWebSocketToken(token);
            if (loginUser == null || loginUser.getUser() == null) {
                log.warn("WS 握手失败：token 无效或用户不存在");
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return false;
            }
            Long userId = loginUser.getUser().getUserId();
            attributes.put(ATTR_USER_ID, userId);
            log.info("WS 握手成功：userId={}", userId);
            return true;
        } catch (Exception e) {
            log.warn("WS 握手失败：token 解析异常 {}", e.getMessage());
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后无需处理
    }

    /**
     * 从 URI query 中解析 token 参数
     */
    private String extractToken(URI uri) {
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0 && "token".equals(pair.substring(0, idx))) {
                return pair.substring(idx + 1);
            }
        }
        return null;
    }
}
