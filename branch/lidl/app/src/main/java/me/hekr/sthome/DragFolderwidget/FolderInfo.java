package me.hekr.sthome.DragFolderwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.FloatMath;

import java.util.List;
import java.util.Vector;

/**
 * 文件夹
 *
 */
public class FolderInfo extends ApplicationInfo {
    private Vector<ApplicationInfo> apps = new Vector<ApplicationInfo>();//包含的应用
    private FolderView folderView;//对应的文件夹view
    private String textSub;       //缩略名称

    public FolderInfo() {
        type = TYPE_FOLDER;
    }

    @Override
    public void drawIcon(LayoutCalculator lc, ObjectPool op, Canvas canvas, int x, int y, Paint iconPaint) {
        drawFolderIcon(op, canvas, x, y, iconPaint);
        drawIconContent(lc, op, canvas, x, y, iconPaint);
    }
    
    @Override
    public void drawBlackCircle(LayoutCalculator lc, ObjectPool op, Canvas canvas, int x, int y) {
        //文件夹不绘制删除按钮
    }
    /**
     * 绘制文件夹外框图标
     */
    public void drawFolderIcon(ObjectPool op, Canvas canvas, int x, int y, Paint iconPaint) {
        Bitmap bitmap = op.getFolderIcon();
        canvas.drawBitmap(bitmap, x, y, iconPaint);
    }
    /**
     * 绘制文件夹内容图标
     */
    @Override
    public void drawIconContent(LayoutCalculator lc, ObjectPool op, Canvas canvas, int x, int y, Paint iconPaint) {
        float margin = lc.dpToPixel(5);//外间距
        float folderIconSize = lc.folderIconSize;//图标大小
        float folderGap = lc.folderGap;//图标内间距
        int rows = 3;
        int width = (int) (3 * folderIconSize + 2 * folderGap + 2 * margin);
        int height = (int) (folderIconSize * rows + folderGap * (rows - 1) + 2 * margin);
        clearIcon();
        Bitmap icon = BitmapManager.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        setIcon(icon);
        Canvas content = new Canvas(icon);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
//        //绘制前9个图标的小图标
//        for (int i = 0; i < apps.size() && i < 9; i++) {
//            int row = i / 3;
//            int column = i % 3;
//            float x1 = margin + column * (folderIconSize + folderGap);
//            float y1 = margin + row * (folderIconSize + folderGap);
//            ApplicationInfo info = apps.get(i);
//            if (info != null) {
//                Bitmap bitmap = info.getIcon(lc);
//                if (bitmap != null) {
//                    content.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(x1, y1,
//                            x1 + folderIconSize, y1 + folderIconSize), paint);
//                }
//            }
//        }
        super.drawIcon(lc, op, canvas, x, y, iconPaint);
    }
    /**
     * 返回应用
     * @return
     */
    public List<ApplicationInfo> getIcons() {
        return apps;
    }
    /**
     * 设置应用
     * @param icons
     */
    public void setIcons(List<ApplicationInfo> icons) {
        apps.clear();
        apps.addAll(icons);
    }


    /**
     * 返回文件夹视图
     * @param context
     * @return
     */
    public FolderView getFolderView(Context context) {
        if (folderView == null) {
            folderView = new FolderView(context);
            folderView.setInfo(this);
        }
        return folderView;
    }
    /**
     * 添加应用
     * @param app
     */
    public void addIcon(ApplicationInfo app) {
        apps.add(app);
    }


    /**
     * 绘文字
     */
    @Override
    public void drawTitle(LayoutCalculator lc, Paint paint, Canvas canvas, int x, int y) {
        paint.setAlpha(255);
        int i = x + lc.getIconWidth() / 2;
        if (!titleMeasured)
            measureTitle(paint, lc);
        canvas.drawText(textSub.toString(), i, y + lc.textTop, paint);
    }

    /**
     * 格式化文字，超出部分替换...
     */
    @Override
    public void measureTitle(Paint paint, LayoutCalculator lc) {
        this.textSub = text;
        int itemWidth = lc.getItemWidth();
        float length = paint.measureText(textSub, 0, textSub.length());
        float scale = 1;
        if (length > itemWidth)
            scale = length / textSub.length();
        for (int j = (textSub.length() - (int) Math.ceil((length - itemWidth) / scale)); paint.measureText(textSub, 0,
                textSub.length()) > itemWidth; --j) {
            textSub = (textSub.subSequence(0, j).toString() + "...");
        }
        titleMeasured = true;
    }
}
