package cn.dev33.satoken.secure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Sa-Token Base64工具类
 * @author kong
 *
 */
public class SaBase64Util {

	private static final Base64.Encoder ENCODER = Base64.getEncoder();
	private static final Base64.Decoder DECODER = Base64.getDecoder();
	
	/**
	 * Base64编码，byte[] 转 String
	 * @param bytes byte[]
	 * @return 字符串
	 */
	public static String encodeBytesToString(byte[] bytes){
		return ENCODER.encodeToString(bytes);
	}

	/**
	 * Base64解码，String 转 byte[]
	 * @param text 字符串
	 * @return byte[]
	 */
	public static byte[] decodeStringToBytes(String text){
		return DECODER.decode(text);
	}
	
	/**
	 * Base64编码，String 转 String
	 * @param text 字符串
	 * @return Base64格式字符串
	 */
	public static String encode(String text){
		return ENCODER.encodeToString(text.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Base64解码，String 转 String
	 * @param base64Text Base64格式字符串
	 * @return 字符串
	 */
	public static String decode(String base64Text){
		return new String(DECODER.decode(base64Text), StandardCharsets.UTF_8);
	}
	
}
