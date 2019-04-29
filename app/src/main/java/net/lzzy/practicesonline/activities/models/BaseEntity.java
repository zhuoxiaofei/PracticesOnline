package net.lzzy.practicesonline.activities.models;

import net.lzzy.sqllib.AsPrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 *
 * @author lzzy_gxy
 * @date 2019/3/11
 * Description:
 */
public abstract class BaseEntity {
    @AsPrimaryKey
    UUID id;
    BaseEntity(){
        id = UUID.randomUUID();
    }

    public Object getIdentityValue() {
        return id;
    }

    public UUID getId() {
        return id;
    }

    public abstract JSONObject toJson() throws JSONException;

    public abstract void fromJson(JSONObject json) throws JSONException;
}
