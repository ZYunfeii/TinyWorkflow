import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunfei.tinyworkflow.dao.TaskDao;
import com.yunfei.tinyworkflow.dao.WorkflowCtxDao;
import com.yunfei.tinyworkflow.entity.TaskDo;
import com.yunfei.tinyworkflow.entity.WorkflowCtxDo;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
@RunWith(JUnit4.class)
public class TestMysql {
    private SqlSession session;
//    @Test
//    public void testDataSource() throws Exception {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://123.56.98.228:3306/tinyworkflow?useUnicode=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC");
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUsername("root");
//        dataSource.setPassword("YUNFEI");
//        dataSource.setIdleTimeout(60000);
//        dataSource.setAutoCommit(true);
//        dataSource.setMaximumPoolSize(5);
//        dataSource.setMinimumIdle(1);
//        dataSource.setMaxLifetime(60000 * 10);
//        dataSource.setConnectionTestQuery("SELECT 1");
//        Connection connection = dataSource.getConnection();
//        PreparedStatement preparedStatement = connection.prepareStatement("show table status");
//        ResultSet resultSet = preparedStatement.executeQuery();
//        while(resultSet.next()){
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            int columnCount = metaData.getColumnCount();
//            for (int i = 1; i <= columnCount; i++) {
//                String name = metaData.getColumnLabel(i);
//                String field = resultSet.getString(i);
//                System.out.printf("%s:%s\t",name,field);
//            }
//            System.out.println();
//        }
//    }

    @Before
    public void before() throws IOException {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //这是mybatis-plus的配置对象，对mybatis的Configuration进行增强
        MybatisConfiguration configuration = new MybatisConfiguration();
        //这是初始化配置，后面会添加这部分代码
        initConfiguration(configuration);
        //这是初始化连接器，如mybatis-plus的分页插件
        configuration.addInterceptor(initInterceptor());
        //配置日志实现
        configuration.setLogImpl(Slf4jImpl.class);
        //扫描mapper接口所在包
        configuration.addMappers("com.yunfei.tinyworkflow.dao");
        //构建mybatis-plus需要的globalconfig
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        //此参数会自动生成实现baseMapper的基础方法映射
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        //设置id生成器
        globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
        //设置超类mapper
        globalConfig.setSuperMapperClass(BaseMapper.class);
        //设置数据源
        Environment environment = new Environment("1", new JdbcTransactionFactory(), initDataSource());
        configuration.setEnvironment(environment);
        this.registryMapperXml(configuration, "mapper");
        //构建sqlSessionFactory
        SqlSessionFactory sqlSessionFactory = builder.build(configuration);
        //创建session
        this.session = sqlSessionFactory.openSession();
    }

    /**
     * 初始化配置
     *
     * @param configuration
     */
    private void initConfiguration(MybatisConfiguration configuration) {
        //开启驼峰大小写转换
        configuration.setMapUnderscoreToCamelCase(true);
        //配置添加数据自动返回数据主键
        configuration.setUseGeneratedKeys(true);
    }

    /**
     * 初始化数据源
     *
     * @return
     */
    private DataSource initDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://123.56.98.228:3306/tinyworkflow?allowPublicKeyRetrieval=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&useUnicode=true");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("YUNFEI");
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
    }

    /**
     * 初始化拦截器
     *
     * @return
     */
    private Interceptor initInterceptor() {
        //创建mybatis-plus插件对象
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //构建分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    /**
     * 解析mapper.xml文件
     * @param configuration
     * @param classPath
     * @throws IOException
     */
    private void registryMapperXml(MybatisConfiguration configuration, String classPath) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> mapper = contextClassLoader.getResources(classPath);
        while (mapper.hasMoreElements()) {
            URL url = mapper.nextElement();
            if (url.getProtocol().equals("file")) {
                String path = url.getPath();
                File file = new File(path);
                File[] files = file.listFiles();
                for (File f : files) {
                    FileInputStream in = new FileInputStream(f);
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, f.getPath(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                    in.close();
                }
            } else {
                JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = urlConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().endsWith(".xml")) {
                        InputStream in = jarFile.getInputStream(jarEntry);
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, jarEntry.getName(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                        in.close();
                    }
                }
            }
        }
    }

    @Test
    public void testSelectPage() {
        TaskDao mapper = session.getMapper(TaskDao.class);
        List<TaskDo> taskDos = mapper.queryAll();
        System.out.println(taskDos);
    }

    @Test
    public void testInsert() {
        TaskDao mapper = session.getMapper(TaskDao.class);
        TaskDo taskDo = new TaskDo();
        taskDo.setTaskName("ddd");
        taskDo.setStatus("READY");
        taskDo.setWorkflowId(234123L);
        mapper.insert(taskDo);
        session.commit();
    }

    @Test
    public void testInsertCtx() {
        WorkflowCtxDao mapper = session.getMapper(WorkflowCtxDao.class);
        WorkflowCtxDo workflowCtxDo = new WorkflowCtxDo();
        workflowCtxDo.setCtx("afdsaf");
        workflowCtxDo.setWorkflowId(1231231L);
        mapper.insert(workflowCtxDo);
        session.commit();
    }

    @Test
    public void testQuertCtx() {
        WorkflowCtxDao mapper = session.getMapper(WorkflowCtxDao.class);
        WorkflowCtxDo workflowCtxDo = new WorkflowCtxDo();
        workflowCtxDo.setWorkflowId(1231231L);
        List<TaskDo> query = mapper.query(workflowCtxDo);
        System.out.println(query);
    }

    @Test
    public void testQueryWorkflowTask() {
        WorkflowCtxDao mapper = session.getMapper(WorkflowCtxDao.class);
        WorkflowCtxDo workflowCtxDo = new WorkflowCtxDo();
        workflowCtxDo.setWorkflowId(1231231L);
        List<TaskDo> query = mapper.query(workflowCtxDo);
        System.out.println(query);
    }

//    @Test
//    public void testJdbc() {
//        // 数据库连接信息
//        String url = "jdbc:mysql://123.56.98.228:3306/tinyworkflow";
//        String username = "root";
//        String password = "YUNFEI";
//
//        // 建立数据库连接
//        try (Connection connection = DriverManager.getConnection(url, username, password)) {
//            System.out.println("成功连接到数据库！");
//
//            // 执行查询
//            String sql = "SELECT * FROM workflow";
//            try (Statement statement = connection.createStatement();
//                 ResultSet resultSet = statement.executeQuery(sql)) {
//
//                // 处理结果集
//                while (resultSet.next()) {
//                    String name = resultSet.getString("task_name");
//                    System.out.println(name);
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("数据库连接失败！错误信息: " + e.getMessage());
//        }
//    }

}
