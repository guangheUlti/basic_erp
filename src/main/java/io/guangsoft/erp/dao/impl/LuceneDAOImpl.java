package io.guangsoft.erp.dao.impl;

import io.guangsoft.erp.dao.LuceneDAO;
import io.guangsoft.erp.util.LuceneUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;

import java.util.Map;

public class LuceneDAOImpl implements LuceneDAO {

    @Override
    public void insertDoc(Map<String, String> docMap) throws Exception {
        FieldType fieldType = new FieldType();
        //是否存储记录
        fieldType.setStored(true);
        //文档型索引，只索引文档，不支持打分和位置检索
        fieldType.setIndexOptions(IndexOptions.DOCS);
        //是否要忽略field的加权基准值，如果为true可以节省内存消耗
        //但在打分质量方面会有更高的消耗，也不能使用index-time进行加权操作。
        fieldType.setOmitNorms(true);
        //是否使用分析器将域值分解成独立的语汇单元流，是否分词
        fieldType.setTokenized(true);
        //lucene索引库的一条记录
        Document document = new Document();
        for(Map.Entry<String, String> entry : docMap.entrySet()) {
            Field field = new Field(entry.getKey(), entry.getValue(), fieldType);
            document.add(field);
        }
        //保存到索引库
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        indexWriter.addDocument(document);
        indexWriter.close();
    }

    @Override
    public void deleteDoc(String id) throws Exception {
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        Term term = new Term("id", id);
        indexWriter.deleteDocuments(term);
        indexWriter.forceMergeDeletes();
        indexWriter.commit();
        indexWriter.close();
    }

    @Override
    public void updateDoc(Map<String, String> docMap) throws Exception {
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        fieldType.setIndexOptions(IndexOptions.DOCS);
        fieldType.setOmitNorms(true);
        fieldType.setTokenized(true);
        Document document = new Document();
        for(Map.Entry<String, String> entry : docMap.entrySet()) {
            Field field = new Field(entry.getKey(), entry.getValue(), fieldType);
            document.add(field);
        }
        Term term = new Term("id", docMap.get("id"));
        IndexWriter indexWriter = LuceneUtil.getIndexWriter();
        indexWriter.updateDocument(term, document);
        indexWriter.close();
    }

    @Override
    public void insertOrUpdateDoc(Map<String, String> docMap) throws Exception {
        Term term = new Term("id", docMap.get("id"));
        TermQuery termQuery = new TermQuery(term);
        TopDocs topDocs = LuceneUtil.getIndexSearcher().search(termQuery, 1);
        if(topDocs.totalHits == 0) {
            insertDoc(docMap);
        } else {
            updateDoc(docMap);
        }
    }

    @Override
    public TopDocs searchDocsByTerm(Map<String, String> termMap) throws Exception {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        for(Map.Entry<String, String> termEntry : termMap.entrySet()) {
            Term term = new Term(termEntry.getKey(), termEntry.getValue());
            TermQuery termQuery = new TermQuery(term);
            booleanQueryBuilder.add(termQuery, BooleanClause.Occur.MUST);
        }
        BooleanQuery booleanQuery = booleanQueryBuilder.build();
        //是否开启特定字段排序
        boolean orderFlag = false;
        TopDocs topDocs = null;
        if(orderFlag) {
            Sort sort = new Sort(new SortField[]{new SortField("createTime", SortField.Type.LONG, true)});
            topDocs = LuceneUtil.getIndexSearcher().search(booleanQuery, 99999999, sort);
        } else {
            topDocs = LuceneUtil.getIndexSearcher().search(booleanQuery, 99999999);
        }
        return topDocs;
    }

    @Override
    public TopDocs searchDocsByParser(Map<String, String> parserMap) throws Exception {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        for(Map.Entry<String, String> parserEntry : parserMap.entrySet()) {
            QueryParser queryParser = new QueryParser(parserEntry.getKey(), LuceneUtil.getAnalyzer());
            queryParser.setDefaultOperator(QueryParserBase.AND_OPERATOR);
            Query query = queryParser.parse(parserEntry.getValue());
            booleanQueryBuilder.add(query, BooleanClause.Occur.MUST);
        }
        BooleanQuery booleanQuery = booleanQueryBuilder.build();
        //是否开启特定字段排序
        boolean orderFlag = false;
        TopDocs topDocs = null;
        if(orderFlag) {
            Sort sort = new Sort(new SortField[]{new SortField("createTime", SortField.Type.LONG, true)});
            topDocs = LuceneUtil.getIndexSearcher().search(booleanQuery, 99999999, sort);
        } else {
            topDocs = LuceneUtil.getIndexSearcher().search(booleanQuery, 99999999);
        }
        return topDocs;
    }

}
