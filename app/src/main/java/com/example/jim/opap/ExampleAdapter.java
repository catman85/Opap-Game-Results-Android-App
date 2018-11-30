package com.example.jim.opap;

import android.support.v7.widget.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<ExampleItem> mExampleList;

    public ExampleAdapter(Context context, ArrayList<ExampleItem> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.example_item, parent, false);
        return new ExampleViewHolder(v);
    }

    public void updateData(ArrayList<ExampleItem> viewModels) {
        mExampleList.clear();
        mExampleList.addAll(viewModels);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);

        String num = currentItem.getmNum();
        String res = currentItem.getmRes();
        Log.v("data", String.valueOf(num));
        Log.v("data", String.valueOf(res));

        holder.mTextViewNum.setText(String.valueOf(num));
        holder.mTextViewRes.setText(String.valueOf(res));
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewNum;
        public TextView mTextViewRes;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mTextViewNum = itemView.findViewById(R.id.text_view_num);
            mTextViewRes = itemView.findViewById(R.id.text_view_result);
        }
    }
}