package com.example.shaunrain.gesturelock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.shaunrain.gesturelock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShaunRain on 15/9/28.
 */
public class LockPatternView extends View {

    private static final int POINT_SIZE = 5;

    private Point[][] points = new Point[3][3];

    private Matrix matrix = new Matrix();

    private float width, height, offstart, moveX, moveY, pointRadius;

    private Bitmap bitmap_pressed, bitmap_normal, bitmap_error, bitmap_disable, bitmap_line, bitmap_line_press, bitmap_line_error;

    private OnPatternChangeListener listener;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        linePaint.setAlpha(65);
    }

    private List<Point> pointList = new ArrayList<>();//已连接的点集合

    public LockPatternView(Context context) {
        super(context);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 绘制九宫格
     */
    private boolean isInit, isSelect, isFinish, movingNotPoint, isStart;

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            //初始化九个点
            initPoints();
        }
        //绘制九个点
        points2Canvas(canvas);

        //画线
        if (pointList.size() > 0) {
            Point a = pointList.get(0);
            //绘制九宫格坐标点
            for (int i = 0; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                line2Canvas(canvas, a, b);
                a = b;
            }
            //绘制手指坐标点
            if (movingNotPoint) {
                line2Canvas(canvas, a, new Point(moveX, moveY));
            }

        }
    }

    /**
     * 初始化九個點
     */
    private void initPoints() {

        //获取布局宽高
        width = getWidth();
        height = getHeight();

        //横屏和竖屏
        if (width > height) {
            offstart = (width - height) / 2;
            width = height;
        } else {
            offstart = (height - width) / 2;
            height = width;
        }

        //图片资源
        bitmap_normal = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        bitmap_error = BitmapFactory.decodeResource(getResources(), R.drawable.circle_error);
        bitmap_pressed = BitmapFactory.decodeResource(getResources(), R.drawable.circle_press);
        bitmap_disable = BitmapFactory.decodeResource(getResources(), R.drawable.circle_disable);

        pointRadius = bitmap_normal.getWidth() / 2;

        bitmap_line = BitmapFactory.decodeResource(getResources(), R.drawable.line);
        bitmap_line_press = BitmapFactory.decodeResource(getResources(), R.drawable.line_press);
        bitmap_line_error = BitmapFactory.decodeResource(getResources(), R.drawable.line_error);

        points[0][0] = new Point(width / 4, offstart + width / 4);
        points[0][1] = new Point(width / 2, offstart + width / 4);
        points[0][2] = new Point((width / 4) * 3, offstart + width / 4);

        points[1][0] = new Point(width / 4, offstart + width / 2);
        points[1][1] = new Point(width / 2, offstart + width / 2);
        points[1][2] = new Point((width / 4) * 3, offstart + width / 2);

        points[2][0] = new Point(width / 4, offstart + (width / 4) * 3);
        points[2][1] = new Point(width / 2, offstart + (width / 4) * 3);
        points[2][2] = new Point((width / 4) * 3, offstart + (width / 4) * 3);

        int index = 0;
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++)
                points[i][j].index = index++;
        }

        isInit = true;

    }

    /**
     * 点绘制
     */
    private void points2Canvas(Canvas canvas) {

        Point point = null;

        //循环遍历
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                point = points[i][j];
                if (point.state == Point.STATE_PRESS) {
                    canvas.drawBitmap(bitmap_pressed, point.x - pointRadius
                            , point.y - pointRadius, paint);
                } else if (point.state == Point.STATE_ERROR) {
                    canvas.drawBitmap(bitmap_error, point.x - pointRadius
                            , point.y - pointRadius, paint);
                } else if (point.state == Point.STATE_NORMAL) {
                    canvas.drawBitmap(bitmap_normal, point.x - pointRadius
                            , point.y - pointRadius, paint);
                } else {
                    canvas.drawBitmap(bitmap_disable, point.x - pointRadius
                            , point.y - pointRadius, paint);
                }
            }
        }
    }

    /**
     * 线绘制
     *
     * @param a 起点
     * @param b 终点
     */
    public void line2Canvas(Canvas canvas, Point a, Point b) {
        //获取距离
        float lineLength = (float) Point.distance(a, b);
        //获取角度
        float degree = Point.getDegree(a, b);
        //根据a点进行旋转
        canvas.rotate(degree, a.x, a.y);

        if (a.state == Point.STATE_PRESS) {
            //xy方向上的缩放比例
            matrix.setScale(lineLength / bitmap_line_press.getWidth(), 1);
            matrix.postTranslate(a.x, a.y - bitmap_line_press.getHeight() / 2);
            canvas.drawBitmap(bitmap_line_press, matrix, linePaint);
        } else {
            matrix.setScale(lineLength / bitmap_line_error.getWidth(), 1);
            matrix.postTranslate(a.x, a.y - bitmap_line_error.getHeight() / 2);
            canvas.drawBitmap(bitmap_line_error, matrix, linePaint);
        }
        //画线完成恢复角度
        canvas.rotate(-degree, a.x, a.y);

    }

    /**
     * onTouch事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveX = event.getX();
        moveY = event.getY();

        movingNotPoint = false;
        //isStart = false;
        isFinish = false;

        Point point = null;

        switch (event.getAction()) {
            //按下操作,代表重新绘制界面
            case MotionEvent.ACTION_DOWN:
                resetPoint();

                point = checkSelectPoint();
                if (point != null)
                    isSelect = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSelect) {
                    if (listener != null)
                        listener.onPatternStart(true);
                    point = checkSelectPoint();
                    if (point == null)
                        movingNotPoint = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish = true;
                isSelect = false;
                break;

        }
        //选中重复检查
        if (!isFinish && isSelect && point != null) {
            //交叉点
            if (crossPoint(point)) {
                movingNotPoint = true;
            }
            //新点
            else {
                if (pointList.size() > 0) {
                    int mid = jumpPoint(pointList.get(pointList.size() - 1), point);
                    if (mid != -1) {
                        Point midPoint = points[mid / points.length][mid % points.length];
                        midPoint.state = Point.STATE_PRESS;
                        pointList.add(midPoint);
                    }
                }
                point.state = Point.STATE_PRESS;
                pointList.add(point);
            }
        }
        //绘制结束
        if (isFinish) {
            int size = pointList.size();
            //绘制不成立
            if (size == 1) {
                beError();
            }
            //绘制错误
            else if (size < POINT_SIZE && size >= 2) {
                beError();
                if (listener != null)
                    listener.onPatternChange(null);
            }
            //绘制成功
            else {
                if (listener != null) {
                    StringBuilder password = new StringBuilder();
                    for (Point p : pointList)
                        password.append(p.index + "");
                    if (!TextUtils.isEmpty(password.toString()))
                        listener.onPatternChange(password.toString());
                }
            }
        }

        //刷新
        postInvalidate();
        return true;
    }

    /**
     * 交叉点检查
     */
    public boolean crossPoint(Point p) {
        if (pointList.contains(p))
            return true;
        else
            return false;
    }

    /**
     * 跳跃点检查
     */
    public int jumpPoint(Point lastPoint, Point p) {
        if (lastPoint == null)
            return -1;
        if ((lastPoint.index + p.index) % 2 != 0)
            return -1;
        int mid = (lastPoint.index + p.index) / 2;
        if ((mid % 2 == 0 && mid != 4) || (p.index == 4 && (mid == 3 || mid == 5)))
            return -1;
        for (Point po : pointList) {
            if (po.index == mid)
                return -1;
        }
        return mid;
    }

    public void resetPoint() {

        for (Point p : pointList)
            p.state = Point.STATE_NORMAL;
        pointList.clear();
        listener.onPatternReset();
    }

    public void beError() {
        for (Point p : pointList)
            p.state = Point.STATE_ERROR;
        listener.onPatternError();
    }

    public void disablePoint() {
        resetPoint();
        Log.d("AllApp", isInit + "");
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[0].length; j++) {
                points[i][j].state = Point.STATE_DISABLE;
            }
        }
        listener.onPatternDisable();
    }

    /**
     * 在九宫格里?
     *
     * @returnß
     */
    private Point checkSelectPoint() {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point p = points[i][j];
                if (p.with(new Point(moveX, moveY), pointRadius))
                    return p;
            }
        }
        return null;
    }

    //自定义点
    public static class Point {

        //三种状态
        private static int STATE_NORMAL = 0;
        private static int STATE_PRESS = 1;
        private static int STATE_ERROR = 2;
        private static int STATE_DISABLE = 3;

        //坐标
        public float x, y;

        public int index = 0;
        public int state = 0;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        /**
         * 两点距离
         */
        public static double distance(Point a, Point b) {
            return Math.sqrt((a.x - b.x) * (a.x - b.x)
                    + (a.y - b.y) * (a.y - b.y));
        }

        /**
         * 两点直线角度
         */
        public static float getDegree(Point a, Point b) {
            return (float) Math.toDegrees(Math.atan2(b.y - a.y, b.x - a.x));
        }

        /**
         * 是否重合
         *
         * @param b 移动点
         * @param r 圆点半径
         */
        public boolean with(Point b, float r) {
            return distance(this, b) < r;
        }

    }

    /**
     * 图案监听器
     */
    public static interface OnPatternChangeListener {
        void onPatternChange(String password);

        void onPatternError();

        void onPatternReset();

        void onPatternStart(boolean isStart);

        void onPatternDisable();
    }

    /**
     * 设置监听器
     */
    public void setPatternListener(OnPatternChangeListener listener) {
        if (listener != null)
            this.listener = listener;
    }

}


