### mybatisplus-generator-web
基于mybatisplus的web代码生成工具

### 项目特色
- 该项目运行无需任何数据库链接
- 数据源配置记录在属性文件中，项目重启也不会丢失
- 基于mybatispluls的代码工具编写，同公司人员无需在本机运行main方法，可以方便统一使用该web项目
- 漂亮的登录页面（amaze云适配）

### 修改的地方

启动项目后需要修改数据库中存放的连接地址 界面修改会有问题 不想解决呀

项目路径：C:\Users\Administrator\Desktop\demo\src\main\java

包名：com.example.demo

其他可以不修改 此项目定制化的模板生成 所以需要自己写模板

如果生成除了controller xml mapper model service serviceimpl外的接口 需要在这里重新定义

CodeController中的customerConfig()方法添加实现 目前已经有个vo的例子了 files.add添加方法呀

initMap中是自定义的参数 可以直接使用${xxx}获取

模板中一些常用的参数呀：[模板使用到的参数](vm.md)

![avatar](/img/mybatisplus-web.png)