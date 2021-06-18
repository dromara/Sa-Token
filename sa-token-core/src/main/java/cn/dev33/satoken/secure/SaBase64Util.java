package cn.dev33.satoken.secure;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Sa-Token Base64工具类
 * @author kong
 *
 */
public class SaBase64Util {

	private static Base64.Encoder encoder = Base64.getEncoder();
	private static Base64.Decoder decoder = Base64.getDecoder();
	
	/**
	 * Base64编码，byte[] 转 String
	 * @param bytes byte[]
	 * @return 字符串
	 */
	public static String encodeBytesToString(byte[] bytes){
		return encoder.encodeToString(bytes);
	}

	/**
	 * Base64解码，String 转 byte[]
	 * @param text 字符串
	 * @return byte[]
	 */
	public static byte[] decodeStringToBytes(String text){
		return decoder.decode(text);
	}
	
	/**
	 * Base64编码，String 转 String
	 * @param text 字符串
	 * @return Base64格式字符串
	 */
	public static String encode(String text){
		try {
			return encoder.encodeToString(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Base64解码，String 转 String
	 * @param base64Text Base64格式字符串
	 * @return 字符串
	 */
	public static String decode(String base64Text){
		try {
			return new String(decoder.decode(base64Text), "UTF-8"); 
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e); 
		}
	}
	
}
