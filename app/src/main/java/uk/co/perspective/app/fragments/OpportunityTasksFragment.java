package uk.co.perspective.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.OpportunityTasksRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.NewOpportunityTaskDialog;
import uk.co.perspective.app.dialogs.NewProjectTaskDialog;
import uk.co.perspective.app.joins.JoinOpportunityTask;

public class OpportunityTasksFragment extends Fragment implements OpportunityTasksRecyclerViewAdapter.TaskListener, NewOpportunityTaskDialog.NewTaskListener {

    private View root;

    public int ID;
    public int CustomerID;
    public String CustomerName;

    private OpportunityTasksRecyclerViewAdapter mAdapter;

    public OpportunityTasksFragment() {
        // Required empty public constructor
    }

    public static OpportunityTasksFragment newInstance() {
        OpportunityTasksFragment fragment = new OpportunityTasksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ID = getArguments().getInt("ID");
        }
        else
        {
            ID = 0;
        }

        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_opportunity_tasks, container, false);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        refreshTasks();

    }

    private void refreshTasks()
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);

        List<JoinOpportunityTask> tasks;

        tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .opportunityDao()
                .getOpportunityTasks(ID);

        //Add new task

        tasks.add(new JoinOpportunityTask(0, "New Task"));

        generateTaskList(recyclerView, tasks);
    }

    private void generateTaskList(RecyclerView recyclerView, List<JoinOpportunityTask> Tasks) {

        Collections.sort(Tasks);

        //Collate

        String currentPhaseName = "";
        int currentPhaseLocalID;

        List<JoinOpportunityTask> collatedTasks = new ArrayList<JoinOpportunityTask>();;

        for(JoinOpportunityTask theTask : Tasks)
        {
            theTask.setHeader(false);

            if (theTask.getSubject() != null) {
                if (theTask.getSubject().equals("New Task")) {
                    collatedTasks.add(theTask);
                } else {

//                    JoinOpportunityTask headerTask = new JoinOpportunityTask();
//                    headerTask.setSubject("");
//                    headerTask.setHeader(true);
//
//                    collatedTasks.add(headerTask);

                    //Add task

                    collatedTasks.add(theTask);

                }
            }
            else
            {
                //Empty
            }
        }

        mAdapter = new OpportunityTasksRecyclerViewAdapter(collatedTasks, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        inflater.inflate(R.menu.opportunity_detail, menu);
//
//        MenuItem item = menu.findItem(R.id.action_activity);
//
//        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//        {
//            item.setVisible(false);
//        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void CreateNewTask() {
        NewOpportunityTaskDialog newDialog = NewOpportunityTaskDialog.newInstance(this, ID);
        newDialog.show(getChildFragmentManager(), "New Project Task");
    }

    @Override
    public void EditTask(int id) {

        Intent intent = new Intent(root.getContext(), HostActivity.class);
        intent.putExtra("Target", "TaskDetails");
        intent.putExtra("TargetID", id);
        root.getContext().startActivity(intent);
    }

    @Override
    public void CompleteTask(int id, boolean isComplete) {

        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .taskDao()
                .updateTaskComplete(id, isComplete? 1 : 0);
    }

    @Override
    public void RemoveTask() {
        refreshTasks();
    }

    @Override
    public void NewTask() {

    }

}
