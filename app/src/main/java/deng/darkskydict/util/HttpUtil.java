package deng.darkskydict.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

/**
 * 通讯工具类
 *
 * @author Deng
 */
public class HttpUtil {

    private static final String charset = "utf-8";

    public String doGet(String urlString, Map<String, String> parameterMap)
            throws Exception {
        final int connectTimeout = 10000;
        final int socketTimeout = 10000;
        StringBuilder parameterBuffer = new StringBuilder();
        if (parameterMap != null) {
            Iterator<String> iterator = parameterMap.keySet().iterator();
            String key;
            String value;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (parameterMap.get(key) != null) {
                    value = parameterMap.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
            urlString = urlString + "?" + parameterBuffer;
        }

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(socketTimeout);
        InputStream inputStream = null;
        String result;

        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception(
                    "HTTP Request is not success, Response code is "
                            + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            result = changeInputStream(inputStream);

        } catch (Exception e) {
            System.err.println("Error->" + e.toString());
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return result;
    }

    /*
      把输入流转换成字符串
     */
    public static String changeInputStream(InputStream in) throws Exception {
        // TODO 自动生成的方法存根
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        String result = "";
        if (in != null) {
            try {
                while ((len = in.read(data)) != -1) {
                    bAOS.write(data, 0, len);
                }
                result = bAOS.toString(charset);
            } finally {
                bAOS.close();
            }
        }
        return result;
    }
}