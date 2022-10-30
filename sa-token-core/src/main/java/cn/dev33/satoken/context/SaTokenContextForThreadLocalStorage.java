package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.InvalidContextException;

/**
 * Sa-Token 上下文处理器 [ThreadLocal版本] ---- 对象存储器 
 * 
 * @author kong
 *
 */
public class SaTokenContextForThreadLocalStorage {
	
	/**
	 * 基于 ThreadLocal 的 [Box存储器] 
	 */
	public static ThreadLocal<Box> boxThreadLocal = new InheritableThreadLocal<Box>();
	
	/**
	 * 初始化 [Box存储器]
	 * @param request {@link SaRequest}
	 * @param response {@link SaResponse}
	 * @param storage {@link SaStorage}
	 */
	public static void setBox(SaRequest request, SaResponse response, SaStorage storage) {
		Box bok = new Box(request, response, storage);
		boxThreadLocal.set(bok);
	};

	/**
	 * 清除 [Box存储器]
	 */
	public static void clearBox() {
		boxThreadLocal.remove();
	};

	/**
	 * 获取 [Box存储器]
	 * @return see note
	 */
	public static Box getBox() {
		return boxThreadLocal.get();
	};
	
	/**
	 * 获取 [Box存储器], 如果为空则抛出异常 
	 * @return see note
	 */
	public static Box getBoxNotNull() {
		Box box = boxThreadLocal.get();
		if(box ==  null) {
			throw new InvalidContextException("未能获取有效的上下文").setCode(SaErrorCode.CODE_10002);
		}
		return box;
	};

	/**
	 * 在 [Box存储器] 获取 [Request] 对象
	 * 
	 * @return see note 
	 */
	public static SaRequest getRequest() {
		return getBoxNotNull().getRequest();
	}

	/**
	 * 在 [Box存储器] 获取 [Response] 对象
	 * 
	 * @return see note 
	 */
	public static SaResponse getResponse() {
		return getBoxNotNull().getResponse();
	}

	/**
	 * 在 [Box存储器] 获取 [存储器] 对象 
	 * 
	 * @return see note 
	 */
	public static SaStorage getStorage() {
		return getBoxNotNull().getStorage();
	}

	
	/**
	 * 临时内部类，用于存储[request、response、storage]三个对象 
	 * @author kong
	 */
	/**
	 * @author kong
	 *
	 */
	public static class Box {
		
		public SaRequest request;
		
		public SaResponse response;
		
		public SaStorage storage;
		
		public Box(SaRequest request, SaResponse response, SaStorage storage){
			this.request = request;
			this.response = response;
			this.storage = storage;
		}

		public SaRequest getRequest() {
			return request;
		}

		public void setRequest(SaRequest request) {
			this.request = request;
		}

		public SaResponse getResponse() {
			return response;
		}

		public void setResponse(SaResponse response) {
			this.response = response;
		}

		public SaStorage getStorage() {
			return storage;
		}

		public void setStorage(SaStorage storage) {
			this.storage = storage;
		}

		@Override
		public String toString() {
			return "Box [request=" + request + ", response=" + response + ", storage=" + storage + "]";
		}
		
	}
	
}
