package com.xuhong.smarthome.activity.ConfigActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;
import com.romainpiel.shimmer.ShimmerTextView;
import com.xuhong.smarthome.R;
import com.xuhong.smarthome.activity.BaseActivity;
import com.xuhong.smarthome.adapter.mPagerAdapter;
import com.xuhong.smarthome.constant.Constant;
import com.xuhong.smarthome.utils.L;
import com.xuhong.smarthome.utils.NetStatusUtil;
import com.xuhong.smarthome.utils.SharePreUtils;
import com.xuhong.smarthome.utils.ToastUtils;
import com.xuhong.smarthome.view.GifView;
import com.xuhong.smarthome.view.NoScollViewPager;
import com.xuhong.smarthome.view.RippleView;
import com.xuhong.smarthome.view.StepsView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AirLinkAddDevicesActivity extends BaseActivity implements View.OnClickListener {

    private StepsView stepsView;
    private ShimmerTextView shimmer_tv;
    private ShimmerButton btnNext, btnNextReady;
    private Shimmer shimmer;
    private CheckBox mCheckBox;
    private TextView tv_Message, tv_Progress, tvShow;
    private RippleView mRippleView;


    private EditText etSSID, etPsw;
    private String currentSSID;

    private NoScollViewPager mViewPager;
    private List<View> viewList;

    private mPagerAdapter mPagerAdapter;


    private static final int HANDLER_CODE_PROGRESS = 102;
    private int Flag = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case HANDLER_CODE_PROGRESS:
                    L.e("Flag:" + Flag);

                    if (Flag < 100) {
                        Flag++;
                        tv_Progress.setText(Flag + "%");
                        if (Flag == 30) {
                            tvShow.setText("正在尝试与设备连接....");
                        }
                        mHandler.sendEmptyMessageDelayed(HANDLER_CODE_PROGRESS, 600);
                    } else {
                        L.e("Flag:连接失败"  );
                        mRippleView.stopRippleAnimation();
                        tvShow.setText("连接失败~");
                    }

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arilink_add_devices);

        initView();
        initData();


    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {

        if (NetStatusUtil.isWifiConnected(this)) {
            //获取当前已经连接的Wi-Fi
            currentSSID = NetStatusUtil.getCurentWifiSSID(this);
        } else {
            currentSSID = "请先连接到Wi-Fi网络!";
            etSSID.setFocusable(false);
        }

        String savaWifiSSid = SharePreUtils.getString(this, Constant.WIFI_NAME, null);

        //判断是否
        if (savaWifiSSid != null && savaWifiSSid.equals(currentSSID)) {


        }
        etSSID.setText(currentSSID);

    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImmersionBar.setTitleBar(this, toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //主页面view
        stepsView = (StepsView) findViewById(R.id.stepsView);
        stepsView.setTitle(new String[]{"输入wifi密码", "确认设备状态", "搜索设备", "配网成功"});
        stepsView.setmUnDoneColor(getResources().getColor(R.color.black5));
        stepsView.setmDoneColor(getResources().getColor(R.color.yellow0));

        shimmer_tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);

        mViewPager = (NoScollViewPager) findViewById(R.id.mViewPager);
        viewList = new ArrayList<>();


        View View1 = getLayoutInflater().inflate(R.layout.viewpager_add_devices_one, null);
        View View2 = getLayoutInflater().inflate(R.layout.viewpager_add_devices_two, null);
        View View3 = getLayoutInflater().inflate(R.layout.viewpager_add_devices_three, null);
        View View4 = getLayoutInflater().inflate(R.layout.viewpager_add_devices_four, null);
        viewList.add(View1);
        viewList.add(View2);
        viewList.add(View3);
        viewList.add(View4);

        mPagerAdapter = new mPagerAdapter(viewList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPagingEnabled(false);


        //第一个view

        btnNext = (ShimmerButton) View1.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);

        shimmer = new Shimmer();
        shimmer.start(shimmer_tv);
        shimmer.start(btnNext);

        etSSID = (EditText) View1.findViewById(R.id.etSSID);
        etPsw = (EditText) View1.findViewById(R.id.etPsw);

        //第二个view
        GifView gifView = (GifView) View2.findViewById(R.id.GifView);
        gifView.setMovieResource(R.drawable.airlink);

        btnNextReady = (ShimmerButton) View2.findViewById(R.id.btnNextReady);
        btnNextReady.setClickable(false);
        btnNextReady.setBackground(getResources().getDrawable(R.drawable.img_bg_black_shape));


        mCheckBox = (CheckBox) View2.findViewById(R.id.checkBox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    btnNextReady.setBackground(getResources().getDrawable(R.drawable.img_bg_shape));
                    btnNextReady.setClickable(true);
                    btnNextReady.setOnClickListener(AirLinkAddDevicesActivity.this);
                } else {
                    btnNextReady.setClickable(false);
                    btnNextReady.setBackground(getResources().getDrawable(R.drawable.img_bg_black_shape));
                }
            }
        });

        tv_Message = (TextView) View2.findViewById(R.id.tv_Message);
        tv_Message.setOnClickListener(this);

        //第三个View

        tv_Progress = (TextView) View3.findViewById(R.id.tvShowProgress);
        tvShow = (TextView) View3.findViewById(R.id.tvShow);

        mRippleView = (RippleView) View3.findViewById(R.id.mRippleView);
        mRippleView.setAnimationProgressListener(new RippleView.AnimationListener() {
            @Override
            public void startAnimation() {
                mHandler.sendEmptyMessage(HANDLER_CODE_PROGRESS);

            }

            @Override
            public void EndAnimation() {

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //下一步
            case R.id.btnNext:

                if (currentSSID.contains("请先连接到Wi-Fi网络!")) {
                    ToastUtils.showToast(AirLinkAddDevicesActivity.this, "请先连接到Wi-Fi网络，再点击下一步！");
                    break;
                }

                if (etPsw.getText().toString().isEmpty()) {

                    new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("温馨提示")
                            .setContentText("您确认该Wf-Fi的密码为空!")
                            .setConfirmText("确定")
                            .setCancelText("取消")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    mViewPager.setCurrentItem(1, true);
                                    stepsView.next();
                                }
                            })
                            .show();

                }
                SharePreUtils.putString(AirLinkAddDevicesActivity.this, Constant.WIFI_NAME, currentSSID);

                break;


            case R.id.tv_Message:
                if (mCheckBox.isChecked()) {
                    mCheckBox.setChecked(false);
                    btnNextReady.setClickable(false);
                    btnNextReady.setBackground(getResources().getDrawable(R.drawable.img_bg_black_shape));

                } else {
                    mCheckBox.setChecked(true);
                    btnNextReady.setBackground(getResources().getDrawable(R.drawable.img_bg_shape));
                    btnNextReady.setClickable(true);
                    btnNextReady.setOnClickListener(AirLinkAddDevicesActivity.this);
                }

                break;
            case R.id.btnNextReady:
                mViewPager.setCurrentItem(2, true);
                stepsView.next();
                mRippleView.startRippleAnimation();
                break;
        }
    }

    // 拦截系统的返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("警告")
                    .setContentText("您确认放弃配置吗!")
                    .setConfirmText("放弃")
                    .setCancelText("不了")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            finish();
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }
}
