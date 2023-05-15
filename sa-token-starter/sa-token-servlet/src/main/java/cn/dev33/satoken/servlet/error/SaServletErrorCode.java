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
package cn.dev33.satoken.servlet.error;

/**
 * 定义 sa-token-servlet 所有异常细分状态码 
 * 
 * @author click33
 * @since 2022-10-30
 */
public interface SaServletErrorCode {
	
	/** 转发失败 */
	int CODE_20001 = 20001;

	/** 重定向失败 */
	int CODE_20002 = 20002;

}
