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
package cn.dev33.satoken.servlet.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SaTokenContext 上下文读写工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTokenContextUtil {

	/**
	 * 写入当前上下文
	 * @param request /
	 * @param response /
	 */
	public static void setContext(HttpServletRequest request, HttpServletResponse response) {
		SaRequest req = new SaRequestForServlet(request);
		SaResponse res = new SaResponseForServlet(response);
		SaStorage stg = new SaStorageForServlet(request);
		SaManager.getSaTokenContext().setContext(req, res, stg);
	}

	/**
	 * 清除当前上下文
	 */
	public static void clearContext() {
		SaManager.getSaTokenContext().clearContext();
	}

	/**
	 * 写入上下文对象, 并在执行函数后将其清除
	 * @param request /
	 * @param response /
	 * @param fun /
	 */
	public static void setContext(HttpServletRequest request, HttpServletResponse response, SaFunction fun) {
		try {
			setContext(request, response);
			fun.run();
		} finally {
			clearContext();
		}
	}

	/**
	 * 获取当前 ModelBox
	 * @return /
	 */
	public static SaTokenContextModelBox getModelBox() {
		return SaManager.getSaTokenContext().getModelBox();
	}

	/**
	 * 获取当前 Request
	 * @return /
	 */
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getModelBox().getRequest().getSource();
	}

	/**
	 * 获取当前 Response
	 * @return /
	 */
	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) getModelBox().getResponse().getSource();
	}

}
