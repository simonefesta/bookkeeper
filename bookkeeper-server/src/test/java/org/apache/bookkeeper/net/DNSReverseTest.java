package org.apache.bookkeeper.net;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.naming.NamingException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DNSReverseTest {

    private String expected;
    private InetAddress hostIp; //che voglio trasformare in nome
    private String nameServer; //name server dns server

    public DNSReverseTest(String expected, String ipString, String nameServer) {
        configure(expected, ipString, nameServer);
    }



    private void configure(String expected, String ipString, String nameServer)  {
        try{
            if (ipString != null)
                hostIp = InetAddress.getByName(ipString);
            else
                hostIp = null;
        this.expected = expected;
        this.nameServer = nameServer;
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Parameterized.Parameters
    public static Collection<?> getParameter()  {

        return Arrays.asList(new Object[][]{
                //expected                              //hostIp               //nameServer
                {"mil04s50-in-f4.1e100.net.",      "142.251.209.4",              "8.8.8.8"},         // [Ip public, nameServer public] google
                {"error",		                   "191.154.176.1",		         "8.8.8.8"},          // [invalid ip,nameServer valid]
                {"null",                               null,                         null},         // [Ip invalid, nameServer invalid]
                {"null",                               null,                        "8.8.8.8"},         // [Ip invalid, nameServer invalid]
                {"error",		                   "142.251.209.4",			        "255.255.255.255"}, // [ip valid, nameServer invalid]
                //Fine test minimali
                  {"dns.google.",                    "8.8.8.8",                    "8.8.8.8"},        // [ip valid, nameServer valid]
                  {"error",                          "2001:4860:4860::8888",       "8.8.8.8"},         // [Ipv6 valid, nameServerV public]. Dovrebbe ritornare mil04s50-in-f4.1e100.net.
                  {"error",		                   "0.0.0.0",			        "8.8.8.8"},          // [special ip,nameServer valid]
                  {"error",		                   "8.8.8.8",			        "localhost"}  ,        // [special ip,nameServer valid] */



        });

    }

    @Test
    public void TestReverseDNS() {
        String actual;
        switch (expected) {
            case "null":
                try {
                     DNS.reverseDns(hostIp, nameServer);
                    Assert.fail("Fail in ReverseTest case 'null': got success instead of fail");
                    }
                catch (NullPointerException | NamingException e)
                {
                  if((e instanceof NullPointerException)) {
                      assertTrue(true);
                    }
                }
                break;

            case "error":
                try {
                    DNS.reverseDns(hostIp, nameServer);
                    Assert.fail("Fail in ReverseTest case 'error': got success instead of fail");
                }
                catch (NamingException e) {
                    assertTrue(true);
                }
                break;

            default:
                try {
                    actual = DNS.reverseDns(hostIp, nameServer);
                }catch (NamingException e) {
                    actual = "error";
                }
                assertEquals(expected,actual);
        }
    }


}