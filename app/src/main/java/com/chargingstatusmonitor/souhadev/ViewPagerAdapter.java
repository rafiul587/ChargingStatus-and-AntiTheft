package com.chargingstatusmonitor.souhadev;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chargingstatusmonitor.souhadev.ui.fragments.MyRecordsFragment;
import com.chargingstatusmonitor.souhadev.ui.fragments.VoiceRecordFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new VoiceRecordFragment();
            case 1:
                return new MyRecordsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
