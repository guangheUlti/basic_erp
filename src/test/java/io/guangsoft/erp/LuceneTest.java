package io.guangsoft.erp;

import io.guangsoft.erp.util.LuceneUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

public class LuceneTest {

    @Test
    public void searchDocs(Query query, int num) throws Exception {
        IndexSearcher indexSearcher = LuceneUtil.getIndexSearcher();
        TopDocs topDocs = indexSearcher.search(query, num);
        LuceneUtil.printTopDocs(topDocs);
        LuceneUtil.printTopDocsHighlight(topDocs, query);
    }
}
