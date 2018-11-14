package me.hekr.sthome.DragFolderwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.PackDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;

public class MyContentView extends View implements IPageView {
    private static final String TAG = "MyContentView";
    private boolean jiggle = false;//是否为编辑模式
    private boolean messed = false;
    private int selected = -1;
    private Point selectedLocation;
    private LayoutCalculator lc;
    private ObjectPool pp;
    private int columns;
    private int rows;
    private int viewHeight;
    private List<ApplicationInfo> icons = new ArrayList<ApplicationInfo>();
    private boolean isAboveFolder = false;
    private int aboveIndex = -1;
    public int drawback = -1;
    private static int FRENSH_NUM = 5;
    private PackDAO PDO;
    private EquipDAO EDO;
	
	public MyContentView(Context context) {
		super(context);
        PDO = new PackDAO(context);
        EDO = new EquipDAO(context);
		// TODO Auto-generated constructor stub
	}

	public MyContentView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
        PDO = new PackDAO(context);
        EDO = new EquipDAO(context);
		// TODO Auto-generated constructor stub
	}

	public MyContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
        PDO = new PackDAO(context);
        EDO = new EquipDAO(context);
		// TODO Auto-generated constructor stub
	}
	
    public void init(LayoutCalculator lc, ObjectPool pp) {
        this.lc = lc;
        this.pp = pp;
    }

	@Override
	public Point getIconLocation(int index) {
		// TODO Auto-generated method stub
        int i = index / columns;
        int j = index % columns;
        Point point = new Point();
        int k = getVerticalGap();
        point.x = (lc.getMarginLeft() + lc.gapH + j * (lc.getIconWidth() + lc.gapH));
        point.y = (k + lc.getMarginTop() + i * (k + lc.getItemHeight()));
        return point;
	}

    public int getSelectedIndex() {
        return selected;
    }

    public void select(int index) {
        selected = index;
        Point point = getIconLocation(index);
        invalidate(lc.toItemRect(point.x, point.y));
        this.selectedLocation = point;
    }

    public void hitTest3(int x, int y, HitTestResult3 hitTest3) {
        hitTest3.index = -1;
        hitTest3.buttonRemove = false;
        //        Log.d(TAG, "X = " + x + "," + "Y = " + y);
        int i = lc.dpToPixel(12);
        int j = lc.dpToPixel(26) + i * 2;
        int k = lc.dpToPixel(26) + i * 2;
        int gapV = getVerticalGap();
        int top = gapV + lc.marginTop;
        int y1 = top;
        int left = lc.marginLeft + lc.gapH;
        //        Log.d(TAG, "gapV =" + gapV + ", top =" + top + ", y1 =" + y1 + ", left =" + left);
        Bitmap remove = pp.getBitmapBlackCircle();
        int rWidth = remove.getWidth();
        int rHeight = remove.getHeight();
        for (int row = 0; row < rows; row++) {
            boolean inItemY = false;
            boolean inRemoveY = false;
            //            Log.d(TAG, "row = " + row + "     y1 = " + y1 + "," + "y2 = " + (y1 + k));
            //            Log.d(TAG,
            //                    "row = " + row + " remove    y1 = " + (y1 - lc.dpToPixel(10)) + "," + "y2 = "
            //                            + (y1 - lc.dpToPixel(10) + rHeight));
            if ((y >= y1) && (y < y1 + lc.iconHeight)) {
                inItemY = true;
            }
            if (y >= (y1 - lc.dpToPixel(10)) && y <= (y1 - lc.dpToPixel(10) + rHeight)) {
                inRemoveY = true;
            }
            //            Log.d(TAG, "inRemoveY " + inRemoveY + "inItemY " + inItemY);
            if (!inItemY && !inRemoveY) {
                y1 += top + lc.itemHeight;
                continue;
            }
            for (int column = 0; column < columns; column++) {
                int index = column + row * columns;
                int x1 = left;
                //                Log.d(TAG, "column = " + column + "    x1 = " + x1 + "," + "x2 = " + (x1 + j));
                if (inItemY && (x >= x1) && (x < x1 + lc.iconWidth)) {
                    hitTest3.index = index;
                    hitTest3.buttonRemove = false;
                }
                if (inRemoveY
                        && (x >= x1 + lc.iconWidth - lc.dpToPixel(10) && x < x1 + lc.iconWidth - lc.dpToPixel(10)
                                + rWidth)) {
                    hitTest3.index = index;
                    hitTest3.buttonRemove = true;
                }
                if (hitTest3.index != -1) {
                    return;
                }
                left += lc.iconWidth + lc.gapH;
            }
            y1 += top + lc.itemHeight;
        }
        hitTest3.index = -1;
    }

    public int hitTest2(int x, int y, HitTestResult3 hitTest2, boolean isFolder) {
        int left = lc.iconMarginLeft;
        int right = (lc.iconWidth + lc.gapH) * columns - lc.iconMarginLeft;
        int top = getVerticalGap() + lc.marginTop;
        int y1 = top;
        //        Log.d(TAG, "x = " + x + ",y = " + y);
        if (x <= left) {
            return -1;
        }
        if (x >= right) {
            return 1;
        }
        int oldIndex = -1;
        if (icons.size() == 0) {
            hitTest2.index = 0;
            hitTest2.inIcon = false;
            return 0;
        }
        for (int i = 0; i < icons.size(); i++) {
            if (icons.get(i) == null) {
                oldIndex = i;
                break;
            }
        }
        if (oldIndex == -1) {
            oldIndex = icons.size();
        }
        int itemX1 = lc.gapH / 2;
        int count = icons.size();
        if (icons.get(count - 1) == null) {
            count--;
        }
        int currentRow = count / columns + 1;
        if (y > currentRow * (top + lc.itemHeight)) {
          //如果y超过最后一行的y坐标，则放在最后一个。
            if (oldIndex < count) {
                hitTest2.index = count - 1;
            } else {
                hitTest2.index = count;
            }
            return 0;
        }
        for (int i = 0; i <rows; i++) {
            //            Log.d(TAG, "y1 = " + y1 + "y2 = " + (y1 + lc.itemHeight));
            //            Log.d(TAG, "`````` y1 = " + y1 + ",y2 = " + (y1 + lc.itemHeight));
            if ((y >= y1) && (y < y1 + lc.itemHeight)) {

            } else {
                y1 += top + lc.itemHeight;
                continue;
            }
            for (int j = 0; j < columns; j++) {
                int inType = -1;
                //                Log.d(TAG, "1111111111 x1 = " + left + ",x2 = " + (itemX1));
                //                Log.d(TAG, "2222222222 x1 = " + (itemX1) + ",x2 = " + (lc.iconWidth + itemX1));
                //                Log.d(TAG, "3333333333 x1 = " + (lc.iconWidth + itemX1) + ",x2 = "
                //                        + (lc.iconWidth + itemX1 - lc.gapH / 2));
                if (x > left && x <= itemX1) {
                    //item左半边
                    inType = 0;
                } else if (x > itemX1 && x < lc.iconWidth + itemX1) {
                    //item里面
                    inType = 2;
                } else if (x > lc.iconWidth + itemX1 && x < (lc.iconWidth + itemX1 + lc.gapH / 2)) {
                    //item右半边
                    inType = 1;
                }
                int position = j + i * columns;
                if (position >= count) {
                  //如果y超过最后一行的y坐标，则放在最后一个。
                    if (oldIndex < count) {
                        hitTest2.index = count - 1;
                    } else {
                        hitTest2.index = count;
                    }
                    return 0;
                }
                if (inType == 0) {
                    if (position > oldIndex) {
                        if (position % columns != 0) {
                            hitTest2.index = position - 1;
                            hitTest2.inIcon = false;
                            return 0;
                        }
                        return 2;
                    }
                    hitTest2.index = position;
                    hitTest2.inIcon = false;
                    return 0;
                } else if (inType == 1) {
                    if (position < oldIndex) {
                        if (position % columns != columns - 1) {
                            hitTest2.index = position + 1;
                            hitTest2.inIcon = false;
                            return 0;
                        }
                        return 2;
                    }
                    hitTest2.index = position;
                    hitTest2.inIcon = false;
                    return 0;
                } else if (inType == 2) {
                    hitTest2.index = position;
                    hitTest2.inIcon = false;
                    if (!isFolder) {
                        hitTest2.inIcon = true;
                        if (position >= icons.size() || (position < icons.size() && icons.get(position) == null)) {
                            hitTest2.inIcon = false;
                        }
                        return 0;
                    } else {
                        hitTest2.inIcon = false;
                        if (position < oldIndex) {
                            if (position % columns != columns - 1) {
                                hitTest2.index = position + 1;
                                return 0;
                            }
                            return 2;
                        } else if (position > oldIndex) {
                            if (position % columns != 0) {
                                hitTest2.index = position - 1;
                                return 0;
                            }
                            return 2;
                        }
                    }

                    return 2;
                }
                if (j == 0) {
                    left = 0;
                }
                left += lc.iconWidth + lc.gapH;
                itemX1 += lc.iconWidth + lc.gapH;
            }
            y1 += top + lc.itemHeight;
        }
        return 2;
    }

	@Override
	public ApplicationInfo getIcon(int index) {
		// TODO Auto-generated method stub
        if ((index < 0) || (index >= icons.size()))
            return null;
        ApplicationInfo info = icons.get(index);
        return info;
	}

	@Override
	public ApplicationInfo getSelectedApp() {
		// TODO Auto-generated method stub
	      if (selected >= 0 && selected < icons.size()) {
	            return icons.get(selected);
	        }
	        return null;
	}

	@Override
	public void deselect() {
		// TODO Auto-generated method stub
        if (selected < 0)
            return;
        selected = -1;
        Point point = selectedLocation;
        invalidate(lc.toItemRect(point.x, point.y));
	}

    public void jiggle() {
    	jiggle = true;
        for (int i = 0; i < icons.size(); i++) {
            ApplicationInfo info = icons.get(i);
            if (info != null) {
                info.jiggle();
            }
        }
        invalidate();
    }

    public void unJiggle() {
        for (int i = 0; i < icons.size(); i++) {
            ApplicationInfo info = icons.get(i);
            if (info != null) {
                info.unJiggle();
            }
        }
        jiggle = false;
        invalidate();
    }


	@Override
	public int getIconsCount() {
		// TODO Auto-generated method stub
        int count = 0;
        for (int i = 0; i < icons.size(); i++) {
            if (icons.get(i) != null) {
                count++;
            }
        }
        return count;
	}

	@Override
	public boolean setMoveTo(int index) {

        int oldIndex = -1;//起始位置
        if (index < 0) {
            return false;
        }
        //查找起始位置，当前页中为null的位置即起始位置
        for (int i = 0; i < icons.size(); i++) {
            if (icons.get(i) == null) {
                oldIndex = i;
                break;
            }
        }
        if (oldIndex == -1) {
            //当前页中没有为空则表示这个应用是新增进来的，默认位置为最后一个。
            oldIndex = icons.size();
            icons.add(null);
        }
        if (index >= icons.size()) {
            int i = oldIndex;
            drawback = icons.size()+FRENSH_NUM;
            for (; i < icons.size() - 1; i++) {
                //向前移动应用
                moveIcon(i + 1, i, 50 * (i - oldIndex));
            }
            icons.set(i, null);
            return true;//移动成功
        }
        ApplicationInfo info = icons.get(index);
        //当前位置为null，则移动到当前位置
        if (info == null) {
            return true;
        }
        int i = 0;
        if (oldIndex > index) {
            drawback = icons.size()+FRENSH_NUM;
            for (i = oldIndex; i > index; i--) {
                //向后移动
                moveIcon(i - 1, i, 50 * (oldIndex - i));
            }
        } else if (oldIndex < index) {
            drawback = icons.size()+FRENSH_NUM;
            for (i = oldIndex; i < index; i++) {
                //向前移动
                moveIcon(i + 1, i, 50 * (i - oldIndex));
            }
        }
        icons.set(i, null);
        return true;
	}

    public void clearUp(ApplicationInfo info) {
        boolean isAdd = info == null ? true : false;
        for (int i = 0; i < icons.size(); i++) {
            ApplicationInfo app = icons.get(i);
            if (app == null) {
                if (info != null) {
                    icons.set(i, info);
                    try {
                        if(info instanceof FolderInfo){
                            info.setOrder(i);
                            PDO.updateOrder(((FolderInfo) info));
                        }else{
                            info.setOrder(i);
                            EDO.updateOrder(info);
                        }
                    }catch (Exception e){
                        Log.i(TAG,"null err2");
                    }
                    isAdd = true;
                } else {
                    int j = i;
                    drawback = icons.size()+FRENSH_NUM;
                    for (; j < icons.size() - 1; j++) {
                        moveIcon(j+1, j, 50 * (j - i + 1));
                    }
                    icons.remove(j);
                }
                messed = true;
            } else {
                if (app.getType() == ItemInfo.TYPE_FOLDER) {
                    FolderInfo folder = (FolderInfo) app;
                    if (folder.getIcons().size() == 0) {
                        int packageid = folder.getPackId();
                        PDO.deletePack(packageid, ConnectionPojo.getInstance().deviceTid);
                        int j = i;
                        drawback = icons.size()+FRENSH_NUM;
                        for (; j < icons.size() - 1; j++) {
                            moveIcon(j+1, j, 50 * (j - i + 1));
                        }
                        icons.remove(j);
                    }
                    messed = true;
                }
            }
        }
        if (!isAdd) {
            icons.add(info);
            messed = true;
        }
        //重新计算行数和视图高度
        rows = (icons.size() - 1) / columns + 1;
        viewHeight = rows * (lc.getItemHeight() + getVerticalGap()) + getVerticalGap();
    }

    /**
     * 移动应用
     * @param from 起始位置
     * @param to 目标位置
     * @param offset 动画延迟
     */
    private void moveIcon(int from, int to, long offset) {
        Point fromP = getIconLocation(from);
        Point toP = getIconLocation(to);
        ApplicationInfo app = icons.get(from);
        if (to < icons.size() && (app != null)) {
            app.tAnim = new TranslateAnimation(fromP.x, toP.x, fromP.y, toP.y);
            app.tAnim.initialize(lc.getItemWidth() + lc.gapH, lc.getItemHeight(), getWidth(), getHeight());
            app.tAnim.setFillAfter(true);
            app.tAnim.setFillBefore(true);
            app.tAnim.setStartOffset(offset);
            app.tAnim.setDuration(0L);
            app.tAnim.startNow();
        }

        setIconIntoPage(to, app);
    }
    
	@Override
    public void removeFolderBound() {
        isAboveFolder = false;
        if (aboveIndex < 0) {
            return;
        }
        final ApplicationInfo app = icons.get(aboveIndex);
        if (app == null) {
            return;
        }
        aboveIndex = -1;
        app.setIsAboveFolder(2);
        app.sAnim = new ScaleAnimation(1F, 1, 1F, 1, 50, 50);
        app.sAnim.initialize(lc.getItemWidth() + lc.gapH, lc.getItemHeight(), getWidth(), getHeight());
        app.sAnim.setFillAfter(true);
        app.sAnim.setFillBefore(true);
        app.sAnim.setDuration(200L);
        app.sAnim.startNow();
        app.sAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                app.setIsAboveFolder(-1);
                invalidate();
            }
        });
    }

    public void setIconIntoPage(int index, ApplicationInfo info) {
        LOG.I("ceshi","setIconIntoPage"+index);
        if (index < 0 || index >= icons.size())
            return;
        ApplicationInfo oldApp = icons.get(index);
        if (oldApp != info) {
            messed = true;
        }

        try {
            if(info instanceof FolderInfo){
                info.setOrder(index);
                PDO.updateOrder(((FolderInfo) info));
            }else{
                info.setOrder(index);
                EDO.updateOrder(info);
            }
        }catch (Exception e){
                 Log.i(TAG,"null err");
        }


        icons.set(index, info);
        invalidateIcon(index);
    }

    private void invalidateIcon(int index) {
    	
        Point point = getIconLocation(index);
        invalidate(lc.toItemRect(point.x, point.y));
    }

	@Override
	public void createFolderBound(int index) {
		// TODO Auto-generated method stub
        aboveIndex = index;
        ApplicationInfo app = icons.get(index);
        if (isAboveFolder == false) {
            app.setIsAboveFolder(1);
            app.sAnim = new ScaleAnimation(1, 1F, 1, 1F);
            app.sAnim.initialize(lc.getItemWidth() + lc.gapH, lc.getItemHeight(), getWidth(), getHeight());
            app.sAnim.setFillAfter(true);
            app.sAnim.setFillBefore(true);
            app.sAnim.setDuration(200L);
            app.sAnim.startNow();
            invalidate();
        }
        isAboveFolder = true;
	}

	@Override
	public void addToFolder(int i, ApplicationInfo app) {
		// TODO Auto-generated method stub
        ApplicationInfo info = icons.get(i);
        FolderInfo folder;
        if (info != null) {
            if (info.getType() == ItemInfo.TYPE_FOLDER) {
                folder = (FolderInfo) info;
                folder.addIcon(app);
                app.setPackId(folder.getPackId());
                EDO.updatePack(app);
            } else {
                folder = new FolderInfo();
                folder.addIcon(info);
                folder.addIcon(app);
                folder.setTitle(MyApplication.getAppResources().getString(R.string.unamed));
                folder.setOrder(i);
                int d = PDO.findPackCount(ConnectionPojo.getInstance().deviceTid);
                folder.setPackId(d+1);
                folder.setDeviceid(ConnectionPojo.getInstance().deviceTid);
                PDO.addPack(folder);
                app.setPackId(d+1);
                info.setPackId(d+1);
                EDO.updatePack(app);
                EDO.updatePack(info);
            }
            setIconIntoPage(i, folder);
        }
	}

	@Override
	public void removeApp(int index) {
		// TODO Auto-generated method stub
        setIconIntoPage(index, null);
        clearUp(null);
	}

	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawNormal(canvas);
        if (drawback>=0) {
            //编辑状态时不断重绘
            invalidate();
            drawback--;
        }
    }

    @Override
    public boolean isMessed() {
        return messed;
    }


    @Override
    public void setMessed(boolean isMessed) {
        messed = isMessed;
    }
    
    public int getViewHeight() {
        return viewHeight;
    }
	
    private int getVerticalGap() {
        return lc.gapV;
    }
	
    private void drawNormal(Canvas canvas) {
        Paint paint = pp.getPaintTextBlack();
        Paint iconPaint = null;
        int gapV = getVerticalGap();
        int y = gapV + lc.marginTop;
        int x;
        for (int i = 0; i < rows; i++) {
            x = lc.marginLeft + lc.gapH;
            for (int j = 0; j < columns; j++) {
                int index = j + i * columns;
                if (index >= icons.size()) {
                    return;
                }
                if (index != selected) {
                    ApplicationInfo info = icons.get(index);
                    if (info != null) {
                        Transformation tFormation = new Transformation();
                        Transformation sFormation = new Transformation();
                        if (info.tAnim != null
                                && info.tAnim
                                        .getTransformation(AnimationUtils.currentAnimationTimeMillis(), tFormation)) {
                            float[] point = new float[2];
                            point[0] = 0.0F;
                            point[1] = 0.0F;
                            tFormation.getMatrix().mapPoints(point);
                            int x1 = (int) point[0];
                            int y1 = (int) point[1];
                            info.drawBoundIcon(lc, pp, canvas, x1, y1, paint, new Paint());
                           // info.drawBlackCircle(lc, pp, canvas, x1 + lc.iconWidth, y1);
                        } else {
                            info.tAnim = null;
                            long time = AnimationUtils.currentAnimationTimeMillis();
                            if (info.sAnim != null && info.sAnim.getTransformation(time, sFormation)) {
                                if (info.getIsAboveFolder() == 1) {
                                    int x1 = (int) (lc.iconWidth  - lc.iconWidth) / 2;
                                    info.drawFolderBound(lc, pp, canvas, x - x1, y - x1, iconPaint, 1.0f);
                                    info.drawIconContent(lc, pp, canvas, x, y, paint);
                                } else if (info.getIsAboveFolder() == 2) {
                                    int x1 = (int) (lc.iconWidth  - lc.iconWidth) / 2;
                                    info.drawFolderBound(lc, pp, canvas, x - x1, y - x1, iconPaint, 1.0f);
                                    info.drawIconContent(lc, pp, canvas, x, y, paint);
                                } else {
                                    if (isAboveFolder && info.getIsAboveFolder() == 1) {
                                        int x1 = (int) (lc.iconWidth - lc.iconWidth) / 2;
                                        info.drawFolderBound(lc, pp, canvas, x - x1, y - x1, iconPaint, 1.0f);
                                        info.drawIconContent(lc, pp, canvas, x, y, paint);
                                    } else {
                                        drawNormalIcon(info, canvas, x, y, paint);
                                    }
                                }
                            } else {
                                info.sAnim = null;
                                if (isAboveFolder && info.getIsAboveFolder() == 1) {
                                    int x1 = (int) (lc.iconWidth  - lc.iconWidth) / 2;
                                    info.drawFolderBound(lc, pp, canvas, x - x1, y - x1, iconPaint, 1.0f);
                                    info.drawIconContent(lc, pp, canvas, x, y, paint);
                                } else {
                                    drawNormalIcon(info, canvas, x, y, paint);
                                }

                            }
                        }

                    }
                } else {
                    ApplicationInfo info = icons.get(index);
                    if (info != null) {
                        iconPaint = pp.getPaintDarkener();
                        ApplicationInfo app = (ApplicationInfo) info;
                        app.drawBoundIcon(lc, pp, canvas, x, y, paint, iconPaint);
                        if (app.isJiggle()) {
                            app.drawBlackCircle(lc, pp, canvas, x + lc.iconWidth, y);
                        }
                    }
                }
                x += lc.iconWidth + lc.gapH;
            }
            y += gapV + lc.itemHeight;
        }
    }
    private void drawNormalIcon(ApplicationInfo info, Canvas canvas, int x, int y, Paint paint) {
        info.drawBoundIcon(lc, pp, canvas, x, y, paint, null);
        if (info.isJiggle()) {
            info.drawBlackCircle(lc, pp, canvas, x + lc.iconWidth, y);
        }
    }
    
	
    public void setIcons(List<ApplicationInfo> icons1) {
    	icons = icons1;
        columns = 3;
        rows = (icons.size() - 1) / columns + 1;
        viewHeight = rows * (lc.getItemHeight() + getVerticalGap()) + getVerticalGap();

    }

}
