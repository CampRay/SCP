package com.missionsky.scp.dataanalysis.algorithm;

import org.apache.mahout.common.AbstractJob;

import com.missionsky.scp.dataanalysis.entity.AlgorithmInfoAnnotation;
import com.missionsky.scp.dataanalysis.entity.AlgorithmType;

/**
 * @author ellis.xie 
 * @version 1.0
 * Miner Algorithm Interface
 */

@AlgorithmInfoAnnotation(name="MinerAlgorithm", algorithmType=AlgorithmType.MinerAlgorithm, description="Miner Algorithm Interface.")
public abstract class MinerAlgorithm extends AbstractJob implements Algorithm {
	
	

}
