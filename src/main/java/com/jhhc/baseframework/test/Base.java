package com.jhhc.baseframework.test;

import java.sql.SQLException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yecq
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class Base {

    @Autowired
    protected DbUnitPrepare prepare;

    // 测试异常用的，必须为public
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void before() throws DatabaseUnitException, SQLException {
        // 初始化数据库
        DatabaseOperation.CLEAN_INSERT.execute(prepare.getIDatabaseConnection(), prepare.getIDataSet());

    }

    @After
    public void after() throws DatabaseUnitException, SQLException {

        DatabaseOperation.DELETE_ALL.execute(prepare.getIDatabaseConnection(), prepare.getIDataSet());

    }
}
