package com.hcs.calldemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.call.LinphoneService;
import com.hcs.android.call.api.FriendHelper;
import com.hcs.android.call.api.IFriendListener;
import com.hcs.android.call.api.IPresenceListener;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.calldemo.R;
import com.hcs.calldemo.entity.FriendBo;
import com.hcs.calldemo.factory.CommonViewModel;

import org.linphone.core.Friend;
import org.linphone.core.FriendList;

import java.util.ArrayList;
import java.util.List;


public class FriendViewModel extends BaseRefreshViewModel<FriendBo, CommonViewModel> implements IFriendListener, IPresenceListener {
    private RobustTimer mTimer;
    private boolean mWaiting = false;

    /**
     * 用于搜索的分组名称
     */
    private String findGroupName;
    /**
     * 用于改变的出席状态
     */
    private String presenceId;

    public FriendViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        mTimer = new RobustTimer(true);
        findGroupName = "";
        presenceId = "0";
    }

    private List<FriendBo> convertFriendList(FriendList friendList){
        List<FriendBo> tmpList = new ArrayList<>();
        if (friendList != null && friendList.getFriends() != null && friendList.getFriends().length > 0) {
            for (Friend friend : friendList.getFriends()) {
                FriendBo businessFriend = new FriendBo(friend);
                businessFriend.setGroupName(friendList.getDisplayName());
                tmpList.add(businessFriend);
            }
        }
        return tmpList;
    }

    @Override
    public void refreshData() {
        postShowNoDataViewEvent(true);
        if(!LinphoneService.isReady()){
            waitLinphoneReady();
            return;
        }
        postShowNoDataViewEvent(false);
        mList.clear();
        List<FriendBo> businessFriendList = new ArrayList<>();
        //创建一个空的好友用于添加
        FriendBo tmpFriend = new FriendBo();
        mList.add(tmpFriend);
        tmpFriend.setName(BaseApplication.getAppContext().getResources().getString(R.string.friend_add));
        if(StringUtil.isEmpty(findGroupName)) {
            FriendList[] friendLists = FriendHelper.getFriends();
            if (friendLists != null && friendLists.length > 0) {
                for (FriendList friendList : friendLists) {
                    businessFriendList.addAll(convertFriendList(friendList));
                }
            }
        }else{
            FriendList findFriendList = FriendHelper.getFriends(findGroupName);
            businessFriendList.addAll(convertFriendList(findFriendList));
        }
        mList.addAll(businessFriendList);
        postStopRefreshEvent();
    }

    @Override
    public void loadMore() {
        refreshData();
    }

    private void waitLinphoneReady(){
        if(mWaiting){
            return;
        }
        RobustTimerTask timerTask = new RobustTimerTask() {
            @Override
            public void run() {
                if(!LinphoneService.isReady()){
                    return;
                }
                mWaiting = false;
                mTimer.cancel();
                UIThreadUtil.runOnUiThread(()->{
                    refreshData();
                });
            }
        };
        mWaiting = true;
        mTimer.schedule(timerTask, 30, 30);
    }

    /**
     * 有新朋友加入
     * @param friend 新加入的好友
     */
    @Override
    public void onFriendAdded(Friend friend){
        FriendDetailViewModel friendDetailViewModel = new FriendDetailViewModel();
        friendDetailViewModel.loadFriend(friend);
        friendDetailViewModel.saveFriend();
        UIThreadUtil.runOnUiThread(()->{
            refreshData();
        });
    }

    /**
     * 有好友被删除
     * @param friend 被删除的好友
     */
    @Override
    public void onFriendRemoved(Friend friend){
        FriendDetailViewModel friendDetailViewModel = new FriendDetailViewModel();
        friendDetailViewModel.removeFriend();
        UIThreadUtil.runOnUiThread(()->{
            refreshData();
        });
    }

    public String getFindGroupName(){
        return findGroupName;
    }
    public void setFindGroupName(String findGroupName){
        this.findGroupName = findGroupName;
    }

    public String getPresenceId(){
        return presenceId;
    }
    public void setPresenceId(String presenceId){
        this.presenceId = presenceId;
    }


    /**
     * 好友状态改变
     * @param friend 好友
     * @param presence 出席状态
     */
    @Override
    public void onPresenceChanged(Friend friend,int presence){
        synchronized (mList){
            for(FriendBo friendBo : mList){
                if(friend.getRefKey().equals(friendBo.getRefId())){
                    friendBo.setPresence(String.valueOf(presence));
                    break;
                }
            }
        }
    }
}

