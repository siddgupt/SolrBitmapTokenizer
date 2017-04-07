package snapdeal.search.index;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource.AttributeFactory;

public class BitMapTokenizerFactory extends TokenizerFactory {

	private int maxVal = 30000;
	private int maxPfor = 5000;
	
	public BitMapTokenizerFactory(Map<String, String> args) {
		super(args);
		
		String maxValStr = args.get("maxVal");
		String maxPforStr = args.get("maxPfor");
		
		if(maxValStr != null)
			maxVal = Integer.parseInt(maxValStr);
		
		if(maxPforStr != null)
			maxPfor = Integer.parseInt(maxPforStr);
		
	}

	@Override
	public Tokenizer create(AttributeFactory arg0, Reader arg1) {
		return new BitMapTokenizer(arg1, maxVal, maxPfor);
	}
	
	
	
}
