package jewpigeon.apps.newgrounds.Views.DashboardData.DashItems.DashDataViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import jewpigeon.apps.newgrounds.R;
import jewpigeon.apps.newgrounds.Views.DashboardData.DashItems.DashDataItems.ListItem;

import static jewpigeon.apps.newgrounds.Utils.DimensionTool.sp;

public class ListItemView extends View implements Target<Drawable> {

    private Drawable DashAudioIcon;
    private StaticLayout DashTitle;
    private StaticLayout DashAuthor;
    private ColorDrawable DashBackground;


    private final int TitleColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
    private final int AuthorColor = ContextCompat.getColor(getContext(), R.color.colorItemAuthorText);
    private final int DashBackgroundEnabledColor = ContextCompat.getColor(getContext(), R.color.colorFeaturedAudioItemBackground);

    private int ITEM_HEIGHT = (int) getResources().getDimension(R.dimen.dashboard_item_size_featured_small_list);
    private final int ICON_SIZE = ITEM_HEIGHT * 3 / 4;


    private static TextPaint DashTitlePainter;
    private static TextPaint DashAuthorPainter;
    private Drawable defIcon;

    private StateListDrawable DashForeground;

    private boolean isBackgroundEnabled = false;


    {
            defIcon = ContextCompat.getDrawable(getContext(), R.drawable.ng_icon_undefined_circle);
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


    public ListItemView(Context context) {
        super(context);
        establishState();
    }

    public ListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        establishState();
    }

    public ListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        establishState();
    }

    public ListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        establishState();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST), ITEM_HEIGHT);
    }

    public void setDashItem(ListItem item) {
        Glide.
                with(getContext())
                .load(item.getAudioIcon())
                .circleCrop()
                .placeholder(new ColorDrawable(Color.BLACK))
                .fallback(defIcon)
                .into(this);

        DashTitle = ListItemView.TextCache.INSTANCE().titleLayoutFor(item.getTitle());
        DashAuthor = ListItemView.TextCache.INSTANCE().authorLayoutFor(item.getAuthor());

        requestLayout();
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        DashForeground.setState(getDrawableState());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isBackgroundEnabled) {
            DashBackground.setBounds(0, 0, getWidth(), ITEM_HEIGHT);
            DashBackground.draw(canvas);
        }

        canvas.save();
        canvas.translate((ITEM_HEIGHT - ICON_SIZE)/2, (ITEM_HEIGHT - ICON_SIZE)/2);
        DashAudioIcon.draw(canvas);
        canvas.translate(ICON_SIZE*5/4, (ITEM_HEIGHT - ICON_SIZE)/2);
        DashTitle.draw(canvas);
        canvas.translate(DashTitle.getWidth() + ICON_SIZE*5/4, 0);
        DashAuthor.draw(canvas);
        canvas.translate(-((ITEM_HEIGHT - ICON_SIZE) + ICON_SIZE*5/2 + DashTitle.getWidth()),0);
        canvas.restore();

        DashForeground.draw(canvas);

    }

    private void establishState() {
        DashTitlePainter = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        DashTitlePainter.setColor(TitleColor);
        DashTitlePainter.setTextSize(sp(14));

        DashAuthorPainter = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        DashAuthorPainter.setColor(AuthorColor);
        DashAuthorPainter.setTextSize(sp(14));

        //this.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.grid_item_ripple));
        this.setClickable(true);


    }

    public void enableBackground() {
        isBackgroundEnabled = true;
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        try {
            DashAudioIcon = placeholder;

        }catch (NullPointerException e){
            DashAudioIcon = new ColorDrawable(Color.BLACK);
        }finally {
            DashAudioIcon.setBounds(0,0,ICON_SIZE,ICON_SIZE);
        }

        invalidate();

    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        try {
            DashAudioIcon = errorDrawable;

        }catch (NullPointerException e){
            DashAudioIcon = new ColorDrawable(Color.BLACK);
        }
        finally {
            DashAudioIcon.setBounds(0,0,ICON_SIZE,ICON_SIZE);
        }

        invalidate();
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        try {
            DashAudioIcon = resource;

        }catch (NullPointerException e){
            DashAudioIcon = new ColorDrawable(Color.BLACK);
        }
        finally {
            DashAudioIcon.setBounds(0,0, ICON_SIZE, ICON_SIZE);
        }
        invalidate();
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
    }
    @Override
    public void getSize(@NonNull SizeReadyCallback cb) {
        cb.onSizeReady(getWidth(), getHeight());
    }
    @Override
    public void removeCallback(@NonNull SizeReadyCallback cb) {

    }
    @Override
    public void setRequest(@Nullable Request request) {

    }
    @Nullable
    @Override
    public Request getRequest() {
        return null;
    }
    @Override
    public void onStart() {

    }
    @Override
    public void onStop() {

    }
    @Override
    public void onDestroy() {

    }

    private static class TextCache {
        public static TextCache instance;

        public static TextCache INSTANCE(){
            if(instance == null) instance = new TextCache();
            return instance;
        }

        private int MaxAuthorWidth = 400;
        private int MaxTitleWidth = 400;
        private final LruCache<CharSequence, StaticLayout> titleCache = new LruCache<CharSequence, StaticLayout>(100) {
            @Override
            protected StaticLayout create(CharSequence key) {
                int TEXT_WIDTH = (int) Math.min(MaxTitleWidth, DashTitlePainter.measureText(String.valueOf(key)));
                CharSequence titleEllipisized = TextUtils.ellipsize(key, DashTitlePainter, TEXT_WIDTH, TextUtils.TruncateAt.END);
                return StaticLayout.Builder.obtain(titleEllipisized, 0, titleEllipisized.length(), DashTitlePainter, TEXT_WIDTH)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .build();

            }

        };
        private final LruCache<CharSequence, StaticLayout> authorCache = new LruCache<CharSequence, StaticLayout>(100) {
            @Override
            protected StaticLayout create(CharSequence key) {
                int TEXT_WIDTH = (int) Math.min(MaxAuthorWidth, DashAuthorPainter.measureText(String.valueOf(key)));
                CharSequence authorEllipisized = TextUtils.ellipsize(key, DashAuthorPainter, TEXT_WIDTH, TextUtils.TruncateAt.END);
                return StaticLayout.Builder.obtain(authorEllipisized, 0, authorEllipisized.length(), DashAuthorPainter, TEXT_WIDTH)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .build();
            }
        };

        public void changeMaxTitleWidth(int newWidth) {
            if (MaxTitleWidth != newWidth) {
                MaxTitleWidth = newWidth;
                titleCache.evictAll();
            }
        }
        public void changeMaxAuthorWidth(int newWidth){
            if (MaxAuthorWidth != newWidth) {
                MaxAuthorWidth = newWidth;
                authorCache.evictAll();
            }
        }

        public StaticLayout titleLayoutFor(CharSequence text) {
            return titleCache.get(text);
        }

        public StaticLayout authorLayoutFor(CharSequence text) {
            return authorCache.get(text);
        }
    }
}
