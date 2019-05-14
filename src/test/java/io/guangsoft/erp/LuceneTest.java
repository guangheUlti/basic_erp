package io.guangsoft.erp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.guangsoft.erp.dao.LuceneDAO;
import io.guangsoft.erp.dao.impl.LuceneDAOImpl;
import io.guangsoft.erp.util.LuceneUtil;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LuceneTest {

    LuceneDAO luceneDAO = new LuceneDAOImpl();

    @Test
    public void testInsertDoc() throws Exception {
        JSONArray jsonArray = JSONArray.parseArray(
                "[{id:'1',name:'李白',desc:'朝辞白帝彩云间'}, " +
                        "{id:'2',name:'杜甫',desc:'润物细无声'}, " +
                        "{id:'3',name:'苏轼',desc:'大江东去浪淘尽'}]");
        for(int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, String> docMap = jsonObject.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry :: getKey, entry -> entry.getValue().toString()
            ));
            luceneDAO.insertDoc(docMap);
        }
    }

    @Test
    public void testSearchDocsByTerm() throws Exception {
        Map<String, String> docMap = new HashMap<String, String>();
        docMap.put("name", "李白");
        TopDocs topDocs = luceneDAO.searchDocsByTerm(docMap);
        LuceneUtil.printTopDocs(topDocs);
    }

    @Test
    public void testSearchDocsByParser() throws Exception {
        Map<String, String> docMap = new HashMap<String, String>();
        docMap.put("name", "李白");
        TopDocs topDocs = luceneDAO.searchDocsByParser(docMap);
        LuceneUtil.printTopDocsHighlight(topDocs, new TermQuery(new Term("name", "李白")));
    }

    @Test
    public void testUpdateDoc() throws Exception {
        Map<String, String> docMap = new HashMap<String, String>();
        docMap.put("name", "李白");
        LuceneUtil.printTopDocs(luceneDAO.searchDocsByTerm(docMap));
        docMap.put("id", "1");
        docMap.put("desc", "人生得意须尽欢");
        luceneDAO.updateDoc(docMap);
        docMap.remove("id");
        docMap.remove("desc");
        LuceneUtil.printTopDocs(luceneDAO.searchDocsByTerm(docMap));
    }

    @Test
    public void testDeleteDoc() throws Exception{
        Map<String, String> docMap = new HashMap<String, String>();
        docMap.put("id", "1");
        LuceneUtil.printTopDocs(luceneDAO.searchDocsByTerm(docMap));
        luceneDAO.deleteDoc("1");
        LuceneUtil.printTopDocs(luceneDAO.searchDocsByTerm(docMap));
    }
}
