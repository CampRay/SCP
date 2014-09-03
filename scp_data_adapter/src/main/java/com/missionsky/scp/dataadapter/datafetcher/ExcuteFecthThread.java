package com.missionsky.scp.dataadapter.datafetcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.common.SingleInstance;
import com.missionsky.scp.dataadapter.common.SystemConstants;
import com.missionsky.scp.dataadapter.datafilter.DataFilterApi;
import com.missionsky.scp.dataadapter.entity.DataSource;

/**
 * @author Ellis Xie
 * @version 1.0
 * data-adapter线程
 */
public class ExcuteFecthThread extends Thread {

	private DataSource dataSource;

	private DataFetcher dataFetcher;
	
	private Logger logger = LoggerFactory.getLogger(ExcuteFecthThread.class);
	
	public ExcuteFecthThread(DataSource dataSource, DataFetcher dataFetcher) {
		this.dataSource = dataSource;
		this.dataFetcher = dataFetcher;
	}

	@Override
	public void run() {
		HashMap<String, DataSource> dataSources = SingleInstance.getDataSources();
		Integer step = dataSource.getStep();
		Integer offset = 0;
		
		if (dataSources.get(dataSource.getName()).getIsFirst()) {
			Integer total = divideProcess(offset, step);
			
			dataSource.setIsFirst(false);
			dataSource.setTotal(total);
			dataSources.put(dataSource.getName(), dataSource);
			SingleInstance.setDataSources(dataSources);
		} else {
			Integer currentTotal = dataFetcher.getTotal(dataSource);
			
			if (dataSource.getTotal() != null && currentTotal != null) {
				if (dataSource.getTotal() < currentTotal) {
					// 增量update,保存增加的数据
					divideProcess(dataSource.getTotal(), step);
				} else if (dataSource.getTotal() > currentTotal) {
					divideProcess(offset, step);
				}
			}

		}
	}
	
	// 分块处理
	protected Integer divideProcess(Integer offset, Integer step){
		Integer total = 0;
		Integer result = null;
		if (step != null && step > 0) {
			do {
				result = dataProcess(offset);
				offset += step;
				total += result;
			} while (result.equals(step));
		} else {
			total = dataProcess(null);
		}
		return total;
	}
	
	// 处理数据
	protected Integer dataProcess(Integer offset) {
		List<Map<String, String>> records = dataFetcher.renderMergedFetchedData(dataSource, offset);
		logger.info("Save data to hdfs.");
		DataFilterApi.filterClassifiedStorage(records, dataSource.getName(), SystemConstants.DATA_FLITER_TYPE_INSERT);

		return records.size();
	}

}
