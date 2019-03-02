STACK_NAME=$1
NUID=$2
CODEDEPLOYS3BUCKETNAME="code-deploy.$NUID.me.csye6225.com"
LAMBDAS3BUCKETNAME="lambda.csye6225-fall2018-$NUID.me"

aws s3 rm s3://$CODEDEPLOYS3BUCKETNAME --recursive
echo "Cleaning s3 bucket $CODEDEPLOYS3BUCKETNAME"

aws s3 rm s3://$LAMBDAS3BUCKETNAME --recursive
echo "Cleaning s3 bucket $LAMBDAS3BUCKETNAME"

aws cloudformation delete-stack --stack-name $STACK_NAME

aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME

if [ $? -ne "0" ]
then 
    echo "Deletion of Stack failed"
else
    echo "Deletion of Stack Success"
fi