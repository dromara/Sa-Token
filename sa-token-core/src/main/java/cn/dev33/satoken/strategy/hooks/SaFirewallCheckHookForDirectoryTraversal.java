/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.strategy.hooks;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.RequestPathInvalidException;

/**
 * 防火墙策略校验钩子函数：请求 path 目录遍历符检测
 *
 * @author click33
 * @since 1.41.0
 */
public class SaFirewallCheckHookForDirectoryTraversal implements SaFirewallCheckHook {

    /**
     * 默认实例
     */
    public static SaFirewallCheckHookForDirectoryTraversal instance = new SaFirewallCheckHookForDirectoryTraversal();

    /**
     * 执行的方法
     *
     * @param req 请求对象
     * @param res 响应对象
     * @param extArg 预留扩展参数
     */
    @Override
    public void execute(SaRequest req, SaResponse res, Object extArg) {
        String requestPath = req.getRequestPath();
        if(!isPathValid(requestPath)) {
            throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
        }
    }

    /**
     * 检查路径是否有效
     * @param path /
     * @return /
     */
    public static boolean isPathValid(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        // 必须以 '/' 开头
        if (path.charAt(0) != '/') {
            return false;
        }

        // 特殊处理根路径 "/"
        if (path.equals("/")) {
            return true;
        }

        String[] components = path.split("/");
        for (int i = 0; i < components.length; i++) {
            String component = components[i];

            // 处理空组件
            if (component.isEmpty()) {
                if (i == 0) {
                    // 允许路径以 "/" 开头（第一个组件为空）
                    continue;
                } else {
                    // 其他位置的空组件（如中间或末尾的 "//"）非法
                    return false;
                }
            }

            // 检查是否包含 "." 或 ".." 组件
            if (component.equals(".") || component.equals("..")) {
                return false;
            }
        }
        return true;
    }

    // 测试
//    public static void main(String[] args) {
//        test("/user/info", true);      // 合法
//        test("/user/info/.", false);   // 末尾包含 /.
//        test("/user/info/..", false);  // 末尾包含 /..
//        test("/user/info/./get", false); // 中间包含 /./
//        test("/user/info/../get", false); // 中间包含 /../
//        test("/user/info/.js", true);  // 合法后缀
//        test("/.abcdef", true);         // 合法隐藏文件
//        test("//user", false);          // 多余斜杠
//        test("/user//info", false);     // 中间多余斜杠
//        test("/", true);               // 根目录合法
//        test("user/../info", false);    // 不以 / 开头
//        test("a/b/c/..", false);       // 不以 / 开头
//        test("test/.", false);          // 不以 / 开头
//        test("", true);                // 空路径非法
//    }
//
//    private static void test(String path, boolean expected) {
//        boolean result = isPathValid(path);
//        System.out.printf("Path: %-20s Expected: %-5s Actual: %-5s %s%n",
//                path, expected, result, (result == expected) ? "✓" : "✗");
//    }

}
