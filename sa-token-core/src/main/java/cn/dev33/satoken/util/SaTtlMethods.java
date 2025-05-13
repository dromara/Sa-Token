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
package cn.dev33.satoken.util;

import cn.dev33.satoken.dao.SaTokenDao;

import java.util.*;

/**
 * TTL 操作工具方法
 *
 * @author click33
 * @since 1.43.0
 */
public interface SaTtlMethods {

	/**
	 * 获取一个新的 Token 集合
	 * @return /
	 */
	default List<String> newTokenValueList() {
		return new ArrayList<>();
	}

	/**
	 * 获取一个新的 TokenIndexMap 集合
	 * @return /
	 */
	default Map<String, Long> newTokenIndexMap() {
		return new LinkedHashMap<>();
	}

	/**
	 * 获取最大 ttl 值
	 * @param ttlList /
	 * @return /
	 */
	default long getMaxTtl(ArrayList<Long> ttlList) {
		long maxTtl = 0;
		for (long ttl : ttlList) {
			if(ttl == SaTokenDao.NEVER_EXPIRE) {
				maxTtl = SaTokenDao.NEVER_EXPIRE;
				break;
			}
			if(ttl > maxTtl) {
				maxTtl = ttl;
			}
		}
		return maxTtl;
	}

	/**
	 * 获取最大 ttl 值：过期时间 (13位时间戳) 转 ttl (秒)
	 * @param expireTimeList /
	 * @return /
	 */
	default long getMaxTtlByExpireTime(Collection<Long> expireTimeList) {
		long maxTtl = 0;
		for (long expireTime : expireTimeList) {
			long ttl = expireTimeToTtl(expireTime);
			if(ttl == SaTokenDao.NEVER_EXPIRE) {
				maxTtl = SaTokenDao.NEVER_EXPIRE;
				break;
			}
			if(ttl > maxTtl) {
				maxTtl = ttl;
			}
		}
		return maxTtl;
	}

	/**
	 * 过期时间 (13位时间戳) 转 (13位时间戳) ttl (秒)
	 * @param expireTime /
	 * @return /
	 */
	default long expireTimeToTtl(long expireTime) {
		if(expireTime == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}
		if(expireTime == SaTokenDao.NOT_VALUE_EXPIRE) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		long currentTime = System.currentTimeMillis();
		if(expireTime < currentTime) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return (expireTime - currentTime) / 1000;
	}

	/**
	 * ttl (秒) 转 过期时间 (13位时间戳)
	 * @param ttl /
	 * @return /
	 */
	default long ttlToExpireTime(long ttl) {
		if(ttl == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}
		if(ttl < 0) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return ttl * 1000 + System.currentTimeMillis();
	}

}
