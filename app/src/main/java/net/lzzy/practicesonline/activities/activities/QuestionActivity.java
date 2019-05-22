package net.lzzy.practicesonline.activities.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragments.QuestionFragment;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.QuestionFactory;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.models.view.PracticeReult;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;
import net.lzzy.practicesonline.activities.network.PracticeService;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.lzzy.practicesonline.activities.activities.ResultActivity.POSITION;
import static net.lzzy.practicesonline.activities.activities.ResultActivity.RESULT_CODE;

/**
 * @author Administrator
 */
public class QuestionActivity extends AppCompatActivity {
    private static final int WHAT_PRACTICE_DONE = 0;
    private static final int WHAT_EXCEPTION = 1;
    public static final int EXTRA_REQUEST_CODE = 0;

    private String practiceId;
    private int apiId;
    private List<Question> questions;
    private TextView tvView;
    private TextView tvCommit;
    private ViewPager pager;
    private boolean isCommitted=false;
    private TextView tvHint;
    private int pos;
    private LinearLayout container;
    private View[] dost;
    private DownloadHandler handler= new DownloadHandler(this);
    public static final String EXTRA_PRACTICE_ID= "practiceId";
    public static final String EXTRA_RESULTS= "results";

    /**
     * 自定义线程 返回数据的方法
     *
     */
    private static class DownloadHandler extends AbstractStaticHandler<QuestionActivity> {

        DownloadHandler(QuestionActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, QuestionActivity questionActivity) {
            switch (msg.what){
                case WHAT_PRACTICE_DONE:
                    ViewUtils.dismissProgress();
                    questionActivity.isCommitted=true;
                    Toast.makeText(questionActivity, "提交成功", Toast.LENGTH_SHORT).show();
                    UserCookies.getInstance().commitPractice(questionActivity.practiceId);
                    questionActivity.redirect();
                    break;
                case WHAT_EXCEPTION:
                    Toast.makeText(questionActivity,"提交失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);
        AppUtils.addActivity(this);
        retrieveDate();
        initViews();
        initDots();
        setListteners();
        pos = UserCookies.getInstance().getCurrentQuestion(practiceId);
        pager.setCurrentItem(pos);
        refreshDots(pos);
        UserCookies.getInstance().updateReadCount(questions.get(pos).getId().toString());

    }

    private void setListteners() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                refreshDots(position);
                UserCookies.getInstance().updateCurrentQuestion(practiceId,position);
                UserCookies.getInstance().updateReadCount(questions.get(position).getId().toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvCommit.setOnClickListener(v -> commitPractice());
        tvView.setOnClickListener(v -> redirect());
    }
    /**查看成绩*/
    private void redirect() {
        List<QuestionResult> results= UserCookies.getInstance().getResultFromCoolies(questions);
        Intent intent=new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA_PRACTICE_ID,practiceId);
        intent.putParcelableArrayListExtra(EXTRA_RESULTS, (ArrayList<? extends Parcelable>) results);
        startActivityForResult(intent, EXTRA_REQUEST_CODE);
    }

    /**返回处理*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==EXTRA_REQUEST_CODE&&resultCode== RESULT_CODE){
            int position=data.getIntExtra(POSITION,-1);
            pager.setCurrentItem(position);
        }

    }

    String info;
    /**选择mac地址*/
    private void commitPractice() {
        List<QuestionResult> results= UserCookies.getInstance().getResultFromCoolies(questions);
        List<String> macs= AppUtils.getMacAddress();
        String[] items = new String[macs.size()];
        macs.toArray(items);
        info = items[0];
        new AlertDialog.Builder(this)
                .setTitle("选择mac地址")
                //单选setSingleChoiceItems，， //多选setMultiChoiceItems
                .setSingleChoiceItems(items,0,(dialog, which) -> info = items[which])
                .setNeutralButton("取消",null)
                .setPositiveButton("提交",(dialog, which) -> {
                    PracticeReult result = new PracticeReult(results,apiId,"卓宵飞,"+info);
                    //提交方法
                    postResult(result);
                }).show();
    }

    private void postResult(PracticeReult result) {
        ViewUtils.showProgress(this,"正在提交成绩");
            AppUtils.getExecutor().execute(()->{
                try {
                    int aa= PracticeService.postResult(result);
                    if (aa>=200 && aa<=220){
                        handler.sendMessage(handler.obtainMessage(WHAT_PRACTICE_DONE,aa));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
                }
            });
    }

    private void initDots() {
        //region  导航栏
        int count=questions.size();
        dost = new View[count];
        container = findViewById(R.id.activity_question_dots);
        container.removeAllViews();
        int px= ViewUtils.dp2px(16,this);

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(px,px);
        px= ViewUtils.dp2px(5,this);
        params.setMargins(px,px,px,px);
        for (int i=0;i<count;i++){
            TextView tvDot=new TextView(this);
            tvDot.setLayoutParams(params);
            tvDot.setBackgroundResource(R.drawable.dot_style);
            tvDot.setTag(i);
            //todo:tvDot添加点击监听
            tvDot.setOnClickListener(v -> pager.setCurrentItem((Integer) v.getTag()));
            container.addView(tvDot);
            dost[i]=tvDot;
        }
        //endregion
    }

    private void refreshDots(int pos){
        for (int i= 0;i<dost.length;i++){
            int drawable=i==pos? R.drawable.dot_fill_style: R.drawable.dot_style;
            dost[i].setBackgroundResource(drawable);
        }
    }

    private void initViews() {
        tvView = findViewById(R.id.activity_question_tv_view);
        tvCommit = findViewById(R.id.activity_question_tv_commit);
        tvHint = findViewById(R.id.activity_question_tv_hint);
        pager = findViewById(R.id.activity_question_pager);
        if (isCommitted){
            tvCommit.setVisibility(View.GONE);
            tvView.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
        }else {
            tvCommit.setVisibility(View.VISIBLE);
            tvView.setVisibility(View.GONE);
            tvHint.setVisibility(View.GONE);
        }
        FragmentStatePagerAdapter adapter=new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Question question=questions.get(position);
                return QuestionFragment.newInstance(question.getId().toString(),position,isCommitted);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        };
        pager.setAdapter(adapter);
    }

    private void retrieveDate() {
        practiceId = getIntent().getStringExtra(PracticesActivity.EXTRA_PRACTICE_ID);
        apiId = getIntent().getIntExtra(PracticesActivity.EXTRA_API_ID,-1);
        questions= QuestionFactory.getInstance().getByPractices(practiceId);
        isCommitted= UserCookies.getInstance().isPracticeCommitted(practiceId);
        if (apiId<0||questions==null||questions.size()==0){
            Toast.makeText(this, "no questions", Toast.LENGTH_SHORT).show();
            finish();
        }

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
}
