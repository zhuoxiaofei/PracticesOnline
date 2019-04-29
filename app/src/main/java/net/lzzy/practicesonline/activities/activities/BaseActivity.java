package net.lzzy.practicesonline.activities.activities;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.practicesonline.activities.utils.AppUtils;

/**
 * @author lzzy_gxy on 2019/4/11.
 * Description:
 */
public  abstract class BaseActivity extends AppCompatActivity {
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        AppUtils.addActivity(this);
        FragmentManager manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(getContainerId());
        if(fragment == null){
            fragment = createFragment();
            manager.beginTransaction().add(getContainerId(),fragment).commit();
        }
    }

    protected Fragment getFragment(){
        return fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }

    /**
     *Activity的布局文件id
     * @return 布局资源猫id
     */
    protected abstract int getLayoutRes();
    /**
     * */
    protected abstract int getContainerId();
    /**
     * 生成托管的Fragment对象
     * @return fragment
     */
    protected abstract Fragment createFragment();
}
