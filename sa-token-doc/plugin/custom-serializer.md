# 自定义序列化插件
--- 

引入此插件可以为 Sa-Token 提供一些有意思的序列化方案。（娱乐向，不建议上生产）

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 自定义 String 序列化方案合集 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-serializer-features</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 自定义 String 序列化方案合集
implementation 'cn.dev33:sa-token-serializer-features:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


#### 1、SaSerializerForBase64UseTianGan
base64 编码，采用 十大天干、十二地支 等64个中文字符作为元字符集

``` java
// 设置序列化方案: base64 编码，采用 十大天干、十二地支 等64个中文字符作为元字符集
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerForBase64UseTianGan());
}
```

效果图：

![sa-custom-serializer-tiangan.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-custom-serializer-tiangan.png?x-oss-process=style/st 's-w')


#### 2、SaSerializerForBase64UsePeriodicTable
base64 编码，采用 元素周期表 前六十四位作为元字符集

``` java
// 设置序列化方案: base64 编码，采用 元素周期表 前六十四位作为元字符集
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerForBase64UsePeriodicTable());
}
```

效果图：

![sa-custom-serializer-yszqb.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-custom-serializer-yszqb.png?x-oss-process=style/st 's-w')



#### 3、SaSerializerForBase64UseSpecialSymbols
base64 编码，采用64个特殊符号作为元字符集

``` java
// 设置序列化方案: base64 编码，采用64个特殊符号作为元字符集
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerForBase64UseSpecialSymbols());
}
```

效果图：

![sa-custom-serializer-tsfh.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-custom-serializer-tsfh.png?x-oss-process=style/st 's-w')


#### 4、SaSerializerForBase64UseEmoji
base64 编码，采用 64 个 Emoji 小黄脸作为元字符集，无填充字符

``` java
// 设置序列化方案: base64 编码，采用 64 个 Emoji 小黄脸作为元字符集，无填充字符
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerForBase64UseEmoji());
}
```

效果图：

![sa-custom-serializer-emoji.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-custom-serializer-emoji.png?x-oss-process=style/st 's-w')

![sa-custom-serializer-emoji2.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-custom-serializer-emoji2.png?x-oss-process=style/st 's-w')

















