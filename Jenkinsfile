// This job will be restricted to run only on 'ubuntu18.04-OnDemand' Build machine
node('ubuntu18.04-OnDemand'){

// Stage for checking out the sourceCode
stage('scm checkout'){
  checkout scm
}

// Stage to build the project
stage('build iot-package-sdk package'){
  sh 'mvn clean package'
}

// Stage to create the cri package and archive
stage('archive artifacts'){
  sh '''
    mkdir -p Iot-Platform-SDK/docker
    cp -r ops/owner/target/owner*.jar Iot-Platform-SDK
    cp -r to0scheduler/to0client/target/to0client*.jar Iot-Platform-SDK
    cp -r docker Iot-Platform-SDK/
    '''
    zip zipFile: 'Iot-Platform-SDK.zip', archive: false, dir: 'Iot-Platform-SDK'
    archiveArtifacts artifacts: 'Iot-Platform-SDK.zip', fingerprint: true, allowEmptyArchive: false
}

}