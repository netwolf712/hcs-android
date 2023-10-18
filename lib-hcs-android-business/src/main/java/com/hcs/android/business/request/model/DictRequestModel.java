package com.hcs.android.business.request.model;

import com.hcs.android.business.entity.DictModel;
import com.hcs.android.business.service.DictService;

/**
 * 字典数据请求接口
 */
public class DictRequestModel {
    private DictModel mDictModel;
    public final DictModel getDictModel(){
        return mDictModel;
    }

    public DictRequestModel(){
        mDictModel = DictService.getInstance().getDictModelFromCache();
    }
}
