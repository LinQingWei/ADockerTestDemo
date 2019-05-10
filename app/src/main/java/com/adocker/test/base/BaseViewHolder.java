package com.adocker.test.base;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseViewHolder {
    private int mPosition;
    @Nullable
    private View mConvertView;
    private SparseArray<View> mViewSparseArray;

    public BaseViewHolder(final int position, View convertView) {
        mPosition = position;
        mConvertView = convertView;
        mConvertView.setTag(this);
        mViewSparseArray = new SparseArray<>();
    }

    public static BaseViewHolder get(final int position, final @LayoutRes int layoutId,
                                     View convertView, ViewGroup parent) {
        BaseViewHolder holder;
        if (convertView == null) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(layoutId, parent, false);
            holder = new BaseViewHolder(position, itemView);
        } else {
            holder = (BaseViewHolder) convertView.getTag();
            holder.mPosition = position;
        }

        return holder;
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes final int id) {
        View view = mViewSparseArray.get(id);
        if (view == null) {
            view = mConvertView.findViewById(id);
            mViewSparseArray.put(id, view);
        }

        return (T) view;
    }

    @Nullable
    public View getConvertView() {
        return mConvertView;
    }

    public int getPosition() {
        return mPosition;
    }

    public BaseViewHolder setEnable(@IdRes final int id, final boolean enable) {
        View view = findViewById(id);
        if (view != null) {
            view.setEnabled(enable);
        }

        return this;
    }

    public BaseViewHolder setText(@IdRes final int id, @NonNull String text) {
        TextView tv = findViewById(id);
        if (tv != null) {
            tv.setText(text);
        }

        return this;
    }

    public BaseViewHolder setVisible(@IdRes final int id, final boolean visible) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        return this;
    }

    public BaseViewHolder setOnClickListener(@IdRes final int viewId, View.OnClickListener listener) {
        if (listener != null) {
            View view = findViewById(viewId);
            if (view != null) {
                view.setOnClickListener(listener);
            }
        }

        return this;
    }
}
