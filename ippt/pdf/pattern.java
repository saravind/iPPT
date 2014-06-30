package ippt.pdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pattern
{
	public static void main(String[] args)
	{
		Pattern tokenSeperator = Pattern.compile(".*([- .?]).*");
		String str = "aravind sucks-like a-turd.why is he like that?";
		Matcher tokenSeperatorMatcher = tokenSeperator.matcher(str);
		if(tokenSeperatorMatcher.matches())
		{
			//System.out.println(tokenSeperatorMatcher.group(1));
		}
		String word = "";
		
		for(int i=0 ; i<65536 ; i++)
		{
			System.out.println(i + " : " + ((char)i));
		}
	}
}
