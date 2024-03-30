# 自定义 Token 前缀

### 需求场景

在某些系统中，前端提交token时会在前面加个固定的前缀，例如：

``` js
{
	"satoken": "Bearer xxxx-xxxx-xxxx-xxxx"
}
```

此时后端如果不做任何特殊处理，框架将会把`Bearer `视为token的一部分，无法正常读取token信息，导致鉴权失败。

为此，我们需要在yml中添加如下配置：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
	# token前缀
	token-prefix: Bearer
```
<!------------- tab:properties 风格  ------------->
``` properties
# token前缀
sa-token.token-prefix=Bearer
```
<!---------------------------- tabs:end ---------------------------->


此时 Sa-Token 便可在读取 Token 时裁剪掉 `Bearer`，成功获取`xxxx-xxxx-xxxx-xxxx`。


### 注意点

1. Token前缀  与 Token值 之间必须有一个空格。
2. 一旦配置了 Token前缀，则前端提交 `Token` 时，必须带有前缀，否则会导致框架无法读取 Token。
3. 由于`Cookie`中无法存储空格字符，也就意味配置 Token 前缀后，Cookie 鉴权方式将会失效，此时只能将 Token 提交到`header`里进行传输。

