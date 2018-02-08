package com.pyrsoftware;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This is the main class for the Omaha game
 * @author Quan Meng
 *
 */
public class Omaha {
	private String inputFile;
	private String outputFile;
	
	
	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public Omaha(String inputFile, String outputFile){
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}
	
	public void startProcessing(){
		try {
			String result = "";
			File inputF = new File(this.getInputFile());
			Scanner scanner = new Scanner(inputF);
			while(scanner.hasNext()){
				String line = scanner.nextLine();
				String[] splitLine = line.split(" ");
				String handA = splitLine[0].split(":")[1];
				String handB = splitLine[1].split(":")[1];
				String board = splitLine[2].split(":")[1];
				result += "HandA:" + handA + " HandB:" + handB + " Board:" + board + "\n";
				OneCombination combA = new OneCombination(handA, board);
				combA.calculateTopRankCards();
				OneCombination combB = new OneCombination(handB, board);
				combB.calculateTopRankCards();
				result += "BestA:" + combA.outputCardChain() + " (" + combA.getHighRankHandTypeS() + ") BestB:" + combB.outputCardChain() + " (" + combB.getHighRankHandTypeS() + ")\n";				
				int compared = combA.compareTo(combB);
				result += "=>";
				if(compared == -1){
					result += "HandB wins (" + combB.getHighRankHandTypeS() + ")";  
				}else if(compared == 1){
					result += "HandA wins (" + combA.getHighRankHandTypeS() + ")";
				}else{
					result += "Split Pot (" + combA.getHighRankHandTypeS() + ")";
				}
				result += "\n\n";
			}
			scanner.close();
			// Output
			File outputF = new File(this.getOutputFile());
			if(!outputF.exists()){
				outputF.createNewFile();
			}
			PrintWriter printer = new PrintWriter(this.getOutputFile());
			printer.print(result.trim());
			printer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		Omaha game = new Omaha(args[0], args[1]);
		game.startProcessing();
	}
}
