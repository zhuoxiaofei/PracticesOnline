package net.lzzy.practicesonline.activities.activities;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.constants.ApiConstants;
import net.lzzy.practicesonline.activities.fragments.SplashFragment;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


public class SplashActivity extends BaseActivity implements SplashFragment.OnSpalshFinishedListener {
    public static final int WHAT_COUNTING = 0;
    public static final int WHAT_EXCEPTION = 1;
    public static final int WHAT_COUNT_DONE = 2;
    public static final int WHAT_SERVER_OFF = 3;
    private int seconds=10;

    private CountHandler handler=new CountHandler(this);
    private TextView tvTime;
    private boolean isServerOn = true;

    /**接收处理消息*/
    private static class CountHandler extends AbstractStaticHandler<SplashActivity> {

        public CountHandler(SplashActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SplashActivity splashActivity) {
            switch (msg.what){
                case WHAT_COUNTING:
                    String text =msg.obj+"秒";
                    splashActivity.tvTime.setText(text);
                    break;
                case WHAT_EXCEPTION:
                   new AlertDialog.Builder(splashActivity)
                           .setMessage(msg.obj.toString())
                           .setPositiveButton("继续", (dialog, which) -> splashActivity.gotoMain())
                           .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                           .show();
                    break;
                case WHAT_COUNT_DONE:
                    if (splashActivity.isServerOn){
                        splashActivity.gotoMain();
                    }
                    break;
                case WHAT_SERVER_OFF:
                    Activity context= AppUtils.getRunningActivity();
                    new AlertDialog.Builder(Objects.requireNonNull(context))
                            .setMessage("服务器没有响应，是否继续?\n"+msg.obj)
                            .setPositiveButton("确定",(dialog, which) -> {
                                if (context instanceof SplashActivity){
                                    ((SplashActivity) context).gotoMain();
                                }
                            })
                            .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                            .setNeutralButton("设置",(dialog, which) -> {
                                ViewUtils.gotoSetting(context);
                            })
                            .show();
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTime = findViewById(R.id.activity_splash_tv_count_down);
        //region 弹框提示没有网络连接
        if (!AppUtils.isNetworkAvailable()){
            new AlertDialog.Builder(this)
                    .setMessage("当前网络不可用，是否继续")
                    .setPositiveButton("退出", (dialog, which) -> AppUtils.exit())
                    .setNegativeButton("确定", (dialog, which) -> gotoMain()).show();
        }else {
            ThreadPoolExecutor executor= AppUtils.getExecutor();
            executor.execute(this::CountDown);
            executor.execute(this::detectServerStatus);
        }
        //endregion

    }
/**倒计时线程*/
    private void CountDown() {
        while (seconds>=0){
            handler.sendMessage(handler.obtainMessage(WHAT_COUNTING,seconds));
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
            }
            seconds--;
        }
        handler.sendEmptyMessage(WHAT_COUNT_DONE);
    }

/**服务器探测线程*/
    private void detectServerStatus(){
        try {
            AppUtils.tryConnectServer(ApiConstants.URL_API);
        } catch (IOException e) {
            isServerOn=false;
           handler.sendMessage(handler.obtainMessage(WHAT_SERVER_OFF,e.getMessage()));
        }
    }


    public void gotoMain(){
        startActivity(new Intent(this, PracticesActivity.class));
        finish();
    }
    @Override
    public void cancelCount() {
        seconds=0;
    }
/**加载布局*/
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }
/**加载容器*/
    @Override
    protected int getContainerId() {
        return R.id.fragment_splash_container;
    }
/**托管Fragment*/
    @Override
    protected Fragment createFragment() {
        return new SplashFragment();
    }


    /**弹框询问是否退出*/
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("要退出吗？")
                .setPositiveButton("确定", (dialog, which) -> AppUtils.exit()).show();
    }


}
