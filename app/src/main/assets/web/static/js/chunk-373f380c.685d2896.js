(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-373f380c"],{"04d1":function(t,e,a){var r=a("342f"),i=r.match(/firefox\/(\d+)/i);t.exports=!!i&&+i[1]},"0bb7":function(t,e,a){"use strict";a.d(e,"a",(function(){return o})),a.d(e,"b",(function(){return c}));var r=a("70f0"),i=a("b0bd");function o(){var t={};return Object(r["a"])({url:"/business/custom",method:"post",data:Object(i["a"])("req-list-dict",t)})}function c(t){var e={responseList:{updateType:1,totalLength:t.length,list:t,currentPage:0,pageSize:t.length},lastUpdateTime:(new Date).getTime()};return Object(r["a"])({url:"/business/custom",method:"post",data:Object(i["a"])("req-update-dict",e)})}},"0be5":function(t,e,a){},"4e82":function(t,e,a){"use strict";var r=a("23e7"),i=a("e330"),o=a("59ed"),c=a("7b0b"),s=a("07fa"),n=a("577e"),l=a("d039"),u=a("addb"),d=a("a640"),p=a("04d1"),f=a("d998"),h=a("2d00"),b=a("512ce"),m=[],y=i(m.sort),g=i(m.push),v=l((function(){m.sort(void 0)})),k=l((function(){m.sort(null)})),x=d("sort"),D=!l((function(){if(h)return h<70;if(!(p&&p>3)){if(f)return!0;if(b)return b<603;var t,e,a,r,i="";for(t=65;t<76;t++){switch(e=String.fromCharCode(t),t){case 66:case 69:case 70:case 72:a=3;break;case 68:case 71:a=4;break;default:a=2}for(r=0;r<47;r++)m.push({k:e+r,v:a})}for(m.sort((function(t,e){return e.v-t.v})),r=0;r<m.length;r++)e=m[r].k.charAt(0),i.charAt(i.length-1)!==e&&(i+=e);return"DGBEFHACIJK"!==i}})),w=v||!k||!x||!D,C=function(t){return function(e,a){return void 0===a?-1:void 0===e?1:void 0!==t?+t(e,a)||0:n(e)>n(a)?1:-1}};r({target:"Array",proto:!0,forced:w},{sort:function(t){void 0!==t&&o(t);var e=c(this);if(D)return void 0===t?y(e):y(e,t);var a,r,i=[],n=s(e);for(r=0;r<n;r++)r in e&&g(i,e[r]);u(i,C(t)),a=i.length,r=0;while(r<a)e[r]=i[r++];while(r<n)delete e[r++];return e}})},"512ce":function(t,e,a){var r=a("342f"),i=r.match(/AppleWebKit\/(\d+)\./);t.exports=!!i&&+i[1]},addb:function(t,e,a){var r=a("f36a"),i=Math.floor,o=function(t,e){var a=t.length,n=i(a/2);return a<8?c(t,e):s(t,o(r(t,0,n),e),o(r(t,n),e),e)},c=function(t,e){var a,r,i=t.length,o=1;while(o<i){r=o,a=t[o];while(r&&e(t[r-1],a)>0)t[r]=t[--r];r!==o++&&(t[r]=a)}return t},s=function(t,e,a,r){var i=e.length,o=a.length,c=0,s=0;while(c<i||s<o)t[c+s]=c<i&&s<o?r(e[c],a[s])<=0?e[c++]:a[s++]:c<i?e[c++]:a[s++];return t};t.exports=o},b0bd:function(t,e,a){"use strict";function r(t,e){var a={deviceType:7,deviceId:null,parentNo:null,phoneNo:null,commandId:t,ipAddress:null,macAddress:null,data:e};return a}a.d(e,"a",(function(){return r}))},c988:function(t,e,a){"use strict";a.r(e);var r=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("div",[a("el-button",{attrs:{type:"success",plain:""},on:{click:t.handleAdd}},[t._v("添加字典元素")]),a("el-button",{staticStyle:{"margin-righ":"10px"},attrs:{type:"primary",plain:""},on:{click:t.handleSubmit}},[t._v("保存设置")]),a("el-tabs",{staticStyle:{"margin-top":"10px"},attrs:{type:"card"},model:{value:t.tabsActiveName,callback:function(e){t.tabsActiveName=e},expression:"tabsActiveName"}},t._l(t.tabOptions,(function(e,r){return a("el-tab-pane",{key:r,attrs:{label:e.label,name:e.key}},[a("el-table",{staticStyle:{width:"100%"},attrs:{data:e.dicts}},[a("el-table-column",{attrs:{prop:"dictValue",label:"字典值",width:"180"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-input",{class:{dictAlarmBorder:e.row.dictValueBorder},staticStyle:{width:"60px"},attrs:{type:"number"},model:{value:e.row.dictValue,callback:function(a){t.$set(e.row,"dictValue",a)},expression:"scope.row.dictValue"}})]}}],null,!0)}),a("el-table-column",{attrs:{prop:"displayName",label:"显示名称"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-input",{class:{dictAlarmBorder:e.row.displayNameBorder},staticStyle:{width:"180px"},model:{value:e.row.displayName,callback:function(a){t.$set(e.row,"displayName",a)},expression:"scope.row.displayName"}})]}}],null,!0)}),a("el-table-column",{attrs:{prop:"backgroundColor",label:"背景颜色"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-color-picker",{attrs:{"show-alpha":t.colorPickerShowAlpha},on:{change:function(a){return t.changeBackgroundColor(e.row)}},model:{value:e.row.backgroundColorHex,callback:function(a){t.$set(e.row,"backgroundColorHex",a)},expression:"scope.row.backgroundColorHex"}})]}}],null,!0)}),a("el-table-column",{attrs:{prop:"textColor",label:"字体颜色"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-color-picker",{attrs:{"show-alpha":t.colorPickerShowAlpha},on:{change:function(a){return t.changeTextColor(e.row)}},model:{value:e.row.textColorHex,callback:function(a){t.$set(e.row,"textColorHex",a)},expression:"scope.row.textColorHex"}})]}}],null,!0)}),a("el-table-column",{attrs:{prop:"sort",label:"排序"}}),a("el-table-column",{attrs:{label:"样例"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-tag",{style:{color:e.row.textColorHex},attrs:{color:e.row.backgroundColorHex,effect:"dark"}},[t._v(" "+t._s(e.row.displayName)+" ")])]}}],null,!0)}),a("el-table-column",{attrs:{label:"操作",width:"200"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){return t.handleMoveUp(e.row)}}},[t._v("上移")]),a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){return t.handleMoveDown(e.row)}}},[t._v("下移")]),a("el-button",{attrs:{type:"text",size:"small"},on:{click:function(a){return t.handleDelete(e.row)}}},[t._v("删除")])]}}],null,!0)})],1)],1)})),1)],1)])},i=[],o=(a("d3b7"),a("159b"),a("4e82"),a("ac1f"),a("1276"),a("5319"),a("fb6a"),a("25f0"),a("b680"),a("a434"),a("0bb7")),c="sex",s="metering",n="nursing_level",l="critical_type",u="diet",d="allergy",p="medical_insurance_type",f="blood_type",h="protection",b="slave_place",m={name:"Config",data:function(){return{dictData:{sexDictList:[],meteringDictList:[],nursingLevelDictList:[],criticalTypeDictList:[],dietDictList:[],allergyDictList:[],medicalInsuranceTypeDictList:[],protectionDictList:[],bloodTypeDictList:[],placeDictList:[]},tabsActiveName:c,tabOptions:[{key:c,label:"性别",dicts:[]},{key:s,label:"计量",dicts:[]},{key:n,label:"护理等级",dicts:[]},{key:l,label:"危重类型",dicts:[]},{key:u,label:"饮食",dicts:[]},{key:d,label:"过敏",dicts:[]},{key:p,label:"保险类型",dicts:[]},{key:f,label:"血型",dicts:[]},{key:h,label:"防护类型",dicts:[]},{key:b,label:"分机位置",dicts:[]}],colorPickerShowAlpha:!0}},created:function(){this.getList()},methods:{getList:function(){var t=this;Object(o["a"])().then((function(e){200===e.code&&(t.dictData=e.data,t.tabOptions.forEach((function(e){e.dicts=t.getDictsByKey(e.key),e.dicts.forEach((function(e){e.dictValueBorder=!1,e.displayNameBorder=!1,e.textColorHex=t.formatHexToRgba(t.formatColorToHex(e.textColor)),e.backgroundColorHex=t.formatHexToRgba(t.formatColorToHex(e.backgroundColor))}))})))}))},getDictsByKey:function(t){switch(t){case c:return this.dictData.sexDictList;case s:return this.dictData.meteringDictList;case n:return this.dictData.nursingLevelDictList;case l:return this.dictData.criticalTypeDictList;case u:return this.dictData.dietDictList;case d:return this.dictData.allergyDictList;case p:return this.dictData.medicalInsuranceTypeDictList;case f:return this.dictData.bloodTypeDictList;case h:return this.dictData.protectionDictList;case b:return this.dictData.placeDictList}},preCheck:function(){var t=[];for(var e in this.tabOptions){var a=this.tabOptions[e];for(var r in a.dicts){var i=a.dicts[r];if(!i.dictValue&&0!=i.dictValue)return i.dictValueBorder=!0,this.myUpdateForce(i),this.msgError("字典值不能为空"),!1;if(i.dictValueBorder&&(i.dictValueBorder=!1,this.myUpdateForce(i)),!i.displayName)return i.displayNameBorder=!0,this.myUpdateForce(i),this.msgError("字典名称不能为空"),!1;i.dictValueBorder&&(i.displayNameBorder=!1,this.myUpdateForce(i));var o={uid:i.uid,dictType:i.dictType,dictValue:i.dictValue,displayName:i.displayName,sort:i.sort,backgroundColor:this.formatHexToColor(this.formatRGBAToHex(i.backgroundColorHex)),textColor:this.formatHexToColor(this.formatRGBAToHex(i.textColorHex)),icon:i.icon};t.push(o)}}return t},handleSubmit:function(){var t=this,e=this.preCheck();e&&Object(o["b"])(e).then((function(e){200===e.code?t.msgSuccess("更新成功"):t.msgError("更新失败")}))},formatRGBAToHex:function(t){var e=t.replace(/rgba?\(/,"").replace(/\)/,"").replace(/[\s+]/g,"").split(","),a=255*parseFloat(e[3]||1),r=parseInt(e[0]||255),i=parseInt(e[1]||255),o=parseInt(e[2]||255);return"#"+("0"+a.toString(16)).slice(-2)+("0"+r.toString(16)).slice(-2)+("0"+i.toString(16)).slice(-2)+("0"+o.toString(16)).slice(-2)},formatHexToRgba:function(t){var e=t.split(""),a=parseInt(e.slice(1,3).toString().replace(",",""),16);a=isNaN(a)?255:a;var r=parseInt(e.slice(3,5).toString().replace(",",""),16);r=isNaN(r)?255:r;var i=parseInt(e.slice(5,7).toString().replace(",",""),16);i=isNaN(i)?255:i;var o=parseInt(e.slice(7,9).toString().replace(",",""),16);o=isNaN(o)?255:o;var c=parseFloat(parseFloat(a)/parseFloat(255)).toFixed(2);return"rgba("+r+","+i+","+o+","+c+")"},formatColorToHex:function(t){var e="#"+parseInt(t).toString(16);return e},formatHexToColor:function(t){var e=t.replace("#","");return e.length<=6&&(e="FF"+e),parseInt(e,16)},changeBackgroundColor:function(t){t.backgroundColor=this.formatHexToColor(this.formatRGBAToHex(t.backgroundColorHex)),this.myUpdateForce(t)},changeTextColor:function(t){t.textColor=this.formatHexToColor(this.formatRGBAToHex(t.textColorHex)),this.myUpdateForce(t)},myUpdateForce:function(t){var e=this.getDictsByKey(t.dictType),a=0;for(var r in e)if(e[r].dictValue===t.dictValue){a=r;break}switch(t.dictType){case c:this.$set(this.dictData.sexDictList,a,t);break;case s:this.$set(this.dictData.meteringDictList,a,t);break;case n:this.$set(this.dictData.nursingLevelDictList,a,t);break;case l:this.$set(this.dictData.criticalTypeDictList,a,t);break;case u:this.$set(this.dictData.dietDictList,a,t);break;case d:this.$set(this.dictData.allergyDictList,a,t);break;case p:this.$set(this.dictData.medicalInsuranceTypeDictList,a,t);break;case f:this.$set(this.dictData.bloodTypeDictList,a,t);break;case h:this.$set(this.dictData.protectionDictList,a,t);break;case b:this.$set(this.dictData.placeDictList,a,t);break}},moveList:function(t,e,a){var r=t[e],i=t[a];t.splice(a,1,r),t.splice(e,1,i);var o=r.sort;return r.sort=i.sort,i.sort=o,t},upData:function(t,e){t.length>1&&0!==e&&this.moveList(t,e,e-1)},downData:function(t,e){t.length>1&&e!==t.length-1&&this.moveList(t,e,e+1)},handleMoveUp:function(t){var e=this.getDictsByKey(t.dictType);if(e)for(var a in e)if(e[a].dictValue===t.dictValue){this.upData(e,parseInt(a));break}},handleMoveDown:function(t){var e=this.getDictsByKey(t.dictType);if(e)for(var a in e)if(e[a].dictValue===t.dictValue){this.downData(e,parseInt(a));break}},handleDelete:function(t){var e=this.getDictsByKey(t.dictType);if(e)for(var a in e)if(e[a].dictValue===t.dictValue){e.splice(a,1);break}},handleAdd:function(){var t=this.getDictsByKey(this.tabsActiveName),e={dictType:this.tabsActiveName,sort:t.length,backgroundColor:4294901760,backgroundColorHex:"#ffff0000",textColor:4294967295,textColorHex:"#ffffffff",displayNameBorder:!1,dictValueBorder:!1,icon:""};t.push(e)}}},y=m,g=(a("dfb6"),a("2877")),v=Object(g["a"])(y,r,i,!1,null,"afbb9d80",null);e["default"]=v.exports},d998:function(t,e,a){var r=a("342f");t.exports=/MSIE|Trident/.test(r)},dfb6:function(t,e,a){"use strict";a("0be5")}}]);