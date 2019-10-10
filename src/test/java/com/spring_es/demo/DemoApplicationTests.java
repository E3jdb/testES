package com.spring_es.demo;

import com.alibaba.fastjson.JSON;
import com.siyue.common.utils.JsonUtils;
import com.spring_es.demo.controller.ElasticsearchController;
import com.spring_es.demo.pojo.Tests;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {



	@Autowired
	private RestHighLevelClient client;

	public static String INDEX_TEST = null;
	public static String TYPE_TEST = null;
	public static Tests tests = null;
	public static List<Tests> testsList = null;

	@BeforeClass
	public static void before() {
		INDEX_TEST = "index_test"; // 索引名称
		TYPE_TEST = "_doc"; // 索引类型
		testsList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			tests = new Tests();
			tests.setId((long) i);
			tests.setName("this is the test " + i);
			testsList.add(tests);
		}
	}


	@Autowired
	private  ElasticsearchController elasticsearchController;

	@Autowired
	private  Tests t;

	@Autowired
	private  Tests t1;

	@Test
	public void contextLoads() {


		/*Tests t = new Tests();
		t.setId((long) 101);
		t.setName("asdfgsdfsfd");
		elasticsearchController.save(INDEX_TEST, TYPE_TEST, t);*/

		//elasticsearchController.findAll(INDEX_TEST,TYPE_TEST, 1, 10);


		System.out.println(t);
		System.out.println(t1);

	}

	@Test
	public void testIndex() throws IOException {
		// 判断是否存在索引
		/*if (!existsIndex(INDEX_TEST)) {
			// 不存在则创建索引
			createIndex(INDEX_TEST);
		}*/

		// 判断是否存在记录
		/*if (!exists(INDEX_TEST, TYPE_TEST, tests)) {
			// 不存在增加记录
			add(INDEX_TEST, TYPE_TEST, tests);
		}*/

		// 获取记录信息
		/*for (int i = 0; i < testsList.size(); i++) {
			get(INDEX_TEST, TYPE_TEST, testsList.get(i).getId());
		}*/

		// 更新记录信息
		/*tests.setId(Long.valueOf(10));
		tests.setName("u gioiy");
		update(INDEX_TEST, TYPE_TEST, tests);
		get(INDEX_TEST, TYPE_TEST, tests.getId());*/

		// 删除记录信息
		//delete(INDEX_TEST, TYPE_TEST, Long.valueOf(0));
		//get(INDEX_TEST, TYPE_TEST, tests.getId());

		// 批量操作
		//bulk();
	}


}
