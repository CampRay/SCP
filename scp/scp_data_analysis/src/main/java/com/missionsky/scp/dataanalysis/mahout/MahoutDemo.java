package com.missionsky.scp.dataanalysis.mahout;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class MahoutDemo {
	
	public static void main(String [] args){
		try {
			DataModel model = new FileDataModel(new File("/home/hadoop/data.csv"));
			UserSimilarity similarity = new CachingUserSimilarity(new SpearmanCorrelationSimilarity(model),model);
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
			Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
			List<RecommendedItem> recommendedItems = recommender.recommend(1, 2);
			for (RecommendedItem recommendedItem : recommendedItems) {
				System.out.println(recommendedItem);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	

}
