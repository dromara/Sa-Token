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
package cn.dev33.satoken.context.second;

import cn.dev33.satoken.context.SaTokenContext;

/**
 * Sa-Token 二级Context - 基础接口
 * 
 * <p> (利用继承机制实现区别 [ 一级Context ] 与 [ 二级Context ] 的目的)
 *
 * @see SaTokenContext SaTokenContext 上下文处理器
 * 
 * @author click33
 * @since <= 1.34.0
 */
public interface SaTokenSecondContext extends SaTokenContext {
	
}
