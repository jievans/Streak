import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.*;
import java.util.regex.*;


public class Streaming{


	public static double frameRate(String clipPath){

		String playerOutput = "";

		ProcessBuilder builder = new ProcessBuilder("ffmpeg","-i",clipPath);

		try{
			
			Process process = builder.start();
			process.waitFor();
			
			InputStreamReader outputReader = new InputStreamReader(process.getErrorStream());
			BufferedReader bufferReader = new BufferedReader(outputReader);

			String line;

			while( (line = bufferReader.readLine()) != null){
				playerOutput=playerOutput + line;
			}
		
		} catch(IOException e){

			System.out.println("There was a problem in starting the process.");

		} catch(InterruptedException e){
			System.out.println("There was an interrupted exception");
		}

		
		Pattern fpsPattern = Pattern.compile("(?<=\\s)[0-9.]+(?= fps)");
		Matcher fpsMatcher = fpsPattern.matcher(playerOutput);


		double frameRate = 24;
		
		if(fpsMatcher.find()){
			frameRate = Double.parseDouble(fpsMatcher.group());
		} else{
			System.out.println("The source clip framerate was unable to be determined.  Defaulting to 24 frames per second.");
		}
		
		return frameRate;



	}



}