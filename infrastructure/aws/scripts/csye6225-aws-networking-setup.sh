#!/bin/bash
# create-aws-vpc
#csye6225-aws-networking-setup.sh
#variables used in script (these are default variables but can be changed during runtime):
availabilityZone1="us-east-1a"
availabilityZone2="us-east-1b"
availabilityZone3="us-east-1c"

vpcCidrBlock="10.0.0.0/16"
subNet1CidrBlock="10.0.0.0/24"
subNet2CidrBlock="10.0.1.0/24"
subNet3CidrBlock="10.0.2.0/24"
subNet4CidrBlock="10.0.3.0/24"
subNet5CidrBlock="10.0.4.0/24"
subNet6CidrBlock="10.0.5.0/24"
destinationCidrBlock="0.0.0.0/0"

#get stack name
STACK_NAME=$1
echo "Performing setup for stack $STACK_NAME"

if [ -z "$STACK_NAME" ]
  then
    echo "No argument supplied"
    echo "The script will now exit"
    exit 0
fi

vpcName="$STACK_NAME-csye6225-vpc"
subnetName="$STACK_NAME-subnet"
gatewayName="$STACK_NAME-csye6225-InternetGateway"
routeTableName="$STACK_NAME-csye6225-public-route-table"

#create vpc with cidr block /16
echo "Creating VPC...for stack: $STACK_NAME"
echo "VPC CIDR block [default: $vpcCidrBlock]"

aws_response=$(aws ec2 create-vpc \
 --cidr-block "$vpcCidrBlock" \
 --output json)
vpcId=$(echo -e "$aws_response" |  /usr/bin/jq '.Vpc.VpcId' | tr -d '"')

if [ -z "$vpcId" ]
    then
        echo "Error creating VPC"
        exit 0
fi

#name the vpc
aws ec2 create-tags \
  --resources "$vpcId" \
  --tags Key=Name,Value="$vpcName"

#----------------------------------------------------------------------------------------------------
#get the avaiablity zone 1
echo "availability zone 1 in us-east-1 [default: $availabilityZone1]"

#create subnet-1 for vpc with /24 cidr block
echo "Creating subnet for $availabilityZone1"
echo "Using Cidr Block for subnet [default: $subNet1CidrBlock] : "

subnet_response=$(aws ec2 create-subnet \
 --cidr-block "$subNet1CidrBlock" \
 --availability-zone "$availabilityZone1" \
 --vpc-id "$vpcId" \
 --output json)

subnetId1=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')

if [ -z "$subnetId1" ]
    then
        echo "Error creating subnet for $availablityZone1"
        echo "Now exiting...."
        exit 0
fi

#name the subnet-1
aws ec2 create-tags \
  --resources "$subnetId1" \
  --tags Key=Name,Value="$subnetName-1"

# ---------------------------------------------------------------------------------------------------

#create subnet-2 for vpc with /24 cidr block
echo "Creating 2nd subnet for $availabilityZone1"
echo " Using Cidr Block for subnet [default: $subNet2CidrBlock]"

subnet_response=$(aws ec2 create-subnet \
 --cidr-block "$subNet2CidrBlock" \
 --availability-zone "$availabilityZone1" \
 --vpc-id "$vpcId" \
 --output json)

subnetId2=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')

if [ -z "$subnetId2" ]
    then
        echo "Error creating 2nd subnet for $availablityZone1"
        echo "Now exiting...."
        exit 0
fi

#name the subnet-2
aws ec2 create-tags \
  --resources "$subnetId2" \
  --tags Key=Name,Value="$subnetName-2"

#---------------------------------------------------------------------------------------------------------

#get the avaiablity zone 2
echo "availability zone 2 in us-east-1 [default: $availabilityZone2]"

#create subnet-3 for vpc with /24 cidr block
echo "Creating subnet for $availabilityZone2"
echo "Using Cidr Block for subnet  [default: $subNet3CidrBlock] "


subnet_response=$(aws ec2 create-subnet \
 --cidr-block "$subNet3CidrBlock" \
 --availability-zone "$availabilityZone2" \
 --vpc-id "$vpcId" \
 --output json)
subnetId3=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')

if [ -z "$subnetId3" ]
    then
        echo "Error creating subnet for $availablityZone2"
        echo "Now exiting...."
        exit 0
fi

#name the subnet-3
aws ec2 create-tags \
  --resources "$subnetId3" \
  --tags Key=Name,Value="$subnetName-3"
#-----------------------------------------------------------------------------------------------------

#create subnet-4 for vpc with /24 cidr block
echo "Creating subnet for $availabilityZone2"
echo "Using Cidr Block for subnet  [default: $subNet4CidrBlock]"

subnet_response=$(aws ec2 create-subnet \
 --cidr-block "$subNet4CidrBlock" \
 --availability-zone "$availabilityZone2" \
 --vpc-id "$vpcId" \
 --output json)
subnetId4=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')

if [ -z "$subnetId4" ]
    then
        echo "Error creating subnet for $availablityZone2"
        echo "Now exiting...."
        exit 0
fi

#name the subnet-4
aws ec2 create-tags \
  --resources "$subnetId4" \
  --tags Key=Name,Value="$subnetName-4"

#-------------------------------------------------------------------------------------------------------

#get the avaiablity zone 3
echo "availability zone 3 in us-east-1 [default: $availabilityZone3]"

#create subnet-5 for vpc with /24 cidr block
echo "Creating subnet 5 for $availabilityZone3"
echo "Using Cidr Block for subnet  [default: $subNet5CidrBlock]"

subnet_response=$(aws ec2 create-subnet \
 --cidr-block "$subNet5CidrBlock" \
 --availability-zone "$availabilityZone3" \
 --vpc-id "$vpcId" \
 --output json)
subnetId5=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')

if [ -z "$subnetId5" ]
    then
        echo "Error creating subnet for $availablityZone3"
        echo "Now exiting...."
        exit 0
fi
#name the subnet-5
aws ec2 create-tags \
  --resources "$subnetId5" \
  --tags Key=Name,Value="$subnetName-5"


#----------------------------------------------------------------------------------------------------

#create subnet-6 for vpc with /24 cidr block
echo "Creating subnet 5 for $availabilityZone3"
echo "Using Cidr Block for subnet  [default: $subNet6CidrBlock]"

subnet_response=$(aws ec2 create-subnet \
 --cidr-block "$subNet6CidrBlock" \
 --availability-zone "$availabilityZone3" \
 --vpc-id "$vpcId" \
 --output json)
subnetId6=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')

if [ -z "$subnetId6" ]
    then
        echo "Error creating subnet for $availablityZone3"
        echo "Now exiting...."
        exit 0
fi
#name the subnet-6
aws ec2 create-tags \
  --resources "$subnetId6" \
  --tags Key=Name,Value="$subnetName-6"

#----------------------------------------------------------------------------------------------------
#create internet gateway
echo "Creating internet gateway ..."
gateway_response=$(aws ec2 create-internet-gateway \
 --output json)
gatewayId=$(echo -e "$gateway_response" |  /usr/bin/jq '.InternetGateway.InternetGatewayId' | tr -d '"')
if [ -z "$gatewayId" ]
    then 
        echo "Gateway creation failed"
        exit 0
fi

#name the internet gateway

aws ec2 create-tags \
  --resources "$gatewayId" \
  --tags Key=Name,Value="$gatewayName"

#---------------------------------------------------------------------------------------------------


#attach gateway to vpc
echo "Attaching $gatewayName ($gatewayId) gateway to VPC $vpcName" 
#aws ec2 attach-internet-gateway --vpc-id vpc-07a81ec4c2e7072a1 --internet-gateway-id igw-02d6f4a4ad8f7c661
attach_response=$(aws ec2 attach-internet-gateway \
 --vpc-id "$vpcId" \
 --internet-gateway-id "$gatewayId")

#----------------------------------------------------------------------------------------------------

#create route table for vpc
echo "Creating a custom route table for VPC"
route_table_response=$(aws ec2 create-route-table \
 --vpc-id "$vpcId" \
 --output json)
routeTableId=$(echo -e "$route_table_response" |  /usr/bin/jq '.RouteTable.RouteTableId' | tr -d '"')

if [ -z "$routeTableId" ]
    then 
    echo "Error creating custom route table"
    echo "Now exiting..."
    exit 0
fi


#name the route table
aws ec2 create-tags \
  --resources "$routeTableId" \
  --tags Key=Name,Value="$routeTableName"

#add route for the internet gateway
echo "Adding route to route all traffic to internet gateway"
route_response=$(aws ec2 create-route \
 --route-table-id "$routeTableId" \
 --destination-cidr-block "$destinationCidrBlock" \
 --gateway-id "$gatewayId")

if [ -z "$route_response" ]
    then
        echo "Error adding route to internet gateway"
        echo "Now exiting..."
        exit 0
fi

#------------------------------------------------------------------------------------------------------


#associate subnet-1 with route
echo "Associating subnet $subnetId1 from $availabilityZone1 to custom route"  
associate_response1=$(aws ec2 associate-route-table \
 --subnet-id "$subnetId1" \
 --route-table-id "$routeTableId")

if [ -z "$associate_response1" ]
    then
        echo "Error associating subnet $subnetId3 from $availabilityZone1 to custom route"
        echo "Now exiting..."
        exit 0
fi

#associate subnet-3 with route
echo "Associating subnet $subnetId3 from $availabilityZone2 to custom route" 
associate_response2=$(aws ec2 associate-route-table \
 --subnet-id "$subnetId3" \
 --route-table-id "$routeTableId")

if [ -z "$associate_response2" ]
    then
        echo "Error associating subnet $subnetId3 from $availabilityZone1 to custom route"
        echo "Now exiting..."
        exit 0
fi



#associate subnet-3 with route
echo "Associating subnet $subnetId5 from $availabilityZone3 to custom route" 
associate_response3=$(aws ec2 associate-route-table \
 --subnet-id "$subnetId5" \
 --route-table-id "$routeTableId")

if [ -z "$associate_response3" ]
    then
        echo "Error associating subnet $subnetId5 from $availabilityZone3 to custom route"
        echo "Now exiting..."
        exit 0
fi

echo "VPC created: VPC Id ($vpcId)"
# end of create-aws-vpc