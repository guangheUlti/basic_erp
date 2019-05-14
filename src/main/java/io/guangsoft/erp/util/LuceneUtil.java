package io.guangsoft.erp.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.List;

public class LuceneUtil {
    //索引目录位置
    private static final String INDEX_DIR = "/index";
    //索引文件存放目录对象
    private static Directory directory;
    //分词器对象
    private static Analyzer analyzer;
    //索引写对象，线程安全
    private static IndexWriter indexWriter;
    //索引读对象，线程安全
    private static IndexReader indexReader;
    //索引搜索对象，线程安全
    private static IndexSearcher indexSearcher;

    static {
        try {
            directory = FSDirectory.open(Paths.get(INDEX_DIR));
            //系统关闭前关闭索引库的流
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        if(indexWriter != null) {
                            indexWriter.close();
                        }
                        if(indexReader != null) {
                            indexReader.close();
                        }
                        if(directory != null) {
                            directory.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取分词器
    public static Analyzer getAnalyzer() {
        if(analyzer == null) {
            analyzer = new SmartChineseAnalyzer();
        }
        return analyzer;
    }

    //获取索引Writer
    public static IndexWriter getIndexWriter() {
        if(indexWriter == null) {
            try {
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(getAnalyzer());
                indexWriter = new IndexWriter(directory, indexWriterConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return indexWriter;
    }

    //获取索引Reader
    public static IndexReader getIndexReader() {
        try {
            if(indexReader == null) {
                indexReader = DirectoryReader.open(directory);
            } else {
                //对比索引库是否更新，更新则使用更新后的Reader
                IndexReader newIndexReader = DirectoryReader.openIfChanged((DirectoryReader) indexReader);
                if(newIndexReader != null) {
                    indexReader.close();
                    indexReader = newIndexReader;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return indexReader;
    }

    //获取索引Searcher
    public static IndexSearcher getIndexSearcher() {
        if(indexSearcher == null) {
            indexSearcher = new IndexSearcher(getIndexReader());
        }
        return indexSearcher;
    }

    //打印索引文档（表）
    public static void printDocument(Document document) {
        System.out.println(document);
        List<IndexableField> fieldList = document.getFields();
        for(IndexableField field : fieldList) {
            System.out.println(field.name() + " : " + field.stringValue());
        }
    }

    //打印命中文档
    public static void printScoreDoc(ScoreDoc scoreDoc) {
        int docId = scoreDoc.doc;
        System.out.println("文档编号：" + docId);
        System.out.println("文档得分：" + scoreDoc.score);
        try {
            Document document = indexSearcher.doc(docId);
            printDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打印带得分的命中文档
    public static void printTopDocs(TopDocs topDocs) {
        int totalHits = topDocs.totalHits;
        System.out.println("命中文档总条数：" + totalHits);
        System.out.println("命中文档最大分数：" + topDocs.getMaxScore());
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for(ScoreDoc scoreDoc : scoreDocs) {
            printScoreDoc(scoreDoc);
        }
    }

    //高亮打印命中文档
    public static void printTopDocsHighlight(TopDocs topDocs, Query query) {
        // 格式化器：参数1：前置标签，参数2：后置标签
        Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
        //打分对象，参数：query里面的条件，条件里面有搜索关键词
        Scorer scorer = new QueryScorer(query);
        //高亮工具：参数1.需要高亮什么颜色, 参数2.将哪些关键词进行高亮
        Highlighter hightlighter = new Highlighter(formatter, scorer);
        try {
            for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = getIndexSearcher().doc(scoreDoc.doc);
                List<IndexableField> fieldList = document.getFields();
                for(IndexableField field : fieldList) {
                    String highlightValue = hightlighter.getBestFragment(getAnalyzer(), field.name(), field.stringValue());
                    if(highlightValue == null) {
                        highlightValue = field.stringValue();
                    }
                    System.out.println(field.name() + " : " + highlightValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
