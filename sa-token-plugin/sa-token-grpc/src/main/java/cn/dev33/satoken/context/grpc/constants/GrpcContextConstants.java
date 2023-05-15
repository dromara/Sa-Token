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
package cn.dev33.satoken.context.grpc.constants;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import io.grpc.Metadata;

/**
 * 常量 
 * 
 * @author lym
 * @since 2022/8/26 14:27
 */
public class GrpcContextConstants {
    public static final Metadata.Key<String> SA_SAME_TOKEN =
            Metadata.Key.of(SaSameUtil.SAME_TOKEN, Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> SA_JUST_CREATED_NOT_PREFIX =
            Metadata.Key.of(SaTokenConsts.JUST_CREATED_NOT_PREFIX, Metadata.ASCII_STRING_MARSHALLER);

}
