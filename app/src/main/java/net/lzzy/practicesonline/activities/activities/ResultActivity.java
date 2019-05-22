package net.lzzy.practicesonline.activities.activities;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragments.ChartFragment;
import net.lzzy.practicesonline.activities.fragments.GridFragment;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;

import java.util.List;

/**
 *
 * @author lzzy_gxy
 * @date 2019/5/13
 * Description:
 */
public class ResultActivity extends BaseActivity implements GridFragment.OnGridListener ,
GridFragment.GetChartFragmentListener,ChartFragment.GetGridFragmentListener {

    public static final String POSITION = "position";
    public static final int RESULT_CODE = 1;
    private List<QuestionResult> results;

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
        results = getIntent().getParcelableArrayListExtra(QuestionActivity.EXTRA_RESULTS);
        return GridFragment.newInstance(results);
    }
/**点击序号跳转到相对应的题目*/
    @Override
    public void onGrid(int position) {
        Intent intent=new Intent();
        intent.putExtra(POSITION,position);
        setResult(RESULT_CODE,intent);
        finish();
    }
/**点击右下角的"图"跳转到ChartFragment图形视图*/
    @Override
    public void getChartFragment() {
        getManager().beginTransaction().replace(R.id.activity_result_container,
                ChartFragment.newInstance(results)).commit();
    }
    /**点击右下角的"表"跳转到GridFragment表型视图*/
    @Override
    public void getGridFragment() {
        getManager().beginTransaction().replace(R.id.activity_result_container,
                GridFragment.newInstance(results)).commit();
    }
}
