package cn.dev33.satoken.config;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 规则检查器
 *
 * @author einsitang
 */
public class SaRouterRuleCheck implements Serializable {

  private static final long serialVersionUID = 6002708589740924351L;

  /**
   * 检查角色列表,逻辑关系: AND
   */
  private String[] roles;

  /**
   * 检查权限列表,逻辑关系: AND
   */
  private String[] permissions;

  /**
   * 检查角色列表,逻辑关系: OR
   */
  private String[] rolesOr;

  /**
   * 检查权限列表,逻辑关系: OR
   */
  private String[] permissionsOr;

  /**
   * 检查是否登录,默认TRUE
   */
  private Boolean loginRequired = Boolean.TRUE;

  public static SaRouterRuleCheck defaultChecker() {
    return new SaRouterRuleCheck();
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }

  public String[] getPermissions() {
    return permissions;
  }

  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }

  public String[] getRolesOr() {
    return rolesOr;
  }

  public void setRolesOr(String[] rolesOr) {
    this.rolesOr = rolesOr;
  }

  public String[] getPermissionsOr() {
    return permissionsOr;
  }

  public void setPermissionsOr(String[] permissionsOr) {
    this.permissionsOr = permissionsOr;
  }

  public Boolean getLoginRequired() {
    return loginRequired;
  }

  public void setLoginRequired(Boolean loginRequired) {
    this.loginRequired = loginRequired;
  }

  @Override
  public String toString() {
    return "SaRouterRuleCheck{" +
        "roles=" + Arrays.toString(roles) +
        ", permissions=" + Arrays.toString(permissions) +
        ", rolesOr=" + Arrays.toString(rolesOr) +
        ", permissionsOr=" + Arrays.toString(permissionsOr) +
        ", loginRequired=" + loginRequired +
        '}';
  }
}
