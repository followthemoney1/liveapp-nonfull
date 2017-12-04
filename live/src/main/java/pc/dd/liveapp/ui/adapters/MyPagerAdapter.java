package pc.dd.liveapp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import pc.dd.liveapp.ui.fragments.CreateEventFragment;
import pc.dd.liveapp.ui.fragments.EventsFragment;
import pc.dd.liveapp.ui.fragments.ProfileFragment;
import pc.dd.liveapp.ui.fragments.ProposalFragment;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    
    private final int NUM_ITEMS = 4;
    
    private CreateEventFragment createPartyFragment = CreateEventFragment.newInstance(null);
    private EventsFragment mainFragment = EventsFragment.newInstance();
    private ProfileFragment profileFragment =  ProfileFragment.newInstance(null);
    private ProposalFragment proposalFragment = ProposalFragment.newInstance();
    
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
    
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mainFragment;
            case 1:
                return proposalFragment;
            case 2:
                return createPartyFragment;
            case 3:
                return profileFragment;
            default:
                return mainFragment;
        }
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
    
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    
    public void addFragment(int position, Fragment fragment) {
        try {
            int id = getItem(position).getView().getId();
            getItem(position).getChildFragmentManager().beginTransaction().replace(id, fragment, "nextFragment" + position).addToBackStack(fragment.getTag()).commit();
            getItem(position).getChildFragmentManager().executePendingTransactions();
            Log.d("fragment id--", String.valueOf(id));
        } catch (Exception e) {
            Log.d("Error add fragment--", e.toString());
        };
    }
}
