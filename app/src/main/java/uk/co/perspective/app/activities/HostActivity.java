package uk.co.perspective.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import uk.co.perspective.app.R;
import uk.co.perspective.app.fragments.CustomerDetailFragment;
import uk.co.perspective.app.fragments.LeadDetailFragment;
import uk.co.perspective.app.fragments.OpportunityDetailFragment;
import uk.co.perspective.app.fragments.OpportunityPropertiesFragment;
import uk.co.perspective.app.fragments.OrderDetailFragment;
import uk.co.perspective.app.fragments.ProjectPropertiesFragment;
import uk.co.perspective.app.fragments.QuoteDetailFragment;
import uk.co.perspective.app.fragments.TaskDetailFragment;

public class HostActivity extends AppCompatActivity {

    public int ID;
    public String Target;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        Bundle extras;

        assert ab != null;

        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        //get ID

        if (savedInstanceState == null) {

            extras = getIntent().getExtras();
            if (extras == null) {
                Target = "";
                ID = 0;
            } else {
                Target = extras.getString("Target");
                ID = extras.getInt("TargetID");
                System.out.println("Target =  " + Target);
            }

            Bundle bundle = new Bundle();

            Fragment fragment = null;

            if ((Target).equals("CustomerDetails")) {
                if (ab != null) {
                    ab.setTitle("Customer");
                }

                bundle.putInt("ID", ID);
                fragment = new CustomerDetailFragment();
            }
            else if ((Target).equals("LeadDetails")) {
                if (ab != null) {
                    ab.setTitle("Lead");
                }
                bundle.putInt("ID", ID);
                fragment = new LeadDetailFragment();
            }
            else if ((Target).equals("OpportunityDetails")) {
                if (ab != null) {
                    ab.setTitle("Opportunity");
                }
                bundle.putInt("ID", ID);
                fragment = new OpportunityDetailFragment();
            }
            else if ((Target).equals("OpportunityProperties")) {
                if (ab != null) {
                    ab.setTitle("Opportunity");
                }
                bundle.putInt("ID", ID);
                fragment = new OpportunityPropertiesFragment();
            }
            else if ((Target).equals("QuoteDetails")) {
                if (ab != null) {
                    ab.setTitle("Quote");
                }
                bundle.putInt("ID", ID);
                fragment = new QuoteDetailFragment();
            }
            else if ((Target).equals("OrderDetails")) {
                if (ab != null) {
                    ab.setTitle("Order");
                }
                bundle.putInt("ID", ID);
                fragment = new OrderDetailFragment();
            }
            else if ((Target).equals("TaskDetails")) {
                if (ab != null) {
                    ab.setTitle("Task");
                }
                bundle.putInt("ID", ID);
                fragment = new TaskDetailFragment();
            }
            else if ((Target).equals("ProjectDetails")) {
                if (ab != null) {
                    ab.setTitle("Project");
                }
                bundle.putInt("ID", ID);
                fragment = new ProjectPropertiesFragment();
            }

            if(fragment != null) {
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.detail_container, fragment);
                ft.commit();
            }
        }
    }
}