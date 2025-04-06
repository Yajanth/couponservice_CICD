pipeline {
    agent any
    
    tools {
        maven 'maven'  // Ensure Maven is installed in Jenkins
        jdk 'JDK17'
    }
    
    environment {
        GIT_REPO = 'https://github.com/Yajanth/couponservice_CICD'
        GIT_CREDENTIALS_ID = 'Yajanth'  // Replace with your Jenkins credentials ID
        DOCKER_HUB_USER = 'yajanthrr'
        APP_IMAGE = 'couponservice'
        DB_IMAGE = 'coupondb'  
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],  // Checkout the 'main' branch
                        userRemoteConfigs: [
                            [
                                url: "${GIT_REPO}",  // Git repository URL
                                credentialsId: "git_credentials"  // Jenkins credentials ID
                            ]
                        ]
                    ])
                }
            }
        }
        
        stage('Build') {
            steps {
                echo 'Running Maven clean install...'
                bat 'mvn clean install -DskipTests'  // Linux-compatible command
            }
        }
        
        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('SonarQube Analysis') {
            environment {
                SONAR_HOST_URL = "http://localhost:9000"
                SONAR_AUTH_TOKEN = credentials('sonar_token_coupon')
            }
            steps {
                bat "mvn sonar:sonar -Dsonar.projectKey=InterestCalculatorPipeline -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_AUTH_TOKEN"
            }
        }
        

    }
}
