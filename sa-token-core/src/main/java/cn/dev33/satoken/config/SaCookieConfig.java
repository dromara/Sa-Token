package cn.dev33.satoken.config;

/**
 * Sa-Token Cookie写入 相关配置  
 * @author kong
 *
 */
public class SaCookieConfig {
	
    /**
     * 域（写入Cookie时显式指定的作用域, 常用于单点登录二级域名共享Cookie的场景）
     */
    private String domain; 

    /**
     * 路径 
     */
    private String path;

    /**
     * 是否只在 https 协议下有效 
     */
    private Boolean secure = false; 
    
    /**
     * 是否禁止 js 操作 Cookie 
     */
    private Boolean httpOnly = false; 
    
    /**
     * 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
     */
	private String sameSite;

	/**
	 * @return 域 （写入Cookie时显式指定的作用域, 常用于单点登录二级域名共享Cookie的场景）
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain 域 （写入Cookie时显式指定的作用域, 常用于单点登录二级域名共享Cookie的场景）
	 * @return 对象自身 
	 */
	public SaCookieConfig setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	/**
	 * @return 路径 
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path 路径 
	 * @return 对象自身 
	 */
	public SaCookieConfig setPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * @return 是否只在 https 协议下有效 
	 */
	public Boolean getSecure() {
		return secure;
	}

	/**
	 * @param secure 是否只在 https 协议下有效 
	 * @return 对象自身 
	 */
	public SaCookieConfig setSecure(Boolean secure) {
		this.secure = secure;
		return this;
	}

	/**
	 * @return 是否禁止 js 操作 Cookie 
	 */
	public Boolean getHttpOnly() {
		return httpOnly;
	}

	/**
	 * @param httpOnly 是否禁止 js 操作 Cookie 
	 * @return 对象自身 
	 */
	public SaCookieConfig setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	/**
	 * @return 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
	 */
	public String getSameSite() {
		return sameSite;
	}

	/**
	 * @param sameSite 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
	 * @return 对象自身 
	 */
	public SaCookieConfig setSameSite(String sameSite) {
		this.sameSite = sameSite;
		return this;
	}

	// toString 
	@Override
	public String toString() {
		return "SaCookieConfig [domain=" + domain + ", path=" + path + ", secure=" + secure + ", httpOnly=" + httpOnly
				+ ", sameSite=" + sameSite + "]";
	}
	
}
