package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.serializer.SaSerializerForBase64UseEmoji;
import cn.dev33.satoken.serializer.SaSerializerForBase64UsePeriodicTable;
import cn.dev33.satoken.serializer.SaSerializerForBase64UseSpecialSymbols;
import cn.dev33.satoken.serializer.SaSerializerForBase64UseTianGan;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseBase64;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseHex;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseISO_8859_1;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Sa-Token Serializer 序列化模块测试
 * 
 * @author click33 
 *
 */
public class SaSerializerTemplateTest {

	/** 整个测试类开始前做一次性准备 */
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ SaSerializerTemplateTest star ...");
    }

    /** 整个测试类结束后做一次性清理 */
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ SaSerializerTemplateTest end ... \n");
    }

    /** 测试 JDK + Base64 序列化往返 */
    @Test
    public void testSaSerializerTemplateForJdkUseBase64() {
        SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseBase64());
        Assertions.assertEquals(SaManager.getSaSerializerTemplate().getClass(), SaSerializerTemplateForJdkUseBase64.class);

        // test   Object -> String
        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = SaManager.getSaSerializerTemplate().objectToString(user);
        Assertions.assertEquals("rO0ABXNyABljb20ucGoudGVzdC5tb2RlbC5TeXNVc2Vy0MeZoPBtVUwCAARJAANhZ2VKAAJpZEwABG5hbWV0ABJMamF2YS9sYW5nL1N0cmluZztMAARyb2xldAAbTGNvbS9wai90ZXN0L21vZGVsL1N5c1JvbGU7eHAAAAASAAAAAAAAJxF0AAblvKDkuIlw", objectString);

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
        Assertions.assertEquals("ACED000573720019636F6D2E706A2E746573742E6D6F64656C2E53797355736572D0C799A0F06D554C0200044900036167654A000269644C00046E616D657400124C6A6176612F6C616E672F537472696E673B4C0004726F6C6574001B4C636F6D2F706A2F746573742F6D6F64656C2F537973526F6C653B7870000000120000000000002711740006E5BCA0E4B88970", objectString);

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
        Assertions.assertEquals("雷辰中甲乙坤卯西甲乙日天离谷中雾艮庚石雾兑庚亥北兑丙宙霜离谷未日离丙宙酉金坤卯亥艮谷亥西中寅金巽石巳乙霜亥戌东丙甲甲未癸甲甲卯火巽谷亥子甲甲癸田巽戊东甲乙庚宙火离乾亥中甲乙癸寅坎月己谷震申安电震乾宙山丑信卯中艮月日雾巽北霜寅甲甲未西离谷南日兑甲甲离酉庚卯露离申安东坎土安中巽坤卯中丑谷信露巽庚亥电丑信卯宙艮信癸露离庚戌泰金辛甲甲甲甲甲申甲甲甲甲甲甲甲甲癸南己中甲甲离日露子丁地雾壬日东", objectString);

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
        Assertions.assertEquals("钌磷碘氢氦铬硅锑氢氦锶氪镍铯碘银铜氮铌银锌氮钛碲锌锂铈钯镍铯氩锶镍锂铈钙镓铬硅钛铜铯钛锑碘铝镓铁铌硫氦钯钛钪铟锂氢氢氩氖氢氢硅硒铁铯钛钠氢氢氖钼铁硼铟氢氦氮铈硒镍钒钛碘氢氦氖铝钴钇碳铯锰钾钐铑锰钒铈锆镁氙硅碘铜钇锶银铁碲钯铝氢氢氩锑镍铯锡锶锌氢氢镍钙氮硅镉镍钾钐铟钴溴钐碘铁铬硅碘镁铯氙镉铁氮钛铑镁氙硅铈铜氙氖镉镍氮钪钕镓氧氢氢氢氢氢钾氢氢氢氢氢氢氢氢氖锡碳碘氢氢镍锶镉钠铍铷银氟锶铟", objectString);

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
        Assertions.assertEquals("→▃☶▲▼▌▂☳▲▼§♫▬☰☶↘〓▶↑↘◤▶▎☱◤●☀↓▬☰▆§▬●☀█◥▌▂▎〓☰▎☳☶▁◥▊↑▄▼↓▎▏☲●▲▲▆♥▲▲▂♩▊☰▎♦▲▲♥↗▊■☲▲▼▶☀♩▬▍▎☶▲▼♥▁▉〼★☰▋▇‥↙▋▍☀↖♣☵▂☶〓〼§↘▊☱↓▁▲▲▆☳▬☰☷§◤▲▲▬█▶▂☴▬▇‥☲▉♪‥☶▊▌▂☶♣☰☵☴▊▶▎↙♣☵▂☀〓☵♥☴▬▶▏▪◥◀▲▲▲▲▲▇▲▲▲▲▲▲▲▲♥☷★☶▲▲▬§☴♦◆♬↘♠§☲", objectString);

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
        Assertions.assertEquals("😫😎😴😀😁😗😍😲😀😁😥😣😛😶😴😮😜😆😨😮😝😆😕😳😝😂😹😭😛😶😑😥😛😂😹😓😞😗😍😕😜😶😕😲😴😌😞😙😨😏😁😭😕😔😰😂😀😀😑😉😀😀😍😡😙😶😕😊😀😀😉😩😙😄😰😀😁😆😹😡😛😖😕😴😀😁😉😌😚😦😅😶😘😒😽😬😘😖😹😧😋😵😍😴😜😦😥😮😙😳😭😌😀😀😑😲😛😶😱😥😝😀😀😛😓😆😍😯😛😒😽😰😚😢😽😴😙😗😍😴😋😶😵😯😙😆😕😬😋😵😍😹😜😵😉😯😛😆😔😻😞😇😀😀😀😀😀😒😀😀😀😀😀😀😀😀😉😱😅😴😀😀😛😥😯😊😃😤😮😈😥😰", objectString);

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
