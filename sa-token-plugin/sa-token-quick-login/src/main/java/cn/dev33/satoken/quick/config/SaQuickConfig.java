package cn.dev33.satoken.quick.config;

/**
 * sa-quick 配置类 Model
 * 
 * @author click33
 *
 */
public class SaQuickConfig {

	/** 是否开启全局认证 */
	private Boolean auth = true;

	/** 用户名 */
	private String name = "sa";

	/** 密码 */
	private String pwd = "123456";

	/** 是否自动生成一个账号和密码 */
	private Boolean auto = false; 
	
	/** 登录页面的标题 */
	private String title = "Sa-Token 登录";

	/** 是否显示底部版权信息 */
	private Boolean copr = true;

	/** 配置拦截的路径，逗号分隔 */
	private String include = "/**";

	/** 配置拦截的路径，逗号分隔 */
	private String exclude = "";

	public Boolean getAuth() {
		return auth;
	}

	public void setAuth(Boolean auth) {
		this.auth = auth;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Boolean getAuto() {
		return auto;
	}

	public void setAuto(Boolean auto) {
		this.auto = auto;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getCopr() {
		return copr;
	}

	public void setCopr(Boolean copr) {
		this.copr = copr;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	
	
	@Override
	public String toString() {
		return "SaQuickConfig{" +
				"auth=" + auth +
				", name='" + name + '\'' +
				", pwd='" + pwd + '\'' +
				", auto=" + auto +
				", title='" + title + '\'' +
				", copr=" + copr +
				", include='" + include + '\'' +
				", exclude='" + exclude + '\'' +
				'}';
	}
}
