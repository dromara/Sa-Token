package com.pj.test;

import cn.dev33.satoken.util.SaResult;
import com.pj.util.SseEmitterHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SSE 推送
 */
@RestController
public class SseAdminController {

    // 推送消息   --- http://localhost:8081/sse/send?uid=10001&message=hello123
    @RequestMapping(value = "/sse/send")
    public SaResult sendMessage(long uid, String message) {
        SseEmitterHolder.sendMessageByUid(uid, message);
        return SaResult.ok();
    }

    // 断开   --- http://localhost:8081/sse/close?uid=10001
    @RequestMapping(value = "/sse/close")
    public SaResult close(long uid){
        SseEmitterHolder.closeByUid(uid);
        return SaResult.ok();
    }

}

