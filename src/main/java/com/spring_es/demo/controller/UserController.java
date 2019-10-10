package com.spring_es.demo.controller;

import com.siyue.common.result.CommonResult;
import com.siyue.common.utils.JsonUtils;
import com.spring_es.demo.pojo.User;
import com.spring_es.demo.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private UserService userService;


	@ApiOperation(value = "根据id查询", notes = "根据id查询")
	@RequestMapping(value = "get", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
			@ApiImplicitParam(name = "id", value = "id")
	})
	public String get(@RequestParam String index, @RequestParam String type, String id){

		try {

			if ("".equals(id) || id == null) {
				return "请输入查询id";
			}

			User t = new User();
			t.setId(Long.valueOf(id));

			//获取的时候先判断记录是否存在，如果存在就返回
			boolean isF = userService.exists(index, type, t);
			if (isF) {
				User User = userService.get(index, type, t.getId());

				return User.toString();
			} else {
				return "记录不存在！！";
			}


		} catch (IOException e) {
			e.printStackTrace();
		}


		return "记录不存在！！";
	}

	@ApiOperation(value = "添加记录", notes = "添加记录")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
	})
	public String save(@RequestParam String index, @RequestParam String type, User t){

		try {

			//添加的时候先判断记录是否存在，如果不存在就添加
			boolean isF = userService.exists(index, type, t);
			if (!isF) {
				userService.add(index, type, t);

				return "添加记录成功！！";
			} else {
				return "记录已存在！！";
			}


		} catch (IOException e) {
			e.printStackTrace();
		}


		return "添加记录失败！！";
	}

	@ApiOperation(value = "修改记录", notes = "修改记录")
	@RequestMapping(value = "update", method = RequestMethod.PUT)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
	})
	public String updata(@RequestParam String index, @RequestParam String type, User t){

		try {

			//修改的时候先判断记录是否存在，如果存在就修改
			boolean isF = userService.exists(index, type, t);
			if (isF) {
				userService.update(index, type, t);

				return "修改记录成功！！";
			} else {
				return "记录不存在！！";
			}


		} catch (IOException e) {
			e.printStackTrace();
		}


		return "修改记录失败！！";
	}

	@ApiOperation(value = "根据id删除记录", notes = "根据id删除记录")
	@RequestMapping(value = "delete", method = RequestMethod.DELETE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
	})
	public String delete(@RequestParam String index, @RequestParam String type, String id){

		try {
			if ("".equals(id) || id == null) {
				return "请输入完整的信息！！";
			}

			User t = new User();
			t.setId(Long.valueOf(id));

			//删除的时候先判断记录是否存在，如果存在就删除
			boolean isF = userService.exists(index, type, t);
			if (isF) {
				userService.delete(index, type, t.getId());

				return "删除记录成功！！";
			} else {
				return "记录不存在！！";
			}


		} catch (IOException e) {
			e.printStackTrace();
		}


		return "删除记录失败！！";
	}


	@ApiOperation(value = "查询全部", notes = "查询全部")
	@RequestMapping(value = "findAll", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
			@ApiImplicitParam(name = "begin", value = "第几页", defaultValue = "1"),
			@ApiImplicitParam(name = "size", value = "每页显示数据量", defaultValue = "10"),
	})
	public CommonResult findAll(@RequestParam String index, @RequestParam String type, Integer begin, Integer size){


		List<User> list = userService.find(index, type, begin, size,new SearchSourceBuilder());

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("count",list.size());
		map.put("rows", list);


		return CommonResult.buildSuccess("V1",map);
	}


	@ApiOperation(value = "根据名称查询", notes = "根据名称查询")
	@RequestMapping(value = "findByName", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
			@ApiImplicitParam(name = "begin", value = "第几页", defaultValue = "1"),
			@ApiImplicitParam(name = "size", value = "每页显示数据量", defaultValue = "10"),
			@ApiImplicitParam(name = "name", value = "名称"),
	})
	public CommonResult findByName(@RequestParam String index, @RequestParam String type,Integer begin, Integer size, String name){

		if ("".equals(name) || name == null) {
			CommonResult c = new CommonResult();
			c.setMessage("请输入查询name");
			c.setStatus_code("5001");
			return c;
		}

		Map<String, Object> map = null;

		List<User> list = null;
		try {
			list = userService.search(index, type, begin, size, name);

		} catch (IOException e) {
			e.printStackTrace();
		}

		map = new HashMap<String, Object>();

		map.put("count",list.size());
		map.put("rows", list);


		return CommonResult.buildSuccess("V1",map);
	}

	@ApiOperation(value = "批量添加/更新", notes = "批量添加/更新")
	@RequestMapping(value = "bulkAdd", method = RequestMethod.POST)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
			@ApiImplicitParam(name = "dataStr", value = "list类型的数据"),
	})
	public String bulkAdd(@RequestParam String index, @RequestParam String type, String dataStr){

		List listStr = JsonUtils.toList(dataStr);

		List<User> list = new ArrayList<>();

		//遍历集合中的字符串，将它转换成对象并放到新的集合中
		for (int i = 0; i < listStr.size(); i++) {
			String t = listStr.get(i).toString();
			list.add(JsonUtils.toObject(t, User.class));
		}

		try {

			userService.bulkAdd(index, type, list);

			return "批量添加/更新成功！！";
		} catch (IOException e) {
			e.printStackTrace();
		}


		return "批量添加/更新失败！！";
	}

	@ApiOperation(value = "批量删除", notes = "批量删除/更新")
	@RequestMapping(value = "bulkDelete", method = RequestMethod.DELETE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
			@ApiImplicitParam(name = "dataStr", value = "list类型的数据"),
	})
	public String bulkDelete(@RequestParam String index, @RequestParam String type, String dataStr){

		List listStr = JsonUtils.toList(dataStr);

		List<User> list = new ArrayList<>();

		//遍历集合中的字符串，将它转换成对象并放到新的集合中
		for (int i = 0; i < listStr.size(); i++) {
			String t = listStr.get(i).toString();
			list.add(JsonUtils.toObject(t, User.class));
		}

		try {

			userService.bulkDelete(index, type, list);

			return "批量删除成功！！";
		} catch (IOException e) {
			e.printStackTrace();
		}


		return "批量删除失败！！";
	}

	@ApiOperation(value = "根据id进行范围查找", notes = "根据id进行范围查找")
	@RequestMapping(value = "findByIdRange", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "index", value = "索引", defaultValue = "index_test"),
			@ApiImplicitParam(name = "type", value = "文档", defaultValue = "_doc"),
			@ApiImplicitParam(name = "begin", value = "第几页", defaultValue = "1"),
			@ApiImplicitParam(name = "size", value = "每页显示数据量", defaultValue = "10"),
			@ApiImplicitParam(name = "start", value = "开始位置", defaultValue = "0"),
			@ApiImplicitParam(name = "end", value = "结束位置", defaultValue = "10"),
	})
	public CommonResult findByIdRange(@RequestParam String index, @RequestParam String type,Integer begin, Integer size, String start, String end){

		Map<String, Object> map = null;

		List<User> list = userService.findByIdRange(index, type, begin, size, start, end);


		map = new HashMap<String, Object>();

		map.put("count",list.size());
		map.put("rows", list);

		return CommonResult.buildSuccess("V1",map);

	}

}
