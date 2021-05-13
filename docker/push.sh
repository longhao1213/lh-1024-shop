#登录阿里云镜像仓
docker login --username=18581588710 registry.cn-hangzhou.aliyuncs.com --password=LongHao9620

#构建整个项目，或者单独构建common项目,避免依赖未被构建上去
cd ../common
mvn install


#构建网关
cd ../gateway
mvn install -Dmaven.test.skip=true dockerfile:build
sudo docker tag longsan-cloud/gateway:latest registry.cn-hangzhou.aliyuncs.com/longan-cloud/gateway-service:1.4
sudo docker push registry.cn-hangzhou.aliyuncs.com/longan-cloud/gateway-service:1.4
echo "网关构建推送成功"


#用户服务
cd ../user-service
mvn install -Dmaven.test.skip=true dockerfile:build
sudo docker tag longsan-cloud/user-service:latest registry.cn-hangzhou.aliyuncs.com/longan-cloud/user-service:1.4
sudo docker push registry.cn-hangzhou.aliyuncs.com/longan-cloud/user-service:1.4
echo "用户服务构建推送成功"


#商品服务
cd ../product-service
mvn install -Dmaven.test.skip=true dockerfile:build
sudo docker tag longsan-cloud/product-service:latest registry.cn-hangzhou.aliyuncs.com/longan-cloud/product-service:1.4
sudo docker push registry.cn-hangzhou.aliyuncs.com/longan-cloud/product-service:1.4
echo "商品服务构建推送成功"



#订单服务
cd ../order-service
mvn install -Dmaven.test.skip=true dockerfile:build
sudo docker tag longsan-cloud/order-service:latest registry.cn-hangzhou.aliyuncs.com/longan-cloud/order-service:1.4
sudo docker push registry.cn-hangzhou.aliyuncs.com/longan-cloud/order-service:1.4
echo "订单服务构建推送成功"


#优惠券服务
cd ../coupon-service
mvn install -Dmaven.test.skip=true dockerfile:build
sudo docker tag longsan-cloud/coupon-service:latest registry.cn-hangzhou.aliyuncs.com/longan-cloud/coupon-service:1.4
sudo docker push registry.cn-hangzhou.aliyuncs.com/longan-cloud/coupon-service:1.4
echo "优惠券服务构建推送成功"


echo "=======构建脚本执行完毕====="