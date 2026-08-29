/*

	国际化，网页自动翻译。
	作者：管雷鸣
	开原仓库：https://github.com/xnx3/translate
 */ 
if(typeof(translate) == 'object' && typeof(translate.version) == 'string'){
	throw new Error('translate.js 已经加载过一次了，当前是重复加载，避免你的翻译出现异常，已帮你拦截此次加载。本信息只是给你一个提示，你可以检查一下你的项目中是否出现了重复引入 translate.js ，当然，这个异常并不会影响到你的具体使用，它已经自动帮你处理拦截了这个异常，只不过提示出来是让你知道，你的代码里出现了重复引入的情况。');
}
var translate = {
	/**
	 * 当前的版本
	 * 由 npm 脚本自动更新，无需手动修改
	 * 格式：major.minor.patch.date
	 */
	// AUTO_VERSION_START
	version: '4.1.0.20260526',
	
	/*js translate.config start*/
	/*
		用于当前整个 translate.js 配置参数（整形、布尔值、字符串等参数，不包括function参数） 的导出及导入。
		v3.18.99.20251205 增加，主要用于自动注入iframe中的页面实现翻译而作。
		主要用到里面的 get、set 方法。
	*/
	config:{
		//这是一个 Bean 类，避免传统json方式再有写错某个参数
		data: class{
			//只翻译指定的元素 https://translate.zvo.cn/4063.html  translate.setDocuments(documents);  对应的数据 translate.documents
			documents = [];
			language = {
				//设定是否自动出现 select 切换语言， https://translate.zvo.cn/4056.html
				select:{
					// 对应的数据 translate.selectLanguageTag.show
					show: true,
					// 对应的数据 translate.selectLanguageTag.languages = 'english,chinese_simplified,korean';
					languages:'',
					// 将切换语言的选择框显示到哪个id元素上。 对应的数据 translate.selectLanguageTag.documentId
					documentId: 'translate'
				},
				//设置本地语种（当前网页的语种） https://translate.zvo.cn/4066.html translate.language.setLocal('chinese_simplified');  对应的数据 translate.language.local
				local:'', 
				//设置默认翻译为的语种 https://translate.zvo.cn/4071.html translate.language.setDefaultTo('english');  对应的数据 translate.language.defaultTo
				defaultTo:'',
				//自动切换为用户所使用的语种 https://translate.zvo.cn/4065.html  translate.setAutoDiscriminateLocalLanguage();  对应 translate.autoDiscriminateLocalLanguage 的值
				autoDiscriminateLocalLanguage: false,
				//设置只对指定语种进行翻译 https://translate.zvo.cn/4085.html  translate.language.translateLanguagesRange = ['chinese_simplified','english']; 对应的数据 translate.language.translateLanguagesRange
				range:[],
				//根据URL传参控制以何种语种显示  https://translate.zvo.cn/4075.html  
				urlParamControl: {
					// translate.language.setUrlParamControl(); 默认是false， 对应的数据 translate.language.setUrlParamControl_use
					use: false,
					// translate.language.setUrlParamControl('lang'); 默认是 language ，对应的数据 translate.language.setUrlParamControl_name
					name: 'language'
				},
				//本地语种也进行强制翻译 https://translate.zvo.cn/289574.html 对应的数据 translate.language.translateLocal
				translateLocal: false
			};
			//对网页中图片进行翻译 https://translate.zvo.cn/4055.html  translate.images.add(...)  对应的数据 translate.images.queues
			images = [];
			//自定义翻译术语 https://translate.zvo.cn/4070.html translate.nomenclature.append(from, to, properties);  对应的数据  translate.nomenclature.data
			nomenclature = [];
			listener = {
				//监控页面动态渲染的文本进行自动翻译 https://translate.zvo.cn/4067.html translate.listener.start(); 如果为true，则是启用。 对应 translate.listener.use 的值
				use:false,
			};
			ignore = {
				// 翻译时忽略指定的文字不翻译 https://translate.zvo.cn/283381.html  translate.ignore.text.push('你好');  对应的数据 translate.ignore.text
				text:[],
				//通过正则的方式忽略某些文字不翻译 https://translate.zvo.cn/283381.html translate.ignore.setTextRegexs([/请求/g, /[u4a01-u4a05]+/g]);  对应的数据 translate.ignore.textRegex
				textRegex:[],
				// 翻译时忽略指定的id https://translate.zvo.cn/4062.html translate.ignore.id.push('test'); 对应的数据 translate.ignore.id
				id:[],
				//翻译时忽略指定的class属性 https://translate.zvo.cn/4061.html translate.ignore.class.push('test'); 
				//class:[],
				class:{
					//对应的数据 translate.ignore.class.data
					data:[],
					//对应的数据 translate.ignore.class.conditionFunction
					conditionFunction:{}
				},
				//翻译时忽略指定的tag标签 https://translate.zvo.cn/4060.html translate.ignore.tag.push('span');  对应的数据 translate.ignore.tag
				tag:[],

			};
			//设置使用的翻译服务 translate.service.use  https://translate.zvo.cn/4081.html  translate.service.use('client.edge');   对应的数据  translate.service.name , 默认则是 translate.service
			service = 'translate.service';
			//元素的内容整体翻译能力配置  https://translate.zvo.cn/4078.html
			whole = {
				//是否开启对整个html页面的整体翻译，也就是整个页面上所有存在的能被翻译的全部会采用整体翻译的方式。默认是 false不开启		对应的数据 translate.whole.isEnableAll
				enableAll:false,
				/*
					以下三个，也就是  class tag id 分别存储加入的值。
					比如 translate.whole.tag.push('h3');
					对应的数据 translate.whole.tag\class\id
				*/
				class:[],
				tag:[],
				id:[],
				// whole 行内上下文分段翻译开关。默认关闭，避免旧 translate.json 接口无法处理数组分段请求。
				context:{
					// 对应的数据 translate.whole.context.is_use
					use:false
				},
			};
			//鼠标划词翻译 https://translate.zvo.cn/4072.html
			selectionTranslate = {
				//是否启用，默认是false，不启用。如果启用，则是 translate.selectionTranslate.start();   对应的数据  translate.selectionTranslate.use
				use:false
			};
			request = {
				api:{
					// 指定翻译服务接口 https://translate.zvo.cn/4068.html   translate.request.setHost(['https://api.translate.zvo.cn/','https://api2.translate.zvo.cn/']);
					// 这里数据同步的是 translate.request.api.host
					host:[],
					// 获取支持的语种列表接口， 可以设置两种形态。
					// 可以设置为 language.json 具体请求api的文件名
					// 另外它还可以设置为 translate.request.api.language = [{id: "chinese_simplified", name: "简体中文"},{id: "korean", name: "한국어"}]; 这种形态，不需要通过联网即可获取切换的语言。
					language:'language.json',
					translate:'translate.json', //翻译接口
					ip:'ip.json', //根据用户当前ip获取其所在地的语种
					connectTest:'connectTest.json',	//用于 translate.js 多节点翻译自动检测网络连通情况
					init:'init.json', //获取最新版本号，跟当前版本进行比对，用于提醒版本升级等使用
				},
				//网页ajax请求触发自动翻译  https://translate.zvo.cn/4086.html
				listener:{
					// 用户的代码里是否启用了 translate.request.listener.start() ，true：启用  对应的数据 translate.request.listener.use
					use:false,
					// 进行翻译时，延迟翻译执行的时间 当ajax请求结束后，延迟这里设置的时间，然后自动触发 translate.execute() 执行。 对应的数据 translate.request.listener.delayExecuteTime
					delayExecuteTime: 200,
					//两次触发的最小间隔时间，单位是毫秒，这里默认是800毫秒。最小填写时间为 200毫秒。 对应的数据 translate.request.listener.minIntervalTime
					minIntervalTime: 800,
				},
				// 网络请求自定义附加参数-追加请求参数， https://translate.zvo.cn/471711.html  对应的数据 translate.request.appendParams
				appendParams: {},
				// 网络请求自定义附加参数-追加 header 请求头参数， https://translate.zvo.cn/471711.html  对应的数据 translate.request.appendHeaders
				appendHeaders: {},
				// translate.json 的 SSE 流式响应能力，默认关闭。开启后只影响翻译接口请求，并且会在不支持时自动降级回原 JSON 请求。
				sse:{
					// 对应的数据 translate.request.sse.use
					use:false
				},
				// 翻译排队执行  https://translate.zvo.cn/479742.html  对应的数据 translate.waitingExecute.use
				waitingExecute: true,
			};
			element = {
				//增加对指定标签的属性进行翻译  https://translate.zvo.cn/231504.html  translate.element.tagAttribute
				//当前忽略 condition 的function 参数
				tagAttribute: {}
			};
			//翻译中的遮罩层 https://translate.zvo.cn/407105.html
			progress = {
				api:{
					//启用翻译中的遮罩层， 默认不使用，translate.progress.api.startUITip(); 可以设置为启用，对应的数据 translate.progress.api.use
					use: false,
				},
				// 对应 translate.progress.style 的数据
				style:'',
			};
			//网络请求数据拦截并翻译  https://translate.zvo.cn/479724.html
			network = {
				// 对应的数据 translate.network.rules
				rules:[],
				// 对应的数据 translate.network.isUse
				use: false
			};
			visual = {
				//网页打开时自动隐藏文字，翻译完成后显示译文 https://translate.zvo.cn/549731.html 对应的数据 translate.visual.webPageLoadTranslateBeforeHiddenText_use
				webPageLoadTranslateBeforeHiddenText: {
					use: false,
				}
			};
		},


		//获取当前 translate.js 所设置的数据 （排除设置的 function）
		get: function(){

			var data = new translate.config.data();

			data.documents = translate.documents;
			data.language.select.show = translate.selectLanguageTag.show;
			data.language.select.languages = translate.selectLanguageTag.languages;
			data.language.select.documentId = translate.selectLanguageTag.documentId;
			data.language.local = translate.language.local;
			data.language.defaultTo = translate.language.defaultTo;
			data.language.autoDiscriminateLocalLanguage = translate.autoDiscriminateLocalLanguage;
			data.language.range = translate.language.translateLanguagesRange;
			data.language.urlParamControl.use = translate.language.setUrlParamControl_use;
			data.language.urlParamControl.name = translate.language.setUrlParamControl_name;
			data.language.translateLocal = translate.language.translateLocal;
			data.images = translate.images.queues;
			data.nomenclature = translate.nomenclature.data;
			data.listener.use = translate.listener.use;
			data.ignore.text = translate.ignore.text;
			data.ignore.textRegex = translate.ignore.textRegex;
			data.ignore.id = translate.ignore.id;
			data.ignore.class.data = translate.ignore.class.data;
			data.ignore.class.conditionFunction = translate.ignore.class.conditionFunction;
			data.ignore.tag = translate.ignore.tag;
			data.service = translate.service.name;
			data.whole.enableAll = translate.whole.isEnableAll;
			data.whole.class = translate.whole.class;
			data.whole.tag = translate.whole.tag;
			data.whole.id = translate.whole.id;
			data.whole.context.use = translate.whole.context.isUse();
			data.selectionTranslate.use = translate.selectionTranslate.use;
			data.request.api.host = translate.request.api.host;
			data.request.api.language = translate.request.api.language;
			data.request.api.translate = translate.request.api.translate;
			data.request.api.ip = translate.request.api.ip;
			data.request.api.connectTest = translate.request.api.connectTest;
			data.request.api.init = translate.request.api.init;
			data.request.listener.use = translate.request.listener.use;
			data.request.listener.delayExecuteTime = translate.request.listener.delayExecuteTime;
			data.request.listener.minIntervalTime = translate.request.listener.minIntervalTime;
			data.request.appendParams = translate.request.appendParams;
			data.request.appendHeaders = translate.request.appendHeaders;
			data.request.sse.use = translate.request.sse.use;
			data.request.waitingExecute = translate.waitingExecute.use;
			data.element.tagAttribute = translate.element.tagAttribute;
			data.progress.api.use = translate.progress.api.use;
			data.progress.style = translate.progress.style;
			data.network.rules = translate.network.rules;
			data.network.use = translate.network.isUse;
			data.visual.webPageLoadTranslateBeforeHiddenText.use = translate.visual.webPageLoadTranslateBeforeHiddenText_use;
			
			return data;
		},

		/*
			设置数据，传入 Config.data 格式的数据， 设置到当前 translate.js 中
			不想设置的项可以不传入。
		*/
		set: function(data){
			//console.log(data);
			if(typeof(data) !== 'object' || data === null){
				data = {};
			}

			var language = (typeof(data.language) === 'object' && data.language !== null) ? data.language : {};
			var languageSelect = (typeof(language.select) === 'object' && language.select !== null) ? language.select : {};
			var languageUrlParamControl = (typeof(language.urlParamControl) === 'object' && language.urlParamControl !== null) ? language.urlParamControl : {};
			var listener = (typeof(data.listener) === 'object' && data.listener !== null) ? data.listener : {};
			var ignore = (typeof(data.ignore) === 'object' && data.ignore !== null) ? data.ignore : {};
			var whole = (typeof(data.whole) === 'object' && data.whole !== null) ? data.whole : {};
			var wholeContext = (typeof(whole.context) === 'object' && whole.context !== null) ? whole.context : {};
			var selectionTranslate = (typeof(data.selectionTranslate) === 'object' && data.selectionTranslate !== null) ? data.selectionTranslate : {};
			var request = (typeof(data.request) === 'object' && data.request !== null) ? data.request : {};
			var requestApi = (typeof(request.api) === 'object' && request.api !== null) ? request.api : {};
			var requestListener = (typeof(request.listener) === 'object' && request.listener !== null) ? request.listener : {};
			var requestSse = (typeof(request.sse) === 'object' && request.sse !== null) ? request.sse : {};
			var element = (typeof(data.element) === 'object' && data.element !== null) ? data.element : {};
			var progress = (typeof(data.progress) === 'object' && data.progress !== null) ? data.progress : {};
			var progressApi = (typeof(progress.api) === 'object' && progress.api !== null) ? progress.api : {};
			var network = (typeof(data.network) === 'object' && data.network !== null) ? data.network : {};
			var visual = (typeof(data.visual) === 'object' && data.visual !== null) ? data.visual : {};
			var visualWebPageLoadTranslateBeforeHiddenText = (typeof(visual.webPageLoadTranslateBeforeHiddenText) === 'object' && visual.webPageLoadTranslateBeforeHiddenText !== null) ? visual.webPageLoadTranslateBeforeHiddenText : {};

			if(typeof(data.documents) === 'object'){
				translate.setDocuments(data.documents);
			}
			if(typeof(languageSelect.show) === 'boolean'){
				translate.selectLanguageTag.show = languageSelect.show;
			}
			if(typeof(languageSelect.languages) === 'string' && languageSelect.languages.trim().length>0){
				translate.selectLanguageTag.languages = languageSelect.languages;
			}
			if(typeof(languageSelect.documentId) === 'string' && languageSelect.documentId.trim().length>0){
				translate.selectLanguageTag.documentId = languageSelect.documentId;
			}
			if(typeof(language.local) === 'string' && language.local.trim().length>0){
				translate.language.setLocal(language.local);
			}
			if(typeof(language.defaultTo) === 'string' && language.defaultTo.trim().length>0){
				translate.language.setDefaultTo(language.defaultTo);
			}
			if(typeof(language.autoDiscriminateLocalLanguage) === 'boolean' && language.autoDiscriminateLocalLanguage === true){
				translate.setAutoDiscriminateLocalLanguage();
			}
			if(language.range != null && typeof(language.range) === 'object' && language.range.length > 0){
				translate.language.translateLanguagesRange = language.range;
			}
			if(typeof(languageUrlParamControl.use) === 'boolean'){
				translate.language.setUrlParamControl_use = languageUrlParamControl.use;
			}
			if(typeof(languageUrlParamControl.name) === 'string' && languageUrlParamControl.name.trim().toLowerCase() !== 'language'){
				translate.language.setUrlParamControl(languageUrlParamControl.name);
			}
			if(typeof(language.translateLocal) === 'boolean'){
				translate.language.translateLocal = language.translateLocal;
			}
			if(data.images != null && typeof(data.images) === 'object'){
				translate.images.queues = data.images;
			}
			if(data.nomenclature != null && typeof(data.nomenclature) === 'object'){
				translate.nomenclature.data = data.nomenclature;
			}
			if(typeof(listener.use) === 'boolean'){
				translate.listener.use = listener.use;
			}
			if(ignore.text != null && typeof(ignore.text) === 'object'){
				translate.ignore.text = ignore.text;
			}
			if(ignore.textRegex != null && typeof(ignore.textRegex) === 'object'){
				translate.ignore.textRegex = ignore.textRegex;
			}
			if(ignore.id != null && typeof(ignore.id) === 'object'){
				translate.ignore.id = ignore.id;
			}
			if(ignore.class != null && typeof(ignore.class) === 'object'){
				translate.ignore.class = ignore.class;
			}
			if(ignore.tag != null && typeof(ignore.tag) === 'object'){
				translate.ignore.tag = ignore.tag;
			}
			if(typeof(data.service) === 'string' && data.service.trim().length > 0){
				translate.service.name = data.service;
			}
			if(typeof(whole.enableAll) === 'boolean'){
				translate.whole.isEnableAll = whole.enableAll;
			}
			if(whole.class != null && typeof(whole.class) === 'object'){
				translate.whole.class = whole.class;
			}
			if(whole.tag != null && typeof(whole.tag) === 'object'){
				translate.whole.tag = whole.tag;
			}
			if(whole.id != null && typeof(whole.id) === 'object'){
				translate.whole.id = whole.id;
			}
			if(typeof(wholeContext.use) === 'boolean'){
				translate.whole.context.is_use = wholeContext.use;
			}
			if(typeof(selectionTranslate.use) === 'boolean' && selectionTranslate.use === true){
				if(translate.selectionTranslate.use === false){ //没有启动，才会启动
					translate.selectionTranslate.start();
				}
			}
			if(requestApi.host != null && typeof(requestApi.host) === 'object'){
				translate.request.api.host = requestApi.host;
			}
			if(typeof(requestApi.language) === 'string'){
				translate.request.api.language = requestApi.language;
			}
			if(typeof(requestApi.ip) === 'string'){
				translate.request.api.ip = requestApi.ip;
			}
			if(typeof(requestApi.connectTest) === 'string'){
				translate.request.api.connectTest = requestApi.connectTest;
			}
			if(typeof(requestApi.init) === 'string'){
				translate.request.api.init = requestApi.init;
			}
			if(typeof(requestListener.use) === 'boolean'){
				translate.request.listener.use = requestListener.use;
			}
			if(typeof(requestListener.delayExecuteTime) === 'number'){
				translate.request.listener.delayExecuteTime = requestListener.delayExecuteTime;
			}
			if(typeof(requestListener.minIntervalTime) === 'number'){
				translate.request.listener.minIntervalTime = requestListener.minIntervalTime;
			}
			if(request.appendParams != null && typeof(request.appendParams) === 'object'){
				translate.request.appendParams = request.appendParams;
			}
			if(request.appendHeaders != null && typeof(request.appendHeaders) === 'object'){
				translate.request.appendHeaders = request.appendHeaders;
			}
			if(typeof(requestSse.use) === 'boolean'){
				translate.request.sse.use = requestSse.use;
			}
			if(typeof(request.waitingExecute) === 'boolean'){
				translate.waitingExecute.use = request.waitingExecute;
			}
			if(element.tagAttribute != null && typeof(element.tagAttribute) === 'object'){
				translate.element.tagAttribute = element.tagAttribute;
			}
			if(typeof(progressApi.use) === 'boolean' && progressApi.use === true){
				if(translate.progress.api.use === false){ //没有启动，才会启动
					translate.progress.api.startUITip();
				}
			}
			if(typeof(progress.style) === 'string'){
				translate.progress.style = progress.style;
			}
			if(network.rules != null && typeof(network.rules) === 'object'){
				translate.network.rules = network.rules;
			}
			if(typeof(network.use) === 'boolean' && network.use === true){
				if(translate.network.isUse === false){ //没有启动，才会启动
					translate.network.use();
				}
			}
			if(typeof(visualWebPageLoadTranslateBeforeHiddenText.use) === 'boolean' && visualWebPageLoadTranslateBeforeHiddenText.use === true){
				if(translate.visual.webPageLoadTranslateBeforeHiddenText_use === false){ //没有启动，才会启动
					translate.visual.webPageLoadTranslateBeforeHiddenText();
				}
			}
		}
	},
	/*js translate.config end*/


	// AUTO_VERSION_END
	/*
		当前使用的版本，默认使用v2. 可使用 setUseVersion2(); 
		来设置使用v2 ，已废弃，主要是区分是否是v1版本来着，v2跟v3版本是同样的使用方式
	*/
	useVersion:'v2',
	/*js translate.setUseVersion2 start*/
	setUseVersion2:function(){
		translate.useVersion = 'v2';
		translate.log('提示：自 v2.10 之后的版本默认就是使用V2版本（当前版本为:'+translate.version+'）， translate.setUseVersion2() 可以不用再加这一行了。当然加了也无所谓，只是加了跟不加是完全一样的。');
	},
	/*js translate.setUseVersion2 end*/
	/*
	 * 翻译的对象，也就是 new google.translate.TranslateElement(...)
	 * 已废弃，v1使用的
	 */
	translate:null,
	
	/*js translate.includedLanguages end*/
	/*
	 * 支持哪些语言切换，包括：de,hi,lt,hr,lv,ht,hu,zh-CN,hy,uk,mg,id,ur,mk,ml,mn,af,mr,uz,ms,el,mt,is,it,my,es,et,eu,ar,pt-PT,ja,ne,az,fa,ro,nl,en-GB,no,be,fi,ru,bg,fr,bs,sd,se,si,sk,sl,ga,sn,so,gd,ca,sq,sr,kk,st,km,kn,sv,ko,sw,gl,zh-TW,pt-BR,co,ta,gu,ky,cs,pa,te,tg,th,la,cy,pl,da,tr
	 * 已废弃，请使用 translate.selectLanguageTag.languages 
	 */
	includedLanguages:'zh-CN,zh-TW,en',
	/*js translate.includedLanguages end*/

	/*js translate.resourcesUrl start*/
	/*
	 * 资源文件url的路径
	 * 已废弃，v1使用的
 	 */
	resourcesUrl:'//res.zvo.cn/translate',
	/*js translate.resourcesUrl end*/

	/*js translate.log start*/
	log: function(obj){
		console.log(obj);
	},
	/*js translate.log end*/

	/**
	 * 默认出现的选择语言的 select 选择框，可以通过这个选择切换语言。
	 */
	selectLanguageTag:{
		/*
			v3.1 增加，将 select切换语言的选择框赋予哪个id，这里是具体的id的名字。
			如果这个id不存在，会创建这个id的元素
		*/
		documentId:'translate',
		/* 是否显示 select选择语言的选择框，true显示； false不显示。默认为true */
		show:true,
		/* 
			支持哪些语言切换
			v1.x 版本包括：de,hi,lt,hr,lv,ht,hu,zh-CN,hy,uk,mg,id,ur,mk,ml,mn,af,mr,uz,ms,el,mt,is,it,my,es,et,eu,ar,pt-PT,ja,ne,az,fa,ro,nl,en-GB,no,be,fi,ru,bg,fr,bs,sd,se,si,sk,sl,ga,sn,so,gd,ca,sq,sr,kk,st,km,kn,sv,ko,sw,gl,zh-TW,pt-BR,co,ta,gu,ky,cs,pa,te,tg,th,la,cy,pl,da,tr 
			v2.x 版本根据后端翻译服务不同，支持的语言也不同。具体支持哪些，可通过 http://api.translate.zvo.cn/doc/language.json.html 获取 （如果您私有部署的，将请求域名换为您自己私有部署的域名）
		*/
		languages:'',
		alreadyRender:false, //当前是否已渲染过了 true为是 v2.2增加

		changeLanguageBeforeLoadOfflineFile: function(path){

		},

		selectOnChange:function(event){
			var language = event.target.value;
			translate.changeLanguage(language);
		},
		//重新绘制 select 语种下拉选择。比如进行二次开发过translate.js，手动进行了设置 translate.to ，但是手动改动后的，在select语种选择框中并不会自动进行改变，这是就需要手动重新绘制一下 select语种选择的下拉选择框
		refreshRender:function(){
			// 获取元素
			let element = document.getElementById(translate.selectLanguageTag.documentId+"SelectLanguage");

			// 删除元素
			if (element) {
				element.parentNode.removeChild(element);
			}

			//设置为未 render 状态，允许进行 render
			translate.selectLanguageTag.alreadyRender = false;	

			translate.selectLanguageTag.render();
		},
		/*
			自定义语种 translate.selectLanguageTag.languages 的处理，进行按顺序筛选出来
	
			@param languageList 当前支持的所有语种列表，传入格式如：
			[
				{id: 'english', name: 'English', serviceId: 'en'}, 
				{id: 'korean', name: '한국어', serviceId: 'ko'},
				...
			]

			返回值是将当前翻译通道所支持的语种进行按顺序筛选完后的结果返回。
			比如 
			translate.selectLanguageTag.languages = 'english,chinese_simplified,korean';
			那么这里返回的便是

			[
				{id: 'english', name: 'English', serviceId: 'en'}, 
				{id: 'chinese_simplified', name: '简体中文', serviceId: 'zh-CHS'},
				{id: 'korean', name: '한국어', serviceId: 'ko'}
			]
	
			如果 translate.selectLanguageTag.languages 未设置，那么这里将返回当前支持的所有语种
		*/
		customLanguagesHandle:function(languageList){
			if(translate.selectLanguageTag.languages.length > 0){
				//设置了自定义显示的语言，需要重新根据自定义的语言进行过滤，同时顺序也要保持跟它一致

				//都转小写判断
				var divLanguages = translate.selectLanguageTag.languages.toLowerCase();
				var divArray = divLanguages.split(',');
				
				//将支持的语种 languageList 转化为 map 形态
				if(typeof(translate.selectLanguageTag.supportLanguageMap) == 'undefined'){
					translate.selectLanguageTag.supportLanguageMap = new Map();
					for(var si = 0; si<languageList.length; si++){
						if(languageList[si] != null && typeof(languageList[si].id) === 'string'){
							translate.selectLanguageTag.supportLanguageMap.set(languageList[si].id, languageList[si]);
						}
					}
					//console.log(translate.selectLanguageTag.supportLanguageMap)
				}


				//重新组合要显示的语种
				var newLangs = [];
				for(var i = 0; i<divArray.length; i++){
					if(divArray[i].length > 0 && translate.selectLanguageTag.supportLanguageMap.get(divArray[i]) != null){
						newLangs.push(translate.selectLanguageTag.supportLanguageMap.get(divArray[i]));
					}
				}
				return newLangs;
			}

			return languageList;
		},

		/*
			自定义切换语言的样式渲染 v3.2.4 增加
			
		*/
		customUI:function(languageList){
			//select的onchange事件
			var onchange = function(event){ translate.selectLanguageTag.selectOnChange(event); }
			
			//创建 select 标签
			var selectLanguage = document.createElement("select"); 
			selectLanguage.id = translate.selectLanguageTag.documentId+'SelectLanguage';
			selectLanguage.className = translate.selectLanguageTag.documentId+'SelectLanguage';
			var to = translate.language.getCurrent();


			for(var i = 0; i<languageList.length; i++){
				if(languageList[i] == null || typeof(languageList[i].id) !== 'string' || typeof(languageList[i].name) !== 'string'){
					continue;
				}
				var option = document.createElement("option"); 
			    option.setAttribute("value",languageList[i].id);

			    
				/*判断默认要选中哪个语言*/

			    if(to != null && typeof(to) != 'undefined' && to.length > 0){
					//设置了目标语言，那就进行判断显示目标语言
					if(to == languageList[i].id){
						option.setAttribute("selected",'selected');
					}
			    }else{
					//没设置目标语言，那默认选中当前本地的语种
					if(languageList[i].id == translate.language.getLocal()){
						option.setAttribute("selected",'selected');
					}
				}
				
			    option.appendChild(document.createTextNode(languageList[i].name)); 
			    selectLanguage.appendChild(option);
			}
			//增加 onchange 事件
			if(window.addEventListener){ // Mozilla, Netscape, Firefox 
				selectLanguage.addEventListener('change', onchange,false); 
			}else{ // IE 
				selectLanguage.attachEvent('onchange',onchange); 
			} 

			//将select加入进网页显示
			document.getElementById(translate.selectLanguageTag.documentId).appendChild(selectLanguage);
				
		},
		render:function(){ //v2增加
			if(translate.selectLanguageTag.alreadyRender){
				return;
			}
			translate.selectLanguageTag.alreadyRender = true;
			
			//判断如果不显示select选择语言，直接就隐藏掉
			if(!translate.selectLanguageTag.show){
				return;
			}
			
			//判断translate 的id是否存在，不存在就创建一个
			if(document.getElementById(translate.selectLanguageTag.documentId) == null){
				var findBody = document.getElementsByTagName('body');
				if(findBody.length == 0){
					translate.log('body tag not find, translate.selectLanguageTag.render() is not show Select Language');
					return;
				}
				var body_trans = findBody[0];
				var div = document.createElement("div");  //创建一个script标签
				div.id=translate.selectLanguageTag.documentId;
				body_trans.appendChild(div);
			}else{
				//存在，那么判断一下 select是否存在，要是存在就不重复创建了
				if(document.getElementById(translate.selectLanguageTag.documentId+'SelectLanguage') != null){
					//select存在了，就不重复创建了
					return;
				}
			}

			//从服务器加载支持的语言库
			if(typeof(translate.request.api.language) == 'string' && translate.request.api.language.length > 0){
				//从接口加载语种
				translate.request.post(translate.request.api.language, {}, function(responseData, requestData){
					if(responseData.result == 0){
						translate.log('load language list error : '+responseData.info);
						return;
					}
					//console.log(data.list);
					translate.request.api.language = responseData.list; //进行缓存，下一次切换语言渲染的时候直接从缓存取，就不用在通过网络加载了
					translate.selectLanguageTag.customUI(translate.selectLanguageTag.customLanguagesHandle(responseData.list));
				}, null);
			}else if(translate.request.api.language != null && typeof(translate.request.api.language) == 'object'){
				//无网络环境下，自定义显示语种
				translate.selectLanguageTag.customUI(translate.selectLanguageTag.customLanguagesHandle(translate.request.api.language));
			}
		}
	},
	
	/*
	 * 当前本地语言
	 * 已废弃，v1使用的
	 */
	//localLanguage:'zh-CN',
	/*js translate.localLanguage start*/
	localLanguage:'zh-CN',
	/*js translate.localLanguage end*/
	
	/*js translate.googleTranslateElementInit start*/
	/**
	 * google翻译执行的
	 * 已废弃，v1使用的
	 */
	googleTranslateElementInit:function(){
		var selectId = '';
		if(document.getElementById('translate') != null){	// && document.getElementById('translate').innerHTML.indexOf('translateSelectLanguage') > 0
			//已经创建过了,存在
			selectId = 'translate';
		}
		
		translate.translate = new google.translate.TranslateElement(
			{
				//这参数没用，请忽略
				pageLanguage: 'zh-CN',
				//一共80种语言选择，这个是你需要翻译的语言，比如你只需要翻译成越南和英语，这里就只写en,vi
				//includedLanguages: 'de,hi,lt,hr,lv,ht,hu,zh-CN,hy,uk,mg,id,ur,mk,ml,mn,af,mr,uz,ms,el,mt,is,it,my,es,et,eu,ar,pt-PT,ja,ne,az,fa,ro,nl,en-GB,no,be,fi,ru,bg,fr,bs,sd,se,si,sk,sl,ga,sn,so,gd,ca,sq,sr,kk,st,km,kn,sv,ko,sw,gl,zh-TW,pt-BR,co,ta,gu,ky,cs,pa,te,tg,th,la,cy,pl,da,tr',
	            includedLanguages: translate.selectLanguageTag.languages,
				//选择语言的样式，这个是面板，还有下拉框的样式，具体的记不到了，找不到api~~  
				layout: 0,
				//自动显示翻译横幅，就是翻译后顶部出现的那个，有点丑，设置这个属性不起作用的话，请看文章底部的其他方法
				//autoDisplay: false, 
				//disableAutoTranslation:false,
				//还有些其他参数，由于原插件不再维护，找不到详细api了，将就了，实在不行直接上dom操作
			}, 
			selectId //触发按钮的id
		);
	},
	/*js translate.googleTranslateElementInit end*/
	
	/**
	 * 初始化，如加载js、css资源
	 * 已废弃，v1使用的
	 */
	/* v2.11.11.20240124 彻底注释掉，有新的init方法替代
	init:function(){
		var protocol = window.location.protocol;
		if(window.location.protocol == 'file:'){
			//本地的，那就用http
			protocol = 'http:';
		}
		if(this.resourcesUrl.indexOf('://') == -1){
			//还没设置过，进行设置
			this.resourcesUrl = protocol + this.resourcesUrl;
		}
		
		//this.resourcesUrl = 'file://G:/git/translate';
		
	},
	*/


	/*js translate.execute_v1 start*/
	/**
	 * 执行翻译操作
	 * 已废弃，v1使用的
	 */
	execute_v1:function(){
		translate.log('=====ERROR======');
		translate.log('The v1 version has been discontinued since 2022. Please use the latest V3 version and refer to: http://translate.zvo.cn/41162.html');
	},
	/*js translate.execute_v1 end*/

	/*js translate.setCookie start*/
	/**
	 * 设置Cookie，失效时间一年。
	 * @param name
	 * @param value
	 * * 已废弃，v1使用的
	 */
	setCookie:function (name,value){
		var cookieString=name+"="+escape(value); 
		document.cookie=cookieString; 
	},
	/*js translate.setCookie end*/

	/*js translate.getCookie start*/
	//获取Cookie。若是不存再，返回空字符串
	//* 已废弃，v1使用的
	getCookie:function (name){ 
		var strCookie=document.cookie; 
		var arrCookie=strCookie.split("; "); 
		for(var i=0;i<arrCookie.length;i++){ 
			var arr=arrCookie[i].split("="); 
			if(arr[0]==name){
				return unescape(arr[1]);
			}
		}
		return "";
	},
	/*js translate.getCookie end*/
	

	/*js translate.currentLanguage start*/
	/*
	 获取当前页面采用的是什么语言
	 返回值如 en、zh-CN、zh-TW （如果是第一次用，没有设置过，那么返回的是 translate.localLanguage 设置的值）		
	 已废弃，v1使用的
	 */
	currentLanguage:function(){
		//translate.check();
		var cookieValue = translate.getCookie('googtrans');
		if(cookieValue.length > 0){
			return cookieValue.substr(cookieValue.lastIndexOf('/')+1,cookieValue.length-1);
		}else{
			return translate.localLanguage;
		}
	},
	/*js translate.currentLanguage end*/

	/*js translate.postMessage start*/
	/**
	 * postMessage 跨域通信模块
	 * 用于实现跨域 iframe 之间的语言切换同步
	 * v4.0 新增
	 */
	postMessage:{
		/**
		 * 消息类型常量
		 */
		TYPES: {
			// 语言切换请求
			CHANGE_LANGUAGE: 'translate-js-iframe-changeLanguage',
			// 语言切换完成通知
			LANGUAGE_CHANGED: 'translate-js-iframe-languageChanged',
			// 心跳检测
			PING: 'translate-js-iframe-ping',
			PONG: 'translate-js-iframe-pong'
		},

		/**
		 * 是否已初始化监听器
		 */
		initialized: false,

		/**
		 * message 监听函数引用，用于 reset() 时移除监听器。
		 */
		_messageHandler: null,

		/**
		 * 允许接收的跨域消息来源。
		 * 默认不设置时，允许所有网站来源的消息。
		 * 如需限制来源，可显式配置允许的 origin 数组。
		 */
		allowedOrigins: [],

		/**
		 * 当语言切换请求来自父页面时，重放 changeLanguage()
		 * 的过程中不再反向通知父页面，避免形成循环。
		 */
		_suppressParentNotify: false,

		/**
		 * 获取当前页面的 origin。
		 * @returns {string}
		 */
		getCurrentOrigin: function(){
			if(typeof window.location.origin === 'string' && window.location.origin.length > 0){
				return window.location.origin;
			}
			return this.getOriginFromUrl(window.location.href);
		},

		/**
		 * 规范化 origin 字符串。
		 * @param {string} origin
		 * @returns {string}
		 */
		normalizeOrigin: function(origin){
			if(typeof origin !== 'string'){
				return '';
			}
			origin = origin.trim();
			if(origin.length === 0){
				return '';
			}
			if(origin === '*' || origin === 'null'){
				return origin;
			}
			return this.getOriginFromUrl(origin);
		},

		/**
		 * 从 URL 中解析 origin。
		 * @param {string} url
		 * @returns {string}
		 */
		getOriginFromUrl: function(url){
			if(typeof url !== 'string' || url.trim().length === 0){
				return '';
			}
			try{
				var link = document.createElement('a');
				link.href = url;
				if(typeof link.protocol !== 'string' || link.protocol.length === 0){
					return '';
				}
				if(link.protocol === 'file:'){
					return 'null';
				}
				if(typeof link.host !== 'string' || link.host.length === 0){
					return '';
				}
				return link.protocol + '//' + link.host;
			}catch(e){
				return '';
			}
		},

		/**
		 * 为 postMessage() 解析 targetOrigin。
		 * @param {string} origin
		 * @returns {string}
		 */
		resolveTargetOrigin: function(origin){
			var normalizedOrigin = this.normalizeOrigin(origin);
			if(normalizedOrigin === 'null'){
				return '*';
			}
			if(normalizedOrigin.length > 0){
				return normalizedOrigin;
			}

			if(Array.isArray(this.allowedOrigins) && this.allowedOrigins.length === 1){
				normalizedOrigin = this.normalizeOrigin(this.allowedOrigins[0]);
				if(normalizedOrigin.length > 0 || normalizedOrigin === '*'){
					return normalizedOrigin;
				}
			}

			return '*';
		},

		/**
		 * 根据 iframe 的 src 推导其目标 origin。
		 * @param {HTMLIFrameElement} iframe
		 * @returns {string}
		 */
		getIframeOrigin: function(iframe){
			if(!iframe){
				return '';
			}

			var src = '';
			if(typeof iframe.getAttribute === 'function'){
				src = iframe.getAttribute('src');
			}
			if((typeof src !== 'string' || src.trim().length === 0) && typeof iframe.src === 'string'){
				src = iframe.src;
			}

			var origin = this.getOriginFromUrl(src);
			if(origin.length > 0){
				return origin;
			}

			return this.getCurrentOrigin();
		},

		/**
		 * 根据 referrer 获取父页面的 origin。
		 * @returns {string}
		 */
		getParentOrigin: function(){
			return this.getOriginFromUrl(document.referrer);
		},

		/**
		 * 检查接收到的消息来源是否可信。
		 * @param {string} origin
		 * @returns {boolean}
		 */
		isOriginAllowed: function(origin){
			var normalizedOrigin = this.normalizeOrigin(origin);
			if(normalizedOrigin.length === 0){
				return false;
			}

			if(!Array.isArray(this.allowedOrigins) || this.allowedOrigins.length === 0){
				return true;
			}

			if(normalizedOrigin === this.getCurrentOrigin()){
				return true;
			}

			for(var i = 0; i < this.allowedOrigins.length; i++){
				var allowedOrigin = this.normalizeOrigin(this.allowedOrigins[i]);
				if(allowedOrigin === '*'){
					return true;
				}
				if(allowedOrigin === normalizedOrigin){
					return true;
				}
			}

			return false;
		},

		/**
		 * 当前 changeLanguage 流程是否允许通知父页面。
		 * @returns {boolean}
		 */
		shouldNotifyParent: function(){
			return this._suppressParentNotify !== true;
		},

		/**
		 * 初始化 postMessage 监听器
		 * 在 translate.init() 中自动调用
		 */
		init: function(){
			if(this.initialized){
				return;
			}
			this.initialized = true;

			var self = this;
			this._messageHandler = function(event){
				self.handleMessage(event);
			};
			window.addEventListener('message', this._messageHandler, false);
		},

		reset: function(){
			if(this._messageHandler !== null){
				window.removeEventListener('message', this._messageHandler, false);
				this._messageHandler = null;
			}
			this.initialized = false;
		},

		/**
		 * 处理接收到的消息
		 * @param {MessageEvent} event - 消息事件对象
		 */
		handleMessage: function(event){
			try{
				// 忽略当前窗口自己发出的消息，避免自发自收。
				if(event.source === window){
					return;
				}

				var data = event.data;
				if(typeof data !== 'object' || data === null){
					return;
				}
				if(typeof data.type !== 'string'){
					return;
				}

				var type = data.type;
				if(
					type !== translate.postMessage.TYPES.CHANGE_LANGUAGE &&
					type !== translate.postMessage.TYPES.LANGUAGE_CHANGED &&
					type !== translate.postMessage.TYPES.PING &&
					type !== translate.postMessage.TYPES.PONG
				){
					return;
				}

				if(!translate.postMessage.isOriginAllowed(event.origin)){
					translate.log('[postMessage] Ignore message from untrusted origin: ' + event.origin);
					return;
				}

				if(type === translate.postMessage.TYPES.CHANGE_LANGUAGE){
					if(typeof data.language === 'string' && data.language.trim().length > 0){
						translate.postMessage.onReceiveChangeLanguage(data.language, data.fromParent);
					}
				}

				if(type === translate.postMessage.TYPES.PING){
					var source = event.source;
					if(source){
						translate.postMessage.send(source, translate.postMessage.TYPES.PONG, {
							version: translate.version,
							language: translate.to
						}, event.origin);
					}
				}

				if(type === translate.postMessage.TYPES.PONG){
					translate.log('[postMessage] Received PONG from iframe, version: ' + data.version + ', language: ' + data.language);
					if(typeof translate.postMessage._pingCallback === 'function'){
						var callback = translate.postMessage._pingCallback;
						translate.postMessage._pingCallback = null;
						callback(data);
					}
				}

				if(type === translate.postMessage.TYPES.LANGUAGE_CHANGED){
					translate.log('[postMessage] Language changed in iframe: ' + data.language);
				}
			}catch(e){
				translate.log('[postMessage] handleMessage error: ' + e.message);
			}
		},

		/**
		 * 发送消息到目标窗口
		 * @param {Window} targetWindow - 目标窗口
		 * @param {string} type - 消息类型
		 * @param {object} payload - 消息内容
		 */
		send: function(targetWindow, type, payload, targetOrigin){
			if(!targetWindow){
				return;
			}

			var message = {
				type: type,
				timestamp: Date.now(),
				version: translate.version
			};

			// 合并 payload 到 message
			if(typeof payload === 'object' && payload !== null){
				for(var key in payload){
					if(payload.hasOwnProperty(key)){
						message[key] = payload[key];
					}
				}
			}

			try{
				targetWindow.postMessage(message, translate.postMessage.resolveTargetOrigin(targetOrigin));
			}catch(e){
				translate.log('[postMessage] Send error: ' + e.message);
			}
		},

		/**
		 * 接收到语言切换请求时的处理
		 * @param {string} language - 目标语言
		 * @param {boolean} fromParent - 是否来自父页面
		 */
		onReceiveChangeLanguage: function(language, fromParent){
			// 避免重复切换 - 但需要考虑更多情况
			// 修复：不仅要检查 translate.to，还要检查是否真正处于翻译状态
			var currentLanguage = translate.to;
			var hasTranslatedContent = translate.node.data && translate.node.data.size > 0;

			// 如果当前语言等于目标语言，且已经有翻译内容，才跳过
			// 这样可以处理 iframe 初始加载时 translate.to 可能已经等于目标语言但没有翻译内容的情况
			if(currentLanguage === language && hasTranslatedContent){
				translate.log('[postMessage] Already at target language with translated content: ' + language);
				return;
			}

			translate.log('[postMessage] Received changeLanguage request: ' + language + ', fromParent: ' + fromParent + ', currentTo: ' + currentLanguage + ', hasTranslated: ' + hasTranslatedContent);

			// 执行语言切换（使用内部方法避免消息循环）
			translate.postMessage.executeChangeLanguage(language, fromParent);
		},

		/**
		 * 执行语言切换（内部方法，不触发 postMessage 向父/子窗口发送）
		 * @param {string} language - 目标语言
		 * @param {boolean} fromParent - 是否来自父页面
		 */
		executeChangeLanguage: function(language, fromParent){
			var previousSuppressParentNotify = translate.postMessage._suppressParentNotify;
			try{
				if(fromParent === true){
					translate.postMessage._suppressParentNotify = true;
				}

				// 复用正常的 changeLanguage 流程，保持 reset 和传播行为一致。
				translate.changeLanguage(language);
				translate.log('[postMessage] Language changed to: ' + language);

				if(window.self !== window.top && fromParent === true){
					translate.postMessage.send(window.parent, translate.postMessage.TYPES.LANGUAGE_CHANGED, {
						language: language
					}, translate.postMessage.getParentOrigin());
				}
			}catch(e){
				translate.log('[postMessage] executeChangeLanguage error: ' + e.message);
			}finally{
				translate.postMessage._suppressParentNotify = previousSuppressParentNotify;
			}
		},

			/**
			 * 向父页面发送语言切换消息
		 * @param {string} language - 目标语言
		 */
		notifyParent: function(language){
			if(window.self === window.top){
				// 当前页面是顶层页面，无需通知父页面
				return;
			}

			if(!translate.postMessage.shouldNotifyParent()){
				return;
			}

			try{
				translate.postMessage.send(window.parent, translate.postMessage.TYPES.CHANGE_LANGUAGE, {
					language: language,
					fromParent: false,
					source: 'child'
				}, translate.postMessage.getParentOrigin());
				translate.log('[postMessage] Notified parent to change language: ' + language);
			}catch(e){
				translate.log('[postMessage] Failed to notify parent: ' + e.message);
			}
		},

		/**
		 * 向所有子 iframe 发送语言切换消息
		 * @param {string} language - 目标语言
		 */
		notifyChildren: function(language){
			var iframes = document.querySelectorAll('iframe');
			for(var i = 0; i < iframes.length; i++){
				var iframe = iframes[i];
				var iframeWindow = iframe.contentWindow;

				if(!iframeWindow){
					continue;
				}

				// 先尝试同域直接访问
				var sameOrigin = false;
				try{
					// 尝试访问 iframe 的 document，如果成功则同域
					if(iframe.contentDocument){
						sameOrigin = true;
					}
				}catch(e){
					// 跨域，访问会抛出异常
					sameOrigin = false;
				}

				if(sameOrigin){
					// 同域，尝试直接调用
					try{
						if(typeof iframeWindow.translate === 'object' && typeof iframeWindow.translate.version === 'string'){
							if(iframeWindow.translate.to !== language){
								// 直接调用 changeLanguage，它会自己处理传播
								iframeWindow.translate.changeLanguage(language);
							}
						}
					}catch(e){
						// 异常时使用 postMessage
						translate.postMessage.send(iframeWindow, translate.postMessage.TYPES.CHANGE_LANGUAGE, {
							language: language,
							fromParent: true,
							source: 'parent'
						}, translate.postMessage.getIframeOrigin(iframe));
					}
				}else{
					// 跨域，使用 postMessage
					translate.postMessage.send(iframeWindow, translate.postMessage.TYPES.CHANGE_LANGUAGE, {
						language: language,
						fromParent: true,
						source: 'parent'
					}, translate.postMessage.getIframeOrigin(iframe));
				}
			}
		},

		/**
		 * 向指定 iframe 发送心跳检测
		 * @param {HTMLIFrameElement} iframe - iframe 元素
		 * @param {function} callback - 回调函数
		 */
		ping: function(iframe, callback){
			if(!iframe || !iframe.contentWindow){
				return;
			}

			// 存储回调
			if(typeof callback === 'function'){
				this._pingCallback = callback;
			}

			translate.postMessage.send(iframe.contentWindow, translate.postMessage.TYPES.PING, {}, translate.postMessage.getIframeOrigin(iframe));
		}
	},
	/*js translate.postMessage end*/

	/**
	 * 切换语言，比如切换为英语、法语
 	 * @param languageName 要切换的语言语种。传入如 english
	 * 				会自动根据传入的语言来判断使用哪种版本。比如传入 en、zh-CN 等，则会使用v1.x版本
	 * 														传入 chinese_simplified 、english 等，则会使用 v2.x版本
	 */
	changeLanguage:function(languageName){
		translate.time.log('触发');
		//console.log('changeLanguage -> '+languageName);
		//判断使用的是否是v1.x
		var v1 = ',en,de,hi,lt,hr,lv,ht,hu,zh-CN,hy,uk,mg,id,ur,mk,ml,mn,af,mr,uz,ms,el,mt,is,it,my,es,et,eu,ar,pt-PT,ja,ne,az,fa,ro,nl,en-GB,no,be,fi,ru,bg,fr,bs,sd,se,si,sk,sl,ga,sn,so,gd,ca,sq,sr,kk,st,km,kn,sv,ko,sw,gl,zh-TW,pt-BR,co,ta,gu,ky,cs,pa,te,tg,th,la,cy,pl,da,tr,';
		if(v1.indexOf(','+languageName+',') > -1){
			//用的是v1.x
			translate.log('您使用的是v1版本的切换语种方式，v1已在2021年就以废弃，请更换为v2，参考文档： http://translate.zvo.cn/41549.html');
			translate.check();
			
			var googtrans = '/'+translate.localLanguage+'/'+languageName;
			
			//先清空泛解析域名的设置
			var s = document.location.host.split('.');
			if(s.length > 2){
				var fanDomain = s[s.length-2]+'.'+s[s.length-1];
				document.cookie = 'googtrans=;expires='+(new Date(1))+';domain='+fanDomain+';path=/';
				document.cookie = 'googtrans='+googtrans+';domain='+fanDomain+';path=/';
			}
			
			translate.setCookie('googtrans', ''+googtrans);
			translate.refreshCurrentPage();
			return;
		}
		
		translate.lifecycle.changeLanguage.trigger_Trigger({
			to:languageName
		});

		
		//用的是v2.x或更高
		//translate.setUseVersion2();
		translate.useVersion = 'v2';
		var isReload = false; //标记要刷新页面, true刷新， false不刷新
		//判断是否是第一次翻译，如果是，那就不用刷新页面了。 true则是需要刷新，不是第一次翻译
		if(translate.node.data == null){
			translate.node.data = new Map();
		}
		if(translate.node.data.size > 0){  //那当前已经被翻译过
			isReload = true; //标记要刷新页面
		}
		
		translate.to = languageName;
		translate.storage.set('to',languageName);	//设置目标翻译语言
		
		//判断当前页面是否需要进行翻译，如果需要，那还要对整个页面内容文本进行隐藏处理
		if(translate.visual.webPageLoadTranslateBeforeHiddenText_use){
			//清除 最开始的全部文本隐藏的first记录
			if(typeof(translate.visual.hideText.first_translate_request_uuid) != 'undefined'){ 
				//是第一次翻译请求，记录其uuid
				translate.visual.hideText.first_translate_request_uuid = undefined;
			}

			//网页打开时自动隐藏文字，翻译完成后显示译文 http://translate.zvo.cn/549731.html
			translate.visual.webPageLoadTranslateBeforeHiddenText({
				inHeadTip: false  //警告要在head中触发的控制台消息提醒，true是如果发现就打印这个提醒。 默认不设置便是true
			}); 
		}


		//将翻译进行还原
		translate.reset({
			selectLanguageRefreshRender:false //是否重新渲染select选择语言到原始未翻译前的状态，默认不设置则是true，进行重新渲染
		}); 


		/*
			先触发父级，免得当前刷新了，导致父级不执行翻译了
		*/
		//检测当前是否处于iframe中，如果当前是在iframe中，有父级页面，也要触发父级进行翻译
		try{
			if(window.self !== window.top && translate.postMessage.shouldNotifyParent()){
				if(typeof(window.parent.translate) == 'object' && typeof(window.parent.translate.version) == 'string'){
					//iframe页面中存在 translate,那么也控制iframe中的进行翻译
					if(window.parent.translate.to !== languageName){
						//如果父页面当前的语种不是需要翻译的语种，对其进行翻译
						window.parent.translate.changeLanguage(languageName);
					}
				}else{
					// 父页面没有 translate 对象（可能跨域无法访问），使用 postMessage
					translate.postMessage.notifyParent(languageName);
				}
			}
		}catch(e){
			//增加try，避免异常导致无法用。跨域情况下使用 postMessage
			if(window.self !== window.top && translate.postMessage.shouldNotifyParent()){
				translate.postMessage.notifyParent(languageName);
			}
			translate.log('changeLanguage parent iframe cross-origin, use postMessage: ' + e.message);
		}
		
		translate.time.log('父级 iframe 触发changeLanguage完成');

		translate.to = languageName;
		translate.storage.set('to',languageName);	//设置目标翻译语言

		translate.lifecycle.changeLanguage.resetAfter_Trigger({
			to: languageName
		});

		//重新绘制 select 选择语言
		translate.selectLanguageTag.refreshRender();

		//无刷新切换语言		
		isReload = false;
		if(isReload){
			location.reload(); //刷新页面
		}else{
			//不用刷新，直接翻译
		
			translate.execute(); //翻译
		
			//检测是否有iframe中的子页面，如果有，也对子页面下发翻译命令。这个是针对 LayuiAdmin 框架的场景适配，它的主体区域是在 iframe 中的，不能点击切换语言后，只翻译外面的大框，而iframe中的不翻译
			var iframes = document.querySelectorAll('iframe');
			for (var i = 0; i < iframes.length; i++) {
				var iframe = iframes[i];
				var iframeWindow = iframe.contentWindow;
				if(!iframeWindow){
					continue;
				}

				// 判断是否同域
				var sameOrigin = false;
				try{
					// 尝试访问 iframe.contentDocument，如果成功则同域
					if(iframe.contentDocument){
						sameOrigin = true;
					}
				}catch(e){
					// 跨域，访问会抛出异常
					sameOrigin = false;
				}

				try{
					if(sameOrigin){
						// 同域，尝试直接调用
						if(typeof(iframeWindow.translate) == 'object' && typeof(iframeWindow.translate.version) == 'string'){
							//iframe页面中存在 translate,那么也控制iframe中的进行翻译
							// 修复：不仅要检查 translate.to，还要检查是否真正处于翻译状态
							var iframeTo = iframeWindow.translate.to;
							var iframeHasTranslated = iframeWindow.translate.node.data && iframeWindow.translate.node.data.size > 0;

							// 只有当 iframe 的语言等于目标语言且已有翻译内容时才跳过
							if(!(iframeTo === languageName && iframeHasTranslated)){
								iframeWindow.translate.changeLanguage(languageName);
							}
						}
					}else{
						// 跨域，使用 postMessage
						translate.postMessage.send(iframeWindow, translate.postMessage.TYPES.CHANGE_LANGUAGE, {
							language: languageName,
							fromParent: true,
							source: 'parent'
						}, translate.postMessage.getIframeOrigin(iframe));
						if(typeof(iframe.src) === 'string'){
							translate.log('[postMessage] Send changeLanguage to cross-origin iframe, src: ' + iframe.src);
						}
					}
				}catch(e){
					// 异常时，尝试使用 postMessage
					translate.postMessage.send(iframeWindow, translate.postMessage.TYPES.CHANGE_LANGUAGE, {
						language: languageName,
						fromParent: true,
						source: 'parent'
					}, translate.postMessage.getIframeOrigin(iframe));
					if(typeof(iframe.src) === 'string'){
						translate.log('change sub page iframe exception (use postMessage fallback), iframe src : '+iframe.src);
					}
					translate.log(e);
				}
			}
		}

		/*
		放到了 translate.init 中
		//当用户代码设置里启用了 translate.listener.start() 然后用户加载页面后并没有翻译（这时listener是不启动的只是把listener.use标记为true），然后手动点击翻译按钮翻译为其他语种（这是不会刷新页面），翻译后也要跟着启动监听
		if(translate.listener.use == true && translate.listener.isStart == false){
			if(typeof(translate.listener.start) != 'undefined'){
				translate.listener.addListener();
			}
		}
		*/
	},
	
	/**
	 * 自检提示，适用于 v1.x， 在 v2.x中已废弃
	 * english
	 * 已废弃，v1使用的
	 */
	/*js translate.check start*/
	check:function(){
		if(window.location.protocol == 'file:'){
			translate.log('\r\n---WARNING----\r\ntranslate.js 主动翻译组件自检异常，当前协议是file协议，翻译组件要在正常的线上http、https协议下才能正常使用翻译功能\r\n------------');
		}
	},
	/*js translate.check end*/
	

	
	/**************************** v2.0 */
	to:'', //翻译为的目标语言，如 english 、chinese_simplified
	//用户第一次打开网页时，自动判断当前用户所在国家使用的是哪种语言，来自动进行切换为用户所在国家的语种。
	//如果使用后，第二次在用，那就优先以用户所选择的为主，这个就不管用了
	//默认是false，不使用，可设置true：使用
	//使用 setAutoDiscriminateLocalLanguage 进行设置
	autoDiscriminateLocalLanguage:false,
	documents:[], //指定要翻译的元素的集合,可设置多个，如设置： document.getElementsByTagName('DIV')
	
	//翻译时忽略的一些东西，比如忽略某个tag、某个class等
	ignore:{
		tag:['style', 'script', 'link', 'pre', 'code'],
		//class:['ignore','translateSelectLanguage'],
		class:{
			data:['ignore','translateSelectLanguage'],
			conditionFunction:{
				ignore: function(element){return true;}
			},
			/*
				追加一个忽略翻译的 class name
				className 忽略翻译的 class name 的字符串值
				conditionFunction function(element){} 方法，用于呼应 class name 的规则判定
									其中 element 则是当前扫描到的，已经被 class name 所标记影响范围的某个html元素，针对这个元素进行进一步判定，是否真的忽略对它进行翻译。
										如果这个方法返回true则是遵循忽略class name 的规则，不对这个传入的element元素进行翻译；
										如果返回false，则是不遵循class name 的规则，没有达到忽略class name 的条件，对于element 这个元素，依旧正常进行翻译，所设置的 class name 对这个 element 这个元素无效。
									如果不传入	conditionFunction 这个参数，或传入 null ，则默认相当于设置为 function(element){return true;}
			*/
			push:function(className, conditionFunction){
				if(translate.ignore.class.data.indexOf(className) > -1){
					translate.log('translate.ignore.class.push 设置异常，所设置的 className: '+className+' 已存在里面了，所以此次设置被放弃');
					return;
				}
				translate.ignore.class.data.push(className);
				if(conditionFunction === null){
					return;
				}
				if(typeof(conditionFunction) !== 'function'){
					translate.log('translate.ignore.class.push 设置异常，所设置的第二个参数类型不是function，被抛弃');
					return;
				}
				translate.ignore.class.conditionFunction[className] = conditionFunction;
			},
		},
		id:[],
		/*
			传入一个 node 节点，判断这个node是否是被忽略的。 这个会找父类，看看父类中是否包含在忽略的之中。
			

			node node节点
			data 其他的一些属信息，这个参数在这个方法里没有任何使用，它是给 用户自定义ignore 的 function 参数进行自定义判断使用的
					node: 	当前实际要参与翻译的节点。
							如果是 <p>123</p> 这种元素，那上面传入的ele参数跟这里的node参数的值都是相同的，都是这个p元素
							如果是 <input type="text" title="我是title标签的内容" placeholder="请填写你的姓名" /> 这种元素，当前翻译的是其中的 placeholder 属性的内容
								那么传入的 element 参数是 input 这个元素
								而 node 参数则是 placeholder 这个节点属性（打印它时  console.log(node);  会输出 #text ）

					attribute : 当前实际要进行翻译的 node 节点，是否是 element 传入元素其中的某个属性。
								如果是 '' 空字符串 ，则是当前要翻译的 node 它等于 element，是一个元素
								如果是长度大于0的字符串 ，那么当前翻译的 node 数据是 element 参数的某个属性，而这个属性的名字，便是 attribute 的值
								它的值如：  ''、 'title'、'alt'、'placeholder' ... 这种
					
			return true是在忽略的之中，false不再忽略的之中
		*/
		isIgnore:function(node, data){
			if(node == null || typeof(node) == 'undefined'){
				return false;
			}
			if(typeof(data) === 'undefined'){
				data = {
					attribute: null
				};
			}
			if(typeof(data.node) === 'undefined'){
				data.node = node;
			}

			var currentElement = node;
			if(currentElement.nodeType === 2){ //是属性，将其转为元素判断，因为当前忽略配置，是针对元素配置的
				currentElement = currentElement.ownerElement;
			}else if(currentElement.nodeType === 3){
				//文本节点，转为元素
				currentElement = currentElement.parentNode;
			}

			var parentNode = currentElement;

			//为自定义忽略 function 传入参数进行的整理
			currentElement.element = currentElement;
			currentElement.attribute = data.attribute;
			currentElement.node = data.node;
			
			var maxnumber = 100;	//最大循环次数，避免死循环
			while(maxnumber-- > 0){
				if(parentNode == null || typeof(parentNode) == 'undefined'){
					//没有父元素了
					return false;
				}

				//判断Tag
				//var tagName = parentNode.nodeName.toLowerCase(); //tag名字，小写
				var nodename = translate.element.getNodeName(parentNode).toLowerCase(); //tag名字，小写
				if(nodename.length > 0){
					//有nodename
					if(nodename == 'body' || nodename == 'html' || nodename == '#document'){
						//上层元素已经是顶级元素了，那肯定就不是了
						return false;
					}
					if(translate.ignore.tag.indexOf(nodename) > -1){
						//发现ignore.tag 当前是处于被忽略的 tag
						return true;
					}
				}
				

				//判断class name
				if(parentNode.className !== null && typeof(parentNode.className) === 'string'){
					var classNames = parentNode.className;
					if(classNames == null || typeof(classNames) != 'string'){
						continue;
					}
					//console.log('className:'+typeof(classNames));
					//console.log(classNames);
					classNames = classNames.trim().split(' ');
					for(var c_index = 0; c_index < classNames.length; c_index++){
						if(classNames[c_index] != null && classNames[c_index].trim().length > 0){
							//有效的class name，进行判断
							if(translate.ignore.class.data.indexOf(classNames[c_index]) > -1){
								//发现ignore.class 当前是处于被忽略的 class, 在判定它的 conditionFunction 是否正常
								if(typeof(translate.ignore.class.conditionFunction[classNames[c_index]]) === 'function'){
									return translate.ignore.class.conditionFunction[classNames[c_index]](currentElement, data);
								}else{
									return true;	
								}
							}
						}
					}					
				}

				//判断id
				if(parentNode.id != null && typeof(parentNode.id) != 'undefined'){
					//有效的class name，进行判断
					if(translate.ignore.id.indexOf(parentNode.id) > -1){
						//发现ignore.id 当前是处于被忽略的 id
						return true;
					}
				}

				//赋予判断的元素向上一级
				parentNode = parentNode.parentNode;
			}

			return false;
		},

		/*
		 * 忽略不被翻译的文本，这里出现的文本将不会被翻译。
		 * 这个其实是借用了 自定义术语 的能力，设置了自定义术语的原字符等于翻译后的字符， 于是这个字符就不会被翻译了
		 * 这里可以是多个，数组，如 ['你好','世界']
		 */
		text:[],
		/*
			下面的 textRegex 、 setTextRegexs 正则方式设置忽略不翻译text的能力，有 https://github.com/wangliangyu 提交贡献， 弥补 translate.ignore.text 固定设置的不足
		*/
		textRegex:[], 
		/*
			使用方式如：
			translate.ignore.setTextRegexs([/请求/g, /[\u4a01-\u4a05]+/g]);
		*/
		setTextRegexs: function(arr) {
			if (!Array.isArray(arr)) throw new Error('参数必须为数组');
			for (let i = 0; i < arr.length; i++) {
				if (!(arr[i] instanceof RegExp)) {
					throw new Error('第' + i + '项不是RegExp对象');
				}
			}
			//this.textRegex = [...this.textRegex, ...arr];
			//改为兼容 es5 的方式，提供更多兼容
			this.textRegex = this.textRegex.concat(arr); 
		},
	},
	//刷新页面，你可以自定义刷新页面的方式，比如在 uniapp 打包生成 apk 时，apk中的刷新页面就不是h5的这个刷新，而是app的刷新方式，就需要自己进行重写这个刷新页面的方法了
	refreshCurrentPage:function(){
		location.reload();
	},

	/*
		当前是否已进行了翻译处理
		也就是已经使用多语言切换能力进行切换语种了。

		1. 如果已经进行了语言切换，但是还在切换中，尚未切换完，也是返回true
		2. 如果当前未进行过任何语言切换，那么返回true
		3. 如果当前进行了切换语言，但是页面并未进行任何翻译时，也返回true。比如以下两种情况
				1. 点击了切换语言的select，切换到了其他语言
				2. 触发了 translate.changeLanguage(...)  
		4. 如果设置了本地语种也进行强制翻译 https://translate.zvo.cn/289574.html ，且当前语种跟本地语种也是相同时，无论是否实际上页面也没有元素真正进行了翻译，都会认定为当前是进行翻译处理了，会返回true
		

		注意，它里面会触发 translate.language.getLocal() 进行判定，要保证以下两种满足其中一个：
			1. 提前设置了本地语种
			2. 在dom加载完（网页内容已渲染完毕，以便能进行本地语种自动识别）后使用此
		
		@param to 判断当前是否是以这种语种显示。 如果不传入，则是判断当前页面是否有使用 translate.js 进行了翻译。只要有一个元素参与了翻译，那也是进行了。

		true：是
		false：否，不需要进行任何翻译
	*/
	isTranslateExecute: function(to){
		if(typeof(to) !== 'string' || to.length === 0){
			//没有设置to参数，那么就是对整体是否进行了翻译进行判断了。

			//判断 translate.to 参数，如果没有值，那肯定就是没有进行任何翻译。
			if(typeof(translate.to) !== 'string' || translate.to.length === 0){
				return false;
			}
			to = translate.to;
		}

		//判断  如果没有值，那肯定没有参与过翻译。
		//这个不应该，不管是否产生了元素翻译结果，但是动作有过切换，就是true
		//if(typeof(translate.node.data) !== 'object' || translate.node.data.size === 0){
		//	return false;
		//}
		
		if(to === translate.language.getLocal()){
			if(translate.language.translateLocal){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	},
	/*
		废弃，请使用 translate.isTranslateExecuted(to);
	*/
	isTranslate: function(to){
		return translate.isTranslateExecute(to);
	},

	//自定义翻译术语
	nomenclature:{
		/*
			术语表
			一维：要转换的语种，如 english
			二维：翻译至的目标语种，如 english
			三维：要转换的字符串，如 "你好"
			结果：自定义的翻译结果，如 “Hallo”
		*/
		data:new Array(),
		
		/*
			原始术语表，可编辑的
			一维：要自定义目标词
			二维：针对的是哪个语种
			值：要翻译为什么内容

			其设置如 
			var data = new Array();
			data['版本'] = {
				english : 'banben',
				korean : 'BanBen'
			};
			data['国际化'] = {
				english : 'guojihua',
				korean : 'GuoJiHua'
			};
			
			【已过时】
		*/
		old_Data:[],
		/*
		set:function(data){
			translate.nomenclature.data = data;
		},
		*/
		set:function(data){
			alert('请将 translate.nomenclature.set 更换为 append，具体使用可参考： https://github.com/xnx3/translate ');
		},
		/*
			向当前术语库中追加自定义术语。如果追加的数据重复，会自动去重
			传入参数：
				from 要转换的语种
				to 翻译至的目标语种
				properties 属于配置表，格式如：
						你好=Hello
						世界=ShiJie

		*/
		append:function(from, to, properties){
			if(typeof(from) == 'undefined' || from == null || from == 'auto'){
				//如果from未传入，则自动识别当前页面的语种为from
				//如果自动识别，也要确保是页面加载完后，免得放到了head里，那肯定啥也识别不出来
				if(document.body == null){
					translate.log('使用错误！你使用自定义术语 translate.nomenclature.append 时，from 未传值，此时 translate.js 会自动识别当前翻译区域的内容是什么语种，但是你当前吧 translate.nomenclature.append 放在了body之前就加载了，body都还没加载出来，翻译区域当前无内容，所以无法识别当前页面的语种。请将 translate.nomenclature.append 放在翻译内容加载完后再执行 （注意，要将 translate.nomenclature.append 放在 translate.execute() 的前面），建议将 translate.nomenclature.append 放在 </body> 跟 </html> 之间。');
				}else{
					if (document.readyState === 'loading') {
	   					translate.log('使用异常告警：你使用的自定义术语 translate.nomenclature.append 时，from 未传值，此时 translate.js 会自动识别当前翻译区域的内容是什么语种，但页面Dom还未加载完毕时就触发了它，如果翻译区域当前无内容或者内容不是完整的，会造成识别当前页面的语种会有异常不准确，你需要仔细确认这个问题。建议将 translate.nomenclature.append 放在 </body> 跟 </html> 之间。');
					}
				}
				
				from = translate.language.getLocal();
			}

			if(typeof(translate.nomenclature.data[from]) == 'undefined'){
				translate.nomenclature.data[from] = new Array();
			}
			if(typeof(translate.nomenclature.data[from][to]) == 'undefined'){
				translate.nomenclature.data[from][to] = new Array();
			}
			
			//将properties进行分析
			//按行拆分
			var line = properties.split('\n');
			//console.log(line)
			for(var line_index = 0; line_index < line.length; line_index++){
				var item = line[line_index].trim();
				if(item.length < 1){
					//空行，忽略
					continue;
				}
				var kvs = item.split('=');
				//console.log(kvs)
				if(kvs.length != 2){
					//不是key、value构成的，忽略
					continue;
				}
				var key = kvs[0].trim();
				var value = kvs[1].trim();
				//console.log(key)
				if(key.length == 0 || value.length == 0){
					//其中某个有空，则忽略
					continue;
				}


				//加入，如果之前有加入，则会覆盖
				translate.nomenclature.data[from][to][key] = value;
				//console.log(local+', '+target+', key:'+key+', value:'+value);
			}

			//追加完后，对整个对象数组进行排序，key越大越在前面
			translate.nomenclature.data[from][to] = translate.util.objSort(translate.nomenclature.data[from][to]);

		},
		//获取当前定义的术语表
		get:function(){
			return translate.nomenclature.data;
		},

		/**
		 * 对指定文本进行自定义术语替换
		 * 示例：
		 *  translate.nomenclature.replace(['你好我好她也好'],'好','hao', null)
		 * 结果：
		   {
		       find:true,
		       texts: ['你', '我', '她也'],
		       resultText: "你hao我hao她也hao"
		    }
		 * 
		 * @param text 要进行自定义术语替换的文本
		 * @param nomenclatureKey 自定义术语的key
		 * @param nomenclatureValue 自定义术语的value
		 * @param nodeObject 要进行替换的节点对象，自定义术语命中后，会直接在这个节点上进行替换显示
		 *                  如果传入 null，则不进行任何替换操作
		 *                  如果传入具体的值，则是： 
		 *                      {
		 *                          node: node节点 ，要改动的文字所在的node节点。 如果改动的文字比如是 div 的title中，那么这里传入的node应该是 title 的node，而不是 div 的node
		 *                      }
		 *
		 * @returns {
		 *              find:false,    //是否命中了自定义术语，命中了，则是 true，也代表 textArray 已经不是传入的那个了，已经被处理分割过了
		                texts:['你','好'],    //针对传入的 textArray 参数，进行术语命中完成后，将命中术语的部分剔除掉，进行分割，所返回的新的textArray
		                resultText: "你hao我hao她也hao"  //对text处理后，替换后的文本

		            }
		 */
		replace: function(text, nomenclatureKey, nomenclatureValue, nodeObject){
			/*
		    if(text.trim() == nomenclatureValue.trim()){
		    	


		        //这里是自定义术语被替换后，重新扫描时扫出来的，那么直接忽略，不做任何处理。因为自定义术语的结果就是最终结果了
		        return {
		            texts:[text],
		            find:false,
		            resultText:text
		        }
		    }
		    */
		    if(nomenclatureKey.length == 0){  //上个版本有这个，应该不会有这个情况，但是还是保留了
		    	return {
		            texts:[text],
		            find:false,
		            resultText:text
		        }
		    }

		    //判断一下原始文本是否有出现在了这个word要翻译的字符串中
		    var wordKeyIndex = text.indexOf(nomenclatureKey);
		    if(wordKeyIndex > -1){
		        //出现了，那么需要将其立即进行更改，将自定义术语定义的结果渲染到页面中，并且将 word 要翻译的字符串中，自定义术语部分删除，只翻译除了自定义术语剩余的部分
		        
		        //这里考虑重复替换问题，比如要将 好 替换为  你好 ，如果重复替换，可能会出来 你你你你你好
		        //另外还要考虑特殊字符问题，如果用 split 会出现异常
		        //注意，可能会出现多个key的情况
		        var positions = [];
		        var pos = wordKeyIndex;
		        // 当找到 key的文字时继续查找
		        while (pos !== -1) {
		            positions.push(pos);
		            // 从当前位置的下一个字符开始继续查找
		            pos = text.indexOf(nomenclatureKey, pos + 1);
		        }
		        // 遍历所有找到的位置，判断是否是已经自定义术语替换后的，如果全部都是替换后的，那么就不需要继续替换了，直接 return 退出，避免重复替换。
		        //但是如果只要有一次是没有被替换的，那么都会往下执行，可能会存在重复替换。
		        //比如  "你好吗我好吗大家好都好呀" 将 "好" 替换为 “好吗”，这里会替换为 “你好吗吗我好吗吗大家好吗都好吗呀” ，因为最后的俩“好”经过识别，是没有被替换过的，所以这句是要被进行替换执行的，这个整句替换是现有的方法，这个后续可以把提花你方法拆分一下，进行针对性的只针对最后的俩“好”进行精准替换，而不对前面的俩“好吗”在进行替换
		        
		        var texts = new Array(); //它是text经过pos的分割后的数组，要返回的数组
		        var resultText = text; //这是有text文本经过将 nomenclatureKey 替换为 nomenclatureValue 之后，得到的新的文本
		        var lastPos = text.length; //记录上一个pos的位置
		        for(var i = positions.length-1; i>=0; i--){ //采用--的方式，保证替换后下标依旧能保持一致
		            var itempos = positions[i];

		            //将pos分割之后的文本，加入到 wordSplits 数组中
		            texts.unshift(text.substring(itempos + nomenclatureKey.length, lastPos));
		            //console.log(pos +' --> '+text.substring(pos + nomenclatureKey.length, lastPos));

		            // 将 text 中 的 pos 下标的文本，也就是从 pos 开始，到pos+nomenclatureKey.length 结束的文本，替换为 nomenclatureValue 
		            resultText = resultText.substring(0, itempos) + nomenclatureValue + resultText.substring(itempos+nomenclatureKey.length);

		            lastPos = itempos;
		        }
		        if(lastPos > 0){
		            //将pos分割之前的文本，加入到 wordSplits 数组中
		            texts.unshift(text.substring(0, lastPos));
		        }

		        //筛选 texts ，将空字符串 length == 0 的剔除
		        if(texts.length > 0){
		            for(var di = texts.length - 1; di >= 0; di--){
		                if(texts[di].length == 0){
		                    texts.splice(di, 1);
		                }
		            }
		        }


		        //如果是自定义术语的key等于value，则是属于指定的某些文本不进行翻译的情况，所以这里要单独判断一下，它俩不相等才会去进行替换操作，免得进行性能计算浪费 - 虽然这一步是不会到的，因为在这个方法的入口处就已经经过这个判定了
		        if(nodeObject != null && typeof(nodeObject.node) !== 'undefined' && nodeObject.node !== null){

		        	// 记录此次node的改变是有 translate.js 导致的，避免被dom改变监听给误以为别的引起的
		        	if(translate.node.get(nodeObject.node) != null){
		        		translate.node.get(nodeObject.node).lastTranslateRenderTime = Date.now();
		        	}else{
		        		//这个如果有 translate.js 内部自主触发，肯定不会没有值的。但是如果手动再其他程序里触发，那这个是会没有值的
		        	}

		        	if(nomenclatureKey === nomenclatureValue){
		        		//自定义忽略翻译的文字 ,key 跟 value 相等，便是忽略翻译的
		        		translate.element.nodeAnalyse.set(nodeObject.node, nomenclatureKey, nomenclatureValue, nodeObject.attribute);
		        	}else{
		        		//自定义术语的
		        		translate.element.nodeAnalyse.set(nodeObject.node, nomenclatureKey, nomenclatureValue, nodeObject.attribute);
		        	}
		        }
		        
		        return {
		            texts:texts,
		            find:true,
		            resultText:resultText
		        }
		    }else{
		        return {
		            texts:[text],
		            find:false,
		            resultText:text
		        }
		    }
		},
		//对传入的str字符进行替换，将其中的自定义术语提前进行替换，然后将替换后的结果返回
		/*
		  自定义术语
		  示例：
		   translate.nomenclature.dispose(['你好我好她也好'],'好','hao', null)
		  结果：
		    {
		       find:true,
		       texts: ['你', '我', '她也'],
		       resultText: "你hao我hao她也hao"
		    }
		  
		  @param {*} texts 要进行自定义术语替换的文本数组，传入比如 ["你好","世界"]
		  @param {*} nomenclatureKey 
		  @param {*} nomenclatureValue 
		  @param {*} nodeObject 要进行替换的节点对象，自定义术语命中后，会直接在这个节点上进行替换显示
		                   如果传入 null，则不进行任何替换操作
		                   如果传入具体的值，则是： 
		                       {
		                           node: node节点 ，要改动的文字所在的node节点。 如果改动的文字比如是 div 的title中，那么这里传入的node应该是 title 的node，而不是 div 的node
		                       }
		 

		  @returns {
		               find:false,    //是否命中了自定义术语，命中了，则是 true，也代表 textArray 已经不是传入的那个了，已经被处理分割过了
		               texts:['你','好'],    //针对传入的 textArray 参数，进行术语命中完成后，将命中术语的部分剔除掉，进行分割，所返回的新的textArray . 如果没有命中术语，那么这里是只有一个值，那便是返回传入的text
		               resultText: "你hao我hao她也hao"
		           }
		 */
		dispose: function(textArray, nomenclatureKey, nomenclatureValue, nodeObject){
		    // 输入验证
		    if (!Array.isArray(textArray)) {
		        textArray = [String(textArray)];
		    }

		    //这里要调用 translate.nomenclature.replace 方法，对 textArray 中的每个文本进行自定义术语替换处理
		    var result = {
		    	find:false
		    };
		    result.texts = new Array();
		    result.resultText = new Array();
		    for(var i = 0; i < textArray.length; i++){
		        var text = textArray[i];
		        var res = translate.nomenclature.replace(text, nomenclatureKey, nomenclatureValue, nodeObject);
		        if(res.find){
		        	result.find = true;
		        }
		        result.texts = result.texts.concat(res.texts);
		        result.resultText.push(res.resultText);
		    }

		    //对 result.texts 进行去重处理
		    if(result.texts.length > 0){
		        for(var di = result.texts.length - 1; di >= 0; di--){
		            if(result.texts.indexOf(result.texts[di]) != di){
		                result.texts.splice(di, 1);
		            }
		        }
		    }

		    return result;

		},
	},

	//已转为 offline ，这个是对旧版做兼容
	office:{
		export:function(){
			console.log('请使用最新版本的 translate.offline.export , 而不是 translate.office.export');
		},
		showPanel:function(){
			console.log('请使用最新版本的 translate.offline.showPanel , 而不是 translate.office.export');
		},
		append:function(to, properties){
			translate.offline.append(to, properties);
		},
		fullExtract:{
			isUse:false
		}
	},
	offline:{
		/*
			网页上翻译之后，自动导出当前页面的术语库
			
			需要先指定本地语种，会自动将本地语种进行配置术语库
			
		*/
		export:function(){
			if(translate.language.getLocal() == translate.language.getCurrent()){
				alert('本地语种跟要翻译的语种一致，无需导出');
				return;
			}

			var text = '';
			for(var uuid in translate.nodeQueue){
				if (!translate.nodeQueue.hasOwnProperty(uuid)) {
		    		continue;
		    	}

				var queueValue = translate.nodeQueue[uuid];
				for(var lang in translate.nodeQueue[uuid].list){
					if (!translate.nodeQueue[uuid].list.hasOwnProperty(lang)) {
			    		continue;
			    	}
					//console.log('------'+lang)
					if(typeof(lang) != 'string' || lang.length < 1){
						continue;
					}
					//if(translate.language.getLocal() == lang){
						//console.log(translate.nodeQueue[uuid].list[lang]);
						for(var hash in translate.nodeQueue[uuid].list[lang]){
							if (!translate.nodeQueue[uuid].list[lang].hasOwnProperty(hash)) {
					    		continue;
					    	}
					    	
					    	var result = translate.storage.get('hash_'+translate.language.getCurrent()+'_'+hash);
							//如果翻译结果不存在，可能是同语种本身就没有翻译，忽略就好了 （因为有个本地语种也强制翻译的能力，所以同语种也放行，在这里进行一次结果判断，免得遗漏同语种也翻译的情况）
							if(typeof(result) === 'undefined' || result === null || result.length === 0){
								continue;
							}
							
							//将配置中出现的换行替换为 \n 这个符号
							var lineText = translate.nodeQueue[uuid].list[lang][hash].original + '='+result;
							text = text + '\n' + (lineText.replace(/\n/g, '{\\\\n}'));
						}
					//}
				}
				
			}

			if(text.length > 0){
				//有内容
				text = 'translate.offline.append(\''+translate.language.getCurrent()+'\',`'+text+'\n`);';
				//console.log(text);
				translate.util.loadMsgJs();
				msg.popups({
				    text:'<textarea id="msgPopupsTextarea" style="width:100%; height:100%; color: black; padding: 8px;">loaing...</textarea>',
				    width:'750px',
				    height:'600px',
				    padding:'1px',
				});	
				document.getElementById('msgPopupsTextarea').value = text;
			}else{
				msg.alert('无有效内容！');
			}


		},
		//显示导出面板
		showPanel:function(){
			translate.recycle = function(){}; //重写垃圾回收，弃用

			let panel = document.createElement('div');
			panel.setAttribute('id', 'translate_export');
			panel.setAttribute('class','ignore');

			//导出按钮
			let button = document.createElement('button');
			button.onclick = function() {
			  translate.offline.export();
			};
			button.innerHTML = '导出配置信息';
			button.setAttribute('style', 'margin-left: 72px; margin-top: 30px; margin-bottom: 20px; font-size: 25px; background-color: blue; padding: 15px; padding-top: 3px; padding-bottom: 3px; border-radius: 3px;');
			panel.appendChild(button);

			//说明文字
			let textdiv = document.createElement('div');
			textdiv.innerHTML = '1. 首先将当前语种切换为你要翻译的语种<br/>2. 点击导出按钮，将翻译的配置信息导出<br/>3. 将导出的配置信息粘贴到代码中，即可完成<br/><a href="http://translate.zvo.cn/4076.html" target="_black" style="color: aliceblue; text-decoration: underline;">点此进行查阅详细使用说明</a>';
			textdiv.setAttribute('style','font-size: 14px; padding: 12px;');

			panel.appendChild(textdiv);			
			
			panel.setAttribute('style', 'background-color: black; color: #fff; width: 320px; height: 206px; position: fixed; bottom: 50px; right: 50px;');
			//把元素节点添加到body元素节点中成为其子节点，放在body的现有子节点的最后
			document.body.appendChild(panel);

			translate.util.loadMsgJs();
		},
		/*
			追加离线翻译数据。如果追加的数据重复，会自动去重
			传入参数：
				from 要转换的语种
				to 翻译至的目标语种
				properties 属于配置表，格式如：
						你好=Hello
						世界=ShiJie
			这个传入参数跟 translate.nomenclature.append 的传入参数格式是一致的			
		*/
		append:function(to, properties){
			//console.log(properties)
			//将properties进行分析
			//按行拆分
			var line = properties.split('\n');
			//console.log(line)

			//计算前10行，判定当前配置文件的行开头缩进方式
			var lmap = new Map();
			for(var line_index = 0; line_index < line.length && line_index < 10; line_index++){
				const match = line[line_index].match(/^[ \t]+/);
  				var suojin = match ? match[0] : '0'; //0便是没有空白符缩进
  				var sum = 1; //累加次数
				if(typeof(lmap.get(suojin)) !== 'undefined'){
					sum = sum+lmap.get(suojin);
				}
				lmap.set(suojin, sum);
			}

			// 核心逻辑：遍历Map找到次数最多的key
			let maxCount = 0; // 记录最大次数（初始为0，次数至少为1，不影响）
			let maxKey = null; // 记录次数最多的key

			// 方式1：for...of遍历Map.entries()（推荐，直观）
			for (const [key, count] of lmap.entries()) {
			    if (count > maxCount) {
			        maxCount = count; // 更新最大次数
			        maxKey = key;     // 更新对应key
			    }
			}
			//console.log(lmap);
			//console.log(maxKey+' -> '+maxCount);
			lmap = null;

			for(var line_index = 0; line_index < line.length; line_index++){
				var item = line[line_index];
				//有缩进，那就需要把行开始的缩进去掉
				if(maxKey !== '0'){ 
					if(line[line_index].startsWith(maxKey)){
						var item = line[line_index].slice(maxKey.length);
					}else{
						//异常提示告警
						if(line[line_index].trim().length > 0){
							translate.log('WAINING : translate.offline.append 异常，发现某行的配置项缩进异常，这行的缩进应该跟其他行的缩进保持一致！异常的这行配置项为：\n'+item);
						}
					}
				}
				if(item.length < 1){
					//空行，忽略
					continue;
				}
				item = item.replace(/\{\\n\}/g, '\n');
				var kvs = item.split('=');
				//console.log(kvs)
				if(kvs.length != 2){
					//不是key、value构成的，忽略
					continue;
				}
				var key = kvs[0];
				var value = kvs[1];
				//console.log(key)
				if(key.length == 0 || value.length == 0){
					//其中某个有空，则忽略
					continue;
				}
				//console.log('set---'+key);
				//加入 storate
				translate.storage.set('hash_'+to+'_'+translate.util.hash(key), value);
			}
		},

		
		//全部提取能力（整站的离线翻译数据提取）
		fullExtract:{
			/*js translate.offline.fullExtract.set start*/
			/*
				将翻译的结果加入
				hash: 翻译前的文本的hash
				originalText: 翻以前的文本，原始文本
				toLanguage: 翻译为什么语言
				translateText: 翻译结果的文本
			*/
			set: async function(hash, originalText, toLanguage, translateText){
				if(typeof(translate.storage.IndexedDB) == 'undefined'){
					translate.log('ERROR: translate.storage.IndexedDB not find');
					return;
				}
				var obj = await translate.storage.IndexedDB.get('hash_'+hash);
				if(typeof(obj) == 'undefined' || obj == null){
					obj = {
						originalText:originalText
					};
				}
				obj[toLanguage] = translateText;
				await translate.storage.IndexedDB.set('hash_'+hash, obj);
			},
			/*js translate.offline.fullExtract.set end*/

			/*js translate.offline.fullExtract.export start*/
			/*
				将存储的数据导出为 txt 文件下载下来
			*/
			export: async function(to){
				if(typeof(translate.storage.IndexedDB) == 'undefined'){
					translate.log('ERROR: translate.storage.IndexedDB not find');
					return;
				}
				if(typeof(to) != 'string'){
					translate.log('error : to param not find, example: "english"');
					return;
				}
				var text = 'translate.offline.append(\''+to+'\',`';
				
				var data = await translate.storage.IndexedDB.list('hash_*');
				for(var i in data){
					if (!data.hasOwnProperty(i)) {
			    		continue;
			    	}
					//var originalText = data[i].value.originalText.replace(/\n/g, "\\n").replace(/\t/g, "\\t");
					//text = text + '\n' + originalText + '='+data[i].value.english.replace(/\n/g, "\\n").replace(/\t/g, "\\t");

			    	//如果翻译结果不存在，可能是同语种本身就没有翻译，忽略就好了 （因为有个本地语种也强制翻译的能力，所以同语种也放行，在这里进行一次结果判断，免得遗漏同语种也翻译的情况）
					if(data[i].value == null || typeof(data[i].value[to]) !== 'string' || data[i].value[to].trim().length === 0){
						continue;
					}

					var lineText = data[i].value.originalText+'='+data[i].value[to];
					text = text + '\n' + (lineText.replace(/\n/g, '{\\\\n}'));
				}
				text = text + '\n`);'

				const blob = new Blob([text], { type: "text/plain" });
				const url = URL.createObjectURL(blob);
				const link = document.createElement("a");
				try{
					link.href = url;
					link.download = to+".txt";
					link.style.display = "none";
					(document.body || document.documentElement).appendChild(link);
					link.click();
				}finally{
					setTimeout(function(){
						if(link.parentNode){
							link.parentNode.removeChild(link);
						}
						URL.revokeObjectURL(url);
					}, 100);
				}
			},
			/*js translate.offline.fullExtract.export end*/

			/*
				是否启用全部提取的能力
				true: 启用，  默认是false不启用。
				如果设置为true，则每次通过调用翻译接口进行翻译后，都会将翻译的原文、译文、翻译为什么语种，都会单独记录一次，存入浏览器的 IndexedDB 的 translate.js 数据库
					然后可以浏览所有页面后，把所有翻译一对一的对应翻译结果直接全部导出，用于做离线翻译配置使用。
			*/
			isUse:false,
		}
	},
	setAutoDiscriminateLocalLanguage:function(){
		translate.autoDiscriminateLocalLanguage = true;
	},
	/*
		待翻译的页面的node队列
		一维：key:uuid，也就是execute每次执行都会创建一个翻译队列，这个是翻译队列的唯一标识。   
			 value:
				k/v 
		二维：对象形态，具体有：
			 key:expireTime 当前一维数组key的过期时间，到达过期时间会自动删除掉这个一维数组。如果<0则代表永不删除，常驻内存
			 value:list 从DOM中自动识别出的语言文本及节点数据，按照语种进行了划分，每个语种便是其中的一项。
		三维：针对二维的value，  key:english、chinese_simplified等语种，这里的key便是对value的判断，取value中的要翻译的词是什么语种，对其进行了语种分类    value: k/v
		四维：针对三维的value，  key:要翻译的词（经过语种分割的）的hash，   value: node数组
		五维：针对四维的value，  这是个对象， 其中
				original: 是三维的key的hash的原始文字， node 元素中的原始文字（可能是node元素整个内容，也可能是被分割出的某一块内容，比如中英文混合时单独提取中文）
				cacheHash: 如果翻译时匹配到了自定义术语库中的词，那么翻译完后存入到缓存中时，其缓存的翻译前字符串已经不是original，而是匹配完术语库后的文本的hash了。所以这里额外多增加了这个属性。如果匹配了术语库，那这里就是要进行缓存的翻译前文本的hash，如果未使用术语库，这里就跟其key-hash 相同。
				translateText: 针对 original 的经过加工过的文字，比如经过自定义术语、以及其他处理操作后的，待进行文本翻译的文字。
				nodes: 有哪些node元素中包含了这个词，都会在这里记录
		六维：针对五维的 nodes，将各个具体的 node 以及 其操作的 attribute 以数组形式列出
		七维：针对六维列出的nodes数组，其中包含：
				node: 具体操作的node元素
				attribute: 也就是翻译文本针对的是什么，是node本身（nodeValue），还是 node 的某个属性，比如title属性，这则是设置为 "title"。如果这里不为空，那就是针对的属性操作的。 如果这里为空或者undefined ，那就是针对node本身，也就是 nodeValue 的字符串操作的
				beforeText: node元素中进行翻译结果赋予时，额外在翻译结果的前面加上的字符串。其应用场景为，如果中英文混合场景下，避免中文跟英文挨着导致翻译为英语后，连到一块了。默认是空字符串 ''
				afterText:  node元素中进行翻译结果赋予时，额外在翻译结果的后面加上的字符串。其应用场景为，如果中英文混合场景下，避免中文跟英文挨着导致翻译为英语后，连到一块了。默认是空字符串 ''

		生命周期： 当execute()执行时创建，  当execute结束（其中的所有request接收到响应并渲染完毕）时销毁（当前暂时不销毁，以方便调试）
	*/
	nodeQueue:{},
	//指定要翻译的元素的集合,可传入一个元素或多个元素
	//如设置一个元素，可传入如： document.getElementById('test')
	//如设置多个元素，可传入如： document.getElementsByTagName('DIV')
	setDocuments:function(documents){
		if (documents == null || typeof(documents) == 'undefined') {
			return;
		}
		
		if(typeof(documents.length) == 'undefined'){
			//不是数组，是单个元素
			translate.documents[0] = documents;
		}else{
			//是数组，直接赋予
			for(var i = 0; i < documents.length; i++){
				if(typeof(documents[i]) === 'undefined' || documents[i] === null){
					//这个元素不存在，从这里面删除掉
					 documents.splice(i, 1);
				}
			}

			if(documents.length > 0){
				translate.documents = documents;
			}
		}
		//清空翻译队列，下次翻译时重新检索
		translate.nodeQueue = {};
		//console.log('set documents , clear translate.nodeQueue');
	},
	//获取当前指定翻译的元素（数组形式 [document,document,...]）
	//如果用户未使用setDocuments 指定的，那么返回整个网页
	//它返回的永远是个数组形式
	getDocuments:function(){
		if(translate.documents != null && typeof(translate.documents) != 'undefined' && translate.documents.length > 0){
			// setDocuments 指定的
			return translate.documents;
		}else{
			//未使用 setDocuments指定，那就是整个网页了
			//return document.all; //翻译所有的  这是 v3.5.0之前的
			//v3.5.0 之后采用 拿 html的最上层的demo，而不是 document.all 拿到可能几千个dom
			var doms = new Array();
			doms[0] = document.documentElement;
			return doms;
		}
	},
	
	listener:{
		//当前页面打开后，是否已经执行完execute() 方法进行翻译了，只要执行完一次，这里便是true。 （多种语言的API请求完毕并已渲染html）
		//isExecuteFinish:false,
		//是否已经使用了 translate.listener.start() 了，如果使用了，那这里为true，多次调用 translate.listener.start() 只有第一次有效
		isStart:false,
		//用户的代码里是否启用了 translate.listener.start() ，true：启用
		//当用户加载页面后，但是未启用翻译时，为了降低性能，监听是不会启动的，但是用户手动点击翻译后，也要把监听启动起来，所以就加了这个参数，来表示当前是否在代码里启用了监听，以便当触发翻译时，监听也跟着触发
		use:false, 

		//针对 input 的 value 监听情况, 它无法用dom监控，针对像是 vant 框架，要用 input 的 value 进行作为内容显示的，就要采用这种方式来监听变动并翻译了
        input:{
			/*
				原生value属性描述符
                如果为null，则是还没对input的value进行监听。
                如果已进行监听，会把原本的 value 改变的 set ... 赋予这里。
			*/
			originalValueDescriptor : null,

			/*
				启动对 input value 变动的监听及翻译
			*/
			start:function(){
				if(translate.listener.input.originalValueDescriptor !== null){
					console.log('已启动过了，无需在启动');
					return;
				}

				// 1. 保存原生value属性描述符
				translate.listener.input.originalValueDescriptor = Object.getOwnPropertyDescriptor(
					HTMLInputElement.prototype,
					'value'
				);

				// 2. 重写HTMLInputElement原型的value setter（影响所有input）
				Object.defineProperty(HTMLInputElement.prototype, 'value', {
					...translate.listener.input.originalValueDescriptor,
					set(newValue) {
						const oldValue = this.value; // this指向当前被修改的input

						// 执行原生赋值
						translate.listener.input.originalValueDescriptor.set.call(this, newValue);

						// 值变化时触发逻辑
						if (newValue !== oldValue) {
							//console.log(`JS修改了input值：`);
							//console.log(`  旧值=${oldValue} → 新值=${newValue}`);
							//console.log(this)

							//如果有 translate.node 历史，要根据历史判定一下，如果当前不是translate.js 导致的改变，那就是其他js触发的，那么将其删掉，这样才能触发它重新翻译
							if(translate.node.find(this)){
								var nodeData = translate.node.get(this);
								
								if(typeof(nodeData.lastTranslateRenderTime) === 'number' && Date.now() - nodeData.lastTranslateRenderTime < 100){
									//小于100毫秒，这是 translate.js 引起的改动，不需要任何处理
								}else{
									//不是 translate.js 引起的，那么需要进行翻译
									//删掉当前的记录，以便能正常扫描加入翻译
									translate.node.delete(this);
								}

							}
							translate.execute([this]);
						}
					}
				});

			},

			/*
				当启动对input value监听时，如果切换回源语种了且本地语种并不强制翻译，那么就不需要再翻译了，还原回来，避免性能浪费。 
				也就是相当于对 translate.listener.input.start() 触发后的还原
			*/
			reset: function(){
				if(translate.listener.input.originalValueDescriptor === null){
					return;
				}

				// 1. 还原HTMLInputElement原型的原生value属性描述符
			    Object.defineProperty(
			        HTMLInputElement.prototype,
			        'value',
			        translate.listener.input.originalValueDescriptor
			    );

			    // 2. 重置标记为未监听状态，允许后续重新启动监听
			    translate.listener.input.originalValueDescriptor = null;
			}

          
        },


		//translate.listener.start();	//开启html页面变化的监控，对变化部分会进行自动翻译。注意，这里变化区域，是指使用 translate.setDocuments(...) 设置的区域。如果未设置，那么为监控整个网页的变化
		start:function(){
			if(typeof(translate.temp_listenerStartInterval) != 'undefined'){
				//已经触发过一次了，不需要再触发了
				return;
			}
			translate.listener.use = true;

			/*
			放到了 translate.init 中
			translate.temp_listenerStartInterval = setInterval(function(){
				if(document.readyState == 'complete'){
					//dom加载完成，进行启动

					// 先判断定时器是否已被清除（防止重复执行）
    				if (!translate.temp_listenerStartInterval){
    					return;
    				}

					clearInterval(translate.temp_listenerStartInterval);//停止
					
					//如果不需要翻译的情况，是不需要进行监听的
					if(translate.language.getCurrent() == translate.language.getLocal()){
						if(translate.language.translateLocal){
							//本地语种也要强制翻译跟本地语种不一致的语种
						}else{
							//console.log('本地语种跟目标语种一致，不进行翻译操作，无需监听。');
							return;
						}
					}

					//console.log('进行监听。。');
					translate.listener.addListener();
				}
				
	        }, 300);
	        */
	        
		},
		/*
			对 dom 动态监听进行还原操作，还原到未监听时的状态，进行还原
		*/
		reset: function(){

			//清除 translate.listener 
			if(typeof(translate.listener.observer) != 'undefined' && translate.listener.observer != null){
				translate.listener.observer.disconnect();
			}

			//设置为未启动	
			if(translate.listener.isStart){
				translate.listener.isStart = false; 
			}

			//还原 input value 监听
			translate.listener.input.reset();
		},
		
		/*
			用于监听发生改变的这个 node 是否有正常需要翻译的内容、以及是否是非translate.js触发的需要被翻译。
			注意，传入进行判断的node中的文本必须是 node.nodeValue ，也就是这个必须是 node.nodeType == 2(某个元素的属性，比如 input 的 placeholder) 或 3(文本节点)， 这样他们才会有正常的 node.nodeValue，而且文本也存在于 node.nodeValue 中
			比如 div title="你好" ，要对 title 的 你好 这个值进行判定，传入的node必须是 title 的 node，而非 div 的 node
			它主要是为了给 translate.listener.addListener 中的动态监听node改变所服务的

			@param node 要判断的这个是否需要触发翻译的node
			@return boolean true：需要触发 translate.execute(node) 进行翻译
		*/
		nodeValueChangeNeedTranslate: function(node){
			if(typeof(node) === 'undefined' || node === null){
				return false;
			}

			//是否是要加入翻译扫描触发执行，是则是true
			var addTranslateExecute = true;

			/*
				不会进入翻译的情况 - 
					1. 认为是有 translate.js 本身翻译导致的改变，不进行翻译
						取 translate.node.data 中的数据，当改变的node节点在其中找到了对应的数据后，进行判定
							1. 是整体翻译，且当前node改变后的内容，跟上次翻译后的结果一样，那说明当前node改变事件
							2. 不是整体翻译，可能是触发自定义术语、或直接没启用整体翻译能力，那就要根据最后翻译时间这个来判定了。如果这个node元素，已经被翻译过了，最后一次翻译渲染时间，距离当前时间不超过500毫秒
					2. 其他的情况如果后续发现有遗漏，再加入，当前没有这种考虑
				*/	
			if(translate.node.get(node) != null){
				//根据现实结果来判断是否是有translate.js 本身翻译导致的dom改变
				if(typeof(translate.node.get(node).translateResults) !== 'undefined' && typeof(translate.node.get(node).translateResults[node.nodeValue]) === 'number'){
					//是translate.js翻译导致的dom文字改变
					addTranslateExecute = false;
				}
				
				if(addTranslateExecute === true){
					if(typeof(translate.node.get(node).whole) !== 'undefined' && translate.node.get(node).whole === true){
						//整体翻译
						if(typeof(translate.node.get(node).resultText) !== 'undefined' && translate.node.get(node).resultText === node.nodeValue){
							//当前改变后的内容，跟上次翻译后的结果一样，那说明当前node改变事件，是有translate.js 本身翻译导致的，不进行翻译
							addTranslateExecute = false;
						}
					}else{
						//不是整体翻译，可能是触发自定义术语、或直接没启用整体翻译能力

						//这就要根据最后翻译时间这个来判定了 -- 这个计划要剔除，因为本身在 translate.node.get(node).translateResults 已经判定了，这个属于重复判定。 这个先留一段时间
						if(typeof(translate.node.get(node).lastTranslateRenderTime) === 'number' && translate.node.get(node).lastTranslateRenderTime + 30 > Date.now()){
							//如果这个node元素，已经被翻译过了，最后一次翻译渲染时间，距离当前时间不超过500毫秒，那认为这个元素动态改变，是有translate.js 本身引起的，将不做任何动作	
							addTranslateExecute = false;
						}
					}
				}
			}

			//如果新的里面没有非空白字符的值，那也不再触发翻译
			if(addTranslateExecute === true){
				if(node.nodeValue.trim().length === 0){
					addTranslateExecute = false;
				}
			}

			return addTranslateExecute;
		},


		//增加监听，开始监听。这个不要直接调用，需要使用上面的 start() 开启
		addListener:function(){
			if(translate.listener.isStart == true){
				//console.log('translate.listener.start() 已经启动了，无需再重复启动监听，增加浏览器负担');
				return;
			}
			translate.listener.isStart = true; //记录已执行过启动方法了
			//console.log('translate.listener.addListener() ...');

			// 观察器的配置（需要观察什么变动）
			translate.listener.config = { attributes: true, childList: true, subtree: true, characterData: true, attributeOldValue:true, characterDataOldValue:true };
			// 当观察到变动时执行的回调函数
			translate.listener.callback = function(mutationsList, observer) {
				var documents = []; //有变动的元素
				//console.log('--------- lisetner 变动');
				//console.log(mutationsList);
			    // Use traditional 'for loops' for IE 11
			    for(let mutation of mutationsList) {
			    	let addNodes = [];
					if (mutation.type === 'childList') {
						if(mutation.addedNodes.length > 0){
							//多了组件
							for(var ani = 0; ani < mutation.addedNodes.length; ani++){
								var addNodeName = translate.element.getNodeName(mutation.addedNodes[ani]).toLowerCase();
								if(addNodeName === 'iframe'){	//如果是iframe，还要进行注入进去翻译
									//console.log(mutation.addedNodes[ani]);
									if(typeof(translate.element.iframe) !== 'undefined'){
										translate.element.iframe.execute(mutation.addedNodes[ani]);
									}
								}
								if(addNodeName.length > 0 && translate.ignore.tag.indexOf(addNodeName) == -1){
									// 使用现有的忽略机制检查节点
									//var addedNode = mutation.addedNodes[ani];
									//if(!translate.element.isIgnore(addedNode)){
										addNodes.push(mutation.addedNodes[ani]);
									//}
								}
							}
							//addNodes = mutation.addedNodes;
							//documents.push.apply(documents, mutation.addedNodes);
						}
						if(mutation.removedNodes.length > 0){
							//console.log('remove:');
							//console.log(mutation.removedNodes);
							for(var ri = 0; ri < mutation.removedNodes.length; ri++){
								//console.log('listener --  mutation.type === childList -- delete node ----: ');
								//console.log(mutation.removedNodes[ri]);
								translate.node.delete(mutation.removedNodes[ri]); //删除掉被dom给移除的节点，比如执行了 InnerHTML 操作的元素会自动删除
							}
						}
					}else if (mutation.type === 'attributes') {
						if(mutation.attributeName === 'class' || mutation.attributeName === 'style'){
							//如果是class/ style 这种常见的，不做任何改变，直接跳出
							continue;
						}
						
						/*
							这里要判断一些允许翻译的属性
							input 的 placeholder 属性 ,直接判断 placeholder 就行了，也就 input、textarea 有这个属性
							img 的 alt 属性
							所有标签的 title 属性
						*/

						if(mutation.attributeName === 'placeholder' || mutation.attributeName === 'alt' || mutation.attributeName === 'title'){
							//允许翻译
						}else{
							
							var nodeNameLowerCase = mutation.target.nodeName.toLowerCase();
							
							//判断是否是 iframe 的，允许翻译
							if(nodeNameLowerCase === 'iframe' && typeof(mutation.attributeName) === 'string' && mutation.attributeName.toLowerCase() === 'src'){
								//iframe 改变了src，那么iframe会重新加载新网页，针对这个新网页，也要监听
								if(typeof(translate.element.iframe) !== 'undefined'){
									translate.element.iframe.execute(mutation.target);
								}
							}
							
							
							//判断是否是 translate.element.tagAttribute 自定义翻译属性的
							var divTagAttribute = translate.element.tagAttribute[nodeNameLowerCase];
							//console.log('divTagAttribute:'+divTagAttribute);
							if(typeof(divTagAttribute) !== 'undefined' && divTagAttribute.attribute.indexOf(mutation.attributeName) > -1 && divTagAttribute.condition(mutation.target)){
								//是自定义翻译这个属性的，以及判定是否达到翻译条件
								//条件满足，允许翻译
							}else{
								//条件不满足，不在翻译的属性范围
								continue;
							}
							
						}

						//这里出现的 mutation.target 是定位到了元素上面，而不是变化的这个 attributes 属性上，需要用 mutation.attributeName 获取到这个属性的node
						var node = mutation.target.getAttributeNode(mutation.attributeName);
						
						//是否是要加入翻译扫描触发执行，是则是true
						var addTranslateExecute = translate.listener.nodeValueChangeNeedTranslate(node);
						if(addTranslateExecute){ //不是 translate.js 翻译引起的改变，那么
							//console.log('listener attributes change ' + mutation.target.nodeName+'['+ mutation.attributeName + '] '+mutation.oldValue+' --> '+node.nodeValue);
							translate.node.delete(node); 
							addNodes = [node]; //将这个属性转为的node加入待翻译
						}
					}else if(mutation.type === 'characterData'){
						//内容改变
						
						//是否是要加入翻译扫描触发执行，是则是true
						var addTranslateExecute = translate.listener.nodeValueChangeNeedTranslate(mutation.target);

						if(addTranslateExecute){ //不是 translate.js 翻译引起的改变，那么
							translate.node.delete(mutation.target); 
							addNodes = [mutation.target]; //将重新触发 translate.execute();
							//console.log('listener - mutation.type === \'characterData\' , node: ');
							//console.log(mutation.target)
						}
						
						//documents.push.apply(documents, [mutation.target]);
					}

					//去重并加入 documents
					for(let item of addNodes){
						//console.log(item);

						//判断是否已经加入过了，如果已经加入过了，就不重复加了
						var isFind = false;
						for(var di = 0; di < documents.length; di++){
							if(documents[di].isSameNode(item)){
								isFind = true;
								break;
							}
						}
						if(isFind){
							break;
						}
						documents.push.apply(documents, [item]);
					}
	          	}
			    
			    if(documents.length > 0){
					//有变动，需要看看是否需要翻译，延迟10毫秒执行
					translate.time.log('监听到元素发生变化,'+documents.length+'个元素');
					translate.execute(documents);					
				}
			};
			// 创建一个观察器实例并传入回调函数
			translate.listener.observer = new MutationObserver(translate.listener.callback);
			// 以上述配置开始观察目标节点
			var docs = translate.getDocuments();
			for(var docs_index = 0; docs_index < docs.length; docs_index++){
				var doc = docs[docs_index];
				if(doc != null){
					translate.listener.observer.observe(doc, translate.listener.config);
				}
			}

			
			//如果要对 input 的value进行翻译，那么还要监听 input 的 value 的值
			if(typeof(translate.element.tagAttribute['input']) === 'object' && translate.element.tagAttribute['input'].attribute.indexOf('value') > -1){
				translate.listener.input.start();
			}


		},
		/*
			每当执行完一次渲染任务（翻译）时会触发此。注意页面一次翻译会触发多个渲染任务。普通情况下，一次页面的翻译可能会触发两三次渲染任务。
			另外如果页面中有ajax交互方面的信息，时，每次ajax信息刷新后，也会进行翻译，也是一次渲染任务。
			这个是为了方便扩展使用。比如在layui中扩展，监控 select 的渲染
		*/
		renderTaskFinish:function(renderTask){
			//console.log(renderTask);
		},

		/*
            翻译执行过程中，相关的监控
        */
        execute:{

            /*
                每当触发执行 translate.execute() 时，当缓存中未发现，需要请求翻译API进行翻译时，在发送API请求前，触发此

                @param uuid：translate.nodeQueue[uuid] 这里的
                @param from 来源语种，翻译前的语种
				@param to 翻译为的语种
            */
            renderStartByApi : [],
            renderStartByApiRun:function(uuid, from, to){
                //console.log(translate.nodeQueue[uuid]);
                for(var i = 0; i < translate.listener.execute.renderStartByApi.length; i++){
                    try{
                        translate.listener.execute.renderStartByApi[i](uuid, from, to);
                    }catch(e){
                        translate.log(e);
                    }
                }
            },

            /*
                每当 translate.execute() 执行完毕（前提是采用API翻译的，API将翻译结果返回，并且界面上的翻译结果也已经渲染完毕）后，触发此方法。

                @param uuid：translate.nodeQueue[uuid] 这里的
                @param from 来源语种，翻译前的语种
				@param to 翻译为的语种
            */
            renderFinishByApi : [],
            renderFinishByApiRun:function(uuid, from, to){
                //console.log(translate.nodeQueue[uuid]);
                for(var i = 0; i < translate.listener.execute.renderFinishByApi.length; i++){
                    try{
                    	translate.listener.execute.renderFinishByApi[i](uuid, from, to);
                    }catch(e){
                        translate.log(e);
                    }
                }
            }
        }

	},
	//对翻译结果进行替换渲染的任务，将待翻译内容替换为翻译内容的过程
	renderTask:class{
		constructor(){
			/*
			 * 任务列表
			 * map
			 * key: node
			 * value: [task,task,...]  是多个task的数组集合，存放多个 task，每个task是一个替换。这里的数组是同一个nodeValue的多个task替换
			 * 				每个 task:  
			 					task['originalText'] 
			 					task['resultText'] 存放要替换的字符串
			 					task['attribute'] 存放要替换的属性，比如 a标签的title属性。 如果是直接替换node.nodeValue ，那这个没有
			 */
			this.taskQueue = new Map();
			
			/*
			 * 要进行翻译的node元素，
			 * 一维数组 key:node.nodeValue 的 hash ， value:node的元素数组
			 * 二维数组，也就是value中包含的node集合 [node,node,...]
	 		 */
			this.nodes = [];
		}
		
		/**
		 * 向替换队列中增加替换任务
		 * node:要替换的字符属于那个node元素
		 * originalText:待翻译的字符
		 * resultText:翻译后的结果字符
		 * attribute: 要替换的是哪个属性，比如 a标签的title属性，这里便是传入title。如果不是替换属性，这里不用传入，或者传入null
		 * participles: 分词，数组形态。默认不传则是没有其他分词需要保留的。 传入比如  ['你好','你是谁'] 
        		比如 translateOriginal 传入 '你' 时， text 中的 '你好','你是谁' 是不能被拆出'你'这个字进行替换的，不然就破坏了分词了
		 */
		add(node, originalText, resultText, attribute, participles){
			//console.log('renderTask.add : originalText:'+originalText+', resultText:'+resultText+', attribute:'+attribute+', node:');
			//console.log(node);
			var nodeAnaly = translate.element.nodeAnalyse.get(node, attribute); //node解析
			//var hash = translate.util.hash(translate.element.getTextByNode(node)); 	//node中内容的hash
			var hash = translate.util.hash(nodeAnaly['text']);
			//console.log('--------------'+hash);
			//console.log(nodeAnaly);

			/****** 加入翻译的元素队列  */
			if(typeof(this.nodes[hash]) == 'undefined'){
				this.nodes[hash] = new Array();
			}
			this.nodes[hash].push(node);
			//console.log(node)
			
			/****** 加入翻译的任务队列  */
			//var tasks = this.taskQueue[hash];
			var tasks = this.taskQueue.get(node);
			if(tasks == null || typeof(tasks) == 'undefined'){
				//console.log(node.nodeValue);
				tasks = new Array(); //任务列表，存放多个 task，每个task是一个替换。这里的数组是同一个nodeValue的多个task替换
			}
			var task = new Array();
			
			//v2.3.3 增加 -- 开始
			//这里要进行处理，因为有时候翻译前，它前或者后是有空格的，但是翻译后会把前或者后的空格给自动弄没了，如果是这种情况，要手动补上
			if (originalText.substr(0, 1) == ' ') {
				//console.log('第一个字符是空格');
				if(resultText.substr(0, 1) != ' '){
					//翻译结果的第一个字符不是空格，那么补上
					resultText = ' ' + resultText;
				}
			}
			if (originalText.substr(originalText.length - 1, 1) === ' ') {
				//console.log('最后一个字符是空格');
				if(resultText.substr(0, 1) != ' '){
					//翻译结果的最后一个字符不是空格，那么补上
					resultText = resultText + ' ';
				}
			}
			//v2.3.3 增加 -- 结束

			task['originalText'] = originalText;
			task['resultText'] = resultText;
			task['attribute'] = attribute;
			task['participles'] = participles;
			

			//console.log(task);
			tasks.push(task);
			//this.taskQueue[hash] = tasks;
			this.taskQueue.set(node, tasks);
			/****** 加入翻译的任务队列 end  */
		}
		//进行替换渲染任务，对页面进行渲染替换翻译
		execute(){
			//先对tasks任务队列的替换词进行排序，将同一个node的替换词有大到小排列，避免先替换了小的，大的替换时找不到
			//for(var hash in this.taskQueue){
			for (let node of this.taskQueue.keys()) {
				var tasks = this.taskQueue.get(node);
				if (tasks == null) {
		    		continue;
		    	}
				if(typeof(tasks) == 'function'){
					//进行异常的预处理调出
					continue;
				}

				//进行排序,将原字符串长的放前面，避免造成有部分不翻译的情况（bug是先翻译了短的，导致长的被打断而无法进行适配）
				tasks.sort((a, b) => b.originalText.length - a.originalText.length);
				
				//this.taskQueue[hash] = tasks;
				this.taskQueue.set(node, tasks);
			}
			
			//console.log('===========task=========');
			//console.log(this.taskQueue);
			//console.log(this.nodes);
			//console.log('===========task======end===');

			
			//对nodeQueue进行翻译
			for(var hash in this.nodes){
				if (!this.nodes.hasOwnProperty(hash)) {
		    		continue;
		    	}
		    	
				//var tasks = this.taskQueue[hash]; //取出当前node元素对应的替换任务
				//var tagName = this.nodes[hash][0].nodeName; //以下节点的tag name
				//console.log(tasks);
				for(var node_index = 0; node_index < this.nodes[hash].length; node_index++){
					//当前翻译的node
					var node = this.nodes[hash][node_index];

					//取出当前node元素对应的替换任务
					var tasks = this.taskQueue.get(node);
					//console.log(tasks);
					if (tasks == null) {
						translate.log('WARNING : renderTask.execute 中，this.taskQueue.get(node) == null ，理论上要进行替换任务，就应该会有内容的，数据在理论上出现异常');
			    		continue;
			    	}

					//对这个node元素进行替换翻译字符
					for(var task_index=0; task_index<tasks.length; task_index++){
						var task = tasks[task_index];
						if(typeof(tasks) == 'function'){
							//进行异常的预处理调出
							continue;
						}
						

						// translate.node 记录
						
						var translateNode = null; //当前操作的，要记录入 translate.node 中的，进行翻译的node
						var translateNode_attribute = ''; //当前操作的是node中的哪个attribute，如果没有是node本身则是空字符串
						if(typeof(task['attribute']) === 'string' && task['attribute'].length > 0){
							//当前渲染任务是针对的元素的某个属性，这是要取出这个元素的具体属性，作为一个目的 node 来进行加入 translate.node 
							//是操作的元素的某个属性,这时要判断 是否是 input、textarea 的value属性
							if(task['attribute'] === 'value'){
								var nodeNameLowerCase = translate.element.getNodeName(this.nodes[hash][node_index]).toLowerCase();
								if((nodeNameLowerCase === 'input' || nodeNameLowerCase === 'textarea')){
									translateNode = this.nodes[hash][node_index];
									translateNode_attribute = 'value';
								}
							}
							if(translateNode === null){
								translateNode = this.nodes[hash][node_index].getAttributeNode(task['attribute']);
								translateNode_attribute = task['attribute'];
							}
						}else{
							//操作的就是node本身
							translateNode = this.nodes[hash][node_index];
						}
						//console.log(translateNode)
						//var nodeAttribute = translate.node.getAttribute(task['attribute']);
						if(typeof(translate.node.data.get(translateNode)) === 'undefined' || translate.node.data.get(translateNode) === null){
							translate.log('执行异常，渲染时，node 未在 translate.node 中找到, 这个很有可能是点击过快，上一个翻译任务还在网络请求中，又点击了别的地方导致内容又被改变。当前异常已被容错。 node：');
							translate.log(translateNode);

							var getNodeText = translate.element.nodeAnalyse.get(node, task['attribute']);
							translate.node.set(translateNode, {
								attribute: translateNode_attribute,
								originalText: getNodeText.text,
								whole: true,
								translateTexts: {}
							});
							translate.node.setModified(translateNode, 'create:translate.renderTask.execute');
						}

						// 记录当前有 translate.js 所触发翻译之后渲染到dom界面显示的时间，13位时间戳
						translate.node.get(translateNode).lastTranslateRenderTime = Date.now();


						//渲染页面进行翻译显示
						//console.log(task.originalText+' ('+task['attribute']+') --> ' + task.resultText+', node:');
						//console.log(node);
						//console.log(typeof(task['participles']) === 'undefined'? []:task['participles'])
						var analyseSet = translate.element.nodeAnalyse.set(node, task.originalText, task.resultText, task['attribute'], typeof(task['participles']) === 'undefined'? []:task['participles']);
						//console.log(analyseSet);

						if(translate.node.data.get(translateNode) != null){
							//将具体通过文本翻译接口进行翻译的文本记录到 translate.node.data
							translate.node.get(translateNode).translateTexts[task.originalText] = task.resultText;
							//将翻译完成后要显示出的文本进行记录
							translate.node.get(translateNode).resultText = analyseSet.resultText;

							//将其加入 translate.history.translateTexts 
							translate.history.translateText.add(translate.node.get(translateNode).originalText ,analyseSet.resultText);
						}
						
						
						/*
						//var tagName = translate.element.getTagNameByNode(this.nodes[hash][task_index]);//节点的tag name
						//console.log(tagName)
						//console.log(this.nodes[hash][task_index])
						//var tagName = this.nodes[hash][task_index].nodeName; //节点的tag name
						var nodename = translate.element.getNodeName(this.nodes[hash][task_index]);
						
						//console.log(this.nodes[hash][task_index]+', '+task.originalText+', '+task.resultText+', tagName:'+tagName);
						if(nodename == 'META'){
							if(typeof(this.nodes[hash][task_index].name) != 'undefined' && this.nodes[hash][task_index].name != null){
								//var nodeName = this.nodes[hash][task_index].name.toLowerCase();  //取meta 标签的name 属性
								
								this.nodes[hash][task_index].content = this.nodes[hash][task_index].content.replace(new RegExp(translate.util.regExp.pattern(task.originalText),'g'), translate.util.regExp.resultText(task.resultText));
							}
						}else if(nodename == 'IMG'){
							this.nodes[hash][task_index].alt = this.nodes[hash][task_index].alt.replace(new RegExp(translate.util.regExp.pattern(task.originalText),'g'), translate.util.regExp.resultText(task.resultText));
						}else{
							//普通的
							//console.log('task.originalText : '+task.originalText);
							//console.log(translate.util.regExp.pattern(task.originalText))
							//console.log('task.resultText : '+task.resultText);
							this.nodes[hash][task_index].nodeValue = this.nodes[hash][task_index].nodeValue.replace(new RegExp(translate.util.regExp.pattern(task.originalText),'g'), translate.util.regExp.resultText(task.resultText));
						}
						*/
					}
				}
			}
			
			//console.log('---listen');

			//监听 - 增加到翻译历史里面 nodeHistory
			var taskQueueSize = 0;
			if(typeof(this.taskQueue) != 'undefined' && this.taskQueue != null){
				// taskQueue 当前是 Map，必须使用 size 判断任务数量；保留 Object.keys 兜底，
				// 避免后续扩展把 taskQueue 换成普通对象时影响 renderTaskFinish 的触发。
				taskQueueSize = typeof(this.taskQueue.size) == 'number' ? this.taskQueue.size : Object.keys(this.taskQueue).length;
			}
			if(taskQueueSize > 0){
				//50毫秒后执行，以便页面渲染完毕
				var renderTask = this;
				setTimeout(function() {
					/** 执行完成后，触发用户自定义的翻译完成执行函数 **/
					translate.listener.renderTaskFinish(renderTask);
				}, 5);
				
			}else{
				//console.log(this.taskQueue);
				//console.log('---this.taskQueue is null');
			}
		}
	},


	/*
		当前状态，执行状态
		0 空闲(或者执行翻译完毕)
		2 translate.execute 触发，立即变为3，然后再执行 translate.execute 的一些初始化自检啥的
		10 扫描要翻译的node，并读取浏览器缓存的翻译内容进行渲染显示
		20 浏览器缓存渲染完毕，ajax通过文本翻译接口开始请求，在发起ajax请求前，状态变为20，然后再发起ajax请求
		至于翻译完毕后进行渲染，这个就不单独记录了，因为如果页面存在不同的语种，不同的语种是按照不同的请求来的，是多个异步同时进行的过程
	*/
	state:0,


	/*
		等待翻译队列  v3.12.6 增加
		当前是否有需要等待翻译的任务，这个目的是为了保证同一时间 translate.execute() 只有一次在执行，免得被新手前端给造成死循环，导致edge翻译给你屏蔽，用户网页还卡死
		当执行 translate.execute() 时，会先判断状态 translate.state 是否是0空闲的状态，如果空闲，才会执行，如果不是空闲，则不会执行，而是进入到这里进行等待，等待执行完毕后 translate.state 变成0空闲之后，再来执行这里的
		
	*/
	waitingExecute:{
		use:true, //默认是使用，自有部署场景不担心并发的场景，可以禁用，以提高用户使用体验。

		/*
			一维数组形态，存放执行的翻译任务
			二维对象形态，存放执行传入的 docs
		*/
		queue:[],
		//当前队列调度器的 intervalId。 null 表示未启动调度器
		intervalId:null,
		/*
			增加一个翻译任务到翻译队列中
			docs 同 translate.execute(docs) 的传入参数
		 */ 
		add:function(docs){
			//向数组末尾追加
			translate.waitingExecute.queue.push(docs);
			//调度器已启动，那么只需要入队即可，避免多个 interval 并发竞争同一个队列
			if(translate.waitingExecute.intervalId !== null){
				return;
			}
			//开启唯一一个定时器进行触发
			translate.waitingExecute.intervalId = setInterval(function() {
				if(translate.waitingExecute.queue.length < 1){
					//队列已空，关闭调度器
					clearInterval(translate.waitingExecute.intervalId);
					translate.waitingExecute.intervalId = null;
					return;
				}
				if(translate.state == 0){
					var docs = translate.waitingExecute.get();
					if(docs == null){
						clearInterval(translate.waitingExecute.intervalId);
						translate.waitingExecute.intervalId = null;
						return;
					}
					translate.execute(docs);
				}
			}, 500);
		},
		/*
			从 quque 中取第一个元素，同时将其从queue中删除掉它。
			如果取的时候 quque已经没有任何元素了，会返回 null， 但是理论上不会出现null
		 */
		get:function(){
			//使用 shift 方法删除数组的第一个元素，并将第一个元素的值返回
			if(translate.waitingExecute.queue.length > 0){
				return translate.waitingExecute.queue.shift();
			}else{
				translate.log('警告， translate.waitingExecute.get 出现异常，quque已空，但还往外取。');
				return null;
			}
		},
		/*
			当前 translate.translateRequest[uuid] 的是否已经全部执行完毕
			这里单纯只是对 translate.translateRequest[uuid] 的进行判断
			这里要在 translate.json 接口触发完并渲染完毕后触发，当然接口失败时也要触发。

			正常情况下，是根据本地语言不同，进行分别请求翻译的，比如本地中包含中文、英文、俄语三种语种，要翻译为韩语，那么
				* 中文->韩语会请求一次api
				* 英文->韩语会请求一次APi
				* 俄语->韩语会请求一次APi
			也就会触发三次

			@param uuid translate.translateRequest[uuid]中的uuid，也是 translate.nodeQueue 中的uuid
			@param from 来源语种，翻译前的语种
			@param to 翻译为的语种
			@param result 本次网络请求的结果， 1成功， 0失败。  网络不通，翻译结果返回result非1都是记入0
			@param info 如果result为0，这里是失败信息
		*/
		isAllExecuteFinish:function(uuid, from, to, result, info){

			translate.listener.execute.renderFinishByApiRun(uuid, from, to);

			//通过 uuid、from 取得本次翻译相关的 texts、nodes , 触发 translateNetworkAfter_Trigger 钩子
			//获取请求日志
			var requestData = translate.request.data[uuid].list[from][to];
			translate.lifecycle.execute.translateNetworkAfter_Trigger({
				uuid: uuid,
				from: from,
				to: to,
				texts: requestData.texts,
				nodes: requestData.nodes,
				result: result,
				info: info
			});

			//console.log('uuid:'+uuid+', from:'+from+', to:'+to);
			for(var lang in translate.translateRequest[uuid]){
				if (!translate.translateRequest[uuid].hasOwnProperty(lang)) {
		    		continue;
		    	}
				if(translate.translateRequest[uuid][lang].executeFinish == 0){
					//这个还没执行完，那么直接退出，不在向后执行了
					//console.log('uuid:'+uuid+'  lang:'+lang+'  executeFinish:0  time:'+translate.translateRequest[uuid][lang][i][addtime]);
					
					//这里要考虑进行时间判断

					return;
				}
			}

			//生命周期触发事件
			translate.lifecycle.execute.renderFinish_Trigger(uuid, to);

			//都执行完了，那么设置完毕
			translate.state = 0;
			translate.executeNumber++;
		}

	},
	
	//execute() 方法已经被执行过多少次了， 只有 translate.execute() 完全执行完，也就是界面渲染完毕后，它才会+1
	executeNumber:0,
	//translate.execute() 方法已经被触发过多少次了， 只要 translate.execute() 被触发，它就会在触发时立即 +1 (translate.execute() 默认是同一时刻只能执行一次，这个触发是在这个同一时刻执行一次的判定之前进行++ 的，如果这个同一时刻执行一次不通过，还有其他在执行，进入排队执行时，这里也会++ ，当从排队的中顺序排到进行执行时，又会执行++ ) 。 当页面打开第一次触发执行translate.execute()，这里便是 1
	executeTriggerNumber:0, 
	
	lifecycle:{

		/*
		 * 切换语言 
		 */
		changeLanguage:{

			/*
				当触发 translate.changeLanguage(...) 时，会立即先触发此，再去执行 translate.changeLanguage(...) 的处理
			*/
			trigger:[],
			trigger_Trigger:function(data){
				for(var i = 0; i < translate.lifecycle.changeLanguage.trigger.length; i++){
	                try{
	                    translate.lifecycle.changeLanguage.trigger[i](data);
	                }catch(e){
	                    translate.log(e);
	                }
	            }

	            //兼容旧版本的
	            for(var i = 0; i < translate.lifecycle.changeLanguage.old_trigger_array.length; i++){
	                try{
	                    translate.lifecycle.changeLanguage.old_trigger_array[i](data.to);
	                }catch(e){
	                    translate.log(e);
	                }
	            }
			},
			/*
				下面这两个是兼容以前版本的
			*/
			//通过 push 加入的，只会加入到 old_trigger_array 中，传入参数是 to，也就是 string格式
			old_trigger_array:[],
			push: function(func){
				translate.log('提示， translate.lifecycle.changeLanguage.push 方式已过时，但依旧生效，可正常使用。 最新的方式，文档参考 http://translate.zvo.cn/540189.html ');
				translate.lifecycle.changeLanguage.trigger.push(func);
				translate.lifecycle.changeLanguage.old_trigger_array.push(func);
			},



			/*
				在触发 translate.reset() 之后、 执行切换语言动作之前，进行触发
				{
					to: 			//翻译为的语种
				}
			*/
			resetAfter:[],
			resetAfter_Trigger:function(data){
				var isNextExecute = true; //是否继续向下执行，true则是继续执行，false则是不继续执行。 
            	for(var i = 0; i < translate.lifecycle.changeLanguage.resetAfter.length; i++){
            		try{
                        translate.lifecycle.changeLanguage.resetAfter[i](data);
                    }catch(e){
                        translate.log(e);
                    }
                }
            },

		},

		/*
			translate.execute() 执行相关
		*/
		execute:{
			/*
                每当触发执行 translate.execute() 时，会直接触发此。  
                这个触发是指在所有判断之前，也就是只要 触发了 translate.execute() 会立即触发此，然后在进行执行其他的。
                {
					to: ,			//翻译为的语种
					docs: 			//当前触发 translate.execute() 要进行翻译的元素。
										比如单纯触发执行 translate.execute() 、translate.request.listener.start()  那么这里 docs 则是 通过 translate.setDocuments(...) 所设置的元素。 如果没有使用 translate.setDocuments(...) 设置过，那就是翻译整个html页面。
										如果是 translate.listener.start(); 监控页面发生变化的元素进行翻译，则这里的docs 则是发生变化的元素
					executeTriggerNumber:  整数型，当前触发 translate.execute() 执行，属于打开页面后第几次执行 translate.execute() ， 它不会经过任何初始化判断，只要触发了 translate.execute() 就会立即+1，即使初始化判断当前不需要翻译、或者当前正在翻译需要排队等待，它依旧也会+1
					
				}
               	
               	注意，它有返回参数，boolean 类型：
               		true 则是继续执行 translate.execute() 
               		false 则是不继续执行，直接终止本次的 translate.execute() 也就是后面的 translate.lifecycle.execute.start 都不会执行到，不会触发。
					如果钩子没有任何返回值，则默认是 true

               		如果本钩子有多个实现，其中某个实现返回 false，它不会阻止其他钩子的执行，其他的钩子实现也都会触发执行。 只不过里面只要其中有一个是返回 false，那么 translate.execute() 都会终止。
            */
			trigger: [],
			trigger_Trigger:function(data){
				var isNextExecute = true; //是否继续向下执行，true则是继续执行，false则是不继续执行。 
            	for(var i = 0; i < translate.lifecycle.execute.trigger.length; i++){
            		try{
                        var isNext = translate.lifecycle.execute.trigger[i](data);
                        if(typeof(isNext) === 'boolean' && isNext === false){
                        	isNextExecute = false;
                        }
                    }catch(e){
                        translate.log(e);
                    }
                }
                return isNextExecute;
            },

			/*
                每当触发执行 translate.execute() 时，会先进行当前是否可以正常进行翻译的判定，比如 当前语种是否就已经是翻译之后的语种了是否没必要翻译了等。（这些初始判定可以理解成它的耗时小于1毫秒，几乎没有耗时）
                经过初始的判断后，发现允许被翻译，那么在向后执行之前，先触发此。  
                也就是在进行翻译之前，触发此。 

				{
					uuid: ,			//translate.nodeQueue[uuid] 这里的
					to: ,			//翻译为的语种
				}
               
            */
            start : [],
            //start_Trigger:function(uuid, to){
            start_Trigger:function(data){
            	for(var i = 0; i < translate.lifecycle.execute.start.length; i++){
            		if(translate.lifecycle.execute.start[i].length === 2){
            			//原本的，旧版 20250925 之前的，是string方式传入 uuid, to 这2个参数
            			try{
	                        translate.lifecycle.execute.start[i](data.uuid, data.to);
	                    }catch(e){
	                        translate.log(e);
	                    }
            		}else{
            			try{
	                        translate.lifecycle.execute.start[i](data);
	                    }catch(e){
	                        translate.log(e);
	                    }
            		}
                    
                }
            },


			//待整理
            start_old : [],
            startRun:function(uuid, from, to){
                //console.log(translate.nodeQueue[uuid]);
                for(var i = 0; i < translate.listener.execute.renderStartByApi.length; i++){
                    try{
                        translate.listener.execute.renderStartByApi[i](uuid, from, to);
                    }catch(e){
                        translate.log(e);
                    }
                }
            },

            /*
                当扫描整个节点完成，进行翻译（1. 命中本地缓存、 2.进行网络翻译请求）之前，触发
                待整理
			 */
            scanNodesFinsh: [],

            
            /*
                每当触发执行 translate.execute() 时，当缓存中未发现，需要请求翻译API进行翻译时，在发送API请求前，触发此
				
				{
					uuid: ,			//translate.nodeQueue[uuid] 这里的
					lang: 			//来源语种，翻译前的语种
					to: ,			//翻译为的语种
					texts: ,		//要翻译的文本，它是一个数组形态，是要进行通过API翻译接口进行翻译的文本，格式如 ['你好','世界']
					nodes: 			//要翻译的文本的node集合，也就是有哪些node中的文本参与了 通过API接口进行翻译文本，这里是这些node。 格式如 [node1, node2, ...]
				}

            */
            translateNetworkBefore:[],
            //translateNetworkBefore_Trigger:function(uuid, from, to, texts){
            translateNetworkBefore_Trigger:function(data){
            	/*
            	if(typeof(data) == 'string'){
            		data = {
            			uuid: data,
            		};
            	}
            	if(typeof(from) == 'string'){
            		data.from = from;
            	}
            	if(typeof(to) == 'string'){
            		data.to = to;
            	}
            	if(typeof(texts) == 'string'){
            		data.texts = texts;
            	}
            	*/

            	for(var i = 0; i < translate.lifecycle.execute.translateNetworkBefore.length; i++){
            		//console.log('translate.lifecycle.execute.translateNetworkBefore[i] 传入参数的数量：'+translate.lifecycle.execute.translateNetworkBefore[i].length);
            		if(translate.lifecycle.execute.translateNetworkBefore[i].length === 4){
            			//原本的，旧版 20250915 之前的，是string方式传入 uuid, from, to, texts 这四个参数
            			try{
	                        translate.lifecycle.execute.translateNetworkBefore[i](data.uuid, data.from, data.to, data.texts);
	                    }catch(e){
	                        translate.log(e);
	                    }
            		}else{
            			//2025.9.15 之后的新的
            			try{
	                        translate.lifecycle.execute.translateNetworkBefore[i](data);
	                    }catch(e){
	                        translate.log(e);
	                    }
            		}
                    
                }
            },

            /*
				当 translate.execute() 触发网络翻译请求完毕，并将翻译结果渲染到页面完毕后（不管网络翻译请求成功还是失败、还是翻译请求本身返回翻译失败），都触发此。
				
				{
					uuid: ,			//translate.nodeQueue[uuid] 这里的
					lang: 			//来源语种，翻译前的语种
					to: ,			//翻译为的语种
					texts: ,		//要翻译的文本，它是一个数组形态，是要进行通过API翻译接口进行翻译的文本，格式如 ['你好','世界']
					nodes: ,		//要翻译的文本的node集合，也就是有哪些node中的文本参与了 通过API接口进行翻译文本，这里是这些node。 格式如 [node1, node2, ...]
					result: 		//执行结果 1成功， 0失败
				}
            */
            translateNetworkAfter:[], 
            translateNetworkAfter_Trigger:function(data){
                for(var i = 0; i < translate.lifecycle.execute.translateNetworkAfter.length; i++){
                    try{
                        translate.lifecycle.execute.translateNetworkAfter[i](data);
                    }catch(e){
                        translate.log(e);
                    }
                }
            },
           


            /*
				translate.execute() 的翻译渲染完毕触发
				这个完毕是指它当触发 translate.execute() 进行翻译后，无论是全部命中了本地缓存，还是有部分要通过翻译接口发起多个网络请求，当拿到结果（缓存中的翻译结果或多个不同的有xx语种翻译的网络请求全部完成，这个完成是包含所有成功跟失败的响应），并完成将翻译结果渲染到页面中进行显示后，触发此
				它跟 translateNetworkFinish 的区别是， translateNetworkFinish 仅仅针对有网络请求的才会触发，而 renderFinish 是如果全部命中了浏览器本地缓存，无需发起任何网络翻译请求这种情况时，也会触发。
            	@param uuid translate.nodeQueue 的uuid
				@param to 当前是执行的翻译为什么语种
            */
            renderFinish:[function(uuid, to){ //这里默认带着一个触发翻译为英文后，自动对英文进行元素视觉处理，追加空格的
            	if(typeof(translate.visual) != 'undefined'){
            		translate.visual.adjustTranslationSpacesByNodequeueUuid(uuid);
            	}
            }],
            renderFinish_Trigger:function(uuid, to){
            	for(var i = 0; i < translate.lifecycle.execute.renderFinish.length; i++){
                    try{
                        translate.lifecycle.execute.renderFinish[i](uuid, to);
                    }catch(e){
                        translate.log(e);
                    }
                }
            },

            /*
                每当 translate.execute() 执行结束、中止、自检不通过跳出 ... 等，都会触发这个。  
                注意，不管在 translate.execute() 是否自检通过、不管是否进行了翻译、不管文本翻译API接口是否拿到翻译结果，只要 translate.execute 执行完毕或触发了什么自检不通过不再往下执行，都会触发这个。  
                这个仅仅只是用于 translate.execute() 从上而下执行完跳出时，进行触发的。
				
               	{
					uuid: translate.nodeQueue[uuid] 这里的。 如果当前没有进行正常翻译，比如自检失败不在执行跳出了，那这个将会返回空字符串 ''
					to:   翻译为什么语种，如果当前没有进行正常翻译，比如自检失败不在执行跳出了，那这个将会返回空字符串 ''
					state : 状态，用于判断是什么情况执行完的，整数型，取值有：
						1 当前翻译未完结，新翻译任务已加入等待翻译队列，待上个翻译任务结束后便会执行当前翻译任务
						3 没有指定翻译目标语言，不翻译
						5 本地语种跟要翻译的目标语种一样，且没有启用本地语种也强制翻译，那么当前不需要执行翻译，退出
						16 已经匹配完自定义术语跟离线翻译，但是用户设置了不掉翻译接口进行翻译，不在向后执行通过文本翻译接口进行翻译
						18 已经匹配完自定义术语跟离线翻译，此时所有要翻译的文本都已经匹配完了，没有在需要通过文本翻译接口进行翻译的了
						21 进行通过文本翻译API进行调用接口翻译时，某个语种的数据校验失败导致退出。 这个情况理论上应该不会出现，预留这个情况，后续将会剔除这个状态
						25 已通过文本翻译接口发起所有翻译请求，translate.execute 执行完毕。 （只是发起网络请求，不代表翻译完成，因为这里还没有等着拿到网络请求的响应结果，还处于网络请求的过程中）
					triggerNumber:	translate.execute() 方法已经被触发过多少次了， 只要 translate.execute() 被触发，它就会在触发时立即 +1 (translate.execute() 默认是同一时刻只能执行一次，这个触发是在这个同一时刻执行一次的判定之前进行++ 的，如果这个同一时刻执行一次不通过，还有其他在执行，进入排队执行时，这里也会++ ，当从排队的中顺序排到进行执行时，又会执行++ ) 。 当页面打开第一次触发执行translate.execute()，这里便是 1
               	}
            */
            finally : [],
            finally_Trigger:function(data){
            	//console.log(data)
            	for(var i = 0; i < translate.lifecycle.execute.finally.length; i++){
                    try{
                        translate.lifecycle.execute.finally[i](data);
                    }catch(e){
                        translate.log(e);
                    }
                }
            },
		}
	},

	/*translate.execute() start */
	/*
		执行翻译操作。翻译的是 nodeQueue 中的
		docs 如果传入，那么翻译的只是传入的这个docs的。传入如 [document.getElementById('xxx'),document.getElementById('xxx'),...]
			 如果不传入或者传入null，则是翻译整个网页所有能翻译的元素	
	 */ 
	execute:function(docs){
		translate.executeTriggerNumber = translate.executeTriggerNumber + 1;
		var triggerNumber = translate.executeTriggerNumber; //为了整个 translate.execute 的数据一致性，下面都是使用这个变量

		//每次执行execute，都会生成一个唯一uuid，也可以叫做队列的唯一标识，每一次执行execute都会创建一个独立的翻译执行队列
		var uuid = translate.util.uuid();
		translate.time.log('创建uuid:'+uuid);

		//如果页面打开第一次使用，先判断缓存中有没有上次使用的语种，从缓存中取出
		if(translate.to == null || translate.to == ''){
			var to_storage = translate.storage.get('to');
			if(to_storage != null && typeof(to_storage) != 'undefined' && to_storage.length > 0){
				translate.to = to_storage;
			}
		}

		/*
			进行翻译指定的node操作。优先级为：
			1. 这个方法已经指定的翻译 nodes
			2. setDocuments 指定的 
			3. 整个网页 
			其实2、3都是通过 getDocuments() 取，在getDocuments() 就对2、3进行了判断
		*/
		var all;
		if(typeof(docs) != 'undefined' && docs != null){
			if(typeof(docs.length) == 'undefined'){
				//不是数组，是单个元素
				all = new Array();
				all[0] = docs;
			}else{
				//是数组，直接赋予
				all = docs;
			}
		}else{
			//2、3
			all = translate.getDocuments();
		}


		//钩子
		var triggerIsNextExecute = translate.lifecycle.execute.trigger_Trigger({
		    to:translate.to,
		    docs: all,
		    executeTriggerNumber: triggerNumber,
		    uuid: uuid
		});
		if(!triggerIsNextExecute){
			//终止执行
			
			//钩子
			translate.lifecycle.execute.finally_Trigger({
			    uuid:uuid,
			    to:translate.to,
			    state: 2,
			    triggerNumber: triggerNumber
			});

			return;
		}
		


		if(translate.waitingExecute.use){
			if(translate.state != 0){
				var sliceDocString = '';

				if(typeof(docs) != 'undefined' && docs != null){
					var sliceDoc = docs.slice(0, 2);
				
					for(var di = 0; di < sliceDoc.length; di++){
						if(sliceDocString.length > 0){
							sliceDocString = sliceDocString + ', ';
						}
						if(sliceDoc[di].nodeType === 1){
							//元素
							sliceDocString = sliceDocString + ""+sliceDoc[di].tagName;
							if(typeof(sliceDoc[di].id) == 'string' && sliceDoc[di].id.length > 0){
								sliceDocString = sliceDocString + " id="+sliceDoc[di].id;
							}
							if(sliceDoc[di].getAttribute('class') != null && typeof(sliceDoc[di].getAttribute('class')) == 'string' && sliceDoc[di].getAttribute('class').length > 0){
								sliceDocString = sliceDocString + " class="+sliceDoc[di].getAttribute('class');
							}
						}else if(sliceDoc[di].nodeType === 2 || sliceDoc[di].nodeType === 3){
							//2属性 或 3文本节点
							sliceDocString = sliceDocString + sliceDoc[di].nodeValue.replaceAll(/\r?\n/g, '[换行符]');
						}
					}
					sliceDocString = ' ('+docs.length+')['+sliceDocString+(docs.length > 2 ? ', ...':'')+']';
				}
				

				translate.log('当前翻译未完结，新翻译任务已加入等待翻译队列，待上个翻译任务结束后便会执行当前翻译任务'+sliceDocString);
				translate.waitingExecute.add(docs);

				//钩子
				translate.lifecycle.execute.finally_Trigger({
				    uuid:uuid,
				    to:translate.to,
				    state: 4,
				    triggerNumber: triggerNumber
				});

				return;
			}
		}
		
		
		translate.state = 2;
		translate.time.log('触发');

		//init.json
		translate.request.initRequest();

		//console.log('translate.state = 1');
		if(typeof(docs) != 'undefined'){
			//execute传入参数，只有v2版本才支持
			translate.useVersion = 'v2';
		}
		
		if(translate.useVersion == 'v1'){
		//if(this.to == null || this.to == ''){
			//采用1.x版本的翻译，使用google翻译
			//translate.execute_v1();
			//return;
			//v2.5.1增加
			translate.log('提示：https://github.com/xnx3/translate 在 v2.5 版本之后，由于谷歌翻译调整，免费翻译通道不再支持，所以v1版本的翻译接口不再被支持，v1全线下架。考虑到v1已不能使用，当前已自动切换到v2版本。如果您使用中发现什么异常，请针对v2版本进行适配。');
			translate.useVersion = 'v2';
		}
		
		

		/****** 采用 2.x 版本的翻译，使用自有翻译算法 */
		

		
		//console.log('=====')
		//console.log(translate.nodeQueue);
		
		/* v2.4.3 将初始化放到了 translate.element.whileNodes 中，如果uuid对应的没有，则自动创建

		translate.nodeQueue[uuid] = new Array(); //创建
		translate.nodeQueue[uuid]['expireTime'] = Date.now() + 120*1000; //删除时间，10分钟后删除
		translate.nodeQueue[uuid]['list'] = new Array(); 
		*/
		//console.log(translate.nodeQueue);
		//console.log('=====end')
		
		
		
		translate.time.log('渲染出选择语言的select窗口-开始');
		//渲染select选择语言
		try{
			translate.selectLanguageTag.render();	
		}catch(e){
			translate.log(e);
		}
		
		translate.time.log('渲染出选择语言的select窗口-已完成');

		//判断是否还未指定翻译的目标语言
		if(translate.to == null || typeof(translate.to) == 'undefined' || translate.to.length == 0){
			//未指定，判断如果指定了自动获取用户本国语种了，那么进行获取
			if(translate.autoDiscriminateLocalLanguage){
				translate.executeByLocalLanguage();
			}else{
				//没有指定翻译目标语言、又没自动获取用户本国语种，则不翻译
				translate.state = 0;

				//钩子
				translate.lifecycle.execute.finally_Trigger({
				    uuid:uuid,
				    to:translate.to,
				    state: 6,
				    triggerNumber: triggerNumber
				});

				return;
			}
		}
		
		//判断本地语种跟要翻译的目标语种是否一样，如果是一样，那就不需要进行任何翻译
		if(translate.to == translate.language.getLocal()){
			if(translate.language.translateLocal){
				//这是自定义设置的允许翻译本地语种中，跟本地语种不一致的语言进行翻译

			}else{
				translate.state = 0;

				//钩子
				translate.lifecycle.execute.finally_Trigger({
				    uuid:uuid,
				    to:translate.to,
				    state: 8,
				    triggerNumber: triggerNumber
				});

				return;
			}
		}
		
		//初始化 translate.element.tagAttribute ，主要针对 v3.17.10 版本的适配调整，对 translate.element.tagAttribute  的设置做了改变，做旧版本的适配
		try{
			for(var te_tag in translate.element.tagAttribute){
				if (!translate.element.tagAttribute.hasOwnProperty(te_tag)) {
		    		continue;
		    	}
		    	if(translate.element.tagAttribute[te_tag] instanceof Array){
		    		//是 v3.17.10 之前版本的设置方式，要进行对旧版本的适配
		    		var tArray = translate.element.tagAttribute[te_tag];
		    		translate.element.tagAttribute[te_tag] = {
		    			attribute: tArray,
		    			condition: function(element){
							return true;
						}
		    		}
		    	}
			}  
		}catch(e){
			translate.log(e);
		}


		/********** 翻译进行 */
		
		translate.time.log('生命周期-触发翻译进行之前，用户自定义的钩子-开始');

		//生命周期-触发翻译进行之前，用户自定义的钩子
		translate.lifecycle.execute.start_Trigger({
			uuid:uuid,
			to: translate.to
		});
		translate.time.log('生命周期-触发翻译进行之前，用户自定义的钩子-完成');
		
		translate.time.log('进行图片翻译-开始');
		//先进行图片的翻译替换，毕竟图片还有加载的过程
		translate.images.execute();
		translate.time.log('进行图片翻译-完成');

		
		//console.log('----要翻译的目标元素-----');
		//console.log(all)
		
		if(all.length > 1500){
			translate.log('------tip------');
			translate.log('警告 translate.execute( docs ) 传入的docs.length 过大，超过1500，这不正常，当前 docs.length : '+all.length+' , 它依旧会正常进行执行，但会有性能损耗。 这个情况很可能是你用的某些框架，没有等dom渲染完就执行了translate.execute() ，同时启用了dom变动监听，导致监听到页面加载大量的DOM渲染； 另外也有可能你本身页面就是列表页或者什么原因就是有大量的动态元素获取后渲染，如果是这种情况，这是正常的。');
		}


		translate.time.log('开始扫描要翻译区域的元素');
		//检索目标内的node元素
		for(var i = 0; i< all.length & i < 15000; i++){
			var node = all[i];
			translate.element.whileNodes(uuid, node);	
		}
		translate.time.log('扫描要翻译区域的元素完成');

		/***** translate.language.translateLanguagesRange 开始 *****/
		if(translate.language.translateLanguagesRange.length > 0){
			//如果大于0，则是有设置，那么只翻译有设置的语种，不在设置中的语种不会参与翻译
			for(var lang in translate.nodeQueue[uuid].list){
				if (!translate.nodeQueue[uuid].list.hasOwnProperty(lang)) {
		    		continue;
		    	}
				if(translate.language.translateLanguagesRange.indexOf(lang) < 0){
					//删除这个语种
					delete translate.nodeQueue[uuid].list[lang];
				}
			}
		}
		
		/***** translate.language.translateLanguagesRange 结束 *****/
		
		//修复如果translate放在了页面最顶部，此时执行肯定扫描不到任何东西的，避免这种情况出现报错
		if(typeof(translate.nodeQueue[uuid]) == 'undefined'){
			translate.nodeQueue[uuid] = new Array();
			translate.nodeQueue[uuid].list = [];
			translate.log('--- translate.js warn tip 警告！！ ---');
			translate.log('您使用translate.js时可能放的位置不对，不要吧 translate.js 放在网页最顶部，这样当 translate.js 进行执行，也就是 translate.execute() 执行时，因为网页是从上往下加载，它放在网页最顶部，那么它执行时网页后面的内容都还没加载出来，这个是不会获取到网页任何内容的，也就是它是不起任何作用的');
		}
		for(var lang in translate.nodeQueue[uuid].list){
			if (!translate.nodeQueue[uuid].list.hasOwnProperty(lang)) {
	    		continue;
	    	}
			//console.log('lang:'+lang)
			for(var hash in translate.nodeQueue[uuid].list[lang]){
				if (!translate.nodeQueue[uuid].list[lang].hasOwnProperty(hash)) {
		    		continue;
		    	}
				//console.log(hash)
				if(typeof(translate.nodeQueue[uuid].list[lang][hash]) == 'function'){
					//v2.10增加，避免hash冒出个 Contains 出来导致for中的.length 出错
					continue;
				}
				if(typeof(translate.nodeQueue[uuid].list[lang][hash].nodes) == 'undefined' || typeof(translate.nodeQueue[uuid].list[lang][hash].nodes.length) == 'undefined'){
					//v3.16.2 增加，针对深圳北理莫斯科学校龙老师提出的这里 .length 遇到了 undefined 的情况
					continue;
				}


				/* 20250912 删除，因为在扫描阶段就已经判定了
				for(var nodeindex = translate.nodeQueue[uuid].list[lang][hash].nodes.length-1; nodeindex > -1; nodeindex--){
					//console.log(translate.nodeQueue[uuid].list[lang][hash].nodes[nodeindex]);
					var analyse = translate.element.nodeAnalyse.get(translate.nodeQueue[uuid].list[lang][hash].nodes[nodeindex].node);
					//analyse.text  analyse.node
					var nodeid = nodeuuid.uuid(analyse.node);
					//translate.nodeQueue[uuid].list[lang][hash].nodes.splice(nodeindex, 1);
					//console.log(nodeid+'\t'+analyse.text);
					//这个放到了node扫描里去进行判定了,后续要考虑删除
					if(translate.node.get(analyse.node) != null){
						//存在，判断其内容是否发生了改变
						//console.log('比较---------');
						//console.log(translate.node[nodeid].translateText);
						//console.log(analyse.text);
						var nodeAttribute = translate.node.getAttribute(translate.nodeQueue[uuid].list[lang][hash].nodes[nodeindex].attribute);
						//console.log(translate.node.get(analyse.node)[nodeAttribute.key]);
						if(translate.node.get(analyse.node)[nodeAttribute.key].resultText == analyse.text){
							//内容未发生改变，那么不需要再翻译了，从translate.nodeQueue中删除这个node
							translate.nodeQueue[uuid].list[lang][hash].nodes.splice(nodeindex, 1);
							//console.log('发现相等的node，删除 '+analyse.text+'\t'+hash);
						}else{
							//console.log("发现变化的node =======nodeid:"+nodeid);
							//console.log(translate.node[nodeid].translateText == analyse.text);
							//console.log(translate.node[nodeid].node);
							//console.log(translate.node[nodeid].translateText);
							//console.log(analyse.text);
							
						}
					}else{
						//console.log('未在 nodeHistory 中发现，新的node  nodeid:'+nodeid);
						//console.log(analyse.node)
					}

					//以上考虑删除

				}
				*/

				if(translate.nodeQueue[uuid].list[lang][hash].nodes.length == 0){
					//如果node数组中已经没有了，那么直接把这个hash去掉
					delete translate.nodeQueue[uuid].list[lang][hash];
				}
			}
			if(Object.keys(translate.nodeQueue[uuid].list[lang]).length == 0){
				//如果这个语言中没有要翻译的node了，那么删除这个语言
				delete translate.nodeQueue[uuid].list[lang];
			}
		}

		translate.time.log('对扫描到的元素进行预处理完毕');
		//console.log('new queuq');
		//console.log(translate.nodeQueue[uuid])
		//translate.node.data[nodeid]

		
		//console.log('-----待翻译：----');
		//console.log(translate.nodeQueue);
		
		//translateTextArray[lang][0]
		var translateTextArray = {};	//要翻译的文本的数组，格式如 ["你好","欢迎"]
		var translateHashArray = {};	//要翻译的文本的hash,跟上面的index是一致的，只不过上面是存要翻译的文本，这个存hash值
		/*
			要翻译的文本所在的 node ，这些要翻译的文本是在哪些node中。
			它是二维的。
			一维：
				key: language
				value: map
					key: node
					value: 1		//value无任何意义，只是凑上去的 ， 这样key会自动排重

		*/
		var translateTextNodeMap = new Map(); 
		

		/*
				要进行第二次扫描的node - 2023.8.22 解决缓存会打散扫描到的翻译文本，导致翻译结束后找寻不到而导致不翻译的问题
				一维 key: lang
				二维 key: hash
				三维 key: 
						node: 当前的node元素

				四维	-delete ...	 array: 当前缓存中进行翻译的文本数组：
							cacheOriginal: 已缓存被替换前的文本
							cacheTranslateText: 已缓存被替换后的翻译文本		
					
		*/
		var twoScanNodes = {};
		var cacheScanNodes = []; //同上面的 twoScanNodes，只不过 twoScanNodes 是按照lang存的，而这个不再有lang区分
		for(var lang in translate.nodeQueue[uuid]['list']){ //二维数组中，取语言
			if (!translate.nodeQueue[uuid]['list'].hasOwnProperty(lang)) {
	    		continue;
	    	}
			//console.log('lang:'+lang); //lang为english这种语言标识
			if(lang == null || typeof(lang) == 'undefined' || lang.length == 0 || lang == 'undefined'){
				//console.log('lang is null : '+lang);
				continue;
			}

			translateTextArray[lang] = [];
			translateTextNodeMap.set(lang, new Map());
			translateHashArray[lang] = [];
			
			let task = new translate.renderTask();
			//console.log(translate.nodeQueue);
			
			twoScanNodes[lang] = [];
			//二维数组，取hash、value
			for(var hash in translate.nodeQueue[uuid]['list'][lang]){
				if (!translate.nodeQueue[uuid]['list'][lang].hasOwnProperty(hash)) {
		    		continue;
		    	}
				if(typeof(translate.nodeQueue[uuid]['list'][lang][hash]) == 'function'){
					//跳出，增加容错。  正常情况下应该不会这样
					continue;
				}

				//原始的node中的词
				var originalWord = translate.nodeQueue[uuid]['list'][lang][hash]['original'];	
				//要翻译的词
				var translateText = translate.nodeQueue[uuid]['list'][lang][hash]['translateText'];
				//console.log(originalWord);

				//根据hash，判断本地是否有缓存了
				var cacheHash = originalWord == translateText ? hash:translate.util.hash(translateText); //如果匹配到了自定义术语库，那翻译前的hash是被改变了
				translate.nodeQueue[uuid]['list'][lang][hash]['cacheHash'] = cacheHash; //缓存的hash。 缓存时，其hash跟翻译的语言是完全对应的，缓存的hash就是翻译的语言转换来的
				var cache = translate.storage.get('hash_'+translate.to+'_'+cacheHash);

				//缓存是否有拿到具体缓存内容
				if(cache != null && cache.length > 0){
					for(var node_index = 0; node_index < translate.nodeQueue[uuid]['list'][lang][hash]['nodes'].length; node_index++){
						//console.log(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]);

						//判断是否是整体翻译，如果不是整体翻译，要考虑到缓存中短句会打散整个句子结构，破坏断句分词，造成 长句子中包含的短句子被翻译了，最后长句子翻译之后未能替换，产生部分未翻译的情况，所以要讲断句的分词也要拿出来
						var translateNodeData = translate.node.get(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index].node);
						var participles = [];
						if(typeof(translateNodeData) !== 'undefined' && translateNodeData !== null && typeof(translateNodeData.whole) === 'boolean' && translateNodeData.whole === false){
							//console.log(translateNodeData);
							//console.log(typeof(translateNodeData.translateTexts))
							//不是整体翻译，那就要将拆分的每句都整理，避免破坏分词结构
							for(var translateText_original in translateNodeData.translateTexts){
								//console.log(translateText_original);
								if (!translateNodeData.translateTexts.hasOwnProperty(translateText_original)) {
						    		continue;
						    	}
						    	participles.push(translateText_original);
						    }
						}
						
						
						//翻译结果的文本，包含了before  、 after 了
						var translateResultText = translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['beforeText']+cache+translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['afterText'];
						task.add(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'], originalWord, translateResultText, translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['attribute'], participles);
						//this.nodeQueue[lang][hash]['nodes'][node_index].nodeValue = this.nodeQueue[lang][hash]['nodes'][node_index].nodeValue.replace(new RegExp(originalWord,'g'), cache);
						//console.log(translateResultText);

						//重新扫描这个node,避免这种情况：
						/*
							localstorage缓存中有几个词的缓存了，但是从缓存中使用时，把原本识别的要翻译的数据给打散了，导致翻译结果没法赋予，导致用户展示时有些句子没成功翻译的问题 -- 2023.8.22
							比如有这个 node，其内容为：
								你是谁？你好世界
							扫描完后，触发了自定义术语将文本分割成多个、或者未启用整体翻译，出现分割后的文本数组为
								['你是谁','你','世界']
							这时， '你' 这个字发现有本地缓存，被触发立即替换为 you ，替换完成后，会导致将 '你是谁' 也被替换了，node 当前的文本变成了
								you 是谁？you 好世界
							
						*/
						//console.log('继续扫描 + 1 - '+twoScanNodes.length);
						var twoScanIndex = -1; //当前元素是否在 twoScan 中已经加入了，如果已经加入了，那么这里赋予当前所在的下标
						for(var i = 0; i<twoScanNodes[lang].length; i++){
							if(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'].isSameNode(twoScanNodes[lang][i]['node'])){
							//if(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'].isSameNode(cacheScanNodes[i]['node'])){
								//如果已经加入过了，那么跳过
								twoScanIndex = i;
								break;
							}
						}
						var twoScanIndex_cache = -1; //当前元素是否在 twoScan 中已经加入了，如果已经加入了，那么这里赋予当前所在的下标
						for(var i = 0; i<cacheScanNodes.length; i++){
							//if(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'].isSameNode(twoScanNodes[lang][i]['node'])){
							if(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'].isSameNode(cacheScanNodes[i]['node'])){
								//如果已经加入过了，那么跳过
								twoScanIndex_cache = i;
								break;
							}
						}

						if(twoScanIndex == -1){
							//console.log(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node']);
							twoScanIndex = twoScanNodes[lang].length;
							twoScanNodes[lang][twoScanIndex] = {};
							twoScanNodes[lang][twoScanIndex]['node'] = translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'];
							twoScanNodes[lang][twoScanIndex]['array'] = [];
						}

						if(twoScanIndex_cache == -1){
							twoScanIndex_cache = cacheScanNodes.length;
							cacheScanNodes[twoScanIndex_cache] = {};
							cacheScanNodes[twoScanIndex_cache]['node'] = translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'];
							cacheScanNodes[twoScanIndex_cache]['array'] = [];
						}

						//未加入过，那么加入
						var arrayIndex = twoScanNodes[lang][twoScanIndex]['array'].length;
						twoScanNodes[lang][twoScanIndex]['array'][arrayIndex] = translateResultText;
						
						var arrayIndex_cache = cacheScanNodes[twoScanIndex_cache]['array'].length;
						cacheScanNodes[twoScanIndex_cache]['array'][arrayIndex_cache] = translateResultText;
						
					}



					continue;	//跳出，不用在传入下面的翻译接口了
				}
				
				/*
				//取出数组
				var queueNodes = this.nodeQueue[lang][hash];
				if(queueNodes.length > 0){
					//因为在这个数组中的值都是一样的，那么只需要取出第一个就行了
					var valueStr = queueNodes[0].nodeValue;
					valueStr = this.util.charReplace(valueStr);

					translateTextArray[lang].push(valueStr);
					translateHashArray[lang].push(hash);
				}
				*/
				
				//加入待翻译数组
				translateTextArray[lang].push(translateText);
				for(var ni = 0; ni<translate.nodeQueue[uuid]['list'][lang][hash].nodes.length; ni++){
					translateTextNodeMap.get(lang).set(translate.nodeQueue[uuid]['list'][lang][hash].nodes[ni].node, 1)
				}
				translateHashArray[lang].push(hash); //这里存入的依旧还是用原始hash，未使用自定义术语库前的hash，目的是不破坏 nodeQueue 的 key
			}

			task.execute(); //执行渲染任务
		}

		translate.time.log('对扫描到的元素进行浏览器本地缓存命中-完毕');
		//console.log(twoScanNodes);
		//console.log('cacheScanNodes:');
		//console.log(cacheScanNodes);
		//console.log(translateTextArray);
		//return;

		if(typeof(translate.request.api.translate) != 'string' || translate.request.api.translate == null || translate.request.api.translate.length < 1){
			//用户已经设置了不掉翻译接口进行翻译
			translate.state = 0;
			
			//生命周期触发事件
			translate.lifecycle.execute.renderFinish_Trigger(uuid, translate.to);
			translate.executeNumber++;

			//钩子
			translate.lifecycle.execute.finally_Trigger({
			    uuid:uuid,
			    to:translate.to,
			    state: 16,
			    triggerNumber: triggerNumber
			});

			return;
		}



		/******* 进行第二次扫描、追加入翻译队列。目的是防止缓存打散扫描的待翻译文本 ********/
		/*
		for(var lang in twoScanNodes){
			if (!twoScanNodes.hasOwnProperty(lang)) {
	    		continue;
	    	}

			//记录第一次扫描的数据，以便跟第二次扫描后的进行对比
			var firstScan = Object.keys(translate.nodeQueue[uuid]['list'][lang]);
			var firstScan_lang_langth = firstScan.length; //第一次扫描后的数组长度

			//console.log(twoScanNodes[lang]);
			for(var i = 0; i<twoScanNodes[lang].length; i++){
				
				//找到这个node元素命中缓存后的翻译记录
				for(var ci = 0; ci<cacheScanNodes.length; ci++){
					if(twoScanNodes[lang][i].node.isSameNode(cacheScanNodes[ci]['node'])){
						//如果发现，那么赋予
						twoScanNodes[lang][i].array = cacheScanNodes[ci].array;
						break;
					}
				}

				twoScanNodes[lang][i].array.sort(function(a, b) { return b.length - a.length; });
				//console.log(twoScanNodes[lang][i].array);

				var nodeAnaly = translate.element.nodeAnalyse.get(twoScanNodes[lang][i].node);
				//console.log(nodeAnaly);
				var text = nodeAnaly.text;
				//console.log(text.indexOf(twoScanNodes[lang][i].array[0]));

				for(var ai = 0; ai < twoScanNodes[lang][i].array.length; ai++){
					if(twoScanNodes[lang][i].array[ai] < 1){
						continue;
					}
					text = text.replace(new RegExp(translate.util.regExp.pattern(twoScanNodes[lang][i].array[ai]),'g'), translate.util.regExp.resultText('\n'));
				}
				
				//console.log(text);
				var textArray = text.split('\n');
				//console.log(textArray);
				for(var tai = 0; tai < textArray.length; tai++){
					if(textArray[tai] < 1){
						continue;
					}
					//console.log(textArray[tai]);
					//将新增的追加到 translate.nodeQueue 中
					translate.addNodeToQueue(uuid, nodeAnaly['node'], textArray[tai]);
				}
			}


			//取第二次扫描追加后的数据
			var twoScan = Object.keys(translate.nodeQueue[uuid]['list'][lang]);
			var twoScan_lang_langth = twoScan.length; //第二次扫描后的数组长度
			//console.log(firstScan_lang_langth+ '=='+twoScan_lang_langth);
			if(firstScan_lang_langth - twoScan_lang_langth == 0){
				//一致，没有新增，那么直接跳出，忽略
				continue;
			}

			//console.log(translate.nodeQueue[uuid]['list'][lang]);
			//console.log(firstScan);
			for(var ti=0; ti<twoScan.length; ti++){
				var twoHash = twoScan[ti];
				//console.log(twoHash + '-- '+firstScan.indexOf(twoHash));
				if(firstScan.indexOf(twoHash) == -1){
					//需要追加了
					var item = translate.nodeQueue[uuid]['list'][lang][twoHash];

					var cacheHash = item.original == item.translateText ? twoHash:translate.util.hash(item.translateText); //如果匹配到了自定义术语库，那翻译前的hash是被改变了
					translate.nodeQueue[uuid]['list'][lang][twoHash]['cacheHash'] = cacheHash; //缓存的hash。 缓存时，其hash跟翻译的语言是完全对应的，缓存的hash就是翻译的语言转换来的
					
					translateTextArray[lang].push(item.translateText);
					translateHashArray[lang].push(twoHash);
				}
			}
			
		}
		translate.time.log('对未命中本地缓存的元素进行第二轮扫描-完毕');
		*/
		/******* 进行第二次扫描、追加入翻译队列  -- 结束 ********/

		
		//window.translateHashArray = translateHashArray;
		
		//统计出要翻译哪些语种 ，这里面的语种会调用接口进行翻译。其内格式如 english
		var fanyiLangs = []; 
		//console.log(translateTextArray)
		for(var lang in translate.nodeQueue[uuid]['list']){ //二维数组中取语言
			if (!translate.nodeQueue[uuid]['list'].hasOwnProperty(lang)) {
	    		continue;
	    	}
	    	
			if(typeof(translateTextArray[lang]) == 'undefined'){
				continue;
			}
			if(translateTextArray[lang].length < 1){
				continue;
			}

			//如果当前语种就是需要显示的语种（也就是如果要切换的语种），那么也不会进行翻译，直接忽略
			if(lang == translate.to){
				continue;
			}
			fanyiLangs.push(lang);
		}
		

		/******* 用以记录当前是否进行完第一次翻译了 *******/
		/*
		if(!translate.listener.isExecuteFinish){
			translate.temp_executeFinishNumber = 0;	//下面请求接口渲染，翻译执行完成的次数	
			//判断是否是执行完一次了
	        translate.temp_executeFinishInterval = setInterval(function(){
				if(translate.temp_executeFinishNumber == fanyiLangs.length){
					translate.listener.isExecuteFinish = true; //记录当前已执行完第一次了
					clearInterval(translate.temp_executeFinishInterval);//停止
					console.log('translate.execute() Finish!');
					//console.log(uuid);
					
				}
	        }, 50);
		}
		*/

		//console.log(translate.nodeQueue[uuid]['list'])
		if(fanyiLangs.length == 0){
			//没有需要翻译的，直接退出

			//生命周期触发事件
			translate.lifecycle.execute.renderFinish_Trigger(uuid, translate.to);

			translate.state = 0;
			translate.executeNumber++;

			//钩子
			translate.lifecycle.execute.finally_Trigger({
			    uuid:uuid,
			    to:translate.to,
			    state: 18,
			    triggerNumber: triggerNumber
			});

			return;
		}
		
		
		//状态
		translate.state = 20;

		translate.time.log('调用翻译接口进行翻译 - 开始');

		/* 
			将翻译请求的信息记录到 translate.js 本身中
			uuid 每次 translate.execute() 触发生成的uuid
				time: 触发后加入到 data 中的时间,13位时间戳
				list: 记录当前uuid下发起的网络请求
					from: 从什么语种进行的翻译，如： chinese_simplified 
						to: 翻译为什么语种，如 ： english
							nodes: 当前网络请求有哪些node节点，值为 [node1, node2, ...]
							texts: 当前网络请求有哪些文本进行翻译，值为 [text1, text2, ...]

		*/
		translate.request.data[uuid] = {
			time:Date.now(),
			list:{}
		};

		// 当前 translate.execute 内部专用的 SSE 进度遮罩状态。
		// 只传给 translate.request.sse.collectSafeProgressElements 使用，不作为全局状态保存。
		let sseProgressState = {};

		//进行掉接口翻译
		for(var lang_index in fanyiLangs){ //一维数组，取语言
			if (!fanyiLangs.hasOwnProperty(lang_index)) {
	    		continue;
	    	}
			var lang = fanyiLangs[lang_index];
			if(typeof(lang) != 'string'){
				continue;
			}
			
			if(typeof(translateTextArray[lang]) == 'undefined' || translateTextArray[lang].length < 1){
				translate.log('异常,理论上不应该存在, lang:'+lang+', translateTextArray:');
				translate.log(translateTextArray);
				translate.log('你无需担心，这个只是个提示，它并不影响你翻译的正常进行，只是个异常提示而已，它会自动容错处理的，不会影响翻译的使用。');

				translate.state = 0;
				translate.executeNumber++;

				//钩子
				translate.lifecycle.execute.finally_Trigger({
				    uuid:uuid,
				    to:translate.to,
				    state: 21,
				    triggerNumber: triggerNumber
				});

				return;
			}

			//自定义术语
			/*var nomenclatureCache = translate.nomenclature.dispose(cache);
			for(var ttr_index = 0; ttr_index<translateTextArray[lang].length; ttr_index++){
				console.log(translateTextArray[lang][ttr_index])
			}*/

			//将需要请求翻译接口的加入到 translate.translateRequest 中
			if(typeof(translate.translateRequest[uuid]) == 'undefined' || translate.translateRequest[uuid] == null){
				translate.translateRequest[uuid] = {};
			}
			translate.translateRequest[uuid][lang] = {};
			translate.translateRequest[uuid][lang].executeFinish = 0; //是否执行完毕，0是执行中， 1是执行完毕（不管是失败还是成功） 而且执行完毕是指ajax请求获得响应，并且dom渲染完成之后才算完毕。当然如果ajax接口失败那也是直接算完毕
			translate.translateRequest[uuid][lang].addtime = Math.floor(Date.now() / 1000);


			//listener
			translate.listener.execute.renderStartByApiRun(uuid, lang, translate.to); 
			
			//console.log(translateTextArray[lang]);
			var translateTextNodes = [];
			for (let key of translateTextNodeMap.get(lang).keys()) {
   				translateTextNodes.push(key);
			}
			//console.log(translateTextNodes)
			translate.lifecycle.execute.translateNetworkBefore_Trigger({
				uuid: uuid,
				lang: lang,
				to: translate.to,
				texts: translateTextArray[lang],
				nodes: translateTextNodes
			}); 

			//记入请求日志
			if(typeof(translate.request.data[uuid].list[lang]) === 'undefined'){
				translate.request.data[uuid].list[lang] = {};
			}
			translate.request.data[uuid].list[lang][translate.to] = {
				texts: translateTextArray[lang],
				nodes: translateTextNodes,
			};

			
			/*** 翻译开始 ***/
			var url = translate.request.api.translate;
			var data = {
				from:lang,
				to:translate.to,
				//lowercase:translate.whole.isEnableAll? '0':'1', //首字母大写
				//text:JSON.stringify(translateTextArray[lang])
				text:encodeURIComponent(JSON.stringify(translateTextArray[lang]))
			};
			let requestLang = lang;
			let requestTo = translate.to;
			// 记录当前这一次 translate.json 请求中已经通过 SSE 提前渲染过的原始 text 下标。
			// done 事件仍然会返回完整结果，这里用于跳过已渲染下标，避免同一段 DOM 被重复替换。
			let sseRenderedIndexMap = {};
			// SSE 增量渲染的目标节点索引，只在当前 translate.execute() 闭包内生效。
			// 以前每个 item/batch 都会反向扫描整批待翻译文本，文本上千且并发较高时会产生大量重复遍历。
			// 这里按语种预先记录“原始 text 下标 -> 它会影响哪些 node+attribute”，后续只看当前下标涉及的目标，避免跨请求共享状态。
			let sseRenderTargetStateMap = {};
			let isTranslateNodeQueueAvailable = function(){
				if(typeof(translate.nodeQueue[uuid]) == 'undefined'){
					translate.log('提示：你很可能多次引入了 translate.js 所以造成了翻译本身的数据错乱，这只是个提示，它还是会给你正常翻译的，但是你最好不要重复引入太多次 translate.js ，正常情况下只需要引入一次 translate.js 就可以了。太多的话很可能会导致你页面卡顿');
					return false;
				}
				return true;
			};
			let buildSseEventResponseData = function(requestData){
				var responseData = {};
				responseData.from = requestLang;
				responseData.to = requestTo;
				if(typeof(requestData) == 'object' && requestData != null){
					if(typeof(requestData.from) != 'undefined' && requestData.from != null){
						responseData.from = requestData.from;
					}
					if(typeof(requestData.to) != 'undefined' && requestData.to != null){
						responseData.to = requestData.to;
					}
				}
				return responseData;
			};
			let getSseRenderTargetState = function(renderLang){
				if(typeof(sseRenderTargetStateMap[renderLang]) != 'undefined'){
					return sseRenderTargetStateMap[renderLang];
				}
				var state = {
					// targetMap 使用 DOM node 作为第一层 key，attribute 作为第二层 key，避免把 DOM 对象拼成字符串造成误判。
					targetMap:new Map(),
					// indexTargets[index] 保存这个原始 text 下标会影响的目标集合，用于后续 O(当前节点数) 判断。
					indexTargets:[],
					batchToken:0
				};
				if(typeof(translateHashArray[renderLang]) == 'undefined'){
					sseRenderTargetStateMap[renderLang] = state;
					return state;
				}

				for(var itemIndex = 0; itemIndex < translateHashArray[renderLang].length; itemIndex++){
					var indexTargets = [];
					state.indexTargets[itemIndex] = indexTargets;
					var hash = translateHashArray[renderLang][itemIndex];
					if(typeof(hash) == 'undefined'
						|| typeof(translate.nodeQueue[uuid]['list']) == 'undefined'
						|| typeof(translate.nodeQueue[uuid]['list'][renderLang]) == 'undefined'
						|| typeof(translate.nodeQueue[uuid]['list'][renderLang][hash]) == 'undefined'
						|| typeof(translate.nodeQueue[uuid]['list'][renderLang][hash].nodes) == 'undefined'){
						continue;
					}

					var nodes = translate.nodeQueue[uuid]['list'][renderLang][hash].nodes;
					for(var nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++){
						if(typeof(nodes[nodeIndex]) != 'object' || nodes[nodeIndex] == null || typeof(nodes[nodeIndex].node) == 'undefined' || nodes[nodeIndex].node == null){
							continue;
						}
						var attribute = typeof(nodes[nodeIndex].attribute) == 'string' ? nodes[nodeIndex].attribute : '';
						var attributeMap = state.targetMap.get(nodes[nodeIndex].node);
						if(attributeMap == null){
							attributeMap = new Map();
							state.targetMap.set(nodes[nodeIndex].node, attributeMap);
						}
						var targetState = attributeMap.get(attribute);
						if(targetState == null){
							targetState = {
								pending:0,
								batchToken:0,
								batchCount:0
							};
							attributeMap.set(attribute, targetState);
						}

						var alreadyInIndex = false;
						for(var targetIndex = 0; targetIndex < indexTargets.length; targetIndex++){
							if(indexTargets[targetIndex] === targetState){
								alreadyInIndex = true;
								break;
							}
						}
						if(alreadyInIndex){
							continue;
						}
						// pending 代表这个 node+attribute 还有多少原始 text 下标没有被 SSE 提前渲染。
						// 后续判断只需要看当前事件是否覆盖了这些 pending 下标，不再全量扫描 translateHashArray。
						targetState.pending++;
						indexTargets.push(targetState);
					}
				}
				sseRenderTargetStateMap[renderLang] = state;
				return state;
			};
			let prepareSseRenderBatchState = function(renderLang, currentIndexMap, isSsePartial){
				if(isSsePartial !== true){
					return null;
				}
				var state = getSseRenderTargetState(renderLang);
				state.batchToken++;
				for(var itemIndexKey in currentIndexMap){
					if(!currentIndexMap.hasOwnProperty(itemIndexKey)){
						continue;
					}
					var itemIndex = parseInt(itemIndexKey, 10);
					if(isNaN(itemIndex) || sseRenderedIndexMap[itemIndex] === 1){
						continue;
					}
					var indexTargets = state.indexTargets[itemIndex];
					if(typeof(indexTargets) == 'undefined' || indexTargets == null){
						continue;
					}
					for(var targetIndex = 0; targetIndex < indexTargets.length; targetIndex++){
						var targetState = indexTargets[targetIndex];
						if(targetState.batchToken != state.batchToken){
							targetState.batchToken = state.batchToken;
							targetState.batchCount = 0;
						}
						// batchCount 只统计当前这次 SSE 事件中覆盖到的 pending 下标。
						// 如果某个目标还有未包含在本事件里的文本，就继续等 done 兜底，避免提前替换打断长文本匹配。
						targetState.batchCount++;
					}
				}
				return state;
			};
			let canRenderSseItemNow = function(renderLang, itemIndex, isSsePartial, batchState){
				if(isSsePartial !== true){
					return true;
				}
				if(typeof(translateHashArray[renderLang]) == 'undefined' || typeof(translateHashArray[renderLang][itemIndex]) == 'undefined' || batchState == null){
					return false;
				}
				var indexTargets = batchState.indexTargets[itemIndex];
				if(typeof(indexTargets) == 'undefined' || indexTargets == null || indexTargets.length < 1){
					return false;
				}
				// SSE 的 batch/item 会比 done 更早渲染。若同一个 DOM 节点里还有未返回的文本，
				// 提前替换其中一段可能破坏后续长文本匹配；这种情况交给 done 统一兜底渲染。
				for(var targetIndex = 0; targetIndex < indexTargets.length; targetIndex++){
					var targetState = indexTargets[targetIndex];
					var currentBatchCount = targetState.batchToken == batchState.batchToken ? targetState.batchCount : 0;
					if(targetState.pending - currentBatchCount > 0){
						return false;
					}
				}
				return true;
			};
			let markSseItemRendered = function(renderLang, itemIndex, isSsePartial, batchState){
				sseRenderedIndexMap[itemIndex] = 1;
				if(isSsePartial !== true || batchState == null){
					return;
				}
				var indexTargets = batchState.indexTargets[itemIndex];
				if(typeof(indexTargets) == 'undefined' || indexTargets == null){
					return;
				}
				for(var targetIndex = 0; targetIndex < indexTargets.length; targetIndex++){
					if(indexTargets[targetIndex].pending > 0){
						indexTargets[targetIndex].pending--;
					}
				}
			};
			let renderTranslateResultItems = function(responseData, requestData, items, isSsePartial){
				if(!isTranslateNodeQueueAvailable()){
					return 0;
				}
				if(typeof(responseData) != 'object' || responseData == null){
					responseData = {};
				}
				var renderLang = requestLang;
				var renderTo = requestTo;
				if(typeof(responseData.from) != 'undefined' && responseData.from != null){
					renderLang = responseData.from;
				}else if(typeof(requestData) == 'object' && requestData != null && typeof(requestData.from) != 'undefined' && requestData.from != null){
					renderLang = requestData.from;
				}
				if(typeof(responseData.to) != 'undefined' && responseData.to != null){
					renderTo = responseData.to;
				}else if(typeof(requestData) == 'object' && requestData != null && typeof(requestData.to) != 'undefined' && requestData.to != null){
					renderTo = requestData.to;
				}
				if(typeof(translateHashArray[renderLang]) == 'undefined'){
					translate.log('WARNING : translateHashArray['+renderLang+'] is undefined');
					return 0;
				}

				var renderItems = [];
				var currentIndexMap = {};
				if(typeof(items) == 'object' && items != null && typeof(items.length) == 'number'){
					for(var itemIndex = 0; itemIndex < items.length; itemIndex++){
						if(typeof(items[itemIndex]) != 'object' || items[itemIndex] == null){
							continue;
						}
						var originalIndex = parseInt(items[itemIndex].index, 10);
						if(isNaN(originalIndex) || originalIndex < 0){
							continue;
						}
						renderItems.push({
							index:originalIndex,
							text:items[itemIndex].text
						});
						currentIndexMap[originalIndex] = 1;
					}
				}else{
					for(var fullIndex = 0; fullIndex < translateHashArray[renderLang].length; fullIndex++){
						renderItems.push({
							index:fullIndex,
							text:typeof(responseData.text) == 'object' && responseData.text != null ? responseData.text[fullIndex] : null
						});
						currentIndexMap[fullIndex] = 1;
					}
				}
				var sseRenderBatchState = prepareSseRenderBatchState(renderLang, currentIndexMap, isSsePartial);
				var sseProgressRenderedIndexes = [];

				let task = new translate.renderTask();
				var renderNumber = 0;
				for(var renderItemIndex = 0; renderItemIndex < renderItems.length; renderItemIndex++){
					var i = renderItems[renderItemIndex].index;
					if(sseRenderedIndexMap[i] === 1){
						continue;
					}
					if(!canRenderSseItemNow(renderLang, i, isSsePartial, sseRenderBatchState)){
						continue;
					}

					//翻译前的语种，如 english
					var lang = renderLang;
					//翻译后的内容
					var text = renderItems[renderItemIndex].text;
					//如果 text 为 null，说明服务端为了保持结果数组下标对齐填充了空结果，这种结果不能渲染。
					if(text == null){
						continue;
					}

					// 保留原有保护逻辑：如果译文完整包含原文，认为翻译结果不可信，回退显示原始文本。
					// SSE 的 batch/item 与 done 都必须走同一判断，避免两种返回方式展示不一致。
					if(typeof(text) == 'string' && typeof(translateTextArray[renderLang]) != 'undefined' && typeof(translateTextArray[renderLang][i]) == 'string' && text.toLowerCase().indexOf(translateTextArray[renderLang][i].toLowerCase()) > -1){
						text = translateTextArray[renderLang][i];
					}

					//翻译前的 hash 对应下标，SSE 事件中的 index 永远对应原始 text 数组下标。
					var hash = translateHashArray[renderLang][i];
					if(typeof(hash) == 'undefined' || typeof(translate.nodeQueue[uuid]['list'][lang]) == 'undefined' || typeof(translate.nodeQueue[uuid]['list'][lang][hash]) == 'undefined'){
						continue;
					}
					var cacheHash = translate.nodeQueue[uuid]['list'][lang][hash]['cacheHash'];

					//取原始的词，还未经过翻译的、需要进行翻译的词。
					var originalWord = '';
					try{
						originalWord = translate.nodeQueue[uuid]['list'][lang][hash]['original'];
					}catch(e){
						translate.log('uuid:'+uuid+', originalWord:'+originalWord+', lang:'+lang+', hash:'+hash+', text:'+text+', queue:'+translate.nodeQueue[uuid]);
						translate.log(e);
						continue;
					}

					for(var node_index = 0; node_index < translate.nodeQueue[uuid]['list'][lang][hash]['nodes'].length; node_index++){
						task.add(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'], originalWord, translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['beforeText']+text+translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['afterText'], translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['attribute']);
					}

					//将翻译结果写入浏览器缓存；SSE 提前渲染和 done 兜底渲染共用同一缓存规则。
					translate.storage.set('hash_'+renderTo+'_'+cacheHash,text);
					if(translate.offline.fullExtract.isUse){
						translate.offline.fullExtract.set(hash, originalWord, renderTo, text);
					}
					markSseItemRendered(renderLang, i, isSsePartial, sseRenderBatchState);
					if(isSsePartial === true){
						// 这里只记录已经通过安全判断并加入渲染任务的 index；
						// 解除遮罩必须等 task.execute() 完成后再做，避免 DOM 尚未替换完成时露出原文。
						sseProgressRenderedIndexes.push(i);
					}
					renderNumber++;
				}
				if(renderNumber > 0){
					task.execute(); //执行渲染任务
					if(isSsePartial === true && sseProgressRenderedIndexes.length > 0){
						var safeProgressElements = translate.request.sse.collectSafeProgressElements(sseProgressState, {
							uuid:uuid,
							fanyiLangs:fanyiLangs,
							translateHashArray:translateHashArray,
							renderLang:renderLang,
							renderedIndexes:sseProgressRenderedIndexes
						});
						if(safeProgressElements.length > 0){
							translate.progress.api.removeUITipByElements(safeProgressElements);
						}
					}
				}
				return renderNumber;
			};
			translate.request.post(url, data, function(responseData, requestData){
				//console.log(data); 
				//console.log(translateTextArray[data.from]);

				//针对 giteeai 增加了账户余额、账户是否激活的拍的判定，所以增加了 401 这个参数，凡是账户异常的，参数值是 401~499 之间。所以只要不是1都是失败
				if(responseData.result != 1){
					if(typeof(translate.translateRequest[uuid]) == 'object' && typeof(translate.translateRequest[uuid][requestData.from]) == 'object'){
						translate.translateRequest[uuid][requestData.from]['result'] = 2;
						translate.translateRequest[uuid][requestData.from].executeFinish = 1; //1是执行完毕
						translate.translateRequest[uuid][requestData.from].stoptime = Math.floor(Date.now() / 1000);
					}else{
						translate.log('WARINNG!!! translate.translateRequest[uuid][requestData.from] is not object');
					}

					//为了兼容 v3.14以前的translate.service 版本，做了判断
					var from = '';
					if(typeof(requestData.from) != 'undefined' && requestData.from != null){
						from = requestData.from;
					}
					var to = '';
					if(typeof(requestData.to) != 'undefined' && requestData.to != null){
						to = requestData.to;
					}else{
						to = translate.to;
					}
					translate.waitingExecute.isAllExecuteFinish(uuid, from, to, 0, responseData.info);
					

					translate.log('=======ERROR START=======');
					translate.log(translateTextArray[requestData.from]);
					//console.log(encodeURIComponent(JSON.stringify(translateTextArray[data.from])));
					translate.log('response : '+responseData.info);
					translate.log('=======ERROR END  =======');
					//translate.temp_executeFinishNumber++; //记录执行完的次数
					return;
				}
				
				if(!isTranslateNodeQueueAvailable()){
					return;
				}
				renderTranslateResultItems(responseData, requestData, null, false);
				//translate.temp_executeFinishNumber++; //记录执行完的次数

				var finishLang = typeof(responseData.from) != 'undefined' && responseData.from != null ? responseData.from : requestData.from;
				var finishTo = typeof(responseData.to) != 'undefined' && responseData.to != null ? responseData.to : requestData.to;
				translate.translateRequest[uuid][finishLang].result = 1;
				translate.translateRequest[uuid][finishLang].executeFinish = 1; //1是执行完毕
				translate.translateRequest[uuid][finishLang].stoptime = Math.floor(Date.now() / 1000);
				setTimeout(function(){
					translate.waitingExecute.isAllExecuteFinish(uuid, finishLang, finishTo, 1, '');
				},5);
			}, function(xhr){
				translate.translateRequest[uuid][xhr.data.from].executeFinish = 1; //1是执行完毕
				translate.translateRequest[uuid][xhr.data.from].stoptime = Math.floor(Date.now() / 1000);
				translate.translateRequest[uuid][xhr.data.from].result = 3;
				var info = '';
				if(typeof(xhr.status) != 'undefined'){
					if(xhr.status < 1){
						info = 'Network connection failed. url: '+xhr.requestURL;
					}else{
						info = 'HTTP response code : '+xhr.status+', url: '+xhr.requestURL;
					}
				}else{
					info = 'Network connection failed. url: '+xhr.requestURL;
				}
				translate.waitingExecute.isAllExecuteFinish(uuid, xhr.data.from, translate.to, 0, info);
			}, {
				onBatch:function(eventData, requestData){
					if(typeof(eventData) != 'object' || eventData == null || typeof(eventData.items) != 'object' || eventData.items == null){
						return;
					}
					// batch 是服务端已经确定的一批结果，按原始 text 下标提前渲染；不在这里标记请求完成。
					renderTranslateResultItems(buildSseEventResponseData(requestData), requestData, eventData.items, true);
				},
				onItem:function(eventData, requestData){
					if(typeof(eventData) != 'object' || eventData == null){
						return;
					}
					// item 是服务端返回的单条 API 翻译结果，仍然只按原始 text 下标渲染当前条。
					renderTranslateResultItems(buildSseEventResponseData(requestData), requestData, [eventData], true);
				}
			});
			/*** 翻译end ***/
		}

		//钩子
		translate.lifecycle.execute.finally_Trigger({
		    uuid:uuid,
		    to:translate.to,
		    state: 25,
		    triggerNumber: triggerNumber
		});
	},
	/*translate.execute() end */

	/**
	 * 翻译请求记录
	 * 一维：key:uuid，也就是execute每次执行都会创建一个翻译队列，这个是翻译队列的唯一标识。  这个uuid跟 nodeQueue 的uuid是一样的
	 * 		value:对象
	 * 二维: 对象，包含：
	 * 		from 存放的是要翻译的源语种，比如要讲简体中文翻译为英文，这里存放的就是 chinese_simplified
	 * 		state 是否执行完毕，0是执行中， 1是执行完毕（不管是失败还是成功） 而且执行完毕是指ajax请求获得响应，并且dom渲染完成之后才算完毕。当然如果ajax接口失败那也是直接算完毕
	 * 		addtime 这条数据加入到本数组的时间，也就是进行ajax请求开始那一刻的时间，10位时间戳
	 * 		stoptime 执行完毕的时间，也就是state转为2那一刻的时间
	 * 		result 执行结果， 0 是还没执行完，等待执行完， > 0 是执行完了有结果了，  
	 * 												  1 是执行成功
	 * 												  2 是接口有响应，也是200响应，但是接口响应的结果返回了错误，也就是返回了 {result:0, info:'...'}
	 * 												  3 是接口不是200响应码
	 * 
	 */
	translateRequest:{
		/* 
		uuid:[
			'chinese_simplified':{
				executeFinish:0,
				addtime:150001111,
				stoptime:150001111,
				result:0
			},
			...
		] 
		*/
	},

	//20250908 废弃，要删除这个
	nodeHistory:null,

	//当前页面中，翻译后操作的 ，dom 中有效的node节点
	node:{
		/*
			将已扫描的节点进行记录，这里是只要进行扫描到了，也就是在加入 translate.nodeQueue 时就也要加入到这里。
			这是一个map，为了兼容es5，这里设置为null，在 translate.execute 中在进行初始化

			key: node ,进行翻译的文本的node， 如果是 div 的 title属性进行的翻译，那这个node是定位在 title 上的node，而不是 div 这个依附的元素
					注意，如果是对 input、textarea 的value进行翻译，而 value是通过js赋予的，那么这个value属性的值并不是一个单独的node，所以是为空的，此时要记录的node便是  input、textarea 这个node本身。
			value: 这是一个对像
				其中，key的取值有这几种：
				translate_default_value: 如果当前翻译的是元素本身的值或node节点本身的值(nodeValue)，那么这里的key就是固定的 translate_default_value
				attribute_属性名: 如果当前翻译的是元素的attribute 的某个属性，那么这里就是 attribute_属性名， 比如 a 标签的 title ，那这里便是 attribute_title
				modified: 被改动的动作，比如创建、值发生改动，都会记录到这里，它没有什么实际作用，仅仅只是为了方便开发调试使用。 
						这是一个数组格式，其值如：
						[
							'create:translate.faultTolerance.documentCreateTextNode.enable',
							'update:translate.execute'
						]
						创建便是 create 开头，数据修改（追加属性、属性值修改、删除属性等）便是 update 开头，后面跟着的是操作它是在哪个方法里
						越早操作，下标越小，也就是 [0] 是最开始创建的，然后每次修改都会push进一个数据进去

				lastTranslateRenderTime: 记录当前有 translate.js 所触发翻译之后渲染到dom界面显示的时间，13位时间戳。
										 每当触发渲染时这里都会重新赋予一次最新的时间，这里也就是最后一次渲染的时间。 如果还没渲染那这里便是 undefined 或者 null，总之 typeof 不是 number
										 另外这个时间是渲染的前一刻赋予的，赋予后立即进行的DOM渲染
				
				translateResults: array string 文本数组，这里是被 translate.element.nodeAnalyse.set 进行翻译渲染之后，每次针对node进行一次渲染，它都会讲渲染的文本（注意是翻译之后的文本，而不是原文）设置进来，不管是node本身还是属性还是什么，都会直接讲其具体结果拿过来。
						注意，翻译完毕进行渲染时，是先将要显示的文本（翻译后的文本）拿来赋予到这里，然后在执行 dom渲染（触发listener）  
						当listener动态监听时，也是根据这个来判定当前是否是有 translate.js 本身导致的node发生了改变
						{
							你好，世界:1
							你是谁:1
						}
						它使用是 typeof(translate.node.get(node).translateResults['你好世界']) === 'number' 这样使用，至于后面的value为1那纯属是凑的，没任何意义						 
				
				
				attribute 这个翻译的node对象是否是翻译的其中的某个attribute属性，如果是，那么这里便是长度大于0， 如果是元素或节点本身(nodeValue)，那么这里就是空字符串，注意，是空字符串 ''
						另外这个字段，当前应该仅仅只是针对 input、textarea 的 value 属性有用，也就是它的值要么是空字符串，要么是 'value'， 因为像是 input value 的属性是不属于dom的，必须 input.value 这样才能点出来

				resultText: string 翻译完成后，当前node节点的内容文本，注意，是node节点整体所有的内容文本（是已经翻译渲染过的）
									注意，翻译失败或者本身是特殊字符比如数字，不需要被翻译，是没有这个属性的
				originalText: string 翻译前显示的文本，是node节点所有的内容文本，原始的文本，（当前这里仅仅只对元素整体翻译时才会记录这个 - v3.18.14.20250903 增加）	
				
				translateTexts: array string 文本数组，这里是被文本翻译接口所翻译的文本。 
						比如其中某项为 '你好':'hello' ，其中key是翻译前的， value是翻译后的结果， 如果 value 为 null，则代表还未进行翻译拿到翻译结果
				
				whole: boolean 当前是否是整体进行翻译的，比如当前即使是设置的整体翻译，但是这个node命中了自定义术语，被术语分割了，那当前翻译也不是整体翻译的。 
						这个属性在扫描完节点，进行请求翻译接口或命中本地缓存之前，就要被设置。  
						true:是节点内容整体翻译
					
		*/
		data: null,
		/*
			从 translate.node.data 中，根据key，进行获取 translate.node.data.get(node)
		 */ 
		get:function(node){
			return translate.node.data.get(node);
		},
		//判断某个node (key) 是否在 translate.node.data 中是否存在，如果存在返回true
		find:function(node){
			return translate.node.get(node) != null;
		},
		set:function(node, value){
			translate.node.data.set(node,value);
		},
		/*
			向 translate.node 的元素中，追加属性 modified 的数组内容
		*/
		setModified:function(node, text){
			if(typeof(translate.node.data.get(node)) === 'undefined' || translate.node.data.get(node) === null){
				translate.log('translate.node.setModified exception: node not find in translate.node,  node:');
				translate.log(node);
				return;
			}

			if(typeof(translate.node.data.get(node).modified) === 'undefined'){
				translate.node.data.get(node).modified = [];
			}
			translate.node.data.get(node).modified.push(text);
		},
		//从 translate.node.data 中 删除 key 是 node 的
		delete: function(node){
			//console.log('delete node -- > '+node.nodeValue);
			translate.node.data.delete(node);
		},
		/*
			获取 translate.node.get(node)[attribute] 这里的 attribute
			
			attribute 传入的可以是 undefined、null、'' 、 以及具体的字符串

			返回的是一个对象：
			{
				key: translate.node 中 translate.node.get(node)[attribute] 所使用的 attribute 的字符串，如 attribute_title 、translate_default_value
				attribute: 这里是attribute具体的内容，比如 key 是 attribute_title 那么这里就是 title , key 是 translate_default_value 这里就是 '' 空字符串
			}
		 
		getAttribute:function(attribute){
			var history_attribute;
			if(typeof(attribute) != 'undefined' && attribute.length > 0){
				//是对 attribute 进行的操作
				history_attribute = 'attribute_'+attribute
			}else{
				//是对节点本身进行的操作，操作的是 nodeValue
				attribute = '';
				history_attribute = 'translate_default_value';
			}
			return {
				key:history_attribute,
				attribute:attribute
			}
		},
		*/
		/*
			刷新 translate.node.data 中的数据，剔除过时的（node已经不存在于dom的）
		*/
		refresh: function(){

			// 收集要删除的无效节点
			const deleteKeys = new Array();

			for (let key of translate.node.data.keys()) {
				// 检查节点是否还在DOM中
			    let isValidNode = false;
			    
			    if (key.nodeType === Node.ELEMENT_NODE) {
			        // 元素节点
			        isValidNode = key.isConnected;
			    } else if (key.nodeType === Node.ATTRIBUTE_NODE) {
			        // 属性节点（如placeholder）
			        isValidNode = key.ownerElement && key.ownerElement.isConnected;
			    } else if (key.nodeType === Node.TEXT_NODE) {
			        // 文本节点
			        isValidNode = key.isConnected;
			    }
			    
			    if (!isValidNode) {
			        //console.log('节点已经不存在，剔除节点');
			        deleteKeys.push(key);
			    }
			    
			    // 处理有效节点...
				//if(!key.isConnected){  text node 没有 isConnected
				//	console.log(key.nodeValue+' 这个translate.node 中的 node不存在,忽略');
				//	continue;
				//}
			}

			// 统一删除无效节点
			for (var i = 0; i < deleteKeys.length; i++) {
			    translate.node.delete(deleteKeys[i]);
			}

		}
	},

	element:{

		/*
			注意，里面全部的必须小写。
			第一个是tag，第二个是tag的属性。比如要翻译 input 的 value 属性，那么如下：
				translate.element.tagAttribute['input']=['value'];
			比如要翻译 input 的 value 、 data-value 这两个属性，那么如下：
				translate.element.tagAttribute['input']=['value','data-value'];
			有几个要翻译的属性，就写上几个。
			同样，有几个要额外翻译的tag，就加上几行。  
			详细文档参考：  http://translate.zvo.cn/231504.html

	
			//针对宁德时代提出的需求，需要对 标签本身进行一个判定，是否符合条件符合条件才会翻译，不符合条件则不要进行翻译
			//比如标签带有 disabled 的才会被翻译，所以要增加一个自定义入参的 function ，返回 true、false
			translate.element.tagAttribute['input']={
				//要被翻译的tag的属性，这里是要翻译 input 的 value 、 data-value 这两个属性。 
				//数组格式，可以一个或多个属性
				attribute:['value','data-value'],  
				//条件，传入一个function，返回一个布尔值。
				//只有当返回的布尔值是true时，才会对上面设置的 attribute 进行翻译，否则并不会对当前设定标签的 attribute 进行任何翻译操作。
				condition:function(element){ 	   
					//	element 便是当前的元素，
					//	比如这里是 translate.element.tagAttribute['input']  那这个 element 参数便是扫描到的具体的 input 元素
					//	可以针对 element 这个当前元素本身来进行判定，来决定是否进行翻译。
					//	返回值是布尔值 true、false
					//	    return true; //要对 attribute中设置的 ['value','data-value'] 这两个input 的属性的值进行翻译。 
					//	                     如果不设置或传入 condition ，比如单纯这样设置： 
					//	                     translate.element.tagAttribute['input']={ 
					//	                         attribute:['value','data-value'] 
					//	                     } 
					//	                     那么这里默认就是 return true;
					//	    return false; //不对 attribute中设置的 ['value','data-value'] 这两个input 的属性的值进行任何操作
					return true;
				}
			};

		*/
		tagAttribute : {},

		//对翻译前后的node元素的分析（翻以前）及渲染（翻译后）
		nodeAnalyse:{
			/*
				获取node中的要进行翻译的文本内容、以及要操作的实际node对象（这个node对象很可能是传入的node中的某个子node）
				node 
				attribute 要获取的是某个属性的值，还是node本身的值。比如 a标签的title属性的值，则传入 title。  如果是直接获取node.nodeValue ，那这个没有

				返回结果是一个数组。其中：
					['text']:要进行翻译的text内容文本
					['node']:要进行翻译的目标node

			*/
			get:function(node, attribute){
				return translate.element.nodeAnalyse.analyse(node,'','', attribute);
			},
			/*
				同上，只不过这个是扫描 element/node 下的所有可翻译的子节点（下层节点），返回数组形态。
				这里面的数组，已经经过判断， text 必然是有不为空的值的。
				所以它的返回值，有可能是一个空的数组

				[
					{
						node: 当前扫描出的node （传入的node、或下层node）
						attribute: 是否是下层属性，比如 alt、placeholder , 如果是传入的node本身，不是任何下层属性，则这里是空白字符串 ''
						text: 可进行翻译的文本，也就是当前数组中 node 的值的文本
					},
					...
				]
			*/
			gets:function(node){
				var resultArray = [];

				var nodename = translate.element.getNodeName(node).toUpperCase();
				switch (nodename) {
				  case 'META': 	//meta标签，如是关键词、描述等
				    var nodeAttributeName = node.name.toLowerCase();  //取meta 标签的name 属性
					var nodeAttributePropertyOri = node.getAttribute('property'); //取 property的值
					var nodeAttributeProperty = '';
					if(typeof(nodeAttributePropertyOri) === 'string' && nodeAttributePropertyOri.length > 0){
						nodeAttributeProperty = nodeAttributePropertyOri.toLowerCase();
					}

					if(nodeAttributeName == 'keywords' || nodeAttributeName == 'description' || nodeAttributeName == 'sharetitle' || nodeAttributeProperty == 'og:title' || nodeAttributeProperty == 'og:description' || nodeAttributeProperty == 'og:site_name' || nodeAttributeProperty == 'og:novel:latest_chapter_name'){
						if(typeof(node.content) === 'string' && node.content.trim().length > 0){
							resultArray.push({
								text: node.content,
								attribute: 'content',
								node: node.getAttributeNode('content')
							});
						}
					}
				    break;
				  case 'IMG':
				    if(typeof(node.alt) === 'string' && node.alt.trim().length > 0){
						resultArray.push({
							text: node.alt,
							attribute: 'alt',
							node: node.getAttributeNode('alt')
						});
					}
				    break;
				  case 'INPUT':  
				  	/*
						input，要对以下情况进行翻译
							placeholder
							type=button、submit 的情况下的 value 
				  	*/

				  	//针对 type=button、submit 的情况下的 value 
				  	if(typeof(node.attributes.type) !== 'undefined' && node.attributes.type !== null &&  typeof(node.attributes.type.nodeValue) === 'string' && (node.attributes.type.nodeValue.toLowerCase() == 'button' || node.attributes.type.nodeValue.toLowerCase() == 'submit')){
						//取它的value
						var input_value_node = node.attributes.value;
						if(typeof(input_value_node) !== 'undefined' && input_value_node !== null && typeof(input_value_node.nodeValue) === 'string' && input_value_node.nodeValue.trim().length > 0){
							resultArray.push({
								text: input_value_node.nodeValue,
								attribute: 'value',
								node: input_value_node
							});
						}
					}

					//针对 placeholder
					if(typeof(node.attributes['placeholder']) !== 'undefined' && typeof(node.attributes['placeholder'].nodeValue) === 'string' && node.attributes['placeholder'].nodeValue.trim().length > 0){
						resultArray.push({
							text: node.attributes['placeholder'].nodeValue,
							attribute: 'placeholder',
							node: node.attributes['placeholder']
						});
					}
					break;
				  case 'TEXTAREA':	
				  	//针对 placeholder
					if(typeof(node.attributes['placeholder']) !== 'undefined' && typeof(node.attributes['placeholder'].nodeValue) === 'string' && node.attributes['placeholder'].nodeValue.trim().length > 0){
						resultArray.push({
							text: node.attributes['placeholder'].nodeValue,
							attribute: 'placeholder',
							node: node.attributes['placeholder']
						});
					}
					break;
				}

				//判断是否是 translate.element.tagAttribute 自定义翻译属性的
				var divTagAttribute = translate.element.tagAttribute[nodename.toLowerCase()];
				if(typeof(divTagAttribute) !== 'undefined'){
					//有这个标签的自定义翻译某个属性
					for(var ai = 0; ai<node.attributes.length; ai++){
						var arrtibuteNodeName = translate.element.getNodeName(node.attributes[ai]).toLowerCase();
						if(divTagAttribute.attribute.indexOf(arrtibuteNodeName) > -1 && divTagAttribute.condition(node)){
							//包含这个属性，且自定义判断条件满足，允许翻译
							//判定一下是否已经加入过了，如果没有加入过，才会加入。这里主要是针对input 标签进行判断，比如 input type="submit" 的，value值如果也被用户自定义翻译，那上面的value就已经加上了，不需要在加了
							var isAlreadyAdd = false; //true已经加入过了
							for(var ri = 0; ri < resultArray.length; ri++){
								if(resultArray[ri].node === node.attributes[ai]){
									//相同，则不在加入了
									isAlreadyAdd = true;
								}
							}
							if(!isAlreadyAdd){
								resultArray.push({
									text: node.attributes[ai].nodeValue,
									attribute: arrtibuteNodeName,
									node: node.attributes[ai]
								});
							}
						}
					}
				}else{
					//条件不满足，不在翻译的属性范围
				}


				//所有元素都要判定的属性 - title 属性
				if(typeof(node['title']) === 'string' && node['title'].trim().length > 0){
					var titleNode = node.getAttributeNode('title');
					resultArray.push({
						text: titleNode.nodeValue,
						attribute: 'title',
						node: titleNode
					});
				}


				//最后判定 node 本身
				if(typeof(node.nodeValue) === 'string' && node.nodeValue.trim().length > 0){
			  		//返回传入的node本身
				    resultArray.push({
						text: node.nodeValue,
						attribute: '',
						node: node
					});
			  	}

				
				return resultArray;
			},
			/*
				进行翻译之后的渲染显示
				注意，它会对node本身进行扫描的，需要进行通过文本翻译接口进行翻译的文本进行识别，比如 这个 node 其内容为：
					你是谁？你好世界
				扫描完后，触发了自定义术语将文本分割成多个、或者未启用整体翻译，出现分割后的文本数组为
					['你是谁','你','世界']
				那如果命中缓存 '你' 后，进行替换时，就不能将 '你是谁' 给替换了，不然会造成字符串无需拆分，直接纯单词翻译，没有什么语义了。另外这样也会导致漏翻译的情况。  经过这次调整，将 translate.execute() 二次扫描直接给优化掉了，提高了语义通顺、自定义术语的精准
				这个取值，是从 translate.node.get(node).translateTexts 中取这个要进行文本翻译的数组的。 
				当然，如果 translate.node.get(node).whole 为 true，本身就是整体翻译，那就没这些破事，直接替换就好了


				参数：
					node 当前翻译的node元素
						注意，如果是对 input、textarea 的value进行翻译，而 value是通过js赋予的，那么这个value属性的值并不是一个单独的node，所以是为空的，此时要记录的node便是  input、textarea 这个node本身。
					originalText 翻译之前的内容文本
					resultText 翻译之后的内容文本
					attribute 存放要替换的属性，比如 a标签的title属性。 如果是直接替换node.nodeValue ，那这个没有
				返回结果是一个数组，其中：
					resultText: 翻译完成之后的text内容文本，注意，如果返回的是空字符串，那么则是翻译结果进行替换时，并没有成功替换，应该是翻译的过程中，这个node的值被其他js又赋予其他内容了。
					node: 进行翻译的目标node	
					participles: 分词，数组形态。默认不传则是没有其他分词需要保留的。 传入比如  ['你好','你是谁'] 
        		比如 translateOriginal 传入 '你' 时， text 中的 '你好','你是谁' 是不能被拆出'你'这个字进行替换的，不然就破坏了分词了

				注意，使用本set方法，不要用 返回的 text参数，要用 	resultText 这个参数，这个才是翻译之后的文本	
			*/
			set:function(node, originalText, resultText, attribute, participles){
				return translate.element.nodeAnalyse.analyse(node,originalText,resultText, attribute, participles);
			},
			/*	
				
				注意，这个不使用，只是服务于上面的get、set使用。具体使用用上面的get、set

				1. 只传入 node：
					获取node中的要进行翻译的文本内容、以及要操作的实际node对象（这个node对象很可能是传入的node中的某个子node）
					返回结果是一个数组。其中：
						['text']:要进行翻译的text内容文本
						['node']:要进行翻译的目标node
									注意，如果是对 input、textarea 的value进行翻译，而 value是通过js赋予的，那么这个value属性的值并不是一个单独的node，所以是为空的，此时要记录的node便是  input、textarea 这个node本身。
				2. 传入 node、originalText、 resultText
					则是进行翻译之后的渲染显示

				attribute : 进行替换渲染时使用，存放要替换的属性，比如 a标签的title属性。 如果是直接替换node.nodeValue ，那这个没有
				participles: 分词，数组形态。保障 originalText 不被拆乱了。 默认不传则是没有其他分词需要保留的。 传入比如  ['你好','你是谁'] 
        					比如 translateOriginal 传入 '你' 时， text 中的 '你好','你是谁' 是不能被拆出'你'这个字进行替换的，不然就破坏了分词了
				
				返回结果是一个数组，其中：
					resultText: 翻译完成之后的text内容文本。 当使用 translate.element.nodeAnalyse.set 时才会有这个参数返回。 注意，如果返回的是空字符串，那么则是翻译结果进行替换时，并没有成功替换，应该是翻译的过程中，这个node的值被其他js又赋予其他内容了。
					text : 要进行翻译的text内容文本，当使用 translate.element.nodeAnalyse.get 时才会有这个参数的返回
					node: 要进行翻译的目标node
							注意，如果是对 input、textarea 的value进行翻译，而 value是通过js赋予的，那么这个value属性的值并不是一个单独的node，所以是为空的，此时要记录的node便是  input、textarea 这个node本身。
			*/
			analyse:function(node, originalText, resultText, attribute, participles){
				var result = new Array(); //返回的结果
				result['node'] = node;
				result['text'] = '';
				
				var nodename = translate.element.getNodeName(node);
				//console.log('nodeAnalyse.analyse: NodeName:'+nodename+', originalText:'+originalText+', resultText:'+resultText+', attribute:'+attribute+', node:');
				//console.log(node)

				//console.log('participles:');
				//console.log(participles);
				if(attribute != null && typeof(attribute) == 'string' && attribute.length > 0){
					//这个node有属性，替换的是node的属性，而不是nodeValue

					var nodeAttributeValue; //这个 attribute 属性的值
					if((nodename === 'INPUT' || nodename === 'TEXTAREA') && attribute.toLowerCase() == 'value'){
						//如果是input\textarea 的value属性，那么要直接获取，而非通过 attribute ，不然用户自己输入的通过 attribute 是获取不到的 -- catl 赵阳 提出
						
						nodeAttributeValue = node.value;
					}else{
						nodeAttributeValue = node[attribute];
					}
					result['text'] = nodeAttributeValue;
					

					//替换渲染
					if(typeof(originalText) != 'undefined' && originalText.length > 0){
						if(typeof(nodeAttributeValue) != 'undefined'){
							//这种是主流框架，像是vue、element、react 都是用这种 DOM Property 的方式，更快
							var resultShowText = translate.util.textReplace(nodeAttributeValue, originalText, resultText, translate.to, participles);
							translate.element.nodeAnalyse.analyseReplaceBefore_DateToTranslateNode(node, attribute, resultShowText);

							if((nodename === 'INPUT' || nodename === 'TEXTAREA') && attribute.toLowerCase() == 'value'){
								//input 的value 对于用户输入的必须用 .value 操作
								node.value = resultShowText;
							}else{
								node[attribute] = resultShowText;  //2025.4.26 变更为此方式
							}
							if(resultShowText.indexOf(resultText) > -1){
								result['resultText'] = resultShowText;
							}else{
								result['resultText'] = '';
							}
						}

						/* 20250911 删除
						//这种 Html Attribute 方式 是 v3.12 版本之前一直使用的方式，速度上要慢于 上面的，为了向前兼容不至于升级出问题，后面可能会优化掉
						if(node.nodeType === 1){ //是 element 节点
							var htmlAttributeValue = node.getAttribute(attribute);
							if(htmlAttributeValue != null && typeof(htmlAttributeValue) != 'undefined'){
								var resultShowText = translate.util.textReplace(htmlAttributeValue, originalText, resultText, translate.to);
								//这个才是在v3.9.2 后要用的，上面的留着只是为了适配以前的
								node.setAttribute(attribute, resultShowText); 
								if(resultShowText.indexOf(resultText) > -1){
									result['resultText'] = resultShowText;
								}else{
									result['resultText'] = '';
								}
							}
						}
						*/
					}
					return result;
				}

				

				//正常的node ，typeof 都是 object

				/* 这里是通用方法，不应该有限制
				//console.log(typeof(node)+node);
				if(nodename == '#text'){
					//如果是普通文本，判断一下上层是否是包含在textarea标签中
					if(typeof(node.parentNode) != 'undefined'){
						var parentNodename = translate.element.getNodeName(node.parentNode);
						//console.log(parentNodename)
						if(parentNodename == 'TEXTAREA'){
							//是textarea标签，那将nodename 纳入 textarea的判断中，同时将判断对象交于上级，也就是textarea标签
							nodename = 'TEXTAREA';
							node = node.parentNode;
						}
					}
				}
				*/


				//console.log(nodename)
				//console.log(translate.element.getNodeName(node.parentNode))
				//console.log(node)
				if(nodename == 'INPUT' || nodename == 'TEXTAREA'){
					//console.log(node.attributes)
					/*
						1. input、textarea 输入框，要对 placeholder 做翻译
						2. input 要对 type=button 的情况进行翻译
					*/
					if(node.attributes == null || typeof(node.attributes) == 'undefined'){
						result['text'] = '';
						return result;
					}

					//input，要对 type=button、submit 的情况进行翻译
					if(nodename == 'INPUT'){
						if(node.attributes.type != null && typeof(node.attributes.type.nodeValue) === 'string' && (node.attributes.type.nodeValue.toLowerCase() == 'button' || node.attributes.type.nodeValue.toLowerCase() == 'submit')){
							//console.log('----是 <input type="button"');
							//取它的value
							var input_value_node = node.attributes.value;
							if(input_value_node != null && typeof(input_value_node) != 'undefined' && typeof(input_value_node.nodeValue) != 'undefined' && input_value_node.nodeValue.length > 0){
								//替换渲染
								if(typeof(originalText) != 'undefined' && originalText.length > 0){
									var resultShowText = translate.util.textReplace(input_value_node.nodeValue, originalText, resultText, translate.to, participles);
									translate.element.nodeAnalyse.analyseReplaceBefore_DateToTranslateNode(node, attribute, resultShowText);

									input_value_node.nodeValue = resultShowText;  //2025.4.26 变更为此方式
									if(resultShowText.indexOf(resultText) > -1){
										result['resultText'] = resultShowText;
									}else{
										result['resultText'] = '';
									}
								}

								result['text'] = input_value_node.nodeValue;
								result['node'] = input_value_node;
								return result;
							}
						}
					}
					//console.log(node)

					//input textarea 的 placeholder 情况
					if(typeof(node.attributes['placeholder']) != 'undefined'){
						//console.log(node);
						//替换渲染
						if(typeof(originalText) != 'undefined' && originalText.length > 0){
							var resultShowText = translate.util.textReplace(node.attributes['placeholder'].nodeValue, originalText, resultText, translate.to, participles);
							translate.element.nodeAnalyse.analyseReplaceBefore_DateToTranslateNode(node, attribute, resultShowText);

							node.attributes['placeholder'].nodeValue = resultShowText;  //2025.4.26 变更为此方式
							if(resultShowText.indexOf(resultText) > -1){
								result['resultText'] = resultShowText;
							}else{
								result['resultText'] = '';
							}
						}

						result['text'] = node.attributes['placeholder'].nodeValue;
						result['node'] = node.attributes['placeholder'];
						return result;
						//return node.attributes['placeholder'].nodeValue;
					}
					//console.log(node)
					result['text'] = '';
					return result;
				}
				if(nodename == 'META'){
					//meta标签，如是关键词、描述等
					if(typeof(node.name) != 'undefined' && node.name != null){
						var nodeAttributeName = node.name.toLowerCase();  //取meta 标签的name 属性
						var nodeAttributePropertyOri = node.getAttribute('property'); //取 property的值
						var nodeAttributeProperty = '';
						if(typeof(nodeAttributePropertyOri) != 'undefined' && nodeAttributePropertyOri != null && nodeAttributePropertyOri.length > 0){
							nodeAttributeProperty = nodeAttributePropertyOri.toLowerCase();
						}
						if(nodeAttributeName == 'keywords' || nodeAttributeName == 'description' || nodeAttributeName == 'sharetitle' || nodeAttributeProperty == 'og:title' || nodeAttributeProperty == 'og:description' || nodeAttributeProperty == 'og:site_name' || nodeAttributeProperty == 'og:novel:latest_chapter_name'){
							//替换渲染
							if(typeof(originalText) != 'undefined' && originalText != null && originalText.length > 0){
								var resultShowText = translate.util.textReplace(node.content, originalText, resultText, translate.to, participles);
								translate.element.nodeAnalyse.analyseReplaceBefore_DateToTranslateNode(node, attribute, resultShowText);

								node.content = resultShowText;  //2025.4.26 变更为此方式
								if(resultShowText.indexOf(resultText) > -1){
									result['resultText'] = resultShowText;
								}else{
									result['resultText'] = '';
								}
							}

							result['text'] = node.content;
							return result;
						}
					}

					result['text'] = '';
					return result;
				}

				if(nodename == 'IMG'){

					if(typeof(node.alt) == 'undefined' || node.alt == null){
						result['text'] = '';
						return result;
					}

					//替换渲染
					if(typeof(originalText) != 'undefined' && originalText.length > 0){
						var resultShowText = translate.util.textReplace(node.alt, originalText, resultText, translate.to, participles);
						translate.element.nodeAnalyse.analyseReplaceBefore_DateToTranslateNode(node, attribute, resultShowText);

						node.alt = resultShowText;  //2025.4.26 变更为此方式
						if(resultShowText.indexOf(resultText) > -1){
							result['resultText'] = resultShowText;
						}else{
							result['resultText'] = '';
						}
					}
					result['text'] = node.alt;
					return result;
				}
				
				
				//其他的
				if(node.nodeValue == null || typeof(node.nodeValue) == 'undefined'){
					result['text'] = '';
				}else if(node.nodeValue.trim().length == 0){
					//避免就是单纯的空格或者换行
					result['text'] = '';
				}else{
					//替换渲染
					if(typeof(originalText) != 'undefined' && originalText != null && originalText.length > 0){
						//console.log(originalText+'|');
						var resultShowText = translate.util.textReplace(node.nodeValue, originalText, resultText, translate.to, participles);
						translate.element.nodeAnalyse.analyseReplaceBefore_DateToTranslateNode(node, attribute, resultShowText);

						//console.log(resultShowText+'|');
						node.nodeValue = resultShowText;  //2025.4.26 变更为此方式
						if(resultShowText.indexOf(resultText) > -1){
							result['resultText'] = resultShowText;
						}else{
							result['resultText'] = '';
						}
					}
					result['text'] = node.nodeValue;
				}
				return result;
			},
			/*
				在 analyse set 设置到dom之前，先将数据同步到 translate.node 中进行记录
				
				node: translate.element.nodeAnalyse.analyse中传入的node
				attribute: translate.element.nodeAnalyse.analyse中传入的attribute
				resultShowText: translate.element.nodeAnalyse.analyse 进行设置翻译后的文本渲染时，提前计算好这个node显示的所有文本，然后在赋予 dom，这里是计算好的node要整体显示的文本
			*/	
			analyseReplaceBefore_DateToTranslateNode:function(node, attribute, resultShowText){
				//console.log('analyseReplaceBefore_DateToTranslateNode: attribute:'+attribute+', resultShowText:'+resultShowText+', node:');
				//console.log(node);

				var translateNode = null; //当前操作的，要记录入 translate.node 中的，进行翻译的node
				var translateNode_attribute = ''; //当前操作的是node中的哪个attribute，如果没有是node本身则是空字符串

				if(typeof(attribute) === 'string' && attribute.length > 0){
					//是操作的元素的某个属性,这时要判断 是否是 input、textarea 的value属性
					if(attribute !== null && attribute === 'value'){
						var nodeNameLowerCase = translate.element.getNodeName(node).toLowerCase();
						if((nodeNameLowerCase === 'input' || nodeNameLowerCase === 'textarea')){
							translateNode = node;
							translateNode_attribute = 'value';
						}
					}
					if(translateNode === null){
						translateNode = node.getAttributeNode(attribute);
						translateNode_attribute = attribute;
					}
				}else{
					//操作的就是node本身
					translateNode = node;
				}

				if(translate.node.find(translateNode)){
					if(typeof(translate.node.get(translateNode).translateResults) === 'undefined'){
						translate.node.get(translateNode).translateResults = {};
					}
					translate.node.get(translateNode).translateResults[resultShowText] = 1;
					translate.node.get(translateNode).resultText = resultShowText;
				}else{
					//翻译过程中，会有时间差，比如通过文本翻译api请求，这时node元素本身被其他js改变了，导致翻译完成后，原本的node不存在了
					//console.log('[debug] 数据异常，analyse - set 中发现 translate.node 中的 node 不存在，理论上应该只要被扫描了，被翻译了，到这里就一定会存在的，不存在怎么会扫描到交给去翻译呢');
				}
			},
		},

		/*js translate.element.iframe start*/
		iframe:{
			isUse:false, //是否启用，对非跨域的iframe的页面也进行自动翻译。true则是启用。默认是false为不启用
			translateJsUrl: '', //设置载入的 translate.js 这个文件的url， iframe 中会自动
			//启用对同域的iframe也进行翻译（即使页面中没有引入 translate.js）
			use: function(translateJsUrl){
				if(typeof(window.location.protocol) === 'string' && window.location.protocol.toLowerCase() === 'file:'){
					console.log('ERROR: 您当前设置了 translate.element.iframe.use(...); 但是您当前的协议是file协议访问的，这个协议访问会存在跨域问题，所以您的设置没有生效。 您可以通过本地开一个访问服务，以 http、https 等正常请求的方式进行访问。');
					return;
				}
				translate.element.iframe.isUse = true;
				translate.element.iframe.translateJsUrl = translateJsUrl;
			},
			/*
				用于记录已经操作过的iframe
				key: iframe 元素
				value: 
					addLoad: true  如果没有添加 load 的事件，这里是空的，也就是通过判断 typeof(iframeMap.get(iframe).addLoad) === 'boolean' && iframeMap.get(iframe).addLoad === true 来判断是否已经添加 load 事件了
					isTranslate: true 是否已经触发过 ifr.injectJs(); 翻译了， 如果已经触发过，则是true 也就是通过判断 typeof(iframeMap.get(iframe).isTranslate) === 'boolean' && iframeMap.get(iframe).isTranslate === true 来判断是否已经触发过
			*/
			// iframe 节点可能被页面动态移除，使用 WeakMap 避免缓存强引用导致节点无法释放。
			iframeMap: new WeakMap(),
			
			/**
			 * 通过URL判断iframe是否未跨域（true=未跨域，false=跨域）
			 * @param {HTMLIFrameElement} iframe - iframe DOM对象
			 * @returns {boolean} true=未跨域，false=跨域
			 */
			isIframeSameOrigin: function(iframe) {
				// 1. 先校验iframe参数有效性：不是有效DOM对象，直接返回false（跨域）
				if (!iframe || !(iframe instanceof HTMLIFrameElement)) {
					console.warn('传入的iframe不是有效的DOM对象');
					return false;
				}

				// 2. 获取当前页面的origin
				const currentOrigin = window.location.origin;

				// 3. 获取iframe的实际URL（优先取内部URL，跨域则取src，增加兜底）
				let iframeUrl;
				try {
					// 同域时：获取iframe最终的URL（含重定向）
					iframeUrl = iframe.contentDocument?.URL || iframe.contentWindow?.location.href;
				} catch (e) {
					// 跨域时：取src，若src为空则默认about:blank
					iframeUrl = iframe.src || 'about:blank';
				}

				// 4. 特殊处理：about:blank 视为未跨域
				if (iframeUrl === 'about:blank') return true;

				// 5. 解析iframe URL的origin并对比（增加解析容错）
				try {
					const iframeOrigin = new URL(iframeUrl).origin;
					return currentOrigin === iframeOrigin;
				} catch (e) {
					// 无效URL解析失败，默认视为跨域
					return false;
				}
			},
			
			/*
				要执行注入时的对象。
				每次注入一个iframe，都会new一个出来
			*/
			iframeJsInjector : class{
				// 存储要注入的 JS 地址（可选，也可在调用时传入）
				translateJsUrl = '';
				iframe = null;

				// 构造函数：初始化 JS 地址
				constructor(iframe, translateJsUrl) {
					this.translateJsUrl = translateJsUrl;
					this.iframe = iframe;
				}

				//是否已经注入了tranlate.js ， true已经触发 importJsAndTranslateExecute(...) 注入
				isInjectJs=false;

				importJsAndTranslateExecute = function(){
					//console.log('importJsAndTranslateExecute -> '+this.translateJsUrl);
					if(this.isInjectJs === true){
						console.log('已导入了，不在继续导入');
						return;
					}
					if(!translate.element.iframe.isIframeSameOrigin(this.iframe)){
						//console.log('iframe跨域，忽略 - ');
						//console.log(this.iframe);
						return;
					}
					
					var iframeContentWindow;
					try{
						iframeContentWindow = this.iframe.contentWindow;
					} catch (e) {
						console.error('注入失败（大概率跨域）', e);
						return;
					}
					
					this.isInjectJs = true;
					try {
						const iframeDoc = this.iframe.contentDocument || this.iframe.contentWindow.document;
						// 用 iframe 新文档创建 script（此时是新文档，不是之前的 about:blank）
						const script = iframeDoc.createElement('script');
						script.type = 'text/javascript';
						script.src = this.translateJsUrl;
						
						script.onload = function() {
							//console.log('✅ JS 注入成功');

							var parentConfigData = parent.translate.config.get();
							iframeContentWindow.translate.config.set(parentConfigData);
							iframeContentWindow.translate.to = iframeContentWindow.translate.language.getCurrent();
							//iframeContentWindow.translate.time.use = true;

							setTimeout(function(){
								iframeContentWindow.translate.execute();
							},10);

						}
						script.onerror = function(err) {
							console.log('失败：');
							console.log(err);
						}

						// 插入到新文档的任意位置（无需 head，body/html 都可）
						iframeDoc.documentElement.appendChild(script);
						
					} catch (e) {
						console.error('注入失败（大概率跨域）', e);
					}
				}

				injectJs = function(){
					if(typeof(this.iframe) === 'object'){
						//存在于当前页面的dom中了
						if(typeof(this.iframe.contentDocument) === 'object'){
							//有了dom了
							if(typeof(this.iframe.contentDocument.readyState) === 'string'){
								//有了正常的状态了
								this.importJsAndTranslateExecute();
							}else{
								console.log('iframe - '+this.translateJsUrl+' state is not string');
							}
						}else{
							console.log('iframe - '+this.translateJsUrl+' state is not string');
						}
					}else{
						console.log('iframe - '+this.translateJsUrl+' is not find (not object)');
					}
				}
			},


			/*
				对某个iframe进行翻译  
				iframeTag: 传入 iframe 的对象，比如  document.getElementById('iframe')
			*/
			execute: function(iframeTag){
				if(translate.element.iframe.isUse === false){
					return;
				}

				if(!translate.element.iframe.isIframeSameOrigin(iframeTag)){
					//console.log('iframe跨域，忽略 - ');
					//console.log(this.iframeTag);
					return;
				}

				if(translate.element.iframe.iframeMap.get(iframeTag) === null || typeof(translate.element.iframe.iframeMap.get(iframeTag)) === 'undefined'){
					translate.element.iframe.iframeMap.set(iframeTag, {});
				}
				
				if(typeof(iframeTag.src) === 'string' && iframeTag.src.trim().length > 0){
					//是通过 src 加载内容的
					
					// 先监听 iframe 的 load（确保 iframe 内部 window 存在）
					//console.log(typeof(translate.element.iframe.iframeMap.get(iframeTag)));
					if(typeof(translate.element.iframe.iframeMap.get(iframeTag).addLoad) !== 'boolean' || translate.element.iframe.iframeMap.get(iframeTag).addLoad !== true){
						//未添加过 load 事件，需要添加
						translate.element.iframe.iframeMap.get(iframeTag).addLoad = true;
						iframeTag.addEventListener('load', function() {
							console.log('----load url: '+iframeTag.src);
							var iframeWindow = iframeTag.contentWindow;
							if(typeof(iframeWindow.translate) === 'object' && typeof(iframeWindow.translate.version) === 'string'){
								//发现了iframe中已经成功引入了 translate.js ，将不在注入
							}else{
								//iframe中没有发现 translate.js ，进行注入								
								translate.element.iframe.iframeMap.get(iframeTag).isTranslate = true;
								var ifr = new translate.element.iframe.iframeJsInjector(iframeTag, translate.element.iframe.translateJsUrl);
								ifr.injectJs();
							}
						});
					}
					
					
					
				}else{
					//不通过src，根本就没有src参数，直接用js渲染赋予内容的，那么就不用监听了，直接强制赋予
					var ifr = new translate.element.iframe.iframeJsInjector(iframeTag, translate.element.iframe.translateJsUrl);
					ifr.injectJs();
				}
				
				
				
				// 先监听 iframe 的 load（确保 iframe 内部 window 存在）
				//iframeTag.addEventListener('load', function() {
				//	console.log('----load');
				//});
			}
		},
		/*js translate.element.iframe end*/

		//获取这个node元素的node name ,如果未发现，则返回''空字符串
		getNodeName:function(node){
			if(node == null || typeof(node) == 'undefined'){
				return '';
			}

			if(node.nodeName == null || typeof(node.nodeName) == 'undefined'){
				return '';
			}

			var nodename = node.nodeName;
			if(typeof(node.nodeName) == 'string'){
				return node.nodeName;
			}else{
				if(typeof(node.tagName) == 'string' && node.tagName.length > 0){
					return node.tagName;
				}else{
					translate.log('warn : get nodeName is null, this node ignore translate. node : ');
					translate.log(node);
					return '';
				}
			}
		},
		/*
			向下遍历node
			其中如果使用了自定义 textarea 、input 的 value 属性，则认为是 node 本身进行挂钩，而非其 value 值（value值并不是个node）
		*/
		whileNodes:function(uuid, node){
			if(node == null || typeof(node) == 'undefined'){
				return;
			}

			//如果这个uuid没有，则创建
			if(typeof(translate.nodeQueue[uuid]) == 'undefined' || translate.nodeQueue[uuid] == null){
				translate.nodeQueue[uuid] = new Array(); //创建
				translate.nodeQueue[uuid]['expireTime'] = Date.now() + 120*1000; //删除时间，10分钟后删除
				translate.nodeQueue[uuid]['list'] = new Array(); 
				//console.log('创建 --- ');
				//console.log(uuid)
			}

			//console.log('---'+typeof(node)+', ');
			//判断是否是有title属性，title属性也要翻译
			if(typeof(node) == 'object' && typeof(node['title']) == 'string' && node['title'].trim().length > 0){
				//将title加入翻译队列
				//console.log('---'+node.title+'\t'+node.tagName);
				//console.log(node)
				//console.log('------------');
				
				//判断当前元素是否在ignore忽略的tag、id、class name中
				if(!translate.ignore.isIgnore(node, {node: node.getAttributeNode('title'), attribute: 'title'})){
					//不在忽略的里面，才会加入翻译
					//translate.addNodeToQueue(uuid, node, node['title'], 'title');
					translate.addNodeToQueue(uuid, node.getAttributeNode('title'), node['title'], '');
				}
			}

			//v3.9.2 增加, 用户可自定义标签内 attribute 的翻译
			var nodeNameLowerCase = translate.element.getNodeName(node).toLowerCase();
			if(typeof(translate.element.tagAttribute[nodeNameLowerCase]) != 'undefined'){
				//console.log('find:'+nodeNameLowerCase);
				//console.log(translate.element.tagAttribute[nodeNameLowerCase]);
				//console.log(translate.element.tagAttribute[nodeNameLowerCase].attribute);

				for(var attributeName_index in translate.element.tagAttribute[nodeNameLowerCase].attribute){
					if (!translate.element.tagAttribute[nodeNameLowerCase].attribute.hasOwnProperty(attributeName_index)) {
			    		continue;
			    	}
			    	if(typeof(translate.element.tagAttribute[nodeNameLowerCase].condition) !='undefined' && !translate.element.tagAttribute[nodeNameLowerCase].condition(node)){
			    		continue;
			    	}
					
					var attributeName = translate.element.tagAttribute[nodeNameLowerCase].attribute[attributeName_index];
					//console.log(attributeName);
					//console.log(node.getAttribute(attributeName));

					//是否是 input、 textarea 的 value ，如果是 则是 true
					var isInputValue = false;
					if((nodeNameLowerCase === 'input' || nodeNameLowerCase === 'textarea') && attributeName.toLowerCase() == 'value'){
						//如果是input 的value属性，那么要直接获取，而非通过 attribute ，不然用户自己输入的通过 attribute 是获取不到的 - catl 赵阳 提出
						attributeValue = node.value;
						DOMPropOrHTMLAttr = 'DOMProperty';
						isInputValue = true;
					}else{
						/*
						 * 默认是 HtmlAtrribute 也就是 HTML特性。取值有两个:
						 * HTMLAtrribute : HTML特性
						 * DOMProperty : DOM属性
						 */
						var DOMPropOrHTMLAttr = 'HTMLAtrribute'; 
						var attributeValue = node.getAttribute(attributeName);
						if(typeof(attributeValue) == 'undefined' || attributeValue == null){
							//vue、element、react 中的一些动态赋值，比如 element 中的 el-select 选中后赋予显示出来的文本，getAttribute 就取不到，因为是改动的 DOM属性，所以要用这种方式才能取出来
							attributeValue = node[attributeName];
							DOMPropOrHTMLAttr = 'DOMProperty';
						}
						if(typeof(attributeValue) == 'undefined' || attributeValue == null){
							//这个tag标签没有这个属性，忽略
							continue;
						}
					}
					

					//if(typeof(node.getAttribute(attributeName)) == 'undefined' && typeof(node[attributeName]) == 'undefined'){
					//	//这个tag标签没有这个 attribute，忽略
					//	continue
					//}
					//判断当前元素是否在ignore忽略的tag、id、class name中   v3.15.7 增加	
					if(!translate.ignore.isIgnore(node, {attribute: attributeName})){
						//加入翻译
						translate.addNodeToQueue(uuid, isInputValue? node:node.getAttributeNode(attributeName), attributeValue, isInputValue? 'value':'');
					}
				}
			}

			
			var childNodes = node.childNodes;
			if(childNodes == null || typeof(childNodes) == 'undefined'){
				return;
			}
			if(childNodes.length > 0){
				for(var i = 0; i<childNodes.length; i++){
					translate.element.whileNodes(uuid, childNodes[i]);
				}
			}else{
				//单个了
				translate.element.findNode(uuid, node);
			}
		},
		findNode:function(uuid, node){
			if(node == null || typeof(node) == 'undefined'){
				return;
			}
			if(node.nodeType === 2){  //是属性node，比如 div 的 title 属性的 node
				if(node.ownerElement == null){
					return;
				}
			}else{		//是元素了
				if(node.parentNode == null){
					return;
				}	
			}
			
			/****** 判断忽略的class ******/
			/*
			这段理论上不需要了，因为在  translate.ignore.isIgnore 判断了
			var ignoreClass = false;	//是否是被忽略的class，true是
			var parentNode = node.parentNode;
			while(node != parentNode && parentNode != null){
				//console.log('node:'+node+', parentNode:'+parentNode);
				if(parentNode.className != null){
					if(translate.ignore.class.indexOf(parentNode.className) > -1){
						//发现ignore.class 当前是处于被忽略的 class
						ignoreClass = true;
					}
				}
				
				parentNode = parentNode.parentNode;
			}
			if(ignoreClass){
				//console.log('ignore class :  node:'+node.nodeValue);
				return;
			}
			*/
			/**** 判断忽略的class结束 ******/



			//node分析，分析这个node的所有可翻译属性（包含自定义翻译属性 translate.element.tagAttribute ）
			var nodeAnalyChild = translate.element.nodeAnalyse.gets(node);
			//console.log(nodeAnalyChild);
			for(var nci = 0; nci < nodeAnalyChild.length; nci++){

				/**** 避免中途局部翻译，在判断一下 ****/
				//判断当前元素是否在ignore忽略的tag、id、class name中。 这里要放到循环里面，是因为class 有 function 参数进行可编程判断
				if(translate.ignore.isIgnore(node, {node: nodeAnalyChild[nci].node, attribute: nodeAnalyChild[nci].attribute})){
					//console.log('node包含在要忽略的元素中：');
					//console.log(node);
					continue;
				}

				translate.addNodeToQueue(uuid, nodeAnalyChild[nci].node, nodeAnalyChild[nci].text, '');
			}
			/*
			var nodeAnaly = translate.element.nodeAnalyse.get(node);
			if(nodeAnaly['text'].length > 0){
				//有要翻译的目标内容，加入翻译队列
				console.log(nodeAnaly)
				console.log('addNodeToQueue -- '+nodeAnaly['node']+', text:' + nodeAnaly['text']);
				translate.addNodeToQueue(uuid, nodeAnaly['node'], nodeAnaly['text'], '');
			}
			*/
			
			//console.log(nodeAnaly);
			/*
			//console.log(node.nodeName+', type:'+node.nodeType+', '+node.nodeValue);
			var nodename = translate.element.getNodeName(node);
			if(nodename == 'INPUT' || nodename == 'TEXTAREA'){
				//input 输入框，要对 placeholder 做翻译
				console.log('input---'+node.attributes);
				if(node.attributes == null || typeof(node.attributes) == 'undefined'){
					return;
				}
	
				if(typeof(node.attributes['placeholder']) != 'undefined'){
					//console.log(node.attributes['placeholder'].nodeValue);
					//加入要翻译的node队列
					//translate.nodeQueue[translate.hash(node.nodeValue)] = node.attributes['placeholder'];
					//加入要翻译的node队列
					//translate.addNodeToQueue(translate.hash(node.attributes['placeholder'].nodeValue), node.attributes['placeholder']);
					translate.addNodeToQueue(uuid, node.attributes['placeholder'], node.attributes['placeholder'].nodeValue);
				}
				
				//console.log(node.getAttribute("placeholder"));
			}else if(nodename == 'META'){
				//meta标签，如是关键词、描述等
				if(typeof(node.name) != 'undefined' && node.name != null){
					var nodeAttributeName = node.name.toLowerCase();  //取meta 标签的name 属性
					//console.log(nodeName);
					if(nodeAttributeName == 'keywords' || nodeAttributeName == 'description'){
						//关键词、描述
						translate.addNodeToQueue(uuid, node, node.content);
					}
				}
				//console.log(node.name)
			}else if(nodename == 'IMG'){
				//console.log('-------'+node.alt);
				translate.addNodeToQueue(uuid, node, node.alt);
			}else if(node.nodeValue != null && node.nodeValue.trim().length > 0){
	
				//过滤掉无效的值
				if(node.nodeValue != null && typeof(node.nodeValue) == 'string' && node.nodeValue.length > 0){
				}else{
					return;
				}
	
				//console.log(node.nodeValue+' --- ' + translate.language.get(node.nodeValue));
				
				//console.log(node.nodeName);
				//console.log(node.parentNode.nodeName);
				//console.log(node.nodeValue);
				//加入要翻译的node队列
				translate.addNodeToQueue(uuid, node, node.nodeValue);	
				//translate.addNodeToQueue(translate.hash(node.nodeValue), node);
				//translate.nodeQueue[translate.hash(node.nodeValue)] = node;
				//translate.nodeQueue[translate.hash(node.nodeValue)] = node.nodeValue;
				//node.nodeValue = node.nodeValue+'|';
	
			}
			*/

		},
		/*
			将node转为element输出。
				如果node是文本元素，则转化为这个文本元素所在的element元素
				如果node是属性，则转化为这个属性所在的element元素
				如果node本身就是元素标签，那就还是这样返回。
			
			
			nodes: node数组，传入如 [node1,node2, ...] 它里面可能包含 node.nodeType 1\2\3 等值

			返回这些node转化为所在元素后的数组，返回如 [element1, element2, ...]
			注意的是
				1. 输出的一定是 element 元素，也就是 node.nodeType 一定等于1
				2. 输出的元素数组不一定等于传入的nodes数组，也就是他们的数量跟下标并不是对应相等的
			
		*/
		nodeToElement: function(nodes){
			var elements = new Array(); //要改动的元素

			//遍历所有node组合到 nodes. 这个不单纯只是遍历组合，它会判断如果是文本节点，则取它的父级元素。它组合的结果是元素的集合
		    for(var r = 0; r<nodes.length; r++){
		    	var node = nodes[r];
	    		if(typeof(node) == 'undefined' || typeof(node.parentNode) == 'undefined'){
	    			continue;
	    		}
	    		if(node.nodeType === 2){
	    			//是属性节点，可能是input、textarea 的 placeholder ，获取它的父元素
	    			var nodeParentElement = node.ownerElement;
			        if(nodeParentElement == null){
			        	continue;
			        }
			        elements.push(nodeParentElement);
	    		}else if(node.nodeType === 3){
	    			//是文本节点
	    			var nodeParentElement = node.parentNode;
	    			if(nodeParentElement == null){
			        	continue;
			        }
			        elements.push(nodeParentElement);
	    		}else if(node.nodeType === 1){
	    			//元素节点了，直接加入
	    			elements.push(node);
	    		}else{
	    			//1\2\3 都不是，这不应该是 translate.js 中应该出现的
	    			translate.log('translate.element.nodeToElement 中，发现传入的node.nodeType 类型有异常，理论上不应该存在， node.nodeType:'+node.nodeType);
	    			translate.log(node);
	    		}
	    	}	

	    	return elements;
		}
	},

	


	
	/*
	 * 将发现的元素节点加入待翻译队列
	 * uuid execute方法执行的唯一id
	 * node 当前text所在的node
	 * text 当前要翻译的目标文本
	 * attribute 是否是元素的某个属性。比如 a标签中的title属性， a.title 再以node参数传入时是string类型的，本身并不是node类型，所以就要传入这个 attribute=title 来代表这是a标签的title属性。同样第二个参数node传入的也不能是a.title，而是传入a这个node元素
	 			如果不穿或者传入 '' 空字符串，则代表不是 attribute 属性，而是nodeValue 本身
	 			注意， textarea、input 标签的 value 属性的特殊性，如果 node 是textarea、input ，那么value时这个 attribute 要传递 'value' 进来的
	 */
	addNodeToQueue:function(uuid, node, text, attribute){
		//console.log('addNodeToQueue - params: uuid:'+uuid+', text:'+text+', attribute:'+attribute+', node:');
		//console.log(node);
		
		if(node == null || text == null || text.length == 0){
			return;
		}


		//console.log('find tag ignore : '+node.nodeValue+', '+node.nodeName+", "+node.nodeType+", "+node.tagName);
		//console.log('addNodeToQueue into -- node:'+node+', text:'+text+', attribute:'+attribute);
		var nodename = translate.element.getNodeName(node).toLowerCase();
		
		//判断如果是被 <!--  --> 注释的区域，不进行翻译
		if(nodename == '#comment'){
			return;
		}
		//console.log('\t\t'+text);
		//取要翻译字符的hash
		var key = translate.util.hash(text);
		/*
		如果是input 的 placeholder ,就会出现这个情况
		if(node.parentNode == null){
			console.log('node.parentNode == null');
			return;
		}
		*/

		//console.log(node.parentNode);
		//console.log(node.parentNode.nodeName);
		
		//判断其内容是否是 script、style 等编程的文本，如果是，则不进行翻译，不然翻译后还会影响页面正常使用
		if(translate.util.findTag(text)){
			//console.log('find tag ignore : '+node.nodeValue+', '+node.nodeName+", "+node.nodeType+", "+node.tagName);
			//console.log(node.parentNode.nodeName);
			
			//获取到当前文本是属于那个tag标签中的，如果是script、style 这样的标签中，那也会忽略掉它，不进行翻译
			if(node.parentNode == null){
				//没有上级了，或是没获取到上级，忽略
				return;
			}
			//去上级的tag name
			var parentNodeName = translate.element.getNodeName(node.parentNode);
			//node.parentNode.nodeName;
			if(parentNodeName == 'SCRIPT' || parentNodeName == 'STYLE'){
				//如果是script、style中发现的，那也忽略
				return;
			}
		}
		//console.log(node.nodeValue);


		/***** 记录这个node 到 translate.node.data，这也是node进入 translate.node.data 记录的第一入口 *****/
		var translateNode; //当前操作的，要记录入 translate.node 中的，进行翻译的node
		var translateNode_attribute = ''; //当前操作的是node中的哪个attribute，如果没有是node本身则是空字符串
		if(typeof(attribute) === 'string' && attribute.length > 0){
			//是操作的元素的某个属性,这时要判断 是否是 input、textarea 的value属性
			if((nodename === 'input' || nodename === 'textarea') && attribute !== null && attribute === 'value'){
				translateNode = node;
				translateNode_attribute = 'value';
			}else{
				translateNode = node.getAttributeNode(attribute);
				translateNode_attribute = attribute;
			}
		}else{
			//操作的就是node本身
			translateNode = node;
		}
		if(translate.node.get(translateNode) == null){
			translate.node.set(translateNode, {});
		}

		//var nodeAttribute = translate.node.getAttribute(attribute);
		//console.log(text+'-----:');
		//console.log(translate.node.get(translateNode));
		//if(typeof(translate.node.get(translateNode)[nodeAttribute.key]) == 'undefined'){
		//	translate.node.get(node)[nodeAttribute.key] = {};
		//}
		translate.node.get(translateNode).attribute = translateNode_attribute;
		if(typeof(translate.node.get(translateNode).originalText) === 'string'){
			//这个节点有过记录原始显示的文本了，那么不再对其进行后续的扫描，除非它有被触发过动态监听元素改变， --- 至于它有被触发过动态监听元素改变--后续想怎么判定
			//console.log(translate.node.get(node)[nodeAttribute.key].originalText+'\t又过了，不在翻译');
			return;
		}else{
			//没有过，是第一次，那么赋予值
			translate.node.get(translateNode).originalText = text;
		}
		//console.log(translateNode);
		//console.log(translate.node.get(translateNode));
		/*
		if(typeof(translate.node.get(node).translateTexts) != 'undefined'){ 
			//这个node之前已经被扫描过了，那么判断一下上次扫描的文本跟当前获取到的文本是否一致，如果一致，那就没必要进行翻译了
			//这个一致，是跟通过文本翻译接口的，翻译前或者翻译后的文本，任何一个相等，就都不需要被翻译
			for(var originalText in translate.node.get(node).translateTexts){
				if (!translate.node.get(node).translateTexts.hasOwnProperty(originalText)) {
		    		continue;
		    	}
			    if(originalText === text || (translate.node.get(node).translateTexts[originalText] != null && translate.node.get(node).translateTexts[originalText] === text)){
			    	console.log('这个node之前已经被翻译过了，有翻译结果，那么判断一下翻译结果跟当前获取到的文本是否一致，如果一致，那就没必要进行翻译了, text：'+text);
					return;
			    }
			}
		}
		*/
		/*
		// 将传入的 hitNomenclatureArray 的所有键值对添加到 translate.node.get(node).hitNomenclatureArray 中
		hitNomenclatureArray.forEach((value, key) => {
		  translate.node.get(node).hitNomenclatureArray.set(key, value);
		});
		*/
		/*
		if(typeof(translate.node.get(node).originalText) == 'string' && translate.node.get(node).originalText === text){ 
			console.log('这个node之前已经被搜索节点并分析过了, text：'+text);
			return;
		}
		*/
		if(typeof(translate.node.get(translateNode).translateTexts) === 'undefined'){
			translate.node.get(translateNode).translateTexts = {};
		}
		/***** 自检完毕，准备进行翻译了 *****/


		//原本传入的text会被切割为多个小块
		var textArray = new Array();
		textArray.push(text); //先将主 text 赋予 ，后面如果对主text进行加工分割，分割后会将主text给删除掉
		//console.log(textArray);

		// 处理 ignore.regex
		var temporaryIgnoreTexts = [];  //仅仅针对当前text文本，通过 translate.ignore.textRegex 所产生的临时不翻译的文本，它并不能作用于其他节点的文本
		for (var ri = 0; ri < translate.ignore.textRegex.length; ri++) {
			var regex = translate.ignore.textRegex[ri];
			for (var tai = 0; tai < textArray.length; tai++) {
				var currentText = textArray[tai];
				//temporaryIgnoreTexts = text.match(regex) || []
				var matches = currentText.match(regex) || [];
				temporaryIgnoreTexts = temporaryIgnoreTexts.concat(matches);
				//translate.ignore.text = translate.ignore.text.concat(ignoreTexts)
			}
		}
		
		//将当前节点文本的 不翻译文本规则，重新组合到 temporaryIgnoreTextsByRegex
		if(temporaryIgnoreTexts.length == 0){
			temporaryIgnoreTexts = translate.ignore.text;
		}else{
			//将其加入 translate.history.translateTexts 中
			temporaryIgnoreTexts = temporaryIgnoreTexts.concat(translate.ignore.text);
			for(var ti = 0; ti<temporaryIgnoreTexts.length; ti ++){
				translate.history.translateText.add(temporaryIgnoreTexts[ti], temporaryIgnoreTexts[ti]);
			}
		}
		
		/**** v3.10.2.20241206 - 增加自定义忽略翻译的文本，忽略翻译的文本不会被翻译 - 当然这样会打乱翻译之后阅读的连贯性 ****/
		for(var ti = 0; ti<temporaryIgnoreTexts.length; ti++){
			if(temporaryIgnoreTexts[ti].trim().length == 0){
				continue;
			}

			//textArray = translate.addNodeToQueueTextAnalysis(uuid, node, textArray, attribute, temporaryIgnoreTexts[ti], temporaryIgnoreTexts[ti]);
			
			//console.log(textArray);
			textArray = translate.nomenclature.dispose(textArray, temporaryIgnoreTexts[ti], temporaryIgnoreTexts[ti], {
				node:translateNode,
				attribute:translateNode_attribute
			}).texts;
			//console.log(textArray);
		}


		/**** v3.10.2.20241206 - 自定义术语能力全面优化 - 当然这样会打乱翻译之后阅读的连贯性 ****/
		//判断是否进行了翻译，也就是有设置目标语种，并且跟当前语种不一致
		if(typeof(translate.temp_nomenclature) == 'undefined'){
			translate.temp_nomenclature = new Array();
		}
		if(typeof(translate.temp_nomenclature[translate.language.getLocal()]) == 'undefined'){
			nomenclatureKeyArray = new Array();
		}
		if(typeof(translate.nomenclature.data[translate.language.getLocal()]) != 'undefined' && typeof(translate.nomenclature.data[translate.language.getLocal()][translate.to]) != 'undefined'){
			var nomenclatureKeyArray;
			for(var nomenclatureKey in translate.nomenclature.data[translate.language.getLocal()][translate.to]){
				if (!translate.nomenclature.data[translate.language.getLocal()][translate.to].hasOwnProperty(nomenclatureKey)) {
		    		continue;
		    	}
				//nomenclatureKey 便是自定义术语的原始文本，值是要替换为的文本
				//console.log(nomenclatureKey);
				//自定义属于的指定的结果字符串
				var nomenclatureValue = translate.nomenclature.data[translate.language.getLocal()][translate.to][nomenclatureKey];

				//console.log('----translate.nomenclature.dispose---');
				//console.log(textArray);
				var nomenclatureDispose = translate.nomenclature.dispose(textArray, nomenclatureKey, nomenclatureValue, {
					node:translateNode,
					attribute:translateNode_attribute
				});
				
				textArray = nomenclatureDispose.texts;
				if(nomenclatureDispose.find){
					//console.log('发现自定义术语，并已进行替换处理：');
					//console.log(nomenclatureDispose);
				}
				
				if(typeof(nomenclatureKeyArray) != 'undefined'){
					nomenclatureKeyArray.push(nomenclatureKey);
				}
			}

			if(typeof(translate.temp_nomenclature[translate.language.getLocal()]) == 'undefined'){
				translate.temp_nomenclature[translate.language.getLocal()] = nomenclatureKeyArray;
			}
		}
		/**** v3.10.2.20241206 - 自定义术语能力全面优化 - end ****/
		

		//记录 nodeHistory - 判断text是否已经被拆分了
		if(textArray.length > 0 && textArray[0] != text){  //主要是后面的是否相等，前面的>0只是避免代码报错
			translate.node.get(translateNode).whole = false; //已经被拆分了，不是整体翻译了
			//这时，也默认给其赋值操作，将自定义术语匹配后的结果进行赋予

			for(var tai = 0; tai < textArray.length; tai++){
				translate.node.get(translateNode).translateTexts[textArray[tai]] = null;
			}
		}else{
			translate.node.get(translateNode).whole = true; //未拆分，是整体翻译
		}
		//成功加入到 nodeQueue 的对象。 如果长度为0，那就是还没有加入到 translate.nodeQueue 中，可能全被自定义术语命中了
		var addQueueObjectArray = [];

		//console.log(textArray);
		for(var tai = 0; tai<textArray.length; tai++){
			if(textArray[tai].trim().length == 0){
				continue;
			}

			/* 自定义术语 - 忽略翻译文本  ， 在本方法的上面已经完成了识别，这里就不再需要了
			//判断是否出现在自定义忽略字符串
			if(translate.ignore.text.indexOf(textArray[tai].trim()) > -1){
				//console.log(textArray[tai]+' 是忽略翻译的文本，不翻译');
				continue;
			}
			*/

			/* 自定义术语，在本方法的上面已经完成了识别，这里就不再需要了
			//判断是否出现在自定义术语的
			if(typeof(translate.temp_nomenclature[translate.language.getLocal()]) != 'undefined'){
				if(translate.temp_nomenclature[translate.language.getLocal()].indexOf(textArray[tai].trim()) > -1){
					//console.log(textArray[tai]+' 是自定义术语，不翻译');
					continue;
				}
			}
			*/

			var newAddQueueArray = translate.addNodeToQueueAnalysis(uuid, node, textArray[tai], attribute);
			//console.log(newAddQueueArray)
			Array.prototype.push.apply(addQueueObjectArray, newAddQueueArray);
		}
		
		//console.log('成功加入进nodequeue的数量：'+addQueueObjectArray.length);
		//console.log(addQueueObjectArray);

		if(addQueueObjectArray.length == 0){
			//没有加入到 nodeQueue 中，那么也就是在自定义术语这一层，就已经完成了渲染，此时要触发相关钩子
			// translate.node 记录
			
			// 记录当前有 translate.js 所触发翻译之后渲染到dom界面显示的时间，13位时间戳
			translate.node.get(translateNode).lastTranslateRenderTime = Date.now();
			//将具体通过文本翻译接口进行翻译的文本记录到 translate.node.data
			translate.node.get(translateNode).translateTexts = {}; //这里全部命中了，所以根本没有走翻译接口的文本
			//将翻译完成后要显示出的文本进行记录
			translate.node.get(translateNode).resultText = translate.element.nodeAnalyse.get(node, attribute).text; //直接获取当前node显示出来的文本作为最后的结果的文本

			//将其加入 translate.history.translateTexts 中
			translate.history.translateText.add(translate.node.get(translateNode).originalText, translate.node.get(translateNode).resultText);
		}
		
	},

	

	/*

		服务于上面的 addNodeToQueue ，用于区分不同type情况，进行调用此加入 translate.nodeQueue
		uuid, node, attribute 这五个参数说明见 addNodeToQueue 的参数说明，相同
		
		word 要实际进行翻译的文本，也就是要把它拿来进行通过后端翻译接口进行翻译的文本
		lang 当前要翻译的文本的语种，如 english
		beforeText 参见 translate.nodeQueue 注释中第七维的解释
		afterText 参见 translate.nodeQueue 注释中第七维的解释
		

		返回:
			加入 nodeQueue 后的对象。 
			这里跟addNodeQueueItem方法返回一样，只不过 addNodeQueueItem 方法返回的是一个，而这里是多个，数组的形式。
			如果一个也没有加入到 nodeQueue,那么这里返回的数组长度便是0
	*/
	addNodeToQueueAnalysis:function(uuid, node, text, attribute){
		//获取当前是什么语种
		//console.log('uuid:'+uuid+', text:'+text+', attribute:'+attribute+'node:');
		//console.log(node);
		//var langs = translate.language.get(text);
		var textRecognition = translate.language.recognition(text);
		var langs = textRecognition.languageArray;
		//console.log('langs');
		//console.log(langs);

		
		//过滤掉要转换为的目标语种，比如要转为英语，那就将本来是英语的部分过滤掉，不用再翻译了
		if(typeof(langs[translate.to]) != 'undefined'){
			delete langs[translate.to];
		}
		
		var isWhole = translate.whole.isWhole(node);
		//console.log('isWhole:'+isWhole+', '+text);

		//记录成功加入 nodeQueue 的，如果加入了多个，那就是多个数组，如果长度为0，那就是啥也没加入了
		var addNodeQueueArray = [];

		if(!isWhole){
			//常规方式，进行语种分类


			/* if(this.nodeQueue[lang] == null || typeof(this.nodeQueue[lang]) == 'undefined'){
				this.nodeQueue[lang] = new Array();
			} 
			//创建二维数组
			if(this.nodeQueue[lang][key] == null || typeof(this.nodeQueue[lang][key]) == 'undefined'){
				this.nodeQueue[lang][key] = new Array();
			}
			*/
			//console.log(langs);
			
			for(var lang in langs) {
				if (!langs.hasOwnProperty(lang)) {
		    		continue;
		    	}
				//创建二维数组， key为语种，如 english
				/*
				放到了 translate.addNodeQueueItem 进行判断
				if(translate.nodeQueue[uuid]['list'][lang] == null || typeof(translate.nodeQueue[uuid]['list'][lang]) == 'undefined'){
					translate.nodeQueue[uuid]['list'][lang] = new Array();
				}
				*/
				//console.log('|'+langs[lang].length);
				//遍历出该语种下有哪些词需要翻译
				for(var word_index = 0; word_index < langs[lang].list.length; word_index++){
					//console.log('start:'+word_index)
					//console.log(langs[lang].list[word_index]);
					if(typeof(langs[lang].list[word_index]) == 'undefined' || typeof(langs[lang].list[word_index]['text']) == 'undefined'){
						//理论上应该不会，但多加个判断
						continue;
					}
					var word = langs[lang].list[word_index]['text']; //要翻译的词
					var beforeText = langs[lang].list[word_index]['beforeText'];
					var afterText = langs[lang].list[word_index]['afterText'];

					//console.log(lang+' - '+word+', attribute:'+attribute);
					var addQueue = translate.addNodeQueueItem(uuid, node, word, attribute, lang, beforeText, afterText);
					if(addQueue != null){
						addNodeQueueArray.push(addQueue);
					}

					/*
					var hash = translate.util.hash(word); 	//要翻译的词的hash					
					//创建三维数组， key为要通过接口翻译的文本词或句子的 hash （注意并不是node的文本，而是node拆分后的文本）
					if(translate.nodeQueue[uuid]['list'][lang][hash] == null || typeof(translate.nodeQueue[uuid]['list'][lang][hash]) == 'undefined'){
						translate.nodeQueue[uuid]['list'][lang][hash] = new Array();
						
						translate.nodeQueue[uuid]['list'][lang][hash]['nodes'] = new Array();
						translate.nodeQueue[uuid]['list'][lang][hash]['original'] = word;
						translate.nodeQueue[uuid]['list'][lang][hash]['translateText'] = translate.nomenclature.dispose(word); //自定义术语处理
						//translate.nodeQueue[uuid]['list'][lang][hash]['beforeText'] = beforeText;
						//translate.nodeQueue[uuid]['list'][lang][hash]['afterText'] = afterText;
						//translate.nodeQueue[uuid]['list'][lang][hash]['attribute'] = attribute; //放入 nodes[index][attribute] 元素中
						
						//其中key： nodes 是第四维数组，里面存放具体的node元素对象
						

						//console.log(translate.nodeQueue[uuid]['list'][lang][hash]);
					}
					
					var isEquals = false; //queue中是否已经加入过这个node了（当然是同一hash同一node情况）
					if(typeof(node.isSameNode) != 'undefined'){	//支持 isSameNode 方法判断对象是否相等
						for(var node_index = 0; node_index < translate.nodeQueue[uuid]['list'][lang][hash]['nodes'].length; node_index++){
							if(node.isSameNode(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'])){
								//相同，那就不用在存入了
								//console.log('相同，那就不用在存入了')
								isEquals = true;
								//console.log(node)
								continue;
							}
						}
					}
					if(isEquals){
						//相同，那就不用在存入了
						continue;
					}

					//往五维数组nodes中追加node元素
					var nodesIndex = translate.nodeQueue[uuid]['list'][lang][hash]['nodes'].length;
					translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex] = new Array();
					translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['node']=node; 
					translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['attribute']=attribute;
					translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['beforeText'] = beforeText;
					translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['afterText'] = afterText;
					
					*/

					//console.log('end:'+word_index)
				}
				
			}
			



		}else{
			//直接翻译整个元素内的内容，不再做语种分类
			var lang = translate.language.recognition_languageName_force(textRecognition);
			//console.log(lang+' - '+text);
			var addQueue = translate.addNodeQueueItem(uuid, node, text, attribute, lang, '', '');
			if(addQueue != null){
				addNodeQueueArray.push(addQueue);
			}
		}

		//console.log('-----'+addNodeQueueArray.length);
		return addNodeQueueArray;
	},

	/*

		服务于上面的 addNodeToQueue ，用于区分不同type情况，进行调用此加入 translate.nodeQueue
		uuid, node, attribute 这五个参数说明见 addNodeToQueue 的参数说明，相同
		
		word 要实际进行翻译的文本，也就是要把它拿来进行通过后端翻译接口进行翻译的文本
		lang 当前要翻译的文本的语种，如 english
		beforeText 参见 translate.nodeQueue 注释中第七维的解释
		afterText 参见 translate.nodeQueue 注释中第七维的解释

		返回值判断 不为 null，则是成功加入了 nodeQueue ，返回加入后的  translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex] ,包含这些：
			afterText: 
			attribute: 
			beforeText: 
			node: 

	*/
	addNodeQueueItem:function(uuid, node, word, attribute, lang, beforeText, afterText){
		//console.log('uuid:'+uuid+', word:'+word+', attribute:'+attribute+', lang:'+lang+', beforeText:'+beforeText+', afterText:'+afterText+', node:');
		//console.log(node);

		//创建二维数组， key为语种，如 english
		if(translate.nodeQueue[uuid]['list'][lang] == null || typeof(translate.nodeQueue[uuid]['list'][lang]) == 'undefined'){
			translate.nodeQueue[uuid]['list'][lang] = new Array();
		}
		//console.log(word)
		//var word = text;	//要翻译的文本
		var hash = translate.util.hash(word); 	//要翻译的文本的hash

		//创建三维数组， key为要通过接口翻译的文本词或句子的 hash 。这里翻译的文本也就是整个node元素的内容了，不用在做拆分了
		if(translate.nodeQueue[uuid]['list'][lang][hash] == null || typeof(translate.nodeQueue[uuid]['list'][lang][hash]) == 'undefined'){
			translate.nodeQueue[uuid]['list'][lang][hash] = new Array();

			/*
			 * 创建四维数组，存放具体数据
			 * key: nodes 包含了这个hash的node元素的数组集合，array 多个。其中
			 		nodes[index]['node'] 存放当前的node元素
			 		nodes[index]['attribute'] 存放当前hash，也就是翻译文本针对的是什么，是node本身（nodeValue），还是 node 的某个属性，比如title属性。如果这里不为空，那就是针对的属性操作的
			 * key: original 原始的要翻译的词或句子，html加载完成但还没翻译前的文本，用于支持当前页面多次语种翻译切换而无需跳转
			 * beforeText、afterText:见 translate.nodeQueue 的说明
			 */
			translate.nodeQueue[uuid]['list'][lang][hash]['nodes'] = new Array();
			translate.nodeQueue[uuid]['list'][lang][hash]['original'] = word;
			//自定义术语处理在此前面已经执行过了，所以这个废弃，不需要处理自定义术语部分了
			//translate.nodeQueue[uuid]['list'][lang][hash]['translateText'] = translate.nomenclature.dispose(word); 
			translate.nodeQueue[uuid]['list'][lang][hash]['translateText'] = word;
			//console.log(word)



			//其中key： nodes 是第四维数组，里面存放具体的node元素对象
		}


		var isEquals = false; //queue中是否已经加入过这个node了（当然是同一hash同一node且同一 attribute的 情况）
		if(typeof(node.isSameNode) != 'undefined'){	//支持 isSameNode 方法判断对象是否相等
			for(var node_index = 0; node_index < translate.nodeQueue[uuid]['list'][lang][hash]['nodes'].length; node_index++){
				if(node.isSameNode(translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index]['node'])){
					//在判断 attribute 是否相同
					//console.log('attribute:'+attribute+", 对比的 :"+translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index].attribute)
					if(attribute === translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][node_index].attribute){
						//相同，那就不用在存入了
						isEquals = true;
					}
					//console.log(node)
					continue;
				}
			}
		}
		if(isEquals){
			//相同，那就不用在存入了
			return null;
		}

		//往五维数组nodes中追加node元素
		var nodesIndex = translate.nodeQueue[uuid]['list'][lang][hash]['nodes'].length;
		translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex] = new Array();
		translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['node']=node; 
		translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['attribute']=attribute;
		translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['beforeText'] = beforeText;
		translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex]['afterText'] = afterText;
		
		return translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodesIndex];
		/*
		//记录这个node
		if(translate.node.get(node) == null){
			translate.node.set(node, {});
		}
		
		if(typeof(translate.node.get(node).hitNomenclatureArray) == 'undefined'){
			translate.node.get(node).hitNomenclatureArray = new Map();
		}
		translate.node.get(node).translateTexts[word] = null; //设置要进行通过文本翻译接口翻译的文字
		*/
		
	},

	/*
		将 wholeContext 行内文本组加入 translate.nodeQueue。

		这个方法只创建一种受控的 wholeContext queue item，不在本提交中接入扫描、
		请求、缓存或回填流程。普通 nodeQueue item 的结构保持不变；wholeContext
		item 只通过 type:'wholeContext' 明确标记自己的特殊语义。

		普通 item 的 nodes 表示“多个 DOM 节点共享同一个字符串译文”。
		wholeContext item 的 nodes 表示“同一个上下文翻译组里的多个分段回填目标”，
		因此必须满足：

			translateText[i] 对应 nodes[i].node

		这里不复用 addNodeQueueItem(...)，因为 addNodeQueueItem(...) 会按字符串
		hash 合并相同文本节点；wholeContext 第一版必须让一个 group 对应一个独立
		item，避免两个相同 group 共用 nodes 数组后破坏下标映射。

		hash 使用 wholeContext 独立命名空间，并加入当前 execute uuid 和随机 uuid：
		1. 避免 ["Please read ", "the docs"] 与普通 "Please read the docs" 混用同一个 hash。
		2. 避免两个文本完全相同的 wholeContext group 合并到同一个 item。
		3. 第一版不做 wholeContext 去重和缓存，避免引入 nodeGroups 等复杂结构。
	*/
	addWholeContextToQueue:function(uuid, group){
		if(!translate.whole.context.isUse()){
			return null;
		}
		if(group == null || typeof(group) == 'undefined'
			|| typeof(group.nodes) == 'undefined'
			|| typeof(group.texts) == 'undefined'
			|| typeof(group.nodes.length) == 'undefined'
			|| typeof(group.texts.length) == 'undefined'){
			return null;
		}
		if(group.nodes.length < 2 || group.nodes.length !== group.texts.length){
			return null;
		}

		for(var i = 0; i < group.texts.length; i++){
			if(group.nodes[i] == null || typeof(group.nodes[i]) == 'undefined'){
				return null;
			}
			if(typeof(group.texts[i]) !== 'string' || group.texts[i].trim().length == 0){
				return null;
			}

			/*
				普通 addNodeToQueue(...) 会用 originalText 判断节点是否已经入队。
				wholeContext 后续接入扫描时也必须遵守这个根规则：如果某个 TextNode
				已经被旧流程记录过，就不能再加入 wholeContext，避免同一节点重复进入
				普通 item 和 wholeContext item。
			*/
			var nodeData = translate.node.get(group.nodes[i]);
			if(nodeData != null && typeof(nodeData.originalText) === 'string'){
				return null;
			}
		}

		var contextText = group.texts.join('');
		if(typeof(contextText) !== 'string' || contextText.trim().length == 0){
			return null;
		}

		var textRecognition = translate.language.recognition(contextText);
		var lang = translate.language.recognition_languageName_force(textRecognition);
		if(typeof(lang) !== 'string' || lang.length == 0){
			return null;
		}

		if(typeof(translate.nodeQueue[uuid]) == 'undefined' || translate.nodeQueue[uuid] == null){
			translate.nodeQueue[uuid] = new Array();
			translate.nodeQueue[uuid]['expireTime'] = Date.now() + 120*1000;
			translate.nodeQueue[uuid]['list'] = new Array();
		}
		if(translate.nodeQueue[uuid]['list'][lang] == null || typeof(translate.nodeQueue[uuid]['list'][lang]) == 'undefined'){
			translate.nodeQueue[uuid]['list'][lang] = new Array();
		}

		var hashSeed = 'wholeContext:'+uuid+':'+translate.util.uuid()+':'+JSON.stringify(group.texts);
		var hash = translate.util.hash(hashSeed);
		if(translate.nodeQueue[uuid]['list'][lang][hash] != null && typeof(translate.nodeQueue[uuid]['list'][lang][hash]) != 'undefined'){
			// 理论上随机 uuid 已经足够避免冲突；这里保守跳过，避免覆盖已有队列项。
			return null;
		}

		translate.nodeQueue[uuid]['list'][lang][hash] = new Array();
		translate.nodeQueue[uuid]['list'][lang][hash]['type'] = 'wholeContext';
		translate.nodeQueue[uuid]['list'][lang][hash]['nodes'] = new Array();
		translate.nodeQueue[uuid]['list'][lang][hash]['original'] = contextText;
		translate.nodeQueue[uuid]['list'][lang][hash]['translateText'] = group.texts.slice(0);

		for(var nodeIndex = 0; nodeIndex < group.nodes.length; nodeIndex++){
			var node = group.nodes[nodeIndex];
			var text = group.texts[nodeIndex];

			if(translate.node.get(node) == null){
				translate.node.set(node, {});
			}
			translate.node.get(node).attribute = '';
			translate.node.get(node).originalText = text;
			translate.node.get(node).whole = true;
			if(typeof(translate.node.get(node).translateTexts) === 'undefined'){
				translate.node.get(node).translateTexts = {};
			}
			translate.node.get(node).translateTexts[text] = null;

			translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodeIndex] = new Array();
			translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodeIndex]['node'] = node;
			translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodeIndex]['attribute'] = '';
			translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodeIndex]['beforeText'] = '';
			translate.nodeQueue[uuid]['list'][lang][hash]['nodes'][nodeIndex]['afterText'] = '';
		}

		return translate.nodeQueue[uuid]['list'][lang][hash];
	},

	//全部翻译，node内容全部翻译，而不是进行语种提取，直接对node本身的全部内容拿出来进行直接全部翻译
	whole:{
		isEnableAll:false, //是否开启对整个html页面的整体翻译，也就是整个页面上所有存在的能被翻译的全部会采用整体翻译的方式。默认是 false不开启		

		enableAll:function(){
			translate.whole.isEnableAll = true;
		},

		/*
			一下三个，也就是  class tag id 分别存储加入的值。使用参考：http://translate.zvo.cn/42563.html
		*/
		class:[],
		tag:[],
		id:[],

		/*
			whole 行内上下文分段翻译的根开关。

			默认关闭：
			1. 旧用户即使已经使用 translate.whole，也不会自动进入新的分段上下文请求。
			2. 只有明确调用 translate.whole.context.use() 后，后续 wholeContext 收集才允许生效。
			3. 这里不直接触发扫描、入队或请求，只作为后续能力的兼容性开关。
		*/
		context:{
			is_use:false,

			// 开启 whole 行内上下文分段翻译能力；只设置开关，不立即扫描或发起请求。
			use:function(){
				translate.whole.context.is_use = true;
			},

			// 判断 whole 行内上下文分段翻译能力是否已启用，供后续扫描、入队、请求逻辑统一使用。
			isUse:function(){
				return translate.whole.context.is_use === true;
			},

			/*
				只判断当前元素自身是否命中 translate.whole.tag/class/id。
				不能复用 translate.whole.isWhole()，因为 isWhole() 会向父级追溯；
				wholeContext 的收集根必须只认当前元素，否则子元素会重复触发收集。
				这里不接入 translate.whole.isEnableAll，避免全页面级上下文收集过大。
			*/
			isRootElement:function(ele){
				if(!translate.whole.context.isUse()){
					return false;
				}
				if(ele == null || typeof(ele) == 'undefined' || ele.nodeType !== 1){
					return false;
				}

				var nodename = translate.element.getNodeName(ele).toLowerCase();
				if(nodename.length == 0 || nodename === 'html'){
					return false;
				}

				if(translate.whole.tag.length > 0 && translate.whole.tag.indexOf(nodename) > -1){
					return true;
				}

				if(translate.whole.id.length > 0 && typeof(ele.id) === 'string' && ele.id.length > 0 && translate.whole.id.indexOf(ele.id) > -1){
					return true;
				}

				if(translate.whole.class.length > 0 && typeof(ele.className) === 'string'){
					var className = ele.className.trim();
					if(className.length > 0){
						var classNames = className.split(/\s+/);
						for(var i = 0; i < classNames.length; i++){
							if(translate.whole.class.indexOf(classNames[i]) > -1){
								return true;
							}
						}
					}
				}

				return false;
			},

			/*
				判断元素是否应切断 wholeContext 行内文本流。

				这个方法服务于后续的 wholeContext 收集函数：当一个 translate.whole
				容器中同时存在 TextNode、a、span 等行内节点时，收集函数会尽量把
				连续的行内文本组成一个上下文翻译组；但遇到 br、块级元素、code、
				ignore、translate="no" 等边界时，必须结束当前组，避免把不应该共用
				上下文的文本强行合并。

				示例：
				<p>
					Please <a>read the docs</a><br><code>npm install</code> before use.
				</p>

				后续收集 p 的子节点时：
				1. "Please " 和 a 中的 "read the docs" 可以归入同一个行内上下文组。
				2. br 会让本方法返回 true，从而结束当前组。
				3. code 也会返回 true，代码内容不参与翻译，也不跨 code 合并上下文。
				4. " before use." 会在边界之后作为新的文本流重新开始判断。

				这里故意只做静态、保守的边界判断，不读取 getComputedStyle()。
				原因是 DOM 扫描阶段可能会频繁调用本方法，如果读取运行时样式，
				浏览器可能触发布局计算，增加页面翻译时的性能成本。第一版先用
				明确的标签、属性、class 和 translate.ignore 规则保证行为可控。

				translate.ignore.isIgnore(ele) 会向父级追溯并执行用户自定义 ignore
				函数，成本比普通标签和属性判断更高，所以放在最后执行。
			*/
			isBreakElement:function(ele){
				if(ele == null || typeof(ele) == 'undefined' || ele.nodeType !== 1){
					return true;
				}

				var nodename = translate.element.getNodeName(ele).toLowerCase();
				switch(nodename){
					case 'br':
					case 'hr':
					case 'pre':
					case 'code':
					case 'script':
					case 'style':
					case 'template':
					case 'noscript':
					case 'iframe':
					case 'canvas':
					case 'svg':
					case 'math':
					case 'div':
					case 'p':
					case 'section':
					case 'article':
					case 'header':
					case 'footer':
					case 'main':
					case 'nav':
					case 'aside':
					case 'blockquote':
					case 'h1':
					case 'h2':
					case 'h3':
					case 'h4':
					case 'h5':
					case 'h6':
					case 'table':
					case 'thead':
					case 'tbody':
					case 'tfoot':
					case 'tr':
					case 'td':
					case 'th':
					case 'ul':
					case 'ol':
					case 'li':
					case 'dl':
					case 'dt':
					case 'dd':
					case 'figure':
					case 'figcaption':
					case 'form':
					case 'input':
					case 'textarea':
					case 'select':
					case 'option':
					case 'button':
						return true;
				}

				if(ele.hidden === true || (ele.getAttribute && ele.getAttribute('hidden') !== null)){
					return true;
				}

				if(ele.getAttribute){
					var translateAttr = ele.getAttribute('translate');
					if(typeof(translateAttr) === 'string' && translateAttr.toLowerCase() === 'no'){
						return true;
					}
				}

				if(ele.isContentEditable === true){
					return true;
				}

				if(typeof(ele.className) === 'string'){
					var className = ele.className.trim();
					if(className.length > 0){
						var classNames = className.split(/\s+/);
						for(var i = 0; i < classNames.length; i++){
							if(classNames[i] === 'notranslate'){
								return true;
							}
						}
					}
				}

				if(translate.ignore.isIgnore(ele)){
					return true;
				}

				return false;
			},

			/*
				收集 wholeContext 根元素内连续的行内 TextNode。

				返回值只是一组临时扫描结果：
				[
					{
						nodes: [textNode1, textNode2],
						texts: ["Please read ", "the docs"]
					}
				]

				这里不标记 TextNode，也不加入 nodeQueue；后续接入入队能力时，
				再决定哪些 group 需要真正进入翻译队列。这样可以避免当前提交
				因为半成品逻辑跳过旧翻译流程。

				注意：
				1. 从 root.childNodes 开始遍历，而不是直接 walk(root)。root 本身
				   通常是 p、div 等 whole 容器，如果把 root 交给 isBreakElement(root)，
				   会被块级规则直接切断，导致完全收集不到内容。
				2. 参与收集的 TextNode 必须先通过 nodeAnalyse.gets(node) 和
				   translate.ignore.isIgnore(...) 这两层旧流程判断；wholeContext 只改变
				   上下文组织方式，不新增旧流程不翻译的节点。
				3. 空白、换行、缩进 TextNode 按旧流程视为空文本，不参与 wholeContext。
				   如果多个 segment 之间需要空格辅助翻译，应由后端 segment-aware 接口
				   根据语言和标点在内部处理，前端不在 DOM 结构外主动补空格。
				4. 只有跨多个有效 TextNode 的文本流才返回 group；单个 TextNode 继续走旧流程。
			*/
			collectInlineTextGroups:function(root){
				var groups = [];
				if(!translate.whole.context.isRootElement(root)){
					return groups;
				}

				var currentGroup = {nodes:[], texts:[]};

				var flushGroup = function(){
					if(currentGroup.nodes.length > 1){
						groups.push(currentGroup);
					}
					currentGroup = {nodes:[], texts:[]};
				};

				var appendTextNode = function(node){
					/*
						必须复用旧流程的 nodeAnalyse.gets(node)，不能直接读取 node.nodeValue。
						这样 wholeContext 只改变“多个 TextNode 如何组成上下文”，不改变
						“哪些 TextNode 有资格参与翻译”的根规则。
					*/
					var nodeAnalyChild = translate.element.nodeAnalyse.gets(node);
					for(var nci = 0; nci < nodeAnalyChild.length; nci++){
						if(nodeAnalyChild[nci].attribute !== '' || nodeAnalyChild[nci].node !== node){
							continue;
						}

						if(translate.ignore.isIgnore(node, {node: nodeAnalyChild[nci].node, attribute: nodeAnalyChild[nci].attribute})){
							flushGroup();
							return;
						}

						currentGroup.nodes.push(nodeAnalyChild[nci].node);
						currentGroup.texts.push(nodeAnalyChild[nci].text);
					}
				};

				var walk = function(node){
					if(node == null || typeof(node) == 'undefined'){
						flushGroup();
						return;
					}

					if(node.nodeType === 3){
						appendTextNode(node);
						return;
					}

					if(node.nodeType !== 1){
						flushGroup();
						return;
					}

					if(translate.whole.context.isBreakElement(node)){
						flushGroup();
						return;
					}

					var childNodes = node.childNodes;
					if(childNodes == null || typeof(childNodes) == 'undefined'){
						return;
					}
					for(var i = 0; i < childNodes.length; i++){
						walk(childNodes[i]);
					}
				};

				var childNodes = root.childNodes;
				if(childNodes == null || typeof(childNodes) == 'undefined'){
					return groups;
				}
				for(var i = 0; i < childNodes.length; i++){
					walk(childNodes[i]);
				}
				flushGroup();

				return groups;
			}
		},

		//运行时出现自检并在浏览器控制台提示性文本。 
		//在执行翻译，也就是 execute() 时，会调用此方法。
		executeTip:function(){
			if(translate.whole.class.length == 0 && translate.whole.tag.length == 0 && translate.whole.id.length == 0){
				
			}else{
				translate.log('您开启了 translate.whole 此次行为避开了浏览器端的文本语种自动识别，而是暴力的直接对某个元素的整个文本进行翻译，很可能会产生非常大的翻译量，请谨慎！有关每日翻译字符的说明，可参考： http://translate.zvo.cn/42557.html ');
			}

			if(translate.whole.tag.indexOf('html') > -1){
				translate.log('自检发现您设置了 translate.whole.tag 其中有 html ，这个是不生效的，最大只允许设置到 body ');
			}
		},

		//当前元素是属于全部翻译定义的元素
		/*
			传入一个元素，判断这个元素是否是被包含的。 这个会找父类，看看父类中是否包含在其之中。
			return true是在其中，false不再其中
		*/
		isWhole:function(ele){

			if(translate.whole.isEnableAll){
				return true;
			}

			//如果设置了 class|tag|id 其中某个，或者 all=true ，那么就是启用，反之未启用
			if((translate.whole.class.length == 0 && translate.whole.tag.length == 0 && translate.whole.id.length == 0) && translate.whole.isEnableAll == false){
				//未设置，那么直接返回false
				return false;
			}
			if(ele == null || typeof(ele) == 'undefined'){
				return false;
			}
			

			var parentNode = ele;
			var maxnumber = 100;	//最大循环次数，避免死循环
			while(maxnumber-- > 0){
				if(parentNode == null || typeof(parentNode) == 'undefined'){
					//没有父元素了
					return false;
				}

				//判断Tag
				//var tagName = parentNode.nodeName.toLowerCase(); //tag名字，小写
				var nodename = translate.element.getNodeName(parentNode).toLowerCase(); //tag名字，小写
				if(nodename.length > 0){
					//有nodename
					if(nodename == 'html' || nodename == '#document'){
						//上层元素已经是顶级元素了，那肯定就不是了
						return false;
					}
					if(translate.whole.tag.indexOf(nodename) > -1){
						//发现ignore.tag 当前是处于被忽略的 tag
						return true;
					}
				}
				

				//判断class name
				if(parentNode.className !== null && typeof(parentNode.className) === 'string'){
					var classNames = parentNode.className;
					if(classNames == null || typeof(classNames) != 'string'){
						continue;
					}
					//console.log('className:'+typeof(classNames));
					//console.log(classNames);
					classNames = classNames.trim().split(' ');
					for(var c_index = 0; c_index < classNames.length; c_index++){
						if(classNames[c_index] != null && classNames[c_index].trim().length > 0){
							//有效的class name，进行判断
							if(translate.whole.class.indexOf(classNames[c_index]) > -1){
								//发现ignore.class 当前是处于被忽略的 class
								return true;
							}
						}
					}					
				}

				//判断id
				if(parentNode.id != null && typeof(parentNode.id) != 'undefined'){
					//有效的class name，进行判断
					if(translate.whole.id.indexOf(parentNode.id) > -1){
						//发现ignore.id 当前是处于被忽略的 id
						return true;
					}
				}

				//赋予判断的元素向上一级
				parentNode = parentNode.parentElement;
			}

			return false;
		}
	},

	language:{

		

		/*	
			英语的变种语种，也就是在英语26个字母的基础上加了点别的特殊字母另成的一种语言，而这些语言是没法直接通过识别字符来判断出是哪种语种的
			
			法语、意大利语、德语、葡萄牙语

			要废弃，用下面的 systems 、 name
		*/
		englishVarietys : ['french','italian','deutsch', 'portuguese'],

		/*
			语言的书写体系，分成哪几个语言体系。
				这里区分，主要是单纯从文字组成长进行区分的。

			其中
			key : 语言体系的名字
			value: 语言体系的详细信息
				direction: 书写方向 （当前只是记录，无其他意义）
				remark: 说明备注 （当前只是记录，无其他意义）
				languages: 这个语言体系下，有哪些具体语种， translate.js 的语言标识

		 */ 
		systems : {
			
			// 拉丁字母体系
			latin:{
				direction: "left-to-right", // 书写方向
				coreFeatures: "基础字母26个（A-Z），部分语言添加变音符号（如é、ñ、ü），从左到右书写",
				languages: [ //包含的语种
			      "english", "latin", "french", "spanish", "deutsch", "portuguese",
			      "italian", "越南语", "马来语", "印尼语",
			      "土耳其语", "波兰语", "荷兰语", "瑞典语", "非洲诸语（多数）",
			      "美洲诸语（多数）", "菲律宾语", "哈萨克语（现代拉丁化）"
			    ]
			},
			
			// 汉字体系（表意文字）
			chinese:{
				direction: "left-to-right",
				coreFeatures: "表意文字，单字独立，可组合成词，笔画复杂，现代多横向书写",
				languages: [ 
			      "chinese_simplified", "chinese_traditional", "japanese", "korean"
			    ]
			},

			//阿拉伯字母体系
			arabic:{
				direction: "right-to-left",
				coreFeatures: "表意文字，单字独立，可组合成词，笔画复杂，现代多横向书写",
				languages: [ 
			      "阿拉伯语", "波斯语", "乌尔都语", "旁遮普语（巴基斯坦）","豪萨语（西非）", "普什图语",
			    ]
			},

			//西里尔字母体系
			cyrillic:{
				direction: "left-to-right",
				coreFeatures: "源于希腊字母，字母形态独特（如п、в、м），部分字母与拉丁字母形似但发音不同",
				languages: [ 
			      "俄语", "乌克兰语", "白俄罗斯语", "保加利亚语", "塞尔维亚语（官方）"
			    ]
			},

			//泰语体系
			thai:{
				direction: "left-to-right",
				coreFeatures: "元音附标文字，字母弯曲优美，含音调符号（影响词义）",
				languages: [ 
			      "thai"
			    ]
			},

			//其他后续补充吧
			
		},

		/*
			生成 translate.language.english 这种语种对象，通过 translate.language.systems
		*/
		generateLanguageNameObject:function(){
			var languages = new Map();
			for(var key in translate.language.systems){
				if (!translate.language.systems.hasOwnProperty(key)) {
		    		continue;
		    	}
				for(var li = 0; li < translate.language.systems[key].languages.length; li++){
					//console.log(translate.language.systems[key].languages[li])
					languages.set(translate.language.systems[key].languages[li], {
						system: key
					});
				}
			}
			return languages;
		},

		/*
			语言表示：属性相关，他会在translate.js 加载完后自动初始化，从 translate.language.systems 中遍历出来，赋予 translate.language.name
			它里面的值为： 
				translate.language.map.get('english') = {
					system:'latin'	//所属系统语族 , 也就是 translate.language.systems[key] 的 key
				}
				
			它会在 translate.execute() 是进行初始化，通过触发 translate.language.generateLanguageNameObject 赋予值
		*/
		map_data: null, //这是一个map 
		map: function(){
			if(translate.language.map_data == null){
				translate.language.map_data = translate.language.generateLanguageNameObject();
			}
			return translate.language.map_data;
		},
		

		//当前本地语种，本地语言，默认是简体中文。设置请使用 translate.language.setLocal(...)。不可直接使用，使用需用 getLocal()
		local:'',

		/*
		 * v3.12增加, 是否会翻译本地语种，默认是false，不会翻译。
		 * 比如当前设置的本地语种是简体中文， 但是网页中也有一段英文， 如果设置了translate.to 为中文，也就是要以中文显示 默认是false的情况下，整个页面是不会被任何翻译的，也就是有的那段英文也不会进行任何翻译，依旧是显示英文。
		 * 如果这里设置为 true， 则英文也会被翻译，只要不是中文的，都会被翻译为要显示的语种，也就是都会被翻译为中文。
		 */
		translateLocal:false,

		/*
			翻译语种范围
			比如传入 ['chinese_simplified','chinese_traditional','english'] 则表示仅对网页中的简体中文、繁体中文、英文 进行翻译，而网页中出现的其他的像是法语、韩语则不会进行翻译
			如果为空 []，则是翻译时，翻译网页中的所有语种			
			设置方式为：  translate.language.translateLanguagesRange = ['chinese_simplified','chinese_traditional']
		*/
		translateLanguagesRange: [], 
		//传入语种。具体可传入哪些参考： http://api.translate.zvo.cn/doc/language.json.html
		setLocal:function(languageName){
			//translate.setUseVersion2(); //Set to use v2.x version
			translate.useVersion = 'v2';
			translate.language.local = languageName;
		},
		//获取当前本地语种，本地语言，默认是简体中文。设置请使用 translate.language.setLocal(...)
		getLocal:function(){
			//判断是否设置了本地语种，如果没设置，自动给其设置
			if(translate.language.local == null || translate.language.local.length < 1){
				translate.language.autoRecognitionLocalLanguage();
			}
			return translate.language.local;
		},
		/*
			获取当前语种。
			比如当前设置的本地语种是简体中文，用户并未切换其他语种，那么这个方法将返回本地当前的语种，也就是等同于 translate.language.getLocal()
			如果用户切换为英语进行浏览，那么这个方法将返回翻译的目标语种，也就是 english
		*/
		getCurrent:function(){
			var to_storage = translate.storage.get('to');
			if(to_storage != null && typeof(to_storage) != 'undefined' && to_storage.length > 0){
				//之前有过使用，并且主动设置过目标语种
				return to_storage;
			}
			return translate.language.getLocal();
		},


		//如果第一次用，默认以什么语种显示。
		//比如本地当前语种是简体中文，这里设置为english，那么用户第一次使用时，会自动翻译为english进行显示。如果用户手动切换为其他语种比如韩语，那么就遵循用户手动切换的为主，显示韩语。
		defaultTo:'',
		setDefaultTo:function(languageName){
			if(typeof(languageName) === 'string' && languageName.trim().length > 0){
				translate.language.defaultTo = languageName;
			}
			var to_storage = translate.storage.get('to');
			if(to_storage != null && typeof(to_storage) != 'undefined' && to_storage.length > 0){
				//之前有过使用，并且主动设置过目标语种，那么不进行处理
			}else{
				//没有设置过，进行处理
				translate.storage.set('to', languageName);
				translate.to = languageName;
			}
		},
		/*
			清除历史翻译语种的缓存
		*/
		clearCacheLanguage:function(){
			if(typeof(translate.language.setUrlParamControl_use) != 'undefined'){
				if(translate.language.setUrlParamControl_use){
					translate.log('使用提示：')
					translate.log('translate.language.setUrlParamControl(...) 的作用是 可以通过URL传一个语种，来指定当前页面以什么语种显示。 参考文档： http://translate.zvo.cn/4075.html');
					translate.log('translate.language.clearCacheLanguage() 是清除历史翻译语种缓存，也就是清除之前指定翻译为什么语种。 参考文档：http://translate.zvo.cn/4080.html')
					translate.log('如果你执行了 translate.language.setUrlParamControl(...) 那么是要根据url传参来切换语种的，但是后面又出现了 translate.language.clearCacheLanguage() 它会阻止 translate.language.setUrlParamControl(...) 它的设置，即使有url传递翻译为什么语言，也会因为 translate.language.clearCacheLanguage() 给清除掉，使URL传参的语种不起任何作用。')
				}
			}
			translate.to = '';
			translate.storage.set('to','');
		},
		//标记已执行了 translate.language.setUrlParamControl  如果已经执行启用，则是true，默认是不启用是false
		setUrlParamControl_use: false,
		// translate.language.setUrlParamControl('language') 这里传入的 language 参数，默认不设置则是 language ，比如传入 lang ，那这个 setUrlParamControl_name 值便是 lang
		setUrlParamControl_name: 'language',
		//根据URL传参控制以何种语种显示
		//设置可以根据当前访问url的某个get参数来控制使用哪种语言显示。
		//比如当前语种是简体中文，网页url是http://translate.zvo.cn/index.html ,那么可以通过在url后面增加 language 参数指定翻译语种，来使网页内容以英文形态显示 http://translate.zvo.cn/index.html?language=english
		setUrlParamControl:function(paramName){
			translate.language.setUrlParamControl_use = true; //标记已执行了 translate.language.setUrlParamControl  ,仅仅只是标记，无其他作用
			if(typeof(paramName) == 'undefined' || paramName.length < 1){
				paramName = 'language';
			}
			translate.language.setUrlParamControl_name = paramName;
			var paramValue = translate.util.getUrlParam(paramName);
			if(typeof(paramValue) == 'undefined'){
				return;
			}
			if(paramValue == '' || paramValue == 'null' || paramValue == 'undefined'){
				return;
			}

			translate.storage.set('to', paramValue);
			translate.to = paramValue;
		},
		/* 
			获取翻译区域的原始文本，翻译前的文本。 这里会把空白符等过滤掉，只返回纯显示的文本
			也就是获取 translate.setDocument(...) 定义的翻译区域中，翻译前，要参与翻译的文本。 
			其中像是 translate.ignore.tag 这种忽略翻译的标签，这里也不会获取的，这里只是获取实际要参与翻译的文本。

			返回值： 字符串。 如果获取不到，则返回空字符串 ''
		 */
		getTranslateAreaText:function(){
			//v3.16.1 优化，获取本地语种，针对开源中国只对 readme 部分进行翻译的场景，将针对设置的 translate.setDocument() 区域的元素的显示文本进行判定语种
			var translateAreaText = ''; //翻译区域内当前的文本
			
			/** 构建虚拟容器，将要翻译的区域放入虚拟容器，以便后续处理 **/
			var virtualContainer = document.createElement('div'); // 创建虚拟容器，处理、判断也都是针对这个虚拟容器
			if(translate.documents != null && typeof(translate.documents) != 'undefined' && translate.documents.length > 0){
				// setDocuments 指定的
				for(var docs_index = 0; docs_index < translate.documents.length; docs_index++){
					var doc = translate.documents[docs_index];
					if(typeof(doc) != 'undefined' && doc != null && typeof(doc.innerText) != 'undefined' && doc.innerText != null && doc.innerText.length > 0){
						virtualContainer.appendChild(doc.cloneNode(true));
					}
				}
			}else{
				//未使用 setDocuments指定，那就是整个网页了
				//return document.all; //翻译所有的  这是 v3.5.0之前的
				//v3.5.0 之后采用 拿 html的最上层的demo，而不是 document.all 拿到可能几千个dom
				if(typeof(document.head) != 'undefined'){
					virtualContainer.appendChild(document.head.cloneNode(true));
				}
				if(typeof(document.body) != 'undefined'){
					virtualContainer.appendChild(document.body.cloneNode(true));
				}
			}
			//console.log(virtualContainer);


			/** 对虚拟容器中的元素进行处理，移除忽略的 tag （这里暂时就只是移除忽略的tag， 其他忽略的后续再加） **/
			// 遍历标签列表
			//console.log('---- remove element');
		    for (var i = 0; i < translate.ignore.tag.length; i++) {
		        var tagName = translate.ignore.tag[i];
		        var elements = virtualContainer.querySelectorAll(tagName);
		        // 将 NodeList 转换为数组
		        var elementArray = Array.prototype.slice.call(elements);
		        // 遍历并移除每个匹配的元素
		        for (var j = 0; j < elementArray.length; j++) {
		            var element = elementArray[j];
		            if (element.parentNode) {
		                //console.log(element);
		                element.parentNode.removeChild(element);
		            }
		        }
		    }
			//console.log('---- remove element end');


			/*** 取过滤完后的文本字符 ***/
			translateAreaText = virtualContainer.innerText;
			if(translateAreaText == null || typeof(translateAreaText) == 'undefined' || translateAreaText.length < 1){
				//未取到，默认赋予简体中文
				translate.language.local = 'chinese_simplified';
				return '';
			}
			// 移除所有空白字符（包括空格、制表符、换行符等）
			translateAreaText = translateAreaText.replace(/\s/g, '');

			//console.log('translateAreaText:\n'+translateAreaText);
			return translateAreaText;
		},
		//自动识别当前页面是什么语种
		autoRecognitionLocalLanguage:function(){
			if(translate.language.local != null && translate.language.local.length > 2){
				//已设置过了，不需要再设置
				return translate.language.local;
			}

			var translateAreaText = translate.language.getTranslateAreaText();

			//默认赋予简体中文
			translate.language.local = 'chinese_simplified';
			var recognition = translate.language.recognition(translateAreaText);
			//console.log(recognition);
			translate.language.local = recognition.languageName;
			return translate.language.local;
			/* v3.1优化
			var langs = new Array(); //上一个字符的语种是什么，当前字符向上数第一个字符。格式如 ['language']='english', ['chatstr']='a', ['storage_language']='english'  这里面有3个参数，分别代表这个字符属于那个语种，其字符是什么、存入了哪种语种的队列。因为像是逗号，句号，一般是存入本身语种中，而不是存入特殊符号中。 
			for(var i=0; i<bodyText.length; i++){
				var charstr = bodyText.charAt(i);
				var lang = translate.language.getCharLanguage(charstr);
				if(lang == ''){
					//未获取到，未发现是什么语言
					//continue;
					lang = 'unidentification';
				}
				langs.push(lang);
			}

			//从数组中取出现频率最高的
			var newLangs = translate.util.arrayFindMaxNumber(langs);

			//移除数组中的特殊字符
			var index = newLangs.indexOf('specialCharacter');
			if(index > -1){
				newLangs.splice(index,1); //移除数组中的特殊字符
			}

			if(newLangs.length > 0){
				//找到排序出现频率最多的
				translate.language.local = newLangs[0];
			}else{
				//没有，默认赋予简体中文
				translate.language.local = 'chinese_simplified';
			}
			*/
		},
		
		/*
		 * 获取当前字符是什么语种。返回值是一个语言标识，有  chinese_simplified简体中文、japanese日语、korean韩语、
		 * str : node.nodeValue 或 图片的 node.alt 等
		 * 如果语句长，会全句翻译，以保证翻译的准确性，提高可读性。
		 * 如果语句短，会自动将特殊字符、要翻译的目标语种给过滤掉，只取出具体的要翻译的目标语种文本
		 *
		 * 返回 存放不同语言的数组，格式如
		 *  	[
					"english":[
						{beforeText: '', afterText: '', text: 'emoambue hag'},
						......
					],
					"japanese":[
						{beforeText: ' ', afterText: ' ', text: 'ẽ '},
						......
					]
		 		]
		 * 		
		 */
		get:function(str){
			//将str拆分为单个char进行判断

			var langs = new Array(); //当前字符串包含哪些语言的数组，其内如 english
			var langStrs = new Array();	//存放不同语言的文本，格式如 ['english'][0] = 'hello'
			var upLangs = []; //上一个字符的语种是什么，当前字符向上数第一个字符。格式如 ['language']='english', ['chatstr']='a', ['storage_language']='english'  这里面有3个参数，分别代表这个字符属于那个语种，其字符是什么、存入了哪种语种的队列。因为像是逗号，句号，一般是存入本身语种中，而不是存入特殊符号中。 
			var upLangsTwo = []; //上二个字符的语种是什么 ，当前字符向上数第二个字符。 格式如 ['language']='english', ['chatstr']='a', ['storage_language']='english'  这里面有3个参数，分别代表这个字符属于那个语种，其字符是什么、存入了哪种语种的队列。因为像是逗号，句号，一般是存入本身语种中，而不是存入特殊符号中。
			
			//var upLangs = ''; //上一个字符的语种是什么，格式如 english
			for(var i=0; i<str.length; i++){
				var charstr = str.charAt(i);
				//console.log('charstr:'+charstr)
				var lang = translate.language.getCharLanguage(charstr);
				if(lang == ''){
					//未获取到，未发现是什么语言
					//continue;
					lang = 'unidentification';
				}
				var result = translate.language.analyse(lang, langStrs, upLangs, upLangsTwo, charstr);
				//console.log(result)
				langStrs = result['langStrs'];
				//记录上几个字符
				if(typeof(upLangs['language']) != 'undefined'){
					upLangsTwo['language'] = upLangs['language'];
					upLangsTwo['charstr'] = upLangs['charstr'];
					upLangsTwo['storage_language'] = upLangs['storage_language'];
				}
				//upLangs['language'] = lang;
				upLangs['language'] = result['storage_language'];
				upLangs['charstr'] = charstr;
				upLangs['storage_language'] = result['storage_language'];
				//console.log(result['storage_language'])
				//console.log(upLangs['language']);
				langs.push(lang);
			}
			
			//console.log(langStrs);
			
			//console.log(langs);
			//console.log(langStrs);

/*
			//从数组中取出现频率最高的
			var newLangs = translate.util.arrayFindMaxNumber(langs);
			
			//移除当前翻译目标的语言。因为已经是目标预言了，不需要翻译了
			var index = newLangs.indexOf(translate.to);
			if(index > -1){
				newLangs.splice(index,1); //移除
			}

			//移除特殊字符
			var index = newLangs.indexOf('specialCharacter');
			if(index > -1){
				newLangs.splice(index,1); //移除数组中的特殊字符
			}

			if(newLangs.length > 0){
				//还剩一个或多个，（如果是多个，那应该是这几个出现的频率一样，所以取频率最高的时返回了多个）
				return newLangs[0];
			}else{
				//没找到，直接返回空字符串
				return '';
			}
			*/
			
			
			//去除特殊符号
			//for(var i = 0; i<langStrs.length; i++){
			/*
			var i = 0;
			for(var item in langStrs) {
				if(item == 'unidentification' || item == 'specialCharacter'){
					//langStrs.splice(i,1); //移除
					delete langStrs[item];
				}
				console.log(item);
				i++;
			}
			*/
			
			//console.log(langStrs);
			if(typeof(langStrs['unidentification']) != 'undefined'){
				delete langStrs['unidentification'];
			}
			if(typeof(langStrs['specialCharacter']) != 'undefined'){
				delete langStrs['specialCharacter'];
			}
			if(typeof(langStrs['number']) != 'undefined'){
				delete langStrs['number'];
			}
			
			
			//console.log('get end');
			return langStrs;
		},
		/*	
			语种识别策略

			str 要识别的字符串 
			data 对于str字符串识别的结果，格式如：
				{
					languageName: 'english',
			 		languageArray:[
						english:[
							list[
								{beforeText: ' ', afterText: ' ', text: 'hello word'},
								{beforeText: ' ', afterText: ' ', text: 'who?'},
							],
							number:12
						],
						japanese:[
							......
						]
			 		]
			 	}
			 	有关这里面具体参数的说明，参考 translate.language.recognition 的说明
			languagesSize key:语言名， value:语言字符数
			allSize 当前所有发现的语种，加起来的总字符数，也就是 languagesSize 遍历所有的value相加的数

			最后，要 return data;
		*/
		recognitionAlgorithm:function(str, data, languagesSize, allSize){
			
			/*
				如果英语跟罗曼语族(法语意大利语等多个语言)一起出现，且当前 data.languageName 认定是英语（也就是英文字符占比最大），那么要判定一下：
					如果 罗曼语族的字符数/英文的字符数 > 0.008 ， 那么认为当前是罗曼语族的中的某个语种， 在对其判定出具体是罗曼语族中的哪个语种赋予最终结果。
			*/
			if(typeof(languagesSize['english']) != 'undefined' && typeof(languagesSize['romance']) != 'undefined' && data.languageName == 'english'){
				if(languagesSize['romance']/languagesSize['english'] > 0.008){
					//排定是罗曼语族了，那么判断一下到底是 法语、西班牙语、葡萄牙语、意大利语 中的哪一种呢

					//先判定是否有设置本地语种是罗曼语族中其中的某一个
					if(typeof(translate.language.local) != 'undefined' && translate.language.local.length > 1){
						if(translate.language.englishVarietys.indexOf(translate.language.local) > -1){
							//发现当前设置的是小语种，那么将当前识别的语种识别为 本地设置的这个小语种。
							data.languageName = translate.language.local;
						}
					}

					if(data.languageName == 'english'){
						//还是英语，那就是没有经过上面本地语种的判定，那进行罗曼语的具体语种识别

						var romanceSentenceLanguage = translate.language.romanceSentenceAnaly(str);
						if(romanceSentenceLanguage.length == 0){
							translate.log('语种识别异常，应该是 法语、西班牙语、葡萄牙语、意大利语 中的一种才是，除非是除了这四种语种之外的别的 罗曼语族 中的语种，当前已将 '+ str +'识别为英语。 你可以联系我们求助 https://translate.zvo.cn/4030.html');
						}else{
							data.languageName = romanceSentenceLanguage;
						}
					}
				}
			}

			
			/*
				日语判定
				如果发现日语存在，且当前 data.languageName 认定不是日语，那么要判定一下：
					如果 日语的字符数/所有字符数 的字符数 > 0.08 ， 那么认为当前是日语的
			*/
			if( typeof(languagesSize['japanese']) != 'undefined' && data.languageName != 'japanese'){
				if(languagesSize['japanese']/allSize > 0.08){
					data.languageName = 'japanese'
				}
			}

			/*
				如果发现英语、简体中文或繁体中文 一起存在，且当前 data.languageName 认定是英语时，那么要判定一下：
					如果 (简体中文+繁体中文)的字符数/英语 > 0.05 ， 那么认为当前是简体中文（不认为是繁体中文，因为下面还有 简体中文跟繁体中文的判定）
			*/
			if( (typeof(languagesSize['chinese_simplified']) != 'undefined' || typeof(languagesSize['chinese_traditional']) != 'undefined' ) && typeof(languagesSize['english']) != 'undefined' && data.languageName == 'english'){
				var size = 0;
				if(typeof(languagesSize['chinese_simplified']) != 'undefined'){
					size = size + languagesSize['chinese_simplified'];
				}
				if(typeof(languagesSize['chinese_traditional']) != 'undefined'){
					size = size + languagesSize['chinese_traditional'];
				}
				if(size/languagesSize['english'] > 0.05){
					data.languageName = 'chinese_simplified'
				}
			}


			/*
				如果简体中文跟繁体中文一起出现，且当前 data.languageName 认定是简体中文（也就是简体中文字符占比最大），那么要判定一下繁体中文：
					如果 繁体中文的字符数/简体中文的字符数 > 0.08 ， 那么认为当前是繁体中文的
			*/
			if(typeof(languagesSize['chinese_simplified']) != 'undefined' && typeof(languagesSize['chinese_traditional']) != 'undefined' && data.languageName == 'chinese_simplified'){
				if(languagesSize['chinese_traditional']/languagesSize['chinese_simplified'] > 0.03){
					data.languageName = 'chinese_traditional'
				}
			}
			/* if(langkeys.indexOf('chinese_simplified') > -1 && langkeys.indexOf('chinese_traditional') > -1){
				langsNumber['chinese_simplified'] = 0;
			} */



			return data;
		},
		/*
			强制识别，这里目前咱是配合  translate.language.translateLocal 使用，当它为true时才会进行强制识别，不管比例，只要出现字符，就强制识别。 
			这个也是只能有大模型翻译、自动识别语种的翻译才使用它。

			比如： translate.language.recognition('你 @¿Lo que introduzco ahora es contenido español, ¿ se puede traducir en chino? Este texto está en inglés. Si desea ')
			会被识别为西班牙语，因为按照上面的逻辑，简体中文占得比重太小了，而西班牙语占比重大，所以是西班牙语。
			但是如果当前要翻译为的语种是西班牙语，那么它根据比例识别出也是西班牙语，这句话是不会被翻译的，但是用户实际看上去，却是有显示 '你' 这个中文文字，是不合适的，所以不管是出现了多小的比重，都不能在含有 中文、日语的字符，不然不符合用户以西班牙语阅读的习惯，即使显示单个中文单词，那也属于刺眼的
		
			返回：
			当前 recognition 结果识别的语种，比如 english ,translate.js 的语言标识
		*/
		recognition_languageName_force:function(recognition_result){

			//未启用，那用 recognition 的结果
			if(!translate.language.translateLocal){
				return recognition_result.languageName;
			}
			if(translate.language.getLocal() == translate.language.getCurrent() && translate.language.translateLocal === false){
				//如果本地语种跟当前语种一致,且不进行强制翻译，那么肯定就不进行翻译的,直接原样返回
				return recognition_result.languageName;
			}else{
				//其他的情况就是要翻译了

				//当前语种
				var currentLanguage = translate.language.getCurrent(); 
				
				if(typeof(translate.language.map().get(currentLanguage)) != 'undefined' && typeof(translate.language.map().get(translate.language.getLocal())) != 'undefined'){
					//本地语种跟当前语种都是有语族的
					
					/*
						语族 ，当前文本中的文字包含多少语种
						key 是语族的名字，如 latin （如果 translate.language.map().get 中没有取到语族，那么这里就直接去掉）
						value 是具体的语种名字，如 english 。 这里比如字符串中有英语也有西班牙语，那这里只会记录其中一个，因为主要记录的是key语族的名字
					*/
					var languageSystem = {}; 	
					//遍历当前有的语种
					for (var language in recognition_result.languageArray) {
						// 必须加 hasOwnProperty 检查，避免遍历原型链上的属性
						if (!recognition_result.languageArray.hasOwnProperty(language)) {
					  		continue;
						}

						if(typeof(translate.language.map().get(language)) != 'undefined'){
					  		languageSystem[translate.language.map().get(language).system] = language;
						}
					}

					if(translate.language.map().get(currentLanguage).system == 'latin'){
						//要以拉丁语族显示，那如果其中字符有 chinese 语族的，那么要把这个语族的全部翻译
						
						delete languageSystem['latin'];
						var yuzuArray = Object.keys(languageSystem);
						if(yuzuArray.length > 0){
							//发现还有其他语族的，那么以其他语族为主，目的是能一起翻译，那么直接返回第一个语族名即可
							return languageSystem[yuzuArray[0]];
						}
					}
				}
			}

			//其他那就都是用 recognition 的结果
			return recognition_result.languageName;
		},

		/*
		 * 识别字符串是什么语种。它是 get() 的扩展，以代替get返回更多
		 * str : 要识别的字符串
		 *
		 * 返回 存放不同语言的数组，格式如
		 *  	
			{
				languageName: 'english',
		 		languageArray:[
					english:[
						list[
							{beforeText: ' ', afterText: ' ', text: 'hello word'},
							{beforeText: ' ', afterText: ' ', text: 'who?'},
						],
						number:12
					],
					japanese:[
						......
					]
		 		]
		 	}
		 	languageName 是当前字符串最终判定结果是什么语种。它的识别有以下特点：
		 		1. 如果出现英语跟中文、罗曼语族、德语等混合的情况，也就是不纯粹英语的情况，那么会以其他语种为准，而不是识别为英语。不论英语字符出现的比例占多少。
		 		2. 如果出现简体中文跟繁体中文混合的情况，那么识别为繁体中文。不论简体中文字符出现的比例占多少。
		 		3. 如果出现简体中文、繁体中文、日语混合的情况，那么识别为日语。不论简体中文、繁体中文出现的比例占多少。 2025.4.19 增加
				4. 除了以上两种规则外，如果出现了多个语种，那么会识别为出现字符数量最多的语种当做当前句子的语种。（注意是字符数，而不是语种的数组数）
			languageArray 对传入字符串进行分析，识别出都有哪些语种，每个语种的字符是什么
		 * 		
		 */
		recognition:function(str){
			var langs = translate.language.get(str);
			//var langkeys = Object.keys(langs);
			//console.log(langkeys);
			var langsNumber = []; //key  语言名，  value 语言字符数
			var langsNumberOriginal = []; //同上，只不过这个不会进行清空字符数
			var allNumber = 0;//总字数

			/** 进行字数统计相关 - start **/
			for(var key in langs){
				if (!langs.hasOwnProperty(key)) {
		    		continue;
		    	}
				if(typeof(langs[key]) != 'object'){
					continue;
				}
				var langStrLength = 0;
				for(var ls = 0; ls < langs[key].length; ls++){
					langStrLength = langStrLength + langs[key][ls].text.length;
				}
				allNumber = allNumber + langStrLength;
				langsNumber[key] = langStrLength;
				langsNumberOriginal[key] = langStrLength;
			}
			/** 进行字数统计相关 - end **/



			//从 langsNumber 中找出字数最多的来
			var maxLang = ''; //字数最多的语种
			var maxNumber = 0;
			for(var lang in langsNumber){
				if (!langsNumber.hasOwnProperty(lang)) {
		    		continue;
		    	}
				if(langsNumber[lang] > maxNumber){
					maxLang = lang;
					maxNumber = langsNumber[lang];
				}
			}

			//重新组合返回值的 languageArray
			var languageArray = {};
			for(var lang in langs){
				if (!langs.hasOwnProperty(lang)) {
		    		continue;
		    	}
				languageArray[lang] = {};
				languageArray[lang].number = langsNumberOriginal[lang];
				languageArray[lang].list = langs[lang];
			}

			var result = {
				languageName: maxLang,
				languageArray: languageArray
			};
			
			//最后进行一层简单的算法处理
			return translate.language.recognitionAlgorithm(str, result, langsNumber, allNumber);
		},
		/*
			传入一个char，返回这个char属于什么语种，返回如   如果返回空字符串，那么表示未获取到是什么语种
			chinese_simplified 简体中文
			chinese_traditional 繁体中文
			russian 俄罗斯语
			english 英语
			romance 罗曼语族，它是 法语、西班牙语、意大利语、葡萄牙語 的集合，并不是单个语言
			specialCharacter 特殊字符，符号
			number 阿拉伯数字
			japanese 日语
			korean 韩语
			greek 希腊语
			thai 泰语
			arabic 阿拉伯语
			romanian 罗马尼亚语
			hebrew 希伯来语

		*/
		getCharLanguage:function(charstr){
			if(charstr == null || typeof(charstr) == 'undefined'){
				return '';
			}
			
			if(this.russian(charstr)){
				return 'russian';
			}
			if(this.english(charstr)){
				return 'english';
			}
			if(this.romance(charstr)){
				return 'romance';
			}
			if(this.specialCharacter(charstr)){
				return 'specialCharacter';
			}
			if(this.number(charstr)){
				return 'number';	
			}

			//中文的判断包含两种，简体跟繁体
			var chinesetype = this.chinese(charstr);
			if(chinesetype == 'simplified'){
				return 'chinese_simplified';
			}else if(chinesetype == 'traditional'){
				return 'chinese_traditional';
			}

			if(this.japanese(charstr)){
				return 'japanese';
			}
			if(this.korean(charstr)){
				return 'korean';
			}
			if(this.greek(charstr)){
				return 'greek';
			}
			if(this.thai(charstr)){
				return 'thai';
			}
			if(this.arabic(charstr)){
				return 'arabic';
			}
			if(this.romanian(charstr)){
				return 'romanian';
			}
			if(this.hebrew(charstr)){
				return 'hebrew';
			}
			//未识别是什么语种
			//console.log('not find is language , char : '+charstr+', unicode: '+charstr.charCodeAt(0).toString(16));
			return '';
			
		},
		/*
		 * 对字符串进行分析，分析字符串是有哪几种语言组成。
		 * language : 当前字符的语种，传入如 english
		 * langStrs : 操作的，如 langStrs['english'][0] = '你好'
		 * upLangs  : 当前字符之前的上一个字符的语种是什么，当前字符向上数第一个字符。格式如 ['language']='english', ['chatstr']='a', ['storage_language']='english'  这里面有3个参数，分别代表这个字符属于那个语种，其字符是什么、存入了哪种语种的队列。因为像是逗号，句号，一般是存入本身语种中，而不是存入特殊符号中。
		 * upLangsTwo : 当前字符之前的上二个字符的语种是什么 ，当前字符向上数第二个字符。 格式如 ['language']='english', ['chatstr']='a', ['storage_language']='english'  这里面有3个参数，分别代表这个字符属于那个语种，其字符是什么、存入了哪种语种的队列。因为像是逗号，句号，一般是存入本身语种中，而不是存入特殊符号中。
		 * chatstr  : 当前字符，如  h
		 */
		analyse:function(language, langStrs, upLangs, upLangsTwo, charstr){
			if(typeof(langStrs[language]) == 'undefined'){
				langStrs[language] = new Array();
			}
			var index = 0; //当前要存入的数组下标
			if(typeof(upLangs['storage_language']) == 'undefined'){
				//第一次，那么还没存入值，index肯定为0
				//console.log('第一次，那么还没存入值，index肯定为0')
				//console.log(upLangs['language'])
			}else{
				//console.log('analyse, charstr : '+charstr+', upLangs :');
				//console.log(upLangs);
				//var isEqual = upLangs['storage_language'] == language; //上次跟当前字符是否都是同一个语种（这个字符跟这个字符前一个字符）

				/*
					英语每个单词之间都会有空格分割. 如果是英文的话，英文跟特殊字符还要单独判断一下，避免拆开，造成翻译不准，单个单词翻译的情况
					所以如果上次的字符是英文或特殊符号，当前字符是特殊符号(逗号、句号、空格，然后直接笼统就吧特殊符号都算上吧)，那么也将当次的特殊符号变为英文来进行适配
					示例  
						hello word  的 "o w"
						hello  word  的 "  w"
						hello  word  的 "w  "
						this is a dog  的 " a "
				*/
				//console.log(language == 'specialCharacter');
				//如果两个字符类型不一致，但当前字符是英文或连接符时，进行判断
				/*
				if(!isEqual){
					if(language == 'english' || translate.language.connector(charstr)){
						console.log('1.'+(language == 'english' || translate.language.connector(charstr))+', upLangs str:'+upLangs['charstr']);
						//上一个字符是英文或连接符
						//console.log('teshu:'+translate.language.connector(upLangs['charstr'])+', str:'+upLangs['charstr']);
						if(upLangs['language'] == 'english' || translate.language.connector(upLangs['charstr'])) {
							console.log('2');
							//如果上二个字符不存在，那么刚开始，不再上面几种情况之中，直接不用考虑
							if(typeof(upLangsTwo['language']) != 'undefined'){
								console.log('3')
								//上二个字符是空（字符串刚开始），或者是英文
								if(upLangsTwo['language'] == 'english' || translate.language.connector(upLangsTwo['charstr'])){
									//满足这三个条件，那就将这三个拼接到一起
									console.log('4/5: '+', two lang:'+upLangsTwo['language']+', str:'+upLangsTwo['charstr'])
									isEqual = true;
									if(language == 'specialCharacter' && upLangs['language'] == 'specialCharacter' && upLangsTwo['language'] == 'specialCharacter'){
										//如果三个都是特殊字符，或后两个是特殊字符，第一个是空（刚开始），那就归入特殊字符
										language = 'specialCharacter';
										//console.log('4')
									}else{
										//不然就都归于英文中。
										//这里更改是为了让下面能将特殊字符（像是空格逗号等）也一起存入数组
										language = 'english';
										console.log(5)
									}
								}
							}
						}
					}
				}
				*/

				/*
					不判断当前字符，而判断上个字符，是因为当前字符没法获取未知的下个字符。
				*/
				//if(!isEqual){

					//如果当前字符是连接符
					if(translate.language.connector(charstr)){
						language = upLangs['storage_language'];
						/*
						//判断上个字符是否存入了待翻译字符，如要将中文翻译为英文，而上个字符是中文，待翻译，那将连接符一并加入待翻译字符中去，保持句子完整性
						//判断依据是上个字符存储至的翻译字符语种序列，不是特殊字符，而且也不是要翻译的目标语种，那肯定就是待翻译的，将连接符加入待翻译中一起进行翻译
						if(upLangs['storage_language'] != 'specialCharacter' && upLangs['storage_language'] != translate.to){
							
							language = upLangs['storage_language'];
							console.log('teshu:'+charstr+', 当前字符并入上个字符存储翻译语种:'+upLangs['storage_language']);
						}
						*/
					}
				//}

				//console.log('isEqual:'+isEqual);
				/*
				if(isEqual){
					//跟上次语言一样，那么直接拼接
					index = langStrs[language].length-1; 
					//但是还有别的特殊情况，v2.1针对英文翻译准确度的适配，会有特殊字符的问题
					if(typeof(upLangs['storage_language']) != 'undefined' && upLangs['storage_language'] != language){
						//如果上个字符存入的翻译队列跟当前这个要存入的队列不一个的话，那应该是特殊字符像是逗号句号等导致的，那样还要额外一个数组，不能在存入之前的数组了
						index = langStrs[language].length; 
					}
				}else{
					//console.log('新开');
					//当前字符跟上次语言不样，那么新开一个数组
					index = langStrs[language].length;
					//console.log('++, inde:'+index+',lang:'+language+', length:'+langStrs[language].length)
				}
				*/

				//当前要翻译的语种跟上个字符要翻译的语种一样，那么直接拼接
				if(upLangs['storage_language'] == language){
					index = langStrs[language].length-1; 
				}else{
					//console.log('新开');
					//当前字符跟上次语言不样，那么新开一个数组
					index = langStrs[language].length;
				}
			}
			if(typeof(langStrs[language][index]) == 'undefined'){
				langStrs[language][index] = new Array();
				langStrs[language][index]['beforeText'] = '';
				langStrs[language][index]['afterText'] = '';
				langStrs[language][index]['text'] = '';
			}
			langStrs[language][index]['text'] = langStrs[language][index]['text'] + charstr;
			/*
				中文英文混合时，当中文+英文并没有空格间隔，翻译为英文时，会使中文翻译英文的结果跟原本的英文单词连到一块。这里就是解决这种情况
				针对当前非英文(不需要空格分隔符，像是中文、韩语)，但要翻译为英文（需要空格作为分割符号，像是法语等）时的情况进行判断
			*/
			//if(translate.language.getLocal() != 'english' && translate.to == 'english'){
			//当前本地语种的语言是连续的，但翻译的目标语言不是连续的（空格间隔）
			if( translate.language.wordBlankConnector(translate.language.getLocal()) == false && translate.language.wordBlankConnector(translate.to)){	
				if((upLangs['storage_language'] != null && typeof(upLangs['storage_language']) != 'undefined' && upLangs['storage_language'].length > 0)){
					//上个字符存在
					//console.log(upLangs['storage_language']);
					if(upLangs['storage_language'] != 'specialCharacter'){
						//上个字符不是特殊字符 （是正常语种。且不会是连接符，连接符都并入了正常语种）

						//if( upLangs['storage_language'] != 'english' && language == 'english'){
						//上个字符的语言是连续的，但当前字符的语言不是连续的（空格间隔）
						if( translate.language.wordBlankConnector(upLangs['storage_language']) == false && translate.language.wordBlankConnector(language) ){
							//上个字符不是英语，当前字符是英语，这种情况要在上个字符后面追加空格，因为当前字符是英文，就不会在执行翻译操作了
							//console.log(upLangs['language']);
							langStrs[upLangs['storage_language']][langStrs[upLangs['storage_language']].length-1]['afterText'] = ' ';
						}else if(upLangs['storage_language'] == 'english' && language != 'english'){
							//上个字符是英语，当前字符不是英语，直接在当前字符前面追加空格
							langStrs[language][index]['beforeText'] = ' ';
						}
					}

					
				}
			}

			var result = new Array();
			result['langStrs'] = langStrs;
			result['storage_language'] = language;	//实际存入了哪种语种队列
			//console.log(result);
			//console.log(langStrs)
			//console.log(charstr);
			return result;
		},
		
		/*
		 * 不同于语言，这个只是单纯的连接符。比如英文单词之间有逗号、句号、空格， 汉字之间有逗号句号书名号的。避免一行完整的句子被分割，导致翻译不准确
		 * 单独拿他出来，目的是为了更好的判断计算，提高翻译的准确率
		 */
		connector:function(str){
			
			/*
				通用的有 空格、阿拉伯数字
				1.不间断空格\u00A0,主要用在office中,让一个单词在结尾处不会换行显示,快捷键ctrl+shift+space ;
				2.半角空格(英文符号)\u0020,代码中常用的;
				3.全角空格(中文符号)\u3000,中文文章中使用; 
			*/	
			if(/.*[\u0020\u00A0\u202F\u205F\u3000]+.*$/.test(str)){
				return true;
			}
			/*
				U+0030 0 数字 0
				U+0031 1 数字 1
				U+0032 2 数字 2
				U+0033 3 数字 3
				U+0034 4 数字 4
				U+0035 5 数字 5
				U+0036 6 数字 6
				U+0037 7 数字 7
				U+0038 8 数字 8
				U+0039 9 数字 9
			*/
			if(/.*[\u0030-\u0039]+.*$/.test(str)){ 
				return true
			}
			
		
			/*
				英文场景
				英文逗号、句号
				这里不包括() 因为这里面的基本属于补充，对语句前后并无强依赖关系
				
				U+0021 ! 叹号
				U+0022 " 双引号
				U+0023 # 井号
				U+0024 $ 价钱/货币符号
				U+0025 % 百分比符号
				U+0026 & 英文“and”的简写符号
				U+0027 ' 引号
				U+002C , 逗号
				U+002D - 连字号/减号
				U+002E . 句号
				U+003A : 冒号
				U+003B ; 分号
				U+003F ? 问号
				U+0040 @ 英文“at”的简写符号


			*/
			if(/.*[\u0021\u0022\u0023\u0024\u0025\u0026\u0027\u002C\u002D\u002E\u003A\u003B\u003F\u0040]+.*$/.test(str)){
				return true;
			}
			
			/*
				中文标点符号
				名称	Unicode	符号
				句号	3002	。
				问号	FF1F	？
				叹号	FF01	！
				逗号	FF0C	，
				顿号	3001	、
				分号	FF1B	；
				冒号	FF1A	：
				引号	300C	「
				 	300D	」
				引号	300E	『
				 	300F	』
				引号	2018	‘
				 	2019	’
				引号	201C	“
				 	201D	”
				括号	FF08	（
				 	FF09	）
				括号	3014	〔
				 	3015	〕
				括号	3010	【
				 	3011	】
				破折号	2014	—
				省略号	2026	…
				连接号	2013	–
				间隔号	FF0E	．
				书名号	300A	《
				 	300B	》
				书名号	3008	〈
				 	3009	〉
				键盘123前面的那个符号 · 00b7
			*/
			if(/.*[\u3002\uFF1F\uFF01\uFF0C\u3001\uFF1B\uFF1A\u300C\u300D\u300E\u300F\u2018\u2019\u201C\u201D\uFF08\uFF09\u3014\u3015\u3010\u3011\u2014\u2026\u2013\uFF0E\u300A\u300B\u3008\u3009\u00b7]+.*$/.test(str)){
				return true;
			}



			
			//不是，返回false
			return false;
		},
		//语种的单词连接符是否需要空格，比如中文简体、繁体、韩文、日语都不需要空格，则返回false, 但是像是英文的单词间需要空格进行隔开，则返回true
		//另外这也是区分是否使用标点符号 ，。还是 ,. 的
		//如果未匹配到，默认返回true
		//language：语种，传入如  english
		wordBlankConnector:function(language){
			if(language == null || typeof(language) == 'undefined'){
				return true;
			}
			switch (language.trim().toLowerCase()){
		  		case 'chinese_simplified':
		  			return false;
		  		case 'chinese_traditional':
		  			return false;
		  		case 'korean':
		  			return false;
		  		case 'japanese':
		  			return false;
		  	}
		  	//其他情况则返回true
		  	return true;
		},
		//繁体中文的字典，判断繁体中文就是通过此判断
		chinese_traditional_dict: '皚藹礙愛翺襖奧壩罷擺敗頒辦絆幫綁鎊謗剝飽寶報鮑輩貝鋇狽備憊繃筆畢斃閉邊編貶變辯辮鼈癟瀕濱賓擯餅撥缽鉑駁蔔補參蠶殘慚慘燦蒼艙倉滄廁側冊測層詫攙摻蟬饞讒纏鏟産闡顫場嘗長償腸廠暢鈔車徹塵陳襯撐稱懲誠騁癡遲馳恥齒熾沖蟲寵疇躊籌綢醜櫥廚鋤雛礎儲觸處傳瘡闖創錘純綽辭詞賜聰蔥囪從叢湊竄錯達帶貸擔單鄲撣膽憚誕彈當擋黨蕩檔搗島禱導盜燈鄧敵滌遞締點墊電澱釣調諜疊釘頂錠訂東動棟凍鬥犢獨讀賭鍍鍛斷緞兌隊對噸頓鈍奪鵝額訛惡餓兒爾餌貳發罰閥琺礬釩煩範販飯訪紡飛廢費紛墳奮憤糞豐楓鋒風瘋馮縫諷鳳膚輻撫輔賦複負訃婦縛該鈣蓋幹趕稈贛岡剛鋼綱崗臯鎬擱鴿閣鉻個給龔宮鞏貢鈎溝構購夠蠱顧剮關觀館慣貫廣規矽歸龜閨軌詭櫃貴劊輥滾鍋國過駭韓漢閡鶴賀橫轟鴻紅後壺護滬戶嘩華畫劃話懷壞歡環還緩換喚瘓煥渙黃謊揮輝毀賄穢會燴彙諱誨繪葷渾夥獲貨禍擊機積饑譏雞績緝極輯級擠幾薊劑濟計記際繼紀夾莢頰賈鉀價駕殲監堅箋間艱緘繭檢堿鹼揀撿簡儉減薦檻鑒踐賤見鍵艦劍餞漸濺澗漿蔣槳獎講醬膠澆驕嬌攪鉸矯僥腳餃繳絞轎較稭階節莖驚經頸靜鏡徑痙競淨糾廄舊駒舉據鋸懼劇鵑絹傑潔結誡屆緊錦僅謹進晉燼盡勁荊覺決訣絕鈞軍駿開凱顆殼課墾懇摳庫褲誇塊儈寬礦曠況虧巋窺饋潰擴闊蠟臘萊來賴藍欄攔籃闌蘭瀾讕攬覽懶纜爛濫撈勞澇樂鐳壘類淚籬離裏鯉禮麗厲勵礫曆瀝隸倆聯蓮連鐮憐漣簾斂臉鏈戀煉練糧涼兩輛諒療遼鐐獵臨鄰鱗凜賃齡鈴淩靈嶺領餾劉龍聾嚨籠壟攏隴樓婁摟簍蘆盧顱廬爐擄鹵虜魯賂祿錄陸驢呂鋁侶屢縷慮濾綠巒攣孿灤亂掄輪倫侖淪綸論蘿羅邏鑼籮騾駱絡媽瑪碼螞馬罵嗎買麥賣邁脈瞞饅蠻滿謾貓錨鉚貿麽黴沒鎂門悶們錳夢謎彌覓綿緬廟滅憫閩鳴銘謬謀畝鈉納難撓腦惱鬧餒膩攆撚釀鳥聶齧鑷鎳檸獰甯擰濘鈕紐膿濃農瘧諾歐鷗毆嘔漚盤龐國愛賠噴鵬騙飄頻貧蘋憑評潑頗撲鋪樸譜臍齊騎豈啓氣棄訖牽扡釺鉛遷簽謙錢鉗潛淺譴塹槍嗆牆薔強搶鍬橋喬僑翹竅竊欽親輕氫傾頃請慶瓊窮趨區軀驅齲顴權勸卻鵲讓饒擾繞熱韌認紉榮絨軟銳閏潤灑薩鰓賽傘喪騷掃澀殺紗篩曬閃陝贍繕傷賞燒紹賒攝懾設紳審嬸腎滲聲繩勝聖師獅濕詩屍時蝕實識駛勢釋飾視試壽獸樞輸書贖屬術樹豎數帥雙誰稅順說碩爍絲飼聳慫頌訟誦擻蘇訴肅雖綏歲孫損筍縮瑣鎖獺撻擡攤貪癱灘壇譚談歎湯燙濤縧騰謄銻題體屜條貼鐵廳聽烴銅統頭圖塗團頹蛻脫鴕馱駝橢窪襪彎灣頑萬網韋違圍爲濰維葦偉僞緯謂衛溫聞紋穩問甕撾蝸渦窩嗚鎢烏誣無蕪吳塢霧務誤錫犧襲習銑戲細蝦轄峽俠狹廈鍁鮮纖鹹賢銜閑顯險現獻縣餡羨憲線廂鑲鄉詳響項蕭銷曉嘯蠍協挾攜脅諧寫瀉謝鋅釁興洶鏽繡虛噓須許緒續軒懸選癬絢學勳詢尋馴訓訊遜壓鴉鴨啞亞訝閹煙鹽嚴顔閻豔厭硯彥諺驗鴦楊揚瘍陽癢養樣瑤搖堯遙窯謠藥爺頁業葉醫銥頤遺儀彜蟻藝億憶義詣議誼譯異繹蔭陰銀飲櫻嬰鷹應纓瑩螢營熒蠅穎喲擁傭癰踴詠湧優憂郵鈾猶遊誘輿魚漁娛與嶼語籲禦獄譽預馭鴛淵轅園員圓緣遠願約躍鑰嶽粵悅閱雲鄖勻隕運蘊醞暈韻雜災載攢暫贊贓髒鑿棗竈責擇則澤賊贈紮劄軋鍘閘詐齋債氈盞斬輾嶄棧戰綻張漲帳賬脹趙蟄轍鍺這貞針偵診鎮陣掙睜猙幀鄭證織職執紙摯擲幟質鍾終種腫衆謅軸皺晝驟豬諸誅燭矚囑貯鑄築駐專磚轉賺樁莊裝妝壯狀錐贅墜綴諄濁茲資漬蹤綜總縱鄒詛組鑽緻鐘麼為隻兇準啟闆裡靂餘鍊',
		/*
			中文判断
			返回：
				simplified：简体中文
				traditional：繁体中文
				空字符串：不是中文
		*/   
		chinese:function(str){
			if(/.*[\u4e00-\u9fa5]+.*$/.test(str)){ 
				if(this.chinese_traditional_dict.indexOf(str) > -1){ 
					return 'traditional';
				} else {
					return 'simplified';
				}
			} else {
				return '';
			}
		},
		//是否包含日语，true:包含
		japanese:function(str){
			if(/.*[\u3040-\u309F\u30A0-\u30FF]+.*$/.test(str)){ 
				return true
			} else {
				return false;
			}
		},
		//是否包含韩语，true:包含
		korean:function(str){
			if(/.*[\uAC00-\uD7AF]+.*$/.test(str)){ 
				return true
			} else {
				return false;
			}
		},
		//是否包含俄语
		russian:function(str){
			// 正则表达式匹配俄语大小写字母（包含 Ё/ё，排除其他语言特有的西里尔字符）
			//АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЮюЯя
			//if(/^[А-Яа-яЁё]$/.test(str)){ 
			if(/^[\u0410-\u044F\u0401\u0451]$/.test(str)){ 	
				return true
			} else {
				return false;
			}
		},
		//是否包含泰语
		thai:function(str){
			if(/^[\u0E01-\u0E59]$/.test(str)){ 
				return true
			} else {
				return false;
			}
		},
		//是否包含阿拉伯语
		arabic:function(str){
			/*
				阿拉伯语基本区块（U+0600–U+06FF）
				阿拉伯语补充区块（U+0750–U+077F）
			*/
			return /^[\u0600-\u06FF\u0750-\u077F]$/.test(str);
		},
		//是否包含 罗马尼亚语
		romanian:function(str) {
			/*
				U+00C0–U+00FF：Latin-1 Supplement，排除 U+00D7（×）和 U+00F7（÷）
				U+0100–U+017F：Latin Extended-A （包含罗马尼亚语特有字母 Ă/ă、Â/â、Î/î 等）；
				U+0218–U+021B：Latin Extended-B （包含 Ș/ș 和 Ț/ț，这是罗马尼亚语标志性字母）
			*/
		    return /^[\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u017F\u0218-\u021B]$/.test(str);
		},
		//是否包含希腊语
		greek:function(str){
			const greekRegex = /^[\u0391-\u03A9\u03B1-\u03C9]$/;
			//判断字符有  БВДЖЗИЙЛМНОПСТУФХЦЧШЩЪЫЬЮЯЇІ
			if(/^[\u0391-\u03A9\u03B1-\u03C9]$/.test(str)){ 
				return true
			} else {
				return false;
			}
		},
		//希伯来语
		hebrew:function(str){
			return /^[\u0590-\u05FF]$/.test(str);
		},
		//0-9 阿拉伯数字
		number:function(str){
			if(/.*[\u0030-\u0039]+.*$/.test(str)){
				return true;
			}
			return false;
		},
		//是否包含英文，true:包含
		english:function(str){
			if(/.*[\u0041-\u005a]+.*$/.test(str)){ 
				return true;
			} else if(/.*[\u0061-\u007a]+.*$/.test(str)){
				return true;
			} else {
				return false;
			}
		},
		//是否包含 罗曼语族 的特殊字符，因为 法语、西班牙语、意大利语、葡萄牙語  都属于这个语族，单纯判断特殊字符已经不能判断出到底属于哪个语种了
		romance_dict:['é','è','ê','à','ç','œ','ñ','á','ó','ò','ì','ã','õ'],
		romance:function(str){
			if(this.romance_dict.indexOf(str) > -1){ 
				return true;
			} else {
				return false;
			}
		},
		//对 罗曼语族 的句子进行分析，看它是属于 法语、西班牙语、意大利语、葡萄牙語 的哪个。注意这个是传入的整体的句子，不是传入的单个字符
		//返回识别的语种：  french、spanish、italian、portuguese  如果都没有识别出来，则返回空字符串
		romanceSentenceAnaly:function(text) {
		    // 定义各语言的典型字母/符号权重 (可调整)
		    const langFeatures = {
		        'french': { score:0 , chars: ['é','è','ê','à','ç','œ'] },
		        'spanish': { score:0 , chars: ['ñ','á','ó'], pairs: ['ll'] },
		        'italian': { score:0 , chars: ['ò','ì'], pairs: ['cc', 'ss'] },
		        'portuguese': { score:0 , chars: ['ã', 'õ'] }
		    };

		    // 逐字扫描 + 相邻配对检测
		    for (let i=0; i<text.length; i++) {
		        const char = text[i].toLowerCase(); 
		        
		        // 单字匹配
		        Object.keys(langFeatures).forEach(lang => {
		            if (langFeatures[lang].chars.includes(char)) {
		                langFeatures[lang].score +=1;
		            }
		        });

		        // 双字配对检测 (如 ll)
		        if(i < text.length -1) {
		            const pair = text.slice(i,i+2).toLowerCase();
		            Object.keys(langFeatures).forEach(lang => {
		            	const pairs = langFeatures[lang].pairs;
				        if (pairs && pairs.includes(pair)) {
				            langFeatures[lang].score += 2; // pair权重大于单字
				        }
		            });
		         }
		    }

		    // 结果判定 （取最高分）
		    let maxLang = '';
		    let maxScore = -1;
		    
		    Object.keys(langFeatures).forEach(lang =>{
		       if(langFeatures[lang].score > maxScore){
		           maxScore = langFeatures[lang].score;
		           maxLang = lang;
		       } 
		    });

		    return maxLang || ''; 
		},
		/**romanceSentenceAnaly end**/

		//是否包含特殊字符，包含，则是true
		specialCharacter:function(str){
			//如：① ⑴ ⒈ 
			if(/.*[\u2460-\u24E9]+.*$/.test(str)){ 
				return true
			}

			//如：┊┌┍ ▃ ▄ ▅
			if(/.*[\u2500-\u25FF]+.*$/.test(str)){ 
				return true
			}

			//如：㈠  ㎎ ㎏ ㎡
			if(/.*[\u3200-\u33FF]+.*$/.test(str)){ 
				return true
			}
			
			//如：与ANSI对应的全角字符
			if(/.*[\uFF00-\uFF5E]+.*$/.test(str)){ 
				return true
			}

			/*
				U+00D7 ×：乘号
				U+00F7 ÷：除号
				这两个字符属于数学运算符，不属于罗马尼亚语字母，必须作为特殊字符处理，
				避免计算表达式被错误加入罗马尼亚语翻译队列。
			*/
			if(/.*[\u00D7\u00F7]+.*$/.test(str)){
				return true;
			}

			//其它特殊符号
			if(/.*[\u2000-\u22FF]+.*$/.test(str)){ 
				return true
			}

			// 、><等符号
			if(/.*[\u3001-\u3036]+.*$/.test(str)){
				return true;
			}

			
			
			/*
			U+0020 空格
			U+0021 ! 叹号
			U+0022 " 双引号
			U+0023 # 井号
			U+0024 $ 价钱/货币符号
			U+0025 % 百分比符号
			U+0026 & 英文“and”的简写符号
			U+0027 ' 引号
			U+0028 ( 开 左圆括号
			U+0029 ) 关 右圆括号
			U+002A * 星号
			U+002B + 加号
			U+002C , 逗号
			U+002D - 连字号/减号
			U+002E . 句号
			U+002F / 左斜杠
			*/
			if(/.*[\u0020-\u002F]+.*$/.test(str)){
				return true;
			}

			/*
				U+003A : 冒号
				U+003B ; 分号
				U+003C < 小于符号
				U+003D = 等于号
				U+003E > 大于符号
				U+003F ? 问号
				U+005B [ 开 方括号
				U+005C \ 右斜杠
				U+005D ] 关 方括号
				U+005E ^ 抑扬（重音）符号
				U+005F _ 底线
				U+0060 ` 重音符
				U+007B { 开 左花括号
				U+007C | 直线
				U+007D } 关 右花括号
				U+007E ~ 波浪纹
			*/
			if(/.*[\u003B\u003B\u003C\u003D\u003E\u003F\u005B\u005C\u005D\u005E\u005F\u0060\u007B\u007C\u007D\u007E]+.*$/.test(str)){
				return true;
			}
			
			//空白字符，\u0009\u000a + https://cloud.tencent.com/developer/article/2128593
			if(/.*[\u0009\u000a\u0020\u00A0\u1680\u180E\u202F\u205F\u3000\uFEFF]+.*$/.test(str)){
				return true;
			}
			if(/.*[\u2000-\u200B]+.*$/.test(str)){
				return true;
			}
			
			/*
				这些字符主要是 罕见的拉丁字母变体 ，通常用于：
				某些非洲语言或方言；
				古文字、语音学符号；
				特殊排版或装饰性字体。
			*/
			if(/.*[\u2C60-\u2C77]+.*$/.test(str)){
				return true;
			}
			
			
			return false;
		},
		/*
            文本翻译的替换。

            @Deprecated 2025.4.26 最新的在  translate.util.textReplace 

            text: 原始文本，翻译的某句或者某个词就在这个文本之中
            translateOriginal: 翻译的某个词或句，在翻译之前的文本
            translateResult: 翻译的某个词或句，在翻译之后的文本，翻译结果
            language: 显示的语种，这里是对应的 translateResult 这个文本的语种。 也就是最终替换之后要显示给用户的语种。比如将中文翻译为英文，这里也就是英文。 这里会根据显示的语种不同，来自主决定是否前后加空格进行分割。 另外这里传入的语种也是 translate.js 的语种标识
        	
			(注意，如果 translateResult 与 translateOriginal 完全相同，则不进行空格和标点格式化，直接将 text 返回)
			
			使用此方法：
			var text = '你世好word世界';
			var translateOriginal = '世';
			var translateResult = '世杰'; //翻译结果
			translate.language.textTranslateReplace(text, translateOriginal, translateResult, 'english');
			
        */
        textTranslateReplace:function(text, translateOriginal, translateResult, language){
        	return translate.util.textReplace(text, translateOriginal, translateResult, language);
        }
	},
	//用户第一次打开网页时，自动判断当前用户所在国家使用的是哪种语言，来自动进行切换为用户所在国家的语种。
	//如果使用后，第二次在用，那就优先以用户所选择的为主
	executeByLocalLanguage:function(){
		//先读用户自己浏览器的默认语言
		var browserDefaultLanguage = translate.util.browserDefaultLanguage();
		if(typeof(browserDefaultLanguage) !== 'undefined' && browserDefaultLanguage.length > 0){
			translate.changeLanguage(browserDefaultLanguage);
			return;
		}

		if(typeof(translate.request.api.ip) !== 'string' || translate.request.api.ip === null || translate.request.api.ip.length < 1){
			return;
		}

		//如果用户浏览器没读到默认语言，或者默认语言没有对应到translate.js支持的语种，那么在采用ip识别的方式
		translate.request.post(translate.request.api.ip, {}, function(responseData, requestData){
			//console.log(responseData); 
			if(responseData.result != 1){
				if(typeof(responseData.info) === 'string' && responseData.info.indexOf('file not find') > -1){
					translate.log('WARNING ： 服务端未启动根据用户所在的ip来获取用户所在的具体位置（此能力因准确率问题已被废弃）。当前您的浏览器默认语言为：'+', translate.js自动识别出的为：'+browserDefaultLanguage);
					if(typeof(browserDefaultLanguage) === 'string' && browserDefaultLanguage.length === 0){
						translate.log('原因是浏览器默认语言未在 translate.js 的语言对照数据中找到对应的语种，请联系我们 https://translate.zvo.cn/4030.html 反馈此问题，我们追加对应的语种对应关系。');
					}
				}else{
					translate.log('==== ERROR 获取当前用户所在区域异常 ====');
					translate.log(responseData.info);
					translate.log('==== ERROR END ====');
				}
			}else{
				translate.storage.set('to',responseData.language);	//设置目标翻译语言
				translate.to = responseData.language; //设置目标语言
				//translate.selectLanguageTag
				translate.execute(); //执行翻译
			}
		}, null);
	},
	
	util:{

		/*
			针对 textReplace 处理时对句子生成其全角、半角状态的处理 的 逆向处理

			text: 要处理的文本句子
			language: 目标语言，如 english
			返回数组。

			比如
			 text 传入的是 :
				，是吗。
			 language 传入的是 english
			 那么返回的数组是：
			 	[
					"，是吗。",				
					", 是吗。",
					"，是吗. ",
					", 是吗. ",
			 	]
		*/
		text_full_half_width_generate: function(text, language){
			//console.log(text);

			if(typeof(text) === 'undefined'){
				return [text];
			}
			if(text.trim().length == 0){
				return [text];
			}

			//文字之间需要空格，也就是半角标点符号，像是英语，法语， 则是true
        	var requireSpace = translate.language.wordBlankConnector(language);

        	/**** 判断 findText 的开始字符跟结束字符是否包含着 特殊符号 ，：。 因为在 translate.util.textReplace 替换时，会根据当前语种，自动将前后有句号等符号时进行中英的符号转换，此时如果 findText 传入的带有句号的，比如 “你好，” 而实际上text的内容是已经被替换过，就会导致 “你好，” 找不到，而 “你好,” 能找到 ****/
			var punctuationMarks_fullWidth = ['，','：','。']; //标点符号-全角，用于中文等语种
			var punctuationMarks_halfWidth = [',',':','.']; //标点符号-半角，用于英文等语种
			
			//如果要替换的文本只是原文本中的一部分，那么就需要进行处理了
        	var findText = text;

			//取第一个字符
			var findTextFirstChar = findText.charAt(0);
			//取最后一个字符
			var findTextLastChar = findText.charAt(findText.length-1);

        	/*
			 * translateOriginal 生成的用于替换的变种，可能是多个，比如  “你好，世界。” 中的  "，世界" 在翻译为英文情况时，会出现这几种变种：
			 * , 世界。
			 * , 世界. 
			 * ，世界。
			 * ，世界. 
			 * 根据不同的中英文，标点符号后面是否跟空格也不同
			 */
			var originalArray = [];
			originalArray.push(text); //首先把当前的加入进去

			//翻译替换为半角标点符号，如英语
			if(requireSpace){

				//第一个发现全角字符， 转为半角处理
				if(punctuationMarks_fullWidth.indexOf(findTextFirstChar) > -1){
					var processFirstCharText = punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextFirstChar)]+' '+findText.substring(1, findText.length);
					originalArray.push(processFirstCharText);
					
					//第一个处理后，寻找最后一个全角字符转为半角处理
					if(punctuationMarks_fullWidth.indexOf(findTextLastChar) > -1){
						originalArray.push(processFirstCharText.substring(0, processFirstCharText.length-1)+punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextLastChar)]+' ');
						originalArray.push(processFirstCharText.substring(0, processFirstCharText.length-1)+punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextLastChar)]);
					}
				}

				//将最后一个全角字符转为半角处理
				if(punctuationMarks_fullWidth.indexOf(findTextLastChar) > -1){
					originalArray.push(findText.substring(0, findText.length-1)+punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextLastChar)]+ ' ');
					originalArray.push(findText.substring(0, findText.length-1)+punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextLastChar)]);
				}
			}else{
				//翻译替换为全角标点符号，如中文

				//第一个发现半字符， 转为全角处理。这里不用跟上面似的追加去除空格的，因为 textReplace_service 只为了阅读方便追加空格，并没有做去空格处理。 
				if(punctuationMarks_halfWidth.indexOf(findTextFirstChar) > -1){
					var processLastCharText = punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextFirstChar)]+findText.substring(1, findText.length);
					originalArray.push(processLastCharText);

					//判断第二个字符是否是空格，如果是，那可能是自动有英转中时追加的空格，这也要考虑把空格去掉的情况
					if(processLastCharText.charAt(1) === ' '){
						originalArray.push(processLastCharText.substring(0, 1) + processLastCharText.substring(2, findText.length));
					}

					//第一个处理后，寻找最后一个半角字符转为全角处理
					if(punctuationMarks_halfWidth.indexOf(findTextLastChar) > -1){
						originalArray.push(processLastCharText.substring(0, processLastCharText.length-1)+punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextLastChar)]);
						
						//判断第二个字符是否是空格，如果是，那可能是自动有英转中时追加的空格，这也要考虑把空格去掉的情况
						if(processLastCharText.charAt(1) === ' '){
							originalArray.push(processLastCharText.substring(0, 1) + processLastCharText.substring(2, processLastCharText.length-1)+punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextLastChar)]);
						}
					}
				}

				//将最后一个全角字符转为半角处理
				if(punctuationMarks_halfWidth.indexOf(findTextLastChar) > -1){
					originalArray.push(findText.substring(0, findText.length-1)+punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextLastChar)]);
				}
			}
			//console.log(originalArray);
			return originalArray;
		},

		/*
            文本替换，将替换完毕的结果返回
            自定义术语等都是通过这个来进行替换
            2025.4.26 从 language 中 拿到这里
            
            text: 原始文本，翻译的某句或者某个词就在这个文本之中
            translateOriginal: 翻译的某个词或句，在翻译之前的文本
            translateResult: 翻译的某个词或句，在翻译之后的文本，翻译结果
            language: 显示的语种，这里是对应的 translateResult 这个文本的语种。 也就是最终替换之后要显示给用户的语种。比如将中文翻译为英文，这里也就是英文。 这里会根据显示的语种不同，来自主决定是否前后加空格进行分割。 另外这里传入的语种也是 translate.js 的语种标识
        	participles: 分词，数组形态。默认不传则是没有其他分词需要保留的。 传入比如  ['你好','你是谁'] 
        		比如 translateOriginal 传入 '你' 时， text 中的 '你好','你是谁' 是不能被拆出'你'这个字进行替换的，不然就破坏了分词了

        	(注意，如果 translateResult 中发现 translateOriginal 的存在，将不进行任何处理，因为没必要了，还会造成死循环。直接将 text 返回)
			
			使用此方法：
			var text = '你世好word世界';
			var translateOriginal = '世';
			var translateResult = '世杰'; //翻译结果
			translate.util.textReplace(text, translateOriginal, translateResult, 'english'); //没有分词，正常替换
			translate.util.textReplace(text, translateOriginal, translateResult, 'english',['世界','好word']); //有分词，要保留分词结构，不能被拆分替换，不能拆分分词的语义

        */
        textReplace:function(text, translateOriginal, translateResult, language, participles){
        	//console.log('----text:'+text.replace(/\t/g, '\\t').replace(/\r/g, '\\r').replace(/\n/g, '\\n').replace(/ /g, '[空白符]')+', translateOriginal:'+translateOriginal+', translateResult:'+translateResult+',\tparticiples:');
        	//console.log(participles);
        	
        	//如果要替换的源文本直接就是整个文本，那也就不用在做什么判断了，直接将 翻译的结果文本返回就好了
        	if(text == translateOriginal){
        		return translateResult;
        	}

			// 翻译结果与原文完全相同，不执行额外的空格或标点处理，避免破坏原始文本格式。
			if(translateOriginal === translateResult){
				return text;
			}
        	
        	//console.log('participles ---- 处理');
        	//console.log(participles);
        	if(typeof(participles) === 'object'){
        		for(var pi = participles.length; pi >= 0; pi--){
        			var participlesItemArray = translate.util.text_full_half_width_generate(participles[pi], language);
        			if(participlesItemArray.length > 1){
        				//被拆了，要合并
        				participlesItemArray.shift(); // 移除第一个，也就是原本的participles中的元素
        				participles = participles.concat(participlesItemArray);
        			}
        		}
        	}
        	

        	/*
        	//文字之间需要空格，也就是半角标点符号，像是英语，法语， 则是true
        	var requireSpace = translate.language.wordBlankConnector(language);

        	//如果要替换的文本只是原文本中的一部分，那么就需要进行处理了
        	var findText = translateOriginal;
			*/


        	/**** 判断 findText 的开始字符跟结束字符是否包含着 特殊符号 ，：。 因为在 translate.util.textReplace 替换时，会根据当前语种，自动将前后有句号等符号时进行中英的符号转换，此时如果 findText 传入的带有句号的，比如 “你好，” 而实际上text的内容是已经被替换过，就会导致 “你好，” 找不到，而 “你好,” 能找到 ****/
			/*
			var punctuationMarks_fullWidth = ['，','：','。']; //标点符号-全角，用于中文等语种
			var punctuationMarks_halfWidth = [',',':','.']; //标点符号-半角，用于英文等语种
			
			//取第一个字符
			var findTextFirstChar = findText.charAt(0);
			//取最后一个字符
			var findTextLastChar = findText.charAt(findText.length-1);
			*/

			/*
			 * translateOriginal 生成的用于替换的变种，可能是多个，比如  “你好，世界。” 中的  "，世界" 在翻译为英文情况时，会出现这几种变种：
			 * , 世界。
			 * , 世界. 
			 * ，世界。
			 * ，世界. 
			 * 根据不同的中英文，标点符号后面是否跟空格也不同
			 */
        	var originalArray = translate.util.text_full_half_width_generate(translateOriginal, language);
			/*
			var originalArray = [];
			originalArray.push(translateOriginal); //首先把当前的加入进去

			

			//翻译替换为半角标点符号，如英语
			if(requireSpace){

				//第一个发现全角字符， 转为半角处理
				if(punctuationMarks_fullWidth.indexOf(findTextFirstChar) > -1){
					var processFirstCharText = punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextFirstChar)]+' '+findText.substring(1, findText.length);
					originalArray.push(processFirstCharText);
					
					//第一个处理后，寻找最后一个全角字符转为半角处理
					if(punctuationMarks_fullWidth.indexOf(findTextLastChar) > -1){
						originalArray.push(processFirstCharText.substring(0, processFirstCharText.length-1)+punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextLastChar)]);
					}
				}

				//将最后一个全角字符转为半角处理
				if(punctuationMarks_fullWidth.indexOf(findTextLastChar) > -1){
					originalArray.push(findText.substring(0, findText.length-1)+punctuationMarks_halfWidth[punctuationMarks_fullWidth.indexOf(findTextLastChar)]);
				}
			}else{
				//翻译替换为全角标点符号，如中文

				//第一个发现半字符， 转为全角处理。这里不用跟上面似的追加去除空格的，因为 textReplace_service 只为了阅读方便追加空格，并没有做去空格处理。 
				if(punctuationMarks_halfWidth.indexOf(findTextFirstChar) > -1){
					var processLastCharText = punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextFirstChar)]+' '+findText.substring(1, findText.length);
					originalArray.push(processLastCharText);

					//第一个处理后，寻找最后一个半角字符转为全角处理
					if(punctuationMarks_halfWidth.indexOf(findTextLastChar) > -1){
						originalArray.push(processLastCharText.substring(0, processLastCharText.length-1)+punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextLastChar)]);
					}
				}

				//将最后一个全角字符转为半角处理
				if(punctuationMarks_halfWidth.indexOf(findTextLastChar) > -1){
					originalArray.push(findText.substring(0, findText.length-1)+punctuationMarks_fullWidth[punctuationMarks_halfWidth.indexOf(findTextLastChar)]);
				}
			}
			*/

			for(var i = 0; i < originalArray.length; i++){
				if(text.indexOf(originalArray[i]) > -1){
					text = translate.util.textReplace_service(text, originalArray[i], translateResult, language, participles);
				}
			}
			
			return text;
        },
        /*
            它服务于上面的 textReplace，不需要直接使用这个

        */
        textReplace_service:function(text, translateOriginal, translateResult, language, participles){
        	//console.log('----text:'+text+', translateOriginal:'+translateOriginal+', translateResult:'+translateResult+", participles:");
        	//console.log(participles);
        	//如果要替换的源文本直接就是整个文本，那也就不用在做什么判断了，直接将 翻译的结果文本返回就好了
        	if(text == translateOriginal){
        		return translateResult;
        	}
        	
        	/*

        	//当前替换后，替换结果结束位置的下标。 
        	//一开始还没进行替换，那么这个下标就是 0
        	//比如 你好吗  中的 好 替换为 "好的" 那最后结果为 "你好的吗" ，这里是 “的” 的下标 2
        	let currentReplaceEndIndex = 0;

        	//while最大循环次数30次，免得出现未知异常导致死循环
        	let maxWhileNumber = 30;
			*/
			
			var indexArray = translate.util.findParticiple(text, translateOriginal, participles);
			for(var i = indexArray.length-1; i > -1 ; i--){
        		//console.log('text:'+text+'\tcurrentReplaceEndIndex:'+currentReplaceEndIndex);

        		//通过 translate.util.replaceFromIndex 进行替换时的 index 开始位置 。 比如如果下面识别前面的一个字符，要变为, 那也就是要继续向前一位，这里就要 -1
        		let replaceIndex = indexArray[i]; 
        		//要替换的结果文本（这个文本可能前面有加空格或者后面有加空格的）
           		let replaceResultText = ''+translateResult; 
           		//替换的文本 ，这里有可能会追加上某些标点符号，所以单独也列出来，而不是使用方法中传入的 translateOriginal
           		let replaceOriginalText = '' + translateOriginal; 


           		//根据不同的语种，如果有的语种需要加空格来进行区分单词，那么也要进行空格的判定
           		if(translate.language.wordBlankConnector(language)){
	                //let originalIndex = text.indexOf(translateOriginal, currentReplaceEndIndex); //翻译之前，翻译的单词在字符串中的起始坐标（0开始）
	                let originalIndex = indexArray[i];
	                //console.log("originalIndex: "+originalIndex);

	                //要先判断后面，不然先判断前面，加了后它的长度就又变了

	                //判断它后面是否还有文本
	                var afterCharIndex = indexArray[i]+translateOriginal.length; //translateOriginal之后的第一个文本的index下标
	                if(afterCharIndex < text.length){
	                	let char = text.charAt(afterCharIndex);
	                    //console.log(translateOriginal+' after char : '+char+", index:"+afterCharIndex);
	                    if(/。/.test(char)){
	                    	replaceResultText = replaceResultText + '. ';
	                    	replaceOriginalText = translateOriginal + '。';
	                    }else if(/，/.test(char)){
	                    	replaceResultText = replaceResultText + ', ';
	                    	replaceOriginalText = translateOriginal + '，';
	                    }else if(/：/.test(char)){
	                    	replaceResultText = replaceResultText + ': ';
	                    	replaceOriginalText = translateOriginal + '：';	
	                    }else if([' ', '\n','\t',']','|', '_','-','/'].indexOf(char) !== -1){
							// 如果后面的字符是 这些字符，那么不用添加空格隔开
						}else{
							//补充上一个空格，用于将两个单词隔开。  不过 ，如果当前 replaceResultText 的最后一个字符也是空格，那就不需要再加空格了。 这里就只判断空格就好了，至于其他的换行等基本不会出现这个情况，所以不考虑
							if(replaceResultText.length > 0 && replaceResultText.charAt(replaceResultText.length-1) == ' '){
								//replaceResultText 本身有值，且最后一个字符就是空格，就不需要再追加空格进行隔开了
							}else{
								replaceResultText = replaceResultText + ' ';
							}
	                    }
	                }

	                //判断它前面是否还有文本
	                if(originalIndex > 0){
	                    let char = text.charAt(originalIndex-1);
	                    //console.log(char);
	                    
  						if(/。/.test(char)){
  							replaceIndex--;
	                    	replaceResultText = '. '+replaceResultText;
	                    	replaceOriginalText = '。'+replaceOriginalText;
	                    }else if(/，/.test(char)){
	                    	replaceIndex--;
	                    	replaceResultText = ', '+replaceResultText;
	                    	replaceOriginalText = '，'+replaceOriginalText;
	                    }else if(/：/.test(char)){
	                    	replaceIndex--;
	                    	replaceResultText = ': '+replaceResultText;
	                    	replaceOriginalText = '：'+replaceOriginalText;	
	                    }else if([' ', '\n','\t','[', '|', '_','-','/'].indexOf(char) !== -1){
							// 如果前面的字符是 这些字符，那么不用添加空格隔开
							//console.log('不需要空格隔开的');
						}else{
	                        //补充上一个空格，用于将两个单词隔开。  不过 ，如果当前 replaceResultText 的第一个字符也是空格，那就不需要再加空格了。  这里就只判断空格就好了，至于其他的换行等基本不会出现这个情况，所以不考虑
							if(replaceResultText.length > 0 && replaceResultText.charAt(0) == ' '){
								//replaceResultText 本身有值，且最后一个字符就是空格，就不需要再追加空格进行隔开了
							}else{
								replaceResultText = ' '+replaceResultText;
							}
							//console.log('before add space : '+replaceResultText);
	                    }
	                }
	            }else{
	            	//如果是其他语种比如英语法语翻译为中文、日文，那么标点符号也要判断的，这个因为目前这个场景还没咋遇到，就不判断了，遇到了在加。

	            }
	            //console.log('replaceOriginalText: '+ replaceOriginalText+" --> replaceResultText: "+replaceResultText);
	            
	            let replaceResult  = translate.util.replaceFromIndex(text, replaceIndex, replaceOriginalText, replaceResultText);

	            if(replaceResult.replaceEndIndex < 1){
	            	translate.log('translate.util.findParticiple 中已经发现了，但是实际没有替换，出现异常了！理论上这是不应该出现的。 text:'+text+' , index:'+indexArray[i]+',  translateOriginal:'+translateOriginal);
	            }else{
	            	text = replaceResult.text;
	            }
        	}

        	//console.log(text);
            return text;
        },
        /*
			从一个字符串中, 寻找某个分词。这个分词不能破坏其他分词。
			text: 原始文本，翻译的某句或者某个词就在这个文本之中
			findText: 寻找的分词文本
			participles: 分词，数组形态。默认不传则是没有其他分词需要保留的。 传入比如  ['你好','你是谁'] 
        		比如 translateOriginal 传入 '你' 时， text 中的 '你好','你是谁' 是不能被拆出'你'这个字进行替换的，不然就破坏了分词了
			
			
			return 返回寻找到的分词文本在 text 中的下标数组（下标是从0开始）
					比如： [0, 3, 6] 便是在 text 中的0下标出现了这个 findParticiple 寻找的分词文本
				   如果没有发现，则返回 [] 空数组
				   注意，它里面的元素都是按照顺序由小往大，顺序排的

        */
        findParticiple:function(text, findText, translateTexts){
            var resultArray = [];

            //兼容 translateTexts 不传入的情况
            if(typeof(translateTexts) == 'undefined' || translateTexts == null){
                translateTexts = [];
            }

            /*****1. 先过滤，过滤掉 translateTexts 中 不包含 translateOriginal、 以及文本长度小于等于 translateOriginal  这个分词的情况*****/
            var newTranslateTexts = [];
            for(var i = 0; i < translateTexts.length; i++){
                if(translateTexts[i].indexOf(findText) != -1 && translateTexts[i].length > findText.length){
                    newTranslateTexts.push(translateTexts[i]);
                }
            }
            //console.log('包含'+findText+'的分词：');
            //console.log(newTranslateTexts)

            //当前替换后，替换结果结束位置的下标。
            //一开始还没进行替换，那么这个下标就是 0
            //比如 你好吗  中的 好 替换为 "好的" 那最后结果为 "你好的吗" ，这里是 “的” 的下标 2
            var currentReplaceEndIndex = 0;

            // TODO 【原方案】
            // //while最大循环次数30次，免得出现未知异常导致死循环
            // var maxWhileNumber = 30;
            //
            // // 识别，indexOf 逐个识别 '你' ，识别到之后，再跟 其他比如 '你是谁'  进行判断，比如  '你是谁'  就要讲 indexOf 的下标+2 来截取这text中的三个字符，去跟 '你是谁' 判定，以判定是否是一个正常的不能拆分的分词
            // while(text.indexOf(findText, currentReplaceEndIndex) > -1 && maxWhileNumber-- > 0){
            //     var index = text.indexOf(findText, currentReplaceEndIndex);
            //
            //     var findParticiple = false; //发现是其他分词了是true，没发现可以替换则是false
            //     console.log('index -> ', index)
            //
            //     //进行其他分词发现策略（旧：将句子按下标进行拆分，判断出是否属于分词）
            //     if(newTranslateTexts.length > 0){
            //         //发现的这个词可能是其他分词中的一部分，这个要判断当前index是否是其他分词的一部分。 这里要进行遍历 newTranslateTexts 逐个取出进行对比
            //
            //         for(var j = 0; j < newTranslateTexts.length; j++){
            //             //判断 newTranslateTexts[j] 这个分词中包含的 findText 这个文本，这个文本是在 newTranslateTexts[j] 的下标的多少
						   //注意，这里有bug
						   //当前替换后，替换结果结束位置的下标。 
						   //一开始还没进行替换，那么这个下标就是 0
						   //比如 你好吗  中的 好 替换为 "好的" 那最后结果为 "你好的吗" ，这里是 “的” 的下标 2、
            //             var indexInNewTranslateTexts = newTranslateTexts[j].indexOf(findText);
            //             // 因为 newTranslateTexts 是通过筛选包含 findText 而出的，所以它肯定是包含的，有下标的
            //             // 这里要从 text 中，根据 indexInNewTranslateTexts 及 原本 findText 的 index，在这个index的前或者后，追加几个文本，这追加的文本长度，也就是根据 indexInNewTranslateTexts 以及 newTranslateTexts[j] 的长度
            //
            //             //这里准备要根据 newTranslateTexts[j] 、 indexInNewTranslateTexts ，来定义从 text 中去取 对应 newTranslateTexts[j] 的长度，以判断当前index是否是取的 newTranslateTexts[j] 这个分词的
            //             var length = newTranslateTexts[j].length;
            //             var split_text = text.substring(index-indexInNewTranslateTexts, index-indexInNewTranslateTexts + length);
            //             if(split_text == newTranslateTexts[j]){
            //                 //说明当前index是取的 newTranslateTexts[j] 这个分词的,那这个就不能替换，要忽略
            //                 //console.log('当前是其他分词，不能直接替换 ： '+newTranslateTexts[j]);
            //                 findParticiple = true;
            //                 break;
            //             }
            //             console.log('split_text -> ', split_text);
            //             console.log('newTranslateTexts[j] -> ', newTranslateTexts[j]);
            //             console.log('split_text == newTranslateTexts[j] -> ', split_text == newTranslateTexts[j]);
            //             console.log('indexInNewTranslateTexts -> ', indexInNewTranslateTexts);
            //             console.log('length -> ', length);
            //             console.log('index-indexInNewTranslateTexts -> ', index-indexInNewTranslateTexts);
            //             console.log('index-index-indexInNewTranslateTexts + length -> ', index-indexInNewTranslateTexts + length);
            //         }
            //     }
            //
            //     console.log('findParticiple -> ', findParticiple);
            //     if(!findParticiple){
            //         resultArray.push(index);
            //     }
            //     currentReplaceEndIndex = index+findText.length;
            //     console.log(" ---------- ")
            // }

            // 【方案1】使用下标数组记录位置
            let flagArr = translate.util.participleIndexFind(text, newTranslateTexts);

            // while最大循环次数30次，免得出现未知异常导致死循环
            var maxWhileNumber = 300;

            // 识别，indexOf 逐个识别 '你' ，识别到之后，再跟 其他比如 '你是谁'  进行判断，比如  '你是谁'  就要讲 indexOf 的下标+2 来截取这text中的三个字符，去跟 '你是谁' 判定，以判定是否是一个正常的不能拆分的分词
            while(text.indexOf(findText, currentReplaceEndIndex) > -1 && maxWhileNumber-- > 0){
                var index = text.indexOf(findText, currentReplaceEndIndex);

                var findParticiple = false; //发现是其他分词了是true，没发现可以替换则是false

                // 遍历分词，获取出在原句子中的位置，根据下标对比判断是否属于分词
                if(newTranslateTexts.length > 0 && flagArr.length > 0){
                    // 发现的这个词可能是其他分词中的一部分，这个要判断当前index是否是其他分词的一部分。 这里要进行遍历 flagArr，如果index在此数组中的范围内说明属于其他分词
                    for(var j = 0; j < flagArr.length; j++){
                        // 取出子数组，标记了分词在原文中的位置
                        let flagItem = flagArr[j];
                        if(index >= flagItem.start && index <= flagItem.end){
                            // 说明当前index是取的 flagArr[j] 这个分词的,那这个就不能替换，要忽略
                            findParticiple = true;
                            break;
                        }
                    }
                }

                if(!findParticiple){
                    resultArray.push(index);
                }
                currentReplaceEndIndex = index+findText.length;
            }



            /*
			    这里需要将下面的 9、7 这个下标找出来，然后进行替换。  注意要先从后进行替换，避免从前替换，之后的下标长度出现变化
			  */

            //text = translate.util.replaceFromIndex(text, 9, translateOriginal, translateResult).text;
            //text = translate.util.replaceFromIndex(text, 7, translateOriginal, translateResult).text;


            //return text;

            return resultArray;
        },
        /**
         * 方案1：使用下标数组来标记分词位置
         *  传入原文和分词内容数组，返回分词在原文中的下标数组
         *  例如：
         *      text 传入 "只有那些敢于追求梦想的人，才能实现梦想。"
         *      newTranslateTexts 传入 ["梦想", "敢于追求"]
         *      则输出 [{start: 4, end: 7}, {"start": 8, "end": 9}, {"start": 17, "end": 18}]
         * @param text 原文。字符串
         * @param newTranslateTexts 分词内容数组
         * @returns 分词所在的下标范围的数组。格式为 [{"start": 1, "end": 2}]
         */
        participleIndexFind:function(text, newTranslateTexts) {
        	//console.log('text: '+text+', newTranslateTexts: ');
        	//console.log(newTranslateTexts);
            let indexArr = [];
            // 遍历分词
            for(let i = newTranslateTexts.length-1; i >= 0; i--){
                // 取出分词
                let word = newTranslateTexts[i];
                // 找出分词在原文中出现的所有位置
                let startIndex = 0; // 起始位置
                while(text.indexOf(word, startIndex) > -1) {
                    // 开始的下标
                    let index = text.indexOf(word, startIndex);
                    // 结束的下标
                    let endIndex = index+word.length-1;
                    // 封装成数组，存入二维数组中
                    let item = {
                        start: index,
                        end: endIndex
                    }
                    indexArr.push(item);
                    // 改变起始位置
                    startIndex = index+word.length-1;
                }
            }
            //console.log(indexArr);
            return indexArr;
        },
        /*
			js 的 replace 能力，这个是可以指定从第几个字符开始进行replace
			1. 这里可以 replaceText 本身包含着 originalText
			2. originalText 可以出现多次

			@param
				text 要进行替换的原始文本
				index 要从 text 的哪个下标开始。 （第一个字符下标是0）
				originalText 要替换的文本，被替换的文本
				replaceText 替换为的文本，将 originalText 替换为什么
				replaceFromIndex('你好吗？你也好？', 0, '你', '你是谁');

			@return 对象
				text 替换的结果
				replaceEndIndex 当前替换后，替换结果结束位置的下标。 
        				如果没进行替换，那么这个下标就是 0
        				比如 你好吗  中的 好 替换为 "好的" 那最后结果为 "你好的吗" ，这里是 “的” 的下标 2
		*/
        replaceFromIndex:function(text, index, originalText, replaceText){
		    const before = text.slice(0, index);
		    const after = text.slice(index);
		    const originalTextIndex = after.indexOf(originalText);
		    if(originalTextIndex > -1){
		    	const replacedAfter = after.replace(originalText, replaceText);
		    	return {
		    		text: before + replacedAfter, 
		    		replaceEndIndex: index + originalTextIndex + replaceText.length
		    	}
		    }else{
		    	//没有发现可替换的字符，那么就原样返回
		    	//console.log('after:'+after);
		    	//console.log(text+originalText);
		    	return {
		    		text: before, 
		    		replaceEndIndex: 0
		    	};
		    }
        },

		/* 生成一个随机UUID，复制于 https://gitee.com/mail_osc/kefu.js */
		uuid:function() {
		    var d = new Date().getTime();
		    if (window.performance && typeof window.performance.now === "function") {
		        d += performance.now(); //use high-precision timer if available
		    }
		    var uuid = 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		        var r = (d + Math.random() * 16) % 16 | 0;
		        d = Math.floor(d / 16);
		        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
		    });
		    return uuid;
		},

		//判断字符串中是否存在tag标签。 true存在
		findTag:function(str) {
			var reg = /<[^>]+>/g;
			return reg.test(str);
		},
		//传入一个数组，从数组中找出现频率最多的一个返回。 如果多个频率出现的次数一样，那会返回多个
		arrayFindMaxNumber:function(arr){

			// 储存每个元素出现的次数
			var numbers = {}

			// 储存出现最多次的元素
			var maxStr = []

			// 储存最多出现的元素次数
			var maxNum = 0

			for(var i =0,len=arr.length;i<len;i++){
			    if(!numbers[arr[i]]){
			          numbers[arr[i]] = 1  
			    }else{
			        numbers[arr[i]]++
			    }

			    if(numbers[arr[i]]>maxNum){
			        maxNum = numbers[arr[i]]
			    }
			}

			for(var item in numbers){
				if (!numbers.hasOwnProperty(item)) {
		    		continue;
		    	}
			    if(numbers[item]===maxNum){
			        maxStr.push(item)
			    }
			}
			
			return maxStr;
		},
		//对字符串进行hash化，目的取唯一值进行标识
		hash:function(str){
			if(str == null || typeof(str) == 'undefined'){
				return str;
			}
			var hash = 0, i, chr;
			if (str.length === 0){
				return hash;
			}

			for (i = 0; i < str.length; i++) {
				chr   = str.charCodeAt(i);
				hash  = ((hash << 5) - hash) + chr;
				hash |= 0; // Convert to 32bit integer
			}
			return hash+'';
		},
		//去除一些指定字符，如换行符。 如果传入的是null，则返回空字符串
		charReplace:function(str){

			if(str == null){
				return '';
			}
			str = str.trim();
			str = str.replace(/\t|\n|\v|\r|\f/g,'');	//去除换行符等
			//str = str.replace(/&/g, "%26"); //因为在提交时已经进行了url编码了
			return str;
		},
		//RegExp相关
		regExp:{
			// new RegExp(pattern, resultText); 中的 pattern 字符串的预处理
			pattern:function(str){
				str = str.replace(/\\/g,'\\\\'); //这个一定要放在第一个，不然会被下面的影响
				//str = str.replace(/'/g,'\\\'');
				str = str.replace(/\"/g,'\\\"');
				//str = str.replace(/./g,'\\\.');
				str = str.replace(/\?/g,'\\\?');
				str = str.replace(/\$/g,'\\\$');
				str = str.replace(/\(/g,'\\\(');
				str = str.replace(/\)/g,'\\\)');
				str = str.replace(/\|/g,'\\\|');
				str = str.replace(/\+/g,'\\\+');
				str = str.replace(/\*/g,'\\\*');
				str = str.replace(/\[/g,'\\\[');
				str = str.replace(/\]/g,'\\\]');
				str = str.replace(/\^/g,'\\\^');
				str = str.replace(/\{/g,'\\\{');
				str = str.replace(/\}/g,'\\\}');
				return str;
			},
			// new RegExp(pattern, resultText); 中的 resultText 字符串的预处理
			resultText:function(str){
				//str = str.replace(/&quot;/g,"\"");
				//str = str.replace(/'/g,"\\\'");
				//str = str.replace(/"/g,"\\\"");
				return str;
			}
		},
		//获取URL的GET参数。若没有，返回""
		getUrlParam:function (name){
		     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		     var r = window.location.search.substr(1).match(reg);
		     if(r!=null)return  unescape(r[2]); return "";
		},
		/**
		 * 同步加载JS，加载过程中会阻塞，加载完毕后继续执行后面的。
		 * url: 要加载的js的url
		 */
		synchronizesLoadJs:function(url){
			var  xmlHttp = null;  
			if(window.ActiveXObject){//IE  
				try {  
					//IE6以及以后版本中可以使用  
					xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");  
				} catch (e) {  
					//IE5.5以及以后版本可以使用  
					xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");  
				}  
			}else if(window.XMLHttpRequest){  
				//Firefox，Opera 8.0+，Safari，Chrome  
				xmlHttp = new XMLHttpRequest();  
			}  
			//采用同步加载  
			xmlHttp.open("GET",url,false);  
			//发送同步请求，如果浏览器为Chrome或Opera，必须发布后才能运行，不然会报错  
			xmlHttp.send(null);  
			//4代表数据发送完毕  
			if( xmlHttp.readyState == 4 ){  
				//0为访问的本地，200到300代表访问服务器成功，304代表没做修改访问的是缓存  
				if((xmlHttp.status >= 200 && xmlHttp.status <300) || xmlHttp.status == 0 || xmlHttp.status == 304){  
					var myBody = document.getElementsByTagName("HTML")[0];  
					var myScript = document.createElement( "script" );  
					myScript.language = "javascript";  
					myScript.type = "text/javascript";  
					try{  
						//IE8以及以下不支持这种方式，需要通过text属性来设置  
						myScript.appendChild(document.createTextNode(xmlHttp.responseText));  
					}catch (ex){  
						myScript.text = xmlHttp.responseText;  
					}  
					myBody.appendChild(myScript);  
					return true;  
				}else{  
					return false;  
				}  
			}else{  
				return false;  
			}  
		},

		/*js translate.util.loadMsgJs start*/
		//加载 msg.js
		loadMsgJs:function(){
			if(typeof(msg) != 'undefined'){
				return;
			}
			translate.util.synchronizesLoadJs('https://res.zvo.cn/msg/msg.js');
		},
		/*js translate.util.loadMsgJs end*/
		/*
			对一个对象，按照对象的key的长度进行排序，越长越在前面
		*/
		objSort:function(obj){
			// 获取对象数组的所有 key，并转换为普通数组
			var keys = Array.from(Object.keys(obj));
			//var keys = [].slice.call(Object.keys(obj)); //适配es5

			// 对 key 数组进行排序
			keys.sort(function(a, b){
			  return b.length - a.length;
			});

			// 定义一个新的对象数组，用来存储排序后的结果
			var sortedObj = new Array();

			// 遍历排序后的 key 数组，将对应的值复制到新的对象数组中，并删除原来的对象数组中的键值对
			for (var key of keys) {
			  sortedObj[key] = obj[key];
			}
			return sortedObj;
		},
		/*
			将 2.11.3.20231232 转化为 2011003
			转化时会去掉最后一个日期的字符
		*/
		versionStringToInt:function(versionString){
			var vs = versionString.split('\.');
			var result = 0;
			result = parseInt(vs[0])*1000*1000 + result;
			result = parseInt(vs[1])*1000 + result;
			result = parseInt(vs[2]) + result;

			return result;
		},
		/**
		 * 将一个 JSONArray 数组，按照文字长度进行拆分。
		 *  比如传入的 array 数组的文字长度是6200，传入的 stringLength 是2000，那么就是将 array 数组拆分为多个长度不超出2000的数组返回。
		 * 		如果传入了 maxSize = 5 那么会对拆分后的数组的长度进行判断，如果数组内元素超过5，那么还要进行缩短，拆分后的数组不允许超过这个数
		 * 		也就是拆分后的数组有两重限制，一是限制转化为文本形式的长度、再就是拆分后本身数组的大小。
		 * 		
		 *  注意，这个长度是指 array.toString() 后的长度，也就是包含了 [""] 这种符号的长度
		 * @param array 要被拆分的数组，其内都是String类型，传入格式如 ["你好","世界"]
		 * @param stringLength 要被拆分的数组转化为字符串之后的长度
		 * @param maxSize 被拆分的数组最大包含多少个，数组大小最大允许多大，要小于等于这个数。 如果设置为0则是不启用这个，不对拆分后的数组进行判断。
		 * @return 被拆分后的数组列表
		 * @author 刘晓腾
		 */
		 split:function(array, size, maxSize) {
		 	let orgsize = size;
		    let list = [];
		    // 数组长度小于size，直接进行返回
		    if(JSON.stringify(array).length <= size) {
		        list.push(array);
		    } else {
		        // 转换成String
		        let arrayStr = JSON.stringify(array).trim().substring(1, JSON.stringify(array).length - 1);

		        // 判断size和字符串长度的差值，如果为1或者2，就直接拆成两段
		        if (JSON.stringify(array).length - size <= 2) {
		            size = size - 4;
		            // 拆两段
		            let str1 = arrayStr.substring(0, arrayStr.lastIndexOf("\",\"")+1);
		            let str2 = arrayStr.substring(arrayStr.lastIndexOf("\",\"")+2);
		            list.push(JSON.parse("[" + str1 + "]"));
		            list.push(JSON.parse("[" + str2 + "]"));
		        } else {
		            size = size - 2;
		            // 拆多段
		            let index = 0;
		            while (index - arrayStr.length < 0) {
		                // 按照指定大小拆一段
		                let s = "";
		                if ((index+size) - arrayStr.length >= 0) {
		                    s = arrayStr.substring(index);
		                } else {
		                    s = arrayStr.substring(index, (index+size));
		                }
		                // 结尾长度默认为字符串长度
		                let endIndex = s.length;
		                // 因为下次开始的第一个字符可能会是逗号，所以下次开始需要+1
		                let startNeedAdd = 1;
		                // 判断最后一个字符是否为双引号
		                if (s.endsWith("\"")) {
		                    // 判断倒数第二个是否为逗号
		                    if (s.endsWith("\",\"")) {
		                        // 删除两个字符
		                        endIndex-=2;
		                    } else if (!s.startsWith("\"")) {
		                        // 如果开头不是引号，需要补一个引号，这就导致会超长，所以结尾就要找指定字符的
		                        // 找出最后一个指定字符的位置
		                        let la = s.lastIndexOf("\",\"");
		                        endIndex = la + 1;
		                    }
		                } else if (s.endsWith("\",")) {
		                    // 判断是否为逗号，是的话删除一个字符
		                    endIndex-=1;
		                } else {
		                    // 都不是，那就是内容结尾
		                    // 找出最后一个指定字符的位置
		                    let la = s.lastIndexOf("\",\"");
		                    endIndex = la + 1;
		                    // 内容超长，endIndex就会变成0，这时需要手动赋值
		                    if (endIndex <= 0) {
		                        // 看看是否以引号开头，如果不是，需要拼两个引号
		                        if (s.startsWith("\"")) {
		                            // 拼一个引号，-1
		                            endIndex = s.length - 1;
		                        } else {
		                            // 拼两个引号，-2
		                            endIndex = s.length - 2;
		                        }
		                        if (!s.endsWith("\"")) {
		                            // 开始不是逗号了，不能-1
		                            startNeedAdd = 0;
		                        }
		                    }
		                }
		                // 根据处理的结尾长度进行第二次拆分
		                let s2 = "";
		                if (endIndex - s.length > 0 || endIndex - 0 == 0) {
		                    s2 = s;
		                    endIndex = endIndex + s2.length;
		                } else {
		                    s2 = s.substring(0, endIndex);
		                }
		                if (!s2.startsWith("\"") && !s2.startsWith(",\"")) {
		                    // 拼一个引号
		                    s2 = "\"" + s2;
		                }
		                if (!s2.endsWith("\"")) {
		                    // 拼一个引号
		                    s2 = s2 +"\"";
		                }
		                // 计算下次循环开始的长度
		                index += (endIndex + startNeedAdd);
		                // 加到list
		                s2 = "[" + s2 + "]";
		                try {
		                    list.push(JSON.parse(s2));
		                } catch (e) {
		                    // 遇到错误，略过一个字符
		                    index = index - (endIndex + startNeedAdd) + 1;
		                }
		            }
		        }
		    }
			// 设置了maxSize，进行处理
			if (maxSize && maxSize > 0) {
				list = translate.util._splitMaxSize(list, orgsize, maxSize);
			}
		    return list;
		},
		/**
		 * 针对split函数中maxSize的处理
		 * 	private
		 * @param array 已拆分的二维数组
		 * @param size 拆分的长度
		 * @param maxSize 元素数量
		 * @author 刘晓腾
		 */
		_splitMaxSize:function(array, size, maxSize) {
			// console.log("------ splitMaxSize run ------")

			// 返回的数据
			let list = [];
			// 暂存的数组，用来存储每次遍历时超出的数据
			let tmp = [];

		 	// 遍历二维数组
			array.forEach(function(arr, index) {
				// 累加数组
				arr = tmp.concat(arr);
				// 计算元素数量
				let length = arr.length;
				// 数组中元素数量大于maxSize，对多余的元素进行移除
				if (length > maxSize) {
					// 第一个数组，包含前N个元素
					let firstArray = arr.slice(0, maxSize);
					// 第二个数组，包含剩下的元素
					let secondArray = arr.slice(maxSize);

					// 处理长度
					let len = 1;
					while (JSON.stringify(firstArray).length > size) {
						// 长度超过限制，进行处理
						firstArray = arr.slice(0, maxSize - len);
						secondArray = arr.slice(maxSize - len);
						len++;
						if (len >= arr.length+1) {
							break;
						}
					}

					// 第一个数组记录
					list.push(firstArray);
					// 第二个数组暂存
					tmp.length = 0;
					tmp = secondArray;
				} else {
					// 没超，只处理长度
					// 处理长度
					let firstArray = arr;
					let secondArray = [];
					let len = 1;
					while (JSON.stringify(firstArray).length > size) {
						// 长度超过限制，进行处理
						firstArray = arr.slice(0, maxSize - len);
						secondArray = arr.slice(maxSize - len);
						len++;
						if (len >= arr.length+1) {
							break;
						}
					}

					// 第一个数组记录
					list.push(firstArray);
					// 第二个数组暂存
					tmp.length = 0;
					tmp = secondArray;
				}

			});

			// 临时数组中还有元素，也要进行处理
			if (tmp.length > 0) {
				let tmpl = [];
				tmpl.push(tmp);
				// 递归处理
				let l = translate.util._splitMaxSize(tmpl, size, maxSize);
				list = list.concat(l);
			}

			return list;
		},
		/* 
			浏览器的语种标识跟translate.js的语种标识的对应
			key: 浏览器的语种标识
			value: translate.js 的语种标识
		 */
		browserLanguage:{
			'zh':'chinese_simplified',
			'zh-CN':'chinese_simplified',
			'zh-TW':'chinese_traditional',
			'zh-HK':'chinese_traditional',
			'co':'corsican',
			'gn':'guarani',
			'rw':'kinyarwanda',
			'ha':'hausa',
			'no':'norwegian',
			'nl':'dutch',
			'yo':'yoruba',
			'en':'english',
			'en-US':'english',
			'kok':'gongen',
			'la':'latin',
			'ne':'nepali',
			'fr':'french',
			'cs':'czech',
			'haw':'hawaiian',
			'ka':'georgian',
			'ru':'russian',
			'fa':'persian',
			'bho':'bhojpuri',
			'hi':'hindi',
			'be':'belarusian',
			'sw':'swahili',
			'is':'icelandic',
			'yi':'yiddish',
			'tw':'twi',
			'ga':'irish',
			'gu':'gujarati',
			'km':'khmer',
			'sk':'slovak',
			'he':'hebrew',
			'kn':'kannada',
			'hu':'hungarian',
			'ta':'tamil',
			'ar':'arabic',
			'bn':'bengali',
			'az':'azerbaijani',
			'sm':'samoan',
			'af':'afrikaans',
			'id':'indonesian',
			'da':'danish',
			'sn':'shona',
			'bm':'bambara',
			'lt':'lithuanian',
			'vi':'vietnamese',
			'mt':'maltese',
			'tk':'turkmen',
			'as':'assamese',
			'ca':'catalan',
			'si':'singapore',
			'ceb':'cebuano',
			'gd':'scottish-gaelic',
			'sa':'sanskrit',
			'pl':'polish',
			'gl':'galician',
			'lv':'latvian',
			'uk':'ukrainian',
			'tt':'tatar',
			'cy':'welsh',
			'ja':'japanese',
			'fil':'filipino',
			'ay':'aymara',
			'lo':'lao',
			'te':'telugu',
			'ro':'romanian',
			'ht':'haitian_creole',
			'doi':'dogrid',
			'sv':'swedish',
			'mai':'maithili',
			'th':'thai',
			'hy':'armenian',
			'my':'burmese',
			'ps':'pashto',
			'hmn':'hmong',
			'dv':'dhivehi',
			'lb':'luxembourgish',
			'sd':'sindhi',
			'ku':'kurdish',
			'tr':'turkish',
			'mk':'macedonian',
			'bg':'bulgarian',
			'ms':'malay',
			'lg':'luganda',
			'mr':'marathi',
			'et':'estonian',
			'ml':'malayalam',
			'de':'deutsch',
			'sl':'slovene',
			'ur':'urdu',
			'pt':'portuguese',
			'ig':'igbo',
			'ckb':'kurdish_sorani',
			'om':'oromo',
			'el':'greek',
			'es':'spanish',
			'fy':'frisian',
			'so':'somali',
			'am':'amharic',
			'ny':'nyanja',
			'pa':'punjabi',
			'eu':'basque',
			'it':'italian',
			'sq':'albanian',
			'ko':'korean',
			'tg':'tajik',
			'fi':'finnish',
			'ky':'kyrgyz',
			'ee':'ewe',
			'hr':'croatian',
			'kri':'creole',
			'qu':'quechua',
			'bs':'bosnian',
			'mi':'maori'
		},
		/*
			获取浏览器中设置的默认使用语言
			返回的是 translate.js 的语言唯一标识
			如果返回的是空字符串，则是没有匹配到（可能是没有获取到本地语言，也可能是本地语言跟translate.js 翻译通道没有对应上）
		*/
		browserDefaultLanguage:function(){
			var language = navigator.language || navigator.userLanguage;
			if(typeof(language) === 'string' && language.length > 0){
				var tLang = translate.util.browserLanguage[language];
				if(typeof(tLang) == 'undefined'){
					//没有在里面
					translate.log('browser default language : '+language +', translate.js current translate channel not support this language ');
				}else{
					return tLang;
				}
			}
			
			//将其转化为  translate.js 的语言id，比如简体中文是 chinese_simplified 、 英语是 english
			return '';
		},
		/*
			对输入的文本 text 进行判断，判断它里面是否有url存在。如果有url存在，对其进行截取，将url跟非url进行截取处理。
			比如传入 “这个示例：https://www.ungm.org/Public/Notice/261001，其他得示例是 http://api.translate.zvo.cn 我呢”
			那么返回的截取结果为：
			{
				"https://www.ungm.org/Public/Notice/261001":"1",
				"http://api.translate.zvo.cn":"1",
				"，其他得示例是 ":"0",
				"这个示例：":"0"
				" 我呢":"0"
			}
			其中的key 为截取的文本，value 的值是1或0， 1代表当前key的文本是网址，0则不是网址  
		*/
		urlSplitByText:function(text){
			// 匹配 http/https 的 URL 正则表达式（包含常见 URL 符号，排除中文等非 ASCII 字符）
			const urlRegex = /(https?:\/\/[\w\-._~:\/?#[\]@!$&'()*+;=%]+(?=[\s\u4e00-\u9fa5，。；,!?]|$))/gi;

			// 使用正则表达式分割文本，保留URL
			const parts = text.split(urlRegex);

			// 结果对象
			let result = {};

			// 添加非URL部分，并标记为 0
			for (let i = 0; i < parts.length; i++) {
				if (i % 2 === 0) {
					// 非URL部分
					if (parts[i] !== "") {
						result[parts[i]] = "0";
					}
				} else {
					// URL部分
					result[parts[i]] = "1";
				}
			}

			return result;
		},

		/*js translate.util.getElementPosition start*/
		/*
			计算一个元素在浏览器中的坐标系，其绝对定位、以及实际显示出来所占用的区域，宽、高
		*/
		getElementPosition:function (node) {
			// 获取元素的边界矩形信息（相对于视口）
			const rect = node.getBoundingClientRect();

			// 获取当前页面的滚动位置（兼容不同浏览器）
			const scrollX = window.scrollX || document.documentElement.scrollLeft;
			const scrollY = window.scrollY || document.documentElement.scrollTop;

			// 计算元素在文档中的起始坐标
			const startX = rect.left + scrollX;
			const startY = rect.top + scrollY;

			// 计算元素的宽度和高度
			const width = rect.right - rect.left;
			const height = rect.bottom - rect.top;

			// 计算元素在文档中的结束坐标
			const endX = startX + width;
			const endY = startY + height;

			// 返回包含所有信息的对象（使用ES5兼容语法）
		    return {
		        startX: startX,
		        startY: startY,
		        endX: endX,
		        endY: endY,
		        width: width,
		        height: height
		    };
		},
		/*js translate.util.getElementPosition end*/

		/*js translate.util.compareStringsIgnoringNumbers start*/
		/*	
			比较两个字符串，是否除了数字之外，其他的完全一致。
			实测 i5 双核 2.4G ，计算1亿次 - 7s

			["abc123def", "abc456def", true],
	        ["hello7world", "hello8world", true],
	        ["test123", "test", true],
	        ["123test", "test", true],
	        ["abc", "def", false],
	        ["a1b2c3", "a4b5c", false],
	        ["", "", true],
	        ["123", "456", true],
	        ["你好123世界", "3你好1世界", true],
	        ["你好123世界", "你好世界4", true],
		*/
		compareStringsIgnoringNumbers: function(a, b) {
		    let i = 0, j = 0;
		    const lenA = a.length, lenB = b.length;
		    
		    while (i < lenA || j < lenB) {
		        // 跳过a中的数字 (0-9的ASCII码是48-57)
		        while (i < lenA && a.charCodeAt(i) >= 48 && a.charCodeAt(i) <= 57) {
		            i++;
		        }
		        
		        // 跳过b中的数字
		        while (j < lenB && b.charCodeAt(j) >= 48 && b.charCodeAt(j) <= 57) {
		            j++;
		        }
		        
		        // 检查是否有一个字符串还有非数字字符而另一个已经结束
		        if ((i < lenA) !== (j < lenB)) {
		            return false;
		        }
		        
		        // 如果都结束了，返回true
		        if (i >= lenA && j >= lenB) {
		            return true;
		        }
		        
		        // 比较当前非数字字符
		        if (a[i] !== b[j]) {
		            return false;
		        }
		        
		        i++;
		        j++;
		    }
		    
		    return true;
		}
		/*js translate.util.compareStringsIgnoringNumbers end*/

	},
	//机器翻译采用哪种翻译服务
	service:{  
		/*
			name填写的值,参考 translate.service.use 的注释
		*/
		name:'translate.service',  

		/*js translate.service.use start*/
		/*
			其实就是设置 translate.service.name
			可以设置为：

			translate.service 自行部署的translate.service 翻译API服务，部署参考： https://translate.zvo.cn/391129.html
			client.edge 使用无服务器的翻译,有edge浏览器接口提供翻译服务
			siliconflow 使用指点云提供的服务器、硅基流动提供的AI算力进行大模型翻译
			giteeAI 使用 giteeAI ， 亚洲、美洲、欧洲 网络节点覆盖
	
		*/
		use: function(serviceName){
			if(typeof(translate.enterprise) != 'undefined' && translate.enterprise.isUse == true){
				translate.log('您已启用了企业级翻译通道 translate.enterprise.use(); (文档：https://translate.zvo.cn/4087.html) , 所以您设置的 translate.service.use(\''+serviceName+'\'); (文档：https://translate.zvo.cn/4081.html) 将失效不起作用，有企业级翻译通道全部接管。');
				return;
			}
			//console.log('--'+serviceName);
			if(typeof(serviceName) == 'string'){
				translate.service.name = serviceName;
				if(serviceName != 'translate.service'){
					//增加元素整体翻译能力
					translate.whole.enableAll();

					if(serviceName.toLowerCase() == 'giteeai'){
						//设定翻译接口为GiteeAI的
						translate.request.api.host=['https://giteeai.zvo.cn/','https://deutsch.enterprise.api.translate.zvo.cn:1000/','https://api.translate.zvo.cn:1000/', 'https://america.api.translate.zvo.cn:1000/'];
						return;
					}
					if(serviceName.toLowerCase() == 'siliconflow'){
						//设定翻译接口为硅基流动的
						translate.request.api.host=['https://siliconflow.zvo.cn/','https://america.api.translate.zvo.cn:1414/','https://deutsch.enterprise.api.translate.zvo.cn:1414/'];
						return;
					}
				}
			}
		},
		/*js translate.service.use end*/

		/*js translate.service.edge start*/
		//客户端方式的edge提供机器翻译服务
		edge:{
			api:{ //edge浏览器的翻译功能
				translate:'https://edge.microsoft.com/translate/translatetext?from={from}&to={to}&isEnterpriseClient=false' //翻译接口
			},
			language:{

				json:[{"id":"ukrainian","name":"Україна","serviceId":"uk"},{"id":"norwegian","name":"Norge","serviceId":"no"},{"id":"welsh","name":"Iaith Weleg","serviceId":"cy"},{"id":"dutch","name":"nederlands","serviceId":"nl"},{"id":"japanese","name":"日本語","serviceId":"ja"},{"id":"filipino","name":"Pilipino","serviceId":"fil"},{"id":"english","name":"English","serviceId":"en"},{"id":"lao","name":"ກະຣຸນາ","serviceId":"lo"},{"id":"telugu","name":"తెలుగుName","serviceId":"te"},{"id":"romanian","name":"Română","serviceId":"ro"},{"id":"nepali","name":"नेपालीName","serviceId":"ne"},{"id":"french","name":"Français","serviceId":"fr"},{"id":"haitian_creole","name":"Kreyòl ayisyen","serviceId":"ht"},{"id":"czech","name":"český","serviceId":"cs"},{"id":"swedish","name":"Svenska","serviceId":"sv"},{"id":"russian","name":"Русский язык","serviceId":"ru"},{"id":"malagasy","name":"Malagasy","serviceId":"mg"},{"id":"burmese","name":"ဗာရမ်","serviceId":"my"},{"id":"pashto","name":"پښتوName","serviceId":"ps"},{"id":"thai","name":"คนไทย","serviceId":"th"},{"id":"armenian","name":"Արմենյան","serviceId":"hy"},{"id":"chinese_simplified","name":"简体中文","serviceId":"zh-CHS"},{"id":"persian","name":"Persian","serviceId":"fa"},{"id":"chinese_traditional","name":"繁體中文","serviceId":"zh-CHT"},{"id":"kurdish","name":"Kurdî","serviceId":"ku"},{"id":"turkish","name":"Türkçe","serviceId":"tr"},{"id":"hindi","name":"हिन्दी","serviceId":"hi"},{"id":"bulgarian","name":"български","serviceId":"bg"},{"id":"malay","name":"Malay","serviceId":"ms"},{"id":"swahili","name":"Kiswahili","serviceId":"sw"},{"id":"oriya","name":"ଓଡିଆ","serviceId":"or"},{"id":"icelandic","name":"ÍslandName","serviceId":"is"},{"id":"irish","name":"Íris","serviceId":"ga"},{"id":"khmer","name":"ភាសា​ខ្មែរName","serviceId":"km"},{"id":"gujarati","name":"ગુજરાતી","serviceId":"gu"},{"id":"slovak","name":"Slovenská","serviceId":"sk"},{"id":"kannada","name":"ಕನ್ನಡ್Name","serviceId":"kn"},{"id":"hebrew","name":"היברית","serviceId":"he"},{"id":"hungarian","name":"magyar","serviceId":"hu"},{"id":"marathi","name":"मराठीName","serviceId":"mr"},{"id":"tamil","name":"தாமில்","serviceId":"ta"},{"id":"estonian","name":"eesti keel","serviceId":"et"},{"id":"malayalam","name":"മലമാലം","serviceId":"ml"},{"id":"inuktitut","name":"ᐃᓄᒃᑎᑐᑦ","serviceId":"iu"},{"id":"arabic","name":"بالعربية","serviceId":"ar"},{"id":"deutsch","name":"Deutsch","serviceId":"de"},{"id":"slovene","name":"slovenščina","serviceId":"sl"},{"id":"bengali","name":"বেঙ্গালী","serviceId":"bn"},{"id":"urdu","name":"اوردو","serviceId":"ur"},{"id":"azerbaijani","name":"azerbaijani","serviceId":"az"},{"id":"portuguese","name":"português","serviceId":"pt"},{"id":"samoan","name":"lifiava","serviceId":"sm"},{"id":"afrikaans","name":"afrikaans","serviceId":"af"},{"id":"tongan","name":"汤加语","serviceId":"to"},{"id":"greek","name":"ελληνικά","serviceId":"el"},{"id":"indonesian","name":"IndonesiaName","serviceId":"id"},{"id":"spanish","name":"Español","serviceId":"es"},{"id":"danish","name":"dansk","serviceId":"da"},{"id":"amharic","name":"amharic","serviceId":"am"},{"id":"punjabi","name":"ਪੰਜਾਬੀName","serviceId":"pa"},{"id":"albanian","name":"albanian","serviceId":"sq"},{"id":"lithuanian","name":"Lietuva","serviceId":"lt"},{"id":"italian","name":"italiano","serviceId":"it"},{"id":"vietnamese","name":"Tiếng Việt","serviceId":"vi"},{"id":"korean","name":"한국어","serviceId":"ko"},{"id":"maltese","name":"Malti","serviceId":"mt"},{"id":"finnish","name":"suomi","serviceId":"fi"},{"id":"catalan","name":"català","serviceId":"ca"},{"id":"croatian","name":"hrvatski","serviceId":"hr"},{"id":"bosnian","name":"bosnian","serviceId":"bs-Latn"},{"id":"polish","name":"Polski","serviceId":"pl"},{"id":"latvian","name":"latviešu","serviceId":"lv"},{"id":"maori","name":"Maori","serviceId":"mi"}],
				/*
					获取map形式的语言列表 
					key为 translate.service 的 name  
					value为serviceId

				*/
				getMap:function(){
					if(typeof(translate.service.edge.language.map) == 'undefined'){
						translate.service.edge.language.map = new Array();
						for(var i = 0; i < translate.service.edge.language.json.length; i++){
							var item = translate.service.edge.language.json[i];
							translate.service.edge.language.map[item.id] = item.serviceId;
						}
					}
					return translate.service.edge.language.map;
				}
			},
			/**
			 * edge 进行翻译。 这个传入参数跟 translate.request.post 是一样的
			 * @param path 请求的path（path，传入的是translate.request.api.translate 这种的，需要使用 getUrl 来组合真正请求的url ）
			 * @param data 请求的参数数据
			 * @param func 请求完成的回调，传入如 function(data){ console.log(data); }
			 */
			translate:function(path, data, func, abnormalFunc){
				var textArray = JSON.parse(decodeURIComponent(data.text));
				var translateTextArray = translate.util.split(textArray, 40000, 900);


				var appendXhrData = {
					"from":data.from+'',
					"to":data.to,
					"text":data.text
				};
				var from = data.from;
				if(from != 'auto'){
					if(from == 'romance'){
						//这里额外加了一个罗曼语族(romance)会自动认为是法语(fr)
						from = 'fr';
					}else{
						from = translate.service.edge.language.getMap()[data.from];
					}
				}
				
				var to = translate.service.edge.language.getMap()[data.to];
				var transUrl = translate.service.edge.api.translate.replace('{from}',from).replace('{to}',to);

				//如果翻译量大，要拆分成多次翻译请求
				for(var tai = 0; tai<translateTextArray.length; tai++){
					/*
					var json = [];
					for(var i = 0; i<translateTextArray[tai].length; i++){
						json.push({"Text":translateTextArray[tai][i]});
					}
					*/

					(function(chunkIndex){
						translate.request.send(transUrl, JSON.stringify(translateTextArray[chunkIndex]), appendXhrData, function(result){
						var d = {};
						d.info = 'SUCCESS';
						d.result = 1;
						d.from = data.from;
						d.to = data.to;
						d.text = [];
						for(var t = 0; t < result.length; t++){
							d.text.push(result[t].translations[0].text);
						}
						

						//判断当前翻译是否又被拆分过，比如一次超过5万字符的话就要拆分成多次请求了
						if(translateTextArray.length > 1){
							//这一次翻译呗拆分了多次请求，那么要进行补全数组，使数组个数能一致

							//使用当前分片下标，不再根据返回的长度猜测属于哪个分片
							var currentIndex = chunkIndex;	//当前翻译请求属于被拆分的第几个的数组下标，从0开始的

							//进行对前后进行补齐数组
							if(currentIndex < 0){
								translate.log('------ERROR--------');
								translate.log('翻译内容过多，进行拆分，但拆分判断出现异常，currentIndex：-1 请联系 http://translate.zvo.cn/43006.html 说明');
							}
							//前插入空数组填充
							for(var addbeforei = 0; addbeforei<currentIndex; addbeforei++){
								var beforeItemArrayLength = translateTextArray[addbeforei].length;
								//console.log('beforeItemArrayLength:'+beforeItemArrayLength);
								for(var bi = 0; bi < beforeItemArrayLength; bi++){
									d.text.unshift(null);
								}
							}
							//后插入空数组填充
							for(var addafteri = translateTextArray.length-1; addafteri>currentIndex; addafteri--){
								var afterItemArrayLength = translateTextArray[addafteri].length;
								for(var bi = 0; bi < afterItemArrayLength; bi++){
									d.text.push(null);
								}
							}
						
						}
						
						func(d);
					}, 'post', true, {
						'Content-Type':'application/json'
					}, abnormalFunc, true);
					})(tai);
					

				}


				
				

				
			}
		}
		/*js translate.service.edge end*/
	},
	//request请求来源于 https://github.com/xnx3/request
	request:{
		/* 
			将通过翻译接口进行翻译请求(/translate.json)的信息记录到 translate.js 本身中
			key uuid 每次 translate.execute() 触发生成的uuid
			value 	
				time: 触发后加入到 data 中的时间,13位时间戳
				list: 对象集合，translate.execute() 的触发会发起多次翻译请求，根据识别的语种不同，发起多次网络请求，这里记录的是多次网络请求
					[
						'english':{						//当前请求是将什么语种进行翻译，也就是 translate.json 请求中的 from 参数
							to: chinese_simplified,		//当前请求要翻译为什么语种，也就是 translate.json 请求中的 to 参数
							texts:['你好', '世界', ...],	//当前请求要进行翻译的具体文本
							nodes:[node1, node2, ...]	//当前请求要翻译的文本所在的node集合，也就是有哪些node中的文本参与了 通过API接口进行翻译文本
						},
						'korean':{
							...
						},
						...
					]

			后面要将 translate.translateRequest  合并到这里面		
		*/
		data:{},
		//相关API接口方面
		api:{
			/**
			 * 翻译接口请求的域名主机 host
			 * 格式注意前面要带上协议如 https:// 域名后要加 /
			 * v2.8.2 增加数组形态，如 ['https://api.translate.zvo.cn/','xxxxx'] 
			 */
			//host:'https://api.translate.zvo.cn/',
			host:['https://api.translate.zvo.cn/','https://america.api.translate.zvo.cn/'],
			//host的备用接口，格式同host，可以填写多个，只不过这里是数组格式。只有当主 host 无法连通时，才会采用备host来提供访问。如果为空也就是 [] 则是不采用备方案。
			//backupHost:['',''],
			language:'language.json', //获取支持的语种列表接口
			translate:'translate.json', //翻译接口
			ip:'', //根据用户当前ip获取其所在地的语种 ，原本的值为 ip.json ，v4版本已废弃。 如果启用，可手动设置此值为 ip.json ,注意，需服务端的 ip.json 开启此能力
			connectTest:'connectTest.json',	//用于 translate.js 多节点翻译自动检测网络连通情况
			init:'init.json', //获取最新版本号，跟当前版本进行比对，用于提醒版本升级等使用

		},
		// translate.json 的 SSE 流式响应能力。默认关闭，开启后仍然保留 XHR JSON 降级路径。
		sse:{
			use:false,
			/**
			 * 所有 SSE 事件的统一旁路回调。
			 * <p>当前步骤先建立协议读取能力，不在这里直接改 DOM 渲染主流程。后续接入渐进渲染时，
			 * 可以在 translate.execute() 的翻译请求上下文中消费 batch/item。</p>
			 */
			onEvent:null,
			onBatch:null,
			onItem:null,
			onDone:null,
			onError:null,
			start:function(){
				translate.request.sse.use = true;
			},
			stop:function(){
				translate.request.sse.use = false;
			},
			/**
			 * 判断当前浏览器是否具备 POST SSE 所需的基础能力。
			 * <p>EventSource 只适合 GET，不适合当前 translate.json 的 POST 表单请求；这里必须依赖
			 * fetch + ReadableStream 主动读取 text/event-stream。</p>
			 * <p>这里仅做浏览器能力的同步判断，不判断服务端是否真的返回 text/event-stream；
			 * 服务端响应类型仍由 translate.request.sse.post() 收到 response 后再兜底校验。</p>
			 */
			isSupport:function(){
				if(typeof(window) == 'undefined'
					|| typeof(window.fetch) != 'function'
					|| typeof(window.TextDecoder) != 'function'
					|| typeof(window.Promise) != 'function'
					|| typeof(window.Response) != 'function'){
					return false;
				}
				try{
					var response = new window.Response('');
					return response.body != null && typeof(response.body.getReader) == 'function';
				}catch(e){
					return false;
				}
			},
			/**
			 * 解析一段完整的 SSE 事件文本块。
			 *
			 * @param block 不包含空行分隔符的 SSE 文本块
			 * @return {name, data, dataText, dispatch}
			 */
			parseEventBlock:function(block){
				var eventName = 'message';
				var dataLines = [];
				var hasDataField = false;
				var lines = block.split('\n');
				for(var i = 0; i < lines.length; i++){
					var line = lines[i];
					if(line.length == 0){
						continue;
					}
					if(line.indexOf(':') == 0){
						// SSE 允许服务端发送 ": xxx" 注释行作为心跳；注释不属于业务事件，必须忽略。
						continue;
					}
					var separatorIndex = line.indexOf(':');
					var field = line;
					var value = '';
					if(separatorIndex > -1){
						field = line.substring(0, separatorIndex);
						value = line.substring(separatorIndex+1);
						if(value.indexOf(' ') == 0){
							// SSE 规范只剥离冒号后的一个前导空格，避免破坏 data 正文中的有效空格。
							value = value.substring(1);
						}
					}
					if(field == 'event'){
						eventName = value;
					}else if(field == 'data'){
						hasDataField = true;
						dataLines.push(value);
					}
				}
				var dataText = dataLines.join('\n');
				var data = dataText;
				if(dataText.length > 0){
					try{
						data = JSON.parse(dataText);
					}catch(e){
						// data 不一定必须是 JSON，解析失败时保留原始字符串，避免因为服务端扩展事件导致流被中断。
						data = dataText;
					}
				}
				return {
					// event: 为空时按 SSE 规范回落为 message，避免空事件名阻断统一 onEvent 监听。
					name:eventName.length > 0 ? eventName : 'message',
					data:data,
					dataText:dataText,
					// 只有包含 data 字段的事件才应该派发；纯心跳、id、retry 等控制块不能触发业务回调。
					dispatch:hasDataField
				};
			},
			/**
			 * 触发 SSE 事件旁路回调。
			 * <p>这些回调不能影响主请求结果；回调异常只记录日志，不中断后续 done/error 处理。</p>
			 */
			triggerEvent:function(eventName, eventData, requestData, sseCallbacks){
				var runCallback = function(owner, callback, args, callbackName){
					if(typeof(callback) != 'function'){
						return;
					}
					try{
						// 每个回调单独捕获异常，避免某个监听失败后阻断同一事件的后续监听。
						callback.apply(owner, args);
					}catch(e){
						translate.log('translate.request.sse '+callbackName+' callback error: '+e.message);
					}
				};
				// 单次请求回调用于 translate.execute() 这种带有请求上下文的消费场景。
				// 这里不复用全局 onBatch/onItem，避免多个翻译请求并发时互相覆盖回调状态。
				if(typeof(sseCallbacks) == 'object' && sseCallbacks != null){
					runCallback(sseCallbacks, sseCallbacks.onEvent, [eventName, eventData, requestData], 'onEvent');
					if(eventName == 'batch'){
						runCallback(sseCallbacks, sseCallbacks.onBatch, [eventData, requestData], 'onBatch');
					}else if(eventName == 'item'){
						runCallback(sseCallbacks, sseCallbacks.onItem, [eventData, requestData], 'onItem');
					}else if(eventName == 'done'){
						runCallback(sseCallbacks, sseCallbacks.onDone, [eventData, requestData], 'onDone');
					}else if(eventName == 'error'){
						runCallback(sseCallbacks, sseCallbacks.onError, [eventData, requestData], 'onError');
					}
				}
				runCallback(translate.request.sse, translate.request.sse.onEvent, [eventName, eventData, requestData], 'global onEvent');
				if(eventName == 'batch'){
					runCallback(translate.request.sse, translate.request.sse.onBatch, [eventData, requestData], 'global onBatch');
				}else if(eventName == 'item'){
					runCallback(translate.request.sse, translate.request.sse.onItem, [eventData, requestData], 'global onItem');
				}else if(eventName == 'done'){
					runCallback(translate.request.sse, translate.request.sse.onDone, [eventData, requestData], 'global onDone');
				}else if(eventName == 'error'){
					runCallback(translate.request.sse, translate.request.sse.onError, [eventData, requestData], 'global onError');
				}
			},
			/**
			 * 收集本次 SSE 安全增量渲染后，可以提前移除翻译进度遮罩的元素。
			 * <p>这里不直接改 DOM，只根据当前 translate.execute 的临时状态做输入输出，方便后续排查。
			 * 如果判断异常，只记录日志并返回空数组，剩余遮罩仍会在最终 translateNetworkAfter 中统一清理。</p>
			 *
			 * @param state 当前 translate.execute 闭包内的 SSE 进度状态
			 * @param data {uuid, fanyiLangs, translateHashArray, renderLang, renderedIndexes}
			 * @return 可以安全取消遮罩的元素数组
			 */
			collectSafeProgressElements:function(state, data){
				var logPrefix = 'translate.request.sse.collectSafeProgressElements';
				try{
					if(translate.progress.api.use !== true || translate.progress.api.isTip !== true){
						return [];
					}
					if(typeof(state) != 'object' || state == null){
						translate.log(logPrefix+' 参数异常：state 不是对象');
						return [];
					}
					if(typeof(data) != 'object' || data == null){
						translate.log(logPrefix+' 参数异常：data 不是对象');
						return [];
					}
					if(typeof(data.uuid) == 'undefined' || data.uuid == null){
						translate.log(logPrefix+' 参数异常：uuid 为空');
						return [];
					}
					if(typeof(data.renderLang) != 'string' || data.renderLang.length < 1){
						translate.log(logPrefix+' 参数异常：renderLang 为空');
						return [];
					}
					if(typeof(data.renderedIndexes) != 'object' || data.renderedIndexes == null || typeof(data.renderedIndexes.length) != 'number'){
						translate.log(logPrefix+' 参数异常：renderedIndexes 不是数组');
						return [];
					}
					if(typeof(data.fanyiLangs) != 'object' || data.fanyiLangs == null || typeof(data.fanyiLangs.length) != 'number'){
						translate.log(logPrefix+' 参数异常：fanyiLangs 不是数组');
						return [];
					}
					if(typeof(data.translateHashArray) != 'object' || data.translateHashArray == null){
						translate.log(logPrefix+' 参数异常：translateHashArray 不是对象');
						return [];
					}
					if(typeof(data.translateHashArray[data.renderLang]) == 'undefined'){
						translate.log(logPrefix+' 数据异常：translateHashArray 中不存在 renderLang '+data.renderLang);
						return [];
					}
					if(typeof(translate.nodeQueue[data.uuid]) == 'undefined' || translate.nodeQueue[data.uuid] == null || typeof(translate.nodeQueue[data.uuid]['list']) == 'undefined'){
						translate.log(logPrefix+' 数据异常：nodeQueue 中不存在 uuid '+data.uuid);
						return [];
					}

					var buildIndexElements = function(lang, itemIndex){
						var resultElements = [];
						var elementMap = new Map();
						if(typeof(data.translateHashArray[lang]) == 'undefined' || typeof(data.translateHashArray[lang][itemIndex]) == 'undefined'){
							translate.log(logPrefix+' 数据异常：未找到 translateHashArray，uuid:'+data.uuid+', lang:'+lang+', index:'+itemIndex);
							return resultElements;
						}
						var hash = data.translateHashArray[lang][itemIndex];
						if(typeof(translate.nodeQueue[data.uuid]['list'][lang]) == 'undefined'
							|| typeof(translate.nodeQueue[data.uuid]['list'][lang][hash]) == 'undefined'
							|| typeof(translate.nodeQueue[data.uuid]['list'][lang][hash].nodes) == 'undefined'){
							translate.log(logPrefix+' 数据异常：未找到渲染 index 对应的 nodeQueue，uuid:'+data.uuid+', lang:'+lang+', index:'+itemIndex);
							return resultElements;
						}
						var nodes = translate.nodeQueue[data.uuid]['list'][lang][hash].nodes;
						for(var nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++){
							if(typeof(nodes[nodeIndex]) != 'object' || nodes[nodeIndex] == null || typeof(nodes[nodeIndex].node) == 'undefined' || nodes[nodeIndex].node == null){
								continue;
							}
							var elements = translate.element.nodeToElement([nodes[nodeIndex].node]);
							for(var elementIndex = 0; elementIndex < elements.length; elementIndex++){
								elementMap.set(elements[elementIndex], elements[elementIndex]);
							}
						}
						for(let element of elementMap.keys()){
							resultElements.push(element);
						}
						return resultElements;
					};

					if(state.initialized !== true){
						state.elementPendingMap = new Map();
						state.indexElementMap = {};
						state.renderedIndexMap = {};
						for(var langIndex = 0; langIndex < data.fanyiLangs.length; langIndex++){
							var lang = data.fanyiLangs[langIndex];
							if(typeof(lang) != 'string' || lang.length < 1){
								continue;
							}
							if(typeof(data.translateHashArray[lang]) == 'undefined'){
								translate.log(logPrefix+' 数据异常：初始化时 translateHashArray 中不存在 lang '+lang);
								continue;
							}
							state.indexElementMap[lang] = [];
							for(var itemIndex = 0; itemIndex < data.translateHashArray[lang].length; itemIndex++){
								var indexElements = buildIndexElements(lang, itemIndex);
								state.indexElementMap[lang][itemIndex] = indexElements;
								for(var elementIndex = 0; elementIndex < indexElements.length; elementIndex++){
									var pending = state.elementPendingMap.get(indexElements[elementIndex]);
									state.elementPendingMap.set(indexElements[elementIndex], typeof(pending) == 'number' ? pending + 1 : 1);
								}
							}
						}
						state.initialized = true;
					}

					if(state.elementPendingMap == null || typeof(state.elementPendingMap.get) != 'function'){
						translate.log(logPrefix+' 状态异常：elementPendingMap 不存在');
						return [];
					}
					if(typeof(state.indexElementMap) != 'object' || state.indexElementMap == null){
						translate.log(logPrefix+' 状态异常：indexElementMap 不存在');
						return [];
					}
					if(typeof(state.renderedIndexMap) != 'object' || state.renderedIndexMap == null){
						state.renderedIndexMap = {};
					}

					var safeElementMap = new Map();
					var renderedIndexMap = {};
					for(var renderedIndex = 0; renderedIndex < data.renderedIndexes.length; renderedIndex++){
						var itemIndex = parseInt(data.renderedIndexes[renderedIndex], 10);
						if(isNaN(itemIndex) || itemIndex < 0){
							translate.log(logPrefix+' 参数异常：renderedIndexes 中存在非法 index，uuid:'+data.uuid+', lang:'+data.renderLang+', index:'+data.renderedIndexes[renderedIndex]);
							continue;
						}
						if(renderedIndexMap[itemIndex] === 1){
							continue;
						}
						renderedIndexMap[itemIndex] = 1;

						var renderedKey = data.renderLang+'_'+itemIndex;
						if(state.renderedIndexMap[renderedKey] === 1){
							continue;
						}
						state.renderedIndexMap[renderedKey] = 1;

						if(typeof(state.indexElementMap[data.renderLang]) == 'undefined' || typeof(state.indexElementMap[data.renderLang][itemIndex]) == 'undefined'){
							translate.log(logPrefix+' 状态异常：未找到 indexElementMap，uuid:'+data.uuid+', lang:'+data.renderLang+', index:'+itemIndex);
							continue;
						}
						var indexElements = state.indexElementMap[data.renderLang][itemIndex];
						for(var elementIndex = 0; elementIndex < indexElements.length; elementIndex++){
							var pending = state.elementPendingMap.get(indexElements[elementIndex]);
							if(typeof(pending) != 'number'){
								translate.log(logPrefix+' 状态异常：elementPendingMap 中未找到元素 pending，uuid:'+data.uuid+', lang:'+data.renderLang+', index:'+itemIndex);
								continue;
							}
							pending--;
							if(pending > 0){
								state.elementPendingMap.set(indexElements[elementIndex], pending);
							}else{
								state.elementPendingMap.delete(indexElements[elementIndex]);
								safeElementMap.set(indexElements[elementIndex], indexElements[elementIndex]);
							}
						}
					}

					var safeElements = [];
					for(let element of safeElementMap.keys()){
						safeElements.push(element);
					}
					return safeElements;
				}catch(e){
					translate.log(logPrefix+' 执行异常：'+e.message);
					return [];
				}
			},
			/**
			 * 使用 fetch + ReadableStream 发起 translate.json SSE POST 请求。
			 * <p>返回 true 表示请求已经由 SSE 接管；如果浏览器不支持流式读取会返回 false，让调用方继续走 XHR。
			 * 如果 fetch 在收到任何 SSE 事件前失败，会调用 fallbackFunc 降级到原 JSON 请求。</p>
			 * <p>如果服务端返回 200 但不是 text/event-stream，说明服务端按普通 translate.json 响应了；
			 * 此时直接消费当前响应，避免同一批大文本再发起一次 XHR 请求。</p>
			 */
			post:function(path, data, func, abnormalFunc, fallbackFunc, sseCallbacks){
				if(!translate.request.sse.isSupport()){
					return false;
				}

				var url = translate.request.getUrl(path);
				var params = translate.request.buildPostParams(data, {stream:'1'});
				var headers = translate.request.buildHeaders({
					'content-type':'application/x-www-form-urlencoded',
					'Accept':'text/event-stream'
				});
				var requestState = {
					data:data,
					requestURL:url,
					status:0,
					readyState:0,
					responseText:'',
					response:'',
					// SSE 没有原生 XMLHttpRequest 对象。这里仅标记当前是 SSE 最小兼容响应对象，
					// 供 translate.request.response(xhr) 的旧扩展代码识别来源，避免误认为它是完整 XHR。
					sse:true
				};
				var hasEvent = false;
				var finished = false;
				var fallbacked = false;
				var responseTriggered = false;
				var triggerResponse = function(responseData){
					if(responseTriggered){
						return;
					}
					responseTriggered = true;
					requestState.readyState = 4;
					if(typeof(responseData) != 'undefined'){
						try{
							// 保持与普通 translate.json 尽量接近：最终 done/error 的 data 作为响应正文。
							// batch/item 只是流式中间结果，不写入 responseText，也不触发 response 回调。
							requestState.responseText = typeof(responseData) == 'string' ? responseData : JSON.stringify(responseData);
						}catch(e){
							requestState.responseText = '';
						}
						requestState.response = requestState.responseText;
					}
					try{
						translate.request.response(requestState);
					}catch(e){
						// 用户自定义 response 回调不能影响 SSE 网络状态，否则 Promise catch 会误判为请求失败。
						translate.log('translate.request.response SSE callback error: '+e.message);
					}
				};
				var callFallback = function(){
					if(fallbacked){
						return;
					}
					fallbacked = true;
					if(typeof(fallbackFunc) == 'function'){
						fallbackFunc();
					}
				};
				var callAbnormalFunc = function(){
					if(typeof(abnormalFunc) != 'function'){
						return;
					}
					try{
						abnormalFunc(requestState);
					}catch(e){
						// abnormalFunc 是用户异常回调，它自身的异常应暴露给调用方，
						// 但不能再次进入 fetch/read 的 Promise catch 被包装成新的 SSE 网络异常。
						setTimeout(function(){
							throw e;
						}, 0);
					}
				};
				var callAbnormal = function(info){
					requestState.info = info;
					triggerResponse();
					callAbnormalFunc();
				};
				var callResponseFunc = function(args){
					try{
						func.apply(null, args);
					}catch(e){
						// func 是业务完成回调，异常应像 XHR onreadystatechange 中的回调异常一样暴露出去，
						// 但不能进入 fetch/read 的 Promise catch，否则会被误判为 SSE 网络失败并触发 abnormalFunc。
						setTimeout(function(){
							throw e;
						}, 0);
					}
				};
				var handleNormalResponse = function(response){
					return response.text().then(function(responseText){
						// 这里代表 translate.json 已经返回了完整普通响应，不再触发 fallback 重复请求。
						// 后续若用户回调自身抛错，也应按已收到业务响应处理，而不是误判为 fetch 失败后再发 XHR。
						hasEvent = true;
						triggerResponse(responseText);

						var json = null;
						if(typeof(responseText) == 'undefined' || responseText == null){
							// 与 XHR 旧逻辑保持一致：空响应不解析 JSON，直接把原始内容交给调用方。
						}else{
							if(responseText.indexOf('{') > -1 && responseText.indexOf('}') > -1){
								try{
									json = JSON.parse(responseText);
								}catch(e){
									translate.log(e);
								}
							}
						}

						if(json === null){
							callResponseFunc([responseText]);
						}else{
							callResponseFunc([json, data, requestState]);
						}
					});
				};

				window.fetch(url, {
					method:'POST',
					headers:headers,
					body:params
				}).then(function(response){
					requestState.status = response.status;
					if(response.status != 200){
						if(!hasEvent){
							callFallback();
							return null;
						}
						callAbnormal('HTTP response code : '+response.status+', url: '+url);
						return null;
					}
					var contentType = '';
					if(response.headers != null && typeof(response.headers.get) == 'function'){
						contentType = response.headers.get('content-type') || '';
					}
					if(contentType.toLowerCase().indexOf('text/event-stream') < 0){
						return handleNormalResponse(response);
					}
					if(typeof(response.body) == 'undefined' || response.body == null || typeof(response.body.getReader) != 'function'){
						callFallback();
						return null;
					}

					var reader = response.body.getReader();
					var decoder = new window.TextDecoder('utf-8');
					var buffer = '';
					var handleBlock = function(block){
						if(block == null || block.length < 1){
							return;
						}
						var event = translate.request.sse.parseEventBlock(block);
						if(event.dispatch !== true){
							// 注释心跳、id、retry 等 SSE 控制块不代表服务端已经返回业务数据，
							// 不能把 hasEvent 提前置为 true，否则后续断流时会阻断原 JSON 请求降级。
							return;
						}
						hasEvent = true;
						if(event.name == 'done'){
							finished = true;
							requestState.sseEventName = event.name;
							triggerResponse(event.data);
						}else if(event.name == 'error'){
							finished = true;
							requestState.sseEventName = event.name;
							triggerResponse(event.data);
						}
						translate.request.sse.triggerEvent(event.name, event.data, data, sseCallbacks);
						if(event.name == 'done'){
							callResponseFunc([event.data, data, requestState]);
						}else if(event.name == 'error'){
							callResponseFunc([event.data, data, requestState]);
						}
					};
					var read = function(){
						return reader.read().then(function(result){
							if(result.done){
								buffer = buffer + decoder.decode();
								buffer = buffer.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
								if(buffer.length > 0){
									handleBlock(buffer);
									buffer = '';
								}
								if(!finished){
									if(!hasEvent){
										callFallback();
									}else{
										callAbnormal('SSE connection finished before done event. url: '+url);
									}
								}
								return;
							}
							buffer = buffer + decoder.decode(result.value, {stream:true});
							buffer = buffer.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
							var splitIndex = buffer.indexOf('\n\n');
							while(splitIndex > -1){
								var block = buffer.substring(0, splitIndex);
								buffer = buffer.substring(splitIndex+2);
								handleBlock(block);
								splitIndex = buffer.indexOf('\n\n');
							}
							return read();
						});
					};
					return read();
				}).catch(function(e){
					if(!hasEvent){
						callFallback();
					}else{
						callAbnormal('SSE request error: '+e.message+', url: '+url);
					}
				});
				return true;
			}
		},
		/*
			v3.18.35.20250920 增加
		
			hosts: 主机域名数组，数组形式，可传入多个主机，传入格式如 ['https://api.translate.zvo.cn/','https://api2.translate.zvo.cn/'] 一定注意最后还有个 /
					其中数组的第一个将被优先使用，第一个是主的，可靠性要更高的
		*/
		setHost:function(hosts){
			translate.service.use('translate.service');

			if (typeof translate.request.api.host == 'string') {
				//单个，那么赋予数组形式
				//translate.request.speedDetectionControl.hostQueue = [{"host":translate.request.api.host, time:0 }];
				translate.request.api.host = [hosts];
			}else{
				translate.request.api.host = hosts;
			}

			translate.request.speedDetectionControl.state = 0; //设置为未进行测速

			translate.storage.set('speedDetectionControl_hostQueue', '');
			translate.request.speedDetectionControl.hostQueue = [];
			translate.request.speedDetectionControl.checkHostQueue = new Array()
			//translate.request.speedDetectionControl.checkResponseSpeed_Storage(host, 0)

			//进行对host测速
			translate.request.speedDetectionControl.checkResponseSpeed();

			// init.json 的请求
			translate.temp_request_init = undefined;
			setTimeout(function(){
				translate.request.initRequest();
			}, 3000);

		},

		/*
			发起 init.json 的请求
			这个应该在translate.execute 未执行完之前就要触发，最好在 setHost() 时、或者刚加载后越早触发越好
			它触发多次时，只有第一次才会正常执行。
		*/
		initRequest:function(){
			//初始化请求
			if(typeof(translate.request.api.init) == 'string' && translate.request.api.init != null && translate.request.api.init.length > 0){
				if(typeof(translate.temp_request_init) == 'undefined'){
					translate.temp_request_init = 1;
				}else{
					//第二次以及之后执行，都直接给返回不允许在执行了
					return;
				}

				try{
					translate.request.send(
						translate.request.api.init,
						{},
						{},
						function(data){
							if (data.result == 0){
								translate.log('translate.js init 初始化异常：'+data.info);
								return;
							}else if(data.result == 1){
								//服务端返回的最新版本
								var newVersion = translate.util.versionStringToInt(data.version);
								//当前translate.js的版本
								var currentVersion = translate.util.versionStringToInt(translate.version.replace('v',''));

								if(newVersion > currentVersion){
									translate.log('Tip : translate.js find new version : '+data.version);
								}
							}
						},
						'post',
						true,
						null,
						function(data){
							//console.log('eeerrr');
						},
						false
					);
				}catch(e){
				}
			}
		},

		/*
			追加参数，  v3.15.9.20250527 增加
			所有通过 translate.request.send 进行网络请求的，都会追加上这个参数
			默认是空，没有任何追加参数。

			设置方式： https://translate.zvo.cn/471711.html
			translate.request.appendParams = {
				key1:'key1',
				key2:'key2'
			}
		*/
		appendParams:{

		},
		/*
			追加header头的参数，  v3.15.13 增加
			所有通过 translate.request.send 进行网络请求的，都会追加上这个参数
			默认是空，没有任何追加参数。

			设置方式： https://translate.zvo.cn/471711.html
			translate.request.appendHeaders = {
				key1:'key1',
				Aauthorization:'Bearer xxxxxxxxxx'
			}
		*/
		appendHeaders:{

		},
		/*
			请求后端接口的响应。无论是否成功，都会触发此处。
			普通 XHR 请求会在 xhr.readyState==4 的状态时触发。
			如果 translate.json 启用了 SSE 并由 SSE 成功接管请求，这里会传入一个最小兼容响应对象，
			它不是原生 XMLHttpRequest，但会保留 status、readyState、responseText、response、data、requestURL 等常用字段，
			并通过 sse:true 标记来源，方便旧扩展代码兼容判断。
			此处会在接口请求响应后、且在translate.js处理前就会触发
			@param xhr XMLHttpRequest 接口请求；SSE 请求为最小兼容响应对象
			
		*/
		response:function(xhr){
			//console.log('response------');
			//console.log(xhr);
		},


		/*
			速度检测控制中心， 检测主备翻译接口的响应速度进行排列，真正请求时，按照排列的顺序进行请求
			v2.8.2增加	
			
			storage存储方面
			storage存储的key  						存的什么
			speedDetectionControl_hostQueue			hostQueue
			speedDetectionControl_hostQueueIndex	当前要使用的是 hostQueue 中的数组下标。如果没有，这里默认视为0
			speedDetectionControl_lasttime			最后一次执行速度检测的时间戳，13位时间戳


			
		*/
		speedDetectionControl:{
			/*
				当前测速的状态，
				0 尚未进行
				1 进行中
				2 已测速完毕

				这个也是用于判断是否为0，来避免多次发起测速情况
			 */ 
			state: 0, 

			/*
				
				进行 connect主节点缩减的时间，单位是毫秒.
				这个是进行 translate.request.speedDetectionControl.checkResponseSpeed() 节点测速时，translate.request.api.host 第一个元素是默认的主节点。
				主节点在实际测速完后，会减去一定的时间，以便让用户大部分时间可以使用主节点，而不必走分节点。
				例如主节点实际响应速度 3500 毫秒，那么会减去这里设置的2000毫秒，记为 1500 毫秒
				当然如果是小于这里设置的2000毫秒，那么会记为0毫秒。
				这样再跟其他分节点的响应时间进行对比，主节点只要不是响应超时，就会有更大的几率被选中为实际使用的翻译的节点
				
				这里的单位是毫秒。
				v2.10.2.20231225 增加
			*/
			hostMasterNodeCutTime:2000,	

			/*
				翻译的队列，这是根据网络相应的速度排列的，0下标为请求最快，1次之...
				其格式为：
					[
						{
							"host":"xxxxxxxx",
							"time":123 			//这里的单位是毫秒
						},
						{
							"host":"xxxxxxxx",
							"time":123 			//这里的单位是毫秒
						}
					]
			*/
			hostQueue:[],	
			hostQueueIndex:-1,	//当前使用的 hostQueue的数组下标，  -1表示还未初始化赋予值，不可直接使用，通过 getHostQueueIndex() 使用
			disableTime:1000000,	//不可用的时间，storage中存储的 speedDetectionControl_hostQueue 其中 time 这里，如果值是 这个，便是代表这个host处于不可用状态

			/*
				设置当前使用的翻译通道 host
				适用于 进行中时，中途切临时换翻译通道。
			*/
			setCurrentHost:function(host){
				translate.storage.set('speedDetectionControl_hostQueue','');  
				translate.request.api.host=host;
				translate.request.speedDetectionControl.checkHostQueue = new Array();
				translate.request.speedDetectionControl.checkResponseSpeed_Storage(host, 0);
			},

			//获取 host queue 队列
			getHostQueue:function(){
				if(translate.request.speedDetectionControl.hostQueue.length == 0){
					//还没有，先从本地存储中取，看之前是否已经设置过了
					// 只有经过真正的网络测速后，才会加入 storage 的 hostQueue
					var storage_hostQueue = translate.storage.get('speedDetectionControl_hostQueue');
					if(storage_hostQueue == null || typeof(storage_hostQueue) == 'undefined' || storage_hostQueue == ''){
						//本地存储中没有，也就是之前没设置过，是第一次用，那么直接讲 translate.request.api.host 赋予之
						//translate.request.api.host
				
						if(typeof(translate.request.api.host) == 'string'){
							//单个，那么赋予数组形式
							//translate.request.speedDetectionControl.hostQueue = [{"host":translate.request.api.host, time:0 }];
							translate.request.api.host = [''+translate.request.api.host];
						}

						//数组形态，多个，v2.8.2 增加多个，根据优先级返回
						translate.request.speedDetectionControl.hostQueue = [];
						for(var i = 0; i<translate.request.api.host.length; i++){
							var h = translate.request.api.host[i];
							//console.log(h);
							translate.request.speedDetectionControl.hostQueue[i] = {"host":h, time:0 };
						}
						//console.log(translate.request.speedDetectionControl.hostQueue);
						
					}else{
						//storage中有，那么赋予
						translate.request.speedDetectionControl.hostQueue = JSON.parse(storage_hostQueue);
						//console.log(storage_hostQueue);
						//console.log(translate.request.speedDetectionControl.hostQueue[0].time);
					}

					//console.log(translate.request.speedDetectionControl.hostQueue)

					/*
						当页面第一次打开初始化这个时才会进行测速，另外测速也是要判断时间的，五分钟一次
						进行测速
					*/
					var lasttime = translate.storage.get('speedDetectionControl_lasttime');
					if(lasttime == null || typeof(lasttime) == 'undefined'){
						lasttime = 0;
					}
					var updateTime = 60000;	//1分钟检测一次
					if(new Date().getTime() - lasttime > updateTime){
						translate.request.speedDetectionControl.checkResponseSpeed();
					}
					
				}
				

				return translate.request.speedDetectionControl.hostQueue;
			},

			/*
				服务于 checkResponseSpeed 用于将测试结果存入 storage
				time: 当前接口请求的耗时，单位是毫秒。如果是 1000000 那么表示这个接口不可用
			*/
			checkResponseSpeed_Storage:function(host, time){

				translate.request.speedDetectionControl.checkHostQueue.push({"host":host, "time":time });
				//按照time进行排序
				translate.request.speedDetectionControl.checkHostQueue.sort((a, b) => a.time - b.time);

				//存储到 storage 持久化
				translate.storage.set('speedDetectionControl_hostQueue',JSON.stringify(translate.request.speedDetectionControl.checkHostQueue));
				translate.storage.set('speedDetectionControl_lasttime', new Date().getTime());

				translate.request.speedDetectionControl.hostQueue = translate.request.speedDetectionControl.checkHostQueue;
			},

			/*
				执行测试响应速度动作
			*/
			checkResponseSpeed:function(){
				translate.request.speedDetectionControl.state = 1; //设置为进行测速中

				var headers = {
					'content-type':'application/x-www-form-urlencoded',
				};

				if(typeof(translate.request.api.connectTest) != 'string' || translate.request.api.connectTest == null || translate.request.api.connectTest.length < 1){
					return;
				}
				

				translate.request.speedDetectionControl.checkHostQueue = []; //用于实际存储
				translate.request.speedDetectionControl.checkHostQueueMap = []; //只是map，通过key取值，无其他作用

				if(typeof(translate.request.api.host) == 'string'){
					//单个，那么赋予数组形式
					translate.request.api.host = [''+translate.request.api.host];
				}

				for(var i = 0; i < translate.request.api.host.length; i++){
					var host = translate.request.api.host[i];
					// 获取当前时间的时间戳
					translate.request.speedDetectionControl.checkHostQueueMap[host] = {
						start:new Date().getTime()
					};

					
					try{
						translate.request.send(
							host+translate.request.api.connectTest,
							{host:host},
							{host:host},
							function(data){
								//只要其中某个取得响应，都代表测速完成
								translate.request.speedDetectionControl.state = 2;

								var host = data.info;
								var map = translate.request.speedDetectionControl.checkHostQueueMap[host];
								var time = new Date().getTime() - map.start;

								if(translate.request.api.host[0] == host){
									//console.log('如果是第一个，那么是主的，默认允许缩减2000毫秒，也就是优先使用主的');
									time = time - translate.request.speedDetectionControl.hostMasterNodeCutTime;
									if(time < 0){
										time = 0;
									}
								}

								translate.request.speedDetectionControl.checkResponseSpeed_Storage(host, time);
								/*
								translate.request.speedDetectionControl.checkHostQueue.push({"host":host, "time":time });
								//按照time进行排序
								translate.request.speedDetectionControl.checkHostQueue.sort((a, b) => a.time - b.time);

								//存储到 storage 持久化
								translate.storage.set('speedDetectionControl_hostQueue',JSON.stringify(translate.request.speedDetectionControl.checkHostQueue));
								translate.storage.set('speedDetectionControl_lasttime', new Date().getTime());

								translate.request.speedDetectionControl.hostQueue = translate.request.speedDetectionControl.checkHostQueue;
								//console.log(translate.request.speedDetectionControl.hostQueue);
								*/
							},
							'post',
							true,
							headers,
							function(data){
								//只要其中某个取得响应，都代表测速完成
								translate.request.speedDetectionControl.state = 2;

								//translate.request.speedDetectionControl.checkResponseSpeed_Storage(host, time);
								var hostUrl = data.requestURL.replace(translate.request.api.connectTest,'');
								translate.request.speedDetectionControl.checkResponseSpeed_Storage(hostUrl, translate.request.speedDetectionControl.disableTime);
							},
							false
						);
					}catch(e){
						//console.log('e0000');
						translate.log(e);
						//time = 300000; //无法连接的，那么赋予 300 秒吧
					}

				}
				
			},

			//获取当前使用的host的数组下标
			getHostQueueIndex:function(){
				if(translate.request.speedDetectionControl.hostQueueIndex < 0){
					//页面当前第一次使用，赋予值
					//先从 storage 中取
					var storage_index = translate.storage.get('speedDetectionControl_hostQueueIndex');
					if(typeof(storage_index) == 'undefined' || storage_index == null){
						//存储中不存在，当前用户（浏览器）第一次使用，默认赋予0
						translate.request.speedDetectionControl.hostQueueIndex = 0;
						translate.storage.set('speedDetectionControl_hostQueueIndex',0);
					}else{
						translate.request.speedDetectionControl.hostQueueIndex = storage_index;
					}
				}
				return translate.request.speedDetectionControl.hostQueueIndex;
			},

			//获取当前要使用的host
			getHost:function(){
				var queue = translate.request.speedDetectionControl.getHostQueue();
				//console.log(queue);
				var queueIndex = translate.request.speedDetectionControl.getHostQueueIndex();
				if(queue.length > queueIndex){
					//正常，没有超出越界
					
				}else{
					//异常，下标越界了！，固定返回最后一个
					translate.log('异常，下标越界了！index：'+queueIndex);
					queueIndex = queue.length-1;
				}
				//console.log(queueIndex);
				return queue[queueIndex].host;
			},

		},
		//生成post请求的url
		getUrl:function(path){
			var currentHost = translate.request.speedDetectionControl.getHost();
			var url = currentHost+path+'?v='+translate.version;
			//console.log('url: '+url);
			return url;
		},
		/**
		 * 按 translate.request.send 原有规则组装 POST 表单参数。
		 * <p>XHR 和 SSE 都必须通过这里生成参数，避免 stream=1 分支遗漏 browserDefaultLanguage、
		 * appendParams 或企业版 key，导致同一个 translate.json 请求在两条传输路径上的行为不一致。</p>
		 *
		 * @param data 请求参数对象或字符串。传入对象时会按原逻辑追加公共参数。
		 * @param extraParams 仅当前请求额外追加的参数，例如 SSE 请求的 stream=1。
		 * @return application/x-www-form-urlencoded 格式的请求体
		 */
		buildPostParams:function(data, extraParams){
			var params = '';

			if(data == null || typeof(data) == 'undefined'){
				data = {};
			}

			if(typeof(data) == 'string'){
				params = data; //payload 方式 , edge 的方式
			}else{
				//表单提交方式

				//加入浏览器默认语种  v3.6.1 增加，以便更好的进行自动切换语种
				data.browserDefaultLanguage = translate.util.browserDefaultLanguage();

				//追加附加参数
				for(var apindex in translate.request.appendParams){
					if (!translate.request.appendParams.hasOwnProperty(apindex)) {
						continue;
					}
					data[apindex] = translate.request.appendParams[apindex];
				}

				if(typeof(translate.enterprise) != 'undefined'){
					//加入key
					if(typeof(translate.enterprise.key) != 'undefined' && typeof(translate.enterprise.key) == 'string' && translate.enterprise.key.length > 0){
						data.key = translate.enterprise.key;
					}
				}

				//只服务当前传输方式的临时参数放在最后追加，避免被 appendParams 覆盖。
				if(typeof(extraParams) == 'object' && extraParams != null){
					for(var epindex in extraParams){
						if (!extraParams.hasOwnProperty(epindex)) {
							continue;
						}
						data[epindex] = extraParams[epindex];
					}
				}

				//组合参数
				for(var index in data){
					if (!data.hasOwnProperty(index)) {
						continue;
					}
					if(params.length > 0){
						params = params + '&';
					}
					params = params + index + '=' + data[index];
				}
			}
			return params;
		},
		/**
		 * 按 translate.request.send 原有规则组装请求头。
		 * <p>这里返回普通对象，XHR 会逐个 setRequestHeader，fetch 会直接作为 headers 使用。</p>
		 *
		 * @param headers 当前请求自己的 header
		 * @return 合并 appendHeaders 和 currentpage 后的 header 对象
		 */
		buildHeaders:function(headers){
			var requestHeaders = {};
			if(headers != null){
				for(var index in headers){
					if (!headers.hasOwnProperty(index)) {
						continue;
					}
					requestHeaders[index] = headers[index];
				}
			}

			//追加附加参数
			for(var ahindex in translate.request.appendHeaders){
				if (!translate.request.appendHeaders.hasOwnProperty(ahindex)) {
					continue;
				}
				requestHeaders[ahindex] = translate.request.appendHeaders[ahindex];
			}

			if(translate.service.name != 'client.edge'){
				requestHeaders.currentpage = window.location.href+'';
			}
			return requestHeaders;
		},
		/**
		 * post请求
		 * @param path 请求的path（path，传入的是translate.request.api.translate 这种的，需要使用 getUrl 来组合真正请求的url ）
		 * @param data 请求的参数数据，传入如 
		 * 		{
		 * 			from: "chinese_simplified",
		 * 			text: "%5B%22%E4%BD%A0%E5%A5%BD%EF%BC%8C%E6%88%91",
		 * 			to: "chinese_traditional
		 * 		}
		 * 		
		 * @param func 请求完成的回调，也就是只要响应码是 200 ，则会触发这个方法。 传入如 function(responseData, requestData){ console.log(responseData); }
		 * 				其中的参数：
		 * 					responseData 响应的数据
		 * 					requestData post请求所携带的数据
		 * 				注意，是响应数据是第一个参数，请求数据是第二个参数。 以向前兼容
		 * @param abnormalFunc 响应异常所执行的方法，响应码不是200就会执行这个方法 ,传入如 function(xhr){}  另外这里的 xhr 会额外有个参数  xhr.requestURL 返回当前请求失败的url
		 * @param sseCallbacks SSE 单次请求回调，只对当前请求生效，避免 translate.execute() 并发请求共享全局回调造成串线。
		 */
		post:function(path, data, func, abnormalFunc, sseCallbacks){
			var headers = {
				'content-type':'application/x-www-form-urlencoded',
			};
			if(typeof(data) == 'undefined'){
				return;
			}

			//企业级翻译自动检测
			if(typeof(translate.enterprise) != 'undefined'){
				translate.enterprise.automaticAdaptationService();
			}
			
			// ------- edge start --------
			var url = translate.request.getUrl(path);
			//if(url.indexOf('edge') > -1 && path == translate.request.api.translate){
			if(translate.service.name == 'client.edge'){	
				if(path == translate.request.api.translate){
					translate.service.edge.translate(path, data, func, abnormalFunc);
					return;
				}
				if(path == translate.request.api.language){
					var d = {};
					d.info = 'SUCCESS';
					d.result = 1;
					d.list = translate.service.edge.language.json;
					func(d);
					return;
				}
				
				//return;
			}
			// ------- edge end --------

			if(path == translate.request.api.translate && translate.request.sse.use === true && typeof(data) == 'object' && data != null){
				var sseData = {};
				for(var sseDataIndex in data){
					if (!data.hasOwnProperty(sseDataIndex)) {
						continue;
					}
					sseData[sseDataIndex] = data[sseDataIndex];
				}
				var selfRequest = this;
				var sseStarted = translate.request.sse.post(path, sseData, func, abnormalFunc, function(){
					// 只有在 SSE 还没有收到任何事件前失败，才降级回原始 JSON 请求。
					// 这里继续使用原始 data，避免 stream=1 残留到降级请求里造成再次进入 SSE 入口。
					selfRequest.send(path, data, data, func, 'post', true, headers, abnormalFunc, true);
				}, sseCallbacks);
				if(sseStarted){
					return;
				}
			}

			this.send(path, data, data, func, 'post', true, headers, abnormalFunc, true);
		},
		/**
		 * 发送请求
		 * url 请求的url或者path（path，传入的是translate.request.api.translate 这种的，需要使用 getUrl 来组合真正请求的url ）
		 * data 请求的数据，如 {"author":"管雷鸣",'site':'www.guanleiming.com'} 
		 * appendXhrData 附加到 xhr.data 中的对象数据，传入比如  {"from":"english","to":"japanese"} ，他会直接赋予 xhr.data
		 * func 请求完成的回调，也就是只要响应码是 200 ，则会触发这个方法。 传入如 function(requestData, responseData, xhr){ console.log(responseData); }
		 * 				其中的参数：
		 * 					requestData post请求所携带的数据
		 * 					responseData 响应的数据
		 * method 请求方式，可传入 post、get
		 * isAsynchronize 是否是异步请求， 传入 true 是异步请求，传入false 是同步请求。 如果传入false，则本方法返回xhr
		 * headers 设置请求的header，传入如 {'content-type':'application/x-www-form-urlencoded'};
		 * abnormalFunc 响应异常所执行的方法，响应码不是200就会执行这个方法 ,传入如 function(xhr){}  另外这里的 xhr 会额外有个参数  xhr.requestURL 返回当前请求失败的url
		 * showErrorLog 是否控制台打印出来错误日志，true打印， false 不打印
		 */
		send:function(url, data, appendXhrData, func, method, isAsynchronize, headers, abnormalFunc, showErrorLog){
			//post提交的参数
			var params = translate.request.buildPostParams(data);
			if(url.indexOf('https://') == 0 || url.indexOf('http://') == 0){
				//采用的url绝对路径
			}else{
				//相对路径，拼接上host
				url = translate.request.getUrl(url);
			}

			var xhr=null;
			try{
				xhr=new XMLHttpRequest();
			}catch(e){
				xhr=new ActiveXObject("Microsoft.XMLHTTP");
			}
			xhr.data=appendXhrData;
			//2.调用open方法（true----异步）
			xhr.open(method,url,isAsynchronize);
			//设置headers
			var requestHeaders = translate.request.buildHeaders(headers);
			for(var headerIndex in requestHeaders){
				if (!requestHeaders.hasOwnProperty(headerIndex)) {
		    		continue;
		    	}
				xhr.setRequestHeader(headerIndex,requestHeaders[headerIndex]);
			}
			xhr.send(params);
			//4.请求状态改变事件
			xhr.onreadystatechange=function(){
			    if(xhr.readyState==4){
			    	translate.request.response(xhr); //自定义响应的拦截

			        if(xhr.status==200){
			        	//请求正常，响应码 200
			        	var json = null;
			        	if(typeof(xhr.responseText) == 'undefined' || xhr.responseText == null){
			        		//相应内容为空
			        	}else{
			        		//响应内容有值
			        		if(xhr.responseText.indexOf('{') > -1 && xhr.responseText.indexOf('}') > -1){
				        		//应该是json格式
				        		try{
					        		json = JSON.parse(xhr.responseText);
					        	}catch(e){
					        		translate.log(e);
					        	}
				        	}
			        	}
			        	
			        	if(json === null){
			        		func(xhr.responseText);
			        	}else{
			        		func(json, xhr.data, xhr);
			        	}
			        }else{
			        	if(showErrorLog){
			        		if(url.indexOf(translate.request.api.connectTest) > -1){
			        			//测试链接速度的不在报错里面
			        		}else{

			        			//判断是否是v2版本的翻译，如果是 translate.service 模式并且没有使用企业级翻译，参会提示
			        			//2024.3月底开始，翻译使用量增加的太快，开源的翻译服务器有点扛不住经常出故障，所以直接把这个提示加到这里
			        			if(translate.service.name == 'translate.service'){
			        				translate.log('----- translate.js 提示 -----\n翻译服务响应异常，解决这种情况可以有两种方案：\n【方案一】：使用采用最新版本 3.16.0及更高版本，js引用文件为 https://cdn.staticfile.net/translate.js/3.16.0/translate.js 并且使用 client.edge 模式 （增加一行设置代码就好，可参考 https://translate.zvo.cn/4081.html ），这样就不会再出现这种情况了，而且这个方案也是完全免费的。 \n【方案二】：采用企业级稳定翻译通道 ,但是这个相比于 方案一 来说，是有一定的收费的，大概一年600，这个就是专门为了高速及高稳定准备的，而相比于这个方案二，方案一则是全免费的。 因为方案二我们是部署了两个集群，而每个集群又下分了数个网络节点，包含中国大陆、香港、美国、欧洲、 等多个州，充分保障稳定、高效，同样也产生了不少成本，所以才需要付费。更多信息说明可以参考： http://translate.zvo.cn/4087.html \n【方案三】：私有部署你自己的翻译通道，并且启用内存级翻译缓存，毫秒级响应，但是需要依赖一台1核2G服务器，是最推荐的方式。具体参考：https://translate.zvo.cn/391129.html\n-------------');
			        			}

			        			//console.log(xhr);
					        	translate.log('------- translate.js service api response error --------');
					        	translate.log('    http code : '+xhr.status);
					        	translate.log('    response : '+xhr.response);
					        	translate.log('    request url : '+url);
					        	translate.log('    request data : '+JSON.stringify(data));
					        	translate.log('    request method : '+method);
					        	translate.log('---------------------- end ----------------------');
			        		}
			        		
			        	}
			        	xhr.requestURL = url;
			        	if(abnormalFunc != null){
			        		abnormalFunc(xhr);
			        	}
			        }
			    }
			}
			return xhr;
		},
		/*

			手动进行翻译操作。参数说明：
				texts: 可传入要翻译的文本、以及文本数组。 比如要一次翻译多个句子，那就可以传入数组的方式
				function: 翻译完毕后的处理函数。传入如 function(data){ console.log(data); }
						  注意，返回的data.result 为 1，则是翻译成功。  为0则是出错，可通过data.info 得到错误原因。 更详细说明参考： http://api.zvo.cn/translate/service/20230807/translate.json.html

				abnormalFunc: 翻译失败后的处理函数。传入如 function(xhr){ console.log(xhr); }
						  注意，这里的 xhr 是 XMLHttpRequest 对象，可以通过 xhr.status 获取响应状态码，通过 xhr.responseText 获取响应内容。

			使用案例一： 
			translate.request.translateText('你好，我是翻译的内容', function(data){
				//打印翻译结果
				console.log(data);
			}, function(xhr){
				//打印翻译失败后的信息
				console.log(xhr);
			});
			
			使用案例二：
			var texts = ['我是翻译的第一句','我是翻译的第二句','我是翻译的第三句'];
			translate.request.translateText(texts, function(data){
				//打印翻译结果
				console.log(data);
			}, function(xhr){
				//打印翻译失败后的信息
				console.log(xhr);
			});

			使用案例三：
			var obj = {
				from:'chinese_simplified',
				to:'english',
				texts: ['我是翻译的第一句','我是翻译的第二句','我是翻译的第三句']
			}
			translate.request.translateText(obj, function(data){
				//打印翻译结果
				console.log(data);
			}, function(xhr){
				//打印翻译失败后的信息
				console.log(xhr);
			});
		*/
		translateText:function(obj, func, abnormalFunc){
			var texts = new Array();
			var from = translate.language.getLocal();
			var to = translate.language.getCurrent();

			if(typeof(obj) == 'string'){
				//案例一的场景，传入单个字符串
				texts[0] = obj;
			}else{
				//不是字符串了，而是对象了，判断是案例二还是案例三

				var type = Object.prototype.toString.call(obj);
				//console.log(type);
				if(type == '[object Array]'){
					//案例二
					texts = obj;
				}else if(type == '[object Object]'){
					//案例三
					if(typeof(obj.texts) == 'undefined'){
						translate.log('translate.request.translateText 传入的值类型异常，因为你没有传入 obj.texts 要翻译的具体文本！ 请查阅文档： https://translate.zvo.cn/4077.html');	
					}
					if(typeof(obj.texts) == 'string'){
						//单个字符串
						texts = [obj.texts];
					}else{
						//多个字符串，数组形态
						texts = obj.texts;
					}
					if(typeof(obj.from) == 'string' && obj.from.length > 0){
						from = obj.from;
					}
					if(typeof(obj.to) == 'string' && obj.to.length > 0){
						to = obj.to;
					}
				}else{
					translate.log('translate.request.translateText 传入的值类型错误，请查阅文档： https://translate.zvo.cn/4077.html');
					return;
				}
			}
			//console.log(obj);
			//返回的翻译结果，下标跟 obj.texts 一一对应的
			var translateResultArray = new Array();

			// 筛选需要翻译的文本及其原始索引
  			var apiTranslateText = [];
			var apiTranslateItems = [];
			for(var i = 0; i < texts.length; i++){
				//判断是否在浏览器缓存中出现了
				var hash = translate.util.hash(texts[i]);
				var cache = translate.storage.get('hash_'+to+'_'+hash);
				//console.log(hash+'\t'+texts[i]+'\t'+cache);
				if(cache != null && cache.length > 0){
					//缓存中发现了这个得结果，那这个就不需要再进行翻译了
					translateResultArray[i] = cache;
				}else{
					translateResultArray[i] = '';
					apiTranslateText.push(texts[i]);
					apiTranslateItems.push({
						index: i,
						text: texts[i],
						hash: hash
					});
				}
			}
			if (apiTranslateText.length == 0) {
				//没有需要进行通过网络API翻译的任务了，全部命中缓存，那么直接返回
				var data = {
					from:from,
					to: to,
					text:translateResultArray,
					result:1
				};
				//console.log(data);
			    func(data);
			    return;
			}



			//还有需要进行通过API接口进行翻译的文本，需要调用翻译接口
			if(typeof(translate.request.api.translate) != 'string' || translate.request.api.translate == null || translate.request.api.translate.length < 1){
				//用户已经设置了不掉翻译接口进行翻译
				return;
			}

			var url = translate.request.api.translate;
			var data = {
				from:from,
				to: to,
				text:encodeURIComponent(JSON.stringify(apiTranslateText))
			};
			//console.log(apiTranslateText);
			translate.request.post(url, data, function(responseData, requestData){
				//console.log(responseData); 
				//console.log(data); 
				if(responseData.result != 1){
					translate.log('=======ERROR START=======');
					translate.log('from : '+requestData.from);
					translate.log('to : '+requestData.to);
					translate.log('translate text array : '+texts);
					translate.log('response error info: '+responseData.info);
					translate.log('=======ERROR END  =======');
					return;
				}

				for(var i = 0; i < responseData.text.length; i++){
					if(typeof(apiTranslateItems[i]) === 'undefined'){
						continue;
					}

					//将翻译结果以 key：hash  value翻译结果的形式缓存
					var hash = apiTranslateItems[i].hash;
					translate.storage.set('hash_'+to+'_'+hash, responseData.text[i]);
					//如果离线翻译启用了全部提取，那么还要存入离线翻译指定存储
					if(translate.offline.fullExtract.isUse){
						translate.offline.fullExtract.set(hash, apiTranslateItems[i].text, data.to, responseData.text[i]);
					}

					//进行组合数据到 translateResultArray
					translateResultArray[apiTranslateItems[i].index] = responseData.text[i];
				}
				responseData.text = translateResultArray;			

				func(responseData);
			}, (function(xhr){
				if(abnormalFunc && typeof(abnormalFunc) == 'function'){
					abnormalFunc(xhr);
				}
			}));
		},
		listener:{
			//是否已经启动过 translate.request.listener.addListener() 开始监听了，开始了则是true，默认没开始则是false
			isStart:false,
			//用户的代码里是否启用了 translate.request.listener.start() ，true：启用
			use:false, 
			// request listener 启动后创建的资源，保存引用便于 reset() 释放。
			intervalId:null,
			observer:null,
			minIntervalTime:800, // 两次触发的最小间隔时间，单位是毫秒，这里默认是800毫秒。最小填写时间为 200毫秒
			lasttime:0,// 最后一次触发执行 translate.execute() 的时间，进行执行的那一刻，而不是执行完。13位时间戳
			/*
				设置要在未来的某一时刻执行，单位是毫秒，13位时间戳。
				执行时如果当前时间大于这个数，则执行，并且将这个数置为0。
				会有一个循环执行函数每间隔200毫秒触发一次
			*/
			executetime:0,
			/*
				进行翻译时，延迟翻译执行的时间
				当ajax请求结束后，延迟这里设置的时间，然后自动触发 translate.execute() 执行
			*/
			delayExecuteTime:200, 
			/*
				满足ajax出发条件，设置要执行翻译。
				注意，设置这个后并不是立马就会执行，而是加入了一个执行队列，避免1秒请求了10次会触发10次执行的情况
			*/
			addExecute:function(){
				var currentTime = Date.now();
				if(translate.request.listener.lasttime == 0){
					//是第一次，lasttime还没设置过，那么直接设置执行时间为当前时间
					translate.request.listener.executetime = currentTime;
					translate.request.listener.lasttime = 1;
				}else{
					//不是第一次了

					if(translate.request.listener.executetime > 1){
						//当前有执行队列等待，不用再加入执行等待了
						//console.log('已在执行队列，不用再加入了 '+currentTime);
					}else{
						//执行队列中还没有，可以加入执行命令

						if(currentTime < translate.request.listener.lasttime + translate.request.listener.minIntervalTime){
							//如果当前时间小于最后一次执行时间+间隔时间，那么就是上次才刚刚执行过，这次执行的太快了，那么赋予未来执行翻译的时间为最后一次时间+间隔时间
							translate.request.listener.executetime = translate.request.listener.lasttime + translate.request.listener.minIntervalTime;
							//console.log('addexecute - < 如果当前时间小于最后一次执行时间+间隔时间，那么就是上次才刚刚执行过，这次执行的太快了，那么赋予未来执行翻译的时间为最后一次时间+间隔时间');
						}else{
							translate.request.listener.executetime = currentTime;
							//console.log('addexecute -- OK ');
						}
					}
					

				}

				
			},
			/*
				自定义是否会被触发的方法判断
				url 当前ajax请求的url，注意是这个url请求完毕获取到200相应的内容时才会触发此方法
				返回值 return true; 默认是不管什么url，全部返回true，表示会触发翻译自动执行 translate.execute; ,如果你不想让某个url触发翻译，那么你可以自行在这个方法中用代码进行判断，然后返回false，那么这个url将不会自动触发翻译操作。
			*/
			trigger:function(url){
				return true;
			},

			/*js translate.request.listener.start start*/
			/*
				启动根据ajax请求来自动触发执行翻译，避免有时候有的框架存在漏翻译的情况。
				这个只需要执行一次即可，如果执行多次，只有第一次会生效
			*/
			start:function(){
				translate.request.listener.use = true;
			},

			reset:function(){
				if(translate.request.listener.intervalId !== null){
					clearInterval(translate.request.listener.intervalId);
					translate.request.listener.intervalId = null;
				}
				if(translate.request.listener.observer !== null){
					translate.request.listener.observer.disconnect();
					translate.request.listener.observer = null;
				}
				translate.request.listener.isStart = false;
				translate.request.listener.executetime = 0;
			},
			/*js translate.request.listener.start end*/

			// 当 translate.execute() 触发时，也就是触发了生命周期的 start 时，才会启动这里。这里要在翻译进行后才能触发，不然提前出发会导致跟用户设置的启动时间不相符造成异常
			addListener:function(){
				if(translate.request.listener.use == false){
					//根本就没设置启用，直接推出
					return;
				}

				//确保这个方法只会触发一次，不会过多触发
				if(typeof(translate.request.listener.isStart) != 'undefined' && translate.request.listener.isStart == true){
					return;
				}else{
					translate.request.listener.isStart = true;
				}

				//增加一个没100毫秒检查一次执行任务的线程
				translate.request.listener.intervalId = setInterval(function(){
					var currentTime = Date.now();
					//console.log(translate.request.listener.executetime)
					if(translate.request.listener.executetime > 1 && currentTime > translate.request.listener.executetime+translate.request.listener.delayExecuteTime){
						translate.request.listener.executetime = 0;
						translate.request.listener.lasttime = currentTime;
						if(translate.executeTriggerNumber > 0){ //已经执行过了 translate.execute() ，那么才会触发
							try{
								//console.log('translate.request.listener.start ... 执行翻译 --'+currentTime);
								translate.execute();
							}catch(e){
								translate.log(e);
							}
						}
					}
				}, 100);

				if(typeof(PerformanceObserver) == 'undefined'){
					translate.log('因浏览器版本较低， translate.request.listener.start() 中 PerformanceObserver 对象不存在，浏览器不支持，所以 translate.request.listener.start() 未生效。');
					translate.request.listener.reset();
					return;
				}

				const observer = new PerformanceObserver((list) => {
					var translateExecute = false;	//是否需要执行翻译 true 要执行
				    for(var e = 0; e < list.getEntries().length; e++){
				    	var entry = list.getEntries()[e];
				    	//console.log(entry)

				    	if (entry.initiatorType === 'fetch' || entry.initiatorType === 'xmlhttprequest') {
				        	var url = entry.name;
				        	//console.log(url);
				        	//判断url是否是当前translate.js本身使用的
				        	if(typeof(translate.request.api.host) == 'string'){
				        		translate.request.api.host = [translate.request.api.host];
				        	}
				        	var ignoreUrl = false; // 是否是忽略的url true是

				        	//translate.service 模式判断
				        	for(var i = 0; i < translate.request.api.host.length; i++){
				        		if(url.indexOf(translate.request.api.host[i]) > -1){
				        			//是，那么直接忽略
				        			ignoreUrl = true;
				        			break;
				        		}
				        	}
				        	//client.edge 判断   translate.service.edge可能会被精简translate.js定制时给直接干掉，所以提前加个判断
				        	if(typeof(translate.service.edge) != 'undefined'){
				        		if(url.indexOf('edge.microsoft.com/translate/translatetext') > -1){
					        		ignoreUrl = true;
					        	}
				        	}
				        	
				        	if(ignoreUrl){
				        		//console.log('忽略：'+url);
								continue;
				        	}
				        	if(translate.request.listener.trigger(url)){
				        		//正常，会触发翻译，也是默认的
				        	}else{
				        		//不触发翻译，跳过
				        		continue;
				        	}

				        	translateExecute = true;
				        	break;
				        }
				    }
				    if(translateExecute){
				    	//console.log('translate.request.listener.addExecute() -- '+Date.now());
				    	translate.request.listener.addExecute();
				    }
				});
				translate.request.listener.observer = observer;

				//v3.15.14.20250617 增加
				// 优先使用 entryTypes  兼容 ES5 的写法

				var supportedTypes = PerformanceObserver.supportedEntryTypes;
				if (supportedTypes) {
				    var hasResource = false;
				    for (var i = 0; i < supportedTypes.length; i++) {
				        if (supportedTypes[i] === "resource") {
				            hasResource = true;
				            break;
				        }
				    }
				    if (hasResource) {
				        try {
				            observer.observe({ entryTypes: ["resource"] });
				            return;
				        } catch (e) {
				            translate.log("PerformanceObserver entryTypes 失败，尝试 type 参数");
				        }
				    }
				}


				// 回退到 type 参数
				try {
					observer.observe({ type: "resource", buffered: true });
					translate.log("使用 PerformanceObserver type");
				} catch (e) {
					translate.log("当前浏览器不支持 PerformanceObserver 的任何参数, translate.request.listener.start() 未启动");
					translate.request.listener.reset();
				}

			}
			
		}
	},
	//存储，本地缓存
	storage:{
		/*js translate.storage.IndexedDB start*/
		//对浏览器的 IndexedDB 操作
		IndexedDB:{
			db: null,
			// 初始化数据库
			initDB: function () {
				const self = this;
				return new Promise((resolve, reject) => {
					const DB_NAME = 'translate.js';
					const STORE_NAME = 'kvStore';
					const DB_VERSION = 1;

					const request = indexedDB.open(DB_NAME, DB_VERSION);

					request.onupgradeneeded = function(event) {
						const upgradedDb = event.target.result;
						if (!upgradedDb.objectStoreNames.contains(STORE_NAME)) {
							upgradedDb.createObjectStore(STORE_NAME, { keyPath: 'key' });
						}
					};

					request.onsuccess = function(event) {
						self.db = event.target.result;
						resolve();
					};

					request.onerror = function(event) {
						reject('IndexedDB 打开失败');
					};
				});
			},
			/*
				存储键值对
				使用方式：
					await translate.storage.indexedDB.set("user_001", { name: "Alice" });
			*/
			set: async function (key, value) {
				if (!this.db) await this.initDB();

				return new Promise((resolve, reject) => {
					const tx = this.db.transaction('kvStore', 'readwrite');
					const store = tx.objectStore('kvStore');
					const item = { key, value };
					const request = store.put(item);

					request.onsuccess = () => resolve();
					request.onerror = () => reject('写入失败');
				});
			},
			/*
				获取键对应的值
				使用方式：
					var user = await translate.storage.indexedDB.get("user_001");
			*/
			get: async function (key) {
				if (!this.db) await this.initDB();

				return new Promise((resolve, reject) => {
					const tx = this.db.transaction('kvStore', 'readonly');
					const store = tx.objectStore('kvStore');
					const request = store.get(key);

					request.onsuccess = () => {
						const result = request.result;
						resolve(result ? result.value : undefined);
					};

					request.onerror = () => reject('读取失败');
				});
			},
			/*
				列出针对key进行模糊匹配的所有键值对
				使用方式：
					const users = await translate.storage.IndexedDB.list("*us*r*");
					其中传入的key可以模糊搜索，其中的 * 标识另个或多个
			*/
			list: async function (key = '') {
				if (!this.db) await this.initDB();

				return new Promise((resolve, reject) => {
					const tx = this.db.transaction('kvStore', 'readonly');
					const store = tx.objectStore('kvStore');
					const request = store.openCursor();
					const results = [];

					// 将通配符 pattern 转换为正则表达式
					const regexStr = '^' + key.replace(/\*/g, '.*') + '$';
					const regex = new RegExp(regexStr);

					request.onsuccess = (event) => {
						const cursor = event.target.result;
						if (cursor) {
							if (regex.test(cursor.key)) {
								results.push({ key: cursor.key, value: cursor.value.value });
							}
							cursor.continue();
						} else {
							resolve(results);
						}
					};

					request.onerror = () => reject('游标读取失败');
				});
			}
		},
		/*js translate.storage.IndexedDB end*/

		set:function(key,value){
			localStorage.setItem(key,value);
		},
		get:function(key){
			return localStorage.getItem(key);
		}

	},
	//针对图片进行相关的语种图片替换
	images:{
		/* 要替换的图片队列，数组形态，其中某个数组的：
			key："/uploads/allimg/160721/2-160H11URA25-lp.jpg"; //旧图片，也就是原网站本身的图片。也可以绝对路径，会自动匹配 img src 的值，匹配时会进行完全匹配
			value："https://xxx.com/abc_{language}.jpg" //新图片，要被替换为的新图片。新图片路径需要为绝对路径，能直接访问到的。其中 {language} 会自动替换为当前要显示的语种。比如你要将你中文网站翻译为繁体中文，那这里会自动替换为：https://xxx.com/abc_chinese_traditional.jpg  有关{language}的取值，可查阅 http://api.translate.zvo.cn/doc/language.json.html 其中的语言标识id便是
		*/
		queues:[], 
		
		/*
			向图片替换队列中追加要替换的图片
			传入格式如：
			
			translate.images.add({
				"/uploads/a.jpg":"https://www.zvo.cn/a_{language}.jpg",
				"/uploads/b.jpg":"https://www.zvo.cn/b_{language}.jpg",
			});
			
			参数说明：
			key  //旧图片，也就是原网站本身的图片。也可以绝对路径，会自动匹配 img src 的值，匹配时会进行完全匹配
			value //新图片，要被替换为的新图片。新图片路径需要为绝对路径，能直接访问到的。其中 {language} 会自动替换为当前要显示的语种。比如你要将你中文网站翻译为繁体中文，那这里会自动替换为：https://xxx.com/abc_chinese_traditional.jpg  有关{language}的取值，可查阅 http://api.translate.zvo.cn/doc/language.json.html 其中的语言标识id便是
		*/
		add:function(queueArray){
			/*
			translate.images.queues[translate.images.queues.length] = {
				old:oldImage,
				new:newImage
			}
			*/
			for(var key in queueArray){
				if (!queueArray.hasOwnProperty(key)) {
		    		continue;
		    	}
				translate.images.queues[key] = queueArray[key];
			}
		},
		//执行图片替换操作，将原本的图片替换为跟翻译语种一样的图片
		execute:function(){
			//console.log(translate.images.queues);
			if(Object.keys(translate.images.queues).length < 1){
				//如果没有，那么直接取消图片的替换扫描
				return;
			}
			
			/*** 寻找img标签中的图片 ***/
			var imgs = document.getElementsByTagName('img');
			for(var i = 0; i < imgs.length; i ++){
				var img = imgs[i];
				if(typeof(img.src) == 'undefined' || img.src == null || img.src.length == 0){
					continue;
				}
				var imgSrc = img.getAttribute('src');  //这样获取到的才是src原始的值，不然 img.src 是拿到一个绝对路径

				for(var key in translate.images.queues){
					var oldImage = key; //原本的图片src
					var newImage = translate.images.queues[key]; //新的图片src，要替换为的
					//console.log('queue : '+oldImage + ' , img.src: '+imgSrc);
					if(oldImage == imgSrc){
						//console.log('发现匹配图片:'+imgSrc);
						/*
						//判断当前元素是否在ignore忽略的tag、id、class name中
						if(translate.ignore.isIgnore(node)){
							console.log('node包含在要忽略的元素中：');
							console.log(node);
							continue;
						}
						*/
						
						//没在忽略元素里，可以替换
						newImage = newImage.replace(new RegExp('{language}','g'), translate.to);
						img.src = newImage;
					}
				}
				
			}
			
			
			/********** 还要替换style中的背景图 */
			// 获取当前网页中所有的元素
			var elems = document.getElementsByTagName("*");
			// 遍历每个元素，检查它们是否有背景图
			for (var i = 0; i < elems.length; i++) {
				var elem = elems[i];
				// 获取元素的计算后样式
				var style = window.getComputedStyle(elem, null);
				// 获取元素的背景图URL
				var bg = style.backgroundImage;
				// 如果背景图不为空，打印出来
				if (bg != "none") {
					//console.log(bg);
					var old_img = translate.images.gainCssBackgroundUrl(bg);
					//console.log("old_img:"+old_img);
					if(typeof(translate.images.queues[old_img]) != 'undefined'){
						//存在
						var newImage = translate.images.queues[old_img];
						newImage = newImage.replace(new RegExp('{language}','g'), translate.to);
						//更换翻译指定图像
						elem.style.backgroundImage='url("'+newImage+'")';
					}else{
						//console.log('发现图像'+old_img+', 但未做语种适配');
					}
				}
			}



			
		},
		//取css中的背景图，传入 url("https://xxx.com/a.jpg")  返回里面单纯的url
		gainCssBackgroundUrl:function(str){
			// 使用indexOf方法，找到第一个双引号的位置
			var start = str.indexOf("\"");
			// 使用lastIndexOf方法，找到最后一个双引号的位置
			var end = str.lastIndexOf("\"");
			// 如果找到了双引号，使用substring方法，截取中间的内容
			if (start != -1 && end != -1) {
				var url = str.substring(start + 1, end); // +1是为了去掉双引号本身
				//console.log(url); // https://e-assets.gitee.com/gitee-community-web/_next/static/media/mini_app.2e6b6d93.jpg!/quality/100
				return url;
			}
			return str;
		}
	},
	/*js translate.reset start*/
	/*
		对翻译结果进行复原。比如当前网页是简体中文的，被翻译为了英文，执行此方法即可复原为网页本身简体中文的状态，而无需在通过刷新页面来实现
		config 可不传，则是直接恢复到默认未翻译前的状态。
			{
				selectLanguageRefreshRender:true, //是否重新渲染select选择语言到原始未翻译前的状态，默认不设置则是true，进行重新渲染
				notTranslateTip:true 			  //如果当前未执行过翻译，然后触发的 translate.reset() ，是否在控制台打印友好提示，提示未执行翻译，还原指令忽略， true则是正常打印这个提示， false则是不打印这个提示
			}
	*/
	reset:function(config){
		if(typeof(config) == 'undefined'){
			config = {};
		}
		if(typeof(config.selectLanguageRefreshRender) == 'undefined'){
			config.selectLanguageRefreshRender = true;
		}
		if(typeof(config.notTranslateTip) == 'undefined'){
			config.notTranslateTip = true;
		}

		
		/*
		for(var lang in translate.nodeQueue[lastUuid].list){
			if (!translate.nodeQueue[lastUuid].list.hasOwnProperty(lang)) {
	    		continue;
	    	}
			//console.log(lang);
			
			for(var hash in translate.nodeQueue[lastUuid].list[lang]){
				if (!translate.nodeQueue[lastUuid].list[lang].hasOwnProperty(hash)) {
		    		continue;
		    	}
				var item = translate.nodeQueue[lastUuid].list[lang][hash];
				//console.log(item);
				for(var index in item.nodes){
					if (!item.nodes.hasOwnProperty(index)) {
			    		continue;
			    	}
					//console.log(item.nodes[index]);
					//item.nodes[index].node.nodeValue = item.original;
					var currentShow = translate.storage.get('hash_'+currentLanguage+'_'+hash); //当前显示出来的文字，也就是已经翻译后的文字
					//console.log('hash_'+lang+'_'+hash+'  --  '+currentShow);
					if(typeof(currentShow) == 'undefined'){
						continue;
					}
					if(currentShow == null){
						continue;
					}
					if(currentShow.length == 0){
						continue;
					}
					// v3.16.5 针对gitee 的 readme 接入优化
					if(typeof(item.nodes[index].node) == 'undefined'){
						continue;
					}
					
					var attribute = typeof(item.nodes[index].node.attribute) == 'undefined' ? null:item.nodes[index].node.attribute;
					var analyse = translate.element.nodeAnalyse.analyse(item.nodes[index].node, '', '', attribute);
					translate.element.nodeAnalyse.analyse(item.nodes[index].node, analyse.text, item.original, attribute);
				}
			}
		}

		*/

		//清除 translate.listener 
		translate.listener.reset();

		//translate.temp_listenerStartInterval = undefined; //设置为尚未启动
		translate.init_first_trigger_execute = undefined; //translate.init 的 execute钩子，设置为未初始化状态
		

		/** 使用基于 translate.node 的还原 **/
		for (let key of translate.node.data.keys()) {
			if (translate.node.get(key) == null) {
	    		continue;
	    	}
			//for(var attr in translate.node.get(key)){
				//if (!translate.node.get(key).hasOwnProperty(attr)) {
		    	//	continue;
		    	//}

				//var analyse = translate.element.nodeAnalyse.get(key,translate.node.get(key).attribute);
	    		if(typeof(translate.node.get(key).originalText) !== 'string'){
					continue;
				}
				//translate.element.nodeAnalyse.analyse(key, analyse.text, translate.node.get(key).originalText, translate.node.get(key).attribute);
				
				//标注此次改动是有 translate.js 导致的 -- 这里就不用标记了，因为先已经移除了 translate.listener.observer 监听，所以不会再监听到还原的操作了
				
				//是否是 input、 textarea 的 value ，如果是 则是 true
				var isInputValue = false;
				if(typeof(translate.node.get(key).attribute) === 'string' && translate.node.get(key).attribute === 'value'){
					//可能是input\textarea 的value
					var nodename = translate.element.getNodeName(key).toLowerCase();
					if(nodename === 'input' || nodename === 'textarea'){
						key.value = translate.node.get(key).originalText;
						isInputValue = true;
					}
				}	
				if(!isInputValue){
					key.nodeValue = translate.node.get(key).originalText;
				}
			//}
		}


		//清除 node 中的记录
		if(translate.node.data != null){
			translate.node.data.clear();
		}
		
		//清除 translate.nodeQueue 的记录
		translate.nodeQueue = {};
		
		//清除 time 的记录
		if(typeof(translate.time.execute.data) != 'undefined'){
			translate.time.execute.data = {};
		}
		
		//清除设置storage中的翻译至的语种
		translate.storage.set('to', '');
		translate.to = null;

		//清除文本翻译记录
		if(translate.history.translateText.originalMap !== null){
			translate.history.translateText.originalMap.clear();
		}
		if(translate.history.translateText.resultMap !== null){
			translate.history.translateText.resultMap.clear();
		}
		

		//重新绘制 select 选择语言
		if(config.selectLanguageRefreshRender){
			translate.selectLanguageTag.refreshRender();
		}
		

		//清除正在进行的 translate.execute() 的执行状态记录
		translate.state = 0;

	},
	/*js translate.reset end*/
	
	/*js translate.selectionTranslate start*/
	/*
		划词翻译，鼠标在网页中选中一段文字，会自动出现对应翻译后的文本
		有网友 https://gitee.com/huangguishen 提供。
		详细使用说明参见：https://translate.zvo.cn/4072.html
	*/
	selectionTranslate:{
		//是否启用，默认是false，不启用。如果启用，则是 translate.selectionTranslate.start();
		use:false,
		selectionX:0,
		selectionY:0,
		callTranslate:function (event){
			let curSelection = window.getSelection();
			//相等认为没有划词
			if (curSelection.anchorOffset == curSelection.focusOffset) return;
			let translateText = window.getSelection().toString();

			//还有需要进行通过API接口进行翻译的文本，需要调用翻译接口
			if(typeof(translate.request.api.translate) != 'string' || translate.request.api.translate == null || translate.request.api.translate.length < 1){
				//用户已经设置了不掉翻译接口进行翻译
				translate.log('已设置了不使用 translate 翻译接口，翻译请求被阻止');
				return;
			}

			//简单Copy原有代码了
			var url = translate.request.api.translate
			var data = {
				from:translate.language.getLocal(),
				to:translate.to,
				text:encodeURIComponent(JSON.stringify([translateText]))
			};
			translate.request.post(url, data, function(responseData, requestData) {
				if (responseData.result != 1){
					translate.log('translate.selectionTranslate network response error : '+responseData.info);
					return;
				};
				let curTooltipEle = document.querySelector('#translateTooltip')
				curTooltipEle.innerText = responseData.text[0];
				curTooltipEle.style.top =selectionY+20+"px";
				curTooltipEle.style.left = selectionX+50+"px" ;
				curTooltipEle.style.display = "";
			}, null);
		},
		start:function () {
			// start() 是公开方法，可能被用户代码直接多次调用。
			// 已启动时直接返回，避免重复创建 tooltip 节点以及重复绑定 document 事件。
			if(translate.selectionTranslate.use === true){
				return;
			}

			translate.selectionTranslate.use = true;

			//新建一个tooltip元素节点用于显示翻译
			let tooltipEle = document.createElement('span');
			tooltipEle.innerText = '';
			tooltipEle.setAttribute('id', 'translateTooltip');
			tooltipEle.setAttribute('style', 'background-color:black;color:#fff;text-align:center;border-radius:6px;padding:5px;position:absolute;z-index:999;top:150%;left:50%; ');
			//把元素节点添加到body元素节点中成为其子节点，放在body的现有子节点的最后
			document.body.appendChild(tooltipEle);
			//监听鼠标按下事件，点击起始点位置作为显示翻译的位置点
			document.addEventListener('mousedown', (event)=>{ selectionX= event.pageX;selectionY= event.pageY ;}, false);			
			//监听鼠标弹起事件，便于判断是否处于划词
			document.addEventListener('mouseup', translate.selectionTranslate.callTranslate, false);
			//监听鼠标点击事件，隐藏tooltip，此处可优化
			document.addEventListener('click', (event)=>{  document.querySelector('#translateTooltip').style.display = "none"}, false);
		}
	},
	/*js translate.selectionTranslate end*/

	/*js translate.enterprise start*/	
	/*
		企业级翻译服务
		注意，这个企业级翻译中的不在开源免费之中，企业级翻译服务追求的是高稳定，这个是收费的！详情可参考：http://translate.zvo.cn/43262.html

	*/
	enterprise:{
		//默认不启用企业级，除非设置了 translate.enterprise.use() 这里才会变成true
		isUse:false,	
		use:function(){
			translate.enterprise.isUse = true; //设置为使用企业级翻译服务

			//主节点额外权重降低，更追求响应速度
			translate.request.speedDetectionControl.hostMasterNodeCutTime = 300; 
			translate.request.api.host=['https://america-enterprise-api-translate.zvo.cn/','https://beijing.enterprise.api.translate.zvo.cn/','https://deutsch.enterprise.api.translate.zvo.cn/', 'https://america.api.translate.zvo.cn:666/', 'https://api.translate.zvo.cn:666/', 'https://api.translate.zvo.cn:888/'];
			
			if(translate.service.name == 'client.edge'){
				translate.service.name = 'translate.service';
				translate.log('您已启用了企业级翻译通道 translate.enterprise.use(); (文档：https://translate.zvo.cn/4087.html) , 所以您设置的 translate.service.use(\'client.edge\'); (文档：https://translate.zvo.cn/4081.html) 将失效不起作用，有企业级翻译通道全部接管。');
				return;
			}
		},
		/*
			自动适配翻译服务通道，如果当前所有网络节点均不可用，会自动切换到 edge.client 进行使用
			这个会在 post请求 执行前开始时进行触发
		*/
		automaticAdaptationService:function(){
			if(!translate.enterprise.isUse){
				return;
			}
			var hosts = translate.request.speedDetectionControl.getHostQueue();
			//console.log(hosts);
			if(hosts.length > 0){
				if(hosts[0].time + 1 > translate.request.speedDetectionControl.disableTime){
					//所有节点都处于不可用状态，自动切换到 client.edge 模式
					translate.service.name = 'client.edge';
				} 
			}
		},
		/* 企业级翻译通道的key， v3.12.3.20250107 增加，针对打包成APP的场景 */
		key:'', 
	},
	/*js translate.enterprise end*/

	/*
		如果使用的是 translate.service 翻译通道，那么翻译后的语种会自动以小写的方式进行显示。
		如果你不想将翻译后的文本全部以小写显示，而是首字母大写，那么可以通过此方法设置一下
		v3.8.0.20240828 增加
		目前感觉应该用不到，所以先忽略
	*/
	/*
	notConvertLowerCase:function(){

	},
	*/


	/*js translate.progress start*/
	/*
		翻译执行的进展相关
		比如，浏览器本地缓存没有，需要走API接口的文本所在的元素区域，出现 记载中的动画蒙版，给用户以友好的使用提示
	*/
	progress:{
		style: `
			/* CSS部分 */
			/* 灰色水平加载动画 */
			.translate_api_in_progress {
			  position: relative;
			  overflow: hidden; /* 隐藏超出部分的动画 */
			}

			/* 蒙版层 */
			.translate_api_in_progress::after {
			  content: '';
			  position: absolute;
			  top: 0;
			  left: 0%;
			  width: 100%;
			  height: 100%;
			  background: rgba(255, 255, 255, 1); /* 半透明白色遮罩 */
			  z-index: 2;
			}

			/* 水平加载条动画 */
			.translate_api_in_progress::before {
			  content: '';
			  position: absolute;
			  top: 50%;
			  left: 0%;
			  width: 100%;
			  height:100%; /* 细线高度 */
			  background: linear-gradient(
			    90deg,
			    transparent 0%,
			    #e8e8e8 25%,  /* 浅灰色 */
			    #d0d0d0 50%,  /* 中灰色 */
			    #e8e8e8 75%,  /* 浅灰色 */
			    transparent 100%
			  );
			  background-size: 200% 100%;
			  animation: translate_api_in_progress_horizontal-loader 3.5s linear infinite;
			  z-index: 3;
			  transform: translateY(-50%);
			}

			@keyframes translate_api_in_progress_horizontal-loader {
			  0% {
			    background-position: 200% 0;
			  }
			  100% {
			    background-position: -200% 0;
			  }
			}
		`,

		/*
			通过文本翻译API进行的
		 */
		api:{
			isTip:true,//是否显示ui的提示，true显示，false不显示
			use: false, //默认不使用，translate.progress.api.startUITip(); 可以设置为启用
			setUITip:function(tip){
				translate.progress.api.isTip = tip;
			},
			//移除子元素（无限级别）中的所有 class name 的loading 遮罩
			//level 层级，数字，比如第一次调用，传入1， 第一次里面产生的第二次调用，这里就是2
			removeChildClass:function(node, level){

				//判断是否有子元素，判断其两级子元素，是否有加了loading遮罩了
		        var childNodes = node.childNodes;
				if(childNodes == null || typeof(childNodes) == 'undefined'){
					
				}else if(childNodes.length > 0){
					for(var i = 0; i<childNodes.length; i++){
						translate.progress.api.removeChildClass(childNodes[i], level+1);
					}
				}

				if(level == 1){
					//第一次调用，是不删除本身的class name
					return;
				}
				if(typeof(node) == 'undefined'){
					return;
				}
				if(typeof(node.className) != 'string'){
					return;
				}
				if(node.className.indexOf('translate_api_in_progress') === -1){
					return;
				}
				node.className = node.className.replace(/translate_api_in_progress/g, '');
			},
			// 移除指定元素上的翻译中 UI 提示。
			// 这里保持原有 className 字符串替换方式，避免第一步抽公共能力时改变旧浏览器或特殊元素的行为。
			removeUITipByElements:function(elements){
				if(typeof(elements) == 'undefined' || elements == null){
					return;
				}
				for(var r = 0; r<elements.length; r++){
					if(typeof(elements[r]) == 'undefined' || elements[r] == null || typeof(elements[r].className) !== 'string'){
						continue;
					}
					if(elements[r].className.indexOf('translatejs-text-element-hidden') > -1){
						elements[r].className = elements[r].className.replace(/translatejs-text-element-hidden/g, '');
					}
					if(elements[r].className.indexOf('translate_api_in_progress') > -1){
						elements[r].className = elements[r].className.replace(/translate_api_in_progress/g, '');
					}
				}
			},
			
			/*
				config: 可设置的一些参数
					{
						maskLayerMinWidth:10.0 	//当翻译时，需要请求网络，此时翻译的文本上会出现遮罩层显示一个进行中的动画，这个动画出现在的元素，最小宽度是多少。如果不设置，默认是10，也就是10像素，也就是当元素大于等于10像素时，才会在上面显示这个进行中的动画。而小于10像素宽度的元素，则是空白一片什么也不显示。 它支持设置float类型的值
					}

				
			*/
			startUITip:function(config){
				// 进度提示只需要启用一次，重复调用会重复注册生命周期回调。
				if(translate.progress.api.use === true){
					return;
				}

				translate.progress.api.use = true;

				if(typeof(config) === 'undefined'){
					config = {};
				}
				if(typeof(config.maskLayerMinWidth) !== 'number'){
					config.maskLayerMinWidth = 10;
				}
				
				//创建隐藏文字的 style
				var translatejsTextElementHidden = document.getElementById('translatejs-text-element-hidden');
				if(typeof(translatejsTextElementHidden) == 'undefined' || translatejsTextElementHidden == null){
					const style = document.createElement('style');
			        // 设置 style 元素的文本内容为要添加的 CSS 规则
			       	style.textContent = ' .translatejs-text-element-hidden, .translatejs-text-element-hidden[type="text"]::placeholder{color: transparent !important; -webkit-text-fill-color: transparent !important; text-shadow: none !important;} ';
			        style.id = 'translatejs-text-element-hidden';
			        // 将 style 元素插入到 head 元素中
			        document.head.appendChild(style);
				}

				// 创建一个 遮罩层加载中动画的 style 元素
				var translatejsMaskLayerAnimation = document.getElementById('translatejs-mask-layer-animation');
				if(typeof(translatejsMaskLayerAnimation) == 'undefined' || translatejsMaskLayerAnimation == null){
					const style = document.createElement('style');
			        // 设置 style 元素的文本内容为要添加的 CSS 规则
			       	style.textContent = translate.progress.style;
			       	style.id = 'translatejs-mask-layer-animation';
			        // 将 style 元素插入到 head 元素中
			        document.head.appendChild(style);
				}
		        

				if(translate.progress.api.isTip){
					//translate.listener.execute.renderStartByApi.push(function(uuid, from, to){
					translate.lifecycle.execute.translateNetworkBefore.push(function(data){
						var startTime = new Date().getTime();

						//取出当前变动的node，对应的元素
						var elements = translate.element.nodeToElement(data.nodes);
						//console.log(elements)
				    	
						//隐藏所有node的文本
					    for(var r = 0; r<elements.length; r++){
							elements[r].className = elements[r].className+' translatejs-text-element-hidden';
						}

						var rects = translate.visual.getRects(elements);
					    //console.log(rects)
					    var rectsOneArray = translate.visual.rectsToOneArray(rects);

					    //排序
					    var sortRects = translate.visual.coordinateSort(rectsOneArray);
						//console.log(sortRects);

					    //过滤，比如过滤掉宽度非常小的，不然显示出来会很丑
						// 1. 收集需要删除的下标
						const indicesToRemove = [];
						for (let i = 0; i < sortRects.length; i++) {
						  if (sortRects[i].width < config.maskLayerMinWidth) {
						    indicesToRemove.push(i);
						  }
						}
						// 2. 移除宽度极小的rects元素
						for(var di = indicesToRemove.length-1; di > -1; di--){
							//console.log(sortRects[indicesToRemove[di]]);
							sortRects.splice(indicesToRemove[di], 1);
						}

					    //去除空间重叠
					    var spaceEORects = translate.visual.rectsSpaceEliminateOverlap(sortRects);
					    //console.log('计算耗时：'+(new Date().getTime() - startTime));

						//var rectLineSplit = translate.visual.filterRectsByLineInterval(spaceEORects.rects, 1);
						var rectLineSplit = spaceEORects.rects;
						//var rectLineSplit = sortRects;
						for(var r = 0; r<rectLineSplit.length; r++){
							//判断这个元素的父级是否已经添加了，可能存在检测到多个本地语种，然后中文转英语后，又出现了日语转英语。 这里避免第二次日语转英语时，跟第一次中文转英语重复，导致出现样式过渡动画的重叠
							var parentNode = rectLineSplit[r].node.parentNode;
							if(typeof(parentNode) !== 'undefined' && typeof(parentNode.className) === 'string' && parentNode.className.indexOf('translate_api_in_progress') > -1){
								//上级已经有了，那么就不需要再加动画了
							}else{
								//上级没有加，那么这个才能考虑加
								if(typeof(rectLineSplit[r].node.className) === 'string' && rectLineSplit[r].node.className.indexOf('translate_api_in_progress') > -1){
						    		//已经存在了，就不继续加了
						    	}else{
						    		rectLineSplit[r].node.className = rectLineSplit[r].node.className+' translate_api_in_progress';	
						    	}
							}
						}
						//console.log('计算+渲染耗时：'+(new Date().getTime() - startTime));
					});
					
					translate.lifecycle.execute.translateNetworkAfter.push(function(data){
						//取出当前变动的node，对应的元素
						var elements = translate.element.nodeToElement(data.nodes);
						translate.progress.api.removeUITipByElements(elements);
						
						
					});

				}
			}
		}
	},
	/*js translate.progress end*/

	/*js dispose start*/
	/*
		对js对象内的值进行翻译,可以是JS定义的 对象、数组、甚至是单个具体的值
	*/
	js:{

		/*
			jsString 传入的js对象的字符串格式
			targetLanguage 翻译为的目标语言
			successFunction 执行成功后触发,传入格式  function(obj){ console.log(obj); }  其中 obj 是翻译之后的结果
			failureFunction 执行失败后触发,传入格式 function(failureInfo){ console.log(failureInfo); } 其中 failureInfo 是失败原因

			示例：

			var str = `
				{
				  "hello":"你好",
				  "word":"单词",
				  "你是谁": [
				      "世界",
				      "大海"
				    ]
				}
			`
			translate.js.transString(str,'english',function(obj){ console.log(obj); }, function(failureInfo){ console.log(failureInfo); });

		*/
		transString: function (jsString, targetLanguage, successFunction, failureFunction) {
			let jsObject;
			try{
				jsObject = JSON.parse(jsString);
			}catch(e){
				try{
					jsObject =  eval('(' + jsString + ')');
				}catch(e){
					translate.log(e)
					failureFunction(e);	
					return;
				}
			}
			translate.js.transObject(jsObject, targetLanguage, successFunction, failureFunction);
		},

		/*
			jsObject 传入的js对象，支持对象、数组等
			targetLanguage 翻译为的目标语言
			successFunction 执行成功后触发,传入格式  function(obj){ console.log(obj); }  其中 obj 是翻译之后的结果
			failureFunction 执行失败后触发,传入格式 function(failureInfo){ console.log(failureInfo); } 其中 failureInfo 是失败原因

			示例：

			var obj = {
				"hello":"你好",
				"word":"单词",
				"世界":["世界","大海"]
			};
			translate.js.transObject(obj,'english',function(obj){ console.log(obj); }, function(failureInfo){ console.log(failureInfo); });

		*/
		transObject: function (jsObject, targetLanguage, successFunction, failureFunction) {
			let tj_find = translate.js.find(jsObject);
			let kvs = tj_find.stringResult;
			
			/**** 第二步，将文本值进行翻译 ***/
			//先将其 kvs 的key 取出来
			var texts = new Array();
			for (const key in kvs) {
				texts.push(key);
			}

			/*
				它主要用于拆分场景，如果不需要拆分，它用不到
				下标对应，上面 texts 的原始下标跟拆分后的下标对应
				key 新数组的下标
				value 对象，包含:
					  index: 旧数组的下标
					  original: 翻译的原始文本
					  isSplit: 是否是被拆分的， true是，false不是
				
			*/
			var originalArrayIndexMap = new Array();

			var obj = {
				from:'auto',
				to: targetLanguage,
				texts: texts
			}

			if(translate.ignore.text.length > 0){ //有设置忽略翻译的文本
				var newTexts = new Array(); //新组合的

				//组合  split 切割
				var splitStrng = '';
				for(var ig = 0; ig < translate.ignore.text.length; ig++){
					if(translate.ignore.text[ig].trim().length == 0){
						continue;
					}
					var ignoretext = translate.ignore.text[ig];
					if(ignoretext.indexOf('.') > -1){
						ignoretext = ignoretext.replaceAll(/\./g, "\\.");
					}
					if(ignoretext.indexOf('$') > -1){
						ignoretext = ignoretext.replaceAll(/\$/g, "\\$");
					}

					if(splitStrng != ''){
						splitStrng = splitStrng + '|';
					}
					splitStrng = splitStrng + ignoretext;
				}
				var regex = new RegExp(splitStrng, 'g'); // 创建正则表达式对象，添加'g'修饰符表示全局匹配
				//console.log(regex);

				for(var tai = 0; tai<texts.length; tai++){
					
					var isFind = false; //是否发现匹配进行拆分了，true是
						
					//发现了忽略翻译的文本，将其单独抽取出来，不进行翻译
					//进行拆分
					var splits = texts[tai].split(regex);
					if(splits.length > 1){
						isFind = true; //拆分了
						for(var s = 0; s < splits.length; s++){
							if(splits[s].length > 0){
								newTexts.push(splits[s]);
								originalArrayIndexMap[newTexts.length-1] = {
									index:tai,
									original:splits[s],
									isSplit:true
								};
							}
						}
					}else{
						//没有拆分，那就原样加入
						newTexts.push(texts[tai]);
						originalArrayIndexMap[newTexts.length-1] = {
							index:tai,
							original:texts[tai],
							isSplit:false
						};
					}
					
				}
				obj.texts = newTexts;
			}
			//console.log(originalArrayIndexMap);
			
			translate.request.translateText(obj, function (data) {
				//打印翻译结果
				//console.log(data);
				if(typeof(data.result) == 'undefined' || data.result == 0){
					failureFunction('network connect failure');
					return;
				}
				if(data.result == 0){
					failureFunction(data.info);
					return;
				}

				/**** 第三步，将翻译结果赋予 jsObject ***/
				var translatedTexts; //跟最初拆分前的 texts 下标一一对应

				//判断是否有过分割
				if(translate.ignore.text.length > 0){ //有过分割，进行合并
					translatedTexts = new Array();

					for(var i = 0; i < data.text.length; i++){
						var originalTextIndex = originalArrayIndexMap[i].index; //最初分割前的原数组下标
						if(translatedTexts.length < originalTextIndex+1){
							translatedTexts.push(texts[originalTextIndex]);
						}
						translatedTexts[originalTextIndex] = translate.util.textReplace(translatedTexts[originalTextIndex], originalArrayIndexMap[i].original, data.text[i], data.to);
					}
				}else{
					translatedTexts = data.text; // 直接获取翻译结果数组赋予
				}

				if (translatedTexts && translatedTexts.length === texts.length) {
					texts.forEach((originalText, index) => {
						const translatedText = translatedTexts[index]; // 根据索引获取翻译结果
						const paths = kvs[originalText]; // 获取该文本的路径数组
						if (paths && paths.length > 0) {
							paths.forEach(path => {
								translate.js.setValueByPath(jsObject, path, translatedText); // 更新 jsObject
							});
						}
					});
				} else {
					console.error("翻译结果长度不匹配或为空");
				}

				if(Object.keys(tj_find.functionResult).length > 0){
					for(var sf in tj_find.functionResult){
						if (!tj_find.functionResult.hasOwnProperty(sf)) {
							continue;
						}
						translate.js.setValueByPath(jsObject, sf, tj_find.functionResult[sf]); // 更新 jsObject
					}
				}
				successFunction(jsObject);
				//console.log("翻译后的 jsObject:", jsObject);
			});
		},
		setValueByPath: function(obj, path, value){
			const parts = path.replace(/\[(\d+)\]/g, '.$1').split('.');
			let current = obj;
			for (let i = 0; i < parts.length - 1; i++) {
				current = current[parts[i]];
			}
			current[parts[parts.length - 1]] = value;
		},
		/*
			对js对象进行翻译
			obj: 可以是JS定义的 对象、数组、甚至是单个具体的值

			var obj = {
				"hello":"你好",
				"word":"单词",
				"世界":["世界","大海"]
			};
			translate.js.find(obj);

			返回值：
			{
				stringResult:
				functionResult:
			}

		*/
		find: function (obj, parentKey = '') {
			let kvs = {}; //stringResult
			let frs = {}; //functionResult

			if (typeof obj === 'object' && obj !== null) {
				if (Array.isArray(obj)) {
					obj.forEach((item, index) => {
						const currentKey = parentKey ? `${parentKey}[${index}]` : `[${index}]`;
						const tj_find = translate.js.find(item, currentKey);
						const subKvs = tj_find.stringResult;
						for (const [text, paths] of Object.entries(subKvs)) {
							if (!kvs[text]) {
								kvs[text] = [];
							}
							kvs[text] = kvs[text].concat(paths);
						}

						const subFrs = tj_find.functionResult;
						for(var sf in subFrs){
							if (!subFrs.hasOwnProperty(sf)) {
								continue;
							}
							frs[sf] = subFrs[sf];
						}
					});
				} else {

					for (const key in obj) {
						const currentKey = parentKey ? `${parentKey}.${key}` : key;
						if (typeof obj[key] === 'object' && obj[key] !== null) {
							const tj_find = translate.js.find(obj[key], currentKey);
							const subKvs = tj_find.stringResult;
							for (const [text, paths] of Object.entries(subKvs)) {
								if (!kvs[text]) {
									kvs[text] = [];
								}
								kvs[text] = kvs[text].concat(paths);
							}
							const subFrs = tj_find.functionResult;
							for(var sf in subFrs){
								if (!subFrs.hasOwnProperty(sf)) {
									continue;
								}
								frs[sf] = subFrs[sf];
							}

						} else if (typeof obj[key] === 'string') {
							if (typeof kvs[obj[key]] === 'undefined') {
								kvs[obj[key]] = [];
							}
							kvs[obj[key]].push(currentKey);
						}else if(typeof(obj[key]) == 'function'){
							//value是一个方法，那么也将他返回
							frs[currentKey]=obj[key];
						}
					}
				}
			} else if (typeof obj === 'string') {
				if (typeof kvs[obj] === 'undefined') {
					kvs[obj] = [];
				}
				kvs[obj].push(parentKey);
			}
			return {
				stringResult:kvs,
				functionResult:frs
			};
		},
		/*
			将 translate.js.transString 执行结果的 obj 对象 转化为 字符串输出
			这个可以直接输出到 textarea 中显示

			obj: js对象
			formatSupplementaryCharLength: 对这个js对象进行格式化自动补充字符的长度，比如 2、 4

			2025.10.10 优化传入参数
			obj:{
				jsObject: 原本的obj参数， 也就是js对象
				formatSupplementaryCharLength: 对这个js对象进行格式化自动补充字符的长度，比如 2、 4 ,默认不设置则是4
				functionBodyHandle: 针对值是function函数类型时，可以自定义对函数体的源码进行处理，它是传入 function 类型的，比如：
						functionBodyHandle: function(functionBody){
							functionBody = functionBody+'123';
							return functionBody;
						}
						传入值是函数体的string类型的源码内容
						返回值是修改过后最新的函数体的string类型的源码内容
						这是 2025.10.10 新增参数，应对layui的 i18n 全自动翻译函数体中的字符串文本
			}
		*/
		objToString:function(obj, formatSupplementaryCharLength){
			if(obj != null && typeof(obj) === 'object'){
				if(typeof(obj.jsObject) === 'object'){
					//是 2025.10.10 以后的新版本
				}else{
					//是 2025.10.10 以前的旧版本
					var newObj = {
						jsObject: obj
					}
					obj = newObj;
				}
			}else{
				obj = {};
			}
			if(typeof(formatSupplementaryCharLength) === 'number'){
				obj.formatSupplementaryCharLength = formatSupplementaryCharLength;
			}
			//未设置，就赋予默认值4
			if(typeof(obj.formatSupplementaryCharLength) !== 'number'){
				obj.formatSupplementaryCharLength = 4; 
			}

			// 自定义replacer函数，将函数转换为字符串
			let jsonStr = JSON.stringify(obj.jsObject, (key, value) => {
			  if (typeof value === 'function') {
			    // 将函数转换为其源代码字符串
			    var funcString = value.toString();
			    if(typeof(funcString) === 'string' && funcString.length > 0){
			    	funcString = funcString.replace(/\n/g, '___TRANSLATEJS_LINE_BREAK___');	
			    }
			    return funcString;
			  }else{
			  	return value;
			  }

	          return result;
			}, obj.formatSupplementaryCharLength);


			//对 function 的函数体进行处理
			// 将转义的\n替换为实际的换行符 -- 20251009 优化，去掉换行符替换，layui 工具中发现这样会将原本文本中的换行符替换掉，而是调整为仅仅针对function方法进行针对替换
			// 逐行判断，判断其中哪一行的value是function，要将function的字符串格式变为function函数格式
			if(jsonStr.indexOf('___TRANSLATEJS_LINE_BREAK___') > -1){
				const lines = jsonStr.split('\n');
			    for(var li = 0; li<lines.length; li++){
			    	// 检查当前行是否包含特定标记
			        if (lines[li].includes('___TRANSLATEJS_LINE_BREAK___')) {
			        	lines[li] = lines[li].replace(/___TRANSLATEJS_LINE_BREAK___/g, '\n'); //将其替换为原本的换行符

			            // 查找值部分（假设格式是 "key": "function..."）
			            const valueMatch = lines[li].match(/"[^"]+":\s*"([^"]+)"/);
			            if (valueMatch && valueMatch[1]) {
			                // 替换换行标记为实际换行
			                let functionStr = valueMatch[1].replace(/___TRANSLATEJS_LINE_BREAK___/g, '\n');
			                
			                // 将函数字符串转换为实际函数
			                try {
			                    // 使用Function构造函数创建函数更安全一些
			                    const functionParts = functionStr.match(/function\s*([^\(]*)\(([^)]*)\)\s*\{([\s\S]*)\}/);
			                    
			                    if (functionParts) {
			                        var [, name, params, body] = functionParts;
			                        if(typeof(obj.functionBodyHandle) === 'function'){
			                        	body = obj.functionBodyHandle(body);
			                        }
			                        // 替换原行中的字符串为函数表达式
			                        lines[li] = lines[li].replace(`"${valueMatch[1]}"`, `function${name}(${params}){${body}}`);
			                    }
			                } catch (e) {
			                    console.error('转换函数时出错:', e);
			                }
			            }
			        }
			    }
			 	jsonStr = lines.join('\n');   
			}
		    
			return jsonStr;
		}
	},
	/*js dispose end*/

	/*js translate.network start*/
	/*
		网络请求数据拦截并翻译
		当用户触发ajax请求时，它可以针对ajax请求中的某个参数，进行获取，并进行翻译，将翻译后的文本赋予这个参数，然后再放开请求。
		
		使用场景如：
			搜索场景，原本是中文的页面，翻译为英文后，给美国人使用，美国人使用时，进行搜索，输入的是英文，然后点击搜索按钮，发起搜索。
			然后此会拦截网络请求，将请求中用户输入的搜索文本的内容提取出来，识别它输入的是中文还是英文，如果不是本地的语种中文，那就将其翻译为中文，然后再赋予此请求的这个参数中，然后再放开这次请求。
			这样请求真正到达服务端接口时，服务端接受到的搜索的文本内容实际就是翻译后的中文文本，而不是用户输入的英文文本。
		
		何时自动进行翻译：
			1. 当前用户没有进行切换语言
			2. 切换语言了,但是输入的文本的语言是不需要进行翻译的, 输入的文本本身就是本地的语言
			这两种情况那就不需要拦截翻译
				

	*/	
	network: {
		//是否启用， true为启用 ，通过 translate.network.use(); 设置启用。 更多说明：  https://translate.zvo.cn/479724.html
	    isUse:false, 

	    // 原始方法保存
	    originalOpen: XMLHttpRequest.prototype.open,
	    originalSend: XMLHttpRequest.prototype.send,
	    setRequestHeaderOriginal: XMLHttpRequest.prototype.setRequestHeader,

	    // 规则配置
	    rules: [
	        {
	            url: /https:\/\/www\.guanleiming\.com\/a\/b\/.html/,
	            methods: ['GET', 'POST'],
	            params: ['a','b1']
	        }
	    ],
	    //根据 当前请求的url 跟 method 来判断当前请求是否符合规则， 
	    //如果符合，则返回符合的 rule 规则，也就是 translate.network.rules 中配置的某个。
	    //如果没有找到符合的，则返回 null
	    getRuleMatch:function(url, method){
			for (let i = 0; i < translate.network.rules.length; i++) {
			    const rule = translate.network.rules[i];
			    
			    // 检查 URL 是否匹配
			    if(typeof(rule.url) == 'undefined' || rule.url == ''){
			    	translate.log('WARINNG : translate.network.rule find url is null:');
			    	translate.log(rule);
			    	continue;
			    }
			    //console.log(rule);
			    const isUrlMatch = rule.url.test(url);
			    if(!isUrlMatch){
			    	continue;
			    }
			    
			    // 检查方法是否匹配（忽略大小写）
			    const isMethodMatch = rule.methods.includes(method.toUpperCase());
			    if(!isMethodMatch){
			    	continue;
			    }

			    return rule;
			}

			return null;
	    },
	    use:function(){
	    	translate.network.isUse = true;

	    	// 应用Hook
			XMLHttpRequest.prototype.open = function(...args) {
			    return translate.network.hookOpen.apply(this, args);
			};

			XMLHttpRequest.prototype.send = function(...args) {
			    return translate.network.hookSend.apply(this, args);
			};

			// 劫持 setRequestHeader 方法
		    XMLHttpRequest.prototype.setRequestHeader = function(...args) {
		        return translate.network.setRequestHeader.apply(this, args);
		    };

		    translate.network.fetch.use();
	    },
	    // 私有工具方法
	    _translateText(text) {
	    	if(translate.language.getLocal() == translate.language.getCurrent() || (typeof(text) == 'string' && text.length > 0 && translate.language.recognition(text).languageName == translate.language.getLocal())){
	    		/*
					1. 没有进行切换语言
					2. 切换语言了,但是输入的文本的语言是不需要进行翻译的, 输入的文本本身就是本地的语言

					这两种情况那就不需要拦截翻译
				*/
	    		
	    		return new Promise((resolve, reject) => {
		            const obj = {
		                from: 'auto',
		                to: translate.language.getLocal(),
		                text: [text]
		            };
		            
		            resolve(obj);
		        });
	    	}else{
	    		//有进行切换了，那进行翻译，将其他语种翻译为当前的本地语种
	    		return new Promise((resolve, reject) => {
		            const obj = {
		                from: 'auto',
		                to: translate.language.getLocal(),
		                texts: [text]
		            };
		            
		            //console.log('翻译请求:', obj);
		            translate.request.translateText(obj, function(data) {
		                if (data.result === 1) {
		                    resolve(data);
		                } else {
		                    reject(data);
		                }
		            });
		        });
	    	}
	        
	    },
		//劫持 setRequestHeader
		setRequestHeader: function(header, value) {
		    if (this._requestContext) {
		        this._requestContext.headers = this._requestContext.headers || {};
		        this._requestContext.headers[header] = value;
		    }

		    return translate.network.setRequestHeaderOriginal.call(this, header, value);
		},
	    // 请求处理工具
	    RequestHandler: {
	        async handleGet(url, rule) {
	        	//console.log(url);
	        	//console.log(rule);
				if(!Array.isArray(rule.params) || rule.params.length < 1){
	        		translate.log('WARINNG: rule not find params , rule : ');
	        		translate.log(rule);
	        		rule.params = [];
	        	}
	        	

	            try {
	                const urlObj = new URL(url, window.location.origin);
	                const params = urlObj.searchParams;
	                //console.log(rule.params);

	                //for (const paramName in rule.params) {
	                for(var p = 0; p < rule.params.length; p++){
	                	var paramName = rule.params[p];
	                		//console.log(paramName);
	                    if (params.has(paramName)) {
	                        const original = params.get(paramName);
	                        const translateResultData = await translate.network._translateText(original);
	                        
	                        if(typeof(translateResultData) == 'undefined'){
	                    				translate.log('WARINNG: translateResultData is undefined');
	                    		}else if(typeof(translateResultData.result) == 'undefined'){
	                    				translate.log('WARINNG: translateResultData.result is undefined');
	                    		}else if(translateResultData.result != 1){
	                    				translate.log('WARINNG: translateResultData.result failure : '+translateResultData.info);
	                    		}else{
	                    				params.set(paramName, decodeURIComponent(translateResultData.text[0]));
	                    		}

	                    }
	                }
	                
	                return urlObj.toString();
	            } catch (e) {
	                console.warn('GET处理失败:', e);
	                return url;
	            }
	        },

	        async handleForm(body, rule) {
	            try {
	                const params = new URLSearchParams(body);
	                const modified = {...params};
	                
	                for (const paramName of rule.params) {
	                    if (params.has(paramName)) {
	                        const original = params.get(paramName);
	                        const translated = await translate.network._translateText(original);
	                        modified[paramName] = translated;
	                    }
	                }
	                
	                return new URLSearchParams(modified).toString();
	            } catch (e) {
	                console.warn('表单处理失败:', e);
	                return body;
	            }
	        },

	        async handleJson(body, rule) {
	            try {
	                const json = JSON.parse(body);
	                const modified = {...json};
	                
	                for (const paramName of rule.params) {
	                    if (modified.hasOwnProperty(paramName)) {
	                        const original = modified[paramName];
	                        modified[paramName] = await translate.network._translateText(original);
	                    }
	                }
	                
	                return JSON.stringify(modified);
	            } catch (e) {
	                console.warn('JSON处理失败:', e);
	                return body;
	            }
	        }
	    },

	    // 请求上下文管理
	    _requestContext: null,

	    

	    // Hook open 方法
	    hookOpen(method, url, async, user, password) {
	    	let matchedRule = null;
	        this._requestContext = {
	            method: method.toUpperCase(),
	            originalUrl: url,
	            async: async,
	            user: user,
	            password: password,
	            matchedRule: translate.network.getRuleMatch(url, method)
	        };

	        return translate.network.originalOpen.call(this, method, url, async, user, password);
	    },

	    // Hook send 方法
	    hookSend(body) {
	        const ctx = this._requestContext;
	        if (!ctx || !ctx.matchedRule) {
	            return translate.network.originalSend.call(this, body);
	        }

	        const processRequest = async () => {
	            let modifiedBody = body;
	            const method = ctx.method;

	            try {
	                // 处理GET请求
	                //if (method === 'GET') {
	                    const newUrl = await translate.network.RequestHandler.handleGet(ctx.originalUrl, ctx.matchedRule);
	                    translate.network.originalOpen.call(this, method, newUrl, ctx.async, ctx.user, ctx.password);
	                //}

	                // 恢复请求头
	                if (ctx.headers) {
	                    for (const header in ctx.headers) {
	                        translate.network.setRequestHeaderOriginal.call(this, header, ctx.headers[header]);
	                    }
	                }    
	                
	                // 处理POST请求
	                if (method === 'POST') {
                    	if(typeof(body) != 'undefined' && body != null && body.length < 2000){
                			var isJsonBody = false; //是否是json格式的数据，是否json已经处理了， true 是
                			if(body.trim().indexOf('[') == 0 || body.trim().indexOf('{') == 0){
                				//可能是json
                				try{
            						modifiedBody = await translate.network.RequestHandler.handleJson(body, ctx.matchedRule);
            						isJsonBody = true;
                				}catch(je){ 
            						isJsonBody = false;
                				}
                			}
                			if(!isJsonBody){
                				try{
                					modifiedBody = await translate.network.RequestHandler.handleForm(body, ctx.matchedRule);
                				}catch(je){ 
                				}
                			}
                		}
	                }
	            } catch (e) {
	                console.warn('请求处理异常:', e);
	            }

	            translate.network.originalSend.call(this, modifiedBody);
	        };

	        // 异步处理
	        if (ctx.async !== false) {
	            processRequest.call(this);
	        } else {
	            console.warn('同步请求不支持翻译拦截');
	            translate.network.originalSend.call(this, body);
	        }
	    },
	    //fetch请求
	    fetch:{
			originalFetch: window.fetch,

			// 保存原始 fetch 方法
			use: function () {
				const self = this;
				window.fetch = function (...args) {
					return self.hookFetch.apply(self, args);
				};
			},

			// 拦截 fetch 请求
			hookFetch: async function (input, init) {
				const request = new Request(input, init);
				const url = request.url;
				const method = request.method;

				// 获取匹配规则
				const rule = translate.network.getRuleMatch(url, method);
				if (!rule) {
					return this.originalFetch.call(window, request);
				}

				// 初始化请求上下文
				const ctx = {
					method,
					url,
					headers: {},
					rule,
					isModified: false
				};

				// 保存请求头
				request.headers.forEach((value, key) => {
					ctx.headers[key] = value;
				});

				this._requestContext = ctx;

				try {
					const newUrl = await translate.network.RequestHandler.handleGet(url, rule);
					// 处理 GET 请求
					if (method === 'GET') {
						
						const newRequest = new Request(newUrl, {
							method,
							headers: new Headers(ctx.headers),
							mode: request.mode,
							credentials: request.credentials,
							cache: request.cache,
							redirect: request.redirect,
							referrer: request.referrer,
							referrerPolicy: request.referrerPolicy
						});
						return this.originalFetch.call(window, newRequest);
					}

					// 处理 POST 请求
					if (method === 'POST') {
						let body = null;
						if (request.body) {
							body = await request.clone().text();
						}

						const contentType = request.headers.get('Content-Type');
						let modifiedBody = body;

						if(typeof(body) != 'undefined' && body != null && body.length < 2000){
                			var isJsonBody = false; //是否是json格式的数据，是否json已经处理了， true 是
                			if(body.trim().indexOf('[') == 0 || body.trim().indexOf('{') == 0){
                				//可能是json
                				try{
                					modifiedBody = await translate.network.RequestHandler.handleJson(body, rule);
                					isJsonBody = true;
                				}catch(je){ 
                					isJsonBody = false;
                				}
                			}
                			if(!isJsonBody){
                				try{
                					modifiedBody = await translate.network.RequestHandler.handleForm(body, rule);
                				}catch(je){ 
                				}
                			}
                		}

						const newRequest = new Request(newUrl, {
							method,
							headers: new Headers(ctx.headers),
							body: modifiedBody,
							mode: request.mode,
							credentials: request.credentials,
							cache: request.cache,
							redirect: request.redirect,
							referrer: request.referrer,
							referrerPolicy: request.referrerPolicy
						});

						return this.originalFetch.call(window, newRequest);
					}

					// 其他方法直接返回原始请求
					return this.originalFetch.call(window, request);
				} catch (e) {
					console.warn('fetch 请求处理异常:', e);
					return this.originalFetch.call(window, request);
				}
			},
			// 请求上下文管理
			_requestContext: null

	    }
	},

	/*js translate.network end*/


	/*js translate.visual start*/
	/*
		人眼所看到的纯视觉层的处理
	*/
	visual: {
		/**
		 * 获取一组节点的视觉矩形信息
		 * @param nodes - 节点数组，格式如 ：
		 * 			[node1,node2,node3]
		 * @returns 返回的是二维数组，其中第一维度跟输入的 nodes 下标一一对应。
		 * 				其中第二维度，是应对换行的情况。比如  node1 没有换行，那第二维度就只有一个
		 * 												node2 有换行，有三行，那么第二维度就有三个，每行一个。 这个也是每行都有一个 开始坐标(x,y)、结束坐标(x,y)
		 * 	
		 */
		getRects:function(nodes){
			/*

		  return nodes.map(node => {
		    if (!node) return null;
		    
		    let rect;
		    if (node.nodeType === Node.TEXT_NODE) {
		      const range = document.createRange();
		      range.selectNodeContents(node);
		      const rects = range.getClientRects();
		      //console.log(rect);
		      rect = rects.length > 0 ? rects[0] : null;
		    } else if (node.nodeType === Node.ELEMENT_NODE) {
		      rect = node.getBoundingClientRect();
		    }
		    
		    return rect ? {
		      node,
		      left: rect.left,
		      top: rect.top,
		      right: rect.right,
		      bottom: rect.bottom,
		      width: rect.width,
		      height: rect.height
		    } : null;
		  });
		  */

			return nodes.map(node => {
                if (!node) return []; // 节点不存在时返回空数组
                
                let rects = [];
                if (node.nodeType === Node.TEXT_NODE) {
                    // 处理文本节点：获取所有行的矩形
                    const range = document.createRange();
                    range.selectNodeContents(node);
                    const clientRects = range.getClientRects();
                    // 转换为数组并处理每个行矩形
                    rects = Array.from(clientRects).map(rect => ({
                        node,
                        left: rect.left,
                        top: rect.top,
                        right: rect.right,
                        bottom: rect.bottom,
                        width: rect.width,
                        height: rect.height,
                        lineIndex: Array.from(clientRects).indexOf(rect) // 增加行索引，方便区分第几行
                    }));
                } else if (node.nodeType === Node.ELEMENT_NODE) {
                    // 处理元素节点：获取元素整体矩形（保持原有逻辑）
                    const rect = node.getBoundingClientRect();
                    rects = rect ? [{
                        node,
                        left: rect.left,
                        top: rect.top,
                        right: rect.right,
                        bottom: rect.bottom,
                        width: rect.width,
                        height: rect.height
                    }] : [];
                }
                
                return rects;
            });
		},
		/**
		 * 将 translate.visual.getRects 获取到的二维坐标数据转为一维坐标
		 */
		rectsToOneArray:function(rects){
			// 将 reacts 二维数组转化为 一维数组，以便对一维数组进行排序
			var oneArrayRects = new Array();
			for(var r = 0; r < rects.length; r++){
				for(var twoR = 0; twoR < rects[r].length; twoR++){
					oneArrayRects.push(rects[r][twoR]);
				}
			}
			return oneArrayRects;
		},
		/**
		 * 按行间隔筛选rects数组中的节点
		 * @param rects 一维的矩形信息数组（包含node和坐标信息），也就是 translate.visual.rectsToOneArray(translate.visual.getRects(nodes)); 取得的信息。它并不需要提前排序
		 * @param line - 间隔行数，1表示每行都取，2表示隔一行取一个，3表示隔2行取一个，以此类推
		 * @returns 筛选后的矩形信息数组，并按照 top 的值有小往大排序
		 */
		filterRectsByLineInterval:function(rects, line) {
	        // 验证输入
		    if (!Array.isArray(rects) || typeof line !== 'number' || line < 1) {
		        console.error('输入参数无效，请确保rects是数组且line是大于0的数字');
		        return [];
		    }
		    
		    // 1. 先处理所有矩形，计算每行的基准线（使用top作为主要依据）
		    // 为每个矩形添加行标识临时属性
		    const processedRects = rects.map(rect => {
		        if (!rect || rect.top === undefined) {
		            return null; // 过滤无效矩形
		        }
		        return {
		            ...rect,
		            // 计算行基准（使用top的整数部分，处理可能的浮点精度问题）
		            rowBase: Math.round(rect.top)
		        };
		    }).filter(Boolean); // 移除null值
		    
		    // 2. 按行基准分组（完全相同的rowBase属于同一行）
		    const rowMap = new Map();
		    processedRects.forEach(rect => {
		        const key = rect.rowBase;
		        if (!rowMap.has(key)) {
		            rowMap.set(key, []);
		        }
		        rowMap.get(key).push(rect);
		    });
		    
		    // 3. 将Map转换为数组并按行基准排序（确保从上到下的顺序）
		    const lineGroups = Array.from(rowMap.entries())
		        .sort((a, b) => a[0] - b[0]) // 按行基准升序排序
		        .map(entry => entry[1]); // 提取每组的矩形数组
		    
		    // 4. 按间隔行数筛选行组，并只保留每行的第一个元素
		    const filtered = [];
		    lineGroups.forEach((group, index) => {
		        if (index % line === 0 && group.length > 0) {
		            // 保留每行的第一个元素
		            filtered.push(group[0]);
		        }
		    });
		    
		    // 调试：打印所有行组的基准值和数量，方便验证
		    /*
		    console.log('行分组基准与数量:', lineGroups.map((g, i) => ({
		        rowBase: g[0].rowBase,
		        top: g[0].top,
		        count: g.length,
		        isSelected: i % line === 0 // 是否被选中
		    })));
		    */
		    
		    return filtered;
		},
		/*
			对传入的 rects 进行重叠识别排除，将重叠的、且面积小的删掉。
			说明：
				 * - 认为“重叠”必须在水平和垂直两个方向均严格交叉，交叉的位置比如水平或垂直产生了2个像素或超过2个像素的重叠，也就是面积上实际上已经重叠了。
				 *   所以如果两个矩形仅在边界上相接（例如 a.bottom === b.top 或 a.right === b.left）则不视为重叠，甚至稍微重叠不超过2像素也不视为重叠， 不会删除任何一方。
				 * - 决定保留哪一个：保留面积更大的矩形；若面积相等，则保留在排序中先出现的那个（确定性）。
				 * - 性能优化：先按 left 升序排序，比较时只与那些 left < current.right 的后续矩形比较（剪枝）。
				 * - 不做原地 splice（避免 O(n^2) 的移动开销），而是用布尔标记 removed[]，最后重建结果数组。

			@param rects 一维的矩形信息数组（包含node和坐标信息），比如 translate.visual.coordinateSort(rects); 排序后取得的信息。
						输入: rects: [{left, top, right, bottom}, ...]
			@return 返回排除重叠的坐标数组。
					{ 
						rects: 保留的不互相覆盖的矩形数组, 
						removes: rects中被移除的矩形数组 
					}

		*/
		rectsSpaceEliminateOverlap: function (inputRects) {
			if (!Array.isArray(inputRects) || inputRects.length === 0){
				return { rects: [], removes: [] };
			}

			const pixelThreshold = 2;
			const EPS = 1e-6;

			const areaOf = r =>
			Math.max(0, r.right - r.left) * Math.max(0, r.bottom - r.top);

			const intersectionWH = (a, b) => ({
				w: Math.min(a.right, b.right) - Math.max(a.left, b.left),
				h: Math.min(a.bottom, b.bottom) - Math.max(a.top, b.top)
			});

			const rectsWithIndex = inputRects.map((r, idx) => {
				let { left, top, right, bottom } = r;
				if (right < left){
					[left, right] = [right, left];
				}
				if (bottom < top){
					[top, bottom] = [bottom, top]
				};
				return { r: { left, top, right, bottom }, idx };
			});

			rectsWithIndex.sort((A, B) => {
				if (Math.abs(A.r.left - B.r.left) > EPS){
					return A.r.left - B.r.left;
				}
				if (Math.abs(A.r.top - B.r.top) > EPS){
					return A.r.top - B.r.top;
				}
				if (Math.abs(A.r.right - B.r.right) > EPS){
					return A.r.right - B.r.right;
				}
				return A.r.bottom - B.r.bottom;
			});

			const n = rectsWithIndex.length;
			const removed = new Array(n).fill(false);
			const areas = rectsWithIndex.map(x => areaOf(x.r));

			for (let i = 0; i < n; i++) {
				if (removed[i]) {
					continue;
				}
				const Ai = rectsWithIndex[i].r;
				const Ai_area = areas[i];

				for (let j = i + 1; j < n; j++) {
					if (removed[j]) {
						continue;
					}
					const Bj = rectsWithIndex[j].r;

					// ---- 优先检测包含（几何方式，带阈值）
					const A_contains_B =
						Ai.left <= Bj.left + pixelThreshold &&
						Ai.top <= Bj.top + pixelThreshold &&
						Ai.right >= Bj.right - pixelThreshold &&
						Ai.bottom >= Bj.bottom - pixelThreshold;

					const B_contains_A =
						Bj.left <= Ai.left + pixelThreshold &&
						Bj.top <= Ai.top + pixelThreshold &&
						Bj.right >= Ai.right - pixelThreshold &&
						Bj.bottom >= Ai.bottom - pixelThreshold;

					if (A_contains_B || B_contains_A) {
						if (A_contains_B && !B_contains_A) {
							removed[j] = true;
							continue;
						}
						if (B_contains_A && !A_contains_B) {
							removed[i] = true;
							break;
						}
						// 双包含（几乎重合）按面积或顺序
						const Bj_area = areas[j];
						if (Ai_area >= Bj_area){
							removed[j] = true;
						}else {
							removed[i] = true;
							break;
						}
					}

					// ---- 剪枝 ----
					if (Bj.left >= Ai.right - EPS){
						break;
					}

					// ---- 检查普通重叠 ----
					const { w, h } = intersectionWH(Ai, Bj);
					if (w <= pixelThreshold || h <= pixelThreshold){
						continue
					};

					const Bj_area = areas[j];
					if (Ai_area > Bj_area){
						 removed[j] = true;
					}else if (Bj_area > Ai_area) {
						removed[i] = true;
						break;
					} else {
						removed[j] = true;
					}
				}
			}

			const keeps = [], removes = [];
			const sortedToOrig = rectsWithIndex.map(x => x.idx);
			const origToSorted = new Map();
			for (let p = 0; p < n; p++){
				origToSorted.set(sortedToOrig[p], p);
			}
			for (let origIdx = 0; origIdx < inputRects.length; origIdx++) {
				const pos = origToSorted.get(origIdx);
				if (pos === undefined || !removed[pos]) {
					keeps.push(inputRects[origIdx]);
				}else{
					removes.push(inputRects[origIdx]);
				}
			}

			return { rects: keeps, removes };
		},

		/*
			对一组坐标进行排序
			按开始坐标从左到右、从上到下排序
			@param rects translate.visual.getRects获取到的坐标数据
		*/
		coordinateSort:function(rects){
			// 按从左到右、从上到下排序
		  const sortedRects = rects
		    .filter(rect => rect !== null)
		    .sort((a, b) => {
		      if (Math.abs(a.top - b.top) < 5) { // 同一行
		        return a.left - b.left;
		      }
		      return a.top - b.top;
		    });
		  return sortedRects;
		},
		
		/**
		 * 查找左右紧邻的矩形对
		 * @param rects translate.visual.getRects 获取到的坐标数据，转化为 一维数组 后传入
		 * @returns {Array<{before: Object, after: Object}>} - 左右紧邻的矩形对数组
		 */
		afterAdjacent:function(rects){
		  //进行从左到右-从上到下进行排序
		  var sortedRects = translate.visual.coordinateSort(rects);

		  const adjacentPairs = [];
		  //按行分组的矩形
		  const lineGroups = translate.visual.groupRectsByLine(sortedRects);
		  
		  // 检查每行中的所有紧邻元素对
		  lineGroups.forEach(line => {
		    for (let i = 0; i < line.length; i++) {
		      for (let j = i + 1; j < line.length; j++) {
		        const prev = line[i];
		        const next = line[j];
		        
		        // 如果后续元素与当前元素不紧邻，则后续其他元素也不可能紧邻
		        if (!translate.visual.areHorizontallyAdjacent(prev, next)) {
		          break;
		        }
		        
		        adjacentPairs.push({ before: prev, after: next });
		      }
		    }
		  });
		  
		  return adjacentPairs;
		},
		/**
		 * 按行分组矩形
		 * @param rects - 排序后的矩形数组 @param rects translate.visual.coordinateSort 获取到的坐标数据
		 * @returns {Object[][]} - 按行分组的矩形
		 */
		groupRectsByLine:function(rects){
			const lineGroups = [];
		  let currentLine = [];
		  
		  rects.forEach(rect => {
		    if (currentLine.length === 0) {
		      currentLine.push(rect);
		    } else {
		      const lastRect = currentLine[currentLine.length - 1];
		      // 如果在同一行，则添加到当前行
		      if (Math.abs(rect.top - lastRect.top) < 5) {
		        currentLine.push(rect);
		      } else {
		        // 否则开始新的一行
		        lineGroups.push(currentLine);
		        currentLine = [rect];
		      }
		    }
		  });
		  
		  // 添加最后一行
		  if (currentLine.length > 0) {
		    lineGroups.push(currentLine);
		  }
		  
		  return lineGroups;
		},
		/**
		 * 判断两个矩形是否水平紧邻
		 * @param {Object} rect1 - 第一个矩形
		 * @param {Object} rect2 - 第二个矩形
		 * @returns {boolean} - 是否水平紧邻
		 */
		areHorizontallyAdjacent:function(rect1, rect2){
			// 检查垂直方向是否有重叠（在同一行）
		  const verticalOverlap = Math.min(rect1.bottom, rect2.bottom) - Math.max(rect1.top, rect2.top);
		  
		  // 检查水平间距是否在阈值范围内
		  const horizontalGap = rect2.left - rect1.right;
		  
		  return verticalOverlap > 0 && Math.abs(horizontalGap) < 1; // 允许1px误差
		},
		/**
		 * 找到需要在节点文本末尾添加空格的节点
		 * @param {Array<{before: Object, after: Object}>} adjacentPairs - 左右紧邻的矩形对数组
		 * @returns {Node[]} - 需要添加空格的节点数组
		 */
		afterAddSpace:function(adjacentPairs){

		  const nodesToAddSpace = [];
		  
		  adjacentPairs.forEach(pair => {
		    const { before, after } = pair;
		    const beforeNode = before.node;
		    const afterNode = after.node;
		    
		    // 获取计算样式
		    const beforeStyle = window.getComputedStyle(
		      beforeNode.nodeType === Node.TEXT_NODE ? beforeNode.parentElement : beforeNode
		    );
		    
		    const afterStyle = window.getComputedStyle(
		      afterNode.nodeType === Node.TEXT_NODE ? afterNode.parentElement : afterNode
		    );
		    
		    // 检查间距是否由CSS属性引起
		    const hasRightSpacing = parseFloat(beforeStyle.marginRight) > 0 || 
		                           parseFloat(beforeStyle.paddingRight) > 0;
		    
		    const hasLeftSpacing = parseFloat(afterStyle.marginLeft) > 0 || 
		                          parseFloat(afterStyle.paddingLeft) > 0;
		    
		    // 如果没有明确的间距，且后一个节点的开始非空白符，则需要添加空格
		    if (!hasRightSpacing && !hasLeftSpacing) {
		    	//判断 before 节点的最后一个字符是否是空白符
		    	if(typeof(beforeNode.textContent) == 'string' && typeof(afterNode.textContent) == 'string'){
		    		if(/\s$/.test(beforeNode.textContent)){
		    			//before 最后一个字符是空格，则不需要追加空格符了
		    		}else if(/^\s/.test(afterNode.textContent)){
		    			//after 节点的开始第一个字符是空白符，那么也不需要追加空格符了
		    		}else{
		    			//这里就需要对 beforeNode 追加空格了
		    			nodesToAddSpace.push(beforeNode);
		    		}
		    	}
		    }
		  });
		  
		  return nodesToAddSpace;
		},
		/**
		 * 主函数：处理翻译后的空格调整
		 * @param {Node[]} nodes - 节点数组
		 */
		adjustTranslationSpaces:function(nodes){

			//先判断当前要显示的语种，是否需要用空格进行间隔单词，如果本身不需要空格间隔，像是中文，那就根本不需要去计算视觉距离
			if(!translate.language.wordBlankConnector(translate.to)){
				return;
			}

			//var startTime = Date.now();
			// 1. 获取节点视觉矩形
			const rects = translate.visual.getRects(nodes);

			// 将 reacts 二维数组转化为 一维数组，以便对一维数组进行排序
			var oneArrayRects = translate.visual.rectsToOneArray(rects);

			//console.log('rects:');
			//console.log(rects);
			//console.log('将 reacts 二维数组转化为一维数组 oneArrayRects:');
			//console.log(oneArrayRects);

			// 2. 查找左右紧邻的矩形对
			const adjacentPairs = translate.visual.afterAdjacent(oneArrayRects);
			//console.log('adjacentPairs:');
			//console.log(adjacentPairs);

			// 3. 确定需要添加空格的节点
			const nodesToAddSpace = translate.visual.afterAddSpace(adjacentPairs);
			//console.log('nodesToAddSpace:');
			//console.log(nodesToAddSpace);

			// 4. 添加非断行空格
			nodesToAddSpace.forEach(node => {
			// 确保只修改文本内容，不影响HTML结构
			if (node.nodeType === Node.TEXT_NODE) {
				
				//判断它的最后一个字符是否是空格，如果不是空格，才有必要加空格符
				if(node.textContent.length === 0 || node.textContent.substring(node.textContent.length -1, node.textContent.length) !== '\u00A0'){

					//找到它对应的 translate.node.data 的数据，先将其进行改动 - 目的是 listener 监听改动知道这是translate.js自己改的 - 以及 让 translate.node 的数据对应起来
					if(translate.node.get(node) !== null){
						if(typeof(translate.node.get(node).resultText) !== 'string'){
							//没有resultText这个属性，如果翻译失败或者本身是特殊字符比如数字，不需要被翻译，是没有这个属性的，那这里默认赋予 originalText 给他，以做记录，免得被listener监听
							translate.node.get(node).resultText = translate.node.get(node).originalText;
						}
						translate.node.get(node).resultText = translate.node.get(node).resultText + '\u00A0';

						if(typeof(translate.node.get(node).translateResults) === 'undefined'){
							translate.node.get(node).translateResults = {};
						}
						translate.node.get(node).translateResults[translate.node.get(node).resultText] = 1;

						translate.node.get(node).lastTranslateRenderTime = Date.now();
					}

					//console.log(node.textContent+'-->'+node.textContent.substring(node.textContent.length -1, node.textContent.length));
					node.textContent = node.textContent + '\u00A0';
				}
				
				//console.log(translate.node.get(node))
			} else if (node.nodeType === Node.ELEMENT_NODE) {
				// 如果是元素节点，修改其最后一个子节点（假设是文本节点）
				const lastChild = node.lastChild;
				if (lastChild && lastChild.nodeType === Node.TEXT_NODE) {
					//判断它的最后一个字符是否是空格，如果不是空格，才有必要加空格符
					if(lastChild.textContent.length === 0 || lastChild.textContent.substring(lastChild.textContent.length -1, lastChild.textContent.length) !== '\u00A0'){
						//找到它对应的 translate.node.data 的数据，先将其进行改动 - 目的是 listener 监听改动知道这是translate.js自己改的 - 以及 让 translate.node 的数据对应起来
						if(translate.node.get(lastChild) !== null){
							if(typeof(translate.node.get(lastChild).resultText) === 'string'){
								translate.node.get(lastChild).resultText = translate.node.get(lastChild).resultText + '\u00A0';
								translate.node.get(lastChild).translateResults[translate.node.get(lastChild).resultText] = 1;
								translate.node.get(lastChild).lastTranslateRenderTime = Date.now();
							}
						}
						lastChild.textContent = lastChild.textContent + '\u00A0';
					}
				}
			}
			});
			//var endTime = Date.now();
			//console.log('visual recognition time: '+(endTime-startTime)+'ms');
		},
		/*
			通过 translate.nodeQueue[uuid] 中的uuid，来传入这个 translate.nodeQueue[uuid] 中所包含涉及到的所有node (除特殊字符外 ，也就是 translate.nodeQueue[uuid].list 下 特殊字符那一类是不会使用的)
		*/
		adjustTranslationSpacesByNodequeueUuid:function(uuid){
			var nodes = [];
			for(var from in translate.nodeQueue[uuid].list){
				if (!translate.nodeQueue[uuid].list.hasOwnProperty(from)) {
					continue;
				}
				//空的，也就是有数字标点符号等这一类，也要加入，因为也要算入视觉间隔中去，比如 我有9个，其中的9如果不算的话，翻译后 my have9ge 就没有间隔了
				//if(from.length < 1){
				//	continue;
				//}
				if(typeof(translate.nodeQueue[uuid].list[from]) === 'undefined'){
					continue;
				}
				for(var hash in translate.nodeQueue[uuid].list[from]){
			    	if (!translate.nodeQueue[uuid].list[from].hasOwnProperty(hash)) {
			    		continue;
			    	}
			    	for(var nodeindex in translate.nodeQueue[uuid].list[from][hash].nodes){
			    		if (!translate.nodeQueue[uuid].list[from][hash].nodes.hasOwnProperty(nodeindex)) {
				    		continue;
				    	}
			    		var node = translate.nodeQueue[uuid].list[from][hash].nodes[nodeindex].node;
			    		nodes.push(node);
			    	}
			    }	
			}
			translate.visual.adjustTranslationSpaces(nodes);
		},
		
		/**
		 * 隐藏当前网页的所有文本
		 *
		 */
		hideText:{
			style:`
				/* 文本隐藏核心样式 - 仅隐藏文本内容 */

		        html.translatejs-text-hidden p, html.translatejs-text-hidden div, html.translatejs-text-hidden small, 
		        html.translatejs-text-hidden h1, html.translatejs-text-hidden h2, html.translatejs-text-hidden h3,
		        html.translatejs-text-hidden h4, html.translatejs-text-hidden h5, html.translatejs-text-hidden h6,
		        html.translatejs-text-hidden span, html.translatejs-text-hidden a, html.translatejs-text-hidden b,
		        html.translatejs-text-hidden strong, html.translatejs-text-hidden i, html.translatejs-text-hidden em,
		        html.translatejs-text-hidden mark,
		        html.translatejs-text-hidden blockquote, html.translatejs-text-hidden ul, html.translatejs-text-hidden ol,
		        html.translatejs-text-hidden li, html.translatejs-text-hidden table, html.translatejs-text-hidden th,
		        html.translatejs-text-hidden td, html.translatejs-text-hidden label, html.translatejs-text-hidden button,
		        html.translatejs-text-hidden input, html.translatejs-text-hidden select, html.translatejs-text-hidden textarea {
		            color: transparent !important;
		            text-shadow: none !important;
					transition: none !important;
		        }

		        /* 隐藏占位符文字 */
		        html.translatejs-text-hidden ::placeholder {
		            color: transparent !important;
		        }

		        /* 确保媒体元素不受影响 */
		        img, video, iframe, canvas, svg,
		        object, embed, picture, source {
		            color: initial !important;
		        }

		        /* 忽略隐藏的元素保持可见 */
		        .ignore-hidden {
		            color: inherit !important;
		        }
			`,

			/**
			 * 当点击切换语言按钮后，会刷新当前页面，然后再进行翻译。 
			 * 这时会出现刷新当前页面后，会先显示原本的文本，然后再翻译为切换为的语种，体验效果有点欠缺。  
			 * 这个得作用就是增强用户视觉的体验效果，在页面初始化加载时，如果判定需要翻译，那么会隐藏所有网页中的文本 。
			 * 这个需要在body标签之前执行，需要在head标签中执行此。也就是加载 translate.js 以及触发此都要放到head标签中
			 * 
			 * id 唯一标识，可能会隐藏多次，或者同一时间出发多次不同的元素隐藏，每次隐藏跟显示都是根据这个id唯一标识来的， 字符串类型。 如果没有，默认就是 translatejs-text-hidden
			 */
			hide:function(id){
				const style = document.createElement('style');

				if(typeof(id) == 'undefined' || id == null || id.length == 0){
					id = 'translatejs-text-hidden';
					style.textContent = translate.visual.hideText.style;
				}else{
					//有值
					id = 'translatejs-text-hidden-'+id;
					style.textContent = translate.visual.hideText.style.replace(/translatejs-text-hidden/g, id).replace(/\/\*(.*)\*\//g, ' ').replace(/\n/g, ' ');
				}
				style.id = id;
				document.head.appendChild(style);
			    document.documentElement.classList.add(id);
			},
			/**
			 * 撤销隐藏状态，将原本的文本正常显示出来 
			 * 
			 * id 同 hide 的
			 */
			show:function(id){
				if(typeof(id) == 'undefined' || id == null || id.length == 0){
					id = 'translatejs-text-hidden';
				}else{
					//有值
					id = 'translatejs-text-hidden-'+id;
				}

				//删除html 的 class name
				document.documentElement.classList.remove(id);
				//删除 style
				var style_translatejs_text_hidden = document.getElementById(id);
				if(style_translatejs_text_hidden !== null){
					style_translatejs_text_hidden.remove();
				}
			}
		},

		/*
			这个主要是配合下面的，如果下面的 webPageLoadTranslateBeforeHiddenText 触发，则自动设置此处为true，为启用切换语种或刷新页面后先隐藏原本的文本
			它只是提供判断使用，不可直接设置操作
		*/
		webPageLoadTranslateBeforeHiddenText_use: false,

		/**
			 网页加载，且要进行翻译时，翻译之前，隐藏当前网页的文本。
			 当点击切换语言按钮后，会刷新当前页面，然后再进行翻译。 
			 这时会出现刷新当前页面后，会先显示原本的文本，然后再翻译为切换为的语种，体验效果有点欠缺。  
			 
			 这个得作用就是增强用户视觉的体验效果，在页面初始化加载时，如果判定需要翻译，那么会隐藏所有网页中的文本 。
			 他会先隐藏网页所有文本，然后再第一次 translate.execute 执行时，在扫描完节点，
			 	1. 将扫描到的几种语种的文本全部发送网络请求之后，（也就是已经触发了发送网络请求的文本node已经处于隐藏状态）， 才会去掉整个网页文本的隐藏。
				2. 在第一次 translate.execute 执行渲染完毕后，去掉整个网页文本的隐藏。
				3. 在 dom

			 这个需要在body标签之前执行，需要在head标签中执行此。也就是加载 translate.js 以及触发此都要放到head标签中

			 config 参数，配置项，默认不传
			 	{
					inHeadTip:true, 	//警告要在head中触发的控制台消息提醒，true是如果发现就打印这个提醒。 默认不设置便是true
			 	}
		 */
		webPageLoadTranslateBeforeHiddenText:function(config){
			// 该能力只需要在页面加载阶段启用一次，重复调用会重复注册生命周期回调。
			if(translate.visual.webPageLoadTranslateBeforeHiddenText_use === true){
				return;
			}

			if(typeof(config) == 'undefined'){
				config = {};
			}
			if(typeof(config.inHeadTip) == 'undefined'){
				config.inHeadTip = true;
			}
			
			//标记，当前启用整体隐藏文本的能力
			translate.visual.webPageLoadTranslateBeforeHiddenText_use = true;

			if(typeof(document.body) == 'undefined' || document.body == null){
				//正常，body还没加载
			}else{
				if(config.inHeadTip){
					translate.log('警告： translate.visual.webPageLoadTranslateBeforeHiddenText() 要在 head 标签中触发才能达到最好的效果！');
				}
			}
			if(translate.language.local == ''){
				translate.log('提醒：在使用 translate.visual.webPageLoadTranslateBeforeHiddenText() 之前，请先手动设置你的本地语种，参考： http://translate.zvo.cn/4066.html  如果你不设置本地语种，则不管你是否有切换语言，网页打开后都会先短暂的不显示文字');
			}

			if(translate.language.local == '' || translate.language.translateLocal == true || translate.language.local != translate.language.getCurrent()){
				//如果当前触发翻译，才会出现这个隐藏文本，因为取消隐藏必须要 translate.execute() 触发后才会取消隐藏

				translate.visual.hideText.hide();

				/*
				// 创建定时器，每10ms执行一次，以保持最顶层 html 标签上的class 不被项目或框架本身自动给覆盖掉
				//针对 60HZ刷新率，避免人眼视觉上出现屏闪，所以使用 10ms
				translate.visual.hideText.htmlAppendClassIntervalId = setInterval(function(){
					document.documentElement.classList.add('translatejs-text-hidden');
				}, 10);
				*/

				//设置发起网络请求前，记录发起了几次翻译请求，避免发起了多次，但是第一次执行完了就显示文本了，但是后几次还在翻译中，还是会出现显示原文的情况
				//translate.lifecycle.execute.translateNetworkBefore.push(function(uuid, from, to, texts){
				translate.lifecycle.execute.translateNetworkBefore.push(function(data){
					if(typeof(translate.visual.hideText.first_translate_request_uuid) == 'undefined'){ 
						//是第一次翻译请求，记录其uuid
						translate.visual.hideText.first_translate_request_uuid = data.uuid;
					}

					//只有第一次通过网络翻译接口请求才会记录uuid
					if(translate.visual.hideText.first_translate_request_uuid == data.uuid){
						if(typeof(translate.visual.hideText.first_translate_request_number) == 'undefined'){
							translate.visual.hideText.first_translate_request_number = 0;
						}
						translate.visual.hideText.first_translate_request_number++;
						//console.log('translate.visual.hideText.first_translate_request_number++   from:'+from+', ++ after number: '+translate.visual.hideText.first_translate_request_number);
					}
				});

				//设置翻译完成后，移除隐藏文本的css 的class name
				translate.lifecycle.execute.renderFinish.push(function(uuid, to){
					//console.log('renderFinish : '+uuid);
					if(typeof(translate.visual.hideText.first_translate_request_uuid) == 'undefined'){
						//为空，那么可能是已经触发过浏览器缓存了，所有翻译的文本在浏览器缓存中都有，就不必再发起网络请求了


					}else{
						//是发起过网络请求的，要计算请求数，所有的语种都翻译完后才能显示文本
						if(translate.visual.hideText.first_translate_request_uuid != uuid){
							//不是同一个uuid的，那也就是并不是第一次翻译了，而这个 webPageLoadTranslateBeforeHiddenText 针对的是页面加载后第一次翻译的避免原文一闪的情况 
							return;
						}
					}
					/*
					销毁定时器 - 不要删，预留
					if(typeof(translate.visual.hideText.htmlAppendClassIntervalId) != 'undefined'){
						clearInterval(translate.visual.hideText.htmlAppendClassIntervalId);
						console.log("translate.visual.hideText.htmlAppendClassIntervalId 已销毁 : "+translate.visual.hideText.htmlAppendClassIntervalId);
					}
					*/
					
					translate.visual.hideText.show();
				});
			}

			//translate.execute 触发执行结束触发
			translate.lifecycle.execute.finally.push(function(data){
			    if(data.triggerNumber < 3){
			    	//只有在第一次、第二次 触发后才会隐藏文本，这里避免只第一次，是万一第一次出现异常，网页在空白不显示内容了，多触发几次也不会影响多少性能。而且这个是对网页整体进行显示的，只有页面初始化打开的时候才会用到这个相关的隐藏跟显示， 正常网络请求使用的就不是这个了
					translate.visual.hideText.show();
					//console.log('隐藏 translate.visual.hideText.show();');
			    }
			});


		}

		



	},
	/*js translate.visual end*/

	/*
		历史， 20250924 增加

	*/
	history:{
		/*
			翻译文本相关，map的初始化在 translate.init() 中进行
			只有当正常翻译且翻译完成（成功）的，才会记录到这里
			比如 自定义忽略翻译文字  ‘你好’ ，元素的内容为 ‘你好世界’，它会将   你好、你好世界  这两个都加入进去
		*/
		translateText: {
			/*
				以翻译结果为 key 的 map
				value: 
					original 翻译的原文
			*/
			resultMap:null,
			/*
				以翻译原文为 key 的 map
				value: 
					result 翻译的结果
			*/
			originalMap:null,

			/*
				加入一条翻译记录
			*/
			add: function(original, result){
				//console.log(original +' - '+result);
				translate.history.translateText.resultMap.set(result, original);
				translate.history.translateText.originalMap.set(original, result);
			}
		},

	},
	
	/*
		记录打印翻译执行的耗时情况
	*/
	time:{
		// 执行 translate.execute() 的时间相关
		execute:{
			//true启用， false不启用，默认是不启用状态，不要直接调用，而是使用 translate.time.execute.start();
			isUse: false,

			/*
				key: uuid ，也就是 每次 translate.execute() 都会创建一个uuid
				value: 执行 translate.execute() 的耗时，分为几部分：
					all: 总耗时，单位是毫秒，从触发 translate.execute() 到所有的接口请求渲染完毕的耗时


			*/
			data: {},

			/*
				启动耗时打印
			*/
			start:function(){
				if(translate.time.execute.isUse){
					//已经启动过了，不需要再启动了
					translate.log('translate.time.execute.start() 已经启动过了，不需要再启动了');
					return;
				}

				translate.time.execute.isUse = true;
				translate.time.execute.data.isUse = true;

				//翻译开始
				translate.lifecycle.execute.start.push(function(uuid, to){
					if(typeof(translate.time.execute.data[uuid]) == 'undefined'){
						translate.time.execute.data[uuid] = {};
					}
					translate.time.execute.data[uuid].startTime = new Date().getTime();
				});

				//发起网络请求前
				translate.lifecycle.execute.translateNetworkBefore.push(function(data){
				    translate.time.execute.data[data.uuid].translateNetworkBeforeTime = new Date().getTime();
				});
				
				/*
				//发起网络请求后
				translate.lifecycle.execute.translateNetworkAfter.push(function(data){
				    translate.time.execute.data[data.uuid].translateNetworkBeforeTime = new Date().getTime();
				});
				*/

				//翻译完成（渲染全部语种都完成）
				translate.lifecycle.execute.renderFinish.push(function(uuid, to){
					translate.time.execute.data[uuid].finishTime = new Date().getTime();
					translate.time.execute.data[uuid].allTime = translate.time.execute.data[uuid].finishTime - translate.time.execute.data[uuid].startTime;
					

					/*** 取当前 translate.execute() 翻译，进行翻译的文本跟翻译的原语种 - start ***/
					var translateTexts = new Array(); //翻译的文本的数组，翻译的原文本
					var translateLanguages = new Array(); //翻译的语种数组

					var queueValue = translate.nodeQueue[uuid];
					//console.log(queueValue);
					for(var lang in translate.nodeQueue[uuid].list){
						if (!translate.nodeQueue[uuid].list.hasOwnProperty(lang)) {
				    		continue;
				    	}
						if(typeof(lang) != 'string' || lang.length < 1){
							continue;
						}

						translateLanguages.push(lang);
						
						for(var hash in translate.nodeQueue[uuid].list[lang]){
							if (!translate.nodeQueue[uuid].list[lang].hasOwnProperty(hash)) {
					    		continue;
					    	}
					    	translateTexts.push(translate.nodeQueue[uuid].list[lang][hash].original);
						}
						
					}
					
					//console.log(translateTexts)
					//console.log(translateLanguages)
					/*** 取当前 translate.execute() 翻译，进行翻译的文本跟翻译的原语种 - end ***/

					translate.time.execute.data[uuid].translateLanguages = translateLanguages;
					translate.time.execute.data[uuid].translateTexts = translateTexts;


					translate.log('[time][translate.execute()] '+translate.time.execute.data[uuid].allTime+'ms '+(typeof(translate.time.execute.data[uuid].translateNetworkBeforeTime) != 'undefined'? '(search&cache '+(translate.time.execute.data[uuid].translateNetworkBeforeTime - translate.time.execute.data[uuid].startTime)+'ms)':'')+ (translateTexts.length > 0 ?  (' , ['+translateLanguages+'] : ('+translateTexts.length+')['+translateTexts.slice(0, 3)+(translateTexts.length > 3 ? ', ...':'')+']'):''));
				});
			},
			
		},

		use:false, //true启用， false不启用，默认是不启用状态


		printTime: 0, //打印耗时大于这个的，默认是0，也就是全部打印。单位是毫秒。 比如设置为 100 ，则只打印耗时大于等于100毫秒的动作

		/**
		 * 增加一条日志记录
		 * functionName 触发调用此处log的方法名，传入如 translate.execute
		 * remark 备注文字，说明
		 */ 
		log:function(remark){
			if(translate.time.use == false){
				return;
			}

			var timestamp = new Date().getTime(); // 例如：1725053445123

			var usetime = 0; //跟上一次记录的间隔耗时，单位是毫秒
			if(typeof(translate.time.temp_lasttime) != 'undefined'){
				usetime = timestamp-translate.time.temp_lasttime;
			}
			translate.time.temp_lasttime = timestamp;
			
			if(usetime < translate.time.printTime){
				//不需要打印
				return; 
			}

			var functionName = '';
			try {
				// 创建一个Error对象来获取调用栈
				var error = new Error();
				// 解析调用栈，获取调用者信息
				// 不同环境下调用栈的索引可能不同，这里做了兼容处理
				var stackLines = error.stack.split('\n');
				//console.log(stackLines);
				var callerLine = stackLines[2] || stackLines[3]; // 兼容不同环境

				// 从调用栈中提取方法名
				var functionMatch = callerLine.match(/at (\S+)/);

				if (functionMatch && functionMatch[1]) {
				  functionName = functionMatch[1];
				}
			} catch (e) {
				// 如果获取调用栈失败，使用原始log方法
				translate.log(e);
			}

			functionName = functionName.replace('Object.','translate.');
			translate.log(functionName+'() '+usetime+' -> '+remark);
		}

	},

	/*
		容错
	*/
	faultTolerance: {

		// 优化文本节点创建的拦截逻辑
		// 在对 continew-admin-ui 框架进行适配时，发现有tip鼠标提示场景，而且是出现在table中的，一下就会出来十个，它的渲染跟 translate.listener.start(); 监听有几率会出现一直循环的情况，也就是 translate.listener.start(); 将文本翻译了，然后vue自动给渲染还原，然后 translate.listener.start(); 继续给翻译，造成性能损耗。这里就是处理这种情况的
		documentCreateTextNode: {
			/*
				原本的 document.createTextNode
				如果不为null，则是已开启，也就是已经触发了 translate.faultTolerance.documentCreateTextNode.enable();
				如果为null，则是未开启，有两种可能
						1. 未触发 translate.faultTolerance.documentCreateTextNode.enable();
						2. 触发了 translate.faultTolerance.documentCreateTextNode.disable();
			*/
			originalCreateTextNode: null,

			/*
				发生改动操作的文本节点
				key： node
				value： 
			*/
			node:null,

			/*
				启用此容错的能力
				如果触发此启用，那么会根据用户切换语言及设置，自动进行判定是否介入
			*/
			use:function(){
				// use() 只需要注册一次生命周期回调，重复调用会导致同一套容错逻辑重复执行。
				if(translate.faultTolerance.documentCreateTextNode.node != null){
					return;
				}
				// 文本节点可能被页面动态移除，使用 WeakMap 避免缓存强引用导致节点无法释放。
				translate.faultTolerance.documentCreateTextNode.node = new WeakMap();

				//当用户点击切换语言时触发
				translate.lifecycle.changeLanguage.push(function(to){
					if(translate.isTranslate(to)){
						//需要触发翻译
						translate.faultTolerance.documentCreateTextNode.enable();
						//console.log('translate.faultTolerance.documentCreateTextNode enable');
					}else{
						//不在翻译，禁用，释放
						translate.faultTolerance.documentCreateTextNode.disable();
						//console.log('translate.faultTolerance.documentCreateTextNode disable');
					}
				});

				//当第一次打开页面执行翻译时，触发
				translate.lifecycle.execute.start.push(function(data){
				    if(translate.executeNumber === 0){
				        //console.log('这是打开页面后，第一次触发 translate.execute() ，因为translate.executeNumber 记录的是translate.execute() 执行完的次数。');
				    	if(translate.isTranslate(data.to)){
				    		//console.log('data to -->'+data.to);
				    		//需要触发翻译
							translate.faultTolerance.documentCreateTextNode.enable();
				    	}
				    }
				});
			},

			/*
				启用
				可多次调用，如果多次调用，第一次启用，之后的都会不做任何处理
			*/
			enable: function(){

				//如果已开启，那就不需要再重复启用了
				if(translate.faultTolerance.documentCreateTextNode.originalCreateTextNode != null){
					return;
				}


				translate.faultTolerance.documentCreateTextNode.originalCreateTextNode = document.createTextNode;
				document.createTextNode = function(text) {
					var isTrans = false; //是否进行了翻译处理，true是
					var originalText = null; //原本要创建node的文本，如果 isTrans为true，这里才会赋予

					if(translate.executeTriggerNumber > 0){
						//已经触发过翻译执行了，那么才会启用这个能力

						if(typeof(text) === 'string' && text.length > 0){
							var textTranslateResult = translate.history.translateText.originalMap.get(text);
							if(typeof(textTranslateResult) === 'string' && textTranslateResult.length > 0){
								// 直接更新text
								originalText = text;
								text = textTranslateResult;
								//console.log('创建文本节点: '+textTranslateResult);
								isTrans = true;
							}
						}
					}

					// 创建文本节点 - 使用[text]数组代替arguments，使代码更明确和现代
					const textNode = translate.faultTolerance.documentCreateTextNode.originalCreateTextNode.call(this, text);
					if(isTrans){
						//console.log(textNode);
						translate.faultTolerance.documentCreateTextNode.node.set(textNode, {
							originalText: originalText,
							resultText: text
						});
						//将其记录到 translate.node.data
						translate.node.set(textNode,{
							attribute:"",
							originalText: originalText,
							resultText: text,
							translateTexts: {}, //这里因为直接从缓存中取的，没有走网络接口，所以这里直接空
							whole: true,
							translateResults: {
								[originalText]:1
							},
							lastTranslateRenderTime: Date.now()
						});
						
						
					}
					
					return textNode;
				};
			},
			/*
				禁用。不再做任何处理，释放性能
			*/
			disable: function(){
				if(translate.faultTolerance.documentCreateTextNode.originalCreateTextNode != null){
					document.createTextNode = translate.faultTolerance.documentCreateTextNode.originalCreateTextNode;
					translate.faultTolerance.documentCreateTextNode.originalCreateTextNode = null;
				}
			}
		}
	},

	/*
		快速接入，在head中引入使用，它集成了 translate.execute() 进去
		
		需要提前做的：
			//需要提前设置本地语种（当前网页的语种）
			translate.language.setLocal('chinese_simplified'); 
		
		建议做的：
			//设置机器翻译服务通道，相关说明参考 http://translate.zvo.cn/545867.html
		    translate.service.use('client.edge'); 

	*/
	quickUse:function(){
		//网页打开时自动隐藏文字，翻译完成后显示译文 http://translate.zvo.cn/549731.html
		translate.visual.webPageLoadTranslateBeforeHiddenText(); 

		//启用翻译中的遮罩层 http://translate.zvo.cn/407105.html
		translate.progress.api.startUITip(); 

	    //开启页面元素动态监控，js改变的内容也会被翻译，参考文档： http://translate.zvo.cn/4067.html
	    translate.listener.start(); 

	    //网页ajax请求触发自动翻译 http://translate.zvo.cn/4086.html
	    translate.request.listener.start();
	    
	    //url参数后可以加get方式传递 language 参数的方式控制当前网页以什么语种显示 http://translate.zvo.cn/4075.html
	    translate.language.setUrlParamControl(); 

	    //本地语种也进行强制翻译 http://translate.zvo.cn/289574.html
	    translate.language.translateLocal = true;

	    //元素的内容整体翻译能力配置 ，提高翻译的语义 https://translate.zvo.cn/4078.html
	    translate.whole.enableAll();

	    //dom加载完毕后立即触发翻译
	    document.addEventListener('DOMContentLoaded', function() {
	    	translate.execute();//完成翻译初始化，进行翻译

	    	setTimeout(function(){
	    		translate.execute();//完成翻译初始化，进行翻译
	    	}, 500);
	    	setTimeout(function(){
	    		translate.execute();//完成翻译初始化，进行翻译
	    	}, 2000);
		});

	},

	/*js translate.recycle start*/
	/*
		自动回收 translate.js 本身记录的相关信息，避免某些页面有循环触发，停留页面过长导致数据持续堆积

		translate.nodeQueue
		translate.node.data
		
	*/
	recycle: function(){
		var currentTime = new Date().getTime(); //当前时间
		//var before_second = 120; 	//要删除往前多少秒的数据

		
		/*** translate.nodeQueue ****/
		var nodeQueueDeleteArray = []; //要删除的nodeQueue，其中存储的是 uuid
		for(var uuid in translate.nodeQueue){
			if (!translate.nodeQueue.hasOwnProperty(uuid)) {
	    		continue;
	    	}
			var expireTime = translate.nodeQueue[uuid].expireTime;
			if(expireTime+120 < currentTime){
				nodeQueueDeleteArray.push(uuid);
			}	
		}
		for(var qi = 0; qi < nodeQueueDeleteArray.length; qi++){
			delete translate.nodeQueue[nodeQueueDeleteArray[qi]];
			//console.log('delete nodeQueue -> '+nodeQueueDeleteArray[qi]);
		}


		/*** translate.node.data ****/
		translate.node.refresh();

		/*** translate.time.execute.data ****/
		var timeExecuteDeleteArray = []; //要删除的，其中存储的是 uuid
		for(var uuid in translate.time.execute.data){
			if (!translate.time.execute.data.hasOwnProperty(uuid)) {
	    		continue;
	    	}
	    	if(typeof(translate.time.execute.data[uuid].finishTime) == 'undefined'){
	    		//还没执行完
	    		continue;
	    	}
			var finishTime = translate.time.execute.data[uuid].finishTime;
			if(finishTime+120 < currentTime){
				timeExecuteDeleteArray.push(uuid);
			}	
		}
		for(var ti = 0; ti < timeExecuteDeleteArray.length; ti++){
			delete translate.time.execute.data[timeExecuteDeleteArray[ti]];
			//console.log('delete translate.time.execute.data -> '+timeExecuteDeleteArray[ti]);
		}
		


	},
	/*js translate.recycle end*/

	/*js translate.debug start*/
	debug: {

		loadDebugJs: function(func, debugJsUrl){
			if(typeof(debugJsUrl) !== 'string' || debugJsUrl.length < 5){
				debugJsUrl = 'https://translate.zvo.cn/static/debug.min.js';
			}
			if(typeof(translate.debug.data) === 'undefined'){
				//载入 translate_debug.js

				//if(window.location.protocol.toLowerCase() === 'file:'){
					if(debugJsUrl.indexOf('file') !== 0){
						//alert('您当前的页面是file协议，请手动下载 https://translate.zvo.cn/static/debug.min.js 这个js文件，然后传入 translate.debug.showUIDialog(\'file://a/b/debug.min.js\'); 使用')
					}
					// 1. 创建script标签
				    const script = document.createElement('script');
				    script.src = debugJsUrl;
				    script.onload = script.onreadystatechange = function() {
				    	func();
				    }
				    document.head.appendChild(script);
				//}else{
				//	translate.util.synchronizesLoadJs(debugJsUrl);
				//}

			}
		},

		/*
			显示debug 的 UI对话界面
		*/
		use: function(debugJsUrl){
			translate.debug.loadDebugJs(function(){
				translate.debug.showUIDialog();
			}, debugJsUrl);
		}
	},
	/*js translate.debug end*/

	/*js translate.init start*/
	/*
		初始化，如版本检测、初始数据加载、map声明、监听启动 等
		会自动在 translate.js 加载完自动触发执行
	*/
	init:function(){
		
		// 确保初始化只进行一次
		if(typeof(translate.init_execute) != 'undefined'){
			return;
		}
		translate.init_execute = '已进行';

		//初始化 translate.node.data
		if(translate.node.data == null){
			translate.node.data = new Map();
		}
		//初始化 历史
		if(translate.history.translateText.resultMap == null){
			translate.history.translateText.resultMap = new Map();
		}
		if(translate.history.translateText.originalMap == null){
			translate.history.translateText.originalMap = new Map();
		}
		//语系相关
		if(translate.language.name == null){
			translate.language.generateLanguageNameObject();
		}

		//监听，当第一次触发 translate.execute() 时，执行
		translate.lifecycle.execute.start.push(function(uuid, to){
			//只在第一次触发时，才会做一些初始化
			if(typeof(translate.init_first_trigger_execute) != 'undefined'){
				return;
			}
			translate.init_first_trigger_execute = 1;

			//将自定义术语加入 translate.history.translateTexts 中
			//console.log(translate.nomenclature.data);
			for(var currentLanguage in translate.nomenclature.data){
				if (!translate.nomenclature.data.hasOwnProperty(currentLanguage)) {
		    		continue;
		    	}
		    	for(var targetLanguage in translate.nomenclature.data[currentLanguage]){
					if (!translate.nomenclature.data[currentLanguage].hasOwnProperty(targetLanguage)) {
			    		continue;
			    	}
			    	for(var originalText in translate.nomenclature.data[currentLanguage][targetLanguage]){
						if (!translate.nomenclature.data[currentLanguage][targetLanguage].hasOwnProperty(originalText)) {
				    		continue;
				    	}
				    	translate.history.translateText.add(originalText, translate.nomenclature.data[currentLanguage][targetLanguage][originalText]);
				    }
			    }
		    }
		   	//将忽略翻译的文本（固定的，非正则）加入 translate.history.translateTexts 中
		    for(var ignore_i = 0; ignore_i < translate.ignore.text.length; ignore_i++){
		    	translate.history.translateText.add(translate.ignore.text[ignore_i], translate.ignore.text[ignore_i]);
		    }
			
			

			//进行判断，DOM是否加载完成了，如果未加载完成就触发了 translate.execute 执行，那么弹出警告提示
			if(document.readyState == 'loading'){
				translate.log('WARNING : The dom triggered translate.exece() before it was fully loaded, which does not comply with usage standards. The execution of translate.exece() should be done after the DOM is loaded');
				translate.log('警告：DOM 在完全加载之前触发了 translate.execute() ，这不符合使用规范，容易出现异常。你应该检查一下你的代码，translate.execute() 的执行应该在DOM加载完成后');
			}
			

			//translate.listener.start() 的触发。
			if(translate.listener.use == true && translate.listener.isStart == false){
				if(typeof(translate.listener.start) != 'undefined'){
					translate.listener.addListener();
				}
			}

			//translate.request.lilstener.start() 触发
			if(translate.request.listener.use == true && translate.request.listener.isStart == false){
				translate.request.listener.addListener();
			}
		});

		//产生的数据回收，避免一直扩大占用内存
		if(typeof(translate.recycle) != 'undefined'){
			// 创建定时器，每1分钟执行一次 translate.recycle 进行清理数据存储
			setInterval(translate.recycle, 60 * 1000);
		}

		//初始化 postMessage 监听器，用于跨域 iframe 通信
		translate.postMessage.init();
	},
	/*js translate.init end*/

}
/*
	将页面中的所有node节点，生成其在当前页面的唯一标识字符串uuid
	开源仓库： https://github.com/xnx3/nodeuuid.js
	原理： 当前节点的nodeName + 当前节点在父节点下，属于第几个 tagName ，然后追个向父级进行取，将node本身+父级+父父级+.... 拼接在一起
	注意，如果动态添加一个节点到第一个，那么其他节点就会挤下去导致节点标记异常
*/
var nodeuuid = {
	index:function(node){
		var parent = node.parentElement;
        if(parent == null){
          return '';
        }

        var childs;
        if(typeof(node.tagName) == 'undefined'){
        	//console.log('undefi');
        	childs = parent.childNodes;
        	//console.log(Array.prototype.indexOf.call(childs, node));
        }else{
        	// 使用querySelectorAll()方法获取所有与node元素相同标签名的子节点
	        //childs = parent.querySelectorAll(node.tagName);

	        // 不使用querySelectorAll，手动遍历子节点来找到相同标签名的子节点
            childs = [];
            var allChilds = parent.childNodes;
            for (var i = 0; i < allChilds.length; i++) {
                if (allChilds[i].tagName === node.tagName) {
                    childs.push(allChilds[i]);
                }
            }
        }
        var index = Array.prototype.indexOf.call(childs, node); 
        //console.log('--------'+node.tagName);
        return node.nodeName + "" + (index+1);
	},
	uuid:function(node){
		var uuid = '';
		var n = node;
		while(n != null){
			var id = nodeuuid.index(n);
			//console.log(id);
			if(id != ''){
				if(uuid != ''){
					uuid = '_'+uuid;
				}
				uuid = id + uuid;
			}
			//console.log(uuid)
			n = n.parentElement;
		}
		return uuid;
	}
}


/*js copyright-notice start*/
//延迟触发，方便拦截自定义
setTimeout(function(){
	translate.log('------ translate.js ------\nTwo lines of js html automatic translation, page without change, no language configuration file, no API Key, SEO friendly! Open warehouse : https://github.com/xnx3/translate \n两行js实现html全自动翻译。 无需改动页面、无语言配置文件、无API Key、对SEO友好！完全开源，代码仓库：https://gitee.com/mail_osc/translate');
}, 3000);
/*js copyright-notice end*/

//初始化
try{
	translate.init();
}catch(e){  }

/*js amd-cmd-commonjs start*/
/*兼容 AMD、CMD、CommonJS 规范 - start*/
/**
 * 兼容 AMD、CMD、CommonJS 规范
 * node 环境使用：`npm i i18n-jsautotranslate` 安装包
 */
;(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define([], () => factory());
  } else if (typeof module === 'object' && module.exports) {
    module.exports = factory();
  } else {
  	if(typeof(root) != 'undefined'){
		root['translate'] = factory();
	}
  }
})(this, function () {
  return translate;
});
/*兼容 AMD、CMD、CommonJS 规范 - end*/
/*js amd-cmd-commonjs end*/
