# Portable EHR Java Feed Demo

> The intention of this project is to demonstrate how to interface with Portable EHR FeedHub using the Portable EHR Java Feed SDK.
> 
> Being a demonstration this code is not intended to be use in production, and the following aspects had been ignored:
> 
>  - Multithreading
>  - Circuit brakers
>  - Configuration flexibility
>  - Proper exception management 
>  - Unit testing
>  - UX (I know... I know...)
>  - etc.

### Running the application

Compile it `mvn package` and run it `java -jar target/portableehr-java-feed-X.jar [--server.port={SOME_PORT}] [--feedhub.basePath={FEED_HUB_URL}] [--mocks.basePath={SOME_PATH}]`

Or run it directly `mvn spring-boot:run [-Dspring-boot.run.arguments=[--server.port={SOME_PORT}][,--feedhub.basePath={FEED_HUB_URL}][,--mocks.basePath={SOME_PATH}]]`

Or run it without install Maven `./mvnw spring-boot:run [-Dspring-boot.run.arguments=[--server.port={SOME_PORT}][,--feedhub.basePath={FEED_HUB_URL}][,--mocks.basePath={SOME_PATH}]]` 

Command line arguments (all of them are optional):
<pre style="background-color: transparent">
- server.port        The port where the application will run. <b>Default: 4004</b> 
- feedhub.basePath   The FeedHub url. <b>Default: https://feed.portableehr.io:3004</b> 
- mocks.basePath     The path where the *mocks* folder is. <b>Default: ./</b>
</pre>

Start using it : 
 - Go to [Console web (change port accordingly)](https://localhost:4004) to :
    - Change which answer to use for each server endpoint
    - See a live history of request received by the Feed server
    - Sent requests to FeedHub
    - See the responses of FeedHub
 - By deleting, adding, and editing the json files under {checkout dir}/mocks you can customize the requests and responses sent (refresh the WebConsole to see the adds and deletes)
 - Import `Feed.postman_collection.json` and `Feed_local.postman_environment.json` in [Postman](https://www.postman.com/) and start hitting your brand-new Feed

Notice :
 - Some of the response's id will be changed (to create new appointments for example)
 - The lastUpdated attribute of the objects will be updated to
 - The appointments startTime and endTime will be set to 48 and 49 hours from the time when the request is received

### You want to integrate your project now?
This projects uses the [Portable EHR Java Feed SDK](https://github.com/Portable-EHR/PortableEHR-Feed-SDK). Add the maven dependency in your project to have a jump start
```xml
<dependency>
    <groupId>org.portableehr</groupId>
    <artifactId>portableehr-feed-sdk</artifactId>
    <version>1.0.1</version>
</dependency>
```

### References
- [The swagger to Feed API this project (and any Feed) IMPLEMENTS](https://feed.portableehr.io:4004/docs)
- [The swagger to FeedHub API this project (and any Feed) CONSUMES](https://feed.portableehr.io:3004/docs)
