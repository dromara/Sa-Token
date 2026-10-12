.PHONY: help clean compile test package install deploy doc core starter plugin test-module demo-clean all

# 默认目标
help:
	@echo "Sa-Token Makefile 命令列表:"
	@echo ""
	@echo "  make clean          清理所有构建产物"
	@echo "  make compile        编译项目"
	@echo "  make test           运行单元测试"
	@echo "  make package        打包项目 (跳过测试)"
	@echo "  make install        安装到本地仓库 (跳过测试)"
	@echo "  make deploy         发布到中央仓库"
	@echo "  make core           仅编译 sa-token-core"
	@echo "  make starter        仅编译 sa-token-starter"
	@echo "  make plugin         仅编译 sa-token-plugin"
	@echo "  make test-module    仅运行 sa-token-test 单元测试"
	@echo "  make demo-clean     清理所有 demo 模块构建产物"
	@echo "  make doc            预览文档 (需要安装 browser-sync)"
	@echo "  make all            清理 + 编译 + 测试"

# 清理所有构建产物
clean:
	mvn clean

# 编译项目
compile:
	mvn compile

# 运行单元测试
test:
	mvn clean test

# 打包项目 (跳过测试)
package:
	mvn package -DskipTests

# 安装到本地仓库 (跳过测试)
install:
	mvn install -DskipTests

# 发布到中央仓库
deploy:
	mvn deploy

# 仅编译 sa-token-core
core:
	cd sa-token-core && mvn compile

# 仅编译 sa-token-starter
starter:
	cd sa-token-starter && mvn compile

# 仅编译 sa-token-plugin
plugin:
	cd sa-token-plugin && mvn compile

# 仅运行 sa-token-test 单元测试
test-module:
	cd sa-token-test && mvn clean test

# 清理所有 demo 模块构建产物
demo-clean:
	cd sa-token-demo && mvn clean
	cd sa-token-test && mvn clean

# 预览文档 (需要先安装: npm install -g browser-sync)
doc:
	cd sa-token-doc && browser-sync start --server --files ""

# 清理 + 编译 + 测试
all: clean test
