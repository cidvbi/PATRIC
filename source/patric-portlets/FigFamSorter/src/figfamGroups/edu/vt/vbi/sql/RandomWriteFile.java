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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Provides a means of buffered writing to a random access file
 * @author mscott
 * 
 */
public class RandomWriteFile {
	public final static int BYTES_PER_LONG = 8;

	public final static int BYTES_PER_INT = 4;

	public final static int CHOP_TO_BYTE = 0xFF;

	public final static int BITS_PER_BYTE = 8;

	private byte[] buffer = null;

	private int top = 0;

	private long baseAt = 0;

	private byte[] singleByte = new byte[1];

	private RandomAccessFile baseFile;

	private File bottomFile;

	private byte[] longBuffer = new byte[BYTES_PER_LONG];

	public RandomWriteFile(File file, int byteRoom) throws FileNotFoundException {
		bottomFile = file;
		baseFile = new RandomAccessFile(file, "rw");
		buffer = new byte[byteRoom];
	}

	public RandomWriteFile(File file) throws IOException {
		bottomFile = file;
		baseFile = new RandomAccessFile(file, "rw");
		buffer = new byte[RandomReadFile.DEFAULT_BUFFER_SIZE];
	}

	public final static void setLongAsBytes(long value, int byteRoom, byte[] storeIn, int storeAt) {
		for (int i = byteRoom - 1; i >= 0; i--) {
			storeIn[storeAt + i] = (byte) (value & CHOP_TO_BYTE);
			value >>= BITS_PER_BYTE;
		}
	}

	public final static void setIntBytes(int value, byte[] storeIn, int storeAt) {
		storeIn[storeAt + BYTES_PER_INT - 1] = (byte) (value & CHOP_TO_BYTE);
		value >>= BITS_PER_BYTE;
		storeIn[storeAt + BYTES_PER_INT - 2] = (byte) (value & CHOP_TO_BYTE);
		value >>= BITS_PER_BYTE;
		storeIn[storeAt + BYTES_PER_INT - 3] = (byte) (value & CHOP_TO_BYTE);
		value >>= BITS_PER_BYTE;
		storeIn[storeAt] = (byte) (value & CHOP_TO_BYTE);
	}

	public void empty() throws IOException {
		baseFile.setLength(0);
	}

	public long getFilePointer() throws IOException {
		return (baseFile.getFilePointer());
	}

	public String getFileName() {
		return bottomFile.getName();
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		int free = buffer.length - top;

		while (free <= count) {
			System.arraycopy(bytes, offset, buffer, top, free);
			baseFile.write(buffer);
			baseAt += buffer.length;
			top = 0;
			offset += free;
			count -= free;
			free = buffer.length;
		}
		if (count > 0) {
			System.arraycopy(bytes, offset, buffer, top, count);
			top += count;
		}
	}

	public void write(byte value) throws IOException {
		singleByte[0] = value;
		write(singleByte, 0, 1);
	}

	public void write(byte[] bytes) throws IOException {
		write(bytes, 0, bytes.length);
	}

	public long getTailPosition() {
		return (baseAt + top);
	}

	public void setTailPosition(long tailAt) throws IOException {
		if (tailAt != baseAt + top) {
			if (top > 0) {
				baseFile.write(buffer, 0, top);
			}
			baseFile.seek(tailAt);
			top = 0;
			baseAt = tailAt;
		}
	}

	public void writeLong(long value) throws IOException {
		setLongAsBytes(value, BYTES_PER_LONG, longBuffer, 0);
		write(longBuffer);
	}

	public void writeInt(int value) throws IOException {
		setIntBytes(value, longBuffer, 0);
		write(longBuffer, 0, BYTES_PER_INT);
	}

	public void writeLongs(long[] longs, int offset, int count) throws IOException {
		for (int i = 0; i < count; i++) {
			writeLong(longs[offset + i]);
		}
	}

	public boolean writeAt(long place, byte[] toWrite) throws IOException {
		long writeEnd = place + toWrite.length;
		boolean result = (writeEnd <= baseAt + top);
		if (result) {
			if (baseAt <= place) {
				System.arraycopy(toWrite, 0, buffer, (int) (place - baseAt), toWrite.length);
			}
			else {
				baseFile.seek(place);
				if (writeEnd <= baseAt) {
					baseFile.write(toWrite);
					baseFile.seek(baseAt);
				}
				else {
					int chop = (int) (baseAt - place);
					baseFile.write(toWrite, 0, chop);
					System.arraycopy(toWrite, chop, buffer, 0, (int) (writeEnd - baseAt));
				}
			}
		}
		return result;
	}

	public void writeLongAt(long place, long value) throws IOException {
		int longTop = (int) (place + BYTES_PER_LONG - baseAt);
		if (longTop <= 0) {
			baseFile.seek(place);
			baseFile.writeLong(value);
			baseFile.seek(baseAt);
		}
		else {
			setLongAsBytes(value, BYTES_PER_LONG, longBuffer, 0);
			int longAt = (int) (place - baseAt);
			if (longAt < 0) {
				baseFile.seek(place);
				int chop = (int) (baseAt - place);
				baseFile.write(longBuffer, 0, chop);
				System.arraycopy(longBuffer, chop, buffer, 0, longTop);
				top = Math.max(top, longTop);
				baseFile.seek(baseAt);
			}
			else {
				if (longTop <= top) {
					System.arraycopy(longBuffer, 0, buffer, longAt, BYTES_PER_LONG);
				}
				else {
					top = longAt;
					write(longBuffer, 0, BYTES_PER_LONG);
				}
			}
		}
	}

	public void close() throws IOException {
		if (top > 0) {
			baseFile.write(buffer, 0, top);
			baseAt += top;
			top = 0;
		}
		baseFile.close();
	}

	public void flush() throws IOException {
		close();
		baseFile = new RandomAccessFile(bottomFile, "rw");
		baseFile.seek(baseAt);
	}

	public long getLength() throws IOException {
		return (baseFile.length());
	}

	public void writeString(String toWrite) throws IOException {
		int charCount = toWrite.length();
		byte[] toBytes = new byte[2 * charCount + 2];
		for (int i = 0; i < charCount; i++) {
			int toInt = toWrite.charAt(i);
			toBytes[2 * i] = (byte) (toInt >> BITS_PER_BYTE);
			toBytes[2 * i + 1] = (byte) (toInt & CHOP_TO_BYTE);
		}
		write(toBytes);
	}

}
