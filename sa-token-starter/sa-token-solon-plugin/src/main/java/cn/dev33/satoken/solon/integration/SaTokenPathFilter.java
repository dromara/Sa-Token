package cn.dev33.satoken.solon.integration;


import org.noear.solon.annotation.Note;

/**
 * sa-token 基于路由的过滤式鉴权（增加了注解的处理）；使用优先级要低些 //对静态文件有处理效果
 *
 * @author noear
 * @since 1.10
 */
@Note("推荐：由 SaTokenFilter 替代")
@Deprecated
public class SaTokenPathFilter extends SaTokenFilter {

}
