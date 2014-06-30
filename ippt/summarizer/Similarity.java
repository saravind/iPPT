import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import edu.stanford.nlp.ling.TaggedWord;

public class Similarity
{
	private static int senseContextWindowSize = 3;
	private static double alpha = 0.25;
	private static double beta = 0.25; 
	
	public static double IDFModifiedCosineSimilarity(List<String> sentence1, List<String> sentence2)
	{
//		List<Map<String, Integer>> sentenceFrequencyCounts = new ArrayList<Map<String, Integer>>();
//		sentenceFrequencyCounts.add(new HashMap<String, Integer>());
//		sentenceFrequencyCounts.add(new HashMap<String, Integer>());
//		
//		Set<String> unionOfWords = new HashSet<String>();
//		
//		double similarityValue = 0;
//		double[] tfIdfDotProducts = new double[2];
//		
//		Map<String, Integer> curMap = sentenceFrequencyCounts.get(0);
//		for(String word : sentence1)
//		{
//			if(!curMap.containsKey(word))
//			{
//				curMap.put(word, 1);
//			}
//			else
//			{
//				curMap.put(word, curMap.get(word));
//			}
//			
//			unionOfWords.add(word);
//		}
//		
//		curMap = sentenceFrequencyCounts.get(1);
//		for(String word : sentence2)
//		{
//			if(!curMap.containsKey(word))
//			{
//				curMap.put(word, 1);
//			}
//			else
//			{
//				curMap.put(word, curMap.get(word));
//			}
//			
//			unionOfWords.add(word);
//		}
//		
//		for(String word : unionOfWords)
//		{
//			if(TfIdf.idf.containsKey(word))
//			{
//				int[] frequencyCounts = new int[2];
//				for(int i=0; i<2; i++)
//				{
//					if(sentenceFrequencyCounts.get(i).containsKey(word))
//					{
//						frequencyCounts[i] = sentenceFrequencyCounts.get(i).get(word);
//					}
//				}
//				
//				similarityValue += frequencyCounts[0] * frequencyCounts[1] * Math.pow(TfIdf.idf.get(word), 2);
//			}
//		}
//		
//		curMap = sentenceFrequencyCounts.get(0);
//		for(String word : curMap.keySet())
//		{
//			if(TfIdf.idf.containsKey(word))
//			{
//				tfIdfDotProducts[0] += Math.pow(curMap.get(word) * TfIdf.idf.get(word), 2);
//			}
//		}
//		
//		curMap = sentenceFrequencyCounts.get(1);
//		for(String word : sentence2)
//		{
//			if(TfIdf.idf.containsKey(word))
//			{
//				tfIdfDotProducts[1] += Math.pow(curMap.get(word) * TfIdf.idf.get(word), 2);
//			}
//		}
//
//		tfIdfDotProducts[0] = Math.sqrt(tfIdfDotProducts[0]);
//		tfIdfDotProducts[1] = Math.sqrt(tfIdfDotProducts[1]);
//		
//		double returnValue;
//		if(tfIdfDotProducts[0] * tfIdfDotProducts[1] == 0)
//		{
//			returnValue = 0;
//		}
//		else
//		{
//			returnValue= similarityValue / (tfIdfDotProducts[0] * tfIdfDotProducts[1]);
//		}
//		
//		System.out.println(returnValue + " : " + sentence1 + " : " + sentence2);
//		return Double.isNaN(returnValue) ? 0 : returnValue;
		
		Set<String> allWordsSet = new HashSet<String>();
		Set<String> commonWordsSet = new HashSet<String>();
		
		Set<String> sentence1Words = new HashSet<String>();
		for(String word : sentence1)
		{
			sentence1Words.add(word);
		}
		
		Set<String> sentence2Words = new HashSet<String>();
		for(String word : sentence2)
		{
			sentence2Words.add(word);
		}
		
		allWordsSet.addAll(sentence1Words);
		allWordsSet.addAll(sentence2Words);
		
		for(String word : sentence1Words)
		{
			if(sentence2Words.contains(word))
			{
				commonWordsSet.add(word);
			}
		}

		double similarityValue = 0;
		for(String word : commonWordsSet)
		{
			if(TfIdf.documentTermFrequency.containsKey(word) && TfIdf.idf.containsKey(word))
			{
				similarityValue += TfIdf.documentTermFrequency.get(word) * TfIdf.idf.get(word);
			}
		}
		
		double sentence1DotProduct = 0;
		for(String word : sentence1Words)
		{
			sentence1DotProduct += Math.pow(TfIdf.documentTermFrequency.get(word), 2);
		}
		sentence1DotProduct = Math.sqrt(sentence1DotProduct);
		
		double sentence2DotProduct = 0;
		for(String word : sentence2Words)
		{
			sentence2DotProduct += Math.pow(TfIdf.documentTermFrequency.get(word), 2);
		}
		sentence2DotProduct = Math.sqrt(sentence2DotProduct);
		
		double returnValue;
		if(sentence1DotProduct * sentence2DotProduct != 0)
		{
			returnValue = similarityValue / (sentence1DotProduct * sentence2DotProduct);
		}
		else
		{
			returnValue = 0;
		}
		
		return returnValue;
	}
	
	public static double WordnetSimilarity(List<IndexWord> sentence1, List<IndexWord> sentence2)
	{
		double[][] wordPairSimilarity = new double[sentence1.size()][sentence2.size()];
		
		List<Synset> sentence1Synsets = new ArrayList<Synset>();
		List<Synset> sentence2Synsets = new ArrayList<Synset>();
		
		List<List<Synset>> sentence1Senses = new ArrayList<List<Synset>>();
		List<List<Synset>> sentence2Senses = new ArrayList<List<Synset>>();
		
		for(IndexWord taggedWord : sentence1)
		{
			if(taggedWord.getPOS() == POS.ADJECTIVE || taggedWord.getPOS() == POS.ADVERB)
			{
				continue;
			}
			
			List<Synset> senses = WordNetDictionary.getSynsets(taggedWord);
			sentence1Senses.add(senses);
		}
		
		for(IndexWord taggedWord : sentence2)
		{
			if(taggedWord.getPOS() == POS.ADJECTIVE || taggedWord.getPOS() == POS.ADVERB)
			{
				continue;
			}
			
			List<Synset> senses = WordNetDictionary.getSynsets(taggedWord);
			sentence2Senses.add(senses);
		}
		
		for(int i=0; i<sentence1Senses.size(); i++)
		{
			sentence1Synsets = getBestSenses(sentence1Senses);
			sentence2Synsets = getBestSenses(sentence2Senses);
		}
		
		for(int i=0; i<sentence1Synsets.size(); i++)
		{
			Synset sentence1Word = sentence1Synsets.get(i);
			
			for(int j=0; j<sentence2Synsets.size(); j++)
			{
				Synset sentence2Word = sentence2Synsets.get(j);
				
				wordPairSimilarity[i][j] = getWordnetSimilarity(sentence1Word, sentence2Word);
			}
		}
		
		return matchAndScoreSentenceWords(wordPairSimilarity);		
	}
	
	private static List<Synset> getBestSenses(List<List<Synset>> sensesList) 
	{
		List<Synset> bestSenses = new ArrayList<Synset>();
		
		List<List<Synset>> contextWordSenses = new ArrayList<List<Synset>>();
		for(int i=0; i<senseContextWindowSize/2; i++)
		{
			contextWordSenses.add(new ArrayList<Synset>());
		}
		
		for(int i=1; i<=senseContextWindowSize/2; i++)
		{
			contextWordSenses.add(sensesList.get(i));
		}
		
		List<List<String>> contextWordGloss = new ArrayList<List<String>>();
		for(int i=0; i<senseContextWindowSize/2; i++)
		{
			contextWordGloss.add(new ArrayList<String>());
		}
		
		for(int i=1; i<=senseContextWindowSize/2; i++)
		{
			List<Synset> wordSenses = sensesList.get(i);
			List<String> wordSenseGloss = new ArrayList<String>();
			for(Synset sense : wordSenses)
			{
				wordSenseGloss.add(sense.getGloss());
			}
		}
		
		for(int curWordNo=0; curWordNo<sensesList.size(); curWordNo++)
		{
			List<Synset> curWord = sensesList.get(curWordNo);
			double maxSenseScore = Double.NEGATIVE_INFINITY;
			Synset bestSense = null;
			
			for(Synset sense : curWord)
			{
				double curSenseScore = getLCSSimilarity(sense.getGloss(), contextWordGloss);
				if(curSenseScore > maxSenseScore)
				{
					maxSenseScore = curSenseScore;
					bestSense = sense;
				}
			}
			bestSenses.add(bestSense);
		}
		
		return bestSenses;
	}
	
	private static double getLCSSimilarity(String targetSenseGloss, List<List<String>> contextSensesGloss)
	{
		double similarityScore = 0;
		
		List<String> targetSentence = Tokenizer.tokenize(targetSenseGloss);
		
		Iterator<List<String>> contextSensesGlossIterator = contextSensesGloss.iterator();
		while(contextSensesGlossIterator.hasNext())
		{
			List<String> referenceWordGlossList = contextSensesGlossIterator.next();
			Iterator<String> referenceWordGlossIterator = referenceWordGlossList.iterator();
			while(referenceWordGlossIterator.hasNext())
			{
				String referenceWordGloss = referenceWordGlossIterator.next();				
				List<String> referenceSentence = Tokenizer.tokenize(referenceWordGloss);
				
				int[][] num = new int[targetSentence.size()+1][referenceSentence.size()+1];
				 
		        for(int i=1; i<=targetSentence.size(); i++)
		        {
		        	for (int j=1; j<=referenceSentence.size(); j++)
		            {
		        		if (targetSentence.get(i-1).equals(referenceSentence.get(j-1)))
		        		{
		        			num[i][j] = 1 + num[i-1][j-1];
		        		}
		                else
		                {
		                	num[i][j] = Math.max(num[i-1][j], num[i][j-1]);
		                }
		            }
		        }
		 	 
		        int s1position = targetSentence.size();
		        int s2position = referenceSentence.size();
		        
		        List<String> lcsWords = new ArrayList<String>();
		        List<Integer> lcsPositions = new ArrayList<Integer>();
		 
		        while(s1position != 0 && s2position != 0)
		        {
		        	String targetWord = targetSentence.get(s1position-1);
		        	String referenceWord = referenceSentence.get(s2position-1);
		        	if(targetWord.equals(referenceWord))
	                {
//		        		lcsWords.add(referenceWord);
		        		lcsPositions.add(s1position-1);
	                    s1position--;
	                    s2position--;
	                }
	                else if(num[s1position][s2position-1] >= num[s1position-1][s2position])
	                {
	                    s2position--;
	                }
	                else
	                {
	                    s1position--;
	                }
		        }
//		        Collections.reverse(lcsWords);
//		        Collections.reverse(lcsPositions);
		        
		        int lcsSimilarity = 0;
		        int contiguousSequenceSize = 1;
		        for(int i=1; i<lcsPositions.size(); i++)
		        {
		        	if(lcsPositions.get(i) == lcsPositions.get(i-1)-1)
		        	{
		        		contiguousSequenceSize++;
		        	}
		        	else
		        	{
		        		lcsSimilarity += Math.pow(contiguousSequenceSize, 2);
		        		contiguousSequenceSize = 1;
		        	}
		        }
		        lcsSimilarity += Math.pow(contiguousSequenceSize, 2);
		        
		        if(lcsSimilarity > similarityScore)
		        {
		        	similarityScore = lcsSimilarity;
		        }
			}
		}
		
		return similarityScore;
	}
	
	private static double getLCSSimilarity(List<Synset> referenceSenseList, List<Synset> candidateSenseList)
	{
		double similarityScore = 0;
		
		Iterator<Synset> referenceSenseIterator = referenceSenseList.iterator();
		while(referenceSenseIterator.hasNext())
		{
			Synset referenceSense = referenceSenseIterator.next();
			String referenceSenseGloss = WordNetDictionary.getGloss(referenceSense);
			
			Iterator<Synset> candidateSenseIterator = candidateSenseList.iterator();
			while(referenceSenseIterator.hasNext())
			{
				Synset candidateSense = candidateSenseIterator.next();
				String candidateSenseGloss = WordNetDictionary.getGloss(candidateSense);
				
				List<String> referenceSentence = Tokenizer.tokenize(referenceSenseGloss);
				List<String> candidateSentence = Tokenizer.tokenize(candidateSenseGloss);
				
				int[][] num = new int[referenceSentence.size()+1][candidateSentence.size()+1];
				 
		        for(int i=1; i<=referenceSentence.size(); i++)
		        {
		        	for (int j=1; j<=candidateSentence.size(); j++)
		            {
		        		if (referenceSentence.get(i-1).equals(candidateSentence.get(j-1)))
		        		{
		        			num[i][j] = 1 + num[i-1][j-1];
		        		}
		                else
		                {
		                	num[i][j] = Math.max(num[i-1][j], num[i][j-1]);
		                }
		            }
		        }
		 	 
		        int s1position = referenceSentence.size();
		        int s2position = candidateSentence.size();
		        
		        List<String> lcsWords = new ArrayList<String>();
		        List<Integer> lcsPositions = new ArrayList<Integer>();
		 
		        while(s1position != 0 && s2position != 0)
		        {
		        	String referenceWord = referenceSentence.get(s1position-1);
		        	String candidateWord = candidateSentence.get(s2position-1);
		        	if(referenceWord.equals(candidateWord))
	                {
//		        		lcsWords.add(referenceWord);
		        		lcsPositions.add(s1position-1);
	                    s1position--;
	                    s2position--;
	                }
	                else if(num[s1position][s2position-1] >= num[s1position-1][s2position])
	                {
	                    s2position--;
	                }
	                else
	                {
	                    s1position--;
	                }
		        }
//		        Collections.reverse(lcsWords);
//		        Collections.reverse(lcsPositions);
		        
		        int lcsSimilarity = 0;
		        int contiguousSequenceSize = 1;
		        for(int i=1; i<lcsPositions.size(); i++)
		        {
		        	if(lcsPositions.get(i) == lcsPositions.get(i-1)-1)
		        	{
		        		contiguousSequenceSize++;
		        	}
		        	else
		        	{
		        		lcsSimilarity += Math.pow(contiguousSequenceSize, 2);
		        		contiguousSequenceSize = 1;
		        	}
		        }
		        lcsSimilarity += Math.pow(contiguousSequenceSize, 2);
		        
		        if(lcsSimilarity > similarityScore)
		        {
		        	similarityScore = lcsSimilarity;
		        }
			}
		}
		
		return similarityScore;
	}
	
	private static double getWordnetSimilarity(Synset synset1, Synset synset2)
	{
		List<Synset> synset1HypernymChain = null;
		List<Synset> synset2HypernymChain = null;
		
		int synset1Depth = WordNetDictionary.getDepthAndHypernymChain(synset1, synset1HypernymChain);
		int synset2Depth = WordNetDictionary.getDepthAndHypernymChain(synset2, synset2HypernymChain);
		
		Synset tempSynset1 = synset1;
		Synset tempSynset2 = synset2;
		
		int synset1Offset = 0;
		int synset2Offset = 0;
		
		if(synset1Depth > synset2Depth)
		{
			synset1Offset = synset1Depth-synset2Depth; 
		}
		else
		{
			synset2Offset = synset2Depth-synset1Depth;
		}
		
		while(synset1HypernymChain.get(synset1Offset) != synset2HypernymChain.get(synset2Offset))
		{
			synset1Offset++;
			synset2Offset++;
		}
		
		return apply(alpha * (synset1Depth - synset1Offset)) / (apply(alpha * (synset1Depth - synset1Offset)) + apply(beta * (synset2Depth - synset1Offset + 1)));
	}
	
	private static double matchAndScoreSentenceWords(double[][] costMatrix)
	{
		boolean done = false;
		
		while(!done)
		{
			for(int i=0; i<costMatrix.length; i++)
			{
				double minValue = Double.POSITIVE_INFINITY;
				for(int j=0; j<costMatrix.length; j++)
				{
					if(costMatrix[i][j] < minValue)
					{
						minValue = costMatrix[i][j];
					}
				}
				
				for(int j=0; j<costMatrix.length; i++)
				{
					costMatrix[i][j] -= minValue;
				}
			}
			
			int[] rowCover = new int[costMatrix.length];
			int[] colCover = new int[costMatrix.length];
			
			for(int i=0; i<costMatrix.length; i++)
			{
				int zeroIndex = -1;
				for(int j=0; j<costMatrix.length; j++)
				{
					if(zeroIndex == -1 && costMatrix[i][j] == 0)
					{
						zeroIndex = j;
					}
					else if(costMatrix[i][j] == 0)
					{
						zeroIndex = costMatrix.length;
						break;
					}
				}
				
				if(zeroIndex >= 0 && zeroIndex < costMatrix.length)
				{
					rowCover[i] = 1;
				}
			}
			
			for(int i=0; i<rowCover.length; i++)
			{
				
			}
		}
		
		return 0;
	}
	
	private static double apply(double x)
	{
		return Math.exp(x) - 1;
	}
	
	public enum SimilarityMethod
	{
		IDF,
		WORDNET,
		IDF_WORDNET;
		
		public static SimilarityMethod get(String similarity) throws Exception
		{
			if(similarity.equalsIgnoreCase("tfidf"))
			{
				return IDF;
			}
			else if(similarity.equalsIgnoreCase("wordnet"))
			{
				return WORDNET;
			}
			else if(similarity.equalsIgnoreCase("tfidf_wordnet"))
			{
				return IDF_WORDNET;
			}
			else
			{
				throw new Exception("invalid similarity method");
			}			
		}
	}
}