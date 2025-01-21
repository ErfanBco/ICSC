package shady.bco.js;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class Data {
    public static String GROUP_NAME=null;


    public static int[] getDisplayMetrics(WindowManager windowManager) {
        int DisplayWidth,DisplayHeight;
        Display DisplaySize = windowManager.getDefaultDisplay();
        final Point Size = new Point();
        DisplaySize.getSize(Size);
        DisplayWidth = Size.x;
        DisplayHeight = Size.y;
        return new int[] {DisplayWidth,DisplayHeight};
    }
}
