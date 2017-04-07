package snapdeal.search.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.lemire.integercompression.differential.IntegratedIntCompressor;

import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.FixedBitSet.FixedBitSetIterator;
/**
 * The Main Codec class which takes a list of Integers ranging from 0 to maxVal and compresses the list
 * using one of the two schemes : simple bitmap of maxVal bits or PFOR Delta compression of the list
 * The PFOR scheme is chosen if the size of the list is at most maxPforCnt. Otherwise, simple bitmap is
 * chosen. Experimentally, for a maxVal of 30 K, maxPforCnt is around 5 K. Hence, any list of Integers
 * in the range 0 to 30 K is better compressed by PFOR if the size of list is less than 5 K   
 * @author siddharth
 *
 */
public class IntListCodec {
	
	private int maxPforCnt = 5000; 
	
	private int maxVal = 30000;
	
	public IntListCodec(){
		
	}
	
	/**
	 * The constructor of Codec
	 * @param maxVal is the maximum value of any integer in the list to be compressed
	 * @param maxPforCnt is the maximum size of list for which PFOR is better for a given maxVal
	 */
	public IntListCodec(int maxVal, int maxPforCnt){
		this.maxVal = maxVal;
		this.maxPforCnt = maxPforCnt;
	}
	
	/**
	 * The main encoder method to compress the list of integers into byte[]
	 * @param intList
	 * @return The byte[] after applying compression depending upon the size of list
	 */
	public byte[] encode(int[] intList){
		byte[] res = null;
		if(intList != null){			
			if(intList.length <= maxPforCnt){
				// Apply the PFOR compression for shorter lists
				
				for(int i : intList){
					if(i < 0 || i > maxVal)
						throw new IllegalArgumentException(i + " is Out of Range : " + 0 + ", " + maxVal);													
				}
				
				Arrays.sort(intList);
				// The PFOR delta compressor
				IntegratedIntCompressor iic = new IntegratedIntCompressor();
				
				int[] cint = iic.compress(intList);
				res = new byte[1 + cint.length * ByteUtils.INT_SZ];
				res[0] = (byte)0;
				ByteUtils.getBytes(cint, res, 1);
			} else {
				// Apply the usual bit map for longer lists
				FixedBitSet fbs = new FixedBitSet(maxVal + 1);
				for(int i : intList){
					if(i < 0 || i > maxVal)
						throw new IllegalArgumentException(i + " is Out of Range : " + 0 + ", " + maxVal);
					
					fbs.set(i);					
				}
				long[] data = fbs.getBits();
				res = new byte[1 + data.length * ByteUtils.LONG_SZ];
				res[0] = (byte)1;
				ByteUtils.getBytes(data, res, 1);
			}
		}
		
		return res;
	}
	
	/**
	 * The decoder method to get an Iterator of the Integers from the encoded data.
	 * It returns iterator instead of a list for easier integration with Lucene Analyzer pipeline
	 * @param data
	 * @return
	 */
	public Iterator<Integer> decodeIter(byte[] data){
		if(data != null && data.length > 1){
			if(data[0] == (byte)0){
				int[] cint = ByteUtils.getInts(data, 1, data.length);
				IntegratedIntCompressor iic = new IntegratedIntCompressor();
				int[] intarr = iic.uncompress(cint);
				return new IntArrIterator(intarr);
			} else {
				long[] bits = ByteUtils.getLongs(data, 1, data.length);
				FixedBitSet fbs = new FixedBitSet(bits, maxVal + 1);
				return new FixedBitIterator(new FixedBitSetIterator(fbs)); 
			}
		}
		
		return null;
	}
	
	private static class IntArrIterator implements Iterator<Integer> {
		
		private int[] arr;
		private int indx = 0;
		public IntArrIterator(int[] intarr) {
			arr = intarr;
		}
		
		public boolean hasNext() {
			if(arr != null && indx < arr.length)
				return true;
			else
				return false;
		}
		
		public Integer next() {
			if(arr != null && indx < arr.length)
				return arr[indx++];
			
			return null;
		}
		
		public void remove() {
			throw new IllegalAccessError("Removal Not Supported");
		}
	}	
	
	private static class FixedBitIterator implements Iterator<Integer> {
		private FixedBitSetIterator fbsitr;
		private Integer cur;
		
		public FixedBitIterator(FixedBitSetIterator itr){
			fbsitr = itr;
		}

		public boolean hasNext() {
			cur = fbsitr.nextDoc();
			return cur != fbsitr.NO_MORE_DOCS;
		}

		public Integer next() {
			if(cur != fbsitr.NO_MORE_DOCS)
				return cur;
			
			return null;
		}

		public void remove() {
			throw new IllegalAccessError("Removal Not Supported");			
		}
	}

}
