
// --------------------- 工具方法 ---------------------

// 打开 loading
loadingIcon = function(msg) {
	layer.closeAll();	// 开始前先把所有弹窗关了
	return layer.msg(msg, {icon: 16, shade: 0.3, time: 1000 * 20, skin: 'ajax-layer-load' });
};

// 隐藏 loading
hideLoadingIcon = function() {
	layer.closeAll();
};


// --------------------- 渲染赞助者名单 ---------------------

// 返回赞助者名单副本
function getCopyDonateList() {
	var arr = [];
	for (var i = 0; i < donateList.length; i++) {
		var item = donateList[i];
		// 时间转时间戳，方便排序 
		item.dateT = new Date(item.date).getTime();
		// 金额补 .0 
		item.moneyS = item.money + '';  
		if(item.moneyS.indexOf('.') == -1) {
			item.moneyS = item.moneyS + '.0';
		}
		arr.push(item);
	}
	return arr;
}
// 返回赞助者名单副本，根据日期倒叙排列
function getCopyDonateListByDateSort() {
	var arr = getCopyDonateList();
	arr.sort(function(a, b){
		var value = b.dateT - a.dateT;
		if(value == 0) {
			value = -1;
		}
		return value;
	})
	return arr;
}
// 返回赞助者名单副本，根据赞助金额倒叙排列
function getCopyDonateListByMoneySort() {
	var arr = getCopyDonateList();
	arr.sort(function(a, b){
		var value = b.money - a.money;
		if(value == 0) {
			value = b.dateT - a.dateT;
		}
		return value;
	})
	return arr;
}
console.log(getCopyDonateListByMoneySort());


// 赞助配置 
var zzCfg = {
	curr: 1,  // 当前页
	size: 15, // 页大小 
	pageCount: 0, // 页总数 
	dataCount: 0, // 数据总数 
	sort: 1,  // 排序方式（1=按照日期倒叙，2=按照金额倒叙）
}

// 将赞助者名单渲染到页面上 
function renderDonateTable() {
	// 先清空旧数据 
	$('.zanzhu-table tbody').empty();
	
	// 拼接 tr 字符串 
	var trArrStr = '';
	var arr = zzCfg.sort == 1 ? getCopyDonateListByDateSort() : getCopyDonateListByMoneySort();
	
	// 按照页参数进行遍历 
	let index = (zzCfg.curr - 1) * zzCfg.size; // 起始索引
	let end = index + zzCfg.size;	// 结束索引 
	if(end > arr.length) {
		end = arr.length;
	}
	zzCfg.pageCount = parseInt(arr.length / zzCfg.size); // 页总数 
	if(arr.length % zzCfg.size != 0) {
		zzCfg.pageCount++;
	}
	zzCfg.dataCount = arr.length; // 数据总数 
	
	// 开始拼接字符串 
	for (let i = index; i < end; i++) {
		// console.log(item);
		let item = arr[i];
		let name = item.name;
		if(item.link) {
			name = '<a href="' + item.link + '" target="_blank">' + name + '</a>'
		}
		var trStr = `
			<tr>
				<td class="zanzhu-name">${name}</td>
				<td class="zanzhu-money">¥ ${item.moneyS}</td>
				<td>${item.msg}</td>
				<td>${item.date}</td>
			</tr>
		`;
		trArrStr += trStr;
	}
	
	// 渲染到 table 里 
	$('.zanzhu-table tbody').html(trArrStr);
	
	// 重置分页信息 
	const pageInfo = `第 ${zzCfg.curr}/${zzCfg.pageCount} 页（共${zzCfg.dataCount}位）`;
	$('.zz-pageInfo').text(pageInfo);
}
// 带动画的渲染 
function renderDonateTable2() {
	// 模拟ajax的延时
	loadingIcon('努力加载中...');
	setTimeout(function() {
		hideLoadingIcon();	// 隐藏掉转圈圈 
		renderDonateTable();
	}, 300);
}
renderDonateTable();

// 上一页
function prevPageRDT(){
	if(zzCfg.curr <= 1) {
		return layer.msg('达咩，不能再往前了');
	}
	zzCfg.curr--;
	renderDonateTable2();
}
// 下一页
function nextPageRDT(){
	if(zzCfg.curr >= zzCfg.pageCount) {
		return layer.msg('嘿，到底了');
	}
	zzCfg.curr++;
	renderDonateTable2();
}

// 切换排序
$('.zanzhu-sort-btn').click(function(){
	// 切换 class
	$('.zz-sort-native').removeClass('zz-sort-native');
	$(this).addClass('zz-sort-native');
	
	// 切换数据 
	zzCfg.curr = 1;  // 重置为第1页 
	zzCfg.sort = parseInt($(this).attr('sort-value'));
	renderDonateTable2();
})


// 读取 sa-token-donate 页数据为 json 
function readDataToJson() {
	var arr = [];
	var trList = $('.zanzhu-box table tbody tr');
	for (let tr of trList) {
		var tdArr = $(tr).find('td');
		var item = {
			name: $(tdArr[0]).text(),
			link: $(tdArr[0]).find('a').attr('href') || '',
			money: parseFloat($(tdArr[1]).text().replaceAll('¥', '')),
			msg: $(tdArr[2]).html(),
			date: $(tdArr[3]).text(),
		};
		arr.push(item);
	}
	return arr;
}
function readDataToJsonStr() {
	var arr = readDataToJson();
	var str = '';
	for (let item of arr) {
		str = JSON.stringify(item) + ',' + str;
	}
	return str;
}

