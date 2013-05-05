package pl.edu.pw.elka.mmarkiew.tkom;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Utilities {

	@SuppressWarnings("resource")
	public static String readFileToString(String path) {
		try {
			FileChannel fch = new FileInputStream(path).getChannel();

			ByteBuffer byteBuff;
			byteBuff = fch.map(FileChannel.MapMode.READ_ONLY, 0, fch.size());

			CharBuffer chBuff = Charset.defaultCharset().decode(byteBuff);
			// CharBuffer chBuff = Charset.forName("UTF-8").decode(byteBuff);

			return chBuff.toString();
		} catch (IOException e) {
			return "";
		}
	}

	public static String genTabs(int i) {
		StringBuilder str = new StringBuilder();
		
		for (int j = 0; j < i; ++j)
			str.append("\t");

		return str.toString();
	}
	
}
