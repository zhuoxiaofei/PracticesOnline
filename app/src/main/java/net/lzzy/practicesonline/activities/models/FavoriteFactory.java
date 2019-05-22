package net.lzzy.practicesonline.activities.models;

import net.lzzy.practicesonline.activities.constants.DbConstants;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/17
 * Description:
 */
public class FavoriteFactory {
    private static final FavoriteFactory OUR_INSTANCE = new FavoriteFactory();
    private static SqlRepository<Favorite> repository;

    public static FavoriteFactory getInstance() {
        return OUR_INSTANCE;
    }

    private FavoriteFactory() {
        repository=new SqlRepository<>(AppUtils.getContext(), Favorite.class, DbConstants.packager);
    }

    private Favorite getFavoriteByQuestion(String questionId){
        try {
            List<Favorite> favorites=repository.getByKeyword(questionId,new String[]{Favorite.COL_QUESTION_ID},true);
            if (favorites.size()>0){
                return favorites.get(0);
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isQuestionStarred(String questionId){
        try {
            List<Favorite> favorites=repository.getByKeyword(questionId,
                    new String[]{Favorite.COL_QUESTION_ID},true);
            return favorites.size()>0;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return false;
        }

    }
/**收藏题目*/
    public void starQuestion(UUID questionId){
        Favorite favorite=getFavoriteByQuestion(questionId.toString());
        if (favorite==null){
            favorite=new Favorite();
            favorite.setQuestionId(questionId);
            repository.insert(favorite);
        }
    }
/**取消收藏*/
    public void cancelStarQuestion(UUID questionId){
        Favorite favorite=getFavoriteByQuestion(questionId.toString());
        if (favorite!=null){
            repository.delete(favorite);
        }
    }

    protected  String getDeleteString(String questionId){
        Favorite favorite=getFavoriteByQuestion(questionId);
        return favorite==null?null:repository.getDeleteString(favorite);
    }

    public List<Question> getAllFavorites(List<Question> questions){
        return null;
    }
}
