package net.lzzy.practicesonline.activities.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.FavoriteFactory;
import net.lzzy.practicesonline.activities.models.Option;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.QuestionFactory;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.models.view.QuestionType;

import java.util.List;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/26
 * Description:
 */
public class QuestionFragment extends BaseFragment {
    public static final String ARGS_QUESTION_ID = "argsQuestionId";
    public static final String ARGS_POS = "argsPos";
    public static final String ARGS_ISCOMMITTED = "argsIscommitted";
    private Question question;
    private int pos;
    private boolean iscommitted;
    private boolean isMulti=false;
    private TextView tvType;
    private ImageButton imageButton;
    private RadioGroup group;
    private TextView tvGray;

    public static QuestionFragment newInstance(String questionId, int pos, boolean iscommitted){
        QuestionFragment fragment=new QuestionFragment();
        Bundle args=new Bundle();
        args.putString(ARGS_QUESTION_ID,questionId);
        args.putInt(ARGS_POS,pos);
        args.putBoolean(ARGS_ISCOMMITTED,iscommitted);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            pos=getArguments().getInt(ARGS_POS);
            iscommitted=getArguments().getBoolean(ARGS_ISCOMMITTED);
            question= QuestionFactory.getInstance().getById(getArguments().getString(ARGS_QUESTION_ID));
        }
    }

    @Override
    protected void populate() {
        //题目类型
        tvType = find(R.id.fragment_question_tv_type);
        //收藏标志
        imageButton = find(R.id.fragment_question_img_favorite);
        //题目
        tvGray = find(R.id.fragment_question_tv_content);
        //选项
        group = find(R.id.fragment_question_option_content);
        if (iscommitted){
            group.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                    .setMessage(question.getAnalysis())
            .show());
        }

        //显示题目类型与收藏标志
        displayOption();
        //显示题目和选项
        generateOption();

    }
    /**显示题目和选项*/
    private void generateOption() {
        isMulti=question.getType()== QuestionType.MULTI_CHOICE;
        String qAnalysis=question.getAnalysis();
        tvGray.setText(qAnalysis);
        List<Option> options=question.getOptions();
        for (Option option:options){
            CompoundButton button=isMulti? new CheckBox(getContext()):new RadioButton(getContext());
            String content=option.getLabel()+"."+option.getContent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置选项控件颜色
                button.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            }
            button.setText(content);
            button.setEnabled(!iscommitted);
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //保存选中的选项
                    UserCookies.getInstance().changeOptionState(option,isChecked,isMulti);
                }
            });
            //设置选项字体大小和颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                button.setTextAppearance(R.style.styleText);
            }
            group.addView(button);
            //region读取选中的选项
            boolean shouldCheck= UserCookies.getInstance().isOptionSelected(option);
            if (isMulti){
                button.setChecked(shouldCheck);
            }else if (shouldCheck){
                group.check(button.getId());
            }
            //endregion
            if (iscommitted&&option.isAnswer()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    button.setTextColor(getResources().getColor(R.color.colorGreen,null));
                }else {
                    button.setTextColor(getResources().getColor(R.color.colorGreen));
                }
            }
        }
    }
    /**显示题目类型与收藏标志*/
    private void displayOption() {
        int label=pos+1;
        String qType=label+"."+question.getType().toString();
        tvType.setText(qType);
        int starId= FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString())?
                android.R.drawable.star_on : android.R.drawable.star_off;
        imageButton.setImageResource(starId);

        //点击收藏，取消收藏
        imageButton.setOnClickListener(v -> switchStar());
    }
    /**点击收藏，取消收藏*/
    private void switchStar() {
        FavoriteFactory factory= FavoriteFactory.getInstance();
        if (factory.isQuestionStarred(question.getId().toString())){
            factory.cancelStarQuestion(question.getId());
            imageButton.setImageResource(android.R.drawable.star_off);
        }else {
            factory.starQuestion(question.getId());
            imageButton.setImageResource(android.R.drawable.star_on);
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.question_fragment;
    }

    @Override
    public void search(String kw) {

    }
}
