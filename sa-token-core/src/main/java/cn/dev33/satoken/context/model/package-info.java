/**
 * 因为不能确定最终运行的 web 容器属于标准 Servlet 模型还是非 Servlet 模型，特封装此包下的包装类进行对接。
 * 调用路径为：Sa-Token 功能函数 -> SaRequest 封装接口 -> SaRequest 具体实现类。
 */
package cn.dev33.satoken.context.model;