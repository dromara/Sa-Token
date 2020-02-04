package cn.dev33.satoken.exception;

/**
 * 没有指定权限码，抛出的异常 
 */
public class NotPermissionException extends RuntimeException {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 6806129545290130142L;
	
	private Object code;
	

	/**
	 * @return 获得权限码 
	 */
	public Object getCode() {
		return code;
	}

	public NotPermissionException(Object code) {
        super("无此权限：" + code);
        this.code = code;
    }

//	public NotPermissionException(Object code, String s) {
//		super(s);
//		this.code = code;
//	}

}
