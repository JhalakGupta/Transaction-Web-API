#!/bin/sh
#
# Delete a VPC and its dependencies
STACK_NAME=$1
echo "Beginning teardown for stack $STACK_NAME"

vpcName="$STACK_NAME-csye6225-vpc"

vpcid=$(aws ec2 describe-vpcs --filters "Name=tag:Name,Values=$STACK_NAME-csye6225-vpc" --query "Vpcs[].VpcId[]" --output text)
echo "VPC ID: $vpcid"

if [ -z "$vpcid" ]
    then 
        echo "VPC $vpcName is not detected"
        echo "Now exiting"
        exit 0
fi

# Delete subnets // Done
echo "Deleting all the subnets attached to VPC: $vpcName($vpcid)" 
for i in `aws ec2 describe-subnets --filters Name=vpc-id,Values="${vpcid}" | grep subnet- | sed -E 's/^.*(subnet-[a-z0-9]+).*$/\1/'`; do aws ec2 delete-subnet --subnet-id $i; done
echo "All subnets deleted successfully!"

#Delete the custom route tables
echo "Getting the route tables associated with the VPC $vpcName($vpcid)"
for rtb in `aws ec2 describe-route-tables --filters Name=vpc-id,Values="${vpcid}" | grep rtb- | sed -E 's/^.*(rtb-[a-z0-9]+).*$/\1/'`; do aws ec2 delete-route-table --route-table-id $rtb; done
echo "All route-tables associated with $vpcid deleted successfully!"

# Detach and delete internet gateways
for i in `aws ec2 describe-internet-gateways --filters Name=attachment.vpc-id,Values="${vpcid}" | grep igw- | sed -E 's/^.*(igw-[a-z0-9]+).*$/\1/'`; do aws ec2 detach-internet-gateway --internet-gateway-id=$i --vpc-id="$vpcid"; aws ec2 delete-internet-gateway --internet-gateway-id=$i; done
if [ $? -eq "0" ]
then 
	echo "All internet gateways associated with $vpcid deleted successfully!"
else
	echo "Deletion of internet gateways from VPC failed"
	exit 1
fi
# Delete the VPC
echo "Deleting VPC $vpcName ($vpcid)"
aws ec2 delete-vpc --vpc-id $vpcid
if [ $? -eq "0" ]
then 
	echo "VPC $vpcName($vpcid) deleted successfully!"
else
	echo "Deletion of VPC $vpcName(vpcid) failed"
	exit 1
fi

if [ $? -ne "0" ]
then 
	echo "Termination of AWS CLI Stack failed"
else
	echo "Termination of AWS CLI Stack Success"
fi
