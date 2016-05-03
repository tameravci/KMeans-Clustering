import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

public class AvciKMeans {

	private ArrayList<Point> allPoints;
	private ArrayList<Cluster> allClusters;
	
	public AvciKMeans() {
		this.allPoints = new ArrayList<Point>();
		this.allClusters = new ArrayList<Cluster>();
	}
	/**
	 * Reads the data, initializes k-means, runs the algorithm 50 times
	 * to find the best k clusters with minimum sse
	 * @param args input file, k, output file
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
	
	
		String input = args[0]; 
		int k = Integer.parseInt(args[1]); 
		String output = args[2];
		System.setOut(new PrintStream(new FileOutputStream(output)));
		
		TreeMap<Double, AvciKMeans> tree = new TreeMap<Double, AvciKMeans>(); //to find the best k-clusters
		
		int iter = 0; 


		
		while(iter<50) { //Run k-means algorithm 50 times
			
			AvciKMeans kmeans = new AvciKMeans();
			kmeans.loadData(input);
			
			kmeans.createClusters(k);
			
			kmeans.assignPoints();

			double sse;
			
			int[] centroids_prev = kmeans.calculateCentroid(k);
			kmeans.assignPoints();
			
			
			while(true) {
				
				int[] centroids = kmeans.calculateCentroid(k);
				kmeans.assignPoints();
				
				if(Arrays.equals(centroids,centroids_prev)) //if centroids do not move anymore
					break;
				else {
					centroids_prev = centroids;
				}
				
			}
			sse = kmeans.evaluate(); //evaluate the quality
			tree.put(sse, kmeans); //put it into the tree
			iter++;
		}
		
		
		AvciKMeans kmeans = tree.get(tree.firstKey()); //Best k-means with lowest sse
		int i=0;
		while(i!=k) {
			
			kmeans.allClusters.get(i).plotCluster(); //describe the clusters
			i++;
		}
		
		System.out.println("SSE equals " + kmeans.evaluate());
		

	}

	/**
	 * Evaluates the within clusters sum of squared errors
	 * @return
	 */
	private double evaluate() {
		
		double SSE = 0;
		for(Cluster cluster: allClusters) {
			for(Point p : cluster.getPoints()) {
				SSE += Math.pow(Point.distance(p, cluster.getCentroid()), 2);
			}
		}
		return SSE;
	}

	/**
	 * Adjusts the centroids based on the average of the points in the clusters
	 * @param k
	 * @return an array of cluster sizes
	 */
	private int[] calculateCentroid(int k) {
		
		int numAttr = allPoints.get(0).getCoord().length;
		int[] centroids = new int[k];
		int j = 0;
		for(Cluster cluster : allClusters) {
			
			double[] coord = new double[numAttr];
			for(Point p : cluster.getPoints()) {
				for(int i = 0; i<numAttr; i++) {
					coord[i] += p.getCoord()[i];
				}
			}
			
			if(cluster.getPoints().size()!=0) {
				for(int i = 0; i<numAttr; i++) {
						coord[i] = coord[i] / ((double) cluster.getPoints().size());
				}
				cluster.setCentroid(new Point(coord));
			}
			centroids[j] = cluster.getPoints().size();
			j++;
		}
		
		return centroids;
	}

	
	/**
	 * assigns points to the clusters based on euclidan distance
	 */
	private void assignPoints() {
		
		for(Cluster cluster : allClusters) {
			cluster.setPoints(new ArrayList<Point>());
		}
		
		
		for(Point p : allPoints) {
			double distance = Double.MAX_VALUE;
			int k=0;
			for(Cluster cluster : allClusters) {
				double candidate = Point.distance(p, cluster.getCentroid());
				if(candidate<distance) {
					distance = candidate;
					k = cluster.getId();
				}
			}
			allClusters.get(k).addPoint(p);
		}
		
	}

	/**
	 * randomly generates clusters from the points in the dataset
	 * @param k
	 */
	private void createClusters(int k) {
		HashSet<Integer> set = new HashSet<Integer>();
		int numAttr = allPoints.get(0).getCoord().length;
		int cluster = k;
		k=0;
		while(k!=cluster) {
			int rand = (int) (Math.random() * allPoints.size());
			if(set.contains(rand))
				continue;
			else
				set.add(rand);
			double[] coord = allPoints.get(rand).getCoord().clone();
			Cluster newCluster = new Cluster(k);
			newCluster.setCentroid(new Point(coord));
			allClusters.add(newCluster);
			k++;
		}
		
	}
	/**
	 * Reads the input
	 * @param input
	 * @throws IOException
	 */
	private void loadData(String input) throws IOException {
		Scanner scan = new Scanner(new File(input));
		while(scan.hasNextLine()) {
			
			String line = scan.nextLine();
			String record[]  = line.split(",");
			double[] coord = new double[record.length-1]; //if there is a class label
			
			for(int i=0; i<record.length-1; i++) {
				coord[i] = Double.parseDouble(record[i]);
			}
			
			allPoints.add(new Point(coord));
			
		}
		
	}

}
