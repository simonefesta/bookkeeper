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
public class DNSGetIpTest {

    private String expected;
    private String strInterface;
    private boolean returnSubInterfaces;



    public DNSGetIpTest(String expected, String strInterface, boolean returnSubInterfaces) {
        configure(expected,strInterface, returnSubInterfaces);
    }


    public void configure(String expected, String strInterface, boolean returnSubInterfaces) {

        if(strInterface != null && (strInterface.equals("available") || strInterface.equals("not_available")))
        {
            try {
                boolean isFounded = false;
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                NetworkInterface networkInterface;
                while(interfaces.hasMoreElements() && !isFounded)
                {
                    networkInterface=interfaces.nextElement();
                    if(strInterface.equals("available"))
                    {
                        if(networkInterface.isUp() && !networkInterface.getName().equals("lo0")) //"lo" in Linux.
                        {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.returnSubInterfaces = returnSubInterfaces;
                            isFounded = true;

                        }
                    }
                    else {
                        if(!networkInterface.isUp() && !networkInterface.getName().equals("lo0"))
                        {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.returnSubInterfaces = returnSubInterfaces;
                            isFounded = true;
                        }
                    }
                }

                if(!isFounded)
                    Assert.fail("No interfaces in the system for testing. Maybe all networkInterfaces are available, and you're trying to test a not_available networkInterface. Try in terminal:  sudo ifconfig [nameInterface] down.");
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        else
        { // strInterface = null || = default
            this.strInterface = strInterface;
            this.expected = expected;
            this.returnSubInterfaces = returnSubInterfaces;
        }


    }

    @Parameterized.Parameters
    public static Collection<?> getParameter()  {



        return Arrays.asList(new Object[][]{
                //expected                //strInterface      //returnSub
                {"down",	               "not_available",	    true},	        //{not_available},//Non disponibile sul mio pc
                {"valid",	              "available",		    true},			//{available},
                {"valid",		           "default",			true},			//{special_string},
                {"error", 	                "-1", 				true},			//{undefined},
                {"error", 	                null, 				true},			//{null},
                {"valid",		           "default",			false},			//{special_string},
                {"valid",		           "available",		    false},			//{available},		{false} -> Mutation bug
                {"down",	              "not_available",	    false},			//{not_available},	{false}
                {"valid",		           "lo0",		       false},
              //  {"valid",		           "wlp2s0",		       true},		//increase coverage on Linux




        });

    }


    @Test
    public void getIPsTest()
    {
        String[] iPList, iPListWithSub;
        boolean check = true;

        switch(expected)
        {
            case "valid":
                try {
                    iPList=DNS.getIPs(strInterface, returnSubInterfaces);
                    if(iPList.length>0)
                    { for (String iP : iPList) {
                            if (!UtilitiesDNS.isIpAddress(iP)) {
                                check = false;
                                break;
                            }
                        }

                    }
                    else
                        check=false; //non ho trovato nessuno
                   // if(strInterface.equals("wlp2s0") && returnSubInterfaces) {assertEquals(3,iPList.length); } Works on Linux
                    assertTrue(check);
                }
                catch (UnknownHostException e) {
                    Assert.fail("Fail getIPsTest case 'valid'");
                }

                if(!returnSubInterfaces)
                {
                    try {
                        iPList=DNS.getIPs(strInterface, returnSubInterfaces);
                        iPListWithSub=DNS.getIPs(strInterface, !returnSubInterfaces);
                        assertTrue(iPListWithSub.length >= iPList.length); //mi aspetto che il metodo che include le subInterface mi dia più risultati rispetto a un metodo che non le include
                    }
                    catch (UnknownHostException e) {
                        Assert.fail("Fail getIPsTest case 'valid' with 'no' returnSubInt");
                    }

                }
                break;

            case "error":
                if (strInterface == null)
                {
                  try {
                        DNS.getIPs(strInterface, returnSubInterfaces);
                      Assert.fail("Fail getIPsTest case 'error': success instead exception with strInterface = null");

                  } catch (NullPointerException e) { //mi aspetto lei

                        assertTrue(true);
                        return;
                    }
                    catch (UnknownHostException e) {
                        Assert.fail("Fail getIPsTest case 'error': HostException instead NullPoint");
                    }
                }
                else
                {
                    try {
                        DNS.getIPs(strInterface, returnSubInterfaces);
                        Assert.fail("Fail getIPsTest case 'error': success instead exception with strInterface != null");

                    } catch (UnknownHostException e) {

                        assertTrue(true);
                        return;
                    }
                }
                break;

            case "down":
                try {

                    iPList=DNS.getIPs(strInterface, returnSubInterfaces);
                    //Si attende l'indirizzo IP della sola interfaccia e non altri, poiché l'interfaccia risulta "down".
                    assertTrue(iPList.length == 1 && UtilitiesDNS.isIpAddress(iPList[0]));
                } catch (UnknownHostException e) {

                    Assert.fail("Fail getDefaultIPTest:\nExpected: "+expected+"\nstrInterface: "+strInterface+"\n");
                }

                break;

            default:
                Assert.fail("Fail getIPsTest:\nExpected: "+expected+"\nstrInterface: "+strInterface+"\nreturnSubinterfaces: "+returnSubInterfaces+"\n");
        }
    }
}




