package org.insight.word2vec.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import org.insight.word2vec.Word2Vec;

import com.ochafik.util.string.StringUtils;


public class GloveTextModelLoader {

	public static Word2Vec load (String word2vecModel) {

		Word2Vec model = new Word2Vec();

		try {

			//InputStream fileStream = new FileInputStream(Parameters.outputDirectory() + Parameters.getModelName(param.withModel("stream"), windowEndDate) + ".gz");
			//InputStream gzipStream = new GZIPInputStream(fileStream);
			//Reader decoder = new InputStreamReader(gzipStream, "UTF-8");

			InputStream fileStream = new FileInputStream(word2vecModel);

			System.out.println("Loading GloVe: " + word2vecModel);


			Reader decoder = new InputStreamReader(fileStream, "UTF-8");

			BufferedReader buffered = new BufferedReader(decoder);

			double numWords = 0;
			String line;			

			while ((line = buffered.readLine()) != null) {
				// Split into words:
				String[] wordvec = line.split(" ");

				if (wordvec.length < 2) { break; }
				//System.err.println(line);

				model.put(wordvec[0], readFloatVector(wordvec, wordvec.length-1));

				//System.err.println(wordvec[0] + Arrays.toString(readFloatVector(wordvec, wordvec.length-1)));



				numWords++;

			} // Over entire file
			buffered.close();
			fileStream.close();

			System.out.println( numWords + " Words loaded. ");

		} catch (IOException e) {
			System.err.println("ERROR: Failed to load model: " + word2vecModel);
			e.printStackTrace();
		}

		return model;
	}



	/*
	 * Read a Vector - Array of Floats from the binary model:
	 */
	private static float[] readFloatVector(String[] line, int vectorSize) throws IOException {
		// Vector is an Array of Floats...

		float[] vector = new float[vectorSize];

		// Vector:
		for (int j=1; j < vectorSize; j++) {
			try {
				double d = Double.parseDouble(line[j]);
				vector[j-1] = (float)d;
			} catch (NumberFormatException e) {

				System.out.println(vectorSize + ":" + Arrays.toString(line));
				System.out.println("ERROR:" + e.getMessage());

				vector[j-1] = 0.0f;
			}
		}
		return vector;
	}

}


