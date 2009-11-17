package net.sourceforge.synergy;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Handles the transmission of an object and contains related methods.
 */

public class SendObject {

	char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'B', 'C', 'D', 'E', 'F' };

	/**
	 * Converts a byte to its equivalent hex string.
	 * 
	 * @param b the byte to convert
	 * @return the hex equivalent
	 */
	private String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			
			/* look up high nibble char and fill left with zero bits */
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			
			/* look up low nibble char */
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	/**
	 * Converts a object to hex for transmission.
	 * 
	 * @param inputObject the object to convert
	 * @return the hex equivalent
	 */
	public String ObjecttoHex(Object inputObject) {

		try {
			ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream serializer = new ObjectOutputStream(
					memoryOutputStream);
			serializer.writeObject(inputObject);
			serializer.flush();

			return toHexString(memoryOutputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
