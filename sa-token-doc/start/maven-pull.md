# Maven 依赖一直无法拉取成功？

--- 
方法1、先重启一下试试。

--- 
方法2、可能依赖还没有下载完毕，请看一下编辑器下方是否有正在构建项目的进度条。

--- 
方法3、可能是网络不太稳定，导致本地下载了一些残碎文件，先把这些残碎文件删除了，再重新构建项目试试。

一般本地的文件都在 `C:\Users\你的电脑用户名\.m2\repository\cn\dev33`，打开后，把文件全部删除。注：如果你修改过 Maven jar 下载目录，就按照你修改的来。

--- 
方法4、可能你给你的 Maven 配置了阿里云镜像，而部分 jar 包无法通过阿里云镜像加载成功。

打开你的 Maven setting.xml 文件，看看有没有以下配置：

``` xml
<mirror>
	<id>nexus-aliyun</id>
	<mirrorOf>central</mirrorOf>
	<name>Nexus aliyun</name>
	<url>http://maven.aliyun.com/nexus/content/groups/public</url> 
</mirror>
```

如果有的话，先把它注释掉（注释掉就直连 Maven 中央仓库了），或者修改为其它的镜像，例如腾讯云的：

``` xml
<mirror> 
	<id>tencent</id> 
	<name>tencent maven</name> 
	<url>http://mirrors.cloud.tencent.com/nexus/repository/maven-public/</url>
	<mirrorOf>central</mirrorOf> 
</mirror>
```

然后重启你的代码编辑器，重新构建项目。

--- 

再不行的话，就加群反馈吧。
