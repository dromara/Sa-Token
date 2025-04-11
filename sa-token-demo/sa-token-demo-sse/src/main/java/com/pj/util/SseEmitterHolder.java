package com.pj.util;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 连接管理器
 *
 * @author click33
 * @since 2025/4/11
 */
public class SseEmitterHolder {

    public static final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建客户端
     */
    public static SseEmitter createSse(String satoken) {

        Object loginId = StpUtil.getLoginIdByToken(satoken);
        if(loginId == null) {
            throw new NotLoginException("无效 token", StpUtil.TYPE, NotLoginException.INVALID_TOKEN);
        }
        long uid = SaFoxUtil.getValueByType(loginId, Long.class);

        // 默认 30 秒超时，设置为 0L 则永不超时
        SseEmitter sseEmitter = new SseEmitter(600 * 1000L);
        sseEmitterMap.put(satoken, sseEmitter);
        System.out.println("连接成功：satoken=" + satoken + "，uid=" + uid);

        // 完成后回调
        sseEmitter.onCompletion(() -> {
            System.out.println("结束连接：satoken=" + satoken + "，uid=" + uid);
            sseEmitterMap.remove(satoken);
        });

        //超时回调
        sseEmitter.onTimeout(() -> {
            System.out.println("连接超时：satoken=" + satoken + "，uid=" + uid);
        });

        //异常回调
        sseEmitter.onError( e -> {
//            try {
                System.out.println("连接异常：satoken=" + satoken + "，uid=" + uid);
                System.err.println(e.getMessage());
//                sseEmitter.send(SseEmitter.event()
//                        .id(String.valueOf(uid))
//                        .name("发生异常！")
//                        .data("发生异常请重试！")
//                        .reconnectTime(3000));
//                sseEmitterMap.put(uid, sseEmitter);
//            } catch (IOException ee) {
//                ee.printStackTrace();
//            }
        });
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sseEmitter;
    }

    /**
     * 给指定 token 客户端发送消息
     *
     */
    public static void sendMessageByToken(String satoken, String message) {
        SseEmitter sseEmitter = sseEmitterMap.get(satoken);
        if (sseEmitter == null) {
            System.out.println("该 token 暂未建立连接：" + satoken);
            return;
        }
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(60 * 1000L).data(message));
            System.out.println("消息推送成功，token=" + satoken + ", message=" + message);
        }catch (Exception e) {
            e.printStackTrace();
//            sseEmitterMap.remove(uid);
//            log.info("用户{},消息id:{},推送异常:{}", uid,messageId, e.getMessage());
//            sseEmitter.complete();
        }
    }

    /**
     * 给指定 用户 所有客户端发送消息
     *
     */
    public static void sendMessageByUid(long uid, String message) {
        List<String> tokenList = StpUtil.getTokenValueListByLoginId(uid);
        for (String token : tokenList) {
            sendMessageByToken(token, message);
        }
    }

    /**
     * 指定 token 断开连接
     *
     */
    public static void closeByToken(String satoken) {
        SseEmitter sseEmitter = sseEmitterMap.get(satoken);
        if (sseEmitter == null) {
            System.out.println("该 token 暂未建立连接：" + satoken);
            return;
        }
        try {
            sendMessageByToken(satoken, "连接已断开！");
            sseEmitter.complete();
            System.out.println("连接已断开，token=" + satoken);
        }catch (Exception e) {
            e.printStackTrace();
            // sseEmitterMap.remove(uid);
            // log.info("用户{},消息id:{},推送异常:{}", uid,messageId, e.getMessage());
            // sseEmitter.complete();
        }
    }

    /**
     * 指定 uid 断开连接
     *
     */
    public static void closeByUid(long uid) {
        List<String> tokenList = StpUtil.getTokenValueListByLoginId(uid);
        for (String token : tokenList) {
            closeByToken(token);
        }
    }

}