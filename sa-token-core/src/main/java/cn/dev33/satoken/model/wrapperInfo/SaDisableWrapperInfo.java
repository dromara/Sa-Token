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
package cn.dev33.satoken.model.wrapperInfo;

import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 返回值包装类：描述一个账号是否已被封禁等信息
 *
 * @author click33
 * @since 1.40.0
 */
public class SaDisableWrapperInfo {

    /**
     * 是否被封禁
     */
    public boolean isDisable;

    /**
     * 封禁剩余时间，单位：秒（-1=永久封禁，0 or -2=未封禁）
     */
    public long disableTime;

    /**
     * 封禁等级（最小1级，0=未封禁）
     */
    public int disableLevel;

    /**
     * 构建对象
     *
     * @param isDisable 是否被封禁
     * @param disableTime 封禁剩余时间，单位：秒（-1=永久封禁，0 or -2=未封禁）
     * @param disableLevel 封禁等级（最小1级，0=未封禁）
     */
    public SaDisableWrapperInfo(boolean isDisable, long disableTime, int disableLevel) {
        this.isDisable = isDisable;
        this.disableTime = disableTime;
        this.disableLevel = disableLevel;
    }

    /**
     * 创建一个已封禁描述对象
     * @param disableTime 封禁时间
     * @param disableLevel 封禁等级
     * @return /
     */
    public static SaDisableWrapperInfo createDisabled(long disableTime, int disableLevel) {
        return new SaDisableWrapperInfo(true, disableTime, disableLevel);
    }

    /**
     * 创建一个未封禁描述对象
     * @return /
     */
    public static SaDisableWrapperInfo createNotDisabled() {
        return new SaDisableWrapperInfo(false, 0, SaTokenConsts.NOT_DISABLE_LEVEL);
    }

    /**
     * 创建一个未封禁描述对象，并指定缓存时间，指定时间内不再重复查询
     * @param cacheTime 缓存时间（单位：秒）
     * @return /
     */
    public static SaDisableWrapperInfo createNotDisabled(long cacheTime) {
        return new SaDisableWrapperInfo(false, cacheTime, SaTokenConsts.NOT_DISABLE_LEVEL);
    }

    @Override
    public String toString() {
        return "SaDisableWrapperInfo{" +
                "isDisable=" + isDisable +
                ", disableTime=" + disableTime +
                ", disableLevel=" + disableLevel +
                '}';
    }

    // setter / getter 仅为兼容部分框架序列化操作，不建议调用

    public boolean getIsDisable() {
        return isDisable;
    }

    public SaDisableWrapperInfo setIsDisable(boolean isDisable) {
        this.isDisable = isDisable;
        return this;
    }

    public long getDisableTime() {
        return disableTime;
    }

    public SaDisableWrapperInfo setDisableTime(long disableTime) {
        this.disableTime = disableTime;
        return this;
    }

    public int getDisableLevel() {
        return disableLevel;
    }

    public SaDisableWrapperInfo setDisableLevel(int disableLevel) {
        this.disableLevel = disableLevel;
        return this;
    }

}