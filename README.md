### INSTRUCTIONS TO RUN THE APPLICATION<br>
All commands are run from the project root directory.  
Steps to deploy application to Docker:

Step 1. build and package executable JAR:  
*`mvn clean package spring-boot:repackage`*  

Step 2. build Docker container  
*`docker build -t assignment/purchase-orders .`*  

Step 3. Run Docker container first time  
*`docker run -d -p 8080:8080 --name purchase_orders assignment/purchase-orders`*  
or on consecutive runs after stopping first run  
*`docker start purchase-orders`*  

Step 4. After testing stop Docker container  
*`docker stop purchase_orders`*

### SWAGGER 
On web service startup Swagger UI default URL is  
http://localhost:8080/swagger-ui/

### ACTUATOR
On web service startup basic actuator is available on  
http://localhost:8080/actuator

### POSTMAN REQUESTS
Postman requests collection is available in folder postman 

