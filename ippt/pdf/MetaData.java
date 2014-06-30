package ippt.pdf;

import java.util.List;

public class MetaData
{
	List<Section> titles = null;
	List<Section> content = null;
	List<Section> headers = null;
	List<Section> footers = null;
	
	public MetaData()
	{
		
	}
	
	public MetaData(List<Section> titles, List<Section> content, List<Section> headers, List<Section> footers)
	{
		this.titles = titles;
		this.content = content;
		this.headers = headers;
		this. footers = footers;
	}
	
	public void setTitles(List<Section> titles)
	{
		this.titles = titles;
	}
	
	public void setContent(List<Section> content)
	{
		this.content = content;
	}
	
	public void setHeaders(List<Section> headers)
	{
		this.headers = headers;
	}
	
	public void setFooters(List<Section> footers)
	{
		this.footers = footers;
	}
	
	public void addTitle(Section title)
	{
		titles.add(title);
	}
	
	public void addContent(Section content)
	{
		this.content.add(content);
	}
	
	public void addHeader(Section header)
	{
		headers.add(header);
	}
	
	public void addFooter(Section footer)
	{
		footers.add(footer);
	}
	
	public List<Section> getTitles()
	{
		return titles;
	}
	
	public List<Section> getContent()
	{
		return content;
	}
	
	public List<Section> getHeaders()
	{
		return headers;
	}
	
	public List<Section> getFooters()
	{
		return footers;
	}
	
}