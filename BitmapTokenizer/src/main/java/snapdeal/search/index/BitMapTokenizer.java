package snapdeal.search.index;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * The Tokenizer of Strings that are Base 64 representation of IntListCodec encoding output.
 * The encoding will always represent a list of Integers within range 0 to some maxVal hence,
 * each Integer represents a bit in the bitmap of maxVal values. 
 * This Tokenizer will extract the integers encoded by the input String and generate them as separate 
 * String Tokens. This allows the checking of set bits by searching for a normal Lucene Term of that
 * bit offset. e.g. field:100 Term will match if 100 is present in the list of integers encoded by
 * input string.
 * @author siddharth
 *
 */
public class BitMapTokenizer extends Tokenizer {

	private int count = 0;
	
	private int maxVal = 30000;
	private int maxPfor = 5000;
	
	protected BitMapTokenizer(Reader input, int maxVal, int maxPfor) {
		super(input);
		this.maxVal = maxVal;
		this.maxPfor = maxPfor;		
	}
	
	private CharTermAttribute curIntTerm = addAttribute(CharTermAttribute.class);
	
	private Iterator<Integer> itr;

	@Override
	public void reset() throws IOException {
		super.reset();
		// Reset the iterator 
		setupItr();
	}
	
	/*
	 * Use the Inherited "input" Reader to setup the iterator. 
	 * Can be called only after super.reset()
	 */
	private void setupItr(){
		
		//System.out.println("BitMapTokenixer iterator setup count : " + (count++));
		StringBuilder bldr = new StringBuilder();
		int ch;
		try {
			ch = this.input.read();
			while(ch != -1){
				char c = (char)ch;
				bldr.append(c);
				ch = this.input.read();
			}
			
			byte[] data = ByteUtils.decodeB64(bldr.toString());
			IntListCodec cdc = new IntListCodec(maxVal, maxPfor);
			itr = cdc.decodeIter(data);
		} catch (IOException e) {
			System.err.println("Error in iterator setup of BitMapTokenizer: " + e.getMessage());
			e.printStackTrace();
		}
	}	
	
	/**
	 * Generates the String tokens of the Integers present in the input string of this field 
	 */
	@Override
	public final boolean incrementToken() throws IOException {
		if(itr == null)
			return false;
		
		clearAttributes();
		if(itr.hasNext()){
			curIntTerm.append(itr.next().toString());			
			return true;
		}
		
		return false;
	}

}
