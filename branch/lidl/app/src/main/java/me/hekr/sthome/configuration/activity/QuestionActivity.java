package me.hekr.sthome.configuration.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;

/**
 * Created by gc-0001 on 2017/5/28.
 */

public class QuestionActivity extends TopbarSuperActivity implements QuestionAdapter.OnRecyclerViewItemClickListener{
    private final String TAG = "QuestionActivity";
    private RecyclerView recyclerView;
    private QuestionAdapter questionAdapter;
    private List<QuestionBean> questionBeenlist;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreateInit() {
        initdata();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_question;
    }


    private void initView(){
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.help), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);
        recyclerView = (RecyclerView)findViewById(R.id.questionlist);
        mLayoutManager=new GridLayoutManager(this,1, GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        questionAdapter = new QuestionAdapter(this,questionBeenlist);
        recyclerView.setAdapter(questionAdapter);
        questionAdapter.setOnItemClickListener(this);
    }

    private void initdata(){
        questionBeenlist = new ArrayList<QuestionBean>();
        String[] ques = getResources().getStringArray(R.array.question);
        String[] answer = getResources().getStringArray(R.array.question_detail);
        for(int i=0;i<ques.length;i++){
            QuestionBean questionBean = new QuestionBean();

            if(i==0){
                questionBean.setIsopen(true);
            }else{
                questionBean.setIsopen(false);
            }
            questionBean.setAnswer(answer[i]);
            questionBean.setQuestion(ques[i]);
            questionBeenlist.add(questionBean);

        }

    }

    @Override
    public void onItemClick(View view, int position) {
          if(questionBeenlist.get(position).isopen()){
              questionBeenlist.get(position).setIsopen(false);
          }else{
              questionBeenlist.get(position).setIsopen(true);
          }
        questionAdapter.Refresh(questionBeenlist);
    }
}
