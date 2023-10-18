package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.Dict;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestUpdateDict;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.service.DictService;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 字典
 */
@CommandMapping
public class DictController {
    /**
     * 向分机请求更新字典
     */
    @CommandId("req-update-dict")
    public AjaxResult updateDict(String str){
        RequestDTO<RequestUpdateDict> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestUpdateDict.class});
        DictService.getInstance().updateDict(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 请求获取字典列表
     */
    @CommandId("req-list-dict")
    public AjaxResult getDistList(String str){
        return AjaxResult.success("",DictService.getInstance().getDictModelFromCache());
    }
}
