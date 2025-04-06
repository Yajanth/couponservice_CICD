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
        DOCKER_HUB_USER = 'yajanthrr'  // Define empty variables for better scope
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
            post {
                success {
                    echo 'Checkout successful!'
                }
                failure {
                    script {
                        echo 'Checkout failed!'
                        currentBuild.result = 'FAILURE'
                    }
                }
            }
        }
        
        stage('Build') {
            steps {
                echo 'Running Maven clean install...'
                bat 'mvn clean install -DskipTests'  // Linux-compatible command
            }
            post {
                success {
                    echo 'Build successful!'
                }
                failure {
                    script {
                        echo 'Build failed!'
                        currentBuild.result = 'FAILURE'
                    }
                }
            }
        }
        
        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
            post {
                success {
                    echo 'Artifact archived successfully!'
                }
                failure {
                    script {
                        echo 'Artifact archiving failed!'
                        currentBuild.result = 'FAILURE'
                    }
                }
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
            post {
                success {
                    echo 'SonarQube analysis completed successfully!'
                }
                failure {
                    script {
                        echo 'SonarQube analysis failed!'
                        currentBuild.result = 'FAILURE'
                    }
                }
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
            post {
                success {
                    echo 'Docker image built and pushed successfully!'
                }
                failure {
                    script {
                        echo 'Docker image build/push failed!'
                        currentBuild.result = 'FAILURE'
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
            post {
                success {
                    echo 'Deployment successful!'
                }
                failure {
                    script {
                        echo 'Deployment failed!'
                        currentBuild.result = 'FAILURE'
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for details.'
        }
    }
}
