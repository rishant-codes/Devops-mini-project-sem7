pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    triggers {
        // Works whether the Jenkins job is backed by a GitHub webhook (commit-triggered)
        // or has no webhook reachable (falls back to polling SCM every 5 minutes).
        pollSCM('H/5 * * * *')
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['staging', 'production'], description: 'Target deploy environment')
        booleanParam(name: 'RUN_SELENIUM', defaultValue: true, description: 'Run the Selenium quality gate')
        booleanParam(name: 'PUSH_IMAGE', defaultValue: false, description: 'Push Docker images to the registry (needs dockerhub-creds)')
        string(name: 'DOCKER_REGISTRY_NAMESPACE', defaultValue: 'yourdockerhubuser', description: 'Docker Hub namespace/user to tag images under')
    }

    tools {
        maven 'Maven3'
        nodejs 'Node20'
    }

    environment {
        BACKEND_PORT = '8082'
        FRONTEND_PORT = '5173'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                script {
                    if (isUnix()) {
                        sh 'git log -5 --oneline'
                    } else {
                        bat 'git log -5 --oneline'
                    }
                }
            }
        }

        stage('Backend - Build & Unit Test') {
            steps {
                dir('backend') {
                    script {
                        if (isUnix()) {
                            sh 'mvn -B clean package'
                        } else {
                            bat 'mvn -B clean package'
                        }
                    }
                }
            }
            post {
                always {
                    junit testResults: 'backend/target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Frontend - Install & Build') {
            steps {
                dir('frontend') {
                    script {
                        if (isUnix()) {
                            sh 'npm ci'
                            sh 'npm run lint'
                            sh 'npm run build'
                        } else {
                            bat 'npm ci'
                            bat 'npm run lint'
                            bat 'npm run build'
                        }
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'frontend/dist/**', allowEmptyArchive: true
                }
            }
        }

        stage('Start Services for E2E') {
            when { expression { params.RUN_SELENIUM } }
            steps {
                script {
                    if (isUnix()) {
                        dir('backend') {
                            sh 'nohup java -jar target/*.jar > backend.log 2>&1 &'
                        }
                        dir('frontend') {
                            sh 'nohup npx vite preview --port ${FRONTEND_PORT} --host > frontend.log 2>&1 &'
                        }
                        sh """
                            for i in \$(seq 1 30); do
                                curl -sf http://localhost:${BACKEND_PORT}/api/health && break
                                sleep 2
                            done
                        """
                    } else {
                        dir('backend') {
                            bat 'powershell -Command "$p = Start-Process java -ArgumentList \'-jar\', (Get-Item target\\*.jar).FullName -PassThru -RedirectStandardOutput backend.log -RedirectStandardError backend_err.log; $p.Id | Out-File -FilePath backend.pid"'
                        }
                        dir('frontend') {
                            bat 'powershell -Command "$p = Start-Process npx -ArgumentList \'vite\', \'preview\', \'--port\', \'%FRONTEND_PORT%\', \'--host\' -PassThru -RedirectStandardOutput frontend.log -RedirectStandardError frontend_err.log; $p.Id | Out-File -FilePath frontend.pid"'
                        }
                        bat """powershell -Command "for (\$i=1; \$i -le 30; \$i++) { try { \$r = Invoke-WebRequest -Uri http://localhost:%BACKEND_PORT%/api/health -UseBasicParsing; if (\$r.StatusCode -eq 200) { break } } catch {}; Start-Sleep -Seconds 2 }" """
                    }
                }
            }
        }

        stage('Continuous Testing - Selenium') {
            when { expression { params.RUN_SELENIUM } }
            steps {
                dir('selenium-tests') {
                    script {
                        if (isUnix()) {
                            sh "mvn -B test -Dbase.url=http://localhost:${FRONTEND_PORT} -Dapi.url=http://localhost:${BACKEND_PORT} -Dheadless=true"
                        } else {
                            bat "mvn -B test -Dbase.url=http://localhost:${FRONTEND_PORT} -Dapi.url=http://localhost:${BACKEND_PORT} -Dheadless=true"
                        }
                    }
                }
            }
            post {
                always {
                    junit testResults: 'selenium-tests/target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'selenium-tests/target/screenshots/**', allowEmptyArchive: true
                }
                // A failed Selenium gate must stop the pipeline before Docker/deploy stages run.
                failure {
                    error('Selenium quality gate failed - blocking deployment.')
                }
            }
        }

        stage('Stop E2E Services') {
            when { expression { params.RUN_SELENIUM } }
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            pkill -f "target/.*\\.jar" || true
                            pkill -f "vite preview" || true
                        '''
                    } else {
                        dir('backend') {
                            bat 'powershell -Command "if (Test-Path backend.pid) { $pid = Get-Content backend.pid; Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue; Remove-Item backend.pid -Force }"'
                        }
                        dir('frontend') {
                            bat 'powershell -Command "if (Test-Path frontend.pid) { $pid = Get-Content frontend.pid; Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue; Remove-Item frontend.pid -Force }"'
                        }
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "docker build -t aqmp-backend:${IMAGE_TAG} ./backend"
                        sh "docker build -t aqmp-frontend:${IMAGE_TAG} --build-arg VITE_API_BASE_URL=http://localhost:${BACKEND_PORT} ./frontend"
                    } else {
                        bat "docker build -t aqmp-backend:${IMAGE_TAG} ./backend"
                        bat "docker build -t aqmp-frontend:${IMAGE_TAG} --build-arg VITE_API_BASE_URL=http://localhost:${BACKEND_PORT} ./frontend"
                    }
                }
            }
        }

        stage('Docker Push') {
            when { expression { params.PUSH_IMAGE } }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        if (isUnix()) {
                            sh '''
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                                docker tag aqmp-backend:${IMAGE_TAG} ${DOCKER_REGISTRY_NAMESPACE}/aqmp-backend:${IMAGE_TAG}
                                docker tag aqmp-frontend:${IMAGE_TAG} ${DOCKER_REGISTRY_NAMESPACE}/aqmp-frontend:${IMAGE_TAG}
                                docker push ${DOCKER_REGISTRY_NAMESPACE}/aqmp-backend:${IMAGE_TAG}
                                docker push ${DOCKER_REGISTRY_NAMESPACE}/aqmp-frontend:${IMAGE_TAG}
                            '''
                        } else {
                            bat """
                                echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                                docker tag aqmp-backend:${IMAGE_TAG} ${DOCKER_REGISTRY_NAMESPACE}/aqmp-backend:${IMAGE_TAG}
                                docker tag aqmp-frontend:${IMAGE_TAG} ${DOCKER_REGISTRY_NAMESPACE}/aqmp-frontend:${IMAGE_TAG}
                                docker push ${DOCKER_REGISTRY_NAMESPACE}/aqmp-backend:${IMAGE_TAG}
                                docker push ${DOCKER_REGISTRY_NAMESPACE}/aqmp-frontend:${IMAGE_TAG}
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def backPort = params.ENVIRONMENT == 'production' ? '80' : '8082'
                    def frontPort = params.ENVIRONMENT == 'production' ? '8080' : '8081'
                    if (isUnix()) {
                        sh """
                            docker rm -f aqmp-backend-${params.ENVIRONMENT} aqmp-frontend-${params.ENVIRONMENT} || true
                            docker run -d --name aqmp-backend-${params.ENVIRONMENT} -p ${backPort}:8080 aqmp-backend:${IMAGE_TAG}
                            docker run -d --name aqmp-frontend-${params.ENVIRONMENT} -p ${frontPort}:80 aqmp-frontend:${IMAGE_TAG}
                        """
                    } else {
                        bat """
                            docker rm -f aqmp-backend-${params.ENVIRONMENT} aqmp-frontend-${params.ENVIRONMENT} 2>nul || exit 0
                            docker run -d --name aqmp-backend-${params.ENVIRONMENT} -p ${backPort}:8080 aqmp-backend:${IMAGE_TAG}
                            docker run -d --name aqmp-frontend-${params.ENVIRONMENT} -p ${frontPort}:80 aqmp-frontend:${IMAGE_TAG}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Build ${env.BUILD_NUMBER} for environment ${params.ENVIRONMENT} finished with status ${currentBuild.currentResult}"
        }
    }
}
