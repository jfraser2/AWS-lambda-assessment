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

Now the local AWS Lambda runtime. It is AWS_SAM_CLI<br/>
Download AWS_SAM_CLI_64_PY3.msi(I chose version 1.142.1) url is:<br/>
https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html<br/>
This is much nicer, path is set during install<br/>
now you can open your windows Administrator shell, and cd to the project install folder<br/>

an AWS CLI install is also required to create a profile, that you need for AWS_SAM_CLI<br/>
Of course you will need an AWS account to do that,I am using a free one, sign up url is:<br/>
https://signin.aws.amazon.com/signup?request_type=register<br/>
You will have to create an access key in that account, under IAM security credentials, but remember<br/>
the secret key only appears during access key creation, so copy it to your local machine, you will need it<br/>
If you forget to copy the secret key you have deactivate the old access key and create another one<br/>

the url to download AWS_CLI is: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html<br/>
Then go down and click on your desired OS and you will see the real download link, click it<br/>
mine is a windows .msi file which is easy to install.<br/>
Instructions to create the profile are at url:https://www.google.com/search?q=how+to+create+a+profile+with+aws++cli&sca_esv=cc1c4a1f7d117633&rlz=1C1JSBI_enUS1092US1092&sxsrf=AE3TifO3fXRtpzERjYfAKZyM7Kf_6DIEpA%3A1752241710828&ei=LhZxaJymMoigqtsPttWIkAM&ved=0ahUKEwic9e-F-bSOAxUIkGoFHbYqAjIQ4dUDCBA&uact=5&oq=how+to+create+a+profile+with+aws++cli&gs_lp=Egxnd3Mtd2l6LXNlcnAiJWhvdyB0byBjcmVhdGUgYSBwcm9maWxlIHdpdGggYXdzICBjbGkyCBAhGKABGMMEMgUQIRirAkibOlCrDljeLnABeACQAQCYAW6gAdoFqgEDNi4yuAEDyAEA-AEBmAIIoAK2BcICChAAGLADGNYEGEfCAggQABiiBBiJBcICBRAAGO8FwgIIEAAYgAQYogTCAgoQIRigARjDBBgKmAMAiAYBkAYIkgcDNS4zoAfRI7IHAzQuM7gHsAXCBwUxLjMuNMgHGQ&sclient=gws-wiz-serp<br/>

#Building the Lambda Functions(do this fourth)

You do not need a DockerHub Account. I would not discourage it. The good news is they have lots of free docker images.<br/>
The signup url is: https://app.docker.com/ After you sign up, two helpful commands are docker login and docker logout<br/>
The Dockerfile in the project is: Dockerfile and the Image it creates in DockerDesktop is notificationandtemplate<br/>
You may delete the dangling Images in DockerDesktop, created by the build.<br/>
A single dangling image is created after every run. A dangling Image, always says none, none.<br/>
You may also delete the notificationandtemplate image and/or container if you change the code<br/>

The first is to figure out is the container-host, this is the first of two settings that change in the sam local start-api<br/>
wsl -d docker-desktop hostname -i<br/>
In a windows shell type"docker context ls" look for keywords like docker or npipe, in the response listing for<br/>
Current DOCKER_HOST based configuration, those are the values for container-host. The second one that changes is --profile<br/>
--profile is discussed in the Testing Installs section. If you do change other values you will get wired errors.<br/>
OMG this sam local start-api command was very hard to figure out and I had to read lots of articles

From your windows Administrator shell, cd to your project folder(cd C:\work\java\eclipse-workspace2\AWS-lambda-assessment)<br/>
The build is done from project file template.yaml, and it must contain the full path to your project folder.<br/>
Your JAVA_HOME and path(windows Environment variables) must be set to JAVA 21<br/>
To Begin, type: sam build --docker-network VA-assessment --use-container --profile my-local-dev<br/>
To validate you should see new files in your project folder called .aws-sam<br/>
If you make project changes remove the project folder .aws-sam Then delete the DockerDesktop Images and/or any Containers.<br/>
Again run sam build --no-cached  --docker-network VA-assessment --use-container --profile my-local-dev .<br/>
To start the local Http Server to handle curl requests, and a container for your build image, the command is: <br/>
sam local start-api --docker-network VA-assessment --warm-containers EAGER -p 9000 -d 8080 --profile my-local-dev --add-host host.docker.internal:host-gateway --host localhost --debug<br/> 

In the above command, 0.0.0.0 means bind to any interface. The command above will start a container for the image in Docker Desktop<br/>
In the new container after a request is made, you will see a port mapping of 8080:8080. This map is for connecting an exteral debugger.<br/>
This container will be used over and over again on each request. This is needed because a connection pool runs in the container.</br>
Before any Ctrl-C to exit the listener, a person should run a shutdown. "curl localhost:9000/v1/shutdown"<br/>
The idea of running a shutdown, is to clean up the Hikari connection pool. The Handler Function creates a connection pool. <br/>
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
&nbsp; \"message\" : \"Notification Table is empty.\"S<br/>
}<br/>

curl localhost:9000/v1/all/templates<br/>
result is the same<br/>

All possible curl tests are in file template.yaml<br/>

Second way for local testing, remove the DOCKER_HOST windows environment variable(set DOCKER_HOST=) and then run the commands below<br/>
sam local invoke NotificationAndTemplate --docker-network VA-assessment -d 8080 --container-host localhost:2375 --profile my-local-dev --add-host host.docker.internal:host-gateway --debug --event src/test/resources/AllNotificationsEvent.json<br/>



















