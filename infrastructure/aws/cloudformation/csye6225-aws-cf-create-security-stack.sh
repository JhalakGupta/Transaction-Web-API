#!/bin/bash
StackName=$1
stackstatus=""
createStackStatus=""
createFlag=true
DomainName=$2
AccessKeyId=$3
SecretAccessKey=$4
MySqlClientPass=$5


if [ -z "$StackName" ]; then
  echo "No stack name provided. Script exiting.."
  exit 1
fi


echo "Starting $StackName network setup"

echo "Starting to create the stack......"

createStackStatus=`aws cloudformation create-stack --stack-name $StackName \
	--template-body file://csye6225-cf-owaps.yml`
  
   

if [ -z "$createStackStatus" ]; then
  echo "Failed to create stack"
  exit 1
fi

until [ "$stackstatus" = "CREATE_COMPLETE" ]; do
  echo "Adding resources to the stack......"

  #ADD function to check resources
  myresources(){
    resourceStatus=`aws cloudformation describe-stack-events --stack-name $StackName --query 'StackEvents[?(ResourceType=='$@' && ResourceStatus==\`CREATE_FAILED\`)][ResourceStatus]' --output text`
    if [ "$resourceStatus" = "CREATE_FAILED" ]; then
      createFlag=false
      echo "$@ creation failed! "
      aws cloudformation describe-stack-events --stack-name $StackName --query 'StackEvents[?(ResourceType=='$@' && ResourceStatus==`CREATE_FAILED`)]'
      echo "deleting stack..... "
      bash ./csye6225-aws-cf-terminate-security-stack.sh $StackName $DomainName
      break
    fi
  }

myresources '`AWS::WAFRegional::SqlInjectionMatchSet`'
myresources '`AWS::WAF::SqlInjectionMatchSet`'
myresources '`AWS::WAFRegional::Rule`'
myresources '`AWAWS::WAF::Rule`'
myresources '`AWS::WAFRegional::ByteMatchSet`'
myresources '`AWS::WAF::ByteMatchSet`'
myresources '`AWS::WAFRegional::XssMatchSet`'
myresources '`AWS::WAF::XssMatchSet`'
myresources '`AWS::WAFRegional::IPSet`'
myresources '`AWS::WAF::IPSet`'
myresources '`AWS::WAFRegional::SizeConstraintSet`'
myresources '`AWS::WAF::SizeConstraintSet`'
myresources '`AWS::WAFRegional::WebACL`'
myresources '`AWS::WAF::WebACL`'
myresources '`AWS::CloudFront::Distribution`'

  stackstatus=`aws cloudformation describe-stacks --stack-name $StackName --query 'Stacks[*][StackStatus]' --output text`
  sleep 20
done

if [ "$createFlag" = true ]; then
  echo "Stack resources created successfully"
  aws cloudformation list-stack-resources --stack-name $StackName
fi
exit 0
