package ippt.pdf;

import at.ac.tuwien.dbai.pdfwrap.model.document.TextBlock;

public class Image
{
	TextBlock caption = null;
	String path = null;
	ClusterInfo imageClusterInformation = null;
	
	public Image()
	{
		
	}
	
	public Image(TextBlock caption, String path, ClusterInfo imageClusterInformation)
	{
		this.caption = caption;
		this.path = path;
		this.imageClusterInformation = imageClusterInformation;
	}
	
	public void setCaption(TextBlock caption)
	{
		this.caption = caption;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public void setImageClusterInformation(ClusterInfo imageClusterInformation)
	{
		this.imageClusterInformation = imageClusterInformation;
	}
	
	public TextBlock getCaption()
	{
		return caption;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public ClusterInfo getImageClusterInformation()
	{
		return imageClusterInformation;
	}
}