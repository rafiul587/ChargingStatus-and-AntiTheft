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

    List<GuideLine> guideLines = new ArrayList<>();

    public GuideLineViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        initGuideLineModels();
    }

    private void initGuideLineModels() {
        addGuideLine(R.drawable.ic_starting_guide_battery_info, R.string.battery_info, R.string.guide_batter_info_desc);
        addGuideLine(R.drawable.ic_starting_guide_charging_sound, R.string.charging_sound, R.string.guide_charging_sounds_desc);
        addGuideLine(R.drawable.ic_starting_guide_anti_theft, R.string.anti_theft_protection, R.string.guide_anti_theft_protection_desc);
        addGuideLine(R.drawable.ic_starting_guide_charging_animation, R.string.charging_animation, R.string.guide_charging_animation_desc);
        addGuideLine(R.drawable.ic_starting_guide_complete, R.string.all_set, R.string.guide_all_set_desc);
    }

    public void addGuideLine(int image, int title, int description) {
        GuideLine guideLine = new GuideLine(image, title, description);
        guideLines.add(guideLine);
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
