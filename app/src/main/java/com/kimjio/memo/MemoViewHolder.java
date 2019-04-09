package com.kimjio.memo;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kimjio.memo.databinding.MemoItemBinding;


public class MemoViewHolder extends RecyclerView.ViewHolder {

    MemoItemBinding binding;

    public MemoViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}
