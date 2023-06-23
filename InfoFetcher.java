import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InfoFetcher {

    /**
 *      * @return the Ip of current machine.
 *           */
    public List<String> getIpList() {
        List<String> ipList=new ArrayList<String>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {                
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        ipList.add(inetAddress.getHostAddress().toString());
                    } 
                }
            }
        } catch (SocketException e) {
            System.out.println("Error occured :: "+e.getMessage());
        }
        return ipList;
    }
    
    /**
 *      * @param Ip - Ip of the system.
 *           * @return - Mac id for the specified IP.
 *                */
    public String getIP_Mac(String Ip) {
        InetAddress ip;
        StringBuilder sb = new StringBuilder();
        try {
            ip = InetAddress.getByName(Ip);
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));        
            }
        } catch (UnknownHostException e) {
            System.out.println("Error occured :: "+e.getMessage());
        } catch (SocketException e){
            System.out.println("Error occured :: "+e.getMessage());
        }
        return sb.toString();
    }
    /**
 *      * @param ip - Ip address.
 *           * @return - Gives hostname of the given Ip address.
 *                */
    public String getHostName(String ip) {
        String hostName="";
        try {
            hostName = InetAddress.getByName(ip).getHostName();
        } catch (UnknownHostException e) {
            System.out.println("Error occured :: "+e.getMessage());
        }
        return hostName;
        
    }
    /**
 *      * @param macId - macId of the server.
 *           * @param host - hostname of server.
 *                * @return - serverId as combination of macId and host.
 *                     */
    public String generateServerId(String macId,String host) {
        String serverId = null;
        String [] macArray = macId.toUpperCase().split(":");
        int intlh1=10;
        int intlh2=10; 
        int intlh3=10;
        if(macArray.length == 6) {
            String mid1 = new StringBuilder(macArray[0]+macArray[1]).reverse().toString();
            String mid2 = new StringBuilder(macArray[2]+macArray[3]).reverse().toString();
            String mid3 = new StringBuilder(macArray[4]+macArray[5]).reverse().toString();
            if(host!=null) {
                host=host.replaceAll("\\.", "").trim();
                int hostLen=host.length();
                int factor = hostLen/3;
                String lh1 = host.substring(0,factor);
                String lh2 = host.substring(factor, 2*factor);
                String lh3 = host.substring(factor*2);
                intlh1 = Math.abs(lh1.hashCode())%100;
                intlh2 = Math.abs(lh2.hashCode())%100;
                intlh3 = Math.abs(lh3.hashCode())%100;
            }
            serverId = intlh1+mid1+"-"+intlh2+mid2+"-"+intlh3+mid3;
        }
        return serverId;
    }
    public static void main(String[] arg){
    
        InfoFetcher infoFetcher = new InfoFetcher();
        List<String> ipList = infoFetcher.getIpList();
        if(ipList.size()>0) {
            String serverId = infoFetcher.generateServerId(infoFetcher.getIP_Mac(ipList.get(0)), infoFetcher.getHostName(ipList.get(0)));
            String host = infoFetcher.getHostName(ipList.get(0));
            System.out.println("Server Id :: "+serverId);
            System.out.println("Host Name :: "+ host);
        }
    }
}


