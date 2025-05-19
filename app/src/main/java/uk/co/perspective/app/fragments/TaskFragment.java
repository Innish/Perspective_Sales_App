package uk.co.perspective.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.BlankFragment;
import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.TasksRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.FilterTaskDialog;
import uk.co.perspective.app.dialogs.NewTaskDialog;
import uk.co.perspective.app.entities.Task;

public class TaskFragment extends Fragment implements TasksRecyclerViewAdapter.TaskListener, NewTaskDialog.NewTaskListener, TaskDetailFragment.ChangeTaskListener, FilterTaskDialog.SetFilterListener {

    private View root;

    private SearchView searchView;
    private ImageView filterSelection;
    private TasksRecyclerViewAdapter mAdapter;

    private String filterStartDate;
    private String filterEndDate;

    public TaskFragment() {

    }

    public static TaskFragment newInstance(String param1, String param2) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_task, container, false);

        searchView = root.findViewById(R.id.searchFor);
        filterSelection = root.findViewById(R.id.filter_selection);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        filterStartDate = "";
        filterEndDate = "";

        refreshTasks();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTasks(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshTasks();
                    searchView.setFocusable(false);
                    searchView.setIconified(false);
                    searchView.clearFocus();
                    return false;
                }
                else {
                    return false;
                }
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refreshTasks();
                searchView.setFocusable(false);
                searchView.setIconified(false);
                searchView.clearFocus();
                return false;
            }
        });

        filterSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                FilterTaskDialog newFragment = FilterTaskDialog.newInstance(TaskFragment.this);
                newFragment.show(fm, "Filter");
            }
        });

        //Set detail as blank

        if (view.findViewById(R.id.task_detail_container) != null) {

            Fragment fragment = new BlankFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.task_detail_container, fragment);
            ft.commit();

        }
    }

    private void refreshTasks()
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);

        List<Task> tasks;

        if (!filterStartDate.equals("") && !filterEndDate.equals(""))
        {
            tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .taskDao()
                    .getTasksInRange(filterStartDate, filterEndDate);
        }
        else
        {
            tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .taskDao()
                    .getRecentTasks();
        }

        //Add new task

        tasks.add(new Task(0, "New Task"));

        generateTaskList(recyclerView, tasks);
    }

    private void updateTasks()
    {
        List<Task> tasks;

        if (searchView.getQuery() != "")
        {
            tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .taskDao()
                    .searchTasks(searchView.getQuery().toString());
        }
        else {
            if (!filterStartDate.equals("") && !filterEndDate.equals(""))
            {
                tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .taskDao()
                        .getTasksInRange(filterStartDate, filterEndDate);
            }
            else
            {
                tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                        .taskDao()
                        .getRecentTasks();
            }
        }

        //Add new task (this gets removed on the update)

        tasks.add(new Task(0, "New Task"));

        updateTaskList(tasks);
    }

    private void searchTasks(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);

        List<Task> tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .taskDao()
                .searchTasks(searchText);

        //Add new task

        tasks.add(new Task(0, "New Task"));

        generateTaskList(recyclerView, tasks);
    }


    private void generateTaskList(RecyclerView recyclerView, List<Task> Tasks) {

        Collections.sort(Tasks);

        mAdapter = new TasksRecyclerViewAdapter(Tasks, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateTaskList(List<Task> Tasks) {

        //sort

        Collections.sort(Tasks);

        if (mAdapter != null) {
            mAdapter.updateList(Tasks);
        }

    }

    @Override
    public void CreateNewTask() {
        NewTaskDialog newDialog = NewTaskDialog.newInstance(this);
        newDialog.show(getChildFragmentManager(), "New Task");
    }

    @Override
    public void EditTask(int id) {

        if (root.findViewById(R.id.task_detail_container) != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", id);

            TaskDetailFragment fragment = TaskDetailFragment.newInstance(this);
            fragment.setArguments(bundle);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.task_detail_container, fragment);
            ft.commit();
        }
        else
        {
            Intent intent = new Intent(root.getContext(), HostActivity.class);
            intent.putExtra("Target", "TaskDetails");
            intent.putExtra("TargetID", id);
            root.getContext().startActivity(intent);
        }
    }

    @Override
    public void CompleteTask(int id, boolean isComplete) {

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .taskDao()
                .updateTaskComplete(id, isComplete? 1 : 0);
    }

    @Override
    public void RemoveTask() {

        //should remove details panel if it's the one thats been deleted

        Fragment fragment = new BlankFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.task_detail_container, fragment);
        ft.commit();
    }

    @Override
    public void NewTask() {
        refreshTasks();
    }

    @Override
    public void TaskChanged() {
        updateTasks();
    }

    @Override
    public void SetFilter(String startDate, String endDate, String Label) {

        filterStartDate = startDate;
        filterEndDate = endDate;

        refreshTasks();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_new) {
            NewTaskDialog newDialog = NewTaskDialog.newInstance(this);
            newDialog.show(getChildFragmentManager(), "New Task");
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }
}