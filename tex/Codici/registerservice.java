public void registerService(int port) {
    // Create the NsdServiceInfo object, and populate it.
    NsdServiceInfo serviceInfo  = new NsdServiceInfo();

    // The name is subject to change based on conflicts
    // with other services advertised on the same network.
    serviceInfo.setServiceName("LiquidAndroid");
    serviceInfo.setServiceType("liquid._tcp");
    serviceInfo.setPort(port);
    ....
}
