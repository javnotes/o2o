package org.vuffy.o2o;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JustTest {

    @Test
    public void test(){
        List<String> strings = new ArrayList<>();
        List<String> strings2 = new ArrayList<>();
//        List<String> strings2 = null;
        strings.add("123");
        strings.add("ewqe");
        strings.add("qewe");
        strings.add("q");
        strings.add("18uj23");

        for (String string : strings2) {
            System.out.println(string);
        }
    }

}
