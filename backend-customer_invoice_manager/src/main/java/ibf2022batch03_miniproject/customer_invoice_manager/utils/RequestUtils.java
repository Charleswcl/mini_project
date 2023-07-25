package ibf2022batch03_miniproject.customer_invoice_manager.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;


public class RequestUtils {

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = "Unknown IP";
        if(request != null) {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if(ipAddress == null || "".equals(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest request) {
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
        UserAgent agent = userAgentAnalyzer.parse(request.getHeader("user-agent"));
        return agent.getValue("AgentName") + " - " + agent.getValue("DeviceName");
    }
}
