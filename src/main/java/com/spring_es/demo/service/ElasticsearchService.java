package com.spring_es.demo.service;

import com.spring_es.demo.pojo.Tests;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface ElasticsearchService {


	void createIndex(String index) throws IOException;


	boolean existsIndex(String index) throws IOException;


	void add(String index, String type, Tests tests) throws IOException;


	boolean exists(String index, String type, Tests tests) throws IOException;


	Tests get(String index, String type, Long id) throws IOException;


	void update(String index, String type, Tests tests) throws IOException;


	void delete(String index, String type, Long id) throws IOException;


	List<Tests> search(String index, String type, Integer begin, Integer size,  String name) throws IOException;


	void bulkAdd(String index, String type, List<Tests> list) throws IOException;

	void bulkDelete(String index, String type, List<Tests> list) throws IOException;


	List<Tests> find(String index, String type, Integer begin, Integer size, SearchSourceBuilder sourceBuilder);

	int count(String index, String type,SearchSourceBuilder sourceBuilder);

	SearchHits search(String index, String type, SearchSourceBuilder sourceBuilder) throws IOException;

	List<Tests> findByIdRange(String index, String type,Integer begin, Integer size, String start, String end);
}
