(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-556d4022"],{5831:function(t,e,s){"use strict";var a=s("53ca"),n=(s("fb6a"),s("e9c4"),s("d3b7"),s("caad"),s("2532"),s("bc3a")),o=s.n(n),r=s("5c96"),i=s("3048"),c=s("1bb3"),m=s("e6f3"),u=s("9ee1"),l=s("1908"),d={show:!1};o.a.defaults.headers["Content-Type"]="application/json;charset=utf-8";var p=o.a.create({baseURL:"",timeout:1e4});p.interceptors.request.use((function(t){var e=!1===(t.headers||{}).isToken,s=!1===(t.headers||{}).repeatSubmit;if(Object(c["a"])()&&!e&&(t.headers["Authorization"]="Bearer "+Object(c["a"])()),"get"===t.method&&t.params){var n=t.url+"?"+Object(u["k"])(t.params);n=n.slice(0,-1),t.params={},t.url=n}if(!s&&("post"===t.method||"put"===t.method)){var o={url:t.url,data:"object"===Object(a["a"])(t.data)?JSON.stringify(t.data):t.data,time:(new Date).getTime()},r=l["a"].session.getJSON("sessionObj");if(void 0===r||null===r||""===r)l["a"].session.setJSON("sessionObj",o);else{var i=r.url,m=r.data,d=r.time,p=1e3;if(m===o.data&&o.time-d<p&&i===o.url){var h="数据正在处理，请勿重复提交";return console.warn("[".concat(i,"]: ")+h),Promise.reject(new Error(h))}l["a"].session.setJSON("sessionObj",o)}}return t}),(function(t){console.log(t),Promise.reject(t)})),p.interceptors.response.use((function(t){var e=t.data.code||200,s=m["a"][e]||t.data.msg||m["a"]["default"];return"blob"===t.request.responseType||"arraybuffer"===t.request.responseType?t.data:401===e?(d.show||(d.show=!0,r["MessageBox"].confirm("登录状态已过期，您可以继续留在该页面，或者重新登录","系统提示",{confirmButtonText:"重新登录",cancelButtonText:"取消",type:"warning"}).then((function(){d.show=!1,i["a"].dispatch("LogOut").then((function(){location.href="/index"}))})).catch((function(){d.show=!1}))),Promise.reject("无效的会话，或者会话已过期，请重新登录。")):500===e?(Object(r["Message"])({message:s,type:"error"}),Promise.reject(new Error(s))):200!==e?(r["Notification"].error({title:s}),Promise.reject("error")):t.data}),(function(t){console.log("err"+t);var e={},s=t.message;return"Network Error"==s?(s="后端接口连接异常",e.code=500):s.includes("timeout")?(s="系统接口请求超时",e.code=500):s.includes("Request failed with status code")&&(t.response&&t.response.data&&!t.response.data.msg?(s=t.response.data.msg,e.code=500):(s="系统接口"+s.substr(s.length-3)+"异常",e.code=t.response.status)),e.message=s,e})),e["a"]=p},a6b5:function(t,e,s){"use strict";s.r(e);var a=function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"app-container"},[s("el-row",[s("el-button",{attrs:{type:"primary"},on:{click:t.pingTest}},[t._v("ping测试")]),s("el-button",{attrs:{type:"success"},on:{click:t.topTest}},[t._v("top测试")]),s("el-button",{attrs:{type:"info"},on:{click:t.memTest}},[t._v("查看内存")]),s("el-button",{attrs:{type:"warning"},on:{click:t.diskTest}},[t._v("查看磁盘空间")]),s("el-button",{attrs:{type:"danger"},on:{click:t.customTest}},[t._v("自定义")]),t.needUserCommand?s("el-button",{attrs:{type:"primary",round:""},on:{click:t.startTest}},[t._v(t._s(t.commandBtnName))]):t._e()],1),t.needUserCommand?s("el-row",{staticStyle:{"margin-top":"10px"}},[s("el-input",{attrs:{placeholder:t.placeHolder},model:{value:t.testUserInputCommand,callback:function(e){t.testUserInputCommand=e},expression:"testUserInputCommand"}})],1):t._e(),s("el-row",{staticStyle:{"margin-top":"10px"}},[s("el-input",{attrs:{type:"textarea",autosize:{minRows:10,maxRows:100},placeholder:""},model:{value:t.testResult,callback:function(e){t.testResult=e},expression:"testResult"}})],1)],1)},n=[],o=s("70f0"),r=s("5831");function i(t,e){var s={commandId:"maintain-req-test",data:{testCommand:t,startTest:e}};return Object(o["a"])({url:"/maintain/custom",method:"post",data:s})}function c(){var t={commandId:"maintain-req-test-status"};return Object(r["a"])({url:"/maintain/custom",method:"post",data:t})}var m=2,u={preStatus:null,checkTimeInterval:2e3,checkTimer:null,statusCallback:null,init:function(){u.stopCheck()},startCheck:function(t,e){u.stopCheck(),u.statusCallback=e,i(t,!0).then((function(t){null==u.checkTimer&&(u.checkTimer=setInterval((function(){c().then((function(t){if(200===t.code){var e=t.data.status;u.preStatus!=e&&(u.preStatus=e,u.statusCallback&&u.statusCallback(t.data.status,t.data.result),e===m&&u.stopCheck())}})).catch((function(t){console.log(t)}))}),u.checkTimeInterval))}))},stopCheck:function(){null!=u.checkTimer&&(clearInterval(u.checkTimer),u.checkTimer=null,i("",!1)),u.preStatus=null}},l=u,d=0,p=1,h="ping -c 10 ",f="top -n 5",C="free -m",b="df -h",k="",g={name:"resetSystem",data:function(){return{testResult:"",testCommand:h,testUserInputCommand:"",placeHolder:"请输入要ping的地址",needUserCommand:!0,commandBtnName:"开始ping测试"}},created:function(){},methods:{pingTest:function(){this.testCommand=h,this.placeHolder="请输入要ping的地址",this.commandBtnName="开始ping测试",this.needUserCommand=!0},topTest:function(){this.testCommand=f,this.testUserInputCommand="",this.needUserCommand=!1,this.startTest()},memTest:function(){this.testCommand=C,this.testUserInputCommand="",this.needUserCommand=!1,this.startTest()},diskTest:function(){this.testCommand=b,this.testUserInputCommand="",this.needUserCommand=!1,this.startTest()},customTest:function(){this.testCommand=k,this.testUserInputCommand="",this.placeHolder="请输入测试命令",this.commandBtnName="开始自定义测试",this.needUserCommand=!0},startTest:function(){if(!this.needUserCommand||this.testUserInputCommand){var t=this.testCommand+this.testUserInputCommand;this.$modal.loading("测试中，请稍候！"),l.startCheck(t,this.onTestCallback)}else this.msgError("测试内容不能为空")},onTestCallback:function(t,e){t===d?this.$modal.closeLoading():t===p?this.testResult="测试中":(this.$modal.closeLoading(),this.testResult=e||"测试结束，但结果为空")}}},T=g,v=s("2877"),w=Object(v["a"])(T,a,n,!1,null,null,null);e["default"]=w.exports}}]);