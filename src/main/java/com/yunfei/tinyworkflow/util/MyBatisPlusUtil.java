package com.yunfei.tinyworkflow.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 适用于非spring项目使用mybatis-plus
 */
@Slf4j
@Builder
public class MyBatisPlusUtil {
    private String jdbcUrl;
    private String userName;
    private String password;
    private String driverName;
    private String daoPackageName;
    private SqlSessionFactory sqlSessionFactory;
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    public SqlSession getSession() {
        return sqlSessionFactory.openSession();
    }
    public void init() throws IOException {
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
        configuration.addMappers(daoPackageName);
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
        sqlSessionFactory = builder.build(configuration);
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
     * 初始化数据源
     *
     * @return
     */
    private DataSource initDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName(driverName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
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
}
