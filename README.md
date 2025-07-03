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
The Maven build Run Configuration you create needs to have the goals set to: clean package<br/>
right click on Project AWS-lambda-assessment<br>
Hover on Run As, and choose your run configuration<br/>

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

From your windows Administrator shell, cd to your project folder(cd C:\work\java\eclipse-workspace2\AWS-lambda-assessment)<br/>
The build is done from project file template.yaml, and it must contain the full path to your project folder.<br/>
Your JAVA_HOME and path(windows Environment variables) must be set to JAVA 21<br/>
To Begin, type: sam build <br/>
To validate you should see new files in your build folder of directory .aws-sam<br/>
If you make project changes remove the build folder and again run sam build<br/>
To start the app, the command is: sam local start-api --docker-network VA-assessment<br/>
Before any Ctrl-C to exit the App, a person should run two shutdowns.<br/>
 "curl localhost:3000/v1/notification/shutdown" and "curl localhost:3000/v1/template/shutdown"<br/>
this will clean up the Hikari connection pool. Each Function has its own connection pool. <br/>
If you forget you could stop and start postgres in DockerDesktop.<br/>

#Start Testing

Open a new windows Administrator Shell<br/>
curl localhost:3000/v1/all/notifications<br/>
result After long wait:<br/>
{<br/>
&nbsp; "status" : "NO\_CONTENT",<br/>
&nbsp; "timestamp" : "07-02-2025 00:00:32",<br/>
&nbsp; "message" : "Notification Table is empty.",<br/>
&nbsp; "debugMessage" : null,<br/>
&nbsp; "subErrors" : null<br/>
}<br/>

curl localhost:3000/v1/all/templates<br/>
result is the same<br/>

All possible curl tests are in file template.yaml<br/>



















