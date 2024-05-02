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
package cn.dev33.satoken.sso.util;

/**
 * Sa-Token-SSO模块相关常量
 *
 * @author click33
 * @since 1.30.0
 */
public class SaSsoConsts {

	/** Client端单点注销回调URL的Set集合，存储在Session中使用的key */
	@Deprecated
	public static final String SLO_CALLBACK_SET_KEY = "SLO_CALLBACK_SET_KEY_";

	/** Client 端 Model 信息的 List 集合，存储在 SaSession 中使用的key */
	public static final String SSO_CLIENT_MODEL_LIST_KEY_ = "SSO_CLIENT_MODEL_LIST_KEY_";

	/** 表示OK的返回结果 */
	public static final String OK = "ok";

	/** 表示自己 */
	public static final String SELF = "self";

	/** 表示简单模式（SSO模式一） */
	public static final String MODE_SIMPLE = "simple";

	/** 表示ticket模式（SSO模式二和模式三） */
	public static final String MODE_TICKET = "ticket";
	
	/** 表示请求没有得到任何有效处理 {msg: "not handle"} */
	public static final String NOT_HANDLE = "{\"msg\": \"not handle\"}";

	/** client 身份，* 代表通配，可以解析出所有 client 的 ticket */
	public static final String CLIENT_WILDCARD = "*";

	/** SSO 模式1 */
	public static final int SSO_MODE_1 = 1;
	/** SSO 模式2 */
	public static final int SSO_MODE_2 = 2;
	/** SSO 模式3 */
	public static final int SSO_MODE_3 = 3;



}
