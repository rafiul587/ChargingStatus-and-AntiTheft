package com.chargingstatusmonitor.souhadev.ui.guideline;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.chargingstatusmonitor.souhadev.ui.activities.MainActivity;
import com.chargingstatusmonitor.souhadev.R;
import com.chargingstatusmonitor.souhadev.databinding.ActivityGuideScreenBinding;

public class GuideScreenActivity extends AppCompatActivity {
    
    ActivityGuideScreenBinding binding;
    
    ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if(position == 4){
                binding.next.setImageResource(R.drawable.start_finish);
            }else binding.next.setImageResource(R.drawable.ic_next);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.viewPager.setAdapter(new GuideLineViewPagerAdapter(this));

        binding.next.setOnClickListener(v -> {
            if(binding.viewPager.getCurrentItem() == 4){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
        });
        binding.viewPager.registerOnPageChangeCallback(callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.viewPager.unregisterOnPageChangeCallback(callback);
    }
}