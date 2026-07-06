package com.cigabyte.sitesentinel.website;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class WebsiteTargetValidator {

    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");

    private final boolean allowPrivateTargets;

    public WebsiteTargetValidator(
            @Value("${sitesentinel.scanner.allow-private-targets:false}") boolean allowPrivateTargets
    ) {
        this.allowPrivateTargets = allowPrivateTargets;
    }

    public void validateConfiguredHost(String host) {
        String normalizedHost = normalizeHost(host);

        if (normalizedHost.isBlank()) {
            throw new IllegalArgumentException("Website host is required.");
        }

        if (allowPrivateTargets) {
            return;
        }

        if (isLocalHostname(normalizedHost)) {
            throw new IllegalArgumentException("Local or internal hostnames cannot be monitored.");
        }

        int[] ipv4Octets = parseIpv4(normalizedHost);

        if (ipv4Octets != null && isPrivateOrSpecialIpv4(ipv4Octets)) {
            throw new IllegalArgumentException("Private, loopback or special-use IPv4 targets cannot be monitored.");
        }

        if (isIpv6Literal(normalizedHost) && isPrivateOrSpecialIpv6(normalizedHost)) {
            throw new IllegalArgumentException("Private, loopback or special-use IPv6 targets cannot be monitored.");
        }
    }

    public void validateScanTarget(String host) {
        String normalizedHost = normalizeHost(host);

        validateConfiguredHost(normalizedHost);

        if (allowPrivateTargets) {
            return;
        }

        validateResolvedAddresses(normalizedHost);
    }

    private void validateResolvedAddresses(String host) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);

            for (InetAddress address : addresses) {
                if (isUnsafeAddress(address)) {
                    throw new IllegalArgumentException(
                            "Resolved scanner target points to a private, loopback or special-use address."
                    );
                }
            }
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException("Scanner target could not be resolved: " + host, exception);
        }
    }

    private boolean isUnsafeAddress(InetAddress address) {
        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return true;
        }

        byte[] bytes = address.getAddress();

        if (bytes.length == 4) {
            int first = bytes[0] & 0xff;
            int second = bytes[1] & 0xff;

            return isPrivateOrSpecialIpv4(new int[]{first, second, bytes[2] & 0xff, bytes[3] & 0xff});
        }

        if (address instanceof Inet6Address && bytes.length == 16) {
            int first = bytes[0] & 0xff;

            return (first & 0xfe) == 0xfc;
        }

        return false;
    }

    private String normalizeHost(String host) {
        if (host == null) {
            return "";
        }

        String value = host.trim().toLowerCase(Locale.ROOT);

        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1);
        }

        while (value.endsWith(".")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }

    private boolean isLocalHostname(String host) {
        return "localhost".equals(host)
                || host.endsWith(".localhost")
                || host.endsWith(".local")
                || host.endsWith(".internal")
                || host.endsWith(".lan")
                || host.endsWith(".home")
                || host.endsWith(".corp");
    }

    private boolean isIpv6Literal(String host) {
        return host.contains(":");
    }

    private boolean isPrivateOrSpecialIpv6(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);

            if (!(address instanceof Inet6Address)) {
                return false;
            }

            return isUnsafeAddress(address);
        } catch (UnknownHostException exception) {
            return true;
        }
    }

    private int[] parseIpv4(String host) {
        if (!IPV4_PATTERN.matcher(host).matches()) {
            return null;
        }

        String[] parts = host.split("\\.");
        int[] octets = new int[4];

        for (int index = 0; index < parts.length; index++) {
            try {
                int value = Integer.parseInt(parts[index]);

                if (value < 0 || value > 255) {
                    return null;
                }

                octets[index] = value;
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        return octets;
    }

    private boolean isPrivateOrSpecialIpv4(int[] octets) {
        int first = octets[0];
        int second = octets[1];

        if (first == 0) {
            return true;
        }

        if (first == 10) {
            return true;
        }

        if (first == 100 && second >= 64 && second <= 127) {
            return true;
        }

        if (first == 127) {
            return true;
        }

        if (first == 169 && second == 254) {
            return true;
        }

        if (first == 172 && second >= 16 && second <= 31) {
            return true;
        }

        if (first == 192 && second == 168) {
            return true;
        }

        if (first == 192 && second == 0) {
            return true;
        }

        if (first == 198 && (second == 18 || second == 19)) {
            return true;
        }

        if (first >= 224) {
            return true;
        }

        return false;
    }
}