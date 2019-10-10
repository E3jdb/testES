package com.spring_es.demo.service;

import com.spring_es.demo.pojo.User;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;

public interface UserService {

	void add(String index, String type, User User) throws IOException;

	boolean exists(String index, String type, User User) throws IOException;

	User get(String index, String type, Long id) throws IOException;

	void update(String index, String type, User User) throws IOException;

	void delete(String index, String type, Long id) throws IOException;

	List<User> search(String index, String type, Integer begin, Integer size, String name) throws IOException;

	void bulkAdd(String index, String type, List<User> list) throws IOException;

	void bulkDelete(String index, String type, List<User> list) throws IOException;

	List<User> find(String index, String type, Integer begin, Integer size, SearchSourceBuilder sourceBuilder);

	int count(String index, String type, SearchSourceBuilder sourceBuilder);

	SearchHits search(String index, String type, SearchSourceBuilder sourceBuilder) throws IOException;

	List<User> findByIdRange(String index, String type, Integer begin, Integer size, String start, String end);
}
