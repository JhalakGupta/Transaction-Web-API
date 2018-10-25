STACK_NAME=$1

aws cloudformation delete-stack --stack-name $STACK_NAME

aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME

if [ $? -ne "0" ]
then 
	echo "Deletion of Stack failed"
else
	echo "Deletion of Stack Success"
fi
