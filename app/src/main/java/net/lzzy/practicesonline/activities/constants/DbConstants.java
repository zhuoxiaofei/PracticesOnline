package net.lzzy.practicesonline.activities.constants;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.DbPackager;

/**
 * Created by lzzy_gxy on 2019/3/11.
 * Description:
 */
public final class DbConstants {
    private DbConstants(){}
    /**数据库名称*/
    private static final String DB_NAME="practices.db";
    /**版本号*/
    private static final int DB_VERSION=1;
/**打包数据库信息*/
    public static DbPackager packager;

    static {
        packager= DbPackager.getInstance(AppUtils.getContext()
                ,DB_NAME,DB_VERSION, R.raw.models);
    }


}
