package com.chargingstatusmonitor.souhadev.snake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.chargingstatusmonitor.souhadev.AdsManagerTradePlus;
import com.chargingstatusmonitor.souhadev.Continue;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.R;

public class MainActivity_Snake extends Activity implements View.OnClickListener {

  private SnakePanelView mSnakePanelView;
  private AppCompatActivity mActivity;
  AdsManagerTradePlus adsManagerTradePlus;
  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_snake);

    mSnakePanelView = findViewById(R.id.snake_view);
    mSnakePanelView.reStartGame();
    findViewById(R.id.left_btn).setOnClickListener(this);
    findViewById(R.id.right_btn).setOnClickListener(this);
    findViewById(R.id.top_btn).setOnClickListener(this);
    findViewById(R.id.bottom_btn).setOnClickListener(this);
    findViewById(R.id.start_btn).setOnClickListener(this);
    findViewById(R.id.back_btn).setOnClickListener(this);

    // ADS
      adsManagerTradePlus = new AdsManagerTradePlus(mActivity);
      adsManagerTradePlus.showbanner_tradplus(this, MyApplication.TADUNIT_Banner_Game);
      adsManagerTradePlus.NormalNativeBanner(this , MyApplication.TADUNIT_Native_Banner_Game);
      //__________________________________________________//


  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.left_btn:
        mSnakePanelView.setSnakeDirection(GameType.LEFT);
        break;
      case R.id.right_btn:
        mSnakePanelView.setSnakeDirection(GameType.RIGHT);
        break;
      case R.id.top_btn:
        mSnakePanelView.setSnakeDirection(GameType.TOP);
        break;
      case R.id.bottom_btn:
        mSnakePanelView.setSnakeDirection(GameType.BOTTOM);
        break;
      case R.id.start_btn:
        mSnakePanelView.reStartGame();
        break;
        case R.id.back_btn:
          super.onBackPressed();
        break;
    }
  }

  @Override
  public void onBackPressed() {
     super.onBackPressed();
  }
}
