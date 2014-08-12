package org.insight.word2vec;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.insight.word2vec.util.ModelLoader;
import org.insight.word2vec.util.VectorMath;
import org.insight.word2vec.util.WordSim;


/*
 * A Java wrapper for Word2Vec - Reads a pre trained model from disk, doesn't load into memory!
 */
public class DiskWord2Vec extends Word2Vec {

	public Word2Vec cache = new Word2Vec(); 

	private static final long serialVersionUID = 1L;

	private final String word2vecModel;

	public DiskWord2Vec(String model) {
		this.word2vecModel= model;
	}

	public static DiskWord2Vec normalizeModel(DiskWord2Vec raw) {
		return raw;
	}

	/*
	 * Check if word is in vocab:
	 */
	public boolean contains(String word) {

		if (cache.contains(word)) {
			return true;
		} else {

			if (vector(word) != null) {
				return true;
			} else {
				return false;
			}

		}

	}

	/*
	 * Get Vector Representation of a word
	 */
	public float[] vector(String searchWord) {

		boolean found = false;
		
		if (cache.contains(searchWord)) {
			return cache.vector(searchWord);
		}
		
		try {

			BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(word2vecModel), 131072); // 128KB Buffer
			DataInputStream ds = new DataInputStream(bufIn);

			// Read header:
			int numWords = Integer.parseInt(ModelLoader.readString(ds));
			int vecSize = Integer.parseInt(ModelLoader.readString(ds));

			
			for (int i=0; i < numWords; i++) {
				// Word:
				String word = ModelLoader.readString(ds);
				// Vector:
				float[] vector = ModelLoader.readFloatVector(ds, vecSize);

				if (word.equalsIgnoreCase(searchWord)) {

					cache.put(word, vector);
					found = true;
					break;

				}


				// Progress bar...
				if (i % 10000 == 0) {
					System.err.print(".");
				}
				if (i % 100000 == 0) {
					System.err.println("");
				}

			}

			ds.close();
			bufIn.close();

			//System.out.println(numWords + " Words with Vectors of size " + vecSize + " LOADED!");

		} catch (IOException e) {
			System.err.println("ERROR: Failed to load model: " + word2vecModel);
			e.printStackTrace();
		}

		if (found) {
			return cache.vector(searchWord);
		} else {
			return null;
		}
	}


}



