package cn.dev33.satoken.exception;

public class UnrecognizedLoginKeyException extends RuntimeException{

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 6806129545290130140L;

    /**
     * loginKey
     */
    private String loginKey;

    /**
     * 获得loginKey
     *
     * @return loginKey
     */
    public String getLoginKey() {
        return loginKey;
    }

    public UnrecognizedLoginKeyException(String loginKey) {
        super("未知的loginKey: " + loginKey);
        this.loginKey = loginKey;
    }

}
