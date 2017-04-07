package snapdeal.search.index.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import snapdeal.search.index.BitMapTokenizerFactory;
import snapdeal.search.index.ByteUtils;
import snapdeal.search.index.IntListCodec;

public class BitMapTokenizerTest {

	@Test
	public void testTokenizer() throws IOException{
		int maxVal = 30000;
		Set<Integer> iset = new HashSet<Integer>();
		for(int i = 0; i < 10; i++){
			iset.add(ThreadLocalRandom.current().nextInt(0, maxVal));
		}
		
		int[] iarr = new int[iset.size()];
		int k = 0;
		for(int i : iset){
			iarr[k++] = i;			
		}
		
		IntListCodec ilc = new IntListCodec(maxVal, 5000);
		String str = ByteUtils.encodeB64(ilc.encode(iarr));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStreamWriter wrtr = new OutputStreamWriter(bos);
		wrtr.write(str);
		wrtr.close();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		InputStreamReader rdr = new InputStreamReader(bis);
		
		Set<Integer> oset = new HashSet<Integer>();
		BitMapTokenizerFactory fac = new BitMapTokenizerFactory(new HashMap<String, String>());
		Tokenizer tknzr = fac.create(rdr);
		CharTermAttribute term = tknzr.addAttribute(CharTermAttribute.class);
		tknzr.reset();
		while(tknzr.incrementToken()){
			String s = term.toString();
			oset.add(Integer.parseInt(s));
		}
		tknzr.close();
		assertEquals("Tokenizer Failed", iset, oset);
		
		ByteArrayInputStream bis2 = new ByteArrayInputStream(bos.toByteArray());
		InputStreamReader rdr2 = new InputStreamReader(bis2);
		
		oset = new HashSet<Integer>();
		tknzr.setReader(rdr2);
		tknzr.reset();
		while(tknzr.incrementToken()){
			String s = term.toString();
			oset.add(Integer.parseInt(s));
		}
		
		assertEquals("Tokenizer Reuse Failed", iset, oset);
	}
}
