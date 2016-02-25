package com.codepath.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    private EditText etEditItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        setTitle(R.string.title_activity_edit_item);

        String itemText = getIntent().getStringExtra("itemText");
        etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(itemText);
        showSoftKeyboard(etEditItem);
    }

    public void onSave(View v) {
        Intent data = new Intent();
        int position = getIntent().getIntExtra("position", 0);
        data.putExtra("position", position);
        data.putExtra("itemText", etEditItem.getText().toString());
        setResult(RESULT_OK, data);
        this.finish();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
