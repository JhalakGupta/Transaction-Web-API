#!/bin/bash
StackName=$1
DomainName=$2
if [ -z "$StackName" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

echo "Terminating $StackName network setup"
aws cloudformation delete-stack --stack-name $StackName

stackid=`aws cloudformation describe-stacks --stack-name $StackName --query 'Stacks[*][StackId]' --output text`
stackstatus=""
if [ -z "$stackid" ]; then
  exit 1
fi
until [ "$stackstatus" = 'DELETE_COMPLETE' ]; do
  stackstatus=`aws cloudformation list-stacks --query 'StackSummaries[?StackId==\`'$stackid'\`][StackStatus]' --output text`

done
if [ "$stackstatus" = 'DELETE_COMPLETE' ]; then
  echo "$StackName terminated sucessfully!!"
fi
