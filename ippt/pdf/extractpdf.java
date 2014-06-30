package ippt.pdf;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Array;
import java.net.ContentHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.modelmbean.XMLParseException;
import javax.swing.text.Document;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stax.StAXSource;

import jj2000.j2k.util.StringFormatException;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.PDFImageWriter;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.apache.pdfbox.util.TextPosition;
import org.apache.poi.hdf.extractor.NewOleFile;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import at.ac.tuwien.dbai.pdfwrap.ProcessFile;
import at.ac.tuwien.dbai.pdfwrap.analysis.PageProcessor;
import at.ac.tuwien.dbai.pdfwrap.pdfread.PDFObjectExtractor;
import at.ac.tuwien.dbai.pdfwrap.exceptions.DocumentProcessingException;
import at.ac.tuwien.dbai.pdfwrap.model.document.GenericSegment;
import at.ac.tuwien.dbai.pdfwrap.model.document.IXHTMLSegment;
import at.ac.tuwien.dbai.pdfwrap.model.document.ImageSegment;
import at.ac.tuwien.dbai.pdfwrap.model.document.LineFragment;
import at.ac.tuwien.dbai.pdfwrap.model.document.LineSegment;
import at.ac.tuwien.dbai.pdfwrap.model.document.Page;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextBlock;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextFragment;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextLine;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextSegment;
import at.ac.tuwien.dbai.pdfwrap.model.graph.AdjacencyGraph;
import at.ac.tuwien.dbai.pdfwrap.pdfread.PDFPage;
import at.ac.tuwien.dbai.pdfwrap.utils.Utils;

public class extractpdf
{	
	static String fileName = "";
	
	static List<List<List<GenericSegment>>> doc = null;
	
	static List<Section> logicalStructure = null;
	static MetaData meta = new MetaData();
	static List<List<Image>> pagewiseImages = new ArrayList<List<Image>>();
	static Section remainingImagesSection = null;
	
	static List<GenericSegment> textBlocks = null;
	static List<GenericSegment> lineSegments = null;
	static List<GenericSegment> imageSegments = null;
	
	static List<Page> sortedPagewiseTextBlocks = null;
	static List<List<List<GenericSegment>>> pagewiseUpperLimits = new ArrayList<List<List<GenericSegment>>>();
	static List<List<List<GenericSegment>>> pagewiseLowerLimits = new ArrayList<List<List<GenericSegment>>>();
	static PDDocument pdfBoxDocument;
	static String pdfBoxPassword;
	static String outputPrefix;
	
	static Stack<Section> sectionHierarchy = null;
	static Stack<String> sectionHeadingPrefixHierarchy = null;
	static String currentSectionHeadingPrefix = null;
	static boolean regEx1Matched = false;
	
	static List<Page> pagewiseTextBlocks = null;
	static List<Page> pagewiseLineSegments = null;
	static List<Page> pagewiseImageSegments = null;
	
	static int pagewiseLineCount[] = null;
	static int documentLineCount = 0;
	static double averageFontSize = 0.0;
	
	Set<Double> fontSizeSet = null;
	static List<Double> documentWideFontSizeOrdering = new ArrayList<Double>();
	List<Double> fontSizeOrder;
	static List<List<Double>> pagewiseFontSizeOrdering = new ArrayList<List<Double>>();
		
	static List<Integer> documentWideFontFrequencyOrdering = null;
	List<Integer> fontOrder = null;
	static List<List<Integer>> pagewiseFontFrequencyOrdering = new ArrayList<List<Integer>>();
	
	static Map<List<GenericSegment>, ClusterInfo> clusterInfoMap = new HashMap<List<GenericSegment>, ClusterInfo>();
	static Set<Image> associatedImageSet = new HashSet<Image>();
	static List<Set<List<GenericSegment>>> pagewiseDirtyClusterSet = new ArrayList<Set<List<GenericSegment>>>(); 
	
	static Map<String, Integer> documentWideFontMap = new HashMap<String, Integer>();
	Map<String, Integer> fontMap = null;
	static List<Map<String, Integer>> pagewiseFontMap = new ArrayList<Map<String,Integer>>();
	
	
	public extractpdf()
	{
		
	}
	
		
	public extractpdf(String inputFile, String outputFile)
	{
		//Fix Call to PosTaggerPR
		
		ArrayList<ArrayList<String>> annotationDocument = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> annotationTags = new ArrayList<ArrayList<String>>();
		
		// new PosTaggerPR(PosTaggingOperations.ANNOTATE, true, inputFile, PosOutputFile, true, annotationDocument, annotationTags, unigramCounts);
		
		//Fix PosTaggerPR interface to possibly return document and tag arraylists back to summarizer.
		
		//Fix PosTaggerPR to maintain Unigram counts across entire document.
		
		
		
		
	}
	
		
	public static void main(String[] args) throws Exception
    {
    	boolean toConsole = false;
//        boolean table = false;
//        boolean autotable = false;
    	boolean toXHTML = true;
        boolean borders = true;
        boolean rulingLines = true;
        boolean processSpaces = false;
        int currentArgumentIndex = 0;
        String password = "";
        String encoding = ProcessFile.DEFAULT_ENCODING;
        PDFObjectExtractor extractor = new PDFObjectExtractor();
        String inFile = null;
        String outFile = null;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        for( int i=0; i<args.length; i++ )
        {
            if( args[i].equals( ProcessFile.PASSWORD ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                password = args[i];
            }
            else if( args[i].equals( ProcessFile.ENCODING ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                encoding = args[i];
            }
            else if( args[i].equals( ProcessFile.START_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                startPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( ProcessFile.END_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                endPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( ProcessFile.CONSOLE ) )
            {
                toConsole = true;
            }
            /*
            else if( args[i].equals( AUTOTABLE ))
            {
                autotable = true;
            }
            else if( args[i].equals( TABLE ))
            {
                table = true;
            }
            */
            else if( args[i].equals( ProcessFile.NOBORDERS ))
            {
            	borders = false;
            }
            /*else if( args[i].equals( XMILLUM ) )
            {
                toXHTML = false;
            }*/
            else if( args[i].equals( ProcessFile.NORULINGLINES ))
            {
            	rulingLines = false;
            }
            else if( args[i].equals( ProcessFile.PROCESS_SPACES ))
            {
            	processSpaces = false;
            }
            else
            {
                if( inFile == null )
                {
                    inFile = args[i];
                }
                else
                {
                    outFile = args[i];
                }
            }
        }

        if( inFile == null )
        {
            usage();
        }

        if( outFile == null && inFile.length() >4 )
        {
            outFile = inFile.substring( 0, inFile.length() -4 ) + ".txt";
        }
        
        // decide whether we have a pdf or image (TODO: command-line override)
        /*
        boolean pdf = true;
		if (inFile.endsWith("png") ||
			inFile.endsWith("tif") ||
			inFile.endsWith("tiff")||
			inFile.endsWith("jpg") ||
			inFile.endsWith("jpeg")||
			inFile.endsWith("PNG") ||
			inFile.endsWith("TIF") ||
			inFile.endsWith("TIFF") ||
			inFile.endsWith("JPG") ||
			inFile.endsWith("JPEG")) pdf = false;
		*/
        
//		System.err.println("Processing: " + inFile);
		
        // load the input file
        
        File inputFile = new File(inFile);
        
        fileName = inFile;
        /*
        STR_INFILE = inputFile.getCanonicalPath();
        File tempOutFile = new File(outFile); // tmp for str only
        if (tempOutFile.getParent() != null)
        	STR_OUTPUT_PATH = tempOutFile.getParent();
        */
        byte[] inputDoc = ProcessFile.getBytesFromFile(inputFile);

//        org.w3c.dom.Document resultDocument = null;
        
        List<Page> theResult;
        
        // set up page processor object
        PageProcessor pp = new PageProcessor();
        pp.setProcessType(PageProcessor.PP_BLOCK);
        //pp.setProcessType(PageProcessor.PP_FRAGMENT);
        pp.setRulingLines(rulingLines);
        pp.setProcessSpaces(processSpaces);
        // no iterations should be automatically set to -1
        
        // do the processing
    	theResult =
    		ProcessFile.processPDFtoPageList(inputDoc, pp, borders,
    		startPage, endPage, encoding, password);
    	
    	//System.out.println(theResult);
    	
    	List<List<MyTextLine>> pages = pdfboxExtract(inFile);
    	
    	//System.out.println(pages);
    	
    	new extractpdf().preprocess(theResult, pages);
    	
    	cluster();
    	
//    	cleanClusters();
    	
    	processImages();
    	
    	tagContent();
    	
    }
	
	
	private static void usage()
    {
        System.err.println( "Usage: java at.ac.tuwien.dbai.pdfwrap.ProcessFile [OPTIONS] <PDF file> [Text File]\n" +
            "  -password  <password>        Password to decrypt document\n" +
            "  -encoding  <output encoding> (ISO-8859-1,UTF-16BE,UTF-16LE,...)\n" +
            "  -norulinglines               do not process ruling lines\n" +
            "  -spaces                      split low-level segments according to spaces\n" +
            "  -console                     Send text to console instead of file\n" +
            "  -startPage <number>          The first page to start extraction(1 based)\n" +
            "  -endPage <number>            The last page to extract(inclusive)\n" +
            "  <PDF file>                   The PDF document to use\n" +
            "  [Text File]                  The file to write the text to\n"
            );
        System.exit( 1 );
    }
	
		
	public static List<List<MyTextLine>> pdfboxExtract(String inputFile) throws Exception
    {
        boolean toConsole = false;
        boolean toHTML = false;
        boolean sort = false;
        boolean separateBeads = true;
        boolean force = false;
        
        pdfBoxPassword = "";
        String encoding = null;
        //String pdfFile = null;
        String outputFile = null;
        
        List<List<List<TextPosition>>> retrievedPages = null;
        
        List<List<MyTextLine>> pages = null;
        
        // Defaults to text files
        String ext = ".txt";
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        
    	Writer output = null;
        pdfBoxDocument = null;
        
        outputPrefix = inputFile;
        
        try
        {
            try
            {
                //basically try to load it from a url first and if the URL
                //is not recognized then try to load it from the file system.
                //URL url = new URL( pdfFile );
            	URL url = new URL( inputFile );
                pdfBoxDocument = PDDocument.load(url, force);
                String fileName = url.getFile();
                if( outputFile == null && fileName.length() >4 )
                {
                    outputFile = new File( fileName.substring( 0, fileName.length() -4 ) + ext ).getName();
                }
            }
            catch( MalformedURLException e )
            {
//                document = PDDocument.load(pdfFile, force);
//                if( outputFile == null && pdfFile.length() >4 )
//                {
//                    outputFile = pdfFile.substring( 0, pdfFile.length() -4 ) + ext;
//                }
            	
            	pdfBoxDocument = PDDocument.load(inputFile, force);
                if( outputFile == null && inputFile.length() >4 )
                {
                    outputFile = inputFile.substring( 0, inputFile.length() -4 ) + ext;
                }
            }

            if( pdfBoxDocument.isEncrypted() )
            {
                StandardDecryptionMaterial sdm = new StandardDecryptionMaterial( pdfBoxPassword );
                pdfBoxDocument.openProtection( sdm );
                AccessPermission ap = pdfBoxDocument.getCurrentAccessPermission();

                if( ! ap.canExtractContent() )
                {
                    throw new IOException( "You do not have permission to extract text" );
                }
            }

            Writer output1 = new OutputStreamWriter( System.out );
            
            //PDFTextStripper stripper = new PDFTextStripper(encoding);
            PDFTextStripper stripper = new PDFTextStripper(encoding);
            
            stripper.setForceParsing( force );
            stripper.setSortByPosition( sort );
            stripper.setShouldSeparateByBeads( separateBeads );
            stripper.setStartPage( startPage );
            stripper.setEndPage( endPage );

            retrievedPages = stripper.retrieveText( pdfBoxDocument, output1 );                
        }
        finally
        {
            if( output != null )
            {
                output.close();
            }
//            if( pdfBoxDocument != null )
//            {
//                pdfBoxDocument.close();
//            }
        }
        
        System.out.println();
        System.out.println();
        
        pages = new ArrayList<List<MyTextLine>>();
        
        for(List<List<TextPosition>> page : retrievedPages)
        {
        	List<MyTextLine> curPage = new ArrayList<MyTextLine>();
        	
        	for(List<TextPosition> line : page)
        	{
        		
        		MyTextLine tempLine = new MyTextLine(line);
        		
//        		MyTextLine tempLine = new MyTextLine();
//        		
//        		TextPosition firstCharacter = line.iterator().next();
//        		TextPosition lastCharacter = null;
//        		
//        		String tempText = "";
//        		
//        		tempLine.setX1(firstCharacter.getX());
//        		tempLine.setY1(firstCharacter.getY());
//        		
//        		for(TextPosition curPosition : line)
//        		{
//        			if(curPosition.toString() != null)
//        				System.out.println(curPosition.getCharacter() + " " + curPosition.getX() + " " + curPosition.getY());
//        				//System.out.println("here");
//        			System.out.println();
//        			if(curPosition.toString() == null)
//        				tempText += " ";
//        			else
//        				tempText += curPosition.getCharacter();
//        			
//        			//lastCharacter = curPosition;
//        		}
//        		
//        		tempLine.setX2(line.get(line.size() - 1).getX());
//        		tempLine.setY2(line.get(line.size() - 1).getY());
//        		tempLine.setText(tempText);        		
        		
        		curPage.add(tempLine);
        		
//        		MyTextLine curInserted = curPage.get((curPage.size() - 1));
//        		        		
//        		System.out.println(curInserted.getText() + " " + curInserted.getX1() + " " + curInserted.getY1() + " " + curInserted.getX2() + " " + curInserted.getY2());
//        		
//        		new BufferedReader(new InputStreamReader(System.in)).readLine();
        		
        	}
        	
        	pages.add(curPage);
        	
        }
        
        
        return pages;
    }
		
	
	public void preprocess(List<Page> theResult, List<List<MyTextLine>> pages)
	{
		
		List<List<MyTextLine>> pagesCopy = new ArrayList<List<MyTextLine>>(pages);
		TextBlock textBlock = null;
		List<MyTextLine> pdfBoxPage = null;
		Page pdfXtkPage = null;
		double curPdfXtkPageHeight = 0.0;
		
		System.out.println();
		System.out.println();
		System.out.println("Start of preprocess!!!");
		
		try
		{
			int pageCount = 0;
			
			sortedPagewiseTextBlocks = new ArrayList<Page>();
			
			pagewiseTextBlocks = new ArrayList<Page>();
			pagewiseLineSegments = new ArrayList<Page>();
			pagewiseImageSegments = new ArrayList<Page>();
			
			//curPdfXtkPageHeight = theResult.iterator().next().getHeight();
					
			for(Page page : theResult)
			{
				List<GenericSegment> pageItems = page.getItems();
				
				textBlocks = new ArrayList<GenericSegment>();
				lineSegments = new ArrayList<GenericSegment>();
				imageSegments = new ArrayList<GenericSegment>();
				
				for(GenericSegment item : pageItems)
				{
					if (item instanceof TextBlock) 
					{
						textBlocks.add((TextBlock) item);
					}					
					else if (item instanceof LineSegment) 
					{
						lineSegments.add((LineSegment) item);
					}					
					else if (item instanceof ImageSegment) 
					{
						imageSegments.add((ImageSegment) item);
					}				
				}
				
				Page tempPage = new Page(page.getX1(), page.getX2(), page.getY1(), page.getY2(), imageSegments);
				pagewiseImageSegments.add(tempPage);
				tempPage = new Page(page.getX1(), page.getX2(), page.getY1(), page.getY2(), textBlocks);
				pagewiseTextBlocks.add(tempPage);
				tempPage = new Page(page.getX1(), page.getX2(), page.getY1(), page.getY2(), lineSegments);
				pagewiseLineSegments.add(tempPage);
				
//				System.out.println(tempPage.getHeight() + " : " + tempPage.getWidth() + " : " + tempPage.getArea());
				
				pageCount++;				
			}
			
			Iterator<Page> pagewiseTextBlockIterator = pagewiseTextBlocks.iterator();
			Iterator<List<MyTextLine>> pdfboxPageIterator = pagesCopy.iterator();
			
			int counter = 0;
			
			while(pdfboxPageIterator.hasNext() && pagewiseTextBlockIterator.hasNext())
			{	
				counter++;
//				System.out.println("Fetching new Page - Page : " + (counter));
				
				pdfXtkPage = (Page) pagewiseTextBlockIterator.next(); 
				pdfBoxPage = pdfboxPageIterator.next();
				
				fontMap = new HashMap<String, Integer>();
												
				curPdfXtkPageHeight = pdfXtkPage.getHeight();
//				System.out.println("PdfXtkPageheight : " + curPdfXtkPageHeight);
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
				List<GenericSegment> pdfXtkTextBlocks = pdfXtkPage.getItems();
				List<GenericSegment> sortedTextBlocks = new ArrayList<GenericSegment>();
				
				List<List<GenericSegment>> upperLimits = new ArrayList<List<GenericSegment>>();
				List<List<GenericSegment>> lowerLimits = new ArrayList<List<GenericSegment>>();
				
				pdfBoxLineLoop : for( int pdfBoxLineIndex = 0 ; pdfBoxLineIndex < pdfBoxPage.size() ; pdfBoxLineIndex++ )
				{
					pdfXtkTextBlockLoop : for(int pdfXtkBlockIndex = 0 ; pdfXtkBlockIndex < pdfXtkTextBlocks.size() ; pdfXtkBlockIndex++)
					{	
						MyTextLine curPdfBoxLine;
						GenericSegment curPdfXtkTextBlock;
						
						if(pdfBoxLineIndex < pdfBoxPage.size())
						{
							curPdfBoxLine = pdfBoxPage.get(pdfBoxLineIndex);
						}
						else
						{
//							System.out.println("pdfBoxLineIndex out of bounds, pdfBoxLineIndex = " + pdfBoxLineIndex + " pdfBoxPage list size : "  + pdfBoxPage.size());
							//new BufferedReader(new InputStreamReader(System.in)).readLine();
							break pdfBoxLineLoop;
						}
						
						if(pdfXtkBlockIndex < pdfXtkTextBlocks.size())
						{
							curPdfXtkTextBlock = pdfXtkTextBlocks.get(pdfXtkBlockIndex);
						}
						else
						{
//							System.out.println("pdfXtkBlockIndex out of bounds, pdfXtkBlockIndex = " + pdfXtkBlockIndex + " pdfXtkTextBlocks list size : "  + pdfXtkTextBlocks.size());
							//new BufferedReader(new InputStreamReader(System.in)).readLine();
							break pdfXtkTextBlockLoop;
						}
						
//						if(!Float.isNaN((((TextBlock)curPdfXtkTextBlock).getFontSize())))
//						{
//							System.out.println("key : " + curPdfXtkTextBlock);
//							
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
//							
//							if(!fontMap.containsKey((Math.round((double)(((TextBlock)curPdfXtkTextBlock).getFontSize())) * 1000) / (double)1000))
//							{
//								fontMap.put((Math.round((double)(((TextBlock)curPdfXtkTextBlock).getFontSize())) * 1000) / (double)1000, 1);
//							}
//							else
//							{
//								int count = fontMap.get((Math.round((double)(((TextBlock)curPdfXtkTextBlock).getFontSize())) * 1000) / (double)1000).intValue();
//								count++;
//								fontMap.put((Math.round((double)(((TextBlock)curPdfXtkTextBlock).getFontSize())) * 1000) / (double)1000, count);
//							}
//						}
						
//						System.out.println(curPdfBoxLine.getText() + " " + curPdfBoxLine.getX1() + " " + curPdfBoxLine.getY1() + " " + curPdfBoxLine.getX2() + " " + curPdfBoxLine.getY2());
						
//						System.out.println(((TextBlock)curPdfXtkTextBlock).getText() + " " + ((TextBlock)curPdfXtkTextBlock).getX1() + " " + (curPdfXtkPageHeight - ((TextBlock)curPdfXtkTextBlock).getY1()) + " " + ((TextBlock)curPdfXtkTextBlock).getX2() + " " + (curPdfXtkPageHeight - ((TextBlock)curPdfXtkTextBlock).getY2() + curPdfXtkPageHeight));

//						new BufferedReader(new InputStreamReader(System.in)).readLine();
						
						List<TextLine> curPdfXtkTextBlockLines;
						
						TextLine firstLine = ((TextBlock)curPdfXtkTextBlock).getItems().iterator().next();
						
						if(((double)Math.round(curPdfBoxLine.getX1() * 100) / 100 == (double)Math.round((firstLine.getX1()) * 100) / 100) 
								&& ((double)Math.round(curPdfBoxLine.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - firstLine.getY1()) * 100) / 100)
								&& (((double)Math.round(curPdfBoxLine.getX2() * 100) / 100 - (double)Math.round((firstLine.getX2()) * 100) / 100) < 10 || (-(double)Math.round(curPdfBoxLine.getX2() * 100) / 100 + (double)Math.round((firstLine.getX2()) * 100) / 100) > -10)
								&& ((double)Math.round(curPdfBoxLine.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - firstLine.getY2() + firstLine.getFontSize()) * 100) / 100))
						{
//							System.out.println(curPdfBoxLine.getText() + " " + curPdfBoxLine.getX1() + " " + curPdfBoxLine.getY1() + " " + curPdfBoxLine.getX2() + " " + curPdfBoxLine.getY2());
//							System.out.println(firstLine);
							
//							System.out.println(((TextBlock)curPdfXtkTextBlock).getText());
							
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
							
//							System.out.println();
//							System.out.println();
//							System.out.println("Continuing matching!!");
//							
//							System.out.println("No of pdfbox lines : " + pdfBoxPage.size());
							
							int lineCount = 0;
							
							curPdfXtkTextBlockLines = ((TextBlock) curPdfXtkTextBlock).getItems();
							
							for(TextLine curPdfXtkLine : curPdfXtkTextBlockLines)
							{								
								if(!Float.isNaN(curPdfXtkLine.getFontSize()))
								{
									String key = curPdfXtkLine.getFontName() + ":" + (Math.round(curPdfXtkLine.getFontSize() * 10000) / (double)10000);
									
//									System.out.println(key + " : " + key.hashCode());
//									new BufferedReader(new InputStreamReader(System.in)).readLine();
									
									if(!fontMap.containsKey(key))
									{
//										System.out.println("NEW ENTRY !! K : " + key + " V : 1");
										fontMap.put(key, 1);
									}
									else
									{
										int count = fontMap.get(key).intValue();
//										System.out.println("Already present");
										count++;
										fontMap.put(key, count);
//										count = fontMap.get(curPdfXtkLine.getFontSize()).intValue();
//										System.out.println("Updated count : " + count);
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
									}
									
									if(!documentWideFontMap.containsKey(key))
									{
//										System.out.println("NEW ENTRY !! K : " + key + " V : 1");
										documentWideFontMap.put(key, 1);
									}
									else
									{
										int count = documentWideFontMap.get(key).intValue();
//										System.out.println("Already present");
										count++;
										documentWideFontMap.put(key, count);
//										count = fontMap.get(curPdfXtkLine.getFontSize()).intValue();
//										System.out.println("Updated count : " + count);
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
									}									
								}
								else
								{
//									System.out.println("FONTSIZE : NAN");
									new BufferedReader(new InputStreamReader(System.in)).readLine();
								}
								
								for(int pdfBoxLineIndex2 = 0 ; pdfBoxLineIndex2 < pdfBoxPage.size() ; pdfBoxLineIndex2++ )
								{
//									System.out.println(pdfBoxLineIndex2);
//									new BufferedReader(new InputStreamReader(System.in)).readLine();
									
									MyTextLine curPdfBoxLine2 = pdfBoxPage.get(pdfBoxLineIndex2);
									
//									System.out.println("pdfBox line : " + curPdfBoxLine2.getText() + " " + curPdfBoxLine2.getX1() + " " + curPdfBoxLine2.getY1() + " " + curPdfBoxLine2.getX2() + " " + curPdfBoxLine2.getY2());
//									System.out.println("pdfXtk line : " + curPdfXtkLine.getText() + " " + curPdfXtkLine.getX1() + " " + (curPdfXtkPageHeight - curPdfXtkLine.getY1()) + " " + curPdfXtkLine.getX2() + " " + (curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()));
									
									if(((double)Math.round(curPdfBoxLine2.getX1() * 100) / 100 == (double)Math.round((curPdfXtkLine.getX1()) * 100) / 100) 
											&& ((double)Math.round(curPdfBoxLine2.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY1()) * 100) / 100)
											&& (((double)Math.round(curPdfBoxLine2.getX2() * 100) / 100 - (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100) < 10 || (-(double)Math.round(curPdfBoxLine.getX2() * 100) / 100 + (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100) > -10)
											&& ((double)Math.round(curPdfBoxLine2.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()) * 100) / 100))
									{
//										System.out.println("Matched pdfbox line : " + curPdfBoxLine2.getText());
//										System.out.println("Matched pdfXtk line : " + curPdfXtkLine.getText());
										
										pdfBoxPage.remove(pdfBoxLineIndex2);
										
										lineCount++;
										
//										System.out.println(pdfBoxPage.size());
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										break;
									}
									else
									{
										if(//((double)Math.round(curPdfBoxLine2.getX1() * 100) / 100 <= (double)Math.round((curPdfXtkLine.getX1()) * 100) / 100) &&
												((double)Math.round(curPdfBoxLine2.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY1()) * 100) / 100) &&
												((double)Math.round(curPdfBoxLine2.getX2() * 100) / 100 + 15 > (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100) &&
												((double)Math.round(curPdfBoxLine2.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()) * 100) / 100))
										{
//											System.out.println("PDFBOX LINE LARGER THAN PDFXTKLINE - NOT RIGHT ALIGNED -- inner");
											
//											System.out.println(curPdfBoxLine2.getText());
//											System.out.println(curPdfXtkLine.getText());
//											
//											System.out.println("Matched pdfbox line : " + curPdfBoxLine2.getText());
//											System.out.println("Matched pdfXtk line : " + curPdfXtkLine.getText());
											
											lineCount++;
											
//											new BufferedReader(new InputStreamReader(System.in)).readLine();
											
											break;

										}
//										else if(((double)Math.round(curPdfBoxLine2.getX1() * 100) / 100 <= (double)Math.round((curPdfXtkLine.getX1()) * 100) / 100) 
//												&& ((double)Math.round(curPdfBoxLine2.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY1()) * 100) / 100)
//												&& ((double)Math.round(curPdfBoxLine2.getX2() * 100) / 100 - (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100 < 10)
//												&& ((double)Math.round(curPdfBoxLine2.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()) * 100) / 100))
//										{
//											System.out.println("PDFBOX LINE LARGER THAN PDFXTKLINE - RIGHT ALIGNED -- inner");
//											
//											System.out.println(curPdfBoxLine2.getText());
//											System.out.println(curPdfXtkLine.getText());
//											
//											System.out.println("Matched pdfbox line : " + curPdfBoxLine2.getText());
//											System.out.println("Matched pdfXtk line : " + curPdfXtkLine.getText());
//
//											lineCount++;
//											
////											new BufferedReader(new InputStreamReader(System.in)).readLine();
//											
//											break;
//										}
									}
								}
							}
							
							System.out.println("Matched Line Count : " + lineCount + "  Total Line Count : " + curPdfXtkTextBlockLines.size());
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
							
							TextBlock tempBlock = (TextBlock) curPdfXtkTextBlock.clone();
														
//							---------------------------------------------
							
							if(sortedTextBlocks.size() > 1)
							{
								TextBlock lastBlock = (TextBlock) sortedTextBlocks.get(sortedTextBlocks.size() - 1);
//								TextBlock penultimateBlock = (TextBlock) sortedTextBlocks.get(sortedTextBlocks.size() - 2);
								
								int lastBlockSize = lastBlock.getItems().size();
								int curBlockSize = curPdfXtkTextBlockLines.size();
								
								if(lastBlockSize == 1 || lastBlockSize == 2)
								{
									List<TextLine> lastBlockLines = lastBlock.getItems();
									if(curBlockSize == 1 || curBlockSize == 2)
									{
										List<TextLine> tempBlockLines = tempBlock.getItems();
//										System.out.println(lastBlock.getText());
//										System.out.println(tempBlock.getText());
										
										if(pickAsNeighbor(lastBlock, tempBlock.getX1(), tempBlock.getY1(), tempBlock.getX2(), tempBlock.getY2(), (double) 25))
										{
											if(lastBlockLines.get(0).getFontName().compareToIgnoreCase(tempBlockLines.get(0).getFontName()) == 0 && lastBlockLines.get(0).getFontSize() == tempBlockLines.get(0).getFontSize())
											{
												for(int i=0 ; i<tempBlockLines.size() ; i++)
													lastBlockLines.add(tempBlockLines.get(i));
												float x1, x2, y1, y2;
												
												x1 = tempBlock.getX1() < lastBlock.getX1() ? tempBlock.getX1() : lastBlock.getX1();
												x2 = tempBlock.getX2() > lastBlock.getX2() ? tempBlock.getX2() : lastBlock.getX2();
												y1 = tempBlock.getY1() < lastBlock.getY1() ? tempBlock.getY1() : lastBlock.getY1();
												y2 = tempBlock.getY2() > lastBlock.getY2() ? tempBlock.getY2() : lastBlock.getY2();
												
												TextBlock newBlock = new TextBlock(x1, x2, y1, y2, lastBlock.getText() + tempBlock.getText(), lastBlockLines.get(0).getFontName(), lastBlockLines.get(0).getFontSize(), lastBlockLines);
											
												tempBlock = (TextBlock) newBlock.clone();
												
//												System.out.println("font based text-block clustering");
												
//												System.out.println(tempBlock);
												
//												new BufferedReader(new InputStreamReader(System.in)).readLine();												
												sortedTextBlocks.remove(sortedTextBlocks.size() - 1);
											}												
										}										
									}
								}
							}
							
//							---------------------------------------------

							pdfXtkTextBlocks.remove(pdfXtkBlockIndex);
							pdfXtkBlockIndex = 0;
							
//							if(upperLimits.size() == 0)
//							{
//								List<GenericSegment> curColumn = new ArrayList<GenericSegment>();
//								curColumn.add(tempBlock);
//								upperLimits.add(curColumn);
//							}
//							else
//							{
//								List<GenericSegment> curColumn = null;
//								
//								for(int i=upperLimits.size() ; i > 0 ; i--)
//								{
//									curColumn = upperLimits.get(i);								
//									if(tempBlock.getX1() > curColumn.get(i).getX2())
//									{
//										curColumn = upperLimits.get(i);
//									}
//								}
//								
//								int yLimit = curColumn.size() < 5 ? curColumn.size() : 5;
//								for(int j=0 ; j < yLimit ; j++)
//								{
//									if(tempBlock.getY2() > curColumn.get(j).getY2())
//									{
//										curColumn.add(j, tempBlock);
//										if(curColumn.size() > 5)
//										{
//											curColumn.remove(5);
//										}
//									}
//								}
//							}
							
														
							sortedTextBlocks.add(tempBlock);		
//							System.out.println(((TextBlock)sortedTextBlocks.get(sortedTextBlocks.size() - 1)).getText());
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
						}
						else
						{
//							System.out.println("firstLine : " + firstLine);
							
//							System.out.println(((double)Math.round(curPdfBoxLine.getY1() * 100) / 100 + " : " + (double)Math.round((curPdfXtkPageHeight - firstLine.getY1()) * 100) / 100));
//							System.out.println(((double)Math.round(curPdfBoxLine.getY2() * 100) / 100 + " : " + (double)Math.round((curPdfXtkPageHeight - firstLine.getY2() + firstLine.getFontSize()) * 100) / 100));
							
							if(//((double)Math.round(curPdfBoxLine.getX1() * 100) / 100 <= (double)Math.round((firstLine.getX1()) * 100) / 100) &&
									((double)Math.round(curPdfBoxLine.getX2() * 100) / 100 + 15 > (double)Math.round((firstLine.getX2()) * 100) / 100) &&
									((double)Math.round(curPdfBoxLine.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - firstLine.getY1()) * 100) / 100) &&
									((double)Math.round(curPdfBoxLine.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - firstLine.getY2() + firstLine.getFontSize()) * 100) / 100))
							{
//								System.out.println("PDFBOX LINE LARGER THAN PDFXTKLINE - NOT RIGHT ALIGNED -- outer");
//								
//								System.out.println(curPdfBoxLine.getText());
//								System.out.println(firstLine.getText());
//								
//								System.out.println("Line sizes differ : firstline y2 : " + (curPdfXtkPageHeight - firstLine.getY2() + firstLine.getFontSize()));
								
								int lineCount = 0;
								
								curPdfXtkTextBlockLines = ((TextBlock) curPdfXtkTextBlock).getItems();
								
								for(TextLine curPdfXtkLine : curPdfXtkTextBlockLines)
								{
									if(!Float.isNaN(curPdfXtkLine.getFontSize()))
									{
										String key = curPdfXtkLine.getFontName() + ":" + (Math.round(curPdfXtkLine.getFontSize() * 10000) / (double)10000);
										
//										System.out.println(key + " : " + key.hashCode());
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										if(!fontMap.containsKey(key))
										{
//											System.out.println("NEW ENTRY !! K : " + key + " V : 1");
											fontMap.put(key, 1);
										}
										else
										{
											int count = fontMap.get(key).intValue();
//											System.out.println("Already present");
											count++;
											fontMap.put(key, count);
//											count = fontMap.get(curPdfXtkLine.getFontSize()).intValue();
//											System.out.println("Updated count : " + count);
											
//											new BufferedReader(new InputStreamReader(System.in)).readLine();
										}
										
										if(!documentWideFontMap.containsKey(key))
										{
//											System.out.println("NEW ENTRY !! K : " + key + " V : 1");
											documentWideFontMap.put(key, 1);
										}
										else
										{
											int count = documentWideFontMap.get(key).intValue();
//											System.out.println("Already present");
											count++;
											documentWideFontMap.put(key, count);
//											count = fontMap.get(curPdfXtkLine.getFontSize()).intValue();
//											System.out.println("Updated count : " + count);
											
//											new BufferedReader(new InputStreamReader(System.in)).readLine();
										}									
									}
									else
									{
										System.out.println("FONTSIZE : NAN");
										new BufferedReader(new InputStreamReader(System.in)).readLine();
									}
									
									for(int pdfBoxLineIndex2 = 0 ; pdfBoxLineIndex2 < pdfBoxPage.size() ; pdfBoxLineIndex2++ )
									{
//										System.out.println(pdfBoxLineIndex2);
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										MyTextLine curPdfBoxLine2 = pdfBoxPage.get(pdfBoxLineIndex2);
										
//										System.out.println("pdfBox line : " + curPdfBoxLine2.getText() + " " + curPdfBoxLine2.getX1() + " " + curPdfBoxLine2.getY1() + " " + curPdfBoxLine2.getX2() + " " + curPdfBoxLine2.getY2());
//										System.out.println("pdfXtk line : " + curPdfXtkLine.getText() + " " + curPdfXtkLine.getX1() + " " + (curPdfXtkPageHeight - curPdfXtkLine.getY1()) + " " + curPdfXtkLine.getX2() + " " + (curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()));
										
										if(((double)Math.round(curPdfBoxLine2.getX1() * 100) / 100 == (double)Math.round((curPdfXtkLine.getX1()) * 100) / 100) 
												&& ((double)Math.round(curPdfBoxLine2.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY1()) * 100) / 100)
												&& (((double)Math.round(curPdfBoxLine2.getX2() * 100) / 100 - (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100) < 10 || (-(double)Math.round(curPdfBoxLine.getX2() * 100) / 100 + (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100) > -10)
												&& ((double)Math.round(curPdfBoxLine2.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()) * 100) / 100))
										{
//											System.out.println("Matched pdfbox line : " + curPdfBoxLine2.getText());
//											System.out.println("Matched pdfXtk line : " + curPdfXtkLine.getText());
											
											pdfBoxPage.remove(pdfBoxLineIndex2);
											
											lineCount++;
											
//											System.out.println(pdfBoxPage.size());
											
//											new BufferedReader(new InputStreamReader(System.in)).readLine();
											
											break;
										}
										else
										{
											if(//((double)Math.round(curPdfBoxLine2.getX1() * 100) / 100 <= (double)Math.round((curPdfXtkLine.getX1()) * 100) / 100) && 
													((double)Math.round(curPdfBoxLine2.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY1()) * 100) / 100) &&
													((double)Math.round(curPdfBoxLine2.getX2() * 100) / 100 + 15 > (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100) &&
													((double)Math.round(curPdfBoxLine2.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()) * 100) / 100))
											{
//												System.out.println("PDFBOX LINE LARGER THAN PDFXTKLINE - NOT RIGHT ALIGNED -- inner");
//												
//												System.out.println(curPdfBoxLine2.getText());
//												System.out.println(curPdfXtkLine.getText());
//												
//												System.out.println("Matched pdfbox line : " + curPdfBoxLine2.getText());
//												System.out.println("Matched pdfXtk line : " + curPdfXtkLine.getText());
												
												lineCount++;
												
//												new BufferedReader(new InputStreamReader(System.in)).readLine();
												
												break;

											}
//											else if(((double)Math.round(curPdfBoxLine2.getX1() * 100) / 100 <= (double)Math.round((curPdfXtkLine.getX1()) * 100) / 100) 
//													&& ((double)Math.round(curPdfBoxLine2.getY1() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY1()) * 100) / 100)
//													&& ((double)Math.round(curPdfBoxLine2.getX2() * 100) / 100 - (double)Math.round((curPdfXtkLine.getX2()) * 100) / 100 < 10)
//													&& ((double)Math.round(curPdfBoxLine2.getY2() * 100) / 100 == (double)Math.round((curPdfXtkPageHeight - curPdfXtkLine.getY2() + curPdfXtkLine.getFontSize()) * 100) / 100))
//											{
//												System.out.println("PDFBOX LINE LARGER THAN PDFXTKLINE - RIGHT ALIGNED -- inner");
//												
//												System.out.println(curPdfBoxLine2.getText());
//												System.out.println(curPdfXtkLine.getText());
//												
//												System.out.println("Matched pdfbox line : " + curPdfBoxLine2.getText());
//												System.out.println("Matched pdfXtk line : " + curPdfXtkLine.getText());
//
//												lineCount++;
//												
////												new BufferedReader(new InputStreamReader(System.in)).readLine();
//												
//												break;
//											}
										}
									}									
								}
								
								System.out.println("Matched Line Count : " + lineCount + "  Total Line Count : " + curPdfXtkTextBlockLines.size());
//								new BufferedReader(new InputStreamReader(System.in)).readLine();
								
								TextBlock tempBlock = (TextBlock) curPdfXtkTextBlock.clone();
								
//								---------------------------------------------
								
								if(sortedTextBlocks.size() > 1)
								{
									TextBlock lastBlock = (TextBlock) sortedTextBlocks.get(sortedTextBlocks.size() - 1);
//									TextBlock penultimateBlock = (TextBlock) sortedTextBlocks.get(sortedTextBlocks.size() - 2);
									
									int lastBlockSize = lastBlock.getItems().size();
									int curBlockSize = curPdfXtkTextBlockLines.size();
									
									if(lastBlockSize == 1 || lastBlockSize == 2)
									{
										List<TextLine> lastBlockLines = lastBlock.getItems();
										if(curBlockSize == 1 || curBlockSize == 2)
										{
											List<TextLine> tempBlockLines = tempBlock.getItems();
//											System.out.println(lastBlock.getText());
//											System.out.println(tempBlock.getText());
											
											if(pickAsNeighbor(lastBlock, tempBlock.getX1(), tempBlock.getY1(), tempBlock.getX2(), tempBlock.getY2(), (double) 25))
											{
												if(lastBlockLines.get(0).getFontName().compareToIgnoreCase(tempBlockLines.get(0).getFontName()) == 0 && lastBlockLines.get(0).getFontSize() == tempBlockLines.get(0).getFontSize())
												{
													for(int i=0 ; i<tempBlockLines.size() ; i++)
														lastBlockLines.add(tempBlockLines.get(i));
													float x1, x2, y1, y2;
													
													x1 = tempBlock.getX1() < lastBlock.getX1() ? tempBlock.getX1() : lastBlock.getX1();
													x2 = tempBlock.getX2() > lastBlock.getX2() ? tempBlock.getX2() : lastBlock.getX2();
													y1 = tempBlock.getY1() < lastBlock.getY1() ? tempBlock.getY1() : lastBlock.getY1();
													y2 = tempBlock.getY2() > lastBlock.getY2() ? tempBlock.getY2() : lastBlock.getY2();
													
													TextBlock newBlock = new TextBlock(x1, x2, y1, y2, lastBlock.getText() + tempBlock.getText(), lastBlockLines.get(0).getFontName(), lastBlockLines.get(0).getFontSize(), lastBlockLines);
												
													tempBlock = (TextBlock) newBlock.clone();
													
//													System.out.println("font based text-block clustering");
													
//													System.out.println(tempBlock);
													
//													new BufferedReader(new InputStreamReader(System.in)).readLine();												
													sortedTextBlocks.remove(sortedTextBlocks.size() - 1);
												}												
											}										
										}
									}
								}
								
//								---------------------------------------------
								
								pdfXtkTextBlocks.remove(pdfXtkBlockIndex);
								pdfXtkBlockIndex = 0;
								
								sortedTextBlocks.add(tempBlock);							
//								System.out.println(((TextBlock)sortedTextBlocks.get(sortedTextBlocks.size() - 1)).getText());
//								new BufferedReader(new InputStreamReader(System.in)).readLine();

							}
						}
					}				
				}
				
//				System.out.println(fontMap + " : " + fontMap.isEmpty());
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
				pagewiseFontMap.add(fontMap);
				Page tempPage = new Page(pdfXtkPage.getX1(), pdfXtkPage.getX2(), pdfXtkPage.getY1(), pdfXtkPage.getY2(), sortedTextBlocks);
				tempPage.setPageNo(counter - 1);
				sortedPagewiseTextBlocks.add(tempPage);
			
			}
			
			pagewiseLineCount = new int[counter];
						
			System.out.println();
			System.out.println();
			System.out.println("----------------------------------------------------------------------------");
			
//			for(Page page : sortedPagewiseTextBlocks)
//			{
//				List<GenericSegment> textBlocks = page.getItems();
//				for(GenericSegment block : textBlocks)
//				{
//					System.out.println("Font name : " + ((TextBlock)block).getFontName() + "Font size : " + ((TextBlock)block).getFontSize());
//					System.out.println(((TextBlock)block).getText());
//					System.out.println();
//				}
//				System.out.println();
////				new BufferedReader(new InputStreamReader(System.in)).readLine();
//			}
//			
//			System.out.println();
//			System.out.println();
//			System.out.println("----------------------------------------------------------------------------");
//			
//			System.out.println();
//			System.out.println();
//			System.out.println("FONT MAP");
			
			int pageCount1 = 0;
			
			for(Map<String, Integer> page : pagewiseFontMap)
			{
				fontOrder = new ArrayList<Integer>(page.values());
				Collections.sort(fontOrder);
				
				for(int i=fontOrder.size()-1 ; i>0 ; i--)
				{
					pagewiseLineCount[pageCount1] += fontOrder.get(i).intValue();
					documentLineCount += pagewiseLineCount[pageCount1];
//					System.out.println(fontOrder.get(i));
				}
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
				pageCount1++;
				pagewiseFontFrequencyOrdering.add(fontOrder);
				
				fontSizeSet = new HashSet<Double>();
				fontSizeOrder = new ArrayList<Double>();
				
				Iterator<String> keys = page.keySet().iterator();
				
				while(keys.hasNext())
				{
					String key = keys.next();
					
					Double fontSize = Double.parseDouble((key.split(":"))[1]);
//					System.out.println(fontSize);
					if(!fontSizeSet.contains(fontSize))
					{
						fontSizeSet.add(fontSize);
						fontSizeOrder.add(fontSize);
					}
					
					System.out.println("K : " + key + " V : " + page.get(key));
				}
				
				Collections.sort(fontSizeOrder);
				pagewiseFontSizeOrdering.add(fontSizeOrder);
				
				System.out.println();				
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
			}
			
//			System.out.println();
//			System.out.println();
//			System.out.println("Document-Wide Font Map");
			
			Iterator<String> keys = documentWideFontMap.keySet().iterator();
			
			documentWideFontFrequencyOrdering = new ArrayList<Integer>(documentWideFontMap.values());
			Collections.sort(documentWideFontFrequencyOrdering);
			
			fontSizeSet = new HashSet<Double>();
			
			while(keys.hasNext())
			{
					String key = keys.next();
					
					Double fontSize = Double.parseDouble((key.split(":"))[1]);
//					System.out.println(fontSize);
					if(!fontSizeSet.contains(fontSize))
					{
						fontSizeSet.add(fontSize);
						documentWideFontSizeOrdering.add(fontSize);
					}
					
//					System.out.println("K : " + key + " V : " + documentWideFontMap.get(key));
			}
			
			Collections.sort(documentWideFontSizeOrdering);
			
			System.out.println("End of preprocess!!!");
			System.out.println();
			System.out.println();
			
//			new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	static void cluster()
	{
		doc = new ArrayList<List<List<GenericSegment>>>();
		
		try
		{
			for(int pageIndex = 0 ; pageIndex < sortedPagewiseTextBlocks.size() ; pageIndex++)
			{
				
				List<List<GenericSegment>> page = new ArrayList<List<GenericSegment>>();
				List<GenericSegment> cluster;
				
				pagewiseImages.add(new ArrayList<Image>());
				List<Image> tempImages = pagewiseImages.get(pageIndex);
				
				System.out.println("Page No: " + pageIndex);
				
				List<GenericSegment> curPageTextBlocks = sortedPagewiseTextBlocks.get(pageIndex).getItems();
				List<GenericSegment> curPageLineSegments = pagewiseLineSegments.get(pageIndex).getItems();
				List<GenericSegment> curPageImageSegments = pagewiseImageSegments.get(pageIndex).getItems();
				
				Collections.sort(curPageLineSegments, new Comparator<GenericSegment>() {
	
					@Override
					public int compare(GenericSegment o1, GenericSegment o2) {
						
						if(o1 instanceof LineSegment && o2 instanceof LineSegment)
						{
							o1 = (LineSegment)o1;
							o2 = (LineSegment)o2;
							
							if(o1.getY2() < o2.getY2())
								return 1;
							if(o1.getY2() > o2.getY2())
								return -1;
						}
						
						return 0;
					}				
				});
				
				Collections.sort(curPageImageSegments, new Comparator<GenericSegment>() {
	
					@Override
					public int compare(GenericSegment o1, GenericSegment o2) {
						
						if(o1 instanceof ImageSegment && o2 instanceof ImageSegment)
						{
							o1 = ((ImageSegment)o1);
							o2 = ((ImageSegment)o2);
							
							if(o1.getY2() < o2.getY2())
								return 1;
							if(o1.getY2() > o2.getY2())
								return -1;
						}
						
						return 0;
					}				
				});
				
				System.out.println("No of image-segments : " + curPageImageSegments.size());
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
				int imageIndex;
				for(imageIndex=0 ; imageIndex < curPageImageSegments.size() ; imageIndex++)
				{
					ImageSegment image = (ImageSegment) curPageImageSegments.get(imageIndex);
					System.out.println(image.getArea());
//					new BufferedReader(new InputStreamReader(System.in)).readLine();
					
					if(image.getArea() > 500)
					{
						ClusterInfo tempImageClusterInfo = new ClusterInfo(image.getX1(), image.getY1(), image.getX2(), image.getY2(), 0, 1, 0, 0);
						Image tempImage = new Image();
						tempImage.setImageClusterInformation(tempImageClusterInfo);
						tempImages.add(tempImage);											
					}
				}
				
				
//				int counter = 0;
//				for(GenericSegment image : curPageImageSegments)
//				{
//					System.out.println("Images");
//					if(image instanceof ImageSegment)
//					{
//						System.out.println(image);
//						writeImage(pageIndex + 1, counter++, image.getX1(), image.getY1(), image.getX2(), image.getY2());
//					}
//				}
//				
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
				double prevClusterX1, prevClusterX2, prevClusterY1, prevClusterY2;
				prevClusterX1 = prevClusterX2 = prevClusterY1 = prevClusterY2 = 0.0;
				boolean prevCluster = false;
				boolean added = false;
				
				int textBlockIndex = 0;
				
				while(textBlockIndex < curPageTextBlocks.size())
				{
					TextBlock textBlock = (TextBlock) curPageTextBlocks.get(textBlockIndex);
					List<TextLine> textBlockLines = textBlock.getItems();
															
					if(textBlockLines.size() == 1 || textBlockLines.size() == 2 || textBlock.getText().length() < 65)
					{
//						System.out.println(textBlock);
						
						double x1, x2, y1, y2;
						int nextBlockIndex = 0;
						cluster = new ArrayList<GenericSegment>();
						cluster.add((GenericSegment) textBlock.clone());
						curPageTextBlocks.remove(textBlock);
						x1 = textBlock.getX1();
						x2 = textBlock.getX2();
						y1 = textBlock.getY1();
						y2 = textBlock.getY2();
						
						ClusterInfo curClusterInfo = new ClusterInfo(x1, y1, x2, y2, 0, 0, 1, 0);
						if(clusterInfoMap.containsKey(cluster))
						{
//							System.out.println("ClusterInfo for cluster already present - update");
							clusterInfoMap.put(cluster, curClusterInfo);
						}
						else
						{
							clusterInfoMap.put(cluster, curClusterInfo);
						}
						
						boolean clusterChanged;
						double threshold = 25.0;
//						int distance = 15;
						int clusterLineCount = 0;
						int clusterTextBlockCount = 0;
						int clusterImageCount = 0;
						
						double averageClusterSeparation = 0.0;
						double distance = 15.0;
						double totalClusterSeparation = 0.0;
										
//						TextBlock captionBlock = null;
//						boolean captionFound = false;
						
						added = false;
						
						do
						{							
//							System.out.println("Beginning clusering ---- ");
//							System.out.println("nextBlockIndex : " + nextBlockIndex + "  " + "prevBlockIndex : " + prevBlockIndex);
							
							if(isCaption(textBlock))
							{								
								textBlockIndex = 0;
								prevCluster = false;
								break;
							}
							else if(isSectionHeading(textBlock))
							{
//								System.out.println(textBlock.getText());
								new BufferedReader(new InputStreamReader(System.in));
								
								textBlockIndex = 0;
								prevCluster = false;
								break;
							}
							
							System.out.println("Distance : " + distance);
							System.out.println("Threshold : " + threshold);
							
							clusterChanged = false;
//							TextBlock prevBlock = null;
							TextBlock nextBlock = null;
							
							List<GenericSegment> neighbors = new ArrayList<GenericSegment>();
							int numTextBlocks = 0;
							int numLineSegments = 0;
							int newTextBlocks = 0;
							int newLineSegments = 0;
							int newImageSegments = 0;
							
							boolean nextBlockPicked = false;
														
							if(nextBlockIndex < curPageTextBlocks.size())
							{
								nextBlock = (TextBlock) curPageTextBlocks.get(nextBlockIndex);
//								System.out.println((nextBlock.getItems().size() == 1 || nextBlock.getItems().size() == 2 || nextBlock.getText().length() < 65));
								if(nextBlock.getItems().size() == 1 || nextBlock.getItems().size() == 2 || nextBlock.getText().length() < 65)
								{
									if(!isCaption(nextBlock) && !isSectionHeading(nextBlock))
									{
//										System.out.println("not a caption");
//										System.out.println(pickAsNeighbor(nextBlock, x1, y1, x2, y2, distance));
										if(pickAsNeighbor(nextBlock, x1, y1, x2, y2, distance))
										{
											numTextBlocks++;
											neighbors.add(nextBlock);
										}
										else
										{
//											System.out.println("Next Block--");
//											System.out.println(nextBlock.getText());
//											System.out.println(nextBlock.getX1() + " : " + nextBlock.getY1() + " : " + nextBlock.getX2() + " : " + nextBlock.getY2());
//											System.out.println(x1 + " : " + y1 + " : " + x2 + " : " + y2);
										}
									}
								}
								else
								{
									if(isOverlap(curClusterInfo, nextBlock))
									{
										neighbors.add(nextBlock);
									}
								}
							}
							
							System.out.println("nextBlockIndex : " + nextBlockIndex + " -- " + nextBlock);
							
							int lineSegmentIndex; 
							
							for(lineSegmentIndex=0 ; lineSegmentIndex < curPageLineSegments.size() ; lineSegmentIndex++)
							{
								LineSegment line = (LineSegment) curPageLineSegments.get(lineSegmentIndex);
								
								if(pickAsNeighbor(line, x1, y1, x2, y2, distance))
								{
									neighbors.add(line);
//									cluster.add((GenericSegment) line.clone());
									newLineSegments++;
									
//									clusterChanged = true;
//									x1 = x1 < line.getX1() ? x1 : line.getX1();
//									x2 = x2 > line.getX2() ? x2 : line.getX2();
//									y1 = y1 < line.getY1() ? y1 : line.getY1();
//									y2 = y2 > line.getY2() ? y2 : line.getY2();
								}
							}
							
//							for(imageIndex=0 ; imageIndex < curPageImageSegments.size() ; imageIndex++)
//							{
//								ImageSegment image = (ImageSegment) curPageImageSegments.get(imageIndex);
//								
//								if(pickAsNeighbor(image, x1, y1, x2, y2, distance))
//								{
//									if((image.getY2() - image.getY1()) <= 25)
//									{
//										neighbors.add(image);
//										newImageSegments++;
//									}
//								}
//							}
							
							System.out.println("Neighbors----------");						
							for(GenericSegment temp : neighbors)
							{
								boolean addToCluster = false;
//								System.out.println(" +   " + temp);
								if(temp instanceof TextBlock)
								{
//									System.out.println(clusterLineCount);
									if(clusterLineCount > 0)
									{
										addToCluster = true;
									}
									else if(cluster.size() > 0)
									{
										TextBlock leadingBlock = (TextBlock) cluster.get(0);
										if((leadingBlock.getItems().size() == 1 && leadingBlock.getText().length() < 10) || (leadingBlock.getItems().size() == 2 && leadingBlock.getText().length() < 15) || (leadingBlock.getItems().size() > 2 && leadingBlock.getText().length() < 50))
										{
											if((((TextBlock)temp).getItems().size() == 1 && ((TextBlock)temp).getText().length() < 10) || (((TextBlock)temp).getItems().size() == 2 && ((TextBlock)temp).getText().length() < 15) || (((TextBlock)temp).getItems().size() > 2 && ((TextBlock)temp).getText().length() < 50))
											{
												addToCluster = true;
											}
										}
									}
									
									if(addToCluster)
									{
										cluster.add((GenericSegment) temp.clone());
										
//										System.out.println("Text Block");
										
										double distanceBetweenMembers = getDistanceBetweenSegments(temp, curClusterInfo);
										
										System.out.println("Average Cluster Separation : " + averageClusterSeparation + " Distance between members : " + distanceBetweenMembers);
										if (averageClusterSeparation > 0)
										{
											if(distanceBetweenMembers > 0)
											{
												averageClusterSeparation = (distanceBetweenMembers + averageClusterSeparation) / (double)2;
											}
										}
										else
										{
											if(distanceBetweenMembers > 0)
											{
												averageClusterSeparation = distanceBetweenMembers;
											}
										}
//										System.out.println("Average Cluster Separation : " + averageClusterSeparation);
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										if(curPageTextBlocks.remove(temp))
										{
											textBlockIndex = 0;
											System.out.println(temp);
										}
										
										newTextBlocks++;
										clusterChanged = true;
										
										x1 = x1 < temp.getX1() ? x1 : temp.getX1();
										x2 = x2 > temp.getX2() ? x2 : temp.getX2();
										y1 = y1 < temp.getY1() ? y1 : temp.getY1();
										y2 = y2 > temp.getY2() ? y2 : temp.getY2();	
										
										curClusterInfo.setX1(x1);
										curClusterInfo.setX2(x2);
										curClusterInfo.setY1(y1);
										curClusterInfo.setY2(y2);
										
									}
								}
								else if(temp instanceof LineSegment)
								{
									addToCluster = true;
									
									if(addToCluster)
									{
										cluster.add((GenericSegment) temp.clone());
										
										double distanceBetweenMembers = getDistanceBetweenSegments(temp, curClusterInfo);
										
//										System.out.println("Line Segment");
										System.out.println("Average Cluster Separation : " + averageClusterSeparation + " Distance between members : " + distanceBetweenMembers);
										if (averageClusterSeparation > 0)
										{
											if(distanceBetweenMembers > 0)
											{
												averageClusterSeparation = (distanceBetweenMembers + averageClusterSeparation) / (double)2;
											}
										}
										else
										{
											if(distanceBetweenMembers > 0)
											{
												averageClusterSeparation = distanceBetweenMembers;
											}
										}
//										System.out.println("Average Cluster Separation : " + averageClusterSeparation);
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										clusterChanged = true;								
										x1 = x1 < temp.getX1() ? x1 : temp.getX1();
										x2 = x2 > temp.getX2() ? x2 : temp.getX2();
										y1 = y1 < temp.getY1() ? y1 : temp.getY1();
										y2 = y2 > temp.getY2() ? y2 : temp.getY2();
										
										curClusterInfo.setX1(x1);
										curClusterInfo.setX2(x2);
										curClusterInfo.setY1(y1);
										curClusterInfo.setY2(y2);
									}
									
									if(curPageLineSegments.remove(temp))
									{
										System.out.println(temp);
									}
								}
								else if(temp instanceof ImageSegment)
								{
									addToCluster = true;
									
									if(addToCluster)
									{
										cluster.add((GenericSegment) temp.clone());
										
										double distanceBetweenMembers = getDistanceBetweenSegments(temp, curClusterInfo);
										
//										System.out.println("Image Segment");
										System.out.println("Average Cluster Separation : " + averageClusterSeparation + " Distance between members : " + distanceBetweenMembers);
										if (averageClusterSeparation > 0)
										{
											if(distanceBetweenMembers > 0)
											{
												averageClusterSeparation = (distanceBetweenMembers + averageClusterSeparation) / (double)2;
											}
										}
										else
										{
											if(distanceBetweenMembers > 0)
											{
												averageClusterSeparation = distanceBetweenMembers;
											}
										}
//										System.out.println("Average Cluster Separation : " + averageClusterSeparation);
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										clusterChanged = true;								
										x1 = x1 < temp.getX1() ? x1 : temp.getX1();
										x2 = x2 > temp.getX2() ? x2 : temp.getX2();
										y1 = y1 < temp.getY1() ? y1 : temp.getY1();
										y2 = y2 > temp.getY2() ? y2 : temp.getY2();
										
										curClusterInfo.setX1(x1);
										curClusterInfo.setX2(x2);
										curClusterInfo.setY1(y1);
										curClusterInfo.setY2(y2);
									}
									
									if(curPageImageSegments.remove(temp))
									{
										System.out.println(temp);
									}
								}
							}
							
							System.out.println("---------------");
							
							clusterLineCount += newLineSegments;
							clusterTextBlockCount += newTextBlocks;
							clusterImageCount += newImageSegments;
								
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
							
							if(cluster.size() > 1)
							{
								distance = averageClusterSeparation;
//								threshold = averageClusterSeparation + 10;
							}
							
							if(!clusterChanged)
							{
								System.out.println("Cluster not changed");
								if(distance <= threshold)
								{
									distance += 5;
									averageClusterSeparation = distance;
								}
							}
							else
							{
								System.out.println("Cluster changed");
//								distance = 15
								distance = averageClusterSeparation;
							}
							
						}while(clusterChanged || distance <= threshold);
						
						System.out.println(x1 + " : " + y1 + " : " + x2 + " : " + y2);
						System.out.println("Cluster line count : " + clusterLineCount + " Cluster textBlock count : " + clusterTextBlockCount);
						
						if(prevCluster)
						{
							System.out.println("neighbor is cluster!!");
							System.out.println(prevClusterX1 + " : " + prevClusterX2 + " : " + prevClusterY1 + " : " + prevClusterY2);
							System.out.println(x1 + " : " + x2 + " : " + y1 + " : " + y2);
							GenericSegment temp = new GenericSegment((float)x1, (float)x2, (float)y1, (float)y2);
							if(pickAsNeighbor(temp, prevClusterX1, prevClusterY1, prevClusterX2, prevClusterY2, (double)65))
							{
								boolean mergeWithPreviousCluster = false;
								List<GenericSegment> previousCluster = page.get(page.size() - 1);
								
								ClusterInfo prevClusterInfo = clusterInfoMap.get(previousCluster);
															
//								TextBlock previousClusterSegment = (TextBlock)previousCluster.get(0);
								if(prevClusterInfo.getClusterLineCount() > 0)
								{
									System.out.println("Previous cluster has line segments");
									mergeWithPreviousCluster = true;
								}
								else if(prevClusterInfo.getClusterImageCount() > 0)
								{
									System.out.println("Previous cluster has image segments");
								}
								else
								{
									TextBlock previousClusterLeadingBlock = (TextBlock)previousCluster.get(1);
									if(previousClusterLeadingBlock.getText().length() < 10)
									{
										System.out.println("Previous cluster has small bits");
										if(((TextBlock)cluster.get(0)).getText().length() < 10)
										{
											mergeWithPreviousCluster = true;
										}
									}
								}
								
								if(mergeWithPreviousCluster)
								{
									added = true;								
									previousCluster.addAll(cluster);
									
//									TextBlock clusterSegment = (TextBlock) previousCluster.get(0);
									
//									System.out.println("Cluster checking!!");								
//									System.out.println(clusterSegment.getX1());
									
									x1 = x1 < prevClusterX1 ? x1 : prevClusterX1;
									y1 = y1 < prevClusterY1 ? y1 : prevClusterY1;
									x2 = x2 > prevClusterX2 ? x2 : prevClusterX2;
									y2 = y2 > prevClusterY2 ? y2 : prevClusterY2;
									
									prevClusterInfo.setX1(x1);
									prevClusterInfo.setY1(y1);
									prevClusterInfo.setX2(x2);
									prevClusterInfo.setY2(y2);
									
									prevClusterInfo.setClusterLineCount(clusterLineCount + prevClusterInfo.getClusterLineCount());
									prevClusterInfo.setClusterTextBlockCount(clusterTextBlockCount + prevClusterInfo.getClusterTextBlockCount());
									prevClusterInfo.setClusterImageCount(clusterImageCount + prevClusterInfo.getClusterImageCount());
									prevClusterInfo.setAverageClusterSeparation((averageClusterSeparation + prevClusterInfo.getAverageClusterSeparation()) / (double)2);
									
									clusterInfoMap.put(previousCluster, prevClusterInfo);
									
									for(int i=0 ; i<curPageTextBlocks.size() ; i++)
									{
										TextBlock tempOverlapBlock = (TextBlock) curPageTextBlocks.get(i);
										if(isOverlap(prevClusterInfo, tempOverlapBlock))
										{
											TextBlock tempBlockPrint = (TextBlock) tempOverlapBlock.clone();
											if(curPageTextBlocks.remove(tempOverlapBlock))
											{
												System.out.println("Removing due to overlap");
												System.out.println(tempBlockPrint);
											}
										}
									}
									
//									clusterSegment.setX1((float)x1);
//									clusterSegment.setX2((float)x2);
//									clusterSegment.setY1((float)y1);
//									clusterSegment.setY2((float)y2);
									
//									clusterSegment.setLineSpacing(clusterLineCount + clusterSegment.getLineSpacing());								
									
//									System.out.println(clusterSegment.getX1());								
//									new BufferedReader(new InputStreamReader(System.in)).readLine();
								}
								else if(cluster.size() > 1)
								{
									curClusterInfo.setX1(x1);
									curClusterInfo.setX2(x2);
									curClusterInfo.setY1(y1);
									curClusterInfo.setY2(y2);
									
									curClusterInfo.setClusterLineCount(clusterLineCount);
									curClusterInfo.setClusterTextBlockCount(clusterTextBlockCount);
									curClusterInfo.setClusterImageCount(clusterImageCount);
									curClusterInfo.setAverageClusterSeparation(averageClusterSeparation);
									
									if(clusterInfoMap.containsKey(cluster))
									{
//										System.out.println("ClusterInfo for cluster already present - update");
										clusterInfoMap.put(cluster,curClusterInfo);
									}
									else
									{
										clusterInfoMap.put(cluster, curClusterInfo);
									}									
									
//									cluster.add(0, new TextBlock((float)x1, (float)x2, (float)y1, (float)y2));								
//									((TextBlock)cluster.get(0)).setLineSpacing(clusterLineCount);
									
//									GenericSegment clusterSegment = cluster.get(0);
//									System.out.println(clusterSegment.getX1() + " : " + clusterSegment.getY1() + " : " + clusterSegment.getX2() + " : " + clusterSegment.getY2());								
//									new BufferedReader(new InputStreamReader(System.in)).readLine();
									
									added = false;
								}
							}
							else //if(cluster.size() > 1)
							{
//								cluster.add(0, new TextBlock((float)x1, (float)x2, (float)y1, (float)y2));
								
								curClusterInfo.setX1(x1);
								curClusterInfo.setX2(x2);
								curClusterInfo.setY1(y1);
								curClusterInfo.setY2(y2);
								
								curClusterInfo.setClusterLineCount(clusterLineCount);
								curClusterInfo.setClusterTextBlockCount(clusterTextBlockCount);
								curClusterInfo.setClusterImageCount(clusterImageCount);
								curClusterInfo.setAverageClusterSeparation(averageClusterSeparation);
								
								if(clusterInfoMap.containsKey(cluster))
								{
//									System.out.println("ClusterInfo for cluster already present - update");
									clusterInfoMap.put(cluster, curClusterInfo);
								}
								else
								{
									clusterInfoMap.put(cluster, curClusterInfo);
								}
								
//								((TextBlock)cluster.get(0)).setLineSpacing(clusterLineCount);
								
//								GenericSegment clusterSegment = cluster.get(0);
//								System.out.println(clusterSegment.getX1() + " : " + clusterSegment.getY1() + " : " + clusterSegment.getX2() + " : " + clusterSegment.getY2());								
//								new BufferedReader(new InputStreamReader(System.in)).readLine();
								
								added = false;
							}
						}
						else //if(cluster.size() > 1)
						{
							curClusterInfo.setX1(x1);
							curClusterInfo.setX2(x2);
							curClusterInfo.setY1(y1);
							curClusterInfo.setY2(y2);
							
							curClusterInfo.setClusterLineCount(clusterLineCount);
							curClusterInfo.setClusterTextBlockCount(clusterTextBlockCount);
							curClusterInfo.setClusterImageCount(clusterImageCount);
							curClusterInfo.setAverageClusterSeparation(averageClusterSeparation);
							
							if(clusterInfoMap.containsKey(cluster))
							{
//								System.out.println("ClusterInfo for cluster already present - update");
								clusterInfoMap.put(cluster,curClusterInfo);
							}
							else
							{
								clusterInfoMap.put(cluster, curClusterInfo);
							}
							
//							cluster.add(0, new TextBlock((float)x1, (float)x2, (float)y1, (float)y2));
//							TextBlock clusterSegment = (TextBlock) cluster.get(0);
//							clusterSegment.setLineSpacing(clusterLineCount);
//							
//							System.out.println(clusterSegment.getX1() + " : " + clusterSegment.getY1() + " : " + clusterSegment.getX2() + " : " + clusterSegment.getY2());
							
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
							
							added = false;
						}
						
						if(cluster.size() > 1)
						{
							prevCluster = true;
							prevClusterX1 = x1;
							prevClusterX2 = x2;
							prevClusterY1 = y1;
							prevClusterY2 = y2;
						}
						else
						{
							prevCluster = false;
						}
					}
					else
					{
						cluster = new ArrayList<GenericSegment>();
						cluster.add((GenericSegment) textBlock.clone());
						
						double x1 = textBlock.getX1();
						double x2 = textBlock.getX2();
						double y1 = textBlock.getY1();
						double y2 = textBlock.getY2();
						
						ClusterInfo curClusterInfo = new ClusterInfo(x1, y1, x2, y2, 0, 0, 1, 0);
						if(clusterInfoMap.containsKey(cluster))
						{
//							System.out.println("ClusterInfo for cluster already present - update");
							clusterInfoMap.put(cluster,curClusterInfo);
						}
						else
						{
							clusterInfoMap.put(cluster, curClusterInfo);
						}
						
						curPageTextBlocks.remove(textBlock);
						prevCluster = false;
					}
					
					System.out.println("----FINAL CLUSTER----");
					
					if(!added)
					{
						for(GenericSegment temp : cluster)
						{
							System.out.println(temp);
						}
					}
					else
					{
						List<GenericSegment> tempCluster = page.get(page.size() - 1);
						for(GenericSegment temp : tempCluster)
						{
							System.out.println(temp);
						}
					}
					
					System.out.println(prevCluster);
					
//					new BufferedReader(new InputStreamReader(System.in)).readLine();
					
					if(!added)
					{
						page.add(cluster);
					}					
				}
				
				doc.add(page);				
			}	
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	static void cleanClusters()
	{
		int pageNo = 0;
		for(List<List<GenericSegment>> page : doc)
		{
			Set<List<GenericSegment>> curPageDirtyCluster = null;
			
			if(pageNo < pagewiseDirtyClusterSet.size())
				curPageDirtyCluster = pagewiseDirtyClusterSet.get(pageNo);
			else
				curPageDirtyCluster = new HashSet<List<GenericSegment>>();
			
			curClusterLoop : for(int curClusterIndex = 0 ; curClusterIndex < page.size() ; curClusterIndex++)
			{
				List<GenericSegment> curCluster = page.get(curClusterIndex);
				ClusterInfo curClusterInfo = clusterInfoMap.get(curCluster);
						
				tempClusterLoop : for(int tempClusterIndex = 0 ; tempClusterIndex < page.size() ; tempClusterIndex++)
				{
//					System.out.print(curClusterIndex + " " + tempClusterIndex + " " + page.size() + " ");
					
					if(tempClusterIndex != curClusterIndex)
					{
						List<GenericSegment> tempCluster = page.get(tempClusterIndex);
						ClusterInfo tempClusterInfo = clusterInfoMap.get(tempCluster);
												
						if(curClusterInfo == null)
						{
							System.out.print("curClusterInfo null  --  " + curClusterIndex + " ");
							System.out.println(curCluster.get(0));
							continue;
						}
						
						if(tempClusterInfo == null)
						{
							System.out.print("tempClusterInfo null  --  " + tempClusterIndex + " ");
							System.out.println(tempCluster.get(0));
							continue;
						}
						
						if(isOverlap(curClusterInfo, tempClusterInfo))
						{
							System.out.println("overlap");
							curCluster.addAll(tempCluster);
							curPageDirtyCluster.add(tempCluster);
//							clusterInfoMap.remove(tempCluster);
//							if(page.remove(tempCluster))
//							{
//								System.out.println("removed cluster while cleaning --- " + tempCluster.get(0));
//							}
						}
					}
					
					System.out.println();
				}
			}
		
			pagewiseDirtyClusterSet.add(curPageDirtyCluster);
			pageNo++;
		}
		
		pageNo--;
		
		while(pageNo >= 0)
		{
			List<List<GenericSegment>> curPage = doc.get(pageNo);
			Set<List<GenericSegment>> dirtyClusterSet = pagewiseDirtyClusterSet.get(pageNo);
			Iterator<List<GenericSegment>> dirtyClusterIterator = dirtyClusterSet.iterator();
			while(dirtyClusterIterator.hasNext())
			{
				List<GenericSegment> tempCluster = dirtyClusterIterator.next();
				clusterInfoMap.remove(tempCluster);
				if(curPage.remove(tempCluster))
				{
					System.out.println("removed cluster while cleaning --- " + tempCluster.get(0));
				}
			}
			pageNo--;
		}
	}
	
	
	static String writeImage(int pageNo, int counter, double x1, double y1, double x2, double y2)
	{
		String imageFormat = "jpg";
		int startPage = pageNo;
		int endPage = pageNo;
		int imageType = BufferedImage.TYPE_INT_RGB;
		int resolution = 200;
		
		String tempOutputPrefix = outputPrefix;
		tempOutputPrefix += "" + counter;
		
		try
		{
			List pages = pdfBoxDocument.getDocumentCatalog().getAllPages();
    		
			PDPage page = (PDPage)pages.get( pageNo - 1 );
	    	PDRectangle rectangle = new PDRectangle();
	    	rectangle.setLowerLeftX((float)x1);
	    	rectangle.setLowerLeftY((float)y1);
	    	rectangle.setUpperRightX((float)x2);
	    	rectangle.setUpperRightY((float)y2);
	    	page.setMediaBox(rectangle);
	    	page.setCropBox(rectangle);
			
			PDFImageWriter imageWriter = new PDFImageWriter();
	        boolean success = imageWriter.writeImage(pdfBoxDocument, imageFormat, pdfBoxPassword,
	                    startPage, endPage, tempOutputPrefix, imageType, resolution);
	           
	        if (!success)
	        {
	            System.err.println( "Error: no writer found for image format '"
	                    + imageFormat + "'" );
	            System.exit(1);
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return tempOutputPrefix + pageNo + ".jpg";
	}
		
	
	public static boolean pickAsNeighbor(GenericSegment segment, double x1, double y1, double x2, double y2, double distance)
	{
		if(!(segment.getX1() > x2) && !(segment.getX2() < x1) && !(segment.getY1() > y2) && !(segment.getY2() < y1))
		{
			return true;
		}
		else if((segment.getX2() < x1) && (segment.getY1() > y2))
		{
			if(Math.pow(Math.abs(segment.getX2() - x1), 2) + Math.pow(Math.abs(segment.getY1() - y2), 2) < distance * distance)
			{
				return true;
			}
		}
		else if((segment.getY1() > y2) && (segment.getX1() > x2))
		{
			if(Math.pow(Math.abs(segment.getY1() - y2), 2) + Math.pow(Math.abs(segment.getX1() - x2), 2) < distance * distance)
			{
				return true;
			}
		}
		else if((segment.getX1() > x2) && (segment.getY2() < y1))
		{
			if(Math.pow(Math.abs(segment.getX1() - x2), 2) + Math.pow(Math.abs(segment.getY2() - y1), 2) < distance * distance)
			{
				return true;
			}
		}
		else if((segment.getY2() < y1) && (segment.getX2() < x1))
		{
			if(Math.pow(Math.abs(segment.getY2() - y1), 2) + Math.pow(Math.abs(segment.getX2() - x1), 2) < distance * distance)
			{
				return true;
			}
		}
		else if(segment.getX2() < x1)
		{
			if(Math.abs(segment.getX2() - x1) < distance)
			{
				return true;
			}
		}
		else if(segment.getY1() > y2)
		{
			if(Math.abs(segment.getY1() - y2) < distance)
			{
				return true;
			}
		}
		else if(segment.getX1() > x2)
		{
			if(Math.abs(segment.getX1() - x2) < distance)
			{
				return true;
			}
		}
		else if(segment.getY2() < y1)
		{
			if(Math.abs(segment.getY2() - y1) < distance)
			{
				return true;
			}
		}
		return false;
	}
		
	
	public static boolean isOverlap(ClusterInfo clusterInfo, GenericSegment genericSegment)
	{
		if(!(genericSegment.getX2() < clusterInfo.getX1()) && !(genericSegment.getX1() > clusterInfo.getX2()) && !(genericSegment.getY2() < clusterInfo.getY1()) && !(genericSegment.getY1() > clusterInfo.getY2()))
		{
			return true;
		}
		
		return false;
	}

	
	public static boolean isOverlap(ClusterInfo clusterInfo1, ClusterInfo clusterInfo2)
	{
		if(!(clusterInfo1.getX2() < clusterInfo2.getX1()) && !(clusterInfo1.getX1() > clusterInfo2.getX2()) && !(clusterInfo1.getY2() < clusterInfo2.getY1()) && !(clusterInfo1.getY1() > clusterInfo2.getY2()))
		{
			return true;
		}
		
		return false;
	}
	
	
	public static double getDistanceBetweenSegments(GenericSegment curAddition, ClusterInfo clusterInfo)
	{
		
		double x1 = clusterInfo.getX1();
		double y1 = clusterInfo.getY1();
		double x2 = clusterInfo.getX2();
		double y2 = clusterInfo.getY2();
		
		System.out.println("current addition : " + curAddition.getX1() + " " + curAddition.getX2() + " " + curAddition.getY1() + " " + curAddition.getY2());
		System.out.println("current cluster information : " + x1 + " " + x2 + " " + y1 + " " + y2);
		
		if(!(curAddition.getX1() > x2) && !(curAddition.getX2() < x1) && !(curAddition.getY1() > y2) && !(curAddition.getY2() < y1))
		{
			return 0.0;
		}
		else if((curAddition.getX2() < x1) && (curAddition.getY1() > y2))
		{
			return Math.sqrt(Math.pow(Math.abs(curAddition.getX2() - x1), 2) + Math.pow(Math.abs(curAddition.getY1() - y2), 2));
		}
		else if((curAddition.getY1() > y2) && (curAddition.getX1() > x2))
		{
			return Math.sqrt(Math.pow(Math.abs(curAddition.getY1() - y2), 2) + Math.pow(Math.abs(curAddition.getX1() - x2), 2));
		}
		else if((curAddition.getX1() > x2) && (curAddition.getY2() < y1))
		{
			return Math.sqrt(Math.pow(Math.abs(curAddition.getX1() - x2), 2) + Math.pow(Math.abs(curAddition.getY2() - y1), 2));
		}
		else if((curAddition.getY2() < y1) && (curAddition.getX2() < x1))
		{
			return Math.sqrt(Math.pow(Math.abs(curAddition.getY2() - y1), 2) + Math.pow(Math.abs(curAddition.getX2() - x1), 2));
		}
		else if(curAddition.getX2() < x1)
		{
			return Math.abs(curAddition.getX2() - x1);
		}
		else if(curAddition.getY1() > y2)
		{
			return Math.abs(curAddition.getY1() - y2);
		}
		else if(curAddition.getX1() > x2)
		{
			return Math.abs(curAddition.getX1() - x2);
		}
		else if(curAddition.getY2() < y1)
		{
			return Math.abs(curAddition.getY2() - y1);
		}
		
		return 0.0;
	}

	
	public static boolean isCaption(TextBlock textBlock)
	{
		
		String tableCaptionPattern = "(Table[.: ]?)([0-9]+(([.][0-9]+)[ ]*)*[.:]?[ ]*)([^0-9]+)([^ ]+[ ]*)+";
		String figureCaptionPattern1 = "(Fig[.: ]?)([0-9]+(([.][0-9]+)[ ]*)*[.:]?[ ]*)([^0-9]+)([^ ]+[ ]*)+";
		String figureCaptionPattern2 = "(Figure[.: ]?)([0-9]+(([.][0-9]+)[ ]*)*[.:]?[ ]*)([^0-9]+)([^ ]+[ ]*)+";
		String algoCaptionPattern1 = "(Algo[.: ]?)([0-9]+(([.][0-9]+)[ ]*)*[.:]?[ ]*)([^0-9]+)([^ ]+[ ]*)+";
		String algoCaptionPattern2 = "(Algorithm[.: ]?)([0-9]+(([.][0-9]+)[ ]*)*[.:]?[ ]*)([^0-9]+)([^ ]+[ ]*)+";
		
		String blockText = ((TextBlock) textBlock).getText();
		
		Pattern tableCaptionRegex = Pattern.compile(tableCaptionPattern, Pattern.CASE_INSENSITIVE);
		Pattern figureCaptionRegex1 = Pattern.compile(figureCaptionPattern1, Pattern.CASE_INSENSITIVE);
		Pattern algoCaptionRegex1 = Pattern.compile(algoCaptionPattern1, Pattern.CASE_INSENSITIVE);
		Pattern figureCaptionRegex2 = Pattern.compile(figureCaptionPattern2, Pattern.CASE_INSENSITIVE);
		Pattern algoCaptionRegex2 = Pattern.compile(algoCaptionPattern2, Pattern.CASE_INSENSITIVE);
		
		Matcher tableCaptionMatcher = tableCaptionRegex.matcher(blockText);
		Matcher figureCaptionMatcher = figureCaptionRegex1.matcher(blockText);
		Matcher algoCaptionMatcher = algoCaptionRegex1.matcher(blockText);
		
		
		if(tableCaptionMatcher.matches())
		{
			System.out.println(blockText);
			System.out.println("Table Caption Matched");
			return true;
		}
		if(figureCaptionMatcher.matches())
		{
			System.out.println(blockText);
			System.out.println("Figure Caption Matched");
			return true;
		}
		figureCaptionMatcher = figureCaptionRegex2.matcher(blockText);
		if(figureCaptionMatcher.matches())
		{
			System.out.println(blockText);
			System.out.println("Figure Caption Matched");
			return true;
		}
		if(algoCaptionMatcher.matches())
		{		
			System.out.println(blockText);
			System.out.println("Algorithm Caption Matched");
			return true;
		}
		algoCaptionMatcher = algoCaptionRegex2.matcher(blockText);
		if(algoCaptionMatcher.matches())
		{		
			System.out.println(blockText);
			System.out.println("Algorithm Caption Matched");
			return true;
		}
		
		return false;
	}
		
	
	public static boolean isSectionHeading(TextBlock textBlock)
	{
//		String sectionHeadingPattern1 = "((([0-9])+[.: ][ ]*)+)(([A-Za-z0-9])+([-)(.,/\"\': ])?)([ ])*(([A-Za-z0-9])+([-)(.,/\"\': ])?)*([ ])*[:]?";
		String sectionHeadingPattern1 = "^([1-9]+(([.][1-9]+)[ ]*)*[.:]?[ ]*)([^0-9\n\r]+)([^ ]+[ ]*)+$";
		String ending = "([^ ]+[ ]*)+[ ]*";
		String sectionHeadingPattern2 = ".*abstract( )*";
		String sectionHeadingPattern3 = ".*references( )*";
		String sectionHeadingPattern4 = ".*conclusions( )*";
		String sectionHeadingPattern5 = ".*futurework( )*";
		String sectionHeadingPattern6 = ".*conclusionsandfuturework( )*";
		String sectionHeadingPattern7 = ".*conclusions&futurewok( )*";
		String sectionHeadingPattern8 = ".*introduction( )*";
		
		Pattern sectionHeadingRegex1 = Pattern.compile(sectionHeadingPattern1, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex2 = Pattern.compile(sectionHeadingPattern2, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex3 = Pattern.compile(sectionHeadingPattern3, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex4 = Pattern.compile(sectionHeadingPattern4, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex5 = Pattern.compile(sectionHeadingPattern5, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex6 = Pattern.compile(sectionHeadingPattern6, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex7 = Pattern.compile(sectionHeadingPattern7, Pattern.CASE_INSENSITIVE);
		Pattern sectionHeadingRegex8 = Pattern.compile(sectionHeadingPattern8, Pattern.CASE_INSENSITIVE);
		
		String blockText = textBlock.getText();
		
		regEx1Matched = false;
		
		Matcher sectionHeadingMatcher = sectionHeadingRegex1.matcher(blockText.trim());
		if(sectionHeadingMatcher.matches())
		{
			System.out.println("Section Heading Matched");
			regEx1Matched = true;
			currentSectionHeadingPrefix = sectionHeadingMatcher.group(1);
			System.out.println(sectionHeadingMatcher.group(1) + sectionHeadingMatcher.group(4));
			return true;
		}
		else
		{
			regEx1Matched = false;
			sectionHeadingMatcher = sectionHeadingRegex2.matcher(blockText.trim());
			if(sectionHeadingMatcher.matches())
			{				
				System.out.println("Section Heading Matched");
				return true;
			}
			else
			{
				sectionHeadingMatcher = sectionHeadingRegex3.matcher(blockText.trim());
				if(sectionHeadingMatcher.matches())
				{
					System.out.println("Section Heading Matched");
					return true;
				}
				else
				{
					sectionHeadingMatcher = sectionHeadingRegex4.matcher(blockText.trim());
					if(sectionHeadingMatcher.matches())
					{
						System.out.println("Section Heading Matched");
						return true;
					}
					else
					{
						sectionHeadingMatcher = sectionHeadingRegex5.matcher(blockText.trim());
						if(sectionHeadingMatcher.matches())
						{
							System.out.println("Section Heading Matched");
							return true;
						}
						else
						{
							sectionHeadingMatcher = sectionHeadingRegex6.matcher(blockText.trim());
							if(sectionHeadingMatcher.matches())
							{
								System.out.println("Section Heading Matched");
								return true;
							}
							else
							{
								sectionHeadingMatcher = sectionHeadingRegex7.matcher(blockText.trim());
								if(sectionHeadingMatcher.matches())
								{
									System.out.println("Section Heading Matched");
									return true;
								}
								else
								{
									sectionHeadingMatcher = sectionHeadingRegex8.matcher(blockText.trim());
									if(sectionHeadingMatcher.matches())
									{
										System.out.println("Section Heading Matched");
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	
	static void processImages()
	{
		int pageNo = 0;
		
		try
		{
			for(List<List<GenericSegment>> page : doc)
			{			
				List<Image> images = pagewiseImages.get(pageNo);
				for(List<GenericSegment> cluster : page)
				{
					ClusterInfo tempClusterInfo = clusterInfoMap.get(cluster);
					if(tempClusterInfo != null)
					{
						if((tempClusterInfo.getClusterLineCount() > 2) || (tempClusterInfo.getClusterTextBlockCount() > 2))
						{
							Image tempImage = new Image();
							tempImage.setImageClusterInformation(tempClusterInfo);
							images.add(tempImage);
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
						}
					}
				}
				pageNo++;
			}
			
			pageNo = 0;
			for(List<Image> page : pagewiseImages)
			{
				int imageCounter = 0;
				for(Image image : page)
				{
//					System.out.println(pageNo);
//					new BufferedReader(new InputStreamReader(System.in)).readLine();
					String path = writeImage(pageNo + 1, imageCounter, image.getImageClusterInformation().getX1(), image.getImageClusterInformation().getY1(), image.getImageClusterInformation().getX2(), image.getImageClusterInformation().getY2());
					imageCounter++;
					System.out.println(path);
					image.setPath(path);
				}
				pageNo++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	static void tagContent()
	{
		try
		{
//			System.out.println("Document wide font ordering");
//			
			
			int n = 0;
			
			for(Double fontSize : documentWideFontSizeOrdering)
			{
				System.out.println(fontSize);
				
				Iterator<String> fontKeyIterator = documentWideFontMap.keySet().iterator();
				
				while(fontKeyIterator.hasNext())
				{
					String fontKey = fontKeyIterator.next();
					
					if(fontKey.endsWith(fontSize.toString()))
					{
						averageFontSize += fontSize * documentWideFontMap.get(fontKey);
						n += documentWideFontMap.get(fontKey);
					}
				}
			}
			
			averageFontSize = averageFontSize / n;
			
			int pageNo = 0;
						
			logicalStructure = new ArrayList<Section>();
			sectionHierarchy = new Stack<Section>();
			sectionHeadingPrefixHierarchy = new Stack<String>();
			
			for(List<List<GenericSegment>> page : doc)
			{
				int clusterIndex = 0;
				int imageCounter = 0;
				boolean noSectionHeadingYet = true;
				
				for(List<GenericSegment> cluster : page)
				{
					clusterIndex++;
					
					ClusterInfo curClusterInfo = clusterInfoMap.get(cluster);
					
					if(curClusterInfo != null)
					{
						if(!(curClusterInfo.getClusterLineCount() > 2) && !(curClusterInfo.getClusterTextBlockCount() > 2))
						{
							int segmentIndex = 0;
//							for(GenericSegment segment : cluster)
							for(segmentIndex = 0 ; segmentIndex < cluster.size() ; segmentIndex++)
							{
								GenericSegment segment = cluster.get(segmentIndex);
								if(segment instanceof TextBlock)
								{
									int tagClass = score(segment, sortedPagewiseTextBlocks.get(pageNo));
									
									if(tagClass == 0 || tagClass == 2 || tagClass == 3)
									{
										Section tempSection = new Section();
										List<TextBlock> tempContent = new ArrayList<TextBlock>();
										tempContent.add((TextBlock)segment);
										tempSection.setContent(tempContent);
										
										List<Section> listToAddTo = null;
										
										switch(tagClass)
										{
											case 0: if(meta.getTitles() == null)
													{
														List<Section> titles = new ArrayList<Section>();
														listToAddTo = titles;
														meta.setTitles(titles);
													}
													else
													{
														listToAddTo = meta.getTitles();
													}
											case 2: if(meta.getHeaders() == null)
													{
														List<Section> headers = new ArrayList<Section>();
														listToAddTo = headers;
														meta.setHeaders(headers);
													}
													else
													{
														listToAddTo = meta.getHeaders();
													}
											
											case 3: if(meta.getFooters() == null)
													{
														List<Section> footers = new ArrayList<Section>();
														listToAddTo = footers;
														meta.setFooters(footers);
													}
													else
													{
														listToAddTo = meta.getFooters();
													}
													
										}
										
										listToAddTo.add(tempSection);
									}
									else if(tagClass == 1)
									{
										Section tempSection = new Section();
										TextBlock tempHeading = new TextBlock();
										tempHeading = (TextBlock) segment;
										tempSection.setHeading(tempHeading);
										boolean isSubSection = false;
										boolean notHeading = false;
										boolean popSections = false;
																				
										if(regEx1Matched)
										{
//											Stack<String> sectionHeadingPrefixes = new Stack<String>();
											String lastSectionHeadingPrefix;
											TextBlock tempHeading2 = (TextBlock) tempHeading.clone();
											List<TextLine> tempHeadingLines = tempHeading2.getItems();
											
											if(tempHeadingLines.size() > 1)
											{
												tempHeadingLines.remove(0);
												tempHeading2.setItems(tempHeadingLines);
												TextLine tempHeadingLine = tempHeadingLines.get(0);
												tempHeading2.setX1(tempHeadingLine.getX1());
												tempHeading2.setY1(tempHeadingLine.getY1());
												String tempHeading2Text = "";
												for(TextLine line : tempHeadingLines)
												{
													tempHeading2Text += line.getText();
												}
												tempHeading2.setText(tempHeading2Text);
//												System.out.println(tempHeadingLine);
//												int newSegmentCounter = 1;
												
												lastSectionHeadingPrefix = currentSectionHeadingPrefix;
												int tempTagClass = score(tempHeading2, sortedPagewiseTextBlocks.get(pageNo));
												if(tempTagClass == 1)
												{
													if(regEx1Matched)
													{
														System.out.println(" -- Splitting Heading -- ");
																										
														cluster.add(segmentIndex + 1, tempHeading2);
														TextLine leadingLine = tempHeading.getItems().get(0);
														tempHeading.setX2(leadingLine.getX2());
														tempHeading.setY2(leadingLine.getY2());
														tempHeading.setText(leadingLine.getText());
														tempHeading.setFontName(leadingLine.getFontName());
														tempHeading.setFontSize(leadingLine.getFontSize());
														List<TextLine> tempList = new ArrayList<TextLine>();
														tempList.add(leadingLine);
														tempHeading.setItems(tempList);														
														System.out.println(tempHeading);
														System.out.println(tempHeading2);
													}
													else
													{
														regEx1Matched = true;	//Switch back regEx1Matched flag to indicate state of original section heading.
													}
												}
												else
												{
													regEx1Matched = true;	//Switch back regEx1Matched flag to indicate state of original section heading.
												}
												
												currentSectionHeadingPrefix = lastSectionHeadingPrefix;											
												
//												new BufferedReader(new InputStreamReader(System.in)).readLine();
											}
											
											if(sectionHeadingPrefixHierarchy.size() > 0)
											{
												notHeading = false;
												
												while(sectionHierarchy.size() > 0)
												{
													Section topSection = sectionHierarchy.peek();
//													Check for subsection and main section - push and pop from sectionHierarchy
													String topSectionHeadingPrefix = sectionHeadingPrefixHierarchy.peek();
													if(topSectionHeadingPrefix != null)
													{
														System.out.println(topSectionHeadingPrefix + " -- " + currentSectionHeadingPrefix);
//														new BufferedReader(new InputStreamReader(System.in)).readLine();
																										
														int dotIndex = 0;
														String topSectionString = "";
														String currentSectionString = "";
														
														String temp = topSectionHeadingPrefix.trim();
														
														for(int i=0 ; i<temp.length() ; i++)
														{
															if(temp.charAt(i) != '.')
															{
																topSectionString += temp.charAt(i);
															}
														}
														
														temp = currentSectionHeadingPrefix.trim();
														
														for(int i=0 ; i<temp.length() ; i++)
														{
															if(temp.charAt(i) != '.')
															{
																currentSectionString += temp.charAt(i);
															}
														}
														
														int topSectionNumber = 0;
														int currentSectionNumber = 0;
														int currentSectionStringLength = currentSectionString.length();
														int topSectionStringLength = topSectionString.length();
														
														if(topSectionStringLength < currentSectionStringLength)
														{
															while(topSectionStringLength < currentSectionStringLength)
															{
																topSectionString += "0";
																topSectionStringLength++;
															}
														}
														else if(topSectionStringLength > currentSectionStringLength)
														{
															while(topSectionStringLength > currentSectionStringLength)
															{
																currentSectionString += "0";
																currentSectionStringLength++;
															}
														}
														
														try
														{
															topSectionNumber = Integer.parseInt(topSectionString);
															currentSectionNumber = Integer.parseInt(currentSectionString);
															
															System.out.println(topSectionNumber);
															System.out.println(currentSectionNumber);
															
															if(currentSectionNumber < topSectionNumber)
															{
																topSection.getContent().add(tempHeading);
																notHeading = true;
																break;
															}
														}
														catch (NumberFormatException e) {
															System.out.println("Not numbered heading");
															System.out.println(topSectionString);
															System.out.println(currentSectionString);
														}
														
														if(currentSectionHeadingPrefix.startsWith(topSectionHeadingPrefix.trim()))
														{									
															if(topSection.getSubSections() == null)
															{
																List<Section> tempSubSections = new ArrayList<Section>();
																tempSubSections.add(tempSection);
																topSection.setSubSections(tempSubSections);
															}
															else
															{
																topSection.getSubSections().add(tempSection);
															}
															
															isSubSection = true;
															break;
														}
														else
														{
															Section poppedSection = sectionHierarchy.pop();
															
															System.out.println("Page No : " + pageNo);
															List<Image> curPageImages = pagewiseImages.get(pageNo);
															for (Image image : curPageImages)
															{
																if(!associatedImageSet.contains(image))
																{																	
																	boolean addImage = false;
																	
																	if(noSectionHeadingYet)
																	{
																		addImage = true;
																	}
																	else
																	{
																		ClusterInfo tempImageClusterInfo = image.getImageClusterInformation();
																		TextBlock poppedSectionHeading = poppedSection.getHeading();
																		TextBlock comparisonBlock = null;
																		if(poppedSectionHeading != null)
																		{
																			comparisonBlock = poppedSectionHeading;
																		}
																		else
																		{
																			TextBlock leadingBlock = poppedSection.getContent().get(0);
																			comparisonBlock = leadingBlock;
																		}
																		
																		if(comparisonBlock != null)
																		{
																			System.out.println("-- Comparing --");
																			System.out.println(comparisonBlock);
																			System.out.println(tempHeading);
																			System.out.println(tempImageClusterInfo);
																			
																			if(!(tempImageClusterInfo.getX2() < (tempHeading.getX1() + 5)) && !(tempImageClusterInfo.getX1() > (comparisonBlock.getX2() - 5)))
																			{
																				if(tempImageClusterInfo.getY2() < (comparisonBlock.getY1() + 5) && tempImageClusterInfo.getY1() > (tempHeading.getY2() - 5))
																				{
																					addImage = true;
																				}
																			}
																			else if(tempImageClusterInfo.getX2() < (tempHeading.getX1() + 5))
																			{
																				if(tempImageClusterInfo.getY2() < (comparisonBlock.getY1() + 5))
																				{
																					addImage = true;
																				}
																			}
																			else if(tempImageClusterInfo.getX1() > (comparisonBlock.getX2() - 5))
																			{
																				if(tempImageClusterInfo.getY1() > (tempHeading.getY2() - 5) && Math.abs(tempImageClusterInfo.getX1() - tempHeading.getX1()) < 5)
																				{
																					addImage = true;
																				}
																			}
																		}
																		
																		if(addImage)
																		{
																			List<Image> poppedSectionImages = poppedSection.getImages();
																			if(poppedSectionImages == null)
																			{
																				poppedSectionImages = new ArrayList<Image>();
																			}
																			
																			poppedSectionImages.add(image);
																			associatedImageSet.add(image);
																			System.out.println(image.getPath());
																		}
																	}																	
																}
																
//																new BufferedReader(new InputStreamReader(System.in)).readLine();
															}
															
															String poppedSectionHeadingPrefix = sectionHeadingPrefixHierarchy.pop();
															System.out.println(poppedSectionHeadingPrefix);
														}
													}
													else
													{
//														Cut Image processing from here
														popSections = true;
														break;
													}
													
													if(popSections)
													{
														logicalStructure.add(tempSection);														
														while(sectionHierarchy.size() > 0)
														{
															Section poppedSection = sectionHierarchy.pop();
															
															List<Image> curPageImages = pagewiseImages.get(pageNo);
															for (Image image : curPageImages)
															{
																if(!associatedImageSet.contains(image))
																{																	
																	boolean addImage = false;
																	
																	if(noSectionHeadingYet)
																	{
																		addImage = true;
																	}
																	else
																	{
																		ClusterInfo tempImageClusterInfo = image.getImageClusterInformation();
																		TextBlock poppedSectionHeading = poppedSection.getHeading();
																		TextBlock comparisonBlock = null;
																		if(poppedSectionHeading != null)
																		{
																			comparisonBlock = poppedSectionHeading;
																		}
																		else
																		{
																			TextBlock leadingBlock = poppedSection.getContent().get(0);
																			comparisonBlock = leadingBlock;
																		}
																		
																		if(comparisonBlock != null)
																		{
																			System.out.println("-- Comparing --");
																			System.out.println(comparisonBlock);
																			System.out.println(tempHeading);
																			System.out.println(tempImageClusterInfo);
																			
																			if(!(tempImageClusterInfo.getX2() < (tempHeading.getX1() + 5)) && !(tempImageClusterInfo.getX1() > (comparisonBlock.getX2() - 5)))
																			{
																				if(tempImageClusterInfo.getY2() < (comparisonBlock.getY1() + 5) && tempImageClusterInfo.getY1() > (tempHeading.getY2() - 5))
																				{
																					addImage = true;
																				}
																			}
																			else if(tempImageClusterInfo.getX2() < (tempHeading.getX1() + 5))
																			{
																				if(tempImageClusterInfo.getY2() < (comparisonBlock.getY1() + 5))
																				{
																					addImage = true;
																				}
																			}
																			else if(tempImageClusterInfo.getX1() > (comparisonBlock.getX2() - 5))
																			{
																				if(tempImageClusterInfo.getY1() > (tempHeading.getY2() - 5) && Math.abs(tempImageClusterInfo.getX1() - tempHeading.getX1()) < 5)
																				{
																					addImage = true;
																				}
																			}
																		}
																		
																		if(addImage)
																		{
																			List<Image> poppedSectionImages = poppedSection.getImages();
																			if(poppedSectionImages == null)
																			{
																				poppedSectionImages = new ArrayList<Image>();
																			}
																			
																			poppedSectionImages.add(image);
																			associatedImageSet.add(image);
																			System.out.println(image.getPath());
																		}
																	}																	
																}
																
//																new BufferedReader(new InputStreamReader(System.in)).readLine();
															}											
														}
													}
												}
												
												if(!isSubSection && !notHeading)
												{
//													Cut image proccessing from here
													popSections = true;
												}
											}
											else
											{
//												Cut image processing from here
												popSections = true;
												sectionHeadingPrefixHierarchy.clear();
											}
										}
										else
										{
//											Cut image processing from here
											popSections = true;
											sectionHeadingPrefixHierarchy.clear();
										}
										
										if(popSections)
										{
											logicalStructure.add(tempSection);														
											while(sectionHierarchy.size() > 0)
											{
												Section poppedSection = sectionHierarchy.pop();
												
												List<Image> curPageImages = pagewiseImages.get(pageNo);
												for (Image image : curPageImages)
												{
													if(!associatedImageSet.contains(image))
													{																	
														boolean addImage = false;
														
														if(noSectionHeadingYet)
														{
															addImage = true;
														}
														else
														{
															ClusterInfo tempImageClusterInfo = image.getImageClusterInformation();
															TextBlock poppedSectionHeading = poppedSection.getHeading();
															TextBlock comparisonBlock = null;
															if(poppedSectionHeading != null)
															{
																comparisonBlock = poppedSectionHeading;
															}
															else
															{
																TextBlock leadingBlock = poppedSection.getContent().get(0);
																comparisonBlock = leadingBlock;
															}
															
															if(comparisonBlock != null)
															{
																System.out.println("-- Comparing --");
																System.out.println(comparisonBlock);
																System.out.println(tempHeading);
																System.out.println(tempImageClusterInfo);
																
																if(!(tempImageClusterInfo.getX2() < (tempHeading.getX1() + 5)) && !(tempImageClusterInfo.getX1() > (comparisonBlock.getX2() - 5)))
																{
																	if(tempImageClusterInfo.getY2() < (comparisonBlock.getY1() + 5) && tempImageClusterInfo.getY1() > (tempHeading.getY2() - 5))
																	{
																		addImage = true;
																	}
																}
																else if(tempImageClusterInfo.getX2() < (tempHeading.getX1() + 5))
																{
																	if(tempImageClusterInfo.getY2() < (comparisonBlock.getY1() + 5))
																	{
																		addImage = true;
																	}
																}
																else if(tempImageClusterInfo.getX1() > (comparisonBlock.getX2() - 5))
																{
																	if(tempImageClusterInfo.getY1() > (tempHeading.getY2() - 5) && Math.abs(tempImageClusterInfo.getX1() - tempHeading.getX1()) < 5)
																	{
																		addImage = true;
																	}
																}
															}
															
															if(addImage)
															{
																List<Image> poppedSectionImages = poppedSection.getImages();
																if(poppedSectionImages == null)
																{
																	poppedSectionImages = new ArrayList<Image>();
																}
																
																poppedSectionImages.add(image);
																associatedImageSet.add(image);
																System.out.println(image.getPath());
															}
														}																	
													}
													
//													new BufferedReader(new InputStreamReader(System.in)).readLine();
												}											
											}
										}
										
										if(regEx1Matched && !notHeading)
										{
											System.out.println("Pushing  --  " + currentSectionHeadingPrefix);
											sectionHeadingPrefixHierarchy.push(currentSectionHeadingPrefix);
										}
										new BufferedReader(new InputStreamReader(System.in)).readLine();
										if(!notHeading)
										{
											noSectionHeadingYet = false;
											sectionHierarchy.push(tempSection);
										}
									}
									else if(tagClass == 4 || tagClass == 5 || tagClass == 6)
									{
										Image imageToBeAdded = null;
										
										if(tagClass == 4)
										{
											List<Image> curPageImages = pagewiseImages.get(pageNo);
											List<Image> neighbors = new ArrayList<Image>();
											
											for(Image image : curPageImages)
											{
												ClusterInfo imageClusterInfo = image.getImageClusterInformation();
												imageClusterInfo.setY1(imageClusterInfo.getY1() - 5);
												imageClusterInfo.setY2(imageClusterInfo.getY2() + 5);
												
												if(getDistanceBetweenSegments(segment, imageClusterInfo) < 15)
												{
													neighbors.add(image);
												}
											}
											
											System.out.println("--Neighboring Images--");							
											for(Image image : neighbors)
											{
												System.out.println(segment);
												System.out.println(image.getImageClusterInformation());
//												new BufferedReader(new InputStreamReader(System.in)).readLine();
												if(image.getImageClusterInformation().getY1() > segment.getY1())
												{
													image.setCaption((TextBlock) segment);
													imageToBeAdded = image;
													System.out.println(image.getPath());
													System.out.println("Caption below");
													break;
												}
												if(image.getImageClusterInformation().getY2() < segment.getY2())
												{
													image.setCaption((TextBlock) segment);
													imageToBeAdded = image;
													System.out.println("Caption Above");
													System.out.println(image.getPath());
												}
											}							
										}
										
										if(sectionHierarchy.size() > 0)
										{
											Section topSection = sectionHierarchy.peek();
											
											if(tagClass == 4)
											{
												if(topSection.getImages() == null && imageToBeAdded != null)
												{
													List<Image> tempImages = new ArrayList<Image>();
													tempImages.add(imageToBeAdded);
													topSection.setImages(tempImages);
												}
												else if(imageToBeAdded != null)
												{
													topSection.getImages().add(imageToBeAdded);
												}
												associatedImageSet.add(imageToBeAdded);
											}
											else
											{
												if(topSection.getContent() == null)
												{
													List<TextBlock> topSectionContent = new ArrayList<TextBlock>();
													topSectionContent.add((TextBlock) segment);
													topSection.setContent(topSectionContent);
												}
												else
												{
													topSection.getContent().add((TextBlock) segment);
												}
											}
										}
										else
										{
											if(tagClass == 4)
											{
												Section tempSection = new Section();
												List<Image> tempImages = new ArrayList<Image>();
												tempImages.add(imageToBeAdded);
												tempSection.setImages(tempImages);
												logicalStructure.add(tempSection);
												associatedImageSet.add(imageToBeAdded);
											}
											else
											{
												Section tempSection = new Section();
												List<TextBlock> tempContent = new ArrayList<TextBlock>();
												tempContent.add((TextBlock) segment);
												tempSection.setContent(tempContent);
												logicalStructure.add(tempSection);
											}
										}
									}
								}
//								segmentIndex++;
							}
						}
						else
						{
//							String path = writeImage(pageNo + 1, imageCounter, curClusterInfo.getX1(), curClusterInfo.getY1(), curClusterInfo.getX2(), curClusterInfo.getY2());
//							imageCounter++;
//							System.out.println(path);
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
						}
					}
					else
					{
						int segmentIndex = 0;
						for(GenericSegment segment : cluster)
						{
							if(segment instanceof TextBlock)
							{
								score(segment, sortedPagewiseTextBlocks.get(pageNo));
								segmentIndex++;
							}
						}
					}					
//					System.out.println(curClusterInfo.toString());
//					new BufferedReader(new InputStreamReader(System.in)).readLine();
				}
				
				List<Image> curPageImages = pagewiseImages.get(pageNo);
				if(curPageImages != null)
				{
					for(Image image : curPageImages)
					{
						if(!associatedImageSet.contains(image))
						{
							boolean addToRemainingImages = false;
							
							if(sectionHierarchy.size() == 0)
							{
								addToRemainingImages = true;
							}
							else
							{
								Section lastSection = sectionHierarchy.peek();
								
								if(lastSection != null)
								{
									List<Image> lastSectionImages = lastSection.getImages();
									if(lastSectionImages == null)
									{
										lastSectionImages = new ArrayList<Image>();
									}
									
									lastSectionImages.add(image);
									associatedImageSet.add(image);
									System.out.println(image.getPath());								
								}
								else
								{
									addToRemainingImages = true;
								}
							}
							
							if(addToRemainingImages)
							{
								if(remainingImagesSection == null)
								{
									remainingImagesSection = new Section();
									TextBlock tempTextBlock = new TextBlock(0, 0, 0, 0, "Misc. Images", "", 0);
									remainingImagesSection.setHeading(tempTextBlock);
								}
								List<Image> tempImages = new ArrayList<Image>();
								tempImages.add(image);
								remainingImagesSection.setImages(tempImages);
							}
						}
					}
				}
				
				pageNo++;
			}
			
			if(remainingImagesSection != null)
				logicalStructure.add(remainingImagesSection);	
			
			displayLogicalStructure(logicalStructure);
			
			
//			new BufferedReader(new InputStreamReader(System.in)).readLine();
//			
//			for(Map<String, Integer> page : pagewiseFontMap)
//			{
//				Iterator<String> keys = page.keySet().iterator();
//				
//				while(keys.hasNext())
//				{
//					String key = keys.next();
//					System.out.println("K : " + key + " V : " + page.get(key));
//				}
//				
//				System.out.println();				
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
//			}
//			
//			Iterator<String> keys = documentWideFontMap.keySet().iterator();
//			
//			while(keys.hasNext())
//			{
//					String key = keys.next();
//					System.out.println("K : " + key + " V : " + documentWideFontMap.get(key));
//			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	public static List<Section> getPDFText(String[] args)
    {
    	boolean toConsole = false;
//        boolean table = false;
//        boolean autotable = false;
    	boolean toXHTML = true;
        boolean borders = true;
        boolean rulingLines = true;
        boolean processSpaces = false;
        int currentArgumentIndex = 0;
        String password = "";
        String encoding = ProcessFile.DEFAULT_ENCODING;
        try {
			PDFObjectExtractor extractor = new PDFObjectExtractor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String inFile = null;
        String outFile = null;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        for( int i=0; i<args.length; i++ )
        {
            if( args[i].equals( ProcessFile.PASSWORD ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                password = args[i];
            }
            else if( args[i].equals( ProcessFile.ENCODING ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                encoding = args[i];
            }
            else if( args[i].equals( ProcessFile.START_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                startPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( ProcessFile.END_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                endPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( ProcessFile.CONSOLE ) )
            {
                toConsole = true;
            }
            /*
            else if( args[i].equals( AUTOTABLE ))
            {
                autotable = true;
            }
            else if( args[i].equals( TABLE ))
            {
                table = true;
            }
            */
            else if( args[i].equals( ProcessFile.NOBORDERS ))
            {
            	borders = false;
            }
            /*else if( args[i].equals( XMILLUM ) )
            {
                toXHTML = false;
            }*/
            else if( args[i].equals( ProcessFile.NORULINGLINES ))
            {
            	rulingLines = false;
            }
            else if( args[i].equals( ProcessFile.PROCESS_SPACES ))
            {
            	processSpaces = false;
            }
            else
            {
                if( inFile == null )
                {
                    inFile = args[i];
                }
                else
                {
                    outFile = args[i];
                }
            }
        }

        if( inFile == null )
        {
            usage();
        }

        if( outFile == null && inFile.length() >4 )
        {
            outFile = inFile.substring( 0, inFile.length() -4 ) + ".txt";
        }
        
        // decide whether we have a pdf or image (TODO: command-line override)
        /*
        boolean pdf = true;
		if (inFile.endsWith("png") ||
			inFile.endsWith("tif") ||
			inFile.endsWith("tiff")||
			inFile.endsWith("jpg") ||
			inFile.endsWith("jpeg")||
			inFile.endsWith("PNG") ||
			inFile.endsWith("TIF") ||
			inFile.endsWith("TIFF") ||
			inFile.endsWith("JPG") ||
			inFile.endsWith("JPEG")) pdf = false;
		*/
        
//		System.err.println("Processing: " + inFile);
		
        // load the input file
        
        File inputFile = new File(inFile);
        
        fileName = inFile;
        /*
        STR_INFILE = inputFile.getCanonicalPath();
        File tempOutFile = new File(outFile); // tmp for str only
        if (tempOutFile.getParent() != null)
        	STR_OUTPUT_PATH = tempOutFile.getParent();
        */
        byte[] inputDoc = null;
		try {
			inputDoc = ProcessFile.getBytesFromFile(inputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//        org.w3c.dom.Document resultDocument = null;
        
        List<Page> theResult = null;
        
        // set up page processor object
        PageProcessor pp = new PageProcessor();
        pp.setProcessType(PageProcessor.PP_BLOCK);
        //pp.setProcessType(PageProcessor.PP_FRAGMENT);
        pp.setRulingLines(rulingLines);
        pp.setProcessSpaces(processSpaces);
        // no iterations should be automatically set to -1
        
        // do the processing
    	try {
			theResult =
				ProcessFile.processPDFtoPageList(inputDoc, pp, borders,
				startPage, endPage, encoding, password);
		} catch (DocumentProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//System.out.println(theResult);
    	
    	List<List<MyTextLine>> pages = null;
		try {
			pages = pdfboxExtract(inFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//System.out.println(pages);
    	
    	new extractpdf().preprocess(theResult, pages);
    	
    	cluster();   	
    	
    	processImages();
    	
    	tagContent();
    	
    	return logicalStructure;
    	//tagContent();
    	
    }
	
	
	
	public static int score(GenericSegment block, Page page)
	{
		double probCount[] = new double[7];
		String classes[] = new String[7];
		
		classes[0] = new String("Title text");
		classes[1] = new String("Section Heading");
		classes[2] = new String("Header");
		classes[3] = new String("Footer");
		classes[4] = new String("Caption (Table, Figure)");
		classes[5] = new String("Body content");
		classes[6] = new String("Non-textual content");
		
		for(int i=0 ; i<7 ; i++)
			probCount[i] = 0.0;
		
		try
		{
			
			
			System.out.println("------------Scoring------------");
			
//			new BufferedReader(new InputStreamReader(System.in)).readLine();
			
			if(block instanceof TextBlock)
			{
				List<TextLine> lines = ((TextBlock) block).getItems();
				
//				Text Content - SIZE scoring
				
				int textBlockSize = 0;
				textBlockSize = lines.size();
				if(textBlockSize > 0)
				{	
					for(int i=0 ; i<7 ; i++)
						probCount[i] += 10 / textBlockSize;
				}
				if(textBlockSize >= 4)
				{
					probCount[2] += 20 / textBlockSize;
					probCount[3] += 20 / textBlockSize;
					probCount[5] += 20 / textBlockSize;
				}
				if(textBlockSize >= 6)
				{
					probCount[5] += 30 / textBlockSize;
				}
				
//				Text Content - SIZE scoring ends
				
//				Position and Span scoring
				
//				Page number	
				
				int pageNo = page.getPageNo();
				
				System.out.println("Page No Scoring : " + pageNo);
				
				if(pageNo == 0)
				{
					probCount[0] += 5;
					for(int i=1 ; i<7 ; i++)
						probCount[i] += 3;
				}
				else if(pageNo > 0)
				{
					probCount[0] += 3;
					for(int i=1 ; i<7 ; i++)
						probCount[i] += 5;
				}
				
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
//				Location within page
				
				float pageHeight = page.getHeight();
				float pageWidth = page.getWidth();
				float pageMid = pageHeight / 2;
								
				System.out.println("Page Height : " + pageHeight);
				System.out.println("Page Width : " + pageWidth);
				
				float bottomY = pageHeight - block.getY1();
				float topY = pageHeight - block.getY2();
				float midY = pageHeight - block.getYmid();
				
				System.out.println("Y -- Top : Mid : Bottom -- " + topY + " : " + midY + " : " + bottomY);
				
				if(bottomY * 100 / pageHeight < 7)
				{
					System.out.println("top 7%");
					probCount[2] += 15;
					probCount[0] += 6;
					probCount[1] += 5;
					probCount[4] += 5;
					probCount[5] += 5;
					probCount[6] += 5;
					probCount[3] += 3;
				}
				else if(topY * 100 / pageHeight > 93)
				{
					System.out.println("bottom 7%");
					probCount[2] += 3;
					probCount[0] += 5;
					probCount[1] += 6;
					probCount[4] += 6;
					probCount[5] += 6;
					probCount[6] += 6;
					probCount[3] += 15;
				}
				else
				{
					double difference = midY - pageMid;
					
					if(difference > -0.9 * pageMid)
					{
						probCount[3] += 1;
						if(difference < 0)
						{
							probCount[0] -= 10 / difference;
							probCount[1] -= 20 / difference;
							probCount[4] -= 20 / difference;
							probCount[5] -= 20 / difference;
							probCount[6] -= 20 / difference;
						}
						else if(difference > 0)
						{
							probCount[0] += 10 / difference;
							probCount[1] += 20 / difference;
							probCount[4] += 20 / difference;
							probCount[5] += 20 / difference;
							probCount[6] += 20 / difference;
						}
						else
						{
							if(pageNo == 0)
								probCount[0] += 12;
							else
								probCount[0] += 5;
							probCount[1] += 10;
							probCount[4] += 10;
							probCount[5] += 10;
							probCount[6] += 10;
						}
						probCount[2] += 1;						
					}
					else if(difference <= -0.9 * pageMid)
					{
						probCount[3] += 1;
						probCount[0] -= 20 / difference;
						probCount[1] -= 10 / difference;
						probCount[4] -= 10 / difference;
						probCount[5] -= 10 / difference;
						probCount[6] -= 10 / difference;
						probCount[2] += 1;
					}
				}
				
//				Span
				
				float pageArea = page.getArea();
				float blockArea = block.getArea();
				System.out.println("page height & width : " + pageHeight + " " + pageWidth);
				System.out.println("Page Area Scoring : " + pageArea + " : " + blockArea);
				
				if(((blockArea / pageArea) * 100) > 0)
				{
					for(int i=0 ; i<7 ; i++)
						probCount[i] += blockArea * 50 / pageArea;
				}
				if(((blockArea / pageArea) * 100) > 5)
				{
					probCount[5] += blockArea * 50 / pageArea;
				}
				
				
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
//				Position and Span scoring ends
				
//				Text Content - Content REGEX scoring
				
				Boolean sectionHeadingMatched = false;
				Boolean captionMatched = false;
				
				System.out.println("Text Content - REGEX scoring");

				String blockText = ((TextBlock)block).getText();
				
				if(isSectionHeading((TextBlock) block))
				{
					sectionHeadingMatched = true;
					System.out.println("Section Heading Matched");
					probCount[1] += 17;
					probCount[0] += 3;
					for(int i=2 ; i<7 ; i++)
						probCount[i] += 5;
					probCount[5] += 4;
				}
								
				System.out.println(isCaption((TextBlock) block));
					
				if(isCaption((TextBlock) block))
				{
					captionMatched = true;
//					System.out.println("Table Caption Matched");
					probCount[4] += 21;
					probCount[5] += 8;
					probCount[6] += 3;
					for(int i=0 ; i <4 ; i++)
						probCount[i] += 3;
				}
				
				
//				Content length based scoring
				
				if(textBlockSize <= 2)
				{
					if(blockText.length() <= 3)
					{
						probCount[6] += 5;
						for(int i=0 ; i<6 ; i++)
							probCount[i] += 1;
					}
					else if(blockText.length() <= 6)
					{
						probCount[6] += 7;
						probCount[1] += 2;
						probCount[2] += 2;
						probCount[3] += 2;
						for(int i=0 ; i<6 ; i++)
							probCount[i] += 3;
					}
					else if(blockText.length() <= 12)
					{
						probCount[6] += 3;
						probCount[5] += 2;
						probCount[4] += 3;
						for(int i=0 ; i<4 ; i++)
							probCount[i] += 4;
					}
					else
					{
						probCount[6] += 2;
						probCount[5] += 3;
						probCount[4] += 7;
						for(int i=0 ; i<4 ; i++)
							probCount[i] += 4;
					}
				}
				else if(textBlockSize >= 3 && textBlockSize <= 5)
				{
					if(blockText.length() <= 15)
					{
						probCount[0] += 5;
						probCount[1] += 4;
						probCount[2] += 3;
						probCount[3] += 3;
						probCount[4] += 5;
						probCount[5] += 7;
						probCount[6] += 9;
					}
					else if(blockText.length() <= 30)
					{
						probCount[0] += 4;
						probCount[1] += 3;
						probCount[2] += 5;
						probCount[3] += 5;
						probCount[4] += 4;
						probCount[5] += 8;
						probCount[6] += 7;
					}
					else if(blockText.length() <= 50)
					{
						probCount[0] += 4;
						probCount[1] += 3;
						probCount[2] += 5;
						probCount[3] += 5;
						probCount[4] += 4;
						probCount[5] += 8;
						probCount[6] += 6;
					}
					else
					{
						probCount[0] += 4;
						probCount[1] += 3;
						probCount[2] += 4;
						probCount[3] += 4;
						probCount[4] += 4;
						probCount[5] += 9;
						probCount[6] += 5;
					}
				}
				else if(textBlockSize >= 5)
				{
					if(blockText.length() <= 50)
					{
						probCount[0] += 3;
						probCount[1] += 2;
						probCount[2] += 6;
						probCount[3] += 6;
						probCount[4] += 2;
						probCount[5] += 5;
						probCount[6] += 9;
					}
					else if(blockText.length() <= 75)
					{
						probCount[0] += 3;
						probCount[1] += 2;
						probCount[2] += 5;
						probCount[3] += 5;
						probCount[4] += 2;
						probCount[5] += 7;
						probCount[6] += 7;
					}
					else
					{
						probCount[0] += 3;
						probCount[1] += 2;
						probCount[2] += 4;
						probCount[3] += 4;
						probCount[4] += 2;
						probCount[5] += 9;
						probCount[6] += 5;
					}
				}
							
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
//				Text Content - Content REGEX scoring ends
				
//				Font Info scoring
				
				System.out.println("Font Info scoring");
				
				Map<String, Integer> textBlockFontMap = new HashMap<String, Integer>();
				
				for(TextLine line : lines)
				{
					String key = line.getFontName() + ":" + line.getFontSize();
					
					System.out.println(key);
					
					if(!textBlockFontMap.containsKey(key))
					{
						textBlockFontMap.put(key, 1);
					}
					else
					{
						int count = ((Integer)textBlockFontMap.get(key)).intValue();
						count++;
						textBlockFontMap.put(key, count);
					}
				}
				
				if(textBlockFontMap.size() == 1)
				{
					Iterator<String> textBlockFontKeyIterator = textBlockFontMap.keySet().iterator();
					while(textBlockFontKeyIterator.hasNext())
					{
						String key = textBlockFontKeyIterator.next();
						
						if(key == null)
							continue;
						
						Map<String, Integer> curPageFontMap = pagewiseFontMap.get(page.getPageNo());
						
						Boolean bold = false;
						Boolean italic = false;
						Boolean medium = false;
											
						String fontName = (key.split(":"))[0];
						if(fontName != null)
						{
							String boldRegEx = ".*bold.*";
							String italicRegEx = ".*italic.*";
							String medium1RegEx = ".*med(ium)?.*";
							String medium2RegEx = ".*medi(um)?.*";
							
							Pattern boldPattern = Pattern.compile(boldRegEx, Pattern.CASE_INSENSITIVE);
							Pattern italicPattern = Pattern.compile(italicRegEx, Pattern.CASE_INSENSITIVE);
							Pattern medium1Pattern = Pattern.compile(medium1RegEx, Pattern.CASE_INSENSITIVE);
							Pattern medium2Pattern = Pattern.compile(medium2RegEx, Pattern.CASE_INSENSITIVE);
							
							Matcher matcher = boldPattern.matcher(fontName);
							if(matcher.matches())
							{
								System.out.println("BOLD FONT : "+ fontName);
								probCount[0] += 13;
								probCount[1] += 13;
								probCount[2] += 5;
								probCount[3] += 5;
								probCount[4] += 4;
								probCount[5] += 3;
								probCount[6] += 2;
								bold = true;
							}
							matcher = italicPattern.matcher(fontName);
							if(matcher.matches())
							{
								System.out.println("ITALIC FONT : "+ fontName);
								probCount[0] += 9;
								probCount[1] += 11;
								probCount[2] += 7;
								probCount[3] += 7;
								probCount[4] += 5;
								probCount[5] += 3;
								probCount[6] += 2;
								italic = true;
							}
							matcher = medium1Pattern.matcher(fontName);
							if(matcher.matches())
							{
								System.out.println("MEDIUM FONT : "+ fontName);
								probCount[0] += 5;
								probCount[1] += 8;
								probCount[2] += 5;
								probCount[3] += 5;
								probCount[4] += 4;
								probCount[5] += 3;
								probCount[6] += 3;
								medium = true;
							}
							matcher = medium2Pattern.matcher(fontName);
							if(matcher.matches())
							{
								System.out.println("MEDIUM FONT : " + fontName);
								probCount[0] += 5;
								probCount[1] += 8;
								probCount[2] += 5;
								probCount[3] += 5;
								probCount[4] += 4;
								probCount[5] += 3;
								probCount[6] += 3;
								medium = true;
							}
							else if(!bold && !italic && !medium)
							{
								System.out.println("REGULAR FONT : "+ fontName);
								probCount[0] += 4;
								probCount[1] += 5;
								probCount[2] += 8;
								probCount[3] += 8;
								probCount[4] += 8;
								probCount[5] += 8;
								probCount[6] += 6;
							}						
						}
						
						Boolean mostFrequent = false;
						
						if(documentWideFontMap.containsKey(key))
						{
							int documentWideFrequency = documentWideFontMap.get(key).intValue();
							
							List<Integer> fontFrequency = new ArrayList<Integer>(documentWideFontMap.values());
							Collections.sort(fontFrequency);
							
							if(documentWideFrequency == fontFrequency.get(fontFrequency.size() - 1))
							{
								probCount[4] += 15;
								probCount[5] += 20;
								probCount[6] += 10;
								for(int i=0 ; i<4 ; i++)
									probCount[i] += 1;
								
								mostFrequent = true;
							}
						}
						
						if(!mostFrequent)
						{
							double fontSize = Double.parseDouble((key.split(":"))[1]); 
							double difference = fontSize - averageFontSize;
							
							System.out.println("Difference : " + difference);
							
							if(difference <= 1 && difference >= -1)
							{
								probCount[0] += 2;
								probCount[1] += 4;
								probCount[2] += 10;
								probCount[3] += 10;
								probCount[4] += 10;
								probCount[5] += 15;
								probCount[6] += 8;
							}
							else if(difference > 1)
							{
								probCount[0] += 4.2 * difference;
								probCount[1] += 3 * difference;
								probCount[2] += 12 / difference;
								probCount[3] += 12 / difference;
								probCount[4] += 10 / difference;
								probCount[5] += 10 / difference;
								probCount[6] += 3 * difference;
							}
							else if(difference < -1)
							{
								probCount[0] -= 8 / difference;
								probCount[1] -= 8 / difference;
								probCount[2] -= 2.5 * difference;
								probCount[3] -= 2.5 * difference;
								probCount[4] -= 15 / difference;
								probCount[5] -= 10 / difference;
								probCount[6] -= 3 * difference;
							}
						}
					}
				}
				else if(textBlockFontMap.size() <= 3)
				{
					Iterator<String> textBlockFontKeyIterator = textBlockFontMap.keySet().iterator();
					
					while(textBlockFontKeyIterator.hasNext())
					{
						String key = textBlockFontKeyIterator.next();
						
						if(key == null)
							continue;
						
						Map<String, Integer> curPageFontMap = pagewiseFontMap.get(page.getPageNo());
						
						int curPageFontFrequency = 0;
						int documentWideFontFrequency = 0;
						
						if(curPageFontMap.containsKey(key))
							curPageFontFrequency = curPageFontMap.get(key).intValue();
						
						if(documentWideFontMap.containsKey(key))
							documentWideFontFrequency = documentWideFontMap.get(key);
						
						double curPageFontPercentage = curPageFontFrequency * (double)100 / pagewiseLineCount[page.getPageNo()];
						double documentWideFontPercentage = documentWideFontFrequency * (double)100 / documentLineCount;
						
						System.out.println("Page Font Percentage : " + curPageFontPercentage + " Document Font Percentage : " + documentWideFontFrequency + " Font : " + key);
						
						double fontPercentage = 0.85 * curPageFontFrequency + 0.15 * documentWideFontFrequency * pagewiseLineCount[page.getPageNo()] / documentLineCount;
						
						System.out.println("Font Percentage : " + fontPercentage);
						
						if(fontPercentage > 50)
						{
							probCount[5] += 18;
							probCount[6] += 1;
							probCount[4] += 1;
							for(int i=0 ; i<5 ; i++)
								probCount[i] += 1;							
						}
						else if(fontPercentage > 30)
						{
							probCount[5] += 10;
							probCount[6] += 1;
							probCount[4] += 2;
							for(int i=0 ; i<5 ; i++)
								probCount[i] += 1;
						}
						else if(fontPercentage > 10)
						{
							probCount[5] += 8;
							probCount[6] += 1;
							probCount[4] += 2;
							probCount[1] += 2; 
							for(int i=0 ; i<5 ; i++)
								probCount[i] += 1;
						}
						else
						{
							probCount[5] += 3;
							probCount[6] += 4;
							for(int i=0 ; i<5 ; i++)
								probCount[i] += 7;
						}
					}
					
				}
				else
				{
					// Don't know
				}
				
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
//				Font Info scoring ends
				
				System.out.println("---------Block scores------");
				for(int i=0 ; i<7 ; i++)
					System.out.println(probCount[i] + "");
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
				System.out.println("---------------------------");
				
				int first = 0;
				int second = 0;
				int third = 0;
				
				if(probCount[0] > probCount[1] && probCount[0] > probCount[2])
				{
					first = 0;
					if(probCount[1] > probCount[2])
					{
						second = 1;
						third = 2;
					}
					else
					{
						second = 2;
						third = 1;
					}
				}
				else if(probCount[1] > probCount[0] && probCount[1] > probCount[2])
				{
					first = 1;
					if(probCount[0] > probCount[2])
					{
						second = 0;
						third = 2;
					}
					else
					{
						second = 2;
						third = 0;
					}
				}
				else// if(probCount[2] > probCount[1] && probCount[2] > probCount[0])
				{
					first = 2;
					if(probCount[1] > probCount[0])
					{
						second = 1;
						third = 0;
					}
					else
					{
						second = 0;
						third = 1;
					}
				}
				
				for(int index=3 ; index < 7 ; index++)
				{
					if(probCount[index] > probCount[first])
					{
						second = first;
						third = second;
						first = index;
					}
					else if(probCount[index] > probCount[second])
					{
						third = second;
						second = index;
					}
					else if(probCount[index] > probCount[third])
					{
						third = index;
					}
				}
				
				System.out.println(probCount[first] + " > " + probCount[second] + " > " + probCount[third]);

				
				if((probCount[first] - probCount[second]) > 4.5)
				{
					System.out.println(((TextBlock) block).getText() + " ---- " + classes[first]);
					/* CUT FROM HERE */
					return first;
				}
				else
				{
					if(first == 2 || first == 3 || second == 2 || second == 3)
					{
						System.out.println(((TextBlock) block).getText() + " ---- Untagged (maybe non-textual content)");
					}
					else if(first == 6 || second == 6)
					{
						System.out.println(((TextBlock) block).getText() + " ---- Probably Non-textual Content");
					}
					else
					{
						System.out.println(((TextBlock) block).getText() + " ---- Untagged - (may be textual content)");
					}
					
					if(sectionHierarchy.size() > 0)
					{
						Section topSection = sectionHierarchy.peek();
						
						if(topSection.getContent() == null)
						{
							List<TextBlock> topSectionContent = new ArrayList<TextBlock>();
							topSectionContent.add((TextBlock) block);
							topSection.setContent(topSectionContent);
						}
						else
						{
							topSection.getContent().add((TextBlock) block);
						}
					}
					else
					{
						Section tempSection = new Section();
						List<TextBlock> tempContent = new ArrayList<TextBlock>();
						tempContent.add((TextBlock) block);
						tempSection.setContent(tempContent);
						logicalStructure.add(tempSection);
					}
					
					return -1;
				}				
			}
			
//				new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	
	public static void displayLogicalStructure(List<Section> logicalStructure)
	{
		if(logicalStructure != null)
		{
			for(Section section : logicalStructure)
			{
				System.out.println("Section begin");
										
				TextBlock heading = section.getHeading();
				List<TextBlock> content = section.getContent();
				List<Image> images = section.getImages();
				
				if(heading != null)
				{
					System.out.println("Heading : " + heading.getText());
				}
				
				if(content != null)
				{
					System.out.println("Content");
					for(TextBlock contentItem : content)
					{
						System.out.println(contentItem.getText());
//						List<TextLine> lines = contentItem.getItems();
//						for(TextLine line : lines)
//						{
//							List<LineFragment> fragments = line.getItems();
//							for (LineFragment lineFragment : fragments)
//							{
//								List<TextFragment> textFragments = lineFragment.getItems();
//								for (TextFragment textFragment : textFragments)
//								{
//									System.out.println(textFragment.getText());
//								}
//							}
//						}
					}
				}
				
				if(images != null)
				{
					System.out.println("Images");
					System.out.println(images.size());
					for (Image image : images)
					{
						System.out.println("Image Path : " + image.getPath());
						System.out.println("Image Caption : " + image.getCaption());
						System.out.println("Image Cluster Information : " + image.getImageClusterInformation().toString());
					}
				}
				
				List<Section> subSections = section.getSubSections();
				displayLogicalStructure(subSections);
				
				System.out.println("Section end");
			}
		}
	}
}
