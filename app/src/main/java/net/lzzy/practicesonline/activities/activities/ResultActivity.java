package net.lzzy.practicesonline.activities.activities;

import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;

/**
 * @author lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ResultActivity extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_result;
    }

    @Override
    protected int getContainerId() {
        return R.id.activity_result_container;
    }

    @Override
    protected Fragment createFragment() {
        return null;
    }
}
