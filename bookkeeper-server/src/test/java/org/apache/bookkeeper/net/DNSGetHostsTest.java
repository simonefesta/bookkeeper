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
                        if (networkInterface.isUp()  && networkInterface.getName() != "lo") {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.nameserver = nameserver;
                            isFounded = true;

                        }
                    }else if(strInterface.equals("not_available")) {
                        if (!networkInterface.isUp()  && networkInterface.getName() != "lo" ) {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.nameserver = nameserver;
                            isFounded = true;
                        }
                    }
                }
                if (!isFounded)
                    Assert.fail("No interfaces in the system for testing");
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
                {"valid",                         "available",         "8.8.8.8"},  //[ interface ok, nameserver non locale ok]     //MINIMALE
                {"valid",                         "available",           "local"},   // [interface ok, nameserver locale ok]
                {"valid",                         "default",             "local"},     //[interface special ok, nameserver locale ok]   //MINIMALE
                {"valid",                         "available",              null},
                {"error",                            null,                  null},                          //MINIMALE
                {"error",                           null,                "local"},
                {"error",                           "-1",              "8.8.8.8"},
                {"error",                           "-1",                  null},
                {"error",                         "available",         "255.255.255.255"},  //error nameserver
                {"error",                         "default",           "255.255.255.255"},  //error nameserver
                {"error",                            null,             "255.255.255.255"},  //error nameserver



                {"down",	"not_available",	"8.8.8.8"},					//{not_available},	{valid_notlocal_nameserver}
                {"down",	"not_available",	"local"},					//{not_available},	{valid_local_namesarver}
                {"down",	"not_available",	null},						//{not_available},	{null}

        });

    }

    @Test
    public void TestGetHosts()
    {
        String[] hostList;
        switch (expected)
        {

            case "valid":
            try {
                hostList = DNS.getHosts(strInterface, nameserver);
                assertNotNull(hostList);
                if (hostList.length>0)
                {
                    for (String host : hostList)
                    {

                          assertTrue(UtilitiesDNS.isIpAddress(host));
                        }
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
                        System.out.println("error case ok");//mi aspetto lei
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
                    } catch (UnknownHostException e) {
                        assertTrue(true);
                        return;
                    }
                }
                break;
            case "down":
                System.out.println("down");
                try {
                    hostList = DNS.getHosts(strInterface, nameserver);
                    assertEquals(1, hostList.length);
                    assertNotNull(hostList);

                } catch (UnknownHostException | NullPointerException e) {
                    e.printStackTrace();
                    Assert.fail("Fail getHosts:\nExpected: "+expected+"\nstrInterface: "+strInterface+"\nnameserver"+nameserver+"\n");
                }
                break;


        }
    }
}




