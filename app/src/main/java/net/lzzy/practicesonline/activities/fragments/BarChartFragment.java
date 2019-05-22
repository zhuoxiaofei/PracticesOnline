package net.lzzy.practicesonline.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 *
 * @author lzzy_gxy
 * @date 2019/5/16
 * Description:
 * 柱形图
 */
public class BarChartFragment extends BaseFragment {
    public static final String GRID_RESULTS = "results";
    private TextView tvChart;
    private ChartFragment.GetGridFragmentListener fragmentListener;
    List<QuestionResult> results;

    @Override
    protected void populate() {
        if (getArguments()!=null){
            results=getArguments().getParcelableArrayList(GRID_RESULTS);
        }
        tvChart = find(R.id.fragment_analysis_tv);
        tvChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentListener.getGridFragment();
            }
        });
        int right=0,extra=0,miss=0,wrong=0;
        for (QuestionResult result:results){
            switch (result.getType()){
                case RIGHT_OPTIONS:
                    right++;
                    break;
                case EXTRA_OPTIONS:
                    extra++;
                    break;
                case MISS_OPTIONS:
                    miss++;
                    break;
                case WRONG_OPTIONS:
                    wrong++;
                    break;
                default:
                    break;
            }
        }
        float[] data={right,extra,miss,wrong};
        String[] horizontalAxis={"正确","多选","少选","错选"};
        BarChartView barChartView=find(R.id.fragment_analysis_bar);
        barChartView.setHorizontalAxis(horizontalAxis);
        barChartView.setDataList(data,results.size());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bar_chart;
    }

    @Override
    public void search(String kw) {

    }
    /**静态工厂传参数*/
    public static BarChartFragment newInstance(List<QuestionResult> results){
        BarChartFragment fragment=new BarChartFragment();
        Bundle args=new Bundle();
        args.putParcelableArrayList(GRID_RESULTS, (ArrayList<? extends Parcelable>) results);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            fragmentListener= (ChartFragment.GetGridFragmentListener) context;
        }catch (CancellationException e){
            throw new CancellationException(context.toString()+"实现接口GetGridFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentListener=null;
    }

}
