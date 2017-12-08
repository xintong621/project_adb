# Advanced Database System

This is the final project of Advanced Database System course from Graduate School of Arts and Science, New York University. The aim of this project is to implement a **Replicated Concurrency Control and Recovery Distributed System** with multiversion concurrency control, deadlock detection, replication, and failure recovery.

### **Author:**

    Xintong Wang(N18322289)
  
    Dailing Zhu(N11754882)
  


### **Github Repository:** 
    https://github.com/xintong621/project_adb




### **Compile and Run:**

##### (A)Compile using Eclipse:

Since we wrote our project using Eclipse，then it will be great if you are using Eclipse!
If you are using Eclipse, simpliy import whole thing into Eclipse as a project.

##### (B)Compile in terminal:
We have packed our project as a .jar file named ADBProject.jar. In order to run the project, you need first reach to the location of ADBProject.jar, then type:
```
        $ java -jar ADBProject.jar <location/of/test/file/filename.txt>
```
to run all testcases in one time using ADBProject.jar (the result will store in output.txt):
```
        $ sh run.sh > output.txt
```
If you wanna compile and run our program without using ADBProject.jar, you can follow steps below:
1. First get into the folder where all .java files are.
```
	$ cd ~/src/project_adb
```
2. Compile all files in order to create .class files.
```
	$ javac Main.java DM.java Graph.java Site.java TM.java Transaction.java Variable.java
```
3. Get back to the directory where project_adb is
```
	$ cd ..
```
4. Run the project
```
	$ java project_adb.Main <location/of/test/file/filename.txt>
```




### **Input file:**

We have several sample test file in testcase folder. Test files from No.1 to No.19 were provided by professor, and we have come up with several corner cases. Please feel free to test results of those cases to see if we get the right answer or not. 
