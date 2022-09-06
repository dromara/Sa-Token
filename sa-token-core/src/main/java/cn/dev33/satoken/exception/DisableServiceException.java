package cn.dev33.satoken.exception;

/**
 * 一个异常：代表指定账号指定服务已被封禁 
 * 
 * @author kong
 */
public class DisableServiceException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130143L;

	/** 异常标记值 */
	public static final String BE_VALUE = "disable";
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "此账号已被禁止访问服务：";

	/**
	 * 账号类型 
	 */
	private String loginType;

	/**
	 * 被封禁的账号id 
	 */
	private Object loginId;
	
	/**
	 * 封禁剩余时间，单位：秒 
	 */
	private long disableTime;

	/**
	 * 具体被封禁的服务 
	 */
	private String service;

	/**
	 * 获取账号类型 
	 * 
	 * @return See Note
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * 获取: 被封禁的账号id 
	 * 
	 * @return See above
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * 获取: 被封禁的服务 
	 * 
	 * @return See above
	 */
	public Object getService() {
		return service;
	}
	
	/**
	 * 获取: 封禁剩余时间，单位：秒
	 * @return See above
	 */
	public long getDisableTime() {
		return disableTime;
	}
	
	/**
	 * 一个异常：代表指定账号指定服务已被封禁 
	 * 
	 * @param loginType 账号类型
	 * @param loginId  被封禁的账号id 
	 * @param service  具体封禁的服务 
	 * @param disableTime 封禁剩余时间，单位：秒 
	 */
	public DisableServiceException(String loginType, Object loginId, String service, long disableTime) {
		super(BE_MESSAGE + service);
		this.loginId = loginId;
		this.loginType = loginType;
		this.service = service;
		this.disableTime = disableTime;
	}

}
