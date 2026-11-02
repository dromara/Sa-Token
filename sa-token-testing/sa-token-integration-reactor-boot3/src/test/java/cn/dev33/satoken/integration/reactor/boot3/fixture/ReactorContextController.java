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
package cn.dev33.satoken.integration.reactor.boot3.fixture;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Boot 3 Reactor 上下文测试端点：走真实请求触发 request / response / storage / holder。
 */
@RestController
@RequestMapping("/reactor/")
public class ReactorContextController {

    /** 用 SaReactorHolder.sync 读取当前请求的 path、参数、Cookie、请求头 */
    @GetMapping("/requestInfo")
    public Mono<SaResult> requestInfo() {
        return SaReactorHolder.sync(() -> {
            SaRequest request = SaHolder.getRequest();
            return SaResult.ok()
                    .set("url", request.getUrl())
                    .set("path", request.getRequestPath())
                    .set("method", request.getMethod())
                    .set("param", request.getParam("q"))
                    .set("header", request.getHeader("X-Test-Header"))
                    .set("host", request.getHost())
                    .set("cookieFirst", request.getCookieFirstValue("token"))
                    .set("cookieLast", request.getCookieLastValue("token"))
                    .set("isAjax", request.isAjax())
                    .set("sourceNotNull", request.getSource() != null);
        });
    }

    /** 用 SaReactorHolder.sync 往当前响应写入状态码和响应头 */
    @GetMapping("/responseInfo")
    public Mono<SaResult> responseInfo() {
        return SaReactorHolder.sync(() -> {
            SaResponse response = SaHolder.getResponse();
            response.setStatus(200);
            response.setHeader("X-Test-Set", "set-value");
            response.addHeader("X-Test-Add", "add-value");
            return SaResult.ok().set("sourceNotNull", response.getSource() != null);
        });
    }

    /** 用 SaReactorHolder.sync 在 exchange 作用域里写入、读取再删除一个值 */
    @GetMapping("/storageInfo")
    public Mono<SaResult> storageInfo() {
        return SaReactorHolder.sync(() -> {
            SaStorage storage = SaHolder.getStorage();
            storage.set("satoken-reactor-key", "stored-value");
            Object value = storage.get("satoken-reactor-key");
            storage.delete("satoken-reactor-key");
            return SaResult.ok()
                    .set("value", value)
                    .set("afterDelete", storage.get("satoken-reactor-key"))
                    .set("sourceNotNull", storage.getSource() != null);
        });
    }

    /** 用 SaReactorSyncHolder 函数版临时包一层同步上下文 */
    @GetMapping("/contextUtil")
    public SaResult contextUtil(ServerWebExchange exchange) {
        return SaReactorSyncHolder.setContext(exchange, () -> SaResult.ok()
                .set("data", SaHolder.getRequest().getRequestPath())
                .set("exchangeNotNull", SaReactorSyncHolder.getExchange() != null));
    }

}
