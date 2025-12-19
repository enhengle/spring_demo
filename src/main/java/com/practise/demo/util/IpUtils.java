package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * IP工具类
 */
@Slf4j
public class IpUtils {

    private static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final String IPV6_REGEX = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
    private static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX);

    /**
     * 获取本机IP地址
     */
    public static String getLocalIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本机IP地址失败", e);
            return null;
        }
    }

    /**
     * 获取本机所有IP地址
     */
    public static String[] getAllLocalIps() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            StringBuilder ips = new StringBuilder();
            
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.getHostAddress().indexOf(':') == -1) { // 只获取IPv4地址
                        ips.append(address.getHostAddress()).append(",");
                    }
                }
            }
            
            if (ips.length() > 0) {
                ips.setLength(ips.length() - 1); // 移除最后一个逗号
                return ips.toString().split(",");
            }
        } catch (SocketException e) {
            log.error("获取本机所有IP地址失败", e);
        }
        return new String[0];
    }

    /**
     * 获取本机首选IP地址（非回环地址）
     */
    public static String getPreferredLocalIp() {
        String[] ips = getAllLocalIps();
        if (ips.length > 0) {
            return ips[0];
        }
        return getLocalIp();
    }

    /**
     * 判断是否为合法IPv4地址
     */
    public static boolean isValidIpv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip.trim()).matches();
    }

    /**
     * 判断是否为合法IPv6地址
     */
    public static boolean isValidIpv6(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        return IPV6_PATTERN.matcher(ip.trim()).matches();
    }

    /**
     * 判断是否为合法IP地址（IPv4或IPv6）
     */
    public static boolean isValidIp(String ip) {
        return isValidIpv4(ip) || isValidIpv6(ip);
    }

    /**
     * 将IP地址转换为长整型
     */
    public static long ipToLong(String ip) {
        if (!isValidIpv4(ip)) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }
        
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result << 8 | Integer.parseInt(parts[i]);
        }
        return result;
    }

    /**
     * 将长整型转换为IP地址
     */
    public static String longToIp(long ipLong) {
        StringBuilder ip = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            ip.append((ipLong >> (i * 8)) & 0xFF);
            if (i > 0) {
                ip.append(".");
            }
        }
        return ip.toString();
    }

    /**
     * 判断IP是否在指定网段内
     * @param ip 要检查的IP地址
     * @param network 网段地址（如：192.168.1.0）
     * @param mask 子网掩码（如：255.255.255.0）
     */
    public static boolean isIpInNetwork(String ip, String network, String mask) {
        if (!isValidIpv4(ip) || !isValidIpv4(network) || !isValidIpv4(mask)) {
            return false;
        }
        
        try {
            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(network);
            long maskLong = ipToLong(mask);
            
            return (ipLong & maskLong) == (networkLong & maskLong);
        } catch (Exception e) {
            log.error("检查IP是否在网段内失败", e);
            return false;
        }
    }

    /**
     * 判断IP是否在指定网段内（CIDR格式）
     * @param ip 要检查的IP地址
     * @param cidr 网段（如：192.168.1.0/24）
     */
    public static boolean isIpInCidr(String ip, String cidr) {
        if (!isValidIpv4(ip) || cidr == null || !cidr.contains("/")) {
            return false;
        }
        
        try {
            String[] parts = cidr.split("/");
            String network = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);
            
            if (prefixLength < 0 || prefixLength > 32) {
                return false;
            }
            
            long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;
            String maskStr = longToIp(mask);
            
            return isIpInNetwork(ip, network, maskStr);
        } catch (Exception e) {
            log.error("检查IP是否在CIDR网段内失败", e);
            return false;
        }
    }

    /**
     * 获取IP地址类型
     */
    public static String getIpType(String ip) {
        if (isValidIpv4(ip)) {
            if (isPrivateIpv4(ip)) {
                return "Private IPv4";
            } else if (isLoopbackIpv4(ip)) {
                return "Loopback IPv4";
            } else {
                return "Public IPv4";
            }
        } else if (isValidIpv6(ip)) {
            if (isPrivateIpv6(ip)) {
                return "Private IPv6";
            } else if (isLoopbackIpv6(ip)) {
                return "Loopback IPv6";
            } else {
                return "Public IPv6";
            }
        }
        return "Invalid IP";
    }

    /**
     * 判断是否为私有IPv4地址
     */
    public static boolean isPrivateIpv4(String ip) {
        if (!isValidIpv4(ip)) {
            return false;
        }
        
        long ipLong = ipToLong(ip);
        
        // 10.0.0.0/8
        if ((ipLong & 0xFF000000L) == 0x0A000000L) {
            return true;
        }
        
        // 172.16.0.0/12
        if ((ipLong & 0xFFF00000L) == 0xAC100000L) {
            return true;
        }
        
        // 192.168.0.0/16
        if ((ipLong & 0xFFFF0000L) == 0xC0A80000L) {
            return true;
        }
        
        return false;
    }

    /**
     * 判断是否为回环IPv4地址
     */
    public static boolean isLoopbackIpv4(String ip) {
        if (!isValidIpv4(ip)) {
            return false;
        }
        
        long ipLong = ipToLong(ip);
        return (ipLong & 0xFF000000L) == 0x7F000000L; // 127.0.0.0/8
    }

    /**
     * 判断是否为私有IPv6地址
     */
    public static boolean isPrivateIpv6(String ip) {
        if (!isValidIpv6(ip)) {
            return false;
        }
        
        return ip.startsWith("fc00:") || ip.startsWith("fd00:") || 
               ip.startsWith("fe80:") || ip.startsWith("::1");
    }

    /**
     * 判断是否为回环IPv6地址
     */
    public static boolean isLoopbackIpv6(String ip) {
        if (!isValidIpv6(ip)) {
            return false;
        }
        
        return ip.equals("::1");
    }

    /**
     * 获取IP地址的地理位置信息（简化版）
     */
    public static String getIpLocation(String ip) {
        if (!isValidIp(ip)) {
            return "Invalid IP";
        }
        
        if (isPrivateIpv4(ip) || isPrivateIpv6(ip)) {
            return "Private Network";
        }
        
        if (isLoopbackIpv4(ip) || isLoopbackIpv6(ip)) {
            return "Localhost";
        }
        
        // 这里可以集成第三方IP地理位置服务
        return "Unknown Location";
    }
}