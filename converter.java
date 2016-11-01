/*#
 # SRT_TO_DFXP Converter java JAR
 # Project: NLM Digital Repository
 # Author: Calvin XU
 # Date: 8/12/16
 #
 # To run: change directory to the jar containing folder and run
 # java -jar SRT_TO_DFXP.jar <input_file> <output_file> [-d]
 # Note: please include full output .extension. ex. sample.dfxp.xml 
 #
 # Description:
 # 	This jar java executable jar will convert the srt file to dfxp format,
 #  elements within the converted dfxp file was set to existing dfxp.xml file as
 #  reference.
 #
 #
 #
 # Update history:
 # 9/11/16: took out <br /> tag line

 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SRT_TO_DFXP_Converter {
	File input_file;
	File output_file;
	ArrayList<CaptionLine> node_list;
	String error = null;
	String styleXMLString;

	private final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tt xml:lang=\"en\" xmlns=\"http://www.w3.org/2006/04/ttaf1\"  xmlns:tts=\"http://www.w3.org/2006/04/ttaf1#styling\">\n";
	private BufferedReader bis;

	public SRT_TO_DFXP_Converter(File input_file, File output_file) {
		this.input_file = input_file;
		this.output_file = output_file;
		this.node_list = new ArrayList<CaptionLine>();
	}

	public SRT_TO_DFXP_Converter(File input_file, File output_file,
			String styleXMLString) {
		this.input_file = input_file;
		this.output_file = output_file;
		this.styleXMLString = styleXMLString;
		this.node_list = new ArrayList<CaptionLine>();
	}

	class CaptionLine {
		int line_num;
		String begin_time;
		String end_time;
		ArrayList<String> content;

		public CaptionLine(int line_num, String begin_time, String end_time,
				ArrayList<String> content) {
			this.line_num = line_num;
			this.end_time = end_time;
			this.begin_time = begin_time;
			this.content = content;
		}

		public String toString() {
			return (line_num + ": " + begin_time + " --> " + end_time + "\n" + content);
		}
	}

	private ArrayList<CaptionLine> readSRT() {
		FileReader fis = null;
		try {
			fis = new FileReader(input_file);
		} catch (FileNotFoundException e) {
			System.out.println("Input File Not Found");
			error = "Input File Not Found";
		}
		bis = new BufferedReader(fis);
		String line = null;
		boolean line1 = true;
		try {
			do {
				CaptionLine node;
				line = bis.readLine();

				Integer line_num;
				if (line1) {
					line_num = Integer.valueOf(line.charAt(line.length() - 1));
					line1 = false;
				} else {
					try {
						Integer.valueOf(line);
					} catch (NumberFormatException e) {
						error = "numformat";
						break;
					}
					line_num = Integer.valueOf(line);
				}
				line = bis.readLine();
				String[] time_split = line.split(" --> ");
				String begin_time = time_split[0];
				begin_time = begin_time.replace(',', '.');
				String end_time = time_split[1];
				end_time = end_time.replace(',', '.');
				ArrayList<String> content = new ArrayList<String>();

				while (((line = bis.readLine()) != null)
						&& (!(line.trim().equals("")))) {
					content.add(line);
				}
				node = new CaptionLine(line_num, begin_time, end_time, content);
				node_list.add(node);

			} while (line != null);
		} catch (Exception e) {
			System.out.println("Incorrectly Formatted SubScript File\n");
			error = "Incorrectly Formatted SubScript File";
			return null;
		}
		// bis.close();
		return node_list;
	}

	private String convertToXML() {
		String dfxp = "";
		dfxp += header;
		dfxp += "     <head>\n";
		dfxp += "          <styling>\n";
		if (styleXMLString != null) {
			dfxp += "              " + styleXMLString;
		} else {
			dfxp += "              <style id=\"1\" tts:backgroundColor=\"black\"  tts:fontFamily=\"Arial\" tts:fontSize=\"14\" tts:color=\"white\" tts:textAlign=\"center\" tts:fontStyle=\"Plain\" />\n";
		}
		dfxp += "          </styling>\n";
		dfxp += "     </head>\n";
		dfxp += "     <body>\n";
		if (styleXMLString != null) {
			dfxp += "          <div xml:lang=\"en\" style=\"default\">\n";
		} else {
			dfxp += "          <div xml:lang=\"en\">\n";
		}
		for (int i = 0; i < node_list.size(); i++) {
			dfxp += "               <p begin=\"" + node_list.get(i).begin_time
					+ "\" ";
			dfxp += "end=\"" + node_list.get(i).end_time
					+ "\" style=\"1\">\n";
			for (int k = 0; k < node_list.get(i).content.size(); k++) {
				dfxp += "                    "
						+ node_list.get(i).content.get(k);
				if ((node_list.get(i).content.size() > 1)
						&& (k != node_list.get(i).content.size() - 1)) {
					dfxp += "<br />\n";
				} 
			}
			dfxp += "               </p>\n";
		}
		dfxp += "          </div>\n";
		dfxp += "     </body>\n";
		dfxp += "</tt>\n";
		return dfxp;
	}

	private void writeXML(String dfxp) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(output_file));
			out.write(dfxp);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			error = "Error Writing To File";
			System.out.println("Error Writing To File\n");
			System.exit(0);
		}
	}

	public void doAll() {
		try {
			node_list = readSRT();
			writeXML(convertToXML());
		} catch (Exception e) {
			error = "Incorrectly Formatted SubScript File";
		}
	}

	public static void main(String[] args) {
		if ((args.length < 2) || (args[1].equals("-h"))) {
			System.out.println("\n<---   SRT to DFXP Converter Usage   --->");
			System.out
					.println("Conversion: java -jar SRT_TO_DFXP.jar <input_file> <output_file> [-d]");
			System.out
					.println("Conversion REQUIRES a input file and output file");
			System.out.println("[-d] Will Display XML Generated In Console");
			System.out.println("Help: java -jar SRT_TO_DFXP.jar -h");
		} else if (!(new File(args[0]).exists())) {
			System.out.println("Error: Input SubScript File Does Not Exist\n");
		} else {
			// SRT_TO_DFXP converter=new SRT_TO_DFXP(args[0], args[1]);
			SRT_TO_DFXP_Converter converter = new SRT_TO_DFXP_Converter(
					new File(args[0]), new File(args[1]));
			converter.node_list = converter.readSRT();
			String dfxp = converter.convertToXML();
			if ((args.length == 3) && (args[2].equals("-d")))
				System.out.println("\n" + dfxp + "\n");
			converter.writeXML(dfxp);
			System.out.println("Conversion Complete");
		}
	}
}
