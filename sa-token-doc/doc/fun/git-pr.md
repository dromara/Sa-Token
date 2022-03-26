# 如何更新在线文档
1. 打开要修改的文档页面
2. 滑动右侧页面滑块, 查看页面内容最下方, 评论区上方
3. 找到这一行文字
   
![在线编辑提示](https://oss.dev33.cn/sa-token/doc/git-pr/online_1.png)

4. 点击Gitee或GitHub按钮中的任意一个, 国内用户推荐使用 [Gitee](https://gitee.com) (请先注册登录后再往下浏览)
5. 此时会进入当前页面源码预览页面,找到下方按钮组

![按钮组](https://oss.dev33.cn/sa-token/doc/git-pr/online_2.png)

6. 点击编辑按钮
7. 此时进入待修改页面的源码页面, 按照markdown格式编辑为需要的结果(Ctrl+P可查看最终效果,再次按下可恢复源码界面)
8. 滑动到最下方点击提交审核即可


# 如何提交代码
## 环境安装过程
1. 在本地[下载Git软件](https://pc.qq.com/detail/13/detail_22693.html)并安装
2. 配置用户名和邮件地址(Gitee或GitHub上关联的邮箱)

```
git config --global user.name "这里替换为你在项目中希望展示的昵称"
git config --global user.email 这里替换为你的关联邮箱
// 查看是否配置正确
git config --list  
```

3. 为了让Gitee服务器认可你的身份,需要配置一次SSH Key, 在本地生成密匙对, 公钥上传到Gitee服务器后台
4. 具体方法见[Gitee如何配置SSH](https://gitee.com/help/articles/4181#article-header0), [Github如何配置SSH](https://docs.github.com/cn/github/authenticating-to-github/adding-a-new-ssh-key-to-your-github-account)
5. 最小开发环境安装包括[Java JDK 8+](https://pc.qq.com/detail/0/detail_18360.html),[Maven 最新版](http://maven.apache.org/download.cgi) 和[idea IDE 社区版](https://www.jetbrains.com/zh-cn/idea/download/#section=windows)
6. 在idea 中[配置Java环境](https://www.baidu.com/s?wd=idea%20%E9%85%8D%E7%BD%AEjava%E7%8E%AF%E5%A2%83)和[配置maven环境](https://www.baidu.com/s?wd=idea%20%E9%85%8D%E7%BD%AEmaven%E7%8E%AF%E5%A2%83), 基础部分不再赘述

## 项目下载过程
1. 点击[Gitee](https://gitee.com/dromara/sa-token)或[Github](https://github.com/dromara/sa-token)进入Sa-Token项目主页, 以下以Gitee为例,Github类似(请先注册登录后再往下浏览)
2. 找到页面右上角的按钮组, 点击Forked按钮
   
![按钮组](https://oss.dev33.cn/sa-token/doc/git-pr/code_1.png)

3. 选择个人仓库并点击确认
4. 此时在你的个人仓库中会多了一个Sa-Token项目
5. 在新的Sa-Token项目中, 点击 ![克隆/下载](https://oss.dev33.cn/sa-token/doc/git-pr/code_2.png) 按钮, 点击弹出框里面的复制按钮
6.  在本地某空文件夹下右键选择: git bash here

![git bash](https://oss.dev33.cn/sa-token/doc/git-pr/code_4.png)

![git bash 打开后的图](https://oss.dev33.cn/sa-token/doc/git-pr/code_3.png)

14. 在里面输入如下命令, 按换行后自动下载整个项目

```
git clone 这里替换为复制后的链接
```

## 项目载入过程
1. 下载结束后, 开启 idea, 选择 File->Open... 选中项目下载后的Sa-Token文件夹(Trust Project 相信此项目, 否则不可编辑)
2. 这时项目就是可编辑状态, 修改完代码并测试完成后即可提交

## 项目暂存并提交远程
### 方式一
1. 在idea中打开项目进入Commit选项

![本地暂存](https://oss.dev33.cn/sa-token/doc/git-pr/code_5.png)

2. 勾选需要本地暂存的文件
3. 在同一页面的下方输入提示信息

![提示信息](https://oss.dev33.cn/sa-token/doc/git-pr/code_6.png)

4. 点击Commit按钮暂存到本地, 点击Commit and Push按钮暂存之后提交到远程
### 方式二
1. 除了点击Commit and Push按钮外,还有一个地方可以提交git

![git按钮](https://oss.dev33.cn/sa-token/doc/git-pr/code_7.png)

2. 位置在idea右上方的工具栏里面
3. 指向左下箭头为拉取项目,可以随时更新
4. 打对号为本地暂存
5. 指向右上箭头提交远程
## 私人项目推送到主项目
1. 提交后进入Gitee个人仓库中克隆的Sa-Token项目
2. 找到下图的Pull Request按钮

![工具栏](https://oss.dev33.cn/sa-token/doc/git-pr/code_8.png)

3. 点击提交, 进入如下页面

![提交信息填写页面](https://oss.dev33.cn/sa-token/doc/git-pr/code_9.png 's-width')

4. 在这里,你可以选择要提交的分支,一般都是dev开发分支.可以填写合并信息,其他测试审查之类的可以不填写, 最后点击创建即可完成一次提交.

## 远程项目更新
1. 有时候主项目更新了,之前克隆的项目代码陈旧,如何处理?
2. 在个人仓库的Sa-Token项目主页面中, 找到下图的圆圈

![更新按钮](https://oss.dev33.cn/sa-token/doc/git-pr/code_10.png)

3. 点击右侧圆圈按钮后Gitee会自动同步主项目, 这样就不用向我之前一样,删除项目又重新fork了.

## 为什么在国内推荐Gitee
1. 近期Github下载网速较慢
2. Gitee上中文界面方便操作