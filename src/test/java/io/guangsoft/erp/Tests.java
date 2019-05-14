package io.guangsoft.erp;

import org.junit.Test;

import java.util.Objects;

public class Tests {

    @Test
    public void testEquals() {
        Integer hehe = new Integer(1);
        boolean flag = Objects.equals(1, hehe);
        System.out.println(flag);
    }

    @Test
    public void testThreadPoolExecutor() {

    }
}
