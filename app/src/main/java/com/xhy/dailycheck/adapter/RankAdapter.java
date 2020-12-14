package com.xhy.dailycheck.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xhy.dailycheck.R;
import com.xhy.dailycheck.bean.ListItem;


import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private List<ListItem> mRankList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRank, tvTime;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvRank = view.findViewById(R.id.tvRank);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }

    public RankAdapter(List<ListItem> recordList) {
        mRankList = recordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem item = mRankList.get(position);
        holder.tvRank.setText(""+(position+1));
        holder.tvName.setText(item.name);
        holder.tvTime.setText(item.time);
    }

    @Override
    public int getItemCount() {
        return mRankList.size();
    }

}