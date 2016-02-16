package com.jhhc.baseframework.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author yecq
 */
@Component
public class DbUnitPrepare {

    private IDatabaseConnection database;
    private IDataSet dataSet;

    @Autowired
    public DbUnitPrepare(JdbcTemplate jdbcTemplate) {
        try {
            // 获得IDatabaseConnection，使用spring自行的数据源
            this.database = new DatabaseConnection(jdbcTemplate.getDataSource().getConnection());

            // 去掉警告信息
            // WARN  org.dbunit.dataset.AbstractTableMetaData - Potential problem found: The configured data type factory 'class org.dbunit.dataset.datatype.DefaultDataTypeFactory' might cause problems with the current database 'MySQL' (e.g. some datatypes may not be supported properly). In rare cases you might see this message because the list of supported database products is incomplete (list=[derby]). If so please request a java-class update via the forums.If you are using your own IDataTypeFactory extending DefaultDataTypeFactory, ensure that you override getValidDbProducts() to specify the supported database products
            this.database.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

            // 获得xml数据
            File file = new File("target/test-classes/data.xml");
            this.dataSet = new FlatXmlDataSet(file);

            // excel数据源，支持2003，但不支持更高版本
//            File file1 = new File("target/test-classes/data.xls");
//            this.dataSet = new XlsDataSet(file1);
        } catch (DatabaseUnitException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public IDatabaseConnection getIDatabaseConnection() {
        return this.database;
    }

    public IDataSet getIDataSet() {
        return this.dataSet;
    }
}
