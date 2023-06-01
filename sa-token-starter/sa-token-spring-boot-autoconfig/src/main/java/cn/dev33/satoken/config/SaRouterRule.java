package cn.dev33.satoken.config;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 路由规则
 *
 * @author einsitang
 */
public class SaRouterRule implements Serializable {


  private static final long serialVersionUID = -657275926804055868L;

  private String[] match;

  private String[] notMatch;

  private String[] matchMethod;

  private String[] notMatchMethod;

  private SaRouterRuleCheck check;

  public String[] getMatch() {
    return match;
  }

  public void setMatch(String[] match) {
    this.match = match;
  }

  public String[] getNotMatch() {
    return notMatch;
  }

  public void setNotMatch(String[] notMatch) {
    this.notMatch = notMatch;
  }

  public String[] getMatchMethod() {
    return matchMethod;
  }

  public void setMatchMethod(String[] matchMethod) {
    this.matchMethod = matchMethod;
  }

  public String[] getNotMatchMethod() {
    return notMatchMethod;
  }

  public void setNotMatchMethod(String[] notMatchMethod) {
    this.notMatchMethod = notMatchMethod;
  }

  public SaRouterRuleCheck getCheck() {
    return check;
  }

  public void setCheck(SaRouterRuleCheck check) {
    this.check = check;
  }

  @Override
  public String toString() {
    return "SaRouterRule{" + "match=" + Arrays.toString(match) + ", notMatch=" + Arrays.toString(
        notMatch) + ", matchMethod=" + Arrays.toString(matchMethod) + ", notMatchMethod="
        + Arrays.toString(notMatchMethod) + ", check=" + check + '}';
  }
}
