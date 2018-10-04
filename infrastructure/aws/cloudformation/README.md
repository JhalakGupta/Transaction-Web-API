# AWS Cloud formation readme

## Setup requirements
1. csye6225-aws-cf-create-stack.sh for cloudformation to create a VPC stack
2. csye6225-aws-cf-terminate-stack.sh for cloudformation to terminate a VPC stack and clear resources
3. csye6225-cf-networking.json for the networking setup template
4. csye6225-aws-cf-create-application-stack.sh for cloudformation to create a Application stack
5. csye6225-aws-cf-terminate-application-stack.sh for cloudformation to terminate a Apllication stack and clear resources
6. csye6225-cf-application.json for the application setup template
4. A shell terminal with aws cli configured

## Running 
1. All 6 files are required to be in the same folder of cloudformation
2. Check if files have executing and read-write access, if not available please set the same
3. Navigate to the folder containing the files using the terminal

4. For creating stack run the following command with STACK_NAME(any name) as parameter 
   `./csye6225-aws-cf-create-stack.sh STACK_NAME` 
   `./csye6225-aws-cf-create-application-stack.sh STACK_NAME` 

5. For terminating stack run the following command with STACK_NAME(any name) as parameter 
   `./csye6225-aws-cf-terminate-stack.sh STACK_NAME`
   `./csye6225-aws-cf-terminate-application-stack.sh STACK_NAME`
   
