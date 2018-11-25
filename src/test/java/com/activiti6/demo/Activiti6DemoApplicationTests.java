package com.activiti6.demo;

import com.activiti6.demo.util.DbUnit;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Activiti6DemoApplicationTests {


    @Autowired
    private IdentityService identityService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testUser() throws Exception {
//        IdentityService identityService = activitiRule.getIdentityService();
        User user = identityService.newUser("henryyan");
        user.setFirstName("Henry");
        user.setLastName("yan");
        user.setEmail("yanghonglei@gmail.com");
        identityService.saveUser(user);
        User userInDb = identityService.createUserQuery().userId("henryyan").singleResult();
        Assert.assertNotNull(userInDb);
        identityService.deleteUser("henryyan");
        userInDb = identityService.createUserQuery().userId("henryyan").singleResult();
        Assert.assertNull(userInDb);
    }

    @Test
    public void testInsertDB(){
        DbUnit dbUnit = new DbUnit("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/activiti?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "666666");
        IDataSet dataSet = getDataSet("E:\\WorkSpace\\Practicev\\Activiti6Demo\\src\\main\\resources\\data\\identity-data.xml");
        try {
            dbUnit.setSchema("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbUnit.insertData(dataSet);
    }


    public IDataSet getDataSet(String path) {

        FlatXmlDataSet dataSet = null;
        try {
            dataSet = new FlatXmlDataSet(new FileInputStream(new File(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSet;
    }


}
