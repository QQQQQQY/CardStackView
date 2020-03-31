package com.example.cardstackview;

import android.content.Context;
import android.content.res.Resources;
import android.database.Observable;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.List;

/**
 * @Author: qqyang
 * @Date: 2020/3/18
 * @Description:
 */
public class CardStackView extends FrameLayout {
    public static final String TAG = "CardStackView";
    private Context mContext;

    // card view 间距（单位：dp）
    private int cardStackViewSpacing = 8;
    // card 圆角 （单位：dp）
    private int cardStackViewRadius = 4;
    // card 背景颜色
    private @ColorInt
    int cardViewBackgroundColor = Color.parseColor("#FFFFFFFF");
    private Adapter adapter;
    private CollapsibleCardView.OnCollapsibleCardViewClickListener collapsibleCardViewClickListener =
            new CollapsibleCardView.OnCollapsibleCardViewClickListener() {
                @Override
                public void onCollapsibleCardViewClick(CollapsibleCardView collapsibleCardView) {
                    if (collapsibleCardView.isCollapse()) {
                        expandView(collapsibleCardView);
                    } else {
                        collapsibleCardView.setCollapse(true);
                    }
                    requestLayout();
                    if (null != onCardStackViewStateChangedListener) {
                        onCardStackViewStateChangedListener.onCardStackViewStateChanged(collapsibleCardView);
                    }
                }
            };

    public CardStackView(Context context) {
        super(context);
        initView(context);
    }

    public CardStackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CardStackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
    }

    private void addSubView() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View viewHeader = LayoutInflater.from(mContext).inflate(adapter.getItemLayoutIdHeader(), null);
            View viewContent = LayoutInflater.from(mContext).inflate(adapter.getItemLayoutIdContent(), null);
            CollapsibleCardView view = new CollapsibleCardView(mContext);
            view.setCardElevation(i + 4);
            view.setCardBackgroundColor(cardViewBackgroundColor);
            view.setRadius(dp2px(cardStackViewRadius));
            view.setViewHeader(viewHeader);
            view.setViewContent(viewContent);
            adapter.bindView(view, i, adapter.getData().get(i));
            if (i == adapter.getItemCount() - 1) {
                view.setClickable(false); // 设置最后一个不能点击。
            } else {
                view.setOnCollapsibleCardViewClickListener(collapsibleCardViewClickListener);
            }
            addView(view);
        }
    }

    private void expandView(CollapsibleCardView cardView) {
        // 最后一个始终展开，所以这里设置 getChildCount() - 1.
        for (int i = 0; i < getChildCount() - 1; i++) {
            CollapsibleCardView child = ((CollapsibleCardView) getChildAt(i));
            if (child.isExpand()) {
                child.setCollapse(true);
                if (null != onCardStackViewStateChangedListener) {
                    onCardStackViewStateChangedListener.onCardStackViewStateChanged(child);
                }
            }
        }
        cardView.setExpand(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() < 1) {
            return;
        }
        CollapsibleCardView collapsibleCardView = ((CollapsibleCardView) getChildAt(getChildCount() - 1));
        // compute height.
        final LayoutParams lp = (LayoutParams) collapsibleCardView.getLayoutParams();
        int marginTop = lp.topMargin;
        int marginBottom = lp.bottomMargin;
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int mHeaderViewHeight = collapsibleCardView.getViewHeader().getMeasuredHeight();
        int mContentViewHeight = collapsibleCardView.getViewContent().getMeasuredHeight();
        int totalHeight = paddingTop + marginTop
                + mHeaderViewHeight * (getChildCount() - 1)
                + collapsibleCardView.getMeasuredHeight()
                + paddingBottom + marginBottom;

        if (hasExpandView()) {
            totalHeight -= collapsibleCardView.getViewHeader().getMeasuredHeight();
            totalHeight += collapsibleCardView.getMeasuredHeight() + dp2px(cardStackViewSpacing);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight);
    }

    private boolean hasExpandView() {
        // 最后一个始终展开，所以这里设置 getChildCount() - 1.
        for (int i = 0; i < getChildCount() - 1; i++) {
            CollapsibleCardView child = (CollapsibleCardView) getChildAt(i);
            if (child.isExpand()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getChildCount() < 1) {
            return;
        }
        CollapsibleCardView collapsibleCardView = (CollapsibleCardView) getChildAt(0);
        final LayoutParams lp = (LayoutParams) collapsibleCardView.getLayoutParams();
        int marginStart = lp.getMarginStart();
        int marginTop = lp.topMargin;
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            CollapsibleCardView child = (CollapsibleCardView) getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            child.layout(childLeft + marginStart,
                    childTop + marginTop,
                    childLeft + marginStart + childWidth,
                    childTop + marginTop + childHeight);
            if (child.isExpand()) {
                childTop += childHeight + dp2px(cardStackViewSpacing);
            } else {
                childTop += child.getViewHeader().getMeasuredHeight();
            }
        }
    }

    private OnCardStackViewStateChangedListener onCardStackViewStateChangedListener;

    public interface OnCardStackViewStateChangedListener {
        void onCardStackViewStateChanged(CollapsibleCardView cardView);
    }

    public void setOnCardStackViewStateChangedListener(OnCardStackViewStateChangedListener onCardStackViewStateChangedListener) {
        this.onCardStackViewStateChangedListener = onCardStackViewStateChangedListener;
    }

    public int getCardStackViewSpacing() {
        return cardStackViewSpacing;
    }

    public void setCardStackViewSpacing(int cardStackViewSpacing) {
        this.cardStackViewSpacing = cardStackViewSpacing;
    }

    public int getCardStackViewRadius() {
        return cardStackViewRadius;
    }

    public void setCardStackViewRadius(int cardStackViewRadius) {
        this.cardStackViewRadius = cardStackViewRadius;
        for (int i = 0; i < getChildCount(); i++) {
            CollapsibleCardView child = (CollapsibleCardView) getChildAt(i);
            child.setRadius(dp2px(cardStackViewRadius));
        }
    }

    public int getCardViewBackgroundColor() {
        return cardViewBackgroundColor;
    }

    public void setCardViewBackgroundColor(int cardViewBackgroundColor) {
        this.cardViewBackgroundColor = cardViewBackgroundColor;
        for (int i = 0; i < getChildCount(); i++) {
            CollapsibleCardView child = (CollapsibleCardView) getChildAt(i);
            child.setCardBackgroundColor(cardViewBackgroundColor);
        }
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                removeAllViews();
                addSubView();
                requestLayout();
            }
        });
        addSubView();
        requestLayout();
    }

    private abstract static class AdapterDataObserver {
        public void onChanged() {
            // Do nothing
        }
    }

    private static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public abstract static class Adapter<T> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();
        private int layoutIdHeader, layoutIdContent;
        private List<T> list;

        public Adapter(@Nullable List<T> list, @LayoutRes int layoutIdHeader, @LayoutRes int layoutIdContent) {
            this.list = list;
            this.layoutIdHeader = layoutIdHeader;
            this.layoutIdContent = layoutIdContent;
        }

        private int getItemCount() {
            if (null == list) {
                return 0;
            }
            return list.size();
        }

        private @LayoutRes
        int getItemLayoutIdHeader() {
            return layoutIdHeader;
        }

        private @LayoutRes
        int getItemLayoutIdContent() {
            return layoutIdContent;
        }

        public List<T> getData() {
            return list;
        }

        public void setNewData(List<T> list) {
            this.list = list;
            mObservable.notifyChanged();
        }

        void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }


        public abstract void bindView(CollapsibleCardView v, int pos, T item);
    }

    public static class CollapsibleCardView extends CardView {
        private LinearLayout container;
        private Context mContext;

        private boolean isExpand = false;
        private boolean isCollapse = true;

        private View viewHeader, viewContent;

        private OnCollapsibleCardViewClickListener onCollapsibleCardViewClickListener;

        public CollapsibleCardView(Context context) {
            super(context);
            init(context);
        }

        public CollapsibleCardView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public CollapsibleCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            this.mContext = context;
            setUseCompatPadding(true);
            container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            addView(container);
        }

        public View getViewHeader() {
            return viewHeader;
        }

        public void setViewHeader(View viewHeader) {
            this.viewHeader = viewHeader;
            viewHeader.setOnClickListener(v -> {
                if (null != onCollapsibleCardViewClickListener) {
                    onCollapsibleCardViewClickListener.onCollapsibleCardViewClick(this);
                }
            });
            container.addView(viewHeader);
        }

        public View getViewContent() {
            return viewContent;
        }

        public void setViewContent(View viewContent) {
            this.viewContent = viewContent;
            viewContent.setOnClickListener(v -> {
                if (isExpand()) {
                    if (null != onCollapsibleCardViewClickListener) {
                        onCollapsibleCardViewClickListener.onCollapsibleCardViewClick(this);
                    }
                }
            });
            container.addView(viewContent);
        }

        public boolean isExpand() {
            return isExpand;
        }

        public void setExpand(boolean expand) {
            isExpand = expand;
            isCollapse = !expand;
        }

        public boolean isCollapse() {
            return isCollapse;
        }

        public void setCollapse(boolean collapse) {
            isCollapse = collapse;
            isExpand = !collapse;
        }

        public void setOnCollapsibleCardViewClickListener(OnCollapsibleCardViewClickListener onCollapsibleCardViewClickListener) {
            this.onCollapsibleCardViewClickListener = onCollapsibleCardViewClickListener;
        }

        public interface OnCollapsibleCardViewClickListener {
            void onCollapsibleCardViewClick(CollapsibleCardView collapsibleCardView);
        }
    }

    private static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
