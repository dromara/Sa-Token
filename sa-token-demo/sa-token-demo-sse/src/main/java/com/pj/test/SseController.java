package com.pj.test;

import com.pj.util.SseEmitterHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 连接
 */
@RestController
public class SseController {


    // 创建连接   --- http://localhost:8081/sse?satoken=d8a8e1c7-62a4-4656-8b54-cc14e6348ceb
    @RequestMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createSse(String satoken) {
        return SseEmitterHolder.createSse(satoken);
    }

}

