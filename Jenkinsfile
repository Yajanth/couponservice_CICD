pipeline {
    agent any
    
    tools {
        maven 'maven'  // Ensure Maven is installed in Jenkins
        jdk 'JDK17'
    }
    
    environment {
        GIT_REPO = 'https://github.com/Yajanth/couponservice'
        GIT_CREDENTIALS_ID = 'Yajanth'  // Replace with your Jenkins credentials ID
        APP_IMAGE = 'couponservice'
        DB_IMAGE = 'coupondb'  
        DOCKER_HUB_USER = 'yajanthrr'  
        DOCKER_HUB_PASS = ''
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
                bat "mvn sonar:sonar -Dsonar.projectKey=CouponService_analysis -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_AUTH_TOKEN"
            }
        }
        
        stage('Build and Push Docker Images') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker_hub_credentials', usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASS')]) {
                        echo "Logging into Docker Hub..."
                        bat "echo $DOCKER_HUB_PASS | docker -D login -u $DOCKER_HUB_USER --password-stdin"
                        
                        echo 'Building Docker image for the application...'
                        bat "docker build --no-cache -t $DOCKER_HUB_USER/$APP_IMAGE:latest ."

                        echo 'Pushing application image to Docker Hub...'
                        bat "docker push $DOCKER_HUB_USER/$APP_IMAGE:latest"
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo 'Stopping existing containers...'
                    bat 'docker compose down -v'

                    echo 'Pulling latest images...'
                    bat "docker pull $DOCKER_HUB_USER/$APP_IMAGE:v1"

                    echo 'Starting new deployment...'
                    bat 'docker compose up -d'
                    
                    echo 'Showing docker-compose logs...'
                    bat 'docker compose logs'
                }
            }
        }
    }
}