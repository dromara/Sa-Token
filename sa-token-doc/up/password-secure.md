# 密码加密

严格来讲，密码加密不属于 [权限认证] 的范畴，但是对于大多数系统来讲，密码加密又是安全认证不可或缺的部分，
所以，应大家要求，`Sa-Token`在 v1.14 版本添加密码加密模块，该模块非常简单，仅仅封装了一些常见的加密算法。



### 摘要加密
md5、sha1、sha256
``` java
// md5加密 
SaSecureUtil.md5("123456");

// sha1加密 
SaSecureUtil.sha1("123456");

// sha256加密 
SaSecureUtil.sha256("123456");
```


### 对称加密
AES加密
``` java
// 定义秘钥和明文
String key = "123456";
String text = "Sa-Token 一个轻量级java权限认证框架";

// 加密 
String ciphertext = SaSecureUtil.aesEncrypt(key, text);
System.out.println("AES加密后：" + ciphertext);

// 解密 
String text2 = SaSecureUtil.aesDecrypt(key, ciphertext);
System.out.println("AES解密后：" + text2);
```

附：内部密钥生成策略，方便其他开发语言对接
```java
    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        //获取SHA1PRNG伪随机数生成器
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        //将实际密码作为伪随机数生成器的种子
        random.setSeed(password.getBytes());
        //利用伪随机数生成器生成128位的密钥，能确保解密时生成的密钥的一致性
        kg.init(128, random);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
```


### 非对称加密
~~RSA加密(已过时)~~
``` java
// 定义私钥和公钥 
String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO+wmt01pwm9lHMdq7A8gkEigk0XKMfjv+4IjAFhWCSiTeP7dtlnceFJbkWxvbc7Qo3fCOpwmfcskwUc3VSgyiJkNJDs9ivPbvlt8IU2bZ+PBDxYxSCJFrgouVOpAr8ar/b6gNuYTi1vt3FkGtSjACFb002/68RKUTye8/tdcVilAgMBAAECgYA1COmrSqTUJeuD8Su9ChZ0HROhxR8T45PjMmbwIz7ilDsR1+E7R4VOKPZKW4Kz2VvnklMhtJqMs4MwXWunvxAaUFzQTTg2Fu/WU8Y9ha14OaWZABfChMZlpkmpJW9arKmI22ZuxCEsFGxghTiJQ3tK8npj5IZq5vk+6mFHQ6aJAQJBAPghz91Dpuj+0bOUfOUmzi22obWCBncAD/0CqCLnJlpfOoa9bOcXSusGuSPuKy5KiGyblHMgKI6bq7gcM2DWrGUCQQD3SkOcmia2s/6i7DUEzMKaB0bkkX4Ela/xrfV+A3GzTPv9bIBamu0VIHznuiZbeNeyw7sVo4/GTItq/zn2QJdBAkEA8xHsVoyXTVeShaDIWJKTFyT5dJ1TR++/udKIcuiNIap34tZdgGPI+EM1yoTduBM7YWlnGwA9urW0mj7F9e9WIQJAFjxqSfmeg40512KP/ed/lCQVXtYqU7U2BfBTg8pBfhLtEcOg4wTNTroGITwe2NjL5HovJ2n2sqkNXEio6Ji0QQJAFLW1Kt80qypMqot+mHhS+0KfdOpaKeMWMSR4Ij5VfE63WzETEeWAMQESxzhavN1WOTb3/p6icgcVbgPQBaWhGg==";
String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvsJrdNacJvZRzHauwPIJBIoJNFyjH47/uCIwBYVgkok3j+3bZZ3HhSW5Fsb23O0KN3wjqcJn3LJMFHN1UoMoiZDSQ7PYrz275bfCFNm2fjwQ8WMUgiRa4KLlTqQK/Gq/2+oDbmE4tb7dxZBrUowAhW9NNv+vESlE8nvP7XXFYpQIDAQAB";

// 文本
String text = "Sa-Token 一个轻量级java权限认证框架";

// 使用公钥加密
String ciphertext = SaSecureUtil.rsaEncryptByPublic(publicKey, text);
System.out.println("公钥加密后：" + ciphertext);

// 使用私钥解密
String text2 = SaSecureUtil.rsaDecryptByPrivate(privateKey, ciphertext);
System.out.println("私钥解密后：" + text2); 
```

你可能会有疑问，私钥和公钥这么长的一大串，我怎么弄出来，手写吗？当然不是，调用以下方法生成即可
``` java
// 生成一对公钥和私钥，其中Map对象 (private=私钥, public=公钥)
System.out.println(SaSecureUtil.rsaGenerateKeyPair());
```


### Base64编码与解码
``` java
// 文本
String text = "Sa-Token 一个轻量级java权限认证框架";

// 使用Base64编码
String base64Text = SaBase64Util.encode(text);
System.out.println("Base64编码后：" + base64Text);

// 使用Base64解码
String text2 = SaBase64Util.decode(base64Text);
System.out.println("Base64解码后：" + text2); 
```

### BCrypt加密
由它加密的文件可在所有支持的操作系统和处理器上进行转移

它的口令必须是8至56个字符，并将在内部被转化为448位的密钥

> 此类来自于https://github.com/jeremyh/jBCrypt/
``` java
// 使用方法
String pw_hash = BCrypt.hashpw(plain_password, BCrypt.gensalt()); 

// 使用checkpw方法检查被加密的字符串是否与原始字符串匹配：
BCrypt.checkpw(candidate_password, stored_hash); 

// gensalt方法提供了可选参数 (log_rounds) 来定义加盐多少，也决定了加密的复杂度:
String strong_salt = BCrypt.gensalt(10);
String stronger_salt = BCrypt.gensalt(12); 
```


<br>

如需更多加密算法，可参考 [Hutool-crypto: 加密](https://hutool.cn/docs/#/crypto/%E6%A6%82%E8%BF%B0)


--- 

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/up/SecureController.java"
	target="_blank">
	本章代码示例：Sa-Token 密码加密 —— [ SecureController.java ]
</a>