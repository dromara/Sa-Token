package cn.dev33.satoken.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * sa-token基于路由的拦截式鉴权 
 * @author kong
 */
public class SaRouteInterceptor implements HandlerInterceptor {

	
	// ----------------- 属性 ----------------- 

	/**
	 * 底层的 StpLogic 对象 
	 */
	private StpLogic stpLogic;
	
	/**
	 * 验证类型 (1=登录验证, 2=角色验证, 3=权限验证, 4=自定义验证) 
	 */
	private int type;
	
	/**
	 * 验证模式 AND | OR 
	 */
	private SaMode mode;
	
	/**
	 * 标识码数组 
	 */
	private String[] codes;
	
	/**
	 * 自定义模式下的执行函数
	 */
	private SaFunction function;
	
	
	/**
	 * 表示登录验证 
	 */
	public static final int LOGIN = 1;

	/**
	 * 表示角色验证 
	 */
	public static final int ROLE = 2;

	/**
	 * 表示权限验证 
	 */
	public static final int PERMISSION = 3;

	/**
	 * 表示自定义验证 
	 */
	public static final int CUSTOM = 4;
	

	/**
	 * @return 底层的 StpLogic 对象 
	 */
	public StpLogic getStpLogic() {
		if(stpLogic == null) {
			stpLogic = StpUtil.stpLogic;
		}
		return stpLogic;
	}

	/**
	 * @param stpLogic 底层的 StpLogic 对象 
	 */
	public SaRouteInterceptor setStpLogic(StpLogic stpLogic) {
		this.stpLogic = stpLogic;
		return this;
	}

	/**
	 * @return 验证类型 (1=登录验证, 2=角色验证, 3=权限验证, 4=自定义验证) 
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type 验证类型 (1=登录验证, 2=角色验证, 3=权限验证, 4=自定义验证) 
	 */
	public SaRouteInterceptor setType(int type) {
		this.type = type;
		return this;
	}
	
	/**
	 * @return 验证模式 AND | OR 
	 */
	public SaMode getMode() {
		return mode;
	}

	/**
	 * @param mode 验证模式 AND | OR 
	 */
	public SaRouteInterceptor setMode(SaMode mode) {
		this.mode = mode;
		return this;
	}
	
	/**
	 * @return 标识码数组 
	 */
	public String[] getCodes() {
		return codes;
	}

	/**
	 * @param codes 标识码数组 
	 */
	public SaRouteInterceptor setCodes(String... codes) {
		this.codes = codes;
		return this;
	}

	/**
	 * @return 自定义模式下的执行函数
	 */
	public SaFunction getFunction() {
		return function;
	}

	/**
	 * @param function 设置自定义模式下的执行函数
	 */
	public SaRouteInterceptor setFunction(SaFunction function) {
		this.type = SaRouteInterceptor.CUSTOM;
		this.function = function;
		return this;
	}

	
	// ----------------- 构建相关 ----------------- 
	
	/**
	 * 创建 (全参数) 
	 * @param type 验证类型 (1=登录验证, 2=角色验证, 3=权限验证, 4=自定义验证) 
	 * @param mode 验证模式 AND | OR 
	 * @param codes 标识码数组
	 */
	public SaRouteInterceptor(int type, SaMode mode, String... codes) {
		super();
		this.type = type;
		this.mode = mode;
		this.codes = codes;
	}

	/**
	 * 创建 (默认为登录验证) 
	 */
	public SaRouteInterceptor() {
		this(SaRouteInterceptor.LOGIN, null, new String[0]);
	}

	/**
	 * 创建 (默认为自定义认证) 
	 * @param function 自定义模式下的执行函数
	 */
	public SaRouteInterceptor(SaFunction function) {
		this(SaRouteInterceptor.CUSTOM, null, new String[0]);
		setFunction(function);
	}

	/**
	 * 构建一个模式为登录认证的sa路由拦截器
	 * @return sa拦截器 
	 */
	public static SaRouteInterceptor createLoginVal() {
		return new SaRouteInterceptor();
	}
	
	/**
	 * 构建一个模式为角色认证的sa路由拦截器 
	 * @param roles 需要验证的数组标识列表
	 * @return sa拦截器 
	 */
	public static SaRouteInterceptor createRoleVal(String... roles) {
		return new SaRouteInterceptor(SaRouteInterceptor.ROLE, SaMode.AND, roles);
	}
	
	/**
	 * 构建一个模式为权限认证的sa路由拦截器 
	 * @param permissions 需要验证的数组权限列表 
	 * @return sa拦截器 
	 */
	public static SaRouteInterceptor createPermissionVal(String... permissions) {
		return new SaRouteInterceptor(SaRouteInterceptor.PERMISSION, SaMode.AND, permissions);
	}
	
	/**
	 * 创建一个模式为自定义认证的sa路由拦截器 
	 * @param function 自定义模式下的执行函数
	 * @return sa拦截器 
	 */
	public static SaRouteInterceptor createCustomVal(SaFunction function) {
		return new SaRouteInterceptor(function);
	}
	
	
	// ----------------- 验证方法 ----------------- 

	/**
	 * 每次请求之前触发的方法 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// 根据模式进行验证 
		if(this.type == SaRouteInterceptor.LOGIN) {
			getStpLogic().checkLogin();
		} else if(this.type == SaRouteInterceptor.ROLE) {
			if(mode == SaMode.AND) {
				getStpLogic().checkRoleAnd(codes);
			} else {
				getStpLogic().checkRoleOr(codes);
			}
		} else if(this.type == SaRouteInterceptor.PERMISSION) {
			if(mode == SaMode.AND) {
				getStpLogic().checkPermissionAnd(codes);
			} else {
				getStpLogic().checkPermissionOr(codes);
			}
		} else if(this.type == SaRouteInterceptor.CUSTOM) {
			function.run();
		}
		
		// 通过验证 
		return true;
	}

	
	
	
}
