package com.jhhc.baseframework.test;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * restful类型的测试基类
 *
 * @author yecq
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "file:src/test/webapp")        // 默认是 file: src/main/webapp 下
@ContextConfiguration(locations = {"file:src/test/webapp/WEB-INF/applicationContext.xml", "file:src/test/webapp/WEB-INF/dispatcher-servlet.xml"})
public class IntegrateRestfulBase {

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected DbUnitPrepare prepare;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    protected MockMvc mockMvc;

    @Before
    public void before() throws DatabaseUnitException, SQLException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        DatabaseOperation.CLEAN_INSERT.execute(prepare.getIDatabaseConnection(), prepare.getIDataSet());

        // 针对Client类的特殊需求，每次重新读一次
//        Root.getInstance().getBean(Client.class).init();
    }

    @After
    public void after() throws DatabaseUnitException, SQLException {
        DatabaseOperation.DELETE_ALL.execute(prepare.getIDatabaseConnection(), prepare.getIDataSet());
    }

    //总的方法
    protected TestReturn executeHttp(String method, String url, Map<String, Object> param, HttpStatus status) {
        if (method == null || method.trim().equals("") || url == null || status == null) {
            throw new IllegalArgumentException("参数错误");
        }
        method = method.trim();
        MockHttpServletRequestBuilder builder = null;
        try {
            if (method.equalsIgnoreCase("get")) {
                builder = get(url);
            } else if (method.equalsIgnoreCase("post")) {
                builder = post(url);
            } else if (method.equalsIgnoreCase("put")) {
                builder = put(url);
            } else if (method.equalsIgnoreCase("delete")) {
                builder = delete(url);
            } else {
                throw new IllegalArgumentException("只支持get、post、put、delete方法");
            }

            if (param != null) {
                Iterator<Entry<String, Object>> ite = param.entrySet().iterator();
                while (ite.hasNext()) {
                    Entry<String, Object> ent = ite.next();
                    // 如果String也转成json，会额外加一对引号
                    String pp = null;
                    Object v = ent.getValue();
                    if (v.getClass().equals(String.class)) {
                        pp = (String) v;
                    } else {
                        pp = new Gson().toJson(v);
                    }
                    builder.param(ent.getKey(), pp);
                }
            }

            ResultActions actions = this.mockMvc.perform(builder.accept("application/json;charset=utf-8"));
            if (null != status) {
                switch (status) {
                    case OK:
                        actions.andExpect(status().isOk());
                        break;
                    case CREATED:
                        actions.andExpect(status().isCreated());
                        break;
                    default:
                        throw new IllegalArgumentException("暂不支持状态" + status + "(" + status.name() + ")");
                }
            }
            MockHttpServletResponse response = actions.andReturn().getResponse();
            Object[] names = response.getHeaderNames().toArray();
            Map<String, String> map = new HashMap();
            for (int i = 0; i < names.length; i++) {
                String key = (String) names[i];
                map.put(key, response.getHeader(key));
            }
            return new TestReturn(map, response.getContentAsString());
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected TestReturn doGet(String url, Map<String, Object> param) {
        return executeHttp("get", url, param, HttpStatus.OK);
    }

    protected TestReturn doGet(String url) {
        return doGet(url, null);
    }

    protected TestReturn doPost(String url, Map<String, Object> param) {
        return executeHttp("post", url, param, HttpStatus.OK);
    }

    protected TestReturn doPost(String url) {
        return doPost(url, null);
    }

    protected TestReturn doPut(String url, Map<String, Object> param) {
        return executeHttp("put", url, param, HttpStatus.OK);
    }

    protected TestReturn doPut(String url) {
        return doPut(url, null);
    }

    protected TestReturn doDelete(String url, Map<String, Object> param) {
        return executeHttp("delete", url, param, HttpStatus.OK);
    }

    protected TestReturn doDelete(String url) {
        return doDelete(url, null);
    }
}
