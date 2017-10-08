package com.emotibot.srl.server;

import static com.emotibot.srl.server.Constants.chinese_srl_port;
import static com.emotibot.srl.server.Constants.english_srl_port;
import static com.emotibot.srl.server.Constants.host;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.emotibot.srl.tmr.datastructure.SRL;
import com.google.gson.Gson;
public class SRLSocketClient {


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				SRLSocketClient sr=new SRLSocketClient();
				
//				String doc="I want to drink coffee tomorrow because i am tired.";
//				String result=sr.semanticParsingResultForEnglish(doc);
				//System.err.println(result);
				
			
				String doc2="哦哦哦哦哦";	
				String result=sr.semanticParsingResultForChinese(doc2);
				System.err.println(result);
				
				
				Gson gson=new Gson();
				SRL srl=gson.fromJson(result, SRL.class);
				System.out.println(srl);
	
	}

	/**
	 * Function to obtain SRL result from semantic role labeler running as socket server
	 */
	public String semanticParsingResultForEnglish(String sentence) {
		String result = "";
		// System.err.println(sentence);
		// see if a parser socket server is available
		
		
		Socket client;
		PrintWriter pw;
		BufferedReader br;
		String line;

		String parse = "";
		try {
			client = new Socket(host,english_srl_port);

			pw = new PrintWriter(new OutputStreamWriter(
					client.getOutputStream(), StandardCharsets.UTF_8),true);
			br = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			pw.println(sentence);
			pw.flush(); // flush to complete the transmission

			/**
			 * 1)Removed the ready method. It was giving issues 2)Removed the
			 * else condition and parseScore method
			 */
			while ((line = br.readLine()) != null) {
				
				result += line+"\n" ;

			}

			br.close();
			pw.close();
			client.close();

			if (parse == null) {
				parse = "NAN";
			} else {
				parse = result;
			}

			return parse;

		} catch (Exception ex) {

			System.err.println("Could not connect to parser server.");
			// ex.printStackTrace();
		}

		System.err.println("parsing:" + sentence);

		return null;
	}
	/**
	 * Function to obtain SRL result from semantic role labeler running as socket server
	 */
	public String semanticParsingResultForChinese(String sentence) {
		String result = "";
		
		Socket client;
		PrintWriter pw;
		BufferedReader br;
		String line;

		String parse = "";
		try {
			client = new Socket(host, chinese_srl_port);

			
			pw = new PrintWriter(new OutputStreamWriter(
					client.getOutputStream(), StandardCharsets.UTF_8),true);
			
			 br = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
			
			pw.println(sentence);
			pw.flush(); // flush to complete the transmission

			/**
			 * 1)Removed the ready method. It was giving issues 2)Removed the
			 * else condition and parseScore method
			 */
			while ((line = br.readLine()) != null) {
				
				result += line+"\n" ;

			}

			br.close();
			pw.close();
			client.close();

			if (parse == null) {
				parse = "NAN";
			} else {
				parse = result;
			}

			return parse;

		} catch (Exception ex) {

			System.err.println("Could not connect to parser server.");
			// ex.printStackTrace();
		}

		System.err.println("parsing:" + sentence);

		return null;
	}
	
	
	/**
	 * Function to obtain SRL result from semantic role labeler running as socket server
	 */
	public String semanticParsingResultwithTableForChinese(String sentence) {
		String result = "";
		
		Socket client;
		PrintWriter pw;
		BufferedReader br;
		String line;

		String parse = "";
		try {
			client = new Socket(host, chinese_srl_port);

			
			pw = new PrintWriter(new OutputStreamWriter(
					client.getOutputStream(), StandardCharsets.UTF_8),true);
			
			 br = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
			
			pw.println(sentence);
			pw.flush(); // flush to complete the transmission

			/**
			 * 1)Removed the ready method. It was giving issues 2)Removed the
			 * else condition and parseScore method
			 */
			while ((line = br.readLine()) != null) {
				
				result += line+"\n" ;

			}

			br.close();
			pw.close();
			client.close();

			if (parse == null) {
				parse = "NAN";
			} else {
				parse = result;
			}

			return parse;

		} catch (Exception ex) {

			System.err.println("Could not connect to parser server.");
			// ex.printStackTrace();
		}

		System.err.println("parsing:" + sentence);

		return null;
	}
	
	
	

}
