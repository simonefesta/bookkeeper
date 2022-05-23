package org.apache.bookkeeper.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DNSReverseTest {

    private String expected;
    private InetAddress hostIp; //che voglio trasformare in nome
    private String nameServer; //name server dns server

    public DNSReverseTest(String expected, InetAddress hostIp, String nameServer) {
        configure(expected, hostIp, nameServer);
    }


    public void configure(String expected, InetAddress hostIp, String nameServer) {
        this.expected = expected;
        this.hostIp = hostIp;
        this.nameServer = nameServer;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameter() throws UnknownHostException {

        return Arrays.asList(new Object[][]{
                //expected    //hostIp  //nameServer
                {"mil04s50-in-f4.1e100.net.", InetAddress.getByName("142.251.209.4"), "8.8.8.8"},
                {"error", InetAddress.getByName("104.78.251.72"), null },
                {"error", null, "8.8.8.8"},
                {"error",InetAddress.getByName("127.0.0.1"),"8.8.8.8"},
                {"mil04s50-in-f4.1e100.net.",InetAddress.getByName("2001:4860:4860::8888"),"8.8.8.8"},

        });


    }

    @Test
    public void TestReverseDNS() {
        String actual;
        try {
            actual = DNS.reverseDns(hostIp,nameServer);
        } catch (Exception e) {
            actual = "error";
        }
        assertEquals(expected,actual);
    }


}