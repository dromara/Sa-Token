package cn.dev33.satoken.exception;

/**
 * 一个异常：代表账号已被封禁 
 * 
 * @author kong
 */
public class DisableLoginException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130143L;

	/** 异常标记值 */
	public static final String BE_VALUE = "disable";
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "此账号已被封禁";

	/**
	 * LoginKey
	 */
	private String loginKey;

	/**
	 * 被封禁的账号id 
	 */
	private Object loginId;
	
	/**
	 * 封禁剩余时间，单位：秒 
	 */
	private long disableTime;
	
	/**
	 * 获得LoginKey
	 * 
	 * @return See above
	 */
	public String getLoginKey() {
		return loginKey;
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
	 * 获取: 封禁剩余时间，单位：秒
	 * @return See above
	 */
	public long getDisableTime() {
		return disableTime;
	}
	
	/**
	 * 构造方法创建一个
	 * 
	 * @param loginKey loginKey
	 * @param loginId  被封禁的账号id 
	 * @param disableTime 封禁剩余时间，单位：秒 
	 */
	public DisableLoginException(String loginKey, Object loginId, long disableTime) {
		super(BE_MESSAGE);
		this.loginId = loginId;
		this.loginKey = loginKey;
		this.disableTime = disableTime;
	}

	
	
}
