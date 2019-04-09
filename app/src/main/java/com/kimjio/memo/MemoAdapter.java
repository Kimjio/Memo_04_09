package com.kimjio.memo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MemoAdapter extends RecyclerView.Adapter<MemoViewHolder> {

    private List<Memo> memos;
    private OnItemClickListener itemClickListener;

    public MemoAdapter(List<Memo> memos, OnItemClickListener itemClickListener) {
        this.memos = memos;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memos.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, memo.getId(), holder.getAdapterPosition());
        });
        holder.binding.memoText.setText(memo.getTitle());
        holder.binding.createdTimeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(memo.getCreated()));
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int memoId, int position);
    }
}
