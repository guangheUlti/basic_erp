package io.guangsoft.erp.dao;

import org.apache.lucene.search.TopDocs;

import java.util.Map;

public interface LuceneDAO {

    public void insertDoc(Map<String, String> docMap) throws Exception;

    public void deleteDoc(String id) throws Exception;

    public void updateDoc(Map<String, String> docMap) throws Exception;

    public void insertOrUpdateDoc(Map<String, String> docMap) throws Exception;

    //严格匹配整个字段，可传多个字段
    public TopDocs searchDocsByTerm(Map<String, String> termMap) throws Exception;

    //匹配分词后的字段，可传多个字段
    public TopDocs searchDocsByParser(Map<String, String> parserMap) throws Exception;

}
