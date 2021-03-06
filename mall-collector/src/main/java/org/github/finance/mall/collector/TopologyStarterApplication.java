package org.github.finance.mall.collector;

import org.github.finance.mall.collector.bolt.order.AssembleCreateOrderinfoBolt;
import org.github.finance.mall.collector.bolt.order.CacheProvinceOrderAmountBolt;
import org.github.finance.mall.collector.bolt.order.CountProvinceOrderAmountBolt;
import org.github.finance.mall.collector.bolt.register.AssembleUserInfoBolt;
import org.github.finance.mall.collector.bolt.register.CacheCatNameUsersBolt;
import org.github.finance.mall.collector.bolt.register.CacheProvinceUsersBolt;
import org.github.finance.mall.collector.bolt.register.CountCatNameUsersBolt;
import org.github.finance.mall.collector.bolt.register.CountProvinceUsersBolt;
import org.github.finance.mall.collector.spout.KafkaSpout;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

/**
 * @author ligaofeng 2017年1月19日 下午3:15:22
 */
public class TopologyStarterApplication {

    private static final String kafkaCluster    = "192.168.197.129:9092";

    private static final String consumerGroupId = "mallCollectorSpoutConsumer";

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(TopologyDefinition.kafkaSpoutName, new KafkaSpout(), 1);
        //register事件的拓扑
        buildRegisterTopology(builder);
        //createOrder事件的拓扑
        buildCreateOrderTopology(builder);

        Config conf = getConfig();
        StormSubmitter.submitTopology(TopologyDefinition.topologyName, conf, builder.createTopology());
    }

    private static void buildCreateOrderTopology(TopologyBuilder builder) {
        builder.setBolt(TopologyDefinition.assembleCreateOrderinfoBolt, new AssembleCreateOrderinfoBolt(), 3)
                .shuffleGrouping(TopologyDefinition.kafkaSpoutName, CollectEvent.CREATE_ORDER.getStreamId());

        builder.setBolt(TopologyDefinition.countProvinceOrderAmountBolt, new CountProvinceOrderAmountBolt(), 2)
                .fieldsGrouping(TopologyDefinition.assembleCreateOrderinfoBolt, new Fields("province"));
        builder.setBolt(TopologyDefinition.cacheProvinceOrderAmountBolt, new CacheProvinceOrderAmountBolt(), 2)
                .fieldsGrouping(TopologyDefinition.countProvinceOrderAmountBolt, new Fields("provinceOrderAmountKey"));
    }

    /**
     * register事件的拓扑
     * 
     * @param builder
     */
    private static void buildRegisterTopology(TopologyBuilder builder) {
        builder.setBolt(TopologyDefinition.assembleUserinfoBolt, new AssembleUserInfoBolt(), 2)
                .shuffleGrouping(TopologyDefinition.kafkaSpoutName, CollectEvent.REGISTER.getStreamId());

        builder.setBolt(TopologyDefinition.countProvinceUsersBolt, new CountProvinceUsersBolt(), 2)
                .fieldsGrouping(TopologyDefinition.assembleUserinfoBolt, new Fields("province"));
        builder.setBolt(TopologyDefinition.cacheProvinceUsersBolt, new CacheProvinceUsersBolt(), 2)
                .fieldsGrouping(TopologyDefinition.countProvinceUsersBolt, new Fields("provinceUsersCntKey"));

        builder.setBolt(TopologyDefinition.countCatNameUsersBolt, new CountCatNameUsersBolt(), 2)
                .fieldsGrouping(TopologyDefinition.assembleUserinfoBolt, new Fields("catName"));
        builder.setBolt(TopologyDefinition.cacheCatNameUsersBolt, new CacheCatNameUsersBolt(), 2)
                .fieldsGrouping(TopologyDefinition.countCatNameUsersBolt, new Fields("fullCatNameKey"));
    }

    private static Config getConfig() {
        Config conf = new Config();
        //2个worker进程
        Config.setNumWorkers(conf, 2);
        conf.setDebug(true);
        conf.put("kafka.consumer.timeout", "10000");
        conf.put("kafka.topics", "flume-20170101");
        conf.put("bootstrap.servers", kafkaCluster);
        //设置consumer group name,必须设置
        conf.put("group.id", consumerGroupId);
        //设置自动提交为false 采用手动提交
        conf.put("enable.auto.commit", "false");
        //设置key以及value的解析（反序列）类
        conf.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        conf.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //设置心跳时间
        conf.put("session.timeout.ms", "30000");
        return conf;
    }

}
