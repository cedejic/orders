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

### QA
<ol>
<li> 
You do not need to add authentication to your web service, but propose a protocol / method and
justify your choice.*<br><br>
Best option would be to use OAuth2 authorization protocol.<br> 
Authentication server could be implemented internally or 3rd party identity provider could be used (like Okta, Amazon Cognito or similar).<br>
Implementation of in-house authentication server could be complicated and could provide maintaining challenges.<br>
3rd party identity providers represent direct cost, but they handle all of the security concerns.<br>
Implementing OAuth2 with 3rd party identity provider would be most effective and secure approach.<br>
Spring security should be activated through code and all methods' access should be checked for access permissions.<br>
If application would grow to become more complex, possible implementation of ACL for fine-grained access control.
<br><br>
</li>
<li>
How can you make the service redundant? What considerations should you do?*<br><br>
If I understand correctly this question is about how can service layer become redundant.<br>
In that case I would remove services and implement Spring Data REST.<br>  
I would need to add dependencies `spring-boot-starter-data-rest`<br>
I would need to use @RepositoryRestResource annotation for repositories<br>
Here my knowledge on this topic ends, as I myself never implemented Spring Data REST<br> 
</li>
</ol>

