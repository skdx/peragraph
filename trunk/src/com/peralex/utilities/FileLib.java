package com.peralex.utilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Noel Grandin
 */
public final class FileLib
{
	/** not meant to be instantiated */
	private FileLib() {}
	
	/**
	 * Replaces all the illegal characters in the name of the file with "_" .
	 * ie. \ / : * ? " < > |
	 *
	 * @param sFileName name to format. Do not supply full path.
	 * @return Formatted file name.
	 */
	public static String cleanupFileName(String sFileName)
	{
		sFileName = sFileName.replace('\\', '_');
		sFileName = sFileName.replace('/', '_');
		sFileName = sFileName.replace(':', '_');
		sFileName = sFileName.replace('*', '_');
		sFileName = sFileName.replace('?', '_');
		sFileName = sFileName.replace('"', '_');
		sFileName = sFileName.replace('<', '_');
		sFileName = sFileName.replace('>', '_');
		sFileName = sFileName.replace('|', '_');
		return sFileName;
	}

	/**
	 * reads entire file into a byte array
	 */
	public static byte [] readFileFully(String fileName) throws IOException
	{
		return readFileFully(new File(fileName));
	}
	
	/**
	 * reads entire file into a byte array
	 */
	public static byte [] readFileFully(File oFile) throws IOException
	{
		final FileInputStream fisInput = new FileInputStream(oFile);
		try {
			final BufferedInputStream bufferedInput = new BufferedInputStream(fisInput);
			try {
				final byte [] ayData = new byte[(int)oFile.length()];
				
				final int lengthRead = bufferedInput.read(ayData);
				if (lengthRead!=ayData.length) {
					throw new IllegalArgumentException(ayData.length + "!=" + lengthRead);
				}
				
				return ayData;
			} finally {
				bufferedInput.close();
			}
		} finally {
			fisInput.close();
		}
	}
	
	/**
	 * reads an entire stream into a byte array
	 */
	public static byte [] readStreamFully(InputStream in) throws IOException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte [] data = new byte[512];
		do {
			int len = in.read(data);
			if (len==-1) break;
			out.write(data, 0, len);
		} while (true);
		
		return out.toByteArray();
	}
	
	/**
	 * utility method to read a required number of bytes from a stream or fail.
	 * Necessary because some streams sometimes do partial reads.
	 */
	public static byte [] readBytes(InputStream in, int cnt) throws IOException
	{
		final byte [] buf = new byte[cnt];
		int idx = 0;
		while (cnt>0) {
			final int x = in.read(buf, idx, cnt);
			if (x==-1) throw new EOFException();
			cnt -= x;
			idx += x;
		}
		return buf;
	}
	
}
