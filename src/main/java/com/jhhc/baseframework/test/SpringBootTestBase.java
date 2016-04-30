package com.jhhc.baseframework.test;

import com.google.gson.Gson;
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

    public String doPost4Json(String url, Map<String, Object> param, Object... v) {
        if (!url.startsWith("http")) {
            url = getUrlPrefix() + url;
        }
        MultiValueMap<String, String> map = getMultiValueFromMap(param);
        String json = this.rest.postForObject(url, map, String.class, v);
        // 这里面可能会有错
        Map ret_status = new Gson().fromJson(json, Map.class);
        if (ret_status.containsKey("error")) {
            // 表明出了错
            throw new IllegalStateException(ret_status.get("status") + "," + ret_status.get("error"));
        }
        return json;
    }

    // 发出post，返回json
    public <T> T doPost(String url, Map<String, Object> param, Class<T> cls, Object... v) {
        String json = doPost4Json(url, param, v);
        return new Gson().fromJson(json, cls);
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

    // 发出post返回Map
    public Map doPost4Map(String url, Map param, Object... v) {
        String json = doPost4Json(url, param, v);
        return new Gson().fromJson(json, Map.class);
    }
}
