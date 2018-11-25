package com.activiti6.demo.util;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUnit {

    private IDatabaseTester databaseTester;


    private IDatabaseConnection connection;


    private QueryDataSet queryDataSet;


    private String driver;


    private String url;


    private String user;


    private String password;


    private Connection jdbcConnection;


    public DbUnit() {
    }


    public DbUnit(String driver, String url, String user, String password) {


        try {

            Class driverClass = Class.forName(driver);

            jdbcConnection = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    public void setSchema(String schema) throws SQLException,Exception {

        connection = new DatabaseConnection(jdbcConnection, schema);

    }


    public void insertData(IDataSet dataSet) {

        try {

            //DatabaseOperation.DELETE.execute(connection, dataSet);

            DatabaseOperation.INSERT.execute(connection, dataSet);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    public void deleteData(IDataSet dataSet) {

        try {

            DatabaseOperation.DELETE.execute(connection, dataSet);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    public void updateData(IDataSet dataSet) {

        try {

            DatabaseOperation.UPDATE.execute(connection, dataSet);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }




    public QueryDataSet getQueryDataSet() {

        return queryDataSet;

    }


    public IDataSet getDataSet(String path) {

        FlatXmlDataSet dataSet = null;

        try {

            dataSet = new FlatXmlDataSet(new FileInputStream(new File(path)));

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return dataSet;

    }
}
