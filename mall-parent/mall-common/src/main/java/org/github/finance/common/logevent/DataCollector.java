package org.github.finance.common.logevent;

import java.util.Map;

/**
 * @author ligaofeng 2017年1月17日 下午1:16:45
 */
public interface DataCollector {

    /**
     * @param dataProvider
     * @return
     */
    public String collectData(DataCollectorProvider dataProvider);

    /**
     * @author ligaofeng 2017年1月17日 下午1:30:57
     */
    public interface DataCollectorOut {

        /**
         * @param metaData
         */
        public void sysoutMetaData(String metaData);
    }

    /**
     * @author ligaofeng 2017年1月17日 下午1:17:27
     */
    public interface DataCollectorProvider {

        /**
         * 日志事件
         * 
         * @return
         */
        public String getLogEvent();

        /**
         * 元数据
         * 
         * @return
         */
        public Map<String, String> getMetaData();
    }
}
