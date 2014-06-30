import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.*;

import at.ac.tuwien.dbai.pdfwrap.model.document.GenericSegment;
import at.ac.tuwien.dbai.pdfwrap.model.document.LineFragment;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextBlock;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextFragment;
import at.ac.tuwien.dbai.pdfwrap.model.document.TextLine;
import ippt.pdf.Image;
import ippt.pdf.Section;
import ippt.pdf.extractpdf;

public class Preprocess
{
	private static String serializationPath = "E:\\8thSemProject\\textToPpt\\Corpus\\";
	
	public static List<List<IndexWord>> annotatePOSTags(List<List<String>> sentences)
	{
		List<List<Token>> tokens = new ArrayList<List<Token>>();
		Iterator<List<String>> sentenceIterator = sentences.iterator();
		while(sentenceIterator.hasNext())
		{
			List<Token> innerTokenList = new ArrayList<Token>();
			
			List<String> sentence = sentenceIterator.next();
			Iterator<String> wordIterator = sentence.iterator();
			while(wordIterator.hasNext())
			{
				Token token = new Token(wordIterator.next());
				innerTokenList.add(token);
			}
			tokens.add(innerTokenList);
		}
		
		MaxentTagger tagger = null;
		try
		{
			tagger = new MaxentTagger("models/wsj-0-18-left3words.tagger");
			List<ArrayList<TaggedWord>> taggedSentences = tagger.process(tokens);
			
			TTags tags = tagger.getTags();
			
			List<List<IndexWord>> indexWordSentencesList = new ArrayList<List<IndexWord>>();
			for(int sentenceNo=0; sentenceNo<taggedSentences.size(); sentenceNo++)
			{
				ArrayList<TaggedWord> taggedSentence = taggedSentences.get(sentenceNo);
				List<IndexWord> indexWordSentence = new ArrayList<IndexWord>();
				for(int wordNo=0; wordNo<taggedSentence.size(); wordNo++)
				{
					TaggedWord taggedWord = taggedSentence.get(wordNo);					
					IndexWord indexWord = WordNetDictionary.getIndexWord(taggedWord);
					
					indexWordSentence.add(indexWord);
				}
				
				indexWordSentencesList.add(indexWordSentence);
			}
			
			return indexWordSentencesList;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void getWordsFromPDFs(String path)
	{
		getWordsFromPDFs(path, false);
	}
	
	public static void getWordsFromPDFs(String path, boolean saveCounts)
	{
		List<List<List<GenericSegment>>> pdfDocument = null;
		
//		String path = "E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\pdf\\anthology-PDF";
		
		TfIdf.collectionTermFrequency = new HashMap<String, Integer>();
		TfIdf.documentFrequency = new HashMap<String, Integer>();

		File folder = new File(path);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles())); 
		for(int fileNo=0; fileNo<files.size(); fileNo++)
		{
			File curFile = files.get(fileNo);
			if(curFile.isDirectory())
			{
				File[] additionalFiles = curFile.listFiles();
				for(File file : additionalFiles)
				{
					files.add(file);
				}
				
				continue;
			}
			
			List<Section> sections = getWordsFromPDF(curFile.toString());

			if(saveCounts)
			{
				Map<String, Boolean> wordsInDocument = new HashMap<String, Boolean>();
				
				Iterator<Section> sectionIterator = sections.iterator();
				while(sectionIterator.hasNext())
				{
					Section section = sectionIterator.next();
					
					Iterator<List<String>> documentIterator = section.getSentenceWordsList().iterator();
					while(documentIterator.hasNext())
					{
						List<String> sentence = documentIterator.next();
						for(String token : sentence)
						{
							if(TfIdf.collectionTermFrequency.containsKey(token))
							{
								TfIdf.collectionTermFrequency.put(token, TfIdf.collectionTermFrequency.get(token) + 1);
							}
							else
							{
								TfIdf.collectionTermFrequency.put(token, 1);
							}
							
							if(!wordsInDocument.containsKey(token))
							{
								wordsInDocument.put(token, true);
								
								if(TfIdf.documentFrequency.containsKey(token))
								{
									TfIdf.documentFrequency.put(token, TfIdf.documentFrequency.get(token) + 1);
								}
								else
								{
									TfIdf.documentFrequency.put(token, 1);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void f(List<Section> logicalStructure)
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
						List<TextLine> lines = contentItem.getItems();
						for(TextLine line : lines)
						{
							List<LineFragment> fragments = line.getItems();
							for (LineFragment lineFragment : fragments)
							{
								List<TextFragment> textFragments = lineFragment.getItems();
								for (TextFragment textFragment : textFragments)
								{
									System.out.println(textFragment.getText());
								}
							}
						}
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
				f(subSections);
				
				System.out.println("Section end");
			}
		}
	}
	
	public static List<Section> getWordsFromPDF(String curPdf)
	{
		List<Section> finalSections = null;
		
		try
		{
			List<String> document = new ArrayList<String>();
							
			String[] args = new String[1];
			args[0] = curPdf;
			
			List<Section> sections = null;
			try
			{
				sections = extractpdf.getPDFText(args);
			}
			catch(Exception ex)
			{
				System.out.println("Error in getPDFText : " + ex);
				ex.printStackTrace();
			}
			
//			f(sections);System.exit(0);
			
			finalSections = getFinalSections(sections, document);
			
			Tokenizer.getSentencesFromPDF(document, finalSections);
			Tokenizer.tokenizeSections(finalSections);
		}
		catch(Exception ex)
		{
			System.out.println("Error extracting from the PDF : " + ex);
			ex.printStackTrace();
		}		
		
		TfIdf.documentTermFrequency = new HashMap<String, Integer>();		
		try
		{
			Iterator<Section> sectionIterator = finalSections.iterator();
			while(sectionIterator.hasNext())
			{
				Section section = sectionIterator.next();
				
				Iterator<List<String>> documentIterator = section.getSentenceWordsList().iterator();
				while(documentIterator.hasNext())
				{
					List<String> sentence = documentIterator.next();
					for(String token : sentence)
					{
						if(TfIdf.documentTermFrequency.containsKey(token))
						{
							TfIdf.documentTermFrequency.put(token, TfIdf.documentTermFrequency.get(token) + 1);
						}
						else
						{
							TfIdf.documentTermFrequency.put(token, 1);
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return finalSections;
	}
	
	public static void getWordsFromTextFile()
	{
//		String path = "E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\txt\\pdfbox-0.72";
		String path = "E:\\8thSemProject\\textToPpt\\Corpus\\aclAnthology\\acl-arc\\txt\\sample";
		
		TfIdf.collectionTermFrequency = new HashMap<String, Integer>();
		TfIdf.documentFrequency = new HashMap<String, Integer>();

		File folder = new File(path);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles())); 
		for(int fileNo=0; fileNo<files.size(); fileNo++)
		{
			File curFile = files.get(fileNo);
			if(curFile.isDirectory())
			{
				File[] additionalFiles = curFile.listFiles();
				for(File file : additionalFiles)
				{
					files.add(file);
				}
				
				continue;
			}
			
			Map<String, Boolean> wordsInDocument = new HashMap<String, Boolean>();
			
			List<String> sentenceList = Tokenizer.getSentencesFromTxtFile(curFile.toString());
			List<List<String>> words = Tokenizer.tokenize(sentenceList);
			
			Iterator<List<String>> sentenceIterator = words.iterator();
			while(sentenceIterator.hasNext())
			{
				List<String> sentence = sentenceIterator.next();System.out.println(sentence.toString());
				for(String word : sentence)
				{
					if(TfIdf.collectionTermFrequency.containsKey(word))
					{
						TfIdf.collectionTermFrequency.put(word, TfIdf.collectionTermFrequency.get(word) + 1);
					}
					else
					{
						TfIdf.collectionTermFrequency.put(word, 1);
					}
					
					if(!wordsInDocument.containsKey(word))
					{
						wordsInDocument.put(word, true);
						
						if(TfIdf.documentFrequency.containsKey(word))
						{
							TfIdf.documentFrequency.put(word, TfIdf.documentFrequency.get(word) + 1);
						}
						else
						{
							TfIdf.documentFrequency.put(word, 1);
						}
					}
				}
			}			
		}
	}
	
	public static List<Section> getFinalSections(List<Section> sections, List<String> document)
	{
		List<Section> finalSections = new ArrayList<Section>();
		
		Iterator<Section> sectionsIterator = sections.iterator();
		while(sectionsIterator.hasNext())
		{
			Section section = sectionsIterator.next();
			
			StringBuilder textBlockBuffer = new StringBuilder();
			
			List<TextBlock> textBlocks = section.getContent();
			
			if(textBlocks == null)
			{
				List<Section> subSections = section.getSubSections();
				if(subSections != null)
				{
					for(Section subSection : subSections)
					{
						if(subSection != null)
						{
							subSection.setParent(section);
						}
					}
					
					finalSections.addAll(getFinalSections(subSections, document));
				}
				
				continue;
			}
			
			finalSections.add(section);
			
			Iterator<TextBlock> textBlockIterator = textBlocks.iterator();
			while(textBlockIterator.hasNext())
			{
				TextBlock textBlock = textBlockIterator.next();
				
				List<TextLine> textLines = textBlock.getItems();
				Iterator<TextLine> textLineIterator = textLines.iterator();
				while(textLineIterator.hasNext())
				{
					TextLine textLine = textLineIterator.next();
					List<LineFragment> lineFragments = textLine.getItems();
					Iterator<LineFragment> lineFragmentIterator = lineFragments.iterator();
					while(lineFragmentIterator.hasNext())
					{
						LineFragment lineFragment = lineFragmentIterator.next();
						List<TextFragment> textFragments = lineFragment.getItems();
						Iterator<TextFragment> textFragmentsIterator = textFragments.iterator();
						while(textFragmentsIterator.hasNext())
						{
							String textBlockText = textFragmentsIterator.next().getText();
							if(textBlockText == "" || textBlockText == null)
							{
								continue;
							}
							
							textBlockBuffer.append(textBlockText.trim() + " ");
						}
					}										
				}
			}
			
			document.add(textBlockBuffer.toString());
			
			List<Section> subSections = section.getSubSections();
			if(subSections != null)
			{
				for(Section subSection : subSections)
				{
					if(subSection != null)
					{
						subSection.setParent(section);
					}
				}
				
				finalSections.addAll(getFinalSections(subSections, document));
			}
		}		
		
		return finalSections;
	}
}