# AWS CLI script readme

## Setup requirements
1. csye6225-aws-networking-setup.sh to create a VPC stack
2. csye6225-aws-networking-teardown.sh for terminating a VPC stack and clear resources
3. A shell terminal with aws cli configured

## Running 
1. Check if files have executing and read-write access, if not available please set the same
2. Navigate to the folder containing the files using the terminal

3. For creating stack run the following command with STACK_NAME(any name) as parameter 
   `./csye6225-aws-networking-setup.sh SATCK_NAME` 

4. For terminating stack run the following command with STACK_NAME(any name) as parameter 
   `./csye6225-aws-networking-teardown.sh SATCK_NAME` 
