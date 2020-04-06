## ORF Predictor group 4
* Armin van Eldik
* Morgan Atmodimedjo
* Yaris van Thiel

### Add Java to Windows PATH:
1. Search and select **advanced system settings**
2. Select **Environment Variables**
3. Select **Path** in **System variables**
4. Select **Edit**
5. Select **New**
6. Paste path to jdk directory, e.g. ```C:\Users\YOUR_USERNAME\Downloads\jdk-14\bin```

### If you get an SQL error, add mysql-connector library to libraries in IntelliJ:
1. Select **File**
2. Select **Project Structure**
3. Select **Libraries**
4. Click the plus sign (+), then **From Maven**
5. Search for **mysql:mysql-connector-java:5.1.40**
6. Click **Ok**, then **Apply**

Your project structure should look as follows:
![Project Structure](images/project_structure.png)
