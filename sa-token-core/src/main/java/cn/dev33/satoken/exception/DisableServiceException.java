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

	/** 异常标记值（已更改为 SaTokenConsts.DEFAULT_DISABLE_LEVEL） */
	@Deprecated
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
	 * 具体被封禁的服务 
	 */
	private String service;

	/**
	 * 具体被封禁的等级 
	 */
	private int level;

	/**
	 * 校验时要求低于的等级 
	 */
	private int limitLevel;
	
	/**
	 * 封禁剩余时间，单位：秒 
	 */
	private long disableTime;

	/**
	 * 获取：账号类型 
	 * 
	 * @return / 
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * 获取: 被封禁的账号id 
	 * 
	 * @return / 
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * 获取: 被封禁的服务 
	 * 
	 * @return / 
	 */
	public Object getService() {
		return service;
	}

	/**
	 * 获取: 被封禁的等级 
	 * 
	 * @return / 
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 获取: 校验时要求低于的等级 
	 * 
	 * @return / 
	 */
	public int getLimitLevel() {
		return limitLevel;
	}
	
	/**
	 * 获取: 封禁剩余时间，单位：秒
	 * @return / 
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
	 * @param level 被封禁的等级 
	 * @param limitLevel 校验时要求低于的等级 
	 * @param disableTime 封禁剩余时间，单位：秒 
	 */
	public DisableServiceException(String loginType, Object loginId, String service, int level, int limitLevel, long disableTime) {
		super(BE_MESSAGE + service);
		this.loginId = loginId;
		this.loginType = loginType;
		this.service = service;
		this.level = level;
		this.limitLevel = limitLevel;
		this.disableTime = disableTime;
	}

}
