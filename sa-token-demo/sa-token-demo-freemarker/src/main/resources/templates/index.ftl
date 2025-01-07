<!DOCTYPE html>
<html lang="zh">
	<head>
		<title>Sa-Token 集成 Freemarker 标签方言</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	</head>
	<body>
		<div class="view-box" style="padding: 30px;">
			<h2>Sa-Token 集成 Freemarker 标签方言 —— 测试页面</h2>
			<p>当前是否登录：<#if stp.isLogin()>是<#else>否</#if></p>
			<p>
				<a href="login" target="_blank">登录</a>
				<a href="logout" target="_blank">注销</a>
			</p>

			<p>登录之后才能显示：<@sa.login>value</@sa.login></p>
			<p>不登录才能显示：<@sa.notLogin>value</@sa.notLogin></p>

			<p>具有角色 admin 才能显示：<@sa.hasRole value="admin">value</@sa.hasRole></p>
			<p>同时具备多个角色才能显示：<@sa.hasRoleAnd value="admin, ceo, cto">value</@sa.hasRoleAnd></p>
			<p>只要具有其中一个角色就能显示：<@sa.hasRoleOr value="admin, ceo, cto">value</@sa.hasRoleOr></p>
			<p>不具有角色 admin 才能显示：<@sa.notRole value="admin">value</@sa.notRole></p>

			<p>具有权限 user-add 才能显示：<@sa.hasPermission value="user-add">value</@sa.hasPermission></p>
			<p>同时具备多个权限才能显示：<@sa.hasPermissionAnd value="user-add, user-delete, user-get">value</@sa.hasPermissionAnd></p>
			<p>只要具有其中一个权限就能显示：<@sa.hasPermissionOr value="user-add, user-delete, user-get">value</@sa.hasPermissionOr></p>
			<p>不具有权限 user-add 才能显示：<@sa.notPermission value="user-add">value</@sa.notPermission></p>

			<p>
				从SaSession中取值：
				<#if stp.isLogin()>
					<span>${stp.getSession().get('name')}</span>
				</#if>
			</p>

		</div>
	</body>
</html>
