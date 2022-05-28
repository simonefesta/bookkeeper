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
                //expected                                                                  //strInterface
                {new String[]{"fe80:0:0:0:0:0:0:1%lo0","0:0:0:0:0:0:0:1%lo0","127.0.0.1"},            "lo0"}, //attenzione:  substring(13) poichè il metodo ritorna MacAir.local/indirizzo IP, e voglio solo indirizzo IP.
               // {new String[]{"localhost"},            "default"}, //attenzione:  substring(13) poichè il metodo ritorna MacAir.local/indirizzo IP, e voglio solo indirizzo IP.

                {new String[]{"fe80::7031:31ff:fe4d:1840%anpi1".replace("::",":0:0:0:")},        "anpi1"}, //ricavato da ifconfig -a, prendendo indirizzo inet6. il replace viene fatto perchè getIps non ritorna fe80:: ma fe80.0.0.0.
                {null,                                                                          "bridge0"}, //non ha alcun 'inet6', quindi mi aspetto di non ottenere nulla.
                {null,                                                                           "invalid"}, //fornisco interfaccia non valida, non mi aspetto nulla.
                {null,                                                                            null}
        });


    }

    @Test
    public void TestGetIpDNS() {
        String actual[];
        try {
            actual = DNS.getIPs(strInterface);
        } catch (UnknownHostException | NullPointerException e)  {
            actual = null;
        }
        assertArrayEquals(expected,actual);
    }


}