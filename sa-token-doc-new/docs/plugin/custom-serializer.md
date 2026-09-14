---
title: "Sa-Token 序列化插件扩展包"
keywords: "Sa-Token,sa-token,satoken,Sa-Token文档,序列化插件扩展包,插件"
description: "Sa-Token 序列化扩展（娱乐向）：天干地支等趣味序列化方案，探索 SaTokenDao 序列化边界。"
---

# 序列化插件扩展包
--- 

引入此插件可以为 Sa-Token 提供一些有意思的序列化方案。（娱乐向，不建议上生产，只为探索序列化插件更多边界能力）

:::tabs
== Maven 方式

``` xml 
<!-- Sa-Token 自定义 String 序列化方案合集 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-serializer-features</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

== Gradle 方式

``` gradle
// Sa-Token 自定义 String 序列化方案合集
implementation 'cn.dev33:sa-token-serializer-features:${sa.top.version}'
```

:::



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

<img class="s-w" src="/big-file/doc/plugin/sa-custom-serializer-tiangan.png" alt="自定义序列化：天干地支编码示例" />


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

<img class="s-w" src="/big-file/doc/plugin/sa-custom-serializer-yszqb.png" alt="自定义序列化：三十六字母编码示例" />



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

<img class="s-w" src="/big-file/doc/plugin/sa-custom-serializer-tsfh.png" alt="自定义序列化：特殊符号编码示例" />


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

<img class="s-w" src="/big-file/doc/plugin/sa-custom-serializer-emoji.png" alt="自定义序列化：Emoji 编码示例（输入）" />

<img class="s-w" src="/big-file/doc/plugin/sa-custom-serializer-emoji2.png" alt="自定义序列化：Emoji 编码示例（输出）" />

















