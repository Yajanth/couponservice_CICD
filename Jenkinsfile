pipeline {
    agent any
    
    tools {
        maven 'maven'  // Ensure Maven is installed in Jenkins
        jdk 'JDK17'
    }
    
    environment {
        GIT_REPO = 'https://github.com/Yajanth/couponservice'
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
                sh 'mvn clean install -DskipTests'  // Linux-compatible command
            }
        }
        
        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        // stage('SonarQube Analysis') {
        //     environment {
        //         SONAR_HOST_URL = "http://localhost:9000"
        //         SONAR_AUTH_TOKEN = credentials('SonarQubeNew')
        //     }
        //     steps {
        //         sh "mvn sonar:sonar -Dsonar.projectKey=InterestCalculatorPipeline -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_AUTH_TOKEN"
        //     }
        // }
        
        stage('Build and Push Docker Images') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker_hub_credentials', usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASS')]) {
                        echo "Logging into Docker Hub..."
                        sh "echo '${DOCKER_HUB_PASS}' | docker login -u '${DOCKER_HUB_USER}' --password-stdin"
                        
                        echo 'Building Docker image for the application...'
                        sh "docker build --no-cache -t ${DOCKER_HUB_USER}/${APP_IMAGE}:latest ."

                        echo 'Pushing application image to Docker Hub...'
                        sh "docker push ${DOCKER_HUB_USER}/${APP_IMAGE}:latest"
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo 'Stopping existing containers...'
                    sh 'docker compose down -v'

                    echo 'Pulling latest images...'
                    sh "docker pull ${DOCKER_HUB_USER}/${APP_IMAGE}:v1"

                    echo 'Starting new deployment...'
                    sh 'docker compose up -d'
                    
                    echo 'Showing docker-compose logs...'
                    sh 'docker compose logs'
                }
            }
        }
    }
}
