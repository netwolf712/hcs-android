package com.hcs.android.maintain.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.maintain.FileManager;
import com.hcs.android.maintain.TestManager;
import com.hcs.android.maintain.entity.RequestDTO;
import com.hcs.android.maintain.entity.RequestFileList;
import com.hcs.android.maintain.entity.RequestTest;
import com.hcs.android.server.entity.RequestFile;
import com.hcs.android.server.web.AjaxResult;

/**
 * 数据测试
 */
@CommandMapping
public class TestController {
    /**
     * 请求测试
     */
    @CommandId("maintain-req-test")
    public AjaxResult handleTest(String str){
        RequestDTO<RequestTest> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestTest.class});
        TestManager.getInstance().handleTest(requestDTO.getData());
        return AjaxResult.success("");
    }

    /**
     * 获取测试状态
     */
    @CommandId("maintain-req-test-status")
    public AjaxResult getTestStatus(String str){
        return AjaxResult.success("", TestManager.getInstance().getTestStatus());
    }
}
