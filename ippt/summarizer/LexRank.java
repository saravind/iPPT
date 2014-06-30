import ippt.pdf.Section;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.extjwnl.data.IndexWord;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.*;

public class LexRank
{
	private double[][] similarityMatrix;
	private double[] sentenceScores;
	
	private double threshold = 0.1;			//minimum change in LexRank vectors required
	private double alpha = 0.85;				//The teleportation factor
	
	private Similarity.SimilarityMethod similarity;
	
	public LexRank()
	{
		
	}
	
	public LexRank(double threshold, double alpha)
	{
		this.threshold = threshold;
		this.alpha = alpha;
	}
	
	public LexRank(Similarity.SimilarityMethod similarity)
	{
		this.similarity = similarity;		
	}
	
	public LexRank(double threshold, double alpha, Similarity.SimilarityMethod similarity)
	{
		this.threshold = threshold;
		this.alpha = alpha;
		
		this.similarity = similarity;
	}
	
	public LexRank(List<List<String>> sentenceList, Similarity.SimilarityMethod similarity)
	{
		this();
		
		scoreSentences(sentenceList, similarity);
	}
	
	public LexRank(double threshold, double alpha, List<List<String>> sentenceList, Similarity.SimilarityMethod similarity)
	{
		this(threshold, alpha);
		
		scoreSentences(sentenceList, similarity);
	}
	
	public void scoreSentences(List<List<String>> sentenceList, Similarity.SimilarityMethod similarity)
	{
		similarityMatrix = computeSimilarityMatrix(sentenceList, similarity);
		PowerMethod(similarityMatrix, sentenceList.size());
	}
	
//	public void scoreTaggedSentences(List<ArrayList<TaggedWord>> sentenceList, Similarity.SimilarityMethod similarity)
//	{
//		computeSimilarityMatrixForTaggedSentences(sentenceList, similarity);
//		PowerMethod(similarityMatrix, sentenceList.size());
//	}
	
	public void scoreIndexWordTaggedSentences(List<List<IndexWord>> sentenceList, Similarity.SimilarityMethod similarity)
	{
		computeSimilarityMatrixForTaggedSentences(sentenceList, similarity);
		PowerMethod(similarityMatrix, sentenceList.size());
	}
	
	public double[][] computeSimilarityMatrix(List<List<String>> sentenceList, Similarity.SimilarityMethod similarity)
	{
		if(similarity == Similarity.SimilarityMethod.WORDNET || similarity == Similarity.SimilarityMethod.IDF_WORDNET)
		{
			System.out.println("Wordnet similarity requires tagged sentences");
			System.exit(0);
		}
		
		TfIdf.CalculateDocumentTermFrequency(sentenceList);
		
		double[][] tfidfSimilarityMatrix = new double[sentenceList.size()][sentenceList.size()];
		
		int noSentences = sentenceList.size();

		System.out.println("TFIDF output begins here : ");
		for(int rowNo=0; rowNo<noSentences; rowNo++)
		{
			List<String> referenceSentence = sentenceList.get(rowNo);
			for(int colNo=0; colNo<noSentences && colNo<(noSentences - rowNo); colNo++)
			{
				List<String> currentSentence = sentenceList.get(colNo);
				tfidfSimilarityMatrix[rowNo][colNo] = tfidfSimilarityMatrix[colNo][rowNo] = Similarity.IDFModifiedCosineSimilarity(referenceSentence, currentSentence);
			}
		}
		System.out.println("TFIDF output ends here");
		
		return tfidfSimilarityMatrix;
	}
	
//	public void computeSimilarityMatrixForTaggedSentences(List<ArrayList<TaggedWord>> taggedSentenceList)
//	{
//		computeSimilarityMatrixForTaggedSentences(taggedSentenceList, this.similarity);
//	}
	
//	public void computeSimilarityMatrixForTaggedSentences(List<ArrayList<TaggedWord>> taggedSentenceList, Similarity.SimilarityMethod similarity)
//	{
//		double[][] tfidfSimilarityMatrix = new double[taggedSentenceList.size()][taggedSentenceList.size()];
//		double[][] wordnetSimilarityMatrix = new double[taggedSentenceList.size()][taggedSentenceList.size()];
//		
//		int noSentences = taggedSentenceList.size();
//
//		for(int rowNo=0; rowNo<noSentences; rowNo++)
//		{
//			List<TaggedWord> taggedReferenceSentence = taggedSentenceList.get(rowNo);
//			List<String> referenceSentence = new ArrayList<String>();
//			for(TaggedWord taggedWord : taggedReferenceSentence)
//			{
//				referenceSentence.add(taggedWord.value());
//			}
//			
//			for(int colNo=0; colNo<noSentences && colNo<(noSentences - rowNo); colNo++)
//			{
//				List<TaggedWord> taggedCurrentSentence = taggedSentenceList.get(colNo);
//				List<String> currentSentence = new ArrayList<String>();
//				for(TaggedWord taggedWord : taggedCurrentSentence)
//				{
//					currentSentence.add(taggedWord.value());
//				}
//				
//				if(similarity == Similarity.SimilarityMethod.IDF || similarity == Similarity.SimilarityMethod.IDF_WORDNET)
//				{
//					tfidfSimilarityMatrix[rowNo][colNo] = tfidfSimilarityMatrix[colNo][rowNo] = Similarity.IDFModifiedCosineSimilarity(referenceSentence, currentSentence);
//				}
//				
//				if(similarity == Similarity.SimilarityMethod.WORDNET || similarity == Similarity.SimilarityMethod.IDF_WORDNET)
//				{
//					tfidfSimilarityMatrix[rowNo][colNo] = tfidfSimilarityMatrix[colNo][rowNo] = Similarity.WordnetSimilarity(taggedReferenceSentence, taggedCurrentSentence);
//				}
//			}
//		}
//	}
	
	public void computeSimilarityMatrixForTaggedSentences(List<List<IndexWord>> taggedSentenceList, Similarity.SimilarityMethod similarity)
	{
		double[][] tfidfSimilarityMatrix = new double[taggedSentenceList.size()][taggedSentenceList.size()];
		double[][] wordnetSimilarityMatrix = new double[taggedSentenceList.size()][taggedSentenceList.size()];
		
		int noSentences = taggedSentenceList.size();
		
		if(similarity == Similarity.SimilarityMethod.WORDNET || similarity == Similarity.SimilarityMethod.IDF_WORDNET)
		{
			for(int rowNo=0; rowNo<noSentences; rowNo++)
			{
				List<IndexWord> taggedReferenceSentence = taggedSentenceList.get(rowNo);
				
				for(int colNo=0; colNo<noSentences && colNo<(noSentences - rowNo); colNo++)
				{
					List<IndexWord> taggedCurrentSentence = taggedSentenceList.get(colNo);					
					tfidfSimilarityMatrix[rowNo][colNo] = tfidfSimilarityMatrix[colNo][rowNo] = Similarity.WordnetSimilarity(taggedReferenceSentence, taggedCurrentSentence);
				}
			}
		}
		else if(similarity == Similarity.SimilarityMethod.IDF || similarity == Similarity.SimilarityMethod.IDF_WORDNET)
		{
			for(int rowNo=0; rowNo<noSentences; rowNo++)
			{
				List<IndexWord> taggedReferenceSentence = taggedSentenceList.get(rowNo);
				List<String> referenceSentence = new ArrayList<String>();
				for(IndexWord indexWord : taggedReferenceSentence)
				{
					referenceSentence.add(indexWord.getLemma());
				}
				
				for(int colNo=0; colNo<noSentences && colNo<(noSentences - rowNo); colNo++)
				{
					List<IndexWord> taggedCurrentSentence = taggedSentenceList.get(colNo);
					List<String> currentSentence = new ArrayList<String>();
					for(IndexWord taggedWord : taggedCurrentSentence)
					{
						currentSentence.add(taggedWord.getLemma());
					}
					
					
					tfidfSimilarityMatrix[rowNo][colNo] = tfidfSimilarityMatrix[colNo][rowNo] = Similarity.IDFModifiedCosineSimilarity(referenceSentence, currentSentence);
				}
			}
		}
	}
	
	public void PowerMethod(double[][] similarityMatrix, int noSentences)
	{
		double[][] HMatrix = new double[this.similarityMatrix.length][this.similarityMatrix.length];
		
		for(int rowNo=0; rowNo<similarityMatrix.length; rowNo++)
		{
			for(int colNo=0; colNo<similarityMatrix.length; colNo++)
			{
				HMatrix[rowNo][colNo] = similarityMatrix[rowNo][colNo];
			}
		}
		
		double[] eigenVector = new double[HMatrix.length];
		double[] prevEigenVector;
		
		double noSentencesInverse = Math.pow(noSentences, -1);
		double[] a = new double[HMatrix.length];
		
		for(int i=0; i<eigenVector.length; i++)
		{
			eigenVector[i] = noSentencesInverse;
		}
		
		for(int rowNo=0; rowNo<HMatrix.length; rowNo++)
		{
			a[rowNo] = 1;
			float rowSum = 0;
			for(int colNo=0; colNo<HMatrix.length; colNo++)
			{
				if(HMatrix[rowNo][colNo] != 0)
				{
					a[rowNo] = 0;
				}
				
				rowSum += HMatrix[rowNo][colNo];
			}
			
			if(a[rowNo] != 1)
			{
				for(int colNo=0; colNo<HMatrix.length; colNo++)
				{
					HMatrix[rowNo][colNo] = HMatrix[rowNo][colNo] / rowSum;
				}
			}
		}
		
//		System.out.println("HMatrix beings here");
//		for(int rowNo=0; rowNo<HMatrix.length; rowNo++)
//		{
//			for(int colNo=0; colNo<HMatrix.length; colNo++)
//			{
//				System.out.print(HMatrix[rowNo][colNo] + ";" + similarityMatrix[rowNo][colNo] + " ");
//			}System.out.println();
//		}System.out.println();
		
		double vectorDiff;
		do
		{
			prevEigenVector = eigenVector;
			eigenVector = new double[HMatrix.length];
			
			System.out.println("eigenvector : ");
			for(int i=0; i<prevEigenVector.length; i++)
			{
				System.out.print(prevEigenVector[i] + " ");
			}System.out.println();
			
			for(int rowNo=0; rowNo<eigenVector.length; rowNo++)
			{
				for(int columnNo=0; columnNo<eigenVector.length; columnNo++)
				{
//					eigenVector[rowNo] += d * noSentencesInverse + (1 - d) * HMatrix[rowNo][columnNo] * prevEigenVector[columnNo];
					eigenVector[rowNo] += ( alpha * HMatrix[rowNo][columnNo] * prevEigenVector[columnNo] ) + ( alpha * prevEigenVector[columnNo] * a[rowNo] + (1 - alpha) ) * noSentencesInverse;
				}
			}
			
			vectorDiff = 0;
			for(int i=0; i<eigenVector.length; i++)
			{
				vectorDiff += Math.pow(eigenVector[i] - prevEigenVector[i], 2);
			}
			System.out.println("vectorDiff : " + vectorDiff);
			vectorDiff = Math.sqrt(vectorDiff);
			
		}while(vectorDiff >= threshold);
		
		sentenceScores = eigenVector;
	}
	
	public double[] getSentenceScores()
	{
		return sentenceScores;
	}
}

class ScoredSection extends Section
{
	private List<String> summarySentenceList;
	
	public ScoredSection(Section section)
	{
		super(section.getHeading(), section.getContent(), section.getSubSections());
		
		sentenceList = new ArrayList<String>();
		sentenceWordsList = new ArrayList<List<String>>();
		images = section.getImages();
		
		parent = section.getParent();
	}
	
	public void createSummarySentenceList()
	{
		summarySentenceList = new ArrayList<String>();
	}
	
	public void setSummarySentenceList(List<String> summarySentenceList)
	{
		this.summarySentenceList = summarySentenceList;
	}
	
	public void addSentenceToSummarySentenceList(String sentence)
	{
		summarySentenceList.add(sentence);
	}
	
	public List<String> getSummarySentenceList()
	{
//		for(String s : summarySentenceList)
//		{
//			System.out.println("^ " + s);
//		}
		return summarySentenceList;
	}
}