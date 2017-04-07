# SolrBitmapTokenizer
A Custom Lucene Tokenizer for efficient indexing of Bitmaps that supports Atomic updates in Solr

This tokenizer is useful for the use case when large bitmaps need to be indexed in Solr and only a few bits need to be checked at Query time to see if they are set. Solr/Lucene do not support bit mask based checks on binary field data. Such binary field can only be stored and returned without any support of analysis. Analysis of field data can only be done on Text fields in Solr/Lucene. 

This tokenizer converts a bitmap to a Base64 Encoded String and generates the Offsets of the bits set in the bitmap as String tokens. The base64 encoded String is itself stored in the text field and hence is available for analysis pipeline of Lucene. Atomic updates in Solr requires fields to be marked as "stored". This tokenizer should be used at Index time analysis and then at Query time, specific bits can be checked by using a usual Lucene Term Query using the bit offset as the Term to search for. 
