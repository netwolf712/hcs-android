package com.hcs.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.R;

import java.util.List;

/**
 * @author liujianyu
 * IP地址组件
 */
public class IpEditText extends LinearLayout {

    /**
     * IP地址框前面文字
     */
    private TextView mDescriptionText;

    /**
     * IP地址第一个文本编辑框
     */
    private EditText mFirstIPEdit;

    /**
     * IP地址第二个文本编辑框
     */
    private EditText mSecondIPEdit;

    /**
     * IP地址第三个文本编辑框
     */
    private EditText mThirdIPEdit;

    /**
     * IP地址第四个文本编辑框
     */
    private EditText mFourthIPEdit;

    /**
     * 第一个点
     */
    private TextView mFirstPoint;

    /**
     * 第二个点
     */
    private TextView mSecondPoint;

    /**
     * 第三个点
     */
    private TextView mThirdPoint;

    private String mFirstIP;
    private String mSecondIP;
    private String mThirdIP;
    private String mFourthIP;

    private String textAddress;

    public String getTextAddress() {
        //return textAddress;
        return getIpText();
    }

    public void setTextAddress(String textAddress) {
        this.textAddress = textAddress;
        if(!StringUtil.isEmpty(textAddress)){
            convertTextAddress(textAddress);
        }else{
            mFirstIPEdit.setText("");
            mSecondIPEdit.setText("");
            mThirdIPEdit.setText("");
            mFourthIPEdit.setText("");
        }
    }

    public IpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.ip_edit_text, this);

        mDescriptionText = (TextView) view.findViewById(R.id.des_text);
        mFirstIPEdit = (EditText) view.findViewById(R.id.firstIPField);
        mSecondIPEdit = (EditText) view.findViewById(R.id.secondIPField);
        mThirdIPEdit = (EditText) view.findViewById(R.id.thirdIPField);
        mFourthIPEdit = (EditText) view.findViewById(R.id.fourthIPField);


        mFirstPoint = (TextView) view.findViewById(R.id.first_point);
        mSecondPoint = (TextView) view.findViewById(R.id.second_point);
        mThirdPoint = (TextView) view.findViewById(R.id.third_point);

        //属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IpEditText);
        //IP每一个文本编辑输入框中字体大小
        int textSize = array.getDimensionPixelSize(R.styleable.IpEditText_android_textSize, 15);
        //IP每一个文本编辑输入框背景
        int background = array.getResourceId(R.styleable.IpEditText_android_editTextBackground, R.drawable.edit_border);
        int textColor = array.getColor(R.styleable.IpEditText_android_textColor, Color.WHITE);
        int descriptionTextColor = array.getColor(R.styleable.IpEditText_descriptionTextColor, Color.WHITE);
        String text = (String) array.getText(R.styleable.IpEditText_descriptionText);
        mDescriptionText.setText(text);
        mDescriptionText.setTextColor(descriptionTextColor);


        mFirstIPEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mSecondIPEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mThirdIPEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mFourthIPEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        mFirstIPEdit.setBackgroundResource(background);
        mSecondIPEdit.setBackgroundResource(background);
        mThirdIPEdit.setBackgroundResource(background);
        mFourthIPEdit.setBackgroundResource(background);

        mFirstIPEdit.setTextColor(textColor);
        mSecondIPEdit.setTextColor(textColor);
        mThirdIPEdit.setTextColor(textColor);
        mFourthIPEdit.setTextColor(textColor);

        mFirstPoint.setTextColor(textColor);
        mSecondPoint.setTextColor(textColor);
        mThirdPoint.setTextColor(textColor);


        textAddress = (String) array.getText(R.styleable.IpEditText_textAddress);
        if(!StringUtil.isEmpty(textAddress)){
            convertTextAddress(textAddress);
        }
        setIPEditTextListener(context);
    }

    /**
     * 将字符串形式的IP地址转换为分节形式
     */
    private void convertTextAddress(String textAddress){
        List<Object> objList = StringUtil.CutStringWithChar(textAddress,'.');
        if(!StringUtil.isEmpty(objList) && objList.size() == 4){
            mFirstIPEdit.setText((String)objList.get(0));
            mSecondIPEdit.setText((String)objList.get(1));
            mThirdIPEdit.setText((String)objList.get(2));
            mFourthIPEdit.setText((String)objList.get(3));
        }
    }
    /**
     * 监听IP地址文本框
     *
     * @param context
     */
    public void setIPEditTextListener(final Context context) {
        //设置第一个字段的事件监听
        mFirstIPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s && s.length() > 0) {
                    boolean contains = s.toString().trim().contains(".");
                    if (s.length() > 2 || contains) {
                        if (contains) {
                            mFirstIP = s.toString().trim().substring(0, s.length() - 1);
                        } else {
                            mFirstIP = s.toString().trim();
                        }
                        if (Integer.parseInt(mFirstIP) > 255) {
                            Toast.makeText(context, "IP大小在0-255之间",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        mSecondIPEdit.setFocusable(true);
                        mSecondIPEdit.requestFocus();
                    } else {
                        mFirstIP = s.toString().trim();
                    }
                } else {
                    mFirstIP = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mFirstIPEdit.removeTextChangedListener(this);
                mFirstIPEdit.setText(mFirstIP);
                mFirstIPEdit.setSelection(mFirstIP.length());
                mFirstIPEdit.addTextChangedListener(this);
            }
        });
        //设置第二个IP字段的事件监听
        mSecondIPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s && s.length() > 0) {
                    boolean contains = s.toString().trim().contains(".");
                    if (s.length() > 2 || contains) {
                        if (contains) {
                            mSecondIP = s.toString().trim().substring(0, s.length() - 1);
                        } else {
                            mSecondIP = s.toString().trim();
                        }
                        if (Integer.parseInt(mSecondIP) > 255) {
                            Toast.makeText(context, "IP大小在0-255之间",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        mThirdIPEdit.setFocusable(true);
                        mThirdIPEdit.requestFocus();
                    } else {
                        mSecondIP = s.toString().trim();
                    }
                } else {
                    mSecondIP = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSecondIPEdit.removeTextChangedListener(this);
                mSecondIPEdit.setText(mSecondIP);
                mSecondIPEdit.setSelection(mSecondIP.length());
                mSecondIPEdit.addTextChangedListener(this);
            }
        });
        //设置第三个IP字段的事件监听
        mThirdIPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s && s.length() > 0) {
                    boolean contains = s.toString().trim().contains(".");
                    if (s.length() > 2 || contains) {
                        if (contains) {
                            mThirdIP = s.toString().trim().substring(0, s.length() - 1);
                        } else {
                            mThirdIP = s.toString().trim();
                        }
                        if (Integer.parseInt(mThirdIP) > 255) {
                            Toast.makeText(context, "IP大小在0-255之间",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        mFourthIPEdit.setFocusable(true);
                        mFourthIPEdit.requestFocus();
                    } else {
                        mThirdIP = s.toString().trim();
                    }
                } else {
                    mThirdIP = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mThirdIPEdit.removeTextChangedListener(this);
                mThirdIPEdit.setText(mThirdIP);
                mThirdIPEdit.setSelection(mThirdIP.length());
                mThirdIPEdit.addTextChangedListener(this);
            }
        });
        //设置第四个IP字段的事件监听
        mFourthIPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s && s.length() > 0) {
                    mFourthIP = s.toString().trim();
                    if (Integer.parseInt(mFourthIP) > 255) {
                        Toast.makeText(context, "请输入合法的ip地址", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

//    @BindingAdapter("mDescriptionText")
//    public static void setText(IpEditText ipEditText, String value) {
//        if (null != value) {
//            ipEditText.mDescriptionText.setText(value);
//        }
//    }


    /**
     * 获取IP地址
     * @return
     */
    public String getIpText() {
        if(StringUtil.isNotBlank(mFirstIP) && StringUtil.isNotBlank(mSecondIP) && StringUtil.isNotBlank(mThirdIP) && StringUtil.isNotBlank(mFourthIP)){
            StringBuilder sb = new StringBuilder(mFirstIP);
            sb.append(":")
                    .append(mSecondIP)
                    .append(":")
                    .append(mThirdIP)
                    .append(":")
                    .append(mFourthIP);
            return sb.toString();
        }
        return null;
    }


    private void EnableDisableEditText(boolean isEnabled, EditText editText,int color) {
        editText.setTextColor(color);//设置只读时的文字颜色
        editText.setFocusable(isEnabled); //无焦点
        editText.setFocusableInTouchMode(isEnabled) ;//触摸时也得不到焦点
        editText.setClickable(isEnabled);
        editText.setLongClickable(isEnabled);
        editText.setCursorVisible(isEnabled) ;//设置输入框中的光标不可见
    }
    /**
     * 设置编辑框为只读的
     */
    public void setEditTextReadOnly(IpEditText ipEditText,int color){
        EnableDisableEditText(false,ipEditText.mFirstIPEdit,color);
        EnableDisableEditText(false,ipEditText.mSecondIPEdit,color);
        EnableDisableEditText(false,ipEditText.mThirdIPEdit,color);
        EnableDisableEditText(false,ipEditText.mFourthIPEdit,color);
    }

    /**
     * 设置编辑框为正常编辑框
     */
    public void setEditTextNormal(IpEditText ipEditText,int color){
        EnableDisableEditText(true,ipEditText.mFirstIPEdit,color);
        EnableDisableEditText(true,ipEditText.mSecondIPEdit,color);
        EnableDisableEditText(true,ipEditText.mThirdIPEdit,color);
        EnableDisableEditText(true,ipEditText.mFourthIPEdit,color);
    }
}


