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
package cn.dev33.satoken.serializer;

/**
 * 序列化器，base64 算法，采用64个特殊符号作为元字符集
 * 
 * @author click33
 * @since 1.41.0
 */
public class SaSerializerForBase64UseSpecialSymbols extends SaSerializerForBase64UseCustomCharacters {

	public SaSerializerForBase64UseSpecialSymbols() {
		super(
				// 自定义字符集，需确保包含64个不重复的字符
				"▲▼●◆■★▶◀" +
				"♠♥♦♣▁▂▃▄" +
				"▅▆▇█▏▎▍▌" +
				"▋▊▉▬〓◤◥◣" +
				"◢♩♪♫♬§〼↖" +
				"↑↗←→↙↓↘☴" +
				"☲☷☳☱☶☵☰◐" +
				"◑☀☼▪•‥…∷"
				,
				// 填充符，确保不在字符集中
				'※'
		);
	}

}
