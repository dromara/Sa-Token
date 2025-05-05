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
package cn.dev33.satoken.sso.model;

import java.io.Serializable;

/**
 * Model: Ticket 码
 *
 * @author click33
 * @since 1.43.0
 */
public class TicketModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * ticket 码
	 */
	public String ticket;

	/**
	 * 应用标识
	 */
	public String client;

	/**
	 * 对应 loginId
	 */
	public Object loginId;

	/**
	 * 会话 token
	 */
	public String tokenValue;

	/**
	 * 创建时间，13位时间戳
	 */
	public long createTime;

	/**
	 * 构建一个
	 */
	public TicketModel() {
		this.createTime = System.currentTimeMillis();
	}

	/**
	 * 构建一个
	 * @param ticket 授权码
	 * @param client 应用id
	 * @param loginId 对应的账号id
	 * @param tokenValue 会话 token
	 */
	public TicketModel(String ticket, String client, Object loginId, String tokenValue) {
		this();
		this.ticket = ticket;
		this.client = client;
		this.loginId = loginId;
		this.tokenValue = tokenValue;
	}


	// get set

	/**
	 * 获取 ticket 码
	 *
	 * @return /
	 */
	public String getTicket() {
		return this.ticket;
	}

	/**
	 * 设置 ticket 码
	 *
	 * @param ticket /
	 * @return 对象自身
	 */
	public TicketModel setTicket(String ticket) {
		this.ticket = ticket;
		return this;
	}

	/**
	 * 获取 应用标识
	 *
	 * @return /
	 */
	public String getClient() {
		return this.client;
	}

	/**
	 * 设置 应用标识
	 *
	 * @param client /
	 * @return 对象自身
	 */
	public TicketModel setClient(String client) {
		this.client = client;
		return this;
	}

	/**
	 * 获取 对应 loginId
	 *
	 * @return /
	 */
	public Object getLoginId() {
		return this.loginId;
	}

	/**
	 * 设置 对应 loginId
	 *
	 * @param loginId /
	 * @return 对象自身
	 */
	public TicketModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	/**
	 * 获取 会话 token
	 *
	 * @return tokenValue 会话 token
	 */
	public String getTokenValue() {
		return this.tokenValue;
	}

	/**
	 * 设置 会话 token
	 *
	 * @param tokenValue 会话 token
	 * @return 对象自身
	 */
	public TicketModel setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
		return this;
	}

	/**
	 * 获取 创建时间，13位时间戳
	 *
	 * @return /
	 */
	public long getCreateTime() {
		return this.createTime;
	}

	/**
	 * 设置 创建时间，13位时间戳
	 *
	 * @param createTime /
	 * @return 对象自身
	 */
	public TicketModel setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}

	@Override
	public String toString() {
		return "TicketModel{" +
				"ticket='" + ticket + '\'' +
				", client='" + client + '\'' +
				", loginId=" + loginId +
				", tokenValue=" + tokenValue +
				", createTime=" + createTime +
				'}';
	}

}
