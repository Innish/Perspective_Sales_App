package uk.co.perspective.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import uk.co.perspective.app.BlankFragment;
import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.ProjectsRecyclerViewAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.FilterProjectsDialog;
import uk.co.perspective.app.dialogs.NewProjectDialog;
import uk.co.perspective.app.entities.Project;

public class ProjectsFragment extends Fragment implements ProjectsRecyclerViewAdapter.ProjectListener, FilterProjectsDialog.SetFilterListener, NewProjectDialog.NewProjectListener, ProjectDetailFragment.ChangeProjectListener {

    private View root;

    private SearchView searchView;
    private ImageView filterSelection;
    private ProjectsRecyclerViewAdapter mAdapter;

    private String filterStartDate;
    private String filterEndDate;
    private String filterStatus;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    public static ProjectsFragment newInstance(String param1, String param2) {
        ProjectsFragment fragment = new ProjectsFragment();
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

        root = inflater.inflate(R.layout.fragment_projects, container, false);

        searchView = root.findViewById(R.id.searchFor);
        filterSelection = root.findViewById(R.id.filter_selection);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.projects_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(viewcontext, LinearLayoutManager.VERTICAL));

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.projects_list);

        filterStartDate = "";
        filterEndDate = "";
        filterStatus = "";

        refreshProjects();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProjects(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                {
                    refreshProjects();
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
                refreshProjects();
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
                FilterProjectsDialog newFragment = FilterProjectsDialog.newInstance(ProjectsFragment.this);
                newFragment.show(fm, "Filter");
            }
        });

        //Set detail as blank

        if (view.findViewById(R.id.project_detail_container) != null) {

            Fragment fragment = new BlankFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.project_detail_container, fragment);
            ft.commit();

        }
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
            NewProjectDialog newDialog = NewProjectDialog.newInstance(this, 0, "");
            newDialog.show(getChildFragmentManager(), "New Project");
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void generateProjectList(RecyclerView recyclerView, List<Project> Projects) {
        Projects.add(new Project(0, "@NewProject"));
        mAdapter = new ProjectsRecyclerViewAdapter(Projects, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateProjects()
    {
        List<Project> projects;

        if (searchView.getQuery() != "")
        {
            projects = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .projectDao()
                    .searchProjects(searchView.getQuery().toString());
        }
        else
        {
            projects = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .projectDao()
                    .getAll();
        }

        updateProjectList(projects);
    }

    private void searchProjects(String searchText)
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.projects_list);

        List<Project> projects = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .projectDao()
                .searchProjects(searchText);

        generateProjectList(recyclerView, projects);
    }

    private void updateProjectList(List<Project> projects) {

        //sort

        Collections.sort(projects);

        //Add new customer

        projects.add(new Project(0, "@NewProject"));

        if (mAdapter != null) {
            mAdapter.updateList(projects);
        }

    }

    private void refreshProjects() {

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.projects_list);

        List<Project> projects;

        if (!filterStartDate.equals("") && !filterEndDate.equals(""))
        {
            projects = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .projectDao()
                    .getProjectsInRange(filterStartDate, filterEndDate);
        }
        else if (!filterStatus.equals(""))
        {
            projects = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .projectDao()
                    .getProjectsByStatus(filterStatus);
        }
        else {

            projects = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                    .projectDao()
                    .getAll();
        }

        generateProjectList(recyclerView, projects);
    }

    @Override
    public void CreateNewProject() {
        NewProjectDialog newDialog = NewProjectDialog.newInstance(this, 0, "");
        newDialog.show(getChildFragmentManager(), "New Project");
    }

    @Override
    public void EditProject(int id) {

        if (root.findViewById(R.id.project_detail_container) != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", id);

            ProjectPropertiesFragment fragment = ProjectPropertiesFragment.newInstance();
            fragment.setArguments(bundle);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.project_detail_container, fragment);
            ft.commit();
        }
        else
        {
            Intent intent = new Intent(root.getContext(), HostActivity.class);
            intent.putExtra("Target", "ProjectDetails");
            intent.putExtra("TargetID", id);
            root.getContext().startActivity(intent);
        }
    }

    @Override
    public void SetFilter(String startDate, String endDate, String Label) {
        filterStartDate = startDate;
        filterEndDate = endDate;
        filterStatus = "";

        refreshProjects();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void SetStatusFilter(String status) {
        filterStartDate = "";
        filterEndDate = "";
        filterStatus = status;

        refreshProjects();

        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    public void RemoveProject() {
        refreshProjects();
    }

    @Override
    public void NewProject(String ProjectName) {
        refreshProjects();
    }

    @Override
    public void ProjectChanged() {
        updateProjects();
    }
}