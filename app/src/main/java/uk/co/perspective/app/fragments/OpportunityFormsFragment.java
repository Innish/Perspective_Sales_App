package uk.co.perspective.app.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.FormEditorActivity;
import uk.co.perspective.app.adapters.OpportunityFormsRecyclerViewAdapter;
import uk.co.perspective.app.adapters.OpportunityTasksRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.NewFormDialog;
import uk.co.perspective.app.dialogs.NewTaskDialog;
import uk.co.perspective.app.entities.OpportunityForm;
import uk.co.perspective.app.joins.JoinOpportunityTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpportunityFormsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpportunityFormsFragment extends Fragment implements OpportunityFormsRecyclerViewAdapter.TaskListener, NewFormDialog.NewFormListener {

    private View root;

    public int ID;
    public int OpportunityID;
    public String CustomerName;

    private static final int CONTENT_REQUEST = 8986;

    private OpportunityFormsRecyclerViewAdapter mAdapter;

    public OpportunityFormsFragment() {
        // Required empty public constructor
    }


    public static OpportunityFormsFragment newInstance(String param1, String param2) {
        OpportunityFormsFragment fragment = new OpportunityFormsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ID = getArguments().getInt("ID");
            OpportunityID= getArguments().getInt("OpportunityID");
        }
        else
        {
            ID = 0;
            OpportunityID = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_opportunity_forms, container, false);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.form_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        refreshForms();

    }

    private void refreshForms()
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.form_list);

        List<OpportunityForm> forms;

        forms = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .opportunityFormDao()
                .getAllOpportunityForms(ID);

        //Add new form

        forms.add(new OpportunityForm(0, "New Form"));

        generateFormList(recyclerView, forms);
    }

    private void generateFormList(RecyclerView recyclerView, List<OpportunityForm> Forms) {

        mAdapter = new OpportunityFormsRecyclerViewAdapter(Forms, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void CreateNewForm() {
        NewFormDialog newDialog = NewFormDialog.newInstance(this, ID, OpportunityID);
        newDialog.show(getChildFragmentManager(), "New Form");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                refreshForms();
            }
        }
    }
    @Override
    public void EditForm(int id) {

        OpportunityForm opportunityForm = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .opportunityFormDao()
                .getOpportunityForm(id);

        Intent intent = new Intent(getContext(), FormEditorActivity.class);
        intent.putExtra("Template", opportunityForm.getFormTemplate());
        intent.putExtra("OpportunityFormID", opportunityForm.getOpportunityFormID());
        intent.putExtra("ID", id);
        intent.putExtra("HasData", opportunityForm.getHasData());
        intent.putExtra("IsComplete", opportunityForm.getIsComplete());
        intent.putExtra("CustomerName", "");

        //if (v.getContext() instanceof Activity) {
        startActivityForResult(intent, CONTENT_REQUEST);
    }

    @Override
    public void RemoveForm() {
        refreshForms();
    }

    @Override
    public void NewFormAdded(int ID) {
        refreshForms();
    }
}