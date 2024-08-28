pipeline {
    agent any

    stages {
        stage('Checkout') {
                steps {
                    git branch: 'feature/cicd', credentialsId: 'github',url: 'https://github.com/Young-Camper/BE.git'

                    sh 'git pull origin feature/cicd'
                }
            }
        stage('Prepare Config') {
            steps {
                configFileProvider([
                configFile(fileId: 'application.properties', targetLocation: 'application.properties'),
                configFile(fileId: 'test.properties', targetLocation: '/src/test/java/resources/application.properties')
                ]) {
                }
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }
         stage('Test') {
                    steps {
                        sh './gradlew test'
                    }
                }


        stage('Run') {
            steps {
                sh 'java -jar build/libs/server-0.0.1-SNAPSHOT.jar'
            }
        }
    }
}
