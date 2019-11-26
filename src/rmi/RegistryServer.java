package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RegistryServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // 启动一个占用10000端口的registry
        LocateRegistry.createRegistry(10000);

        // 创建一个 ping 对象,
        // PingImpl 继承了 UnicastRemoteObject,
        // 这个继承让 ping 在创建过程中就被发布到网络中了
        IPing_ ping = new PingImpl();

        // 把 ping 对象绑定到 registry 上;
        // 下面解释一下 URL 的含义:
        // "localhost:10000" 是 registry 的地址
        // "ping" 是 ping 对象在 registry 绑定的名字
        Naming.rebind("rmi://localhost:10000/ping", ping);
    }
}