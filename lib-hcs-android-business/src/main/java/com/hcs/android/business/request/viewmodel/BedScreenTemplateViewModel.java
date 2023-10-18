package com.hcs.android.business.request.viewmodel;

import android.content.Context;
import android.database.Observable;

import com.hcs.android.business.R;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.request.model.BedScreenTemplateRequestModel;

import java.util.ArrayList;
import java.util.List;

public class BedScreenTemplateViewModel {
    private Context mContext;
    /**
     * 床头屏模板数量上限
     */
    public final static int MAX_BED_SCREEN_TEMPLATE = 3;

    /**
     * 当前的配置id
     */
    private Integer mCurrentTemplateId;
    public Integer getCurrentTemplateId(){
        return mCurrentTemplateId;
    }
    public void setCurrentTemplateId(Integer currentTemplateId){
        mCurrentTemplateId = currentTemplateId;
    }

    /**
     * 配置模板列表
     */
    private final List<BedScreenTemplate> mTemplateList;
    private List<BedScreenTemplate> getTemplateList(){
        return mTemplateList;
    }

    private final BedScreenTemplateRequestModel mRequestModel;
    public BedScreenTemplateViewModel(Context context){
        mContext = context;
        mRequestModel = new BedScreenTemplateRequestModel(context);
        BedScreenTemplate bedScreenTemplate = mRequestModel.getCurrentTemplate();
        if(bedScreenTemplate == null){
            mCurrentTemplateId = Integer.valueOf(mContext.getString(R.string.default_current_template_id_device_type_bed_screen));
        }else{
            mCurrentTemplateId = Integer.valueOf(bedScreenTemplate.getTemplateId());
        }
        mTemplateList = new ArrayList<>();
        for(int i = 0; i < MAX_BED_SCREEN_TEMPLATE; i++){
            BedScreenTemplate tmp = mRequestModel.getTemplate(String.valueOf(i));
            if(tmp == null){
                tmp = new BedScreenTemplate();
                tmp.setTemplateId(String.valueOf(i));
            }
            mTemplateList.add(tmp);
        }
    }

    /**
     * 保存所有配置
     */
    public void saveTemplates(){
        //保存所有配置
        for(BedScreenTemplate bedScreenTemplate : mTemplateList){
            mRequestModel.saveTemplate(bedScreenTemplate);
        }
        //设置当前配置，并发送给所有分机
        mRequestModel.setCurrentTemplate(String.valueOf(mCurrentTemplateId));
    }
}
