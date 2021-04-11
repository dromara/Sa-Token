package cn.dev33.satoken.reactor.model;

import org.springframework.web.server.ServerWebExchange;

import cn.dev33.satoken.context.model.SaStorage;

/**
 * Storage for Reactor 
 * @author kong
 *
 */
public class SaStorageForReactor implements SaStorage {

	/**
	 * 底层Request对象
	 */
	ServerWebExchange exchange;
	
	/**
	 * 实例化
	 * @param exchange exchange对象 
	 */
	public SaStorageForReactor(ServerWebExchange exchange) {
		this.exchange = exchange;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return exchange;
	}

	/**
	 * 在 [Request作用域] 里写入一个值 
	 */
	@Override
	public void set(String key, Object value) {
		exchange.getAttributes().put(key, value);
	}

	/**
	 * 在 [Request作用域] 里获取一个值 
	 */
	@Override
	public Object get(String key) {
		return exchange.getAttributes().get(key);
	}

	/**
	 * 在 [Request作用域] 里删除一个值 
	 */
	@Override
	public void delete(String key) {
		exchange.getAttributes().remove(key);
	}

}
