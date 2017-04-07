package snapdeal.search.index.test;

import static org.junit.Assert.*;

import org.junit.Test;

import snapdeal.search.index.ByteUtils;

public class ByteUtilsTest {
	
	@Test
	public void testGetBytesFromLong(){
		long[] larr = { 0x8081828384858687L };
		byte[] barr = new byte[1 + 8];
		barr[0] = (byte)1;		
		
		byte[] expect = {0x01, (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83,
				(byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87};
		
		ByteUtils.getBytes(larr, barr, 1);
		
		assertArrayEquals(expect, barr);		
	}
	
	@Test
	public void testGetBytesFromInt(){
		int[] iarr = { 0x80818283 };
		byte[] barr = new byte[1 + 4];
		barr[0] = (byte)1;		
		
		byte[] expect = {0x01, (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83};
		
		ByteUtils.getBytes(iarr, barr, 1);
				
		assertArrayEquals(expect, barr);		
	}
	
	@Test
	public void testGetIntFromBytes(){
		byte[] barr = {0x01, (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83};
		
		int[] res = ByteUtils.getInts(barr, 1, barr.length);
		
		int[] expect = {0x80818283};
		
		assertArrayEquals(expect, res);
		
		int[] res2 = ByteUtils.getInts(barr, 0, barr.length);
		
		int[] expect2 = { 0x01808182 };
		
		assertArrayEquals(expect2, res2);
	}
	
	@Test
	public void testMaskLong(){
		long i = ByteUtils.MASK_LONG_0; 
		assertEquals(0xFF00000000000000L, i);
		
		i |= ByteUtils.MASK_LONG_1; 
		assertEquals(0xFFFF000000000000L, i);
		
		i |= ByteUtils.MASK_LONG_2;
		assertEquals(0xFFFFFF0000000000L, i);
		
		i |= ByteUtils.MASK_LONG_3;
		assertEquals(0xFFFFFFFF00000000L, i);
		
		i |= ByteUtils.MASK_LONG_4;
		assertEquals(0xFFFFFFFFFF000000L, i);
		
		i |= ByteUtils.MASK_LONG_5;
		assertEquals(0xFFFFFFFFFFFF0000L, i);
		
		i |= ByteUtils.MASK_LONG_6;
		assertEquals(0xFFFFFFFFFFFFFF00L, i);
		
		i |= ByteUtils.MASK_LONG_7;
		assertEquals(-1, i);		
		
	}
		
	@Test
	public void testGetLongFromBytes(){
		byte[] barr = {0x01, (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83,
				(byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87};
		
		long[] res = ByteUtils.getLongs(barr, 0, barr.length);
		
		long[] expect = {0x0180818283848586L};
		
		assertArrayEquals(expect, res);
		
		long[] res2 = ByteUtils.getLongs(barr, 1, barr.length);
		
		long[] expect2 = { 0x8081828384858687L };
		
		assertArrayEquals(expect2, res2);
	}
	
}
