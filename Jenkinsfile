// pipeline {
// 	agent any

// 	stages {
// 		stage('Clean') {
// 			steps {
// 				sh 'mvn clean'
// 			}
// 		}
// 		stage('Compile') {
// 			steps {
// 				sh 'mvn compile'
// 			}
// 		}
// 		stage('Test') {
// 			steps {
// 				sh 'mvn test -Dmaven.test.failure.ignore=true'
// 			}
// 		}
// 		stage('PMD') {
// 			steps {
// 				sh 'mvn pmd:pmd'
// 			}
// 		}
// 		stage('JaCoCo') {
// 			steps {
// 				sh 'mvn jacoco:report'
// 			}
// 		}
// 		stage('Javadoc') {
// 			steps {
// 				sh 'mvn javadoc:javadoc'
// 			}
// 		}
// 		stage('Site') {
// 			steps {
// 				sh 'mvn site'
// 			}
// 		}
// 		stage('Package') {
// 			steps {
// 				sh 'mvn package -DskipTests'
// 			}
// 		}
// 	}

// 	post {
// 		always {
// 			archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true
// 			archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
// 			archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
// 			junit '**/target/surefire-reports/*.xml'
// 		}
// 	}
// }

pipeline {
	agent any

	environment {
		// Jenkins credentials configuration
		DOCKER_HUB_CREDENTIALS = credentials('docker')
		// Docker Hub Repository's name
		DOCKER_IMAGE = 'violence9331/teedy-app'
		// Use build number as tag
		DOCKER_TAG = "${env.BUILD_NUMBER}"
	}

	stages {
		stage('Build') {
			steps {
				checkout scmGit(
					branches: [[name: '*/master']],
					extensions: [],
					userRemoteConfigs: [[url: 'https://github.com/violence9331/Teedy.git']]
				)
				sh 'mvn -B -DskipTests clean package'
			}
		}

		stage('Building image') {
			steps {
				script {
					// assume Dockerfile locate at root
					docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
				}
			}
		}

		stage('Upload image') {
			steps {
				script {
					sh '''
                        mkdir -p ~/.docker
                        cat > ~/.docker/config.json << 'EOF'
                        {
                            "auths": {
                                "https://index.docker.io/v1/": {}
                            },
                            "HttpHeaders": {
                                "User-Agent": "Docker-Client/19.03.12"
                            }
                        }
                        EOF
                    '''
                    
                    // 然后登录
                    withDockerRegistry([credentialsId: 'DOCKER_HUB_CREDENTIALS', url: 'https://index.docker.io/v1/']) {
                        sh '''
                            # 使用国内镜像源登录
                            docker login --username=$DOCKER_HUB_CREDENTIALS_USR \
                                        --password-stdin \
                                        registry.cn-hangzhou.aliyuncs.com <<< "$DOCKER_HUB_CREDENTIALS_PSW"
                            
                            # 构建和推送镜像
                            docker build -t your-image:tag .
                            docker tag your-image:tag registry.cn-hangzhou.aliyuncs.com/your-namespace/your-image:tag
                            docker push registry.cn-hangzhou.aliyuncs.com/your-namespace/your-image:tag
                        '''
                    }
				}
			}
		}

		stage('Run containers') {
			steps {
				script {
					// stop then remove containers if exists
					sh 'docker stop teedy-container-8081 || true'
					sh 'docker rm teedy-container-8081 || true'
					// run container
					docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-container-8081 -d -p 8081:8080')
					// Optional: list all teedy-containers
					sh 'docker ps --filter "name=teedy-container"'
				}
			}
		}
	}
}
