package com.codepath.simpletodo;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        AddItemDialog.AddItemDialogListener {
    private ArrayList<Todo> items;
    private TodoListAdapter itemsAdapter;
    private ListView lvItems;
    private TodoDatabaseHelper itemsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.simple_todo);

        itemsDb = TodoDatabaseHelper.getInstance(this);

        lvItems = (ListView) findViewById(R.id.lv_items);
        items = (ArrayList<Todo>) itemsDb.getAllTodoOrdered();
        itemsAdapter = new TodoListAdapter(this, items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Todo item = items.get(position);
                itemsDb.deleteTodo(item);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onEditItem(position);
            }
        });
    }

    public void onAddItem(View v) {
        FragmentManager fm = getSupportFragmentManager();
        AddItemDialog dialog = AddItemDialog.newInstance();
        dialog.show(fm, "fragment_add_item");
    }

    private void onEditItem(int position) {
        FragmentManager fm = getSupportFragmentManager();
        AddItemDialog dialog = AddItemDialog.newInstance();
        dialog.setEditItem(items.get(position));
        dialog.show(fm, "fragment_add_item");
    }

    @Override
    public void onFinishAddItem(Todo item) {
        itemsDb.addTodo(item);
        updateList();
    }

    @Override
    public void onFinishEditItem(Todo item) {
        itemsDb.updateTodo(item);
        updateList();
    }

    private void updateList() {
        items.clear();
        items.addAll(itemsDb.getAllTodoOrdered());
        itemsAdapter.notifyDataSetChanged();
    }
}
