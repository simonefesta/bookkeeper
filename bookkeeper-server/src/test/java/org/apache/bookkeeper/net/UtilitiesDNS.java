package org.apache.bookkeeper.net;
/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, version 2.1, dated February 1999.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the latest version of the GNU Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (LICENSE.txt); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import org.apache.commons.lang.StringUtils;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * This class provides a variety of basic utility methods that are not
 * dependent on any other classes within the org.jamwiki package structure.
 */
public class UtilitiesDNS {
    private static Pattern VALID_IPV4_PATTERN = null;
    private static Pattern VALID_IPV6_PATTERN = null;
    private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";

    static {
        try {
            VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
            VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine if the given string is a valid IPv4 or IPv6 address.  This method
     * uses pattern matching to see if the given string could be a valid IP address.
     *
     * @param ipAddress A string that is to be examined to verify whether or not
     *  it could be a valid IP address.
     * @return <code>true</code> if the string is a value that is a valid IP address,
     *  <code>false</code> otherwise.
     */
    public static boolean isIpAddress(String ipAddress) {
        List<String> alternativeNames = Arrays.asList("festinho","festinho.", "localhost", "localhost.", "::1/128");
        // a quanto pare può capitare anche 'festinho' e 'localhost.' <- col punto INCLUSO!
        if (ipAddress == null) return false;

        if (ipAddress.contains("::") && ipAddress.length()>30) //ipv6 lenght
            ipAddress = ipAddress.replace("::",":0:0:0:");

        if (ipAddress.length()>30) //risolvo problema di rappresentazione indirizzo IP
            ipAddress = ipAddress.substring(0,29);

        if(alternativeNames.contains(ipAddress))
        {
            return true; //alias of 127.0.0.1
        }

        if (ipAddress.contains("%"))
            ipAddress = StringUtils.substringBefore(ipAddress,"%");
        if (ipAddress.contains("/"))
            ipAddress = StringUtils.substringBefore(ipAddress,"/");

        Matcher m1 = UtilitiesDNS.VALID_IPV4_PATTERN.matcher(ipAddress);
        if (m1.matches()) {
            return true;
        }
        Matcher m2 = UtilitiesDNS.VALID_IPV6_PATTERN.matcher(ipAddress);
        return m2.matches();
    }

    public static String getCachedHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }


}