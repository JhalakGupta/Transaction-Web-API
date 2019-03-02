STACK_NAME=$1
DomainName=$2
NUID=$3
LAMBDAS3BUCKET="lambda.csye6225-fall2018-$NUID.me"


export lambdaRoleArn=$(aws iam list-roles --query 'Roles[*].[RoleName, Arn]' --output text | grep Lambda |awk '{print $2}')
echo "lambdaRoleArn : ${lambdaRoleArn}"

aws cloudformation create-stack \
--stack-name $STACK_NAME \
--capabilities "CAPABILITY_NAMED_IAM" \
--template-body file://csye6225-cf-serverless.json \
--parameters ParameterKey=LambdaRoleArn,ParameterValue=$lambdaRoleArn \
ParameterKey=LambdaS3Bucket,ParameterValue=$LAMBDAS3BUCKET \
ParameterKey=domainName,ParameterValue=$DomainName

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
  STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
