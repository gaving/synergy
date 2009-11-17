package net.sourceforge.synergy;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Handles the receiving of an object and contains related methods.
 */

public class ReceiveObject {
	
	/**
	 * Converts a hex string to a byte array.
	 * 
	 * @param hexStr the hex to convert
	 * @return the byte array
	 */
	private byte[] toBinArray(String hexStr) {

		byte bArray[] = new byte[hexStr.length() / 2];

		for (int i = 0; i < (hexStr.length() / 2); i++) {

			byte firstNibble = Byte.parseByte(hexStr
					.substring(2 * i, 2 * i + 1), 16);
			byte secondNibble = Byte.parseByte(hexStr.substring(2 * i + 1,
					2 * i + 2), 16);

			/* bit-operations only with numbers, not bytes. */
			int finalByte = (secondNibble) | (firstNibble << 4);
			bArray[i] = (byte) finalByte;

		}
		return bArray;
	}
	
	/**
	 * Reconstructs transmitted hex into an object.
	 * 
	 * @param hexreceived the hex to convert
	 * @return the reconstructed object
	 */
	public Object ObjectfromHex(String hexreceived) {

		try {
			byte[] bts = toBinArray(hexreceived);

			ByteArrayInputStream memoryInputStream = new ByteArrayInputStream(
					bts);
			ObjectInputStream deserializer = new ObjectInputStream(
					memoryInputStream);

			return (Object) deserializer.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}