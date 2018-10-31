package me.hekr.sthome.DragFolderwidget;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.FloatMath;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.tools.NameSolve;

/**
 * 应用
 *
 */
public class ApplicationInfo extends ItemInfo {
    public static final String LOAD_ICON = "LOAD_ICON";
    public TranslateAnimation tAnim; //位移动画
    public ScaleAnimation sAnim; //缩放动画
    private int isAboveFolder = -1; // 1放大， 2缩小
    
    public ApplicationInfo() {
        type = TYPE_APP;
    }
    private final String TAG = "ApplicationInfo";



	/**
     * 绘图标和文字
     */
    public void drawBoundIcon(LayoutCalculator lc, ObjectPool op, Canvas canvas, int x, int y, Paint paint,
                              Paint iconPaint) {
        drawIcon(lc, op, canvas, x, y, iconPaint);
        drawTitle(lc, paint, canvas, x, y);
    }

    /**
     * 绘图标
     */
    public void drawIcon(LayoutCalculator lc, ObjectPool op, Canvas canvas, int x, int y, Paint iconPaint) {
        Bitmap icon = getIcon(lc);
        if (icon != null) {
            icon = op.stretch(icon);
            canvas.drawBitmap(icon, x, y, iconPaint);
        }
    }

    /**
     * 绘文件夹外框
     */
    public void drawFolderBound(LayoutCalculator lc, ObjectPool pp, Canvas canvas, int x, int y, Paint paint,
                                float scale) {
        if (isAboveFolder != -1) {
            Bitmap icon = pp.getFolderIcon();
            if (icon != null) {
                icon = pp.stretch(icon, scale);
                canvas.drawBitmap(icon, x, y, paint);
            }
        }
    }

    /**
     * 绘删除图标
     */
    public void drawBlackCircle(LayoutCalculator lc, ObjectPool pp, Canvas canvas, int x, int y) {
        Bitmap bitmap = pp.getBitmapBlackCircle();
        x = x - bitmap.getWidth() / 2;
        y = y - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, x, y, null);
    }

    /**
     * 绘文字
     */
    public void drawTitle(LayoutCalculator lc, Paint paint, Canvas canvas, int x, int y) {
        paint.setAlpha(255);
        int i = x + lc.getIconWidth() / 2;
        if (!titleMeasured)
            measureTitle(paint, lc);
        canvas.drawText(text.toString(), i, y + lc.textTop, paint);
    }

    /**
     * 格式化文字，超出部分替换...
     */
    public void measureTitle(Paint paint, LayoutCalculator lc) {



        if (TextUtils.isEmpty(text)) {
            if (NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(equipmentDesc))) {      //menci
                text = MyApplication.getAppResources().getString(R.string.door) + eqid;
            } else if (NameSolve.SOCKET.equals(NameSolve.getEqType(equipmentDesc))) {   //socket
                text = MyApplication.getAppResources().getString(R.string.socket) + eqid;
            } else if (NameSolve.PIR_CHECK.equals(NameSolve.getEqType(equipmentDesc))) {  //pir
                text = MyApplication.getAppResources().getString(R.string.pir) + eqid;
            } else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(equipmentDesc))) {  //sod
                text = MyApplication.getAppResources().getString(R.string.soskey) + eqid;
            } else if (NameSolve.SM_ALARM.equals(NameSolve.getEqType(equipmentDesc))) {  //sm
                text = MyApplication.getAppResources().getString(R.string.smalarm) + eqid;
            } else if (NameSolve.CO_ALARM.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.coalarm) + eqid;
            } else if (NameSolve.WT_ALARM.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.wt) + eqid;
            } else if (NameSolve.TH_CHECK.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.thcheck) + eqid;
            } else if (NameSolve.LAMP.equals(NameSolve.getEqType(equipmentDesc))) {   //socket
                text = MyApplication.getAppResources().getString(R.string.lamp) + eqid;
            } else if (NameSolve.GUARD.equals(NameSolve.getEqType(equipmentDesc))) {   //socket
                text = MyApplication.getAppResources().getString(R.string.guardor) + eqid;
            } else if (NameSolve.VALVE.equals(NameSolve.getEqType(equipmentDesc))) {   //socket
                text = MyApplication.getAppResources().getString(R.string.valve) + eqid;
            } else if (NameSolve.CURTAIN.equals(NameSolve.getEqType(equipmentDesc))) {   //socket
                text = MyApplication.getAppResources().getString(R.string.curtain) + eqid;
            } else if (NameSolve.BUTTON.equals(NameSolve.getEqType(equipmentDesc))) {   //socket
                text = MyApplication.getAppResources().getString(R.string.button) + eqid;
            } else if (NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(equipmentDesc))) {  //sm
                text = MyApplication.getAppResources().getString(R.string.cxsmalarm) + eqid;
            } else if (NameSolve.GAS_ALARM.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.gasalarm) + eqid;
            }else if (NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.thermalalarm) + eqid;
            }else if (NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.mode_button) + eqid;
            }else if (NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.mode_button) + eqid;
            }else if (NameSolve.LOCK.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.lock) + eqid;
            }else if (NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.two_channel_socket) + eqid;
            }else if (NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.temp_controler) + eqid;
            }else if (NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(equipmentDesc))) {  //end
                text = MyApplication.getAppResources().getString(R.string.dimming_module) + eqid;
            }

            int itemWidth = lc.getItemWidth();
            float length = paint.measureText(text, 0, text.length());
            float scale = 1;
            if (length > itemWidth)
                scale = length / text.length();
            for (int j = (text.length() - (int) FloatMath.ceil((length - itemWidth) / scale)); paint.measureText(text, 0,
                    text.length()) > itemWidth; --j) {
                text = (text.subSequence(0, j).toString() + "...");
            }
            titleMeasured = true;
        }
    }

    /**
     * 获取图片
     */
    public Bitmap getIcon(LayoutCalculator lc) {
    	
     	
        Bitmap bitmap = null;
        if (icon != null) {
            bitmap = icon;
            return bitmap;
        }
        Bitmap cache = null;
        if (iconRef != null && iconRef.get() != null) {
            cache = iconRef.get();
            icon = cache;
            bitmap = icon;
        }
        return bitmap;
    }

    /**
     * 绘图标内容
     */
    public void drawIconContent(LayoutCalculator lc, ObjectPool op, Canvas canvas, int x, int y, Paint iconPaint) {
        drawIcon(lc, op, canvas, x, y, iconPaint);
    }

    public int getIsAboveFolder() {
        return isAboveFolder;
    }

    public void setIsAboveFolder(int isAboveFolder) {
        this.isAboveFolder = isAboveFolder;
    }

}
