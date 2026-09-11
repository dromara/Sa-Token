/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.serializer;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.test.model.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 四个预置 Base64 字符集序列化器挂到 SaManager 后，对同一个 SysUser 的序列化结果应与固定黄金串一致，并能原样还原
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSerializerForBase64PresetGoldenTest {

    /** 天干地支字符集：序列化结果应等于黄金串，且能还原 */
    @Test
    public void tianGan_goldenRoundTrip() {
        assertGoldenRoundTrip(new SaSerializerForBase64UseTianGan(),
                "雷辰中甲乙坤卯西甲丙卯天离土宙地巽坤震北寅西宙北震坤未露坎谷亥雾丑山未日艮岚午雾离乾安地巽乾东雾戌岚日北亥坤卯日艮霜丁辛月坎丁东离亥亥寅甲水甲戊申午甲丁震乾兑日申水甲丙坎乾未寅甲甲未雾震乾信日兑甲甲申酉庚田火兑月戊露离庚己雾巽西安酉兑辛癸田离月艮泰酉甲甲戊艮月安电巽坤午甲癸戌南天离土安地巽坤震北寅西安北震坤未露坎谷亥雾丑岚未日艮岚午露离乾安地巽乾东露戌岚日北戌月安电巽酉霜宇艮甲甲甲甲乙壬甲甲甲甲甲甲甲甲山戊坤午甲乙雾乾铭石辰申宇土坤甲口");
    }

    /** 元素周期表字符集：序列化结果应等于黄金串，且能还原 */
    @Test
    public void periodicTable_goldenRoundTrip() {
        assertGoldenRoundTrip(new SaSerializerForBase64UsePeriodicTable(),
                "钌磷碘氢氦铬硅锑氢锂硅氪镍溴铈铷铁铬锰碲铝锑铈碲锰铬氩镉钴铯钛银镁锆氩锶铜钡氯银镍钒钐铷铁钒铟银钪钡锶碲钛铬硅锶铜钯铍氧钇钴铍铟镍钛钛铝氢砷氢硼钾氯氢铍锰钒锌锶钾砷氢锂钴钒氩铝氢氢氩银锰钒氙锶锌氢氢钾钙氮钼硒锌钇硼镉镍氮碳银铁锑钐钙锌氧氖钼镍钇铜钕钙氢氢硼铜钇钐铑铁铬氯氢氖钪锡氪镍溴钐铷铁铬锰碲铝锑钐碲锰铬氩镉钴铯钛银镁钡氩锶铜钡氯镉镍钒钐铷铁钒铟镉钪钡锶碲钪钇钐铑铁钙钯镧铜氢氢氢氢氦氟氢氢氢氢氢氢氢氢锆硼铬氯氢氦银钒钷铌磷钾镧溴铬氢鿫");
    }

    /** 特殊符号字符集：序列化结果应等于黄金串，且能还原 */
    @Test
    public void specialSymbols_goldenRoundTrip() {
        assertGoldenRoundTrip(new SaSerializerForBase64UseSpecialSymbols(),
                "→▃☶▲▼▌▂☳▲●▂♫▬♪☀♬▊▌▋☱▁☳☀☱▋▌▆☴▉☰▎↘♣↖▆§〓◐▅↘▬▍‥♬▊▍☲↘▏◐§☱▎▌▂§〓↓◆◀〼▉◆☲▬▎▎▁▲◢▲■▇▅▲◆▋▍◤§▇◢▲●▉▍▆▁▲▲▆↘▋▍☵§◤▲▲▇█▶↗♩◤〼■☴▬▶★↘▊☳‥█◤◀♥↗▬〼〓▪█▲▲■〓〼‥↙▊▌▅▲♥▏☷♫▬♪‥♬▊▌▋☱▁☳‥☱▋▌▆☴▉☰▎↘♣◐▆§〓◐▅☴▬▍‥♬▊▍☲☴▏◐§☱▏〼‥↙▊█↓◑〓▲▲▲▲▼♠▲▲▲▲▲▲▲▲↖■▌▅▲▼↘▍•↑▃▇◑♪▌▲※");
    }

    /** Emoji 字符集：序列化结果应等于黄金串，且能还原 */
    @Test
    public void emoji_goldenRoundTrip() {
        assertGoldenRoundTrip(new SaSerializerForBase64UseEmoji(),
                "😫😎😴😀😁😗😍😲😀😂😍😣😛😢😹😤😙😗😘😳😌😲😹😳😘😗😑😯😚😶😕😮😋😧😑😥😜😷😐😮😛😖😽😤😙😖😰😮😔😷😥😳😕😗😍😥😜😭😃😇😦😚😃😰😛😕😕😌😀😠😀😄😒😐😀😃😘😖😝😥😒😠😀😂😚😖😑😌😀😀😑😮😘😖😵😥😝😀😀😒😓😆😩😡😝😦😄😯😛😆😅😮😙😲😽😓😝😇😉😩😛😦😜😻😓😀😀😄😜😦😽😬😙😗😐😀😉😔😱😣😛😢😽😤😙😗😘😳😌😲😽😳😘😗😑😯😚😶😕😮😋😷😑😥😜😷😐😯😛😖😽😤😙😖😰😯😔😷😥😳😔😦😽😬😙😓😭😸😜😀😀😀😀😁😈😀😀😀😀😀😀😀😀😧😄😗😐😀😁😮😖😼😨😎😒😸😢😗😀");
    }

    /** 把序列化器挂到 SaManager，校验：类型、黄金串、还原结果、null 入参 */
    private static void assertGoldenRoundTrip(SaSerializerTemplate serializer, String golden) {
        SaManager.setSaSerializerTemplate(serializer);
        SaSerializerTemplate current = SaManager.getSaSerializerTemplate();
        Assertions.assertSame(serializer.getClass(), current.getClass());

        SysUser user = new SysUser(10001, "张三", 18);
        String objectString = current.objectToString(user);
        Assertions.assertEquals(golden, objectString);

        SysUser restored = current.stringToObject(objectString, SysUser.class);
        Assertions.assertEquals(user.toString(), restored.toString());

        Assertions.assertNull(current.objectToString(null));
        Assertions.assertNull(current.stringToObject(null, SysUser.class));
        Assertions.assertNull(current.stringToObject(null));
    }

}
