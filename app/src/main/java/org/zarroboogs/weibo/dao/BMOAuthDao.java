
package org.zarroboogs.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

public class BMOAuthDao {

    public String[] login() throws WeiboException {
        String url = WeiBoURLs.OAUTH2_ACCESS_TOKEN;
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        map.put("grant_type", grant_type);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        if ((jsonData != null) && (jsonData.contains("{"))) {
            try {
                JSONObject localJSONObject = new JSONObject(jsonData);
                String[] result = new String[2];
                result[0] = localJSONObject.optString("access_token");
                result[1] = localJSONObject.optString("expires_in");
                return result;
                // setUserId(localJSONObject.optLong("uid"));

            } catch (JSONException localJSONException) {

            }

        }
        return null;

    }

    public BMOAuthDao(String username, String password, String key, String secret) {
        this.username = username;
        this.password = password;
        this.client_id = key;
        this.client_secret = secret;
    }

    private String username;
    private String password;
    private String client_id;
    private String client_secret;
    private String grant_type = "password";

}
