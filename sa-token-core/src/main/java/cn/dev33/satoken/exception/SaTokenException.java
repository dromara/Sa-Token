package cn.dev33.satoken.exception;

/**
 * sa-token框架内部逻辑发生错误抛出的异常 
 * @author kong
 *
 */
public class SaTokenException extends RuntimeException {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 6806129545290130132L;
	

	public SaTokenException(String message) {
		super(message);	
    }


}
