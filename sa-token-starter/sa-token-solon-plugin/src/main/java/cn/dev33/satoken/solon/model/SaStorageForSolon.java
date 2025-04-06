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
package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.context.model.SaStorage;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.4
 */
public class SaStorageForSolon implements SaStorage {

    protected Context ctx;

    public SaStorageForSolon() {
        this(Context.current());
    }

    public SaStorageForSolon(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public Object getSource() {
        return ctx;
    }

    @Override
    public SaStorageForSolon set(String key, Object value) {
        ctx.attrSet(key, value);
        return this;
    }

    @Override
    public Object get(String key) {
        return ctx.attr(key);
    }

    @Override
    public SaStorageForSolon delete(String key) {
        ctx.attrMap().remove(key);
        return this;
    }
}
