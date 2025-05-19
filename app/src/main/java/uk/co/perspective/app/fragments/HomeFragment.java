package uk.co.perspective.app.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.activities.HostActivity;
import uk.co.perspective.app.adapters.TasksRecyclerViewAdapter;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.dialogs.NewTaskDialog;
import uk.co.perspective.app.entities.LeadsSummary;
import uk.co.perspective.app.entities.OpportunitySummary;
import uk.co.perspective.app.entities.Task;

public class
HomeFragment extends Fragment implements TasksRecyclerViewAdapter.TaskListener, NewTaskDialog.NewTaskListener {

    private View root;
    private PieChart chartOpportunities;
    private PieChart chartLeads;
    private LineChart chartSalesByMonth;
    private TextView TotalOpportunities;
    private TextView OpportunitiesValue;
    private TextView ConversionRate;
    private TextView WonValue;
    private ImageView HoldingImageA;
    private ImageView HoldingImageB;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        //First Chart

        chartOpportunities = root.findViewById(R.id.opportunitiesByStage);

        chartOpportunities.setUsePercentValues(true);
        chartOpportunities.getDescription().setEnabled(false);
        chartOpportunities.setExtraOffsets(5, 10, 5, 10);
        chartOpportunities.setDragDecelerationFrictionCoef(0.95f);
        chartOpportunities.setCenterText(generateCenterSpannableText("Opportunites"));
        chartOpportunities.setDrawHoleEnabled(true);
        chartOpportunities.setHoleColor(Color.WHITE);
        chartOpportunities.setTransparentCircleColor(Color.WHITE);
        chartOpportunities.setTransparentCircleAlpha(110);
        chartOpportunities.setHoleRadius(52f);
        chartOpportunities.setTransparentCircleRadius(61f);
        chartOpportunities.setDrawCenterText(true);
        chartOpportunities.setRotationAngle(0);
        chartOpportunities.setRotationEnabled(true);
        chartOpportunities.setHighlightPerTapEnabled(true);
        chartOpportunities.animateY(1200, Easing.EaseInOutQuad);
        chartOpportunities.setEntryLabelColor(Color.DKGRAY);
        chartOpportunities.setEntryLabelTextSize(8f);

        Legend l = chartOpportunities.getLegend();
        l.setEnabled(false);

        //Second Chart

        chartLeads = root.findViewById(R.id.leadsByStage);

        chartLeads.setUsePercentValues(true);
        chartLeads.getDescription().setEnabled(false);
        chartLeads.setExtraOffsets(5, 10, 5, 10);
        chartLeads.setDragDecelerationFrictionCoef(0.95f);
        chartLeads.setCenterText(generateCenterSpannableText("Leads"));
        chartLeads.setDrawHoleEnabled(true);
        chartLeads.setHoleColor(Color.WHITE);
        chartLeads.setTransparentCircleColor(Color.WHITE);
        chartLeads.setTransparentCircleAlpha(110);
        chartLeads.setHoleRadius(52f);
        chartLeads.setTransparentCircleRadius(61f);
        chartLeads.setDrawCenterText(true);
        chartLeads.setRotationEnabled(false);
        chartLeads.setHighlightPerTapEnabled(true);
        chartLeads.animateY(1200, Easing.EaseInOutQuad);
        chartLeads.setEntryLabelColor(Color.DKGRAY);
        chartLeads.setEntryLabelTextSize(8f);
        chartLeads.setMaxAngle(180f); // HALF CHART
        chartLeads.setRotationAngle(180f);
        chartLeads.setCenterTextOffset(0, -20);

        Legend l2 = chartLeads.getLegend();
        l2.setEnabled(false);

        moveOffScreen();

        TotalOpportunities = root.findViewById(R.id.totalOpportunities);
        OpportunitiesValue = root.findViewById(R.id.opportunitiesValue);
        ConversionRate = root.findViewById(R.id.conversionRate);
        WonValue = root.findViewById(R.id.wonValue);

        HoldingImageA  = root.findViewById(R.id.opportunitiesChartHoldingImage);
        HoldingImageB  = root.findViewById(R.id.leadsChartHoldingImage);

        //Tasks

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);
        Context viewcontext = recyclerView.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(viewcontext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }

    private void moveOffScreen() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        int offset = (int)(height * 0.20); /* percent to move */

        RelativeLayout.LayoutParams rlParams =
                (RelativeLayout.LayoutParams) chartLeads.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        chartLeads.setLayoutParams(rlParams);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setData();
        refreshTasks();
    }

    private void refreshTasks()
    {
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.task_list);

        List<Task> tasks = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                .taskDao()
                .getRecentTasks();

        generateTaskList(recyclerView, tasks);
    }

    private void generateTaskList(RecyclerView recyclerView, List<Task> Tasks) {

        Tasks.add(new Task(0,"New Task"));

        TasksRecyclerViewAdapter mAdapter = new TasksRecyclerViewAdapter(Tasks, getChildFragmentManager(), this.getContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    public static final int[] PASTEL_COLORS2 = {
            Color.rgb(205, 220, 242), Color.rgb(177, 237, 230), Color.rgb(189, 225, 176),
            Color.rgb(177, 188, 215), Color.rgb(245, 228, 177), Color.rgb(255, 178, 157)
    };

    private void setData() {

        AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();

        ArrayList<PieEntry> entries1 = new ArrayList<>();
        ArrayList<PieEntry> entries2 = new ArrayList<>();
        ArrayList<Entry> entries3 = new ArrayList<>();

        //Get Summary of opportunities

        List<OpportunitySummary> summary = database
                .opportunityDao()
                .getOpportunitySummary();

        for(OpportunitySummary summaryItem : summary)
        {
            entries1.add(new PieEntry(summaryItem.getStatusCount(), summaryItem.getStatus()));
        }

        List<LeadsSummary> leadSummary = database
                .leadDao()
                .getLeadSummary();

        for(LeadsSummary summaryItem : leadSummary)
        {
            entries2.add(new PieEntry(summaryItem.getStatusCount(), summaryItem.getStatus()));
        }

        int totalOpportunities = database.opportunityDao().getOpportunitiesCount();
        int wonCount = database.opportunityDao().getWonOpportunitiesCount();

        TotalOpportunities.setText(String.format(Locale.UK, "%d", totalOpportunities));
        OpportunitiesValue.setText(String.format(Locale.UK, "£%,.2f", database.opportunityDao().getOpportunitiesValue()));

        //Calculate conversion rate

        float winRate = (100f / totalOpportunities) * wonCount;

        if(!Float.isNaN(winRate)) {
            ConversionRate.setText(String.format(Locale.UK, "%.2f%%", winRate));
        }

        WonValue.setText(String.format(Locale.UK, "£%,.2f", database.opportunityDao().getWonOpportunitiesValue()));

        //Charts

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        ArrayList<Integer> colors2 = new ArrayList<>();

        for (int c : PASTEL_COLORS2)
            colors2.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors2.add(c);

        if (entries1.size() > 0) {

            HoldingImageA.setVisibility(View.GONE);
            chartOpportunities.setVisibility(View.VISIBLE);

            PieDataSet dataSet1 = new PieDataSet(entries1, "Opportunities");
            dataSet1.setDrawIcons(false);
            dataSet1.setSliceSpace(3f);
            dataSet1.setIconsOffset(new MPPointF(0, 40));
            dataSet1.setSelectionShift(5f);
            dataSet1.setValueTextSize(8f);
            dataSet1.setColors(colors);

            PieData data1 = new PieData(dataSet1);
            data1.setValueFormatter(new PercentFormatter());
            data1.setValueTextSize(11f);
            data1.setValueTextColor(Color.DKGRAY);
            chartOpportunities.setData(data1);
            chartOpportunities.highlightValues(null);
            chartOpportunities.invalidate();
        }
        else
        {
            HoldingImageA.setVisibility(View.VISIBLE);
            chartOpportunities.setVisibility(View.GONE);
        }



        if (entries2.size() > 0) {

            HoldingImageB.setVisibility(View.GONE);
            chartLeads.setVisibility(View.VISIBLE);

            PieDataSet dataSet2 = new PieDataSet(entries2, "Leads");
            dataSet2.setDrawIcons(false);
            dataSet2.setSliceSpace(3f);
            dataSet2.setIconsOffset(new MPPointF(0, 40));
            dataSet2.setSelectionShift(5f);
            dataSet2.setDrawValues(false);
            dataSet2.setValueTextSize(8f);
            dataSet2.setColors(colors2);

            PieData data2 = new PieData(dataSet2);
            data2.setValueFormatter(new PercentFormatter());
            data2.setValueTextSize(11f);
            data2.setValueTextColor(Color.DKGRAY);
            //data.setValueTypeface(tfLight);
            chartLeads.setData(data2);
            chartLeads.highlightValues(null);
            chartLeads.invalidate();
        }
        else
        {
            HoldingImageB.setVisibility(View.VISIBLE);
            chartLeads.setVisibility(View.GONE);
        }

    }

    public void animateTextView(int initialValue, int finalValue, final TextView  textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                textview.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();
    }

    private SpannableString generateCenterSpannableText(String title) {

        SpannableString s = new SpannableString(title);
        s.setSpan(new RelativeSizeSpan(1.0f), 0, title.length(), 0);
        return s;
    }

    @Override
    public void CreateNewTask() {

        NewTaskDialog newDialog = NewTaskDialog.newInstance(this);
        newDialog.show(getChildFragmentManager(), "New Task");
        //refreshTasks();
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

        refreshTasks();
    }

    @Override
    public void RemoveTask() {
        refreshTasks();
    }

    @Override
    public void NewTask() {
        refreshTasks();
    }
}