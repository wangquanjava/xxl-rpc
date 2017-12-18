package com.xxl.rpc.netcom.netty.client;

import com.xxl.rpc.registry.ZkServiceDiscovery;
import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.concurrent.ConcurrentHashMap;

/**
 * connect pool
 *
 * @author xuxueli 2015-11-5 22:05:38
 */
public class NettyClientPool {

    private GenericObjectPool<NettyClientPoolProxy> pool;

    public NettyClientPool(String host, int port, Serializer serializer) {
        pool = new GenericObjectPool<NettyClientPoolProxy>(new NettyClientPoolFactory(host, port, serializer));
        pool.setTestOnBorrow(true);
        pool.setMaxTotal(2);
    }

    public GenericObjectPool<NettyClientPoolProxy> getPool() {
        return this.pool;
    }

    // serverAddress : [NettyClientPoolProxy01, NettyClientPoolProxy02]
    private static ConcurrentHashMap<String, NettyClientPool> clientPoolMap = new ConcurrentHashMap<String, NettyClientPool>();

    /**
     * 获取本接口可使用的连接池
     */
    public static GenericObjectPool<NettyClientPoolProxy> getPool(String serverAddress, String className, Serializer serializer)
            throws Exception {

        // valid serverAddress
        if (serverAddress == null || serverAddress.trim().length() == 0) {
            serverAddress = ZkServiceDiscovery.discover(className);
        }
        if (serverAddress == null || serverAddress.trim().length() == 0) {
            throw new IllegalArgumentException(">>>>>>>>>>>> serverAddress is null");
        }

        // get from pool
        NettyClientPool clientPool = clientPoolMap.get(serverAddress);
        if (clientPool != null) {
            return clientPool.getPool();
        }

        // init pool
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        clientPool = new NettyClientPool(host, port, serializer);
        clientPoolMap.put(serverAddress, clientPool);
        return clientPool.getPool();
    }

}
