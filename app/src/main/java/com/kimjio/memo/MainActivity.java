package com.kimjio.memo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kimjio.memo.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_MEMO = 0;

    private MainActivityBinding binding;
    private MemoDBHelper dbHelper;
    private MemoAdapter memoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        dbHelper = new MemoDBHelper(this, MemoDBHelper.TABLE, null, 1);
        setSupportActionBar(binding.appBarLayout.appBar);
        binding.recycler.setAdapter(memoAdapter = new MemoAdapter(dbHelper.getAll(), (v, id, position) -> startActivityForResult(new Intent(this, MemoActivity.class).putExtra("id", id).putExtra("position", position), REQ_MEMO)));
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.fabCreate.setOnClickListener(v -> startActivityForResult(new Intent(this, MemoActivity.class), REQ_MEMO));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_MEMO && resultCode == RESULT_OK) {
            if (data != null) {
                String type = data.getStringExtra("type");
                switch (type) {
                    case MemoActivity.TYPE_SAVE:
                    case MemoActivity.TYPE_UPDATE:
                        updateList();
                        break;
                    case MemoActivity.TYPE_DELETE:
                        removeItem(data.getIntExtra("position", -1));
                        break;
                }
            }
        }
    }

    private void updateList() {
        memoAdapter.setMemos(dbHelper.getAll());
        memoAdapter.notifyDataSetChanged();
    }

    private void removeItem(int position) {
        memoAdapter.setMemos(dbHelper.getAll());
        memoAdapter.notifyItemRemoved(position);
    }
}
