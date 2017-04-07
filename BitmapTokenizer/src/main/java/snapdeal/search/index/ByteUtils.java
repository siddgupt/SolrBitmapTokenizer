package snapdeal.search.index;

import org.apache.commons.codec.binary.Base64;


public class ByteUtils {

	public static String encodeB64(byte[] data){
		return Base64.encodeBase64String(data);		
	}
	
	public static byte[] decodeB64(String data){
		return Base64.decodeBase64(data);
	}
	
	public static void getBytes(long[] data, byte[] destn, int start){
		if(destn == null || data == null 
			|| start < 0 || start > destn.length 
			|| destn.length - start < LONG_SZ * data.length)
			throw new IllegalArgumentException("Bad Destination and Source array or insufficient size");
		
		int pos = start;
		for(long l : data){
			destn[pos++] = (byte)(int)(l >>> 56);
			destn[pos++] = (byte)(int)(l >>> 48);
			destn[pos++] = (byte)(int)(l >>> 40);
			destn[pos++] = (byte)(int)(l >>> 32);
			destn[pos++] = (byte)(int)(l >>> 24);
			destn[pos++] = (byte)(int)(l >>> 16);
			destn[pos++] = (byte)(int)(l >>> 8);
			destn[pos++] = (byte)(int)(l >>> 0);
		}
	}
	
	public static void getBytes(int[] data, byte[] destn, int start){
		if(destn == null || data == null 
				|| start < 0 || start > destn.length 
				|| destn.length - start < INT_SZ * data.length)
				throw new IllegalArgumentException("Bad Destination and Source array or insufficient size");
			
			int pos = start;
			for(int l : data){
				
				destn[pos++] = (byte)(l >>> 24);
				destn[pos++] = (byte)(l >>> 16);
				destn[pos++] = (byte)(l >>> 8);
				destn[pos++] = (byte)(l >>> 0);
			}
	}
	
	public static long[] getLongs(byte[] data, int start, int lastpos){
		if(data == null || lastpos < 0 || lastpos > data.length 
			|| start < 0 || start >= lastpos)
				throw new IllegalArgumentException("Bad Destination and Source array or insufficient size");
		
		int numLongs = (lastpos - start) / LONG_SZ;
		long[] destn = new long[numLongs];
		
		for(int i = start, k = 0; i < start + LONG_SZ * numLongs; i += LONG_SZ, k++){
			/**
			 * By default Byte is casted to Int, so force a cast to Long for left shift to work on Long type
			 * Applying the Mask will ensure only the required bits are used irrespective of sign bit
			 */
			destn[k] = ((long)data[i] << 56) & MASK_LONG_0;
			destn[k] |= ((long)data[i+1] << 48) & MASK_LONG_1;
			destn[k] |= ((long)data[i+2] << 40) & MASK_LONG_2;
			destn[k] |= ((long)data[i+3] << 32) & MASK_LONG_3;
			destn[k] |= ((long)data[i+4] << 24) & MASK_LONG_4;
			destn[k] |= ((long)data[i+5] << 16) & MASK_LONG_5;
			destn[k] |= ((long)data[i+6] << 8) & MASK_LONG_6;
			destn[k] |= ((long)data[i+7] << 0) & MASK_LONG_7;
		}
		
		return destn;
	}
	
	public static int[] getInts(byte[] data, int start, int lastpos){
		if(data == null || lastpos < 0 || lastpos > data.length 
			|| start < 0 || start >= lastpos) 
				throw new IllegalArgumentException("Bad Destination and Source array or insufficient size");
			
		int numInts = (lastpos - start) / INT_SZ; 
		int[] destn = new int[numInts];
		for(int i = start, k = 0; i < start + INT_SZ * numInts; i += INT_SZ, k++){
			/**
			 * By default Byte is casted to Int, so left shift works on Int type
			 * Applying the Mask will ensure only the required bits are used irrespective of sign bit
			 */
			destn[k] = (data[i] << 24) & MASK_INT_0;
			destn[k] |= (data[i+1] << 16) & MASK_INT_1;
			destn[k] |= (data[i+2] << 8) & MASK_INT_2;
			destn[k] |= (data[i+3] << 0) & MASK_INT_3;			
		}
		
		return destn;

	}
	
	public static final int INT_SZ = 4;
	public static final int LONG_SZ = 8;
	
	
	public static final int MASK_INT_3 = 0xFF;
	public static final int MASK_INT_2 = MASK_INT_3 << 8;
	public static final int MASK_INT_1 = MASK_INT_2 << 8;
	public static final int MASK_INT_0 = MASK_INT_1 << 8;
	
	public static final long MASK_LONG_7 = MASK_INT_3;
	public static final long MASK_LONG_6 = MASK_LONG_7 << 8;
	public static final long MASK_LONG_5 = MASK_LONG_6 << 8;
	public static final long MASK_LONG_4 = MASK_LONG_5 << 8;
	public static final long MASK_LONG_3 = MASK_LONG_4 << 8;
	public static final long MASK_LONG_2 = MASK_LONG_3 << 8;
	public static final long MASK_LONG_1 = MASK_LONG_2 << 8;
	public static final long MASK_LONG_0 = MASK_LONG_1 << 8;
}
