package com.activiti6.demo;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.List;

public class TestClass {

    @Test
    public void testArrayList() {
        List<String> stringList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(stringList)) {
            System.out.println("集合数据 非空");
        } else {
            System.out.println("集合数据是空的");
        }
    }

}
