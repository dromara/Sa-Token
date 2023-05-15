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
package cn.dev33.satoken.error;

/**
 * 定义所有异常细分状态码 
 * 
 * @author click33
 * @since 2022-10-30
 */
public interface SaErrorCode {
	
	/** 代表这个异常在抛出时未指定异常细分状态码 */
	int CODE_UNDEFINED = -1;

	// ------------ 
	
	/** 未能获取有效的上下文处理器 */
	int CODE_10001 = 10001;

	/** 未能获取有效的上下文 */
	int CODE_10002 = 10002;

	/** JSON 转换器未实现 */
	int CODE_10003 = 10003;

	/** 未能从全局 StpLogic 集合中找到对应 type 的 StpLogic */
	int CODE_10011 = 10011;

	/** 指定的配置文件加载失败 */
	int CODE_10021 = 10021;

	/** 配置文件属性无法正常读取 */
	int CODE_10022 = 10022;

	/** 重置的侦听器集合不可以为空 */
	int CODE_10031 = 10031;

	/** 注册的侦听器不可以为空 */
	int CODE_10032 = 10032;

	// 1030x core模块

	/** 提供的 Same-Token 是无效的 */
	int CODE_10301 = 10301;

	/** 表示未能通过 Http Basic 认证校验 */
	int CODE_10311 = 10311;

	/** 提供的 HttpMethod 是无效的 */
	int CODE_10321 = 10321;

	// 1100x StpLogic

	/** 未能读取到有效Token */
	int CODE_11001 = 11001;

	/** 登录时的账号id值为空 */
	int CODE_11002 = 11002;

	/** 更改 Token 指向的 账号Id 时，账号Id值为空 */
	int CODE_11003 = 11003;

	/** 未能读取到有效Token */
	int CODE_11011 = 11011;
	
	/** Token无效 */
	int CODE_11012 = 11012;

	/** Token已过期 */
	int CODE_11013 = 11013;
	
	/** Token已被顶下线 */
	int CODE_11014 = 11014;

	/** Token已被踢下线 */
	int CODE_11015 = 11015;

	/** Token已被冻结 */
	int CODE_11016 = 11016;
	
	/** 在未集成 sa-token-jwt 插件时调用 getExtra() 抛出异常 */
	int CODE_11031 = 11031;

	/** 缺少指定的角色 */
	int CODE_11041 = 11041;

	/** 缺少指定的权限 */
	int CODE_11051 = 11051;

	/** 当前账号未通过服务封禁校验 */
	int CODE_11061 = 11061;

	/** 提供要解禁的账号无效 */
	int CODE_11062 = 11062;

	/** 提供要解禁的服务无效 */
	int CODE_11063 = 11063;

	/** 提供要解禁的等级无效 */
	int CODE_11064 = 11064;

	/** 二级认证校验未通过 */
	int CODE_11071 = 11071;
	

	// ------------ 
	
	/** 请求中缺少指定的参数 */
	int CODE_12001 = 12001;

	/** 构建 Cookie 时缺少 name 参数 */
	int CODE_12002 = 12002;

	/** 构建 Cookie 时缺少 value 参数 */
	int CODE_12003 = 12003;

	// ------------ 

	/** Base64 编码异常 */
	int CODE_12101 = 12101;

	/** Base64 解码异常 */
	int CODE_12102 = 12102;

	/** URL 编码异常 */
	int CODE_12103 = 12103;

	/** URL 解码异常 */
	int CODE_12104 = 12104;

	/** md5 加密异常 */
	int CODE_12111 = 12111;

	/** sha1 加密异常 */
	int CODE_12112 = 12112;

	/** sha256 加密异常 */
	int CODE_12113 = 12113;

	/** AES 加密异常 */
	int CODE_12114 = 12114;

	/** AES 解密异常 */
	int CODE_12115 = 12115;

	/** RSA 公钥加密异常 */
	int CODE_12116 = 12116;

	/** RSA 私钥加密异常 */
	int CODE_12117 = 12117;

	/** RSA 公钥解密异常 */
	int CODE_12118 = 12118;

	/** RSA 私钥解密异常 */
	int CODE_12119 = 12119;

	// ------------ 

	/** 参与参数签名的秘钥不可为空 */
	int CODE_12201 = 12201;

	/** 给定的签名无效 */
	int CODE_12202 = 12202;

	/** timestamp 超出允许的范围 */
	int CODE_12203 = 12203;

}
