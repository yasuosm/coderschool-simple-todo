package com.codepath.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by annt on 2/27/16.
 */
public class TodoListAdapter extends ArrayAdapter<Todo> {
    private static String DATE_FORMAT = "MM-dd-yyyy";

    private static class ViewHolder {
        TextView tvText;
        TextView tvDueDate;
    }

    public TodoListAdapter(Context context, ArrayList<Todo> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Todo item = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
            viewHolder.tvText = (TextView) convertView.findViewById(R.id.tv_text);
            viewHolder.tvDueDate = (TextView) convertView.findViewById(R.id.tv_due_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        long dueTimestamp = item.getDueTimestamp();
        String dueText = getDateString(dueTimestamp);
        if (dueText.isEmpty()) {
            dueText = convertView.getResources().getString(R.string.not_set);
        }

        viewHolder.tvText.setText(item.getText());
        viewHolder.tvDueDate.setText(convertView.getResources().getString(R.string.due_date) + ": " + dueText);

        return convertView;
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
