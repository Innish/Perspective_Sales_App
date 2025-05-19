package uk.co.perspective.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import uk.co.perspective.app.R;
import uk.co.perspective.app.adapters.ActivityRecyclerViewAdapter;
import uk.co.perspective.app.adapters.ProjectPagerAdapter;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.EditActivityDialog;
import uk.co.perspective.app.dialogs.NewActivityDialog;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Project;

public class ProjectPropertiesFragment extends Fragment implements NewActivityDialog.NewActivityListener,
        ActivityRecyclerViewAdapter.ActivityListener,
        EditActivityDialog.UpdatedActivityListener {

    private View root;

    public ProjectPagerAdapter adapter;

    private RelativeLayout activityContainer;

    private ActivityRecyclerViewAdapter mActivityAdapter;

    public int ID;
    private boolean drawVisible;

    public ProjectPropertiesFragment() {
        // Required empty public constructor
    }

    public static ProjectPropertiesFragment newInstance() {
        ProjectPropertiesFragment fragment = new ProjectPropertiesFragment();
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_project_properties, container, false);

        TabLayout tabLayout = root.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Overview").setIcon(R.drawable.ic_menu_project));
        tabLayout.addTab(tabLayout.newTab().setText("Tasks").setIcon(R.drawable.ic_menu_tasks));
        tabLayout.addTab(tabLayout.newTab().setText("Documents").setIcon(R.drawable.ic_file));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = root.findViewById(R.id.pager);
        adapter = new ProjectPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), ID);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Activity

        if (root.findViewById(R.id.activity_list) != null) {

            final RecyclerView activityRecyclerView = (RecyclerView) root.findViewById(R.id.activity_list);
            Context activityRecyclerViewContext = activityRecyclerView.getContext();

            activityRecyclerView.setLayoutManager(new LinearLayoutManager(activityRecyclerViewContext));
            activityRecyclerView.setItemAnimator(new DefaultItemAnimator());

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                activityContainer = root.findViewById(R.id.activity_list_container);
            }
        }

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

        drawVisible = false;

        if (root.findViewById(R.id.activity_list) != null) {
            refreshActivity();
        }
    }

    public void refreshActivity()
    {
        final RecyclerView activityRecyclerView = (RecyclerView) root.findViewById(R.id.activity_list);

        Project localProject = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .projectDao()
                .getProject(ID);

        if (localProject != null) {

             if (localProject.getCustomerID() != 0) {

                List<Activity> activities = DatabaseClient.getInstance(requireContext())
                        .getAppDatabase()
                        .activityDao()
                        .getCustomerActivityByCustomerID(localProject.getCustomerID());

                generateActivityList(activityRecyclerView, activities);
            }
            else if (localProject.getLocalCustomerID() != 0) {

                 List<Activity> activities = DatabaseClient.getInstance(requireContext())
                         .getAppDatabase()
                         .activityDao()
                         .getCustomerActivity(localProject.getLocalCustomerID());

                 generateActivityList(activityRecyclerView, activities);
             }

        }
    }

    private void generateActivityList(RecyclerView recyclerView, List<Activity> activities) {

        activities.add(new Activity(0, "New Activity"));

        mActivityAdapter = new ActivityRecyclerViewAdapter(activities, getChildFragmentManager(),this.getContext(), this);
        recyclerView.setAdapter(mActivityAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.project_properties, menu);

        MenuItem item = menu.findItem(R.id.action_activity);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            item.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        int id = item.getItemId();

        if (id == R.id.action_activity) {

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

                if (drawVisible)
                {
                    activityContainer.animate().translationX(dpToPx(300)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawVisible = false;
                        }
                    }).setInterpolator(new LinearInterpolator());
                }
                else {
                    activityContainer.animate().translationX(-dpToPx(300)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawVisible = true;
                        }
                    }).setInterpolator(new LinearInterpolator());
                }
            }

        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void CreateNewActivity() {

        //Need to get projects Customer

        Project localProject = DatabaseClient.getInstance(requireContext())
                .getAppDatabase()
                .projectDao()
                .getProject(ID);

        if (localProject != null) {
            NewActivityDialog newDialog = NewActivityDialog.newInstance(this, localProject.getLocalCustomerID(), localProject.getCustomerName());
            newDialog.show(getChildFragmentManager(), "New Activity");
        }
    }

    @Override
    public void EditActivity(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        EditActivityDialog newDialog = EditActivityDialog.newInstance(this);
        newDialog.setArguments(bundle);
        newDialog.show(getChildFragmentManager(), "Edit Activity");
    }

    @Override
    public void RemoveActivity() {
        refreshActivity();
    }

    public static float dpToPx(int dp) {
        return (int)dp * Resources.getSystem().getDisplayMetrics().density;
    }

    @Override
    public void NewActivityAdded() {
        refreshActivity();
    }

    @Override
    public void ActivityUpdated() {
        refreshActivity();
    }
}
