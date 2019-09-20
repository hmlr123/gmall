# Gamll商城
## 服务分布

开发过程中

gmall-user-service      用户服务的service层    9000  
gmall-user-web          用户服务的web层        8090  

gmall-manage-service    后台管理系统的service层 9001    
gmall-manage-web        后台管理系统的web层     8091  

gmall-item-service      商品详情服务           9002 此服务不写  
gmall-item-web          商品详情展示           8092  

gmall-search-service    商品搜索服务后台        9003  
gmall-search-web        商品搜索服务前台        8093  

gmall-cart-service      购物车服务             9004  
gmall-cart-web          购物车服务             8094  

gmall-user-service      用户服务               9000  
gamll-passport-web      用户认证中心            8095     

gmall-order-service     订单服务               9006  
gmall-order-web         订单服务               8096  

gmall-payment-service   支付服务               9007  
gmall-payment-web       支付服务               8097      

gware-manage            库存系统               9008  

gmall-seckill           秒杀                  9009  



## 端口占用情况

服务器上

```shell
80  默认端口，任何情况不使用
3306 mysql
8080 50000 jenkins 目前挂了
6379 redis访问端口
23   搭建gitlab 没有使用
8081 搭建gitlab 没有使用
8082 dubbo-admin（tomcat）使用
8083 fdfs tracker （nginx）监听端口
8084 fdfs storage （nginx）监听端口
22122 fdfs tracker 访问端口
23000 fdfs storage 访问端口
10080 gogs 访问端口
10022 gogs ssh访问端口
2888 3888 2181 zookeeper端口
```



## 防火墙命令

https://www.cnblogs.com/companionspace/p/10249759.html

开放端口：

```shell
firewall-cmd --zone=public --add-port=8080/tcp --permanent
```

重新载入

```shell
firewall-cmd --reload
```

查看

```shell
firewall-cmd --zone=public --query-port=80/tcp
```

删除端口

```shell
firewall-cmd --zone=public --remove-port=80/tcp --permanent
```

查看所有打开的端口

```shell
firewall-cmd --zone=public --list-ports
```

```shell
启动： systemctl start firewalld
关闭： systemctl stop firewalld
查看状态： systemctl status firewalld 
开机禁用  ： systemctl disable firewalld
开机启用  ： systemctl enable firewalld


启动一个服务：systemctl start firewalld.service
关闭一个服务：systemctl stop firewalld.service
重启一个服务：systemctl restart firewalld.service
显示一个服务的状态：systemctl status firewalld.service
在开机时启用一个服务：systemctl enable firewalld.service
在开机时禁用一个服务：systemctl disable firewalld.service
查看服务是否开机启动：systemctl is-enabled firewalld.service
查看已启动的服务列表：systemctl list-unit-files|grep enabled
查看启动失败的服务列表：systemctl --failed

查看版本： firewall-cmd --version
查看帮助： firewall-cmd --help
显示状态： firewall-cmd --state
查看所有打开的端口： firewall-cmd --zone=public --list-ports
更新防火墙规则： firewall-cmd --reload
查看区域信息:  firewall-cmd --get-active-zones
查看指定接口所属区域： firewall-cmd --get-zone-of-interface=eth0
拒绝所有包：firewall-cmd --panic-on
取消拒绝状态： firewall-cmd --panic-off
查看是否拒绝： firewall-cmd --query-panic
```

**上传下载命令** 终端不支持 使用传输工具

```
rz
sz
```



## git 命令

https://blog.csdn.net/w958796636/article/details/53611133

https://www.cnblogs.com/leaf930814/p/6664706.html

工作区（Working Directory）
版本库（Repository） #.git
stage(index) 暂存区
master Git自动创建的分支
HEAD 指针

### 常用命令

```shell
git init	#初始化仓库
git add README.md	#添加文件
git commit -m "first commit"	#提交到暂存区
git remote add origin http://119.23.110.43:10080/liwei/nguSeckill.git # 添加远程仓库 origin仓库别名
git push -u origin master	# 推送到远程仓库 分支master

# 撤销提交 commit
git reset --soft HEAD^
# HEAD^的意思是上一个版本，也可以写成HEAD~1
# 参数 
# --mixed 不删除工作空间改动代码，撤销commit，并且撤销git add . 操作
#         这个为默认参数,git reset --mixed HEAD^ 和 git reset HEAD^ 效果是一样的。
# --soft  不删除工作空间改动代码，撤销commit，不撤销git add . 
# --hard  删除工作空间改动代码，撤销commit，撤销git add . 
#         注意完成这个操作后，就恢复到了上一次的commit状态。

# 查看文件修改内容
git diff

# 查看提交离职
git log

# 单行显示
git log --pretty=online

# 回退到指定版本
commit id

# 查看历史命令
git reflog
```



### 版本回退

```shell
# 查看工作区和版本库里最新版本的区别
git diff HEAD -- <file>   

# 版本库替换工作区的版本，一键还原
git checkout --<file>

# 暂存区的撤销，重新放回到工作区
git reset HEAD<file>

# 删除文件 ，如果文件提交到版本库，不用担心误删，只能回复到最新版本
git rm <file>                         
```



### 密钥

```shell
# 创建SSH key
ssh-keygen -t rsa -C 'email'

# 关联本地仓库，远程库的名字为origin
git remote add origin git@github.com:username/repostery.git    

# 第一次把当前分支master推送到远程，-u参数不但推送，而且将本地的分支和远程的分支关联起来
git push -u origin master 

# 把当前分支master推送到远程
git push origin master   

# 从远程库克隆一个到本地库
git clone git@github.com:username/repostery.git   
```



### 分支

```shell
# 创建并切换分支 相当于git branch dev 和git checkout dev 
git checkout -b dev

# 创建分支
git branch dev

# 切换分支
git checkout dev

# 合并分支 合并某个分支（dev）到当前分支
git merge dev

# 删除分支
git branch -d dev

# 查看分支合并图
git log --graph

# 禁用Fast forward合并dev分支
git merge --no-ff -m 'message' dev 

# 隐藏当前工作现场，等恢复后继续工作
git stash

# 查看stash记录
git stash list

# 仅恢复现场，不删除stash内容
git stash apply

# 删除stash内容
git stash drop

# 回复现场的同时删除stash内容
git stash pop

#强行删除某个未合并的分支
git branch -D dev

# 查看远程仓库
git remote

# 查看远程仓库版本
git remote -v

# 在本地创建和远程分支对应的分支
git checkout -b branch-name origin/branch-name

# 建立本地分支和远程分支的关联
git branch --set-upstream branch-name origin/branch-name
```



### 标签

```shell
# 当前分支最新的commit打标签
git tag v1.0

# -a 指定标签名 -m指定文字
git tag -a v0.1 -m '评论说' 5345436

# 使用PGP签名
git tag -s <tagname> -m 'blabla'

# 查看所有标签
git tag 

# 查看标签信息
git show v0.1

# 删除标签信息
git tag -d v0.1

# 推动某个标签到远程
git push origin v0.1

# 推送所有尚未推送发的本地标签
git push origin --tags
```









## 环境搭建

### mysql 端口映射 3306

安装脚本：

```shell
docker pull mysql:5.7
docker run -d -p 3306:3306 --privileged=true -v /root/mysql/conf/my.cnf:/etc/mysql/my.cnf -v /root/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=hmlr123 --name mysql mysql:5.7
```

解释：

`--privileged=true`容器内具有root权限，否则没有

`-v `配置文件映射



### redis 端口映射 6379

https://blog.csdn.net/woniu211111/article/details/80970560

```shell
docker run -d --privileged=true -p 6379:6379 -v /root/redis/conf/redis.conf:/etc/redis/redis.conf -v /root/redis/data:/data --name redis redis-server /etc/redis/redis.conf --appendonly yes
```

`--appendonly yes`开启数据持久化

注掉*daemonize yes*



### jenkins 端口映射 8080

官网：https://jenkins.io/zh/doc/book/installing/

```shell
docker run \
  -u root \
  --rm \
  -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v /root/jenkins/jenkins-data:/var/jenkins_home \
  -v /root/jenkins/var/run/docker.sock:/var/run/docker.sock \
  jenkinsci/blueocean
```



### nginx



### fdfs 

https://blog.csdn.net/canyun9798/article/details/83795789

新版本 端口很多，注意区分

tracker-server:22122

tracker-nginx监听：8083

storage-server:

storage-nginx监听：



步骤

1. **安装tracker跟踪器**

   ```shell
   docker run -d --name tracker --net=host morunchang/fastdfs sh tracker.sh
   ```

2. 修改nginx监听tracker的端口

   ```shell
   docker exec -it tracker /bin/bash
   
   vi /etc/nginx/conf/nginx.conf
   
   # nginx监听tracker的端口号 tracker的访问端口号
   listen 8080 改为 8083
   
   ```

3. 修改文件`client.conf`

   ```shell
   vi /etc/fdfs/client.conf
   
   # tracker 容器端口 改IP 不改端口
   tracker_server=119.23.110.43:22122  
   
   # tracker的访问端口号
   http.tracker_server_port=8083
   
   
   ```

4. 修改`tracker.conf`

   ```shell
   vi /etc/fdfs/tracker.conf
   
   # tracker的端口号 不动他
   port=22122
   
   # tracker的监听端口号，http访问的端口号。
   http.server_port=8083
   ```

5. 退出容器

   ```shell
   exit
   ```

6. **安装storage**

   ```shell
   docker run -d --name storage --net=host -e TRACKER_IP=119.23.110.43:22122 -v /root/fdfs/storage:/data/fast_data -e GROUP_NAME=group1 morunchang/fastdfs sh storage.sh
   ```

   注意是tracker的端口号，不是tracker监听的端口号

7. 修改nginx

   ```shell
   docker exec -it storage /bin/bash
   
   vi /etc/nginx/conf/nginx.conf
   # storage容器的监听端口号
   listen 8080 改为 8084
   ```

8. 修改client.conf

   ```shell
   vi /etc/fdfs/client.conf
   
   # tracker 的地址和端口号
   tracker_server=119.23.110.43:22122
   
   # tracker 的访问端口号
   http.tracker_server_port=8083
   ```

9. 修改storage.conf

   ```shell
   vi /etc/fdfs/storage.conf
   
   # storage容器的端口号 不动他
   port = 23000
   
   # tracker的端口号
   traccker_server=119.23.110.34:8083
   
   # storage的访问端口号
   http.server_port=8084
   ```

10. 重启服务

    ```shell
    docker restart storage tracker
    
    # 查看防火墙
    sudo firewall-cmd --zone=public --list-all
    
    # 依次开放端口
    firewall-cmd --zone=public --add-port 8083/tcp --permanent
    
    ```

    

图片访问路径：http://119.23.110.43:8084/group1/M00/00/00/rBAUjV1s2PyAJqkbABP_wuus72s121.jpg

说明访问的是storage的监听端口





### zookeeper dubbo

zookeeper 2181：2181

https://blog.csdn.net/qq_33562996/article/details/80599922

```shell
docker pull zookeeper

docker run --name zookeeper -p 2181:2181 -v /root/zookeeper/data:/data  -d zookeeper:latest
```

方案一

dubbo

```shell
docker run --name dubbo-admin -p 8082:8080 -v /root/dubbo-admin/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml -v /root/dubbo-admin/logs:/usr/local/tomcat/logs -d tomcat:8.5.29
```

方案二

https://blog.csdn.net/u010928589/article/details/93227780

```shell
docker run -d -p 8082:8080 --name dubbo-admin -v /root/dubbo-admin/myapplication.properties:/dubbo-admin/myapplication.properties cao2068959/dubbo-admin:2.7
```





### kafka



### git仓库 gitlab  端口映射8081 23

```shell
docker run \
    --publish 443:443 --publish 8081:80 --publish 23:22 \
    --name gitlab \
    --volume /root/gitlab/config:/etc/gitlab \
    --volume /root/gitlab/logs:/var/log/gitlab \
    --volume /root/gitlab/data:/var/opt/gitlab \
    gitlab/gitlab-ce
```

吃性能，暂时不用



### nexus3私服 吃性能，不用

```shell
docker run -id --privileged=true --name=nexus3 --restart=always -p 8081:8081 -v /root/nexus3/data:/var/nexus-data 6e9721ad473a


docker run -d \
   -p 8083:8081 \
   --name nexus \
   -v /root/nexus3/data:/nexus-data \
   -e INSTALL4J_ADD_VM_PARAMS="-Xms256m -Xmx512m -XX:MaxDirectMemorySize=1024m" \
   sonatype/nexus3 
```



### gogs  端口映射10022:22 10080:3000

```shell
docker run -d --name=gogs -p 10022:22 -p 10080:3000 -v /root/gogs/data:/data gogs/gogs


数据库设置
创建用户：create user 'gogs'@'%' identified by 'gogspwd';
授权用户：grant all privileges on gogs.* to 'gogs'@'%'
删除用户：drop user gogs;此时只能删除%权限的，不能删除localhost权限的，附加：drop user gogss@'localhost'
用户名gogs
密码：gogspwd
```



### win10家庭版安装docker问题

1. 安装Hype-v

   https://blog.csdn.net/weixin_37695006/article/details/91589895

   新建文件Hyper-V.cmd

```shell
pushd "%~dp0"
dir /b %SystemRoot%\servicing\Packages\*Hyper-V*.mum >hyper-v.txt
for /f %%i in ('findstr /i . hyper-v.txt 2^>nul') do dism /online /norestart /add-package:"%SystemRoot%\servicing\Packages\%%i"
del hyper-v.txt
Dism /online /enable-feature /featurename:Microsoft-Hyper-V-All /LimitAccess /ALL
```

2. 管理员权限执行，重启

3. 安装docker 修改注册表

   https://www.jianshu.com/p/1329954aa329

   做之前备份注册表

   定位到HKEY_LOCAL_MACHINE\software\Microsoft\Windows NT\CurrentVersion，点击current version，在右侧找到EditionId，右键点击EditionId 选择“修改“，在弹出的对话框中将第二项”数值数据“的内容改为Professional，然后点击确定

4. docker version报错

   https://blog.csdn.net/fanfan4569/article/details/71340556

   ```cmd
   docker-machine env --shell cmd default
   ```

5. 常规使用

   https://www.jianshu.com/p/9698cc75e00c

   配置国内镜像源

   配置共享磁盘

   配置内存、CPU核数、交换空间等数据



### elasticsearch文本搜索 9300 9200

#### linux下单机版

https://blog.csdn.net/a243293719/article/details/82021823

```shell
docker run -d -p 9200:9200 -p 9300:9300 \
	--name myes \
	-v /root/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
	-v /root/elasticsearch/data:/usr/share/elasticsearch/data \
	-e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
	-e "discovery.type=single-node" \
	elasticsearch:6.8.2
elasticsearch
# 测试是否安装成功
curl localhost:9200
# 注意 9300是内部传输端口，9200是http端口，这两个不同的


```





#### win10下 单机版

https://www.jianshu.com/p/9698cc75e00c

1. 启动es

```shell
docker run -di --name=elasticsearch -p 9200:9200 -p 9300:9300 elasticsearch:6.8.2
```

2. 复制yml文件

```shell
docker cp elasticsearch:/usr/share/elasticsearch/config/elasticsearch.yml E:\Docker\elasticsearch\elasticsearch.yml
```

3. 删除原来的docker容器，启动新的

```shell
docker run -di --name=myes -p 9200:9200 -p 9300:9300 -v E:/Docker/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml elasticsearch:6.8.2

-v E:/Docker/elasticsearch/data:/usr/share/elasticsearch/data 
```

4. **安装ik分词器**

```shell
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.8.2/elasticsearch-analysis-ik-6.8.2.zip
```

5. 重启
6. 安装elasticsearch-head

```shell
docker run --name myhead -p 9100:9100 -dit docker.io/mobz/elasticsearch-head:5
```

7. 安装kibana

```shell
docker run --name kibana -e ELASTICSEARCH_URL=http://10.177.18.9:9200  -p 5601:5601 -d kibana:6.8.2
# 使用本地ip
docker start kibana

```





nohub:启动脚本的日志（命令行）保存

```shell
nohup ./kibana & 

```



#### 集群搭建

https://blog.csdn.net/belonghuang157405/article/details/83301937

1. 配置文件

```shell
cluster.name: elasticsearch-cluster
node.name: es-node1
network.bind_host: 0.0.0.0
network.publish_host: 192.168.9.219
http.port: 9200
transport.tcp.port: 9300
http.cors.enabled: true
http.cors.allow-origin: "*"
node.master: true 
node.data: true  
discovery.zen.ping.unicast.hosts: ["192.168.9.219:9300","192.168.9.219:9301","192.168.9.219:9302"]
discovery.zen.minimum_master_nodes: 2

```



```shell
 docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 -v E:/Docker/elasticsearch/config/es0.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name ES00 myes:v1
 
 docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9201:9201 -p 9301:9301 -v E:/Docker/elasticsearch/config/es1.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name ES01 myes:v1

 docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9202:9202 -p 9302:9302 -v E:/Docker/elasticsearch/config/es2.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name ES02 myes:v1
 
 docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9203:9203 -p 9303:9303 -v E:/Docker/elasticsearch/config/es3.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name ES03 myes:v1
  
 docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9204:9204 -p 9304:9304 -v E:/Docker/elasticsearch/config/es4.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name ES04 myes:v1


```





### 制作镜像

https://www.cnblogs.com/lsgxeva/p/8746644.html

```shell
docker commit -m "增加ki词法分析" -a "Docker liwei" 0b2616b0e5a8 myes:v1

```





### ActiveMq搭建

https://www.jianshu.com/p/c8fe10729624

```shell
docker pull webcenter/activemq
docker run -d --name myactivemq -p 61616:61616 -p 8161:8161 webcenter/activemq

```

61616:tcp服务端口

8161:http管理界面端口

## 技术栈

![技术栈.png](https://i.loli.net/2019/09/20/1KmCNMDwIbEUWjv.png)

### Springboot

### mybatis mybaits增强版使用（tk）

### dubbo + zookeeper 微服务

   RPC框架，相关概念，zookeeper推送消费者基于长连接

   ```properties
   spring.dubbo.consumer.check=false  # 启动的时候是否检查生产者是否启动，如果true，则启动报错，无法注入
   ```

    对比Springcloud

### fdfs小文件（4KB~500MB）上传(环境搭建，相关概念)

   Client、Tracker Server、Storage Server

### thymeleaf(语法，严格语法，缓存)

### redis缓存

redis分布式锁防止高并发（击穿、穿透、雪崩三种情况解决方法，lua脚本）



 **穿透**：用户访问缓存中不存在的数据，使之直接访问db，造成数据库压力过大

 方案一：不存在的数据设置缓存数据，设置较短的过期时间

 方案二：设置布隆过滤器（中文概念：https://www.jianshu.com/p/2104d11ee0a2）类似网状结构，当计算出来的 hash值全部命中，说明该数据可能存在https://hackernoon.com/probabilistic-data-structures-bloom-filter-5374112a7832（en参考文档）

 在java中的实际运用（https://blog.csdn.net/u011521890/article/details/89605577 and https://blog.csdn.net/william0318/article/details/89332970）

 

 **雪崩**：某一时间缓存时间集体失效

 方案一：设置不同的过期时间

 方案二：加锁或者队列的方式保证缓存的单线 程（进程）写，从而避免失效时大量的并发请求落到底层存储系统上

 

 **击穿**：某一热点数据过期，造成大量的请求到达db，造成服务器压力过大

 非分布式锁：https://blog.csdn.net/sanyaoxu_2/article/details/79472465

 解决方案：分布式锁（可重入，锁的控制权一定在一的手里），如下

 

 **分布式锁**（思路演变过程，值得思考）：https://blog.csdn.net/varyall/article/details/88751345

```java
     /**
      * 获取sku数据.
      *
      * @param skuId sku数据
      * @return      sku实体数据
      */
     @Override
     public PmsSkuInfo getBySkuId(String skuId, String ip) {
         //获取连接
         Jedis jedis = redisUtil.getJedis();
         //Key
         String skuKey = "sku:" + skuId + ":info";
         //从缓存中获取数据
         String pmsSkuInfoStr = jedis.get(skuKey);
         PmsSkuInfo pmsSkuInfo = null;
         if (StringUtils.isNotBlank(pmsSkuInfoStr)) {
             pmsSkuInfo = JSON.parseObject(pmsSkuInfoStr, PmsSkuInfo.class);
             System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + "  缓存中存在数据");
         } else {
             //设置分布式锁 防止redis击穿
 
             //防止请求线程还没结束，当前锁失效 加个标识位
             String token = UUID.randomUUID().toString();
             //锁有 key value 过期时间 加锁策略
             String ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "ex", 10);
             if (StringUtils.isNotBlank(ok) && "OK".equals(ok)) {
                 System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + "sku:" + skuId + ":lock" + " 取得对象锁");
                 //设置成功，有权在10s内访问数据库
                 pmsSkuInfo = this.getBySkuIdFromDb(skuId);
                 if (null != pmsSkuInfo) {
                     System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 数据写入缓存");
                     jedis.set("sku:" + pmsSkuInfo.getId() + ":info", JSON.toJSONString(pmsSkuInfo));
                 } else {
                     System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 数据库不存在该记录");
                     //数据库中不存在该sku 防止数据库穿透 过期时间3分钟
                     jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                 }
                 System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 释放锁");
                 //方案一:增加锁标识 在删除的时候判断判断是我们标识的锁.
 //                //释放锁 判断是否是之前标识的那个，如果是就可以删除
 //                String localToken = jedis.get("sku:" + skuId + ":lock");
 //                if (StringUtils.isNotBlank(localToken) && localToken.equals(token)) {
 //                    //说明锁还没有过期
 //                    jedis.del("sku:" + skuId + ":lock");
 //                }
 
                 //方案二:方案一存在在判断的时候锁失效,导致删除其他线程的锁.
                 //A:使用lua脚本,在获取我们标识位的同时删除我们的锁,(不经过我们java,在redis中删除)
                 // 思考,跨技术的交互都会存在这种问题.比如java的原子性操作CAS就是通过底层实现的.
                 List<String> keys  = new ArrayList();
                 keys.add("sku:" + skuId + ":lock");
                 List<String> tokens = new ArrayList<>();
                 tokens.add(token);
                 String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                 jedis.eval(script, keys, tokens);
             } else {
                 System.out.println("IP地址：" + ip + " 线程名：" + Thread.currentThread().getName() + " Key:" + skuKey + " 开始自旋");
                 //设置失败 自旋，睡眠几秒后重新访问
                 try {
                     Thread.sleep(3000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 //注意必须用return 否则重新开辟了新的线程
                 return this.getBySkuId(skuId, ip);
             }
 
         }
         return pmsSkuInfo;
     }
```


​     

   - redis 事务和redissen框架实现秒杀（类似juc的操作）

   - ab压测(相关命令自行百度)

### elasticsearch搜索服务(环境搭建、相关语法使用)
相关概念：

|   名称   | 解释                                                         |
| :------: | :----------------------------------------------------------- |
| cluster  | 整个elasticsearch   默认就是集群状态，整个集群是一份完整、互备的数据。 |
|   node   | 集群中的一个节点，一般只一个进程就是一个node                 |
|  shard   | 分片，即使是一个节点中的数据也会通过hash算法，分成多个片存放，默认是5片。 |
|  index   | 相当于rdbms的database, 对于用户来说是一个逻辑数据库，虽然物理上会被分多个shard存放，也可能存放在多个node中。 |
|   type   | 类似于rdbms的table，但是与其说像table，其实更像面向对象中的class , 同一Json的格式的数据集合。 |
| document | 类似于rdbms的 row、面向对象里的object                        |
|  field   | 相当于字段、属性                                             |

相关命令：

   **https://blog.csdn.net/gwd1154978352/article/details/82781731**

### 认证中心单点登录（服务使用拦截器拦截，使用的思想，对比Springcloud微服务架构的网关）

   使用拦截器，从请求中获取token，再去认证中心认证token。

   认证中心只负责token的认证，token的产生由登录操作产生

   问题点：

   1. 有的请求需要认证

   2. 有的请求一定要token，有的可以不要token

      使用注解区分

      ```java
          /**
           * 拦截的具体操作.
           *
           * @param request
           * @param response
           * @param handler   处理
           * @return
           */
          @Override
          public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
      
              HandlerMethod handlerMethod = (HandlerMethod) handler;
              LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
              //不用登录验证也能使用 游客登陆
              if (null == methodAnnotation) {
                  return true;
              }
      
              String token = "";
      
              //主动登录，保存到cookie的内容
              String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
              if (StringUtils.isNotBlank(oldToken)) {
                  token = oldToken;
              }
      
              //被动登录产生的token
              String newToken = request.getParameter("token");
              if (StringUtils.isNotBlank(newToken)) {
                  token = newToken;
              }
      
              //验证token
              String success = "fail";
              Map<String, String> successMap = new HashMap<>();
              if (StringUtils.isNotBlank(token)) {
                  String successJson = HttpclientUtil.doGet("http://passport.gmall.hmlr123.com/verify?token=" + token + "&currentIp=" + getIp(request));
                  successMap = JSON.parseObject(successJson, Map.class);
                  success = successMap.get("status");
              }
      
              //判断是否需要登陆验证， 验证是否通过
              boolean loginSuccess = methodAnnotation.loginSuccess();
              if (loginSuccess && !"success".equals(success)) {
                  //需要登录成功 才能使用，验证用户信息
                  //验证不通过 驳回登录界面 带上当前请求Url
                  StringBuffer requestURL = request.getRequestURL();
                  //重定向
                  response.sendRedirect("http://passport.gmall.hmlr123.com/index?returnUrl=" + requestURL);
                  return false;
              }
      
              //验证通过 逆向思考过程， 代码更简洁， 从小到大，短板原理
              if ("success".equals(success)) {
                  // 需要将token携带的用户信息写入
                  request.setAttribute("memberId", successMap.get("memberId"));
                  request.setAttribute("nickname", successMap.get("nickname"));
                  // 验证通过，覆盖cookie中的token
                  if(StringUtils.isNotBlank(token)){
                      CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                  }
              }
      
              return true;
      
          }
      ```

      

### jwt 生成token

### outh2.0 社交登录
思路：
1. 用户请求社交登录，引导跳转社交登录界面
2. 用户同意社交登录，第三方给与许可码code（标识当前用户授权），请求我们在第三方登记的回调地址
3. 使用code和我们的私钥获取access_code许可获取资源

### activeMQ消息队列
队列、订阅模式，持久化，延迟队列，static与value、configuration、bean使用

作用：异步、并行、解耦、排队

缺点：不确定性和延迟（需要**引入延迟、周期性的主动轮询，来发现未到达的消息，从而进行补偿**）

延迟队列：当某个请求失败，重复发送我们的消息请求。

幂等性：无论发送多少次请求，最终结果应该一致

封装：

1. 初始化

    ```java
    @Configuration
    public class ActiveMqConfig {
    
        @Value("${spring.activemq.broker-url:disabled}")
        private String brokeURL;
    
        @Value("${activemq.listener.enable:disabled}")
        private String listenerEnable;
    
        /**
         * 解决@value从配置文件获取不到值的问题.
         *
         * @return
         */
    //    @Bean
    //    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    //        return new PropertySourcesPlaceholderConfigurer();
    //    }
    
        /**
         * mq初始化到Spring容器中.
         * Springboot注解不支持静态的变量和方法
         * @return
         */
        @Bean
        public ActiveMqUtil getActiveMqUtil() {
            if ("disabled".equals(brokeURL)) {
                return null;
            }
            ActiveMqUtil activeMqUtil = new ActiveMqUtil();
            activeMqUtil.init(brokeURL);
            return activeMqUtil;
        }
    
        /**
         * 注入Active连接工厂 项目启动的时候实例化Active连接工厂.
         *
         * @return
         */
        @Bean
        public ActiveMQConnectionFactory activeMQConnectionFactory() {
            return new ActiveMQConnectionFactory(brokeURL);
        }
    
        /**
         * 队列模式的监听连接工厂.
         * Spring的mq实现：用Spring的JMS监听工厂包装ActiveMQConnectionFactory 实现软件控制权交给我们Spring
         *
         * @param activeMQConnectionFactory
         * @return
         */
        @Bean(name = Constant.JMS_QUEUE_LISTENER)
        public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
            //Spring封装的连接工厂
            DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
            if (!"true".equals(listenerEnable)) {
                return null;
            }
    
            factory.setConnectionFactory(activeMQConnectionFactory);
            factory.setConcurrency("5");
            //重连间隔时间
            factory.setRecoveryInterval(5000L);
            //配置管理本地事务
            factory.setSessionTransacted(false);
            //确认模式
            factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
            return factory;
        }
    }
    ```

    2. 工具类 **工具类有问题**，当消费者需要继续发送消息服务的时候，所作的操作（CRUD）需要在发送消息**之前一步**执行

    ```java
    public class ActiveMqUtil {
    
        private static PooledConnectionFactory pooledConnectionFactory = null;
    
        /**
         * 初始化连接工厂.
         *
         * @param brokerUrl 负载Url
         * @return
         */
        public ConnectionFactory init(String brokerUrl) {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            //加入连接池
            pooledConnectionFactory=new PooledConnectionFactory(factory);
            //出现异常时重新连接
            pooledConnectionFactory.setReconnectOnException(true);
            pooledConnectionFactory.setMaxConnections(5);
            pooledConnectionFactory.setExpiryTimeout(10000);
            return pooledConnectionFactory;
        }
    
        /**
         * 获取连接工厂.
         *
         * @return
         */
        public static ConnectionFactory getConnectionFactory(){
            return pooledConnectionFactory;
        }
    
        /**
         * 创建连接.
         *
         * @return
         * @throws JMSException
         */
        public static Connection getConnection() throws JMSException {
            return getConnectionFactory().createConnection();
        }
    
    
        /**
         * 发送消息 队列模式
         *
         * @param theme         主题
         * @param message       消息
         * @param sessionStatus 会话模式
         */
        public static void sendMessage(String theme, Message message, int sessionStatus) {
            //创建连接 会话
            Connection connection = null;
            Session session = null;
    
            try {
                //获取连接
                connection = getConnectionFactory().createConnection();
                //开启连接
                connection.start();
                //开启事务
                session = connection.createSession(true, sessionStatus);
                //创建队列
                Queue sessionQueue = session.createQueue(theme);
                //创建生产者
                MessageProducer producer = session.createProducer(sessionQueue);
                //发送消息
                producer.send(message);
                //提交事务
                session.commit();
                //关闭生产者
                producer.close();
                //关闭session
                session.close();
            } catch (Exception e) {
                try {
                    //失败回滚
                    if (null != session) {
                        session.rollback();
                    }
                } catch (JMSException e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    //关闭连接
                    if (null != connection) {
                        connection.close();
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    ```

    

### 支付宝支付（无法实现，没有实现）
交易码防止重传，一次请求一个交易码。

### 秒杀redis

```shell
MULTI	组装事务
EXEC	执行事务
DISCARD	取消事务
WATCH	监视key（如果key在执行事务之前被更改了，就取消当前事务的执行）乐观锁
UNWATCH     取消当前事务中指定监控的keys，如果执行了EXEC或DISCARD命令，则无需再手工执行  该命令了，因为在此之后，事务中所有的keys都将自动取消
```


​    
```java
    @Autowired
    RedisUtil redisUtil;

    /**
     * 使用Redissien框架
     */
    @Autowired
    RedissonClient redissonClient;

	/**
     * 一起请求的一批人中只有一个可以获得.
     * 拼手气
     *
     * @return
     */
    @RequestMapping("kill")
    @ResponseBody
    public Integer kill() {

        Jedis jedis = redisUtil.getJedis();
        //监视
        jedis.watch("106");
        Integer stock = Integer.valueOf(jedis.get("106"));

        if (stock > 0) {
            //开启事务
            Transaction multi = jedis.multi();
            //减少数量
            multi.incrBy("106", -1);
            //执行事务
            List<Object> exec = multi.exec();
            if (null != exec && exec.size() > 0) {
                System.out.println("抢购成功," + "剩余库存：" + stock + "当前抢购人数：" + (1000 - stock));
            } else {
                System.out.println("抢购失败" + "剩余库存：" + stock + "当前抢购人数：" + (1000 - stock));
            }
        }
        jedis.close();
        return 1;
    }

    /**
     * 先到先得
     *
     * @return
     */
    @RequestMapping("secKill")
    @ResponseBody
    public Integer secKill() {
        //获取信号量
        RSemaphore semaphore = redissonClient.getSemaphore("106");
        //尝试减少操作
        boolean b = semaphore.tryAcquire();
        //获取redis连接
        Jedis jedis = redisUtil.getJedis();
        //查询剩余数量
        Integer stock = Integer.valueOf(jedis.get("106"));
        if (b) {
            System.out.println("当前库存剩余数量" + stock + ",某用户抢购成功，当前抢购人数：" + (1000 - stock));
            // 用消息队列发出订单消息
            System.out.println("发出订单的消息队列，由订单系统对当前抢购生成订单");
        } else {
            System.out.println("当前库存剩余数量" + stock + ",某用户抢购失败");
        }
        jedis.close();
        return 1;
    }
```


​    

### 限流

1. 按秒计算，在规定时间交接的间隔点可能会接受双倍的请求，导致服务器压力剧增

2. 漏桶算法，以流量为界限，解决时间间隔点问题

   伪代码

   ```java
   long timeStamp = getNowTime(); 
   int capacity = 10000;// 桶的容量，即最大承载值
   int rate = 1;//水漏出的速度，即服务器的处理请求的能力
   int water = 100;//当前水量，即当前的即时请求压力
   
   //当前请求线程进入漏桶方法，true则不被拒绝，false则说明当前服务器负载水量不足，则被拒绝
   public static bool control() {
   long  now = getNowTime();//当前请求时间
   //先执行漏水代码
   //rate是固定的代表服务器的处理能力，所以可以认为“时间间隔*rate”即为漏出的水量
       water = Math.max(0, water - (now - timeStamp) * rate);//请求时间-上次请求时间=时间间隔
       timeStamp = now;//更新时间，为下次请求计算间隔做准备
       if (water < capacity) { // 执行漏水代码后，发现漏桶未满，则可以继续加水，即没有到服务器可以承担的上线
           water ++; 
           return true; 
       } else { 
           return false;//水满，拒绝加水，到服务器可以承担的上线，拒绝请求
      } 
   }
   
   ```

3. 令牌算法

令牌桶算法的原理是系统会以一个恒定的速度往桶里放入令牌，而如果请求需要被处理，则需要先从桶里获取一个令牌，当桶里没有令牌可取时，则拒绝服务。

```java
long timeStamp=getNowTime(); 
int capacity; // 桶的容量 
int rate ;//令牌放入速度
int tokens;//当前使用的令牌数量  

bool control() {
   //先执行添加令牌的操作
   long  now = getNowTime();
   tokens = max(capacity, tokens+ (now - timeStamp)*rate); 
   timeStamp = now;  
   if(tokens<1){
     return false; //令牌已用完，拒绝访问
   }else{ 
     tokens--;
     retun true; //还有令牌，领取令牌
   }
 }

```






