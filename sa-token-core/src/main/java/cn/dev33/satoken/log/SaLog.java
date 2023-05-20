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
package cn.dev33.satoken.log;

/**
 * Sa-Token 日志输出接口
 * 
 * @author click33
 * @since 1.33.0
 */
public interface SaLog {

    /**
     * 输出 trace 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    void trace(String str, Object ...args);

    /**
     * 输出 debug 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    void debug(String str, Object ...args);

    /**
     * 输出 info 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    void info(String str, Object ...args);

    /**
     * 输出 warn 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    void warn(String str, Object ...args);

    /**
     * 输出 error 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    void error(String str, Object ...args);

    /**
     * 输出 fatal 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    void fatal(String str, Object ...args);
    
}
