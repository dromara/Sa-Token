/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.dao;


import cn.dev33.satoken.dao.auto.SaTokenDaoByStringFollowObject;
import cn.dev33.satoken.dao.timedcache.SaTimedCache;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;

/**
 * Sa-Token 持久层接口，默认实现类，基于 SaTimedCache （内存 Map，系统重启后数据丢失）
 *
 * @author click33
 * @since 1.10.0
 */
public class SaTokenDaoDefaultImpl implements SaTokenDaoByStringFollowObject {

	public SaTimedCache timedCache = new SaTimedCache();
	
	// ------------------------ Object 读写操作 
	
	@Override
	public Object getObject(String key) {
		return timedCache.getObject(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getObject(String key, Class<T> classType){
		return (T) getObject(key);
	}

	@Override
	public void setObject(String key, Object object, long timeout) {
		timedCache.setObject(key, object, timeout);
	}

	@Override
	public void updateObject(String key, Object object) {
		timedCache.updateObject(key, object);
	}

	@Override
	public void deleteObject(String key) {
		timedCache.deleteObject(key);
	}

	@Override
	public long getObjectTimeout(String key) {
		return timedCache.getObjectTimeout(key);
	}

	@Override
	public void updateObjectTimeout(String key, long timeout) {
		timedCache.updateObjectTimeout(key, timeout);
	}


	// --------- 会话管理

	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		return SaFoxUtil.searchList(timedCache.expireMap.keySet(), prefix, keyword, start, size, sortType);
	}


	// --------- 组件生命周期

	/**
	 * 组件被安装时，开始刷新数据线程
	 */
	@Override
	public void init() {
		timedCache.initRefreshThread();
	}

	/**
	 * 组件被卸载时，结束定时任务，不再定时清理过期数据
	 */
	@Override
	public void destroy() {
		timedCache.endRefreshThread();
	}
}
