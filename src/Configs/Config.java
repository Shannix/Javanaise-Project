package Configs;

public class Config {
    /*COORDINATOR*/

    public static final String name = "coordinator";
    public static final int coordinatorHostPort = 2020;
    public static final String coordinatorHost
            = String.format("%s%d%s", "//localhost:", coordinatorHostPort, "/" + name);

    /*SERVER CLIENT*/
    public static final String nameServerClient_1 = "serverclient1";
    public static final int clientServerHostPort_1 = 2021;
    public static final String clientServerHost_1
            = String.format("%s%d%s", "//127.0.0.1:", clientServerHostPort_1, "/" + nameServerClient_1);

    public static final String nameServerClient_2 = "serverclient2";
    public static final int clientServerHostPort_2 = 2022;
    public static final String clientServerHost_2
            = String.format("%s%d%s", "//127.0.0.1:", clientServerHostPort_2, "/" + nameServerClient_2);

}
