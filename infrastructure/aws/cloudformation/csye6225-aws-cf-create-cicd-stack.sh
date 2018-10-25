STACK_NAME=$1

CODEDEPLOYEC2S3POLICYNAME="CodeDeploy-EC2-S3"
CODEDEPLOYSERVICEROLENAME="CodeDeploySerivceRole"
CODEDEPLOYS3BUCKETNAME="code-deploy.murthyva.me.csye6225.com"
TRAVISUSER="travis"
CODEDEPLOYAPPNAME="CodeDeployApplication"
TRAVISUPLOADTOS3POLICYNAME="Travis-Upload-To-S3"
TRAVISCODEDEPLOYPOLICYNAME="Travis-Code-Deploy"
CODEDEPLOYEC2SERVICEROLENAME="CodeDeployEC2ServiceRole"
AWSREGION="us-east-1"
AWSACCOUNTID="769264408510"

aws cloudformation create-stack --stack-name $STACK_NAME --capabilities "CAPABILITY_NAMED_IAM" --template-body file://csye6225-cf-cicd.json --parameters ParameterKey=CodeDeployEC2ServiceRoleName,ParameterValue=$CODEDEPLOYEC2SERVICEROLENAME ParameterKey=TravisUploadtoS3PolicyName,ParameterValue=$TRAVISUPLOADTOS3POLICYNAME ParameterKey=TravisUser,ParameterValue=$TRAVISUSER ParameterKey=CodeDeployS3BucketName,ParameterValue=$CODEDEPLOYS3BUCKETNAME ParameterKey=CodeDeployApplicationName,ParameterValue=$CODEDEPLOYAPPNAME ParameterKey=AWSRegion,ParameterValue=$AWSREGION ParameterKey=AWSAccountID,ParameterValue=$AWSACCOUNTID ParameterKey=CodeDeployServiceRoleName,ParameterValue=$CODEDEPLOYSERVICEROLENAME ParameterKey=CodeDeployEC2S3PolicyName,ParameterValue=$CODEDEPLOYEC2S3POLICYNAME ParameterKey=TravisCodeDeployPolicyName,ParameterValue=$TRAVISCODEDEPLOYPOLICYNAME

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
  STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
