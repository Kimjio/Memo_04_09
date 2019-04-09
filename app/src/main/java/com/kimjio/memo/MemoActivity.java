package com.kimjio.memo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.kimjio.memo.databinding.MemoActivityBinding;

public class MemoActivity extends AppCompatActivity {

    public static final String TYPE_SAVE = "save";
    public static final String TYPE_DELETE = "delete";
    public static final String TYPE_UPDATE = "update";

    private MemoActivityBinding binding;
    private MemoDBHelper dbHelper;
    private Memo target = null;
    private int targetPosition;
    private MenuItem deleteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.memo_activity);
        dbHelper = new MemoDBHelper(this, MemoDBHelper.TABLE, null, 2);
        setSupportActionBar(binding.appBarLayout.appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent;
        if ((intent = getIntent()) != null) {
            target = dbHelper.get(intent.getIntExtra("id", -1));
            targetPosition = intent.getIntExtra("position", -1);
            if (target != null) {
                binding.titleInputText.setText(target.getTitle());
                binding.memoInputText.setText(target.getContent());
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_memo, menu);
        deleteItem = menu.getItem(1);
        deleteItem.setEnabled(target != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save:
                saveMemo();
                break;
            case R.id.delete:
                deleteMemo();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return false;
    }

    private void saveMemo() {
        Editable title = binding.titleInputText.getText();
        Editable content = binding.memoInputText.getText();
        String type;

        if (title == null || content == null) return;
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) return;

        if (target == null) {
            dbHelper.insert(new Memo(title.toString(), content.toString()));
            type = TYPE_SAVE;
        } else {
            dbHelper.update(new Memo(target.getId(), title.toString(), content.toString(), target.getCreated()));
            type = TYPE_UPDATE;
        }

        setResult(RESULT_OK, new Intent().putExtra("type", type));
        finish();
    }

    private void deleteMemo() {
        if (target == null) return;
        dbHelper.delete(target);

        setResult(RESULT_OK, new Intent().putExtra("type", TYPE_DELETE).putExtra("position", targetPosition));
        finish();
    }
}
