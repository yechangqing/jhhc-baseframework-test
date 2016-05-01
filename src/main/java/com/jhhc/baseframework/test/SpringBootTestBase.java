package com.jhhc.baseframework.test;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 增加对Spring boot微服务的测试
 *
 * @author yecq
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest
public abstract class SpringBootTestBase {

    @Autowired
    protected DbUnitPrepare prepare;

    protected RestTemplate rest;

    @Autowired
    protected SqlOperator sql;

    protected SpringBootTestBase() {
        this.rest = new TestRestTemplate();
    }

    protected abstract int getPort();

    protected String getUrlPrefix() {
        return "http://localhost:" + getPort();
    }

    @Before
    public void before() throws DatabaseUnitException, SQLException {
        // 初始化数据库
        DatabaseOperation.CLEAN_INSERT.execute(prepare.getIDatabaseConnection(), prepare.getIDataSet());
    }

    @After
    public void after() throws DatabaseUnitException, SQLException {
        DatabaseOperation.DELETE_ALL.execute(prepare.getIDatabaseConnection(), prepare.getIDataSet());
    }

    public Map get4Map(String url, Map<String, Object> param, Object... v) {
        if (!url.startsWith("http")) {
            url = getUrlPrefix() + url;
        }

        if (param != null && !param.isEmpty()) {
            url += "?" + condition2ParamEncoded(param);
        }
        Map ret = this.rest.getForObject(url, Map.class, v);
        // 这里面可能会有错
        if (ret.containsKey("error")) {
            throw new IllegalStateException(ret.get("status") + ", " + ret.get("error") + ", " + ret.get("message"));
        }
        return ret;
    }

    public Map get4Map(String url, Object... v) {
        return get4Map(url, null, v);
    }

    public String get4Json(String url, Map<String, Object> param, Object... v) {
        Map map = get4Map(url, param, v);
        return new Gson().toJson(map);
    }

    public String get4Json(String url, Object... v) {
        return get4Json(url, null, v);
    }

//    private String condition2Param(Map<String, Object> condition) {
//        String ret = "";
//        Iterator<Entry<String, Object>> ite = condition.entrySet().iterator();
//        while (ite.hasNext()) {
//            Entry<String, Object> ent = ite.next();
//            ret += ent.getKey().trim() + "=" + ent.getValue() + "&";
//        }
//        return ret.substring(0, ret.length() - 1);
//    }
    private String condition2ParamEncoded(Map<String, Object> condition) {
        String ret = "";
        Iterator<Entry<String, Object>> ite = condition.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, Object> ent = ite.next();
            String ori = (String) ent.getValue();
            try {
                ori = URLEncoder.encode(ori + "", "utf-8");
            } catch (UnsupportedEncodingException ex) {

            }
            ret += ent.getKey().trim() + "=" + ori + "&";
        }
        return ret.substring(0, ret.length() - 1);
    }

    public Map post4Map(String url, Map<String, Object> param, Object... v) {
        if (!url.startsWith("http")) {
            url = getUrlPrefix() + url;
        }
        MultiValueMap<String, String> map = getMultiValueFromMap(param);
        Map ret = this.rest.postForObject(url, map, Map.class, v);
        // 这里面可能会有错
        if (ret.containsKey("error")) {
            throw new IllegalStateException(ret.get("status") + ", " + ret.get("error") + ", " + ret.get("message"));
        }
        return ret;
    }

    public Map post4Map(String url, Object... v) {
        return post4Map(url, null, v);
    }

    private MultiValueMap<String, String> getMultiValueFromMap(Map<String, Object> param) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        if (param == null) {
            return map;
        }

        Iterator<Entry<String, Object>> ite = param.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, Object> ent = ite.next();
            map.add(ent.getKey(), ent.getValue() + "");
        }
        return map;
    }

    // put，暂由post来实现，不返回数据，只返回状态
    public void put(String url, Map<String, Object> param, Object... v) {
        if (!url.startsWith("http")) {
            url = getUrlPrefix() + url;
        }
        MultiValueMap<String, String> map = getMultiValueFromMap4PD(param, "put");
        Map ret = this.rest.postForObject(url, map, Map.class, v);
        // 这里面可能会有错
        if (ret.containsKey("error")) {
            throw new IllegalStateException(ret.get("status") + ", " + ret.get("error") + ", " + ret.get("message"));
        }
    }

    private MultiValueMap<String, String> getMultiValueFromMap4PD(Map<String, Object> param, String method) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        map.add("_method", method);
        if (param == null) {
            return map;
        }

        Iterator<Entry<String, Object>> ite = param.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, Object> ent = ite.next();
            map.add(ent.getKey(), ent.getValue() + "");
        }
        return map;
    }

    // delete，也由post来实现
    public void delete(String url, Object... v) {
        if (!url.startsWith("http")) {
            url = getUrlPrefix() + url;
        }
        MultiValueMap<String, String> map = getMultiValueFromMap4PD(null, "delete");
        Map ret = this.rest.postForObject(url, map, Map.class, v);
        // 这里面可能会有错
        if (ret.containsKey("error")) {
            throw new IllegalStateException(ret.get("status") + ", " + ret.get("error") + ", " + ret.get("message"));
        }
    }
}
