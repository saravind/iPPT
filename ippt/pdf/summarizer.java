package ippt.pdf;


import java.awt.Rectangle;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ContentHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.apache.pdfbox.util.TextPosition;
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

public class summarizer
{
	
	static String fileName = "";
	static List<GenericSegment> textBlocks = null;
	static List<GenericSegment> lineSegments = null;
	static List<GenericSegment> imageSegments = null;
	
	static List<Page> sortedPagewiseTextBlocks = null;
	
	static List<Page> pagewiseTextBlocks = null;
	static List<Page> pagewiseLineSegments = null;
	static List<Page> pagewiseImageSegments = null;

	
	public summarizer()
	{
		
	}
	
	public summarizer(String inputFile, String outputFile)
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
    	
    	System.out.println(theResult);
    	
    	List<List<MyTextLine>> pages = pdfboxExtract(inFile);
    	
    	System.out.println(pages);
    	
    	new summarizer().preprocess(theResult, pages);
    	
        // now output the XML Document by serializing it to output
//        Writer output = null;
//        if( toConsole )
//        {
//            output = new OutputStreamWriter( System.out );
//        }
//        else
//        {
//            if( encoding != null )
//            {
//                output = new OutputStreamWriter(
//                    new FileOutputStream( outFile ), encoding );
//            }
//            else
//            {
//                //use default encoding
//                output = new OutputStreamWriter(
//                    new FileOutputStream( outFile ) );
//            }
//            //System.out.println("using out put file: " + outFile);
//        }
//        //System.out.println("resultDocument: " + resultDocument);
//        serializeXML(resultDocument, output);
//        
//        if( output != null )
//        {
//            output.close();
//        }
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
        
        String password = "";
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
        PDDocument document = null;
        try
        {
            try
            {
                //basically try to load it from a url first and if the URL
                //is not recognized then try to load it from the file system.
                //URL url = new URL( pdfFile );
            	URL url = new URL( inputFile );
                document = PDDocument.load(url, force);
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
            	
            	document = PDDocument.load(inputFile, force);
                if( outputFile == null && inputFile.length() >4 )
                {
                    outputFile = inputFile.substring( 0, inputFile.length() -4 ) + ext;
                }
            }

            if( document.isEncrypted() )
            {
                StandardDecryptionMaterial sdm = new StandardDecryptionMaterial( password );
                document.openProtection( sdm );
                AccessPermission ap = document.getCurrentAccessPermission();

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

            retrievedPages = stripper.retrieveText( document, output1 );                
        }
        finally
        {
            if( output != null )
            {
                output.close();
            }
            if( document != null )
            {
                document.close();
            }
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
		TextBlock textBlock = null;
		List<MyTextLine> pdfboxPage = null;
		Page pdfXtkPage = null;
		double pdfXtkPageHeight = 0.0;
		
		try
		{
			int pageCount = 0;
			
			sortedPagewiseTextBlocks = new ArrayList<Page>();
			
			pagewiseTextBlocks = new ArrayList<Page>();
			pagewiseLineSegments = new ArrayList<Page>();
			pagewiseImageSegments = new ArrayList<Page>();
			
			pdfXtkPageHeight = theResult.iterator().next().getHeight();
			
//			System.out.println(theResult.size());
			
//			String regexPattern = "-;:*?";
//			
//			for(int ch = 1 ; ch <= 8 ; ch++)
//			{
//				regexPattern += (char)ch;
//			}
//			
//			for(int ch = 11 ; ch <= 12 ; ch++)
//			{
//				regexPattern += (char)ch;
//			}
//			
//			for(int ch = 14 ; ch <= 27 ; ch++)
//			{
//				regexPattern += (char)ch;
//			}
//			
//			for(int ch = 127 ; ch <= 65535 ; ch++)
//			{
//				regexPattern += (char)ch;
//			}
//			
//			regexPattern = "(.*)" + "([" + regexPattern + "])" + "(.*)";
			
//			System.out.println(regexPattern);
//			System.out.println();
//			
//			new BufferedReader(new InputStreamReader(System.in)).readLine();

			
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
				
				Page tempPage = new Page(imageSegments);
				pagewiseImageSegments.add(tempPage);
				tempPage = new Page(textBlocks);
				pagewiseTextBlocks.add(tempPage);
				tempPage = new Page(lineSegments);
				pagewiseLineSegments.add(tempPage);
				
				pageCount++;				
			}
			
			Iterator<Page> pagewiseTextBlockIterator = pagewiseTextBlocks.iterator();
			Iterator<List<MyTextLine>> pdfboxPageIterator = pages.iterator();
			
//			System.out.println(pages.size());
//			
//			for(List<MyTextLine> page : pages)
//			{
//				for(MyTextLine line : page)
//				{
//					System.out.println(line.getText());
//					new BufferedReader(new InputStreamReader(System.in)).readLine();
//				}
//			}
			
//			System.out.println(pageCount);
//			System.out.println();
//			System.out.println(pagewiseTextBlocks.size());
//			System.out.println();
//			System.out.println(pages.size());
			
			int counter = 0;
			
			//for( pdfXtkPage = (Page) pagewiseTextBlockIterator.next(), pdfboxPage = pdfboxPageIterator.next() ; pdfboxPageIterator.hasNext() && pagewiseTextBlockIterator.hasNext() ; pdfXtkPage = (Page) pagewiseTextBlockIterator.next(), pdfboxPage = pdfboxPageIterator.next() )
			while(pdfboxPageIterator.hasNext() && pagewiseTextBlockIterator.hasNext())
			{				
				pdfXtkPage = (Page) pagewiseTextBlockIterator.next(); 
				pdfboxPage = pdfboxPageIterator.next(); 
				System.out.println(counter++);
				
				List<GenericSegment> pdfXtkTextBlocks = pdfXtkPage.getItems();
				List<GenericSegment> sortedTextBlocks = new ArrayList<GenericSegment>();
				
				//for(MyTextLine curPdfboxLine : pdfboxPage)
				for(int j = 0 ; j < pdfboxPage.size() ; j++)
				{
					MyTextLine curPdfboxLine = pdfboxPage.get(j);
					int k = 0;
										
					
					textBlockloop : for(GenericSegment curTextBlock : pdfXtkTextBlocks)
					{						
						int count = 0;
						
						if (curTextBlock instanceof TextBlock)
						{
							List<TextLine> curTextBlockLines = ((TextBlock) curTextBlock).getItems();
							boolean omitTextBlock = false;

							TextLine firstLine = curTextBlockLines.iterator().next();
							
							String finalText = "";
							
							if(counter == -1)
							{
								TextLine lastLine = curTextBlockLines.get(curTextBlockLines.size() - 1);
								System.out.println(curPdfboxLine.getText() + " : " + curPdfboxLine.getX1() + " " + curPdfboxLine.getY1() + " " + curPdfboxLine.getX2() + " " + curPdfboxLine.getY2());
								System.out.println(firstLine.getText() + " : " + firstLine.getX1() + " " + firstLine.getY1() + " " + firstLine.getX2() + " " + firstLine.getY2());
								System.out.println(lastLine.getText() + " : " + lastLine.getX1() + " " + lastLine.getY1() + " " + lastLine.getX2() + " " + lastLine.getY2());
							}
							
							if(((double)Math.round(curPdfboxLine.getY1() * 100) / 100 == (double)Math.round((pdfXtkPageHeight - firstLine.getY1()) * 100) / 100) 
									&& ((double)Math.round(firstLine.getX1() * 100) / 100 == (double)Math.round(firstLine.getX1() * 100) / 100)
									&& (Math.abs(((double) Math.round(curPdfboxLine.getX1() * 100) / 100) - ((double) Math.round(firstLine.getX1() * 100) / 100)) < 50))
							{
//								System.out.println(curPdfboxLine.getText() + " " + curPdfboxLine.getX1() + " " + curPdfboxLine.getY1() + " " + curPdfboxLine.getX2() + " " + curPdfboxLine.getY2());
//								System.out.println();
//								System.out.println(firstLine.getText() + " " + firstLine.getX1() + " " + (pdfXtkPageHeight - firstLine.getY1()) + " " + firstLine.getX2() + " " + (pdfXtkPageHeight - firstLine.getY2()));
																	
								count ++;
								
								int curTextBlockSize = ((TextBlock) curTextBlock).getItems().size();
								
//								System.out.println(curTextBlockSize);
//								System.out.println();
								
								if(counter == -1)
								{
									System.out.println("text-block");
									for(TextLine line : curTextBlockLines)
									{
										System.out.println(line.getText());
									}
									System.out.println();
									
									new BufferedReader(new InputStreamReader(System.in)).readLine();
								}
								
								//String tempPdfXtkText = ((TextBlock) curTextBlock).getText();
								
								Boolean done = false;
								
								int pdfXtkLineCount = 0;
								int pdfboxLineCount = 0;
								
								Boolean pdfBoxWordsDone = false;
								int exceptionCount = 0;
															
								outer : while(pdfXtkLineCount < curTextBlockLines.size())
								{
									String tempPdfXtkTextLine = curTextBlockLines.get(pdfXtkLineCount).getText();
									String tempPdfBoxLine;
									if(j + pdfboxLineCount < pdfboxPage.size())
									{
										tempPdfBoxLine = pdfboxPage.get(j + pdfboxLineCount).getText();
									}
									else
									{
										break textBlockloop;
									}
									
									if(counter == 4)
									{
										System.out.println("pdfbox line : " + tempPdfBoxLine);
										System.out.println("pdfXtk line : " + tempPdfXtkTextLine);
									}
									
									String tempPdfBoxWordsArray[] = tempPdfBoxLine.split(" ");
									List<String> tempPdfBoxWords = new ArrayList<String>(); 
									Pattern tokenSeperator = Pattern.compile("(.*)([-?!*:; ])(.*)");
									for(int arrayIndex = 0, length = tempPdfBoxWordsArray.length ; arrayIndex < length ; arrayIndex++)
									{
//										Matcher tokenSeperatorMatcher = tokenSeperator.matcher(tempPdfBoxWordsArray[arrayIndex]);
//										if(tokenSeperatorMatcher.matches())
//										{
//											System.out.println("Matcher  :  " + tokenSeperatorMatcher.groupCount());
//											for(int count1 = 0, len = tokenSeperatorMatcher.groupCount() ; count1 <= len ; count1++)
//											{
//												System.out.println(tokenSeperatorMatcher.group(count1));
//												if(tokenSeperatorMatcher.group(count1) != " ")
//												{
//													tempPdfBoxWords.add(tokenSeperatorMatcher.group(count1));
//												}
//											}
//											System.out.println("Matcher end");
//										}
//										else
//										{
//											String tempWord = tempPdfBoxWordsArray[arrayIndex];
//											Boolean flag = false;
//											
//											for(int i=1 ; i<65536 ; i++)
//											{
//												if((i >= 1 && i <= 8) || i == 11 || i == 12 || (i >= 14 && i <= 27) || (i >= 127))
//												{
//													while(tempWord.indexOf(i) != -1 && tempWord.length() != 0)
//													{
//														if(tempWord.indexOf(i) != -1)
//															flag = true;
//														int splitIndex = tempWord.indexOf(i);
//														tempPdfBoxWords.add(tempWord.substring(0, splitIndex));
//														tempPdfBoxWords.add(tempWord.charAt(splitIndex) + "");
//														System.out.println(tempWord.charAt(splitIndex));
//														new BufferedReader(new InputStreamReader(System.in)).readLine();
//														tempWord = tempWord.substring(splitIndex+1, tempWord.length());														
//													}
//													
//													if(tempWord.length() != 0)
//													{
//														tempPdfBoxWords.add(tempWord);
//													}													
//												}
//											}
//											
//											new BufferedReader(new InputStreamReader(System.in)).readLine();
//											if(flag)
//											{
//												tempPdfBoxWords.add(tempPdfBoxWordsArray[arrayIndex]);
//											}
//										}
										
										String tempWord = tempPdfBoxWordsArray[arrayIndex];
										Boolean flag = false;
										System.out.println(tempWord);
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										
										for(int i=1 ; i<65536 ; i++)
										{
											if((i >= 1 && i <= 8) || i == 11 || i == 12 || (i >= 14 && i <= 27) || (i >= 127))
											{
												if(tempWord.indexOf((char)i) != -1)
												{
													while(tempWord.indexOf((char)i) != -1 && tempWord.length() != 0)
													{
														System.out.println("here");
														flag = true;
														int splitIndex = tempWord.indexOf(i);
														if(splitIndex != 0)
															tempPdfBoxWords.add(tempWord.substring(0, splitIndex));
														tempPdfBoxWords.add(tempWord.charAt(splitIndex) + "");
//														System.out.println(tempWord.charAt(splitIndex) + " : " + tempWord.charAt(splitIndex));
//														new BufferedReader(new InputStreamReader(System.in)).readLine();
														tempWord = tempWord.substring(splitIndex+1, tempWord.length());														
													}
												
													if(tempWord.length() != 0)
													{
														tempPdfBoxWords.add(tempWord);
													}
												}
//												System.out.println(i + " : " + (char)i);
//												if(i % 10 == 0)
//													new BufferedReader(new InputStreamReader(System.in)).readLine();
											}
										}
										
//										new BufferedReader(new InputStreamReader(System.in)).readLine();
										if(!flag)
										{
											tempPdfBoxWords.add(tempPdfBoxWordsArray[arrayIndex]);
										}										
									}
									
									if(counter == -1)
									{
										System.out.println("Testing tokenization");
										System.out.println();
										
										int tokenCount = 0;
										
										for(String word : tempPdfBoxWords)
										{
											System.out.println(tokenCount++ + " : " + word + " : " + word.length());
										}
										
										System.out.println();
										new BufferedReader(new InputStreamReader(System.in)).readLine();
									}
									
									inner : while(tempPdfXtkTextLine.length() != 0)
									{
										for( ; k<tempPdfBoxWords.size() && tempPdfXtkTextLine.length() != 0 ; k++)
										{
											String word = tempPdfBoxWords.get(k);
											
											if(counter == -1)
											{
												if(word.length() > tempPdfXtkTextLine.length() && exceptionCount == 0)
												{
													System.out.println("INDEXOUTOFBOUNDS");
//													new BufferedReader(new InputStreamReader(System.in)).readLine();
													exceptionCount++;
													break inner;
												}
												else if(exceptionCount > 0)
												{
													exceptionCount = 0;
													break outer;
												}
												else
													exceptionCount = 0;
												
												System.out.println(tempPdfXtkTextLine.substring(0, word.length()));
												
												System.out.println(tempPdfXtkTextLine + " : " + tempPdfXtkTextLine.length());
												System.out.println(word + " : " + word.length());
												
												new BufferedReader(new InputStreamReader(System.in)).readLine();
											}
											if(word.length() > tempPdfXtkTextLine.length() && exceptionCount == 0)
											{
												System.out.println("IndexOutOfBounds");
												System.out.println(curPdfboxLine.getText());
												System.out.println(word + " : " + tempPdfXtkTextLine);
//												new BufferedReader(new InputStreamReader(System.in)).readLine();
												exceptionCount++;
												break inner;
											}
											else if(exceptionCount > 0)
											{
												exceptionCount = 0;
												omitTextBlock = true;
												break outer;
											}
											else
												exceptionCount = 0;
												
											finalText += tempPdfXtkTextLine.substring(0, word.length()) + " ";
											tempPdfXtkTextLine = tempPdfXtkTextLine.substring(word.length(), tempPdfXtkTextLine.length());
										}
										
										if(k == tempPdfBoxWords.size())
											break;
									}
									
									if(k == tempPdfBoxWords.size())
									{
										k = 0;
										pdfboxLineCount++;
										pdfBoxWordsDone = true;
									}
									else
									{
										pdfBoxWordsDone = false;	
									}
									
									if(tempPdfXtkTextLine.length() == 0)
									{
										pdfXtkLineCount++;
									}
									
									System.out.println(finalText);
									
//									new BufferedReader(new InputStreamReader(System.in)).readLine();
								}
								
								if(pdfBoxWordsDone)
									j = j + pdfboxLineCount - 1;
								
//								new BufferedReader(new InputStreamReader(System.in)).readLine();												
							}
							
							if(finalText.length() > 0)
							{
								((TextBlock) curTextBlock).setText(finalText);
								TextBlock tempTextBlock = (TextBlock) curTextBlock.clone();
								sortedTextBlocks.add(tempTextBlock);
							}
							
//							System.out.println("final text-block");
//							System.out.println(((TextBlock) curTextBlock).getText());
//							new BufferedReader(new InputStreamReader(System.in)).readLine();
						}
					}
					
					if(counter == -1)
					{
						for(GenericSegment curTextBlock : pdfXtkTextBlocks)
						{
							System.out.println("text-block");
							System.out.println(((TextBlock) curTextBlock).getText());
							new BufferedReader(new InputStreamReader(System.in)).readLine();
						}
					}
				}

				Page tempPage = new Page(pdfXtkPage.getX1(), pdfXtkPage.getX2(), pdfXtkPage.getY1(), pdfXtkPage.getY2(), sortedTextBlocks);
				sortedPagewiseTextBlocks.add(tempPage);
				
				System.out.println("Page " + counter);
				
				for(GenericSegment block : sortedTextBlocks)
				{
					System.out.println(((TextBlock)block).getText());
					System.out.println();
				}
				
				new BufferedReader(new InputStreamReader(System.in)).readLine();
			}
			
			for(Page page : sortedPagewiseTextBlocks)
			{
				List<GenericSegment> textBlocks = page.getItems();
				for(GenericSegment block : textBlocks)
				{
					System.out.println("Font name : " + ((TextBlock)block).getFontName() + "Font size : " + ((TextBlock)block).getFontSize());
					System.out.println(((TextBlock)block).getText());
					
					new BufferedReader(new InputStreamReader(System.in)).readLine();
					
					List<TextLine> blockLines = ((TextBlock) block).getItems();
					for(TextLine line : blockLines)
					{
						System.out.println("Font name : " + line.getFontName() + "Font size : " + line.getFontSize());
						System.out.println(line.getText());
						
						new BufferedReader(new InputStreamReader(System.in)).readLine();
						
						List<LineFragment> lineFragments = line.getItems();
						for(LineFragment fragment : lineFragments) 
						{
							System.out.println("Font name : " + fragment.getFontName() + "Font size : " + fragment.getFontSize());
							System.out.println(fragment.getText());
							
							new BufferedReader(new InputStreamReader(System.in)).readLine();
							
							List<TextFragment> textFragments = fragment.getItems();
							for(TextFragment textFragment : textFragments)
							{
								System.out.println("Font name : " + textFragment.getFontName() + "Font size : " + textFragment.getFontSize());
								System.out.println(textFragment.getText());
								
								new BufferedReader(new InputStreamReader(System.in)).readLine();
							}
						}
					}
					
					System.out.println();
				}
				System.out.println();
			}
			
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void extractTextByArea(int pageNo, int x1, int x2, int y1, int y2)
	{
		PDDocument document = null;
        try
        {
            document = PDDocument.load(fileName);
            if( document.isEncrypted() )
            {
                try
                {
                    document.decrypt( "" );
                }
                catch( InvalidPasswordException e )
                {
                    System.err.println( "Error: Document is encrypted with a password." );
                    System.exit( 1 );
                } catch (CryptographyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition( true );
            Rectangle rect = new Rectangle( x1, y1, x2 - x1, y2 - y1);
            stripper.addRegion( "customArea", rect );
            List allPages = document.getDocumentCatalog().getAllPages();
            PDPage curPage = (PDPage)allPages.get( pageNo );
            stripper.extractRegions( curPage );
            System.out.println( "Text in the area:" + rect );
            System.out.println( stripper.getTextForRegion( "customArea" ) );

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally
        {
            if( document != null )
            {
                try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}

}

class MyTextBlock
{	
	String text = "";
	List<MyTextLine> lines;
	
	MyTextBlock()
	{
		text = new String();
		lines = new ArrayList<MyTextLine>();
	}
	
	MyTextBlock(List<MyTextLine> lines)
	{
		this.lines = lines;
		
		for(MyTextLine line : this.lines)
		{
			this.text += line.getText() + " ";
		}
	}
	
	public List<MyTextLine> getLines()
	{
		return lines;
	}
	
	public String getText()
	{
		return text;
	}
}

class MyTextLine
{
	double x1;
	double y1;
	double x2;
	double y2;
	String text = null;
	List<TextPosition> line;
	
	MyTextLine()
	{
		x1 = 0.0;
		y1 = 0.0;
		x2 = 0.0;
		y2 = 0.0;
		text = new String();
	}
	
	MyTextLine(List<TextPosition> line)
	{
		this.line = new ArrayList<TextPosition>(line);
		
		TextPosition firstCharacter = line.iterator().next();
		TextPosition lastCharacter = line.get(line.size() - 1);
		String tempText = "";
		
		setX1(firstCharacter.getX());
		setY1(firstCharacter.getY());
		
		setX2(lastCharacter.getX());
		setY2(lastCharacter.getY());
		
		for(TextPosition character : line)
		{
			if(character.toString() == null)
				tempText += " ";
			else
				tempText += character.toString();
		}			
		
		setText(tempText);
		
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
	
	public String getText()
	{
		return text;
	}
	
	public List<TextPosition> getLine()
	{
		return line;
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
	
	public void setText(String text)
	{
		this.text = text;
	}
}
