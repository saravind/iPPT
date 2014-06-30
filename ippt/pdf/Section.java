package ippt.pdf;

import java.util.List;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextBlock;

public class Section
{
	TextBlock heading = null;
	List<TextBlock> content = null;
	List<Section> subSections = null;
	List<Image> images = null;
	
	public Section()
	{
		
	}
	
	
	public Section(TextBlock heading, List<TextBlock> content, List<Section> subSections)
	{
		this.heading = heading;
		this.content = content;
		this.subSections = subSections;
	}
	
	
	public Section(TextBlock heading, List<TextBlock> content, List<Section> subSections, List<Image> images)
	{
		this.heading = heading;
		this.content = content;
		this.subSections = subSections;
		this.images = images;
	}

	
	public void setHeading(TextBlock heading)
	{
		this.heading = heading;
	}
	
	
	public void setContent(List<TextBlock> content)
	{
		this.content = content;
	}

	
	public void setSubSections(List<Section> subSections)
	{
		this.subSections = subSections;
	}

	
	public void setImages(List<Image> images)
	{
		this.images = images;
	}
	
	
	public List<TextBlock> getContent()
	{
		return content;
	}
		
	
	public List<Section> getSubSections()
	{
		return subSections;
	}
	
	
	public List<Image> getImages()
	{
		return images;
	}

	
	public TextBlock getHeading()
	{
		return heading;
	}
}