package com.spring_es.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.siyue.common.utils.JsonUtils;
import com.spring_es.demo.pojo.Tests;
import com.spring_es.demo.service.ElasticsearchService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

	@Autowired
	private RestHighLevelClient client;


	/**
	 * 创建索引
	 *
	 * @param index
	 * @throws IOException
	 */
	public void createIndex(String index) throws IOException {
		CreateIndexRequest request = new CreateIndexRequest(index);
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println("createIndex: " + JSON.toJSONString(createIndexResponse));
	}

	/**
	 * 判断索引是否存在
	 *
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public boolean existsIndex(String index) throws IOException {
		GetIndexRequest request = new GetIndexRequest();
		request.indices(index);
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println("existsIndex: " + exists);
		return exists;
	}

	/**
	 * 增加记录
	 *
	 * @param index
	 * @param type
	 * @param tests
	 * @throws IOException
	 */
	public void add(String index, String type, Tests tests) throws IOException {
		IndexRequest indexRequest = new IndexRequest(index, type, tests.getId().toString());
		indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		System.out.println("add: " + JSON.toJSONString(indexResponse));
	}

	/**
	 * 判断记录是否存在
	 *
	 * @param index
	 * @param type
	 * @param tests
	 * @return
	 * @throws IOException
	 */
	public boolean exists(String index, String type, Tests tests) throws IOException {
		GetRequest getRequest = new GetRequest(index, type, tests.getId().toString());
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println("exists: " + exists);
		return exists;
	}

	/**
	 * 获取记录信息
	 *
	 * @param index
	 * @param type
	 * @param id
	 * @throws IOException
	 */
	public Tests get(String index, String type, Long id) throws IOException {
		GetRequest getRequest = new GetRequest(index, type, id.toString());
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

		return JsonUtils.toObject(JSON.toJSONString(getResponse.getSource()),Tests.class);
	}

	/**
	 * 更新记录信息
	 *
	 * @param index
	 * @param type
	 * @param tests
	 * @throws IOException
	 */
	public void update(String index, String type, Tests tests) throws IOException {
		tests.setName(tests.getName() + "updated");
		UpdateRequest request = new UpdateRequest(index, type, tests.getId().toString());
		request.doc(JSON.toJSONString(tests), XContentType.JSON);
		UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
		System.out.println("update: " + JSON.toJSONString(updateResponse));
	}

	/**
	 * 删除记录
	 *
	 * @param index		索引
	 * @param type		文档
	 * @param id		id
	 * @throws IOException	IO
	 */
	public void delete(String index, String type, Long id) throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest(index, type, id.toString());
		DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println("delete: " + JSON.toJSONString(response));
	}

	/**
	 * 搜索
	 *
	 * @param index
	 * @param type
	 * @param name
	 * @throws IOException
	 */
	public List<Tests> search(String index, String type, Integer begin, Integer size, String name) throws IOException {

		List<Tests> list = new ArrayList<>();
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

		// 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
		boolBuilder.must(QueryBuilders.wildcardQuery("name", name));
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

		// 查询条件--->生成DSL查询语句
		sourceBuilder.query(boolBuilder);

		// 每页多少条数据，默认10
		sourceBuilder.size(1000);

		// 第一个是获取字段，第二个是过滤的字段，默认获取全部
		sourceBuilder.fetchSource(new String[]{"id", "name"}, new String[]{});

		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		searchRequest.source(sourceBuilder);

		// 3.查询
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

		System.out.println("search: " + JSON.toJSONString(response));

		SearchHits hits = this.search(index, type, sourceBuilder);
		list = this.bybegin(begin, size, hits);

		return list;

	}

	/**
	 * 批量操作
	 *
	 * @throws IOException
	 */
	public void bulkAdd(String index, String type, List<Tests> list) throws IOException {

		Tests tests = null;

		// 批量增加
		BulkRequest bulkAddRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			tests = list.get(i);
			IndexRequest indexRequest = new IndexRequest(index, type, tests.getId().toString());
			indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
			bulkAddRequest.add(indexRequest);
		}
		BulkResponse bulkAddResponse = client.bulk(bulkAddRequest, RequestOptions.DEFAULT);
		System.out.println("bulkAdd: " + JSON.toJSONString(bulkAddResponse));

		// 批量更新
		/*BulkRequest bulkUpdateRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			tests = list.get(i);
			tests.setName(tests.getName() + " updated");
			UpdateRequest updateRequest = new UpdateRequest(index, type, tests.getId().toString());
			updateRequest.doc(JSON.toJSONString(tests), XContentType.JSON);
			bulkUpdateRequest.add(updateRequest);
		}
		BulkResponse bulkUpdateResponse = client.bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
		System.out.println("bulkUpdate: " + JSON.toJSONString(bulkUpdateResponse));*/

		// 批量删除
		/*BulkRequest bulkDeleteRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			tests = list.get(i);
			DeleteRequest deleteRequest = new DeleteRequest(index, type, tests.getId().toString());
			bulkDeleteRequest.add(deleteRequest);
		}
		BulkResponse bulkDeleteResponse = client.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
		System.out.println("bulkDelete: " + JSON.toJSONString(bulkDeleteResponse));*/
	}

	@Override
	public List<Tests> find(String index, String type, Integer begin, Integer size, SearchSourceBuilder sourceBuilder) {

		List<Tests> list = new ArrayList<>();

		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

		// 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
		boolBuilder.must(QueryBuilders.matchAllQuery());

		// 每页多少条数据，默认10
		sourceBuilder.size(1000);

		try {
			SearchHits hits = this.search(index, type, sourceBuilder);

			list = this.bybegin(begin,size,hits);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 *
	 * @param index		索引
	 * @param type		类型
	 * @param sourceBuilder 查询条件，属性信息
	 * @return
	 * @throws IOException
	 */
	public SearchHits search(String index, String type,SearchSourceBuilder sourceBuilder) throws IOException {

		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		searchRequest.source(sourceBuilder);

		// 3.查询
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

		System.out.println("search: " + JSON.toJSONString(response));
		return response.getHits();
	}

	/**
	 *	查询数据记录数
	 * @param index		索引
	 * @param type		类型
	 * @param sourceBuilder 查询条件，属性信息
	 * @return
	 */
	public int count(String index, String type,SearchSourceBuilder sourceBuilder){

		try {

			BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

			boolBuilder.must(QueryBuilders.matchAllQuery());
			// 每页多少条数据，默认10
			sourceBuilder.size(1000);

			sourceBuilder.query(boolBuilder);
			SearchHits hits = this.search(index, type, sourceBuilder);

			return hits.getHits().length;


		} catch (IOException e) {
			e.printStackTrace();
		}


		return -1;
	}

	public List<Tests> bybegin(Integer begin, Integer size,SearchHits hits){


		//记录数量
		int count = hits.getHits().length;

		size = size >= 1000 ? 1000 : size;
		size = size <= 0 ? 10 : size;
		int beginCount = count % size != 0 ? count / size + 1 : count / size;
		begin = begin <= -1 ? 0 : begin;
		begin = begin > beginCount ? beginCount : begin;
		begin = (begin - 1) * size;


		List<Tests> list = new ArrayList<>(),newList = new ArrayList<>();

		SearchHit[] searchHits = hits.getHits();

		Tests t = new Tests();

		for (SearchHit hit : searchHits) {
			System.out.println(hit.getSourceAsString());
			//转换成具体类型并放到集合中
			t = JsonUtils.toObject(hit.getSourceAsString(),Tests.class);
			list.add(t);
		}

		//存放数据的索引范围
		int scope = size + begin;

		while (begin < scope) {
			if (begin < count) {
				newList.add(list.get(begin++));
			} else {
				break;
			}
		}

		return newList;


	}


}
