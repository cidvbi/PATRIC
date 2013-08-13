/*******************************************************************************
 * Copyright 2013 Virginia Polytechnic Institute and State University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package figfamGroups.edu.vt.vbi.sql;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class that supports buffered reading from a binary file
 * @author mscott
 * 
 */

public class RandomReadFile {
	public final static int DEFAULT_BUFFER_SIZE = 0x10000;

	private byte[] buffer = null;

	private int top = 0;

	private int readAt = 0;

	private RandomAccessFile baseFile;

	private long position = 0;

	public RandomReadFile(File file, int byteRoom) throws IOException {
		baseFile = new RandomAccessFile(file, "r");
		buffer = new byte[byteRoom];
		top = baseFile.read(buffer);
	}

	public RandomReadFile(File file) throws IOException {
		baseFile = new RandomAccessFile(file, "r");
		buffer = new byte[DEFAULT_BUFFER_SIZE];
		top = baseFile.read(buffer);
	}

	public byte readByte() throws IOException {
		byte result = 0;

		// attempt to get byte from top of buffer
		if (readAt < top) {
			result = buffer[readAt];
			++position;
			readAt++;
			// if buffer is exhausted try to refill
			if (readAt >= buffer.length) {
				top = baseFile.read(buffer);
				readAt = 0;
			}
		}
		return result;
	}

	public int readInt() throws IOException {
		int result = 0;
		if (readAt < top) {
			result = (int) readByte();
			for (int i = 1; i < RandomWriteFile.BYTES_PER_INT; i++) {
				// create room for next byte
				result = result << RandomWriteFile.BITS_PER_BYTE;
				// insert next byte in result
				// the 0xFF chops off possible sign extension bits
				result |= (RandomWriteFile.CHOP_TO_BYTE & ((int) readByte()));
			}
		}
		return result;
	}

	public long readLong() throws IOException {
		long result = 0;
		if (readAt < top) {
			result = (long) readByte();
			for (int i = 1; i < RandomWriteFile.BYTES_PER_LONG; i++) {
				// create room for next byte
				result = result << RandomWriteFile.BITS_PER_BYTE;
				// insert next byte in result
				// the 0xFF chops off possible sign extension bits
				result |= (RandomWriteFile.CHOP_TO_BYTE & ((long) readByte()));
			}
		}
		return result;
	}

	public void seek(long goTo) throws IOException {
		baseFile.seek(goTo);
		position = goTo;
		top = baseFile.read(buffer);
		readAt = 0;
	}

	public long length() throws IOException {
		return (baseFile.length());
	}

	public void close() throws IOException {
		buffer = null;
		baseFile.close();
	}

	public boolean hasMore() throws IOException {
		return (readAt < top);
	}

	public void toStart() throws IOException {
		baseFile.seek(0);
		position = 0;
		top = baseFile.read(buffer);
		readAt = 0;
	}

	public long getPosition() {
		return position;
	}

	public int read(byte[] toFill) throws IOException {
		int result = 0;
		for (result = 0; (result < toFill.length) && (readAt < top); result++) {
			toFill[result] = readByte();
		}
		return result;
	}

	public int read(byte[] toFill, int offset, int readCount) throws IOException {
		int result = 0;
		for (result = 0; (result < readCount) && (readAt < top); result++) {
			toFill[offset + result] = readByte();
		}
		return result;
	}

	public String readString() throws IOException {
		String result = "";
		byte[] charBytes = new byte[2];
		read(charBytes);
		int intGet = (charBytes[0] & RandomWriteFile.CHOP_TO_BYTE);
		intGet <<= RandomWriteFile.BITS_PER_BYTE;
		intGet |= (charBytes[1] & RandomWriteFile.CHOP_TO_BYTE);
		while (intGet != 0) {
			result += (char) (intGet);
			read(charBytes);
			intGet = (charBytes[0] & RandomWriteFile.CHOP_TO_BYTE);
			intGet <<= RandomWriteFile.BITS_PER_BYTE;
			intGet |= (charBytes[1] & RandomWriteFile.CHOP_TO_BYTE);
		}
		return result;
	}

	public String readString(int maxLength) throws IOException {
		String result = "";
		byte[] charBytes = new byte[2];
		read(charBytes);
		int intGet = (charBytes[0] & RandomWriteFile.CHOP_TO_BYTE);
		intGet <<= RandomWriteFile.BITS_PER_BYTE;
		intGet |= (charBytes[1] & RandomWriteFile.CHOP_TO_BYTE);
		while ((intGet != 0) && (maxLength >= 0)) {
			result += (char) (intGet);
			read(charBytes);
			intGet = (charBytes[0] & RandomWriteFile.CHOP_TO_BYTE);
			intGet <<= RandomWriteFile.BITS_PER_BYTE;
			intGet |= (charBytes[1] & RandomWriteFile.CHOP_TO_BYTE);
			--maxLength;
		}
		if (intGet != 0) {
			result = null;
		}
		return result;
	}

}
