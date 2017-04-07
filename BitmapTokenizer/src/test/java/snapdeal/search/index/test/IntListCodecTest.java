package snapdeal.search.index.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import snapdeal.search.index.ByteUtils;
import snapdeal.search.index.IntListCodec;

public class IntListCodecTest {
	
	@Test
	public void testEncodeFormat(){
		IntListCodec ilc = new IntListCodec(30000, 5000);
		int[] iarr = {50, 30, 45, 1, 3, 24, 50};
		byte[] res = ilc.encode(iarr);
		
		assertEquals("Wrong Format", (byte) 0, res[0]);
		
		iarr = new int[20000];
		for(int i = 0; i < 20000; i++){
			iarr[i] = ThreadLocalRandom.current().nextInt(0, 30000);
		}
		
		res = ilc.encode(iarr);
		assertEquals("Wrong Format", (byte) 1, res[0]);
	}
	
	
	@Test
	public void testCodecSanity(){
		IntListCodec ilc = new IntListCodec(30000, 5000);
		Set<Integer> iset = new HashSet<Integer>();
		for(int i = 0; i < 10000; i++){
			iset.add(ThreadLocalRandom.current().nextInt(0, 30000));
		}
		
		int[] iarr = new int[iset.size()];
		int k = 0;
		for(int i : iset){
			iarr[k++] = i;
		}
		
		byte[] resb = ilc.encode(iarr);
		String str = ByteUtils.encodeB64(resb);
		//System.out.println(Arrays.toString(iarr));
		//System.out.println(str);
		Iterator<Integer> itr = ilc.decodeIter(resb);
		
		Set<Integer> oset = new HashSet<Integer>();
		while(itr.hasNext()){
			oset.add(itr.next());
		}
		
		assertEquals("Codec failed", iset, oset);
	}
	
	@Test
	public void testCodecLongSize(){
		int maxVal = 20000;
		IntListCodec ilc = new IntListCodec(maxVal, 5000);
		int[] iarr = new int[maxVal / 2];
		for(int i = 0, k = 0; i < iarr.length && k < maxVal; k++){
			if(k % 2 != 0)
				iarr[i++] = k;
		}
		
		byte[] res = ilc.encode(iarr);
		int maxLongs = (((maxVal + 1) / 8 + 1) / 8 + 1);
		assertTrue("Size is larger than expected", (res.length - 1) <= 8 * maxLongs ); 
	}

}
