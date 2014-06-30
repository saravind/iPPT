package ippt.pdf;

public class ClusterInfo
{
	int clusterLineCount;
	int clusterTextBlockCount;
	int clusterImageCount;
	double averageClusterSeparation;
		
	double x1;
	double x2;
	double y1;
	double y2;
	
	public ClusterInfo(double x1, double y1, double x2, double y2, int clusterLineCount, int clusterImageCount, int clusterTextBlockCount, double averageClusterSeparation)
	{
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.clusterLineCount = clusterLineCount;
		this.clusterTextBlockCount = clusterTextBlockCount;
		this.averageClusterSeparation = averageClusterSeparation;
	}
	
	
	public String toString()
	{
		return "clsuterInformation - x1 : " + x1 + " y1 : " + y1 + " x2 : " + x2 + " y2 : " + y2 + " clusterLineCount : " + clusterLineCount + " clusterTextBlockCount : " + clusterTextBlockCount + " clusterImageCount : " + clusterImageCount + " averageClusterSeparation : " + averageClusterSeparation;
	}
	
	
	public void setX1(double x1)
	{
		this.x1 = x1;
	}
	
	
	public void setY1(double y1)
	{
		this.y1 = y1;
	}
	
	
	public void setX2(double x2)
	{
		this.x2 = x2;
	}
	
	
	public void setY2(double y2)
	{
		this.y2 = y2;
	}
	
	
	public void setClusterLineCount(int clusterLineCount)
	{
		this.clusterLineCount = clusterLineCount;
	}
	
	
	public void setClusterTextBlockCount(int clusterTextBlockCount)
	{
		this.clusterTextBlockCount = clusterTextBlockCount;
	}	
	
	public void setClusterImageCount(int clusterImageCount)
	{
		this.clusterImageCount = clusterImageCount;
	}
	
	public void setAverageClusterSeparation(double averageClusterSeparation)
	{
		this.averageClusterSeparation = averageClusterSeparation;
	}
	
	public double getX1()
	{
		return x1;
	}
	
	public double getY1()
	{
		return y1;
	}
	
	public double getX2()
	{
		return x2;
	}
	
	public double getY2()
	{
		return y2;
	}
	
	public int getClusterLineCount()
	{
		return clusterLineCount;
	}
	
	public int getClusterImageCount()
	{
		return clusterImageCount;
	}
	
	public int getClusterTextBlockCount()
	{
		return clusterTextBlockCount;
	}
	
	public double getAverageClusterSeparation()
	{
		return averageClusterSeparation;
	}
}