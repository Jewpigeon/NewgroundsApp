package jewpigeon.apps.newgrounds.Views.DashboardData.DashItems.DashDataViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import jewpigeon.apps.newgrounds.R;
import jewpigeon.apps.newgrounds.Utils.DimensionTool;
import jewpigeon.apps.newgrounds.Views.DashboardData.DashItems.DashDataItems.ForumItem;

public class ForumItemView extends ViewGroup {

    private final int ITEM_HEIGHT = (int) DimensionTool.dp(44);
    private final int STATUS_ICON_SIZE = ITEM_HEIGHT * 6 / 7;
    private final int MOOD_ICON_SIZE = ITEM_HEIGHT *6/7;
    private Drawable DashBackground;
    private final int TopicColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
    private final int AmbientColor = ContextCompat.getColor(getContext(), R.color.colorItemAuthorText);
    private final int DashBackgroundEnabledColor = ContextCompat.getColor(getContext(), R.color.colorFeaturedAudioItemBackground);

    private TextView ForumTopic;
    private TextView ForumStarterUser;
    private TextView ForumStartDate;

    private ImageView ForumMood;

    private boolean isBackgroundEnabled = false;

    private Drawable statusDefIcon;
    private Drawable moodDefIcon;
    private StateListDrawable DashForeground;


    {
        statusDefIcon = ContextCompat.getDrawable(getContext(), R.drawable.ng_bbs_nonew);
        moodDefIcon = ContextCompat.getDrawable(getContext(), R.drawable.ng_smile_blank);

        DashBackground = new ColorDrawable(DashBackgroundEnabledColor);

        DashForeground = new StateListDrawable();

        DashForeground.setEnterFadeDuration(50);
        DashForeground.setExitFadeDuration(300);

        DashForeground.addState(new int[]{android.R.attr.state_pressed}, ContextCompat.getDrawable(getContext(), R.color.colorGridRippleEffect));

        DashForeground.setCallback(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        DashForeground.setBounds(0, 0, w, h);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || (who == DashForeground);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        DashForeground.jumpToCurrentState();
    }



    public ForumItemView(Context context) {
        super(context);
        establishSelf(context);
    }


    public ForumItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        establishSelf(context);
    }

    public ForumItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        establishSelf(context);
    }

    public ForumItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        establishSelf(context);
    }


    private void establishSelf(Context context) {
        ForumTopic = new TextView(context);
        ForumStarterUser = new TextView(context);
        ForumStartDate = new TextView(context);


        ForumMood = new ImageView(context);

        ForumMood.setImageDrawable(moodDefIcon);

        ForumTopic.setTextColor(TopicColor);
        ForumTopic.setTypeface(Typeface.DEFAULT_BOLD);
        ForumTopic.setTextSize(DimensionTool.sp(5));
        ForumTopic.setText("DEFAULT");
        //ForumTopic.setInputType(InputType.TYPE_CLASS_NUMBER);

        ForumStarterUser.setTextColor(TopicColor);
        ForumStarterUser.setTextSize(DimensionTool.sp(5));
        ForumStarterUser.setText("DEFAULT");


        ForumStartDate.setTextColor(AmbientColor);
        ForumStartDate.setTextSize(DimensionTool.sp(4));
        ForumStartDate.setText("0/0/0");


        addView(ForumTopic);
        addView(ForumMood);
        addView(ForumStarterUser);
        addView(ForumStartDate);


        this.setClickable(true);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        DashForeground.setState(getDrawableState());
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ForumTopic.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED));
        ForumStarterUser.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED));
        ForumStartDate.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED));

        ForumMood.measure(MeasureSpec.makeMeasureSpec(MOOD_ICON_SIZE, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(MOOD_ICON_SIZE, MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(ITEM_HEIGHT, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;

        ForumMood.layout(
                STATUS_ICON_SIZE/8,
                (ITEM_HEIGHT - MOOD_ICON_SIZE)/2,
                STATUS_ICON_SIZE*9/8,
                ITEM_HEIGHT - (ITEM_HEIGHT-MOOD_ICON_SIZE)/2
        );
        ForumTopic.layout(
                STATUS_ICON_SIZE*2/8 + MOOD_ICON_SIZE + 16,
                (MOOD_ICON_SIZE-ForumTopic.getMeasuredHeight())/2 + 4,
                STATUS_ICON_SIZE*2/8 + MOOD_ICON_SIZE + 16 + ForumTopic.getMeasuredWidth(),
                (MOOD_ICON_SIZE+ForumTopic.getMeasuredHeight())/2 + 4
        );
        ForumStarterUser.layout(
                width-ForumStarterUser.getMeasuredWidth()-64,
                (height - ForumStarterUser.getMeasuredHeight())/6,
                width-64,
                (height - ForumStarterUser.getMeasuredHeight())
        );
        ForumStartDate.layout(
                width-ForumStarterUser.getMeasuredWidth()-64,
                (height - ForumStarterUser.getMeasuredHeight()) + 4,
                width - 64,
                (height - ForumStarterUser.getMeasuredHeight())+ 4 + ForumStartDate.getMeasuredHeight()
        );
    }

    public void setDashItem(ForumItem item){
        Glide.with(getContext())
                .load(item.getMood())
                .fallback(moodDefIcon)
                .into(ForumMood);
        ForumTopic.setText(item.getTopic());
        ForumStarterUser.setText(item.getTopicStarter());
        ForumStartDate.setText(new SimpleDateFormat("dd/mm/yy").format(item.getTopicStartDate()));
        requestLayout();
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isBackgroundEnabled) {
            canvas.save();
            DashBackground.setBounds(0, 0, getWidth(), ITEM_HEIGHT);
            DashBackground.draw(canvas);
            canvas.restore();
        }


        DashForeground.draw(canvas);

        super.dispatchDraw(canvas);
    }

    public void enableBackground() {
        this.isBackgroundEnabled = true;
    }
}
