package org.apache.bookkeeper.net;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;



@RunWith(Parameterized.class)
public class DNSGetHostsTest {

    private String[] expected;
    private String strInterface;
    private String nameserver;

    public DNSGetHostsTest(String[] expected, String strInterface,
                           String nameserver) {
        configure(expected, strInterface, nameserver);
    }


    public void configure(String[] expected, String strInterface, String nameserver) {
        this.expected = expected;
        this.strInterface = strInterface;
        this.nameserver = nameserver;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameter() {

        return Arrays.asList(new Object[][]{
                //expected                       //strInterface             //nameserver
                {new String[]{"192.168.0.104"}, "default", "8.8.8.8"},
                {new String[]{"192.168.0.104"}, "anpi0", null},
                {null, null, null},
                {null,"anpi4",null}
        });


    }

    @Test
    public void TestGetHosts() {
        String[] actual;
        try {
            actual = DNS.getHosts(strInterface, nameserver);
        } catch (UnknownHostException | NullPointerException e) {
            actual = null;
        }
            assertArrayEquals(expected, actual);
      
    }
}


