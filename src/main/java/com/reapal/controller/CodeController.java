package com.reapal.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.reapal.dao.DbConfigDao;
import com.reapal.dao.TableStrategyConfigDao;
import com.reapal.model.ColumnInfo;
import com.reapal.model.DbConfig;
import com.reapal.model.TableInfo;
import com.reapal.model.TableStrategyConfig;
import com.reapal.service.CodeService;
import com.reapal.utils.FileUtils;
import com.reapal.utils.ZipFileUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping
public class CodeController extends BaseController{

	@Autowired
	private CodeService codeService;

	@Autowired
	private DbConfigDao dbConfigDao;

	@Autowired
	private TableStrategyConfigDao tableStrategyConfigDao;

	@RequestMapping("/index")
	public String init(){
		if(request.getSession().getAttribute("user") == null){
			return "redirect:/login";
		}
		return "/views/index/index";
	}

	@GetMapping("/getDbList")
	@ResponseBody
	public JSONObject getDbList(Model model){
		List<DbConfig> dbConfigList = dbConfigDao.findAll();
		return respJson(0,null,dbConfigList);
	}

	@GetMapping("/getByDbId")
	@ResponseBody
	public JSONObject getByDbId(Long id) throws IOException {
		DbConfig dbConfig = dbConfigDao.getOne(id);
		return respJson(0, "", dbConfig);
	}

	/**
	 * 显示Table列表
	 */
	@RequestMapping(value = "/database-list",method=RequestMethod.GET)
	public String databaseList(Model model,DbConfig dbConfig){
		return "/views/db/database_list";
	}

	/**
	 * 显示Table列表
	 */
	@RequestMapping(value = "/edit",method=RequestMethod.PUT)
	@ResponseBody
	public JSONObject edit(@RequestBody DbConfig dbConfig){
		dbConfigDao.save(dbConfig);
		return respJson(0, "修改成功", true);
	}

	/**
	 * 测试数据库配置
	 */
	@ResponseBody
	@RequestMapping(value = "/test",method=RequestMethod.POST)
	public JSONObject test( DbConfig dbConfig){
		String result = codeService.testConnection(dbConfig);
		if(StringUtils.isEmpty(result)){
			return respJson(0,"测试成功",result);
		}else{
			return respJson(1,"数据库链接失败",result);
		}
	}

	/**
	 * 显示Table列表
	 */
	@RequestMapping(value = "/save",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject save(@RequestBody DbConfig dbConfig){
		dbConfigDao.save(dbConfig);
		return respJson(0,"添加成功",true);
	}

	/**
	 * 显示Table列表
	 */
	@RequestMapping(value = "/delete",method=RequestMethod.GET)
	@ResponseBody
	public JSONObject delete(Model model,DbConfig dbConfig){
		dbConfigDao.delete(dbConfig.getId());
		return respJson(0, "删除成功", true);
	}

	@GetMapping("/to-table-list")
	public String toTableList(Model model) throws IOException {
		return "/views/db/tables_list";
	}

	@GetMapping("/table-list")
	@ResponseBody
	public JSONObject tableList(Long id) throws IOException {
		DbConfig dbConfig = dbConfigDao.getOne(id);
		List<TableInfo> tableList = codeService.getAllTables(dbConfig);
		return respJson(0, "succ", tableList);
	}

	@GetMapping("/to-column-list")
	public String toColumnList(){
		return "/views/db/column_list";
	}

	@GetMapping("/column-list")
	@ResponseBody
	public JSONObject columnList(String tableName,Long id) throws IOException {
		DbConfig dbConfig = dbConfigDao.getOne(id);
		TableInfo tableInfo = codeService.getAllColumns(tableName,dbConfig);
		return respJson(0, "succ", tableInfo);
	}

	@GetMapping("/get-table-strategy")
	@ResponseBody
	public JSONObject getTableStrategy(String tableName,Long id) throws IOException {
		return respJson(0, "succ", tableStrategyConfigDao.findByDbIdAndTableName(id,tableName));
	}

	/**
	 * 保存配置信息
	 */
	@RequestMapping(value = "/columnsave",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject save(Long id , String tableName,String comments , TableStrategyConfig tableStrategyConfig){
		String[] remarks = request.getParameterValues("remarks[]");
		DbConfig dbConfig = dbConfigDao.getOne(id);
		TableInfo tableInfo = new TableInfo();
		tableInfo.setTableName(tableName);
		tableInfo.setComments(comments);
		List<ColumnInfo> listItem = new ArrayList<ColumnInfo>();
		for(String remark:remarks){
			System.out.println(remark);
			String[] mark = remark.split("@");
			ColumnInfo item = new ColumnInfo();
			item.setColName(mark[0]);
			item.setColType(mark[1]);
			if(mark.length >= 3) {
				item.setComments(mark[2]);
			}
			if (mark.length >= 4) {
				item.setExtra(mark[3]);
			}
			if (mark.length >= 5) {
				item.setNullable("true".equalsIgnoreCase(mark[4]));
			}
			if (mark.length >= 6) {
				item.setDefaultValue(mark[5]);
			}
			listItem.add(item);
		}
		tableInfo.setListColumn(listItem);
		codeService.saveComment(tableInfo,dbConfig);
		tableStrategyConfig.setDbId(id);
		tableStrategyConfig.setTableName(tableName);
		tableStrategyConfigDao.save(tableStrategyConfig);
		return respJson(0, "保存成功", true);
	}

	/**
	 * 生成代码
	 */
	@RequestMapping(value="/generate",method=RequestMethod.GET)
	public String generate(String tableName, Long id, HttpServletResponse response) throws IOException {
		DbConfig dbConfig = dbConfigDao.getOne(id);
		TableInfo tableInfo = codeService.getAllColumns(tableName,dbConfig);
        TableStrategyConfig tableStrategyConfig = tableStrategyConfigDao.findByDbIdAndTableName(id, tableName);
		String model = tableInfo.getComments().substring(tableInfo.getComments().indexOf("#")+1);
		AutoGenerator mpg = new AutoGenerator();
		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(tableStrategyConfig.getProjectPath());
		gc.setFileOverride(true);
		gc.setActiveRecord(true);
		gc.setEnableCache(false);// XML 二级缓存
		gc.setBaseResultMap(true);// XML ResultMap
		gc.setBaseColumnList(false);// XML columList
		gc.setAuthor(tableStrategyConfig.getAuthor());
		gc.setEntityName(tableStrategyConfig.getEntityName());
		gc.setMapperName(tableStrategyConfig.getMapperName());
		gc.setXmlName(tableStrategyConfig.getXmlName());
		gc.setServiceName(tableStrategyConfig.getServiceName());
		gc.setServiceImplName(tableStrategyConfig.getServiceImplName());
		gc.setOpen(false);//是否生成后打开文件
		mpg.setGlobalConfig(gc);
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(dbConfig.getDriver().indexOf("mysql")>-1?DbType.MYSQL:DbType.ORACLE);
		dsc.setDriverName(dbConfig.getDriver());
		dsc.setUsername(dbConfig.getUsername());
		dsc.setPassword(dbConfig.getPassword());
		dsc.setUrl(dbConfig.getUrl());
		mpg.setDataSource(dsc);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		// strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
		if(!StringUtils.isEmpty(tableStrategyConfig.getPrefix())) {
            strategy.setTablePrefix(tableStrategyConfig.getPrefix());
		}
		strategy.setInclude(new String[] { tableInfo.getTableName() }); // 需要生成的表
		// strategy.setExclude(new String[]{"test"}); // 排除生成的表
		// 字段名生成策略
		strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		// 自定义实体，公共字段
		// strategy.setSuperEntityColumns(new String[] { "test_id", "age" });
		// 自定义 mapper 父类
		// strategy.setSuperMapperClass("com.baomidou.demo.TestMapper");
		// 自定义 service 父类
		// strategy.setSuperServiceClass("com.baomidou.demo.TestService");
		// 自定义 service 实现类父类
		// strategy.setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl");
		// 自定义 controller 父类
		// strategy.setSuperControllerClass("com.baomidou.demo.TestController");
		// 【实体】是否生成字段常量（默认 false）
		// public static final String ID = "test_id";
		// strategy.setEntityColumnConstant(true);
		// 【实体】是否为构建者模型（默认 false）
		// public User setName(String name) {this.name = name; return this;}
		// strategy.setEntityBuliderModel(true);
		mpg.setStrategy(strategy);
		// 包配置
		PackageConfig pc = new PackageConfig();
//		pc.setParent("src.main");
//		pc.setModuleName(tableStrategyConfig.getModelName());
//		pc.setEntity("java."+tableStrategyConfig.getEntityPackage());
//		pc.setService("java."+tableStrategyConfig.getServicePackage());
//		pc.setServiceImpl("java."+tableStrategyConfig.getServiceImplPackage());
//		pc.setMapper("java."+tableStrategyConfig.getMapperPackage());
//		pc.setXml("resources."+tableStrategyConfig.getMapperXmlPackage());
//		pc.setController("java."+tableStrategyConfig.getControllerPackage());

		pc.setParent(tableStrategyConfig.getModelName());
		pc.setModuleName(null);
		pc.setEntity(tableStrategyConfig.getEntityPackage());
		pc.setService(tableStrategyConfig.getServicePackage());
		pc.setServiceImpl(tableStrategyConfig.getServiceImplPackage());
		pc.setMapper(tableStrategyConfig.getMapperPackage());
		//xml代替vo路径
		pc.setXml("resources."+tableStrategyConfig.getMapperXmlPackage());
		pc.setController(tableStrategyConfig.getControllerPackage());

		mpg.setPackageInfo(pc);
		// 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值 这边可以自定义注入一些值
//		InjectionConfig cfg = new InjectionConfig() {
//			@Override
//			public void initMap() {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
//				map.put("entityLombokModel",true);
//				map.put("restControllerStyle",true);
//				this.setMap(map);
//			}
//		};
		mpg.setCfg(customerConfig(tableStrategyConfig));
		// 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/template 下面内容修改，
		// 放置自己项目的 src/main/resources/template 目录下, 默认名称一下可以不配置，也可以自定义模板名称
		 TemplateConfig tc = new TemplateConfig();
		 tc.setController("/templates/sdzb-template/controller.java.vm");
		 //不生成的设置null
		 tc.setMapper(null);
		 tc.setXml(null);
		 tc.setService("/templates/sdzb-template/service.java.vm");
		 tc.setServiceImpl("/templates/sdzb-template/serviceImpl.java.vm");
		 //vo模板
		 tc.setEntity(null);
		 mpg.setTemplate(tc);
		// 执行生成
		mpg.execute();
		// 打印注入设置
		System.err.println(mpg.getCfg().getMap().get("abc"));

//		//打包下载
//		response.setContentType("APPLICATION/OCTET-STREAM");
//		response.setHeader("Content-Disposition","attachment; filename=src.zip");
//		try {
//			ZipFileUtils zip = new ZipFileUtils();
//			ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
//			String fileName = request.getSession().getServletContext().getRealPath("/")+"/WEB-INF/upload/"+request.getSession().getId();
//			File ff = new File(fileName);
//			if(!ff.exists()){
//				ff.mkdirs();
//			}
//			zip.zip(ff,zos,"");
//
//			zos.flush();
//			zos.close();
//
//			//删除目录
//			FileUtils.DeleteFolder(request.getSession().getServletContext().getRealPath("/")+"/WEB-INF/upload/"+request.getSession().getId());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return "/views/ciwei/index";
	}

	/**
	 * 自定义生成文件 生成dto vo这些东西呀
	 */
	private static InjectionConfig customerConfig(TableStrategyConfig tableStrategyConfig) {
		InjectionConfig config = new InjectionConfig() {
			@Override
			public void initMap() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
				map.put("entityLombokModel",true);
				map.put("restControllerStyle",true);
				this.setMap(map);
			}
		};

		List<FileOutConfig> files = new ArrayList<FileOutConfig>();
		files.add(new FileOutConfig("/templates/sdzb-template/entityvo.java.vm") {
			@Override
			public String outputFile(com.baomidou.mybatisplus.generator.config.po.TableInfo tableInfo) {
				String expand = tableStrategyConfig.getProjectPath() + File.separator + tableStrategyConfig.getModelName().replace("." ,File.separator) + File.separator + tableStrategyConfig.getEntityPackage() + File.separator + "vo";
				String entityFile = String.format((expand + File.separator + "%s" + ".java"), tableInfo.getEntityName() + "Vo");
				return entityFile;
			}
		});
		config.setFileOutConfigList(files);
		return config;
	}

}

