#!/bin/sh

# This script can perform pre-execution tasks, e.g., environment setup,
# logging, or conditional logic before invoking the Java handler.

# For example, you might want to log some environment variables:
echo "Starting Lambda functions..."
echo "Handler: $@"

# Execute the Lambda Runtime Interface Client (RIC) with your Java handler
# The AWS Lambda Java base images already include the RIC.

#if /usr/bin/ps -aux | /usr/bin/grep -q "[a]ws-lambda-rie"; then
#  echo "aws-lambda-rie is running."
#else
#  echo "aws-lambda-rie is not running. It will start now."
#  exec /var/rapid/aws-lambda-rie "$@" --runtime-interface-emulator-address=0.0.0.0:8999
#fi


exec /var/rapid/aws-lambda-rie "$@"
