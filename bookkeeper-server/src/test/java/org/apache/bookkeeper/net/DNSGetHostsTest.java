package org.apache.bookkeeper.net;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

import static org.junit.Assert.*;



@RunWith(Parameterized.class)
public class DNSGetHostsTest {


    private String strInterface;
    private String nameserver;
    private String expected;




    public DNSGetHostsTest(String expected, String strInterface, String nameserver) {
        configure(expected, strInterface, nameserver);
    }


    private void configure(String expected, String strInterface, String nameserver)
    {
        if(strInterface != null && (strInterface.equals("available") || strInterface.equals("not_available"))) {

            try {
                boolean isFounded = false;
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                NetworkInterface networkInterface;
                while (interfaces.hasMoreElements() && !isFounded) {
                    networkInterface = interfaces.nextElement();
                    if (strInterface.equals("available")) {
                        if (networkInterface.isUp()  && !networkInterface.getName().equals("lo0")) {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.nameserver = nameserver;
                            isFounded = true;

                        }
                    }else {
                        if (!networkInterface.isUp()  && !networkInterface.getName().equals("lo0") ) {
                            System.out.println("Used interface " + networkInterface.getName());
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.nameserver = nameserver;
                            isFounded = true;
                        }
                    }
                }
                if (!isFounded)
                    Assert.fail("No interfaces in the system for testing. Maybe all networkInterfaces are available, and you're trying to test a not_available networkInterface. Try in terminal:  sudo ifconfig [nameInterface] down.");
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        else
            { // strInterface = null || = default
                 this.strInterface = strInterface;
                 this.expected = expected;
                 this.nameserver = nameserver;
            }
    }

    @Parameterized.Parameters
    public static Collection<?> getParameter() {

        return Arrays.asList(new Object[][]{
                //expected                      //strInterface        //nameserver
                {"valid",                         "available",           "8.8.8.8"},    //{interface ok, nameserver ok}
                {"valid",                         "available",                null},  // {interface ok, nameserver null}
                {"valid",                         "available",         "-1"},         // {interface ok, nameserver invalid}
                {"error",                           null,                "8.8.8.8"}, // {interface null, nameserver valid}
                {"error",                           "-1",                "8.8.8.8"}, // {interface invalid, nameserver valid}

                {"valid",                         "default",           "8.8.8.8"},
                {"error",                           "-1",                  null},
                {"valid",                          "available",         "255.255.255.255"},  //error nameserver
                {"down",	                       "not_available",	        "8.8.8.8"},					//{not_available},	{valid_notlocal_ns}

        });

    }

    @Test
    public void TestGetHosts() {
        String[] hostList;
        switch (expected)
        {

            case "valid":
            try {
                hostList = DNS.getHosts(strInterface, nameserver);
                //if(nameserver==null && strInterface =="wlp2s0") assertNotEquals(UtilitiesDNS.getCachedHostname(),hostList[0]); //increase mutation //works with Ubuntu
                for (String host : hostList)
                {
                    assertTrue(UtilitiesDNS.isIpAddress(host));
                }

            } catch (Exception e)
                {
                    Assert.fail("Fail in getHost case 'valid'");
                }
            break;

            case "error":
                if (strInterface == null)
                {
                    try
                    {
                        DNS.getHosts(strInterface, nameserver);
                        Assert.fail("dns error success");

                    } catch (NullPointerException e) {

                        assertTrue(true);
                        return;
                    }
                    catch (UnknownHostException e) {
                        Assert.fail("Fail getHostsTest case 'error': HostException instead NullPoint");
                    }
                }
                else
                {
                    try {
                        DNS.getHosts(strInterface, nameserver);
                        Assert.fail("dns error case, but success");
                    } catch (UnknownHostException e) {
                        assertTrue(true);
                        return;
                    }
                }
                break;

            case "down":
                try {
                    hostList = DNS.getHosts(strInterface, nameserver);
                    assertTrue(hostList.length == 1 && UtilitiesDNS.isIpAddress(hostList[0]));

                } catch (UnknownHostException | NullPointerException e) {

                    Assert.fail("Fail GetHostsTest case 'down': Exception instead of NotException");
                }
                break;


        }
    }



}




