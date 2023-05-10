package com.chargingstatusmonitor.souhadev;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chargingstatusmonitor.souhadev.model.GuideLine;
import com.chargingstatusmonitor.souhadev.ui.fragments.GuideLineFragment;
import com.chargingstatusmonitor.souhadev.ui.fragments.MyRecordsFragment;
import com.chargingstatusmonitor.souhadev.ui.fragments.VoiceRecordFragment;

import java.util.ArrayList;
import java.util.List;

public class GuideLineViewPagerAdapter extends FragmentStateAdapter {

    List<GuideLine> guideLines;

    public GuideLineViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        guideLines = initGuideLineModels();
    }

    private List<GuideLine> initGuideLineModels() {

        GuideLine guideLine1 = new GuideLine(R.drawable.ic_starting_guide_battery_info, "Battery Info", "Here you will get all the battery related info like battery health, battery level, etc.");
        GuideLine guideLine2 = new GuideLine(R.drawable.ic_starting_guide_charging_sound,"Charging Sounds", "Here you will get all the battery related info like battery health, battery level, etc.");
        GuideLine guideLine3 = new GuideLine(R.drawable.ic_starting_guide_anti_theft,"Anti-theft Protection", "Here you will get all the battery related info like battery health, battery level, etc.");
        GuideLine guideLine4 = new GuideLine(R.drawable.ic_starting_guide_charging_animation,  "Charging Animation", "Here you will get all the battery related info like battery health, battery level, etc.");
        GuideLine guideLine5 = new GuideLine(R.drawable.ic_starting_guide_complete,"You are all set", "Here you will get all the battery related info like battery health, battery level, etc.");
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
