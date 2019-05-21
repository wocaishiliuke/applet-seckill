# applet-seckill

基于SSM的高并发秒杀项目。参考：
- [Sunybyjava/seckill](https://github.com/Sunybyjava/seckill)
- [codingXiaxw/seckill](https://github.com/codingXiaxw/seckill)

## 一、项目来源

项目来源于国内某IT公开课平台，由四个系列的课程组成，分为：
 - Java高并发秒杀业务分析与DAO层
 - Java高并发秒杀Service层
 - Java高并发秒杀Web层
 - Java高并发秒杀优化

## 二、开发环境
- 操作系统：Ubuntu 18.04 LTS
- IDE：IntelliJ IDEA
- JDK：JDK1.8
- Web容器：tomcat7-maven-plugin
- 数据库：Mysql-5.7.25-Linux(x86_64)
- 依赖管理：Maven  

## 三、数据准备

#### 建立数据库

运行工程中sql文件夹下的[seckill.sql](sql/seckill.sql)。

```sql
-- 整个项目的数据库脚本
-- 开始创建一个数据库
CREATE DATABASE seckill;
-- 使用数据库
USE seckill;
-- 创建秒杀库存表

CREATE TABLE sk_product(
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` INT NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '秒杀开启的时间',
  `end_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '秒杀结束的时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建的时间',
  PRIMARY KEY (id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)ENGINE =InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 插入初始化数据

insert into
  sk_product(name,number,start_time,end_time)
values
  ('1000元秒杀iphone6',100,'2019-5-17 00:00:00','2019-5-18 00:00:00'),
  ('500元秒杀iPad2',200,'2019-5-17 00:00:00','2019-5-18 00:00:00'),
  ('300元秒杀小米4',300,'2019-5-17 00:00:00','2019-5-18 00:00:00'),
  ('200元秒杀红米note',400,'2019-5-17 00:00:00','2019-5-18 00:00:00');

-- 秒杀成功明细表
-- 用户登录相关信息
create table sk_record(
  `skproduct_id` BIGINT NOT NULL COMMENT '秒杀商品ID',
  `user_phone` BIGINT NOT NULL COMMENT '用户手机号',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标示:-1无效 0成功 1已付款',
  `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY (skproduct_id,user_phone), /*联合主键*/
  KEY idx_create_time(create_time)
)ENGINE =InnoDB DEFAULT CHARSET =utf8 COMMENT ='秒杀成功明细表';
```

注意mysql不同版本中的模式差异，5.7默认使用严格模式，要求NO_ZERO_DATE，所以这里日期给出默认值。  


---
  
## 四、项目编码 
### 1.Java高并发秒杀业务分析与DAO层

#### 1.1 创建项目
使用IDEA或mvn archetype:generate命令，创建maven-archetype-webapp骨架工程。

> IDEA中还需要完善web.xml，以及main和test中的资源目录，并在Module中完成Mark As

#### 1.2 pom文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.baicai.applet</groupId>
    <artifactId>applet-seckill</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>applet-seckill Maven Webapp</name>
    <url>https://github.com/wocaishiliuke/applet-seckill</url>
    <parent>
        <groupId>com.baicai.parent</groupId>
        <artifactId>study-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <dependencies>
        <!-- 单元测试 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </dependency>
        <!-- 日志接口:slf4j,log4j,logback,common-logging
             日志实现:log4j,logback,common-logging -->
        <!-- 这里使用slf4j门面+logback实现 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
        </dependency>
        <!-- mysql驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!-- 数据源 -->
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
        </dependency>
        <!-- mybatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
        </dependency>
        <!-- Spring -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-expression</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
        </dependency>
      </dependencies>

      <build>
          <finalName>${project.artifactId}-${project.version}</finalName>
          <plugins>
              <plugin>
                  <groupId>org.apache.tomcat.maven</groupId>
                  <artifactId>tomcat7-maven-plugin</artifactId>
                  <version>2.2</version>
                  <configuration>
                      <port>9999</port>
                      <path>/</path>
                  </configuration>
              </plugin>
          </plugins>
      </build>
</project>
```

#### 1.3 类文件

具体参考项目代码。

```java
// 秒杀商品（库存）
public class SkProduct implements Serializable {
    private static final long serialVersionUID = 2912164127598660137L;
    /* 秒杀商品主键*/
    private long id;
    /* 秒杀商品名 */
    private String name;
    /* 秒杀商品编号 */
    private int number;
    /* 秒杀开始时间 */
    private LocalDateTime startTime;
    /* 秒杀结束时间 */
    private LocalDateTime endTime;
    /* 创建时间 */
    private LocalDateTime createTIme;

    public SkProduct() {}

    public SkProduct(long id, String name, int number, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createTIme) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTIme = createTIme;
    }
    // getter、setter、toString
}
```

```java
// 秒杀记录表
public class SkRecord implements Serializable {
    private static final long serialVersionUID = -6921147668978649157L;
    /* 秒杀商品主键 */
    private long skproductId;
    /* 用户手机号 */
    private long userPhone;
    /* 秒杀状态：-1无效 0成功 1已付款 */
    private short state;
    /* 创建时间*/
    private LocalDateTime createTime;
    /* 秒杀的商品 */
    private SkProduct skProduct;

    public SkRecord() {}

    public SkRecord(long skproductId, long userPhone, short state, LocalDateTime createTime, SkProduct skProduct) {
        this.skproductId = skproductId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
        this.skProduct = skProduct;
    }
    // getter、setter、toString
}
```

实体对应的Mapper接口

```java
// 秒杀商品Mapper（映射器）
public interface SkProductMapper {

   // 根据秒杀商品ID，查询商品详情
   SkProduct queryById(@Param("id") Long id);

   // 查询秒杀商品分页列表
   List<SkProduct> queryPageList(@Param("offset") Integer offset, @Param("limit") Integer limit);

   // 根据秒杀商品ID，减库存
   Integer reduceNumber(@Param("id") Long id, @Param("killTime") LocalDateTime killTime);
}
```

```java
// 秒杀记录Mapper映射器
public interface SkRecordMapper {

    // 根据秒杀商品ID&&用户手机号，查询秒杀记录
    SkRecord queryBySkproductIdAndUserPhone(@Param("skproductId") Long skproductId, @Param("userPhone") Long userPhone);

    //新增秒杀记录
    Integer insert(@Param("skproductId") Long skproductId, @Param("userPhone") Long userPhone, @Param("createTime") LocalDateTime createTime);
}
```

#### 1.4 配置文件

Mapper接口对应的mapper.xml

```xml
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.baicai.mapper.SkProductMapper">
    <!-- 根据ID查询 -->
    <select id="queryById" resultType="SkProduct">
        SELECT * FROM sk_product AS s WHERE s.id = #{id}
    </select>

    <!-- 分页查询秒杀商品列表 -->
    <select id="queryPageList" resultType="SkProduct">
        SELECT * FROM sk_product AS s ORDER BY create_time DESC LIMIT #{offset}, #{limit}
    </select>

    <!-- 根据ID，减库存 -->
    <update id="reduceNumber">
        UPDATE sk_product SET number = number - 1
        WHERE id = #{id} AND start_time <![CDATA[<=]]> #{killTime}
              AND end_time >= #{killTime} AND number > 0
    </update>
</mapper>
```

```xml
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.baicai.mapper.SkRecordMapper">
    <!-- 根据联合主键Id&&userPhone，查询秒杀记录（这里也可以使用resultMap） -->
    <select id="queryBySkproductIdAndUserPhone" resultType="SkRecord">
        SELECT skr.skproduct_id, skr.user_phone, skr.create_time, skr.state,
            skp.id  "skProduct.id",
            skp.name "skProduct.name",
            skp.number "skProduct.number",
            skp.start_time  "skProduct.start_time",
            skp.end_time  "skProduct.end_time",
            skp.create_time "skProduct.create_time"
        FROM sk_record skr
        INNER JOIN sk_product skp ON skr.skproduct_id = skp.id
        WHERE skr.skproduct_id = #{skproductId} AND skr.user_phone= #{userPhone}
    </select>

    <!-- 插入秒杀记录（IGNORE：唯一字段值已存在时，忽略错误，影响条数返回0，但主键仍会自增） -->
    <insert id="insert">
        INSERT IGNORE INTO sk_record (skproduct_id, user_phone, state, create_time)
        VALUES (#{skproductId}, #{userPhone}, 0, #{createTime})
    </insert>
</mapper>
```

Mybatis全局配置文件mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC
        "-//mybatis.org//DTD MyBatis Generator Configuration 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd" >
<configuration>
    <!--首先配置全局属性-->
    <settings>
        <!--开启自动填充主键功能,原理时通过jdbc的一个方法getGeneratekeys获取自增主键值-->
        <setting name="useGeneratedKeys" value="true"/>
        <!--使用别名替换列名,默认就是开启的-->
        <setting name="useColumnLabel" value="true"/>
        <!--开启驼峰命名的转换-->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
</configuration>
```

jdbc.properties

```java
jdbc.driver=com.mysql.jdbc.Driver
jdbc.user=root
jdbc.password=root
jdbc.url=jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf-8&useSSL=false
```

Spring整合Mybatis

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--suppress SpringFacetInspection -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
    http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--配置数据库连接池-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driver}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.user}"/>
        <property name="password" value="${jdbc.password}"/>
        <!-- c3p0私有属性 -->
        <property name="maxPoolSize" value="30"/>
        <property name="minPoolSize" value="10"/>
        <!-- 关闭连接后不自动commit -->
        <property name="autoCommitOnClose" value="false"/>
        <!-- 获取连接超时时间 -->
        <property name="checkoutTimeout" value="5000"/>
        <!-- 当获取连接失败重试次数 -->
        <property name="acquireRetryAttempts" value="2"/>
    </bean>

    <!-- 1.配置sqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!-- mybatis全局配置文件 -->
        <property name="configLocation" value="classpath:mybatis/mybatis-config.xml"/>
        <!-- 类别名扫描（也可以在mybatis-config.xml中配置） -->
        <property name="typeAliasesPackage" value="com.baicai.pojo"/>
        <!-- mapper.xml扫描 -->
        <property name="mapperLocations" value="classpath:mybatis/mappers/*.xml"/>
    </bean>
    <!-- 2.配置mapper接口包,动态实现mapper接口,注入到Spring容器 -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 给出要扫描的mapper接口 -->
        <property name="basePackage" value="com.baicai.mapper"/>
    </bean>
</beans>
```

#### 1.5 单元测试

在IDEA中，使用Ctrl+Shift+T，完成单元测试类的创建

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml"})
public class SkProductMapperTest {

    @Autowired
    private SkProductMapper skProductMapper;

    @Test
    public void queryById() {
        Long id = 1000L;
        SkProduct skProduct = skProductMapper.queryById(id);
        System.out.println(skProduct.toString());
    }

    @Test
    public void queryPageList() {
        List<SkProduct> skProducts = skProductMapper.queryPageList(0, 100);
        for (SkProduct skProduct : skProducts) {
            System.out.println(skProduct.toString());
        }
    }

    @Test
    public void reduceNumber() {
        Long id = 1000L;
        LocalDateTime localDateTime=LocalDateTime.now();
        int i = skProductMapper.reduceNumber(id, localDateTime);
        System.out.println(i);
    }
}
```

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml"})
public class SkRecordMapperTest {

    @Autowired
    private SkRecordMapper skRecordMapper;

    @Test
    public void queryBySkproductIdAndUserPhone() {
        Long id = 1000L;
        Long userPhone = 13888888888L;
        SkRecord skRecord = skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
        System.out.println(skRecord);
    }

    @Test
    public void insert() {
        Long id = 1000L;
        Long userPhone = 13888888888L;
        System.out.println(skRecordMapper.insert(id,userPhone, LocalDateTime.now()));
    }
}
```


### 2.Java高并发秒杀Service层

#### 2.1 类文件

接口和实现类

```java
// 秒杀商品（库存）业务层
public interface SkProductService {
    //根据ID，查询秒杀商品
    SkProduct getById(Long id);
    //查询秒杀商品分页列表
    List<SkProduct> getPageList(Integer offset, Integer limit);
    //在秒杀开启时，提供秒杀接口的地址，否则输出系统时间和秒杀地址
    ExposeResult expose(Long id);
    //执行秒杀操作
    SeckillResult seckill(Long id, Long userPhone, String md5);
}
```

```java
@Service
public class SkProductServiceImpl implements SkProductService {

    private static final Logger logger = LoggerFactory.getLogger(SkProductServiceImpl.class);
    /* 盐值 */
    private static final String SECKILL_SALT = "seckillSalt";

    @Autowired
    private SkProductMapper skProductMapper;
    @Autowired
    private SkRecordMapper skRecordMapper;

    /** 根据ID，查询秒杀商品 */
    @Override
    public SkProduct getById(Long id) {
        return skProductMapper.queryPageList(offset, limit);
    }

    /** 查询秒杀商品分页列表 */
    @Override
    public List<SkProduct> getPageList(Integer offset, Integer limit) {
        return skProductMapper.queryPageList(offset, limit);
    }

    /**
     * 在秒杀开启时输出秒杀接口的地址,否则输出系统时间跟秒杀地址
     * @param id 秒杀商品ID
     * @return 秒杀接口地址 或 系统时间和秒杀地址
     */
    @Override
    public ExposeResult expose(Long id) {
        // 判断是否存在该秒杀商品
        SkProduct skProduct = skProductMapper.queryById(id);
        if (skProduct == null) {
            logger.warn("查询不到该商品");
            return new ExposeResult(false, id);
        }
        // 判断是否在秒杀期间
        LocalDateTime startTime = skProduct.getStartTime();
        LocalDateTime endTime = skProduct.getEndTime();
        LocalDateTime nowTime = LocalDateTime.now();
        // 秒杀开启状态，返回商品id（加密）
        if (nowTime.isAfter(startTime) && nowTime.isBefore(endTime)) {
            return new ExposeResult(true, getSaltedMd5(id), id);
        }
        // 秒杀未开启状态，返回商品id
        return new ExposeResult(false, id, nowTime, startTime, endTime);
    }

    /**
     * 执行秒杀操作
     * 注：这里使用的自定义业务异常，都可以直接return代替，只是为了使用的异常统一处理
     * @param id 秒杀商品ID
     * @param userPhone 用户手机号
     * @param md5 md5加密值
     * @return 秒杀结果
     */
    @Override
    @Transactional
    public SeckillResult seckill(Long id, Long userPhone, String md5) {
        if(userPhone == null) {
            logger.error("请求缺少手机号");
            throw new SeckillNoPhoneException("seckill data without phone...");
        }
        if (md5 == null || !md5.equals(getSaltedMd5(id))) {
            logger.error("id的md5不匹配，说明数据被篡改");
            throw new SeckillRewriteException("seckill data is rewrited...");
        }

        // 执行秒杀
        LocalDateTime nowTime = LocalDateTime.now();
        try {
            // 减库存
            Integer affectNumber = skProductMapper.reduceNumber(id, nowTime);
            if (affectNumber <= 0) {
                logger.warn("减库存失败, 说明秒杀结束");
                throw new SeckillClosedException("seckill is closed...");
            } else {
                // 减库存成功，新增秒杀记录
                Integer insertCount = skRecordMapper.insert(id, userPhone, nowTime);
                // 判断是否重复秒杀
                if (insertCount <= 0) {
                    logger.warn("新增秒杀记录失败, 说明重复秒杀");
                    throw new SeckillRepeatedException("seckill repeated...");
                } else {
                    // 秒杀成功（减库存成功&&不是重复秒杀）
                    SkRecord skRecord = this.skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
                    return new SeckillResult(id, SeckillStatusEnum.SUCCESS, skRecord);
                }
            }
        } catch (SeckillClosedException | SeckillRepeatedException e1) {
            throw e1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 其他异常转换为自定义异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }

    private String getSaltedMd5(Long seckillId) {
        String base = seckillId + "/" + SECKILL_SALT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
```

> seckill()中最后又对Exception进行了捕获，转换成可以引起Spring事务回滚的RuntimeException，防止事务出现问题。

异常、枚举和返回的VO

```java
/** 秒杀业务中异常的基类 */
public class SeckillException extends RuntimeException {
  public SeckillException(String message) {super(message);}
  public SeckillException(String message, Throwable cause) {super(message, cause);}
}

/** 秒杀已关闭异常 */
public class SeckillClosedException extends SeckillException {
    public SeckillClosedException(String message) {super(message);}
    public SeckillClosedException(String message, Throwable cause) {super(message, cause);}
}

/** 秒杀无手机号异常 */
public class SeckillNoPhoneException extends SeckillException {
    public SeckillNoPhoneException(String message) {super(message);}
    public SeckillNoPhoneException(String message, Throwable cause) {super(message, cause);}
}

/** 重复秒杀异常 */
public class SeckillRepeatedException extends SeckillException{
   public SeckillRepeatedException(String message) {super(message);}
   public SeckillRepeatedException(String message, Throwable cause) {super(message, cause);}
}

/** 秒杀被重写异常 */
public class SeckillRewriteException extends SeckillException {
    public SeckillRewriteException(String message) {super(message);}
    public SeckillRewriteException(String message, Throwable cause) {super(message, cause);}
}
```
```java
/** 秒杀状态枚举 */
public enum SeckillStatusEnum {
    // status最好和存储过程的返回一致
    NO_PHONE(-4, "缺少手机号"),
    DATE_REWRITE(-3, "数据篡改"),
    INNER_ERROR(-2, "系统异常"),
    REPEAT_KILL(-1, "重复秒杀"),
    CLOSED(0, "秒杀结束"),
    SUCCESS(1, "秒杀成功");

    private Integer status;
    private String info;

    SeckillStatusEnum() {}
    SeckillStatusEnum(Integer status, String info) {
        this.status = status;
        this.info = info;
    }

    public Integer getStatus() {return status;}
    public String getInfo() {return info;}

    public static String info(Integer index) {
        for (SeckillStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus() == index) {
                return statusEnum.getInfo();
            }
        }
        return null;
    }
}
```

```java
/** 秒杀开启前后的暴露信息 */
public class ExposeResult {
    /* 秒杀商品id */
    private long skproductId;
    /* 是否开启秒杀 */
    private boolean exposed;
    /* 对秒杀地址进行加密措施 */
    private String md5;
    /* 当前时间 */
    private LocalDateTime now;
    /* 秒杀开启时间 */
    private LocalDateTime start;
    /* 秒杀结束时间 */
    private LocalDateTime end;

    public ExposeResult() {}
    public ExposeResult(boolean exposed, long skproductId) {
        this.exposed = exposed;
        this.skproductId = skproductId;
    }
    public ExposeResult(boolean exposed, String md5, long skproductId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.skproductId = skproductId;
    }
    public ExposeResult(boolean exposed, long skproductId, LocalDateTime now, LocalDateTime start, LocalDateTime end) {
        this.exposed = exposed;
        this.skproductId = skproductId;
        this.now = now;
        this.start = start;
        this.end = end;
    }
    // getter、setter、toString
}
```
```java
/** 秒杀结果 */
public class SeckillResult {

    /* 秒杀商品ID */
    private Long seckillId;
    /* 秒杀结果的状态 */
    private Integer state;
    /* 状态结果的说明 */
    private String stateInfo;
    /* 秒杀成功时的秒杀记录 */
    private SkRecord skRecord;

    /* 秒杀成功时，调用的构造 */
    public SeckillResult(Long seckillId, SeckillStatusEnum statusEnum, SkRecord skRecord) {
        this.seckillId = seckillId;
        this.state = statusEnum.getStatus();
        this.stateInfo = statusEnum.getInfo();
        this.skRecord = skRecord;
    }
    /* 秒杀失败时，调用的构造 */
    public SeckillResult(Long seckillId, SeckillStatusEnum statusEnum) {
        this.seckillId = seckillId;
        this.state = statusEnum.getStatus();
        this.stateInfo = statusEnum.getInfo();
    }
    // getter、setter、toString
}
```

#### 2.2 配置文件

applicationContext-service.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <context:component-scan base-package="com.baicai.service"/>

    <!-- 事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 开启基于注解的声明式事物 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

#### 2.3 单元测试

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-service.xml", "classpath:spring/applicationContext-dao.xml"})
public class SkProductServiceImplTest {

    @Autowired
    private SkProductService skProductService;

    @Test
    public void getById() throws Exception {
        System.out.println(skProductService.getById(1000L).toString());
    }

    @Test
    public void getPageList() throws Exception {
        List<SkProduct> seckillList = skProductService.getPageList(0,4);
        System.out.println(seckillList.toString());
    }

    @Test
    public void expose() throws Exception {
        ExposeResult exposeResult = skProductService.expose(1000L);
        System.out.println(exposeResult.toString());
    }
    
    /**
     * 可分别制造SeckillClosedException、SeckillRepeatedException、其他RuntimeException */
    @Test
    public void seckill() {
        Long id = 1000L;
        ExposeResult exposeResult = skProductService.expose(id);
        if (exposeResult.isExposed()) {
            long userPhone = 12222222222L;
            try {
                SeckillResult seckillResult = skProductService.seckill(id, userPhone, exposeResult.getMd5());
                System.out.println(seckillResult.toString());
            } catch (SeckillClosedException | SeckillRepeatedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("秒杀未开启");
        }
    }
}
```

### 3.Java高并发秒杀Web层

#### 3.1 引入依赖

```xml
<!-- web相关 -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
<!-- <dependency>
    <groupId>taglibs</groupId>
    <artifactId>standard</artifactId>
    <version>1.1.2</version>
</dependency> -->
```

#### 3.2 web.xml

IDEA生成的描述符版本太低，造成的问题、原因和解决方式，可参考[Web技术中的版本](https://blog.wocaishiliuke.cn/web/2018/04/21/Version01/)。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!-- 加载Spring配置 -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <!-- 避免重复读区springMVC的配置，否则可能会重复初始化扫描的controller，出现覆盖和不可预知的问题 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring/applicationContext-*.xml</param-value>
    </context-param>

    <!-- 配置SpringMVC -->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- springmvc配置文件 -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/springmvc-servlet.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

#### 3.3 Controller

```java
/** 秒杀商品接口 */
@Controller
@RequestMapping("skProduct")
public class SkProductController {

    @Autowired
    private SkProductService skProductService;
    
    /** 秒杀商品列表 */
    @GetMapping("list")
    public String list(Model model) {
        // TODO 分页：offset为页码，limit为每页条数，应有页面传来
        List<SkProduct> skProductList = skProductService.getPageList(0, 4);
        model.addAttribute("list", skProductList);
        return "list";
    }
    
    /** 秒杀商品详情 */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        if (id == null)
            return "redirect:/skProduct/list";
        SkProduct skProduct = skProductService.getById(id);
        if (skProduct == null)
            return "forward:/skProduct/list";
        model.addAttribute("skProduct", skProduct);
        return "detail";
    }
    
    /** 暴露秒杀接口 */
    @ResponseBody
    @RequestMapping(value = "expose/{id}", method = RequestMethod.GET)
    public CommonResult<ExposeResult> expose(@PathVariable("id") Long id) {
        CommonResult<ExposeResult> result;
        try {
            ExposeResult exposeResult = skProductService.expose(id);
            result = new CommonResult<>(true, exposeResult);
        } catch (Exception e) {
            e.printStackTrace();
            result = new CommonResult<>(false, e.getMessage());
        }
        return result;
    }
    
    /** 秒杀 */
    @ResponseBody
    @RequestMapping(value = "seckill/{id}/{md5}", method = RequestMethod.POST)
    public CommonResult<SeckillResult> seckill(@PathVariable("id") Long id, @PathVariable("md5") String md5, @CookieValue(value = "userPhone", required = false) Long userPhone) {
        try {
            SeckillResult seckillResult = skProductService.seckill(id, userPhone, md5);
            return new CommonResult<>(true, seckillResult);
        } catch (SeckillNoPhoneException e1) {
            // 缺少手机号
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.NO_PHONE));
        } catch (SeckillRewriteException e2) {
            // 秒杀被篡改
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.DATE_REWRITE));
        } catch (SeckillClosedException e3) {
            // 秒杀关闭
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.CLOSED));
        } catch (SeckillRepeatedException e4) {
            // 重复秒杀
            return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.REPEAT_KILL));
        }
    }

    /** 获取服务器端时间，防止用户篡改客户端时间提前参与秒杀 */
    @RequestMapping(value = "now", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<LocalDateTime> now() {
        return new CommonResult<>(true, LocalDateTime.now());
    }
}
```

Ajax统一返回对象

```java
/** 通用返回类（方便返回JSON格式） */
public class CommonResult<T> {
    private boolean success;
    private T data;
    private String error;

    public CommonResult() {}
    public CommonResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
    public CommonResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
    // getter、setter、toString
}
```

#### 3.4 前端页面

前端页面基于[Bootstrap](http://www.bootcss.com/)，这里采用本地引用的方式（也可以使用CDN版）。

> 关键库

- 首先下载JQuery，因为Bootstrap依赖JQuery
- 其次下载Bootstrap
- 然后下载一个倒计时插件jquery.countdown.min.js
- 最后再下载一个操作Cookie的插件jquery.cookie.min.js

> 抽取公用文件

- 首先编写一个公共头部jsp文件，WEB-INF/common/head.jsp，引入公用样式
- 然后编写一个公共jstl标签库文件，WEB-INF/common/tag.jsp，引入公用标签

> 列表、详情页

商品列表页和详情页，详见工程，注释详细。

> 自定义tag

jstl中的fmt标签，只能格式化java.Util.Date类型的日期和时间，这里使用了java8的LocalDateTime，所以解析时间会出现异常，需要自定义jstl标签来解析该类型的时间和日期。在/WEB-INF/tags下：

- localData.tag用来格式化日期
- localDataTime.tag用来格式化日期+时间的组合，即数据库中的Timestamp类型

具体的自定义过程参见对应文件，注释详细。

```
applet-seckill/src/main/webapp$ tree
.
├── index.jsp
├── resources
│   ├── plugins
│   │   ├── bootstrap-3.3.0
│   │   │   ├── css
│   │   │   │   ├── bootstrap.css
│   │   │   │   ├── bootstrap.css.map
│   │   │   │   ├── bootstrap.min.css
│   │   │   │   ├── bootstrap-theme.css
│   │   │   │   ├── bootstrap-theme.css.map
│   │   │   │   └── bootstrap-theme.min.css
│   │   │   ├── fonts
│   │   │   │   ├── glyphicons-halflings-regular.eot
│   │   │   │   ├── glyphicons-halflings-regular.svg
│   │   │   │   ├── glyphicons-halflings-regular.ttf
│   │   │   │   └── glyphicons-halflings-regular.woff
│   │   │   └── js
│   │   │       ├── bootstrap.js
│   │   │       ├── bootstrap.min.js
│   │   │       └── npm.js
│   │   ├── jquery.cookie.min.js
│   │   ├── jquery.countdown.min.js
│   │   └── jquery.js
│   └── script
│       └── seckill.js
└── WEB-INF
    ├── jsp
    │   ├── common
    │   │   ├── head.jsp
    │   │   └── tag.jsp
    │   ├── detail.jsp
    │   └── list.jsp
    ├── tags
    │   ├── localData.tag
    │   └── localDataTime.tag
    └── web.xml
```

### 4.Java高并发秒杀优化

#### 4.1 Redis

将商品信息放入Redis。

> Redis和Redis Desktop Manager的安装参考[Ubuntu环境搭建](https://blog.wocaishiliuke.cn/linux/2018/06/02/Ubuntu01/)。

- 1.引入依赖

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
<!-- Google protostuff序列化 -->
<dependency>
    <groupId>io.protostuff</groupId>
    <artifactId>protostuff-core</artifactId>
</dependency>
<dependency>
    <groupId>io.protostuff</groupId>
    <artifactId>protostuff-runtime</artifactId>
</dependency>
<!-- 工具依赖 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
</dependency>
```

- 2.Redis操作类

编写Redis操作类，并配置到applicationContext-dao.xml

```xml
<bean id="redisDao" class="com.baicai.dao.RedisDao">
    <!--构造方法注入值-->
    <constructor-arg index="0" value="${redis.addr}"/>
    <constructor-arg index="1" value="${redis.port}"/>
</bean>
```

```java
/** Redis操作类 */
public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(RedisDao.class);
    private RuntimeSchema<SkProduct> schema = RuntimeSchema.createFrom(SkProduct.class);
    private static final String PREFIX = "skproduct_";
    private static final int TIMEOUT = 60 * 60;

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }
    
    /** 从Redis中获取秒杀商品 */
    public SkProduct getSkProduct(Long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = PREFIX + id;
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                // 反序列化后返回
                SkProduct skProduct = schema.newMessage();
                ProtobufIOUtil.mergeFrom(bytes, skProduct, schema);
                return skProduct;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /** 将秒杀商品存入Redis */
    public String putSkProduct(SkProduct skProduct) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = PREFIX + skProduct.getId();
            byte[] bytes = ProtobufIOUtil.toByteArray(skProduct, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            // 设置超时时间
            return jedis.setex(key.getBytes(), TIMEOUT, bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
```

- 3.使用Redis操作类

使用RedisDao改造原有逻辑

```java
@Service
public class SkProductServiceImpl implements SkProductService {
  
    @Autowired
    private RedisDao redisDao;

    @Override
    public ExposeResult expose(Long id) {
        // 判断是否存在该秒杀商品
        /*SkProduct skProduct = skProductMapper.queryById(id);
        if (skProduct == null) {
            logger.warn("查询不到该商品");
            return new ExposeResult(false, id);
        }*/
        SkProduct skProduct = redisDao.getSkProduct(id);
        if (skProduct == null) {
            // 访问数据库读取数据
            skProduct = skProductMapper.queryById(id);
            if (skProduct == null) {
                logger.warn("查询不到该商品");
                return new ExposeResult(false, id);
            } else {
                // 放入redis
                redisDao.putSkProduct(skProduct);
            }
        }

        // 判断是否在秒杀期间
        LocalDateTime startTime = skProduct.getStartTime();
        LocalDateTime endTime = skProduct.getEndTime();
        LocalDateTime nowTime = LocalDateTime.now();
        // 秒杀开启状态，返回商品id（加密）
        if (nowTime.isAfter(startTime) && nowTime.isBefore(endTime)) {
            return new ExposeResult(true, getSaltedMd5(id), id);
        }
        // 秒杀未开启状态，返回商品id
        return new ExposeResult(false, id, nowTime, startTime, endTime);
    }
    ...
}
```

- 4.测试缓存

查看控制台的SQL打印，和Redis Desktop Manager中的数据。

#### 4.2 存储过程

使用存储过程，组合SQL，减少与数据库的连接次数。

##### 4.2.1 在数据库层创建存储过程

- 1.在Workbench的Stored procedures中创建，输入下列代码（详见sql/procedure.sql），Apply完成创建

```sql
-- 定义储存过程（先插记录表，再减库存）
-- in输入参数, out输出参数
-- row_count() 返回上一条修改类型sql(delete,insert,update)的影响行数, 0-未修改数据、>0-影响行数、<0-sql错误
-- r_result返回值：-2:sql错误, -1:重复秒杀, 0:秒杀结束, 1:秒杀成功
CREATE DEFINER=`root`@`%` PROCEDURE `execute_seckill`(IN v_id BIGINT, IN v_phone BIGINT, IN v_kill_time  TIMESTAMP, OUT r_result INT)
BEGIN
  DECLARE insert_count INT DEFAULT 0;
  START TRANSACTION;
  INSERT IGNORE INTO sk_record(skproduct_id, user_phone, state, create_time) VALUES (v_id, v_phone, 0, v_kill_time);
  SELECT row_count() INTO insert_count;
  IF (insert_count = 0)
    THEN ROLLBACK;
    SET r_result = -1;
  ELSEIF (insert_count < 0)
    THEN ROLLBACK;
    SET r_result = -2;
  ELSE
    UPDATE sk_product SET number = number - 1 WHERE id = v_id AND end_time > v_kill_time AND start_time < v_kill_time AND number > 0;
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0)
      THEN ROLLBACK;
      SET r_result = 0;
    ELSEIF (insert_count < 0)
      THEN ROLLBACK;
      SET r_result = -2;
    ELSE
      COMMIT;
      SET r_result = 1;
    END IF;
  END IF;
END
```

- 2.Workbench中测试

在查询窗口执行下列SQL。

```sql
SET @r_result = -3;
--  执行储存过程
CALL execute_seckill(1003, 13888888888, now(), @r_result);
-- 获取结果
SELECT @r_result;
```

##### 4.2.2 在Java层创建存储过程

在数据库中完成存储过程测试后，编写对应的调用代码。

- 1.在DAO层，定义调用存储过程的Java方法

在SkProductMapper接口中增加，使用存储过程完成"减库存+查记录"的方法。

```java
public interface SkProductMapper {
    ...

    /** 使用储存过程执行秒杀 */
    void seckillByProcedure(Map<String,Object> param);
}
```

在对应SkProductMapper.xml中，增加statement

```xml
<!-- 调用储存过程 -->
<select id="seckillByProcedure" statementType="CALLABLE">
    CALL execute_seckill(
        #{id,jdbcType=BIGINT,mode=IN},
        #{phone,jdbcType=BIGINT,mode=IN},
        #{killTime,jdbcType=TIMESTAMP,mode=IN},
        #{result,jdbcType=INTEGER,mode=OUT}
    )
</select>
```

- 2.在Service层，也添加对应的方法

```java
public interface SkProductService {
    ...

    /** 使用储存过程执行秒杀 */
    SeckillResult seckillByProcedure(Long id, Long userPhone, String md5);
}
```
```java
@Override
@Transactional
public SeckillResult seckillByProcedure(Long id, Long userPhone, String md5) {
    if(userPhone == null) {
        logger.error("请求缺少手机号");
        throw new SeckillNoPhoneException("seckill data without phone...");
    }
    if (md5 == null || !md5.equals(getSaltedMd5(id))) {
        logger.error("id的md5不匹配，说明数据被篡改");
        throw new SeckillRewriteException("seckill data is rewrited...");
    }

    LocalDateTime killTime = LocalDateTime.now();
    Map<String, Object> map = new HashMap<>(8);
    map.put("id", id);
    map.put("phone", userPhone);
    map.put("killTime", killTime);
    map.put("result", null);
    try {
        // 执行储存过程
        skProductMapper.seckillByProcedure(map);
        // 获取result（获取不到时，默认-2，表示sql错误INNER_ERROR）
        int result = MapUtils.getInteger(map, "result", -2);
        // 根据存储过程的返回值，抛异常或返回
        if (result == SeckillStatusEnum.SUCCESS.getStatus()) {
            SkRecord skRecord = skRecordMapper.queryBySkproductIdAndUserPhone(id, userPhone);
            return new SeckillResult(id, SeckillStatusEnum.SUCCESS, skRecord);
        } else if (result == SeckillStatusEnum.CLOSED.getStatus()) {
            logger.warn("减库存失败, 说明秒杀结束");
            throw new SeckillClosedException("seckill is closed...");
        } else if (result == SeckillStatusEnum.REPEAT_KILL.getStatus()) {
            logger.warn("新增秒杀记录失败, 说明重复秒杀");
            throw new SeckillRepeatedException("seckill repeated...");
        } else {
            logger.warn("内部错误");
            throw new SeckillException("seckill inner error...");
        }
    } catch (SeckillClosedException | SeckillRepeatedException e1) {
        throw e1;
    } catch (SeckillException e2) {
        throw e2;
    } catch (Exception e) {
        logger.error(e.getMessage(), e);
        // 其他异常转换为自定义异常
        throw new SeckillException("seckill inner error: " + e.getMessage());
    }
}
```

- 3.单元测试

```java
/** 测试使用存储过程的秒杀 */
@Test
public void executeSeckillProcedureTest() {
    Long id = 1001L;
    Long phone = 13688888888L;
    ExposeResult exposeResult = skProductService.expose(id);
    if (exposeResult.isExposed()) {
        SeckillResult seckillResult = skProductService.seckillByProcedure(id, phone, exposeResult.getMd5());
        System.out.println(seckillResult.getStateInfo());
    }
}
```

##### 4.2.3 使用存储过程方法进行改造

改造SeckillController.execute()，把普通方法seckill()改成调用了储存过程的seckillByProcedure()。

```java
/**
 * 秒杀
 * TODO 异常统一处理（注意处理后的返回数据需要参数id）
 * @param id
 * @param md5
 * @param userPhone 前端保存在Cookie
 */
@ResponseBody
@RequestMapping(value = "seckill/{id}/{md5}", method = RequestMethod.POST)
public CommonResult<SeckillResult> seckill(@PathVariable("id") Long id, @PathVariable("md5") String md5, @CookieValue(value = "userPhone", required = false) Long userPhone) {
    try {
        // 这里换成储存过程
        //SeckillResult seckillResult = skProductService.seckill(id, userPhone, md5);
        SeckillResult seckillResult = skProductService.seckillByProcedure(id, userPhone, md5);
        return new CommonResult<>(true, seckillResult);
    } catch (SeckillNoPhoneException e1) {
        // 缺少手机号
        return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.NO_PHONE));
    } catch (SeckillRewriteException e2) {
        // 秒杀被篡改
        return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.DATE_REWRITE));
    } catch (SeckillClosedException e3) {
        // 秒杀关闭
        return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.CLOSED));
    } catch (SeckillRepeatedException e4) {
        // 重复秒杀
        return new CommonResult<>(false, new SeckillResult(id, SeckillStatusEnum.REPEAT_KILL));
    }
}
```


