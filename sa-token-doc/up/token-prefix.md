# Token 提交前缀

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
	# 指定 token 提交时的前缀
	token-prefix: Bearer
```
<!------------- tab:properties 风格  ------------->
``` properties
# token前缀
sa-token.token-prefix=Bearer
```
<!---------------------------- tabs:end ---------------------------->


此时 Sa-Token 便可在读取 Token 时裁剪掉 `Bearer`，成功获取`xxxx-xxxx-xxxx-xxxx`。

注：**Token前缀  与 Token值 之间必须有一个空格**


### Cookie 模式自动填充前缀

由于`Cookie`中无法存储空格字符，所以配置 Token 前缀后，Cookie 模式将会失效，无法成功提交带有前缀的 token。

如果需要在这种场景下仍然使用 Cookie 模式验证 token，可以使用 `cookieAutoFillPrefix` 配置项打开 Cookie 模式自动填充前缀：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
	# 指定 Cookie 模式下自动填充 token 提交前缀
	cookie-auto-fill-prefix: true
```
<!------------- tab:properties 风格  ------------->
``` properties
# 指定 Cookie 模式下自动填充 token 提交前缀
sa-token.cookie-auto-fill-prefix=true
```
<!---------------------------- tabs:end ---------------------------->


