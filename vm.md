${author} 作者
${date} 日期

${table.comment} 表注释
${table.controllerName} DbConfigController
${table.entityPath} dbConfig首字母小写表名
${table.serviceName} DbConfigService 首字母大写Service
${table.entityName} 首字母大写表名
${table.mapperName} 首字母大写mapper

使用需要先设置#set($lowerCaseServiceName = $table.serviceName.substring(0,1).toLowerCase() + $table.serviceName.substring(1,$table.serviceName.length()))
${lowerCaseServiceName} 首字母小写Service
使用需要先设置#set($lowrEntity = $table.entityName.substring(0,1).toLowerCase() + $table.entityName.substring(1,$table.entityName.length()))
${lowrEntity} 首字母小写表名

${entity} 首字母大写表名

package所有的参数可以参考TemplateConfig对象的属性
${package.Service} service包路径
${package.Entity} 对象包路径
${package.Controller} controller包路径
${package.Mapper} Mapper包路径
${package.Mapper}.${table.mapperName} Mapper使用的路径

imports
${package.Entity}.${entity} 引用的对象路径
${package.Service}.${table.serviceName} 引用的service路径
