package com.machao.RealComm.contract.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecyclerViewAdapter<DataType, Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {
    private Context context;
    private List<DataType> data = new ArrayList<DataType>();

    public AbstractRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public List<DataType> getData() {
        return this.data;
    }

    public DataType getData(int position) {
        return this.data.get(position);
    }

    public void removeData(DataType date) {
        this.data.remove(date);
        this.notifyDataSetChanged();
    }

    public void clearData() {
        this.data.clear();
        this.notifyDataSetChanged();
    }

    public void setData(List<DataType> data) {
        this.data.clear();
        this.notifyDataSetChanged();
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void appendData(List<DataType> data) {
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void appendData(DataType data) {
        this.data.add(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
