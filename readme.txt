//-----------------------------------java文件说明-----------------------------------------


//---------------------五个用于呈现信息的界面----------------------
//主界面（显示网站图标）
MainActivity.java
//“聚合”界面（显示聚合信息）
AggActivity.java
//“我的收藏”界面
MyStarsActivity.java
//信息列表界面
InfoActivity.java
//显示网页的界面
WebActivity.java（显示网站首页）
WebActivity_dview.java（显示信息详情页）

//---------------------四个改变软件设定的界面----------------------
//添加网站
AddSiteActivity.java
//编辑（删除）网站
EditSitesActivity.java
//编辑收藏列表
EditStarsActivity.java
//设置关于信息聚合的相关参数（关键词、信息时限、更新频率）
AggSettingActivity.java

//---------------------------后台服务----------------------------
//到达指定时间点时，在后台更新信息列表并写入文件
RefreshAggInfoService.java

//---------------------------一些函数----------------------------
//关于爬虫的函数
WebTool.java
//关于文件读写的函数
FileTool.java


//----------------------------------res目录（资源文件）说明-----------------------------------


//------------------------res/layout目录下-----------------------
//各activity对应的界面布局
activity_add_site.xml
activity_agg.xml
activity_agg_setting.xml
activity_edit_sites.xml
activity_edit_stars.xml
activity_info.xml
activity_main.xml
activity_my_stars.xml
activity_web.xml

//ListView用到的子项布局
check_site_element.xml
check_site_element_inagg.xml
info_element.xml
info_element_inagg.xml

//-------------------------res/menu目录下------------------------
//各界面的菜单栏布局
agg_menu.xml
agg_setting_menu.xml
edit_sites_menu.xml
main_menu.xml
web_menu.xml
web_menu_dview.xml

//--------------------------res/drawable------------------------
//各种图标

//-----------------------------res/xml--------------------------
//为了在安卓9上使用http添加的文件
network_security_config.xml


//-----------------------assets目录（程序运行时使用的一些文件）说明--------------------------------


//爬下来的聚合信息
agg_infos.xml
//关于信息聚合的一些参数设置
agg_settings.xml
//每个网站是否要进行信息爬取，形成的一个bool列表
checklist.xml
//主页面呈现的所有网站
sites.xml
//收藏的信息列表
star_infos.xml