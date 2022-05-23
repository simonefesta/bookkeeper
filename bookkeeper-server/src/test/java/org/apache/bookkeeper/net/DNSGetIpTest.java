package org.apache.bookkeeper.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DNSGetIpTest {

    private String expected;
    private String strInterface;


    public DNSGetIpTest(String expected, String strInterface) {
        configure(expected,strInterface);
    }


    public void configure(String expected, String strInterface) {
        this.expected = expected;
        this.strInterface = strInterface;
;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameter()  {

        return Arrays.asList(new Object[][]{
                //expected         //strInterface  //returnSub
                {"192.168.0.104",   "default"}, //attenzione: 192.168.0.104 è indirizzo IP nella mia rete. Non sarà sempre questo.
                {"fe80::7031:31ff:fe4d:1840%anpi1".replace("::",":0:0:0:"), "anpi1"}, //ricavato da ifconfig -a, prendendo indirizzo inet6. il replace viene fatto, perchè getIps non ritorna fe80:: ma fe80.0.0.0.
                {null,"bridge0"}, //non ha alcun 'inet6', quindi mi aspetto di non ottenere nulla.
                {null,"invalid"}, //fornisco interfaccia non valida, non mi aspetto nulla.
                {null, null} //
        });


    }

    @Test
    public void TestGetIpDNS() {
        String actual;
        try {
            actual = DNS.getDefaultIP(strInterface);
        } catch (UnknownHostException | NullPointerException e)  {
            actual = null;
        }
        assertEquals(expected,actual);
    }


}