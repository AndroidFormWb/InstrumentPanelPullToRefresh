package com.cc.instrumentpanel.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cc.instrumentpanel.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class NormalRotateLoadingLayout extends LoadingLayout {
    /**旋转动画的时间*/
    static final int ROTATION_ANIMATION_DURATION = 3600;
    /**动画插值*/
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    static final Interpolator ANIMATION_INTERPOLATOR_2 = new AccelerateInterpolator();
    static final Interpolator ANIMATION_INTERPOLATOR_3 = new DecelerateInterpolator();
    /**Header的容器*/
    FrameLayout mFrameLayout;
    FrameLayout mSuccessFrameLayout;
    private View mSuccessTopMask;
    private RelativeLayout mHeaderContainer;
   
    /**箭头图片*/
    private ImageView mArrowImageView;
    /**状态提示TextView*/
    private TextView mHintTextView;
    /**最后更新时间的TextView*/
    private TextView mHeaderTimeView;
    /**最后更新时间的标题*/
    private TextView mHeaderTimeViewTitle;
    /**旋转的动画*/
    private Animation mRotateAnimation;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public NormalRotateLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public NormalRotateLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context) {
    	mFrameLayout = (FrameLayout) findViewById(R.id.Frame_pull_refresh_2);
    	mSuccessFrameLayout = (FrameLayout) findViewById(R.id.success_frame);
    	mSuccessTopMask = mFrameLayout.findViewById(R.id.mask_top);
        mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
        mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
        mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
        mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);
        
        mArrowImageView.setScaleType(ScaleType.CENTER);
        
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = 2160.0f;    // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(toDegree, 0, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR_3);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }
    
    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header2, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        // 如果最后更新的时间的文本是空的话，隐藏前面的标题
        mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
        mHeaderTimeView.setText(label);
    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getHeight();
        }
        
        return (int) (getResources().getDisplayMetrics().density * 60);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
    	mSuccessFrameLayout.setVisibility(View.VISIBLE);
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        resetRotation();
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
        Log.e("onRefreshing", "正在加载");
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
    }
    
    @Override
    protected void onPullToRefresh() {
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }
    
    @Override
    protected void onRefreshing() {
        resetRotation();
        mSuccessFrameLayout.setVisibility(View.INVISIBLE);
        mFrameLayout.startAnimation(mRotateAnimation);
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
        
    }
    
    @SuppressLint("NewApi") @Override
    public void onPull(float scale) {
        float angle = scale * 170f; // SUPPRESS CHECKSTYLE
        if(angle > 90f) {
        	float angle3 = angle - 90f;
        
	        if(angle3 > 180f){
	        	float angle1 = angle3-180f;
	        	
        		mArrowImageView.setRotation(180f+angle1%35);//超过界限后，让它在35度不断重复摆动，造成加速失败效果
                	mSuccessTopMask.setPivotX(mSuccessTopMask.getMeasuredWidth()/2f);
                	mSuccessTopMask.setPivotY(mSuccessTopMask.getMeasuredHeight()); 
                	mSuccessTopMask.setRotation(180f);//白色挡超过180度后就不让它旋转
	        }else {
	        	mArrowImageView.setRotation(angle3);
	        	mSuccessTopMask.setPivotX(mSuccessTopMask.getMeasuredWidth()/2f);
	        	mSuccessTopMask.setPivotY(mSuccessTopMask.getMeasuredHeight()); 
	        	mSuccessTopMask.setRotation(angle3);
	        }
        }
        
    }
    
    /**
     * 重置动画
     */
    @SuppressLint("NewApi") private void resetRotation() {
        mFrameLayout.clearAnimation();
        mFrameLayout.setRotation(0);
        mArrowImageView.setRotation(0); 
    }
}
