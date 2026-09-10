package cn.dev33.satoken.test.serializer;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.serializer.SaSerializerForBase64UseEmoji;
import cn.dev33.satoken.serializer.SaSerializerForBase64UsePeriodicTable;
import cn.dev33.satoken.serializer.SaSerializerForBase64UseSpecialSymbols;
import cn.dev33.satoken.serializer.SaSerializerForBase64UseTianGan;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseBase64;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseHex;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseISO_8859_1;
import cn.dev33.satoken.test.model.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Sa-Token Serializer 序列化模块测试
 * 
 * @author click33 
 *
 */
public class SaSerializerTemplateTest {

    /** 测试 JDK + Base64 序列化往返 */
    @Test
    public void testSaSerializerTemplateForJdkUseBase64() {
        SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseBase64());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerTemplateForJdkUseBase64.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("rO0ABXNyACNjbi5kZXYzMy5zYXRva2VuLnRlc3QubW9kZWwuU3lzVXNlctDHmaDwbVVMAgAESQADYWdlSgACaWRMAARuYW1ldAASTGphdmEvbGFuZy9TdHJpbmc7TAAEcm9sZXQAJUxjbi9kZXYzMy9zYXRva2VuL3Rlc3QvbW9kZWwvU3lzUm9sZTt4cAAAABIAAAAAAAAnEXQABuW8oOS4iXA=", objectString);

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    /** 测试 JDK + Hex 序列化往返 */
    @Test
    public void testSaSerializerTemplateForJdkUseHex() {
        SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseHex());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerTemplateForJdkUseHex.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("ACED000573720023636E2E64657633332E7361746F6B656E2E746573742E6D6F64656C2E53797355736572D0C799A0F06D554C0200044900036167654A000269644C00046E616D657400124C6A6176612F6C616E672F537472696E673B4C0004726F6C657400254C636E2F64657633332F7361746F6B656E2F746573742F6D6F64656C2F537973526F6C653B7870000000120000000000002711740006E5BCA0E4B88970", objectString);

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    /** 测试 JDK + ISO-8859-1 序列化往返 */
    @Test
    public void testSaSerializerTemplateForJdkUseISO_8859_1() {
        SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseISO_8859_1());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerTemplateForJdkUseISO_8859_1.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        // Assertions.assertEquals("xxxx", objectString); // 太过奇形怪状，无法直接断言

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    /** 测试 Base64 天干表序列化往返 */
    @Test
    public void testSaSerializerForBase64UseTianGan() {
        SaManager.setSaSerializerTemplate(new SaSerializerForBase64UseTianGan());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerForBase64UseTianGan.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("雷辰中甲乙坤卯西甲丙卯天离土宙地巽坤震北寅西宙北震坤未露坎谷亥雾丑山未日艮岚午雾离乾安地巽乾东雾戌岚日北亥坤卯日艮霜丁辛月坎丁东离亥亥寅甲水甲戊申午甲丁震乾兑日申水甲丙坎乾未寅甲甲未雾震乾信日兑甲甲申酉庚田火兑月戊露离庚己雾巽西安酉兑辛癸田离月艮泰酉甲甲戊艮月安电巽坤午甲癸戌南天离土安地巽坤震北寅西安北震坤未露坎谷亥雾丑岚未日艮岚午露离乾安地巽乾东露戌岚日北戌月安电巽酉霜宇艮甲甲甲甲乙壬甲甲甲甲甲甲甲甲山戊坤午甲乙雾乾铭石辰申宇土坤甲口", objectString);

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    /** 测试 Base64 元素周期表序列化往返 */
    @Test
    public void testSaSerializerForBase64UsePeriodicTable() {
        SaManager.setSaSerializerTemplate(new SaSerializerForBase64UsePeriodicTable());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerForBase64UsePeriodicTable.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("钌磷碘氢氦铬硅锑氢锂硅氪镍溴铈铷铁铬锰碲铝锑铈碲锰铬氩镉钴铯钛银镁锆氩锶铜钡氯银镍钒钐铷铁钒铟银钪钡锶碲钛铬硅锶铜钯铍氧钇钴铍铟镍钛钛铝氢砷氢硼钾氯氢铍锰钒锌锶钾砷氢锂钴钒氩铝氢氢氩银锰钒氙锶锌氢氢钾钙氮钼硒锌钇硼镉镍氮碳银铁锑钐钙锌氧氖钼镍钇铜钕钙氢氢硼铜钇钐铑铁铬氯氢氖钪锡氪镍溴钐铷铁铬锰碲铝锑钐碲锰铬氩镉钴铯钛银镁钡氩锶铜钡氯镉镍钒钐铷铁钒铟镉钪钡锶碲钪钇钐铑铁钙钯镧铜氢氢氢氢氦氟氢氢氢氢氢氢氢氢锆硼铬氯氢氦银钒钷铌磷钾镧溴铬氢鿫", objectString);

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    /** 测试 Base64 特殊符号序列化往返 */
    @Test
    public void testSaSerializerForBase64UseSpecialSymbols() {
        SaManager.setSaSerializerTemplate(new SaSerializerForBase64UseSpecialSymbols());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerForBase64UseSpecialSymbols.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("→▃☶▲▼▌▂☳▲●▂♫▬♪☀♬▊▌▋☱▁☳☀☱▋▌▆☴▉☰▎↘♣↖▆§〓◐▅↘▬▍‥♬▊▍☲↘▏◐§☱▎▌▂§〓↓◆◀〼▉◆☲▬▎▎▁▲◢▲■▇▅▲◆▋▍◤§▇◢▲●▉▍▆▁▲▲▆↘▋▍☵§◤▲▲▇█▶↗♩◤〼■☴▬▶★↘▊☳‥█◤◀♥↗▬〼〓▪█▲▲■〓〼‥↙▊▌▅▲♥▏☷♫▬♪‥♬▊▌▋☱▁☳‥☱▋▌▆☴▉☰▎↘♣◐▆§〓◐▅☴▬▍‥♬▊▍☲☴▏◐§☱▏〼‥↙▊█↓◑〓▲▲▲▲▼♠▲▲▲▲▲▲▲▲↖■▌▅▲▼↘▍•↑▃▇◑♪▌▲※", objectString);

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    /** 测试 Base64 Emoji 序列化往返 */
    @Test
    public void testSaSerializerForBase64UseEmoji() {
        SaManager.setSaSerializerTemplate(new SaSerializerForBase64UseEmoji());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerForBase64UseEmoji.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("😫😎😴😀😁😗😍😲😀😂😍😣😛😢😹😤😙😗😘😳😌😲😹😳😘😗😑😯😚😶😕😮😋😧😑😥😜😷😐😮😛😖😽😤😙😖😰😮😔😷😥😳😕😗😍😥😜😭😃😇😦😚😃😰😛😕😕😌😀😠😀😄😒😐😀😃😘😖😝😥😒😠😀😂😚😖😑😌😀😀😑😮😘😖😵😥😝😀😀😒😓😆😩😡😝😦😄😯😛😆😅😮😙😲😽😓😝😇😉😩😛😦😜😻😓😀😀😄😜😦😽😬😙😗😐😀😉😔😱😣😛😢😽😤😙😗😘😳😌😲😽😳😘😗😑😯😚😶😕😮😋😷😑😥😜😷😐😯😛😖😽😤😙😖😰😯😔😷😥😳😔😦😽😬😙😓😭😸😜😀😀😀😀😁😈😀😀😀😀😀😀😀😀😧😄😗😐😀😁😮😖😼😨😎😒😸😢😗😀", objectString);

        // test   String -> Object
        SysUser user2 = SaManager.getSaSerializerTemplate().stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
    }

    // 测试 Null 值
    private void testNull() {
        Assertions.assertNull(SaManager.getSaSerializerTemplate().objectToString(null));
        Assertions.assertNull(SaManager.getSaSerializerTemplate().stringToObject(null, SysUser.class));
        Assertions.assertNull(SaManager.getSaSerializerTemplate().stringToObject(null));
    }

}
