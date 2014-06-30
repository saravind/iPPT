import ippt.pdf.Section;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import edu.stanford.nlp.ling.HasWord;

import net.sf.extjwnl.dictionary.Dictionary;

public class Tokenizer
{
	private static Set<String> stopWords;
	public static String output;
	
	public static void setStopWords(String stopWordsFile)
	{
		stopWords = new HashSet<String>();
		
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(stopWordsFile));
			String line = null;
			
			while((line = br.readLine()) != null)
			{
				stopWords.add(line);
			}
		}
		catch(IOException ex)
		{
			System.out.println(ex);
		}
	}
	
	public static List<String> getSentencesFromPDF(List<String> document)
	{
		List<String> sentenceList = new ArrayList<String>();		
		Pattern sentenceDelimiter = Pattern.compile("[.?;!]");
		
		Iterator<String> documentIterator = document.iterator();
		while(documentIterator.hasNext())
		{
			String textSegment = documentIterator.next();
			String[] sentences = sentenceDelimiter.split(textSegment);
			for(String sentence : sentences)
			{
				if(sentence == null || sentence.length() == 0)
				{
					continue;
				}
				
				if(sentence.charAt(0) == ' ')
				{
					sentence = sentence.substring(1);
				}
				
				sentenceList.add(sentence);
			}
		}
		
		return sentenceList;
	}
	
	public static void getSentencesFromPDF(List<String> document, List<Section> sections)
	{
		Pattern sentenceDelimiter = Pattern.compile("[.?][ ]");

		Iterator<String> documentIterator = document.iterator();
		Iterator<Section> sectionIterator = sections.iterator();
		while(documentIterator.hasNext())
		{
			Section section = sectionIterator.next();
			
			String textSegment = documentIterator.next();
			String[] sentences = sentenceDelimiter.split(textSegment);
			
			List<String> sentenceList = new ArrayList<String>();
			for(String sentence : sentences)
			{
				if(sentence == null || sentence.length() == 0)
				{
					continue;
				}
				
				if(sentence.charAt(0) == ' ')
				{
					sentence = sentence.substring(1);
				}
				
				sentenceList.add(sentence);
			}
			
			section.setSentenceList(sentenceList);
		}
	}
	
	public static List<String> getSentencesFromTxtFile(String filename)
	{
		BufferedReader br = null;
		
		StringBuilder document = new StringBuilder();
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line = null;
			while((line = br.readLine()) != null)
			{
				document.append(line);
			}
		}
		catch(IOException ex)
		{
			System.out.println("Error reading text file '" + filename + "' : " + ex);
		}
		
		return getSentences(document.toString());
	}
	
	public static List<String> getSentences(String document)
	{
		List<String> sentenceList = new ArrayList<String>();
		
		String[] sentences = document.split("[\n.;?!]");
		for(String sentence : sentences)
		{
			if(sentence != null && sentence.length() > 1)
			{
				sentenceList.add(sentence);
			}
		}
		
		return sentenceList;
	}
	
	public static List<List<String>> tokenizeWithSpaces(List<String> sentenceList)
	{
		List<List<String>> tokenizedDocument = new ArrayList<List<String>>();
		
		Pattern tokenDelimiter = Pattern.compile("[ ]");
		
		Iterator<String> sentenceIterator = sentenceList.iterator();
		while(sentenceIterator.hasNext())
		{
			String sentence = sentenceIterator.next();
			
			String[] tokens = tokenDelimiter.split(sentence);
			List<String> requiredTokens = new ArrayList<String>();
			for(String token : tokens)
			{
				if(stopWords.contains(token))
				{
					continue;
				}
				
				requiredTokens.add(token);
			}
			
			tokenizedDocument.add(requiredTokens);
		}
		
		return tokenizedDocument;
	}
	
	public static List<List<String>> tokenize(List<String> sentenceList)
	{
		List<List<String>> tokenizedDocument = new ArrayList<List<String>>();
		
		Pattern tokenDelimiter = Pattern.compile("[ ,:(){}\\[\\]]+");
		
		String splitWord = "";
		Iterator<String> sentenceIterator = sentenceList.iterator();
		while(sentenceIterator.hasNext())
		{
			String sentence = sentenceIterator.next();
			
			String[] tokens = tokenDelimiter.split(sentence);
			if(tokens.length == 0)
			{
				continue;
			}
			
			List<String> requiredTokens = new ArrayList<String>();
			int noTokens = tokens.length;
			String token = tokens[0];
			
			if(tokens.length == 1)
			{
				if(splitWord != "")
				{
					String tempWord = splitWord.substring(0, splitWord.length()-1) + token;
					if(WordNetDictionary.lookupWord(tempWord))
					{
						requiredTokens.add(tempWord);
					}
					else
					{
						requiredTokens.add(splitWord);
						requiredTokens.add(token);
					}
					
					splitWord = "";
				}
				else
				{
					if(token.endsWith("-"))
					{
						splitWord = token;
					}
					else
					{
						requiredTokens.add(token);
					}
				}
			}
			else
			{
				if(splitWord != "")
				{
					String tempWord = splitWord.substring(0, splitWord.length()-1) + token;
					if(WordNetDictionary.lookupWord(tempWord))
					{
						requiredTokens.add(tempWord);
					}
					else
					{
						requiredTokens.add(splitWord);
						requiredTokens.add(token);
					}
					
					splitWord = "";
				}
				
				for(int tokenNo=1; tokenNo<noTokens-1; tokenNo++)
				{
					token = tokens[tokenNo];
					
					if(token == null || token == "" || stopWords.contains(token))
					{
						continue;
					}
					
					requiredTokens.add(token);
				}
				
				token = tokens[noTokens-1];
				if(token.endsWith("-"))
				{
					splitWord = token;
				}
				else
				{
					requiredTokens.add(token);
				}
			}
				
			tokenizedDocument.add(requiredTokens);
		}
		
		if(splitWord != "")
		{
			List<String> requiredTokens = tokenizedDocument.remove(tokenizedDocument.size()-1);
			requiredTokens.add(splitWord);
			tokenizedDocument.add(requiredTokens);
		}
		
		return tokenizedDocument;
	}
	
	public static void tokenizeSections(List<Section> sections)
	{
		Pattern tokenDelimiter = Pattern.compile("[ ,:(){}\\[\\]]+");
		
		Iterator<Section> sectionIterator = sections.iterator();
		while(sectionIterator.hasNext())
		{
			Section section = sectionIterator.next();
			
			List<List<String>> tokenizedDocument = new ArrayList<List<String>>();
			String splitWord = "";
			
			List<String> sentenceList = section.getSentenceList();
			Iterator<String> sentenceIterator = sentenceList.iterator();
			while(sentenceIterator.hasNext())
			{
				String sentence = sentenceIterator.next();
				
				String[] tokens = tokenDelimiter.split(sentence);
				if(tokens.length == 0)
				{
					continue;
				}
				
				List<String> requiredTokens = new ArrayList<String>();
				int noTokens = tokens.length;
				String token = tokens[0];
				
				if(tokens.length == 1)
				{
					if(splitWord != "")
					{
						String tempWord = splitWord.substring(0, splitWord.length()-1) + token;
						if(WordNetDictionary.lookupWord(tempWord))
						{
							requiredTokens.add(tempWord);
						}
						else
						{
							requiredTokens.add(splitWord);
							requiredTokens.add(token);
						}
						
						splitWord = "";
					}
					else
					{
						if(token.endsWith("-"))
						{
							splitWord = token;
						}
						else
						{
							requiredTokens.add(token);
						}
					}
				}
				else
				{
					if(splitWord != "")
					{
						String tempWord = splitWord.substring(0, splitWord.length()-1) + token;
						if(WordNetDictionary.lookupWord(tempWord))
						{
							requiredTokens.add(tempWord);
						}
						else
						{
							requiredTokens.add(splitWord);
							requiredTokens.add(token);
						}
						
						splitWord = "";
					}
					
					for(int tokenNo=1; tokenNo<noTokens-1; tokenNo++)
					{
						token = tokens[tokenNo];
						
						if(token == null || token == "" || stopWords.contains(token))
						{
							continue;
						}
						
						requiredTokens.add(token);
					}
					
					token = tokens[noTokens-1];
					if(token.endsWith("-"))
					{
						splitWord = token;
					}
					else
					{
						requiredTokens.add(token);
					}
				}
					
				tokenizedDocument.add(requiredTokens);
			}
			
			if(splitWord != "")
			{
				List<String> requiredTokens = tokenizedDocument.remove(tokenizedDocument.size()-1);
				requiredTokens.add(splitWord);
				tokenizedDocument.add(requiredTokens);
			}
			
			section.setSentenceWordsList(tokenizedDocument);
		}
	}
	
	public static List<String> tokenize(String sentence)
	{
		Pattern tokenDelimiter = Pattern.compile("[ ,:(){}\\[\\]]+");
		
		String[] tokens = tokenDelimiter.split(sentence);
		if(tokens.length == 0)
		{
			return null;
		}
		
		List<String> requiredTokens = new ArrayList<String>();
		
		for(String token : tokens)
		{
			if(token == null || token == "")
			{
				continue;
			}
			
			requiredTokens.add(token);
		}
		
		return requiredTokens;
	}
}

class Token implements HasWord
{
	private String tokenString;
	
	public Token(String tokenString)
	{
		this.tokenString = tokenString;
	}
	
	public String word()
	{
		return tokenString;
	}
	
	public void setWord(String tokenString)
	{
		this.tokenString = tokenString;
	}
}