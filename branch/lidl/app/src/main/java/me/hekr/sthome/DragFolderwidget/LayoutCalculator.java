package me.hekr.sthome.DragFolderwidget;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.ViewGroup;

public class LayoutCalculator {
    public static final int BLACKCIRCLE_RADIUS = 13;
    private static final int FOLDER_GAP = 3;
    private static final int FOLDER_ICON_SIZE = 13;
    public static final int ICON_HEIGHT = 60;
    public static final int ICON_WIDTH = 60;
    public static final int PAGER_GAP = 10;
    public static final int PAGER_HEIGHT = 8;
    public static final int REDCIRCLE_MARGIN_TOP = 8;
    public static final int REDCIRCLE_RADIUS = 12;
    public static final int WALLPAPER_SHADOW_ADJUSTMENT = 16;
    public static int columns =3;
    public static int rows = 5;
    public static int iconsPerPage = columns * rows;
    public int bcMarginLeft;
    public int bcMarginTop;
    public int cMarginTop;
    public boolean canBeTablet = false;
    public int folderGap;
    public int folderIconSize;
    public int folderMargin;
    public int folderMaxHeight;
    public int fullMarginLeft;
    public int fullMarginLeftBlackCircle;
    public int fullMarginRight;
    public int gapFolderV;
    public int gapH;
    public int gapH2;
    public int gapV;
    public int height;
    public int iconHeight;
    public int iconLastMarginRight;
    public int iconMarginLeft;
    public int iconMarginLeftBlackCircle;
    public int iconWidth;
    public boolean isHighResolution;
    public boolean isPortrait = true;
    public boolean isTablet = false;
    public boolean isWide = false;
    public int itemHeight;
    public int marginLeft;
    public int marginLeft2;
    public int marginTop;
    public int pageHeight;
    public int pagerGap;//索引点的间距
    public int rcMarginRight;
    public int rcMarginTop;
    private float scale;
    public int searchIconHeight;
    public int searchIconWidth;
    public int shadowMarginBottom;
    public int shadowMarginLeft;
    public int shadowMarginRight;
    public int textTop;
    public int width;

    public LayoutCalculator(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        scale = displayMetrics.density;
        int Dpi = displayMetrics.densityDpi;
        float f = (float)Math.sqrt(displayMetrics.widthPixels * displayMetrics.widthPixels + displayMetrics.heightPixels
                * displayMetrics.heightPixels)
                / Dpi;
        int icon = (int) (0.65F * Dpi * (f / 9.697F));
        iconHeight = icon;
        iconWidth = icon;
        textTop = iconHeight + dpToPixel(18);
        itemHeight = iconHeight + dpToPixel(21);
        int radius = dpToPixel(BLACKCIRCLE_RADIUS);
        int dp1 = dpToPixel(1);
        bcMarginLeft = (radius - dp1);
        bcMarginTop = (radius - dp1);
        rcMarginTop = dpToPixel(REDCIRCLE_MARGIN_TOP);
        rcMarginRight = rcMarginTop;
        cMarginTop = Math.max(bcMarginTop, rcMarginTop);
        pagerGap = dpToPixel(PAGER_GAP);
        this.folderIconSize = expandWidth(FOLDER_ICON_SIZE);
        this.folderGap = (FOLDER_GAP * this.iconWidth / 60);
        folderMargin = dpToPixel(20);
    }




	public int getIconWidth() {
        return iconWidth;
    }

    public int getItemWidth() {
        return iconMarginLeft + iconWidth + iconLastMarginRight;
    }

    public int expandWidth(int dp) {
        return dp * iconWidth / 60;
    }

    public void layoutReady(ViewGroup viewGroup) {
        width = viewGroup.getWidth();
        height = viewGroup.getHeight();
        iconsPerPage = columns * rows;
        int black = width - iconWidth * columns;
        int black2 = width - iconWidth * 4;
        int pagerGap = dpToPixel(PAGER_GAP) * columns;
        int pagerGap2 = dpToPixel(PAGER_GAP) * 4;
        int pagerHeight = dpToPixel(PAGER_HEIGHT);
        int l = height - pagerHeight - itemHeight * rows;
        gapV = (l / (2 + rows));
        gapH = ((black + pagerGap) / (1 + columns));
        gapH2 =  ((black2 + pagerGap2) / (1 + 4));
        marginLeft = (-(pagerGap / 2));
        marginLeft2 = (-(pagerGap2 / 2));
        marginTop = (l % (2 + rows) / 2);
        iconMarginLeft = Math.min(iconWidth / 4, gapH / 2)+15;
        iconMarginLeftBlackCircle = Math.max(iconMarginLeft, bcMarginLeft);
        iconLastMarginRight = Math.max(iconMarginLeft, rcMarginRight);
        fullMarginLeft = Math.max(shadowMarginLeft, iconMarginLeftBlackCircle);
        fullMarginRight = Math.max(shadowMarginRight, iconLastMarginRight);
        fullMarginLeftBlackCircle = Math.max(fullMarginLeft, bcMarginLeft);
    }

    public Rect toItemRect(int x, int y) {
        Rect rect = new Rect();
        rect.left = (x - iconMarginLeft);
        rect.right = (x + iconWidth + iconMarginLeft + rcMarginRight);
        rect.top = (y - rcMarginTop);
        rect.bottom = (y + itemHeight + shadowMarginBottom);
        return rect;
    }

    public float dpToPixel(float paramFloat) {
        return paramFloat * scale;
    }

    public int dpToPixel(int paramInt) {
        return (int) (0.5F + paramInt * scale);
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public int getVerticalGap() {
        return gapV;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public float propIconHeight(float dp) {
        return dp * iconHeight / ICON_HEIGHT;
    }

    public int getFullItemWidthBlackCircle() {
        return fullMarginLeftBlackCircle + iconWidth + fullMarginRight;
    }

    public int getFullItemHeight() {
        return cMarginTop + itemHeight + shadowMarginBottom;
    }

    public int getFullItemWidth() {
        return this.iconMarginLeft + this.iconWidth + this.fullMarginRight;
    }

    public int getHorizontalGap(int index) {
        return (this.width - index * this.iconWidth) / (index + 1);
    }

    public int getHorizontalGap() {
        return this.gapH;
    }
}
