package uk.co.perspective.app.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import uk.co.perspective.app.fragments.ProjectDetailFragment;
import uk.co.perspective.app.fragments.ProjectFilesFragment;
import uk.co.perspective.app.fragments.ProjectTasksFragment;

public class ProjectPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    int ID;

    Context mContext;

    public ProjectPagerAdapter(FragmentManager fm, int NumOfTabs, int ID) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.ID = ID;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("ID", ID);

        switch (position) {
            case 0:
                ProjectDetailFragment tab1 = new ProjectDetailFragment();
                tab1.setArguments(bundle);
                return tab1;

            case 1:
                ProjectTasksFragment tab2 = new ProjectTasksFragment();
                tab2.setArguments(bundle);
                return tab2;

            case 2:
                ProjectFilesFragment tab3 = new ProjectFilesFragment();
                tab3.setArguments(bundle);
                return tab3;

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