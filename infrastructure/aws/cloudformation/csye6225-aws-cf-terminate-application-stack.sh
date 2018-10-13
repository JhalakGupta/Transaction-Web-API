STACK_NAME=$1
EC2_NAME=${STACK_NAME}-csye6225-ec2

export ec2InstanceId=$(aws ec2 describe-instances --query 'Reservations[*].Instances[*].[InstanceId, State.Name, Tags[*][?Value==`${EC2_NAME}`]]' --output text|grep running|awk '{print $1}')

aws ec2 stop-instances --instance-ids $ec2InstanceId
aws ec2 wait instance-stopped --instance-ids $ec2InstanceId
echo "Instance ${ec2InstanceId} is stopped!!"

aws ec2 modify-instance-attribute --no-disable-api-termination --instance-id $ec2InstanceId
echo "Termination protection is removed from the instance!!"

aws cloudformation delete-stack --stack-name $STACK_NAME
aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME

if [ $? -eq 0 ]; then
echo "Stack ${STACK_NAME} deleted successfully!!"
else
echo "Unable to delete stack. Please input correct name!!"
fi