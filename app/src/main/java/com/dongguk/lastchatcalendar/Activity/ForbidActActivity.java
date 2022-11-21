package com.dongguk.lastchatcalendar.Activity;

import android.os.Bundle;

import com.dongguk.lastchatcalendar.ChatActivity.Activity.BaseActivity;
import com.dongguk.lastchatcalendar.R;

public class ForbidActActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forbid_behaviour);
        setToolbarTitle("금지 행동 안내");
    }
}
