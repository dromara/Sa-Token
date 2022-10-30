package cn.dev33.satoken.error;

/**
 * 定义所有异常细分状态码 
 * 
 * @author kong
 * @since: 2022-10-30
 */
public interface SaErrorCode {
	
	/** 代表这个异常在抛出时未指定异常细分状态码 */
	public static final int CODE_UNDEFINED = -1;

	// ------------ 
	
	/** 未能获取有效的上下文处理器 */
	public static final int CODE_10001 = 10001;

	/** 未能获取有效的上下文 */
	public static final int CODE_10002 = 10002;

	/** JSON 转换器未实现 */
	public static final int CODE_10003 = 10003;

	/** 未能从全局 StpLogic 集合中找到对应 type 的 StpLogic */
	public static final int CODE_10011 = 10011;

	/** 指定的配置文件加载失败 */
	public static final int CODE_10021 = 10021;

	/** 配置文件属性无法正常读取 */
	public static final int CODE_10022 = 10022;

	/** 重置的侦听器集合不可以为空 */
	public static final int CODE_10031 = 10031;

	/** 注册的侦听器不可以为空 */
	public static final int CODE_10032 = 10032;

	// 1030x core模块

	/** 提供的 Same-Token 是无效的 */
	public static final int CODE_10301 = 10301;

	/** 表示未能通过 Http Basic 认证校验 */
	public static final int CODE_10311 = 10311;

	/** 提供的 HttpMethod 是无效的 */
	public static final int CODE_10321 = 10321;

	// 1100x StpLogic

	/** 未能读取到有效Token */
	public static final int CODE_11001 = 11001;

	/** 登录时的账号id值为空 */
	public static final int CODE_11002 = 11002;

	/** 更改 Token 指向的 账号Id 时，账号Id值为空 */
	public static final int CODE_11003 = 11003;

	/** 未能读取到有效Token */
	public static final int CODE_11011 = 11011;
	
	/** Token无效 */
	public static final int CODE_11012 = 11012;

	/** Token已过期 */
	public static final int CODE_11013 = 11013;
	
	/** Token已被顶下线 */
	public static final int CODE_11014 = 11014;

	/** Token已被踢下线 */
	public static final int CODE_11015 = 11015;

	/** Token已临时过期 */
	public static final int CODE_11016 = 11016;
	
	/** 在未集成 sa-token-jwt 插件时调用 getExtra() 抛出异常 */
	public static final int CODE_11031 = 11031;

	/** 缺少指定的角色 */
	public static final int CODE_11041 = 11041;

	/** 缺少指定的权限 */
	public static final int CODE_11051 = 11051;

	/** 当前账号未通过服务封禁校验 */
	public static final int CODE_11061 = 11061;

	/** 提供要解禁的账号无效 */
	public static final int CODE_11062 = 11062;

	/** 提供要解禁的服务无效 */
	public static final int CODE_11063 = 11063;

	/** 提供要解禁的等级无效 */
	public static final int CODE_11064 = 11064;

	/** 二级认证校验未通过 */
	public static final int CODE_11071 = 11071;
	
	
	
	// ------------ 
	
	/** 请求中缺少指定的参数 */
	public static final int CODE_12001 = 12001;

	/** 构建 Cookie 时缺少 name 参数 */
	public static final int CODE_12002 = 12002;

	/** 构建 Cookie 时缺少 value 参数 */
	public static final int CODE_12003 = 12003;

	// ------------ 

	/** Base64 编码异常 */
	public static final int CODE_12101 = 12101;

	/** Base64 解码异常 */
	public static final int CODE_12102 = 12102;

	/** URL 编码异常 */
	public static final int CODE_12103 = 12103;

	/** URL 解码异常 */
	public static final int CODE_12104 = 12104;

	/** md5 加密异常 */
	public static final int CODE_12111 = 12111;

	/** sha1 加密异常 */
	public static final int CODE_12112 = 12112;

	/** sha256 加密异常 */
	public static final int CODE_12113 = 12113;

	/** AES 加密异常 */
	public static final int CODE_12114 = 12114;

	/** AES 解密异常 */
	public static final int CODE_12115 = 12115;

	/** RSA 公钥加密异常 */
	public static final int CODE_12116 = 12116;

	/** RSA 私钥加密异常 */
	public static final int CODE_12117 = 12117;

	/** RSA 公钥解密异常 */
	public static final int CODE_12118 = 12118;

	/** RSA 私钥解密异常 */
	public static final int CODE_12119 = 12119;

}
