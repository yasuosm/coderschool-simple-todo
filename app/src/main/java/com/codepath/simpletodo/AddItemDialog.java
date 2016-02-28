package com.codepath.simpletodo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by annt on 2/27/16.
 */
public class AddItemDialog extends DialogFragment {
    private EditText etEditText;
    private ImageView ivRemoveDueDate;
    private TextView tvEditDueDate;
    private long dueTimestamp;
    private static String DATE_FORMAT = "MM-dd-yyyy";
    private boolean isEdit = false;
    private Todo editingItem;

    public AddItemDialog() {

    }

    public static AddItemDialog newInstance() {
        return new AddItemDialog();
    }

    public interface AddItemDialogListener {
        void onFinishAddItem(Todo item);
        void onFinishEditItem(Todo item);
    }

    public void setEditItem(Todo item) {
        isEdit = true;
        editingItem = item;
        dueTimestamp = item.getDueTimestamp();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_item, null);

        etEditText = (EditText) view.findViewById(R.id.et_edit_text);

        ivRemoveDueDate = (ImageView) view.findViewById(R.id.iv_remove_due_date);
        ivRemoveDueDate.setVisibility(dueTimestamp > 0 ? View.VISIBLE : View.INVISIBLE);
        ivRemoveDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDueDate();
            }
        });

        tvEditDueDate = (TextView) view.findViewById(R.id.tv_edit_due_date);
        tvEditDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        if (isEdit) {
            etEditText.setText(editingItem.getText());
            tvEditDueDate.setText(getDateString(dueTimestamp));
        }

        builder.setTitle(R.string.add_item)
                .setView(view)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        final AlertDialog dialog = builder.create();
        etEditText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPositiveClick(dialog);
                    }
                });

        return dialog;
    }

    public void onPositiveClick(Dialog dialog) {
        String text = etEditText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            etEditText.setError(getResources().getString(R.string.this_field_can_not_be_empty));
            return;
        }

        dialog.dismiss();

        AddItemDialogListener listener = (AddItemDialogListener) getActivity();
        if (isEdit) {
            Todo newItem = new Todo(editingItem.getId(), text, dueTimestamp);
            listener.onFinishEditItem(newItem);
        } else {
            Todo newItem = new Todo(0, text, dueTimestamp);
            listener.onFinishAddItem(newItem);
        }

        dismiss();
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDueDate(year, monthOfYear, dayOfMonth);
            }
        };
        Bundle args = new Bundle();
        args.putLong("timeInMillis", dueTimestamp);
        newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void setDueDate(int year, int monthOfYear, int dayOfMonth) {
        dueTimestamp = ymdToTimeInMillis(year, monthOfYear, dayOfMonth);
        tvEditDueDate.setText(getDateString(dueTimestamp));
        ivRemoveDueDate.setVisibility(View.VISIBLE);
    }

    public void removeDueDate() {
        dueTimestamp = 0;
        tvEditDueDate.setText("");
        ivRemoveDueDate.setVisibility(View.INVISIBLE);
    }

    private long ymdToTimeInMillis(int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return c.getTimeInMillis();
    }

    private String getDateString(long timeStamp){
        if (timeStamp > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            Date date = (new Date(timeStamp));
            return sdf.format(date);
        }

        return "";
    }
}
