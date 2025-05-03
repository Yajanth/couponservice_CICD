pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'JDK17'
    }

    environment {
        GIT_REPO = 'https://github.com/Yajanth/couponservice'
        GIT_CREDENTIALS_ID = 'Yajanth'
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
                        branches: [[name: '*/main']],
                        userRemoteConfigs: [
                            [
                                url: "${GIT_REPO}",
                                credentialsId: "git_credentials"
                            ]
                        ]
                    ])
                }
            }
            post {
                success {
                    echo '✅ Checkout completed successfully.'
                }
                failure {
                    echo '❌ Checkout failed.'
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Running Maven clean install...'
                sh 'mvn clean install -DskipTests'
            }
            post {
                success {
                    echo '✅ Build completed successfully.'
                }
                failure {
                    echo '❌ Build failed.'
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
            post {
                success {
                    echo '✅ Artifacts archived successfully.'
                }
                failure {
                    echo '❌ Failed to archive artifacts.'
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SONAR_HOST_URL = "http://localhost:9000"
                SONAR_AUTH_TOKEN = credentials('SonarToken')
            }
            steps {
                sh "mvn sonar:sonar -Dsonar.projectKey=Coupon_service_analysis
                -Dsonar.host.url=$SONAR_HOST_URL 
                -Dsonar.login=$SONAR_AUTH_TOKEN 
                -Dsonar.jacoco.reportPaths=target/jacoco.xml"
            }
            post {
                success {
                    echo '✅ SonarQube analysis completed.'
                }
                failure {
                    echo '❌ SonarQube analysis failed.'
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker_hub_credentials',
                     usernameVariable: 'DOCKER_HUB_USER', passwordVariable: 'DOCKER_HUB_PASS')]) {
                        echo "Logging into Docker Hub..."
                        sh "echo '${DOCKER_HUB_PASS}' | docker login -u '${DOCKER_HUB_USER}' --password-stdin"

                        echo 'Building Docker image for the application...'
                        sh "docker build --no-cache -t ${DOCKER_HUB_USER}/${APP_IMAGE}:latest ."

                        echo 'Pushing application image to Docker Hub...'
                        sh "docker push ${DOCKER_HUB_USER}/${APP_IMAGE}:latest"
                    }
                }
            }
            post {
                success {
                    echo '✅ Docker image built and pushed successfully.'
                }
                failure {
                    echo '❌ Failed to build/push Docker image.'
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
            post {
                success {
                    echo '✅ Deployment successful.'
                }
                failure {
                    echo '❌ Deployment failed.'
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully.'
        }
        failure {
            echo '🚨 Pipeline execution failed.'
        }
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
    }
}