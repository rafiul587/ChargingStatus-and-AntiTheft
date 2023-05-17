package com.chargingstatusmonitor.souhadev.ui.guideline;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.model.GuideLine;

import java.util.ArrayList;
import java.util.List;

public class GuideLineViewPagerAdapter extends FragmentStateAdapter {

    List<GuideLine> guideLines;

    public GuideLineViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        guideLines = initGuideLineModels();
    }

    private List<GuideLine> initGuideLineModels() {

        GuideLine guideLine1 = new GuideLine(R.drawable.ic_starting_guide_battery_info, R.string.battery_info, R.string.guide_batter_info_desc);
        GuideLine guideLine2 = new GuideLine(R.drawable.ic_starting_guide_charging_sound,R.string.charging_sounds, R.string.guide_charging_sounds_desc);
        GuideLine guideLine3 = new GuideLine(R.drawable.ic_starting_guide_anti_theft,R.string.anti_theft_protection, R.string.guide_anti_theft_protection_desc);
        GuideLine guideLine4 = new GuideLine(R.drawable.ic_starting_guide_charging_animation,  R.string.charging_animation, R.string.guide_charging_animation_desc);
        GuideLine guideLine5 = new GuideLine(R.drawable.ic_starting_guide_complete, R.string.all_set, R.string.guide_all_set_desc);
        List<GuideLine> list = new ArrayList<GuideLine>();
        list.add(guideLine1);
        list.add(guideLine2);
        list.add(guideLine3);
        list.add(guideLine4);
        list.add(guideLine5);
        return list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        GuideLine guideLine = guideLines.get(position);
        return new GuideLineFragment(guideLine);
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
