package com.adocker.test.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {
    @Nullable
    private List<T> mData;
    @LayoutRes
    private int mItemLayoutId;

    public BaseListAdapter(List<T> data, @LayoutRes final int itemLayoutId) {
        mData = data;
        mItemLayoutId = itemLayoutId;
    }

    public void setData(@Nullable List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder = BaseViewHolder.get(position, mItemLayoutId, convertView, parent);
        if (convertView == null) {
            onCreateViewHolder(holder, holder.getConvertView());
        }
        convert(position, getItem(position), holder);

        return holder.getConvertView();
    }

    protected void onCreateViewHolder(BaseViewHolder holder, View itemView) {
        // TODO
    }

    protected abstract void convert(final int position, T data, BaseViewHolder holder);
}
