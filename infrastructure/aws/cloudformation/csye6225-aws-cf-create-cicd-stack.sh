STACK_NAME=$1
NUID=$2
CODEDEPLOYEC2S3POLICYNAME="CodeDeploy-EC2-S3"
CODEDEPLOYSERVICEROLENAME="CodeDeploySerivceRole"
CODEDEPLOYS3BUCKETNAME="code-deploy.$NUID.me.csye6225.com"
LAMBDAS3BUCKETNAME="lambda.csye6225-fall2018-$NUID.me"
TRAVISUSER="travis"
TRAVISUPLOADTOS3POLICYNAME="Travis-Upload-To-S3"
TRAVISCODEDEPLOYPOLICYNAME="Travis-Code-Deploy"
CODEDEPLOYEC2SERVICEROLENAME="CodeDeployEC2ServiceRole"
EC2NAME="ec2"
BUCKET_NAME="$NUID.me.csye6225.com"
SNSPOLICY="SNS-Policy"

aws cloudformation create-stack --stack-name $STACK_NAME --capabilities "CAPABILITY_NAMED_IAM" \
--template-body file://csye6225-cf-cicd.json \
--parameters ParameterKey=CodeDeployEC2ServiceRoleName,ParameterValue=$CODEDEPLOYEC2SERVICEROLENAME \
ParameterKey=TravisUploadtoS3PolicyName,ParameterValue=$TRAVISUPLOADTOS3POLICYNAME \
ParameterKey=TravisUser,ParameterValue=$TRAVISUSER \
ParameterKey=AppBucketName,ParameterValue=$BUCKET_NAME \
ParameterKey=CodeDeployS3BucketName,ParameterValue=$CODEDEPLOYS3BUCKETNAME \
ParameterKey=LambdaS3BucketName,ParameterValue=$LAMBDAS3BUCKETNAME \
ParameterKey=CodeDeployServiceRoleName,ParameterValue=$CODEDEPLOYSERVICEROLENAME \
ParameterKey=CodeDeployEC2S3PolicyName,ParameterValue=$CODEDEPLOYEC2S3POLICYNAME \
ParameterKey=TravisCodeDeployPolicyName,ParameterValue=$TRAVISCODEDEPLOYPOLICYNAME \
ParameterKey=EC2Name,ParameterValue=$EC2NAME \
ParameterKey=SNSPolicy,ParameterValue=$SNSPOLICY

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
  STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
