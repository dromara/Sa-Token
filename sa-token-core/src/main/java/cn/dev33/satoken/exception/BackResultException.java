package cn.dev33.satoken.exception;

/**
 * 一个异常：代表停止匹配，直接退出，向前端输出结果 （框架内部专属异常，一般情况下开发者无需关注）
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class BackResultException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130143L;

	/**
	 * 要输出的结果 
	 */
	public Object result;
	
	/**
	 * 构造 
	 * @param result 要输出的结果 
	 */
	public BackResultException(Object result) {
		super(String.valueOf(result));
		this.result = result;
	}

}
