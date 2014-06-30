import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TfIdf
{
	//Collection statistics
	public static Map<String, Integer> collectionTermFrequency;
	public static Map<String, Integer> documentFrequency;
	public static Map<String, Double> idf;
	
	public static int numDocuments = 94;
	
	private static String serializationPath = "E:\\8thSemProject\\textToPpt\\Corpus\\";
	
	//Document statistics
	public static Map<String, Integer> documentTermFrequency;
	
	public static void PreprocessPDFAndCalculateIdf(String path)
	{
		Preprocess.getWordsFromPDFs(path, true);
		CalculateIdf();
	}
	
	public static void PreprocessTxtFileAndCalculateIdf()
	{
		Preprocess.getWordsFromTextFile();
		CalculateIdf();
	}
	
	public static void CalculateIdf()
	{
		try
		{
			idf = new HashMap<String, Double>();
			for(String key : collectionTermFrequency.keySet())
			{
				double idfValue = Math.log(numDocuments / documentFrequency.get(key));
				idf.put(key, idfValue);
			}
		}
		catch(NullPointerException ex)
		{
			System.out.println(ex);
		}
	}
	
	public static void CalculateIdf(Set<String> words)
	{
		deserializeMaps();
		
		try
		{
			idf = new HashMap<String, Double>();
			for(String key : words)
			{
				double idfValue = Math.log(TfIdf.numDocuments / TfIdf.documentFrequency.get(key));
				idf.put(key, idfValue);
			}
		}
		catch(NullPointerException ex)
		{
			System.out.println(ex);
		}
	}
	
	public static void CalculateDocumentTermFrequency(List<List<String>> sentencesList)
	{
		documentTermFrequency = new HashMap<String, Integer>();
		
		Iterator<List<String>> sentencesIterator = sentencesList.iterator();
		while(sentencesIterator.hasNext())
		{
			List<String> sentence = sentencesIterator.next();
			Iterator<String> wordsIterator = sentence.iterator();
			while(wordsIterator.hasNext())
			{
				String word = wordsIterator.next();
				if(documentTermFrequency.containsKey(word))
				{
					documentTermFrequency.put(word, documentTermFrequency.get(word) + 1);
				}
				else
				{
					documentTermFrequency.put(word, 1);
				}
			}
		}
	}
	
	public static void serializeMaps(String lineNo)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(serializationPath + "collectionTermFrequency" + lineNo + ".count"));
			for(String key : collectionTermFrequency.keySet())
			{
				bw.write(key + " " + collectionTermFrequency.get(key) + "\n");
			}
			bw.flush();
			
			bw = new BufferedWriter(new FileWriter(serializationPath + "documentFrequency" + lineNo + ".count"));
			for(String key : documentFrequency.keySet())
			{
				bw.write(key + " " + documentFrequency.get(key) + "\n");
			}
			bw.flush();
			
			bw = new BufferedWriter(new FileWriter(serializationPath + "idf.count"));
			for(String key : documentFrequency.keySet())
			{
				bw.write(key + " " + documentFrequency.get(key) + "\n");
			}
			bw.flush();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public static void deserializeMaps()
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(serializationPath + "collectionTermFrequency.count")));
			String line = null;
			
			collectionTermFrequency = new HashMap<String, Integer>();
			while((line = br.readLine()) != null)
			{
				String[] lineParts = line.split(" ");
				if(lineParts.length == 2)
				{
					collectionTermFrequency.put(lineParts[0], Integer.parseInt(lineParts[1]));
				}
			}
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(serializationPath + "documentFrequency.count")));
			
			documentFrequency = new HashMap<String, Integer>();
			while((line = br.readLine()) != null)
			{
				String[] lineParts = line.split(" ");
				if(lineParts.length == 2)
				{
					documentFrequency.put(lineParts[0], Integer.parseInt(lineParts[1]));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void serializeIDFMap(String lineNo)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(serializationPath + "idf.count"));
			for(String key : documentFrequency.keySet())
			{
				bw.write(key + " " + documentFrequency.get(key) + "\n");
			}
			bw.flush();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public static void deserializeIDFMap()
	{
		BufferedReader br = null;
		try
		{			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(serializationPath + "idf.count")));
			String line = null;
			
			idf = new HashMap<String, Double>();
			while((line = br.readLine()) != null)
			{
				String[] lineParts = line.split(" ");
				if(lineParts.length == 2)
				{
					idf.put(lineParts[0], Double.parseDouble(lineParts[1]));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static Map<String, Integer> getWordFrequencies()
	{
		if(TfIdf.collectionTermFrequency != null)
		{
			return TfIdf.collectionTermFrequency;
		}
		
		return null;
	}
	
	public static Map<String, Integer> getDocumentFrequencies()
	{
		if(documentFrequency != null)
		{
			return documentFrequency;
		}
		
		return null;
	}
}