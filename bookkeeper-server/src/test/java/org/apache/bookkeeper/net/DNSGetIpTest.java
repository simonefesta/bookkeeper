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
                        if(networkInterface.isUp() && networkInterface.getName() != "lo0")
                        {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.returnSubInterfaces = returnSubInterfaces;
                            isFounded = true;

                        }
                    }
                    else {
                        if(!networkInterface.isUp() && networkInterface.getName() != "lo0")
                        {
                            this.strInterface = networkInterface.getName();
                            this.expected = expected;
                            this.returnSubInterfaces = returnSubInterfaces;
                            isFounded = true;
                        }
                    }

                }
                if(!isFounded)
                    Assert.fail("No interfaces in the system for testing");
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
                //{"down",	               "not_available",	    true},	        //{not_available},//Non disponibile sul mio pc
                {"valid",	              "available",		    true},			//{available},
                {"valid",		           "default",			true},			//{special_string},
                {"error", 	                "-1", 				true},			//{undefined},
                {"error", 	                null, 				true},			//{null},
                {"valid",		           "default",			false},			//{special_string},

        });

    }

    @Test
    public void TestDefaultGetIpDNS() {

        String actual;
        switch(expected)
        {
            case "valid":
                try {
                    actual=DNS.getDefaultIP(strInterface); // Il terminale di test riporta actual = fe80:0:0:0:bdb3:1a6c:c539:60b%utun4, con utun4 interface. Rimuovo questa parte.
                    if (actual.length()>30)
                        actual = actual.substring(0,29);
                    assertTrue(UtilitiesDNS.isIpAddress(actual));
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail("Fail getDefaultIPTest: Expected: "+expected+" for strInterface: "+strInterface);
                }
                break;
            case "error":
                   try {
                        DNS.getDefaultIP(strInterface);
                    } catch (Exception e) {
                        assertTrue(true);
                        return;
                    }
                break;

            /*case "down":
                try {
                    DNS.getDefaultIP(strInterface);
                } catch (UnknownHostException e) {
                    Assert.fail("Fail getDefaultIPTest: Expected: "+expected+" for strInterface: "+strInterface);
                }
                break; */
        }
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
                            if (iP.length()>30) //risolvo problema di rappresentazione indirizzo IP
                                iP = iP.substring(0,29);
                            if (!UtilitiesDNS.isIpAddress(iP)) {
                                check = false;
                                break;
                            }
                        }
                    }
                    else
                        check=false; //non ho trovato nessuno
                    assertTrue(check);
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                    Assert.fail("Fail getIPsTest case 'valid'");
                }
                if(!returnSubInterfaces)
                {
                    try {
                        iPList=DNS.getIPs(strInterface, returnSubInterfaces);
                        iPListWithSub=DNS.getIPs(strInterface, !returnSubInterfaces);
                        assertTrue(iPListWithSub.length >= iPList.length); //mi aspetto che il metodo che include le subInterface mi dia più risultati rispetto ad un metodo che non le include
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
                    } catch (UnknownHostException e) {
                        assertTrue(true);
                        return;
                    }
                }
                break;
        }
    }
}



