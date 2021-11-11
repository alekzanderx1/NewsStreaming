# Spark Streaming app to analyse latest and most popular news keywords
This uses Developer API provided by https://currentsapi.services/en to get latest news and parse their descriptions to find important and trending keywords in a constantly updating 5 minute window.

There is a limit to number of times the API can be called in a Minute and also in a Day, hence the Custom API Receiver has been set to Poll every 12th second. 

Steps to launch:
1. Clone project into local repository 
2. Import into IntelliJ or IDE of your choice as a SBT project
3. Build JAR using 'package' command in sbt shell
4. Register for a key at https://currentsapi.services/en 
5. Goto target/yourSparkVersion 
6. Using the currentsAPI Key, launch spark app using the following command(in local mode)

    `spark-submit --master local[*] --class "NewsStreaming" newstrendstreaming_2.12-1.0.jar https://api.currentsapi.services/v1/latest-news?language=en YourApiKey`
