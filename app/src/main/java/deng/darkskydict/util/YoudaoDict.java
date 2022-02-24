package deng.darkskydict.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class YoudaoDict {

    private static final String URL = "http://fanyi.youdao.com/openapi.do";
    private static final String KEY = "977912839";
    private static final String KEYFROM = "Darksky321";

    public static String lookUpAWord(String word) {
        String ret = "";
        String str = URL + "?keyfrom=" + KEYFROM + "&key=" + KEY
                + "&type=data&doctype=json&version=1.1&q=";
        HttpUtil util = new HttpUtil();
        try {
            str = str + URLEncoder.encode(word, "utf-8");
            ret = util.doGet(str, null);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            return e.toString();
        }
        return ret;
    }

    public static String handleJson(Object object) {
        StringBuffer sb = new StringBuffer();
        Object obj;
        JSONObject jo;
        JSONArray ja;
        String str;
        String key;
        try {
            if (object instanceof JSONObject) {
                jo = (JSONObject) object;
                Iterator<?> it = jo.keys();
                Iterator<?> keys = sortKeys(it); // 把basic的内容提到最前面
                while (keys.hasNext()) {
                    key = (String) keys.next();
                    sb.append(key);
                    sb.append(":\n");
                    obj = jo.get(key);
                    sb.append(handleJson(obj));
                    sb.append("\n");
                }
            } else if (object instanceof JSONArray) {
                ja = (JSONArray) object;
                for (int i = 0; i < ja.length(); ++i) {
                    obj = ja.get(i);
                    sb.append(handleJson(obj));
                }
            } else if (object instanceof String) {
                str = (String) object;
                sb.append(str);
                sb.append("\n");
            } else {
                sb.append(object.toString());
                sb.append("\n");
            }
        } catch (Exception e) {
            // TODO: handle exception
            return e.toString();
        }
        return sb.toString();
    }

    public static Iterator<String> sortKeys(Iterator<?> keys) {
        List<String> sortedKeys = new ArrayList<String>();
        while (keys.hasNext()) {
            sortedKeys.add((String) keys.next());
        }
        // System.out.println(sortedKeys);
        if (sortedKeys.contains("basic")) {
            String tmp;
            int iBasic = sortedKeys.indexOf("basic");
            tmp = sortedKeys.get(0);
            sortedKeys.set(0, "basic");
            sortedKeys.set(iBasic, tmp);
        }
        // System.out.println(sortedKeys);
        return sortedKeys.iterator();
    }
}