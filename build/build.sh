export http_proxy_host=$(echo $http_proxy | awk -F':' {'print $2'} | tr -d '/')
export http_proxy_port=$(echo $http_proxy | awk -F':' {'print $3'} | tr -d '/')

export https_proxy_host=$(echo $https_proxy | awk -F':' {'print $2'} | tr -d '/')
export https_proxy_port=$(echo $https_proxy | awk -F':' {'print $3'} | tr -d '/')

export _JAVA_OPTIONS="-Dhttp.proxyHost=$http_proxy_host -Dhttp.proxyPort=$http_proxy_port -Dhttps.proxyHost=$https_proxy_host -Dhttps.proxyPort=$http_proxy_port"

REMOTE_URL=https://github.com/secure-device-onboard/iot-platform-sdk
REMOTE_BRANCH=master

if [ "$use_remote" = "1" ]; 
    then 
        cd /tmp/
        git clone $REMOTE_URL
        cd /tmp/iot-platform-sdk/ 
        git checkout $REMOTE_BRANCH
        mvn clean install 
        cd demo && find . -name \*.war -exec cp --parents {} /home/sdouser/iot-platform-sdk/demo \;      
    else 
        mvn clean install 
fi 