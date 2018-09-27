STACK_NAME=$1
VPC_NAME=${STACK_NAME}-csye6225-vpc
SUBNET1_NAME=${STACK_NAME}-csye6225-subnet1
SUBNET2_NAME=${STACK_NAME}-csye6225-subnet2
SUBNET3_NAME=${STACK_NAME}-csye6225-subnet3
IG_NAME=${STACK_NAME}-csye6225-InternetGateway
PUBLIC_ROUTE_TABLE=${STACK_NAME}-csye6225-public-route-table

aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-networking.json --parameters ParameterKey=VPCName,ParameterValue=$VPC_NAME ParameterKey=SubnetName1,ParameterValue=$SUBNET1_NAME ParameterKey=SubnetName2,ParameterValue=$SUBNET2_NAME ParameterKey=SubnetName3,ParameterValue=$SUBNET3_NAME ParameterKey=IGName,ParameterValue=$IG_NAME ParameterKey=PubicRouteTableName,ParameterValue=$PUBLIC_ROUTE_TABLE

echo $SUBNET3_NAME
aws cloudformation wait stack-create-complete --stack-name $STACK_NAME

if [ $? -ne "0" ]
then 
	echo "Creation of Stack failed"
else
	echo "Creation of Stack Success"
fi
