package app.com.CATE.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.com.CATE.fragments.HomeFragment;
import app.com.CATE.fragments.LibraryFragment;
import app.com.CATE.fragments.SearchFragment;
import app.com.CATE.fragments.CategoryFragment;

/**
 * Created by mdmunirhossain on 12/18/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeFragment tab1 = new HomeFragment();
                return tab1;
            case 1:
                SearchFragment tab2 = new SearchFragment();
                return tab2;
            case 2:
                CategoryFragment tab3 = new CategoryFragment();
                return tab3;
            case 3:
                LibraryFragment tab4 = new LibraryFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
