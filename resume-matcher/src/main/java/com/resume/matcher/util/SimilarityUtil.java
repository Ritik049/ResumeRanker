package com.resume.matcher.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.*;

public class SimilarityUtil {

    public static float computeSimilarity(String resumeText, String jobDescription) throws IOException {
        RAMDirectory ramDirectory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(ramDirectory, config);

        addDoc(writer, "resume", resumeText);
        addDoc(writer, "job", jobDescription);
        writer.close();

        IndexReader reader = DirectoryReader.open(ramDirectory);
        ClassicSimilarity similarity = new ClassicSimilarity();

        Map<String, Float> resumeVector = getTFIDFVector(reader, 0, similarity);
        Map<String, Float> jobVector = getTFIDFVector(reader, 1, similarity);

        return cosineSimilarity(resumeVector, jobVector);
    }

    private static void addDoc(IndexWriter writer, String id, String content) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("id", id, Field.Store.YES));

        FieldType fieldType = new FieldType(TextField.TYPE_STORED);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorOffsets(true);
        doc.add(new Field("content", content, fieldType));

        writer.addDocument(doc);
    }

    private static Map<String, Float> getTFIDFVector(IndexReader reader, int docId, ClassicSimilarity similarity) throws IOException {
        Map<String, Float> tfidf = new HashMap<>();
        Terms terms = reader.getTermVector(docId, "content");

        if (terms == null) return tfidf;

        TermsEnum termsEnum = terms.iterator();
        PostingsEnum postingsEnum = null;

        while ((termsEnum.next()) != null) {
            String term = termsEnum.term().utf8ToString();
            postingsEnum = termsEnum.postings(postingsEnum, PostingsEnum.FREQS);
            if (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                int tf = postingsEnum.freq();
                int df = reader.docFreq(new Term("content", term));
                float idf = similarity.idf(df, reader.numDocs());
                tfidf.put(term, tf * idf);
            }
        }

        return tfidf;
    }

    private static float cosineSimilarity(Map<String, Float> vec1, Map<String, Float> vec2) {
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(vec1.keySet());
        allTerms.addAll(vec2.keySet());

        float dotProduct = 0f;
        float normA = 0f;
        float normB = 0f;

        for (String term : allTerms) {
            float v1 = vec1.getOrDefault(term, 0f);
            float v2 = vec2.getOrDefault(term, 0f);
            dotProduct += v1 * v2;
            normA += v1 * v1;
            normB += v2 * v2;
        }

        if (normA == 0 || normB == 0) return 0f;

        return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }
}
