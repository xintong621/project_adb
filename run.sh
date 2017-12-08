#!/bin/bash
for number in {1..19}
do
java -jar ADBProject.jar testcase/test$number.txt
done
exit 0
