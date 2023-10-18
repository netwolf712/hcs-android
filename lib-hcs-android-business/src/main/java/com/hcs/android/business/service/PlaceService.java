package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.PlaceDao;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.util.PlaceUtil;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 位置服务
 */
public class PlaceService implements IDbService<Place>{
    private final PlaceDao mPlaceDao;
    private static PlaceService mInstance = null;
    public static PlaceService getInstance(){
        if(mInstance == null){
            synchronized (PlaceService.class){
                if(mInstance == null) {
                    mInstance = new PlaceService();
                }
            }
        }
        return mInstance;
    }

    public PlaceService(){
        mPlaceDao = DataBaseHelper.getInstance().placeDao();
    }

    public Place getPlace(String uid){
        if(uid == null){
            return null;
        }
        return mPlaceDao.findOne(uid);
    }

    public Place getPlace(String masterDeviceId,String placeType,Integer placeSn){
        if(StringUtil.isEmpty(masterDeviceId) || StringUtil.isEmpty(placeType) || placeSn == null){
            return null;
        }
        return mPlaceDao.findOne(PlaceUtil.genPlaceUid(masterDeviceId,placeType,placeSn));
    }

    public List<Place> getPlaceList(int offset,int limit){
        return mPlaceDao.getAll(limit,offset);
    }

    public List<Place> getPlaceList(String deviceId,int offset,int limit){
        return mPlaceDao.getAll(deviceId,limit,offset);
    }

    public List<Place> getPlaceList(String deviceId,int placeType,int offset,int limit){
        return mPlaceDao.getAll(deviceId,placeType,limit,offset);
    }

    public List<Place> getPlaceList(String masterDeviceId){
        return mPlaceDao.getAll(masterDeviceId);
    }

    public List<Place> getPlaceList(String masterDeviceId,int placeType){
        return mPlaceDao.getAll(masterDeviceId,placeType);
    }

    public List<Place> getPlaceListByParent(String masterDeviceId,String parentUid){
        return mPlaceDao.getAllByParent(masterDeviceId,parentUid);
    }

    public List<Place> getPlaceListByGroup(String masterDeviceId,String groupNo){
        return mPlaceDao.getAllByParent(masterDeviceId,groupNo);
    }

    public int getPlaceCount(String masterDeviceId){
        return mPlaceDao.countAll(masterDeviceId);
    }

    public int getPlaceCount(String masterDeviceId,int placeType){
        return mPlaceDao.countAll(masterDeviceId,placeType);
    }

    public int getPlaceCount(){
        return mPlaceDao.countAll();
    }

    /**
     * 更新或添加
     */
    public void updatePlace(@NonNull Place place){
        Place oldPlace = getPlace(PlaceUtil.genPlaceUid(place.getMasterDeviceId(), PlaceTypeEnum.findById(place.getPlaceType()).getName(),place.getPlaceSn()));
        place.setUpdateTime(System.currentTimeMillis());
        if(oldPlace != null){
            place.setUid(oldPlace.getUid());
            mPlaceDao.update(place);
        }else{
            mPlaceDao.insert(place);
        }
    }

    public void deletePlace(@NonNull Place place){
        mPlaceDao.delete(place);
    }

    public void deletePlaceByMasterDeviceId(String masterDeviceId){
        mPlaceDao.deleteByMasterDeviceId(masterDeviceId);
    }

    @Override
    public void deleteAll(){
        mPlaceDao.deleteAll();
    }

    @Override
    public void insertAll(List<Place> placeList){
        if(StringUtil.isEmpty(placeList)){
            return;
        }
        Place[] places = placeList.toArray(new Place[0]);
        mPlaceDao.insertAll(places);
    }
}
