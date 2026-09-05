/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pj.test.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地 Http 服务器测试支持类：基于 JDK 内置 HttpServer 起一个临时端点，
 * 供 SaHttpTemplate 各实现类做真实请求往返测试（不依赖任何外部服务）。
 * <p>
 * 启动时使用 {@code port=0}，由操作系统从临时端口池中分配一个<strong>当前未被占用</strong>的端口，
 * 并非在固定范围内随机猜测端口号，因此不存在「碰巧撞到已占用端口」的问题。
 *
 * @author click33
 * @since 1.46.0
 */
public class LocalHttpServer {

    /** GET 测试路径 */
    public static final String PATH_GET = "/get";

    /** POST 测试路径 */
    public static final String PATH_POST = "/post";

    private HttpServer httpServer;

    /** 最近一次请求的 method */
    private volatile String lastMethod;

    /** 最近一次请求的 query 串（不含问号） */
    private volatile String lastQuery;

    /** 最近一次 POST 请求解析出的表单参数 */
    private volatile Map<String, String> lastFormParams;

    /** 启动服务器（绑定 127.0.0.1:0，由操作系统自动分配当前可用端口，不会因端口占用而失败） */
    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        httpServer.createContext(PATH_GET, this::handleGet);
        httpServer.createContext(PATH_POST, this::handlePost);
        httpServer.start();
    }

    /** 关闭服务器 */
    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }

    /** 返回服务器根地址，例如 http://127.0.0.1:12345 */
    public String getBaseUrl() {
        return "http://127.0.0.1:" + httpServer.getAddress().getPort();
    }

    /** 返回最近一次请求的 method */
    public String getLastMethod() {
        return lastMethod;
    }

    /** 返回最近一次请求的 query 串（不含问号） */
    public String getLastQuery() {
        return lastQuery;
    }

    /** 返回最近一次 POST 请求解析出的表单参数 */
    public Map<String, String> getLastFormParams() {
        return lastFormParams;
    }

    /** 处理 GET 请求：记录 method 和 query，返回固定响应 */
    private synchronized void handleGet(HttpExchange exchange) throws IOException {
        lastMethod = exchange.getRequestMethod();
        lastQuery = exchange.getRequestURI().getQuery();
        respond(exchange, "get-ok");
    }

    /** 处理 POST 请求：记录 method 和解析后的表单参数，返回固定响应 */
    private synchronized void handlePost(HttpExchange exchange) throws IOException {
        lastMethod = exchange.getRequestMethod();
        lastFormParams = parseForm(new String(readBody(exchange.getRequestBody()), StandardCharsets.UTF_8));
        respond(exchange, "post-ok");
    }

    /** 写回一个 utf-8 文本响应 */
    private void respond(HttpExchange exchange, String body) throws IOException {
        byte[] resp = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    /** 读取输入流全部字节 */
    private static byte[] readBody(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            bos.write(buf, 0, len);
        }
        return bos.toByteArray();
    }

    /** 解析 form-urlencoded 参数串（key=value&key=value，值做 URL 解码；UTF-8 恒受支持，异常实际不会发生） */
    private static Map<String, String> parseForm(String body) throws UnsupportedEncodingException {
        Map<String, String> params = new LinkedHashMap<>();
        if (body == null || body.isEmpty()) {
            return params;
        }
        for (String pair : body.split("&")) {
            int idx = pair.indexOf('=');
            String key = idx >= 0 ? pair.substring(0, idx) : pair;
            String value = idx >= 0 ? pair.substring(idx + 1) : "";
            params.put(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
        }
        return params;
    }

}
