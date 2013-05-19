package pl.edu.pw.elka.mmarkiew.tkom;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Utilities {

	/**
	 * Read file into string variable
	 * 
	 * @param path
	 *            Path to file
	 * 
	 * @return Stringified file
	 */
	@SuppressWarnings("resource")
	public static String readFileToString(String path) {
		try {
			FileChannel fch = new FileInputStream(path).getChannel();

			ByteBuffer byteBuff;
			byteBuff = fch.map(FileChannel.MapMode.READ_ONLY, 0, fch.size());

			CharBuffer chBuff = Charset.defaultCharset().decode(byteBuff);
			// CharBuffer chBuff = Charset.forName("UTF-8").decode(byteBuff);

			fch.close();

			return chBuff.toString();
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Write string to file
	 * 
	 * @param path
	 *            Path to file
	 * 
	 * @param text
	 *            String to write
	 * 
	 * @throws IOException
	 *             If IO exception occurred
	 */
	public static void writeToFile(String path, String text) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		out.write(text);
		out.close();
	}

	/**
	 * Generate tab indent
	 * 
	 * @param i
	 *            How many tabs wanted to generate
	 * 
	 * @return String with proper amount of tabs
	 */
	public static String genTabs(int i) {
		StringBuilder str = new StringBuilder();

		for (int j = 0; j < i; ++j)
			str.append("\t");

		return str.toString();
	}

}
