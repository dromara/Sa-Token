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
package cn.dev33.satoken.interceptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link SaInterceptor} 在异步请求的收尾派发（DispatcherType.ASYNC）上不应重复鉴权的回归测试：
 * 首次 REQUEST 派发鉴权通过后，收尾派发若因 token 失效再次抛出 NotLoginException，
 * 会因响应已提交而无法转为错误响应，只能被容器中止连接（SSE 长流场景下客户端丢掉整条流）。
 */
public class SaInterceptorAsyncDispatchTest {

    @RestController
    static class AsyncController {
        @GetMapping("/async")
        public Callable<String> async() {
            return () -> "ok";
        }
    }

    /** REQUEST 派发鉴权一次，ASYNC 收尾派发不再重复鉴权 */
    @Test
    public void asyncDispatchDoesNotReAuth() throws Exception {
        AtomicInteger authCount = new AtomicInteger();
        SaInterceptor interceptor = new SaInterceptor(h -> authCount.incrementAndGet()).isAnnotation(false);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AsyncController())
                .addInterceptors(interceptor)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/async"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());

        Assertions.assertEquals(1, authCount.get(), "收尾 ASYNC 派发不应再次执行鉴权");
    }
}
