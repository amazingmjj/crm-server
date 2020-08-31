--菜单初始化数据
insert into t_menu(id, name, icon_class, create_at, update_at,fact_order) values(1, '首页', 'el-icon-menu', sysdate, sysdate, 1);
insert into t_menu(id, name, icon_class, create_at, update_at,fact_order) values(2, '客服中心', 'iconfont icon-call-center', sysdate, sysdate, 2);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(3, '客户管理', 'iconfont icon-custom', sysdate, sysdate, 3);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(4, '统计分析', 'iconfont icon-statistic', sysdate, sysdate, 4);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(5, '销售管理', 'iconfont icon-sales', sysdate, sysdate, 5);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(6, '营销中心', 'iconfont icon-marketing', sysdate, sysdate, 6);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(7, '系统设置', 'iconfont icon-setting', sysdate, sysdate, 8);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(8, '基础数据', 'iconfont icon-basic-data', sysdate, sysdate, 9);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(9, '潜在客户', '/customManager/potentialCustom', 3, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(10, '正式客户', '/customManager/formalCustom', 3, sysdate, sysdate, 2);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(11, '个人信息', '/setting/profile', 7, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(12, '账号管理', '/setting/acctManager', 7, sysdate, sysdate, 2);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(13, '权限设置', '/setting/authManager', 7, sysdate, sysdate, 3);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(14, '组织架构', '/setting/orgStructure', 7, sysdate, sysdate, 4);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(15, '业务关系', '/basicData/busiRelation', 8, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(16, '客户性质', '/basicData/customProperty', 8, sysdate, sysdate, 2);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(17, '物资品类', '/basicData/supplyCatalog', 8, sysdate, sysdate, 3);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(18, '物资用途', '/basicData/purpose', 8, sysdate, sysdate, 4);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(19, '加工需求', '/basicData/processReq', 8, sysdate, sysdate, 5);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(20, '公共客户', '/customManager/publicCustom', 3, sysdate, sysdate, 3);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(21, '联系人管理', '/customManager/contactManager', 3, sysdate, sysdate, 4);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(22, '客户拜访', '/customManager/customerVisit', 3, sysdate, sysdate, 5);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(23, '客户画像', '/customManager/customerPortrait', 3, sysdate, sysdate, 6);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(24, '基本情况', '/dashboard', 1, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(25, '客户分级', '/customManager/customerRating', 3, sysdate, sysdate, 7);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(26, '分级系数', '/setting/gradingFactor', 7, sysdate, sysdate, 5);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(27, '任务设置', '/setting/taskSetting', 7, sysdate, sysdate, 6);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(28, '短信中心', '/callCenter/sms', 2, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(29, '商机管理', '/customManager/busiOppty', 3, sysdate, sysdate, 8);
insert into t_menu(id, name, icon_class, create_at, update_at, fact_order) values(30, '行政人资', 'iconfont icon-hr', sysdate, sysdate, 7);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(31, '短信收发', '/hrManager/sms', 30, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(32, '所处行业', '/basicData/industry', 8, sysdate, sysdate, 6);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(33, '销售报表', '/salesManage/reports', 5, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(34, '交易跟踪', '/salesManage/tracking', 5, sysdate, sysdate, 2);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(35, '产品资源', '/salesManage/resources', 5, sysdate, sysdate, 3);
insert into t_menu(id, name, icon_class, create_at, update_at,fact_order) values(36, '任务管理', 'iconfont icon-task-line', sysdate, sysdate, 10);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(37, '任务信息', '/taskManage/taskInfo', 36, sysdate, sysdate, 1);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(38, '项目设置', '/taskManage/projectSetting', 36, sysdate, sysdate, 2);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(39, '类型设置', '/taskManage/typeSetting', 36, sysdate, sysdate, 3);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(40, '客户合并', '/customManager/customerCombine', 3, sysdate, sysdate, 9);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(41, '采购供应商', '/customManager/supplier', 3, sysdate, sysdate, 10);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(42, '登录记录', '/setting/loginMsg', 7, sysdate, sysdate, 7);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(43, '期货管理', '/salesManage/futures', 5, sysdate, sysdate, 4);
update t_menu set page_url='/salesManage/salesReport' where name='销售报表';
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(44, '业务员提成汇总表', '/salesManage/reports/empCommSummary', 5, sysdate, sysdate, 5);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(45, '销售专员提成汇总表', '/salesManage/reports/saleCommSummary', 5, sysdate, sysdate, 6);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(46, '直发调货明细表', '/salesManage/reports/deliveryDetail', 5, sysdate, sysdate, 7);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(47, '高卖明细表', '/salesManage/reports/highSellDetail', 5, sysdate, sysdate, 8);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(48, '新增客户业务明细表', '/salesManage/reports/newCustomer', 5, sysdate, sysdate, 9);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(49, '销售专员新增客户明细表', '/salesManage/reports/saleNewCustomer', 5, sysdate, sysdate, 10);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(50, '业务员新增客户明细表', '/salesManage/reports/empNewCustomer', 5, sysdate, sysdate, 11);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(51, '销售其他销量表', '/salesManage/reports/saleOtherWeight', 5, sysdate, sysdate, 12);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(52, '客户评估', '/customManager/customerEvalution', 3, sysdate, sysdate, 11);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(53, '销售绩效提成报表', '/salesManage/reports/salePerformance', 5, sysdate, sysdate, 13);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(54, '超期未提仓储费', '/salesManage/reports/overdueFee', 5, sysdate, sysdate, 14);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(55, '超期客户', '/salesManage/reports/purchaseFrequency', 5, sysdate, sysdate, 15);
insert into t_menu(id, name, page_url, parent_id, create_at, update_at, fact_order) values(56, '销售绩效新客户明细表', '/salesManage/reports/newSalePerformance', 5, sysdate, sysdate, 16);
commit ;

--机构初始化数据
insert into t_organization(id, name, simple_name, STATUS, create_at, update_at) values(1, '江苏智恒达型云网络科技有限公司', '型云',1, sysdate, sysdate);
insert into t_organization(id, name, simple_name, STATUS, create_at, update_at) values(2, '江苏智恒达机械科技有限公司', '智恒达', 1, sysdate, sysdate);
insert into t_organization(id, name, simple_name, STATUS, create_at, update_at) values(3, '江苏智恒达进出口有限公司', '进出口', 1, sysdate, sysdate);
insert into t_organization(id, name, simple_name, STATUS, create_at, update_at) values(4, '江苏智恒达投资管理有限公司', '投资管理', 1, sysdate, sysdate);
insert into t_organization(id, name, simple_name, STATUS, create_at, update_at) values(5, '江苏智恒达投资集团合肥有限公司', '智恒达合肥', 1, sysdate, sysdate);
insert into t_organization(id, name, simple_name, STATUS, create_at, update_at) values(6, '江苏岳洋通金属加工有限公司', '岳洋通', 1, sysdate, sysdate);
commit ;

--部门初始化数据
insert into t_dpt(id, name, fk_org_id, STATUS, create_at, update_at, leader) values(1, '电商平台服务部', 1, 1, sysdate, sysdate,'曹玉林');
commit ;

--角色初始化数据
insert into t_role(id, name, create_at, update_at) values (1, 'superadmin', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (2, '系统管理员', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (3, '总经理', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (4, '分管副总', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (5, '部长', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (6, '销售内勤', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (7, '业务员', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (8, '人力资源', sysdate, sysdate);
insert into t_role(id, name, create_at, update_at) values (9, '财务', sysdate, sysdate);
commit ;

--角色权限初始化数据
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (1, sysdate, sysdate, 1, 1, 1, 9);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (2, sysdate, sysdate, 1, 1, 1, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (3, sysdate, sysdate, 1, 1, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (4, sysdate, sysdate, 0, 0, 1, 12);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (5, sysdate, sysdate, 1, 1, 1, 13);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (6, sysdate, sysdate, 0, 0, 1, 14);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (7, sysdate, sysdate, 1, 1, 1, 15);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (8, sysdate, sysdate, 1, 1, 1, 16);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (9, sysdate, sysdate, 1, 1, 1, 17);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (10, sysdate, sysdate, 1, 1, 1, 18);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (11, sysdate, sysdate, 1, 1, 1, 19);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (12, sysdate, sysdate, 1, 1, 1, 20);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (13, sysdate, sysdate, 1, 1, 1, 21);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (14, sysdate, sysdate, 1, 1, 1, 22);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (15, sysdate, sysdate, 1, 1, 1, 23);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (16, sysdate, sysdate, 1, 1, 1, 25);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (17, sysdate, sysdate, 1, 1, 1, 9);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (18, sysdate, sysdate, 1, 1, 1, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (19, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (20, sysdate, sysdate, 0, 0, 0, 12);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (21, sysdate, sysdate, 1, 0, 1, 13);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (22, sysdate, sysdate, 0, 0, 0, 14);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (23, sysdate, sysdate, 1, 0, 1, 15);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (24, sysdate, sysdate, 1, 0, 1, 16);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (25, sysdate, sysdate, 1, 0, 1, 17);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (26, sysdate, sysdate, 1, 0, 1, 18);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (27, sysdate, sysdate, 1, 0, 1, 19);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (28, sysdate, sysdate, 1, 1, 1, 20);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (29, sysdate, sysdate, 1, 1, 1, 21);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (30, sysdate, sysdate, 1, 1, 1, 22);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (31, sysdate, sysdate, 0, 0, 0, 23);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (32, sysdate, sysdate, 0, 0, 0, 25);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (33, sysdate, sysdate, 1, 1, 1, 9);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (34, sysdate, sysdate, 1, 1, 1, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (35, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (36, sysdate, sysdate, 0, 0, 0, 12);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (37, sysdate, sysdate, 0, 0, 0, 13);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (38, sysdate, sysdate, 0, 0, 0, 14);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (39, sysdate, sysdate, 1, 0, 1, 15);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (40, sysdate, sysdate, 1, 0, 1, 16);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (41, sysdate, sysdate, 1, 0, 1, 17);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (42, sysdate, sysdate, 1, 0, 1, 18);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (43, sysdate, sysdate, 1, 0, 1, 19);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (44, sysdate, sysdate, 1, 1, 1, 20);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (45, sysdate, sysdate, 1, 1, 1, 21);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (46, sysdate, sysdate, 1, 1, 1, 22);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (47, sysdate, sysdate, 0, 0, 0, 23);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (48, sysdate, sysdate, 0, 0, 0, 25);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (49, sysdate, sysdate, 1, 1, 1, 9);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (50, sysdate, sysdate, 1, 1, 1, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (51, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (52, sysdate, sysdate, 1, 0, 0, 15);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (53, sysdate, sysdate, 1, 0, 0, 16);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (54, sysdate, sysdate, 1, 0, 0, 17);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (55, sysdate, sysdate, 1, 0, 0, 18);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (56, sysdate, sysdate, 1, 0, 0, 19);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (57, sysdate, sysdate, 1, 0, 0, 20);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (58, sysdate, sysdate, 1, 1, 1, 21);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (59, sysdate, sysdate, 1, 1, 1, 22);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (60, sysdate, sysdate, 0, 0, 0, 23);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (61, sysdate, sysdate, 1, 0, 1, 9);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (62, sysdate, sysdate, 1, 0, 1, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (63, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (64, sysdate, sysdate, 1, 0, 0, 15);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (65, sysdate, sysdate, 1, 0, 0, 16);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (66, sysdate, sysdate, 1, 0, 0, 17);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (67, sysdate, sysdate, 1, 0, 0, 18);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (68, sysdate, sysdate, 1, 0, 0, 19);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (69, sysdate, sysdate, 1, 0, 0, 20);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (70, sysdate, sysdate, 1, 0, 1, 21);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (71, sysdate, sysdate, 1, 1, 1, 22);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (72, sysdate, sysdate, 0, 0, 0, 23);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (73, sysdate, sysdate, 1, 0, 0, 9);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (74, sysdate, sysdate, 1, 0, 0, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (75, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (76, sysdate, sysdate, 1, 0, 0, 20);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (77, sysdate, sysdate, 1, 0, 0, 21);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (78, sysdate, sysdate, 1, 1, 1, 22);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (79, sysdate, sysdate, 0, 0, 0, 23);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (80, sysdate, sysdate, 1, 1, 1, 31);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (81, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (84, sysdate, sysdate, 0, 0, 0, 10);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (85, sysdate, sysdate, 0, 0, 1, 11);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (86, sysdate, sysdate, 0, 0, 0, 24);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (87, sysdate, sysdate, 0, 0, 0, 24);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (88, sysdate, sysdate, 0, 0, 0, 24);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (89, sysdate, sysdate, 0, 0, 0, 24);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (90, sysdate, sysdate, 0, 0, 0, 24);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (91, sysdate, sysdate, 0, 0, 0, 24);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (92, sysdate, sysdate, 0, 0, 0, 26);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (93, sysdate, sysdate, 0, 0, 0, 27);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (94, sysdate, sysdate, 1, 1, 1, 28);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (95, sysdate, sysdate, 1, 1, 1, 28);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (96, sysdate, sysdate, 1, 1, 1, 28);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (97, sysdate, sysdate, 1, 1, 1, 28);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (98, sysdate, sysdate, 1, 1, 1, 28);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (99, sysdate, sysdate, 1, 0, 0, 29);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (100, sysdate, sysdate, 1, 0, 0, 29);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (101, sysdate, sysdate, 1, 0, 0, 29);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (102, sysdate, sysdate, 1, 0, 0, 29);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (103, sysdate, sysdate, 1, 0, 0, 29);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (104, sysdate, sysdate, 1, 0, 0, 29);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (105, sysdate, sysdate, 1, 1, 1, 32);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (106, sysdate, sysdate, 1, 0, 1, 32);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (107, sysdate, sysdate, 1, 0, 1, 32);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (108, sysdate, sysdate, 1, 0, 0, 32);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (109, sysdate, sysdate, 1, 0, 0, 32);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (110, sysdate, sysdate, 1, 1, 1, 33);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (111, sysdate, sysdate, 1, 1, 1, 34);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (112, sysdate, sysdate, 1, 1, 1, 35);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (113, sysdate, sysdate, 1, 1, 1, 33);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (114, sysdate, sysdate, 1, 1, 1, 34);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (115, sysdate, sysdate, 1, 1, 1, 35);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (116, sysdate, sysdate, 1, 1, 1, 33);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (117, sysdate, sysdate, 1, 1, 1, 34);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (118, sysdate, sysdate, 1, 1, 1, 35);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (119, sysdate, sysdate, 1, 1, 1, 33);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (120, sysdate, sysdate, 1, 1, 1, 34);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (121, sysdate, sysdate, 1, 1, 1, 35);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (122, sysdate, sysdate, 1, 1, 1, 33);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (123, sysdate, sysdate, 1, 1, 1, 34);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (124, sysdate, sysdate, 1, 1, 1, 35);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (125, sysdate, sysdate, 1, 1, 1, 33);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (126, sysdate, sysdate, 1, 1, 1, 34);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (127, sysdate, sysdate, 1, 1, 1, 35);
insert into t_auth (ID, CREATE_AT, UPDATE_AT, HAS_CREATE, HAS_DELETE, HAS_UPDATE, FK_MENU_ID) values (128, sysdate, sysdate, 1, 0, 1, 28);

commit ;

--角色与权限关系初始化数据
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 1);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 2);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 3);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 4);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 5);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 6);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 7);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 8);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 9);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 10);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 11);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 12);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 13);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 14);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 15);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 16);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 86);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 92);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 93);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 94);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 99);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 105);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 110);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 111);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (2, 112);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 17);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 18);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 19);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 20);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 21);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 22);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 23);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 24);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 25);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 26);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 27);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 28);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 29);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 30);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 31);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 32);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 87);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 95);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 100);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 106);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 113);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 114);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (3, 115);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 33);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 34);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 35);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 36);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 37);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 38);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 39);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 40);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 41);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 42);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 43);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 44);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 45);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 46);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 47);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 48);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 88);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 96);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 101);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 107);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 116);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 117);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (4, 118);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 49);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 50);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 51);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 52);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 53);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 54);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 55);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 56);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 57);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 58);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 59);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 60);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 89);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 97);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 102);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 108);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 119);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 120);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (5, 121);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 61);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 62);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 63);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 64);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 65);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 66);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 67);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 68);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 69);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 70);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 71);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 72);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 90);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 98);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 103);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 109);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 122);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 123);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (6, 124);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 73);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 74);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 75);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 76);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 77);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 78);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 79);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 91);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 104);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 125);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 126);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 127);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (7, 128);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (8, 80);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (8, 81);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (9, 84);
insert into ref_role_auth (ROLE_ID, AUTHS_ID) values (9, 85);
commit ;

--超级管理员账号
insert into T_ACCOUNT(id, login_acct, name, pwd, phone, status,fk_dpt_id, FK_ROLE_ID, create_at, update_at, data_level) values(1, 'admin', '超级管理员', '1f82c942befda29b6ed487a51da199f78fce7f05', '88888888888', 1, 1, 1, sysdate, sysdate,'公司');
commit ;

--默认联系人
insert into T_LINKER (ID, CREATE_AT, UPDATE_AT, AGE, EDU, MAIN_STATUS, NAME, NATIVE_PLACE, OTHER_LINK_WAY, PHONE, POSITION, QQ_NO, REMARK, SEX, WB_NAME, WX_NO, CREATOR_ID) values (1, sysdate, sysdate, 0, null, 1, '幺福生', null, null, '15295188021', null, null, null, 1, null, null, 1);
commit ;

--业务关系
insert into t_busi_relation(id, name, create_at, update_at, status) values(1, '客户', sysdate, sysdate, 1);
insert into t_busi_relation(id, name, create_at, update_at, status) values(2, '供应商', sysdate, sysdate, 1);
insert into t_busi_relation(id, name, create_at, update_at, status) values(3, '费用单位', sysdate, sysdate, 1);
insert into t_busi_relation(id, name, create_at, update_at, status) values(4, '内部单位', sysdate, sysdate, 1);
insert into t_busi_relation(id, name, create_at, update_at, status) values(5, '货主', sysdate, sysdate, 1);
insert into t_busi_relation(id, name, create_at, update_at, status) values(6, '质押单位', sysdate, sysdate, 1);
commit ;

--客户性质
insert into t_custom_property(id, name, status, create_at, update_at) values(1, '经销商', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(2, '生产商', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(3, '终端客户', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(4, '协助单位', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(5, '供应商', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(6, '其他', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(7, '贸易商', 1, sysdate, sysdate);
insert into t_custom_property(id, name, status, create_at, update_at) values(8, '电商', 1, sysdate, sysdate);
commit ;

--加工需求
insert into t_process_requirement(id, name, status, create_at, update_at) values(1, '分条', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(2, '剪折', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(3, '折弯', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(4, '切割', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(5, '激光切割', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(6, '水刀切割', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(7, '等离子切割', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(8, '数控火焰切割', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(9, '焊接', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(10, '铆接', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(11, '锯床', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(12, '锻造', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(13, '定尺锯', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(14, '冲压', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(15, '机加工', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(16, '铣/刨/镗', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(17, '车/钻/磨', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(18, '拉丝', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(19, '拉伸', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(20, '扩口', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(21, '抛光', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(22, '回火', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(23, '开槽', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(24, '车丝', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(25, '冲孔', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(26, '拉拔', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(27, '打砂', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(28, '镀锌', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(29, '涂塑', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(30, '上底漆', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(31, '磨砂', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(32, '喷砂', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(33, '防腐', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(34, '制管', 1, sysdate, sysdate);
insert into t_process_requirement(id, name, status, create_at, update_at) values(35, '高频焊接', 1, sysdate, sysdate);
commit ;

-- 客户分级系数基础数据
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(1, '系数公式', sysdate, sysdate, 0, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(2, '销量得分', sysdate, sysdate, 1, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(3, '地区得分', sysdate, sysdate, 2, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(4, '类型得分', sysdate, sysdate, 3, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(5, '欠款得分', sysdate, sysdate, 4, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(6, '高卖得分', sysdate, sysdate, 5, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(7, '频次得分', sysdate, sysdate, 6, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(8, '异常得分', sysdate, sysdate, 7, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(9, '正式/公共得分', sysdate, sysdate, 8, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(10, '品类得分', sysdate, sysdate, 9, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(11, '代开票得分', sysdate, sysdate, 10, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(12, '新旧客户得分', sysdate, sysdate, 11, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(13, '线上销量', sysdate, sysdate, 1, 1, '线上销量得分', 2);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(14, '线下销量', sysdate, sysdate, 2, 1, '线下销量得分', 2);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(15, '其他客户性质', sysdate, sysdate, 1, 1, '', 4);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(16, '经销商', sysdate, sysdate, 2, 1, '', 4);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(17, '终端客户', sysdate, sysdate, 3, 1, '', 4);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(18, '高卖金额', sysdate, sysdate, 1, 1, '高卖得分', 6);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(19, '成交次数', sysdate, sysdate, 1, 1, '频次得分', 7);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(20, '违约次数', sysdate, sysdate, 1, 1, '订单违约得分', 8);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(21, '恶意锁货次数', sysdate, sysdate, 2, 1, '恶意锁货得分', 8);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(22, '正式客户', sysdate, sysdate, 1, 1, '', 9);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(23, '公共客户', sysdate, sysdate, 2, 1, '', 9);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(24, 'Y', sysdate, sysdate, 1, 1, '', 11);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(25, 'N', sysdate, sysdate, 2, 1, '', 11);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(26, '新客户', sysdate, sysdate, 1, 1, '', 12);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(27, '老客户', sysdate, sysdate, 2, 1, '', 12);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(28, '其他地区', sysdate, sysdate, -1, 1, '', 3);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(29, '其他', sysdate, sysdate, -1, 1, '', 10);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient) values(30, '平台活跃得分', sysdate, sysdate, 12, 0);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(31, '日均登录次数', sysdate, sysdate, 1, 1, '平台活跃得分', 30);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(32, '0,1000', sysdate, sysdate, 1, 1, '', 5);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(33, '1001', sysdate, sysdate, 2, 1, '', 5);
insert into t_grade_coefficient(id, name, create_at, update_at, name_order, coefficient, EQUATION_NAME, PARENT_ID) values(34, '取消次数', sysdate, sysdate, 3, 1, '取消得分', 8);
commit ;

-- 字典数据
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (1, sysdate, sysdate, '薄', '默认', 0, 'B');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (2, sysdate, sysdate, '艾', '默认', 0, 'A');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (3, sysdate, sysdate, '拗', '默认', 0, 'A');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (4, sysdate, sysdate, '阿', '默认', 0, 'A');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (5, sysdate, sysdate, '长', '默认', 0, 'C');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (6, sysdate, sysdate, '长沙', null, 0, 'C');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (7, sysdate, sysdate, '长兴', null, 0, 'C');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (8, sysdate, sysdate, '无', '默认', 0, 'W');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (9, sysdate, sysdate, '广', '默认', 0, 'G');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (10, sysdate, sysdate, '夏', '默认', 0, 'X');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (11, sysdate, sysdate, '盛', '默认', 0, 'S');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (12, sysdate, sysdate, '乐', '默认', 0, 'L');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (13, sysdate, sysdate, '晟', '默认', 0, 'S');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (14, sysdate, sysdate, '栖', '默认', 0, 'Q');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (15, sysdate, sysdate, '朝', '默认', 0, 'C');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (16, sysdate, sysdate, '宿', '默认', 0, 'S');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (17, sysdate, sysdate, '句', '默认', 0, 'J');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (18, sysdate, sysdate, '沈', '默认', 0, 'S');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (19, sysdate, sysdate, '合', '默认', 0, 'H');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (20, sysdate, sysdate, '冯', '默认', 0, 'F');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (21, sysdate, sysdate, '厦', '默认', 0, 'X');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (22, sysdate, sysdate, '射', '默认', 0, 'S');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (23, sysdate, sysdate, '洽', '默认', 0, 'Q');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (24, sysdate, sysdate, '涡', '默认', 0, 'G');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (25, sysdate, sysdate, '万', '默认', 0, 'W');
insert into t_dictionary (ID, CREATE_AT, UPDATE_AT, NAME, REMARK, TYPE, VALUE) values (26, sysdate, sysdate, '么', '默认', 0, 'Y');
commit ;

-- 所属行业
insert into t_industry (ID, CREATE_AT, UPDATE_AT, NAME, STATUS) values (1, sysdate, sysdate, '互联网', 1);
insert into t_industry (ID, CREATE_AT, UPDATE_AT, NAME, STATUS) values (2, sysdate, sysdate, '物流/仓储', 1);
insert into t_industry (ID, CREATE_AT, UPDATE_AT, NAME, STATUS) values (3, sysdate, sysdate, '汽车生产', 1);
insert into t_industry (ID, CREATE_AT, UPDATE_AT, NAME, STATUS) values (4, sysdate, sysdate, '贸易/进出口', 1);
insert into t_industry (ID, CREATE_AT, UPDATE_AT, NAME, STATUS) values (5, sysdate, sysdate, '电子商务', 1);
commit ;

-- 客户列表视图 --crm
create or replace view v_cstm_list as
select cstm.id, cstm.comp_name, lk.name as linkName, lk.phone as linkPhone, cstm.create_at, cstm.bill_date, dpt.name as dptName, acct.name as acctName,
       (select at.name
          from t_account at
         where at.id = cstm.create_acct_id) as creatorName,
       cstm.mark as mark,
       org.id as orgId,
       dpt.id as dptId,
       acct.id as acctId,
       acct.platform_code as empCode,
       (select count(cc.id)
          from t_customer_call cc
         where cc.status in (2, 3, 4)
           and cc.customer_id = cstm.id) as visitCount,
       cstm.lock_status,
       cstm.update_at,
       cstm.convert_date,
       cstm.status,
       cstm.erp_code,
       cstm.ebusi_admin_acct_no as xy_code
  from t_customer      cstm,
       t_linker        lk,
       ref_cstm_linker rsl,
       t_dpt           dpt,
       t_organization  org,
       t_account       acct
 where rsl.customers_id = cstm.id
   and rsl.linkers_id = lk.id
   and lk.main_status = 1
   and dpt.id = cstm.fk_dpt_id
   and dpt.fk_org_id = org.id
   and acct.id = cstm.fk_acct_id
   and cstm.status != 2;


-- 客户表erp，型云字段对照表 --crm
create or replace view v_cstm_erp as
select EBUSI_MEMBER_CODE, EBUSI_ADMIN_ACCT_NO, ERP_CODE, COMP_NAME, XY_CODE from t_customer where EBUSI_MEMBER_CODE is not null;

-- 提供给手机app的客户视图 --crm
create or replace view v_mobile_cstm_list as
select a.cstm_id,a.comp_name,a.comp_addr,a.comp_prov,a.comp_city,a.comp_area,a.mark,a.comp_name_initial,a.create_at,a.update_at,a.public_comp_name,a.bill_date,
       a.linker_id,a.linker_name,a.linker_phone,a.linker_position,a.dpt_id,a.dpt_name,a.org_id,a.org_name,a.acct_id,a.acct_name,COALESCE(b.summary, 0.0) as summary,
       COALESCE(c.clock_time,a.create_at) as clock_time,a.region
  from (select cstm.id as cstm_id,cstm.comp_name,cstm.comp_addr,cstm.comp_prov,cstm.comp_city,cstm.comp_area,cstm.region,cstm.mark,cstm.comp_name_initial,cstm.bill_date,
               cstm.create_at,cstm.update_at,cstm.public_comp_name,lk.id as linker_id,lk.name as linker_name,lk.phone as linker_phone,lk.position as linker_position,
               dpt.id as dpt_id,dpt.name as dpt_name,org.id as org_id,org.name as org_name,acct.id as acct_id,acct.name as acct_name
          from t_customer cstm
          left join ref_cstm_linker rcl
            on cstm.id = rcl.customers_id
          left join t_linker lk
            on rcl.linkers_id = lk.id
          left join t_dpt dpt
            on dpt.id = cstm.fk_dpt_id
          left join t_organization org
            on dpt.fk_org_id = org.id
          left join t_account acct
            on acct.id = cstm.fk_acct_id
         where lk.main_status = 1
           and cstm.status = 1) a
  left join (select distinct gs.comp_name, gs.summary
               from t_grade_summary gs
              where gs.summary_date =
                    (select max(tgs.summary_date)
                       from t_grade_summary tgs
                      where gs.comp_name = tgs.comp_name)) b
    on b.comp_name = a.public_comp_name
  left join (select distinct cc.customer_id, cc.clock_time
               from t_customer_call cc
              where cc.clock_time =
                    (select max(tcc.clock_time)
                       from t_customer_call tcc
                      where tcc.customer_id = cc.customer_id and tcc.status = 1)) c
    on a.cstm_id = c.customer_id;

--提供给短信中心的客户联系人列表视图 --修改字段顺序要修改代码中的下标 --crm
create or replace view v_cstm_linker_list as
select distinct aa.link_name,aa.link_phone,aa.main_status,aa.create_at,aa.update_at,aa.comp_name,aa.mark,aa.region,
aa.busi_name,aa.acct_name,aa.dpt_name,aa.pro_name,aa.bill_date,bb.summary_level,aa.acct_id,aa.dpt_id,aa.org_id,aa.erp_code,aa.xy_code
  from (select c.name        as link_name,
               c.phone       as link_phone,
               c.main_status,
               c.create_at,
               c.update_at,
               a.comp_name,
               a.mark,
               a.region,
               e.name        as busi_name,
               f.name        as acct_name,
               g.name        as dpt_name,
               i.name        as pro_name,
               a.bill_date,
               f.id          as acct_id,
               g.id          as dpt_id,
               h.id          as org_id,
               a.erp_code,
               a.ebusi_admin_acct_no      as xy_code
          from t_customer             a,
               ref_cstm_linker        b,
               t_linker               c,
               ref_cstm_busi_relation d,
               t_busi_relation        e,
               t_account              f,
               t_dpt                  g,
               t_organization         h,
               t_custom_property      i
         where a.id = b.customers_id
           and c.id = b.linkers_id
           and a.id = d.customer_id
           and d.busi_relation_id = e.id
           and a.fk_acct_id = f.id
           and a.fk_dpt_id = g.id
           and g.fk_org_id = h.id
           and a.fk_custom_property_id = i.id
           and a.status != 2) aa
  left join (select j.comp_name as compName, j.summary_level
               from T_GRADE_SUMMARY j
              where j.create_at >= to_timestamp(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd hh24:mi:ss.ff')
              and j.create_at < to_timestamp(to_char(sysdate + 1,'yyyy-mm-dd'),'yyyy-mm-dd hh24:mi:ss.ff')) bb
    on aa.comp_name = bb.compName;

--提供给短信中心的发送详情列表视图 --crm
create or replace view v_sms_list as
select a.comp_name,d.name as link_name,d.phone as link_phone,d.parent_id,d.status,d.msg_id,d.create_at,d.update_at,c.main_status
from t_customer a,ref_cstm_linker b,t_linker c,t_sms d where c.id = d.ref_id and b.linkers_id = c.id and a.id = b.customers_id and d.send_type = 1;

--提供给短信中心的发送统计列表视图 --crm
create or replace view v_sms_statistic_list as
select b.id,c.name as acct_name,b.type,b.status,a.comp_name,a.link_name,a.link_phone,b.create_at,b.update_at,b.content,b.delay_time,b.send_count,b.failure_num
from v_sms_list a,t_sms_statistic b,t_account c where a.parent_id = b.id and b.creator_id = c.id and b.send_type = 1;

--有erpCode的客户相关信息视图--crm
create or replace view v_erpcstm_basicinfo_list as
select a.id as cstmId,a.create_at,a.update_at,a.comp_name,a.erp_code,a.mark,a.status,b.name as linkName,b.id as linkId,b.main_status,b.phone,
d.id as acctId,d.name as acctName,e.name as dptName,e.id as dptId,f.name as orgName,f.id as orgId
from t_customer a,t_linker b,ref_cstm_linker c,t_account d,t_dpt e,t_organization f
where c.customers_id = a.id and c.linkers_id = b.id and b.main_status = 1 and a.fk_acct_id = d.id and a.fk_dpt_id = e.id and e.fk_org_id = f.id
and a.status != 2 and a.erp_code is not null order by a.update_at desc;

--联系人查询视图--crm
create or replace view v_linker_cstm_list as
select d.erp_code,
       d.ebusi_admin_acct_no as xy_code,
       d.status              as cstm_status,
       d.mark                as cstm_mark,
       d.id                  as cstm_id,
       d.comp_name,
       e.id                  as acct_id,
       e.name                as acct_name,
       f.id                  as dpt_id,
       f.name                as dpt_name,
       g.id                  as org_id,
       a.id as link_id,a.create_at,a.update_at,a.age,a.edu,a.main_status,a.name,a.phone,a.position,a.qq_no,a.remark,a.wx_no,a.sex,a.creator_id,
       b.name                as creator_name
  from t_linker        a,
       t_account       b,
       ref_cstm_linker c,
       t_customer      d,
       t_account       e,
       t_dpt           f,
       t_organization  g
 where a.creator_id = b.id
   and a.id = c.linkers_id
   and c.customers_id = d.id
   and d.fk_acct_id = e.id
   and d.fk_dpt_id = f.id
   and f.fk_org_id = g.id;

--开发任务查询视图--crm
create or replace view v_developer_task as
select b."ID",b."CREATE_AT",b."UPDATE_AT",b."ACTUAL_TIME",b."DELIVERY_TIME",b."DESCRIPTION",b."ESTIMATED_TIME",b."FILE_URL",b."PIC_URL",b."PRIORITY",b."REASON",b."STATUS",b."TASK_NAME",b."FK_PARENT_ID",b."FK_PROJECT_ID",b."FK_TASK_TYPE_ID",b."OPERATOR_ID",b."REPORTER_ID",b."SUB_STATUS",b."FILE_NAME",b."END_TIME",b."START_TIME",b."BELONG" from t_team_task b where b.fk_parent_id is null and b.sub_status = '-1'
union
select "ID","CREATE_AT","UPDATE_AT","ACTUAL_TIME","DELIVERY_TIME","DESCRIPTION","ESTIMATED_TIME","FILE_URL","PIC_URL","PRIORITY","REASON","STATUS","TASK_NAME","FK_PARENT_ID","FK_PROJECT_ID","FK_TASK_TYPE_ID","OPERATOR_ID","REPORTER_ID","SUB_STATUS","FILE_NAME","END_TIME","START_TIME","BELONG" from t_team_task c where c.id in(
  select max(b.id) from (
         select a.id,a.fk_parent_id from t_team_task a where a.fk_parent_id is not null and a.belong = 0) b
  group by b.fk_parent_id);

--测试任务查询视图--crm
create or replace view v_tester_task as
select "ID","CREATE_AT","UPDATE_AT","ACTUAL_TIME","DELIVERY_TIME","DESCRIPTION","ESTIMATED_TIME","FILE_URL","PIC_URL","PRIORITY","REASON","STATUS","TASK_NAME","FK_PARENT_ID","FK_PROJECT_ID","FK_TASK_TYPE_ID","OPERATOR_ID","REPORTER_ID","SUB_STATUS","FILE_NAME","END_TIME","START_TIME","BELONG" from t_team_task c where c.id in(
  select max(b.id) from (
         select a.id,a.fk_parent_id from t_team_task a where a.fk_parent_id is not null and a.belong = 1) b
  group by b.fk_parent_id);

--提供给OA的客户状态查询视图--crm
create or replace view v_crm2oa_cstm_list as
select a.erp_code, '一次开发' as status from t_customer a where a.mark = 2 and a.bill_date is not null  and a.erp_code is not null
and a.id not in (select distinct b.fk_custom_id from t_customer_record b where b.type = 4)
union
select a.erp_code, '二次开发' as status from t_customer a where a.mark = 2  and a.erp_code is not null
and a.id in (select distinct b.fk_custom_id from t_customer_record b where b.type = 4);

--crm统计库
--离职人员销量视图 --crm统计库
create or replace view v_demission_sales_list as
select a.profit_date,
       a.customer_code,
       a.data_bweight,
       a.gm,
       b.comp_name,
       b.fk_acct_id,
       b.fk_dpt_id,
       c.name          as acct_name,
       c.demission,
       d.name          as dpt_name,
       d.fk_org_id,
       e.name          as org_name
  from t_account@CRMSTAT_CRM  c
  left join t_customer@CRMSTAT_CRM  b
    on b.fk_acct_id = c.id
  left join t_dpt@CRMSTAT_CRM  d
    on b.fk_dpt_id = d.id
  left join t_organization@CRMSTAT_CRM  e
    on d.fk_org_id = e.id
  left join erp_forbi_xs a
    on b.erp_code = a.customer_code
 where c.demission = 1 and a.customer_code is not null;

--用户行为视图 --crm统计库
create or replace view v_behavior_record as
select a.ip,a.ip_location,a.user_,a.user_name,a.emp,a.goods_name,a.supply,a.material,a.standard_,a.search,a.warehouse,a.tolerance_start,a.tolerance_end,
a.order_id,a.tolerance_range,a.weight_range,a.amount,a.weight,a.order_no,COALESCE(to_number(a.diff),0.0) as diff,a.deal_from,a.type_,a.delay,a.carry_no,
COALESCE(to_number(a.money),0.0) as money,a.priority,a.contract_no,a.mphone,a.nick_name,a.ditch_way,a.start_area,a.end_area,COALESCE(to_number(a.num_),0.0) as num,
a.is_special_offer,a.is_new_product,a.length_,COALESCE(to_number(a.lj_price),0.0) as ljPrice,COALESCE(to_number(a.bj_price),0.0) as bjPrice,
COALESCE(to_number(a.min_price),0.0) as minPrice,a.sell_type,a.category_name,COALESCE(to_number(a.min_weight),0.0) as minWeight,a.percentage,a.region_,
a.date_range,a.status,a.measure,to_number(a.time_) as time,a.source_,a.event_str,a.event from tab_log_analysis_event@crmstat_xy a;

--客户联系人关系表
CREATE or replace VIEW v_cstm_link as
	select t."NAME" as link_name, t.MAIN_STATUS, t.PHONE as link_phone, rtc.LINKERS_ID as link_id, rtc.CUSTOMERS_ID as cstm_id, c.COMP_NAME, c.EBUSI_MEMBER_CODE, c.ERP_CODE, CASE WHEN (SELECT rclk.status FROM REF_CSTM_COMM_LINK rclk WHERE rclk.CSTM_ID = rtc.CUSTOMERS_ID AND rclk.LINKER_ID = rtc.LINKERS_ID) IS NULL THEN 0 ELSE (SELECT rclk.status FROM REF_CSTM_COMM_LINK rclk WHERE rclk.CSTM_ID = rtc.CUSTOMERS_ID AND rclk.LINKER_ID = rtc.LINKERS_ID) END AS comm_status from REF_CSTM_LINKER rtc, T_LINKER t, T_CUSTOMER c where t.id = rtc.LINKERS_ID and c.id = rtc.CUSTOMERS_ID and rtc.CUSTOMERS_ID in (select id from T_CUSTOMER);

--调货直发明细 --crm统计库
create or replace view v_fortc_zfmx as
select t.profit_date,
       t.datas_customername,
       t.dept_name,
       case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.employee_code,
       t.zf_bweight,
       t.dh_bweight,
       t.gm,
       t.tcje from v_fortc_zfmx@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--业务员提成汇总报表 --crm统计库
create or replace view v_fortc_ywyhzb as
select t.employee_code,
       t.dept_code,
       case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.dept_name,
       t.ny,
       t.data_bweight,
       nvl(t.new_count,0) new_count,
       t.tc_price,
       t.zf_bweight,
       t.new_bweight,
       t.dm_bweight,
       t.sj_price,
       t.jb_tc,
       t.new_tc,
       t.dm_tc,
       nvl(t.gm_tc,0) gm_tc,
       t.zf_tc,
       t.dh_bweight,
       nvl(t.rwweight,0) rwweight,
       t.new_sumbweight from v_fortc_ywyhzb@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--业务员新增客户明细表 --crm统计库
create or replace view v_fortc_comany_ywy as
select t.datas_customername,
       t.startdate,
       t.dept_name,
       case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.employee_code,
       t.profit_date,
       t.data_bweight,
       t.gm,
       t.gm_tc from v_fortc_comany_ywy@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--高卖明细表 --crm统计库
create or replace view v_fortc_mxgm as
select case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
        end as employee_name,
        t.employee_code,
        t.dept_name,
        t.ny,
        t.datas_customername,
        t.gm from v_fortc_mxgm@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--新增客户明细表 --crm统计库
create or replace view v_fortc_newcomany as
select t.dept_name,
       case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.employee_code,
       t.company_name,
       t.startdate from v_fortc_newcomany@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--销售专员员提成汇总报表 --crm统计库
create or replace view v_fortc_xszymxb as
select case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
         then a.remark||'('||a.name||')' else a.name
       end as employee_name,
        t.employee_code,
        t.dept_name,
        t.ny,
        t.upny,
        t.data_bweight,
        w.weight as other_weight,
        (t.data_bweight+nvl(w.weight,0)) as real_weight,
        t.zf_bweight,
        t.new_bweight,
        t.zf_tc,
        t.new_tc,
        nvl(t.gm_tc,0) gm_tc,
        nvl(t.rwweight,0) rwweight,
        nvl(t.upweight,0) upweight,
        t.xl_tc
        from v_fortc_xszymxb@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code
        left join sale_other_weight w on t.ny=w.year_month and w.platform_code=t.employee_code;

--销售专员新增客户明细表 --crm统计库
create or replace view v_fortc_comany_zy as
select  t.datas_customername,
        t.profit_date,
        t.startdate,
        t.dept_name,
        case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                 then a.remark||'('||a.name||')' else a.name
        end as employee_name,
        t.employee_code,
        t.data_bweight,
        t.gm,
        t.money_gm,
        t.weight_gm,
        t.money_dm,
        t.weight_dm,
        t.zf_weight from v_fortc_comany_zy@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--手动转化成正式客户的历史数据修复
--标记开单自动转化的客户
update t_customer t set t.trans_type=1 where to_char(t.bill_date,'yyyy-MM-dd')>=to_char(t.convert_date,'yyyy-MM-dd') and t.mark=2
--标记手动转化的客户
update t_customer t set t.trans_type=2 where to_char(t.bill_date,'yyyy-MM-dd')<to_char(t.convert_date,'yyyy-MM-dd') and t.mark=2
--修复手动转化后的开单时间
update t_customer t set t.bill_date = t.convert_date where t.trans_type=2 and t.mark=2

-- crm 所有含市的市区
CREATE VIEW v_address_city as
SELECT * FROM T_ADDRESS WHERE name LIKE '%市' AND TYPE IN (2, 3);

-- crm 客户评估中客户性质长期维护，二次开发
CREATE VIEW v_cstm_modify_employee_count AS
SELECT CUSTOMER_ID , count(1) AS count FROM T_CUSTOMER_MODIFY WHERE COLUMN_NAME = '业务员' AND ORIGIN_VAL <> '超级管理员' GROUP BY CUSTOMER_ID;

-- crm 客户评估视图
CREATE OR replace view v_cstm_evaluation as
SELECT tc.id, tc.comp_name,to_char(tc.mark) AS mark,to_char(tc.CREATE_AT,'yyyy-MM-dd HH24:mi:ss') AS create_at,dpt.name AS dpt_name,(CASE WHEN v1.count > 0 THEN '1' ELSE '0' end) AS customer_property_mark, (CASE WHEN (LENGTH(trim(acct.REMARK)) > 0) THEN concat(concat(concat(acct.REMARK, '('), acct.name), ')') ELSE acct.name END) AS employee_name, (SELECT t.name FROM T_LINKER t LEFT JOIN REF_CSTM_LINKER rlk ON rlk.LINKERS_ID  = t.id WHERE t.MAIN_STATUS = 1 AND rlk.CUSTOMERS_ID = tc.id) AS linker_name, nvl(t2.cstm_id, 0) AS cstm_id, nvl(t2.AREA_NAME, tc.REGION) AS area_name, t2.busi_scope, t2.cstm_property_ids, t2.delivery_name, t2.delivery_prefer, tc.erp_code, t2.goods_names, t2.has_storage, t2.loss_reason, t2.main_busi, t2.main_delivery_way, t2.reason,  nvl(t2.storage_capacity, 0.00) AS storage_opacity, nvl(t2.year_percent, 100) AS year_percent, nvl(t2.year_sale_weight, 0.00) AS year_sale_weight, to_char(acct.id) AS employee_id, to_char(dpt.id) AS dpt_id, t2.EXTRA_GOODS_REQUIREMENT, tc.FIRST_BILL_DATE, tc.FIRST_DELIVERY_DATE, tc.STATUS FROM T_CUSTOMER tc LEFT JOIN T_CUSTOMER_EVALUATION t2 ON t2.cstm_id=tc.id LEFT JOIN t_dpt dpt ON dpt.id = tc.FK_DPT_ID LEFT JOIN T_ACCOUNT acct ON acct.id = tc.FK_ACCT_ID LEFT JOIN v_cstm_modify_employee_count v1 ON v1.customer_id = tc.id;

-- 客户评估地区销量 crm统计库
CREATE OR replace VIEW v_crm_area_weight as
SELECT nvl(t2.id, 0) AS id, v1.*, (CASE WHEN (LENGTH(trim(t.REMARK)) > 0) THEN concat(concat(concat(t.REMARK, '('), t.name), ')') ELSE t.name END) AS employee_name, to_char(t.id) AS acct_id, td.name AS dpt_name, to_char(td.id) AS dpt_id,t2.SALE_EVALUATION_WEIGHT, t2.DELIVERY_STATUS_INFO, t2.EFFECT_FOR_SALE FROM v_area_sale v1 LEFT JOIN t_account@crmstat_crm t ON t.platform_code = v1.EMPLOYEE_CODE LEFT join t_dpt@crmstat_crm td ON td.id = t.fk_dpt_id LEFT JOIN crm_area_record t2 ON t2.acct_id= t.id and t2.AREA_NAME  = v1.AREA_NAME;

-- crm 客户超频次提醒跟踪
CREATE VIEW v_cstm_purchase_freq as
SELECT t2.*,nvl(t3.id, 0) AS id,t4.feed_back, (CASE WHEN t4.update_at = NULL THEN NULL ELSE to_char(t4.update_at,'yyyy-MM-dd HH24:mi:ss') end) AS update_at FROM v_cst_trade_period t2 LEFT JOIN (SELECT max(t.id) AS id, t.erp_code FROM t_customer_purchase_freq t GROUP BY erp_code) t3 ON t2.erp_code = t3.erp_code LEFT JOIN t_customer_purchase_freq t4 ON t4.id = t3.id;

--地区销量  --crm统计库
--先建表
--create table t_history_area_sale
--(
--history_area_sale_id NUMBER(18,0) CONSTRAINT PK_history_area_sale PRIMARY KEY,
--area_name varchar2(30),
--yearnum VARCHAR2(30),
--employee_code VARCHAR2(30),
--weight number(25,8)
--);
--
---- Add comments to the table
--comment on table t_history_area_sale
--is '17年开始地区销量历史表';
---- Add comments to the columns
--comment on column t_history_area_sale.history_area_sale_id
--is 'id';
--comment on column t_history_area_sale.yearnum
--is '年份';
--comment on column t_history_area_sale.area_name
--is '地区';
--comment on column t_history_area_sale.employee_code
--is '业务员code';
--comment on column t_history_area_sale.weight
--is '销量';
--
--CREATE SEQUENCE history_area_sale_SEQ MINVALUE 1 MAXVALUE 9999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE ORDER NOCYCLE ;
--
----导数据
--insert into t_history_area_sale (history_area_sale_id,yearnum,area_name,employee_code,weight)
--SELECT history_area_sale_SEQ.Nextval,v1.yearnum,v1.area_name,v1.employee_code,v1.weight
-- FROM czzhd.v_area_sale@crmstat_erp19 v1;

--view
create or replace view v_area_sale as
SELECT case when oldv.area_name is null then nowv.area_name else oldv.area_name end as area_name,
       case when oldv.employee_code is null then nowv.employee_code else oldv.employee_code end as employee_code,
       oldv.weight old_weight,nowv.weight new_weight FROM
(SELECT v1.area_name,v1.employee_code,v1.weight
 FROM t_history_area_sale v1 where v1.yearnum='2019' )  oldv
 full outer join czzhd.v_area_sale@crmstat_erp nowv
 on oldv.area_name=nowv.area_name and oldv.employee_code=nowv.employee_code
 order by case when oldv.area_name is null then nowv.area_name else oldv.area_name end;




 --每年品名销量前三 --crm统计库
 --先建表
-- create table t_history_partsname_order
-- (
-- history_partsname_order_id NUMBER(18,0) CONSTRAINT PK_history_partsname_order PRIMARY KEY,
-- yearnum VARCHAR2(30),
-- datas_customer varchar2(30),
-- partsname_name VARCHAR2(30),
-- pntree_name varchar2(30),
-- weight number(25,8)
-- );
-- -- Add comments to the table
-- comment on table t_history_partsname_order
-- is '17年开始客户品名销量排名历史表';
-- -- Add comments to the columns
-- comment on column t_history_partsname_order.history_partsname_order_id
-- is 'id';
-- comment on column t_history_partsname_order.yearnum
-- is '年份';
-- comment on column t_history_partsname_order.datas_customer
-- is '客户';
-- comment on column t_history_partsname_order.partsname_name
-- is '品名';
-- comment on column t_history_partsname_order.pntree_name
-- is '品名大类';
-- comment on column t_history_partsname_order.weight
-- is '销量';
-- CREATE SEQUENCE history_partsname_order_SEQ MINVALUE 1 MAXVALUE 9999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE ORDER NOCYCLE ;
--
----导数据
--insert into t_history_partsname_order (history_partsname_order_id,yearnum,datas_customer,partsname_name,pntree_name,weight)
--SELECT  history_partsname_order_SEQ.Nextval,v1.yearnum,v1.datas_customer,v1.partsname_name,v1.pntree_name,v1.weight FROM
--czzhd.v_partsname_order@crmstat_erp19 v1;

--view
create or replace view v_partsname_order as
SELECT v1.yearnum,v1.datas_customer,v1.partsname_name,v1.pntree_name,v1.weight FROM
t_history_partsname_order v1
union all
SELECT v2.yearnum,v2.datas_customer,v2.partsname_name,v2.pntree_name,v2.weight FROM
czzhd.v_partsname_order@crmstat_erp v2;



--历史数据销量  --crm统计库
--先建表
--create table t_history_sale_sum
--(
--history_sale_sum_ID NUMBER(18,0) CONSTRAINT PK_history_sale_sum PRIMARY KEY ,
--yearnum VARCHAR2(30),
--datas_customer VARCHAR2(30),
--sbill_weight NUMBER(25,8),
--data_bweight number(25,8),
--gm number(25,8)
--);
--
---- Add comments to the table
--comment on table t_history_sale_sum
--is '客户年度销量历史表';
---- Add comments to the columns
--comment on column t_history_sale_sum.history_sale_sum_ID
--is 'id';
--comment on column t_history_sale_sum.yearnum
--is '年份';
--comment on column t_history_sale_sum.datas_customer
--is '客户code';
--comment on column t_history_sale_sum.sbill_weight
--is '提单销量';
--comment on column t_history_sale_sum.data_bweight
--is '实提量';
--comment on column t_history_sale_sum.gm
--is '高卖';
--
--CREATE SEQUENCE history_sale_sum_SEQ MINVALUE 1 MAXVALUE 9999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE ORDER NOCYCLE ;
--
----导数据
--insert into t_history_sale_sum (history_sale_sum_id,yearnum,datas_customer,sbill_weight,data_bweight,gm)
--SELECT history_sale_sum_SEQ.Nextval,v1.yearnum,v1.datas_customer,v1.sbill_weight,v1.data_bweight,v1.gm
--FROM czzhd.v_history_sale_sum@crmstat_erp19 v1;

--view
create or replace view v_history_sale_sum_all as
SELECT v1.yearnum,v1.datas_customer,v1.sbill_weight,v1.data_bweight,v1.gm
FROM t_history_sale_sum v1
union all
SELECT v1.yearnum,v1.datas_customer,v1.sbill_weight,v1.data_bweight,v1.gm
FROM czzhd.v_history_sale_sum@crmstat_erp v1;


--客户首单  --crm统计库
--建表
--create table t_history_delivery_order
--(
--history_delivery_order_id NUMBER(18,0) CONSTRAINT PK_history_delivery_order PRIMARY KEY,
--datas_customer varchar2(30),
--first_sbill date,
--first_delivery date
--);
--
---- Add comments to the table
--comment on table t_history_delivery_order
--is '17年开始客户首张有效提单历史表';
---- Add comments to the columns
--comment on column t_history_delivery_order.history_delivery_order_id
--is 'id';
--comment on column t_history_delivery_order.datas_customer
--is '客户';
--comment on column t_history_delivery_order.first_sbill
--is '首张有效提单';
--comment on column t_history_delivery_order.first_delivery
--is '首次出库';
--
--CREATE SEQUENCE history_delivery_order_SEQ MINVALUE 1 MAXVALUE 9999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE ORDER NOCYCLE ;
--
----导数据  54分钟
--insert into t_history_delivery_order (history_delivery_order_id,datas_customer,first_sbill,first_delivery)
--SELECT history_delivery_order_SEQ.Nextval,v1.datas_customer,v1.first_sbill,v1.first_delivery
--FROM czzhd.v_delivery_order@crmstat_erp19 v1;

--view
create or replace view v_delivery_order as
SELECT v1.datas_customer,v1.first_sbill,v1.first_delivery
FROM t_history_delivery_order v1
union all
SELECT v1.datas_customer,v1.first_sbill,v1.first_delivery
FROM czzhd.v_delivery_order@crmstat_erp v1;

--超期未提报表 --crm统计库
CREATE OR REPLACE VIEW V_OVERDUE_UNDELIVERY AS
SELECT t.billcode,
       t.wsFlag,
       t.datas_customername,
       t.linkman,
       t.linkmobile,
       t.goods_flag,
       nvl(t.real_undelivery,0) as real_undelivery,
       t.isckflag,
       to_char(t.start_date,'yyyy-MM-dd') as start_date,
       to_char(t.end_date,'yyyy-MM-dd') as end_date,
       t.overdue_date as overdue_date,
       round(nvl(t.undelivery_money,0),3) as undelivery_money,
       t.employee_code,
       case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.dept_name
from (SELECT v1.billcode,
       v1.wsFlag,
       v1.datas_customername,
       v1.linkman,
       v1.linkmobile,
       v1.goods_flag,
       v1.real_undelivery,
       v1.isckflag,
       v1.start_date,
       v1.end_date,
       v1.overdue_date,
       v1.undelivery_money,
       v1.employee_code,
       v1.employee_name,
       v1.dept_name FROM
V_OVERDUE_UNDELIVERY@CRMSTAT_ERP v1
union all
SELECT v2.deal_no as billcode,
        v2.wsFlag,
        v2.datas_customername,
        v2.linkman,
        v2.linkmobile,
        v2.goods_flag,
        v2.real_undelivery,
        v2.isckflag,
        v2.start_date,
        v2.end_date,
        v2.overdue_date,
        v2.undelivery_money,
        v2.employee_code,
        v2.employee_name,
        v2.dept_name
FROM V_OVERDUE_UNDELIVERY@CRMSTAT_XY v2) t left join t_account@crmstat_crm a on a.platform_code=t.employee_code
where t.billcode not in (SELECT distinct u.billcode FROM undelivery_record@crmstat_erp u)
order by t.billcode,t.wsFlag;


--首单视图
create or replace view v_cst_min_sbilldate as
SELECT d.company_code,d.company_name,min(d.delivery_date) as min_ddate,min(d.sbill_date) as min_sdate FROM
(SELECT datas_balcorp as company_code,datas_balcorpname as company_name,delivery_date,sbill_date FROM warehouse_delivery@crm_erp12
union all
SELECT datas_balcorp as company_code,datas_balcorpname as company_name,delivery_date,sbill_date FROM  warehouse_delivery@crm_erp13
union all
SELECT datas_balcorp as company_code,datas_balcorpname as company_name,delivery_date,sbill_date FROM  warehouse_delivery@crm_erp14
union all
SELECT datas_balcorp as company_code,datas_balcorpname as company_name,delivery_date,sbill_date FROM  warehouse_delivery@crm_erp15
union all
SELECT datas_balcorp as company_code,datas_balcorpname as company_name,delivery_date,sbill_date FROM  warehouse_delivery@crm_erp16
union all
SELECT datas_customer as company_code,datas_customername as company_name,delivery_date,sbill_date FROM  warehouse_delivery@crm_erp19
union all
SELECT datas_customer as company_code,datas_customername as company_name,delivery_date,sbill_date FROM  warehouse_delivery@crm_erp)d
group by d.company_code,d.company_name;

-- Add/modify columns
alter table T_CUSTOMER add first_bill_date TIMESTAMP(6);
alter table T_CUSTOMER add first_delivery_date TIMESTAMP(6);
-- Add comments to the columns
comment on column T_CUSTOMER.first_bill_date
  is '首次有效开单日期';
comment on column T_CUSTOMER.first_delivery_date
  is '首次有效提货日期';

--初始化客户首单procedure
create or replace procedure pro_insert_customer_first_date as
begin
  declare
    cursor cst_date_cur is
        SELECT t.company_code,t.min_ddate,t.min_sdate FROM v_cst_min_sbilldate t;
  begin
    for cst_date in cst_date_cur loop
      update t_customer t set t.first_bill_date=cst_date.min_sdate,t.first_delivery_date=cst_date.min_ddate
             where t.erp_code=cst_date.company_code;
      commit;
      update t_customer t set t.start_time=cst_date.min_sdate where t.erp_code=cst_date.company_code and t.start_time is null;
      commit;
    end loop;
  end;
end;

--按角色名，菜单id导入权限
create or replace procedure proc_insert_role_menu(role_name in varchar2,menu_id in int) is
  newID varchar2(50);
  cnt int;
begin
  declare
    cursor aid_cur is
      SELECT a.id FROM t_account a left join t_role r on r.id=a.fk_role_id where r.name=role_name and a.status=1;
  begin
    for aid in aid_cur loop
      begin
        select count(aa.account_id) into cnt from ref_acct_auth aa,t_auth a1,t_account a2 where a1.fk_menu_id=menu_id and a2.id=aid.id and a1.id=aa.auths_id
                   and a2.id=aa.account_id;
        --去重
        if cnt=0 then
          insert into t_auth t (t.id,t.create_at,t.update_at,t.has_create,t.has_delete,t.has_update,t.fk_menu_id) values
                 ((select max(tt.id) FROM t_auth tt)+1,sysdate,sysdate,0,0,0,menu_id) RETURNING ID INTO newID;
          commit;
          insert into ref_acct_auth aa (aa.account_id,aa.auths_id) values (aid.id,newID);
          commit;
        end if;
      end;
    end loop;
  end;
end;

--按角色名，菜单id删除权限 crm主表
create or replace procedure proc_delete_role_menu(role_name in varchar2,menu_id in int) is
begin
  declare
    cursor aid_cur is
      SELECT a.id FROM t_account a left join t_role r on r.id=a.fk_role_id where r.name=role_name and a.status=1;
  begin
    for aid in aid_cur loop
      begin
        --删除个人权限
        declare
            cursor acc_authid_cur is
                select auths_id from ref_acct_auth where account_id=aid.id and auths_id in (SELECT a.id FROM t_auth a where a.FK_MENU_ID=menu_id);
        begin
            for this_authid in acc_authid_cur loop
              delete from ref_acct_auth where auths_id =this_authid.auths_id;
              commit;
              delete from ref_role_auth where AUTHS_ID=this_authid.auths_id;
              commit;
              delete from t_auth where id=this_authid.auths_id;
              commit;
            end loop;
        end;
        --删除角色权限
        declare
            cursor role_authid_cur is
                select rr.auths_id from ref_role_auth rr left join t_role r on r.id=rr.role_id  where r.name=role_name
                 and rr.auths_id in (SELECT a.id FROM t_auth a where a.FK_MENU_ID=menu_id);
        begin
            for this_authid in role_authid_cur loop
              delete from ref_acct_auth where auths_id =this_authid.auths_id;
              commit;
              delete from ref_role_auth where AUTHS_ID=this_authid.auths_id;
              commit;
              delete from t_auth where id=this_authid.auths_id;
              commit;
            end loop;
        end;

      end;
    end loop;
  end;
end;


--新绩效考核 --crm统计库
create or replace view V_SALE_TCHZ as
select case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
               then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.employee_code,
       t.dept_name,
       t.ny,
       t.data_bweight,
       t.new_count,
       t.new_price,
       t.new_weight,
       t.new_tc,
       t.ZF_PRICE,
       t.ZF_BWEIGHT,
       t.zf_tc,
       t.DH_PRICE,
       t.DH_BWEIGHT,
       t.dh_tc,
       t.OLD_PRICE,
       t.old_weight,
       t.old_tc,
       t.tcje from V_SALE_TCHZ@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

-- crm 超过平均购买周期客户
create or replace view v_cst_trade_period as
SELECT d.id as dpt_id,d.name as dpt_name,a.id as acct_id,a.name as employee_name,t.erp_code,t.comp_name,l.phone,
trunc(sysdate)-to_date(to_char(t.bill_date,'yyyy-MM-dd'),'yyyy-MM-dd') as un_deal_days,p.period,
trunc(sysdate)-to_date(to_char(t.bill_date,'yyyy-MM-dd'),'yyyy-MM-dd')-p.period as over_days FROM
t_customer t left join t_dpt d on d.id=t.fk_dpt_id left join t_account a on t.fk_acct_id=a.id
left join ref_cstm_linker rl on rl.customers_id=t.id  left join t_linker l on l.id=rl.linkers_id
left join v_cst_period@crm_erp p on p.datas_customer=t.erp_code
where t.mark = 2 and l.main_status=1 and trunc(sysdate)-to_date(to_char(t.bill_date,'yyyy-MM-dd'),'yyyy-MM-dd')>p.period;


--销售新增客户明细表 --crm统计库
create or replace view v_fortc_comany_sale as
select t.datas_customername,
       t.startdate,
       t.dept_name,
       case when (instr(a.name,'x')>0  and trim(a.remark) is not null)
                then a.remark||'('||a.name||')' else a.name
       end as employee_name,
       t.employee_code,
       t.profit_date,
       t.data_bweight,
       t.zf_bweight,
       t.new_tc from v_fortc_comany_sale@crmstat_erp t left join t_account@crmstat_crm a on a.platform_code=t.employee_code;

--省市对应关系图
create or replace view v_city_prov as
SELECT t.*, (CASE when t1.TYPE = 2 THEN (SELECT t3.name FROM T_ADDRESS t3 WHERE t3.CODE  = t1.PARENT_CODE) ELSE t1.name END) AS provice_name
 FROM T_ADDRESS t LEFT JOIN T_ADDRESS t1 ON t1.code = t.PARENT_CODE WHERE t.name LIKE '%市' AND t.TYPE IN (2, 3);

--销售月度销量
CREATE OR REPLACE VIEW V_MONTH_SALE AS
SELECT datas_customer,wsflag,ny,weight FROM V_MONTH_SALE_2019@crm_erp19
union all
SELECT datas_customer,wsflag,ny,weight FROM V_MONTH_SALE@crm_erp;