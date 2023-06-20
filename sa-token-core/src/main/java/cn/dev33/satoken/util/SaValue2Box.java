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


/**
 * 封装两个值的容器，方便取值、写值等操作，value1 和 value2 用逗号隔开，形如：123,abc
 *
 * @author click33
 * @since 1.35.0
 */
public class SaValue2Box {

    /**
     * 第一个值
     */
    private Object value1;

    /**
     * 第二个值
     */
    private Object value2;

    /**
     * 直接提供两个值构建
     * @param value1 第一个值
     * @param value2 第二个值
     */
    public SaValue2Box(Object value1, Object value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * 根据字符串构建，字符串形如：123,abc
     * @param valueString 形如：123,abc
     */
    public SaValue2Box(String valueString) {
        if(valueString == null){
            return;
        }
        String[] split = valueString.split(",");
        if(split.length == 0){
            // do nothing
        }
        else if(split.length == 1){
            this.value1 = split[0];
        }
        else {
            this.value1 = split[0];
            this.value2 = split[1];
        }
    }

    /**
     * 获取第一个值
     * @return 第一个值
     */
    public Object getValue1() {
        return value1;
    }

    /**
     * 获取第二个值
     * @return 第二个值
     */
    public Object getValue2() {
        return value2;
    }

    /**
     * 设置第一个值
     * @param value1 第一个值
     */
    public void setValue1(Object value1) {
        this.value1 = value1;
    }

    /**
     * 设置第二个值
     * @param value2 第二个值
     */
    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    /**
     * 判断第一个值是否为 null 或者空字符串
     * @return /
     */
    public boolean value1IsEmpty() {
        return SaFoxUtil.isEmpty(value1);
    }

    /**
     * 判断第二个值是否为 null 或者空字符串
     * @return /
     */
    public boolean value2IsEmpty() {
        return SaFoxUtil.isEmpty(value2);
    }

    /**
     * 获取第一个值，并转化为 String 类型
     * @return /
     */
    public String getValue1AsString() {
        return value1 == null ? null : value1.toString();
    }

    /**
     * 获取第二个值，并转化为 String 类型
     * @return /
     */
    public String getValue2AsString() {
        return value2 == null ? null : value2.toString();
    }

    /**
     * 获取第一个值，并转化为 long 类型
     * @return /
     */
    public long getValue1AsLong() {
        return Long.parseLong(value1.toString());
    }

    /**
     * 获取第二个值，并转化为 long 类型
     * @return /
     */
    public long getValue2AsLong() {
        return Long.parseLong(value2.toString());
    }

    /**
     * 获取第一个值，并转化为 long 类型，值不存在则返回默认值
     * @return /
     */
    public Long getValue1AsLong(Long defaultValue) {
        // 这里如果改成三元表达式，会导致自动拆箱造成空指针异常，所以只能用 if-else
        if(value1 == null){
            return defaultValue;
        }
        return Long.parseLong(value1.toString());
    }

    /**
     * 获取第二个值，并转化为 long 类型，值不存在则返回默认值
     * @return /
     */
    public Long getValue2AsLong(Long defaultValue) {
        // 这里如果改成三元表达式，会导致自动拆箱造成空指针异常，所以只能用 if-else
        if(value2 == null){
            return defaultValue;
        }
        return Long.parseLong(value2.toString());
    }

    /**
     * 该容器是否为无值状态，即：value1 无值、value2 无值
     * @return /
     */
    public boolean isNotValueState() {
        return value1IsEmpty() && value2IsEmpty();
    }

    /**
     * 该容器是否为单值状态，即：value1 有值、value2 == 无值
     * @return /
     */
    public boolean isSingleValueState() {
        return ! value1IsEmpty() && value2IsEmpty();
    }

    /**
     * 该容器是否为双值状态，即：value2 有值 （在 value2 有值的情况下，即使 value1 无值，也视为双值状态）
     * @return /
     */
    public boolean isDoubleValueState() {
        return ! value2IsEmpty();
    }

    /**
     * 获取两个值的字符串形式，形如：123,abc
     *
     * <br><br>
     * <pre>
     *     System.out.println(new SaValue2Box(1, 2));     // 1,2
     *     System.out.println(new SaValue2Box(null, null));   // null
     *     System.out.println(new SaValue2Box(1, null));   // 1
     *     System.out.println(new SaValue2Box(null, 2));  // ,2
     * </pre>
     * @return /
     */
    @Override
    public String toString() {
        if(value1 == null && value2 == null) {
            return null;
        }
        if(value1 != null && value2 == null) {
            return value1.toString();
        }
        return (value1 == null ? "" : value1.toString()) + "," + (value2 == null ? "" : value2.toString());
    }

}