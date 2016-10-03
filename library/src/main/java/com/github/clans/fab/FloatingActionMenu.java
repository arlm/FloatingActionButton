package com.github.clans.fab;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FloatingActionMenu extends ViewGroup {

    protected static final int EXTENDED_ACTION_MENU = -1;
    private static final int ANIMATION_DURATION = 300;
    private static final float CLOSED_PLUS_ROTATION = 0f;
    private static final float OPENED_PLUS_ROTATION_LEFT = -90f - 45f;
    private static final float OPENED_PLUS_ROTATION_RIGHT = 90f + 45f;

    private static final int OPEN_UP = 0;
    private static final int OPEN_DOWN = 1;

    private static final int LABELS_POSITION_LEFT = 0;
    private static final int LABELS_POSITION_RIGHT = 1;

    private AnimatorSet mOpenAnimatorSet = new AnimatorSet();
    private AnimatorSet mCloseAnimatorSet = new AnimatorSet();
    private AnimatorSet mIconToggleSet;
    private boolean mIsExtended = false;
    private String extendedButtonText = null;
    private int mExtendedButtonTextColor = 0;
    private int mExtendedButtonBackgroundColor;
    private int mExtendedButtonTextSize = 0;
    private int mButtonSpacing = Util.dpToPx(getContext(), 0f);
    private int mMaxButtonWidth;
    private int mLabelsMargin = Util.dpToPx(getContext(), 0f);
    private int mLabelsVerticalOffset = Util.dpToPx(getContext(), 0f);
    private int mButtonsCount;
    private boolean mMenuOpened;
    private boolean mIsMenuOpening;
    private Handler mUiHandler = new Handler();
    private int mLabelsShowAnimation;
    private int mLabelsHideAnimation;
    private int mLabelsPaddingTop = Util.dpToPx(getContext(), 4f);
    private int mLabelsPaddingRight = Util.dpToPx(getContext(), 0f);
    private int mLabelsPaddingBottom = Util.dpToPx(getContext(), 4f);
    private int mLabelsPaddingLeft = Util.dpToPx(getContext(), 8f);
    private ColorStateList mLabelsNormalTextColor;
    private ColorStateList mLabelsExtendedTextColor;
    private float mLabelsTextSize;
    private int mLabelsCornerRadius = Util.dpToPx(getContext(), 3f);
    private boolean mLabelsShowShadow;
    private boolean mMenuShowShadow;
    private int mMenuShadowColor;
    private float mMenuShadowRadius = 4f;
    private float mMenuShadowXOffset = 1f;
    private float mMenuShadowYOffset = 3f;
    private int mMenuColorNormal;
    private int mMenuColorPressed;
    private int mMenuColorRipple;
    private Drawable mIcon;
    private int mAnimationDelayPerItem;
    private Interpolator mOpenInterpolator;
    private Interpolator mCloseInterpolator;
    private boolean mIsAnimated = true;
    private boolean mLabelsSingleLine;
    private int mLabelsEllipsize;
    private int mLabelsMaxLines;
    private int mMenuFabSize;
    private int mLabelsStyle;
    private Typeface mCustomTypefaceFromFont;
    private boolean mIconAnimated = true;
    private ImageView mImageToggle;
    private TextView mMenuText;
    private Animation mMenuButtonShowAnimation;
    private Animation mMenuButtonHideAnimation;
    private Animation mImageToggleShowAnimation;
    private Animation mImageToggleHideAnimation;
    private boolean mIsMenuButtonAnimationRunning;
    private boolean mIsSetClosedOnTouchOutside;
    private int mOpenDirection;
    private OnMenuToggleListener mToggleListener;
    private FloatingActionButton mMenuButton;
    private ValueAnimator mShowBackgroundAnimator;
    private ValueAnimator mHideBackgroundAnimator;
    private int mBackgroundColor;
    private int mLabelsPosition;
    private Context mLabelsContext;
    private String mMenuLabelText;
    private boolean mUsingMenuLabel;

    public FloatingActionMenu(Context context) {
        this(context, null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionMenu, 0, 0);
        mIsExtended = attr.getBoolean(R.styleable.FloatingActionMenu_menu_isExtended, false);
        extendedButtonText = attr.getString(R.styleable.FloatingActionMenu_menu_extendedButtonText);
        mExtendedButtonTextColor = attr.getInt(R.styleable.FloatingActionMenu_menu_extendedButtonTextColor, Color.WHITE);
        mExtendedButtonBackgroundColor = attr.getInt(R.styleable.FloatingActionMenu_menu_extendedButtonTextColor, Color.WHITE);
        mExtendedButtonTextSize = attr.getInt(R.styleable.FloatingActionMenu_menu_extendedButtonTextSize, 16);
        mButtonSpacing = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_buttonSpacing, mButtonSpacing);
        mLabelsMargin = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_margin, mLabelsMargin);
        mLabelsPosition = attr.getInt(R.styleable.FloatingActionMenu_menu_labels_position, LABELS_POSITION_LEFT);
        mLabelsShowAnimation = attr.getResourceId(R.styleable.FloatingActionMenu_menu_labels_showAnimation,
                mLabelsPosition == LABELS_POSITION_LEFT ? R.anim.fab_slide_in_from_right : R.anim.fab_slide_in_from_left);
        mLabelsHideAnimation = attr.getResourceId(R.styleable.FloatingActionMenu_menu_labels_hideAnimation,
                mLabelsPosition == LABELS_POSITION_LEFT ? R.anim.fab_slide_out_to_right : R.anim.fab_slide_out_to_left);
        mLabelsPaddingTop = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingTop, mLabelsPaddingTop);
        mLabelsPaddingRight = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingRight, mLabelsPaddingRight);
        mLabelsPaddingBottom = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingBottom, mLabelsPaddingBottom);
        mLabelsPaddingLeft = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingLeft, mLabelsPaddingLeft);
        mLabelsNormalTextColor = attr.getColorStateList(R.styleable.FloatingActionMenu_menu_labels_textNormalColorList);
        if (mLabelsNormalTextColor == null) {
            mLabelsNormalTextColor = ColorStateList.valueOf(Color.WHITE);
        }
        mLabelsExtendedTextColor = attr.getColorStateList(R.styleable.FloatingActionMenu_menu_labels_textExtendedColorList);
        if (mLabelsExtendedTextColor == null) {
            mLabelsExtendedTextColor = ColorStateList.valueOf(Color.WHITE);
        }
        mLabelsTextSize = attr.getDimension(R.styleable.FloatingActionMenu_menu_labels_textSize, getResources().getDimension(R.dimen.labels_text_size));
        mLabelsCornerRadius = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_cornerRadius, mLabelsCornerRadius);
        mLabelsShowShadow = attr.getBoolean(R.styleable.FloatingActionMenu_menu_labels_showShadow, true);
        mMenuShowShadow = attr.getBoolean(R.styleable.FloatingActionMenu_menu_showShadow, true);
        mMenuShadowColor = attr.getColor(R.styleable.FloatingActionMenu_menu_shadowColor, 0x66000000);
        mMenuShadowRadius = attr.getDimension(R.styleable.FloatingActionMenu_menu_shadowRadius, mMenuShadowRadius);
        mMenuShadowXOffset = attr.getDimension(R.styleable.FloatingActionMenu_menu_shadowXOffset, mMenuShadowXOffset);
        mMenuShadowYOffset = attr.getDimension(R.styleable.FloatingActionMenu_menu_shadowYOffset, mMenuShadowYOffset);
        mMenuColorNormal = attr.getColor(R.styleable.FloatingActionMenu_menu_colorNormal, 0xFFDA4336);
        mMenuColorPressed = attr.getColor(R.styleable.FloatingActionMenu_menu_colorPressed, 0xFFE75043);
        mMenuColorRipple = attr.getColor(R.styleable.FloatingActionMenu_menu_colorRipple, 0x99FFFFFF);
        mAnimationDelayPerItem = attr.getInt(R.styleable.FloatingActionMenu_menu_animationDelayPerItem, 50);
        mIcon = attr.getDrawable(R.styleable.FloatingActionMenu_menu_icon);
        if (mIcon == null) {
            mIcon = getResources().getDrawable(R.drawable.fab_add);
        }
        mLabelsSingleLine = attr.getBoolean(R.styleable.FloatingActionMenu_menu_labels_singleLine, false);
        mLabelsEllipsize = attr.getInt(R.styleable.FloatingActionMenu_menu_labels_ellipsize, 0);
        mLabelsMaxLines = attr.getInt(R.styleable.FloatingActionMenu_menu_labels_maxLines, -1);
        mMenuFabSize = attr.getInt(R.styleable.FloatingActionMenu_menu_fab_size, FloatingActionButton.SIZE_NORMAL);
        mLabelsStyle = attr.getResourceId(R.styleable.FloatingActionMenu_menu_labels_style, 0);
        String customFont = attr.getString(R.styleable.FloatingActionMenu_menu_labels_customFont);
        try {
            if (!TextUtils.isEmpty(customFont)) {
                mCustomTypefaceFromFont = Typeface.createFromAsset(getContext().getAssets(), customFont);
            }
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Unable to load specified custom font: " + customFont, ex);
        }
        mOpenDirection = attr.getInt(R.styleable.FloatingActionMenu_menu_openDirection, OPEN_UP);
        mBackgroundColor = attr.getColor(R.styleable.FloatingActionMenu_menu_backgroundColor, Color.TRANSPARENT);

        if (attr.hasValue(R.styleable.FloatingActionMenu_menu_fab_label)) {
            mUsingMenuLabel = true;
            mMenuLabelText = attr.getString(R.styleable.FloatingActionMenu_menu_fab_label);
        }

        if (attr.hasValue(R.styleable.FloatingActionMenu_menu_labels_padding)) {
            int padding = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_padding, 0);
            initPadding(padding);
        }

        mOpenInterpolator = new OvershootInterpolator();
        mCloseInterpolator = new AnticipateInterpolator();
        mLabelsContext = new ContextThemeWrapper(getContext(), mLabelsStyle);

        initBackgroundDimAnimation();
        if (mIsExtended) {
            createExtendedMenuButton();
        } else {
            createMenuButton();
        }
        initMenuButtonAnimations(attr);
        attr.recycle();
    }

    private void initMenuButtonAnimations(TypedArray attr) {
        int showResId = attr.getResourceId(R.styleable.FloatingActionMenu_menu_fab_show_animation, R.anim.fab_scale_up);
        setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getContext(), showResId));
        mImageToggleShowAnimation = AnimationUtils.loadAnimation(getContext(), showResId);
        int hideResId = attr.getResourceId(R.styleable.FloatingActionMenu_menu_fab_hide_animation, R.anim.fab_scale_down);
        setMenuButtonHideAnimation(AnimationUtils.loadAnimation(getContext(), hideResId));
        mImageToggleHideAnimation = AnimationUtils.loadAnimation(getContext(), hideResId);
    }

    private void initBackgroundDimAnimation() {
        final int maxAlpha = Color.alpha(mBackgroundColor);
        final int red = Color.red(mBackgroundColor);
        final int green = Color.green(mBackgroundColor);
        final int blue = Color.blue(mBackgroundColor);

        mShowBackgroundAnimator = ValueAnimator.ofInt(0, maxAlpha);
        mShowBackgroundAnimator.setDuration(ANIMATION_DURATION);
        mShowBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer) animation.getAnimatedValue();
                setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        });

        mHideBackgroundAnimator = ValueAnimator.ofInt(maxAlpha, 0);
        mHideBackgroundAnimator.setDuration(ANIMATION_DURATION);
        mHideBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer) animation.getAnimatedValue();
                setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        });
    }

    public void onMenuSizeChange() {
        close(false);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(params);
        if (mMenuText != null) {
            mMenuText.setVisibility(View.VISIBLE);
        }
    }

    public void setIconPosition(float yPos, float xPos) {
        mImageToggle.setX(xPos - mImageToggle.getWidth() / 2);
        mImageToggle.setY(yPos - mImageToggle.getHeight() / 2);
        toggle(true); // Open menu automatically when collapsed
    }

    private boolean isBackgroundEnabled() {
        return mBackgroundColor != Color.TRANSPARENT;
    }

    private void initPadding(int padding) {
        mLabelsPaddingTop = padding;
        mLabelsPaddingRight = padding;
        mLabelsPaddingBottom = padding;
        mLabelsPaddingLeft = padding;
    }

    private void createMenuButton() { // Create circular menu
        mMenuButton = new FloatingActionButton(getContext());

        mMenuButton.mShowShadow = mMenuShowShadow;
        if (mMenuShowShadow) {
            mMenuButton.mShadowRadius = Util.dpToPx(getContext(), mMenuShadowRadius);
            mMenuButton.mShadowXOffset = Util.dpToPx(getContext(), mMenuShadowXOffset);
            mMenuButton.mShadowYOffset = Util.dpToPx(getContext(), mMenuShadowYOffset);
        }
        mMenuButton.setColors(mMenuColorNormal, mMenuColorPressed, mMenuColorRipple);
        mMenuButton.mShadowColor = mMenuShadowColor;
        mMenuButton.mFabSize = mMenuFabSize;
        mMenuButton.updateBackground();
        mMenuButton.setLabelText(mMenuLabelText);
        mImageToggle = new ImageView(getContext());
        mImageToggle.setImageDrawable(mIcon);
        addView(mMenuButton, super.generateDefaultLayoutParams());
        addView(mImageToggle);
        invalidate();
        createDefaultIconAnimation();
    }

    private void createExtendedMenuButton() { // Create wide (extended) menu button
        mMenuButton = new FloatingActionButton(getContext(), null, EXTENDED_ACTION_MENU);
        mMenuButton.mShowShadow = mMenuShowShadow;
        if (mMenuShowShadow) {
            mMenuButton.mShadowRadius = Util.dpToPx(getContext(), mMenuShadowRadius);
            mMenuButton.mShadowXOffset = Util.dpToPx(getContext(), mMenuShadowXOffset);
            mMenuButton.mShadowYOffset = Util.dpToPx(getContext(), mMenuShadowYOffset);
        }
        mMenuButton.setColors(mMenuColorNormal, mMenuColorPressed, mMenuColorRipple);
        mMenuButton.mShadowColor = mMenuShadowColor;
        mMenuButton.updateBackground();
        mMenuButton.setLabelText(mMenuLabelText);

        mMenuText = new TextView(getContext());
        mMenuText.setText(extendedButtonText);
        mMenuText.setTextSize(mExtendedButtonTextSize);
        mMenuText.setTextColor(mExtendedButtonTextColor);

        mImageToggle = new ImageView(getContext());
        mImageToggle.setImageDrawable(mIcon);

        addView(mMenuText);
        addView(mMenuButton, super.generateDefaultLayoutParams());
        addView(mImageToggle);

        createDefaultIconAnimation();
    }

    private void addExtendedActionMenuText() {
        mMenuText = new TextView(getContext());
        mMenuText.setText(extendedButtonText);
        mMenuText.setTextSize(mExtendedButtonTextSize);
        mMenuText.setTextColor(mExtendedButtonTextColor);
        mMenuText.setVisibility(View.GONE);
        addView(mMenuText);
    }

    private void createDefaultIconAnimation() {
        float collapseAngle;
        float expandAngle;
        if (mOpenDirection == OPEN_UP) {
            collapseAngle = mLabelsPosition == LABELS_POSITION_LEFT ? OPENED_PLUS_ROTATION_LEFT : OPENED_PLUS_ROTATION_RIGHT;
            expandAngle = mLabelsPosition == LABELS_POSITION_LEFT ? OPENED_PLUS_ROTATION_LEFT : OPENED_PLUS_ROTATION_RIGHT;
        } else {
            collapseAngle = mLabelsPosition == LABELS_POSITION_LEFT ? OPENED_PLUS_ROTATION_RIGHT : OPENED_PLUS_ROTATION_LEFT;
            expandAngle = mLabelsPosition == LABELS_POSITION_LEFT ? OPENED_PLUS_ROTATION_RIGHT : OPENED_PLUS_ROTATION_LEFT;
        }

        ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(
                mImageToggle,
                "rotation",
                collapseAngle,
                CLOSED_PLUS_ROTATION
        );

        ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(
                mImageToggle,
                "rotation",
                CLOSED_PLUS_ROTATION,
                expandAngle
        );

        mOpenAnimatorSet.play(expandAnimator);
        mCloseAnimatorSet.play(collapseAnimator);

        mOpenAnimatorSet.setInterpolator(mOpenInterpolator);
        mCloseAnimatorSet.setInterpolator(mCloseInterpolator);

        mOpenAnimatorSet.setDuration(ANIMATION_DURATION);
        mCloseAnimatorSet.setDuration(ANIMATION_DURATION);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        mMaxButtonWidth = 0;
        int maxLabelWidth = 0;

        measureChildWithMargins(mImageToggle, widthMeasureSpec, 0, heightMeasureSpec, 0);

        if (mIsExtended) {
            measureChildWithMargins(mMenuText, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        for (int i = 0; i < mButtonsCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == mImageToggle || child == mMenuText)
                continue;

            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            mMaxButtonWidth = Math.max(mMaxButtonWidth, child.getMeasuredWidth());
        }

        for (int i = 0; i < mButtonsCount; i++) {
            int usedWidth = 0;
            View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == mImageToggle || child == mMenuText)
                continue;

            usedWidth += child.getMeasuredWidth();
            height += child.getMeasuredHeight();

            Label label = (Label) child.getTag(R.id.fab_label);
            if (label != null) {
                int labelOffset = (mMaxButtonWidth - child.getMeasuredWidth()) / (mUsingMenuLabel ? 1 : 2);
                int labelUsedWidth = child.getMeasuredWidth() + label.calculateShadowWidth() + mLabelsMargin + labelOffset;
                measureChildWithMargins(label, widthMeasureSpec, mIsExtended ? 0 : labelUsedWidth, heightMeasureSpec, 0); // I allow label to use just the space which is not already used by its FAB - we dont need it with extended FAB it would cause visibility problems
                usedWidth += label.getMeasuredWidth();
                maxLabelWidth = Math.max(maxLabelWidth, usedWidth + labelOffset);
            }
        }

        width = Math.max(mMaxButtonWidth, maxLabelWidth + mLabelsMargin) + getPaddingLeft() + getPaddingRight();
        height += mButtonSpacing * (mButtonsCount - 1) + getPaddingTop() + getPaddingBottom();
        height = adjustForOvershoot(height);
        if (getLayoutParams().width == LayoutParams.MATCH_PARENT) {
            width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        }

        if (getLayoutParams().height == LayoutParams.MATCH_PARENT) {
            height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int buttonsHorizontalCenter = mLabelsPosition == LABELS_POSITION_LEFT
                ? r - l - mMaxButtonWidth / 2 - getPaddingRight()
                : mMaxButtonWidth / 2 + getPaddingLeft();
        boolean openUp = mOpenDirection == OPEN_UP;

        int leftTranslation = 0;
        int menuButtonTop = openUp
                ? b - t - mMenuButton.getMeasuredHeight() - getPaddingBottom()
                : getPaddingTop();
        int menuButtonLeft = buttonsHorizontalCenter - mMenuButton.getMeasuredWidth() / 2;
        if (mIsExtended) {
            leftTranslation = Util.dpToPx(getContext(), 8);
        }
        mMenuButton.layout(menuButtonLeft + leftTranslation, menuButtonTop, menuButtonLeft + leftTranslation + mMenuButton.getMeasuredWidth(),
                menuButtonTop + mMenuButton.getMeasuredHeight());
        int imageLeft = buttonsHorizontalCenter - mImageToggle.getMeasuredWidth() / 2;
        int imageTop = menuButtonTop + mMenuButton.getMeasuredHeight() / 2 - mImageToggle.getMeasuredHeight() / 2;
        if (mIsExtended) { // Sets position for text ("add new expense") and icon - in this case icon must be more left
            alignMenuText();
            alignMenuIcon();
        } else {
            mImageToggle.layout(imageLeft, imageTop, imageLeft + mImageToggle.getMeasuredWidth(),
                    imageTop + mImageToggle.getMeasuredHeight());
        }
        int nextY = openUp
                ? menuButtonTop + mMenuButton.getMeasuredHeight() + mButtonSpacing
                : menuButtonTop;

        for (int i = mButtonsCount - 1; i >= 0; i--) {
            View child = getChildAt(i);

            if (child == mMenuText || child == mImageToggle || child instanceof TextView) continue;

            FloatingActionButton fab = (FloatingActionButton) child;

            if (fab.getVisibility() == GONE) continue;

            int childExtendedExtraX = getResources().getDimensionPixelSize(R.dimen.extended_button_child_left_offset);
            int childX = buttonsHorizontalCenter - fab.getMeasuredWidth() / 2 + (mIsExtended ? childExtendedExtraX : 0);
            int childY = openUp ? nextY - fab.getMeasuredHeight() - mButtonSpacing : nextY;

            if (fab == mMenuButton && !mIsExtended) {
                fab.setNormalMenuLabelColors();
            }
            if (fab != mMenuButton) {
                if (mIsExtended) {
                    fab.setExtended(true);
                    fab.setLabelTextColor(getMenuButtonColorNormal());
                    fab.measure(fab.calculateMeasuredWidth(), fab.calculateMeasuredHeight());
                } else {
                    fab.setExtended(false);
                    fab.setLabelTextColor(mLabelsNormalTextColor);
                    fab.measure(fab.calculateMeasuredWidth(), fab.calculateMeasuredHeight());
                }
                fab.layout(childX, childY, childX + fab.getMeasuredWidth(),
                        childY + fab.getMeasuredHeight());

                if (!mIsMenuOpening) {
                    fab.hide(false);
                }
            }

            View label = (View) fab.getTag(R.id.fab_label);
            if (label != null) {
                int labelsOffset = (mUsingMenuLabel ? mMaxButtonWidth / 2 : fab.getMeasuredWidth() / 2) + mLabelsMargin;
                int labelXNearButton = mLabelsPosition == LABELS_POSITION_LEFT
                        ? buttonsHorizontalCenter - labelsOffset
                        : buttonsHorizontalCenter + labelsOffset;

                int labelXAwayFromButton = mLabelsPosition == LABELS_POSITION_LEFT
                        ? labelXNearButton - label.getMeasuredWidth()
                        : labelXNearButton + label.getMeasuredWidth();

                int labelLeft = mLabelsPosition == LABELS_POSITION_LEFT
                        ? labelXAwayFromButton
                        : labelXNearButton;

                int labelRight = mLabelsPosition == LABELS_POSITION_LEFT
                        ? labelXNearButton
                        : labelXAwayFromButton;

                int labelTop = childY - mLabelsVerticalOffset + (fab.getMeasuredHeight()
                        - label.getMeasuredHeight()) / 2;

                int labelBottom = childY - mLabelsVerticalOffset + (fab.getMeasuredHeight()
                        + label.getMeasuredHeight()) / 2;

                int relatedButtonWidth = fab.calculateMeasuredWidth() / 2;
                if (mIsExtended) {
                    int extendedLabelLeft = (labelLeft + relatedButtonWidth + label.getMeasuredWidth() / 2) + mImageToggle.getWidth() / 2;
                    int extendedLabelRight = (labelRight + relatedButtonWidth + label.getMeasuredWidth() / 2) + mImageToggle.getWidth() / 2;
                    label.layout(extendedLabelLeft, labelTop, extendedLabelRight, labelBottom);
                } else {
                    label.layout(labelLeft, labelTop, labelRight, labelTop + label.getMeasuredHeight()); // Here we set label position
                }
                if (!mIsMenuOpening) {
                    label.setVisibility(INVISIBLE);
                }
            }
            int extendedButtonSpacingExtra = getResources().getDimensionPixelSize(R.dimen.extended_button_spacing_extra);
            int buttonSpacing = mButtonSpacing + (mIsExtended ? extendedButtonSpacingExtra : 0);
            fab.updateBackground();
            nextY = openUp
                    ? childY - buttonSpacing
                    : childY + child.getMeasuredHeight() + buttonSpacing;
        }
    }

    private int adjustForOvershoot(int dimension) {
        return (int) (dimension * 0.03 + dimension);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        bringChildToFront(mMenuButton);
        bringChildToFront(mImageToggle);
        bringChildToFront(mMenuText);
        mButtonsCount = getChildCount();
        createLabels();
    }

    private void createLabels() {
        for (int i = 0; i < mButtonsCount; i++) {

            if (getChildAt(i) instanceof FloatingActionButton) {
                final FloatingActionButton fab = (FloatingActionButton) getChildAt(i);
                if (fab.getTag(R.id.fab_label) != null) continue;
                if (mIsExtended && fab != mMenuButton) {
                    fab.setExtended(true);
                    fab.setBackgroundColor(mExtendedButtonBackgroundColor);
                }
                addLabel(fab);
                if (fab == mMenuButton) {
                    mMenuButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggle(mIsAnimated);
                        }
                    });
                }
            }
        }
    }

    private void alignMenuText() {
        int textIconGap = Math.round(getResources().getDimension(R.dimen.extended_button_gap_between_icon_text));
        int textLeft = (Math.round(mMenuButton.getX()) + mMenuButton.getMeasuredWidth() / 2 - mMenuText.getMeasuredWidth() / 2) + mImageToggle.getWidth() / 3;
        int textTop = Math.round(mMenuButton.getY()) + mMenuButton.getMeasuredHeight() / 2 - mMenuText.getMeasuredHeight() / 2;
        mMenuText.layout(textLeft, textTop, textLeft + mMenuText.getMeasuredWidth(),
                textTop + mMenuText.getMeasuredHeight());
    }

    private void alignMenuIcon() {

        int iconLeft = Math.round(mMenuText.getX()) - (mImageToggle.getMeasuredWidth() / 2 + getResources().getDimensionPixelOffset(R.dimen.extended_button_gap_between_icon_text));
        int iconTop = Math.round(mMenuButton.getY()) + mMenuButton.getMeasuredHeight() / 2 - mImageToggle.getMeasuredHeight() / 2;
        mImageToggle.layout(iconLeft, iconTop, iconLeft + mImageToggle.getMeasuredWidth(),
                iconTop + mImageToggle.getMeasuredHeight());
    }

    private void addLabel(FloatingActionButton fab) {
        String text = fab.getLabelText();

        if (TextUtils.isEmpty(text)) return;

        final Label label = new Label(mLabelsContext);
        label.setClickable(true);
        label.setFab(fab);
        label.setShowAnimation(AnimationUtils.loadAnimation(getContext(), mLabelsShowAnimation));
        label.setHideAnimation(AnimationUtils.loadAnimation(getContext(), mLabelsHideAnimation));
        if (mLabelsStyle > 0) {
            label.setTextAppearance(getContext(), mLabelsStyle);
            label.setShowShadow(false);
            label.setUsingStyle(true);
        } else {
            label.setShowShadow(mLabelsShowShadow);
            label.setCornerRadius(mLabelsCornerRadius);
            if (mLabelsEllipsize > 0) {
                setLabelEllipsize(label);
            }
            label.setMaxLines(mLabelsMaxLines);
            label.updateBackground();
            label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelsTextSize);
            label.setTextColor(mLabelsNormalTextColor);

            int left = mLabelsPaddingLeft;
            int top = mLabelsPaddingTop;
            if (mLabelsShowShadow) {
                left += fab.getShadowRadius() + Math.abs(fab.getShadowXOffset());
                top += fab.getShadowRadius() + Math.abs(fab.getShadowYOffset());
            }

            label.setPadding(
                    left,
                    top,
                    mLabelsPaddingLeft,
                    mLabelsPaddingTop
            );

            if (mLabelsMaxLines < 0 || mLabelsSingleLine) {
                label.setSingleLine(mLabelsSingleLine);
            }
        }

        if (mCustomTypefaceFromFont != null) {
            label.setTypeface(mCustomTypefaceFromFont);
        }
        label.setText(text);
        fab.updateBackground();
        label.setOnClickListener(fab.getOnClickListener());
        addView(label);
        fab.setTag(R.id.fab_label, label);
    }

    private void setLabelEllipsize(Label label) {
        switch (mLabelsEllipsize) {
            case 1:
                label.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case 2:
                label.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
            case 3:
                label.setEllipsize(TextUtils.TruncateAt.END);
                break;
            case 4:
                label.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                break;
        }
    }

    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT,
                MarginLayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    private void hideMenuButtonWithImage(boolean animate) {
        if (!isMenuButtonHidden()) {
            mMenuButton.hide(animate);
            if (animate) {
                mImageToggle.startAnimation(mImageToggleHideAnimation);
            }
            mIsMenuButtonAnimationRunning = false;
        }
    }

    private void showMenuButtonWithImage(boolean animate) {
        if (isMenuButtonHidden()) {
            mMenuButton.show(animate);
            if (animate) {
                mImageToggle.startAnimation(mImageToggleShowAnimation);
            }
            mImageToggle.setVisibility(VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsSetClosedOnTouchOutside) {
            boolean handled = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handled = isOpened();
                    break;
                case MotionEvent.ACTION_UP:
                    close(mIsAnimated);
                    handled = true;
            }
            return handled;
        }

        return super.onTouchEvent(event);
    }

    public boolean isOpened() {
        return mMenuOpened;
    }

    /* ===== API methods ===== */

    public void toggle(boolean animate) {
        if (isOpened()) {
            close(animate);
        } else {
            open(animate);
        }
    }

    public void open(final boolean animate) {
        if (!isOpened()) {
            if (isBackgroundEnabled()) {
                mShowBackgroundAnimator.start();
            }

            if (mIconAnimated) {
                if (mIconToggleSet != null) {
                    mIconToggleSet.start();
                } else {
                    mCloseAnimatorSet.cancel();
                    mOpenAnimatorSet.start();
                }
            }

            int delay = 0;
            int counter = 0;
            mIsMenuOpening = true;
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                if (child instanceof FloatingActionButton && child.getVisibility() != GONE) {
                    counter++;

                    final FloatingActionButton fab = (FloatingActionButton) child;
                    mUiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isOpened()) return;

                            if (fab != mMenuButton) {
                                fab.show(animate);
                            }

                            Label label = (Label) fab.getTag(R.id.fab_label);
                            if (label != null && label.isHandleVisibilityChanges()) {
                                label.show(!mIsExtended && animate);
                            }
                        }
                    }, delay);
                    delay += mAnimationDelayPerItem;
                }
            }

            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuOpened = true;

                    if (mToggleListener != null) {
                        mToggleListener.onMenuToggle(true);
                    }
                }
            }, ++counter * mAnimationDelayPerItem);
        }
    }

    public void close(final boolean animate) {
        if (isOpened()) {
            if (isBackgroundEnabled()) {
                mHideBackgroundAnimator.start();
            }

            if (mIconAnimated) {
                if (mIconToggleSet != null) {
                    mIconToggleSet.start();
                } else {
                    mCloseAnimatorSet.start();
                    mOpenAnimatorSet.cancel();
                }
            }

            int delay = 0;
            int counter = 0;
            mIsMenuOpening = false;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof FloatingActionButton && child.getVisibility() != GONE) {
                    counter++;
                    final FloatingActionButton fab = (FloatingActionButton) child;
                    mUiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isOpened()) return;
                            if (fab != mMenuButton) {
                                fab.hide(animate);
                            }
                            Label label = (Label) fab.getTag(R.id.fab_label);
                            if (label != null && label.isHandleVisibilityChanges()) {
                                label.hide(!mIsExtended && animate);
                            }
                        }
                    }, delay);
                    delay += mAnimationDelayPerItem;
                }
            }

            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuOpened = false;

                    if (mToggleListener != null) {
                        mToggleListener.onMenuToggle(false);
                    }
                }
            }, ++counter * mAnimationDelayPerItem);
        }
    }

    /**
     * Sets the {@link android.view.animation.Interpolator} for <b>FloatingActionButton's</b> icon animation.
     *
     * @param interpolator the Interpolator to be used in animation
     */
    public void setIconAnimationInterpolator(Interpolator interpolator) {
        mOpenAnimatorSet.setInterpolator(interpolator);
        mCloseAnimatorSet.setInterpolator(interpolator);
    }

    public void setIconAnimationOpenInterpolator(Interpolator openInterpolator) {
        mOpenAnimatorSet.setInterpolator(openInterpolator);
    }

    public void setIconAnimationCloseInterpolator(Interpolator closeInterpolator) {
        mCloseAnimatorSet.setInterpolator(closeInterpolator);
    }

    public boolean isAnimated() {
        return mIsAnimated;
    }

    /**
     * Sets whether open and close actions should be animated
     *
     * @param animated if <b>false</b> - menu items will appear/disappear instantly without any animation
     */
    public void setAnimated(boolean animated) {
        mIsAnimated = animated;
        mOpenAnimatorSet.setDuration(animated ? ANIMATION_DURATION : 0);
        mCloseAnimatorSet.setDuration(animated ? ANIMATION_DURATION : 0);
    }

    public Boolean isExtended() {
        return mIsExtended;
    }

    public int getAnimationDelayPerItem() {
        return mAnimationDelayPerItem;
    }

    public void setAnimationDelayPerItem(int animationDelayPerItem) {
        mAnimationDelayPerItem = animationDelayPerItem;
    }

    public void setOnMenuToggleListener(OnMenuToggleListener listener) {
        mToggleListener = listener;
    }

    public boolean isIconAnimated() {
        return mIconAnimated;
    }

    public void setIconAnimated(boolean animated) {
        mIconAnimated = animated;
    }

    public ImageView getMenuIconView() {
        return mImageToggle;
    }

    public AnimatorSet getIconToggleAnimatorSet() {
        return mIconToggleSet;
    }

    public void setIconToggleAnimatorSet(AnimatorSet toggleAnimatorSet) {
        mIconToggleSet = toggleAnimatorSet;
    }

    public void setMenuButtonShowAnimation(Animation showAnimation) {
        mMenuButtonShowAnimation = showAnimation;
        mMenuButton.setShowAnimation(showAnimation);
    }

    public void setMenuButtonHideAnimation(Animation hideAnimation) {
        mMenuButtonHideAnimation = hideAnimation;
        mMenuButton.setHideAnimation(hideAnimation);
    }

    public boolean isMenuHidden() {
        return getVisibility() == INVISIBLE;
    }

    public boolean isMenuButtonHidden() {
        return mMenuButton.isHidden();
    }

    /**
     * Makes the whole {@link #FloatingActionMenu} to appear and sets its visibility to {@link #VISIBLE}
     *
     * @param animate if true - plays "show animation"
     */
    public void showMenu(boolean animate) {
        if (isMenuHidden()) {
            if (animate) {
                startAnimation(mMenuButtonShowAnimation);
            }
            setVisibility(VISIBLE);
        }
    }

    /**
     * Makes the {@link #FloatingActionMenu} to disappear and sets its visibility to {@link #INVISIBLE}
     *
     * @param animate if true - plays "hide animation"
     */
    public void hideMenu(final boolean animate) {
        if (!isMenuHidden() && !mIsMenuButtonAnimationRunning) {
            mIsMenuButtonAnimationRunning = true;
            if (isOpened()) {
                close(animate);
                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (animate) {
                            startAnimation(mMenuButtonHideAnimation);
                        }
                        setVisibility(INVISIBLE);
                        mIsMenuButtonAnimationRunning = false;
                    }
                }, mAnimationDelayPerItem * mButtonsCount);
            } else {
                if (animate) {
                    startAnimation(mMenuButtonHideAnimation);
                }
                setVisibility(INVISIBLE);
                mIsMenuButtonAnimationRunning = false;
            }
        }
    }

    public void toggleMenu(boolean animate) {
        if (isMenuHidden()) {
            showMenu(animate);
        } else {
            hideMenu(animate);
        }
    }

    /**
     * Makes the {@link FloatingActionButton} to appear inside the {@link #FloatingActionMenu} and
     * sets its visibility to {@link #VISIBLE}
     *
     * @param animate if true - plays "show animation"
     */
    public void showMenuButton(boolean animate) {
        if (isMenuButtonHidden()) {
            showMenuButtonWithImage(animate);
        }
    }

    /**
     * Makes the {@link FloatingActionButton} to disappear inside the {@link #FloatingActionMenu} and
     * sets its visibility to {@link #INVISIBLE}
     *
     * @param animate if true - plays "hide animation"
     */
    public void hideMenuButton(final boolean animate) {
        if (!isMenuButtonHidden() && !mIsMenuButtonAnimationRunning) {
            mIsMenuButtonAnimationRunning = true;
            if (isOpened()) {
                close(animate);
                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideMenuButtonWithImage(animate);
                    }
                }, mAnimationDelayPerItem * mButtonsCount);
            } else {
                hideMenuButtonWithImage(animate);
            }
        }
    }

    public void toggleMenuButton(boolean animate) {
        if (isMenuButtonHidden()) {
            showMenuButton(animate);
        } else {
            hideMenuButton(animate);
        }
    }

    public void setClosedOnTouchOutside(boolean close) {
        mIsSetClosedOnTouchOutside = close;
    }

    public void setMenuButtonColorNormalResId(int colorResId) {
        mMenuColorNormal = getResources().getColor(colorResId);
        mMenuButton.setColorNormalResId(colorResId);
    }

    public int getMenuButtonColorNormal() {
        return mMenuColorNormal;
    }

    public void setMenuButtonColorNormal(int color) {
        mMenuColorNormal = color;
        mMenuButton.setColorNormal(color);
    }

    public void setMenuButtonColorPressedResId(int colorResId) {
        mMenuColorPressed = getResources().getColor(colorResId);
        mMenuButton.setColorPressedResId(colorResId);
    }

    public int getMenuButtonColorPressed() {
        return mMenuColorPressed;
    }

    public void setMenuButtonColorPressed(int color) {
        mMenuColorPressed = color;
        mMenuButton.setColorPressed(color);
    }

    public void setMenuButtonColorRippleResId(int colorResId) {
        mMenuColorRipple = getResources().getColor(colorResId);
        mMenuButton.setColorRippleResId(colorResId);
    }

    public int getMenuButtonColorRipple() {
        return mMenuColorRipple;
    }

    public void setMenuButtonColorRipple(int color) {
        mMenuColorRipple = color;
        mMenuButton.setColorRipple(color);
    }

    public void addMenuButton(FloatingActionButton fab) {
        addView(fab, mButtonsCount - 2);
        mButtonsCount++;
        addLabel(fab);
    }

    public void removeMenuButton(FloatingActionButton fab) {
        removeView(fab.getLabelView());
        removeView(fab);
        mButtonsCount--;
    }

    public void setNormalMenuSize() {
        mIsExtended = false;
        mMenuButton.changeMenuSize(false); // animate = false
    }

    public void setExtendedMenuSize() {
        mIsExtended = true;
        addExtendedActionMenuText();
        mMenuButton.changeMenuSize(true); // animate = true
    }

    public void setExtendedButtonText(String menuText) {
        extendedButtonText = menuText;
    }

    public void setmExtendedButtonTextColor(int menuTextColor) {
        mExtendedButtonTextColor = menuTextColor;
    }

    public void setmExtendedButtonTextSize(int menuTextSize) {
        mExtendedButtonTextSize = menuTextSize;
    }

    public void addMenuButton(FloatingActionButton fab, int index) {
        int size = mButtonsCount - 2;
        if (index < 0) {
            index = 0;
        } else if (index > size) {
            index = size;
        }

        addView(fab, index);
        mButtonsCount++;
        addLabel(fab);
    }

    public void removeAllMenuButtons() {
        close(true);

        List<FloatingActionButton> viewsToRemove = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != mMenuButton && v != mImageToggle && v instanceof FloatingActionButton) {
                viewsToRemove.add((FloatingActionButton) v);
            }
        }
        for (FloatingActionButton v : viewsToRemove) {
            removeMenuButton(v);
        }
    }

    public String getMenuButtonLabelText() {
        return mMenuLabelText;
    }

    public void setMenuButtonLabelText(String text) {
        mMenuButton.setLabelText(text);
    }

    public void setOnMenuButtonClickListener(OnClickListener clickListener) {
        mMenuButton.setOnClickListener(clickListener);
    }

    public void setOnMenuButtonLongClickListener(OnLongClickListener longClickListener) {
        mMenuButton.setOnLongClickListener(longClickListener);
    }

    public interface OnMenuToggleListener {
        void onMenuToggle(boolean opened);
    }
}
