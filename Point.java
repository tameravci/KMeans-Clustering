import java.util.Arrays;

 
public class Point {
 
    private double[] coord;
    private int cluster_number = 0;
    
    public Point(double[] coord) {
    	this.coord = coord;
    }
 
    public void setCoord(double[] coord) {
    	this.coord = coord;
    }
    
    public double[] getCoord() {
    	return coord;
    }
    
    public void setCluster(int n) {
        this.cluster_number = n;
    }
    
    public int getCluster() {
        return this.cluster_number;
    }
    
    //Calculates the distance between two points.
    protected static double distance(Point p, Point centroid) {
    	
    	double distance = 0;
    	
    	for(int i=0; i<p.getCoord().length; i++) {
    		distance += Math.pow(p.getCoord()[i]-centroid.getCoord()[i], 2.0);
    	}
 
    	return Math.sqrt(distance);
    }
    
    public String toString() {
    	return Arrays.toString(coord);
    }
}