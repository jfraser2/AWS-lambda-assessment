# configure the build environment
FROM public.ecr.aws/sam/build-java21:latest  AS build


ADD ./aws-lambda-rie-x86_64 /var/rapid/aws-lambda-rie

# Set the working directory inside the container
WORKDIR /app

COPY pom.xml .
COPY template.yaml .
COPY entrypoint.sh .
# Copy your application source code to the container
COPY ./src ./src

RUN echo "JAVA_HOME is set to: ${JAVA_HOME}"
RUN echo "build PATH is set to: ${PATH}"

# Build your Java application using Maven (or Gradle, depending on your project)
# This command will compile your code and put the results in /target/classes and /target/dependency
RUN mkdir -p ./target/dependency
#RUN mvn compile dependency:copy-dependencies -DincludeScope=runtime
RUN mvn compile dependency:copy-dependencies


RUN file="$(ls -1a ./target/classes)" && echo $file
RUN file2="$(ls -1a ./target/dependency)" && echo $file2


FROM public.ecr.aws/lambda/java:21 AS deploy

COPY --from=build /var/rapid/aws-lambda-rie /var/rapid/aws-lambda-rie

WORKDIR /deployment

RUN chmod -R 0777 .

COPY --from=build /app/src/main/resources/log4j2.xml ./resources/log4j2.xml
COPY --from=build /app/template.yaml .
COPY --from=build /app/entrypoint.sh .
#COPY --from=build /app/target/classes ${LAMBDA_TASK_ROOT}
#COPY --from=build /app/target/dependency/* ${LAMBDA_TASK_ROOT}/lib/
COPY --from=build /app/target/classes ./classes/
COPY --from=build /app/target/dependency/* ./lib/

#RUN file3="$(ls -1a " && echo $file3


RUN echo "JAVA_HOME is set to: ${JAVA_HOME}"
ENV PATH=${PATH}:/deployment/classes/dao:/deployment/classes/dto/request:/deployment/classes/dto/response:/deployment/classes/entities:\
/deployment/classes/errorHandling/helpers:/deployment/classes/errorHandling/processing:/deployment/classes/helpers:\
/deployment/classes/hibernate/utils:/deployment/classes/request/handlers:/deployment/classes/servies:/deployment/classes/services/interfaces:\
/deployment/classes/services/validation/request:/deployment/classes/services/validation/request/interfaces/functional:\
/deployment/classes/validation/exceptions

RUN export PATH
RUN echo "runtime PATH is set to: ${PATH}"

ENV CLASSPATH=/development/resources
RUN export CLASSPATH
RUN echo "CLASSPATH is set to: ${CLASSPATH}"


ENV LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/deployment/lib
RUN export LD_LIBRARY_PATH
RUN echo "LD_LIBRARY_PATH is set to: ${LD_LIBRARY_PATH}"

# entry point of the container
#ENTRYPOINT ["./entrypoint.sh"]
ENTRYPOINT ["/var/rapid/aws-lambda-rie"]

# Pass the name of the function handler as an argument to the runtime com.amazonaws.services.lambda.runtime.api.client.AWSLambda
CMD ["request.handlers.NotificationAndTemplateHandler::handleRequest" ]
