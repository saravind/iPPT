import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import edu.stanford.nlp.ling.Tag;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.TTags;

import net.sf.extjwnl.JWNL;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

public class WordNetDictionary
{
	private static String propsFile = "E:\\8thSemProject\\textToPpt\\code\\JWNL\\extjwnl-1.6.4\\src\\extjwnl\\src\\main\\resources\\net\\sf\\extjwnl\\file_properties.xml";
	
	private static Dictionary wordNetDictionary;
	private static POS[] partsOfSpeech;
		
	static
	{
		try
		{
			JWNL.initialize(new FileInputStream(propsFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
		
		wordNetDictionary = Dictionary.getInstance();
		
		partsOfSpeech = new POS[4];
		partsOfSpeech[0] = POS.ADJECTIVE;
		partsOfSpeech[1] = POS.ADVERB;
		partsOfSpeech[2] = POS.NOUN;
		partsOfSpeech[3] = POS.VERB;
	}
	
	public static void printAllWords()
	{		
		try
		{
			for(int i=0; i<partsOfSpeech.length; i++)
			{
				Iterator<IndexWord> indexWordIterator = wordNetDictionary.getIndexWordIterator(partsOfSpeech[i]);
				while(indexWordIterator.hasNext())
				{
					IndexWord indexWord = indexWordIterator.next();
					System.out.println(partsOfSpeech[i] + " : " + indexWord.getLemma());
				}
			}
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean lookupWord(String word)
	{
		try
		{
			IndexWordSet indexWords = wordNetDictionary.lookupAllIndexWords(word);
			if(indexWords.size() == 0)
			{
				return false;
			}
			
			return true;
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean lookupWord(POS pos, String word)
	{
		try
		{
			IndexWord indexWord = wordNetDictionary.getIndexWord(pos, word);
			if(indexWord != null)
			{
				return true;
			}
			
			return false;
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static IndexWord getIndexWord(TaggedWord taggedWord)
	{
		POS pos = getPOS(taggedWord.tag());
		String lemma = taggedWord.value();
		
		IndexWord indexWord = null;
		try
		{
			indexWord = wordNetDictionary.getIndexWord(pos, lemma);
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
		
		return indexWord;
	}
	
	public static List<Synset> getSynsets(IndexWord word)
	{
		return word.getSenses();
	}
	
	public static List<Synset> getSynsets(TaggedWord word)
	{
		List<Synset> senses = null;
		try
		{
			senses = wordNetDictionary.getIndexWord(POS.getPOSForKey(word.tag()), word.value()).getSenses();
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
		
		return senses;
	}
	
	public static List<Synset> getSynsets(String word)
	{
		List<Synset> senses = new ArrayList<Synset>();
		try
		{
			IndexWordSet indexWords = wordNetDictionary.lookupAllIndexWords(word);
			for(IndexWord indexWord : indexWords.getIndexWordArray())
			{
				List<Synset> indexWordSenses = indexWord.getSenses();
				senses.addAll(indexWordSenses);
			}
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
		}
		
		return senses;
	}
	
	public static String[] getGloss(TaggedWord word)
	{
		List<Synset> senses = getSynsets(word);
		String[] gloss = new String[senses.size()];
		
		Iterator<Synset> sensesIterator = senses.iterator();
		int senseNo = 0;
		while(sensesIterator.hasNext())
		{
			Synset sense = sensesIterator.next();
			gloss[senseNo++] = sense.getGloss();
		}
		
		return gloss;
	}
	
	public static String getGloss(Synset sense)
	{
		return sense.getGloss();
	}
	
	public static int getDepthAndHypernymChain(Synset sense, List<Synset> synsetHypernymChain)
	{
		int minDepth = 0;
		
		Queue<List<Synset>> synsetQueue = new LinkedList<List<Synset>>();
		List<Synset> initHypernymChain = new LinkedList<Synset>();
		initHypernymChain.add(sense);
		synsetQueue.add(initHypernymChain);
		
		while(true)
		{
			Queue<List<Synset>> nextLevelSynsetQueue = new LinkedList<List<Synset>>();
			while(synsetQueue.size() != 0)
			{
				List<Synset> currentHypernymChain = synsetQueue.poll(); 
				Synset currentSense = currentHypernymChain.get(0);
				
				List<Pointer> pointers = currentSense.getPointers(PointerType.HYPERNYM);
				if(pointers.size() == 0)
				{
					synsetHypernymChain = currentHypernymChain;
					Collections.reverse(synsetHypernymChain);
					return minDepth;
				}
				
				for(Pointer pointer : pointers)
				{
					List<Synset> newHypernymChain = new LinkedList<Synset>();
					newHypernymChain.add(pointer.getTargetSynset());
					newHypernymChain.addAll(currentHypernymChain);
					nextLevelSynsetQueue.add(newHypernymChain);
				}
			}
			
			minDepth++;
			synsetQueue = nextLevelSynsetQueue;
		}
	}
	
	private static POS getPOS(String tag)
	{
		if(tag.equals("NNP") || tag.equals("NNS") || tag.equals("NN") || tag.equals("NNPS"))
		{
			return POS.NOUN;
		}
		else if(tag.equals("JJ") || tag.equals("JJS") || tag.equals("JJR"))
		{
			return POS.ADJECTIVE;
		}
		else if(tag.equals("MD") || tag.equals("VB") || tag.equals("VBZ") || tag.equals("VBG") || tag.equals("VBD") || tag.equals("VBN") || tag.equals("VBP"))
		{
			return POS.VERB;
		}
		else if(tag.equals("RB") || tag.equals("RBR") || tag.equals("WRB") || tag.equals("RBS"))
		{
			return POS.ADVERB;
		}
		else
		{
			return null;
		}
	}
	
//	public static void sample()
//	{try{
//		getDepthAndHypernymChain(wordNetDictionary.getIndexWord(POS.NOUN, "glamour").getSenses().get(0), null);}catch(Exception ex){}
//	}
	
//	public static void printRoot()
//	{
//		List<Synset> s = null;
//		try {
//			s = wordNetDictionary.getIndexWord(POS.VERB, "move").getSenses();
//		} catch (JWNLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int i = 0;
//		for(Synset q : s)
//		{int j=0;
//			try
//			{
//				while(true)
//				{
//					System.out.println(q + " : " + j++);
//					List<Pointer> pointers = q.getPointers(PointerType.HYPERNYM);
//					if(pointers.size() == 0)
//					{
//						break;
//					}
//					
//					q = pointers.get(0).getTargetSynset();
//				}
//			}
//			catch(Exception ex)
//			{
//				System.out.println("here");
//			}
////		{System.out.println("here:"+ex);
////			List<Pointer> p = s.getPointers(null);System.out.println(p.size());
////			for(Pointer po : p)
////			{
////				System.out.println(po.getTarget().toString());
////			}
//		}
//		try {
//			System.out.println(wordNetDictionary.getSynsetAt(POS.VERB, 1740));
//		} catch (JWNLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.exit(0);
//	}
}