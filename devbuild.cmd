@echo off
echo pi
mvn jira:cli -Dhttp.port=8080 -DcliPort=4330 -Dcontext.path=/jira