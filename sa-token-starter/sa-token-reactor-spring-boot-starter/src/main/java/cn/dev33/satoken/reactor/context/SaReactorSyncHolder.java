package cn.dev33.satoken.reactor.context;

import org.springframework.web.server.ServerWebExchange;

import cn.dev33.satoken.context.SaTokenContextForThreadLocalStorage;
import cn.dev33.satoken.context.SaTokenContextForThreadLocalStorage.Box;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;

/**
 * Reactor上下文操作 [同步]
 * @author kong
 *
 */
public class SaReactorSyncHolder {
	
	/**
	 * 写入上下文对象 
	 * @param exchange see note 
	 */
	public static void setContent(ServerWebExchange exchange) {
		SaRequest request = new SaRequestForReactor(exchange.getRequest());
		SaResponse response = new SaResponseForReactor(exchange.getResponse());
		SaStorage storage = new SaStorageForReactor(exchange);
		SaTokenContextForThreadLocalStorage.setBox(request, response, storage);
	}
	
	/**
	 * 获取上下文对象 
	 * @return see note 
	 */
	public static ServerWebExchange getContent() {
		Box box = SaTokenContextForThreadLocalStorage.getBoxNotNull();
		return (ServerWebExchange)box.getStorage().getSource();
	}
	
	/**
	 * 清除上下文对象
	 */
	public static void clearContent() {
		SaTokenContextForThreadLocalStorage.clearBox();
	}
	
	/**
	 * 写入上下文对象, 并在执行函数后将其清除  
	 * @param exchange see note 
	 * @param fun see note 
	 */
	public static void setContent(ServerWebExchange exchange, SaFunction fun) {
		try {
			setContent(exchange);
			fun.run();
		} finally {
			clearContent();
		}
	}
	
}
