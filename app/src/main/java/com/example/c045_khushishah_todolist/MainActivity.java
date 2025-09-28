package com.example.c045_khushishah_todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TaskAdapter adapter;
    private DatabaseReference dbRef;
    private List<Task> tasks = new ArrayList<>();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recyclerTasks);
        fab = findViewById(R.id.fabAdd);

        adapter = new TaskAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // click to edit
        adapter.setOnItemClickListener(task -> showAddEditDialog(task));

        // Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("tasks");

        // Listen to ordered by priority (1 -> 2 -> 3)
        dbRef.orderByChild("priority").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasks.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Task t = ds.getValue(Task.class);
                    if (t != null) tasks.add(t);
                }
                adapter.setTasks(tasks);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "DB error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(v -> showAddEditDialog(null));

        enableSwipeToDelete();
    }

    // Add new or edit existing task
    private void showAddEditDialog(@Nullable Task taskToEdit) {
        boolean editing = (taskToEdit != null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_edit, null);
        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDesc = view.findViewById(R.id.etDescription);
        Spinner spinner = view.findViewById(R.id.spinnerPriority);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        if (editing) {
            etTitle.setText(taskToEdit.getTitle());
            etDesc.setText(taskToEdit.getDescription());
            spinner.setSelection(mapPriorityToSpinnerIndex(taskToEdit.getPriority()));
            builder.setTitle("Edit Task");
        } else {
            builder.setTitle("Add Task");
        }

        builder.setView(view)
                .setPositiveButton(editing ? "Update" : "Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(this, "Enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String desc = etDesc.getText().toString().trim();
                    if (desc.isEmpty()) {
                        Toast.makeText(this, "Enter a description", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int priority = mapSpinnerIndexToPriority(spinner.getSelectedItemPosition());
                    if (editing) {
                        // Update in DB
                        Task updated = new Task(taskToEdit.getId(), title, desc, priority);
                        dbRef.child(taskToEdit.getId()).setValue(updated);
                    } else {
                        // Create new
                        String id = dbRef.push().getKey();
                        if (id == null) { Toast.makeText(this, "Unable to create ID", Toast.LENGTH_SHORT).show(); return; }
                        Task newTask = new Task(id, title, desc, priority);
                        dbRef.child(id).setValue(newTask);
                    }
                })
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .show();
    }

    private int mapSpinnerIndexToPriority(int index) {
        // spinner: 0=High,1=Medium,2=Low
        if (index == 0) return 1;
        if (index == 1) return 2;
        return 3;
    }

    private int mapPriorityToSpinnerIndex(int p) {
        if (p == 1) return 0;
        if (p == 2) return 1;
        return 2;
    }

    // Swipe to delete
    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int pos = viewHolder.getAdapterPosition();
                        Task task = adapter.getTaskAt(pos);
                        if (task != null) {
                            // remove from database
                            dbRef.child(task.getId()).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                };
        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recycler);
    }
}
