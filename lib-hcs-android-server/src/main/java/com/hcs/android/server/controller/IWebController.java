package com.hcs.android.server.controller;

import com.hcs.android.server.web.AjaxResult;

/**
 * web请求接口，方便请求扩展
 */
public interface IWebController {
    /**
     * 处理web命令并返回结果
     * @param serialData 序列化的字符串数据
     * @return 处理结果
     */
    AjaxResult dispatchWebController(String serialData);
}
