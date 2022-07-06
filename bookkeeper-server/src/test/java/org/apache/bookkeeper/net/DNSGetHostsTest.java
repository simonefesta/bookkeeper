package org.apache.bookkeeper.net;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.InetAddress;
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
                {"valid",                         "available",           "8.8.8.8"},    //MINIMALE
                {"valid",                         "available",                null},  //CASO PARTICOLARE
                {"error",                           null,                "8.8.8.8"}, //MINIMALE
                {"error",                           "-1",                "8.8.8.8"}, //MINIMALE
                {"valid",                         "default",           "8.8.8.8"},

                {"error",                           "-1",                  null},
                {"valid",                         "available",         "-1"},  //error nameserver
                {"valid",                          "available",         "255.255.255.255"},  //error nameserver

                {"down",	"not_available",	"8.8.8.8"},					//{not_available},	{valid_notlocal_nameserver}

        });

    }

    @Test
    public void TestGetHosts() throws UnknownHostException {
        String[] hostList;
        switch (expected)
        {

            case "valid":
                //System.out.println(InetAddress.getLocalHost().getHostName());
            try {
                hostList = DNS.getHosts(strInterface, nameserver);
                if(nameserver==null) assertNotEquals(UtilitiesDNS.getCachedHostname(),hostList[0]); //increase mutation //works with Ubuntu

                //assertNotNull(getIPs(strInterface));
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
                    assertEquals(1, hostList.length);

                } catch (UnknownHostException | NullPointerException e) {

                    Assert.fail("Fail getHosts:\nExpected: "+expected+"\nstrInterface: "+strInterface+"\nnameserver"+nameserver+"\n");
                }
                break;


        }
    }



}




