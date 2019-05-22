package net.lzzy.practicesonline.activities.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragments.ChartFragment;
import net.lzzy.practicesonline.activities.fragments.GridFragment;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;

import java.util.List;

/**
 * @author lzzy_gxy
 * @date 2019/5/13
 * Description:
 */
public class ResultActivity extends BaseActivity implements GridFragment.OnGridListener,
        GridFragment.GetChartFragmentListener, ChartFragment.GetGridFragmentListener {

    public static final String POSITION = "position";
    public static final int RESULT_CODE = 1;
    public static final String QUESTION = "question";
    public static final int RESULT_CODE_TWO = 2;
    private List<QuestionResult> results;
    private String question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = getIntent().getStringExtra(QuestionActivity.EXTRA_PRACTICE_ID);
    }

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

    /**
     * 点击序号跳转到相对应的题目
     */
    @Override
    public void onGrid(int position) {
        Intent intent = new Intent();
        intent.putExtra(POSITION, position);
        setResult(RESULT_CODE, intent);
        finish();
    }

    /**
     * 点击右下角的"图"跳转到ChartFragment图形视图
     */
    @Override
    public void getChartFragment() {
        getManager().beginTransaction().replace(R.id.activity_result_container,
                ChartFragment.newInstance(results)).commit();
    }

    /**
     * 点击右下角的"表"跳转到GridFragment表型视图
     */
    @Override
    public void getGridFragment() {
        getManager().beginTransaction().replace(R.id.activity_result_container,
                GridFragment.newInstance(results)).commit();
    }

    /**
     * 返回
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("返回到哪里？")
                .setNeutralButton("返回题目", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ResultActivity.this, QuestionActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton("章节列表", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ResultActivity.this, PracticesActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setPositiveButton("查看收藏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(QUESTION, question);
                        setResult(RESULT_CODE_TWO, intent);
                        finish();
                    }
                })
                .show();
    }
}
