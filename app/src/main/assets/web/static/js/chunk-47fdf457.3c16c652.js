(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-47fdf457"],{5831:function(e,s,t){"use strict";var a=t("53ca"),l=(t("fb6a"),t("e9c4"),t("d3b7"),t("caad"),t("2532"),t("bc3a")),c=t.n(l),i=t("5c96"),r=t("3048"),d=t("1bb3"),n=t("e6f3"),o=t("9ee1"),_=t("1908"),v={show:!1};c.a.defaults.headers["Content-Type"]="application/json;charset=utf-8";var u=c.a.create({baseURL:"",timeout:1e4});u.interceptors.request.use((function(e){var s=!1===(e.headers||{}).isToken,t=!1===(e.headers||{}).repeatSubmit;if(Object(d["a"])()&&!s&&(e.headers["Authorization"]="Bearer "+Object(d["a"])()),"get"===e.method&&e.params){var l=e.url+"?"+Object(o["k"])(e.params);l=l.slice(0,-1),e.params={},e.url=l}if(!t&&("post"===e.method||"put"===e.method)){var c={url:e.url,data:"object"===Object(a["a"])(e.data)?JSON.stringify(e.data):e.data,time:(new Date).getTime()},i=_["a"].session.getJSON("sessionObj");if(void 0===i||null===i||""===i)_["a"].session.setJSON("sessionObj",c);else{var r=i.url,n=i.data,v=i.time,u=1e3;if(n===c.data&&c.time-v<u&&r===c.url){var m="数据正在处理，请勿重复提交";return console.warn("[".concat(r,"]: ")+m),Promise.reject(new Error(m))}_["a"].session.setJSON("sessionObj",c)}}return e}),(function(e){console.log(e),Promise.reject(e)})),u.interceptors.response.use((function(e){var s=e.data.code||200,t=n["a"][s]||e.data.msg||n["a"]["default"];return"blob"===e.request.responseType||"arraybuffer"===e.request.responseType?e.data:401===s?(v.show||(v.show=!0,i["MessageBox"].confirm("登录状态已过期，您可以继续留在该页面，或者重新登录","系统提示",{confirmButtonText:"重新登录",cancelButtonText:"取消",type:"warning"}).then((function(){v.show=!1,r["a"].dispatch("LogOut").then((function(){location.href="/index"}))})).catch((function(){v.show=!1}))),Promise.reject("无效的会话，或者会话已过期，请重新登录。")):500===s?(Object(i["Message"])({message:t,type:"error"}),Promise.reject(new Error(t))):200!==s?(i["Notification"].error({title:t}),Promise.reject("error")):e.data}),(function(e){console.log("err"+e);var s={},t=e.message;return"Network Error"==t?(t="后端接口连接异常",s.code=500):t.includes("timeout")?(t="系统接口请求超时",s.code=500):t.includes("Request failed with status code")&&(e.response&&e.response.data&&!e.response.data.msg?(t=e.response.data.msg,s.code=500):(t="系统接口"+t.substr(t.length-3)+"异常",s.code=e.response.status)),s.message=t,s})),s["a"]=u},7726:function(e,s,t){"use strict";t.d(s,"b",(function(){return i})),t.d(s,"e",(function(){return r})),t.d(s,"f",(function(){return d})),t.d(s,"d",(function(){return n})),t.d(s,"c",(function(){return o})),t.d(s,"a",(function(){return _}));var a=t("70f0"),l=t("5831"),c=t("1bb3");function i(){var e={commandId:"maintain-req-device-info"};return Object(a["a"])({url:"/maintain/custom",method:"post",data:e})}function r(e){var s={commandId:"maintain-req-restart-app",data:{token:Object(c["a"])(),password:e}};return Object(a["a"])({url:"/maintain/custom",method:"post",data:s})}function d(e){var s={commandId:"maintain-req-restart-system",data:{token:Object(c["a"])(),password:e}};return Object(a["a"])({url:"/maintain/custom",method:"post",data:s})}function n(e){var s={commandId:"maintain-req-reset-system",data:{token:Object(c["a"])(),password:e}};return Object(a["a"])({url:"/maintain/custom",method:"post",data:s})}function o(e,s){var t={commandId:"maintain-req-recover-config",data:{recoverFilePath:e,recoverType:s}};return Object(a["a"])({url:"/maintain/custom",method:"post",data:t})}function _(){var e={commandId:"maintain-req-is-alive"};return Object(l["a"])({url:"/maintain/custom",method:"post",data:e})}},e69f:function(e,s,t){"use strict";t.r(s);var a=function(){var e=this,s=e.$createElement,t=e._self._c||s;return t("div",{staticClass:"app-container"},[t("el-row",[t("el-col",{staticClass:"card-box",attrs:{span:12}},[t("el-card",[t("div",{attrs:{slot:"header"},slot:"header"},[t("span",[e._v("CPU")])]),t("div",{staticClass:"el-table el-table--enable-row-hover el-table--medium"},[t("table",{staticStyle:{width:"100%"},attrs:{cellspacing:"0"}},[t("thead",[t("tr",[t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("属性")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("值")])])])]),t("tbody",[t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("核心数")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.cpu?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.cpu.cpuNum)+" ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("用户使用率")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.cpu?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.cpu.used)+"% ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("系统使用率")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.cpu?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.cpu.sys)+"% ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("当前空闲率")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.cpu?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.cpu.free)+"% ")]):e._e()])])])])])])],1),t("el-col",{staticClass:"card-box",attrs:{span:12}},[t("el-card",[t("div",{attrs:{slot:"header"},slot:"header"},[t("span",[e._v("内存")])]),t("div",{staticClass:"el-table el-table--enable-row-hover el-table--medium"},[t("table",{staticStyle:{width:"100%"},attrs:{cellspacing:"0"}},[t("thead",[t("tr",[t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("属性")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("内存")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("JVM")])])])]),t("tbody",[t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("总内存")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.mem?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.mem.total)+"G ")]):e._e()]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.jvm?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.jvm.total)+"M ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("已用内存")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.mem?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.mem.used)+"G ")]):e._e()]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.jvm?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.jvm.used)+"M ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("剩余内存")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.mem?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.mem.free)+"G ")]):e._e()]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.jvm?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.jvm.free)+"M ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("使用率")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.mem?t("div",{staticClass:"cell",class:{"text-danger":e.server.mem.usage>80}},[e._v(" "+e._s(e.server.mem.usage)+"% ")]):e._e()]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.jvm?t("div",{staticClass:"cell",class:{"text-danger":e.server.jvm.usage>80}},[e._v(" "+e._s(e.server.jvm.usage)+"% ")]):e._e()])])])])])])],1),t("el-col",{staticClass:"card-box",attrs:{span:24}},[t("el-card",[t("div",{attrs:{slot:"header"},slot:"header"},[t("span",[e._v("设备信息")])]),t("div",{staticClass:"el-table el-table--enable-row-hover el-table--medium"},[t("table",{staticStyle:{width:"100%"},attrs:{cellspacing:"0"}},[t("tbody",[t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("设备ID")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.sys?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.sys.computerName)+" ")]):e._e()]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("操作系统")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.sys?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.sys.osName)+" ")]):e._e()])]),t("tr",[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("设备IP")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.sys?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.sys.computerIp)+" ")]):e._e()]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("系统架构")])]),t("td",{staticClass:"el-table__cell is-leaf"},[e.server.sys?t("div",{staticClass:"cell"},[e._v(" "+e._s(e.server.sys.osArch)+" ")]):e._e()])])])])])])],1),t("el-col",{staticClass:"card-box",attrs:{span:24}},[t("el-card",[t("div",{attrs:{slot:"header"},slot:"header"},[t("span",[e._v("磁盘状态")])]),t("div",{staticClass:"el-table el-table--enable-row-hover el-table--medium"},[t("table",{staticStyle:{width:"100%"},attrs:{cellspacing:"0"}},[t("thead",[t("tr",[t("th",{staticClass:"el-table__cell el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("盘符路径")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("盘符类型")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("总大小")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("可用大小")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("已用大小")])]),t("th",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v("已用百分比")])])])]),e.server.sysFiles?t("tbody",e._l(e.server.sysFiles,(function(s,a){return t("tr",{key:a},[t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v(e._s(s.dirName))])]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v(e._s(s.typeName))])]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v(e._s(s.total))])]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v(e._s(s.free))])]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell"},[e._v(e._s(s.used))])]),t("td",{staticClass:"el-table__cell is-leaf"},[t("div",{staticClass:"cell",class:{"text-danger":s.usage>80}},[e._v(" "+e._s(s.usage)+"% ")])])])})),0):e._e()])])])],1)],1)],1)},l=[],c=t("7726"),i={name:"Server",data:function(){return{server:[]}},created:function(){this.getList(),this.openLoading()},methods:{getList:function(){var e=this;Object(c["b"])().then((function(s){e.server=s.data,e.$modal.closeLoading()})).catch((function(s){e.$modal.closeLoading()}))},openLoading:function(){this.$modal.loading("正在加载服务监控数据，请稍候！")}}},r=i,d=t("2877"),n=Object(d["a"])(r,a,l,!1,null,null,null);s["default"]=n.exports}}]);