package com.NTG.QuizAppStudentTask.Services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class GradeWrittenAnswer {

    public double gradeAnswer(String studentAnswer, String correctAnswer) throws IOException {
        // Normalize النصوص
        String student = studentAnswer.toLowerCase().trim();
        String correct = correctAnswer.toLowerCase().trim();

        // استخدم Lucene لتمثيل النصوص
        Analyzer analyzer = new StandardAnalyzer();
//        RAMDirectory ramDirectory = new RAMDirectory();
        ByteBuffersDirectory ramDirectory = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(ramDirectory, config);

        // أضف الإجابات كـ documents
        addDoc(writer, "text", student);
        addDoc(writer, "text", correct);
        writer.close();

        // اقرأ الـ index
        DirectoryReader reader = DirectoryReader.open(ramDirectory);
        Terms terms1 = reader.getTermVector(0, "text");
        Terms terms2 = reader.getTermVector(1, "text");

        Map<String, Float> tfidf1 = getTfIdfVector(terms1, reader);
        Map<String, Float> tfidf2 = getTfIdfVector(terms2, reader);

        reader.close();

        // حساب Cosine Similarity
        return cosineSimilarity(tfidf1, tfidf2) * 100;
    }

    private void addDoc(IndexWriter writer, String fieldName, String text) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(fieldName, text, Field.Store.YES));
        writer.addDocument(doc);
    }

    private Map<String, Float> getTfIdfVector(Terms terms, IndexReader reader) throws IOException {
        Map<String, Float> tfidf = new HashMap<>();
        if (terms == null) return tfidf;

        TermsEnum termsEnum = terms.iterator();
        ClassicSimilarity similarity = new ClassicSimilarity();
        BytesRef term;

        while ((term = termsEnum.next()) != null) {
            String termText = term.utf8ToString();
            long termFreq = termsEnum.totalTermFreq();
            int docFreq = reader.docFreq(new Term("text", term));

            float tf = similarity.tf(termFreq);
            float idf = similarity.idf(docFreq, reader.numDocs());

            tfidf.put(termText, tf * idf);
        }
        return tfidf;
    }

    private double cosineSimilarity(Map<String, Float> vec1, Map<String, Float> vec2) {
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(vec1.keySet());
        allTerms.addAll(vec2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String term : allTerms) {
            float v1 = vec1.getOrDefault(term, 0f);
            float v2 = vec2.getOrDefault(term, 0f);

            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}

