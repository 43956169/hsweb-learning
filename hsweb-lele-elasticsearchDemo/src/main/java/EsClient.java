import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;

/**
 * Created for Intellij IDEA.
 * Author:e.le.lee.
 * Date: 2017/2/21.
 */

class EsClient {
    private static Log log = LogFactory.getLog(EsClient.class);

    //    用于提供单例的TransportClient BulkProcessor
    private static TransportClient tclient = null;
    private static BulkProcessor staticBulkProcessor = null;

    //【获取TransportClient 的方法】
    public static TransportClient getClient() {
        try {
            if (tclient == null) {
                String EsHosts = "192.168.1.41:9300,192.168.1.42:9300,192.168.1.43:9300";
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", "dkes")//设置集群名称
                        .put("tclient.transport.sniff", true).build();//自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中

                tclient = TransportClient.builder().settings(settings).build();
                String[] nodes = EsHosts.split(",");
                for (String node : nodes) {
                    if (node.length() > 0) {//跳过为空的node（当开头、结尾有逗号或多个连续逗号时会出现空node）
                        String[] hostPort = node.split(":");
                        tclient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));

                    }
                }
            }//if
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tclient;
    }
}