STACK_NAME=$1
NETWORKING_STACK_NAME=$2
VPC_NAME="${NETWORKING_STACK_NAME}-csye6225-vpc"
EC2_NAME="${STACK_NAME}-csye6225-ec2"

export vpcId=$(aws ec2 describe-vpcs --filters "Name=tag:Name,Values=$VPC_NAME" --query "Vpcs[].VpcId[]" --output text)

export subnetId1=$(aws ec2 describe-subnets --filters Name=availabilityZone,Values=us-east-1a Name=vpc-id,Values=$vpcId --output text| grep subnet- | sed -E 's/^.*(subnet-[a-z0-9]+).*$/\1/' | head -1)

aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-application.json --parameters ParameterKey=MyVPC,ParameterValue=$vpcId ParameterKey=MyEC2Subnet,ParameterValue=$subnetId1 ParameterKey=EC2Name,ParameterValue=$EC2_NAME

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)

while [ $STACK_STATUS != "CREATE_COMPLETE" ]
do
	STACK_STATUS=`aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text`
done
echo "Created Stack ${STACK_NAME} successfully!"
