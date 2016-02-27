package com.codepath.simpletodo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by annt on 2/27/16.
 */
public class EditItemDialog extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText etItemText;

    public EditItemDialog() {

    }

    public static EditItemDialog newInstance(int position, String itemText) {
        EditItemDialog frag = new EditItemDialog();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("itemText", itemText);
        frag.setArguments(args);
        return frag;
    }

    public interface EditItemDialogListener {
        void onFinishEditDialog(int position, String itemText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(R.string.edit_item);

        etItemText = (EditText) view.findViewById(R.id.etItemText);

        String itemText = getArguments().getString("itemText");
        etItemText.setText(itemText);

        etItemText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etItemText.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            int position = getArguments().getInt("position");
            String itemText = etItemText.getText().toString().trim();
            if (TextUtils.isEmpty(itemText)) {
                etItemText.setError(getResources().getString(R.string.item_text_can_not_be_empty));
                return false;
            }

            EditItemDialogListener listener = (EditItemDialogListener) getActivity();
            listener.onFinishEditDialog(position, itemText);
            dismiss();
            return true;
        }

        return false;
    }
}
