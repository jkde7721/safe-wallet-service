pipeline {
    agent any
    stages {
        stage('Github Pull') {
            steps {
                git branch: 'release/v1.0.0', //추후 수정
                credentialsId: 'github-access-key',
                url: 'https://github.com/jkde7721/safe-wallet-service.git'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube Server') {
                    sh "chmod +x ./gradlew"
                    sh "./gradlew test sonar"
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                   waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Build') {
            steps {
                sh "./gradlew bootJar"
            }
        }
        stage('Zip Files') {
            steps {
                zip archive:true, zipFile:'deploy.zip', glob:'**/build/libs/safe-wallet-0.0.1-SNAPSHOT.jar,**/scripts/*,**/appspec.yml', overwrite:true
            }
        }
        stage('S3 Upload') {
            steps {
                withAWS(region:'ap-northeast-2', credentials:'aws-access-key') {
                    s3Upload(bucket:'safe-wallet-bucket', path:'deploy', includePathPattern:'**/*.zip')
                }
            }
        }
        stage('Deploy') {
            steps {
                withAWS(region:'ap-northeast-2', credentials:'aws-access-key') {
                    createDeployment(
                        s3Bucket: 'safe-wallet-bucket',
                        s3Key: 'deploy/deploy.zip',
                        s3BundleType: 'zip',
                        applicationName: 'safe-wallet-codedeploy',
                        deploymentGroupName: 'safe-wallet-codedeploy-jenkins',
                        deploymentConfigName: 'safe-wallet-codedeploy-config',
                        description: 'Safe Wallet Deploy',
                        waitForCompletion: 'true',
                        //Optional values
                        ignoreApplicationStopFailures: 'false',
                        fileExistsBehavior: 'OVERWRITE'
                    )
                }
            }
        }
    }
}