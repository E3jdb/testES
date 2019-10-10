package com.spring_es.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.siyue.common.utils.JsonUtils;
import com.spring_es.demo.pojo.User;
import com.spring_es.demo.service.ElasticsearchService;
import com.spring_es.demo.service.UserService;
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
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private RestHighLevelClient client;

	/**
	 * 增加记录
	 *
	 * @param index
	 * @param type
	 * @param User
	 * @throws IOException
	 */
	public void add(String index, String type, User User) throws IOException {
		IndexRequest indexRequest = new IndexRequest(index, type, User.getId().toString());
		indexRequest.source(JSON.toJSONString(User), XContentType.JSON);
		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		System.out.println("add: " + JSON.toJSONString(indexResponse));
	}

	/**
	 * 判断记录是否存在
	 *
	 * @param index
	 * @param type
	 * @param User
	 * @return
	 * @throws IOException
	 */
	public boolean exists(String index, String type, User User) throws IOException {
		GetRequest getRequest = new GetRequest(index, type, User.getId().toString());
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
	public User get(String index, String type, Long id) throws IOException {
		GetRequest getRequest = new GetRequest(index, type, id.toString());
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

		return JsonUtils.toObject(JSON.toJSONString(getResponse.getSource()),User.class);
	}

	/**
	 * 更新记录信息
	 *
	 * @param index
	 * @param type
	 * @param User
	 * @throws IOException
	 */
	public void update(String index, String type, User User) throws IOException {
		User.setName(User.getName() + "updated");
		UpdateRequest request = new UpdateRequest(index, type, User.getId().toString());
		request.doc(JSON.toJSONString(User), XContentType.JSON);
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
	public List<User> search(String index, String type, Integer begin, Integer size, String name) throws IOException {

		List<User> list = new ArrayList<>();
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

		// 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
		boolBuilder.must(QueryBuilders.wildcardQuery("name", null));
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
		list = this.byPage(begin, size, hits);

		return list;

	}

	/**
	 * 批量操作
	 *
	 * @throws IOException
	 */
	public void bulkAdd(String index, String type, List<User> list) throws IOException {

		User User = null;

		// 批量增加
		BulkRequest bulkAddRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			User = list.get(i);
			IndexRequest indexRequest = new IndexRequest(index, type, User.getId().toString());
			indexRequest.source(JSON.toJSONString(User), XContentType.JSON);
			bulkAddRequest.add(indexRequest);
		}
		BulkResponse bulkAddResponse = client.bulk(bulkAddRequest, RequestOptions.DEFAULT);
		System.out.println("bulkAdd: " + JSON.toJSONString(bulkAddResponse));

		// 批量更新
		/*BulkRequest bulkUpdateRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			User = list.get(i);
			User.setName(User.getName() + " updated");
			UpdateRequest updateRequest = new UpdateRequest(index, type, User.getId().toString());
			updateRequest.doc(JSON.toJSONString(User), XContentType.JSON);
			bulkUpdateRequest.add(updateRequest);
		}
		BulkResponse bulkUpdateResponse = client.bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
		System.out.println("bulkUpdate: " + JSON.toJSONString(bulkUpdateResponse));*/


	}

	/**
	 * 	批量删除
	 * @param index
	 * @param type
	 * @param list
	 * @throws IOException
	 */
	public void bulkDelete(String index, String type, List<User> list) throws IOException {

		User User = null;

		// 批量删除
		BulkRequest bulkDeleteRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			User = list.get(i);
			DeleteRequest deleteRequest = new DeleteRequest(index, type, User.getId().toString());
			bulkDeleteRequest.add(deleteRequest);
		}
		BulkResponse bulkDeleteResponse = client.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
		System.out.println("bulkDelete: " + JSON.toJSONString(bulkDeleteResponse));

	}


	@Override
	public List<User> find(String index, String type, Integer begin, Integer size, SearchSourceBuilder sourceBuilder) {

		List<User> list = new ArrayList<>();

		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

		// 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
		boolBuilder.must(QueryBuilders.matchAllQuery());

		// 每页多少条数据，默认10
		sourceBuilder.size(1000);

		try {
			SearchHits hits = this.search(index, type, sourceBuilder);

			list = this.byPage(begin,size,hits);

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
	 * 	根据id进行范围查找
	 * @param index
	 * @param type
	 * @param start	起始位置
	 * @param end	结束位置
	 * @return
	 */
	public List<User> findByIdRange(String index, String type,Integer begin, Integer size, String start, String end) {

		List<User> list = new ArrayList<>();

		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("id");
		rangeQueryBuilder.gte(start);
		rangeQueryBuilder.lte(end);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

		// 每页多少条数据，默认10
		sourceBuilder.size(1000);

		// 查询条件--->生成DSL查询语句
		sourceBuilder.query(rangeQueryBuilder);
		try {
			SearchHits hits = this.search(index, type, sourceBuilder);

			list = this.byPage(begin,size,hits);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
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

	public List<User> byPage(Integer begin, Integer size,SearchHits hits){


		//记录数量
		int count = hits.getHits().length;

		size = size >= 1000 ? 1000 : size;
		size = size <= 0 ? 10 : size;
		int beginCount = count % size != 0 ? count / size + 1 : count / size;
		begin = begin <= -1 ? 0 : begin;
		begin = begin > beginCount ? beginCount : begin;
		begin = (begin - 1) * size;


		List<User> list = new ArrayList<>(),newList = new ArrayList<>();


		//此行等同于下面的注释内容
		hits.forEach(hit -> list.add(JsonUtils.toObject(hit.getSourceAsString(),User.class)));

		/*SearchHit[] searchHits = hits.getHits();

		User t = new User();

		for (SearchHit hit : searchHits) {
			System.out.println(hit.getSourceAsString());
			//转换成具体类型并放到集合中
			t = JsonUtils.toObject(hit.getSourceAsString(),User.class);
			list.add(t);
		}*/

		//存放数据的索引范围
		int scope = size + begin;

		if( list.size() != 0){
			while (begin < scope) {
				if (begin < count) {
					newList.add(list.get(begin++));
				} else {
					break;
				}
			}
		}

		return newList;


	}


}
