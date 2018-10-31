package me.hekr.sthome.model.modeladapter;

import java.util.ArrayList;

import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by xjj on 2016/12/27.
 * 添加设备动作详细页面滚轮控件适配器
 */
public class OptionAdapter extends WheelView.WheelArrayAdapter<String> {

    public OptionAdapter(ArrayList<String> items, int length) {
        super(items, length);
    }

    @Override
    public String getItem(int index) {
        String item = super.getItem(index);
        // FIXME: 2015/10/23 如果名称较长，整个文字排版上就容易有问题，故截取显条目字数
            item = cutString(item, 10, "...");
        return item;
    }

    private String cutString(String text, int length, String endWith) {
        //GBK每个汉字长2，UTF-8每个汉字长度为3
        final String CHINESE_ENCODE = "UTF-8";
        final int CHINESE_LENGTH = 3;
        int textLength = text.length();
        int byteLength = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textLength && byteLength < length * CHINESE_LENGTH; i++) {
            String str_i = text.substring(i, i + 1);
            if (str_i.getBytes().length == 1) {
                byteLength++;//英文
            } else {
                byteLength += CHINESE_LENGTH;//中文
            }
            sb.append(str_i);
        }
        try {
            if (byteLength < text.getBytes(CHINESE_ENCODE).length) {
                sb.append(endWith);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
