package cn.dev33.satoken.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 任务调度Util
 * @author kong
 *
 */
public class SaTaskUtil {

	/**
	 * 延时指定毫秒执行一个函数  
	 * @param fc 要执行的函数 
	 * @param delay 延时的毫秒数 
	 * @return timer任务对象
	 */
	public static Timer setTimeout(FunctionRunClass fc, int delay) {
		Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	fc.run();
                timer.cancel();
            }
        }, delay);
        return timer;
	}
	
	/**
	 * 延时delay毫秒，每隔period毫秒执行一个函数  
	 * @param fc 要执行的函数 
	 * @param delay 延时的毫秒数 
	 * @param period 每隔多少毫秒执行一次  
	 * @return timer任务对象
	 */
	public static Timer setInterval(FunctionRunClass fc, int delay, int period) {
		Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	fc.run();
            }
        }, delay, period);
        return timer;
	}
	
	/**
	 * 封装一个内部类，便于操作 
	 * @author kong 
	 */
	public static interface FunctionRunClass{
		public void run();
	}
	
}
