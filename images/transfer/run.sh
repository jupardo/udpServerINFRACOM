read -p "Enter the path of the file you want to obtain: " FILE
read -p "Enter the number of concurrent connections you want to have: " CONCURRENT_CONNECTIONS
docker run -it -p 3312:3312/udp -e FILE=$FILE -e CONCURRENT_CONNECTIONS=$CONCURRENT_CONNECTIONS -v logs:/var/logs udpserver:latest