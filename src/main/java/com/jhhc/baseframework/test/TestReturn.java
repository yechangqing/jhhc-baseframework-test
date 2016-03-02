package com.jhhc.baseframework.test;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yecq
 */
public class TestReturn {

    private Map<String, String> hv;
    private String body;

    public TestReturn(Map<String, String> header, String body) {
        this.hv = header;
        this.body = body;
    }

    public TestReturn() {
    }

    public void setHv(Map<String, String> hv) {
        this.hv = hv;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public <T> T getObject(Class<T> cls) {
        return new Gson().fromJson(this.body, cls);
    }

    public <T> List<T> getObject4List(Class<T> cls) {
        try {
            List li = new Gson().fromJson(this.body, List.class);
            List<T> ret = new LinkedList();
            Iterator ite = li.iterator();
            while (ite.hasNext()) {
                Object o = ite.next();
                T t = new Gson().fromJson(new Gson().toJson(o), cls);
                ret.add(t);
            }
            return ret;
        } catch (Throwable ex) {
            throw new IllegalArgumentException("json解析出错，确认类型是否正确\n" + ex.getMessage());
        }
    }

    private String getHeader(String key) {
        return this.hv.get(key);
    }

    public String getStatus() {
        return getHeader("status");
    }

    public String getMessage() {
        String ori = getHeader("message");
        try {
            // 解码
            ori = URLDecoder.decode(ori, "utf-8");
        } catch (UnsupportedEncodingException ex) {

        }
        return ori;
    }
}
