package org.apache.bookkeeper.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;





import java.net.Inet4Address;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;



@RunWith(Parameterized.class)
public class DNSGetHostsTest {

    private String[] expected;
    private String strInterface;
    private String nameserver;
    private static String[] localAddress;




    public DNSGetHostsTest(String[] expected, String strInterface, String nameserver) {
        configure(expected, strInterface, nameserver);
    }


    public void configure(String[] expected, String strInterface, String nameserver) {
        this.expected = expected;
        this.strInterface = strInterface;
        this.nameserver = nameserver;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameter()  {
         localAddress = new String[]{"192.168.0.104", "localhost", "127.0.0.1"};

        return Arrays.asList(new Object[][]{
                //expected                                                     //strInterface        //nameserver
                {localAddress,     "default",               "8.8.8.8"},
                {localAddress,       "utun4",          "8.8.8.8"},  //new String[]{InetAddress.getLocalHost().toString().substring(13)
                {null,                                                             null,                    null},
                {null,                                                            "anpi4",                 null}
        });


    }


    @Test
    public void TestGetHosts()  {
         String[] actual;
        try{

            actual = DNS.getHosts(strInterface, nameserver);
         } catch (UnknownHostException | NullPointerException e)
              {
                 actual = null;
              }

          if (expected == localAddress && actual != null)
          {     // Durante i test ho notato che actual ritornava 127.0.0.1 or localhost or 192.168.0.104. Non potendo 'prevedere' quale avrei ottenuto, confronto l'actual con tutti e 3.
              assertTrue(Arrays.asList(expected).contains(actual[0]));
          }
          else {
                assertArrayEquals(expected, actual);
                }
        }

      
    }




