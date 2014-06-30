import ippt.pdf.ClusterInfo;
import ippt.pdf.Image;
import ippt.pdf.MetaData;
import ippt.pdf.Section;
import ippt.pdf.extractpdf;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.extjwnl.JWNL;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;

import org.apache.poi.hslf.model.Fill;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.SlideMaster;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase.Files;

import edu.stanford.nlp.ling.TaggedWord;

public class Summarizer
{
	Map<String, Integer> termFrequencies;
	List<String> document;
	List<String> sectionHeadings;
	
	boolean getBestInDocument = false;
	int numSentences = 20;
	
	private static Map<String, String> cliOptions = new HashMap<String, String>();
	
	String defaultPDFPath = "E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\pdf\\anthology-PDF\\poster969.pdf";
	String defaultPDFDirectoryPath = "E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\pdf\\anthology-PDF";
	
	Pattern referenceSectionPattern;
	
	public Summarizer()
	{
		if(cliOptions.containsKey("stopwordfile"))
		{
			Tokenizer.setStopWords(cliOptions.get("stopwordfile"));
		}
		else
		{
			System.out.println("Stop word file not specified");
			System.exit(0);
		}
		
		if(cliOptions.containsKey("numsentences"))
		{
			numSentences = Integer.parseInt(cliOptions.get("numsentences"));
		}
		
//		try
//		{
//			TfIdf.collectionTermFrequency = new HashMap<String, Integer>();
//			
//			for(int lineNo=0; lineNo<94; lineNo++)
//			{
//				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\8thSemProject\\textToPpt\\Corpus\\temp\\collectionTermFrequency" + lineNo + ".count")));
//				String line = null;
//				
//				Map<String, Integer> tempCollectionTermFrequency = new HashMap<String, Integer>();
//				while((line = br.readLine()) != null)
//				{
//					String[] lineParts = line.split(" ");
//					if(lineParts.length == 2)
//					{
//						tempCollectionTermFrequency.put(lineParts[0], Integer.parseInt(lineParts[1]));
//					}
//				}
//				
//				for(String key : tempCollectionTermFrequency.keySet())
//				{
//					if(TfIdf.collectionTermFrequency.containsKey(key))
//					{
//						TfIdf.collectionTermFrequency.put(key, TfIdf.collectionTermFrequency.get(key)+tempCollectionTermFrequency.get(key));
//					}
//					else
//					{
//						TfIdf.collectionTermFrequency.put(key, tempCollectionTermFrequency.get(key));
//					}
//				}
//			}
//			
//			
////			Map<String, Integer> documentTermFrequency = new HashMap<String, Integer>();
////			
////			for(int lineNo=0; lineNo<10000; lineNo++)
////			{
////				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\8thSemProject\\textToPpt\\Corpus\\temp\\documentTermFrequency" + lineNo + ".count")));
////				String line = null;
////				
////				Map<String, Integer> tempDocumentTermFrequency = new HashMap<String, Integer>();
////				while((line = br.readLine()) != null)
////				{
////					String[] lineParts = line.split(" ");
////					if(lineParts.length == 2)
////					{
////						tempDocumentTermFrequency.put(lineParts[0], Integer.parseInt(lineParts[1]));
////					}
////				}
////				
////				for(String key : tempDocumentTermFrequency.keySet())
////				{
////					if(documentTermFrequency.containsKey(key))
////					{
////						documentTermFrequency.put(key, documentTermFrequency.get(key)+tempDocumentTermFrequency.get(key));
////					}
////					else
////					{
////						documentTermFrequency.put(key, tempDocumentTermFrequency.get(key));
////					}
////				}
////			}
//			
//			Map<String, Integer> documentTermFrequency = new HashMap<String, Integer>();
//			
//			for(int lineNo=0; lineNo<94; lineNo++)
//			{
//				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\8thSemProject\\textToPpt\\Corpus\\temp\\collectionTermFrequency" + lineNo + ".count")));
//				String line = null;
//				
//				Map<String, Integer> tempDocumentTermFrequency = new HashMap<String, Integer>();
//				while((line = br.readLine()) != null)
//				{
//					String[] lineParts = line.split(" ");
//					if(lineParts.length == 2)
//					{
//						tempDocumentTermFrequency.put(lineParts[0], Integer.parseInt(lineParts[1]));
//					}
//				}
//				
//				for(String key : tempDocumentTermFrequency.keySet())
//				{
//					if(documentTermFrequency.containsKey(key))
//					{
//						documentTermFrequency.put(key, documentTermFrequency.get(key)+1);
//					}
//					else
//					{
//						documentTermFrequency.put(key, 1);
//					}
//				}
//			}
//			
//			TfIdf.documentFrequency = documentTermFrequency;
//			TfIdf.serializeMaps("");
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}System.exit(0);
		
//		try
//		{
//			Integer lineNo = 0;
//			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\pdf\\Files.txt")));
////			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\pdf\\count.txt")));
//			
//			String line = null;
////			while((line = br2.readLine()) != null)
////			{
////				lineNo = Integer.parseInt(line);System.out.println(lineNo);
////				BufferedWriter bw = new BufferedWriter(new FileWriter("E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\pdf\\count.txt"));
////				bw.write(new Integer(lineNo+1).toString());
////				bw.flush();
////			}
//			
//			while((line = br1.readLine()) != null)
//			{
//				TfIdf.collectionTermFrequency = new HashMap<String, Integer>();
//				TfIdf.documentFrequency = new HashMap<String, Integer>();
//				
//				Preprocess.getWordsFromPDF(line);System.out.println("here");
//				TfIdf.serializeMaps(lineNo.toString());
//				lineNo++;
//			}			
//		}
//		catch(Exception ex)
//		{
//			
//		}
		
		
		if(cliOptions.containsKey("preprocess"))
		{
			if(cliOptions.containsKey("inputtype"))
			{
				if(cliOptions.get("inputtype").equalsIgnoreCase("pdf"))
				{
					if(cliOptions.containsKey("inputfilepath"))
					{
						TfIdf.PreprocessPDFAndCalculateIdf(cliOptions.get("inputfilepath"));
					}
					else
					{
						TfIdf.PreprocessPDFAndCalculateIdf(defaultPDFDirectoryPath);
					}

					TfIdf.serializeMaps("");
				}
				else
				{
					TfIdf.PreprocessTxtFileAndCalculateIdf();
					TfIdf.serializeMaps("");
				}
			}
		}
		else
		{
			TfIdf.deserializeMaps();
		}
		
		
		LexRank lexRank = null;
		Similarity.SimilarityMethod similarity = null;
		
		if(cliOptions.containsKey("similaritymethod"))
		{
			try
			{
				similarity = Similarity.SimilarityMethod.get(cliOptions.get("similaritymethod"));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				System.exit(0);
			}
			
			lexRank = new LexRank(similarity);
		}
		else
		{
			lexRank = new LexRank();
		}
		
		List<Section> sections = null;
		if(cliOptions.containsKey("inputfilepath"))
		{
			sections = Preprocess.getWordsFromPDF(cliOptions.get("inputfilepath"));

			for(Section section : sections)
			{
				String heading = section.getHeadingString();
				if(heading != null && heading.matches("(?i)^[\\s]*([0-9]+\\.)*reference[s]?[\\s]*$"))
				{
					System.out.println("Initial References pattern matched : " + heading);
					continue;
				}
				else if(heading == null)
				{
					System.out.println("Initial Heading is null");
				}
				else
				{
					System.out.println("Initial References pattern not matched : " + heading);
				}
			}
			
			TfIdf.CalculateIdf();
		}
		else
		{
			sections = Preprocess.getWordsFromPDF(defaultPDFPath);
			TfIdf.CalculateIdf();
		}
		
		
		/*List<List<String>> sentenceList = new ArrayList<List<String>>();
		Map<List<String>, ScoredSection> sentenceSectionMap = new HashMap<List<String>, ScoredSection>(); 
		for(Section section : sections)
		{
			List<List<String>> sectionSentenceList = section.getSentenceWordsList();
			
			Iterator<List<String>> sentenceIterator = sectionSentenceList.iterator();
			while(sentenceIterator.hasNext())
			{
				ScoredSection summarySection = new ScoredSection(section);
				summarySection.createSummarySentenceList();
				
				sentenceSectionMap.put(sentenceIterator.next(), summarySection);
			}
			
			sentenceList.addAll(sectionSentenceList);
		}
		
		lexRank.scoreSentences(sentenceList, similarity);
		double[] sentenceScores = lexRank.getSentenceScores();*/
		
		
		List<List<String>> sentenceWordsList = new ArrayList<List<String>>();
		List<String> sentenceList = new ArrayList<String>();
		List<ScoredSection> summarySections = new ArrayList<ScoredSection>();
		Map<String, ScoredSection> sentenceSectionMap = new HashMap<String, ScoredSection>();
		
//		referenceSectionPattern = Pattern.compile("(?i)^[\\s]*([0-9]+\\.)*reference[s]?[\\s]*$", Pattern.CASE_INSENSITIVE);
		
		List<Image> miscellaneousImages = new ArrayList<Image>();		
		
		
		
		
//		System.out.println("Images list");
//		for(Section section : sections)
//		{
//			System.out.println("_" + section.getHeadingString());
//			List<Image> images = section.getImages();
//			if(images != null)
//			{
//				for(Image image : images)
//				{
//					if(image != null)
//					{
//						System.out.println("  " + image.getPath());
//					}
//				}
//			}
//		}	
		
		
		
		
		for(Section section : sections)
		{
			String heading = section.getHeadingString();
			if(heading != null)
			{
				if(heading.matches("(?i)^[\\s]*([0-9]+\\.)*[\\s]*reference[s]?[\\s]*$"))
				{
					System.out.println("References pattern matched : " + heading);
					continue;
				}
				else if(heading.equalsIgnoreCase("Misc. Images"))
				{
					miscellaneousImages.addAll(section.getImages());
					System.out.println("Image added to Miscellaneous Images list");
					continue;
				}
				else
				{
					System.out.println("References pattern not matched : " + heading);
				}
			}
			else
			{
				System.out.println("heading is null");
			}
			
			
			List<List<String>> sectionSentenceWordsList = section.getSentenceWordsList();
			List<String> sectionSentenceList = section.getSentenceList();
			
			ScoredSection summarySection = new ScoredSection(section);
			summarySections.add(summarySection);
			
			Iterator<String> sentenceIterator = sectionSentenceList.iterator();
			while(sentenceIterator.hasNext())
			{
				summarySection.createSummarySentenceList();
				
				sentenceSectionMap.put(sentenceIterator.next(), summarySection);
			}
			sentenceList.addAll(sectionSentenceList);
			sentenceWordsList.addAll(sectionSentenceWordsList);
		}//System.out.println("sentences : " + sentenceWordsList.size());
		
		if(cliOptions.get("similaritymethod").equalsIgnoreCase("tfidf"))
		{
			lexRank.scoreSentences(sentenceWordsList, similarity);
		}
		else if(cliOptions.get("similaritymethod").equalsIgnoreCase("wordnet"))
		{
			List<List<IndexWord>> taggedSentenceWordsList = Preprocess.annotatePOSTags(sentenceWordsList);
			lexRank.scoreIndexWordTaggedSentences(taggedSentenceWordsList, similarity);
		}
		
		double[] sentenceScores = lexRank.getSentenceScores();
		
		
		
//		for(int i=0; i<sentenceScores.length-1; i++)
//		{
//			int pos = i;
//			for(int j=i+1; j<sentenceScores.length; j++)
//			{
//				if(sentenceScores[j] > sentenceScores[pos])
//				{
//					pos = j;
//				}
//			}
//			
//			sentenceScores[i] = sentenceScores[i] + sentenceScores[pos];
//			sentenceScores[pos] = sentenceScores[i] - sentenceScores[pos];
//			sentenceScores[i] = sentenceScores[i] - sentenceScores[pos];
//			
//			List<String> sentence1 = sentenceList.get(i);
//			List<String> sentence2 = sentenceList.get(pos);
//			sentenceList.set(pos, sentence1);
//			sentenceList.set(i, sentence2);
//		}
//		for(int i=0; i<sentenceList.size(); i++)
//		{
//			List<String> sentence = sentenceList.get(i);
//			for(String word : sentence)
//			{
//				System.out.print(word + " ");
//			}
//			System.out.println(" : " + sentenceScores[i]);
//			System.out.println();
//		}
//		System.exit(0);
		
		setSummarySentences(sentenceList, sentenceScores, sentenceSectionMap, numSentences);
		
		for(ScoredSection section : summarySections)
		{
			System.out.println(section.getHeadingString());
		}
		
		for(ScoredSection section : summarySections)
		{
			if(section.getSummarySentenceList() == null || section.getSummarySentenceList().size() == 0)
			{
				List<Image> images = section.getImages();
				if(images != null)
				{
					miscellaneousImages.addAll(images);
				}
			}
		}
		
		
		MetaData metaData = extractpdf.getMetaData();
		String title = metaData.getTitleString();
		System.out.println("The title of the slide is : " + title);
		try
		{
			Section titleBlock = metaData.getTitles().get(0);
			System.out.println(titleBlock);
		}
		catch(Exception ex)
		{
			System.out.println("Caught Exception" + ex);
		}
		
		try
		{
			SlideShow Slideshow = new SlideShow();
			SlideMaster master = Slideshow.getSlidesMasters()[0];

	        Fill fill = master.getBackground().getFill();
	        fill.setFillType(Fill.FILL_SHADE_SHAPE);
	        fill.setBackgroundColor(Color.cyan);
	        
			if(cliOptions.containsKey("title"))
			{
				Slide slide = Slideshow.createSlide();
				
				slide.setFollowMasterBackground(false);
		        fill = slide.getBackground().getFill();
		        int idx = Slideshow.addPicture(new File(defaultPDFDirectoryPath + "\\TitleBackground.jpg"), Picture.JPEG);
		        fill.setFillType(Fill.FILL_PICTURE);
		        fill.setPictureData(idx);
		        
				TextBox textBox = new TextBox();
				textBox.setAnchor(new java.awt.Rectangle(100, 200, 300, 50));

				//use RichTextRun to work with the text format
				RichTextRun rt = textBox.getTextRun().getRichTextRuns()[0];
				rt.setFontSize(32);
				rt.setFontName("Calibri");
				rt.setFontColor(Color.black);
				rt.setBold(true);
				rt.setUnderlined(true);
				rt.setAlignment(TextBox.AlignCenter);
				
				if(title != null && !title.equals(""))
				{
					textBox.setText(title);
				}
				else
				{
					textBox.setText(cliOptions.get("title"));
				}
				
				slide.addShape(textBox);
			}
			
			Slide slide = Slideshow.createSlide();
			slide.setFollowMasterBackground(true);
			
			int titleBoxHeight = 0;
			
			String headingText = "Table Of Contents";
			if(!headingText.equals(""))
			{
//				String sectionHeadingPattern1 = "^([1-9]+(([.][1-9]+)[ ]*)*[.:]?[ ]*)([^0-9\n\r]+)([^ ]+[ ]*)+$";
				
				headingText = headingText.toUpperCase();
				
				TextBox heading = slide.addTitle();
				heading.setText(headingText);
				
				RichTextRun rt = heading.getTextRun().getRichTextRuns()[0];
				rt.setFontSize(24);
				rt.setFontName("Calibri");
				rt.setBold(true);
				rt.setUnderlined(true);
				rt.setAlignment(TextBox.AlignCenter);
				
				titleBoxHeight = heading.getAnchor().height;
			}
			
			TextBox contentTextBox = new TextBox();
			contentTextBox.setAnchor(new java.awt.Rectangle(25, 90, 600, 400));

			//use RichTextRun to work with the text format
			RichTextRun rt = contentTextBox.getTextRun().getRichTextRuns()[0];
			rt.setFontSize(16);
			rt.setFontName("Calibri");
			rt.setFontColor(Color.black);
			rt.setAlignment(TextBox.AlignJustify);
			rt.setBullet(true);
			
			Pattern numberPrefixPattern = Pattern.compile("^([1-9]+(([.][1-9]+)[ ]*)*[.:]?[ ]*)([^0-9\n\r]+)([^ ]+[ ]*)+$", Pattern.CASE_INSENSITIVE);
			String currentSectionNumber = "";
			
			StringBuilder textBoxText = new StringBuilder();
			for(ScoredSection section : summarySections)
			{
				Section parentSection = section.getParent();
				headingText = "";
				
				if(parentSection == null)
				{
					if(section.getSummarySentenceList().size() == 0)
					{
						continue;
					}
					
					headingText = section.getHeadingString();
					
					Matcher numberPrefixMatcher = numberPrefixPattern.matcher(headingText);
					if(numberPrefixMatcher.matches())
					{
						String prefix = numberPrefixMatcher.group(1);
						headingText = headingText.substring(prefix.length()).trim();
						
						currentSectionNumber = prefix;
					}
					
					String[] headingTextWords = headingText.split(" ");
					headingText = "";
					for(String headingTextWord : headingTextWords)
					{
						char[] stringArray = headingTextWord.toLowerCase().toCharArray();
						stringArray[0] = Character.toUpperCase(stringArray[0]);
						headingText = headingText.concat(new String(stringArray) + " ");
					}
					
					textBoxText.append(headingText + "\n");
				}
				else
				{
					String sectionHeading = section.getHeadingString();
					String parentSectionHeading = parentSection.getHeadingString();
					
					String sectionPrefix = "";
					String parentSectionPrefix = "";
					
					Matcher numberPrefixMatcher = numberPrefixPattern.matcher(sectionHeading);
					if(numberPrefixMatcher.matches())
					{
						sectionPrefix = numberPrefixMatcher.group(1).trim();
					}
					
					numberPrefixMatcher = numberPrefixPattern.matcher(parentSectionHeading);
					if(numberPrefixMatcher.matches())
					{
						parentSectionPrefix = numberPrefixMatcher.group(1).trim();
					}
					
					System.out.println(parentSectionHeading + " : " + sectionHeading);
					
					if(sectionPrefix.startsWith(parentSectionPrefix))
					{
						if(!currentSectionNumber.equals(parentSectionPrefix))
						{
							if(section.getSummarySentenceList().size() == 0)
							{
								continue;
							}
							
							headingText = sectionHeading.substring(sectionPrefix.length()).trim();							
							currentSectionNumber = parentSectionPrefix;							
							
							String[] headingTextWords = headingText.split(" ");
							headingText = "";
							for(String headingTextWord : headingTextWords)
							{
								char[] stringArray = headingTextWord.toLowerCase().toCharArray();
								stringArray[0] = Character.toUpperCase(stringArray[0]);
								headingText = headingText.concat(new String(stringArray) + " ");
							}
							
							textBoxText.append(headingText + "\n");
						}
					}
				}
			}
			contentTextBox.setText(textBoxText.toString());
			slide.addShape(contentTextBox);
			
			
			Section parentSection = null;
			headingText = "";
			currentSectionNumber = "";
			boolean sectionInitialized = false;
			int prevIndex = 0;
			int beginIndex = 90;
			
//			for(ScoredSection section : summarySections)
//			{
//				System.out.print(section.getHeadingString() + " : ");
//				Section parent = section.getParent();
//				if(parent != null)
//					System.out.println(parent.getHeadingString());
//				else
//					System.out.println("null");
//			}System.exit(0);
			
			for(ScoredSection section : summarySections)
			{
				System.out.println(section.getHeadingString() + " --- ");
			}
			
			for(ScoredSection section : summarySections)
			{
				parentSection = section.getParent(); 
				if(parentSection == null)
				{
					if(section.getSummarySentenceList().size() == 0)
					{
						continue;
					}
					
					slide = Slideshow.createSlide();
					slide.setFollowMasterBackground(true);
				
					titleBoxHeight = 0;					
				
					headingText = section.getHeadingString();
					
					Matcher numberPrefixMatcher = numberPrefixPattern.matcher(headingText);
					if(numberPrefixMatcher.matches())
					{
						String prefix = numberPrefixMatcher.group(1);
						headingText = headingText.substring(prefix.length()).trim();
						
						currentSectionNumber = prefix;
					}
					
					beginIndex = 90;
					sectionInitialized = false;
				}
				else
				{
					String sectionHeading = section.getHeadingString();
					String parentSectionHeading = parentSection.getHeadingString();
					
					String sectionPrefix = "";
					String parentSectionPrefix = "";
					
					Matcher numberPrefixMatcher = numberPrefixPattern.matcher(sectionHeading);
					if(numberPrefixMatcher.matches())
					{
						sectionPrefix = numberPrefixMatcher.group(1).trim();
					}
					
					numberPrefixMatcher = numberPrefixPattern.matcher(parentSectionHeading);
					if(numberPrefixMatcher.matches())
					{
						parentSectionPrefix = numberPrefixMatcher.group(1).trim();
					}
					
					System.out.println(parentSectionHeading + " : " + sectionHeading);
					
					if(sectionPrefix.startsWith(parentSectionPrefix))
					{
						if(!currentSectionNumber.equals(parentSectionPrefix))
						{
							if(section.getSummarySentenceList().size() == 0)
							{
								continue;
							}
							
							slide = Slideshow.createSlide();
							slide.setFollowMasterBackground(true);
						
							titleBoxHeight = 0;
							
							System.out.println(parentSectionPrefix + " :- " + sectionPrefix);
							headingText = sectionHeading.substring(sectionPrefix.length()).trim();
							
							currentSectionNumber = parentSectionPrefix;
							
							sectionInitialized = false;
							
							beginIndex = 90;
						}
					}
				}
				
				List<String> summarySentenceList = section.getSummarySentenceList();
				if(summarySentenceList.size() != 0)
				{
					if(sectionInitialized == false)
					{
						if(!headingText.equals(""))
						{
	//						String sectionHeadingPattern1 = "^([1-9]+(([.][1-9]+)[ ]*)*[.:]?[ ]*)([^0-9\n\r]+)([^ ]+[ ]*)+$";
							
							headingText = headingText.toUpperCase();
							
							TextBox heading = slide.addTitle();
							heading.setText(headingText);
							
							rt = heading.getTextRun().getRichTextRuns()[0];
							rt.setFontSize(22);
							rt.setFontName("Calibri");
							rt.setBold(true);
							rt.setUnderlined(true);
							rt.setAlignment(TextBox.AlignCenter);
							
							titleBoxHeight = heading.getAnchor().height;
						}
						
						contentTextBox = new TextBox();
						contentTextBox.setAnchor(new java.awt.Rectangle(25, beginIndex, 600, 400));
		
						//use RichTextRun to work with the text format
						rt = contentTextBox.getTextRun().getRichTextRuns()[0];
						rt.setFontSize(16);
						rt.setFontName("Calibri");
						rt.setFontColor(Color.black);
						rt.setAlignment(TextBox.AlignJustify);
						rt.setBullet(true);
						
						textBoxText = new StringBuilder();
						prevIndex = 0;
						
						sectionInitialized = true;
					}
					
					rt = contentTextBox.getTextRun().getRichTextRuns()[0];
					rt.setFontSize(16);
					rt.setFontName("Calibri");
					rt.setFontColor(Color.black);
					rt.setAlignment(TextBox.AlignJustify);
					rt.setBullet(true);
					
					for(int lineNo=0; lineNo<summarySentenceList.size(); lineNo++)
					{
						textBoxText.append(summarySentenceList.get(lineNo) + "\n\n");
						int curIndex = textBoxText.length();
						
						contentTextBox.setText(textBoxText.toString());
						if(contentTextBox.getTextRun().getRichTextRuns()[0].getLength() > 800 || beginIndex > 800)
						{
							if(contentTextBox.getTextRun().getRichTextRuns()[0].getLength() > 800)
							{
								try
								{
									textBoxText.delete(prevIndex, curIndex);
									slide.addShape(contentTextBox);
								}
								catch(Exception ex)
								{
									System.out.println(ex);
								}
							}
							
							slide = Slideshow.createSlide();
							slide.setFollowMasterBackground(true);
							
							TextBox heading = slide.addTitle();
							heading.setText(headingText + " (Contd...)");
							
							rt = heading.getTextRun().getRichTextRuns()[0];
							rt.setFontSize(22);
							rt.setFontName("Calibri");
							rt.setBold(true);
							rt.setUnderlined(true);
							rt.setAlignment(TextBox.AlignCenter);
							
							contentTextBox = new TextBox();
							beginIndex = 90;
							contentTextBox.setAnchor(new java.awt.Rectangle(25, beginIndex, 600, 400));
			
							//use RichTextRun to work with the text format
							rt = contentTextBox.getTextRun().getRichTextRuns()[0];
							rt.setFontSize(16);
							rt.setFontName("Calibri");
							rt.setFontColor(Color.black);
							rt.setAlignment(TextBox.AlignJustify);
							rt.setBullet(true);
							
							textBoxText = new StringBuilder();
							textBoxText.append(summarySentenceList.get(lineNo));
							prevIndex = textBoxText.length();
						}
						else
						{
							prevIndex = (curIndex + beginIndex) * 1000 / Slideshow.getPageSize().height;
						}
					}
					
					contentTextBox.setText(textBoxText.toString());
					slide.addShape(contentTextBox);
					
					List<Image> images = section.getImages();
					if(images != null)
					{
						for(Image image : images)
						{
							String caption = image.getCaptionString();System.out.println(image.getPath());
							if(caption == null || caption.equals(""))
							{
								int idx = Slideshow.addPicture(new File(image.getPath()), Picture.JPEG);
								Picture picture = new Picture(idx);
								Rectangle pictCoordinates = picture.getAnchor();

								ClusterInfo info = image.getImageClusterInformation();								
								int pictureWidth = (int)(info.getX2() - info.getX1());
								int pictureHeight = (int)(info.getY2() - info.getY1());
								
//								double top = (double)prevIndex / 800;
//								top *= 540;
								
								//int top = titleBoxHeight + (Slideshow.getPageSize().height - titleBoxHeight) * prevIndex / 1000;
								
								int top = prevIndex;
								
								int width = pictureWidth * 200 / pictureHeight;								
								int left = (Slideshow.getPageSize().width - width)/2;

								if(top + 200 > Slideshow.getPageSize().height)
								{
									slide = Slideshow.createSlide();
									slide.setFollowMasterBackground(true);
									
									TextBox heading = slide.addTitle();
									heading.setText(headingText + " (Contd...)");
																		
									rt = heading.getTextRun().getRichTextRuns()[0];
									rt.setFontSize(22);
									rt.setFontName("Calibri");
									rt.setBold(true);
									rt.setUnderlined(true);
									rt.setAlignment(TextBox.AlignCenter);
									
									titleBoxHeight = heading.getAnchor().height;
									
									prevIndex = 100;
									top = prevIndex;
//									top = titleBoxHeight + (Slideshow.getPageSize().height - titleBoxHeight) * prevIndex / 1000;
									
									textBoxText = new StringBuilder();
								}

								//prevIndex = prevIndex + pictureHeight * 1000 / Slideshow.getPageSize().height;
								prevIndex = top + 210;
								
								picture.setAnchor(new java.awt.Rectangle(left, top, width, 200));
								slide.addShape(picture);
							}
							else
							{
								int idx = Slideshow.addPicture(new File(image.getPath()), Picture.JPEG);
								Picture picture = new Picture(idx);
								Rectangle pictCoordinates = picture.getAnchor();
								
								ClusterInfo info = image.getImageClusterInformation();								
								int pictureWidth = (int)(info.getX2() - info.getX1());
								int pictureHeight = (int)(info.getY2() - info.getY1());
								
//								int top = titleBoxHeight + (Slideshow.getPageSize().height - titleBoxHeight) * prevIndex / 1000;
								
								int top = prevIndex;
								
								int width = pictureWidth * 200 / pictureHeight;						
								int left = (Slideshow.getPageSize().width - width)/2;

								if(top + 200 > Slideshow.getPageSize().height)
								{
									slide = Slideshow.createSlide();
									slide.setFollowMasterBackground(true);
									
									TextBox heading = slide.addTitle();
									heading.setText(headingText + " (Contd...)");
																		
									rt = heading.getTextRun().getRichTextRuns()[0];
									rt.setFontSize(22);
									rt.setFontName("Calibri");
									rt.setBold(true);
									rt.setUnderlined(true);
									rt.setAlignment(TextBox.AlignCenter);
									
									titleBoxHeight = heading.getAnchor().height;
									
									prevIndex = 100;
									top = prevIndex;
//									top = titleBoxHeight + (Slideshow.getPageSize().height - titleBoxHeight) * prevIndex / 1000;
									
									textBoxText = new StringBuilder();
								}

								//prevIndex = prevIndex + pictureHeight * 1000 / Slideshow.getPageSize().height;
								
								picture.setAnchor(new java.awt.Rectangle(left, top, width, 200));
								slide.addShape(picture);
								
								contentTextBox = new TextBox();
								contentTextBox.setAnchor(new java.awt.Rectangle(25, top + 205, 600, 400));
				
								rt = contentTextBox.getTextRun().getRichTextRuns()[0];
								rt.setFontSize(16);
								rt.setFontName("Calibri");
								rt.setFontColor(Color.black);
								rt.setAlignment(TextBox.AlignCenter);
								
								contentTextBox.setText(caption);
								slide.addShape(contentTextBox);
								
								prevIndex = top + 220;
							}
						}
						
						beginIndex = (prevIndex + 10) * 1000 / Slideshow.getPageSize().height;
						System.out.println("beginIndex --- " + beginIndex);
					}
					else
					{
						System.out.println("...null");
					}
				}
			}
			
			if(miscellaneousImages.size() != 0)
			{
				slide = Slideshow.createSlide();
				slide.setFollowMasterBackground(true);
				
				TextBox heading = slide.addTitle();
				headingText = "Images Not In Summary";
				heading.setText(headingText);
													
				rt = heading.getTextRun().getRichTextRuns()[0];
				rt.setFontSize(28);
				rt.setFontName("Calibri");
				rt.setBold(true);
				rt.setUnderlined(true);
				rt.setAlignment(TextBox.AlignCenter);
				
				titleBoxHeight = heading.getAnchor().height;
				
				prevIndex = 100;				
				for(Image image : miscellaneousImages)
				{
					int idx = Slideshow.addPicture(new File(image.getPath()), Picture.JPEG);
					Picture picture = new Picture(idx);
					Rectangle pictCoordinates = picture.getAnchor();

					ClusterInfo info = image.getImageClusterInformation();								
					int pictureWidth = (int)(info.getX2() - info.getX1());
					int pictureHeight = (int)(info.getY2() - info.getY1());
					
//					double top = (double)prevIndex / 800;
//					top *= 540;
					
					//int top = titleBoxHeight + (Slideshow.getPageSize().height - titleBoxHeight) * prevIndex / 1000;
					
					int top = prevIndex;
					
					int width = pictureWidth * 150 / pictureHeight;								
					int left = (Slideshow.getPageSize().width - width)/2;

					if(top + 150 > Slideshow.getPageSize().height)
					{
						slide = Slideshow.createSlide();
						slide.setFollowMasterBackground(true);
						
						heading = slide.addTitle();
						heading.setText(headingText + " (Contd...)");
															
						rt = heading.getTextRun().getRichTextRuns()[0];
						rt.setFontSize(22);
						rt.setFontName("Calibri");
						rt.setBold(true);
						rt.setUnderlined(true);
						rt.setAlignment(TextBox.AlignCenter);
						
						titleBoxHeight = heading.getAnchor().height;
						
						prevIndex = 100;
						top = prevIndex;
//						top = titleBoxHeight + (Slideshow.getPageSize().height - titleBoxHeight) * prevIndex / 1000;
						
						textBoxText = new StringBuilder();
					}

					//prevIndex = prevIndex + pictureHeight * 1000 / Slideshow.getPageSize().height;
					prevIndex = top + 160;
					
					picture.setAnchor(new java.awt.Rectangle(left, top, width, 150));
					slide.addShape(picture);
				}
			}
			else
			{
				System.out.println("zero");
			}
			
			System.out.println(title);
			Mendeley_Reader reader = new Mendeley_Reader(title);
			
			if(reader.status == true)
			{
				slide = Slideshow.createSlide();
				slide.setFollowMasterBackground(true);
				
				TextBox heading = slide.addTitle();
				heading.setText("Summary Information");
													
				rt = heading.getTextRun().getRichTextRuns()[0];
				rt.setFontSize(22);
				rt.setFontName("Calibri");
				rt.setBold(true);
				rt.setUnderlined(true);
				rt.setAlignment(TextBox.AlignCenter);
				
				titleBoxHeight = heading.getAnchor().height;
				
				prevIndex = 100;				
				
				contentTextBox = new TextBox();
				contentTextBox.setAnchor(new java.awt.Rectangle(25, 90, 600, 400));

				rt = contentTextBox.getTextRun().getRichTextRuns()[0];
				rt.setFontSize(16);
				rt.setFontName("Calibri");
				rt.setFontColor(Color.black);
				rt.setAlignment(TextBox.AlignJustify);
				rt.setBullet(true);
				
				StringBuffer readerText = new StringBuffer();
				readerText.append("Paper Title : " + reader.title + "\n");
				readerText.append("Paper Authors : ");
				for(String author : reader.authors)
				{
					readerText.append(author + ", ");
				}
				readerText.delete(readerText.toString().length()-2, readerText.toString().length()-1);
				readerText.append("Paper Related Articles : \n");
				
				int numArticles = 0;
				for(String article : reader.related_titles)
				{
					readerText.append(article + "\n");
					numArticles++;
					if(numArticles == 5)
					{
						break;
					}
				}				
				
				contentTextBox.setText(readerText.toString());
				slide.addShape(contentTextBox);
			}
			else
			{
				System.out.println(reader.status);
			}
			
			
			
			
			HeadersFooters headersAndFooters = Slideshow.getSlideHeadersFooters();
			headersAndFooters.setFooterVisible(true);
			headersAndFooters.setSlideNumberVisible(true);
			headersAndFooters.setDateTimeVisible(true);
			
			if(cliOptions.containsKey("footer"))
			{
				headersAndFooters.setFootersText(cliOptions.get("footer"));
			}
			if(cliOptions.containsKey("header"))
			{
				headersAndFooters.setHeaderVisible(true);
				headersAndFooters.setFootersText(cliOptions.get("header"));
			}			
			
		    FileOutputStream fos = null;
		    if(cliOptions.containsKey("outputfilepath"))
		    {
		    	fos = new FileOutputStream(cliOptions.get("outputfilepath"));
		    }
		    else
		    {
		    	fos = new FileOutputStream("LexRankOutput.ppt");
		    }
		    
		    Slideshow.write(fos);
		    fos.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			ex.printStackTrace();
		}
	}
	
	public Summarizer(String stopWordFile)
	{
		Tokenizer.setStopWords(stopWordFile);
		
		document = getSections();
		sectionHeadings = getSectionHeadings();
		
		List<String> sentenceList = Tokenizer.getSentencesFromPDF(document);
		List<List<String>> tokenizedDocument = Tokenizer.tokenizeWithSpaces(sentenceList);
		
		termFrequencies = new HashMap<String, Integer>();
		
		Iterator<List<String>> documentIterator = tokenizedDocument.iterator();
		while(documentIterator.hasNext())
		{
			List<String> sentence = documentIterator.next();
			for(String token : sentence)
			{
				if(termFrequencies.containsKey(token))
				{
					termFrequencies.put(token, termFrequencies.get(token)+1);
				}
				else
				{
					termFrequencies.put(token, 1);
				}
			}
		}
		
		if(getBestInDocument)
		{
			Queue<SentenceScore> pQueue = new PriorityQueue<SentenceScore>(tokenizedDocument.size(), new SentenceScoreComparator());
			
			Iterator<List<String>> tokenizedDocumentIterator = tokenizedDocument.iterator();
			Iterator<String> sentenceListIterator = sentenceList.iterator();
			while(tokenizedDocumentIterator.hasNext())
			{
				List<String> tokenArray = tokenizedDocumentIterator.next();
				StringBuilder sentence = new StringBuilder();
				double score = 0;
				for(String token : tokenArray)
				{
					sentence.append(token + " ");
					score += termFrequencies.get(token);
				}
				
				SentenceScore sentenceScore = new SentenceScore(sentence.substring(0,sentence.length()-1), score/tokenArray.size());
				sentenceScore.setOriginalSentence(sentenceListIterator.next());
				pQueue.add(sentenceScore);
			}
		
			int count = 0;
			SentenceScore s = null;
			String[] outputSentences = new String[25];
			while((s = pQueue.poll()) != null && count < outputSentences.length)
			{
				String candidateSentence = s.getOriginalSentence();
				if(candidateSentence.length() <= 25)
				{
					continue;
				}
				
				outputSentences[count++] = candidateSentence;
			}
			
			Arrays.sort(outputSentences, new SectionComparator());
			
//			count = 0;
//			System.out.println("<Presentation>");
//			for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
//			{
//				String section = document.get(sectionNumber);
//				System.out.println("\t<Slide>");
//				int lineNo = 1;
//				while(count < outputSentences.length && section.contains(outputSentences[count]))
//				{
//					System.out.println("\t\t" + lineNo + ". " + outputSentences[count++]);
//					lineNo++;
//				}
//				System.out.println("\t</Slide>");
//			}
//			System.out.println("<Presentation>");
			
			try
			{
				SlideShow Slideshow = new SlideShow();
				count = 0;
				for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
				{
					String section = document.get(sectionNumber);
					
					Slide slide = Slideshow.createSlide();
					
					TextBox heading = slide.addTitle();
					heading.setText(sectionHeadings.get(sectionNumber));
					
					RichTextRun rt = heading.getTextRun().getRichTextRunAt(0);
					rt.setFontSize(28);
					rt.setFontName("Calibri");
					rt.setBold(true);
					rt.setUnderlined(true);
					rt.setAlignment(TextBox.AlignCenter);
					
					TextBox textBox = new TextBox();
					textBox.setAnchor(new java.awt.Rectangle(25, 90, 600, 400));

					//use RichTextRun to work with the text format
					rt = textBox.getTextRun().getRichTextRuns()[0];
					rt.setFontSize(16);
					rt.setFontName("Calibri");
					rt.setFontColor(Color.black);
					rt.setAlignment(TextBox.AlignJustify);
					
					StringBuilder textBoxText = new StringBuilder();
					int lineNo = 1;
					while(count < outputSentences.length && section.contains(outputSentences[count]))
					{
						textBoxText.append(lineNo + ". " + outputSentences[count++] + "\n\n");
						lineNo++;
					}
					
					textBox.setText(textBoxText.toString());					
					slide.addShape(textBox);
				}
				
			    FileOutputStream fos = new FileOutputStream("BestInDocument.ppt");
			    Slideshow.write(fos);
			    fos.close();
			}
			catch(Exception ex)
			{
				System.out.println(ex);
			}
		}
		else
		{
			Queue<SentenceScore>[] pQueue = new PriorityQueue[document.size()];
			for(int queueNo=0; queueNo<document.size(); queueNo++)
			{
				pQueue[queueNo] = new PriorityQueue<SentenceScore>(tokenizedDocument.size(), new SentenceScoreComparator());
			}
			
			Iterator<List<String>> tokenizedDocumentIterator = tokenizedDocument.iterator();
			Iterator<String> sentenceListIterator = sentenceList.iterator();
			while(tokenizedDocumentIterator.hasNext())
			{
				List<String> tokenArray = tokenizedDocumentIterator.next();
				StringBuilder sentence = new StringBuilder();
				double score = 0;
				for(String token : tokenArray)
				{
					sentence.append(token + " ");
					score += termFrequencies.get(token);
				}
				
				SentenceScore sentenceScore = new SentenceScore(sentence.substring(0,sentence.length()-1), score/tokenArray.size());
				String originalSentence = sentenceListIterator.next();
				sentenceScore.setOriginalSentence(originalSentence);
				for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
				{
					if(document.get(sectionNumber).contains(originalSentence))
					{
						pQueue[sectionNumber].add(sentenceScore);
					}
				}
			}
			
			int sentencesPerSection = 3;
			String[][] output = new String[document.size()][sentencesPerSection];
			
			SentenceScore s = null;
			for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
			{
				int iterNo = 0;
				while(iterNo < sentencesPerSection && (s = pQueue[sectionNumber].poll()) != null)
				{
					String candidateSentence = s.getOriginalSentence();
					if(candidateSentence.length() <= 25)
					{
						continue;
					}
					
					output[sectionNumber][iterNo] = candidateSentence;
					iterNo++;
				}
			}
			
//			System.out.println("<Presentation>");
//			for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
//			{
//				String section = document.get(sectionNumber);
//				System.out.println("\t<Slide>");
//				for(int lineNo=1; lineNo<=sentencesPerSection; lineNo++)
//				{
//					System.out.println("\t\t" + lineNo + ". " + output[sectionNumber][lineNo-1]);
//				}
//				System.out.println("\t</Slide>");
//			}
//			System.out.println("<Presentation>");
			
			try
			{
				SlideShow Slideshow = new SlideShow();
				for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
				{
					Slide slide = Slideshow.createSlide();
					
					TextBox heading = slide.addTitle();
					heading.setText(sectionHeadings.get(sectionNumber));
					
					RichTextRun rt = heading.getTextRun().getRichTextRunAt(0);
					rt.setFontSize(28);
					rt.setFontName("Calibri");
					rt.setBold(true);
					rt.setUnderlined(true);
					rt.setAlignment(TextBox.AlignCenter);
					
					TextBox textBox = new TextBox();
					textBox.setAnchor(new java.awt.Rectangle(25, 100, 600, 400));

					//use RichTextRun to work with the text format
					rt = textBox.getTextRun().getRichTextRuns()[0];
					rt.setFontSize(16);
					rt.setFontName("Calibri");
					rt.setFontColor(Color.black);
					rt.setAlignment(TextBox.AlignJustify);
					
					StringBuilder textBoxText = new StringBuilder();
					for(int lineNo=1; lineNo<=sentencesPerSection; lineNo++)
					{
						textBoxText.append(lineNo + ". " + output[sectionNumber][lineNo-1] + "\n\n");
					}
					
					textBox.setText(textBoxText.toString());					
					slide.addShape(textBox);
				}
				
			    FileOutputStream fos = new FileOutputStream("MinimumPerSection.ppt");
			    Slideshow.write(fos);
			    fos.close();
			}
			catch(Exception ex)
			{
				System.out.println(ex);
			}
		}
	}
	
	private void setSummarySentences(List<String> sentenceList, double[] sentenceScores, Map<String, ScoredSection> sentenceSectionMap, int numSentencesInSummary)
	{
		int currNumSentencesInSummary = 0;		

		for(int i=0; i<sentenceScores.length-1; i++)
		{
			int pos = i;			
			for(int j=i+1; j<sentenceScores.length; j++)
			{
				if(sentenceScores[pos] < sentenceScores[j])
				{
					pos = j;
				}
			}
			
			sentenceScores[i] = sentenceScores[i] + sentenceScores[pos];
			sentenceScores[pos] = sentenceScores[i] - sentenceScores[pos];
			sentenceScores[i] = sentenceScores[i] - sentenceScores[pos];
			
			String sentence1 = sentenceList.get(i);
			String sentence2 = sentenceList.get(pos);
			sentenceList.set(i, sentence2);
			sentenceList.set(pos, sentence1);
			
			if(currNumSentencesInSummary < numSentencesInSummary)
			{
				ScoredSection section = sentenceSectionMap.get(sentence2);
				section.addSentenceToSummarySentenceList(sentence2);
				currNumSentencesInSummary++;
			}
		}
	}
	
	private List<String> getSections()
	{
		List<String> sections = new ArrayList<String>();
		StringBuilder sectionText = null;
		
		sectionText = new StringBuilder();
		sectionText.append("This paper introduces a system designed for ");
		sectionText.append("automatically generating personalized annotation ");
		sectionText.append("tags to label Twitter user’s interests and ");
		sectionText.append("concerns. We applied TFIDF ranking and ");
		sectionText.append("TextRank to extract keywords from Twitter ");
		sectionText.append("messages to tag the user. The user tagging precision ");
		sectionText.append("we obtained is comparable to the precision ");
		sectionText.append("of keyword extraction from web pages for ");
		sectionText.append("content-targeted advertising.");
					
		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("Twitter is a communication platform which combines ");
		sectionText.append("SMS, instant messages and social networks. It ");
		sectionText.append("enables users to share information with their friends ");
		sectionText.append("or the public by updating their Twitter messages. ");
		sectionText.append("A large majority of the Twitter users are individual ");
		sectionText.append("subscribers, who use Twitter to share information ");
		sectionText.append("on “what am I doing” or “what’s happening ");
		sectionText.append("right now”. Most of them update their Twitter messages ");
		sectionText.append("very frequently, in which case the Twitter messages ");
		sectionText.append("compose a detailed log of the user’s everyday ");
		sectionText.append("life. These Twitter messages contain rich information ");
		sectionText.append("about an individual user, including what s/he is ");
		sectionText.append("interested in and concerned about. Identifying an ");
		sectionText.append("individual user’s interests and concerns can help potential ");
		sectionText.append("commercial applications. For instance, this ");
		sectionText.append("information can be employed to produce “following” ");
		sectionText.append("suggestions, either a person who shares similar ");
		sectionText.append("interests (for expanding their social network) or ");
		sectionText.append("a company providing products or services the user is ");
		sectionText.append("interested in (for personalized advertisement). ");
		sectionText.append("In this work, we focus on automatically generating ");
		sectionText.append("personalized annotation tags to label Twitter ");
		sectionText.append("user’s interests and concerns. We formulate this ");
		sectionText.append("problem as a keyword extraction task, by selecting ");
		sectionText.append("words from each individual user’s Twitter messages ");
		sectionText.append("as his/her tags. Due to the lack of human generated ");
		sectionText.append("annotations, we employ an unsupervised strategy. ");
		sectionText.append("Specifically, we apply TFIDF ranking and TextRank ");
		sectionText.append("(Mihalcea and Tarau, 2004) keyword extraction on ");
		sectionText.append("Twitter messages after a series of text preprocessing ");
		sectionText.append("steps. Experiments on randomly selected users ");
		sectionText.append("showed good results with TextRank, but high variability ");
		sectionText.append("among users.");
		
		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("Research work related to Twitter message analysis ");
		sectionText.append("includes a user sentiment study (Jansen et al., 2009) ");
		sectionText.append("and information retrieval indexing. To our knowledge, ");
		sectionText.append("no previously published research has yet addressed ");
		sectionText.append("problems on tagging user’s personal interests ");
		sectionText.append("from Twitter messages via keyword extraction, ");
		sectionText.append("though several studies have looked at keyword extraction ");
		sectionText.append("using other genres. ");
		sectionText.append("For supervised keyword extraction, (Turney, ");
		sectionText.append("2000; Turney, 2003; Hulth, 2003; Yih et al., 2006; ");
		sectionText.append("Liu et al., 2008) employed TFIDF or its variants ");
		sectionText.append("with Part-of-Speech (POS), capitalization, phrase ");
		sectionText.append("and sentence length, etc., as features to train keyword ");
		sectionText.append("extraction models, and discriminative training ");
		sectionText.append("is usually adopted. Yih et al. (2006) use logistic ");
		sectionText.append("regression to extract keywords from web pages ");
		sectionText.append("for content-targeted advertising, which has the most ");
		sectionText.append("similar application to our work. However, due to the ");
		sectionText.append("lack of human annotation on Twitter messages, we ");
		sectionText.append("have to adopt an unsupervised strategy. ");
		sectionText.append("For unsupervised keyword extraction, TFIDF ");
		sectionText.append("ranking is a popular method, and its effectiveness ");
		sectionText.append("has been shown in (Hulth, 2003; Yih et al., ");
		sectionText.append("2006). TextRank and its variants (Mihalcea and Tarau, ");
		sectionText.append("2004; Wan et al., 2007; Liu et al., 2009) are ");
		sectionText.append("graph-based text ranking models, which are derived ");
		sectionText.append("from Google’s PageRank algorithm (Brin and Page, ");
		sectionText.append("1998). It outperforms TFIDF ranking on traditional ");
		sectionText.append("keyword extraction tasks. However, previous work ");
		sectionText.append("on both TFIDF ranking and TextRank has been done ");
		sectionText.append("mainly on academic papers, spoken documents or ");
		sectionText.append("web pages, whose language style is more formal (or, ");
		sectionText.append("less “conversational”) than that of Twitter messages. ");
		sectionText.append("Twitter messages contain large amounts of “noise” ");
		sectionText.append("like emoticons, internet slang words, abbreviations, ");
		sectionText.append("and misspelled words. In addition, Twitter messages ");
		sectionText.append("are a casual log of a user’s everyday life, which often ");
		sectionText.append("lacks of a coherent topic sequence compared to academic ");
		sectionText.append("papers and most spoken documents. Hence, ");
		sectionText.append("it remains to see whether TFIDF ranking and TextRank ");
		sectionText.append("are effective for identifying user’s interests ");
		sectionText.append("from Twitter messages.");
		
		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("Figure 1 shows the framework of our system for ");
		sectionText.append("tagging Twitter user’s interests. A preprocessing ");
		sectionText.append("pipeline is designed to deal with various types of ");
		sectionText.append("“noise” in Twitter messages and produce candidate ");
		sectionText.append("words for user tags. Then the TFIDF ranking or TextRank ");
		sectionText.append("algorithm is applied to select user tags from ");
		sectionText.append("the candidate words.");
		
		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("In addition to messages describing “What am I doing” ");
		sectionText.append("or “what’s happening right now”, Twitter users ");
		sectionText.append("also write replying messages to comment on other ");
		sectionText.append("users’ messages. This kind of message generally ");
		sectionText.append("contains more information about the users they reply ");
		sectionText.append("to than about themselves, and therefore they are ");
		sectionText.append("removed in the preprocessing pipeline. ");
		sectionText.append("Emoticons frequently appear in Twitter messages. ");
		sectionText.append("Although some of them help express user’s sentiment ");
		sectionText.append("on certain topics, they are not directly helpful ");
		sectionText.append("for keyword analysis and may interfere with POS ");
		sectionText.append("tagging in the preprocessing pipeline. Therefore, we ");
		sectionText.append("designed a set of regular expressions to detect and ");
		sectionText.append("remove them. ");
		sectionText.append("Internet slang words and abbreviations are widely ");
		sectionText.append("used in Twitter messages. Most of them are out-ofvocabulary ");
		sectionText.append("words in the POS tagging model used ");
		sectionText.append("in the next step, and thus will deteriorate the POS ");
		sectionText.append("tagging accuracy. Hence, we build a lookup table ");
		sectionText.append("based on the list of abbreviations in the NoSlang online ");
		sectionText.append("dictionary, which we divide by hand into three ");
		sectionText.append("sets for different processing. The first set includes ");
		sectionText.append("422 content words and phrases, such as “bff” (best ");
		sectionText.append("friend forever) and “fone” (phone), with valid candidate ");
		sectionText.append("words for user tags. The second set includes ");
		sectionText.append("67 abbreviations of function words that usually form ");
		sectionText.append("grammatical parts in a sentence, such as “im” (i’m), ");
		sectionText.append("“abt” (about). Simply removing them will affect the ");
		sectionText.append("POS tagging. Thus, the abbreviations in both these ");
		sectionText.append("sets are replaced with the corresponding complete ");
		sectionText.append("words or phrases. The third set includes 4576 phrase ");
		sectionText.append("abbreviations that are usually separable parts of a ");
		sectionText.append("sentence that do not directly indicate discussion topics, ");
		sectionText.append("such as “lol” (laugh out loud), “clm” (cool like ");
		sectionText.append("me), which are removed in this step. ");
		sectionText.append("We apply the Stanford POS tagger (Toutanova ");
		sectionText.append("and Manning, 2000) on Twitter messages, and only ");
		sectionText.append("select nouns and adjectives as valid candidates for ");
		sectionText.append("user tags. At the end of the preprocessing pipeline, ");
		sectionText.append("the candidate words are processed with the rulebased ");
		sectionText.append("Porter stemmer and stopwords are filtered using ");
		sectionText.append("a publicly available list.");
		
		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("We employed the Twitter API to download Twitter ");
		sectionText.append("messages. A unigram English language model was ");
		sectionText.append("used to filter out non-English users. We obtained ");
		sectionText.append("messages from 11,376 Twitter users, each of them ");
		sectionText.append("had 180 to 200 messages. The word IDF for TFIDF ");
		sectionText.append("ranking was computed over these users. ");
		sectionText.append("We adopted an evaluation measure similar to the ");
		sectionText.append("one proposed in (Yih et al., 2006) for identifying ");
		sectionText.append("advertising keywords on web pages, which emphasizes ");
		sectionText.append("precision. We randomly selected 156 Twitter ");
		sectionText.append("users to evaluate the top-N precision of TFIDF ");
		sectionText.append("ranking and TextRank. After we obtained the top- ");
		sectionText.append("N outputs from the system, three human evaluators ");
		sectionText.append("were asked to judge whether the output tags from the ");
		sectionText.append("two systems (unidentified) reflected the corresponding ");
		sectionText.append("Twitter user’s interests or concerns according to ");
		sectionText.append("the full set of his/her messages.4 We adopted a conservative ");
		sectionText.append("standard in the evaluation: when a person’s ");
		sectionText.append("name is extracted as a user tag, which is frequent ");
		sectionText.append("among Twitter users, we judge it as a correct tag ");
		sectionText.append("only when it is a name of a famous person (pop star, ");
		sectionText.append("football player, etc). The percentage of the correct ");
		sectionText.append("tags among the top-N selected tags corresponds to ");
		sectionText.append("the top-N precision of the system.");
		
		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("Table 1 gives the top-N precision for TFIDF and ");
		sectionText.append("TextRank for different values of N, showing that ");
		sectionText.append("TextRank leads to higher precision for small N. Although ");
		sectionText.append("Twitter messages are much “noisier” than ");
		sectionText.append("regular web pages, the top-N precision we obtained ");
		sectionText.append("for Twitter user tagging is comparable to the web ");
		sectionText.append("page advertising keyword extraction result reported ");
		sectionText.append("in (Yih et al., 2006). ");
		sectionText.append("Figure 2 shows an example of the candidate word ");
		sectionText.append("ranking result of a Twitter user by TextRank (the ");
		sectionText.append("font size is set to be proportional to each word’s ");
		sectionText.append("TextRank value). By examining the Twitter messages, ");
		sectionText.append("we found that this user is an information technology ");
		sectionText.append("“geek”, who is very interested in writing Apple’s ");
		sectionText.append("iPhone applications, and also a user of Google ");
		sectionText.append("Wave. In this work, we use only isolated words as ");
		sectionText.append("user tags, however, “google”, “wave”, and “palo”, ");
		sectionText.append("“alto” extracted in this example indicate that phrase ");
		sectionText.append("level tagging can bring us more information about ");
		sectionText.append("the user, which is typical of many users. ");
		sectionText.append("Although most Twitter users express their interests ");
		sectionText.append("to some extent in their messages, there are some ");
		sectionText.append("users whose message content is not rich enough to ");
		sectionText.append("extract reliable information. We investigated two ");
		sectionText.append("measures for identifying such users: standard deviation ");
		sectionText.append("of the top-10 TextRank values and the user’s ");
		sectionText.append("message text entropy. Table 2 shows a comparison ");
		sectionText.append("of tagging precision where the users are divided ");
		sectionText.append("into two groups with a threshold on each of the two ");
		sectionText.append("measures. It is shown that users with larger TextRank ");
		sectionText.append("value standard deviation or message text entropy ");
		sectionText.append("tend to have higher tagging precision, and the ");
		sectionText.append("message text entropy has better correlation with the ");
		sectionText.append("top-10 tagging precision than TextRank value standard ");
		sectionText.append("deviation.");

		sections.add(sectionText.toString());
		
		sectionText = new StringBuilder();
		sectionText.append("In this paper, we designed a system to automatically ");
		sectionText.append("extract keywords from Twitter messages to ");
		sectionText.append("tag user interests and concerns. We evaluated two ");
		sectionText.append("tagging algorithms, finding that TextRank outperformed ");
		sectionText.append("TFIDF ranking, but both gave a tagging precision ");
		sectionText.append("that was comparable to that reported for web ");
		sectionText.append("page advertizing keyword extraction. We noticed ");
		sectionText.append("substantial variation in performance across users, ");
		sectionText.append("with low entropy indicative of users with fewer keywords, ");
		sectionText.append("and a need for extracting key phrases (in addition ");
		sectionText.append("to words). Other follow-on work might consider ");
		sectionText.append("temporal characteristics of messages in terms ");
		sectionText.append("of the amount of data needed for reliable tags vs. ");
		sectionText.append("their time-varying nature, as well as sentiment associated ");
		sectionText.append("with the identified tags.");
		
		sections.add(sectionText.toString());
		
		return sections;
	}
	
	private List<String> getSectionHeadings()
	{
		List<String> sectionHeadings = new ArrayList<String>();
		
		sectionHeadings.add("Abstract");
		sectionHeadings.add("Introduction");
		sectionHeadings.add("Related Work");
		sectionHeadings.add("System Architecture");
		sectionHeadings.add("Preprocessing");
		sectionHeadings.add("Experimental Setup");
		sectionHeadings.add("Experimental Results");
		sectionHeadings.add("Summary");
		
		return sectionHeadings;
	}
	
	public static void main(String[] args)
	{
		//new Summarizer(args[0]);
		
		// preprocess pdf/text tfidf/wordnet stopwords filename outputformat outputPath
		
		for(int i=0; i<args.length; i++)
		{
			if(args[i].equals("-p")) //whether to preprocess
			{
				cliOptions.put("preprocess", "true");
			}
			else if(args[i].equals("-i")) //whether to take input from text file or pdf
			{
				cliOptions.put("inputtype", args[++i]);
			}
			else if(args[i].equals("-S")) //similarity method to use
			{
				cliOptions.put("similaritymethod", args[++i]);
			}
			else if(args[i].equals("-w")) //path to stopwords.txt
			{
				cliOptions.put("stopwordfile", args[++i]);
			}
			else if(args[i].equals("-f")) //input file path
			{
				cliOptions.put("inputfilepath", args[++i]);
			}
			else if(args[i].equals("-o")) //output format
			{
				cliOptions.put("outputformat", args[++i]);
			}
			else if(args[i].equals("-O")) // output file path
			{
				cliOptions.put("outputfilepath", args[++i]);
			}
			else if(args[i].equals("-H")) // output file path
			{
				cliOptions.put("header", args[++i]);
			}
			else if(args[i].equals("-F")) // output file path
			{
				cliOptions.put("footer", args[++i]);
			}
			else if(args[i].equals("-a")) // output file path
			{
				cliOptions.put("author", args[++i]);
			}
			else if(args[i].equals("-t")) // output file path
			{
				cliOptions.put("title", args[++i]);
			}
			else if(args[i].equals("-n"))
			{
				cliOptions.put("numsentences", args[++i]);
			}
		}
		
		new Summarizer();
		
//		Tokenizer.setStopWords(args[0]);
//		Preprocess.getWordsFromPDF();
//		Preprocess.serializeMaps();
	}
	
	private class SentenceScoreComparator implements Comparator<SentenceScore>
	{
		//sort in descending order
		public int compare(SentenceScore s1, SentenceScore s2)
		{
			if(s2.getScore() > s1.getScore())
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		
		public boolean equal(Object o)
		{
			return false;
		}
	}
	
	private class SectionComparator implements Comparator<String>
	{
		public int compare(String s1, String s2)
		{
			int indexS1 = 0;
			int indexS2 = 0;
			
			for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
			{
				String section = document.get(sectionNumber);
				if(section.contains(s1))
				{
					indexS1 = sectionNumber;
					break;
				}
			}
			
			for(int sectionNumber=0; sectionNumber<document.size(); sectionNumber++)
			{
				String section = document.get(sectionNumber);
				if(section.contains(s2))
				{
					indexS2 = sectionNumber;
					break;
				}
			}
			
			return indexS1 - indexS2;
		}
		
		public boolean equal(Object o)
		{
			return false;
		}
	}
}

class SentenceScore
{
	private String tokenizedSentence;
	private String originalSentence;
	private double score;
	
	public SentenceScore(String tokenizedSentence, double score)
	{
		this.tokenizedSentence = tokenizedSentence;
		this.score = score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
	
	public double getScore()
	{
		return score;
	}
	
	public String getTokenizedSentence()
	{
		return tokenizedSentence;
	}
	
	public void setOriginalSentence(String originalSentence)
	{
		this.originalSentence = originalSentence;
	}
	public String getOriginalSentence()
	{
		return originalSentence;
	}
}