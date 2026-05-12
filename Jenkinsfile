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
		DOCKER_IMAGE = 'xx/teedy-app'
		// Use build number as tag
		DOCKER_TAG = "${env.BUILD_NUMBER}"
	}

	stages {
		stage('Build') {
			steps {
				checkout scmGit(
					branches: [[name: '*/master']],
					extensions: [],
					userRemoteConfigs: [[url: 'https://github.com/xx/Teedy.git']]
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
					// sign in Docker Hub
					docker.withRegistry('https://registry.hub.docker.com', 'DOCKER_HUB_CREDENTIALS') {
						// push image
						docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
						// optional: label latest
						docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
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
