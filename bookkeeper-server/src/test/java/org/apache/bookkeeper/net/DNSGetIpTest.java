package org.apache.bookkeeper.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;


@RunWith(Parameterized.class)
public class DNSGetIpTest {

    private String[] expected;
    private String strInterface;
    private static String[] localAddress;


    public DNSGetIpTest(String[] expected, String strInterface) {
        configure(expected,strInterface);
    }


    public void configure(String[] expected, String strInterface) {
        this.expected = expected;
        this.strInterface = strInterface;


    }

    @Parameterized.Parameters
    public static Collection<?> getParameter()  {
        localAddress = new String[]{"0:0:0:0:0:0:0:1%lo0","127.0.0.1","fe80:0:0:0:0:0:0:1%lo0"};

        return Arrays.asList(new Object[][]{
                //expected                                                                           //strInterface
                {localAddress,                                                                        "lo0"}, //attenzione:  substring(13) poichè il metodo ritorna MacAir.local/indirizzo IP, e voglio solo indirizzo IP.
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

        } catch (UnknownHostException | NullPointerException e) {
            actual = new String[]{"error"};
        }
        if (expected == localAddress) {

                assertTrue(Arrays.asList(expected).contains(actual[0]));
            }
        else{
                assertArrayEquals(expected, actual);
            }
        }

    }
