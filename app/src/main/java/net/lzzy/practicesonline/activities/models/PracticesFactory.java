package net.lzzy.practicesonline.activities.models;

import net.lzzy.practicesonline.activities.constants.DbConstants;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/17
 * Description:
 */
public class PracticesFactory {
    private static final PracticesFactory OUR_INSTANCE=new PracticesFactory();
    private static SqlRepository<Practice> repository;

    public static PracticesFactory getInstance(){
        return OUR_INSTANCE;
    }

    private PracticesFactory(){
        repository=new SqlRepository<>(AppUtils.getContext(),Practice.class, DbConstants.packager);
    }

    public List<Practice> get(){
        return repository.get();
    }

    public Practice getById(String id){
        return repository.getById(id);
    }

    public List<Practice> searchPractices(String kw){
      try {
          return repository.getByKeyword(kw,
                  new String[]{Practice.COL_NAME,Practice.COL_OUTLINES},false);
      }catch (IllegalAccessException|InstantiationException e){
          e.printStackTrace();
          return new ArrayList<>();
        }
    }

    public boolean addPractices(Practice practice){
            if (!isPracticeInDb(practice)) {
                repository.insert(practice);
                return true;
            }

            return false;

    }

    private boolean isPracticeInDb(Practice practice){
        try {
            return repository.getByKeyword(String.valueOf(practice.getApiId()),
                    new String[]{Practice.COL_API_ID},true).size()>0;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return true;
        }
    }
/***/
    public UUID getPractices(int apiId){
        try {
            List<Practice> practices=repository.getByKeyword(String.valueOf(apiId),
                    new String[]{Practice.COL_API_ID},true);
            if (practices.size()>0){
                return practices.get(0).getId();
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**修改isDownloaded状态*/
    private void setPracticesDown(String id){
        Practice practice=getById(id);
        if (practice!=null){
            practice.setDownloaded(true);
            repository.update(practice);
        }
    }
/**保存题目*/
    public void saveQuestions(List<Question> questions, UUID practiceId){
        for (Question q:questions){
            QuestionFactory.getInstance().insert(q);
        }
        //设置状态
        setPracticesDown(practiceId.toString());
    }

/**删除*/
    public boolean deletePracticesAndRelated(Practice practice){
        try {
            List<String> sqlActions= new ArrayList<>();
            sqlActions.add(repository.getDeleteString(practice));
            QuestionFactory factory= QuestionFactory.getInstance();
            List<Question> questions=factory.getByPractices(practice.getId().toString());
            if (questions.size()>0){
                for (Question q:questions){
                    sqlActions.addAll(factory.getDeleteString(q));
                }
            }
            repository.exeSqls(sqlActions);
            if (!isPracticeInDb(practice)) {
                //todo:清除Cookies
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
