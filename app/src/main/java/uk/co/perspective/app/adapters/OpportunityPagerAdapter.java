package uk.co.perspective.app.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import uk.co.perspective.app.fragments.OpportunityFormsFragment;
import uk.co.perspective.app.fragments.OpportunityDetailFragment;
import uk.co.perspective.app.fragments.OpportunityFilesFragment;
import uk.co.perspective.app.fragments.OpportunityTasksFragment;

public class OpportunityPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    int ID;
    int OpportunityID;

    Context mContext;

    public OpportunityPagerAdapter(FragmentManager fm, int NumOfTabs, int ID, int OpportunityID) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.ID = ID;
        this.OpportunityID = OpportunityID;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("ID", ID);
        bundle.putInt("OpportunityID", OpportunityID);

        switch (position) {
            case 0:
                OpportunityDetailFragment tab1 = new OpportunityDetailFragment();
                tab1.setArguments(bundle);
                return tab1;

//            case 1:
//                OpportunityTasksFragment tab2 = new OpportunityTasksFragment();
//                tab2.setArguments(bundle);
//                return tab2;

            case 1:
                OpportunityFilesFragment tab3 = new OpportunityFilesFragment();
                tab3.setArguments(bundle);
                return tab3;

            case 2:
                OpportunityFormsFragment tab4 = new OpportunityFormsFragment();
                tab4.setArguments(bundle);
                return tab4;

            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {

//        if (object instanceof JobOverview) {
//            JobOverview f = (JobOverview) object;
//            if (f != null) {
//                f.update();
//            }
//        }
//
//        if (object instanceof JobImages) {
//            JobImages f = (JobImages) object;
//            if (f != null) {
//                f.update();
//            }
//        }

        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}