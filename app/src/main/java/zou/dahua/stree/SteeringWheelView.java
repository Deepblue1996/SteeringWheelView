package zou.dahua.stree;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 方向盘 Steering wheel
 * Created by Deep on 2018/7/6 0006.
 */

public class SteeringWheelView extends View {

    // 箭头长度
    private final static int directionWidth = 25;

    // 箭头宽度
    private final static float DIRECTION_WIDTH = 6.0f;

    // X偏移值
    private final static int deviationX = 2;
    // Y偏移值
    private final static int deviationY = 4;

    // 大圆宽度
    private float CIRCLE_WIDTH;

    // 大圆直径
    private float WIDTH_HEIGHT;

    // 大圆画笔
    private Paint paintCircle;

    // 箭头画笔
    private Paint paintDirectionUpper;
    private Paint paintDirectionLower;
    private Paint paintDirectionLeft;
    private Paint paintDirectionRight;

    // 外圈画笔
    private Paint paintMiddleCircle;

    // 内图标画笔
    private Paint paintMiddleCircleBg;

    // 内图标画图区域
    private RectF oval;
    // 外大圆指向区域
    private RectF ovalDeviation;

    // 箭头路径
    private Path path1;
    private Path path2;
    private Path path3;
    private Path path4;

    // 各控件使用状态
    private boolean upperTurn = false;
    private boolean lowerTurn = false;
    private boolean leftTurn = false;
    private boolean rightTurn = false;
    private boolean MTurn = false;

    private WheelTouch wheelTouch;

    public SteeringWheelView(Context context) {
        super(context);
        initData(context);
    }

    public SteeringWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public SteeringWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SteeringWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(context);
    }

    public void setWheelTouch(WheelTouch wheelTouch) {
        this.wheelTouch = wheelTouch;
    }

    /**
     * convert dp to its equivalent px
     * <p>
     * 将dp转换为与之相等的px
     */
    public static float dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    /**
     * 初始化数据
     * @param context 上下文
     */
    private void initData(Context context) {

        WIDTH_HEIGHT = dp2px(context, 180);

        CIRCLE_WIDTH = 6.0f;

        paintCircle = new Paint();

        paintCircle.setAntiAlias(true);
        paintCircle.setDither(true);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(CIRCLE_WIDTH);
        paintCircle.setColor(Color.parseColor("#97694F"));

        // 上
        paintDirectionUpper = new Paint();
        paintDirectionUpper.setAntiAlias(true);
        paintDirectionUpper.setDither(true);
        paintDirectionUpper.setStyle(Paint.Style.STROKE);
        paintDirectionUpper.setStrokeWidth(DIRECTION_WIDTH);
        paintDirectionUpper.setStrokeCap(Paint.Cap.ROUND);
        paintDirectionUpper.setColor(Color.parseColor("#97694F"));

        // 下
        paintDirectionLower = new Paint();
        paintDirectionLower.setAntiAlias(true);
        paintDirectionLower.setDither(true);
        paintDirectionLower.setStyle(Paint.Style.STROKE);
        paintDirectionLower.setStrokeWidth(DIRECTION_WIDTH);
        paintDirectionLower.setStrokeCap(Paint.Cap.ROUND);
        paintDirectionLower.setColor(Color.parseColor("#97694F"));

        // 左
        paintDirectionLeft = new Paint();
        paintDirectionLeft.setAntiAlias(true);
        paintDirectionLeft.setDither(true);
        paintDirectionLeft.setStyle(Paint.Style.STROKE);
        paintDirectionLeft.setStrokeWidth(DIRECTION_WIDTH);
        paintDirectionLeft.setStrokeCap(Paint.Cap.ROUND);
        paintDirectionLeft.setColor(Color.parseColor("#97694F"));

        // 右
        paintDirectionRight = new Paint();
        paintDirectionRight.setAntiAlias(true);
        paintDirectionRight.setDither(true);
        paintDirectionRight.setStyle(Paint.Style.STROKE);
        paintDirectionRight.setStrokeWidth(DIRECTION_WIDTH);
        paintDirectionRight.setStrokeCap(Paint.Cap.ROUND);
        paintDirectionRight.setColor(Color.parseColor("#97694F"));

        // 中间外圈
        paintMiddleCircle = new Paint();
        paintMiddleCircle.setAntiAlias(true);
        paintMiddleCircle.setDither(true);
        paintMiddleCircle.setStyle(Paint.Style.STROKE);
        paintMiddleCircle.setStrokeWidth(DIRECTION_WIDTH);
        paintMiddleCircle.setStrokeCap(Paint.Cap.ROUND);
        paintMiddleCircle.setColor(Color.parseColor("#333333"));

        // 中间内圈
        paintMiddleCircleBg = new Paint();
        paintMiddleCircleBg.setAntiAlias(true);
        paintMiddleCircleBg.setDither(true);
        paintMiddleCircleBg.setStyle(Paint.Style.STROKE);
        paintMiddleCircleBg.setStrokeWidth(DIRECTION_WIDTH);
        paintMiddleCircleBg.setStrokeCap(Paint.Cap.ROUND);
        paintMiddleCircleBg.setColor(Color.parseColor("#FFFFFF"));

        // 内圈区域
        oval = new RectF(WIDTH_HEIGHT / 2 - WIDTH_HEIGHT / 15 + deviationX, WIDTH_HEIGHT / 2 - WIDTH_HEIGHT / 15 + 2 + deviationY,
                WIDTH_HEIGHT / 2 + WIDTH_HEIGHT / 15 + deviationX, WIDTH_HEIGHT / 2 + WIDTH_HEIGHT / 15 + 2 + deviationY);

        // 大外圈区域
        ovalDeviation = new RectF(deviationX * 2, deviationY * 2, WIDTH_HEIGHT + deviationX * 2, WIDTH_HEIGHT + deviationY * 2);

        // 箭头路径
        path1 = new Path();
        path2 = new Path();
        path3 = new Path();
        path4 = new Path();
    }

    /**
     * 比onDraw先执行
     * <p>
     * 一个MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。
     * 一个MeasureSpec由大小和模式组成
     * 它有三种模式：UNSPECIFIED(未指定),父元素部队自元素施加任何束缚，子元素可以得到任意想要的大小;
     * EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
     * AT_MOST(至多)，子元素至多达到指定大小的值。
     * <p>
     * 它常用的三个函数：
     * 1.static int getMode(int measureSpec):根据提供的测量值(格式)提取模式(上述三个模式之一)
     * 2.static int getSize(int measureSpec):根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)
     * 3.static int makeMeasureSpec(int size,int mode):根据提供的大小值和模式创建一个测量值(格式)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        Log.e("YView", "---minimumWidth = " + minimumWidth + "");
        Log.e("YView", "---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.e("YViewWidth", "---speSize = " + specSize + "");


        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) (WIDTH_HEIGHT + CIRCLE_WIDTH * 2 + getPaddingLeft() + getPaddingRight());

                Log.e("YViewWidth", "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.e("YViewWidth", "---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.e("YViewWidth", "---speMode = UNSPECIFIED");
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }

    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.e("YViewHeight", "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (WIDTH_HEIGHT + CIRCLE_WIDTH * 2 + getPaddingTop() + getPaddingBottom());
                Log.e("YViewHeight", "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                Log.e("YViewHeight", "---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                Log.e("YViewHeight", "---speSize = UNSPECIFIED");

//        1.基准点是baseline
//        2.ascent：是baseline之上至字符最高处的距离
//        3.descent：是baseline之下至字符最低处的距离
//        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
//        5.top：是指的是最高字符到baseline的值,即ascent的最大值
//        6.bottom：是指最低字符到baseline的值,即descent的最大值

                break;
        }
        return defaultHeight;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#000000"));

        canvas.drawCircle(WIDTH_HEIGHT / 2 + CIRCLE_WIDTH, WIDTH_HEIGHT / 2 + CIRCLE_WIDTH + 2, WIDTH_HEIGHT / 2, paintCircle);

        if (upperTurn) {
            paintDirectionUpper.setColor(Color.WHITE);
            canvas.drawArc(ovalDeviation, 90 * 3 - 45, 90, false, paintDirectionUpper);
        } else {
            paintDirectionUpper.setColor(Color.parseColor("#97694F"));
        }
        // 上箭头
        path1.moveTo(WIDTH_HEIGHT / 2 - directionWidth + deviationX, WIDTH_HEIGHT / 8 + directionWidth + deviationY); // 路径path默认是在原点(0,0)，当前移植到(100,100)
        path1.lineTo(WIDTH_HEIGHT / 2 + deviationX, WIDTH_HEIGHT / 8 + deviationY);
        path1.lineTo(WIDTH_HEIGHT / 2 + directionWidth + deviationX, WIDTH_HEIGHT / 8 + directionWidth + deviationY);
        paintDirectionUpper.setStrokeJoin(Paint.Join.BEVEL);

        canvas.drawPath(path1, paintDirectionUpper);

        if (lowerTurn) {
            paintDirectionLower.setColor(Color.WHITE);
            canvas.drawArc(ovalDeviation, 90 - 45, 90, false, paintDirectionLower);
        } else {
            paintDirectionLower.setColor(Color.parseColor("#97694F"));
        }
        // 下箭头
        path2.moveTo(WIDTH_HEIGHT / 2 - directionWidth + deviationX, WIDTH_HEIGHT - WIDTH_HEIGHT / 8 - directionWidth + deviationY);
        path2.lineTo(WIDTH_HEIGHT / 2 + deviationX, WIDTH_HEIGHT - WIDTH_HEIGHT / 8 + deviationY);
        path2.lineTo(WIDTH_HEIGHT / 2 + directionWidth + deviationX, WIDTH_HEIGHT - WIDTH_HEIGHT / 8 - directionWidth + deviationY);
        paintDirectionLower.setStrokeJoin(Paint.Join.BEVEL);

        canvas.drawPath(path2, paintDirectionLower);

        if (leftTurn) {
            paintDirectionLeft.setColor(Color.WHITE);
            canvas.drawArc(ovalDeviation, 90 * 2 - 45, 90, false, paintDirectionLeft);
        } else {
            paintDirectionLeft.setColor(Color.parseColor("#97694F"));
        }

        // 左箭头
        path3.moveTo(WIDTH_HEIGHT / 8 + directionWidth + deviationX, WIDTH_HEIGHT / 2 - directionWidth + deviationY);
        path3.lineTo(WIDTH_HEIGHT / 8 + deviationX, WIDTH_HEIGHT / 2 + deviationY);
        path3.lineTo(WIDTH_HEIGHT / 8 + directionWidth + deviationX, WIDTH_HEIGHT / 2 + directionWidth + deviationY);
        paintDirectionLeft.setStrokeJoin(Paint.Join.BEVEL);

        canvas.drawPath(path3, paintDirectionLeft);

        if (rightTurn) {
            paintDirectionRight.setColor(Color.WHITE);
            canvas.drawArc(ovalDeviation, -45, 90, false, paintDirectionRight);
        } else {
            paintDirectionRight.setColor(Color.parseColor("#97694F"));
        }
        // 右箭头
        path4.moveTo(WIDTH_HEIGHT - WIDTH_HEIGHT / 8 - directionWidth + deviationX, WIDTH_HEIGHT / 2 - directionWidth + deviationY);
        path4.lineTo(WIDTH_HEIGHT - WIDTH_HEIGHT / 8 + deviationX, WIDTH_HEIGHT / 2 + deviationY);
        path4.lineTo(WIDTH_HEIGHT - WIDTH_HEIGHT / 8 - directionWidth + deviationX, WIDTH_HEIGHT / 2 + directionWidth + deviationY);
        paintDirectionRight.setStrokeJoin(Paint.Join.BEVEL);

        canvas.drawPath(path4, paintDirectionRight);

        if (MTurn) {
            paintMiddleCircle.setColor(Color.parseColor("#FFFFFF"));
            paintMiddleCircleBg.setColor(Color.parseColor("#333333"));
        } else {
            paintMiddleCircle.setColor(Color.parseColor("#333333"));
            paintMiddleCircleBg.setColor(Color.parseColor("#FFFFFF"));
        }

        canvas.drawCircle(WIDTH_HEIGHT / 2 + deviationX, WIDTH_HEIGHT / 2 + deviationY, WIDTH_HEIGHT / 6, paintMiddleCircle);

        canvas.drawArc(oval, -90 + 25, 360 - 50, false, paintMiddleCircleBg);

        canvas.drawLine(WIDTH_HEIGHT / 2 + deviationX, WIDTH_HEIGHT / 2 - WIDTH_HEIGHT / 15 - 2 + deviationY,
                WIDTH_HEIGHT / 2 + deviationX, WIDTH_HEIGHT / 2 - WIDTH_HEIGHT / 15 + 30 + deviationY, paintMiddleCircleBg);
    }

    /**
     * 默认值
     */
    private void cleanDef() {
        upperTurn = false;
        rightTurn = false;
        lowerTurn = false;
        leftTurn = false;
        MTurn = false;
    }

    /**
     * 处理触摸区域
     *
     * @param event 事件
     */
    private void downAndMove(MotionEvent event) {
        // 计算角度
        double MC = Math.atan2(event.getY() - WIDTH_HEIGHT / 2, event.getX() - WIDTH_HEIGHT / 2);
        double MK = 180 * MC / Math.PI;
        // 计算距离
        double MJ = Math.sqrt(Math.pow(event.getX() - WIDTH_HEIGHT / 2, 2) +
                Math.pow(event.getY() - WIDTH_HEIGHT / 2, 2));
        // 清空
        cleanDef();

        // 判断
        if (MJ > WIDTH_HEIGHT / 15 + 20) {
            if (MK < -45 && MK > -180 + 45) {
                upperTurn = true;
                if(wheelTouch!=null) {
                    wheelTouch.upperTurn();
                }
            } else if (MK > -45 && MK < 45) {
                rightTurn = true;
                if(wheelTouch!=null) {
                    wheelTouch.rightTurn();
                }
            } else if (MK > 45 && MK < 180 - 45) {
                lowerTurn = true;
                if(wheelTouch!=null) {
                    wheelTouch.lowerTurn();
                }
            } else {
                leftTurn = true;
                if(wheelTouch!=null) {
                    wheelTouch.leftTurn();
                }
            }
        } else {
            MTurn = true;
            if(wheelTouch!=null) {
                wheelTouch.MTurn();
            }
        }
        // 重绘
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                downAndMove(event);
                break;
            case MotionEvent.ACTION_UP:
                cleanDef();
                invalidate();
                break;
        }
        return true;
    }
}
