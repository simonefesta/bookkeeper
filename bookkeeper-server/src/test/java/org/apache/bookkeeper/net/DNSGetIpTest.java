package org.apache.bookkeeper.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;


@RunWith(Parameterized.class)
public class DNSGetIpTest {

    private String[] expected;
    private String strInterface;


    public DNSGetIpTest(String[] expected, String strInterface) {
        configure(expected,strInterface);
    }


    public void configure(String[] expected, String strInterface) {
        this.expected = expected;
        this.strInterface = strInterface;
;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameter() throws UnknownHostException {

        return Arrays.asList(new Object[][]{
                //expected                                                                           //strInterface
                {new String[]{"fe80:0:0:0:6010:eff:feb9:ca3f%llw0"},                                   "llw0"}, //attenzione:  substring(13) poichè il metodo ritorna MacAir.local/indirizzo IP, e voglio solo indirizzo IP.
                {new String[]{"error"},                                                               "bridge0"}, //non ha alcun 'inet6', quindi mi aspetto di non ottenere nulla.
                {new String[]{"error"},                                                               "invalid"}, //fornisco interfaccia non valida, non mi aspetto nulla.
                {new String[]{"error"},                                                                 null}
        });


    }

    @Test
    public void TestGetIpDNS() {
        String[] actual;
        try {
            actual = DNS.getIPs(strInterface);
        } catch (UnknownHostException | NullPointerException e)  {
            actual = new String[]{"error"};
        }
        assertArrayEquals(expected,actual);
    }


}