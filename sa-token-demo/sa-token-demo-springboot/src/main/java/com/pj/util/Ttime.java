package com.pj.util;


/**
 * 用于测试用时
 * @author kong
 *
 */
public class Ttime {

	private long start=0;	//开始时间
	private long end=0;		//结束时间
	
	public static Ttime t = new Ttime();	//static快捷使用
	
	/**
	 * 开始计时
	 * @return
	 */
	public Ttime start() {
		start=System.currentTimeMillis();
		return this;
	}
	
	
	/**
	 * 结束计时
	 */
	public Ttime end() {
		end=System.currentTimeMillis();
		return this;
	}

	
	/**
	 * 返回所用毫秒数
	 */
	public long returnMs() {
		return end-start;
	}
	
	/**
	 * 格式化输出结果
	 */
	public void outTime() {
		System.out.println(this.toString());
	}
	
	/**
	 * 结束并格式化输出结果
	 */
	public void endOutTime() {
		this.end().outTime();
	}
	
	@Override
	public String toString() {
		return (returnMs() + 0.0) / 1000 + "s";		// 格式化为：0.01s
	}
	
	
	
}
