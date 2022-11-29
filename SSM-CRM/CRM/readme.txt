CRM的表结构：
    系统功能：
        tbl_user        用户表(登陆)
        tbl_dic_type    数据字典类型(下拉列表类型)
        tbl_dic_value   数据字典值(下拉列表的值)
    业务功能：
        tbl_activity        市场活动表
        tbl_activity_remark 市场活动备注表
        tbl_clue            线索表
        tbl_clue_remark     线索备注表
        tbl_customer        客户表
        tbl_customer_remark 客户备注表
        tbl_contacts        联系人表
        tbl_contacts_remark 联系人备注表
        tbl_tran            交易表
        tbl_tran_remark     交易备注表
        tbl_tran_history    交易历史表
        tbl_task            任务表

一、搭建开发环境：
    1、创建项目、创建模块、补全目录
    2、添加依赖、添加自定义组件
        <dependencies>
            <!--单元测试依赖-->
            <dependency>
              <groupId>junit</groupId>
              <artifactId>junit</artifactId>
              <version>4.11</version>
              <scope>test</scope>
            </dependency>
            <!-- servlet依赖 -->
            <dependency>
              <groupId>javax.servlet</groupId>
              <artifactId>javax.servlet-api</artifactId>
              <version>3.1.0</version>
              <scope>provided</scope>
            </dependency>
            <!-- jsp依赖 -->
            <dependency>
              <groupId>javax.servlet.jsp</groupId>
              <artifactId>jsp-api</artifactId>
              <version>2.2.1-b03</version>
              <scope>provided</scope>
            </dependency>
            <!-- SpringMVC依赖 -->
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-webmvc</artifactId>
              <version>5.2.5.RELEASE</version>
            </dependency>
            <!-- 事务依赖 -->
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-tx</artifactId>
              <version>5.2.5.RELEASE</version>
            </dependency>
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-jdbc</artifactId>
              <version>5.2.5.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-aspects</artifactId>
                <version>5.2.5.RELEASE</version>
            </dependency>
            <!-- Jackson依赖 -->
            <dependency>
              <groupId>com.fasterxml.jackson.core</groupId>
              <artifactId>jackson-core</artifactId>
              <version>2.9.0</version>
            </dependency>
            <dependency>
              <groupId>com.fasterxml.jackson.core</groupId>
              <artifactId>jackson-databind</artifactId>
              <version>2.9.0</version>
            </dependency>
            <!-- spring、mybatis依赖 -->
            <dependency>
              <groupId>org.mybatis</groupId>
              <artifactId>mybatis-spring</artifactId>
              <version>1.3.1</version>
            </dependency>
            <!-- mybatis依赖 -->
            <dependency>
              <groupId>org.mybatis</groupId>
              <artifactId>mybatis</artifactId>
              <version>3.5.1</version>
            </dependency>
            <!-- mysql依赖 -->
            <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <version>5.1.9</version>
            </dependency>
            <!-- druid连接池 -->
            <dependency>
              <groupId>com.alibaba</groupId>
              <artifactId>druid</artifactId>
              <version>1.1.12</version>
            </dependency>
            <!--poi依赖,导出文件时使用-->
            <dependency>
              <groupId>org.apache.poi</groupId>
              <artifactId>poi</artifactId>
              <version>3.15</version>
            </dependency>
            <!-- 文件上传 -->
            <dependency>
              <groupId>commons-fileupload</groupId>
              <artifactId>commons-fileupload</artifactId>
              <version>1.3.1</version>
            </dependency>
            <!-- Log4j2依赖的JAR配置 -->
            <dependency>
              <groupId>org.apache.logging.log4j</groupId>
              <artifactId>log4j-api</artifactId>
              <version>2.3</version>
            </dependency>
            <dependency>
              <groupId>org.apache.logging.log4j</groupId>
              <artifactId>log4j-core</artifactId>
              <version>2.3</version>
            </dependency>
            <dependency>
              <groupId>org.apache.logging.log4j</groupId>
              <artifactId>log4j-jcl</artifactId>
              <version>2.3</version>
            </dependency>
          </dependencies>

          <build>
            <resources>
              <resource>
                <directory>src/main/java</directory>
                <includes>
                  <include>**/*.properties</include>
                  <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
              </resource>
            </resources>
          </build>

    3、添加配置文件
    4、添加静态资源(涉及重要数据的文件放在WEB-INF目录下；可直接访问的放webapp根目录下，如image、jquery)

二、分包：
    settings    系统功能
    workbench   业务功能
    web-controller  既不属于系统也不属于业务，控制首页index的跳转
    commons     公共包

mybatis逆向工程
    1、添加插件
        <!--myBatis逆向工程插件-->
        <plugins>
          <plugin>
        	<groupId>org.mybatis.generator</groupId>
        	<artifactId>mybatis-generator-maven-plugin</artifactId>
        	<version>1.3.2</version>
        	<configuration>
        	  <verbose>true</verbose>
        	  <overwrite>true</overwrite>
        	</configuration>
          </plugin>
        </plugins>
    2、创建配置文件：generator.properties和generatorConfig.xml文件，配置好
                数据库连接信息、生成的实体类放置的位置、生成的dao层文件放置的位置、要操作的表
                【注意：每次只能存在一个表，不然会出错】
    3、在右侧maven中双击Plugins --> mybatis-generator --> mybatis-generator.generate
    【最好新建一个模块来专门用于部署mybatis逆向工程，否则容易出错，且会很乱】
    【参考博客：https://www.cnblogs.com/comlee/p/13152346.html】

三、实现功能
1、首页功能
    (1)访问 http:/127.0.0.1:80/crm 跳转至首页
    (2)首页跳转至登陆页面

2、登陆功能
    用户在登录页面,输入用户名和密码,点击"登录"按钮或者回车,完成用户登录的功能.
        *用户名和密码不能为空
        *用户名或者密码错误,用户已过期,用户状态被锁定,ip受限 都不能登录成功
        *登录成功之后,所有业务页面显示当前用户的名称
        *实现10天记住密码
        *登录成功之后,跳转到业务主页面
        *登录失败,页面不跳转,提示信息

3、安全退出
    用户在任意的业务页面,点击"退出"按钮,弹出确认退出的模态窗口;用户在确认退出的模态窗口,点击"确定"按钮,完成安全退出的功能.
        *安全退出,清空cookie,销毁session
        *退出完成之后,跳转到首页

4、登录验证
    用户访问任何业务资源,都需要进行登录验证.
        *只有登录成功的用户才能访问业务资源
        *没有登录成功的用户访问业务资源,跳转到登录页面

5、创建市场活动
  	用户在市场活动主页面,点击"创建"按钮,弹出创建市场活动的模态窗口;
  	用户在创建市场活动的模态窗口填写表单,点击"保存"按钮,完成创建市场活动的功能.
        *所有者是动态的(//在现实市场活动主页面时，就从数据库中查询出所有用户并且显示在创建的模态窗口中)
        *所有者和名称不能为空
        *如果开始日期和结束日期都不为空,则结束日期不能比开始日期小
        *成本只能为非负整数
        *创建成功之后,关闭模态窗口,刷新市场活动列，显示第一页数据，保持每页显示条数不变
        *创建失败,提示信息创建失败,模态窗口不关闭,市场活动列表也不刷新

