package cn.dev33.satoken.spring;

import cn.dev33.satoken.config.SaRouterRule;
import cn.dev33.satoken.config.SaRouterRuleCheck;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 动态路由执行器
 *
 * @author einsitang
 */
public class SaDynamicRouterExecutor {

  /**
   * 是否启动
   */
  private boolean enable;

  /**
   * 路由规则
   */
  private SaRouterRule[] rules;

  /**
   * 重置规则，可以搭配SpringCloud进一步实现热更新动态路由配置
   *
   * @param enable 是否启动
   * @param rules  SaRouterRule[]
   */
  public void reset(Boolean enable, SaRouterRule[] rules) {
    this.enable = Boolean.TRUE.equals(enable);
    if (rules == null || rules.length == 0) {
      return;
    }
    this.rules = rules;

  }

  public void execute() {
    if (!this.enable) {
      return;
    }
    if (rules == null) {
      return;
    }
    for (SaRouterRule rule : rules) {
      executeRule(rule);
    }
  }

  private void executeRule(SaRouterRule rule) {
    String[] match, notMatch, matchMethod, notMatchMethod;
    match = rule.getMatch();
    notMatch = rule.getNotMatch();
    matchMethod = rule.getMatchMethod();
    notMatchMethod = rule.getNotMatchMethod();
    SaRouterStaff staff = SaRouter.newMatch();
    SaRouterRuleCheck checker = rule.getCheck();
    if (match != null) {
      staff = staff.match(match);
    }
    if (notMatch != null) {
      staff.notMatch(notMatch);
    }

    if (matchMethod != null) {
      staff.matchMethod(matchMethod);
    }
    if (notMatchMethod != null) {
      staff.notMatchMethod(notMatchMethod);
    }
    if (checker == null) {
      checker = SaRouterRuleCheck.defaultChecker();
    }

    SaRouterRuleCheck finalChecker = checker;
    staff.check(r -> {
      if (Boolean.TRUE.equals(finalChecker.getLoginRequired())) {
        StpUtil.checkLogin();
      }
      if (finalChecker.getRoles() != null) {
        StpUtil.checkRoleAnd(finalChecker.getRoles());
      }
      if (finalChecker.getRolesOr() != null) {
        StpUtil.checkRoleOr(finalChecker.getRolesOr());
      }
      if (finalChecker.getPermissions() != null) {
        StpUtil.checkPermissionAnd(finalChecker.getPermissions());
      }
      if (finalChecker.getPermissionsOr() != null) {
        StpUtil.checkPermissionOr(finalChecker.getPermissionsOr());
      }
    });

  }

}
