package com.sun.tunnelmonitoring.bluetooth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.RadioGroup;
import com.sun.tunnelmonitoring.R;

@SuppressLint("Registered")
public class CommunityActivity extends AppCompatActivity {
    private RadioGroup mRgGroup;
    private FragmentManager fragmentManager;

    private static final String[] FRAGMENT_TAG = {"Setting", "Text", "Tiket"};
    private SettingFragment settingFragment;//设置
    private TextFragment textFragment;//打印文字
    private TicketFragment ticketFragment;//打印订单小票

    private final int show_setting = 0;//设置
    private final int show_text = 1;//文字
    private final int show_ticket = 2;//订单小票
    //    private final int show_picture = 3;//图片
    private int mrIndex = show_setting;//默认选中首页

    private int index = -100;// 记录当前的选项
    /**
     * 上一次界面 onSaveInstanceState 之前的tab被选中的状态 key 和 value
     */
    private static final String PRV_SELINDEX = "PREV_SELINDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决底部选项按钮被输入文字框顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //显示界面
        setContentView(R.layout.activity_print_home);

        fragmentManager = getSupportFragmentManager();
        //防止app闪退后fragment重叠
        if (savedInstanceState != null) {
            //读取上一次界面Save的时候tab选中的状态
            mrIndex = savedInstanceState.getInt(PRV_SELINDEX, mrIndex);

            settingFragment = (SettingFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[0]);
            textFragment = (TextFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[1]);
            //ticketFragment = (TicketFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[2]);
        }

        //初始化
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(PRV_SELINDEX, mrIndex);
        super.onSaveInstanceState(outState);
    }

    protected void initView() {
        //获得RadioGroup控件
        mRgGroup = (RadioGroup) findViewById(R.id.rg_group);
        //选择设置Fragment
        setTabSelection(show_setting);
        //点击事件
        mRgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_setting://设置
                        setTabSelection(show_setting);
                        break;
                    case R.id.rb_text://文字
                        setTabSelection(show_text);
                        break;
                    case R.id.rb_ticket://小票
                        setTabSelection(show_ticket);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param id 传入的选择的fragment
     */
    private void setTabSelection(int id) {
        if (id == index) {
            return;
        }
        index = id;
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 设置切换动画
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case show_setting://设置的fragment
                mRgGroup.check(R.id.rb_setting);
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.fl_container, settingFragment, FRAGMENT_TAG[index]);
                } else {
                    transaction.show(settingFragment);
                }
                transaction.commit();
                break;
            case show_text://文字的fragment
                mRgGroup.check(R.id.rb_text);
                if (textFragment == null) {
                    textFragment = new TextFragment();
                    transaction.add(R.id.fl_container, textFragment, FRAGMENT_TAG[index]);
                } else {
                    transaction.show(textFragment);
                }
                transaction.commit();
                break;
            case show_ticket://小票的fragment
                mRgGroup.check(R.id.rb_ticket);//设置商城被选中
                if (ticketFragment == null) {
                    ticketFragment = new TicketFragment();
                    transaction.add(R.id.fl_container, ticketFragment, FRAGMENT_TAG[index]);
                } else {
                    transaction.show(ticketFragment);
                }
                transaction.commit();
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
        if (textFragment != null) {
            transaction.hide(textFragment);
        }
        if (ticketFragment != null) {
            transaction.hide(ticketFragment);
        }
    }

}
