package net.lzzy.practicesonline.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 *
 * @author lzzy_gxy
 * @date 2019/5/13
 * Description:
 */
public class GridFragment extends BaseFragment {

    public static final String GRID_RESULTS = "results";
    private GridView gridView;
    private List<QuestionResult> results;
    private TextView tvGrid;
    private BaseAdapter adapter;
    private OnGridListener listener;
    private GetChartFragmentListener fragmentListener;
   @Override
    protected void populate() {
        gridView = find(R.id.fragment_grid_grid_view);
        tvGrid = find(R.id.fragment_grid_tv);
        if (getArguments()!=null){
            results=getArguments().getParcelableArrayList(GRID_RESULTS);
        }
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return results.size();
            }

            @Override
            public Object getItem(int position) {
                return results.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                   convertView= LayoutInflater.from(getContext()).inflate(R.layout.grid_layout,null);
                }
                TextView textView = convertView.findViewById(R.id.grid_layout_tv);
                QuestionResult result=results.get(position);
                if (result.isRight()){
                    textView.setBackgroundResource(R.drawable.grid_green);
                }else {
                    textView.setBackgroundResource(R.drawable.grid_accent);
                }
                textView.setText(position+1+"");
                return convertView;
            }
        };
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> listener.onGrid(position));
        tvGrid.setOnClickListener(v -> fragmentListener.getChartFragment());

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_grid;
    }

    @Override
    public void search(String kw) {

    }
    /**静态工厂传参数*/
    public static GridFragment newInstance(List<QuestionResult> results){
        GridFragment fragment=new GridFragment();
        Bundle args=new Bundle();
        args.putParcelableArrayList(GRID_RESULTS, (ArrayList<? extends Parcelable>) results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (OnGridListener) context;
            fragmentListener= (GetChartFragmentListener) context;
        }catch (CancellationException e){
            throw  new CancellationException(context.toString()+"实现接口OnGridListener&&GetChartFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
        fragmentListener=null;
    }

    public interface OnGridListener{
        /**跳转回question视图
         *@param position rule position
         */
        void onGrid(int position);
    }

    public interface GetChartFragmentListener{
        /**跳转到ChartFragment
         */
        void getChartFragment();
    }
}
