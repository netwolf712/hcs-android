package com.hcs.android.business.constant;

/**
 * 附件用途枚举
 */
public enum AttachmentUseEnum {
    /**
     * 广播
     */
    GroupMulticast(0),
    /**
     * 铃声
     */
    RING(1);

    /**
     * 枚举值
     */
    private final int value;

    AttachmentUseEnum(int value){
        this.value = value;
    }


    /**
     * 通过id查找枚举
     * @param id id
     * @return 枚举
     */
    public static AttachmentUseEnum findById(int id){
        for(AttachmentUseEnum filterEnum : AttachmentUseEnum.values()){
            if(id == filterEnum.getValue()){
                return filterEnum;
            }
        }
        return GroupMulticast;
    }

    public int getValue(){
        return value;
    }
}
