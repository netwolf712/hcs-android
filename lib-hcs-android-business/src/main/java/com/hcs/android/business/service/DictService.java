package com.hcs.android.business.service;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.UpdateTypeEnum;
import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.DictDao;
import com.hcs.android.business.entity.Dict;
import com.hcs.android.business.entity.DictModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestUpdateDict;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.UpdateDataHelper;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.CastUtil;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.StringUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 字典服务
 */
public class DictService implements IDbService<Dict>{
    /**
     * 默认的字典文件路径
     */
    private final static String DEFAULT_DICT_FILE_NAME = "default_dict.json";
    private DictModel mDictModel;
    private final Context mContext;
    private final DictDao mDictDao;
    private final UpdateDataHelper<Dict> mDictUpdateHelper;
    /**
     * 最近一次更新的时间
     */
    private long mLastUpdateTime = 0L;
    public DictService(){
        mContext = BusinessApplication.getAppContext();
        mDictDao = DataBaseHelper.getInstance().dictDao();
        mDictUpdateHelper = new UpdateDataHelper<>(this);
        autoLoadDefault();
    }

    @SuppressLint("StaticFieldLeak")
    private static DictService mInstance = null;
    public static DictService getInstance(){
        if(mInstance == null){
            synchronized (DictService.class){
                if(mInstance == null) {
                    mInstance = new DictService();
                }
            }
        }
        return mInstance;
    }

    private void updateDictDao(List<Dict> dictList){
        if(StringUtil.isEmpty(dictList)){
            return;
        }
        Dict[] dicts = dictList.toArray(new Dict[0]);
        mDictDao.insertAll(dicts);
    }

    /**
     * 将默认的字典配置更新到数据库中
     */
    public void loadDefault(){
        String filePath = mContext.getFilesDir().getAbsolutePath() + "/" + DEFAULT_DICT_FILE_NAME;
        try {
            FileUtil.copyIfNotExist(BusinessApplication.getAppContext(), R.raw.default_dict, filePath);
            byte[] orgData = FileUtil.getFileByte(filePath);
            String str = new String(orgData, StandardCharsets.UTF_8);
            RequestDTO<List<Dict>> respDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,List.class,Dict.class});
            List<Dict> dictList = respDTO.getData();
            deleteAll();
            updateDictDao(dictList);
            mLastUpdateTime = System.currentTimeMillis();
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.DICT_UPDATE_TIME,mLastUpdateTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 检查数据库是否有默认的字典，
     * 若没有则将默认配置更新进去
     */
    private void autoLoadDefault(){
        if(mDictDao.countAll() == 0){
            loadDefault();
        }
    }

    public Dict getDict(String type,int dictValue){
        return mDictDao.findOne(type,dictValue);
    }

    public List<Dict> getDictList(){
        return mDictDao.getAll();
    }

    public List<Dict> getDictList(String type){
        return mDictDao.getDictList(type);
    }

    public void deleteDict(String type,int dictValue){
        Dict dict = mDictDao.findOne(type,dictValue);
        if(dict != null){
            mDictDao.delete(dict);
        }
    }

    public void deleteDict(String type){
        mDictDao.deleteByType(type);
    }

    public void updateDict(Dict dict){
        Dict oldDict = mDictDao.findOne(dict.getDictType(), dict.getDictValue());
        if(oldDict != null){
            dict.setUid(oldDict.getUid());
            mDictDao.update(dict);
        }else{
            mDictDao.insert(dict);
        }
    }

    /**
     * 查询符合条件的字典集合
     * @param type 字典类型
     * @param values 字典值组成的字符串，每个值间用英文,隔开
     * @return 字典集合
     */
    public List<Dict> getDictList(String type,String values){
        Integer[] valueArray = StringUtil.ConvertStringToIds(values);
        if(valueArray != null && valueArray.length > 0){
            return mDictDao.getDictList(type,valueArray);
        }
        return null;
    }

    /**
     * 将字典列表里的值转换成字符串，每个值间用英文,隔开
     * @param dictList 字典列表
     * @return 字符串，每个值间用英文,隔开
     */
    @NonNull
    public static String convertValueListToString(List<Dict> dictList){
        if(StringUtil.isEmpty(dictList)){
            return "";
        }
        StringBuilder str = new StringBuilder();
        for(Dict dict : dictList){
            if(str.length() > 0){
                str.append(",");
            }
            str.append(dict.getDictValue());
        }
        return str.toString();
    }

    public void updateDict(RequestDTO<RequestUpdateDict> requestDTO){
        try{
            if(requestDTO.getData() != null) {
                RequestUpdateDict requestUpdateDict = CastUtil.cast(requestDTO.getData());
                ResponseList<Dict> dictList = requestUpdateDict.getResponseList();
                if (dictList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
                    if(mDictUpdateHelper.addData(dictList)){
                        loadDictModelToCache();
                    }
                } else {
                    if (!StringUtil.isEmpty(dictList.getList())) {
                        for (Dict dict : dictList.getList()) {
                            if (dictList.getUpdateType() == UpdateTypeEnum.UPDATE_NORMAL.getValue()) {
                                updateDict(dict);
                            } else {
                                mDictDao.delete(dict);
                            }
                        }
                        loadDictModelToCache();
                    }
                }
                if(requestUpdateDict.getLastUpdateTime() == null) {
                    mLastUpdateTime = System.currentTimeMillis();
                }else{
                    mLastUpdateTime = requestUpdateDict.getLastUpdateTime();
                }
                SettingsHelper.getInstance(mContext).putData(PreferenceConstant.DICT_UPDATE_TIME,mLastUpdateTime);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取所有类型的字典列表
     */
    public DictModel getAll(){
        DictModel dictModel = new DictModel();
        dictModel.setSexDictList(getDictList(DictModel.DICT_TYPE_SEX));
        dictModel.setMeteringDictList(getDictList(DictModel.DICT_TYPE_METERING));
        dictModel.setNursingLevelDictList(getDictList(DictModel.DICT_TYPE_NURSING_LEVEL));
        dictModel.setCriticalTypeDictList(getDictList(DictModel.DICT_TYPE_CRITICAL_TYPE));
        dictModel.setDietDictList(getDictList(DictModel.DICT_TYPE_DIET));
        dictModel.setAllergyDictList(getDictList(DictModel.DICT_TYPE_ALLERGY));
        dictModel.setMedicalInsuranceTypeDictList(getDictList(DictModel.DICT_TYPE_MEDICAL_INSURANCE_TYPE));
        dictModel.setProtectionDictList(getDictList(DictModel.DICT_TYPE_PROTECTION));
        dictModel.setBloodTypeDictList(getDictList(DictModel.DICT_TYPE_BLOOD_TYPE));
        dictModel.setPlaceDictList(getDictList(DictModel.DICT_TYPE_SLAVE_PLACE));
        return dictModel;
    }

    public void loadDictModelToCache(){
        mDictModel = DictService.getInstance().getAll();
    }

    public final DictModel getDictModelFromCache(){
        return mDictModel;
    }

    @Override
    public void deleteAll(){
        mDictDao.deleteAll();
    }

    @Override
    public void insertAll(List<Dict> dictList){
        if(StringUtil.isEmpty(dictList)){
            return;
        }
        Dict[] dicts = dictList.toArray(new Dict[0]);
        mDictDao.insertAll(dicts);
    }
}
