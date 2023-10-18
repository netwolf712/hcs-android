package com.hcs.android.ui.player;

/**
 * ijkplayer的属性配置单元
 */
public class IjkOptionItem {
    public IjkOptionItem(){

    }
    public IjkOptionItem(Integer category, String name, String value, Class<?> valueType){
        this.category = category;
        this.name = name;
        this.value = value;
        this.valueType = valueType;
    }
    /**
     * 属于哪个大类的配置
     */
    private Integer category;

    /**
     * 配置名称
     */
    private String name;

    /**
     * 配置值
     */
    private String value;

    /**
     * 配置类型
     */
    private Class<?> valueType;

    public Integer getCategory(){
        return category;
    }
    public void setCategory(Integer category){
        this.category = category;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }

    public Class<?> getValueType(){
        return valueType;
    }
    public void setValueType(Class<?> valueType){
        this.valueType = valueType;
    }


    /**
     * 作为long类型输出
     * @return
     */
    public Long getValueAsLong(){
        return Long.parseLong(value);
    }
}
