cd ..
port=$(<portNumber)
cd ..
clear
sbt "run $port"
