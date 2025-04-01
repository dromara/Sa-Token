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
package cn.dev33.satoken.quick.config;

import cn.dev33.satoken.quick.function.DoLoginHandleFunction;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * sa-quick 配置类 Model
 * 
 * @author click33
 * @since 1.19.0
 */
public class SaQuickConfig {

	/** 是否开启全局登录校验，如果为 false，则不再拦截请求出现登录页 */
	private Boolean auth = true;

	/** 用户名 */
	private String name = "sa";

	/** 密码 */
	private String pwd = "123456";

	/** 是否自动生成一个账号和密码，此配置项为 true 后，name、pwd 字段将失效 */
	private Boolean auto = false; 
	
	/** 登录页面的标题 */
	private String title = "Sa-Token 登录";

	/** 是否显示底部版权信息 */
	private Boolean copr = true;

	/** 配置拦截的路径，逗号分隔 */
	private String include = "/**";

	/** 配置拦截的路径，逗号分隔 */
	private String exclude = "";

	public Boolean getAuth() {
		return auth;
	}

	public void setAuth(Boolean auth) {
		this.auth = auth;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Boolean getAuto() {
		return auto;
	}

	public void setAuto(Boolean auto) {
		this.auto = auto;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getCopr() {
		return copr;
	}

	public void setCopr(Boolean copr) {
		this.copr = copr;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	/**
	 * 登录处理函数
	 */
	public DoLoginHandleFunction doLoginHandle = (name, pwd) -> {

		// 参数完整性校验
		if(SaFoxUtil.isEmpty(name) || SaFoxUtil.isEmpty(pwd)) {
			return SaResult.get(500, "请输入账号和密码", null);
		}

		// 密码校验：将前端提交的 name、pwd 与配置文件中的配置项进行比对
		if(name.equals(this.getName()) && pwd.equals(this.getPwd())) {
			StpUtil.login(this.getName());
			return SaResult.data(StpUtil.getTokenInfo());
		} else {
			return SaResult.error("账号或密码输入错误");
		}
	};


	@Override
	public String toString() {
		return "SaQuickConfig{" +
				"auth=" + auth +
				", name='" + name + '\'' +
				", pwd='" + pwd + '\'' +
				", auto=" + auto +
				", title='" + title + '\'' +
				", copr=" + copr +
				", include='" + include + '\'' +
				", exclude='" + exclude + '\'' +
				'}';
	}
}
