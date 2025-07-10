# AWS-lambda-assessment

Reference Materials for Project<br/>
My Existing Code from other Projects and Assessments<br/>
Google, lol<br/>

#Postgres Database(do this first)
The postgres Database will be installed in Docker Desktop, so it must be installed and running<br/>
follow the instructions in the project file createPostgresInDocker<br/>

Next Build the Schema and Database Tables, follow the instructions in project file dockerPsqlCreateTables<br/>
This is a pain because I could not cut and paste into the psql shell, typed it all by hand.<br/>
Maybe you can fiqure it out. lol <br/>

#Maven Compile in Eclipse(Do this second)
Had to use Eclipse IDE 2025-06 for java 21<br/>
The Project is loaded in Eclipse using (File Import from Git)<br/>
Url is https://github.com/jfraser2/AWS-lambda-assessment.git<br/>
The Maven build Run Configuration you create in eclipse needs to have the goals set to: clean package<br/>
right click on Project AWS-lambda-assessment then Hover on Run As, and choose your run configuration<br/>

#Testing Installs (Do this third)
You have to Install maven, you cannot only use the one in eclipse, because<br/>
the local AWS Lambda runtime project builder needs it. Yes we are faking the <br/>
AWS Lambda runtime locally, you do not have to spend money.<br/>

After download(https://maven.apache.org/download.cgi) you have to set<br/>
the windows MAVEN\_HOME, and add it the windows path.<br/>

Now the local AWS Lambda runtime. It is AWS\_SAM\_CLI<br/>
Download AWS\_SAM\_CLI\_64\_PY3.msi(I chose version 1.142.1) url is:<br/>
https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html<br/>
This is much nicer, path is set during install<br/>
now you can open your windows Administrator shell, and cd to the project install folder<br/>

#Building the Lambda Functions(do this fourth)

You do not need a DockerHub Account. I would not discourage it. The good news is they have lots of free docker images.<br/>
The signup url is: https://app.docker.com/ After you sign up, two helpful commands are docker login and docker logout<br/>
The two Dockerfiles in the project are: notificationDockerfile and templateDockerfile.<br/>
The images they create in DockerDestop are: notification and template. You may delete the dangling Images in DockerDesktop.<br/>
A single dangling image is created after every run. A dangling Image, always says none, none.<br/>

From your windows Administrator shell, cd to your project folder(cd C:\work\java\eclipse-workspace2\AWS-lambda-assessment)<br/>
The build is done from project file template.yaml, and it must contain the full path to your project folder.<br/>
Your JAVA_HOME and path(windows Environment variables) must be set to JAVA 21<br/>
To Begin, type: sam build <br/>
To validate you should see new files in your project folder called .aws-sam<br/>
If you make project changes remove the project folder .aws-sam Then delete the DockerDesktop Images and/or any Containers.<br/>
Again run sam build --no-cached. To start the curl request listener, the command is: <br/>
sam local start-api --docker-network VA-assessment --warm-containers EAGER -p 9000 -d 8080 which will start a container for the image<br/>
This container will be used over and over again on each request. This is need because a connection pool runs in the container</br>
Before any Ctrl-C to exit the listener, a person should run a shutdown. "curl localhost:9000/v1/shutdown"<br/>
The idea of running a shutdown is to clean up the Hikari connection pool. The Handler Function creates connection pool. <br/>
If you forget you could stop and start postgres in DockerDesktop.<br/>

#Start Testing

The testing is done locally and uses the aws-lambda-runtime-interface-emulator<br/>
It's default port is 8080, yes it is a Tomcat conflict, The windows command for<br/>
who got it first or who did not release it is: netstat -ao |find /i "listening"<br/>
Also try netstat -ano | findstr :8080 if that comes back empty the port is free<br/>

Open a new windows Administrator Shell and run the command<br/>
curl localhost:9000/v1/all/notifications<br/>
You should see the following result After a wait:<br/>
{<br/>
&nbsp; \"status\" : \"NO\_CONTENT\",<br/>
&nbsp; \"timestamp\" : \"07-02-2025 00:00:32\",<br/>
&nbsp; \"message\" : \"Notification Table is empty.\",<br/>
}<br/>

curl localhost:9000/v1/all/templates<br/>
result is the same<br/>

All possible curl tests are in file template.yaml<br/>



















