#!/bin/bash

# 1. รัน API Gateway ก่อน
echo "Starting API Gateway..."
cd apigateway-service && ./mvnw spring-boot:run & 

# 2. รอสักพักให้ Gateway พร้อม (ถ้าจำเป็น)
sleep 10 

# 3. วนลูปตามหาทุก Service ในโฟลเดอร์ services แล้วรัน
SERVICES_DIR="./services"
for service in admin-service auth-service file-service notificaiton order-service payment-service product-service user-service; do
    echo "Starting $service..."
    ( cd "$SERVICES_DIR/$service" && ./mvnw spring-boot:run ) &
done

wait