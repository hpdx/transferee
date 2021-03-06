package com.hitomi.tilibrary;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hitomi.tilibrary.view.image.PhotoView;

import java.util.Map;
import java.util.WeakHashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 展示高清图的图片数据适配器
 * Created by hitomi on 2017/1/23.
 */
class TransferAdapter extends PagerAdapter {
    @IdRes
    private static final int ID_IMAGE = 1001;

    private int showIndex;
    private int imageSize;

    private OnDismissListener onDismissListener;
    private OnInstantiateItemListener onInstantListener;

    private Map<Integer, FrameLayout> containnerLayoutMap;

    public TransferAdapter(int showIndex, int imageSize) {
        this.showIndex = showIndex == 0 ? 1 : showIndex;
        this.imageSize = imageSize;
        containnerLayoutMap = new WeakHashMap<>();
    }

    @Override
    public int getCount() {
        return imageSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 获取指定索引页面中的 ImageView
     *
     * @param position
     * @return
     */
    public ImageView getImageItem(int position) {
        FrameLayout parentLayout = containnerLayoutMap.get(position);
        int childCount = parentLayout.getChildCount();
        ImageView imageView = null;
        for (int i = 0; i < childCount; i++) {
            View view = parentLayout.getChildAt(i);
            if (view instanceof ImageView) {
                imageView = (ImageView) view;
                break;
            }
        }
        return imageView;
    }

    public FrameLayout getParentItem(int position) {
        return containnerLayoutMap.get(position);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    public void setOnInstantListener(OnInstantiateItemListener listener) {
        this.onInstantListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // ViewPager instantiateItem 顺序：从 position 开始递减到0位置，再从 positon 递增到 getCount() - 1
        FrameLayout parentLayout = containnerLayoutMap.get(position);
        if (parentLayout == null) {
            parentLayout = newParentLayout(container.getContext(), position);
            containnerLayoutMap.put(position, parentLayout);
        }
        container.addView(parentLayout);
        if (position == showIndex && onInstantListener != null)
            onInstantListener.onComplete();
        return parentLayout;
    }

    @NonNull
    private FrameLayout newParentLayout(Context context, final int pos) {
        // create inner ImageView
        final PhotoView imageView = new PhotoView(context);
        imageView.setId(ID_IMAGE);
        imageView.enable();
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams imageLp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        imageLp.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageLp);

        // create outer ParentLayout
        FrameLayout parentLayout = new FrameLayout(context);
        FrameLayout.LayoutParams parentLp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        parentLayout.setLayoutParams(parentLp);

        parentLayout.addView(imageView);

        // add listener to parentLayout
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismissListener.onDismiss(pos);
            }
        });
        return parentLayout;
    }

    interface OnDismissListener {
        void onDismiss(int pos);
    }

    interface OnInstantiateItemListener {
        void onComplete();
    }

}
