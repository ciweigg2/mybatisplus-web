//js部分:2018-08-01T17:34:48.155+08:00
$UU.init({
    data: {
        loading: false,
        dialogFormVisible: false,
        importFormVisible: false,
        dialogTitle: "",
        search_group: {
            id:'',
            dbName: '',
            url: "",
            driver: "",
            username: "",
            password: "",
            schema: null,
            catalog: null,
            dbType:null
        },
        data_group: {
            active: 'active',
            list: [{}, {}, {}],
            multipleSelection: [],
            multipleTable: [],
            //分页
            pagination: {
                index: 1, //当前页码
                size: 10, //每页记录数
                total: 100 //记录总数
            }
        },
        form: {
            id:null,
            dbName: null,
            url: "",
            driver: "",
            username: "",
            password: "",
            schema: null,
            catalog: null,
            dbType:null
        },
        rules: {
            dbName: [
                { required: true, message: '请输入应用名', trigger: 'blur' }
            ],
            url: [
                { required: true, message: '请输入事件id', trigger: 'blur' }
            ],
            driver: [
                { required: true, message: '请输入事件名称', trigger: 'blur' }
            ],
            username: [
                { required: true, message: '请输入事件内容', trigger: 'blur' }
            ],
            password: [
                { required: true, message: '请输入异常信息', trigger: 'blur' }
            ],
            schema: [
                { required: true, message: '请选择发生时间', trigger: 'blur' }
            ],
            catalog: [
                { required: true, message: '请输入在哪发生的(ip:port)', trigger: 'blur' }
            ],
            dbType: [
                { required: true, message: '请输入是否已传播出去0否1是', trigger: 'blur' }
            ],
        }
    },
    created: function () {
        this.search_group.id = $UF.getUrlParam("id");
        console.log("314314321",this.form)
        this.query();
    },
    mounted: function () {
        console.log("vue mounted");
    },
    watch: {
        //监听数据
        "$data.data_group.multipleSelection": function (multipleSelection) {
            if (multipleSelection.toString().length > 0) {
                this.search_group.btn_disabled = false;
            } else {
                this.search_group.btn_disabled = true;
            }
        }
    },
    methods: {
        resetForm: function (formName) {
            var _this = this;
            _this.$refs[formName].resetFields();
            _this.search_group.vinListStr = '';
        },
        onCurrentPageChange: function (index) {
            var _this = this;
            _this.data_group.pagination.index=index;
            this.query();
        },
        onPageSizeChange: function (size) {
            var _this = this;
            _this.data_group.pagination.size=size;
            this.query();
        },
        query: function () {
           var _this = this;
            //请求参数
            var req = _this.search_group;
            $UU.http.get("/table-list",
                req
                , function (response) {
                    //获取回调数据
                    console.log(response.data);
                    _this.data_group.list = response.data.data;
                }, {
                    requestBody: true,
                    before: function () {
                        _this.btn_disabled = true;
                    },
                    after: function () {
                        _this.btn_disabled = false;
                    }
                });
        },
        openDialog: function (type,dbName) {
            var _this = this;
            if (type === 'new') {
                _this.dialogFormVisible = true;
                _this.dialogTitle = "新增";
                _this.form = {
                    id:null,
                    dbName: null,
                    url: "",
                    driver: "",
                    username: "",
                    password: "",
                    schema: null,
                    catalog: null,
                    dbType:null
                }
            } else if (type === 'edit') {
                _this.dialogFormVisible = true;
                _this.dialogTitle = "编辑";
                var id = dbName;
                _this.getById(id);
            } else if (type === 'delete') {
                if (_this.data_group.multipleSelection.length === 0) {
                    _this.$message.warning("请选择一条数据！");
                }else {
                    var ids = '';
                    for(var i=0;i<_this.data_group.multipleSelection.length;i++){
                        if(i==0){
                            ids = _this.data_group.multipleSelection[i].id;
                        }else{
                            ids += ","+_this.data_group.multipleSelection[i].id;
                        }
                    }
                    _this.deleteById(ids);
                }
            }
        },
        submitForm: function (formName) {
            var _this = this;
            _this.$refs[formName].validate(function (valid) {
                if (valid) {
                    _this.$confirm('确定操作吗?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(function () {
                        if (_this.dialogTitle === "新增") {
                            _this.save();
                        } else if (_this.dialogTitle === "编辑") {
                            _this.update();
                        }
                    }).catch(function () {
                    });
                }
            });
        },
        onSelectionChange: function (val) {
            this.data_group.multipleSelection = val;
        },
        testConn: function(){
            var _this = this;
            $UU.http.post("/test",_this.form,function (response){
                if(response.data.code === 0){
                    _this.$message.success(response.data.msg);
                }else{
                    _this.$message.error(response.data.msg);
                }
            },{
                
            })
        },
        getById: function (id) {
            var _this = this;
            //请求参数
            var req = {};
            $UU.http.get("/getByDbName?dbName=" + id,
                req
                , function (response) {
                    //获取回调数据
                    console.log(response.data);
                    _this.form = response.data.data;
                }, {
                    requestBody: true,
                    before: function () {
                        _this.btn_disabled = true;
                    },
                    after: function () {
                        _this.btn_disabled = false;
                    }
                });
        },
        deleteById: function (row) {
            var _this = this;
            _this.$confirm('确定操作吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function () {
                //请求参数
                var req = {};
                $UU.http.get("/delete?dbName=" + row.dbName,
                    req
                    , function (response) {
                        //获取回调数据
                        console.log(response.data);
                        if (response.data.code === 0) {
                            _this.query();
                        } else {
                            _this.$message.error(response.data.msg);
                        }
                    });
            }).catch(function () {
            });
        },
        setData: function () {
            var _this = this;
        },
        save: function () {
            var _this = this;
            console.info(_this.form);
            $UU.http.post("/save",
                _this.form
                , function (response) {
                    //获取回调数据
                    console.log(response.data);
                    if (response.data.code === 0) {
                        _this.$message.success(response.data.msg);
                        _this.dialogFormVisible = false;
                        _this.query();
                    } else {
                        _this.$message.error(response.data.msg);
                    }
                }, {
                    requestBody: true,
                    before: function () {
                        _this.btn_disabled = true;
                    },
                    after: function () {
                        _this.btn_disabled = false;
                    }
                });
        },
        update: function () {
            var _this = this;
            _this.setData();
            $UU.http.put("/edit",
                _this.form
                , function (response) {
                    //获取回调数据
                    console.log(response.data);
                    if (response.data.code === 0) {
                        _this.$message.success(response.data.msg);
                        _this.dialogFormVisible = false;
                        _this.query();
                    } else {
                        _this.$message.error(response.data.msg);
                    }
                }, {
                    requestBody: true,
                    before: function () {
                        _this.btn_disabled = true;
                    },
                    after: function () {
                        _this.btn_disabled = false;
                    }
                });
        },
        handleCurrentChange: function (val) {
            if (null !== val) {
                this.data_group.multipleSelection = val.id;
            }
        },
        showColumns: function (tableName,comments) {
            var _this = this;
            console.log(543524, tableName);
            location.href = $UC.ctxPath +"/to-column-list?id="+_this.search_group.id+"&tableName="+tableName+'&comments='+comments;
        }
    }
});

