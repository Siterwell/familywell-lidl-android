package me.hekr.sthome.DragFolderwidget;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;

import me.hekr.sthome.model.modelbean.EquipmentBean;

public class ItemInfo extends EquipmentBean {
    public static final int TYPE_APP = 1;
    public static final int TYPE_FOLDER = 2;
    protected Bitmap icon;
    protected String text;
    protected int order;
    protected int type;
    protected SoftReference<Bitmap> iconRef;
    public Bitmap notification;
    public Bitmap buffer;
    private boolean jiggle = false;
    protected boolean titleMeasured = false;

    public void clearIcon() {
        this.icon = null;
        this.iconRef = null;
    }

    public void destroy() {
        this.icon = null;
        this.notification = null;
        this.buffer = null;
    }


    public void setIcon(Bitmap icon) {
        this.icon = icon;
        iconRef = new SoftReference<Bitmap>(icon);
    }

    public Bitmap getIcon() {
        return iconRef.get();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getText() {
        return text ;
    }

    public void setTitle(String title) {
        this.text = title;
        titleMeasured = false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void jiggle() {
        setJiggle(true);
    }

    public void unJiggle() {
        setJiggle(false);
    }

    public boolean isJiggle() {
        return jiggle;
    }

    public void setJiggle(boolean jiggle) {
        this.jiggle = jiggle;
    }
}
